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


package org.openspcoop2.web.lib.audit.dao;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Configurazione dell'appender
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Appender implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -263168495974586424L;
	
	private String nome;
	private String className;
	private ArrayList<AppenderProperty> properties = new ArrayList<AppenderProperty>();
	private long id;
	
	public String getNome() {
		return this.nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getClassName() {
		return this.className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	
	public ArrayList<AppenderProperty> getProperties() {
		return this.properties;
	}
	public void setProperties(ArrayList<AppenderProperty> properties) {
		this.properties = properties;
	}
	public int sizeProperties(){
		return this.properties.size();
	}
	public AppenderProperty getProperty(int index){
		return this.properties.get(index);
	}
	public AppenderProperty removeProperty(int index){
		return this.properties.remove(index);
	}
	public void addProperty(AppenderProperty property){
		this.properties.add(property);
	}
	
	
	public long getId() {
		return this.id;
	}
	public void setId(long id) {
		this.id = id;
	}

	
}
