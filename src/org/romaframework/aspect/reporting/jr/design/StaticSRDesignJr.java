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

import net.sf.jasperreports.engine.design.JasperDesign;

import org.romaframework.core.schema.SchemaClassDefinition;

/**
 * It represents the design of a sub report, a sub report represents a field of
 * a component.
 * 
 * @author Giordano Maestro (giordano.maestro--at--assetdata.it)
 * 
 */
public class StaticSRDesignJr extends StaticDesignJr {

	public StaticSRDesignJr(SchemaClassDefinition iRootDesignClass, String iComponentId) {
		super(iRootDesignClass, iComponentId);
	}

	@Override
	protected JasperDesign getBaseDesign() {
		custom = templateManager.getCustomTemplate(rootDesignClass, componentId);
		if (custom != null) {
			customTemplate = true;
			return custom;
		}
		if (design == null) {

			return templateManager.getBaseSubReportTemplate(componentId, rootDesignClass);

		} else {
			return design;
		}
	}
}
