/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package eu.domibus.configuration.model;

import eu.domibus.configuration.PartyIdType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PartyIdType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PartyIdTypeModel extends AbstractModel<PartyIdType> {

	public PartyIdTypeModel(){
	
		super();
	
		this.NAME = new Field("name",java.lang.String.class,"partyIdType",PartyIdType.class);
		this.VALUE = new Field("value",java.net.URI.class,"partyIdType",PartyIdType.class);
	
	}
	
	public PartyIdTypeModel(IField father){
	
		super(father);
	
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"partyIdType",PartyIdType.class);
		this.VALUE = new ComplexField(father,"value",java.net.URI.class,"partyIdType",PartyIdType.class);
	
	}
	
	

	public IField NAME = null;
	 
	public IField VALUE = null;
	 

	@Override
	public Class<PartyIdType> getModeledClass(){
		return PartyIdType.class;
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