/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import org.openspcoop2.protocol.manifest.SoapConfiguration;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model SoapConfiguration 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoapConfigurationModel extends AbstractModel<SoapConfiguration> {

	public SoapConfigurationModel(){
	
		super();
	
		this.INTEGRATION = new org.openspcoop2.protocol.manifest.model.IntegrationModel(new Field("integration",org.openspcoop2.protocol.manifest.Integration.class,"SoapConfiguration",SoapConfiguration.class));
		this.INTEGRATION_ERROR = new org.openspcoop2.protocol.manifest.model.IntegrationErrorConfigurationModel(new Field("integrationError",org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration.class,"SoapConfiguration",SoapConfiguration.class));
		this.MEDIA_TYPE_COLLECTION = new org.openspcoop2.protocol.manifest.model.SoapMediaTypeCollectionModel(new Field("mediaTypeCollection",org.openspcoop2.protocol.manifest.SoapMediaTypeCollection.class,"SoapConfiguration",SoapConfiguration.class));
		this.INTERFACES = new org.openspcoop2.protocol.manifest.model.InterfacesConfigurationModel(new Field("interfaces",org.openspcoop2.protocol.manifest.InterfacesConfiguration.class,"SoapConfiguration",SoapConfiguration.class));
		this.PROFILE = new org.openspcoop2.protocol.manifest.model.CollaborationProfileModel(new Field("profile",org.openspcoop2.protocol.manifest.CollaborationProfile.class,"SoapConfiguration",SoapConfiguration.class));
		this.FUNCTIONALITY = new org.openspcoop2.protocol.manifest.model.FunctionalityModel(new Field("functionality",org.openspcoop2.protocol.manifest.Functionality.class,"SoapConfiguration",SoapConfiguration.class));
		this.SOAP_HEADER_BYPASS_MUST_UNDERSTAND = new org.openspcoop2.protocol.manifest.model.SoapHeaderBypassMustUnderstandModel(new Field("soapHeaderBypassMustUnderstand",org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand.class,"SoapConfiguration",SoapConfiguration.class));
		this.SOAP_11 = new Field("soap11",boolean.class,"SoapConfiguration",SoapConfiguration.class);
		this.SOAP_11_WITH_ATTACHMENTS = new Field("soap11_withAttachments",boolean.class,"SoapConfiguration",SoapConfiguration.class);
		this.SOAP_11_MTOM = new Field("soap11_mtom",boolean.class,"SoapConfiguration",SoapConfiguration.class);
		this.SOAP_12 = new Field("soap12",boolean.class,"SoapConfiguration",SoapConfiguration.class);
		this.SOAP_12_WITH_ATTACHMENTS = new Field("soap12_withAttachments",boolean.class,"SoapConfiguration",SoapConfiguration.class);
		this.SOAP_12_MTOM = new Field("soap12_mtom",boolean.class,"SoapConfiguration",SoapConfiguration.class);
	
	}
	
	public SoapConfigurationModel(IField father){
	
		super(father);
	
		this.INTEGRATION = new org.openspcoop2.protocol.manifest.model.IntegrationModel(new ComplexField(father,"integration",org.openspcoop2.protocol.manifest.Integration.class,"SoapConfiguration",SoapConfiguration.class));
		this.INTEGRATION_ERROR = new org.openspcoop2.protocol.manifest.model.IntegrationErrorConfigurationModel(new ComplexField(father,"integrationError",org.openspcoop2.protocol.manifest.IntegrationErrorConfiguration.class,"SoapConfiguration",SoapConfiguration.class));
		this.MEDIA_TYPE_COLLECTION = new org.openspcoop2.protocol.manifest.model.SoapMediaTypeCollectionModel(new ComplexField(father,"mediaTypeCollection",org.openspcoop2.protocol.manifest.SoapMediaTypeCollection.class,"SoapConfiguration",SoapConfiguration.class));
		this.INTERFACES = new org.openspcoop2.protocol.manifest.model.InterfacesConfigurationModel(new ComplexField(father,"interfaces",org.openspcoop2.protocol.manifest.InterfacesConfiguration.class,"SoapConfiguration",SoapConfiguration.class));
		this.PROFILE = new org.openspcoop2.protocol.manifest.model.CollaborationProfileModel(new ComplexField(father,"profile",org.openspcoop2.protocol.manifest.CollaborationProfile.class,"SoapConfiguration",SoapConfiguration.class));
		this.FUNCTIONALITY = new org.openspcoop2.protocol.manifest.model.FunctionalityModel(new ComplexField(father,"functionality",org.openspcoop2.protocol.manifest.Functionality.class,"SoapConfiguration",SoapConfiguration.class));
		this.SOAP_HEADER_BYPASS_MUST_UNDERSTAND = new org.openspcoop2.protocol.manifest.model.SoapHeaderBypassMustUnderstandModel(new ComplexField(father,"soapHeaderBypassMustUnderstand",org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstand.class,"SoapConfiguration",SoapConfiguration.class));
		this.SOAP_11 = new ComplexField(father,"soap11",boolean.class,"SoapConfiguration",SoapConfiguration.class);
		this.SOAP_11_WITH_ATTACHMENTS = new ComplexField(father,"soap11_withAttachments",boolean.class,"SoapConfiguration",SoapConfiguration.class);
		this.SOAP_11_MTOM = new ComplexField(father,"soap11_mtom",boolean.class,"SoapConfiguration",SoapConfiguration.class);
		this.SOAP_12 = new ComplexField(father,"soap12",boolean.class,"SoapConfiguration",SoapConfiguration.class);
		this.SOAP_12_WITH_ATTACHMENTS = new ComplexField(father,"soap12_withAttachments",boolean.class,"SoapConfiguration",SoapConfiguration.class);
		this.SOAP_12_MTOM = new ComplexField(father,"soap12_mtom",boolean.class,"SoapConfiguration",SoapConfiguration.class);
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.IntegrationModel INTEGRATION = null;
	 
	public org.openspcoop2.protocol.manifest.model.IntegrationErrorConfigurationModel INTEGRATION_ERROR = null;
	 
	public org.openspcoop2.protocol.manifest.model.SoapMediaTypeCollectionModel MEDIA_TYPE_COLLECTION = null;
	 
	public org.openspcoop2.protocol.manifest.model.InterfacesConfigurationModel INTERFACES = null;
	 
	public org.openspcoop2.protocol.manifest.model.CollaborationProfileModel PROFILE = null;
	 
	public org.openspcoop2.protocol.manifest.model.FunctionalityModel FUNCTIONALITY = null;
	 
	public org.openspcoop2.protocol.manifest.model.SoapHeaderBypassMustUnderstandModel SOAP_HEADER_BYPASS_MUST_UNDERSTAND = null;
	 
	public IField SOAP_11 = null;
	 
	public IField SOAP_11_WITH_ATTACHMENTS = null;
	 
	public IField SOAP_11_MTOM = null;
	 
	public IField SOAP_12 = null;
	 
	public IField SOAP_12_WITH_ATTACHMENTS = null;
	 
	public IField SOAP_12_MTOM = null;
	 

	@Override
	public Class<SoapConfiguration> getModeledClass(){
		return SoapConfiguration.class;
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