/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PartInfo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PartInfoModel extends AbstractModel<PartInfo> {

	public PartInfoModel(){
	
		super();
	
		this.SCHEMA = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.SchemaModel(new Field("Schema",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema.class,"PartInfo",PartInfo.class));
		this.DESCRIPTION = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.DescriptionModel(new Field("Description",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description.class,"PartInfo",PartInfo.class));
		this.PART_PROPERTIES = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.PartPropertiesModel(new Field("PartProperties",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties.class,"PartInfo",PartInfo.class));
		this.HREF = new Field("href",java.lang.String.class,"PartInfo",PartInfo.class);
	
	}
	
	public PartInfoModel(IField father){
	
		super(father);
	
		this.SCHEMA = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.SchemaModel(new ComplexField(father,"Schema",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Schema.class,"PartInfo",PartInfo.class));
		this.DESCRIPTION = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.DescriptionModel(new ComplexField(father,"Description",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description.class,"PartInfo",PartInfo.class));
		this.PART_PROPERTIES = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.PartPropertiesModel(new ComplexField(father,"PartProperties",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartProperties.class,"PartInfo",PartInfo.class));
		this.HREF = new ComplexField(father,"href",java.lang.String.class,"PartInfo",PartInfo.class);
	
	}
	
	

	public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.SchemaModel SCHEMA = null;
	 
	public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.DescriptionModel DESCRIPTION = null;
	 
	public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.PartPropertiesModel PART_PROPERTIES = null;
	 
	public IField HREF = null;
	 

	@Override
	public Class<PartInfo> getModeledClass(){
		return PartInfo.class;
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