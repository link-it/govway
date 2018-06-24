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

import eu.domibus.configuration.Identifier;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Identifier 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IdentifierModel extends AbstractModel<Identifier> {

	public IdentifierModel(){
	
		super();
	
		this.PARTY_ID = new Field("partyId",java.lang.String.class,"identifier",Identifier.class);
		this.PARTY_ID_TYPE = new Field("partyIdType",java.lang.String.class,"identifier",Identifier.class);
	
	}
	
	public IdentifierModel(IField father){
	
		super(father);
	
		this.PARTY_ID = new ComplexField(father,"partyId",java.lang.String.class,"identifier",Identifier.class);
		this.PARTY_ID_TYPE = new ComplexField(father,"partyIdType",java.lang.String.class,"identifier",Identifier.class);
	
	}
	
	

	public IField PARTY_ID = null;
	 
	public IField PARTY_ID_TYPE = null;
	 

	@Override
	public Class<Identifier> getModeledClass(){
		return Identifier.class;
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