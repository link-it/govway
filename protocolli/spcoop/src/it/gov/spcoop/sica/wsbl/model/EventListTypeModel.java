/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package it.gov.spcoop.sica.wsbl.model;

import it.gov.spcoop.sica.wsbl.EventListType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model EventListType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EventListTypeModel extends AbstractModel<EventListType> {

	public EventListTypeModel(){
	
		super();
	
		this.EVENT = new it.gov.spcoop.sica.wsbl.model.EventTypeModel(new Field("event",it.gov.spcoop.sica.wsbl.EventType.class,"eventListType",EventListType.class));
	
	}
	
	public EventListTypeModel(IField father){
	
		super(father);
	
		this.EVENT = new it.gov.spcoop.sica.wsbl.model.EventTypeModel(new ComplexField(father,"event",it.gov.spcoop.sica.wsbl.EventType.class,"eventListType",EventListType.class));
	
	}
	
	

	public it.gov.spcoop.sica.wsbl.model.EventTypeModel EVENT = null;
	 

	@Override
	public Class<EventListType> getModeledClass(){
		return EventListType.class;
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