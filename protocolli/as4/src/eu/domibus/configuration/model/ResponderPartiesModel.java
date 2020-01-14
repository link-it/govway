/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import eu.domibus.configuration.ResponderParties;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ResponderParties 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ResponderPartiesModel extends AbstractModel<ResponderParties> {

	public ResponderPartiesModel(){
	
		super();
	
		this.RESPONDER_PARTY = new eu.domibus.configuration.model.ResponderPartyModel(new Field("responderParty",eu.domibus.configuration.ResponderParty.class,"responderParties",ResponderParties.class));
	
	}
	
	public ResponderPartiesModel(IField father){
	
		super(father);
	
		this.RESPONDER_PARTY = new eu.domibus.configuration.model.ResponderPartyModel(new ComplexField(father,"responderParty",eu.domibus.configuration.ResponderParty.class,"responderParties",ResponderParties.class));
	
	}
	
	

	public eu.domibus.configuration.model.ResponderPartyModel RESPONDER_PARTY = null;
	 

	@Override
	public Class<ResponderParties> getModeledClass(){
		return ResponderParties.class;
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