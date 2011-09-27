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
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignSubreport;

import org.romaframework.aspect.reporting.ReportingFeatureHelper;
import org.romaframework.aspect.reporting.jr.JRDesignHelper;
import org.romaframework.aspect.reporting.jr.ReflectionHelper;
import org.romaframework.aspect.reporting.jr.design.DesignJr;
import org.romaframework.aspect.reporting.jr.ds.RomaHashMapListDataSource;
import org.romaframework.core.schema.SchemaClassDefinition;
import org.romaframework.core.schema.SchemaFeatures;
import org.romaframework.core.schema.SchemaField;

public class ObjectEmbeddedJr extends BaseSRComponentJr implements DesignComponent {

	private SchemaField							subReportField;
	protected SchemaClassDefinition	designClassToRender;

	public ObjectEmbeddedJr(BaseComponentJr parent, SchemaClassDefinition iDesignClassToRender, SchemaClassDefinition iRootClassDesign)
			throws JRException {
		super(parent, iDesignClassToRender, iRootClassDesign);
		designClassToRender = iDesignClassToRender;
	}

	public ObjectEmbeddedJr(BaseComponentJr parent, SchemaField iSubReportField, SchemaClassDefinition iRootClassDesign)
			throws JRException {
		super(parent, iSubReportField, iRootClassDesign);
		subReportField = iSubReportField;

		designClassToRender = iSubReportField.getType();
	}

	protected void addTitle(String reportName, DesignJr iDesign) {
		final JRDesignBand titleBand = JRDesignHelper.getTitleBand(designClassToRender, iDesign.getDesign().getColumnWidth());
		iDesign.getDesign().setColumnHeader(titleBand);
	}

	@Override
	public void fillParentBand(int columnWidth, JRDesignBand iDetailBand) throws JRException {
		final JRDesignSubreport sub = getSubReport();
		sub.setHeight(JRDesignHelper.FONT_BOX);
		sub.setY(iDetailBand.getHeight());
		sub.setWidth(columnWidth);
		iDetailBand.setHeight(iDetailBand.getHeight() + sub.getHeight());
		iDetailBand.addElement(sub);
	}

	@Override
	public void fillDesign(Object iToRender, SchemaFeatures iUserFeatures, Map<String, Object> parametersSource) throws JRException {

		final Map<String, Object> parametersSource2 = new HashMap<String, Object>();
		Iterator<SchemaField> fields;
		if (iUserFeatures == null) {
			fields = designClassToRender.getFieldIterator();
		} else {
			if (iUserFeatures instanceof SchemaField) {
				fields = ((SchemaField) iUserFeatures).getType().getFieldIterator();
			} else {
				fields = ((SchemaClassDefinition) iUserFeatures).getFieldIterator();
			}
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
		parametersSource.put(JRDesignHelper.subReportSourceKey(id), new RomaHashMapListDataSource(list));
		parametersSource.put(JRDesignHelper.subReportDesignKey(id), getCompiledReport());

	}

	@Override
	protected void fillTemplate() throws JRException {
		// Generate the design
		if (subReportField != null) {
			addTitle(ReportingFeatureHelper.getI18NLabel(subReportField), design);
		} else {
			addTitle(ReportingFeatureHelper.getI18NLabel(designClassToRender), design);
		}
		JRDesignHelper.renderFields(design, designClassToRender, this, rootDesignClass);
		// Adding the template to the manager
		JRDesignHelper.addFieldToTemplate(design);

		JRDesignBand detailBand = JRDesignHelper.getBand();
		final Iterator<SchemaField> fields = designClassToRender.getFieldIterator();
		while (fields.hasNext()) {
			final SchemaField field = fields.next();
			if (design.getSubreport(field) != null) {
				log.debug(field);
				design.getSubreport(field).fillParentBand(design.getDesign().getColumnWidth(), detailBand);
			} else {
				log.debug("Field " + field + " not added to Band.");
			}
		}

		((JRDesignSection) design.getDesign().getDetailSection()).addBand(detailBand);

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

	@Override
	protected int getComponentLevel() {
		int iLevel;
		if (parent == null) {
			iLevel = 1;
		} else {
			iLevel = parent.level + 1;
		}
		return iLevel;
	}

}
