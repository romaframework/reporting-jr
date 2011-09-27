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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.sf.jasperreports.engine.design.JasperDesign;

import org.romaframework.aspect.reporting.ReportingException;
import org.romaframework.aspect.reporting.jr.design.DesignJr;
import org.romaframework.aspect.reporting.jr.domain.TemplateFile;
import org.romaframework.core.resource.AutoReloadListener;
import org.romaframework.core.schema.SchemaClassDefinition;

/**
 * Manage the template jrxml and the cache of design components
 * 
 * @author Giordano Maestro (giordano.maestro--at--assetdata.it)
 * 
 */
public interface TemplateManager extends AutoReloadListener {

	/**
	 * Add a component design to the cache
	 * 
	 * @param iId
	 * @param iDesign
	 */
	public void addDesign(String iId, DesignJr iDesign);

	/**
	 * Get the base template of a component
	 * 
	 * @param iName
	 * @param schemaClass
	 * @return
	 * @throws IOException
	 */
	public JasperDesign getBaseTemplate(String iName, SchemaClassDefinition schemaClass);

	/**
	 * Retrieve a template previously saved.
	 * 
	 * @param iParentSchemaClass
	 * @param id
	 * @return
	 */
	public JasperDesign getCustomTemplate(SchemaClassDefinition iParentSchemaClass, String id);

	/**
	 * Get a design from the cache
	 * 
	 * @param iId
	 * @return
	 */
	public DesignJr getDesign(String iId);

	/**
	 * Get the base template of a sub report component
	 * 
	 * @param iName
	 * @param schemaClass
	 * @return
	 * @throws IOException
	 */
	public JasperDesign getBaseSubReportTemplate(String iName, SchemaClassDefinition schemaClass);

	/**
	 * Get the available templates for the class
	 * 
	 * @param classDefinition
	 * @return
	 */
	public List<TemplateFile> getTemplateFiles(SchemaClassDefinition classDefinition);

	/**
	 * Get a Template Stream
	 * 
	 * @param template
	 * @param schemaClassDefinition
	 * @return The template Stream
	 * @throws ReportingException
	 */
	public InputStream getTemplateStream(TemplateFile template, SchemaClassDefinition schemaClassDefinition)
			throws ReportingException;

	/**
	 * Remove a design from the cache
	 * 
	 * @param iId
	 */
	public void removeDesign(String iId);

	/**
	 * Save a template for the given class
	 * 
	 * @param design
	 * @param clazz
	 * @throws Exception
	 */
	public void saveTemplate(JasperDesign design, SchemaClassDefinition clazz) throws Exception;

	/**
	 * Upload a template for the given class
	 * 
	 * @param stream
	 * @param schemaClassDefinition
	 * @param fileName
	 * @throws ReportingException
	 */
	public void uploadTemplate(InputStream stream, SchemaClassDefinition schemaClassDefinition, String fileName)
			throws ReportingException;

	/**
	 * Create the templates for the given class
	 * 
	 * @param example
	 * @throws ReportingException
	 */
	public void createClassTemplate(Object example) throws ReportingException;

	/**
	 * Create the templates for the given class, this method create also class
	 * based template for dynamic components.
	 * 
	 * @param example
	 * @throws ReportingException
	 */
	public void createDynaClassTemplate(Object example) throws ReportingException;

	public String getCustomPath();
}