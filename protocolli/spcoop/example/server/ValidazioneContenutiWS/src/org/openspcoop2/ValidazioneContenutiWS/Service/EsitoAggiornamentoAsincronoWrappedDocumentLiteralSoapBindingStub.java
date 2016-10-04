/*
 * OpenSPCoop - Customizable API Gateway 
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
 * EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingStub.java
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
public class EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingStub extends org.apache.axis.client.Stub implements org.openspcoop2.ValidazioneContenutiWS.Service.EsitoAggiornamentoAsincronoWrappedDocumentLiteral {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingStub._operations = new org.apache.axis.description.OperationDesc[2];
        EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingStub._initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("esitoAggiornamentoUtenteAsincronoSimmetricoWDL");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "esitoAggiornamentoUtenteAsincronoSimmetricoWDLRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", ">esitoAggiornamentoUtenteAsincronoSimmetricoWDLRequest"), org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoSimmetricoWDLRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", ">esitoAggiornamentoUtenteAsincronoSimmetricoWDLResponse"));
        oper.setReturnClass(org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoSimmetricoWDLResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "esitoAggiornamentoUtenteAsincronoSimmetricoWDLResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingStub._operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("esitoAggiornamentoUtenteAsincronoAsimmetricoWDL");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "esitoAggiornamentoUtenteAsincronoAsimmetricoWDLRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", ">esitoAggiornamentoUtenteAsincronoAsimmetricoWDLRequest"), org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoAsimmetricoWDLRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", ">esitoAggiornamentoUtenteAsincronoAsimmetricoWDLResponse"));
        oper.setReturnClass(org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoAsimmetricoWDLResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "esitoAggiornamentoUtenteAsincronoAsimmetricoWDLResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingStub._operations[1] = oper;

    }

    public EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", ">esitoAggiornamentoUtenteAsincronoAsimmetricoWDLRequest");
            this.cachedSerQNames.add(qName);
            cls = org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoAsimmetricoWDLRequest.class;
            this.cachedSerClasses.add(cls);
            this.cachedSerFactories.add(beansf);
            this.cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", ">esitoAggiornamentoUtenteAsincronoAsimmetricoWDLResponse");
            this.cachedSerQNames.add(qName);
            cls = org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoAsimmetricoWDLResponse.class;
            this.cachedSerClasses.add(cls);
            this.cachedSerFactories.add(beansf);
            this.cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", ">esitoAggiornamentoUtenteAsincronoSimmetricoWDLRequest");
            this.cachedSerQNames.add(qName);
            cls = org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoSimmetricoWDLRequest.class;
            this.cachedSerClasses.add(cls);
            this.cachedSerFactories.add(beansf);
            this.cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", ">esitoAggiornamentoUtenteAsincronoSimmetricoWDLResponse");
            this.cachedSerQNames.add(qName);
            cls = org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoSimmetricoWDLResponse.class;
            this.cachedSerClasses.add(cls);
            this.cachedSerFactories.add(beansf);
            this.cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < this.cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) this.cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) this.cachedSerQNames.get(i);
                        java.lang.Object x = this.cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 this.cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 this.cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 this.cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 this.cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    @Override
	public org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoSimmetricoWDLResponse esitoAggiornamentoUtenteAsincronoSimmetricoWDL(org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoSimmetricoWDLRequest argomentoWrapped) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingStub._operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("esitoAggiornamentoUtenteAsincronoSimmetricoWDL");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "esitoAggiornamentoUtenteAsincronoSimmetricoWDL"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {argomentoWrapped});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoSimmetricoWDLResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoSimmetricoWDLResponse) org.apache.axis.utils.JavaUtils.convert(_resp, org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoSimmetricoWDLResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoAsimmetricoWDLResponse esitoAggiornamentoUtenteAsincronoAsimmetricoWDL(org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoAsimmetricoWDLRequest argomentoWrapped) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingStub._operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("esitoAggiornamentoUtenteAsincronoAsimmetricoWDL");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "esitoAggiornamentoUtenteAsincronoAsimmetricoWDL"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {argomentoWrapped});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoAsimmetricoWDLResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoAsimmetricoWDLResponse) org.apache.axis.utils.JavaUtils.convert(_resp, org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoAsimmetricoWDLResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
