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
 * AggiornamentoUtentiWrappedDocumentLiteralSoapBindingSkeleton.java
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
public class AggiornamentoUtentiWrappedDocumentLiteralSoapBindingSkeleton implements org.openspcoop2.ValidazioneContenutiWS.Service.AggiornamentoUtentiWrappedDocumentLiteral, org.apache.axis.wsdl.Skeleton {
   
	/**
	 * 
	 */
	private static final long serialVersionUID = -335553393275355875L;
	private org.openspcoop2.ValidazioneContenutiWS.Service.AggiornamentoUtentiWrappedDocumentLiteral impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)AggiornamentoUtentiWrappedDocumentLiteralSoapBindingSkeleton._myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return AggiornamentoUtentiWrappedDocumentLiteralSoapBindingSkeleton._myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "notificaAggiornamentoUtenteWDLRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", ">notificaAggiornamentoUtenteWDLRequest"), org.openspcoop2.ValidazioneContenutiWS.Service.types.NotificaAggiornamentoUtenteWDLRequest.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("notificaAggiornamentoUtenteWDL", _params, null);
        _oper.setElementQName(new javax.xml.namespace.QName("", "notificaAggiornamentoUtenteWDL"));
        _oper.setSoapAction("notificaAggiornamentoUtenteWDL");
        AggiornamentoUtentiWrappedDocumentLiteralSoapBindingSkeleton._myOperationsList.add(_oper);
        if (AggiornamentoUtentiWrappedDocumentLiteralSoapBindingSkeleton._myOperations.get("notificaAggiornamentoUtenteWDL") == null) {
            AggiornamentoUtentiWrappedDocumentLiteralSoapBindingSkeleton._myOperations.put("notificaAggiornamentoUtenteWDL", new java.util.ArrayList());
        }
        ((java.util.List)AggiornamentoUtentiWrappedDocumentLiteralSoapBindingSkeleton._myOperations.get("notificaAggiornamentoUtenteWDL")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "aggiornamentoUtenteWDLRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", ">aggiornamentoUtenteWDLRequest"), org.openspcoop2.ValidazioneContenutiWS.Service.types.AggiornamentoUtenteWDLRequest.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("aggiornamentoUtenteWDL", _params, new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "aggiornamentoUtenteWDLResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", ">aggiornamentoUtenteWDLResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "aggiornamentoUtenteWDL"));
        _oper.setSoapAction("aggiornamentoUtenteWDL");
        AggiornamentoUtentiWrappedDocumentLiteralSoapBindingSkeleton._myOperationsList.add(_oper);
        if (AggiornamentoUtentiWrappedDocumentLiteralSoapBindingSkeleton._myOperations.get("aggiornamentoUtenteWDL") == null) {
            AggiornamentoUtentiWrappedDocumentLiteralSoapBindingSkeleton._myOperations.put("aggiornamentoUtenteWDL", new java.util.ArrayList());
        }
        ((java.util.List)AggiornamentoUtentiWrappedDocumentLiteralSoapBindingSkeleton._myOperations.get("aggiornamentoUtenteWDL")).add(_oper);
    }

    public AggiornamentoUtentiWrappedDocumentLiteralSoapBindingSkeleton() {
        this.impl = new org.openspcoop2.ValidazioneContenutiWS.Service.AggiornamentoUtentiWrappedDocumentLiteralSoapBindingImpl();
    }

    public AggiornamentoUtentiWrappedDocumentLiteralSoapBindingSkeleton(org.openspcoop2.ValidazioneContenutiWS.Service.AggiornamentoUtentiWrappedDocumentLiteral impl) {
        this.impl = impl;
    }
    @Override
	public void notificaAggiornamentoUtenteWDL(org.openspcoop2.ValidazioneContenutiWS.Service.types.NotificaAggiornamentoUtenteWDLRequest argomentoWrapped) throws java.rmi.RemoteException
    {
        this.impl.notificaAggiornamentoUtenteWDL(argomentoWrapped);
    }

    @Override
	public org.openspcoop2.ValidazioneContenutiWS.Service.types.AggiornamentoUtenteWDLResponse aggiornamentoUtenteWDL(org.openspcoop2.ValidazioneContenutiWS.Service.types.AggiornamentoUtenteWDLRequest argomentoWrapped) throws java.rmi.RemoteException
    {
        org.openspcoop2.ValidazioneContenutiWS.Service.types.AggiornamentoUtenteWDLResponse ret = this.impl.aggiornamentoUtenteWDL(argomentoWrapped);
        return ret;
    }

}
