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
package org.openspcoop2.protocol.manifest.model;

import org.openspcoop2.protocol.manifest.Organization;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Organization 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OrganizationModel extends AbstractModel<Organization> {

	public OrganizationModel(){
	
		super();
	
		this.TYPES = new org.openspcoop2.protocol.manifest.model.OrganizationTypesModel(new Field("types",org.openspcoop2.protocol.manifest.OrganizationTypes.class,"Organization",Organization.class));
		this.AUTHENTICATION = new Field("authentication",boolean.class,"Organization",Organization.class);
		this.HTTPS_WITH_TOKEN_AUTHENTICATION = new Field("httpsWithTokenAuthentication",boolean.class,"Organization",Organization.class);
		this.INBOUND_APPLICATIVE_AUTHENTICATION = new Field("inboundApplicativeAuthentication",boolean.class,"Organization",Organization.class);
		this.INBOUND_ORGANIZATION_AUTHORIZATION_WITHOUT_AUTHENTICATION = new Field("inboundOrganizationAuthorizationWithoutAuthentication",boolean.class,"Organization",Organization.class);
		this.INBOUND_EXTERNAL_APPLICATION_AUTHENTICATION = new Field("inboundExternalApplicationAuthentication",boolean.class,"Organization",Organization.class);
		this.CODE_DOMAIN = new Field("codeDomain",boolean.class,"Organization",Organization.class);
		this.CODE_IPA = new Field("codeIPA",boolean.class,"Organization",Organization.class);
		this.REPLY_TO_ADDRESS = new Field("replyToAddress",boolean.class,"Organization",Organization.class);
		this.DASH = new Field("dash",boolean.class,"Organization",Organization.class);
	
	}
	
	public OrganizationModel(IField father){
	
		super(father);
	
		this.TYPES = new org.openspcoop2.protocol.manifest.model.OrganizationTypesModel(new ComplexField(father,"types",org.openspcoop2.protocol.manifest.OrganizationTypes.class,"Organization",Organization.class));
		this.AUTHENTICATION = new ComplexField(father,"authentication",boolean.class,"Organization",Organization.class);
		this.HTTPS_WITH_TOKEN_AUTHENTICATION = new ComplexField(father,"httpsWithTokenAuthentication",boolean.class,"Organization",Organization.class);
		this.INBOUND_APPLICATIVE_AUTHENTICATION = new ComplexField(father,"inboundApplicativeAuthentication",boolean.class,"Organization",Organization.class);
		this.INBOUND_ORGANIZATION_AUTHORIZATION_WITHOUT_AUTHENTICATION = new ComplexField(father,"inboundOrganizationAuthorizationWithoutAuthentication",boolean.class,"Organization",Organization.class);
		this.INBOUND_EXTERNAL_APPLICATION_AUTHENTICATION = new ComplexField(father,"inboundExternalApplicationAuthentication",boolean.class,"Organization",Organization.class);
		this.CODE_DOMAIN = new ComplexField(father,"codeDomain",boolean.class,"Organization",Organization.class);
		this.CODE_IPA = new ComplexField(father,"codeIPA",boolean.class,"Organization",Organization.class);
		this.REPLY_TO_ADDRESS = new ComplexField(father,"replyToAddress",boolean.class,"Organization",Organization.class);
		this.DASH = new ComplexField(father,"dash",boolean.class,"Organization",Organization.class);
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.OrganizationTypesModel TYPES = null;
	 
	public IField AUTHENTICATION = null;
	 
	public IField HTTPS_WITH_TOKEN_AUTHENTICATION = null;
	 
	public IField INBOUND_APPLICATIVE_AUTHENTICATION = null;
	 
	public IField INBOUND_ORGANIZATION_AUTHORIZATION_WITHOUT_AUTHENTICATION = null;
	 
	public IField INBOUND_EXTERNAL_APPLICATION_AUTHENTICATION = null;
	 
	public IField CODE_DOMAIN = null;
	 
	public IField CODE_IPA = null;
	 
	public IField REPLY_TO_ADDRESS = null;
	 
	public IField DASH = null;
	 

	@Override
	public Class<Organization> getModeledClass(){
		return Organization.class;
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