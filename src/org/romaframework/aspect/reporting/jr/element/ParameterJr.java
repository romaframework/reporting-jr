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
package org.romaframework.aspect.reporting.jr.element;

import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.type.PositionTypeEnum;

import org.romaframework.aspect.reporting.ReportingException;
import org.romaframework.aspect.reporting.jr.JRDesignHelper;
import org.romaframework.aspect.reporting.jr.ReflectionHelper;
import org.romaframework.aspect.reporting.jr.component.DesignComponent;
import org.romaframework.core.schema.SchemaFeatures;
import org.romaframework.core.schema.SchemaField;

public class ParameterJr extends ElementJr implements DesignComponent {

	public ParameterJr(String iContainerId, SchemaField field) {
		super(field);
		name = PARAM_ + iContainerId + "_" + field.getName();
	}

	public void fillParentBand(int columnWidth, JRDesignBand detailBand) {
		// Add the label of the field
		final int x = 0;
		final JRDesignStaticText staticText = getLabel();
		// Label properties
		staticText.setX(x);
		staticText.setY(detailBand.getHeight());
		staticText.setWidth(columnWidth / 2);
		staticText.setPositionType(PositionTypeEnum.FLOAT);

		// Add the parameter varaiable
		final JRDesignElement textField = getElement();

		// PArameter properties
		textField.setX(columnWidth / 2);
		textField.setY(detailBand.getHeight());
		textField.setWidth(columnWidth / 2);

		textField.setPositionType(PositionTypeEnum.FLOAT);

		detailBand.setHeight(detailBand.getHeight() + JRDesignHelper.FONT_BOX);
		detailBand.addElement(textField);
		detailBand.addElement(staticText);
	}

	/**
	 * UserFeatures is not used
	 */
	public void fillDesign(Object iToRender, SchemaFeatures iUserFeatures, Map<String, Object> parametersSource) throws JRException {
		parametersSource.put(name, getObject(iToRender));
	}

	public JRDesignParameter getDefinition() {
		final JRDesignParameter definition = new JRDesignParameter();
		definition.setName(name);
		definition.setValueClass(type);
		return definition;
	}

	/**
	 * Create the parameter text field
	 * 
	 * @param field
	 * @return
	 */
	public JRDesignTextField getElement() {
		return JRDesignHelper.getIndirectFieldExpression(name, type, JRDesignHelper.getMarkupType(schemaFeature));
	}

	protected Object getObject(Object toRender) throws JRException {
		if (ReflectionHelper.isJRType((Class<?>)((SchemaField) schemaFeature).getLanguageType())) {
			return toRender;
		} else {
			return JRDesignHelper.toString(toRender);
		}
	}

	public void saveTemplate() throws ReportingException {
	// Makes nothing
	}
}
