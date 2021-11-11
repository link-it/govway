/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * Properties2Map
 *
 *
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Properties2Map extends XmlAdapter<Properties, Map<String, String>>
{
	@Override
	public Properties marshal(Map<String, String> hash) throws Exception {
		if(hash==null){
			return null;
		}
		Properties prop = new Properties();
		Iterator<Entry<String,String>> i = hash.entrySet().iterator();
		while(i.hasNext()){
			Entry<String,String> e = i.next();
			prop.getEntry().add(new Properties.Entry(e.getKey(),e.getValue()));
		}
		return prop;
	}
	@Override
	public Map<String, String> unmarshal(Properties prop) throws Exception {
		if(prop==null){
			return null;
		}
		Map<String, String> hash = new HashMap<String, String>();
		Iterator<Properties.Entry> i = prop.getEntry().iterator();
		while(i.hasNext()){
			Properties.Entry e = i.next();
			hash.put(e.getKey(), e.getValue());
		}
		return hash;
	}
}

