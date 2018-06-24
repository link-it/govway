/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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

import eu.domibus.configuration.Parties;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Parties 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PartiesModel extends AbstractModel<Parties> {

	public PartiesModel(){
	
		super();
	
		this.PARTY_ID_TYPES = new eu.domibus.configuration.model.PartyIdTypesModel(new Field("partyIdTypes",eu.domibus.configuration.PartyIdTypes.class,"parties",Parties.class));
		this.PARTY = new eu.domibus.configuration.model.PartyModel(new Field("party",eu.domibus.configuration.Party.class,"parties",Parties.class));
	
	}
	
	public PartiesModel(IField father){
	
		super(father);
	
		this.PARTY_ID_TYPES = new eu.domibus.configuration.model.PartyIdTypesModel(new ComplexField(father,"partyIdTypes",eu.domibus.configuration.PartyIdTypes.class,"parties",Parties.class));
		this.PARTY = new eu.domibus.configuration.model.PartyModel(new ComplexField(father,"party",eu.domibus.configuration.Party.class,"parties",Parties.class));
	
	}
	
	

	public eu.domibus.configuration.model.PartyIdTypesModel PARTY_ID_TYPES = null;
	 
	public eu.domibus.configuration.model.PartyModel PARTY = null;
	 

	@Override
	public Class<Parties> getModeledClass(){
		return Parties.class;
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