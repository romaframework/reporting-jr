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
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignSubreport;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.romaframework.aspect.reporting.ReportingException;
import org.romaframework.aspect.reporting.ReportingFeatureHelper;
import org.romaframework.aspect.reporting.jr.JRDesignHelper;
import org.romaframework.aspect.reporting.jr.design.DesignJr;
import org.romaframework.aspect.reporting.jr.design.DynamicSRDesignJr;
import org.romaframework.aspect.reporting.jr.ds.RomaHashMapListDataSource;
import org.romaframework.core.Roma;
import org.romaframework.core.schema.SchemaClass;
import org.romaframework.core.schema.SchemaClassDefinition;
import org.romaframework.core.schema.SchemaFeatures;
import org.romaframework.core.schema.SchemaField;

public class CollectionRowSetComponentJr extends BaseCollectionComponentJr {

	private int					colWidth;
	private SchemaField	schemaField;

	public CollectionRowSetComponentJr(BaseComponentJr parent, SchemaField schemaFeatures, SchemaClassDefinition iRootDesignClass) {
		super(parent, schemaFeatures, iRootDesignClass);
		schemaField = schemaFeatures;
	}

	@Override
	public void fillDesign(Object iToRender, SchemaFeatures userFeatures, Map<String, Object> parametersSource) throws JRException {
		// Create the design
		design = new DynamicSRDesignJr(rootDesignClass, id);
		// Cleaning the design
		((DynamicSRDesignJr) design).clean();
		addHeader(design);

		log.debug("[" + getClass().getSimpleName() + "] Generating design: " + design.getDesign().getName());
		// Adding source field
		JRDesignHelper.addFieldToTemplate(design);

		Map<String, Object> subReportSource = new HashMap<String, Object>();
		List<Object> list = generateOrderedList(iToRender);

		// Adding the design to the template

		JRDesignBand detailBand = JRDesignHelper.getBand();

		// add header

		// The suffix of the dynamic component
		long suffix = 0;
		for (Object currentObj : list) {
			suffix++;
			if (currentObj != null) {
				SchemaClass schemaClass = Roma.schema().getSchemaClass(currentObj.getClass());
				DynamicRowSetEmbeddedJr element = new DynamicRowSetEmbeddedJr(this, schemaClass, rootDesignClass, suffix);
				((DynamicSRDesignJr) design).addSubReport(schemaClass, element);
				element.generateDesign();
				element.fillDesign(currentObj, schemaClass, subReportSource);
				element.fillParentBand(colWidth, detailBand);

			}
		}

		((JRDesignSection) design.getDesign().getDetailSection()).addBand(detailBand);

		final List<Map<String, Object>> thisReportSource = new ArrayList<Map<String, Object>>();
		thisReportSource.add(subReportSource);
		parametersSource.put(JRDesignHelper.subReportSourceKey(id), new RomaHashMapListDataSource(thisReportSource));
		parametersSource.put(JRDesignHelper.subReportDesignKey(id), getCompiledReport());

	}

	/**
	 * Generate a linked list for parameters order
	 * 
	 * @param iToRender
	 * @return
	 */
	private List<Object> generateOrderedList(Object iToRender) {
		Collection<? extends Object> toRender = (Collection<?>) iToRender;
		List<Object> list = new LinkedList<Object>();
		if (toRender != null) {
			list.addAll(toRender);
		}
		return list;
	}

	@Override
	public void fillParentBand(int columnWidth, JRDesignBand detailBand) throws JRException {
		super.fillParentBand(columnWidth, detailBand);
		colWidth = columnWidth;
	}

	@Override
	protected void fillTemplate() throws JRException {
		// the design is generated dynamically during the fill design phase
	}

	@Override
	public void saveTemplate() throws ReportingException {
		super.saveTemplate();
	}

	private void addHeader(DesignJr iDesign) {
		String label = ReportingFeatureHelper.getI18NLabel(schemaField);
		if (label != null && !"".equals(label)) {
			final JRDesignStaticText header = JRDesignHelper.getLabel(schemaField);
			header.setWidth(iDesign.getDesign().getColumnWidth());
			header.getLineBox().getPen().setLineWidth((byte) 1);
			header.setHeight(JRDesignHelper.TITLE_BOX);
			header.setFontSize(JRDesignHelper.TITLE_SIZE);
			final JRDesignBand iBand = new JRDesignBand();
			iBand.addElement(header);
			iBand.setHeight(header.getHeight());
			iDesign.getDesign().setColumnHeader(iBand);
		}
	}

	@Override
	protected DesignJr getBaseDesign() {
		return null;
	}

	@Override
	protected JRDesignSubreport getSubReport() throws JRException {
		design = new DynamicSRDesignJr(rootDesignClass, id);
		final JasperDesign iDesign = design.getDesign();
		return JRDesignHelper.getSubReport(iDesign, id);
	}

}
