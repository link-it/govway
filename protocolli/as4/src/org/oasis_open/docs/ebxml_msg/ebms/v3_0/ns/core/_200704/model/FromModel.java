/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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
package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model From 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FromModel extends AbstractModel<From> {

	public FromModel(){
	
		super();
	
		this.PARTY_ID = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.PartyIdModel(new Field("PartyId",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId.class,"From",From.class));
		this.ROLE = new Field("Role",java.lang.String.class,"From",From.class);
	
	}
	
	public FromModel(IField father){
	
		super(father);
	
		this.PARTY_ID = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.PartyIdModel(new ComplexField(father,"PartyId",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId.class,"From",From.class));
		this.ROLE = new ComplexField(father,"Role",java.lang.String.class,"From",From.class);
	
	}
	
	

	public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.PartyIdModel PARTY_ID = null;
	 
	public IField ROLE = null;
	 

	@Override
	public Class<From> getModeledClass(){
		return From.class;
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