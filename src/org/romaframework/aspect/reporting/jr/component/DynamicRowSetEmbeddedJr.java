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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignSubreport;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.romaframework.aspect.reporting.jr.JRDesignHelper;
import org.romaframework.aspect.reporting.jr.ReflectionHelper;
import org.romaframework.aspect.reporting.jr.ds.RomaHashMapListDataSource;
import org.romaframework.core.schema.SchemaClassDefinition;
import org.romaframework.core.schema.SchemaFeatures;
import org.romaframework.core.schema.SchemaField;

public class DynamicRowSetEmbeddedJr extends ObjectEmbeddedJr {

	private long	suffix;

	public DynamicRowSetEmbeddedJr(BaseComponentJr parent, SchemaClassDefinition subReportField,
			SchemaClassDefinition rootClassDesign, long iSuffix) throws JRException {
		super(parent, subReportField, rootClassDesign);
		suffix = iSuffix;
	}

	@Override
	protected String generateID(BaseComponentJr iParent, SchemaFeatures iSchemaFeatures) {
		final SchemaClassDefinition schemaField = (SchemaClassDefinition) iSchemaFeatures;
		if (iParent != null) {
			return iParent.id + "_row_set_" + schemaField.getSchemaClass().getName();
		} else {
			return "_row_set_" + schemaField.getSchemaClass().getName();
		}
	}

	@Override
	public JRDesignSubreport getSubReport() throws JRException {
		final JasperDesign iDesign = design.getDesign();
		return JRDesignHelper.getSubReport(iDesign, id + suffix);
	}

	@Override
	public void fillDesign(Object iToRender, SchemaFeatures iUserSchemaFeatures, Map<String, Object> parametersSource)
			throws JRException {
		final Map<String, Object> parametersSource2 = new HashMap<String, Object>();
		Iterator<SchemaField> fields;
		if (iUserSchemaFeatures == null) {
			fields = designClassToRender.getFieldIterator();
		} else {
			fields = ((SchemaClassDefinition) iUserSchemaFeatures).getFieldIterator();
		}
		while (fields.hasNext()) {
			final SchemaField field = fields.next();
			final Object toRender = ReflectionHelper.getFieldValue(field, iToRender);
			log.debug(field + " : " + field.getName() + " : toRender -> " + iToRender);
			if (design.getSubreport(field) != null) {
				design.getSubreport(field).fillDesign(toRender, field, parametersSource2);
			} else {
				log.debug("Field " + field + " not binded.");
			}
		}

		final List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list.add(parametersSource2);
		parametersSource.put(JRDesignHelper.subReportSourceKey(id + suffix), new RomaHashMapListDataSource(list));
		parametersSource.put(JRDesignHelper.subReportDesignKey(id + suffix), getCompiledReport());

	}

}
