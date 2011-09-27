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

import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignBand;

import org.romaframework.aspect.reporting.ReportingException;
import org.romaframework.core.schema.SchemaFeatures;

public interface DesignComponent {

	/**
	 * Fill the parent band with the jasper design element needed
	 * 
	 * @param columnWidth the with of the component in the design
	 * @param detailBand the band to fill
	 * @throws JRException
	 */
	public void fillParentBand(int columnWidth, JRDesignBand detailBand) throws JRException;

	/**
	 * Bind the design of an object with the design provided.
	 * 
	 * @param toRender The object to render
	 * @param iUserSchemaFeatures if null will be used the global schema feature
	 * @param parametersSource
	 * @throws JRException
	 */
	public void fillDesign(Object toRender, SchemaFeatures iUserSchemaFeatures, Map<String, Object> parametersSource)
			throws JRException;

	/**
	 * Save the template of the component
	 * 
	 * @throws ReportingException
	 */
	public void saveTemplate() throws ReportingException;

}
