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
package org.apache.nifi.web.api.dto;

import java.util.List;
import javax.xml.bind.annotation.XmlType;

/**
 * A description of a property.
 */
@XmlType(name = "propertyDescriptor")
public class PropertyDescriptorDTO {

    private String name;
    private String displayName;
    private String description;
    private String defaultValue;
    private List<AllowableValueDTO> allowableValues;
    private boolean required;
    private boolean sensitive;
    private boolean dynamic;
    private boolean supportsEl;
    private boolean identifiesControllerService;

    /**
     * The set of allowable values for this property. If empty then the
     * allowable values are not constrained.
     *
     * @return
     */
    public List<AllowableValueDTO> getAllowableValues() {
        return allowableValues;
    }

    public void setAllowableValues(List<AllowableValueDTO> allowableValues) {
        this.allowableValues = allowableValues;
    }

    /**
     * The default value for this property.
     *
     * @return
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * And explanation of the meaning of the given property. This
     * description is meant to be displayed to a user or simply provide a
     * mechanism of documenting intent.
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The property name.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The human-readable name to display to users.
     *
     * @return
     */
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Determines whether the property is required for this processor.
     *
     * @return
     */
    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * Indicates that the value for this property should be considered
     * sensitive and protected whenever stored or represented.
     *
     * @return
     */
    public boolean isSensitive() {
        return sensitive;
    }

    public void setSensitive(boolean sensitive) {
        this.sensitive = sensitive;
    }

    /**
     * Indicates whether this property is dynamic.
     *
     * @return
     */
    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    /**
     * Specifies whether or not this property support expression language.
     *
     * @return
     */
    public boolean getSupportsEl() {
        return supportsEl;
    }

    public void setSupportsEl(boolean supportsEl) {
        this.supportsEl = supportsEl;
    }

    /**
     * Whether this descriptor represents a controller service.
     * 
     * @return 
     */
    public boolean isIdentifiesControllerService() {
        return identifiesControllerService;
    }

    public void setIdentifiesControllerService(boolean identifiesControllerService) {
        this.identifiesControllerService = identifiesControllerService;
    }
    
    /**
     * The allowable values for a property with a constrained set of options.
     */
    @XmlType(name = "allowableValue")
    public static class AllowableValueDTO {

        private String displayName;
        private String value;
        private String description;

        /**
         * Returns the human-readable value that is allowed for this
         * PropertyDescriptor
         *
         * @return
         */
        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        /**
         * Returns the value for this allowable value.
         *
         * @return
         */
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Returns a description of this Allowable Value, or <code>null</code>
         * if no description is given
         *
         * @return
         */
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }

            if (!(obj instanceof AllowableValueDTO)) {
                return false;
            }

            final AllowableValueDTO other = (AllowableValueDTO) obj;
            return (this.value.equals(other.getValue()));
        }

        @Override
        public int hashCode() {
            return 23984731 + 17 * value.hashCode();
        }
    }
}
