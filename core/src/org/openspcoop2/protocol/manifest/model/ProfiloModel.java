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
package org.openspcoop2.protocol.manifest.model;

import org.openspcoop2.protocol.manifest.Profilo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Profilo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProfiloModel extends AbstractModel<Profilo> {

	public ProfiloModel(){
	
		super();
	
		this.ONEWAY = new Field("oneway",boolean.class,"profilo",Profilo.class);
		this.SINCRONO = new Field("sincrono",boolean.class,"profilo",Profilo.class);
		this.ASINCRONO_ASIMMETRICO = new Field("asincronoAsimmetrico",boolean.class,"profilo",Profilo.class);
		this.ASINCRONO_SIMMETRICO = new Field("asincronoSimmetrico",boolean.class,"profilo",Profilo.class);
	
	}
	
	public ProfiloModel(IField father){
	
		super(father);
	
		this.ONEWAY = new ComplexField(father,"oneway",boolean.class,"profilo",Profilo.class);
		this.SINCRONO = new ComplexField(father,"sincrono",boolean.class,"profilo",Profilo.class);
		this.ASINCRONO_ASIMMETRICO = new ComplexField(father,"asincronoAsimmetrico",boolean.class,"profilo",Profilo.class);
		this.ASINCRONO_SIMMETRICO = new ComplexField(father,"asincronoSimmetrico",boolean.class,"profilo",Profilo.class);
	
	}
	
	

	public IField ONEWAY = null;
	 
	public IField SINCRONO = null;
	 
	public IField ASINCRONO_ASIMMETRICO = null;
	 
	public IField ASINCRONO_SIMMETRICO = null;
	 

	@Override
	public Class<Profilo> getModeledClass(){
		return Profilo.class;
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