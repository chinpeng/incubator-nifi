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
package org.apache.nifi.controller.service;

import java.util.Set;

import org.apache.nifi.controller.ControllerService;
import org.apache.nifi.controller.ControllerServiceInitializationContext;
import org.apache.nifi.controller.ControllerServiceLookup;
import org.apache.nifi.logging.ComponentLog;

public class StandardControllerServiceInitializationContext implements ControllerServiceInitializationContext, ControllerServiceLookup {

    private final String id;
    private final ControllerServiceProvider serviceProvider;
    private final ComponentLog logger;

    public StandardControllerServiceInitializationContext(final String identifier, final ComponentLog logger, final ControllerServiceProvider serviceProvider) {
        this.id = identifier;
        this.logger = logger;
        this.serviceProvider = serviceProvider;
    }

    @Override
    public String getIdentifier() {
        return id;
    }

    @Override
    public ControllerService getControllerService(final String identifier) {
        return serviceProvider.getControllerService(identifier);
    }

    @Override
    public Set<String> getControllerServiceIdentifiers(final Class<? extends ControllerService> serviceType) {
        return serviceProvider.getControllerServiceIdentifiers(serviceType);
    }

    @Override
    public ControllerServiceLookup getControllerServiceLookup() {
        return this;
    }

    @Override
    public boolean isControllerServiceEnabled(final String serviceIdentifier) {
        return serviceProvider.isControllerServiceEnabled(serviceIdentifier);
    }

    @Override
    public boolean isControllerServiceEnabled(final ControllerService service) {
        return serviceProvider.isControllerServiceEnabled(service);
    }
    
    @Override
    public boolean isControllerServiceEnabling(String serviceIdentifier) {
        return serviceProvider.isControllerServiceEnabling(serviceIdentifier);
    }
    
    @Override
    public String getControllerServiceName(final String serviceIdentifier) {
    	return serviceProvider.getControllerServiceName(serviceIdentifier);
    }

    @Override
    public ComponentLog getLogger() {
        return logger;
    }
}
