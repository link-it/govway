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
 * GestioneUtentiStileIbridoServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.openspcoop2.ValidazioneContenutiWS.Service;

/**
*
* @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GestioneUtentiStileIbridoServiceLocator extends org.apache.axis.client.Service implements org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiStileIbridoService {

    /**
	 * 
	 */
	private static final long serialVersionUID = 790631174966096815L;

	public GestioneUtentiStileIbridoServiceLocator() {
    }


    public GestioneUtentiStileIbridoServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public GestioneUtentiStileIbridoServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for GestioneUtentiStileIbrido
    private java.lang.String GestioneUtentiStileIbrido_address = "http://127.0.0.1:8080/govway/in";

    @Override
	public java.lang.String getGestioneUtentiStileIbridoAddress() {
        return this.GestioneUtentiStileIbrido_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String GestioneUtentiStileIbridoWSDDServiceName = "GestioneUtentiStileIbrido";

    public java.lang.String getGestioneUtentiStileIbridoWSDDServiceName() {
        return this.GestioneUtentiStileIbridoWSDDServiceName;
    }

    public void setGestioneUtentiStileIbridoWSDDServiceName(java.lang.String name) {
        this.GestioneUtentiStileIbridoWSDDServiceName = name;
    }

    @Override
	public org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiStileIbrido getGestioneUtentiStileIbrido() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(this.GestioneUtentiStileIbrido_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getGestioneUtentiStileIbrido(endpoint);
    }

    @Override
	public org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiStileIbrido getGestioneUtentiStileIbrido(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiStileIbridoSoapBindingStub _stub = new org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiStileIbridoSoapBindingStub(portAddress, this);
            _stub.setPortName(getGestioneUtentiStileIbridoWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setGestioneUtentiStileIbridoEndpointAddress(java.lang.String address) {
        this.GestioneUtentiStileIbrido_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @Override
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiStileIbrido.class.isAssignableFrom(serviceEndpointInterface)) {
                org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiStileIbridoSoapBindingStub _stub = new org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiStileIbridoSoapBindingStub(new java.net.URL(this.GestioneUtentiStileIbrido_address), this);
                _stub.setPortName(getGestioneUtentiStileIbridoWSDDServiceName());
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
        if ("GestioneUtentiStileIbrido".equals(inputPortName)) {
            return getGestioneUtentiStileIbrido();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    @Override
	public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service", "GestioneUtentiStileIbridoService");
    }

    private java.util.HashSet ports = null;

    @Override
	public java.util.Iterator getPorts() {
        if (this.ports == null) {
            this.ports = new java.util.HashSet();
            this.ports.add(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service", "GestioneUtentiStileIbrido"));
        }
        return this.ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("GestioneUtentiStileIbrido".equals(portName)) {
            setGestioneUtentiStileIbridoEndpointAddress(address);
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
