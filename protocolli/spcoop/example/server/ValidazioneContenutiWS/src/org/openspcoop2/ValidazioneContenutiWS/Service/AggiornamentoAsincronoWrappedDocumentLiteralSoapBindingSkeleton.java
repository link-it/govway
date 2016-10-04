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
 * AggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton.java
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
public class AggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton implements org.openspcoop2.ValidazioneContenutiWS.Service.AggiornamentoAsincronoWrappedDocumentLiteral, org.apache.axis.wsdl.Skeleton {
    /**
	 * 
	 */
	private static final long serialVersionUID = -9097932082140129790L;
	private org.openspcoop2.ValidazioneContenutiWS.Service.AggiornamentoAsincronoWrappedDocumentLiteral impl;
	private static java.util.Map _myOperations = new java.util.Hashtable();
	private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
	public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)AggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton._myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
	public static java.util.Collection getOperationDescs() {
        return AggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton._myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "richiestaAggiornamentoUtenteAsincronoSimmetricoWDLRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", ">richiestaAggiornamentoUtenteAsincronoSimmetricoWDLRequest"), org.openspcoop2.ValidazioneContenutiWS.Service.types.RichiestaAggiornamentoUtenteAsincronoSimmetricoWDLRequest.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("richiestaAggiornamentoUtenteAsincronoSimmetricoWDL", _params, new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "richiestaAggiornamentoUtenteAsincronoSimmetricoWDLResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", ">richiestaAggiornamentoUtenteAsincronoSimmetricoWDLResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "richiestaAggiornamentoUtenteAsincronoSimmetricoWDL"));
        _oper.setSoapAction("richiestaAggiornamentoUtenteAsincronoSimmetricoWDL");
        AggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton._myOperationsList.add(_oper);
        if (AggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton._myOperations.get("richiestaAggiornamentoUtenteAsincronoSimmetricoWDL") == null) {
            AggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton._myOperations.put("richiestaAggiornamentoUtenteAsincronoSimmetricoWDL", new java.util.ArrayList());
        }
        ((java.util.List)AggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton._myOperations.get("richiestaAggiornamentoUtenteAsincronoSimmetricoWDL")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "richiestaAggiornamentoUtenteAsincronoAsimmetricoWDLRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", ">richiestaAggiornamentoUtenteAsincronoAsimmetricoWDLRequest"), org.openspcoop2.ValidazioneContenutiWS.Service.types.RichiestaAggiornamentoUtenteAsincronoAsimmetricoWDLRequest.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("richiestaAggiornamentoUtenteAsincronoAsimmetricoWDL", _params, new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "richiestaAggiornamentoUtenteAsincronoAsimmetricoWDLResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", ">richiestaAggiornamentoUtenteAsincronoAsimmetricoWDLResponse"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "richiestaAggiornamentoUtenteAsincronoAsimmetricoWDL"));
        _oper.setSoapAction("richiestaAggiornamentoUtenteAsincronoAsimmetricoWDL");
        AggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton._myOperationsList.add(_oper);
        if (AggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton._myOperations.get("richiestaAggiornamentoUtenteAsincronoAsimmetricoWDL") == null) {
            AggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton._myOperations.put("richiestaAggiornamentoUtenteAsincronoAsimmetricoWDL", new java.util.ArrayList());
        }
        ((java.util.List)AggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton._myOperations.get("richiestaAggiornamentoUtenteAsincronoAsimmetricoWDL")).add(_oper);
    }

    public AggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton() {
        this.impl = new org.openspcoop2.ValidazioneContenutiWS.Service.AggiornamentoAsincronoWrappedDocumentLiteralSoapBindingImpl();
    }

    public AggiornamentoAsincronoWrappedDocumentLiteralSoapBindingSkeleton(org.openspcoop2.ValidazioneContenutiWS.Service.AggiornamentoAsincronoWrappedDocumentLiteral impl) {
        this.impl = impl;
    }
	@Override
	public org.openspcoop2.ValidazioneContenutiWS.Service.types.RichiestaAggiornamentoUtenteAsincronoSimmetricoWDLResponse richiestaAggiornamentoUtenteAsincronoSimmetricoWDL(org.openspcoop2.ValidazioneContenutiWS.Service.types.RichiestaAggiornamentoUtenteAsincronoSimmetricoWDLRequest argomentoWrapped) throws java.rmi.RemoteException
    {
        org.openspcoop2.ValidazioneContenutiWS.Service.types.RichiestaAggiornamentoUtenteAsincronoSimmetricoWDLResponse ret = this.impl.richiestaAggiornamentoUtenteAsincronoSimmetricoWDL(argomentoWrapped);
        return ret;
    }

	@Override
	public org.openspcoop2.ValidazioneContenutiWS.Service.types.RichiestaAggiornamentoUtenteAsincronoAsimmetricoWDLResponse richiestaAggiornamentoUtenteAsincronoAsimmetricoWDL(org.openspcoop2.ValidazioneContenutiWS.Service.types.RichiestaAggiornamentoUtenteAsincronoAsimmetricoWDLRequest argomentoWrapped) throws java.rmi.RemoteException
    {
        org.openspcoop2.ValidazioneContenutiWS.Service.types.RichiestaAggiornamentoUtenteAsincronoAsimmetricoWDLResponse ret = this.impl.richiestaAggiornamentoUtenteAsincronoAsimmetricoWDL(argomentoWrapped);
        return ret;
    }

}
