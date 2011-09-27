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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.jasperreports.engine.JRException;

import org.romaframework.aspect.reporting.jr.JRDesignHelper;
import org.romaframework.aspect.reporting.jr.domain.MapEntry;
import org.romaframework.aspect.reporting.jr.ds.RomaBeanDataSource;
import org.romaframework.core.Roma;
import org.romaframework.core.schema.SchemaClass;
import org.romaframework.core.schema.SchemaClassDefinition;
import org.romaframework.core.schema.SchemaFeatures;
import org.romaframework.core.schema.SchemaField;

public class MapTableComponentJr extends CollectionTableComponentJr {

	public MapTableComponentJr(BaseComponentJr iParent, SchemaField collectionFieldToRender, SchemaClassDefinition iRootDesignClass)
			throws JRException {
		super(iParent, collectionFieldToRender, iRootDesignClass);
	}

	@Override
	public void fillDesign(Object iToRender, SchemaFeatures iUserFeatures, Map<String, Object> parametersSource) throws JRException {
		final SchemaClassDefinition schema = getInnerSchemaField();
		Map<?, ?> map = (Map<?, ?>) iToRender;
		List<MapEntry> list = new ArrayList<MapEntry>();
		if (map != null) {
			Set<?> entrySet = map.entrySet();
			for (Object entry : entrySet) {
				Entry<?, ?> en = (Entry<?, ?>) entry;
				list.add(new MapEntry(en));
			}
		}
		parametersSource.put(JRDesignHelper.subReportSourceKey(id), new RomaBeanDataSource(list, schema));
		parametersSource.put(JRDesignHelper.subReportDesignKey(id), getCompiledReport());
	}

	@Override
	protected SchemaClass getInnerSchemaField() throws JRException {
		return Roma.schema().getSchemaClass(MapEntry.class);
	}

}