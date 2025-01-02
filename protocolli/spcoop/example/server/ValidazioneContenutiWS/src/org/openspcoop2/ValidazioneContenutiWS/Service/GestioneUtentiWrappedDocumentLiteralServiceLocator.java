/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

/**
 * GestioneUtentiWrappedDocumentLiteralServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.openspcoop2.ValidazioneContenutiWS.Service;

import java.net.URISyntaxException;

/**
*
* @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GestioneUtentiWrappedDocumentLiteralServiceLocator extends org.apache.axis.client.Service implements org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiWrappedDocumentLiteralService {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5271369524609405432L;

	public GestioneUtentiWrappedDocumentLiteralServiceLocator() {
    }


    public GestioneUtentiWrappedDocumentLiteralServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public GestioneUtentiWrappedDocumentLiteralServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for GestioneUtentiWrappedDocumentLiteral
    private java.lang.String GestioneUtentiWrappedDocumentLiteral_address = "http://127.0.0.1:8080/govway/in";

    @Override
	public java.lang.String getGestioneUtentiWrappedDocumentLiteralAddress() {
        return this.GestioneUtentiWrappedDocumentLiteral_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String GestioneUtentiWrappedDocumentLiteralWSDDServiceName = "GestioneUtentiWrappedDocumentLiteral";

    public java.lang.String getGestioneUtentiWrappedDocumentLiteralWSDDServiceName() {
        return this.GestioneUtentiWrappedDocumentLiteralWSDDServiceName;
    }

    public void setGestioneUtentiWrappedDocumentLiteralWSDDServiceName(java.lang.String name) {
        this.GestioneUtentiWrappedDocumentLiteralWSDDServiceName = name;
    }

    @Override
	public org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiWrappedDocumentLiteral getGestioneUtentiWrappedDocumentLiteral() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new  java.net.URI(this.GestioneUtentiWrappedDocumentLiteral_address).toURL();
        }
        catch (java.net.MalformedURLException | URISyntaxException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getGestioneUtentiWrappedDocumentLiteral(endpoint);
    }

    @Override
	public org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiWrappedDocumentLiteral getGestioneUtentiWrappedDocumentLiteral(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiWrappedDocumentLiteralSoapBindingStub _stub = new org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiWrappedDocumentLiteralSoapBindingStub(portAddress, this);
            _stub.setPortName(getGestioneUtentiWrappedDocumentLiteralWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setGestioneUtentiWrappedDocumentLiteralEndpointAddress(java.lang.String address) {
        this.GestioneUtentiWrappedDocumentLiteral_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @Override
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiWrappedDocumentLiteral.class.isAssignableFrom(serviceEndpointInterface)) {
                org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiWrappedDocumentLiteralSoapBindingStub _stub = new org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiWrappedDocumentLiteralSoapBindingStub(new  java.net.URI(this.GestioneUtentiWrappedDocumentLiteral_address).toURL(), this);
                _stub.setPortName(getGestioneUtentiWrappedDocumentLiteralWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @Override
	public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("GestioneUtentiWrappedDocumentLiteral".equals(inputPortName)) {
            return getGestioneUtentiWrappedDocumentLiteral();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    @Override
	public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service", "GestioneUtentiWrappedDocumentLiteralService");
    }

    private java.util.HashSet ports = null;

    @Override
	public java.util.Iterator getPorts() {
        if (this.ports == null) {
            this.ports = new java.util.HashSet();
            this.ports.add(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service", "GestioneUtentiWrappedDocumentLiteral"));
        }
        return this.ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("GestioneUtentiWrappedDocumentLiteral".equals(portName)) {
            setGestioneUtentiWrappedDocumentLiteralEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
