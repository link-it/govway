/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
 * GestioneUtentiOverloadedOperationsSoapBindingSkeleton.java
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
@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
public class GestioneUtentiOverloadedOperationsSoapBindingSkeleton implements org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiOverloadedOperations, org.apache.axis.wsdl.Skeleton {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6599047758005340843L;
	private org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiOverloadedOperations impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)GestioneUtentiOverloadedOperationsSoapBindingSkeleton._myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return GestioneUtentiOverloadedOperationsSoapBindingSkeleton._myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "nominativo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "indirizzo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "ora-registrazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"), java.util.Date.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("registrazioneUtenteOverloadedOperations", _params, new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "esito"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "registrazioneUtenteOverloadedOperations"));
        _oper.setSoapAction("registrazioneUtenteOverloadedOperations");
        GestioneUtentiOverloadedOperationsSoapBindingSkeleton._myOperationsList.add(_oper);
        if (GestioneUtentiOverloadedOperationsSoapBindingSkeleton._myOperations.get("registrazioneUtenteOverloadedOperations") == null) {
            GestioneUtentiOverloadedOperationsSoapBindingSkeleton._myOperations.put("registrazioneUtenteOverloadedOperations", new java.util.ArrayList());
        }
        ((java.util.List)GestioneUtentiOverloadedOperationsSoapBindingSkeleton._myOperations.get("registrazioneUtenteOverloadedOperations")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "nominativo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "indirizzo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("registrazioneUtenteOverloadedOperations", _params, new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "esito"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "registrazioneUtenteOverloadedOperations"));
        _oper.setSoapAction("registrazioneUtenteOverloadedOperations");
        GestioneUtentiOverloadedOperationsSoapBindingSkeleton._myOperationsList.add(_oper);
        if (GestioneUtentiOverloadedOperationsSoapBindingSkeleton._myOperations.get("registrazioneUtenteOverloadedOperations") == null) {
            GestioneUtentiOverloadedOperationsSoapBindingSkeleton._myOperations.put("registrazioneUtenteOverloadedOperations", new java.util.ArrayList());
        }
        ((java.util.List)GestioneUtentiOverloadedOperationsSoapBindingSkeleton._myOperations.get("registrazioneUtenteOverloadedOperations")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "nominativo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "ora-registrazione"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"), java.util.Date.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("registrazioneUtenteOverloadedOperations", _params, new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "esito"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://openspcoop2.org/ValidazioneContenutiWS/Service/types", "registrazioneUtenteOverloadedOperations"));
        _oper.setSoapAction("registrazioneUtenteOverloadedOperations");
        GestioneUtentiOverloadedOperationsSoapBindingSkeleton._myOperationsList.add(_oper);
        if (GestioneUtentiOverloadedOperationsSoapBindingSkeleton._myOperations.get("registrazioneUtenteOverloadedOperations") == null) {
            GestioneUtentiOverloadedOperationsSoapBindingSkeleton._myOperations.put("registrazioneUtenteOverloadedOperations", new java.util.ArrayList());
        }
        ((java.util.List)GestioneUtentiOverloadedOperationsSoapBindingSkeleton._myOperations.get("registrazioneUtenteOverloadedOperations")).add(_oper);
    }

    public GestioneUtentiOverloadedOperationsSoapBindingSkeleton() {
        this.impl = new org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiOverloadedOperationsSoapBindingImpl();
    }

    public GestioneUtentiOverloadedOperationsSoapBindingSkeleton(org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiOverloadedOperations impl) {
        this.impl = impl;
    }
    @Override
	public java.lang.String registrazioneUtenteOverloadedOperations(java.lang.String nominativoUtente, java.lang.String indirizzoUtente, java.util.Date oraRegistrazioneUtente) throws java.rmi.RemoteException
    {
        java.lang.String ret = this.impl.registrazioneUtenteOverloadedOperations(nominativoUtente, indirizzoUtente, oraRegistrazioneUtente);
        return ret;
    }

    @Override
	public java.lang.String registrazioneUtenteOverloadedOperations(java.lang.String nominativoUtente, java.lang.String indirizzoUtente) throws java.rmi.RemoteException
    {
        java.lang.String ret = this.impl.registrazioneUtenteOverloadedOperations(nominativoUtente, indirizzoUtente);
        return ret;
    }

    @Override
	public java.lang.String registrazioneUtenteOverloadedOperations(java.lang.String nominativoUtente, java.util.Date oraRegistrazioneUtente) throws java.rmi.RemoteException
    {
        java.lang.String ret = this.impl.registrazioneUtenteOverloadedOperations(nominativoUtente, oraRegistrazioneUtente);
        return ret;
    }

}
