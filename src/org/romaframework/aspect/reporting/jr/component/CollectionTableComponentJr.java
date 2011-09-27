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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.type.LineStyleEnum;

import org.romaframework.aspect.reporting.ReportingConstants;
import org.romaframework.aspect.reporting.ReportingFeatureHelper;
import org.romaframework.aspect.reporting.jr.JRDesignHelper;
import org.romaframework.aspect.reporting.jr.ds.RomaBeanDataSource;
import org.romaframework.aspect.reporting.jr.element.ExpandedFieldJr;
import org.romaframework.aspect.reporting.jr.element.FieldJr;
import org.romaframework.core.schema.SchemaClass;
import org.romaframework.core.schema.SchemaClassDefinition;
import org.romaframework.core.schema.SchemaFeatures;
import org.romaframework.core.schema.SchemaField;

public class CollectionTableComponentJr extends BaseCollectionComponentJr {

	private final SchemaField	schemaField;

	public CollectionTableComponentJr(BaseComponentJr iParent, SchemaField collectionFieldToRender,
			SchemaClassDefinition iRootDesignClass) throws JRException {
		super(iParent, collectionFieldToRender, iRootDesignClass);
		schemaField = collectionFieldToRender;
	}

	@Override
	protected void fillTemplate() throws JRException {
		final SchemaClassDefinition schema = getInnerSchemaField();

		final Iterator<SchemaField> iterator = schema.getFieldIterator();
		final List<FieldJr> fields = new LinkedList<FieldJr>();
		while (iterator.hasNext()) {
			final SchemaField schemaField = iterator.next();
			final String layoutField = ReportingFeatureHelper.getLayout(schemaField);
			if (ReportingFeatureHelper.isVisibleField(schemaField)) {
				if (!ReportingConstants.LAYOUT_EXPAND.equals(layoutField)) {
					fields.add(new FieldJr(schemaField));
				} else {
					fields.addAll(expandObject(schemaField));
				}
			}
		}
		createTable(fields);

	}

	protected SchemaClass getInnerSchemaField() throws JRException {
		return schemaField.getEmbeddedType();
	}

	private void createTable(List<FieldJr> fields) throws JRException {

		final int fieldWidth = design.getDesign().getColumnWidth() / fields.size();
		final JRDesignBand columnHeader = JRDesignHelper.getBand();
		final JRDesignBand detailBand = JRDesignHelper.getBand();

		// Create TableLabel

		final JRDesignStaticText tableLabel = JRDesignHelper.getLabel(schemaField);
		tableLabel.setHeight(JRDesignHelper.TITLE_BOX);
		tableLabel.setFontSize(JRDesignHelper.TITLE_SIZE);
		tableLabel.setY(0);
		tableLabel.setWidth(fieldWidth * fields.size());
		//tableLabel.setHorizontalAlignment(JRAlignment.HORIZONTAL_ALIGN_CENTER);
		
		
		
		tableLabel.getLineBox().getPen().setLineWidth(JRPen.LINE_WIDTH_1);
		tableLabel.getLineBox().getPen().setLineStyle(LineStyleEnum.SOLID);
		tableLabel.setX(0);
		tableLabel.setBold(true);

		columnHeader.addElement(tableLabel);
		columnHeader.setHeight(JRDesignHelper.TITLE_BOX + JRDesignHelper.FONT_BOX);
		detailBand.setHeight(JRDesignHelper.FONT_BOX);

		JRDesignHelper.fillTable(fields, fieldWidth, columnHeader, detailBand, design);
		design.getDesign().setColumnHeader(columnHeader);
		((JRDesignSection) design.getDesign().getDetailSection()).addBand(detailBand);
	}

	@Override
	public void fillDesign(Object iToRender, SchemaFeatures iFeatures, Map<String, Object> parametersSource) throws JRException {
		final Collection<?> toRender = (Collection<?>) iToRender;
		final SchemaClass schema = getInnerSchemaField();
		parametersSource.put(JRDesignHelper.subReportSourceKey(id), new RomaBeanDataSource(toRender, schema));
		parametersSource.put(JRDesignHelper.subReportDesignKey(id), getCompiledReport());
	}

	private List<FieldJr> expandObject(SchemaField sourceField) {
		final List<FieldJr> result = new LinkedList<FieldJr>();
		final SchemaClassDefinition toExpand = sourceField.getType();
		final Iterator<SchemaField> iterator = toExpand.getFieldIterator();
		while (iterator.hasNext()) {
			final SchemaField field = iterator.next();
			if (ReportingFeatureHelper.isVisibleField(field)) {
				result.add(new ExpandedFieldJr(field, sourceField));
			}
		}
		return result;
	}

}
