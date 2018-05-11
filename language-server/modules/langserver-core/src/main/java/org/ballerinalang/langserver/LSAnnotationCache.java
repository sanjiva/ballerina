/*
 * Copyright (c) 2018, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ballerinalang.langserver;

import org.ballerinalang.langserver.compiler.LSContextManager;
import org.ballerinalang.langserver.compiler.LSPackageCache;
import org.ballerinalang.langserver.compiler.LSPackageLoader;
import org.ballerinalang.langserver.compiler.common.modal.BallerinaPackage;
import org.ballerinalang.model.AttachmentPoint;
import org.ballerinalang.model.elements.PackageID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ballerinalang.compiler.tree.BLangAnnotation;
import org.wso2.ballerinalang.compiler.tree.BLangPackage;
import org.wso2.ballerinalang.compiler.util.CompilerContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Annotation cache for Language server.
 * 
 * Note: Annotation cache should be synced with the LS Package Cache
 * 
 * @since 0.970.0
 */
public class LSAnnotationCache {

    private static final Logger logger = LoggerFactory.getLogger(LSPackageCache.class);

    private static HashMap<PackageID, List<BLangAnnotation>> serviceAnnotations = new HashMap<>();
    private static HashMap<PackageID, List<BLangAnnotation>> resourceAnnotations = new HashMap<>();
    private static HashMap<PackageID, List<BLangAnnotation>> functionAnnotations = new HashMap<>();
    private static LSAnnotationCache lsAnnotationCache = null;
    
    private LSAnnotationCache() {
    }
    
    public static LSAnnotationCache getInstance() {
        return lsAnnotationCache;
    }
    
    public static synchronized void initiate() {
        if (lsAnnotationCache == null) {
            lsAnnotationCache = new LSAnnotationCache();
            CompilerContext context = LSContextManager.getInstance().getBuiltInPackagesCompilerContext();
            new Thread(() -> {
                Map<String, BLangPackage> packages = loadPackagesMap(context);
                loadAnnotations(new ArrayList<>(packages.values()));
            }).start();
        }
    }

    private static Map<String, BLangPackage> loadPackagesMap(CompilerContext tempCompilerContext) {
        Map<String, BLangPackage> staticPackages = new HashMap<>();

        // Annotation cache will only load the sk packages initially and the others will load in the runtime
        for (BallerinaPackage sdkPackage : LSPackageLoader.getSdkPackages()) {
            PackageID packageID = new PackageID(new org.wso2.ballerinalang.compiler.util.Name(sdkPackage.getOrgName()),
                    new org.wso2.ballerinalang.compiler.util.Name(sdkPackage.getPackageName()),
                    new org.wso2.ballerinalang.compiler.util.Name(sdkPackage.getPackageName()));
            try {
                // We will wrap this with a try catch to prevent LS crashing due to compiler errors.
                BLangPackage bLangPackage = LSPackageLoader.getPackageById(tempCompilerContext, packageID);
                staticPackages.put(bLangPackage.packageID.bvmAlias(), bLangPackage);
            } catch (Exception e) {
                logger.warn("Error while loading package :" + sdkPackage.getPackageName());
            }
        }

        return staticPackages;
    }
    
    private static void loadAnnotations(List<BLangPackage> packageList) {
        packageList.forEach(LSAnnotationCache::loadAnnotationsFromPackage);
    }
    
    private static void addAttachment(BLangAnnotation bLangAnnotation, HashMap<PackageID, List<BLangAnnotation>> map,
                               PackageID packageID) {
        if (map.containsKey(packageID)) {
            map.get(packageID).add(bLangAnnotation);
            return;
        }
        map.put(packageID, new ArrayList<>(Collections.singletonList(bLangAnnotation)));
    }

    public HashMap<PackageID, List<BLangAnnotation>> getServiceAnnotations() {
        return serviceAnnotations;
    }

    public HashMap<PackageID, List<BLangAnnotation>> getResourceAnnotations() {
        return resourceAnnotations;
    }

    public HashMap<PackageID, List<BLangAnnotation>> getFunctionAnnotations() {
        return functionAnnotations;
    }

    /**
     * Get the annotation map for the given type.
     * @param attachmentPoint   Attachment point
     * @return {@link HashMap}  Map of annotation lists
     */
    public HashMap<PackageID, List<BLangAnnotation>> getAnnotationMapForType(AttachmentPoint attachmentPoint) {
        HashMap<PackageID, List<BLangAnnotation>> annotationMap;
        if (attachmentPoint == null) {
            // TODO: Here return the common annotations
            annotationMap = new HashMap<>();
        } else {
            switch (attachmentPoint) {
                case SERVICE:
                    annotationMap = serviceAnnotations;
                    break;
                case RESOURCE:
                    annotationMap = resourceAnnotations;
                    break;
                case FUNCTION:
                    annotationMap = functionAnnotations;
                    break;
                default:
                    annotationMap = new HashMap<>();
                    break;
            }
        }

        return annotationMap;
    }

    /**
     * Check whether annotations have been loaded from a given package.
     * @param packageID         Package ID to check against
     * @return {@link Boolean}  whether annotations are loaded or not
     */
    public static boolean containsAnnotationsForPackage(PackageID packageID) {
        return containsPackageWithBvmAlias(packageID, serviceAnnotations)
                || containsPackageWithBvmAlias(packageID, resourceAnnotations)
                || containsPackageWithBvmAlias(packageID, functionAnnotations);
    }

    /**
     * Load annotations from the package.
     * @param bLangPackage      BLang Package to load annotations
     */
    public static void loadAnnotationsFromPackage(BLangPackage bLangPackage) {
        bLangPackage.getAnnotations().forEach(bLangAnnotation -> {
            bLangAnnotation.getAttachmentPoints().forEach(attachmentPoint -> {
                switch (attachmentPoint.attachmentPoint) {
                    case SERVICE:
                        addAttachment(bLangAnnotation, serviceAnnotations, bLangPackage.packageID);
                        break;
                    case RESOURCE:
                        addAttachment(bLangAnnotation, resourceAnnotations, bLangPackage.packageID);
                        break;
                    case FUNCTION:
                        addAttachment(bLangAnnotation, functionAnnotations, bLangPackage.packageID);
                        break;
                    default:
                        break;
                }
            });
        });
    }
    
    private static boolean containsPackageWithBvmAlias(PackageID packageID, HashMap<PackageID,
            List<BLangAnnotation>> map) {
        return map.entrySet().stream()
                .filter(entry -> entry.getKey().bvmAlias().equals(packageID.bvmAlias()))
                .findFirst()
                .orElse(null) != null;
    }
}
