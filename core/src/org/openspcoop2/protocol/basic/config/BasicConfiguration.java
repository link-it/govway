/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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

package org.openspcoop2.protocol.basic.config;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.message.config.ServiceBindingConfiguration;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.basic.BasicComponentFactory;
import org.openspcoop2.protocol.manifest.Binding;
import org.openspcoop2.protocol.manifest.CollaborationProfile;
import org.openspcoop2.protocol.manifest.Functionality;
import org.openspcoop2.protocol.manifest.InterfaceConfiguration;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.manifest.OrganizationType;
import org.openspcoop2.protocol.manifest.Registry;
import org.openspcoop2.protocol.manifest.ServiceType;
import org.openspcoop2.protocol.manifest.SoapHeaderBypassMustUnderstandHeader;
import org.openspcoop2.protocol.manifest.Version;
import org.openspcoop2.protocol.manifest.constants.InterfaceType;
import org.openspcoop2.protocol.sdk.BypassMustUnderstandCheck;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.utils.transport.TransportRequestContext;

/**
 * Classe che implementa, l'interfaccia {@link org.openspcoop2.protocol.sdk.config.IProtocolConfiguration} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BasicConfiguration extends BasicComponentFactory implements org.openspcoop2.protocol.sdk.config.IProtocolConfiguration {

	private Registry registroManifest;
	private Binding bindingManifest;
	private Openspcoop2 manifest;

	public BasicConfiguration(IProtocolFactory<?> factory) throws ProtocolException {
		super(factory);
		this.manifest = this.protocolFactory.getManifest();
		this.registroManifest = this.manifest.getRegistry();
		this.bindingManifest = this.manifest.getBinding();
	}
	

	
	
	@Override
	public ServiceBindingConfiguration getDefaultServiceBindingConfiguration(TransportRequestContext transportRequest) throws ProtocolException{
		return ServiceBindingConfigurationReader.getDefaultServiceBindingConfiguration(this.manifest, transportRequest);
	}
	
	@Override
	public ServiceBinding getIntegrationServiceBinding(IDServizio idServizio, IRegistryReader registryReader) throws ProtocolException, RegistryNotFound{
		return ServiceBindingConfigurationReader.getServiceBinding(idServizio, registryReader);
	}
	
	@Override
	public ServiceBinding getProtocolServiceBinding(ServiceBinding integrationServiceBinding, TransportRequestContext transportRequest) throws ProtocolException{
		return integrationServiceBinding; // stesso service binding tra integration e protocol per default
	}
	
	@Override
	public ServiceBindingConfiguration getServiceBindingConfiguration(TransportRequestContext transportRequest, ServiceBinding serviceBinding,
			IDServizio idServizio, IRegistryReader registryReader) throws ProtocolException, RegistryNotFound{
		return ServiceBindingConfigurationReader.getServiceBindingConfiguration(this.manifest, transportRequest, serviceBinding, idServizio, registryReader);
	}
	
	@Override
	public List<InterfaceType> getInterfacceSupportate(ServiceBinding serviceBinding){
		List<InterfaceType> list = new ArrayList<InterfaceType>();
		switch (serviceBinding) {
		case SOAP:
			if(this.bindingManifest.getSoap()!=null && 
				this.bindingManifest.getSoap().getInterfaces()!=null &&
				this.bindingManifest.getSoap().getInterfaces().sizeSpecificationList()>0){
				for (InterfaceConfiguration interfaceConfiguration : this.bindingManifest.getSoap().getInterfaces().getSpecificationList()) {
					list.add(interfaceConfiguration.getType());
				}
			}
			break;
		case REST:
			if(this.bindingManifest.getRest()!=null && 
				this.bindingManifest.getRest().getInterfaces()!=null &&
				this.bindingManifest.getRest().getInterfaces().sizeSpecificationList()>0){
				for (InterfaceConfiguration interfaceConfiguration : this.bindingManifest.getRest().getInterfaces().getSpecificationList()) {
					list.add(interfaceConfiguration.getType());
				}
			}
			break;
		}
		return list;
	}
	
	@Override
	public boolean isSupportoSchemaEsternoInterfaccia(ServiceBinding serviceBinding, InterfaceType interfaceType) {
		switch (serviceBinding) {
		case SOAP:
			if(this.bindingManifest.getSoap()!=null && 
				this.bindingManifest.getSoap().getInterfaces()!=null &&
				this.bindingManifest.getSoap().getInterfaces().sizeSpecificationList()>0){
				for (InterfaceConfiguration interfaceConfiguration : this.bindingManifest.getSoap().getInterfaces().getSpecificationList()) {
					if(interfaceType.equals(interfaceConfiguration.getType())) {
						return interfaceConfiguration.isSchema();
					}
				}
			}
			break;
		case REST:
			if(this.bindingManifest.getRest()!=null && 
				this.bindingManifest.getRest().getInterfaces()!=null &&
				this.bindingManifest.getRest().getInterfaces().sizeSpecificationList()>0){
				for (InterfaceConfiguration interfaceConfiguration : this.bindingManifest.getRest().getInterfaces().getSpecificationList()) {
					if(interfaceType.equals(interfaceConfiguration.getType())) {
						return interfaceConfiguration.isSchema();
					}
				}
			}
			break;
		}
		return false;
	}
	
	@Override
	public boolean isSupportoSpecificaConversazioni(ServiceBinding serviceBinding, InterfaceType interfaceType) {
		switch (serviceBinding) {
		case SOAP:
			if(this.bindingManifest.getSoap()!=null && 
				this.bindingManifest.getSoap().getInterfaces()!=null &&
				this.bindingManifest.getSoap().getInterfaces().sizeSpecificationList()>0){
				for (InterfaceConfiguration interfaceConfiguration : this.bindingManifest.getSoap().getInterfaces().getSpecificationList()) {
					if(interfaceType.equals(interfaceConfiguration.getType())) {
						return interfaceConfiguration.isConversations();
					}
				}
			}
			break;
		case REST:
			if(this.bindingManifest.getRest()!=null && 
				this.bindingManifest.getRest().getInterfaces()!=null &&
				this.bindingManifest.getRest().getInterfaces().sizeSpecificationList()>0){
				for (InterfaceConfiguration interfaceConfiguration : this.bindingManifest.getRest().getInterfaces().getSpecificationList()) {
					if(interfaceType.equals(interfaceConfiguration.getType())) {
						return interfaceConfiguration.isConversations();
					}
				}
			}
			break;
		}
		return false;
	}
	
	@Override
	public boolean isSupportoPortiAccessoAccordiParteSpecifica(ServiceBinding serviceBinding, InterfaceType interfaceType) {
		switch (serviceBinding) {
		case SOAP:
			if(this.bindingManifest.getSoap()!=null && 
				this.bindingManifest.getSoap().getInterfaces()!=null &&
				this.bindingManifest.getSoap().getInterfaces().sizeSpecificationList()>0){
				for (InterfaceConfiguration interfaceConfiguration : this.bindingManifest.getSoap().getInterfaces().getSpecificationList()) {
					if(interfaceType.equals(interfaceConfiguration.getType())) {
						return interfaceConfiguration.isImplementation();
					}
				}
			}
			break;
		case REST:
			if(this.bindingManifest.getRest()!=null && 
				this.bindingManifest.getRest().getInterfaces()!=null &&
				this.bindingManifest.getRest().getInterfaces().sizeSpecificationList()>0){
				for (InterfaceConfiguration interfaceConfiguration : this.bindingManifest.getRest().getInterfaces().getSpecificationList()) {
					if(interfaceType.equals(interfaceConfiguration.getType())) {
						return interfaceConfiguration.isImplementation();
					}
				}
			}
			break;
		}
		return false;
	}
	
	@Override
	public boolean isSupportoAutenticazioneSoggetti() {
		return this.registroManifest.getOrganization().getAuthentication();
	}
	
	@Override
	public boolean isSupportoCodiceIPA() {
		return this.registroManifest.getOrganization().isCodeIPA();
	}
	
	@Override
	public boolean isSupportoIdentificativoPortaSoggetto() {
		return this.registroManifest.getOrganization().isCodeDomain();
	}

	@Override
	public boolean isSupportoIndirizzoRisposta(){
		return this.registroManifest.getOrganization().isReplyToAddress();
	}
	
	@Override
	public boolean isSupportoSoggettoReferenteAccordiParteComune() {
		return this.registroManifest.getService().isApiReferent();
	}
	
	@Override
	public boolean isSupportoVersionamentoAccordiParteSpecifica() {
		return this.registroManifest.getService().isVersion();
	}
	
	@Override
	public boolean isSupportoSbustamentoProtocollo() {
		return this.registroManifest.getService().isProtocolEnvelopeManagement();
	}
	
	@Override
	public boolean isSupportoSceltaFault() {
		return this.registroManifest.getService().isFaultChoice();
	}
	
	@Override
	public List<String> getTipiSoggetti() throws ProtocolException {
		List<String> tipi = new ArrayList<String>();
		List<OrganizationType> l = this.registroManifest.getOrganization().getTypes().getTypeList();
		for (int i = 0; i < l.size(); i++) {
			tipi.add(l.get(i).getName());
		}
		return tipi;
	}
	
	@Override
	public String getTipoSoggettoDefault() throws ProtocolException {
		return this.registroManifest.getOrganization().getTypes().getType(0).getName();
	}

	private org.openspcoop2.protocol.manifest.constants.ServiceBinding convert(ServiceBinding serviceBinding){
		switch (serviceBinding) {
			case SOAP:
				return org.openspcoop2.protocol.manifest.constants.ServiceBinding.SOAP;
			case REST:
				return org.openspcoop2.protocol.manifest.constants.ServiceBinding.REST;
		}
		return null;
	}
	
	@Override
	public List<String> getTipiServizi(ServiceBinding serviceBinding) throws ProtocolException {
		
		org.openspcoop2.protocol.manifest.constants.ServiceBinding sb = null;
		if(serviceBinding!=null) {
			sb = this.convert(serviceBinding);
		}
		
		List<String> tipi = new ArrayList<String>();
		List<ServiceType> l = this.registroManifest.getService().getTypes().getTypeList();
		for (int i = 0; i < l.size(); i++) {
			org.openspcoop2.protocol.manifest.constants.ServiceBinding serviceBindingTmp = l.get(i).getBinding();
			if(serviceBindingTmp==null || (sb!=null && serviceBindingTmp.equals(sb))){
				tipi.add(l.get(i).getName());	
			}
		}
		return tipi;
	}

	@Override
	public String getTipoServizioDefault(ServiceBinding serviceBinding) throws ProtocolException {
		List<String> l = this.getTipiServizi(serviceBinding);
		if(l!=null && l.size()>0){
			return l.get(0);
		}
		return null;
	}
	
	@Override
	public List<String> getVersioni() throws ProtocolException{
		List<String> tipi = new ArrayList<String>();
		List<Version> l = this.registroManifest.getVersions().getVersionList();
		for (int i = 0; i < l.size(); i++) {
			tipi.add(l.get(i).getName());
		}
		return tipi;
	}

	@Override
	public String getVersioneDefault() throws ProtocolException {
		return this.registroManifest.getVersions().getVersion(0).getName();
	}
	
	@Override
	public boolean isSupportato(ServiceBinding serviceBinding, ProfiloDiCollaborazione profiloCollaborazione)
			throws ProtocolException {
		if(profiloCollaborazione==null || serviceBinding==null){
			throw new ProtocolException("Params not defined");
		}
		CollaborationProfile profilo = null;
		if(ServiceBinding.REST.equals(serviceBinding)) {
			if(this.bindingManifest.getRest()!=null) {
				profilo = new CollaborationProfile();
				profilo.setInputOutput(true);
				profilo.setOneway(false);
				profilo.setAsyncInputOutput(false);
				profilo.setPolledInputOutput(false);
			}
		}
		else {
			if(this.bindingManifest.getSoap()!=null) {
				profilo = this.bindingManifest.getSoap().getProfile();
			}
		}
		switch (profiloCollaborazione) {
		case ONEWAY:
			return (profilo!=null ? profilo.isOneway() : true); 
		case SINCRONO:		
			return (profilo!=null ? profilo.isInputOutput() : true); 
		case ASINCRONO_SIMMETRICO:		
			return (profilo!=null ? profilo.isAsyncInputOutput() : false); 
		case ASINCRONO_ASIMMETRICO:		
			return (profilo!=null ? profilo.isPolledInputOutput() : false); 
		case UNKNOWN:
			throw new ProtocolException("Param ["+ProfiloDiCollaborazione.UNKNOWN.name()+"] not valid for this method");
		default:
			throw new ProtocolException("Param ["+profiloCollaborazione.getEngineValue()+"] not supported");
		}
	}

	@Override
	public boolean isSupportato(ServiceBinding serviceBinding, FunzionalitaProtocollo funzionalitaProtocollo)
			throws ProtocolException {
		if(funzionalitaProtocollo==null || serviceBinding==null){
			throw new ProtocolException("Params not defined");
		}
		Functionality funzionalita = null;
		if(ServiceBinding.REST.equals(serviceBinding)) {
			if(this.bindingManifest.getRest()!=null) {
				// non supportato ancora
				//funzionalita = this.bindingManifest.getRest().getFunctionality();
			}
		}
		else {
			if(this.bindingManifest.getSoap()!=null) {
				funzionalita = this.bindingManifest.getSoap().getFunctionality();
			}
		}
		switch (funzionalitaProtocollo) {
		case FILTRO_DUPLICATI:
			return (funzionalita!=null ? funzionalita.isDuplicateFilter() : false); 
		case CONFERMA_RICEZIONE:		
			return (funzionalita!=null ? funzionalita.isAcknowledgement() : false); 
		case COLLABORAZIONE:		
			return (funzionalita!=null ? funzionalita.isConversationIdentifier() : false); 
		case CONSEGNA_IN_ORDINE:		
			return (funzionalita!=null ? funzionalita.isDeliveryOrder() : false); 
		case SCADENZA:		
			return (funzionalita!=null ? funzionalita.isExpiration() : false); 
		case MANIFEST_ATTACHMENTS:		
			return (funzionalita!=null ? funzionalita.getManifestAttachments() : false); 
		default:
			throw new ProtocolException("Param ["+funzionalitaProtocollo.getEngineValue()+"] not supported");
		}
	}
	
	@Override
	public List<BypassMustUnderstandCheck> getBypassMustUnderstandCheck(){
		List<BypassMustUnderstandCheck> list = new ArrayList<BypassMustUnderstandCheck>();
		if( this.manifest.getBinding().getSoap()!=null &&
				this.manifest.getBinding().getSoap().getSoapHeaderBypassMustUnderstand()!=null && 
						this.manifest.getBinding().getSoap().getSoapHeaderBypassMustUnderstand().sizeHeaderList()>0){
			for (SoapHeaderBypassMustUnderstandHeader header : this.manifest.getBinding().getSoap().getSoapHeaderBypassMustUnderstand().getHeaderList()) {
				BypassMustUnderstandCheck bypassMustUnderstandCheck = new BypassMustUnderstandCheck();
				bypassMustUnderstandCheck.setElementName(header.getLocalName());
				bypassMustUnderstandCheck.setNamespace(header.getNamespace());
				list.add(bypassMustUnderstandCheck);
			}
		}
		return list;
	}
}
