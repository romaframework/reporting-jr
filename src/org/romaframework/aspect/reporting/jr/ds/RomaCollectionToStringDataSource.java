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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;

public class RomaCollectionToStringDataSource implements JRRewindableDataSource {
	public final static String	FIELD_NAME	= "toStringField";

	Object											current;

	Iterator<Object>						iterator;

	List<Object>								list				= new LinkedList<Object>();

	public RomaCollectionToStringDataSource(Collection<?> iList) {
		super();
		if (iList != null) {
			list.addAll(iList);
		}
		iterator = list.iterator();
	}

	public Object getFieldValue(JRField arg0) throws JRException {
		if (arg0.getName().equals(FIELD_NAME)) {
			if (current != null) {
				return current.toString();
			} else {
				return "";
			}
		} else {
			throw new JRException("Field " + arg0.getName() + " not defined in dataSource");
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
