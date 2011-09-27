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

import java.util.Iterator;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignBand;

import org.romaframework.aspect.reporting.ReportingException;
import org.romaframework.aspect.reporting.jr.JRDesignHelper;
import org.romaframework.aspect.reporting.jr.ReflectionHelper;
import org.romaframework.aspect.reporting.jr.design.DesignJr;
import org.romaframework.aspect.reporting.jr.design.StaticSRDesignJr;
import org.romaframework.core.schema.SchemaClassDefinition;
import org.romaframework.core.schema.SchemaFeatures;
import org.romaframework.core.schema.SchemaField;

public class ExpandedComponentJr extends BaseComponentJr implements DesignComponent {

	private SchemaClassDefinition	clazzToExpand;
	private SchemaField						fieldSource;

	public ExpandedComponentJr(BaseComponentJr iParent, SchemaField iFieldSource, SchemaClassDefinition iRootDesignClass) {
		super(iParent, iFieldSource, iRootDesignClass);
		fieldSource = iFieldSource;
		clazzToExpand = fieldSource.getType();
	}

	public void fillParentBand(int columnWidth, JRDesignBand iDetailBand) throws JRException {
		final Iterator<SchemaField> fields = clazzToExpand.getFieldIterator();
		while (fields.hasNext()) {
			final SchemaField field = fields.next();
			if (design.getSubreport(field) != null) {
				log.debug(field);
				final DesignComponent subReport = design.getSubreport(field);
				subReport.fillParentBand(columnWidth, iDetailBand);
			} else {
				log.debug("Field " + field + " not added to Band.");
			}
		}
	}

	@Override
	public void fillDesign(Object toRender, SchemaFeatures iUserFeatures, Map<String, Object> iParametersSource) throws JRException {
		Iterator<SchemaField> fields;
		if (iUserFeatures == null) {
			fields = clazzToExpand.getFieldIterator();
		} else {
			fields = ((SchemaField) iUserFeatures).getType().getFieldIterator();
		}
		while (fields.hasNext()) {
			final SchemaField field = fields.next();
			final Object invokeOn = ReflectionHelper.getFieldValue(field, toRender);
			if (design.getSubreport(field) != null) {
				final DesignComponent subReport = design.getSubreport(field);
				subReport.fillDesign(invokeOn, field, iParametersSource);
			} else {
				log.debug("Field " + field + " not binded.");
			}
		}
	}

	@Override
	protected void fillTemplate() throws JRException {
		JRDesignHelper.renderFields(design, clazzToExpand, this, rootDesignClass);
	}

	@Override
	protected String generateID(BaseComponentJr iParent, SchemaFeatures iSchemaFeatures) {
		final SchemaField field = (SchemaField) iSchemaFeatures;
		if (iParent != null) {
			return iParent.id + "_" + field.getName();
		} else {
			return field.getName();
		}
	}

	@Override
	public void saveTemplate() throws ReportingException {
	// Made nothing
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

	@Override
	protected DesignJr getBaseDesign() {
		return new StaticSRDesignJr(rootDesignClass, id);
	}
}
