/*
 * Copyright (c) 2017, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 * <p>
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.model.types;

import org.ballerinalang.bre.StructVarLocation;
import org.ballerinalang.model.BLangPackage;
import org.ballerinalang.model.StructDef;
import org.ballerinalang.model.SymbolName;
import org.ballerinalang.model.SymbolScope;
import org.ballerinalang.model.TypeMapper;
import org.ballerinalang.model.VariableDef;
import org.ballerinalang.model.statements.VariableDefStmt;
import org.ballerinalang.model.symbols.BLangSymbol;
import org.ballerinalang.natives.typemappers.NativeCastMapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Class to hold the types and their connections within ballerina.
 */
public class TypeLattice {

    protected final HashMap<String, TypeVertex> vertices = new HashMap<>();
    protected final HashMap<Integer, TypeEdge> edges = new HashMap<>();
    private static TypeLattice explicitCastLattice = new TypeLattice();
    private static TypeLattice implicitCastLattice = new TypeLattice();

    public static TypeLattice getExplicitCastLattice() {
        return explicitCastLattice;
    }

    public static TypeLattice getImplicitCastLattice() {
        return implicitCastLattice;
    }


    public static void loadImplicitCastLattice(SymbolScope scope) {

        TypeVertex intV = new TypeVertex(scope.resolve(new SymbolName(TypeConstants.INT_TNAME)));
        TypeVertex floatV = new TypeVertex(scope.resolve(new SymbolName(TypeConstants.FLOAT_TNAME)));
        TypeVertex stringV = new TypeVertex(scope.resolve(new SymbolName(TypeConstants.STRING_TNAME)));
        TypeVertex booleanV = new TypeVertex(scope.resolve(new SymbolName(TypeConstants.BOOLEAN_TNAME)));
        TypeVertex jsonV = new TypeVertex(scope.resolve(new SymbolName(TypeConstants.JSON_TNAME)));
        TypeVertex mapV = new TypeVertex(scope.resolve(new SymbolName(TypeConstants.MAP_TNAME)));
        
        implicitCastLattice.addVertex(intV, false);
        implicitCastLattice.addVertex(floatV, false);
        implicitCastLattice.addVertex(stringV, false);
        implicitCastLattice.addEdge(stringV, jsonV, NativeCastMapper.STRING_TO_JSON_FUNC);
        
        implicitCastLattice.addEdge(intV, floatV, NativeCastMapper.INT_TO_FLOAT_FUNC);
        implicitCastLattice.addEdge(intV, stringV, NativeCastMapper.INT_TO_STRING_FUNC);
        implicitCastLattice.addEdge(intV, jsonV, NativeCastMapper.INT_TO_JSON_FUNC);
        
        implicitCastLattice.addEdge(floatV, stringV, NativeCastMapper.FLOAT_TO_STRING_FUNC);
        implicitCastLattice.addEdge(floatV, jsonV, NativeCastMapper.FLOAT_TO_JSON_FUNC);
        
        implicitCastLattice.addEdge(booleanV, stringV, NativeCastMapper.BOOLEAN_TO_STRING_FUNC);
        implicitCastLattice.addEdge(booleanV, intV, NativeCastMapper.BOOLEAN_TO_INT_FUNC);
        implicitCastLattice.addEdge(booleanV, floatV, NativeCastMapper.BOOLEAN_TO_FLOAT_FUNC);
        implicitCastLattice.addEdge(booleanV, jsonV, NativeCastMapper.BOOLEAN_TO_JSON_FUNC);
        
//        implicitCastLattice.addEdge(jsonV, mapV, NativeCastMapper.JSON_TO_MAP_FUNC);
        
//        implicitCastLattice.addEdge(mapV, jsonV, NativeCastMapper.MAP_TO_JSON_FUNC);
    }

    public static void loadExplicitCastLattice(SymbolScope scope) {

        TypeVertex intV = new TypeVertex(scope.resolve(new SymbolName(TypeConstants.INT_TNAME)));
        TypeVertex floatV = new TypeVertex(scope.resolve(new SymbolName(TypeConstants.FLOAT_TNAME)));
        TypeVertex stringV = new TypeVertex(scope.resolve(new SymbolName(TypeConstants.STRING_TNAME)));
        TypeVertex booleanV = new TypeVertex(scope.resolve(new SymbolName(TypeConstants.BOOLEAN_TNAME)));
        TypeVertex xmlV = new TypeVertex(scope.resolve(new SymbolName(TypeConstants.XML_TNAME)));
        TypeVertex jsonV = new TypeVertex(scope.resolve(new SymbolName(TypeConstants.JSON_TNAME)));
        TypeVertex anyV = new TypeVertex(scope.resolve(new SymbolName(TypeConstants.ANY_TNAME)));
        TypeVertex connectorV = new TypeVertex(scope.resolve(new SymbolName(TypeConstants.CONNECTOR_TNAME)));
        TypeVertex mapV = new TypeVertex(scope.resolve(new SymbolName(TypeConstants.MAP_TNAME)));
        
        explicitCastLattice.addVertex(intV, false);
        explicitCastLattice.addVertex(floatV, false);
        explicitCastLattice.addVertex(booleanV, false);
        explicitCastLattice.addVertex(stringV, false);
        explicitCastLattice.addVertex(xmlV, false);
        explicitCastLattice.addVertex(jsonV, false);
        explicitCastLattice.addVertex(anyV, false);
        explicitCastLattice.addVertex(connectorV, false);

        explicitCastLattice.addEdge(intV, floatV, NativeCastMapper.INT_TO_FLOAT_FUNC);
        explicitCastLattice.addEdge(intV, stringV, NativeCastMapper.INT_TO_STRING_FUNC);
        explicitCastLattice.addEdge(intV, booleanV, NativeCastMapper.INT_TO_BOOLEAN_FUNC);
        explicitCastLattice.addEdge(intV, intV, NativeCastMapper.INT_TO_INT_FUNC);
        explicitCastLattice.addEdge(intV, anyV, NativeCastMapper.INT_TO_ANY_FUNC);
        explicitCastLattice.addEdge(intV, jsonV, NativeCastMapper.INT_TO_JSON_FUNC);
        
        explicitCastLattice.addEdge(floatV, floatV, NativeCastMapper.FLOAT_TO_FLOAT_FUNC);
        explicitCastLattice.addEdge(floatV, stringV, NativeCastMapper.FLOAT_TO_STRING_FUNC);
        explicitCastLattice.addEdge(floatV, booleanV, NativeCastMapper.FLOAT_TO_BOOLEAN_FUNC);
        explicitCastLattice.addEdge(floatV, intV, NativeCastMapper.FLOAT_TO_INT_FUNC);
        explicitCastLattice.addEdge(floatV, anyV, NativeCastMapper.FLOAT_TO_ANY_FUNC);
        explicitCastLattice.addEdge(floatV, jsonV, NativeCastMapper.FLOAT_TO_JSON_FUNC);
        
        explicitCastLattice.addEdge(stringV, floatV, NativeCastMapper.STRING_TO_FLOAT_FUNC);
        explicitCastLattice.addEdge(stringV, stringV, NativeCastMapper.STRING_TO_STRING_FUNC);
        explicitCastLattice.addEdge(stringV, intV, NativeCastMapper.STRING_TO_INT_FUNC);
        explicitCastLattice.addEdge(stringV, anyV, NativeCastMapper.STRING_TO_ANY_FUNC);
        explicitCastLattice.addEdge(stringV, jsonV, NativeCastMapper.STRING_TO_JSON_FUNC);
        
        explicitCastLattice.addEdge(booleanV, stringV, NativeCastMapper.BOOLEAN_TO_STRING_FUNC);
        explicitCastLattice.addEdge(booleanV, booleanV, NativeCastMapper.BOOLEAN_TO_BOOLEAN_FUNC);
        explicitCastLattice.addEdge(booleanV, intV, NativeCastMapper.BOOLEAN_TO_INT_FUNC);
        explicitCastLattice.addEdge(booleanV, floatV, NativeCastMapper.BOOLEAN_TO_FLOAT_FUNC);
        explicitCastLattice.addEdge(booleanV, anyV, NativeCastMapper.BOOLEAN_TO_ANY_FUNC);
        explicitCastLattice.addEdge(booleanV, jsonV, NativeCastMapper.BOOLEAN_TO_JSON_FUNC);
        
        explicitCastLattice.addEdge(connectorV, anyV, NativeCastMapper.CONNECTOR_TO_ANY_FUNC);

        explicitCastLattice.addEdge(anyV, floatV, NativeCastMapper.ANY_TO_FLOAT_FUNC);
        explicitCastLattice.addEdge(anyV, stringV, NativeCastMapper.ANY_TO_STRING_FUNC);
        explicitCastLattice.addEdge(anyV, booleanV, NativeCastMapper.ANY_TO_BOOLEAN_FUNC);
        explicitCastLattice.addEdge(anyV, intV, NativeCastMapper.ANY_TO_INT_FUNC);
        explicitCastLattice.addEdge(anyV, jsonV, NativeCastMapper.ANY_TO_JSON_FUNC);
        explicitCastLattice.addEdge(anyV, xmlV, NativeCastMapper.ANY_TO_XML_FUNC);
        explicitCastLattice.addEdge(anyV, connectorV, NativeCastMapper.ANY_TO_CONNECTOR_FUNC);
        explicitCastLattice.addEdge(anyV, anyV, NativeCastMapper.ANY_TO_ANY_FUNC);
        explicitCastLattice.addEdge(anyV, mapV, NativeCastMapper.ANY_TO_MAP_FUNC);

        explicitCastLattice.addEdge(jsonV, anyV, NativeCastMapper.JSON_TO_ANY_FUNC);
        explicitCastLattice.addEdge(jsonV, stringV, NativeCastMapper.JSON_TO_STRING_FUNC);
        explicitCastLattice.addEdge(jsonV, intV, NativeCastMapper.JSON_TO_INT_FUNC);
        explicitCastLattice.addEdge(jsonV, floatV, NativeCastMapper.JSON_TO_FLOAT_FUNC);
        explicitCastLattice.addEdge(jsonV, booleanV, NativeCastMapper.JSON_TO_BOOLEAN_FUNC);
//        explicitCastLattice.addEdge(jsonV, mapV, NativeCastMapper.JSON_TO_MAP_FUNC);
        
        explicitCastLattice.addEdge(xmlV, anyV, NativeCastMapper.XML_TO_ANY_FUNC);
        
        explicitCastLattice.addEdge(mapV, anyV, NativeCastMapper.MAP_TO_ANY_FUNC);
//        explicitCastLattice.addEdge(mapV, jsonV, NativeCastMapper.MAP_TO_JSON_FUNC);
    }

    /**
     * Merges a given type lattice with the current type lattice
     * @param typeLattice given type lattice
     * @param packageName package name to be merged into
     */
    public void merge(TypeLattice typeLattice, String packageName) {
        for (TypeVertex typeVertex : typeLattice.getVertices()) {
            this.addVertex(typeVertex, false);
        }

        for (TypeEdge typeEdge : typeLattice.getEdges()) {
            if (typeEdge.getTypeMapperFunction() != null) {
                this.addEdge(typeEdge.getSource(), typeEdge.getTarget(), typeEdge.getTypeMapperFunction());
            } else {
                this.addEdge(typeEdge.getSource(), typeEdge.getTarget(), typeEdge.getTypeMapper(),
                        packageName);
            }
        }
    }

    /**
     * Accepts two vertices and a weight, and adds the edge
     * ({one, two}, weight) iff no TypeEdge relating one and two
     * exists in the Graph.
     *
     * @param one           The first TypeVertex of the TypeEdge
     * @param two           The second TypeVertex of the TypeEdge
     * @param typeMapper The weight of the TypeEdge
     * @return true iff no TypeEdge already exists in the Graph
     */
    public boolean addEdge(TypeVertex one, TypeVertex two, BiFunction typeMapper) {

        //ensures the TypeEdge is not in the Graph
        TypeEdge e = new TypeEdge(one, two, typeMapper);
        if (this.edges.containsKey(e.hashCode())) {
            return false;
        } else if (one.containsNeighbor(e) || two.containsNeighbor(e)) {
            return false;
        }

        this.edges.put(e.hashCode(), e);
        one.addNeighbor(e);
        two.addNeighbor(e);
        return true;
    }

    /**
     * Accepts two vertices and a weight, and adds the edge
     * ({one, two}, weight) iff no TypeEdge relating one and two
     * exists in the Graph.
     *
     * @param one The first TypeVertex of the TypeEdge
     * @param two The second TypeVertex of the TypeEdge
     * @param typeMapper The weight of the TypeEdge
     * @param packageName package of the TypeMapper
     * @return true iff no TypeEdge already exists in the Graph
     */
    public boolean addEdge(TypeVertex one, TypeVertex two, TypeMapper typeMapper, String packageName) {

        //ensures the TypeEdge is not in the Graph
        TypeEdge e = new TypeEdge(one, two, typeMapper, packageName);
        if (this.edges.containsKey(e.hashCode())) {
            return false;
        } else if (one.containsNeighbor(e) || two.containsNeighbor(e)) {
            return false;
        }

        this.edges.put(e.hashCode(), e);
        one.addNeighbor(e);
        two.addNeighbor(e);
        return true;
    }

    public TypeEdge getEdgeFromTypes(BLangSymbol source, BLangSymbol target, String packageName) {
        TypeEdge result;
        // First check within the package
        result = this.edges.get((source.toString() + target.toString() + packageName).hashCode());
        if (result == null) {
            result = this.edges.get((packageName + ":" + source.toString() + packageName + ":" +
                    target.toString() + packageName).hashCode());
        }
        if (result == null) {
            // If not found, check in native type typemappers
            packageName = TypeConstants.NATIVE_PACKAGE;
            result = this.edges.get((source.toString() + target.toString() + packageName).hashCode());
        }
        return result;
    }

    /**
     * This method removes the specified TypeEdge from the Graph,
     * including as each vertex's incidence neighborhood.
     *
     * @param e The TypeEdge to remove from the Graph
     * @return TypeEdge The TypeEdge removed from the Graph
     */
    public TypeEdge removeEdge(TypeEdge e) {
        e.getSource().removeNeighbor(e);
        e.getTarget().removeNeighbor(e);
        return this.edges.remove(e.hashCode());
    }

    /**
     * This method adds a TypeVertex to the graph. If a TypeVertex with the same label
     * as the parameter exists in the Graph, the existing TypeVertex is overwritten
     * only if overwriteExisting is true. If the existing TypeVertex is overwritten,
     * the Edges incident to it are all removed from the Graph.
     *
     * @param vertex {@link TypeVertex} to add
     * @param overwriteExisting flag indicating whetehr to overide the vertex, if already exists with the same name
     * @return true iff vertex was added to the Graph
     */
    public boolean addVertex(TypeVertex vertex, boolean overwriteExisting) {
        TypeVertex current = this.vertices.get(vertex.toString());
        if (current != null) {
            if (!overwriteExisting) {
                return false;
            }

            while (current.getNeighborCount() > 0) {
                removeEdge(current.getNeighbor(0));
            }
        }

        this.vertices.put(vertex.toString(), vertex);
        return true;
    }

    /**
     * @return Set &lt;TypeEdge&gt; The Edges of this graph
     */
    public Set<TypeEdge> getEdges() {
        return new HashSet<TypeEdge>(this.edges.values());
    }

    /**
     * @return Set &lt;TypeVertex&gt; The Vertices of this graph
     */
    public Set<TypeVertex> getVertices() {
        return new HashSet<TypeVertex>(this.vertices.values());
    }
    
    /**
     * Add edges to type cast lattice for dynamically defined types structs.
     * This method will add edges from the current struct to map-type, json-type,
     * and all other struct-types, and the vice-versa.
     * 
     * @param structDef {@link StructDef} of the dynamically defined struct type
     * @param scope scope of the defined type
     */
    public static void addStructEdges(StructDef structDef, SymbolScope scope) {
        addImplicitEdges(structDef, scope);
        addExplicitEdges(structDef, scope);
    }
    
    private static void addImplicitEdges(StructDef structDef, SymbolScope scope) {
        TypeVertex structV = new TypeVertex(structDef);
//        TypeVertex mapV = new TypeVertex(scope.resolve(new SymbolName(TypeConstants.MAP_TNAME)));
//        TypeVertex jsonV = new TypeVertex(scope.resolve(new SymbolName(TypeConstants.JSON_TNAME)));
        
        implicitCastLattice.addVertex(structV, false);
//        implicitCastLattice.addEdge(structV, mapV, NativeCastMapper.STRUCT_TO_MAP_FUNC);
//        implicitCastLattice.addEdge(structV, jsonV, NativeCastMapper.STRUCT_TO_JSON_FUNC);
//        implicitCastLattice.addEdge(jsonV, structV, NativeCastMapper.JSON_TO_STRUCT_FUNC);
//        implicitCastLattice.addEdge(mapV, structV, NativeCastMapper.MAP_TO_STRUCT_FUNC);
        
        // For all the structs in all the packages imported, check for possibility of casting.
        // Add an edge to the lattice, if casting is possible.
        for (Entry<SymbolName, BLangSymbol> pkg : scope.getEnclosingScope().getSymbolMap().entrySet()) {
            BLangSymbol pkgSymbol = pkg.getValue();
            if (!(pkgSymbol instanceof BLangPackage)) {
                continue;
            }
            for (Entry<SymbolName, BLangSymbol> entry : ((BLangPackage) pkgSymbol).getSymbolMap().entrySet()) {
                BLangSymbol symbol = entry.getValue();
                if (symbol instanceof StructDef && symbol != structDef) {
                    TypeVertex otherStructV = new TypeVertex(symbol);
                    
                    if (isAssignCompatible(structDef, (StructDef) symbol)) {
                        implicitCastLattice.addEdge(otherStructV, structV, NativeCastMapper.STRUCT_TO_STRUCT_FUNC);
                    }
                    
                    if (isAssignCompatible((StructDef) symbol, structDef)) {
                        implicitCastLattice.addEdge(structV, otherStructV, NativeCastMapper.STRUCT_TO_STRUCT_FUNC);
                    }
                }
            }
        }
    }
    
    private static void addExplicitEdges(StructDef structDef, SymbolScope scope) {
        TypeVertex structV = new TypeVertex(structDef);
        TypeVertex anyV = new TypeVertex(scope.resolve(new SymbolName(TypeConstants.ANY_TNAME)));
//        TypeVertex mapV = new TypeVertex(scope.resolve(new SymbolName(TypeConstants.MAP_TNAME)));
//        TypeVertex jsonV = new TypeVertex(scope.resolve(new SymbolName(TypeConstants.JSON_TNAME)));
        
        explicitCastLattice.addVertex(structV, false);
//        explicitCastLattice.addEdge(structV, mapV, NativeCastMapper.STRUCT_TO_MAP_FUNC);
//        explicitCastLattice.addEdge(structV, jsonV, NativeCastMapper.STRUCT_TO_JSON_FUNC);
//        explicitCastLattice.addEdge(jsonV, structV, NativeCastMapper.JSON_TO_STRUCT_FUNC);
//        explicitCastLattice.addEdge(mapV, structV, NativeCastMapper.MAP_TO_STRUCT_FUNC);
        explicitCastLattice.addEdge(anyV, structV, NativeCastMapper.ANY_TO_STRUCT_FUNC);
        explicitCastLattice.addEdge(structV, anyV, NativeCastMapper.STRUCT_TO_ANY_FUNC);
        
        // For all the structs in all the packages imported, check for possibility of casting.
        // Add an edge to the lattice, if casting is possible.
        for (Entry<SymbolName, BLangSymbol> pkg : scope.getEnclosingScope().getSymbolMap().entrySet()) {
            BLangSymbol pkgSymbol = pkg.getValue();
            if (!(pkgSymbol instanceof BLangPackage)) {
                continue;
            }
            for (Entry<SymbolName, BLangSymbol> entry : ((BLangPackage) pkgSymbol).getSymbolMap().entrySet()) {
                BLangSymbol symbol = entry.getValue();
                if (symbol instanceof StructDef && symbol != structDef) {
                    TypeVertex otherStructV = new TypeVertex(symbol);
                    
                    if (isAssignCompatible(structDef, (StructDef) symbol)) {
                        explicitCastLattice.addEdge(otherStructV, structV, NativeCastMapper.STRUCT_TO_STRUCT_FUNC);
                    }
                    
                    if (isAssignCompatible((StructDef) symbol, structDef)) {
                        explicitCastLattice.addEdge(structV, otherStructV, NativeCastMapper.STRUCT_TO_STRUCT_FUNC);
                    }
                }
            }
        }
    }
    
    private static boolean isAssignCompatible(StructDef targetStructDef, StructDef sourceStructDef) {
        for (VariableDefStmt fieldDef : targetStructDef.getFieldDefStmts()) {
            VariableDef targetFieldDef = fieldDef.getVariableDef();
            BType targetFieldType = targetFieldDef.getType();
            SymbolName fieldSymbolName = targetFieldDef.getSymbolName();
            VariableDef sourceFieldDef = (VariableDef) sourceStructDef
                    .resolveMembers(new SymbolName(fieldSymbolName.getName(), sourceStructDef.getPackagePath()));

            // field has to exists
            if (sourceFieldDef == null) {
                return false;
            }
            
            // struct memory index of both the fields has to be same. i.e: order of the fields
            // must be same in the target and the source structs
            if (((StructVarLocation) targetFieldDef.getMemoryLocation()).getStructMemAddrOffset() != 
                    ((StructVarLocation) sourceFieldDef.getMemoryLocation()).getStructMemAddrOffset()) {
                return false;
            }
            
            BType sourceFieldType = sourceFieldDef.getType();
            if (!NativeCastMapper.isCompatible(targetFieldType, sourceFieldType)) {
                return false;
            }
        }
        return true;
    }
}
