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
package org.apache.nifi.web.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.nifi.controller.ScheduledState;
import org.apache.nifi.controller.exception.ControllerServiceInstantiationException;

import org.apache.nifi.controller.exception.ValidationException;
import org.apache.nifi.controller.service.ControllerServiceNode;
import org.apache.nifi.controller.service.ControllerServiceProvider;
import org.apache.nifi.controller.service.ControllerServiceReference;
import org.apache.nifi.controller.service.ControllerServiceState;
import org.apache.nifi.web.NiFiCoreException;
import org.apache.nifi.web.ResourceNotFoundException;
import org.apache.nifi.web.api.dto.ControllerServiceDTO;
import org.apache.nifi.web.dao.ControllerServiceDAO;

public class StandardControllerServiceDAO extends ComponentDAO implements ControllerServiceDAO {

    private ControllerServiceProvider serviceProvider;

    /**
     * Locates the specified controller service.
     *
     * @param controllerServiceId
     * @return
     */
    private ControllerServiceNode locateControllerService(final String controllerServiceId) {
        // get the controller service
        final ControllerServiceNode controllerService = serviceProvider.getControllerServiceNode(controllerServiceId);

        // ensure the controller service exists
        if (controllerService == null) {
            throw new ResourceNotFoundException(String.format("Unable to locate controller service with id '%s'.", controllerServiceId));
        }

        return controllerService;
    }

    /**
     * Creates a controller service.
     *
     * @param controllerServiceDTO The controller service DTO
     * @return The controller service
     */
    @Override
    public ControllerServiceNode createControllerService(final ControllerServiceDTO controllerServiceDTO) {
        // ensure the type is specified
        if (controllerServiceDTO.getType() == null) {
            throw new IllegalArgumentException("The controller service type must be specified.");
        }
        
        try {
            // create the controller service
            final ControllerServiceNode controllerService = serviceProvider.createControllerService(controllerServiceDTO.getType(), controllerServiceDTO.getId(), true);

            // ensure we can perform the update 
            verifyUpdate(controllerService, controllerServiceDTO);

            // perform the update
            configureControllerService(controllerService, controllerServiceDTO);

            return controllerService;
        } catch (final ControllerServiceInstantiationException csie) {
            throw new NiFiCoreException(csie.getMessage(), csie);
        }
    }

    /**
     * Gets the specified controller service.
     *
     * @param controllerServiceId The controller service id
     * @return The controller service
     */
    @Override
    public ControllerServiceNode getControllerService(final String controllerServiceId) {
        return locateControllerService(controllerServiceId);
    }

    /**
     * Determines if the specified controller service exists.
     *
     * @param controllerServiceId
     * @return
     */
    @Override
    public boolean hasControllerService(final String controllerServiceId) {
        return serviceProvider.getControllerServiceNode(controllerServiceId) != null;
    }

    /**
     * Gets all of the controller services.
     *
     * @return The controller services
     */
    @Override
    public Set<ControllerServiceNode> getControllerServices() {
        return serviceProvider.getAllControllerServices();
    }

    /**
     * Updates the specified controller service.
     *
     * @param controllerServiceDTO The controller service DTO
     * @return The controller service
     */
    @Override
    public ControllerServiceNode updateControllerService(final ControllerServiceDTO controllerServiceDTO) {
        // get the controller service
        final ControllerServiceNode controllerService = locateControllerService(controllerServiceDTO.getId());
        
        // ensure we can perform the update 
        verifyUpdate(controllerService, controllerServiceDTO);
        
        // perform the update
        configureControllerService(controllerService, controllerServiceDTO);

        // enable or disable as appropriate
        if (isNotNull(controllerServiceDTO.getState())) {
            final ControllerServiceState purposedControllerServiceState = ControllerServiceState.valueOf(controllerServiceDTO.getState());

            // only attempt an action if it is changing
            if (!purposedControllerServiceState.equals(controllerService.getState())) {
                if (ControllerServiceState.ENABLED.equals(purposedControllerServiceState)) {
                    serviceProvider.enableControllerService(controllerService);
                } else if (ControllerServiceState.DISABLED.equals(purposedControllerServiceState)) {
                    serviceProvider.disableControllerService(controllerService);
                }
            }
        }
        
        return controllerService;
    }

    @Override
    public ControllerServiceReference updateControllerServiceReferencingComponents(final String controllerServiceId, final ScheduledState scheduledState, final ControllerServiceState controllerServiceState) {
        // get the controller service
        final ControllerServiceNode controllerService = locateControllerService(controllerServiceId);
        
        // this request is either acting upon referncing services or schedulable components
        if (controllerServiceState != null) {
            if (ControllerServiceState.ENABLED.equals(controllerServiceState)) {
                serviceProvider.enableReferencingServices(controllerService);
            } else {
                serviceProvider.disableReferencingServices(controllerService);
            }
        } else if (scheduledState != null) {
            if (ScheduledState.RUNNING.equals(scheduledState)) {
                serviceProvider.scheduleReferencingComponents(controllerService);
            } else {
                serviceProvider.unscheduleReferencingComponents(controllerService);
            }
        }
        
        return controllerService.getReferences();
    }

    /**
     * Validates the specified configuration for the specified controller service.
     * 
     * @param controllerService
     * @param controllerServiceDTO
     * @return 
     */
    private List<String> validateProposedConfiguration(final ControllerServiceNode controllerService, final ControllerServiceDTO controllerServiceDTO) {
        final List<String> validationErrors = new ArrayList<>();
        return validationErrors;
    }
    
    @Override
    public void verifyDelete(final String controllerServiceId) {
        final ControllerServiceNode controllerService = locateControllerService(controllerServiceId);
        controllerService.verifyCanDelete();
    }

    @Override
    public void verifyUpdate(final ControllerServiceDTO controllerServiceDTO) {
        final ControllerServiceNode controllerService = locateControllerService(controllerServiceDTO.getId());
        verifyUpdate(controllerService, controllerServiceDTO);
    }

    @Override
    public void verifyUpdateReferencingComponents(String controllerServiceId, ScheduledState scheduledState, ControllerServiceState controllerServiceState) {
        final ControllerServiceNode controllerService = locateControllerService(controllerServiceId);
        
        if (controllerServiceState != null) {
            if (ControllerServiceState.ENABLED.equals(controllerServiceState)) {
                serviceProvider.verifyCanEnableReferencingServices(controllerService);
            } else {
                serviceProvider.verifyCanDisableReferencingServices(controllerService);
            }
        } else if (scheduledState != null) {
            if (ScheduledState.RUNNING.equals(scheduledState)) {
                serviceProvider.verifyCanScheduleReferencingComponents(controllerService);
            } else {
                serviceProvider.verifyCanStopReferencingComponents(controllerService);
            }
        }
    }
    
    /**
     * Verifies the controller service can be updated.
     * 
     * @param controllerService
     * @param controllerServiceDTO 
     */
    private void verifyUpdate(final ControllerServiceNode controllerService, final ControllerServiceDTO controllerServiceDTO) {
        // validate the new controller service state if appropriate
        if (isNotNull(controllerServiceDTO.getState())) {
            try {
                // attempt to parse the service state
                final ControllerServiceState purposedControllerServiceState = ControllerServiceState.valueOf(controllerServiceDTO.getState());
                
                // ensure the state is valid
                if (ControllerServiceState.ENABLING.equals(purposedControllerServiceState) || ControllerServiceState.DISABLING.equals(purposedControllerServiceState)) {
                    throw new IllegalArgumentException();
                }
                
                // only attempt an action if it is changing
                if (!purposedControllerServiceState.equals(controllerService.getState())) {
                    if (ControllerServiceState.ENABLED.equals(purposedControllerServiceState)) {
                        controllerService.verifyCanEnable();
                    } else if (ControllerServiceState.DISABLED.equals(purposedControllerServiceState)) {
                        controllerService.verifyCanDisable();
                    }
                }
            } catch (IllegalArgumentException iae) {
                throw new IllegalArgumentException("Controller Service state: Value must be one of [ENABLED, DISABLED]");
            }
        }
        
        boolean modificationRequest = false;
        if (isAnyNotNull(controllerServiceDTO.getName(),
                controllerServiceDTO.getAnnotationData(),
                controllerServiceDTO.getComments(),
                controllerServiceDTO.getProperties())) {
            modificationRequest = true;
            
            // validate the request
            final List<String> requestValidation = validateProposedConfiguration(controllerService, controllerServiceDTO);

            // ensure there was no validation errors
            if (!requestValidation.isEmpty()) {
                throw new ValidationException(requestValidation);
            }
        }
        
        if (modificationRequest) {
            controllerService.verifyCanUpdate();
        }
    }
    
    /**
     * Configures the specified controller service.
     * 
     * @param controllerService
     * @param controllerServiceDTO 
     */
    private void configureControllerService(final ControllerServiceNode controllerService, final ControllerServiceDTO controllerServiceDTO) {
        final String name = controllerServiceDTO.getName();
        final String annotationData = controllerServiceDTO.getAnnotationData();
        final String comments = controllerServiceDTO.getComments();
        final Map<String, String> properties = controllerServiceDTO.getProperties();
        
        if (isNotNull(name)) {
            controllerService.setName(name);
        }
        if (isNotNull(annotationData)) {
            controllerService.setAnnotationData(annotationData);
        }
        if (isNotNull(comments)) {
            controllerService.setComments(comments);
        }
        if (isNotNull(properties)) {
            for (final Map.Entry<String, String> entry : properties.entrySet()) {
                final String propName = entry.getKey();
                final String propVal = entry.getValue();
                if (isNotNull(propName) && propVal == null) {
                    controllerService.removeProperty(propName);
                } else if (isNotNull(propName)) {
                    controllerService.setProperty(propName, propVal);
                }
            }
        }
    }
    
    /**
     * Deletes the specified controller service.
     *
     * @param controllerServiceId The controller service id
     */
    @Override
    public void deleteControllerService(String controllerServiceId) {
        final ControllerServiceNode controllerService = locateControllerService(controllerServiceId);
        serviceProvider.removeControllerService(controllerService);
    }

    /* setters */
    public void setServiceProvider(ControllerServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }
}
