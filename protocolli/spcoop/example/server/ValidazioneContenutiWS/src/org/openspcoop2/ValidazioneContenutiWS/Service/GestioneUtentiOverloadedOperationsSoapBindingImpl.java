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
 * GestioneUtentiOverloadedOperationsSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.openspcoop2.ValidazioneContenutiWS.Service;

import org.openspcoop2.ValidazioneContenutiWS.utilities.DatabaseComponent;
import org.openspcoop2.ValidazioneContenutiWS.utilities.IntegrationInfo;
import org.openspcoop2.ValidazioneContenutiWS.utilities.IntegrationInfoRepository;
import org.openspcoop2.ValidazioneContenutiWS.utilities.ValidazioneProperties;

/**
*
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class GestioneUtentiOverloadedOperationsSoapBindingImpl implements org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiOverloadedOperations{
	@Override
	public java.lang.String registrazioneUtenteOverloadedOperations(java.lang.String nominativoUtente, java.lang.String indirizzoUtente, java.util.Date oraRegistrazioneUtente) throws java.rmi.RemoteException {
		String ora = "N.D.";
		if(oraRegistrazioneUtente!=null)
			ora = oraRegistrazioneUtente.toString();

		System.out.println("registrazioneUtenteOverloadedOperations Signature1 nominativo(nome:"+nominativoUtente+") "
				+" [ora-registrazione:"+ora+"] indirizzo: "+indirizzoUtente);

		ValidazioneProperties valProp = ValidazioneProperties.getInstance();
		DatabaseComponent db = null;
		try{

			if(valProp.isTraceArrived()){

				// Informazioni di integrazione
				IntegrationInfo info = IntegrationInfoRepository.removeNext();
				if(info.getIdEGov()==null){
					System.out.println("registrazioneUtenteOverloadedOperations Signature1 nominativo(nome:"+nominativoUtente+") "
				+" [ora-registrazione:"+ora+"] indirizzo: indirizzoUtente: IDEGov letto dall'header di integrazione is null");
					return "KO";
				}
				if(info.getDestinatario()==null){
					System.out.println("registrazioneUtenteOverloadedOperations Signature1 nominativo(nome:"+nominativoUtente+") "
				+" [ora-registrazione:"+ora+"] indirizzo: Destinatario letto dall'header di integrazione is null");
					return "KO";
				}

				db = new DatabaseComponent(valProp.getDataSource(),valProp.getJNDIContext_DataSource());
				db.tracciaIsArrivedIntoDatabase(info.getIdEGov(),info.getDestinatario());
			}
		}catch(Exception e){
			System.out.println("registrazioneUtenteOverloadedOperations Signature1: trace into Tracciamento non riuscito: "+e.getMessage());
 			e.printStackTrace(System.out);
			return "KO";
		}finally{
			try{
				db.close();
			}catch(Exception e){}
		}	

		return "OK";
	}

	@Override
	public java.lang.String registrazioneUtenteOverloadedOperations(java.lang.String nominativoUtente, java.lang.String indirizzoUtente) throws java.rmi.RemoteException {
		System.out.println("registrazioneUtenteOverloadedOperations Signature2 nominativo(nome:"+nominativoUtente+") "
				+" indirizzo: "+indirizzoUtente);

		ValidazioneProperties valProp = ValidazioneProperties.getInstance();
		DatabaseComponent db = null;
		try{

			if(valProp.isTraceArrived()){

				// Informazioni di integrazione
				IntegrationInfo info = IntegrationInfoRepository.removeNext();
				if(info.getIdEGov()==null){
					System.out.println("registrazioneUtenteOverloadedOperations Signature2 nominativo(nome:"+nominativoUtente+") "
				+" indirizzo: IDEGov letto dall'header di integrazione is null");
					return "KO";
				}
				if(info.getDestinatario()==null){
					System.out.println("registrazioneUtenteOverloadedOperations Signature2 nominativo(nome:"+nominativoUtente+") "
				+" indirizzo: Destinatario letto dall'header di integrazione is null");
					return "KO";
				}

				db = new DatabaseComponent(valProp.getDataSource(),valProp.getJNDIContext_DataSource());
				db.tracciaIsArrivedIntoDatabase(info.getIdEGov(),info.getDestinatario());
			}
		}catch(Exception e){
			System.out.println("registrazioneUtenteOverloadedOperations Signature2: trace into Tracciamento non riuscito: "+e.getMessage());
 			e.printStackTrace(System.out);
			return "KO";
		}finally{
			try{
				db.close();
			}catch(Exception e){}
		}	

		return "OK";
	}

	@Override
	public java.lang.String registrazioneUtenteOverloadedOperations(java.lang.String nominativoUtente, java.util.Date oraRegistrazioneUtente) throws java.rmi.RemoteException {
		String ora = "N.D.";
		if(oraRegistrazioneUtente!=null)
			ora = oraRegistrazioneUtente.toString();

		System.out.println("registrazioneUtenteOverloadedOperations Signature3 nominativo(nome:"+nominativoUtente+") "
				+" [ora-registrazione:"+ora+"]");

		ValidazioneProperties valProp = ValidazioneProperties.getInstance();
		DatabaseComponent db = null;
		try{

			if(valProp.isTraceArrived()){

				// Informazioni di integrazione
				IntegrationInfo info = IntegrationInfoRepository.removeNext();
				if(info.getIdEGov()==null){
					System.out.println("registrazioneUtenteOverloadedOperations Signature3 nominativo(nome:"+nominativoUtente+") "
				+" [ora-registrazione: IDEGov letto dall'header di integrazione is null");
					return "KO";
				}
				if(info.getDestinatario()==null){
					System.out.println("registrazioneUtenteOverloadedOperations Signature3 nominativo(nome:"+nominativoUtente+") "
				+" [ora-registrazione: Destinatario letto dall'header di integrazione is null");
					return "KO";
				}

				db = new DatabaseComponent(valProp.getDataSource(),valProp.getJNDIContext_DataSource());
				db.tracciaIsArrivedIntoDatabase(info.getIdEGov(),info.getDestinatario());
			}
		}catch(Exception e){
			System.out.println("registrazioneUtenteOverloadedOperations Signature3: trace into Tracciamento non riuscito: "+e.getMessage());
 			e.printStackTrace(System.out);
			return "KO";
		}finally{
			try{
				db.close();
			}catch(Exception e){}
		}	

		return "OK";
	}

}
