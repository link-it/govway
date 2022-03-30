/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Error 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ErrorModel extends AbstractModel<Error> {

	public ErrorModel(){
	
		super();
	
		this.DESCRIPTION = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.DescriptionModel(new Field("Description",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description.class,"Error",Error.class));
		this.ERROR_DETAIL = new Field("ErrorDetail",java.lang.String.class,"Error",Error.class);
		this.CATEGORY = new Field("category",java.lang.String.class,"Error",Error.class);
		this.REF_TO_MESSAGE_IN_ERROR = new Field("refToMessageInError",java.lang.String.class,"Error",Error.class);
		this.ERROR_CODE = new Field("errorCode",java.lang.String.class,"Error",Error.class);
		this.ORIGIN = new Field("origin",java.lang.String.class,"Error",Error.class);
		this.SEVERITY = new Field("severity",java.lang.String.class,"Error",Error.class);
		this.SHORT_DESCRIPTION = new Field("shortDescription",java.lang.String.class,"Error",Error.class);
	
	}
	
	public ErrorModel(IField father){
	
		super(father);
	
		this.DESCRIPTION = new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.DescriptionModel(new ComplexField(father,"Description",org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description.class,"Error",Error.class));
		this.ERROR_DETAIL = new ComplexField(father,"ErrorDetail",java.lang.String.class,"Error",Error.class);
		this.CATEGORY = new ComplexField(father,"category",java.lang.String.class,"Error",Error.class);
		this.REF_TO_MESSAGE_IN_ERROR = new ComplexField(father,"refToMessageInError",java.lang.String.class,"Error",Error.class);
		this.ERROR_CODE = new ComplexField(father,"errorCode",java.lang.String.class,"Error",Error.class);
		this.ORIGIN = new ComplexField(father,"origin",java.lang.String.class,"Error",Error.class);
		this.SEVERITY = new ComplexField(father,"severity",java.lang.String.class,"Error",Error.class);
		this.SHORT_DESCRIPTION = new ComplexField(father,"shortDescription",java.lang.String.class,"Error",Error.class);
	
	}
	
	

	public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.model.DescriptionModel DESCRIPTION = null;
	 
	public IField ERROR_DETAIL = null;
	 
	public IField CATEGORY = null;
	 
	public IField REF_TO_MESSAGE_IN_ERROR = null;
	 
	public IField ERROR_CODE = null;
	 
	public IField ORIGIN = null;
	 
	public IField SEVERITY = null;
	 
	public IField SHORT_DESCRIPTION = null;
	 

	@Override
	public Class<Error> getModeledClass(){
		return Error.class;
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