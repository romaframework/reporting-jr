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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;

import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.romaframework.aspect.reporting.ReportingAspectAbstract;
import org.romaframework.aspect.reporting.ReportingConstants;
import org.romaframework.aspect.reporting.ReportingException;
import org.romaframework.aspect.reporting.feature.ReportingClassFeatures;
import org.romaframework.aspect.reporting.jr.component.ObjectComponentJr;
import org.romaframework.aspect.reporting.jr.template.TemplateManager;
import org.romaframework.core.Roma;
import org.romaframework.core.Utility;
import org.romaframework.core.schema.SchemaClass;
import org.romaframework.core.schema.SchemaClassDefinition;
import org.romaframework.core.schema.SchemaClassResolver;
import org.romaframework.core.schema.SchemaEvent;
import org.romaframework.core.schema.SchemaFeatures;
import org.romaframework.core.schema.SchemaObject;

/**
 * Reporting implementation using JasperReports tool.
 * 
 * @author Giordano Maestro (giordano.maestro--at--assetdata.it)
 * 
 */
public class JRReportingAspect extends ReportingAspectAbstract {

	private static final String				JR_VIEW	= ".jr.view";

	private static final String				JR			= ".jr";

	protected static TemplateManager	templateManager;

	private static final String				UTF_8		= "UTF-8";

	protected final Log								log			= LogFactory.getLog("JR.REPORTING");

	public void createTemplate(Object iExample) throws ReportingException {
		initManager();
		templateManager.createClassTemplate(iExample);
	}

	private void export(JasperPrint jPrint, JRAbstractExporter exporter, OutputStream outputStream) throws JRException {

		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jPrint);
		exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, UTF_8);
		exporter.exportReport();
	}

	private void exportDocument(String iRenderType, SchemaFeatures schemaObject, JasperPrint jPrint, OutputStream outputStream)
			throws JRException {

		String documentType;
		if (iRenderType == null) {
			documentType = (String) schemaObject.getFeature(ReportingClassFeatures.DOCUMENT_TYPE);
		} else {
			documentType = iRenderType;
		}
		documentType = documentType.toLowerCase();
		if (ReportingConstants.DOCUMENT_TYPE_HTML.equals(documentType)) {
			final JRHtmlExporter exporter = new JRHtmlExporter();
			export(jPrint, exporter, outputStream);
		} else if (ReportingConstants.DOCUMENT_TYPE_CSV.equals(documentType)) {
			final JRCsvExporter exporter = new JRCsvExporter();
			export(jPrint, exporter, outputStream);
		} else if (ReportingConstants.DOCUMENT_TYPE_EXCEL.equals(documentType)) {
			final JRXlsExporter exporter = new JRXlsExporter();
			export(jPrint, exporter, outputStream);
		} else if (ReportingConstants.DOCUMENT_TYPE_XML.equals(documentType)) {
			JasperExportManager.exportReportToXmlStream(jPrint, outputStream);
		} else if (ReportingConstants.DOCUMENT_TYPE_ODF.equals(documentType)) {
			final JROdtExporter exporter = new JROdtExporter();
			export(jPrint, exporter, outputStream);
		}

		else if (ReportingConstants.DOCUMENT_TYPE_RTF.equals(documentType)) {

			final JRRtfExporter exporter = new JRRtfExporter();
			HashMap<String, String> fontMap = new HashMap<String, String>();
			fontMap.put("sansserif", "Arial");
			fontMap.put("serif", "Times New Roman");
			fontMap.put("monospaced", "Courier");

			exporter.setParameter(JRExporterParameter.FONT_MAP, fontMap);

			export(jPrint, exporter, outputStream);
		} else if (ReportingConstants.DOCUMENT_TYPE_TXT.equals(documentType)) {

			final JRTextExporter exporter = new JRTextExporter();

			jPrint.setPageWidth(800);
			exporter.setParameter(JRTextExporterParameter.CHARACTER_WIDTH, new Integer(10));
			exporter.setParameter(JRTextExporterParameter.CHARACTER_HEIGHT, new Integer(10));

			export(jPrint, exporter, outputStream);

		} else {
			JasperExportManager.exportReportToPdfStream(jPrint, outputStream);
		}
	}

	public String getDocumentType(String renderType) {
		if (renderType == null) {
			return "application/pdf";
		}
		renderType = renderType.toLowerCase();
		if (renderType.equals(ReportingConstants.DOCUMENT_TYPE_CSV)) {
			return "text/plain";
		} else if (renderType.equals(ReportingConstants.DOCUMENT_TYPE_EXCEL)) {
			return "application/ms-excel";
		} else if (renderType.equals(ReportingConstants.DOCUMENT_TYPE_XML)) {
			return "text/html";
		} else {
			return "application/pdf";
		}
	}

	public String[] getSupportedTypes() {
		final String[] result = { "PDF", "CSV", "XML", "XLS" };
		return result;
	}

	@Override
	protected void refresh(SchemaClassDefinition updatedClass) {
		Roma.component(TemplateManager.class).removeDesign(updatedClass.getSchemaClass().getName());
	}

	public void render(Object iToRender, String iRenderType, SchemaObject iUserSchema, OutputStream outputStream) {
		final long start = System.currentTimeMillis();

		try {
			final SchemaClass schemaObject = Roma.schema().getSchemaClass(iToRender.getClass());
			// Create the report
			final ObjectComponentJr jrReport = new ObjectComponentJr(schemaObject);
			jrReport.generateDesign();
			// Get the printer

			final JasperPrint jPrint = jrReport.getPrinter(iToRender, iUserSchema);

			final long time = System.currentTimeMillis() - start;
			log.debug("Report " + schemaObject + " exported. Millis: " + time);
			exportDocument(iRenderType, schemaObject, jPrint, outputStream);
		}
		catch (final JRException e) {
			throw new RuntimeException(e);
		}

	}

	public byte[] render(Object iToRender, String iRenderType, SchemaObject iUserSchema) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		render(iToRender, iRenderType, iUserSchema, outputStream);
		return outputStream.toByteArray();
	}

	public void renderCollection(Collection<?> collectionToRender, String iRenderType, SchemaObject iUserSchema,
			OutputStream outputStream) {
		final long start = System.currentTimeMillis();
		if (collectionToRender != null && collectionToRender.size() == 0) {
			return;
		}
		try {
			SchemaClass schemaObject = null;
			ObjectComponentJr jrReport = null;
			for (final Object iToRender : collectionToRender) {
				if (schemaObject == null) {
					schemaObject = Roma.schema().getSchemaClass(iToRender.getClass());
				}
				jrReport = new ObjectComponentJr(schemaObject);
				jrReport.generateDesign();
				break;
			}

			final JasperPrint jPrint = jrReport.getCollectionPrinter(collectionToRender, iUserSchema);

			// Get the printer

			final long time = System.currentTimeMillis() - start;
			log.debug("Report exported. Sec: " + time);
			exportDocument(iRenderType, schemaObject, jPrint, outputStream);
		}
		catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] renderCollection(Collection<?> collectionToRender, String iRenderType, SchemaObject iUserSchema) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		renderCollection(collectionToRender, iRenderType, iUserSchema, outputStream);
		return outputStream.toByteArray();
	}

	@Override
	public void startup() {
		super.startup();
		Roma.component(SchemaClassResolver.class).addDomainPackage(Utility.getRomaAspectPackage(aspectName() + JR));
		Roma.component(SchemaClassResolver.class).addDomainPackage(Utility.getRomaAspectPackage(aspectName() + JR_VIEW));
	}

	public void createDynaTemplate(Object iExample) throws ReportingException {
		initManager();
		templateManager.createDynaClassTemplate(iExample);

	}

	private void initManager() {
		if (templateManager == null) {
			templateManager = Roma.component(TemplateManager.class);
		}
	}

	public void configEvent(SchemaEvent event) {

	}

	public Object getUnderlyingComponent() {
		return null;
	}

}
