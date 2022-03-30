/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.Cache;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Cache 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CacheModel extends AbstractModel<Cache> {

	public CacheModel(){
	
		super();
	
		this.DIMENSIONE = new Field("dimensione",java.lang.String.class,"cache",Cache.class);
		this.ALGORITMO = new Field("algoritmo",java.lang.String.class,"cache",Cache.class);
		this.ITEM_IDLE_TIME = new Field("item-idle-time",java.lang.String.class,"cache",Cache.class);
		this.ITEM_LIFE_SECOND = new Field("item-life-second",java.lang.String.class,"cache",Cache.class);
	
	}
	
	public CacheModel(IField father){
	
		super(father);
	
		this.DIMENSIONE = new ComplexField(father,"dimensione",java.lang.String.class,"cache",Cache.class);
		this.ALGORITMO = new ComplexField(father,"algoritmo",java.lang.String.class,"cache",Cache.class);
		this.ITEM_IDLE_TIME = new ComplexField(father,"item-idle-time",java.lang.String.class,"cache",Cache.class);
		this.ITEM_LIFE_SECOND = new ComplexField(father,"item-life-second",java.lang.String.class,"cache",Cache.class);
	
	}
	
	

	public IField DIMENSIONE = null;
	 
	public IField ALGORITMO = null;
	 
	public IField ITEM_IDLE_TIME = null;
	 
	public IField ITEM_LIFE_SECOND = null;
	 

	@Override
	public Class<Cache> getModeledClass(){
		return Cache.class;
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