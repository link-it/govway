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

package org.openspcoop2.protocol.sdk;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Security Info
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SecurityInfo {

	private String subject;
	private String digestHeader;
	private List<Allegato> listaAllegati = null;
	private Hashtable<String, String> properties = null;
	
	public SecurityInfo(){
		this.listaAllegati = new ArrayList<Allegato>();
		this.properties = new Hashtable<String, String>();
	}
	
	public List<Allegato> getListaAllegati() {
        if (this.listaAllegati == null) {
            this.listaAllegati = new ArrayList<Allegato>();
        }
        return this.listaAllegati;
    }
	public int sizeListaAllegati() {
		return this.listaAllegati.size();
	}
	public void addAllegato(Allegato a) {
		this.listaAllegati.add(a);
	}
	public Allegato getAllegato(int index) {
		return this.listaAllegati.get( index );
	}
	public Allegato removeAllegato(int index) {
		return this.listaAllegati.remove(index);
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDigestHeader() {
		return this.digestHeader;
	}

	public void setDigestHeader(String digestHeader) {
		this.digestHeader = digestHeader;
	}

	public void setListaAllegati(List<Allegato> listaAllegati) {
		this.listaAllegati = listaAllegati;
	}
	
	public void addProperty(String key,String value){
    	this.properties.put(key,value);
    }
    
    public int sizeProperties(){
    	return this.properties.size();
    }

    public String getProperty(String key){
    	return this.properties.get(key);
    }
    
    public String removeProperty(String key){
    	return this.properties.remove(key);
    }
    
    public String[] getPropertiesValues() {
    	return this.properties.values().toArray(new String[this.properties.size()]);
    }
    
    public String[] getPropertiesNames() {
    	return this.properties.keySet().toArray(new String[this.properties.size()]);
    }
    
    public void setProperties(Hashtable<String, String> params) {
    	this.properties = params;
    }
    
    public Hashtable<String, String> getProperties() {
    	return this.properties;
    }
}
