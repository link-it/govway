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
package org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PayloadInfo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PayloadInfo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PayloadInfoModel extends AbstractModel<PayloadInfo> {

	public PayloadInfoModel(){
	
		super();
	
		this.PART_INFO = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.PartInfoModel(new Field("PartInfo",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo.class,"PayloadInfo",PayloadInfo.class));
	
	}
	
	public PayloadInfoModel(IField father){
	
		super(father);
	
		this.PART_INFO = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.PartInfoModel(new ComplexField(father,"PartInfo",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo.class,"PayloadInfo",PayloadInfo.class));
	
	}
	
	

	public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.PartInfoModel PART_INFO = null;
	 

	@Override
	public Class<PayloadInfo> getModeledClass(){
		return PayloadInfo.class;
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