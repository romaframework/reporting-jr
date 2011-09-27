/*
 * 
 * Copyright 2007 Giordano Maestro (giordano.maestro--at--assetdata.it)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.romaframework.aspect.reporting.jr.element;

import net.sf.jasperreports.engine.design.JRDesignStaticText;

import org.romaframework.aspect.reporting.jr.JRDesignHelper;
import org.romaframework.aspect.reporting.jr.ReflectionHelper;
import org.romaframework.core.schema.SchemaClassDefinition;
import org.romaframework.core.schema.SchemaFeatures;
import org.romaframework.core.schema.SchemaField;

public abstract class ElementJr {

	protected String					name;
	protected SchemaFeatures	schemaFeature;
	protected Class<?>				type;
	protected static final String	PARAM_	= "param_";

	public ElementJr(SchemaFeatures field) {
		if (field instanceof SchemaField) {
			name = ((SchemaField) field).getName();
			type = (Class<?>)((SchemaField) field).getLanguageType();
		} else {
			name = ((SchemaClassDefinition) field).getSchemaClass().getName();
			type = (Class<?>)(((SchemaClassDefinition) field).getSchemaClass()).getLanguageType();
		}
		schemaFeature = field;

		if (!ReflectionHelper.isJRType(type)) {
			type = String.class;
		}
	}

	/**
	 * Create a label object with the default size and align values
	 * 
	 * @param field, the field to label
	 * @return
	 */
	public JRDesignStaticText getLabel() {
		return JRDesignHelper.getLabel(schemaFeature);
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}

	public void setName(String parameterName) {
		name = parameterName;
	}
}
