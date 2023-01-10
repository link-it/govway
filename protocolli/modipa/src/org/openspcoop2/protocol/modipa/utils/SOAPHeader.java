/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

package org.openspcoop2.protocol.modipa.utils;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * SOAPHeader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SOAPHeader {

	private String localName;
	private String namespace;
	
	public String getLocalName() {
		return this.localName;
	}
	public String getNamespace() {
		return this.namespace;
	}
	
	public SOAPHeader(String namespace, String localName) {
		this.namespace = namespace;
		this.localName = localName;
	}
	
	public static void remove(List<SOAPHeader> list, String namespace, String localName) {
		if(list!=null && !list.isEmpty()) {
			boolean find = true;
			while (find) {
				find = false;
				int i=0;
				for (; i < list.size(); i++) {
					SOAPHeader hdr = list.get(i);
					if(hdr.getLocalName().equals(localName) && hdr.getNamespace().equals(namespace)) {
						find = true;
						break;
					}
				}
				if(find) {
					list.remove(i);
				}
			}
		}
	}
	
	public static List<SOAPHeader> parse(String value) throws ProtocolException{
		List<SOAPHeader> list = new ArrayList<SOAPHeader>();
		if(value!=null && value.length()>0) {
			try(
					StringReader sr = new StringReader(value);
					BufferedReader reader = new BufferedReader(sr);){
				String line = reader.readLine();
				while (line != null) {
					try {
						line = line.trim();
						if(line.startsWith("#")) {
							continue;
						}
						if(!line.startsWith("{")){
							throw new Exception("Wrong format (expected '{namespace}localName') in row '"+line+"'");
						}
						if(!line.contains("}")){
							throw new Exception("Wrong format (expected '{namespace}localName') in row '"+line+"'");
						}
						StringBuilder sbNamespace= new StringBuilder();
						StringBuilder sbLocalname= new StringBuilder();
						boolean localName = false;
						for (int i = 1; i < line.length(); i++) {
							char c = line.charAt(i);
							if(c == '}') {
								localName = true;
								continue;
							}
							if(localName) {
								sbLocalname.append(c);
							}
							else {
								sbNamespace.append(c);
							}
						}
						if(sbNamespace.length()<=0) {
							throw new Exception("Wrong format (expected '{namespace}localName') in row '"+line+"' (namespace not found)");
						}
						if(sbNamespace.toString().contains("{") || sbNamespace.toString().contains("}")) {
							throw new Exception("Wrong format (expected '{namespace}localName') in row '"+line+"'");
						}
						if(sbLocalname.length()<=0) {
							throw new Exception("Wrong format (expected '{namespace}localName') in row '"+line+"' (localName not found)");
						}
						if(sbLocalname.toString().contains("{") || sbLocalname.toString().contains("}")) {
							throw new Exception("Wrong format (expected '{namespace}localName') in row '"+line+"'");
						}
						SOAPHeader hdr = new SOAPHeader(sbNamespace.toString(), sbLocalname.toString());
						list.add(hdr);
						
					}finally {
						line = reader.readLine();
					}
				}
			}catch(Exception e) {
				throw new ProtocolException(e.getMessage(), e);
			}
		}
		return list;
	}
}
