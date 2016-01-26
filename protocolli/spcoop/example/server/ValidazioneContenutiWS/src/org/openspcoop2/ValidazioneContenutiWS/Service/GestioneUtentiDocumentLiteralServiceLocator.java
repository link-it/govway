/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
 * GestioneUtentiDocumentLiteralServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.openspcoop2.ValidazioneContenutiWS.Service;

/**
*
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GestioneUtentiDocumentLiteralServiceLocator extends org.apache.axis.client.Service implements org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiDocumentLiteralService {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8132867266357069022L;

	public GestioneUtentiDocumentLiteralServiceLocator() {
    }


    public GestioneUtentiDocumentLiteralServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public GestioneUtentiDocumentLiteralServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for GestioneUtentiDocumentLiteral
    private java.lang.String GestioneUtentiDocumentLiteral_address = "http://127.0.0.1:8080/openspcoop/PA";

    @Override
	public java.lang.String getGestioneUtentiDocumentLiteralAddress() {
        return this.GestioneUtentiDocumentLiteral_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String GestioneUtentiDocumentLiteralWSDDServiceName = "GestioneUtentiDocumentLiteral";

    public java.lang.String getGestioneUtentiDocumentLiteralWSDDServiceName() {
        return this.GestioneUtentiDocumentLiteralWSDDServiceName;
    }

    public void setGestioneUtentiDocumentLiteralWSDDServiceName(java.lang.String name) {
        this.GestioneUtentiDocumentLiteralWSDDServiceName = name;
    }

    @Override
	public org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiDocumentLiteral getGestioneUtentiDocumentLiteral() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(this.GestioneUtentiDocumentLiteral_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getGestioneUtentiDocumentLiteral(endpoint);
    }

    @Override
	public org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiDocumentLiteral getGestioneUtentiDocumentLiteral(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiDocumentLiteralSoapBindingStub _stub = new org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiDocumentLiteralSoapBindingStub(portAddress, this);
            _stub.setPortName(getGestioneUtentiDocumentLiteralWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setGestioneUtentiDocumentLiteralEndpointAddress(java.lang.String address) {
        this.GestioneUtentiDocumentLiteral_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @Override
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiDocumentLiteral.class.isAssignableFrom(serviceEndpointInterface)) {
                org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiDocumentLiteralSoapBindingStub _stub = new org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiDocumentLiteralSoapBindingStub(new java.net.URL(this.GestioneUtentiDocumentLiteral_address), this);
                _stub.setPortName(getGestioneUtentiDocumentLiteralWSDDServiceName());
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
        if ("GestioneUtentiDocumentLiteral".equals(inputPortName)) {
            return getGestioneUtentiDocumentLiteral();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    @Override
	public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service", "GestioneUtentiDocumentLiteralService");
    }

    private java.util.HashSet ports = null;

    @Override
	public java.util.Iterator getPorts() {
        if (this.ports == null) {
            this.ports = new java.util.HashSet();
            this.ports.add(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service", "GestioneUtentiDocumentLiteral"));
        }
        return this.ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("GestioneUtentiDocumentLiteral".equals(portName)) {
            setGestioneUtentiDocumentLiteralEndpointAddress(address);
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
