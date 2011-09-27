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
package org.romaframework.aspect.reporting.jr.component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;

import org.romaframework.core.schema.SchemaClassDefinition;
import org.romaframework.core.schema.SchemaFeatures;
import org.romaframework.core.schema.SchemaField;

public class MapRowSetComponentJr extends CollectionRowSetComponentJr {

	public MapRowSetComponentJr(BaseComponentJr parent, SchemaField schemaFeatures, SchemaClassDefinition rootDesignClass) {
		super(parent, schemaFeatures, rootDesignClass);
	}

	@Override
	public void fillDesign(Object iToRender, SchemaFeatures iUserFeatures, Map<String, Object> parametersSource) throws JRException {
		List<Object> toRender = new LinkedList<Object>();
		Map<?, ?> map = (Map<?, ?>) iToRender;

		if (iToRender != null) {
			toRender.addAll(map.values());
		}
		super.fillDesign(toRender, iUserFeatures, parametersSource);
	}

}
