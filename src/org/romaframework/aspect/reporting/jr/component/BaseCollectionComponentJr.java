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
package org.romaframework.aspect.reporting.jr.component;

import org.romaframework.core.schema.SchemaClassDefinition;
import org.romaframework.core.schema.SchemaFeatures;
import org.romaframework.core.schema.SchemaField;

public abstract class BaseCollectionComponentJr extends BaseSRComponentJr {

	public BaseCollectionComponentJr(BaseComponentJr iParent, SchemaFeatures iSchemaFeatures, SchemaClassDefinition iRootDesignClass) {
		super(iParent, iSchemaFeatures, iRootDesignClass);
	}

	@Override
	protected int getComponentLevel() {
		int iLevel;
		if (parent == null) {
			iLevel = 1;
		} else {
			iLevel = parent.level;
		}
		return iLevel;
	}

	@Override
	protected String generateID(BaseComponentJr iParent, SchemaFeatures iSchemaFeatures) {
		final SchemaField schemaField = (SchemaField) iSchemaFeatures;
		if (iParent != null) {
			return iParent.id + "_" + schemaField.getName();
		} else {
			return schemaField.getName();
		}
	}

}
