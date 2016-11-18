package org.openspcoop2.pdd.services;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.config.ServiceBindingConfiguration;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.sdk.IProtocolFactory;

public class RequestInfo implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private URLProtocolContext protocolContext;
	private ServiceBindingConfiguration bindingConfig;
	private ServiceBinding serviceBinding;
	private MessageType requestMessageType;
	private IProtocolFactory protocolFactory;
	private IDSoggetto identitaPdD;
	private IDSoggetto fruitore;
	private IDServizio idServizio;
	
	public IProtocolFactory getProtocolFactory() {
		return this.protocolFactory;
	}
	public void setProtocolFactory(IProtocolFactory protocolFactory) {
		this.protocolFactory = protocolFactory;
	}
	public URLProtocolContext getProtocolContext() {
		return this.protocolContext;
	}
	public void setProtocolContext(URLProtocolContext protocolContext) {
		this.protocolContext = protocolContext;
	}
	public ServiceBindingConfiguration getBindingConfig() {
		return this.bindingConfig;
	}
	public void setBindingConfig(ServiceBindingConfiguration bindingConfig) {
		this.bindingConfig = bindingConfig;
	}
	public ServiceBinding getServiceBinding() {
		return this.serviceBinding;
	}
	public void setServiceBinding(ServiceBinding serviceBinding) {
		this.serviceBinding = serviceBinding;
	}
	public MessageType getRequestMessageType() {
		return this.requestMessageType;
	}
	public void setRequestMessageType(MessageType messageType) {
		this.requestMessageType = messageType;
	}
	public IDSoggetto getIdentitaPdD() {
		return this.identitaPdD;
	}
	public void setIdentitaPdD(IDSoggetto identitaPdD) {
		this.identitaPdD = identitaPdD;
	}
	public IDSoggetto getFruitore() {
		return this.fruitore;
	}
	public void setFruitore(IDSoggetto fruitore) {
		this.fruitore = fruitore;
	}
	public IDServizio getIdServizio() {
		return this.idServizio;
	}
	public void setIdServizio(IDServizio idServizio) {
		this.idServizio = idServizio;
	}
}
