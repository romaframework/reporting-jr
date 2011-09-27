/*
 * Copyright 2006 Giordano Maestro (giordano.maestro--at--assetdata.it)
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

import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignTextField;

import org.romaframework.aspect.reporting.jr.JRDesignHelper;
import org.romaframework.core.schema.SchemaFeatures;

public class DynamicToStringFieldJr extends FieldJr {

	private String	index;

	public DynamicToStringFieldJr(SchemaFeatures field, int iIndex) {
		super(field);
		index = iIndex + "";
		type = String.class;
	}

	@Override
	public JRDesignField getDefinition() {
		final JRDesignField field = new JRDesignField();
		field.setName(index);
		field.setValueClass(type);
		return field;
	}

	@Override
	public JRDesignTextField getElement() {
		return JRDesignHelper.getFieldExpression(index, type,JRDesignHelper.getMarkupType(schemaFeature));
	}

}
