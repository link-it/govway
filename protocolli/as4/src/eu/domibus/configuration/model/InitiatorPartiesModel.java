/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

import eu.domibus.configuration.InitiatorParties;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model InitiatorParties 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InitiatorPartiesModel extends AbstractModel<InitiatorParties> {

	public InitiatorPartiesModel(){
	
		super();
	
		this.INITIATOR_PARTY = new eu.domibus.configuration.model.InitiatorPartyModel(new Field("initiatorParty",eu.domibus.configuration.InitiatorParty.class,"initiatorParties",InitiatorParties.class));
	
	}
	
	public InitiatorPartiesModel(IField father){
	
		super(father);
	
		this.INITIATOR_PARTY = new eu.domibus.configuration.model.InitiatorPartyModel(new ComplexField(father,"initiatorParty",eu.domibus.configuration.InitiatorParty.class,"initiatorParties",InitiatorParties.class));
	
	}
	
	

	public eu.domibus.configuration.model.InitiatorPartyModel INITIATOR_PARTY = null;
	 

	@Override
	public Class<InitiatorParties> getModeledClass(){
		return InitiatorParties.class;
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