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
package org.romaframework.aspect.reporting.jr.domain;

public class MapEntry {

	private Object	key;
	private Object	value;

	public MapEntry(java.util.Map.Entry<?, ?> iEntry) {
		key = iEntry.getKey();
		value = iEntry.getValue();
	}

	public String getKey() {
		if (key != null) {
			return key.toString();
		} else {
			return null;
		}
	}

	public String getValue() {
		if (value != null) {
			return value.toString();
		} else {
			return null;
		}
	}

}
