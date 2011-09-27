/*
 * Copyright 2006 Giordano Maestro (giordano.maestro--at--assetdata.it)
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
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;

public class RomaGridDataSource implements JRRewindableDataSource {
	private static String	mock	= "";
	private List<List<?>>	toFetch;
	int										row		= -1;
	private List<?>				firstColumn;

	public RomaGridDataSource(Collection<Collection<?>> collection) {
		toFetch = toList(collection);

	}

	public void moveFirst() throws JRException {
		row = 0;
	}

	public Object getFieldValue(JRField arg0) throws JRException {
		String name = arg0.getName();
		int i = Integer.parseInt(name);
		Object current = toFetch.get(i).get(row);
		if (current != null) {
			return current.toString();
		} else {
			return "";
		}
	}

	public boolean next() throws JRException {
		row++;
		try {
			firstColumn = toFetch.iterator().next();
			firstColumn.get(row);
			return true;
		}
		catch (IndexOutOfBoundsException e) {
			return false;
		}
		catch (NoSuchElementException e1) {
			return false;
		}
	}

	private List<List<?>> toList(Collection<Collection<?>> toPivot) {
		List<List<?>> result = new LinkedList<List<?>>();

		int maxSize = 0;
		for (Collection<?> pivot : toPivot) {
			if (pivot.size() > maxSize) {
				maxSize = pivot.size();
			}
		}

		for (Collection<?> pivot : toPivot) {
			LinkedList<Object> list = new LinkedList<Object>();
			int pivotSize = pivot.size();
			int toAdd = maxSize - pivotSize;
			list.addAll(pivot);
			for (int i = 0; i < toAdd; i++) {
				list.add(mock);
			}
			result.add(list);
		}
		return result;
	}
}
