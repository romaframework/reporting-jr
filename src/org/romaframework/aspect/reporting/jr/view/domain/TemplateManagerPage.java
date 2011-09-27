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
package org.romaframework.aspect.reporting.jr.view.domain;

import java.io.InputStream;
import java.util.List;

import org.romaframework.aspect.core.annotation.AnnotationConstants;
import org.romaframework.aspect.reporting.ReportingException;
import org.romaframework.aspect.reporting.jr.domain.TemplateFile;
import org.romaframework.aspect.reporting.jr.template.TemplateManager;
import org.romaframework.aspect.view.ViewAspect;
import org.romaframework.aspect.view.ViewConstants;
import org.romaframework.aspect.view.annotation.ViewField;
import org.romaframework.aspect.view.command.impl.DownloadStreamViewCommand;
import org.romaframework.core.Roma;
import org.romaframework.core.domain.type.Stream;
import org.romaframework.core.schema.SchemaClassDefinition;

public class TemplateManagerPage {
	private static TemplateManager	manager	= Roma.component(TemplateManager.class);

	private SchemaClassDefinition		classDefinition;

	@ViewField(visible = AnnotationConstants.FALSE)
	private TemplateFile						selectedTemplate;

	@ViewField(render = ViewConstants.RENDER_TABLE, enabled = AnnotationConstants.FALSE, selectionField = "selectedTemplate")
	private List<TemplateFile>			templateFiles;
	@ViewField(render = ViewConstants.RENDER_UPLOAD)
	private Stream									uploadFile;

	public TemplateManagerPage(SchemaClassDefinition iClassDefinition) {
		classDefinition = iClassDefinition;
		selectedTemplate = null;
		loadTemplates();
	}

	public void back() {
		Roma.flow().back();
	}

	public TemplateFile getSelectedTemplate() {
		return selectedTemplate;
	}

	public List<TemplateFile> getTemplateFiles() {
		return templateFiles;
	}

	public Stream getUploadFile() {
		return uploadFile;
	}

	private void loadTemplates() {
		templateFiles = manager.getTemplateFiles(classDefinition);
	}

	public void onUploadFile() throws ReportingException {
		String filename = uploadFile.getFileName();
		InputStream stream = uploadFile.getInputStream();
		manager.uploadTemplate(stream, classDefinition, filename);
		refresh();
	}

	protected void refresh() {
		loadTemplates();
		selectedTemplate = null;
		Roma.fieldChanged(this, "templateFiles");
	}

	public void setSelectedTemplate(TemplateFile selectedTemplate) throws ReportingException {
		this.selectedTemplate = selectedTemplate;
		if (selectedTemplate == null) {
			return;
		} else {
			InputStream stream = manager.getTemplateStream(selectedTemplate, classDefinition);
			Roma.aspect(ViewAspect.class)
					.pushCommand(new DownloadStreamViewCommand(stream, selectedTemplate.getFileName(), "text/plain"));
		}
	}

	public void setUploadFile(Stream uploadFile) {
		this.uploadFile = uploadFile;
	}

}
