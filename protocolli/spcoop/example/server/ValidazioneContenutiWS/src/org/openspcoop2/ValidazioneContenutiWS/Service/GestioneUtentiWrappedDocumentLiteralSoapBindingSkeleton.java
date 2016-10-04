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
 * GestioneUtentiWrappedDocumentLiteralSoapBindingSkeleton.java
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
public class GestioneUtentiWrappedDocumentLiteralSoapBindingSkeleton implements org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiWrappedDocumentLiteral, org.apache.axis.wsdl.Skeleton {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4201218069154879847L;
	private org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiWrappedDocumentLiteral impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)GestioneUtentiWrappedDocumentLiteralSoapBindingSkeleton._myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return GestioneUtentiWrappedDocumentLiteralSoapBindingSkeleton._myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "registrazioneUtenteWDLRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types/esempio1", "registrazioneUtenteWDLRequestType"), org.openspcoop2.ValidazioneContenutiWS.Service.types.esempio1.RegistrazioneUtenteWDLRequestType.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("registrazioneUtenteWDL", _params, new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "registrazioneUtenteWDLResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types/esempio1", "registrazioneUtenteWDLResponseType"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "registrazioneUtenteWDL"));
        _oper.setSoapAction("registrazioneUtenteWDL");
        GestioneUtentiWrappedDocumentLiteralSoapBindingSkeleton._myOperationsList.add(_oper);
        if (GestioneUtentiWrappedDocumentLiteralSoapBindingSkeleton._myOperations.get("registrazioneUtenteWDL") == null) {
            GestioneUtentiWrappedDocumentLiteralSoapBindingSkeleton._myOperations.put("registrazioneUtenteWDL", new java.util.ArrayList());
        }
        ((java.util.List)GestioneUtentiWrappedDocumentLiteralSoapBindingSkeleton._myOperations.get("registrazioneUtenteWDL")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "eliminazioneUtenteWDLRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types/esempio1", "eliminazioneUtenteWDLRequestType"), org.openspcoop2.ValidazioneContenutiWS.Service.types.esempio1.EliminazioneUtenteWDLRequestType.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("eliminazioneUtenteWDL", _params, new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "eliminazioneUtenteWDLResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types/esempio1", "eliminazioneUtenteWDLResponseType"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "eliminazioneUtenteWDL"));
        _oper.setSoapAction("eliminazioneUtenteWDL");
        GestioneUtentiWrappedDocumentLiteralSoapBindingSkeleton._myOperationsList.add(_oper);
        if (GestioneUtentiWrappedDocumentLiteralSoapBindingSkeleton._myOperations.get("eliminazioneUtenteWDL") == null) {
            GestioneUtentiWrappedDocumentLiteralSoapBindingSkeleton._myOperations.put("eliminazioneUtenteWDL", new java.util.ArrayList());
        }
        ((java.util.List)GestioneUtentiWrappedDocumentLiteralSoapBindingSkeleton._myOperations.get("eliminazioneUtenteWDL")).add(_oper);
    }

    public GestioneUtentiWrappedDocumentLiteralSoapBindingSkeleton() {
        this.impl = new org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiWrappedDocumentLiteralSoapBindingImpl();
    }

    public GestioneUtentiWrappedDocumentLiteralSoapBindingSkeleton(org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiWrappedDocumentLiteral impl) {
        this.impl = impl;
    }
    @Override
	public org.openspcoop2.ValidazioneContenutiWS.Service.types.esempio1.RegistrazioneUtenteWDLResponseType registrazioneUtenteWDL(org.openspcoop2.ValidazioneContenutiWS.Service.types.esempio1.RegistrazioneUtenteWDLRequestType argomentoWrapped) throws java.rmi.RemoteException
    {
        org.openspcoop2.ValidazioneContenutiWS.Service.types.esempio1.RegistrazioneUtenteWDLResponseType ret = this.impl.registrazioneUtenteWDL(argomentoWrapped);
        return ret;
    }

    @Override
	public org.openspcoop2.ValidazioneContenutiWS.Service.types.esempio1.EliminazioneUtenteWDLResponseType eliminazioneUtenteWDL(org.openspcoop2.ValidazioneContenutiWS.Service.types.esempio1.EliminazioneUtenteWDLRequestType argomentoWrapped) throws java.rmi.RemoteException
    {
        org.openspcoop2.ValidazioneContenutiWS.Service.types.esempio1.EliminazioneUtenteWDLResponseType ret = this.impl.eliminazioneUtenteWDL(argomentoWrapped);
        return ret;
    }

}
