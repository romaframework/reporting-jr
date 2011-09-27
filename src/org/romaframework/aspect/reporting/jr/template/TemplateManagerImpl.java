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
package org.romaframework.aspect.reporting.jr.template;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.xml.JRXmlWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.romaframework.aspect.reporting.ReportingAspect;
import org.romaframework.aspect.reporting.ReportingException;
import org.romaframework.aspect.reporting.jr.component.ObjectComponentJr;
import org.romaframework.aspect.reporting.jr.design.DesignJr;
import org.romaframework.aspect.reporting.jr.design.StaticDesignJr;
import org.romaframework.aspect.reporting.jr.domain.TemplateFile;
import org.romaframework.core.Roma;
import org.romaframework.core.Utility;
import org.romaframework.core.config.ApplicationConfiguration;
import org.romaframework.core.io.virtualfile.VirtualFileFactory;
import org.romaframework.core.resource.AutoReloadManager;
import org.romaframework.core.schema.SchemaClass;
import org.romaframework.core.schema.SchemaClassDefinition;
import org.romaframework.core.schema.reflection.SchemaClassReflection;

public class TemplateManagerImpl implements TemplateManager {

	private String							customPath;

	private static final String	EXAMPLE_OBJECT_CANNOT_BE_NULL	= "Example object cannot be null";

	private static final String	TEMPLATE											= "template";

	private static final String	SUBTEMPLATE										= "subtemplate";

	private static final String	JRXML_EXTENSION								= ".jrxml";

	private static final String	BASE_TEMPLATE									= "Object.template";

	private static final String	BASE_TEMPLATE_SUBREPORT				= "Object.subtemplate";

	private static final String	UTF_8													= "UTF-8";

	private List<String>				packages											= new ArrayList<String>();

	private InputStream getBaseClassDesign(Class<?> objectClass) {
		if (objectClass.equals(Object.class)) {
			return null;
		}
		String packageFile = objectClass.getPackage().getName();
		packageFile = Utility.getResourcePath(packageFile);
		log.info("GetBaseClassDesign: " + Utility.PATH_SEPARATOR + packageFile + Utility.PATH_SEPARATOR + objectClass.getSimpleName()
				+ Utility.PACKAGE_SEPARATOR_STRING + TEMPLATE + JRXML_EXTENSION);

		String resourceString = Utility.PATH_SEPARATOR + packageFile + Utility.PATH_SEPARATOR + objectClass.getSimpleName()
				+ Utility.PACKAGE_SEPARATOR_STRING + TEMPLATE + JRXML_EXTENSION;
		URL resource = getClass().getResource(resourceString);

		if (resource == null) {
			return getBaseClassDesign(objectClass.getSuperclass());
		} else {
			log.info("GetBaseClassDesign file :" + resource.getFile());
			// log.info("GetBaseClassDesign file in jar : " + resource.getFile());
			final InputStream baseClassTemplate = VirtualFileFactory.getInstance().getFile(resource).getInputStream();
			// log.info("GetBaseClassDesign file input String : " +
			// baseClassTemplate);
			return baseClassTemplate;

		}
	}

	public List<String> getPackages() {
		return packages;
	}

	public void setPackages(List<String> customTemplatePackages) {
		this.packages = customTemplatePackages;
	}

	private InputStream getBaseClassSubDesign(Class<?> objectClass) {
		if (objectClass.equals(Object.class)) {
			return null;
		}

		String packageFile = objectClass.getPackage().getName();
		packageFile = Utility.getResourcePath(packageFile);
		log.info("GetBaseClassSubDesign : " + Utility.PATH_SEPARATOR + packageFile + Utility.PATH_SEPARATOR);
		URL resource = getClass().getResource(
				Utility.PATH_SEPARATOR + packageFile + Utility.PATH_SEPARATOR + objectClass.getSimpleName()
						+ Utility.PACKAGE_SEPARATOR_STRING + SUBTEMPLATE + JRXML_EXTENSION);
		if (resource == null) {
			return getBaseClassSubDesign(objectClass.getSuperclass());
		} else {
			log.info("GetBaseClassDesign file :" + resource.getFile());
			// log.info("GetBaseClassDesign file in jar : " + resource.getFile());
			final InputStream baseClassTemplate = VirtualFileFactory.getInstance().getFile(resource).getInputStream();
			// log.info("GetBaseClassDesign file input String : " +
			// baseClassTemplate);
			return baseClassTemplate;
		}
	}

	private InputStream getBaseFile(SchemaClassDefinition schemaClass) {
		return getBaseTemplate(BASE_TEMPLATE);
	}

	private InputStream getBaseSRFile(SchemaClassDefinition schemaClass) {
		return getBaseTemplate(BASE_TEMPLATE_SUBREPORT);
	}

	private InputStream getBaseTemplate(String fileName) {
		String packageFile;

		String path;

		if (customPath == null) {
			path = Roma.component(ApplicationConfiguration.class).getApplicationPackage();
		} else {
			path = customPath;
		}

		packageFile = path + "." + ReportingAspect.ASPECT_NAME;
		packageFile = Utility.getResourcePath(packageFile);
		log.info("GetBaseSRFile:" + Utility.PATH_SEPARATOR + packageFile + Utility.PATH_SEPARATOR + fileName + JRXML_EXTENSION);
		URL url = getClass().getResource(Utility.PATH_SEPARATOR + packageFile + Utility.PATH_SEPARATOR + fileName + JRXML_EXTENSION);
		return VirtualFileFactory.getInstance().getFile(url).getInputStream();
	}

	private File getClassDirectory(SchemaClassDefinition classDefinition) {
		String packageFile;
		final Class<?> toSave = ((SchemaClassReflection) classDefinition.getSchemaClass()).getLanguageType();
		packageFile = toSave.getPackage().getName();
		packageFile = Utility.getResourcePath(packageFile);
		log.info("GetClassDirectory : " + Utility.PATH_SEPARATOR + packageFile + Utility.PATH_SEPARATOR);
		final File templateDir = new File(getClass().getResource(Utility.PATH_SEPARATOR + packageFile + Utility.PATH_SEPARATOR)
				.getFile());
		return templateDir;
	}

	private Map<String, DesignJr>	designs	= new HashMap<String, DesignJr>();

	protected static Log					log			= LogFactory.getLog("JR.REPORTING.MANAGER");

	public TemplateManagerImpl() {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.romaframework.aspect.reporting.jr.template.TemplateManager#addDesign
	 * (java.lang.String, org.romaframework.aspect.reporting.jr.DesignJr)
	 */
	public void addDesign(String iId, DesignJr iDesign) {
		log.info("[TemplateManager] Adding Template: " + "-" + iDesign.getDesign().getName());
		designs.put(iId, iDesign);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.romaframework.aspect.reporting.jr.template.TemplateManager#getBaseDesign
	 * (java.lang.String, org.romaframework.core.schema.SchemaClassDefinition)
	 */
	public JasperDesign getBaseTemplate(String iName, SchemaClassDefinition schemaClass) {
		JasperDesign jasperDesign;
		try {

			InputStream template = getBaseClassDesign(((SchemaClassReflection) schemaClass.getSchemaClass()).getLanguageType());

			if (template != null) {

				jasperDesign = JRXmlLoader.load(template);
				jasperDesign.setName(iName);
				return jasperDesign;
			}
			template = getBaseFile(schemaClass);

			jasperDesign = JRXmlLoader.load(template);

			jasperDesign.setName(iName);

			return jasperDesign;

		}
		catch (final JRException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.romaframework.aspect.reporting.jr.template.TemplateManager#getCustomSRFile
	 * (org.romaframework.core.schema.SchemaClassDefinition, java.lang.String)
	 */
	public JasperDesign getCustomTemplate(SchemaClassDefinition schemaClass, String id) {
		Object[] customTemplates = (Object[]) packages.toArray();
		boolean stop = false;
		File file = null;
		URL resource = null;
		InputStream resourceAsStream = null;
		int i = customTemplates.length - 1;
		while (i >= 0 && !stop) {
			String packagePath = Utility.getResourcePath((String) customTemplates[i]);
			resourceAsStream = getClass().getResourceAsStream(
					Utility.PATH_SEPARATOR + packagePath + Utility.PATH_SEPARATOR + id + JRXML_EXTENSION);
			if (resourceAsStream != null) {
				resource = getClass().getResource(Utility.PATH_SEPARATOR + packagePath + Utility.PATH_SEPARATOR + id + JRXML_EXTENSION);
				file = new File(resource.getFile());
				stop = true;
			}
			i--;
		}
		if (file == null) {
			String packageFile = ((SchemaClassReflection) schemaClass.getSchemaClass()).getLanguageType().getPackage().getName();
			packageFile = Utility.getResourcePath(packageFile);
			log.info("GetCustomTemplate : " + Utility.PATH_SEPARATOR + packageFile + Utility.PATH_SEPARATOR + id + JRXML_EXTENSION);
			resourceAsStream = getClass().getResourceAsStream(
					Utility.PATH_SEPARATOR + packageFile + Utility.PATH_SEPARATOR + id + JRXML_EXTENSION);
			if (resourceAsStream != null) {
				resource = getClass().getResource(Utility.PATH_SEPARATOR + packageFile + Utility.PATH_SEPARATOR + id + JRXML_EXTENSION);
				file = new File(resource.getFile());
			}
		}
		if (resourceAsStream != null) {
			log.info("GetCustomTemplate file : " + file);
			JasperDesign jasperDesign;
			try {
				if (file != null && file.exists()) {
					Roma.component(AutoReloadManager.class).addResource(file, this);
				}
				jasperDesign = JRXmlLoader.load(resourceAsStream);
			}
			catch (final JRException e) {
				return null;
			}
			log.info("Loaded template from file: " + file.getAbsolutePath());
			return jasperDesign;
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.romaframework.aspect.reporting.jr.template.TemplateManager#getDesign
	 * (java.lang.String)
	 */
	public DesignJr getDesign(String iId) {

		return designs.get(iId);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.romaframework.aspect.reporting.jr.template.TemplateManager#
	 * getSubReportTemplate(java.lang.String,
	 * org.romaframework.core.schema.SchemaClassDefinition)
	 */
	public JasperDesign getBaseSubReportTemplate(String iName, SchemaClassDefinition schemaClass) {
		JasperDesign jasperDesign;
		try {
			InputStream template = getBaseClassSubDesign(((SchemaClassReflection) schemaClass.getSchemaClass()).getLanguageType());
			if (template != null) {
				jasperDesign = JRXmlLoader.load(template);
				jasperDesign.setName(iName);
				return jasperDesign;
			}
			template = getBaseSRFile(schemaClass);
			jasperDesign = JRXmlLoader.load(template);
			jasperDesign.setName(iName);
			return jasperDesign;
		}
		catch (final JRException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<TemplateFile> getTemplateFiles(SchemaClassDefinition classDefinition) {
		List<TemplateFile> templateFiles = new ArrayList<TemplateFile>();
		String[] customTemplates = (String[]) packages.toArray();
		File file;
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(JRXML_EXTENSION);
			}
		};
		for (int i = customTemplates.length - 1; i >= 0; i--) {
			String packagePath = Utility.getResourcePath(customTemplates[i]);
			file = new File(getClass().getResource(Utility.PATH_SEPARATOR + packagePath + Utility.PATH_SEPARATOR).getFile());
			addTemplateFiles(classDefinition, templateFiles, file, filter);
		}
		file = getClassDirectory(classDefinition);
		addTemplateFiles(classDefinition, templateFiles, file, filter);
		return templateFiles;
	}

	private void addTemplateFiles(SchemaClassDefinition classDefinition, List<TemplateFile> templateFiles, File file,
			FilenameFilter filter) {
		String[] files;
		files = file.list(filter);
		for (String templateFile : files) {
			if (templateFile.startsWith(classDefinition.getSchemaClass().getName())) {
				TemplateFile template = new TemplateFile(templateFile);
				if (!templateFiles.contains(template)) {
					templateFiles.add(template);
				}
			}
		}
	}

	public synchronized InputStream getTemplateStream(TemplateFile template, SchemaClassDefinition schemaClassDefinition)
			throws ReportingException {
		File dir = getClassDirectory(schemaClassDefinition);
		File templateFile = new File(dir.getAbsolutePath() + Utility.PATH_SEPARATOR + template.getFileName());
		try {
			return new FileInputStream(templateFile);
		}
		catch (FileNotFoundException e) {
			throw new ReportingException("Cannot recover template " + template.getFileName());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.romaframework.aspect.reporting.jr.template.TemplateManager#removeDesign
	 * (java.lang.String)
	 */
	public void removeDesign(String iId) {
		log.debug("[TemplateManager] Remove design Template: " + iId);
		if (designs.keySet().contains(iId)) {
			log.debug("[" + this.getClass().getSimpleName() + "] Try to remove " + iId);
			for (final String key : designs.keySet()) {
				if (key.startsWith(iId)) {
					if (designs.get(key) != null) {
						log.debug("[" + this.getClass().getSimpleName() + "] Removed " + key);
						designs.put(key, null);
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.romaframework.aspect.reporting.jr.template.TemplateManager#saveTemplate
	 * (net.sf.jasperreports.engine.design.JasperDesign,
	 * org.romaframework.core.schema.SchemaClassDefinition)
	 */
	public synchronized void saveTemplate(JasperDesign design, SchemaClassDefinition clazz) throws Exception {

		log.info("[TemplateManager] Saving Template: " + design.getName());
		final Class<?> toSave = ((SchemaClassReflection) clazz.getSchemaClass()).getLanguageType();
		String packageFile = toSave.getPackage().getName();
		packageFile = Utility.getResourcePath(packageFile);
		log.info("SaveTemplate : " + Utility.PATH_SEPARATOR + packageFile + Utility.PATH_SEPARATOR + design.getName() + JRXML_EXTENSION);
		File fileToSave = new File(getClass().getResource(Utility.PATH_SEPARATOR + packageFile + Utility.PATH_SEPARATOR).getFile()
				+ design.getName() + JRXML_EXTENSION);

		FileOutputStream file2 = new FileOutputStream(fileToSave);
		JRXmlWriter.writeReport(design, file2, UTF_8);
		file2.close();
		Roma.component(AutoReloadManager.class).addResource(fileToSave, this);

	}

	public synchronized void createDynaClassTemplate(Object iExample) throws ReportingException {

		if (iExample == null) {
			throw new ReportingException(EXAMPLE_OBJECT_CANNOT_BE_NULL);
		}
		try {
			SchemaClass schemaClass = Roma.schema().getSchemaClass(iExample.getClass());
			Roma.component(TemplateManager.class).removeDesign(schemaClass.getSchemaClass().getName());
			final ObjectComponentJr component = new ObjectComponentJr(schemaClass);
			component.generateDesign();
			component.getPrinter(iExample, null);
			component.saveTemplate();
		}
		catch (final JRException e) {
			throw new ReportingException(e);
		}
	}

	public synchronized void createClassTemplate(Object iExample) throws ReportingException {

		if (iExample == null) {
			throw new ReportingException(EXAMPLE_OBJECT_CANNOT_BE_NULL);
		}
		try {
			SchemaClass schemaClass = Roma.schema().getSchemaClass(iExample.getClass());
			Roma.component(TemplateManager.class).removeDesign(schemaClass.getSchemaClass().getName());
			final ObjectComponentJr component = new ObjectComponentJr(schemaClass);
			component.generateDesign();
			component.saveTemplate();
		}
		catch (final JRException e) {
			throw new ReportingException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.romaframework.aspect.reporting.jr.template.TemplateManager#
	 * signalUpdatedFile(java.io.File)
	 */
	public void signalUpdatedFile(File iFile) {
		try {
			log.info("[TemplateManager] Reloading file: " + iFile.getAbsolutePath());
			final JasperDesign jasperDesign = JRXmlLoader.load(iFile);
			final String id = jasperDesign.getName();
			final StaticDesignJr design = (StaticDesignJr) designs.get(id);
			if (design != null) {
				design.recompile(jasperDesign);
			}
		}
		catch (final JRException e) {
			e.printStackTrace();
		}
	}

	public synchronized void uploadTemplate(InputStream stream, SchemaClassDefinition schemaClassDefinition, String fileName)
			throws ReportingException {
		log.info("[TemplateManager] Uploading template : " + fileName);
		File dir = getClassDirectory(schemaClassDefinition);
		File templateFile = new File(dir.getAbsolutePath() + Utility.PATH_SEPARATOR + fileName);
		try {
			fileName = firstToUppercase(fileName);
			byte[] bytes = new byte[stream.available()];
			stream.read(bytes);
			FileOutputStream fileOutputStream = new FileOutputStream(templateFile);
			fileOutputStream.write(bytes);
			fileOutputStream.close();
		}
		catch (FileNotFoundException e) {
			throw new ReportingException("Cannot upload template " + fileName, e);
		}
		catch (IOException e) {
			throw new ReportingException("Cannot upload template " + fileName, e);
		}
	}

	public String getCustomPath() {
		return customPath;
	}

	/**
	 * Return the string with the first letter to uppercase
	 * 
	 * @param s
	 * @return
	 */
	private static String firstToUppercase(String s) {
		if (s == null) {
			return null;
		}

		if (s.length() > 1) {
			return s.substring(0, 1).toUpperCase() + s.substring(1);
		}

		return s.toUpperCase();
	}

	public void setCustomPath(String customPath) {
		this.customPath = customPath;
	}

}
