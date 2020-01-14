/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.utils.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * ListInteger2ArrayInt
 *
 *
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ListInteger2ArrayInt extends XmlAdapter<List<Integer>, int[]>
{
	@Override
	public List<Integer> marshal(int[] i) throws Exception {
		if(i==null || i.length<=0){
			return null;
		}
		List<Integer> list = new ArrayList<Integer>();
		for(int j=0; j<i.length; j++)
			list.add(i[j]);
		return list;
	}
	@Override
	public int[] unmarshal(List<Integer> list) throws Exception {
		if(list==null || list.size()<=0){
			return null;
		}
		int[] array = new int[list.size()];
		for(int i=0; i<list.size(); i++){
			try {
				array[i] = list.get(i).intValue();
			} catch (NullPointerException p) {
				array[i] = 0;
			}
		}
		return array;
	}
}

