/*
 * OpenSPCoop - Customizable API Gateway 
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

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * ListLong2ArrayLong
 *
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ListLong2ArrayLong extends XmlAdapter<ArrayList<Long>, long[]>
{
	@Override
	public ArrayList<Long> marshal(long[] array) throws Exception {
		if(array==null || array.length<=0){
			return null;
		}
		ArrayList<Long> list = new ArrayList<Long>();
		for(int i=0; i<array.length; i++)
			list.add(new Long(array[i]));
		return list;
	}
	@Override
	public long[] unmarshal(ArrayList<Long> list) throws Exception {
		if(list==null || list.size()<=0){
			return null;
		}
		long[] array =  new long[list.size()];
		for(int i=0; i<list.size(); i++){
			array[i] = list.get(i).longValue();
		}
		return array;
	}
}

