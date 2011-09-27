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
import net.sf.jasperreports.engine.JasperReport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.romaframework.aspect.reporting.ReportingException;
import org.romaframework.aspect.reporting.jr.design.DesignJr;
import org.romaframework.aspect.reporting.jr.template.TemplateManager;
import org.romaframework.core.Roma;
import org.romaframework.core.schema.SchemaClassDefinition;
import org.romaframework.core.schema.SchemaFeatures;

/**
 * The base class for the components of a template
 * 
 * @author Giordano Maestro (giordano.maestro--at--assetdata.it)
 * 
 */
public abstract class BaseComponentJr implements DesignComponent {

	protected Log										log			= LogFactory.getLog("JR.REPORTING." + getClass().getSimpleName());
	protected DesignJr							design;
	protected String								id;
	protected int										level;
	protected BaseComponentJr				parent	= null;
	protected SchemaClassDefinition	rootDesignClass;

	public BaseComponentJr(BaseComponentJr iParent, SchemaFeatures iSchemaFeatures, SchemaClassDefinition iRootDesignClass) {
		super();
		parent = iParent;
		rootDesignClass = iRootDesignClass;
		id = generateID(iParent, iSchemaFeatures);
		id = id.replaceAll("\\.", "");
		level = getComponentLevel();
	}

	/**
	 * Returns the level of a component
	 * 
	 * @return
	 */
	protected abstract int getComponentLevel();

	public abstract void fillDesign(Object toRender, SchemaFeatures iUserSchemaFeatures, Map<String, Object> parametersSource)
			throws JRException;

	/**
	 * Generate the design of the component
	 * 
	 * @throws JRException
	 */
	public final void generateDesign() throws JRException {
		if (!isRegistered()) {
			design = getBaseDesign();
			fillTemplate();
			if (design != null) {
				Roma.component(TemplateManager.class).addDesign(id, design);
			}
		} else {
			design = Roma.component(TemplateManager.class).getDesign(id);
		}
	}

	/**
	 * Get the base design of the Component.
	 * 
	 * @return
	 */
	protected abstract DesignJr getBaseDesign();

	/**
	 * Generate the jasper design
	 * 
	 * @param iDesign
	 * @throws JRException
	 */
	protected abstract void fillTemplate() throws JRException;

	/**
	 * Generate the id of the component
	 * 
	 * @param iParent
	 * @param iSchemaFeatures
	 * @return
	 */
	protected abstract String generateID(BaseComponentJr iParent, SchemaFeatures iSchemaFeatures);

	/**
	 * Get the jasper compiled report
	 * 
	 * @return
	 * @throws JRException
	 */
	public JasperReport getCompiledReport() throws JRException {

		return design.getCompiledReport();
	}

	/**
	 * Return the id of the component
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * Return the level of the component
	 * 
	 * @return
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Return true if exists in the cache
	 * 
	 * @return
	 */
	protected boolean isRegistered() {
		return !(Roma.component(TemplateManager.class).getDesign(id) == null);
	}

	/**
	 * Save the template of the component
	 */
	public void saveTemplate() throws ReportingException {
		design.saveTemplate();
	}

}
