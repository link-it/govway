/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it).
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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.AccessoRegistroCache;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AccessoRegistroCache 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccessoRegistroCacheModel extends AbstractModel<AccessoRegistroCache> {

	public AccessoRegistroCacheModel(){
	
		super();
	
		this.DIMENSIONE = new Field("dimensione",java.lang.String.class,"accesso-registro-cache",AccessoRegistroCache.class);
		this.ALGORITMO = new Field("algoritmo",java.lang.String.class,"accesso-registro-cache",AccessoRegistroCache.class);
		this.ITEM_IDLE_TIME = new Field("item-idle-time",java.lang.String.class,"accesso-registro-cache",AccessoRegistroCache.class);
		this.ITEM_LIFE_SECOND = new Field("item-life-second",java.lang.String.class,"accesso-registro-cache",AccessoRegistroCache.class);
	
	}
	
	public AccessoRegistroCacheModel(IField father){
	
		super(father);
	
		this.DIMENSIONE = new ComplexField(father,"dimensione",java.lang.String.class,"accesso-registro-cache",AccessoRegistroCache.class);
		this.ALGORITMO = new ComplexField(father,"algoritmo",java.lang.String.class,"accesso-registro-cache",AccessoRegistroCache.class);
		this.ITEM_IDLE_TIME = new ComplexField(father,"item-idle-time",java.lang.String.class,"accesso-registro-cache",AccessoRegistroCache.class);
		this.ITEM_LIFE_SECOND = new ComplexField(father,"item-life-second",java.lang.String.class,"accesso-registro-cache",AccessoRegistroCache.class);
	
	}
	
	

	public IField DIMENSIONE = null;
	 
	public IField ALGORITMO = null;
	 
	public IField ITEM_IDLE_TIME = null;
	 
	public IField ITEM_LIFE_SECOND = null;
	 

	@Override
	public Class<AccessoRegistroCache> getModeledClass(){
		return AccessoRegistroCache.class;
	}
	
	@Override
	public String toString(){
		if(this.getModeledClass()!=null){
			return this.getModeledClass().getName();
		}else{
			return "N.D.";
		}
	}

}