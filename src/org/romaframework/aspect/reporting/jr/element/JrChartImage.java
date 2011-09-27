/*
 * Copyright 2006-2007 Giordano Maestro (giordano.maestro--at--assetdata.it)
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.HorizontalAlignEnum;
import net.sf.jasperreports.engine.type.PositionTypeEnum;
import net.sf.jasperreports.engine.type.StretchTypeEnum;

import org.romaframework.aspect.chart.ChartAspect;
import org.romaframework.aspect.reporting.ReportingException;
import org.romaframework.aspect.reporting.jr.JRDesignHelper;
import org.romaframework.aspect.reporting.jr.component.DesignComponent;
import org.romaframework.core.Roma;
import org.romaframework.core.schema.SchemaFeatures;
import org.romaframework.core.schema.SchemaField;

public class JrChartImage extends ElementJr implements DesignComponent {

	public JrChartImage(String iContainerId, SchemaField field) {
		super(field);
		type = InputStream.class;
		name = PARAM_ + iContainerId + "_" + field.getName();
	}

	public void fillDesign(Object toRender, SchemaFeatures userSchemaFeatures, Map<String, Object> parametersSource)
			throws JRException {
		try {

			byte[] result = Roma.aspect(ChartAspect.class).toChart(toRender);
			parametersSource.put(name, new ByteArrayInputStream(result));
		}
		catch (Exception e) {
			throw new JRException(e);
		}
	}

	public void fillParentBand(int columnWidth, JRDesignBand detailBand) throws JRException {
		// Add the label of the field
		final int x = 0;
		final JRDesignStaticText staticText = getLabel();
		// Label properties
		staticText.setX(x);
		staticText.setY(detailBand.getHeight());
		staticText.setWidth(columnWidth / 2);
		staticText.setPositionType(PositionTypeEnum.FLOAT);

		// Add the parameter variable
		final JRDesignElement textField = getElement();
		textField.setHeight(JRDesignHelper.IMAGE_BOX);

		// PArameter properties
		textField.setX(columnWidth / 2);
		textField.setY(detailBand.getHeight());
		textField.setWidth(columnWidth / 2);

		textField.setPositionType(PositionTypeEnum.FLOAT);

		detailBand.setHeight(detailBand.getHeight() + JRDesignHelper.IMAGE_BOX);
		detailBand.addElement(textField);
		detailBand.addElement(staticText);
	}

	public void saveTemplate() throws ReportingException {
	// Do nothing
	}

	public JRDesignElement getElement() {
		JRDesignImage textField = new JRDesignImage(new JasperDesign());
		textField.setHeight(JRDesignHelper.FONT_BOX);
		textField.setPositionType(PositionTypeEnum.FLOAT);
		textField.setHorizontalAlignment(HorizontalAlignEnum.LEFT);
		textField.setStretchType(StretchTypeEnum.NO_STRETCH);
		// Adding the field expression
		final JRDesignExpression expression = new JRDesignExpression();
		expression.setText("$" + JRDesignHelper.FIELD_TYPE + "{source}.get(\"" + name + "\")");
		textField.setExpression(expression);
		return textField;
	}

}
