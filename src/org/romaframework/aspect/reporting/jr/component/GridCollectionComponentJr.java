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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.type.HorizontalAlignEnum;

import org.romaframework.aspect.reporting.jr.JRDesignHelper;
import org.romaframework.aspect.reporting.jr.design.DynamicSRDesignJr;
import org.romaframework.aspect.reporting.jr.ds.RomaGridDataSource;
import org.romaframework.aspect.reporting.jr.element.DynamicToStringFieldJr;
import org.romaframework.aspect.reporting.jr.element.FieldJr;
import org.romaframework.core.Roma;
import org.romaframework.core.schema.SchemaClass;
import org.romaframework.core.schema.SchemaClassDefinition;
import org.romaframework.core.schema.SchemaFeatures;
import org.romaframework.core.schema.SchemaField;

public class GridCollectionComponentJr extends BaseCollectionComponentJr {

	SchemaField	schemaField;

	public GridCollectionComponentJr(BaseComponentJr parent, SchemaFeatures schemaFeatures, SchemaClassDefinition rootDesignClass) {
		super(parent, schemaFeatures, rootDesignClass);
		schemaField = (SchemaField) schemaFeatures;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void fillDesign(Object iToRender, SchemaFeatures userSchemaFeatures, Map<String, Object> parametersSource)
			throws JRException {
		generateDynaDesign(iToRender);
		parametersSource.put(JRDesignHelper.subReportSourceKey(id), new RomaGridDataSource((Collection<Collection<?>>) iToRender));
		parametersSource.put(JRDesignHelper.subReportDesignKey(id), getCompiledReport());
	}

	@SuppressWarnings("unchecked")
	private void generateDynaDesign(Object iToRender) throws JRException {
		design = new DynamicSRDesignJr(rootDesignClass, id);

		Collection<Collection<?>> toRender = (Collection<Collection<?>>) iToRender;
		int columnWidth = design.getDesign().getColumnWidth();
		JRDesignBand columnHeader = JRDesignHelper.getBand();
		JRDesignBand detailBand = JRDesignHelper.getBand();

		createTableLabel(columnWidth, columnHeader, detailBand);

		//  			

		List<FieldJr> fields = getTableColumns(toRender, columnHeader);
		if (fields != null && fields.size() != 0) {
			int fieldWidth = columnWidth / fields.size();
			JRDesignHelper.fillTable(fields, fieldWidth, columnHeader, detailBand, design);
		}

		design.getDesign().setColumnHeader(columnHeader);
		((JRDesignSection) design.getDesign().getDetailSection()).addBand(detailBand);
	}

	private void createTableLabel(int columnWidth, JRDesignBand columnHeader, JRDesignBand detailBand) {
		// Create TableLabel
		JRDesignStaticText tableLabel = JRDesignHelper.getLabel(schemaField);
		tableLabel.setHeight(JRDesignHelper.TITLE_BOX);
		tableLabel.setFontSize(JRDesignHelper.TITLE_SIZE);
		tableLabel.setY(0);
		tableLabel.setWidth(columnWidth);
		tableLabel.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
		tableLabel.getLineBox().getPen() .setLineWidth (JRPen.LINE_WIDTH_1);
		tableLabel.setX(0);
		tableLabel.setBold(true);
		columnHeader.addElement(tableLabel);
		columnHeader.setHeight(JRDesignHelper.TITLE_BOX + JRDesignHelper.FONT_BOX);
		detailBand.setHeight(JRDesignHelper.FONT_BOX);
	}

	private List<FieldJr> getTableColumns(Collection<Collection<?>> toRender, JRDesignBand columnHeader) {

		if (toRender != null) {
			for (Collection<?> coll : toRender) {
				if (coll == null || coll.size() == 0) {
					log.warn("Cannot create Table because list contains an empty column ");
					return null;
				}
			}

			List<FieldJr> fields = new ArrayList<FieldJr>();

			int columnName = 0;
			for (Collection<?> coll : toRender) {
				Object forHeader = coll.iterator().next();
				String iName = forHeader.getClass().getSimpleName();
				SchemaClass feature = Roma.schema().getSchemaClass(iName);
				fields.add(new DynamicToStringFieldJr(feature, columnName));
				columnName++;
			}

			return fields;

		} else {
			log.warn("Cannot create Header because list is null ");
			return null;
		}
	}

	@Override
	protected void fillTemplate() throws JRException {
	// the template is generated dynamically
	}

}
