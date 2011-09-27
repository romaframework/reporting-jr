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
import org.romaframework.core.schema.SchemaField;

/**
 * It represent a static design of a component.
 * 
 * @author Giordano Maestro (giordano.maestro--at--assetdata.it)
 * 
 */

public class StaticDesignJr extends AbstractDesignJr {

	/**
	 * The list of connected sub reports
	 */
	private Map<String, DesignComponent>	subreports	= new HashMap<String, DesignComponent>();

	public StaticDesignJr(SchemaClassDefinition iRootDesignClass, String iComponentId) {
		super(iRootDesignClass, iComponentId);
	}

	/**
	 * Add a subreport to the design
	 * 
	 * @param iField
	 * @param subreport
	 */
	public void addSubReport(SchemaFeatures iField, DesignComponent subreport) {
		subreports.put(((SchemaField) iField).getName(), subreport);
	}

	/**
	 * compiled JReport
	 * 
	 * @return
	 * @throws JRException
	 */
	public void recompile(JasperDesign iDesign) throws JRException {
		custom = iDesign;
		design = iDesign;
		customTemplate = true;
		report = JasperCompileManager.compileReport(iDesign);
	}

	/**
	 * Get the base JR design to use for the rendering.
	 * 
	 * If no template is defined in the package of the application return the
	 * template schema in {application.package}.reporting.
	 * 
	 * @return
	 */
	@Override
	protected JasperDesign getBaseDesign() {
		custom = templateManager.getCustomTemplate(rootDesignClass, componentId);
		if (custom != null) {
			customTemplate = true;
			return custom;
		}
		if (design == null) {
			return templateManager.getBaseTemplate(componentId, rootDesignClass);
			
			
			
		} else {
			return design;
		}
	}

	/**
	 * Get the compiled JReport
	 * 
	 * @return
	 * @throws JRException
	 */
	public JasperReport getCompiledReport() throws JRException {
		if (report == null) {
			if (customTemplate) {
				report = JasperCompileManager.compileReport(templateManager.getCustomTemplate(rootDesignClass, componentId));
			} else {
				report = JasperCompileManager.compileReport(design);
			}
		}
		return report;
	}

	/**
	 * Return a defined sub report for the field
	 * 
	 * @param iField
	 * @return
	 */
	public DesignComponent getSubreport(SchemaFeatures iField) {
		return subreports.get(((SchemaField) iField).getName());
	}

	/**
	 * Return the list of sub report in the design
	 * 
	 * @return
	 */
	public Collection<DesignComponent> getSubReports() {
		return subreports.values();
	}

	/**
	 * Return true if a template is defined in the package of the class
	 * 
	 * @return
	 */
	public boolean isCustomTemplate() {
		return customTemplate;
	}

	public void saveTemplate() throws ReportingException {
		try {
			templateManager.saveTemplate(design, rootDesignClass);
		}
		catch (final Exception e) {
			throw new ReportingException(e);
		}
		for (final DesignComponent subReport : getSubReports()) {
			subReport.saveTemplate();
		}
	}

}
