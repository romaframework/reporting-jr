/*
 * Copyright 2006-2007 Luca Garulli (luca.garulli--at--assetdata.it)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * 
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.romaframework.aspect.reporting.jr.view.domain;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.romaframework.aspect.core.annotation.AnnotationConstants;
import org.romaframework.aspect.core.annotation.CoreClass;
import org.romaframework.aspect.flow.FlowAspect;
import org.romaframework.aspect.reporting.ReportingAspect;
import org.romaframework.aspect.reporting.ReportingException;
import org.romaframework.aspect.validation.CustomValidation;
import org.romaframework.aspect.validation.ValidationException;
import org.romaframework.aspect.view.ViewCallback;
import org.romaframework.aspect.view.annotation.ViewAction;
import org.romaframework.aspect.view.annotation.ViewField;
import org.romaframework.core.Roma;
import org.romaframework.core.config.ApplicationConfiguration;
import org.romaframework.core.config.ContextException;
import org.romaframework.core.config.Refreshable;
import org.romaframework.core.schema.SchemaClass;
import org.romaframework.core.schema.SchemaManager;
import org.romaframework.core.schema.reflection.SchemaClassReflection;
import org.romaframework.core.serializer.RomaSerializationException;
import org.romaframework.frontend.domain.message.MessageOk;

/**
 * GUI to manage domain entities.
 * 
 * @author Luca Garulli (luca.garulli--at--assetdata.it)
 */
@CoreClass(orderFields = "entity availableClassNames functions", orderActions = "save edit preview")
public class TemplateManagerMain implements CustomValidation, ViewCallback, Refreshable {
	protected boolean						applicationDomainOnly	= true;
	@ViewField(selectionField = "selectedAvailableClassName", enabled = AnnotationConstants.FALSE)
	protected ArrayList<String>	availableClassNames;
	protected String						search;
	protected SchemaClass				selectedAvailableClass;

	public TemplateManagerMain() {
		loadClasses();
	}

	@ViewAction(visible = AnnotationConstants.FALSE)
	public void clean() {
		loadClasses();
		selectedAvailableClass = null;
		search = null;

		Roma.fieldChanged(this, "selectedAvailableClassNames");
		Roma.fieldChanged(this, "availableClassNames");
		Roma.fieldChanged(this, "search");
	}

	public void edit() throws RomaSerializationException {
		if (selectedAvailableClass == null) {
			return;
		}
		TemplateManagerPage pageToShow = new TemplateManagerPage(selectedAvailableClass);
		Roma.aspect(FlowAspect.class).forward(pageToShow);
	}

	public ArrayList<String> getAvailableClassNames() {
		return availableClassNames;
	}

	public String getSearch() {
		return search;
	}

	@ViewField(visible = AnnotationConstants.FALSE)
	public String getSelectedAvailableClassName() {
		if (selectedAvailableClass != null) {
			return selectedAvailableClass.getName();
		}
		return null;
	}

	public boolean isApplicationDomainOnly() {
		return applicationDomainOnly;
	}

	/**
	 * Load a class given the name.
	 * 
	 */
	public void load() {
		try {
			Roma.schema().getSchemaClass(search);
			setSelectedAvailableClassName(search);
			loadClasses();
			Roma.fieldChanged(this, "availableClassNames");
			Roma.aspect(FlowAspect.class).popup(new MessageOk("", "", null, "The Class is now available in list!"));
		}
		catch (Exception e) {
			Roma.aspect(FlowAspect.class).popup(new MessageOk("", "", null, "Class not found"));
		}
	}

	/**
	 * Load all the classes in the schema manager
	 * 
	 */
	private void loadClasses() {
		selectedAvailableClass = null;
		// search = "";

		SchemaManager schemaMgr = Roma.schema();
		Collection<SchemaClass> infos = schemaMgr.getAllClassesInfo();
		availableClassNames = new ArrayList<String>();
		if (!isApplicationDomainOnly()) {
			for (SchemaClass cls : infos) {
				availableClassNames.add(cls.getName());
			}
		} else {
			for (SchemaClass cls : infos) {
				String packageApp = Roma.component(ApplicationConfiguration.class).getApplicationPackage();
				if (cls != null && ((SchemaClassReflection) cls).getLanguageType() != null
						&& ((SchemaClassReflection) cls).getLanguageType().getPackage() != null) {
					String classPackage = ((SchemaClassReflection) cls).getLanguageType().getPackage().getName();
					if (classPackage!=null && classPackage.startsWith(packageApp)) {
						availableClassNames.add(cls.getName());
					}
				}
			}
		}
		Collections.sort(availableClassNames);
		Roma.fieldChanged(this, "availableClassNames");

	}

	public void onDispose() {}

	public void onShow() {
		loadClasses();
	}

	@ViewAction(visible = AnnotationConstants.FALSE)
	public void refresh() {
		loadClasses();
		Roma.fieldChanged(this, "availableClassNames");
		Roma.fieldChanged(this, "search");
	}

	public synchronized void save() throws ReportingException {

		if (selectedAvailableClass == null) {
			return;
		}
		try {
			Roma.aspect(ReportingAspect.class).createTemplate(selectedAvailableClass.newInstance());
		}
		catch (InstantiationException e) {
			throw new ReportingException("Cannot save template, maybe there is no empty constructor defined");
		}
		catch (IllegalAccessException e) {
			throw new ReportingException("Cannot save template, maybe there is no empty constructor defined");
		}
		catch (ContextException e) {
			throw new ReportingException("Cannot save template, maybe there is no empty constructor defined");
		}
		catch (IllegalArgumentException e) {
			throw new ReportingException("Cannot save template, maybe there is no empty constructor defined");
		}
		catch (SecurityException e) {
			throw new ReportingException("Cannot save template, maybe there is no empty constructor defined");
		}
		catch (InvocationTargetException e) {
			throw new ReportingException("Cannot save template, maybe there is no empty constructor defined");
		}
		catch (NoSuchMethodException e) {
			throw new ReportingException("Cannot save template, maybe there is no empty constructor defined");
		}
		Roma.aspect(FlowAspect.class).popup(new MessageOk("", "", null, "Templates saved successfully."));
	}

	public void setApplicationDomainOnly(boolean applicationDomainOnly) {
		this.applicationDomainOnly = applicationDomainOnly;
		loadClasses();
		Roma.aspect(FlowAspect.class).forward(this);
	}

	public void setAvailableClassNames(ArrayList<String> availableClassNames) {
		this.availableClassNames = availableClassNames;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public void setSelectedAvailableClassName(String iSelection) {
		selectedAvailableClass = Roma.schema().getSchemaClass(iSelection.toString());
		Roma.fieldChanged(this, "availableClassNames");

		search = iSelection.toString();
		Roma.fieldChanged(this, "search");
	}

	public void validate() throws ValidationException {}

}
