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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignSection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.romaframework.aspect.reporting.jr.JRDesignHelper;
import org.romaframework.aspect.reporting.jr.ReflectionHelper;
import org.romaframework.aspect.reporting.jr.design.DesignJr;
import org.romaframework.aspect.reporting.jr.design.StaticDesignJr;
import org.romaframework.aspect.reporting.jr.ds.RomaHashMapListDataSource;
import org.romaframework.core.Roma;
import org.romaframework.core.schema.SchemaClass;
import org.romaframework.core.schema.SchemaClassDefinition;
import org.romaframework.core.schema.SchemaFeatures;
import org.romaframework.core.schema.SchemaField;

public class ObjectComponentJr extends BaseComponentJr {

	protected SchemaClassDefinition					designClassToRender;

	protected final JRDesignBand						detailBand				= JRDesignHelper.getBand();

	protected Log														log								= LogFactory.getLog("JR.REPORTING");

	protected final HashMap<String, Object>	parametersSource	= new HashMap<String, Object>();

	private ObjectComponentJr(BaseComponentJr parent, SchemaClassDefinition iClassToRender) throws JRException {
		super(parent, iClassToRender, iClassToRender);
		designClassToRender = iClassToRender;
	}

	public ObjectComponentJr(SchemaClass iClassToRender) throws JRException {
		this(null, iClassToRender);
	}

	/**
	 * Add the title to the report
	 * 
	 */
	protected void addTitle(DesignJr iDesign) {

		final JRDesignBand titleBand = JRDesignHelper.getTitleBand(designClassToRender, iDesign.getDesign().getColumnWidth());
		iDesign.getDesign().setColumnHeader(titleBand);

	}

	public void fillParentBand(int columnWidth, JRDesignBand iDetailBand) throws JRException {
		final Iterator<SchemaField> fields = designClassToRender.getFieldIterator();
		while (fields.hasNext()) {
			final SchemaField field = fields.next();
			if (design.getSubreport(field) != null) {
				log.debug(field);
				design.getSubreport(field).fillParentBand(columnWidth, iDetailBand);
			} else {
				log.debug("Field " + field + " not added to Band.");
			}
		}
		((JRDesignSection) design.getDesign().getDetailSection()).addBand(detailBand);
	}

	@Override
	public void fillDesign(Object iToRender, SchemaFeatures iUserSchemaFeatures, Map<String, Object> parametersSource)
			throws JRException {
		Iterator<SchemaField> fields;
		if (iUserSchemaFeatures != null) {
			fields = ((SchemaClassDefinition) iUserSchemaFeatures).getFieldIterator();
		} else {
			fields = designClassToRender.getFieldIterator();
		}
		while (fields.hasNext()) {
			final SchemaField field = fields.next();
			final Object toRender = ReflectionHelper.getFieldValue(field, iToRender);
			if (design.getSubreport(field) != null) {
				log.debug(field);
				design.getSubreport(field).fillDesign(toRender, field, parametersSource);
			} else {
				log.debug("Field " + field + " not binded.");
			}
		}
	}

	/**
	 * Creates the base design
	 * 
	 * @param iSchemaObject
	 * @throws JRException
	 */
	@Override
	protected void fillTemplate() throws JRException {
		// Generate the design
		addTitle(design);
		JRDesignHelper.renderFields(design, designClassToRender, this, rootDesignClass);
		JRDesignHelper.addFieldToTemplate(design);
		fillParentBand(design.getDesign().getColumnWidth(), detailBand);
	}

	@Override
	protected String generateID(BaseComponentJr iParent, SchemaFeatures iSchemaFeature) {
		final SchemaClassDefinition schemaObject = (SchemaClassDefinition) iSchemaFeature;
		if (iParent != null) {
			return iParent.id + "_" + schemaObject.getName();
		} else {
			return schemaObject.getName();
		}
	}

	public JasperPrint getCollectionPrinter(Collection<?> iToRender, SchemaFeatures iUserSchemaFeatures) throws JRException {

		final List<Map<String, Object>> sources = new LinkedList<Map<String, Object>>();
		for (final Object object : iToRender) {
			final ObjectComponentJr jrReport = new ObjectComponentJr(null, designClassToRender);
			jrReport.generateDesign();
			jrReport.fillDesign(object, iUserSchemaFeatures, jrReport.parametersSource);
			sources.add(jrReport.parametersSource);
		}
		final RomaHashMapListDataSource source = new RomaHashMapListDataSource(sources);

		// Get the printer

		final JasperPrint jPrint = JasperFillManager.fillReport(getCompiledReport(), new HashMap<String, Object>(), source);
		return jPrint;
	}

	public JRDataSource getFieldsSource() {
		final List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
		list.add(parametersSource);
		return new RomaHashMapListDataSource(list);
	}

	public HashMap<String, Object> getParametersSource() {
		return parametersSource;
	}

	public JasperPrint getPrinter(Object iToRender, SchemaFeatures iUserSchemaFeatures) throws JRException {
		fillDesign(iToRender, iUserSchemaFeatures, parametersSource);
		final JasperReport report = getCompiledReport();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(JRParameter.REPORT_LOCALE, Roma.session().getActiveLocale());
		final JasperPrint jPrint = JasperFillManager.fillReport(report, params, getFieldsSource());
		return jPrint;
	}

	@Override
	protected int getComponentLevel() {
		return 1;
	}

	@Override
	protected DesignJr getBaseDesign() {
		return new StaticDesignJr(rootDesignClass, id);
	}

}
