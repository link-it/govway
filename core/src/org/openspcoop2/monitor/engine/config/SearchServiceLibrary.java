/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
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
package org.openspcoop2.monitor.engine.config;

import org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * SearchServiceLibrary
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SearchServiceLibrary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private BasicServiceLibrary basicServiceLibrary;
	private List<ConfigurazioneRicerca> searchActionLibrary;
	private List<ConfigurazioneRicerca> searchActionAllLibrary;

	public BasicServiceLibrary getBasicServiceLibrary() {
		return this.basicServiceLibrary;
	}
	public void setBasicServiceLibrary(BasicServiceLibrary basicServiceLibrary) {
		this.basicServiceLibrary = basicServiceLibrary;
	}
	public List<ConfigurazioneRicerca> getSearchActionLibrary() {
		return this.searchActionLibrary;
	}
	public void setSearchActionLibrary(List<ConfigurazioneRicerca> searchActionLibrary) {
		this.searchActionLibrary = searchActionLibrary;
	}
	public List<ConfigurazioneRicerca> getSearchActionAllLibrary() {
		return this.searchActionAllLibrary;
	}
	public void setSearchActionAllLibrary(
			List<ConfigurazioneRicerca> searchActionAllLibrary) {
		this.searchActionAllLibrary = searchActionAllLibrary;
	}





	// ****** UTILS *************** 

	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();

		if(this.basicServiceLibrary==null){
			bf.append("BasicServiceLibrary: notDefined");
			bf.append("\n");
		}
		else{
			bf.append(this.basicServiceLibrary.toString());
		}

			
		if(this.getSearchActionLibrary()!=null){
			for (int i = 0; i < this.getSearchActionLibrary().size(); i++) {
				bf.append("SearchActionLibrary["+i+"]: ");
				bf.append("id[").
				append(this.getSearchActionLibrary().get(i).getIdConfigurazioneRicerca()).
				append("] plugin.className[").
				append(this.getSearchActionLibrary().get(i).getPlugin().getClassName()).
				append("] plugin.description[").
				append(this.getSearchActionLibrary().get(i).getPlugin().getDescrizione()).
				append("] plugin.label[").
				append(this.getSearchActionLibrary().get(i).getPlugin().getLabel()).
				append("] label[").
				append(this.getSearchActionLibrary().get(i).getLabel()).
				append("] enabled[").
				append(this.getSearchActionLibrary().get(i).isEnabled()).
				append("]");
				bf.append("\n");
			}
		}else{
			bf.append("SearchActionLibrary: ");
			bf.append("-");
		}
		bf.append("\n");

		
		if(this.getSearchActionAllLibrary()!=null){
			for (int i = 0; i < this.getSearchActionAllLibrary().size(); i++) {
				bf.append("SearchActionAllLibrary["+i+"]: ");
				bf.append("id[").
				append(this.getSearchActionAllLibrary().get(i).getIdConfigurazioneRicerca()).
				append("] plugin.className[").
				append(this.getSearchActionAllLibrary().get(i).getPlugin().getClassName()).
				append("] plugin.description[").
				append(this.getSearchActionAllLibrary().get(i).getPlugin().getDescrizione()).
				append("] plugin.label[").
				append(this.getSearchActionAllLibrary().get(i).getPlugin().getLabel()).
				append("] label[").
				append(this.getSearchActionAllLibrary().get(i).getLabel()).
				append("] enabled[").
				append(this.getSearchActionAllLibrary().get(i).isEnabled()).
				append("]");
				bf.append("\n");
			}
		}else{
			bf.append("SearchActionAllLibrary: ");
			bf.append("-");
		}
		bf.append("\n");


		return bf.toString();
	}


	public List<ConfigurazioneRicerca> mergeServiceActionSearchLibrary(boolean onlyEnabled, boolean orderByLabel) throws Exception{

		Hashtable<String,ConfigurazioneRicerca> plugins = new Hashtable<String,ConfigurazioneRicerca>();

		// Leggo le risorse associate all'azione specifica
		if(this.searchActionLibrary!=null && this.searchActionLibrary.size()>0){
			for (ConfigurazioneRicerca configurazioneRicerca : this.searchActionLibrary) {
				if(!onlyEnabled || configurazioneRicerca.isEnabled()){
					plugins.put(configurazioneRicerca.getPlugin().getClassName(), configurazioneRicerca);
				}
			}
		}

		// Leggo le risorse associate all'azione '*'
		if(this.searchActionAllLibrary!=null && this.searchActionAllLibrary.size()>0){
			for (ConfigurazioneRicerca configurazioneRicerca : this.searchActionAllLibrary) {

				String idRisorsa = configurazioneRicerca.getPlugin().getClassName();
				if(plugins.containsKey(idRisorsa)==false){

					// inserisco solo le risorse il cui idPlugin non siano già stati inseriti per l'azione specifica

					if(!onlyEnabled || configurazioneRicerca.isEnabled()){
						plugins.put(configurazioneRicerca.getPlugin().getClassName(), configurazioneRicerca);
					}
				}

			}
		}

		List<ConfigurazioneRicerca> list = new ArrayList<ConfigurazioneRicerca>();
		if(orderByLabel){
			List<String> sortedKey = new ArrayList<String>();
			Hashtable<String, ConfigurazioneRicerca> mapLabelToCorrelazioneRicerca = new Hashtable<String, ConfigurazioneRicerca>();
			Enumeration<String> keys = plugins.keys();
			int count = 0; // lo uso per gestire eventuali label identiche.
			while (keys.hasMoreElements()) {
				String className = (String) keys.nextElement();
				ConfigurazioneRicerca cr = plugins.get(className);
				String labelKey = cr.getLabel()+count;
				sortedKey.add(labelKey);
				mapLabelToCorrelazioneRicerca.put(labelKey, cr);
				count++;
			}
			Collections.sort(sortedKey);
			for (String sortKey : sortedKey) {
				list.add(mapLabelToCorrelazioneRicerca.get(sortKey));
			}
		}
		else{
			list.addAll(plugins.values());
		}
		return list;
	}
	

}
