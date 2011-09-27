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
package org.romaframework.aspect.reporting.jr.design;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.romaframework.aspect.reporting.ReportingException;
import org.romaframework.aspect.reporting.jr.component.DesignComponent;
import org.romaframework.core.schema.SchemaClassDefinition;
import org.romaframework.core.schema.SchemaFeatures;

/**
 * This kind of design is used when it is not possible to define static design
 * for the component, for example the rendering of a row set.
 * 
 * The main difference with a {@link StaticDesignJr} is that the report and the
 * design must be compiled any time.
 * 
 * @author Giordano Maestro (giordano.maestro--at--assetdata.it)
 * 
 */

public class DynamicSRDesignJr extends AbstractDesignJr {

	private Map<String, DesignComponent>	classSubreports	= new HashMap<String, DesignComponent>();

	public DynamicSRDesignJr(SchemaClassDefinition iRootDesignClass, String componentId) {
		super(iRootDesignClass, componentId);
	}

	public void addSubReport(SchemaFeatures iClass, DesignComponent subreport) {
		classSubreports.put(((SchemaClassDefinition) iClass).getSchemaClass().getName(), subreport);
	}

	public void clean() {

		design = templateManager.getBaseSubReportTemplate(componentId, rootDesignClass);

	}

	@Override
	protected JasperDesign getBaseDesign() {
		if (design == null) {

			return templateManager.getBaseSubReportTemplate(componentId, rootDesignClass);

		} else {
			return design;
		}
	}

	/**
	 * Get the compiled JReport, it must be always be compiled
	 * 
	 * @return
	 * @throws JRException
	 */
	public JasperReport getCompiledReport() throws JRException {
		if (report == null) {
			report = JasperCompileManager.compileReport(design);
		}
		return JasperCompileManager.compileReport(design);
	}

	public DesignComponent getSubreport(SchemaFeatures feature) {
		return classSubreports.get(((SchemaClassDefinition) feature).getSchemaClass().getName());
	}

	public Collection<DesignComponent> getSubReports() {
		return classSubreports.values();
	}

	public boolean isCustomTemplate() {
		return false;
	}

	public void saveTemplate() throws ReportingException {
		for (DesignComponent subReport : getSubReports()) {
			subReport.saveTemplate();
		}
	}

}
