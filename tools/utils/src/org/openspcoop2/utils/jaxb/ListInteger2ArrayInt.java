/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
			list.add(new Integer(i[j]));
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

