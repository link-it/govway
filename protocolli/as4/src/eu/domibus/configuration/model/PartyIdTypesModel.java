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

import eu.domibus.configuration.PartyIdTypes;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PartyIdTypes 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PartyIdTypesModel extends AbstractModel<PartyIdTypes> {

	public PartyIdTypesModel(){
	
		super();
	
		this.PARTY_ID_TYPE = new eu.domibus.configuration.model.PartyIdTypeModel(new Field("partyIdType",eu.domibus.configuration.PartyIdType.class,"partyIdTypes",PartyIdTypes.class));
	
	}
	
	public PartyIdTypesModel(IField father){
	
		super(father);
	
		this.PARTY_ID_TYPE = new eu.domibus.configuration.model.PartyIdTypeModel(new ComplexField(father,"partyIdType",eu.domibus.configuration.PartyIdType.class,"partyIdTypes",PartyIdTypes.class));
	
	}
	
	

	public eu.domibus.configuration.model.PartyIdTypeModel PARTY_ID_TYPE = null;
	 

	@Override
	public Class<PartyIdTypes> getModeledClass(){
		return PartyIdTypes.class;
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