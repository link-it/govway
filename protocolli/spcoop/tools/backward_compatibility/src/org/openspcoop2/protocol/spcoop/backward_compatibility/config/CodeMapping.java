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
package org.openspcoop2.protocol.spcoop.backward_compatibility.config;

import java.util.Enumeration;
import java.util.Properties;

import org.openspcoop2.pdd.config.OpenSPCoop2ConfigurationException;
import org.openspcoop2.utils.UtilsException;

/**
 * CodeMapping
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CodeMapping {

	private BackwardCompatibilityProperties props = null;
	private Properties mapping = null;
	
	private static CodeMapping codeMapping = null;
	private static synchronized void initialize() throws OpenSPCoop2ConfigurationException, UtilsException{
		if(codeMapping==null){
			codeMapping = new CodeMapping();
		}
	}
	public static CodeMapping getInstance() throws OpenSPCoop2ConfigurationException, UtilsException {
		if(codeMapping==null){
			initialize();
		}
		return codeMapping;
	}
	
	public CodeMapping() throws OpenSPCoop2ConfigurationException, UtilsException{
		this.props = BackwardCompatibilityProperties.getInstance();
		this.mapping = this.props.readCodeMapping();
	}
	
	public String toOpenSPCoopV1Code(String codice) {
		
		String value = this.mapping.getProperty(codice);
		if(value==null){
			
			// non e' presente un mapping puntuale, provo a vedere se corrisponde un mapping tramite espressione '*'
			Enumeration<?> keys = this.mapping.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				if(key.contains("*")){
					String[]split = key.split("\\*");
					boolean match = true;
					String valoreRimasto = codice;
					for (int i = 0; i < split.length; i++) {
						
						if(!codice.contains(split[i])){
							match = false;
							break;
						}
						else{
							valoreRimasto = valoreRimasto.replace(split[i], "");
						}
					}
					if(match){
						return this.mapping.getProperty(key).replace("*", valoreRimasto);
					}
				}
			}
			
			//throw new UtilsException("Mapping per il codice ["+codice+"] non trovato");
			return codice;
		}
		return value.trim();
		
	}

}
