/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.eventmesh.runtime.storage;

import org.apache.eventmesh.api.storage.StorageResourceService;
import org.apache.eventmesh.runtime.lifecircle.EventMeshComponent;
import org.apache.eventmesh.spi.EventMeshExtensionFactory;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StorageResource extends EventMeshComponent {

    private static final Map<String, StorageResource> STORAGE_RESOURCE_CACHE = new HashMap<>(16);

    private StorageResourceService storageResourceService;

    private StorageResource() {

    }

    public static StorageResource getInstance(String storageResourcePluginType) {
        return STORAGE_RESOURCE_CACHE.computeIfAbsent(storageResourcePluginType, StorageResource::storageResourceBuilder);
    }

    private static StorageResource storageResourceBuilder(String storageResourcePluginType) {
        StorageResourceService storageResourceServiceExt = EventMeshExtensionFactory.getExtension(StorageResourceService.class,
            storageResourcePluginType);
        if (storageResourceServiceExt == null) {
            String errorMsg = "can't load the StorageResourceService plugin, please check.";
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        StorageResource storageResource = new StorageResource();
        storageResource.storageResourceService = storageResourceServiceExt;
        return storageResource;
    }

    @Override
    protected void componentInit() throws Exception {
        storageResourceService.init();
    }

    @Override
    protected void componentStart() throws Exception {
        log.debug("StorageResource Component Starting...");
    }

    @Override
    protected void componentStop() throws Exception {
        storageResourceService.release();
    }

}
