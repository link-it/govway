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
package org.openspcoop2.core.controllo_traffico.model;

import org.openspcoop2.core.controllo_traffico.Cache;

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
	
		this.CACHE = new Field("cache",boolean.class,"cache",Cache.class);
		this.SIZE = new Field("size",java.lang.Long.class,"cache",Cache.class);
		this.ALGORITHM = new Field("algorithm",java.lang.String.class,"cache",Cache.class);
		this.IDLE_TIME = new Field("idle-time",java.lang.Long.class,"cache",Cache.class);
		this.LIFE_TIME = new Field("life-time",java.lang.Long.class,"cache",Cache.class);
	
	}
	
	public CacheModel(IField father){
	
		super(father);
	
		this.CACHE = new ComplexField(father,"cache",boolean.class,"cache",Cache.class);
		this.SIZE = new ComplexField(father,"size",java.lang.Long.class,"cache",Cache.class);
		this.ALGORITHM = new ComplexField(father,"algorithm",java.lang.String.class,"cache",Cache.class);
		this.IDLE_TIME = new ComplexField(father,"idle-time",java.lang.Long.class,"cache",Cache.class);
		this.LIFE_TIME = new ComplexField(father,"life-time",java.lang.Long.class,"cache",Cache.class);
	
	}
	
	

	public IField CACHE = null;
	 
	public IField SIZE = null;
	 
	public IField ALGORITHM = null;
	 
	public IField IDLE_TIME = null;
	 
	public IField LIFE_TIME = null;
	 

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