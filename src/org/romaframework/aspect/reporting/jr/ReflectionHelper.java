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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.romaframework.core.exception.ConfigurationException;
import org.romaframework.core.schema.SchemaClass;
import org.romaframework.core.schema.SchemaField;

public class ReflectionHelper {

	public static Object getFieldValue(SchemaField iField, Object iInstance) {
		return iField.getValue(iInstance);	
	}

	/**
	 * Return true if the clazz is a collection
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isCollection(Class<?> clazz) {
		if (Collection.class.isAssignableFrom(clazz)) {
			return true;
		}
		return false;
	}

	/**
	 * Return true if the clazz is a collection, a map or a set
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isCollectionMapSet(Class<?> clazz) {
		if (Collection.class.isAssignableFrom(clazz)) {
			return true;
		}
		if (Map.class.isAssignableFrom(clazz)) {
			return true;
		}
		if (Set.class.isAssignableFrom(clazz)) {
			return true;
		}
		return false;
	}

	/**
	 * Return true if the clazz is a collection
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isMap(Class<?> clazz) {
		if (Map.class.isAssignableFrom(clazz)) {
			return true;
		}
		return false;
	}

	/**
	 * Return true if the clazz is a primitive type
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isJRType(Class<?> clazz) {
		// DO NOT REPLACE WITH Utility.isPrimitive()
		if (Double.class.isAssignableFrom(clazz) ) {
			return true;
		}
		if (String.class.isAssignableFrom(clazz) ) {
			return true;
		}
		if (Float.class.isAssignableFrom(clazz) ) {
			return true;
		}
		if (Long.class.isAssignableFrom(clazz)) {
			return true;
		}
		if (Short.class.isAssignableFrom(clazz) ) {
			return true;
		}
		if (Integer.class.isAssignableFrom(clazz) ) {
			return true;
		}
		return false;
	}

	

	public static boolean isCollectionOfCollection(SchemaField ischemaFieldToRender) {
		 SchemaClass embType = ischemaFieldToRender.getEmbeddedType();
		if (embType == null) {
			throw new ConfigurationException("Cannot find embedded type definition for the field "
					+ ischemaFieldToRender.getEntity().getSchemaClass().getName() + "." + ischemaFieldToRender.getName());
		}
		
		if (embType.isAssignableAs(Collection.class)) {
			return true;
		}	

		return false;
	}

}
