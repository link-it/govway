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
package org.openspcoop2.core.tracciamento.model;

import org.openspcoop2.core.tracciamento.Data;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Data 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DataModel extends AbstractModel<Data> {

	public DataModel(){
	
		super();
	
		this.DATE_TIME = new Field("date-time",java.util.Date.class,"data",Data.class);
		this.SORGENTE = new org.openspcoop2.core.tracciamento.model.TipoDataModel(new Field("sorgente",org.openspcoop2.core.tracciamento.TipoData.class,"data",Data.class));
	
	}
	
	public DataModel(IField father){
	
		super(father);
	
		this.DATE_TIME = new ComplexField(father,"date-time",java.util.Date.class,"data",Data.class);
		this.SORGENTE = new org.openspcoop2.core.tracciamento.model.TipoDataModel(new ComplexField(father,"sorgente",org.openspcoop2.core.tracciamento.TipoData.class,"data",Data.class));
	
	}
	
	

	public IField DATE_TIME = null;
	 
	public org.openspcoop2.core.tracciamento.model.TipoDataModel SORGENTE = null;
	 

	@Override
	public Class<Data> getModeledClass(){
		return Data.class;
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