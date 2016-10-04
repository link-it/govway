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

package org.openspcoop2.web.ctrlstat.plugins;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.core.constants.CostantiConnettori;

/**     
 * ExtendedConnettoreConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExtendedConnettoreConverter {

	public static void readExtendedInfoFromConnettore(List<ExtendedConnettore> list,org.openspcoop2.core.config.Connettore connettore) throws ExtendedException{
		Properties properties = new Properties();
		if(connettore!=null){
			for (org.openspcoop2.core.config.Property property : connettore.getPropertyList()) {
				if(property.getNome().startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)){
					properties.put(property.getNome().substring(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX.length()), property.getValore());
				}
			}
		}
		if(properties.size()>0){
			readExtendedInfoFromConnettore(list,properties);
		}
	}
	public static void fillExtendedInfoIntoConnettore(List<ExtendedConnettore> list, org.openspcoop2.core.config.Connettore connettore) throws ExtendedException{
		if(list!=null && list.size()>0){
			
			List<String> pDaEliminare = new ArrayList<String>();
			for (org.openspcoop2.core.config.Property property : connettore.getPropertyList()) {
				if(property.getNome().startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)){
					pDaEliminare.add(property.getNome());
				}
			}
			while (pDaEliminare.size()>0) {
				String pNameDaEliminare = pDaEliminare.remove(0);
				int i = 0;
				boolean found = false;
				for (; i < connettore.sizePropertyList(); i++) {
					if(connettore.getProperty(i).getNome().equals(pNameDaEliminare)){
						found = true;
						break;
					}
				}
				if(found){
					connettore.removeProperty(i);	
				}
			}
			
			Properties properties = toProperties(list);
			Enumeration<?> keys = properties.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				String value = properties.getProperty(key);
				
				if((CostantiConnettori.CONNETTORE_EXTENDED_PREFIX+key).length()>255){
					throw new ExtendedException("Property ["+key+"] troppo lunga (max-length:"+(255-CostantiConnettori.CONNETTORE_EXTENDED_PREFIX.length())+")");
				}
				
				org.openspcoop2.core.config.Property property = new org.openspcoop2.core.config.Property();
				property.setNome(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX+key);
				property.setValore(value);
				connettore.addProperty(property);
			}
		}
		
	}
	
	public static void readExtendedInfoFromConnettore(List<ExtendedConnettore> list,org.openspcoop2.core.registry.Connettore connettore) throws ExtendedException{
		Properties properties = new Properties();
		if(connettore!=null){
			for (org.openspcoop2.core.registry.Property property : connettore.getPropertyList()) {
				if(property.getNome().startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)){
					properties.put(property.getNome().substring(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX.length()), property.getValore());
				}
			}
		}
		if(properties.size()>0){
			readExtendedInfoFromConnettore(list,properties);
		}
	}
	
	public static void fillExtendedInfoIntoConnettore(List<ExtendedConnettore> list, org.openspcoop2.core.registry.Connettore connettore) throws ExtendedException{
		if(list!=null && list.size()>0){
			
			List<String> pDaEliminare = new ArrayList<String>();
			for (org.openspcoop2.core.registry.Property property : connettore.getPropertyList()) {
				if(property.getNome().startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)){
					pDaEliminare.add(property.getNome());
				}
			}
			while (pDaEliminare.size()>0) {
				String pNameDaEliminare = pDaEliminare.remove(0);
				int i = 0;
				boolean found = false;
				for (; i < connettore.sizePropertyList(); i++) {
					if(connettore.getProperty(i).getNome().equals(pNameDaEliminare)){
						found = true;
						break;
					}
				}
				if(found){
					connettore.removeProperty(i);	
				}
			}
			
			Properties properties = toProperties(list);
			Enumeration<?> keys = properties.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				String value = properties.getProperty(key);
				
				if((CostantiConnettori.CONNETTORE_EXTENDED_PREFIX+key).length()>255){
					throw new ExtendedException("Property ["+key+"] troppo lunga (max-length:"+(255-CostantiConnettori.CONNETTORE_EXTENDED_PREFIX.length())+")");
				}
				
				org.openspcoop2.core.registry.Property property = new org.openspcoop2.core.registry.Property();
				property.setNome(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX+key);
				property.setValore(value);
				connettore.addProperty(property);
			}
		}
	}
	
	
	// Commons
	
	public static String buildId(String idExtended,String idExtendedItem){
		return idExtended+"_"+idExtendedItem;
	}
	
	private static void readExtendedInfoFromConnettore(List<ExtendedConnettore> list,Properties properties) throws ExtendedException{
		
		try{
			
			if(list!=null){
				for (ExtendedConnettore extendedConnettore : list) {
				
					if(properties.containsKey(extendedConnettore.getId())){
						
						String v = properties.getProperty(extendedConnettore.getId());
						extendedConnettore.setEnabled("true".equals(v));
						
						if(extendedConnettore.isEnabled()){
							
							if(extendedConnettore.getListItem()!=null && extendedConnettore.getListItem().size()>0){
								for (ExtendedConnettoreItem extendedConnettoreItem : extendedConnettore.getListItem()) {
									
									String id = buildId(extendedConnettore.getId(),extendedConnettoreItem.getId());
									if(properties.containsKey(id)){
										
										String vProperty = properties.getProperty(id);
										extendedConnettoreItem.setValue(vProperty);
										
									}
									
								}
							}
							
						}
						
					}
					
				}
			}		
			
		}catch(Exception e){
			throw new ExtendedException(e.getMessage(),e);
		}
	}
	
	private static Properties toProperties(List<ExtendedConnettore> list) throws ExtendedException{
	
		try{
		
			Properties properties = new Properties();
			
			if(list!=null){
				for (ExtendedConnettore extendedConnettore : list) {
					
					properties.put(extendedConnettore.getId(), extendedConnettore.isEnabled()+"");
					
					if(extendedConnettore.isEnabled()){
						
						if(extendedConnettore.getListItem()!=null && extendedConnettore.getListItem().size()>0){
							for (ExtendedConnettoreItem extendedConnettoreItem : extendedConnettore.getListItem()) {
								
								String v = extendedConnettoreItem.getValue();
								if(v!=null && !"".equals(v)){ 
									// altrimenti la stringa vuota equivale ad non avere un valore per l'item
									properties.put(buildId(extendedConnettore.getId(),extendedConnettoreItem.getId()),
											extendedConnettoreItem.getValue());
								}
								
							}
						}
						
					}
				}
			}
			
			return properties;
		
		}catch(Exception e){
			throw new ExtendedException(e.getMessage(),e);
		}
	}
	
}
