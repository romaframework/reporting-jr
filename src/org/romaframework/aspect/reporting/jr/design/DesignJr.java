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

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.romaframework.aspect.reporting.ReportingException;
import org.romaframework.aspect.reporting.jr.component.DesignComponent;
import org.romaframework.core.schema.SchemaFeatures;

/**
 * Represents the design of a component, it contains the compiled report of a
 * template and the components list of the template.
 * 
 * @author Giordano Maestro (giordano.maestro--at--assetdata.it)
 * 
 */
public interface DesignJr {

	public void addSubReport(SchemaFeatures iField, DesignComponent subreport);

	/**
	 * Get the compiled JReport
	 * 
	 * @return
	 * @throws JRException
	 */
	public JasperReport getCompiledReport() throws JRException;

	/**
	 * Get the jasper design of the component
	 * 
	 * @return
	 */
	public JasperDesign getDesign();

	/**
	 * Return a sub component of the current design
	 * 
	 * @param iFeature
	 * @return
	 */
	public DesignComponent getSubreport(SchemaFeatures iFeature);

	/**
	 * Return the list of sub reports in the design
	 * 
	 * @return
	 */
	public Collection<DesignComponent> getSubReports();

	public boolean isCustomTemplate();

	public void saveTemplate() throws ReportingException;

}