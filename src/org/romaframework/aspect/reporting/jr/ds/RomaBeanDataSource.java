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
package org.romaframework.aspect.reporting.jr.ds;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;

import org.romaframework.aspect.reporting.ReportingFeatureHelper;
import org.romaframework.aspect.reporting.jr.JRDesignHelper;
import org.romaframework.aspect.reporting.jr.ReflectionHelper;
import org.romaframework.aspect.reporting.jr.element.ExpandedFieldJr;
import org.romaframework.core.flow.ObjectContext;
import org.romaframework.core.schema.SchemaClassDefinition;
import org.romaframework.core.schema.SchemaField;
import org.romaframework.core.schema.SchemaHelper;
import org.romaframework.core.schema.reflection.SchemaFieldReflection;

public class RomaBeanDataSource implements JRRewindableDataSource {

	private Object								current;
	private Iterator<Object>			iterator;
	private final List<Object>		list								= new LinkedList<Object>();
	private SchemaClassDefinition	schemaObject				= null;
	public static final String		CONTEXT_REQUEST_PAR	= "$#$Http_Request$#$";
	private Object								currentRequest;

	public RomaBeanDataSource(Collection<?> iList, SchemaClassDefinition iSchemaObject) {
		super();
		currentRequest = ObjectContext.getInstance().getContextComponent(CONTEXT_REQUEST_PAR);
		if (iList != null) {
			list.addAll(iList);
		}
		schemaObject = iSchemaObject;
		iterator = list.iterator();
	}

	private void initSessionAspect() {
		ObjectContext.getInstance().setContextComponent(CONTEXT_REQUEST_PAR, currentRequest);
	}

	private void deInitSessionAspect() {
		ObjectContext.getInstance().setContextComponent(CONTEXT_REQUEST_PAR, null);
	}

	public Object getFieldValue(JRField iField) throws JRException {
		initSessionAspect();
		final String fieldName = iField.getName();
		SchemaField schemaField = schemaObject.getField(iField.getName());
		Object returnValue;
		if (fieldName.startsWith(ExpandedFieldJr.EXPANDED)) {
			returnValue = getExpandedField(iField);
		} else {
			returnValue = getField(iField, schemaField, current);
		}
		deInitSessionAspect();
		return returnValue;
	}

	private Object getExpandedField(JRField iField) {
		Object invokeOn;
		final String fieldNames[] = iField.getName().split("\\|");
		final SchemaField sourceSchemaField = schemaObject.getField(fieldNames[2]);
		invokeOn = ReflectionHelper.getFieldValue(sourceSchemaField, current);
		SchemaClassDefinition sourceSchemaClass = null;
		if (invokeOn != null) {
			sourceSchemaClass = sourceSchemaField.getType();
			SchemaField schemaField = sourceSchemaClass.getField(fieldNames[3]);
			return getField(iField, schemaField, invokeOn);
		} else {
			return null;
		}
	}

	private Object getField(JRField iField, SchemaField schemaField, Object iInvokeOn) {

		if (ReflectionHelper.isJRType(((SchemaFieldReflection) schemaField).getLanguageType())) {
			return ReflectionHelper.getFieldValue(schemaField, iInvokeOn);
		} else {
			return JRDesignHelper.toString(SchemaHelper.getFieldValue(schemaField, iInvokeOn));
		}
	}

	public Object getFieldValue(JRField iField, int i) throws JRException {

		final String fieldName = iField.getName();
		SchemaFieldReflection schemaField = null;
		Object invokeOn;
		boolean subInvoke = false;
		if (fieldName.startsWith(ExpandedFieldJr.EXPANDED)) {
			final String fieldNames[] = fieldName.split("\\|");
			final SchemaField sourceSchemaField = schemaObject.getField(fieldNames[2]);
			invokeOn = ReflectionHelper.getFieldValue(sourceSchemaField, current);
			SchemaClassDefinition sourceSchemaClass = null;
			if (invokeOn != null) {
				sourceSchemaClass = sourceSchemaField.getType();
				schemaField = (SchemaFieldReflection) sourceSchemaClass.getField(fieldNames[3]);
				subInvoke = true;
			}
		} else {
			invokeOn = current;
			schemaField = (SchemaFieldReflection) schemaObject.getField(fieldName);
		}

		if (invokeOn == null) {
			return null;
		}
		try {
			Method method;
			if (ReflectionHelper.isJRType(schemaField.getLanguageType())) {
				method = schemaField.getGetterMethod();
			} else if (!subInvoke) {
				method = Object.class.getMethod("toString", new Class[0]);
			} else {
				method = schemaField.getGetterMethod();
				if (ReportingFeatureHelper.isVisibleField(schemaField)) {
					invokeOn = method.invoke(invokeOn, new Object[0]);
				} else {
					return null;
				}
				method = Object.class.getMethod("toString", new Class[0]);
			}

			if (invokeOn == null) {
				return null;
			}
			if (ReportingFeatureHelper.isVisibleField(schemaField)) {

				return method.invoke(invokeOn, new Object[0]);
			} else {
				return null;
			}
		}
		catch (final Exception e) {
			throw new JRException("Error druring " + iField.getName() + " rendering", e);
		}
	}

	public void moveFirst() throws JRException {
		iterator = list.iterator();
	}

	public boolean next() throws JRException {
		try {
			current = iterator.next();
			return true;
		}
		catch (final NoSuchElementException e) {
			return false;
		}
	}
}
