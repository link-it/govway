/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.core.transazioni.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.transport.TransportUtils;

/**     
 * PropertiesSerializator
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PropertiesSerializator {

	
	public static final String TEMPLATE_RITORNO_A_CAPO = "#@$#@$#@$#";
	
	protected Map<String, List<String>> properties;

	public Map<String, List<String>> getProperties() {
		return this.properties;
	}
	
	public PropertiesSerializator(Map<String, List<String>> properties){
		this.properties = properties;
	}
	
	public String convertToDBColumnValue() throws Exception{
		
		StringBuilder bf = new StringBuilder();
		
		if(this.properties!=null && !this.properties.isEmpty()) {
			for (String key : this.properties.keySet()) {
				if(key.contains(" ")){
					throw new Exception("Chiave ["+key+"] contiene il carattere ' ' non ammesso");
				}
				List<String> values = this.properties.get(key);
				if(values!=null && !values.isEmpty()) {
					for (String value : values) {
						//if(value.contains("\n")){
							//throw new Exception("Valore ["+value+"] della chiave ["+key+"] contiene il carattere '\\n' non ammesso");
						//}
						while(value.contains("\n")){
							value = value.replace("\n", TEMPLATE_RITORNO_A_CAPO);
						}
						if(bf.length()>0){
							bf.append("\n");
						}
						bf.append(key).append("=").append(value);		
					}
				}
			}
		}
		
		return bf.toString();
		
	}
	
	public static Map<String, List<String>> convertoFromDBColumnValue(String dbValue) throws Exception{
		
		Map<String, List<String>> map = new HashMap<>();
		
		if(dbValue!=null && !"".equals(dbValue)){
			
			String [] split = dbValue.split("\n");
			for (int i = 0; i < split.length; i++) {
				String keyValueTmp = split[i].trim();
				String [] keyValue = keyValueTmp.split("=");
				if(keyValue.length==1 && keyValueTmp.endsWith("=")) {
					String key = keyValue[0];
					String value = "";
					TransportUtils.addHeader(map, key, value);
					continue;
				}
				if(keyValue.length<2){
					throw new Exception("Valore ["+keyValueTmp+"] non contiene una coppia key=value");
				}
				String key = keyValue[0];
				String value = keyValueTmp.substring((key+"=").length());
				while(value.contains(TEMPLATE_RITORNO_A_CAPO)){
					value = value.replace(TEMPLATE_RITORNO_A_CAPO, "\n");
				}
				TransportUtils.addHeader(map, key, value);
			}
			
		}
		
		return map;
		
	}
}
