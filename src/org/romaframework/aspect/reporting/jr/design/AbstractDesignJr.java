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

import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.romaframework.aspect.reporting.jr.template.TemplateManager;
import org.romaframework.core.Roma;
import org.romaframework.core.schema.SchemaClassDefinition;

public abstract class AbstractDesignJr implements DesignJr {

	protected static TemplateManager	templateManager	= Roma.component(TemplateManager.class);

	/**
	 * There is a saved template for the object
	 */
	protected boolean									customTemplate;

	protected JasperDesign						custom;

	/**
	 * The id of the component
	 */
	protected String									componentId;

	/**
	 * The design of the component
	 */
	protected JasperDesign						design;

	/**
	 * The compiled report of the component
	 */
	protected JasperReport						report;

	/**
	 * The root component parent class.
	 */
	protected SchemaClassDefinition		rootDesignClass;

	public AbstractDesignJr(SchemaClassDefinition iRootDesignClass, String iComponentId) {
		rootDesignClass = iRootDesignClass;
		componentId = iComponentId;
		design = getBaseDesign();
	}

	protected abstract JasperDesign getBaseDesign();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.romaframework.aspect.reporting.jr.DesignJr#getDesign()
	 */
	public JasperDesign getDesign() {
		return design;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.romaframework.aspect.reporting.jr.DesignJr#saveTemplate()
	 */

}
