/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.web.lib.mvc.properties.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.core.mvc.properties.Property;
import org.openspcoop2.web.lib.mvc.properties.utils.ConditionsEngine;

/***
 * 
 * ConfigBean memorizza le informazioni relative ai bean della configurazione
 * 
 * @author pintori
 *
 */
public class ConfigBean {

	private List<String> listaNomiProperties= null; 
	private List<String> listaKeysItem= null;
	private Map<String, BaseItemBean<?>> mapItem = null;
	private Map<String, List<BaseItemBean<?>>> mapPropertyItem = null;

	public ConfigBean() {
		this.listaNomiProperties = new ArrayList<String>();
		this.listaKeysItem = new ArrayList<String>();
		this.mapItem = new HashMap<String, BaseItemBean<?>>();
		this.mapPropertyItem = new HashMap<String, List<BaseItemBean<?>>>();
	}

	public void clear() {
		this.listaNomiProperties.clear();
		this.listaKeysItem.clear();
		this.mapItem.clear();
		this.mapPropertyItem.clear();
	}

	public void addItem(BaseItemBean<?> item) throws Exception{
		if(this.mapItem.containsKey(item.getName()))
			throw new Exception("Item ["+item.getName()+"] viola il vincolo di univocita' degli elementi: rinominare uno dei due item.");

		this.listaKeysItem.add(item.getName());
		this.mapItem.put(item.getName(), item);

		// solo per gli item, aggrego per property destionazione
		if(item.getSaveProperty() != null) {
			List<BaseItemBean<?>> lstItems = null;
			if(this.mapPropertyItem.containsKey(item.getSaveProperty().getName())) {
				lstItems = this.mapPropertyItem.remove(item.getSaveProperty().getName());
			} else {
				lstItems = new ArrayList<>();
			}
			lstItems.add(item);
			this.mapPropertyItem.put(item.getSaveProperty().getName(), lstItems);
		}
	}

	public BaseItemBean<?> getItem(String name){
		return this.mapItem.get(name);
	}

	public List<String> getListakeys(){
		return this.listaKeysItem;
	}

	public List<BaseItemBean<?>> getListaItem(){
		List<BaseItemBean<?>> lista = new ArrayList<>();
		for (String key : this.listaKeysItem) {
			lista.add(this.mapItem.get(key));
		}

		return lista;
	}

	public Map<String, Properties> getPropertiesMap (){
		Map<String, Properties> map = new HashMap<String, Properties>();

		List<BaseItemBean<?>> listaItem = this.getListaItem();

		for (BaseItemBean<?> item : listaItem) { // Scorro la lista degli elementi
			Property saveProperty = item.getSaveProperty();
			String itemValue = item.getPropertyValue(); // valore della property


			System.out.println("SAVE -> Item: Name ["+item.getName()+"] Value ["+itemValue+"]");  
			if(saveProperty != null && itemValue != null) { // per ogni elemento salvabile
				if(saveProperty.isForce() || (!"".equals(itemValue))) {

					String propertyName = saveProperty.getName(); // nome della property di destinazione
					String propertiesName = saveProperty.getProperties() != null ? saveProperty.getProperties() : Costanti.NOME_MAPPA_PROPERTIES_DEFAULT; // nome delle properties di destinazione (vuoto quelle di default)

					System.out.println("SAVE -> Item: propertyName ["+propertyName+"] propertiesName ["+propertiesName+"]");  				
					Properties p = null; // controllo esistenza properties selezionate
					if(map.containsKey(propertiesName)) {
						p = map.remove(propertiesName);
					} else {
						p = new Properties();
					}
					map.put(propertiesName, p);

					if(!saveProperty.isAppend()) { // se la property non e' di tipo append allora setto il valore 
						p.setProperty(propertyName, itemValue);
					} else {
						String appendPropertyKey = Costanti.PRE_KEY_PROPERTIES_DEFAULT + propertyName;
						String appendKeyPropertyValue = null;

						// genero la chiave per decodificare le properties concatenate
						if(p.containsKey(appendPropertyKey)) { 
							appendKeyPropertyValue = p.getProperty(appendPropertyKey);
							p.remove(appendPropertyKey);
							appendKeyPropertyValue += Costanti.KEY_PROPERTIES_DEFAULT_SEPARATOR;
							appendKeyPropertyValue += item.getName();
						} else {
							appendKeyPropertyValue = item.getName();
						}
						p.setProperty(appendPropertyKey, appendKeyPropertyValue);

						String apValue = null;
						if(p.containsKey(propertyName)) { // controllo se la property di tipo append e' gia presente aggiungo separatore e nuovo valore a quello gia' presente
							apValue = p.getProperty(propertyName);
							p.remove(propertyName);
							apValue += saveProperty.getAppendSeparator();
							apValue += itemValue;
						} else {
							apValue = itemValue;
						}
						p.setProperty(propertyName, apValue);
					}

				}

			}
		}

		return map;
	}
	
	public void setValueFromRequest(String name, String parameterValue) {
		this.getItem(name).setValueFromRequest(parameterValue);
	}

	public void updateConfigurazione() throws Exception {
		List<BaseItemBean<?>> listaItem = this.getListaItem();

		for (BaseItemBean<?> item : listaItem) {
			boolean resolve = ConditionsEngine.resolve(item.getConditions(), this);
			System.out.println("Item ["+item.getName()+"] Visibile ["+resolve+"]");
			item.setVisible(resolve);
		}
	}

	public List<String> getListaNomiProperties() {
		return this.listaNomiProperties;
	}

	public Map<String, List<BaseItemBean<?>>> getMapPropertyItem() {
		return this.mapPropertyItem;
	}
}
