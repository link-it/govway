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


package org.openspcoop2.utils.resources;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;



/**
* InstanceProperties
*
* @author Andrea Poli <apoli@link.it>
* @author $Author$
* @version $Rev$, $Date$
*/

public abstract class InstanceProperties {

	private PropertiesReader propertiesOriginale;
	private CollectionProperties propertiesRidefinitoFile;
	private PropertiesReader propertiesRidefinitoObject;
	protected Logger log;
	private String OPENSPCOOP2_LOCAL_HOME;
	private boolean readCallsNotSynchronized;
	
	public InstanceProperties(String OPENSPCOOP2_LOCAL_HOME,Properties propertiesOriginale,Logger log) throws Exception{
		this(OPENSPCOOP2_LOCAL_HOME, propertiesOriginale, log, true);
	}
	public InstanceProperties(String OPENSPCOOP2_LOCAL_HOME,Properties propertiesOriginale,Logger log, boolean readCallsNotSynchronized) throws Exception{
		this.propertiesOriginale = new PropertiesReader(propertiesOriginale,readCallsNotSynchronized);
		this.log = log;
		if(this.log==null){
			this.log = LoggerWrapperFactory.getLogger(InstanceProperties.class);
		}
		this.OPENSPCOOP2_LOCAL_HOME = OPENSPCOOP2_LOCAL_HOME;
		this.readCallsNotSynchronized = readCallsNotSynchronized;
	}
	
	public void setLocalFileImplementation(String variable,String path,String confDirectory){
		CollectionProperties prop = PropertiesUtilities.searchLocalImplementation(this.OPENSPCOOP2_LOCAL_HOME,this.log, variable, path, confDirectory, this.readCallsNotSynchronized);
		if(prop!=null)
			this.propertiesRidefinitoFile = prop;
	}
	
	public void setLocalFileImplementation(CollectionProperties prop){
		this.propertiesRidefinitoFile = prop;
	}
	
	public void setLocalObjectImplementation(Properties prop){
		this.propertiesRidefinitoObject = new PropertiesReader(prop,this.readCallsNotSynchronized);
	}
	
	// RIDEFINIZIONE METODI PROPERTIES READER
	
	public String getValue(String key) throws UtilsException{
		return getValueEngine(key, false);
	}
		
	public String getValue_convertEnvProperties(String key)throws UtilsException{
		return getValueEngine(key, true);
	}
		
	public java.util.Properties readProperties (String prefix)throws UtilsException{
		return readPropertiesEngine(prefix, false);
	}
	
	public java.util.Properties readProperties_convertEnvProperties (String prefix)throws UtilsException{
		return readPropertiesEngine(prefix, true);
	}
	
	public String convertEnvProperties(String value)throws UtilsException{
		return this.propertiesOriginale.convertEnvProperties(value);
	}
	
	public java.util.Enumeration<?> propertyNames(){
		
		java.util.Enumeration<?> enumProp = this.propertiesOriginale.propertyNames();
		Vector<String> object = new Vector<String>();
		while(enumProp.hasMoreElements()){
			object.add((String)enumProp.nextElement());		
		}
		
		if(this.propertiesRidefinitoFile!=null){
			java.util.Enumeration<?> enumPropRidefinito = this.propertiesRidefinitoFile.propertyNames();
			while(enumPropRidefinito!=null && enumPropRidefinito.hasMoreElements()){
				String ridefinito = (String)enumPropRidefinito.nextElement();
				if(object.contains(ridefinito)==false){
					object.add(ridefinito);		
				}
			}
		}
		
		if(this.propertiesRidefinitoObject!=null){
			java.util.Enumeration<?> enumPropRidefinito = this.propertiesRidefinitoObject.propertyNames();
			while(enumPropRidefinito!=null && enumPropRidefinito.hasMoreElements()){
				String ridefinito = (String)enumPropRidefinito.nextElement();
				if(object.contains(ridefinito)==false){
					object.add(ridefinito);		
				}
			}
		}
		
		return object.elements();
	}


	
	
	
	
	/* ------------ UTILITY INTERNE ----------------- */
	private String getValueEngine(String key,boolean convertEnvProperties) throws UtilsException{
		
		// NOTA: La logica di append "+," vale solo rispetto agli elementi PropertiesOriginale, CollectionProperties, PropertiesObject
		//		 Mentre il valore indicato per una proprietà dei files locali all'interno del CollectionProperties non va in append, ma viene utilizzato solamente il primo incontrato (che poi sarà utilizzato per come valore del 2. File).
				
		try{
					
			// 1. Object
			String [] addObject = null;
			boolean appendObject = false;
			String tmpObject = null;
			if(this.propertiesRidefinitoObject!=null){
				if(convertEnvProperties){
					tmpObject = this.propertiesRidefinitoObject.getValue_convertEnvProperties(key);
				}else{
					tmpObject = this.propertiesRidefinitoObject.getValue(key);
				}
			}
			if(tmpObject!=null){
				tmpObject = tmpObject.trim();
				String add = null;
				if(tmpObject.startsWith("+")){
					appendObject = true;
					add = tmpObject.substring(1);
					add = add.trim();
				}
				if(add!=null){
					if(add.startsWith(",") && add.length()>1){
						addObject = add.substring(1).split(",");
						if(addObject!=null && addObject.length>0){
							for (int i = 0; i < addObject.length; i++) {
								addObject[i] = addObject[i].trim();
							}
						}else{
							//return tmpObject; // valore strano, lo ritorno
							addObject = new String[1];
							addObject[0] = tmpObject;
						}
					}
				}else{
					//return tmpObject;
					addObject = new String[1];
					addObject[0] = tmpObject;
				}
			}
			
			
			// 2. File
			String [] addFile = null;
			boolean appendFile = false;
			String tmpFile = null;
			if(this.propertiesRidefinitoFile!=null){
				if(convertEnvProperties){
					tmpFile = this.propertiesRidefinitoFile.getValue_convertEnvProperties(key);
				}else{
					tmpFile = this.propertiesRidefinitoFile.getValue(key);
				}
			}
			if(tmpFile!=null){
				tmpFile = tmpFile.trim();
				String add = null;
				if(tmpFile.startsWith("+")){
					appendFile = true;
					add = tmpFile.substring(1);
					add = add.trim();
				}
				if(add!=null){
					if(add.startsWith(",") && add.length()>1){
						addFile = add.substring(1).split(",");
						if(addFile!=null && addFile.length>0){
							for (int i = 0; i < addFile.length; i++) {
								addFile[i] = addFile[i].trim();
							}
						}else{
							//return tmpFile; // valore strano, lo ritorno
							addFile = new String[1];
							addFile[0] = tmpFile;
						}
					}
				}else{
					//return tmpFile;
					addFile = new String[1];
					addFile[0] = tmpFile;
				}
			}
			
			
			// 3. Originale
			String tmp = null;
			if(tmp==null){
				if(convertEnvProperties){
					tmp = this.propertiesOriginale.getValue_convertEnvProperties(key);
				}else{
					tmp = this.propertiesOriginale.getValue(key);
				}
			}
			
			
			// 4. Gestione valore ritornato
			if(addObject==null && addFile==null){
				return tmp;
			}
			if(!appendObject && !appendFile){
				// Tecnica append non utilizzato
				// Ritorno il primo valore disponibile rispetto all'ordine
				if(addObject!=null){
					return addObject[0];
				}
				else if(addFile!=null){
					return addFile[0];
				}
				else{
					return tmp;
				}
			}
			
			
			// 5. Se sono state fornite proprieta' con il + le gestisco.
			// In caso di valori uguali utilizzo quelli dell'object, poi quelli del file e infine quelli dell'originale
			StringBuffer bf = new StringBuffer();
			List<String> valoriAggiunti = new ArrayList<String>();
			if(addObject!=null){
				for (int i = 0; i < addObject.length; i++) {
					if(valoriAggiunti.contains(addObject[i])==false){
						if(bf.length()>0){
							bf.append(",");
						}
						bf.append(addObject[i]);
						valoriAggiunti.add(addObject[i]);
					}
				}
			}
			if(addFile!=null){
				for (int i = 0; i < addFile.length; i++) {
					if(valoriAggiunti.contains(addFile[i])==false){
						if(bf.length()>0){
							bf.append(",");
						}
						bf.append(addFile[i]);
						valoriAggiunti.add(addFile[i]);
					}
				}
			}
			if(tmp!=null){
				if(valoriAggiunti.contains(tmp)==false){
					if(bf.length()>0){
						bf.append(",");
					}
					bf.append(tmp);
					valoriAggiunti.add(tmp);
				}
			}
			
			return bf.toString();
			
		}catch(Exception e){
			this.log.error("Errore durante la lettura della proprieta' ["+key+"]("+convertEnvProperties+")",e);
			throw new UtilsException(e.getMessage(),e);
		}
		
	}
	
	private java.util.Properties readPropertiesEngine(String prefix,boolean convertEnvProperties)throws UtilsException{
		
		java.util.Properties tmp = null;
		try{
		
			if(convertEnvProperties){
				tmp = this.propertiesOriginale.readProperties_convertEnvProperties(prefix);
			}else{
				tmp = this.propertiesOriginale.readProperties(prefix);
			}
			
			if(this.propertiesRidefinitoFile!=null){
				java.util.Properties tmp2 = null;
				if(convertEnvProperties){
					tmp2 = this.propertiesRidefinitoFile.readProperties_convertEnvProperties(prefix);
				}else{
					tmp2 = this.propertiesRidefinitoFile.readProperties(prefix);
				}
				if(tmp2!=null){
					Enumeration<?> keys = tmp2.keys();
					while (keys.hasMoreElements()) {
						String key = (String) keys.nextElement();
						if(tmp.containsKey(key)){
							tmp.remove(key);
						}
						tmp.put(key, tmp2.get(key));
					}
				}
			}
			
			if(this.propertiesRidefinitoObject!=null){
				java.util.Properties tmp3 = null;
				if(convertEnvProperties){
					tmp3 = this.propertiesRidefinitoObject.readProperties_convertEnvProperties(prefix);
				}else{
					tmp3 = this.propertiesRidefinitoObject.readProperties(prefix);
				}
				if(tmp3!=null){
					Enumeration<?> keys = tmp3.keys();
					while (keys.hasMoreElements()) {
						String key = (String) keys.nextElement();
						if(tmp.containsKey(key)){
							tmp.remove(key);
						}
						tmp.put(key, tmp3.get(key));
					}
				}
			}
			
		}catch(Exception e){
			this.log.error("Errore durante la lettura delle proprieta' con prefix ["+prefix+"]("+convertEnvProperties+")",e);
			throw new UtilsException(e.getMessage(),e);
		}
		
		return tmp;
	}
}
