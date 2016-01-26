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
 * EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton.java
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
@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
public class EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton implements org.openspcoop2.ValidazioneContenutiWS.Service.EsitoAggiornamentoAsincronoWrappedDocumentLiteral, org.apache.axis.wsdl.Skeleton {
    /**
	 * 
	 */
	private static final long serialVersionUID = 9050116895537407068L;
	private org.openspcoop2.ValidazioneContenutiWS.Service.EsitoAggiornamentoAsincronoWrappedDocumentLiteral impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton._myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton._myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "esitoAggiornamentoUtenteAsincronoSimmetricoWDLRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", ">esitoAggiornamentoUtenteAsincronoSimmetricoWDLRequest"), org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoSimmetricoWDLRequest.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("esitoAggiornamentoUtenteAsincronoSimmetricoWDL", _params, new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "esitoAggiornamentoUtenteAsincronoSimmetricoWDLResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", ">esitoAggiornamentoUtenteAsincronoSimmetricoWDLResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "esitoAggiornamentoUtenteAsincronoSimmetricoWDL"));
        _oper.setSoapAction("esitoAggiornamentoUtenteAsincronoSimmetricoWDL");
        EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton._myOperationsList.add(_oper);
        if (EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton._myOperations.get("esitoAggiornamentoUtenteAsincronoSimmetricoWDL") == null) {
            EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton._myOperations.put("esitoAggiornamentoUtenteAsincronoSimmetricoWDL", new java.util.ArrayList());
        }
        ((java.util.List)EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton._myOperations.get("esitoAggiornamentoUtenteAsincronoSimmetricoWDL")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "esitoAggiornamentoUtenteAsincronoAsimmetricoWDLRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", ">esitoAggiornamentoUtenteAsincronoAsimmetricoWDLRequest"), org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoAsimmetricoWDLRequest.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("esitoAggiornamentoUtenteAsincronoAsimmetricoWDL", _params, new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "esitoAggiornamentoUtenteAsincronoAsimmetricoWDLResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", ">esitoAggiornamentoUtenteAsincronoAsimmetricoWDLResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "esitoAggiornamentoUtenteAsincronoAsimmetricoWDL"));
        _oper.setSoapAction("esitoAggiornamentoUtenteAsincronoAsimmetricoWDL");
        EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton._myOperationsList.add(_oper);
        if (EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton._myOperations.get("esitoAggiornamentoUtenteAsincronoAsimmetricoWDL") == null) {
            EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton._myOperations.put("esitoAggiornamentoUtenteAsincronoAsimmetricoWDL", new java.util.ArrayList());
        }
        ((java.util.List)EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton._myOperations.get("esitoAggiornamentoUtenteAsincronoAsimmetricoWDL")).add(_oper);
    }

    public EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton() {
        this.impl = new org.openspcoop2.ValidazioneContenutiWS.Service.EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingImpl();
    }

    public EsitoAggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton(org.openspcoop2.ValidazioneContenutiWS.Service.EsitoAggiornamentoAsincronoWrappedDocumentLiteral impl) {
        this.impl = impl;
    }
    @Override
	public org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoSimmetricoWDLResponse esitoAggiornamentoUtenteAsincronoSimmetricoWDL(org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoSimmetricoWDLRequest argomentoWrapped) throws java.rmi.RemoteException
    {
        org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoSimmetricoWDLResponse ret = this.impl.esitoAggiornamentoUtenteAsincronoSimmetricoWDL(argomentoWrapped);
        return ret;
    }

    @Override
	public org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoAsimmetricoWDLResponse esitoAggiornamentoUtenteAsincronoAsimmetricoWDL(org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoAsimmetricoWDLRequest argomentoWrapped) throws java.rmi.RemoteException
    {
        org.openspcoop2.ValidazioneContenutiWS.Service.types.EsitoAggiornamentoUtenteAsincronoAsimmetricoWDLResponse ret = this.impl.esitoAggiornamentoUtenteAsincronoAsimmetricoWDL(argomentoWrapped);
        return ret;
    }

}
