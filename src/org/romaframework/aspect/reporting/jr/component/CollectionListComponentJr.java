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

import java.util.Collection;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.type.LineStyleEnum;

import org.romaframework.aspect.reporting.jr.JRDesignHelper;
import org.romaframework.aspect.reporting.jr.ds.RomaCollectionToStringDataSource;
import org.romaframework.core.schema.SchemaClassDefinition;
import org.romaframework.core.schema.SchemaFeatures;
import org.romaframework.core.schema.SchemaField;

public class CollectionListComponentJr extends BaseCollectionComponentJr {

	private final SchemaField	schemaField;

	public CollectionListComponentJr(BaseComponentJr parent, SchemaField iField, SchemaClassDefinition iRootClassDesign)
			throws JRException {
		super(parent, iField, iRootClassDesign);
		schemaField = iField;
	}

	private void addFieldExpression() {
		
		final JRDesignTextField field = JRDesignHelper.getFieldExpression(RomaCollectionToStringDataSource.FIELD_NAME, String.class,JRDesignHelper.getMarkupType(schemaField));
		final JRDesignBand iBand = new JRDesignBand();
		iBand.addElement(field);
		field.setWidth(design.getDesign().getColumnWidth());
		field.getLineBox().getPen().setLineWidth(JRPen.LINE_WIDTH_1);
		field.getLineBox().getPen().setLineStyle(LineStyleEnum.SOLID);
		iBand.setHeight(field.getHeight());		
		((JRDesignSection) design.getDesign().getDetailSection()).addBand(iBand);
	}

	private void addFieldToTemplate() throws JRException {
		final JRDesignField field = new JRDesignField();
		field.setName(RomaCollectionToStringDataSource.FIELD_NAME);
		field.setValueClass(String.class);
		if (!design.isCustomTemplate()) {
			design.getDesign().addField(field);
		}
	}

	private void addHeader() {
		final JRDesignStaticText header = JRDesignHelper.getLabel(schemaField);
//		header.setMode(JRElement.MODE_OPAQUE);
//		header.setBackcolor(new Color(174,197,228));
		header.setWidth(design.getDesign().getColumnWidth());
		header.getLineBox(). getPen().setLineWidth(1f);
		
		header.setHeight(JRDesignHelper.TITLE_BOX);
		header.setFontSize(JRDesignHelper.TITLE_SIZE);
		final JRDesignBand iBand = new JRDesignBand();
		iBand.addElement(header);
		iBand.setHeight(header.getHeight());
		design.getDesign().setColumnHeader(iBand);
	}

	@Override
	public void fillDesign(Object iToRender, SchemaFeatures iUserFeatures, Map<String, Object> parametersSource) throws JRException {
		final Collection<?> toRender = (Collection<?>) iToRender;
		parametersSource.put(JRDesignHelper.subReportSourceKey(id), new RomaCollectionToStringDataSource(toRender));
		parametersSource.put(JRDesignHelper.subReportDesignKey(id), getCompiledReport());
	}

	@Override
	protected void fillTemplate() throws JRException {
		addHeader();
		addFieldToTemplate();
		addFieldExpression();
	}

}
