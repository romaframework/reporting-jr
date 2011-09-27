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
package org.romaframework.aspect.reporting.jr;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRSimpleTemplate;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignSubreport;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.HorizontalAlignEnum;
import net.sf.jasperreports.engine.type.LineStyleEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.PositionTypeEnum;
import net.sf.jasperreports.engine.type.StretchTypeEnum;
import net.sf.jasperreports.engine.type.VerticalAlignEnum;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.romaframework.aspect.i18n.I18NHelper;
import org.romaframework.aspect.reporting.ReportingAspect;
import org.romaframework.aspect.reporting.ReportingConstants;
import org.romaframework.aspect.reporting.ReportingFeatureHelper;
import org.romaframework.aspect.reporting.jr.component.BaseCollectionComponentJr;
import org.romaframework.aspect.reporting.jr.component.BaseComponentJr;
import org.romaframework.aspect.reporting.jr.component.CollectionListComponentJr;
import org.romaframework.aspect.reporting.jr.component.CollectionRowSetComponentJr;
import org.romaframework.aspect.reporting.jr.component.CollectionTableComponentJr;
import org.romaframework.aspect.reporting.jr.component.DesignComponent;
import org.romaframework.aspect.reporting.jr.component.ExpandedComponentJr;
import org.romaframework.aspect.reporting.jr.component.GridCollectionComponentJr;
import org.romaframework.aspect.reporting.jr.component.MapRowSetComponentJr;
import org.romaframework.aspect.reporting.jr.component.MapTableComponentJr;
import org.romaframework.aspect.reporting.jr.component.ObjectEmbeddedJr;
import org.romaframework.aspect.reporting.jr.design.DesignJr;
import org.romaframework.aspect.reporting.jr.ds.RomaHashMapListDataSource;
import org.romaframework.aspect.reporting.jr.element.FieldJr;
import org.romaframework.aspect.reporting.jr.element.JrChartImage;
import org.romaframework.aspect.reporting.jr.element.JrDesignImage;
import org.romaframework.aspect.reporting.jr.element.ParameterJr;
import org.romaframework.aspect.reporting.jr.template.TemplateManager;
import org.romaframework.aspect.view.ViewAspect;
import org.romaframework.core.Roma;
import org.romaframework.core.Utility;
import org.romaframework.core.config.ApplicationConfiguration;
import org.romaframework.core.io.virtualfile.VirtualFile;
import org.romaframework.core.io.virtualfile.VirtualFileFactory;
import org.romaframework.core.schema.SchemaClassDefinition;
import org.romaframework.core.schema.SchemaClassElement;
import org.romaframework.core.schema.SchemaFeatures;
import org.romaframework.core.schema.SchemaField;

public class JRDesignHelper {

	private static final String								SOURCE_GET_CLOSE			= "\")";

	private static final String								SOURCE_GET						= "{source}.get(\"";

	private static final String								HELVETICA_BOLD				= "Helvetica-Bold";

	private static final String								_LOG_JRDESIGN_HELPER	= "[JRDesignHelper] ";

	private static final String								DOT										= ".";

	private static final String								_CLOSE								= "}";

	private static final String								_OPEN									= "{";

	private static final String								$											= "$";

	private static final ByteArrayInputStream	EMPTYSTREAM						= null;

	protected static Log											log										= LogFactory.getLog("JR.REPORTING.HELPER");

	public static final String								FIELD_TYPE						= "F";

	public static final String								font									= "Arial";

	public static final int										FONT_BOX							= 19;
	public static final int										IMAGE_BOX							= 100;

	public static final int										FONT_SIZE							= 10;

	private static final int									MAX_EMBEDDED_LEVEL		= 3;
	public static final int										MAX_EXPAND_LEVEL			= 3;
	private static final String								SUBREPORT_DESIGN_KEY	= "subreportDesign_";
	private static final String								SUBREPORT_SOURCE_KEY	= "subreportSource_";
	public static final int										TITLE_BOX							= 26;
	public static final int										TITLE_SIZE						= 14;

	public static void addFieldToTemplate(DesignJr design) throws JRException {
		// Add the fields to the design
		final JRDesignField field = new JRDesignField();
		field.setName(RomaHashMapListDataSource.FIELD_NAME);
		field.setValueClass(Map.class);
		if (!design.isCustomTemplate()) {
			design.getDesign().addField(field);
		}
	}

	private static DesignComponent generateCollectionSubReport(SchemaField schemaField, SchemaClassDefinition designClassToRender,
			BaseComponentJr componentThis) throws JRException {
		final String fieldRender = ReportingFeatureHelper.getRender(schemaField);
		BaseCollectionComponentJr comp;
		if (ReportingConstants.RENDER_ROWSET.equals(fieldRender)) {
			comp = new CollectionRowSetComponentJr(componentThis, schemaField, designClassToRender);
			comp.generateDesign();
		} else if (ReportingConstants.RENDER_TABLE.equals(fieldRender) || ReportingConstants.RENDER_TABLEEDIT.equals(fieldRender)) {
			comp = new CollectionTableComponentJr(componentThis, schemaField, designClassToRender);
			comp.generateDesign();
		} else {
			comp = new CollectionListComponentJr(componentThis, schemaField, designClassToRender);
			comp.generateDesign();
		}
		return comp;
	}

	private static DesignComponent generateMapSubReport(SchemaField schemaField, SchemaClassDefinition designClassToRender,
			BaseComponentJr componentThis) throws JRException {
		final String fieldRender = ReportingFeatureHelper.getRender(schemaField);
		BaseCollectionComponentJr comp;
		if (ReportingConstants.RENDER_ROWSET.equals(fieldRender)) {
			comp = new MapRowSetComponentJr(componentThis, schemaField, designClassToRender);
			comp.generateDesign();
		} else {
			comp = new MapTableComponentJr(componentThis, schemaField, designClassToRender);
			comp.generateDesign();
		}
		return comp;

	}

	public static JRDesignBand getBand() {
		final JRDesignBand band = new JRDesignBand();
		band.setHeight(0);
		return band;
	}

	public static JRDesignTextField getFieldExpression(String name, Class<?> type, String markup) {
		// Adding the field expression
		final JRDesignExpression expression = new JRDesignExpression();
		expression.setText($ + FIELD_TYPE + _OPEN + name + _CLOSE);

		return getJRDesignTextFieldInstance(markup, expression);
	}

	public static JRDesignTextField getIndirectFieldExpression(String name, Class<?> type, String markup) {
		// Adding the field expression
		final JRDesignExpression expression = new JRDesignExpression();
		expression.setText($ + FIELD_TYPE + SOURCE_GET + name + SOURCE_GET_CLOSE);

		return getJRDesignTextFieldInstance(markup, expression);
	}

	private static JRDesignTextField getJRDesignTextFieldInstance(String markup, final JRDesignExpression expression) {
		JRDesignTextField textField = new JRDesignTextField();
		textField.setHeight(FONT_BOX);
		textField.setFontSize(FONT_SIZE);
		textField.setPositionType(PositionTypeEnum.FLOAT);
		textField.setHorizontalAlignment(HorizontalAlignEnum.LEFT);
		textField.setStretchType(StretchTypeEnum.NO_STRETCH);
//		textField.getLineBox().getPen().setLineWidth(JRPen.LINE_WIDTH_1);
//		textField.getLineBox().getPen().setLineStyle(JRPen.LINE_STYLE_SOLID);
		textField.setMarkup(markup);
		textField.setPdfEmbedded(true);

		textField.setPdfFontName(HELVETICA_BOLD);
		textField.setBlankWhenNull(true);
		textField.setExpression(expression);
		return textField;
	}

	public static String getMarkupType(SchemaFeatures iSchemaField) {

		if (ReportingFeatureHelper.isRenderRTF(iSchemaField)) {
			return JRDesignTextField.MARKUP_RTF;

		} else if (ReportingFeatureHelper.isRenderHtml(iSchemaField)) {
			return JRDesignTextField.MARKUP_HTML;
		} else {
			return JRDesignTextField.MARKUP_NONE;
		}

	}


	public static InputStream getImage(String name) {
		log.info(_LOG_JRDESIGN_HELPER + "Loading image: " + name);
		InputStream image = getImageByAspect(name, ReportingAspect.ASPECT_NAME);
		if (image == null) {
			return getImageByAspect(name, ViewAspect.ASPECT_NAME);
		}
		return image;

	}

	private static InputStream getImageByAspect(String name, String aspectName) {
		final Class<?> toSave = Class.class;

		String customPath = Roma.component(TemplateManager.class).getCustomPath();

		String path;
		if (customPath == null) {
			path = Roma.component(ApplicationConfiguration.class).getApplicationPackage();
		} else {
			path = customPath;
		}
		String packageFile = path + DOT + aspectName + ".image";
		packageFile = Utility.getResourcePath(packageFile);
		log.info(_LOG_JRDESIGN_HELPER + "getImage: " + Utility.PATH_SEPARATOR + packageFile + Utility.PATH_SEPARATOR + name);
		URL url = toSave.getResource(Utility.PATH_SEPARATOR + packageFile + Utility.PATH_SEPARATOR + name);

		try {
			InputStream stream = EMPTYSTREAM;
			VirtualFile file = VirtualFileFactory.getInstance().getFile(url);
			if (file != null) {
				stream = file.getInputStream();
			} else {
				log.warn(_LOG_JRDESIGN_HELPER + "getImage: url : " + url);
				log
						.warn(_LOG_JRDESIGN_HELPER + "cannot find image " + Utility.PATH_SEPARATOR + packageFile + Utility.PATH_SEPARATOR
								+ name);
			}
			log.debug(_LOG_JRDESIGN_HELPER + "getImage: " + stream);
			return stream;
		}
		catch (Throwable e) {
			log.error("Unable to get image " + name + " cause: " + e, e);
			return null;
		}
	}

	public static JRDesignImage getIndirectElementImageExpression(String name) {
		JRDesignImage textField = new JRDesignImage(new JasperDesign());
		textField.setHeight(FONT_BOX);
		textField.setPositionType(PositionTypeEnum.FLOAT);
		textField.setHorizontalAlignment(HorizontalAlignEnum.LEFT);
		textField.setStretchType(StretchTypeEnum.NO_STRETCH);
		// Adding the field expression
		final JRDesignExpression expression = new JRDesignExpression();
		expression.setText("JRDesignHelper.getImage((String)(" + $ + FIELD_TYPE + SOURCE_GET + name + SOURCE_GET_CLOSE + "))");
		textField.setExpression(expression);
		return textField;
	}

	/**
	 * Create a label object with the default size and align values
	 * 
	 * @param field, the field to label
	 * @return
	 */
	public static JRDesignStaticText getLabel(SchemaFeatures schemaFeature) {
		final JRDesignStaticText staticText = new JRDesignStaticText(new JRSimpleTemplate());
		staticText.setHeight(FONT_BOX);
		staticText.setPositionType(PositionTypeEnum.FLOAT);
		staticText.setBold(true);
		staticText.getLineBox().setLeftPadding(1);
		staticText.setHorizontalAlignment(HorizontalAlignEnum.LEFT);
		staticText.setVerticalAlignment(VerticalAlignEnum.MIDDLE);
		staticText.setStretchType(StretchTypeEnum.NO_STRETCH);
		if (schemaFeature instanceof SchemaClassElement) {
			staticText.setText(ReportingFeatureHelper.getI18NLabel((SchemaClassElement) schemaFeature));
		} else {
			staticText.setText(ReportingFeatureHelper.getI18NLabel((SchemaClassDefinition) schemaFeature));
		}
		staticText.setBold(true);
		staticText.setPdfEmbedded(true);
		staticText.setPdfFontName(HELVETICA_BOLD);
		staticText.setFontSize(FONT_SIZE);
		return staticText;
	}

	/**
	 * Get a jasper sub report definition.
	 * 
	 * @param iDesign
	 * @param id
	 * @return
	 */
	public static JRDesignSubreport getSubReport(JasperDesign iDesign, String id) {
		final JRDesignSubreport sub = new JRDesignSubreport(iDesign);
		// Setting subExspression
		final JRDesignExpression subExpr = new JRDesignExpression();
		subExpr.setText("$F{source}.get(\"" + JRDesignHelper.subReportDesignKey(id) + SOURCE_GET_CLOSE);
		sub.setExpression(subExpr);
		final JRDesignExpression subExpr2 = new JRDesignExpression();
		subExpr2.setText("$F{source}.get(\"" + JRDesignHelper.subReportSourceKey(id) + SOURCE_GET_CLOSE);
		sub.setDataSourceExpression(subExpr2);
		sub.setPositionType(PositionTypeEnum.FLOAT);
		return sub;
	}

	public static JRDesignBand getTitleBand(SchemaClassDefinition schemaClass, int width) {

		String reportName = ReportingFeatureHelper.getLabel(schemaClass);
		reportName = I18NHelper.getLabel(schemaClass, reportName);

		if (reportName == null) {
			reportName = schemaClass.getName();
		}

		final JRDesignStaticText title = getLabel(schemaClass);
		
		title.setFontSize(TITLE_SIZE);
		title.setHeight(TITLE_BOX);
		
		title.setWidth(width);
		title.setHorizontalAlignment(HorizontalAlignEnum.CENTER);
		title.setPdfEmbedded(true);
		final JRDesignBand titleBand = getBand();
		titleBand.setHeight(titleBand.getHeight() + title.getHeight());
		titleBand.addElement(title);
		return titleBand;
	}

	public static void renderFields(DesignJr design, SchemaClassDefinition designClassToRender, BaseComponentJr toRender,
			SchemaClassDefinition iRootClassDesign) throws JRException {
		final Iterator<SchemaField> iterator = designClassToRender.getFieldIterator();

		while (iterator.hasNext()) {
			final SchemaField schemaField = iterator.next();
			if (!ReportingFeatureHelper.isVisibleField(schemaField)) {
				continue;
			}

			final Class<?> fieldClass = (Class<?>) schemaField.getLanguageType();
			if (ReportingFeatureHelper.isRenderChart(schemaField)) {
				renderChart(design, toRender, schemaField);
			} else if (ReportingFeatureHelper.isRenderImage(schemaField)) {
				renderImage(design, toRender, schemaField);
			} else if (ReflectionHelper.isCollection(fieldClass) && ReflectionHelper.isCollectionOfCollection(schemaField)) {
				renderGrid(design, designClassToRender, toRender, schemaField);
			} else if (!ReflectionHelper.isCollectionMapSet(fieldClass)) {
				renderObject(design, designClassToRender, toRender, iRootClassDesign, schemaField);
			} else if (ReflectionHelper.isCollection(fieldClass)) {
				renderCollection(design, designClassToRender, toRender, schemaField);
			} else if (ReflectionHelper.isMap(fieldClass)) {
				renderMap(design, designClassToRender, toRender, schemaField);
			}
		}

	}

	private static void renderChart(DesignJr design, BaseComponentJr toRender, SchemaField schemaField) {
		JrChartImage param = new JrChartImage(toRender.getId(), schemaField);
		design.addSubReport(schemaField, param);
	}

	private static void renderImage(DesignJr design, BaseComponentJr toRender, final SchemaField schemaField) {
		JrDesignImage param = new JrDesignImage(toRender.getId(), schemaField);
		design.addSubReport(schemaField, param);
	}

	private static void renderGrid(DesignJr design, SchemaClassDefinition designClassToRender, BaseComponentJr toRender,
			final SchemaField schemaField) throws JRException {
		GridCollectionComponentJr comp = new GridCollectionComponentJr(toRender, schemaField, designClassToRender);
		comp.generateDesign();
		design.addSubReport(schemaField, comp);
	}

	private static void renderMap(DesignJr design, SchemaClassDefinition designClassToRender, BaseComponentJr toRender,
			final SchemaField schemaField) throws JRException {
		final DesignComponent comp = JRDesignHelper.generateMapSubReport(schemaField, designClassToRender, toRender);
		design.addSubReport(schemaField, comp);
	}

	private static void renderCollection(DesignJr design, SchemaClassDefinition designClassToRender, BaseComponentJr toRender,
			final SchemaField schemaField) throws JRException {
		// generate collection sub report
		final DesignComponent comp = JRDesignHelper.generateCollectionSubReport(schemaField, designClassToRender, toRender);
		design.addSubReport(schemaField, comp);
		// end generate collection sub report
	}

	private static void renderObject(DesignJr design, SchemaClassDefinition designClassToRender, BaseComponentJr toRender,
			SchemaClassDefinition iRootClassDesign, final SchemaField schemaField) throws JRException {
		final String objectEmbedded = ReportingFeatureHelper.getRender(schemaField);
		if (ReportingConstants.LAYOUT_EXPAND.equals(ReportingFeatureHelper.getLayout(schemaField))) {
			ExpandedComponentJr comp = new ExpandedComponentJr(toRender, schemaField, designClassToRender);
			comp.generateDesign();
			design.addSubReport(schemaField, comp);
		} else if (ReportingConstants.RENDER_OBJECTEMBEDDED.equals(objectEmbedded) && toRender.getLevel() < MAX_EMBEDDED_LEVEL) {
			final ObjectEmbeddedJr comp = new ObjectEmbeddedJr(toRender, schemaField, iRootClassDesign);
			comp.generateDesign();
			design.addSubReport(schemaField, comp);
		} else {
			final ParameterJr param = new ParameterJr(toRender.getId(), schemaField);
			design.addSubReport(schemaField, param);
		}
	}

	public static String subReportDesignKey(String id) {
		return SUBREPORT_DESIGN_KEY + id;
	}

	public static String subReportSourceKey(String id) {
		return SUBREPORT_SOURCE_KEY + id;
	}

	public static String toString(Object iObject) {
		if (iObject == null) {
			return null;
		}
		if (iObject instanceof Date) {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			return format.format(iObject);
		}
		return iObject.toString();
	}

	public static void fillTable(List<FieldJr> fields, final int fieldWidth, final JRDesignBand columnHeader,
			final JRDesignBand detailBand, final DesignJr design) throws JRException {
		int x = 0;
		for (final FieldJr field : fields) {
			// Set the field definition on the jasper template
			if (!design.isCustomTemplate()) {
				design.getDesign().addField(field.getDefinition());
			}

			// Set the header

			final JRDesignStaticText label = field.getLabel();
			label.setBold(true);
			label.setMode(ModeEnum.OPAQUE);
			label.setBackcolor(new Color(174,197,228));
			label.getLineBox().getPen().setLineWidth(JRPen.LINE_WIDTH_1);
			
			label.getLineBox().setLeftPadding(1);
			label.setY(TITLE_BOX);
			label.setWidth(fieldWidth);
			label.setX(x);
			
			columnHeader.addElement(label);

			// Set the field text

			final JRDesignTextField fieldText = field.getElement();
			fieldText.getLineBox().getPen().setLineWidth(JRPen.LINE_WIDTH_1);
			fieldText.getLineBox().getPen().setLineStyle(LineStyleEnum.SOLID);
			fieldText.getLineBox().setLeftPadding(1);
			fieldText.setY(0);
			fieldText.setWidth(fieldWidth);
			fieldText.setX(x);
			fieldText.setStretchWithOverflow(true);
			fieldText.setStretchType(StretchTypeEnum.RELATIVE_TO_BAND_HEIGHT);
			detailBand.addElement(fieldText);

			x = x + fieldWidth;
		}
	}

}
