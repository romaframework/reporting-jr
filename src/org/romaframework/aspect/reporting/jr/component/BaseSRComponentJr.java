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

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignSubreport;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.romaframework.aspect.reporting.jr.JRDesignHelper;
import org.romaframework.aspect.reporting.jr.design.DesignJr;
import org.romaframework.aspect.reporting.jr.design.StaticSRDesignJr;
import org.romaframework.core.schema.SchemaClassDefinition;
import org.romaframework.core.schema.SchemaFeatures;

public abstract class BaseSRComponentJr extends BaseComponentJr implements DesignComponent {

	public BaseSRComponentJr(BaseComponentJr iParent, SchemaFeatures iSchemaFeatures, SchemaClassDefinition iRootDesignClass) {
		super(iParent, iSchemaFeatures, iRootDesignClass);
	}

	public void fillParentBand(int columnWidth, JRDesignBand detailBand) throws JRException {
		final JRDesignSubreport sub = getSubReport();
		sub.setHeight(JRDesignHelper.FONT_BOX);
		sub.setY(detailBand.getHeight());
		sub.setWidth(columnWidth);
		detailBand.setHeight(detailBand.getHeight() + sub.getHeight());
		detailBand.addElement(sub);
	}

	protected JRDesignSubreport getSubReport() throws JRException {
		final JasperDesign iDesign = design.getDesign();
		return JRDesignHelper.getSubReport(iDesign, id);
	}

	@Override
	protected DesignJr getBaseDesign() {
		return new StaticSRDesignJr(rootDesignClass, id);
	}

}
