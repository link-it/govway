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
package eu.domibus.configuration.model;

import eu.domibus.configuration.PayloadProfile;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PayloadProfile 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PayloadProfileModel extends AbstractModel<PayloadProfile> {

	public PayloadProfileModel(){
	
		super();
	
		this.ATTACHMENT = new eu.domibus.configuration.model.AttachmentModel(new Field("attachment",eu.domibus.configuration.Attachment.class,"payloadProfile",PayloadProfile.class));
		this.NAME = new Field("name",java.lang.String.class,"payloadProfile",PayloadProfile.class);
		this.MAX_SIZE = new Field("maxSize",java.math.BigInteger.class,"payloadProfile",PayloadProfile.class);
	
	}
	
	public PayloadProfileModel(IField father){
	
		super(father);
	
		this.ATTACHMENT = new eu.domibus.configuration.model.AttachmentModel(new ComplexField(father,"attachment",eu.domibus.configuration.Attachment.class,"payloadProfile",PayloadProfile.class));
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"payloadProfile",PayloadProfile.class);
		this.MAX_SIZE = new ComplexField(father,"maxSize",java.math.BigInteger.class,"payloadProfile",PayloadProfile.class);
	
	}
	
	

	public eu.domibus.configuration.model.AttachmentModel ATTACHMENT = null;
	 
	public IField NAME = null;
	 
	public IField MAX_SIZE = null;
	 

	@Override
	public Class<PayloadProfile> getModeledClass(){
		return PayloadProfile.class;
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