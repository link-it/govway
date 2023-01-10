/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PartyInfo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PartyInfoModel extends AbstractModel<PartyInfo> {

	public PartyInfoModel(){
	
		super();
	
		this.FROM = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.FromModel(new Field("From",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From.class,"PartyInfo",PartyInfo.class));
		this.TO = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.ToModel(new Field("To",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To.class,"PartyInfo",PartyInfo.class));
	
	}
	
	public PartyInfoModel(IField father){
	
		super(father);
	
		this.FROM = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.FromModel(new ComplexField(father,"From",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.From.class,"PartyInfo",PartyInfo.class));
		this.TO = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.ToModel(new ComplexField(father,"To",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.To.class,"PartyInfo",PartyInfo.class));
	
	}
	
	

	public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.FromModel FROM = null;
	 
	public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.ToModel TO = null;
	 

	@Override
	public Class<PartyInfo> getModeledClass(){
		return PartyInfo.class;
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