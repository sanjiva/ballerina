/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.ballerinalang.compiler;

import org.ballerinalang.model.elements.PackageID;
import org.ballerinalang.repository.AggregatedPackageRepository;
import org.ballerinalang.repository.CompositePackageRepository;
import org.ballerinalang.repository.PackageEntity;
import org.ballerinalang.repository.PackageRepository;
import org.ballerinalang.repository.PackageSource;
import org.ballerinalang.repository.fs.LocalFSPackageRepository;
import org.ballerinalang.spi.ExtensionPackageRepositoryProvider;
import org.ballerinalang.spi.SystemPackageRepositoryProvider;
import org.ballerinalang.spi.UserRepositoryProvider;
import org.wso2.ballerinalang.compiler.parser.Parser;
import org.wso2.ballerinalang.compiler.semantics.analyzer.SymbolEnter;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BPackageSymbol;
import org.wso2.ballerinalang.compiler.tree.BLangIdentifier;
import org.wso2.ballerinalang.compiler.tree.BLangPackage;
import org.wso2.ballerinalang.compiler.util.CompilerContext;
import org.wso2.ballerinalang.compiler.util.CompilerOptions;
import org.wso2.ballerinalang.compiler.util.Name;
import org.wso2.ballerinalang.compiler.util.Names;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

import static org.ballerinalang.compiler.CompilerOptionName.SOURCE_ROOT;

/**
 * This class contains methods to load a given package symbol.
 * It knows how to load source package as well as a package from a compiled version (.balo).
 *
 * @since 0.94
 */
public class PackageLoader {

    private static final CompilerContext.Key<PackageLoader> PACKAGE_LOADER_KEY =
            new CompilerContext.Key<>();

    private CompilerOptions options;
    private Parser parser;
    private SymbolEnter symbolEnter;
    private Names names;

    private Map<PackageID, BPackageSymbol> packages;
    private PackageRepository packageRepo;

    public static PackageLoader getInstance(CompilerContext context) {
        PackageLoader loader = context.get(PACKAGE_LOADER_KEY);
        if (loader == null) {
            loader = new PackageLoader(context);
        }

        return loader;
    }

    public PackageLoader(CompilerContext context) {
        context.put(PACKAGE_LOADER_KEY, this);

        this.options = CompilerOptions.getInstance(context);
        this.parser = Parser.getInstance(context);
        this.symbolEnter = SymbolEnter.getInstance(context);
        this.names = Names.getInstance(context);

        this.packages = new HashMap<>();
        loadPackageRepository(context);
    }

    public BLangPackage loadEntryPackage(String sourcePkg) {
        if (sourcePkg == null || sourcePkg.isEmpty()) {
            throw new IllegalArgumentException("source package/file cannot be null");
        }

        PackageEntity pkgEntity;
        PackageID pkgId = PackageID.DEFAULT;
        if (sourcePkg.endsWith(PackageEntity.Kind.SOURCE.getExtension())) {
            pkgEntity = this.packageRepo.loadPackage(pkgId, sourcePkg);
        } else {
            // split from '.', '\' and '/'
            String[] pkgParts = sourcePkg.split("\\.|\\\\|\\/");
            List<Name> pkgNameComps = Arrays.stream(pkgParts)
                    .map(part -> names.fromString(part))
                    .collect(Collectors.toList());

            pkgId = new PackageID(pkgNameComps, Names.DEFAULT_VERSION);
            pkgEntity = this.packageRepo.loadPackage(pkgId);
        }
        
        if (pkgEntity == null) {
            throw new IllegalArgumentException("valid package not available at '" + sourcePkg + "'");
        }

        return loadPackage(pkgId, pkgEntity);
    }

    public BLangPackage loadPackage(List<BLangIdentifier> pkgNameComps, BLangIdentifier version) {
        List<Name> nameComps = pkgNameComps.stream()
                .map(identifier -> names.fromIdNode(identifier))
                .collect(Collectors.toList());
        PackageID pkgID = new PackageID(nameComps, names.fromIdNode(version));
        return loadPackage(pkgID, this.packageRepo.loadPackage(pkgID));
    }

    public BPackageSymbol getPackageSymbol(PackageID pkgId) {
        return packages.get(pkgId);
    }

    public BLangPackage loadPackageNode(PackageID pkgId) {
        return loadPackage(pkgId, this.packageRepo.loadPackage(pkgId));
    }

    /**
     * List all the packages of packageRepo.
     *
     * @param maxDepth the maximum depth of directories to search in
     * @return a set of PackageIDs
     */
    public Set<PackageID> listPackages(int maxDepth) {
        return this.packageRepo.listPackages(maxDepth);
    }

    private BLangPackage loadPackage(PackageID pkgId, PackageEntity pkgEntity) {
        BLangPackage pkgNode;
        BPackageSymbol pSymbol;

        if (pkgEntity == null) {
            return null;
        }

        if (pkgEntity.getKind() == PackageEntity.Kind.SOURCE) {
            pkgNode = this.sourceCompile((PackageSource) pkgEntity);
            if (pkgNode == null) {
                return null;
            }
            pSymbol = symbolEnter.definePackage(pkgNode, pkgId);
            pkgNode.symbol = pSymbol;
        } else {
            // This is a compiled package.
            // TODO Throw an error. Entry package cannot be a compiled package
            throw new RuntimeException("TODO Entry package cannot be a compiled package");
        }

        packages.put(pkgId, pSymbol);
        return pkgNode;
    }

    private BLangPackage sourceCompile(PackageSource pkgSource) {
        BLangPackage pkgNode = this.parser.parse(pkgSource);
        return pkgNode;
    }

    private void loadPackageRepository(CompilerContext context) {
        // Initialize program dir repository a.k.a entry package repository
        PackageRepository programRepo = context.get(PackageRepository.class);
        if (programRepo == null) {
            // create the default program repo
            String sourceRoot = options.get(SOURCE_ROOT);
            programRepo = new LocalFSPackageRepository(sourceRoot);
        }

        PackageRepository systemRepo = this.loadSystemRepository();
        this.packageRepo = new CompositePackageRepository(systemRepo, this.loadUserRepository(systemRepo), programRepo);
    }

    private PackageRepository loadSystemRepository() {
        ServiceLoader<SystemPackageRepositoryProvider> loader = ServiceLoader.load(
                SystemPackageRepositoryProvider.class);
        AggregatedPackageRepository repo = new AggregatedPackageRepository();
        loader.forEach(e -> repo.addRepository(e.loadRepository()));
        return repo;
    }

    private PackageRepository loadExtensionRepository() {
        ServiceLoader<ExtensionPackageRepositoryProvider> loader = ServiceLoader.load(
                ExtensionPackageRepositoryProvider.class);
        AggregatedPackageRepository repo = new AggregatedPackageRepository();
        loader.forEach(e -> repo.addRepository(e.loadRepository()));
        return repo;
    }

    private PackageRepository loadUserRepository(PackageRepository systemRepo) {
        ServiceLoader<UserRepositoryProvider> loader = ServiceLoader.load(UserRepositoryProvider.class);
        AggregatedPackageRepository userRepo = new AggregatedPackageRepository();
        loader.forEach(e -> userRepo.addRepository(e.loadRepository()));

        return new CompositePackageRepository(systemRepo, this.loadExtensionRepository(), userRepo);
    }
}
