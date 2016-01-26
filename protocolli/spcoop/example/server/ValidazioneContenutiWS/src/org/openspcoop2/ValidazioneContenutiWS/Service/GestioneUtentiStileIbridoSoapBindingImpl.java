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
 * GestioneUtentiStileIbridoSoapBindingImpl.java
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
public class GestioneUtentiStileIbridoSoapBindingImpl implements org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiStileIbrido{
	@Override
	public org.openspcoop2.ValidazioneContenutiWS.Service.types.esempio1.RegistrazioneUtenteWDLResponseType registrazioneUtenteWDL(org.openspcoop2.ValidazioneContenutiWS.Service.types.esempio1.RegistrazioneUtenteWDLRequestType argomentoWrapped) throws java.rmi.RemoteException {
		org.openspcoop2.ValidazioneContenutiWS.Service.types.esempio1.RegistrazioneUtenteWDLResponseType risposta =
			new org.openspcoop2.ValidazioneContenutiWS.Service.types.esempio1.RegistrazioneUtenteWDLResponseType();

		if(argomentoWrapped!=null){
			String ora = "N.D.";
			if(argomentoWrapped.getOraRegistrazione()!=null)
				ora = argomentoWrapped.getOraRegistrazione().toString();

			System.out.println("registrazioneUtenteWDL (IBRIDO) nominativo(nome:"+argomentoWrapped.getNominativo()+") "
					+" [ora-registrazione:"+ora+"] indirizzo: "+argomentoWrapped.getIndirizzo());

			risposta.setEsito("OK");
		}else{
			System.out.println("registrazioneUtenteWDL (IBRIDO): Argomento wrapped is null");
			risposta.setEsito("KO");
		}

		ValidazioneProperties valProp = ValidazioneProperties.getInstance();
 		DatabaseComponent db = null;
 		try{

 			if(valProp.isTraceArrived()){

 				// Informazioni di integrazione
 				IntegrationInfo info = IntegrationInfoRepository.removeNext();
 				if(info.getIdEGov()==null){
 					System.out.println("registrazioneUtenteWDL (IBRIDO): IDEGov letto dall'header di integrazione is null");
 					risposta.setEsito("KO");
 					return risposta;
 				}
 				if(info.getDestinatario()==null){
 					System.out.println("registrazioneUtenteWDL (IBRIDO): Destinatario letto dall'header di integrazione is null");
 					risposta.setEsito("KO");
 					return risposta;
 				}

 				db = new DatabaseComponent(valProp.getDataSource(),valProp.getJNDIContext_DataSource());
 				db.tracciaIsArrivedIntoDatabase(info.getIdEGov(),info.getDestinatario());
 			}
 		}catch(Exception e){
 			System.out.println("registrazioneUtenteWDL (IBRIDO): trace into Tracciamento non riuscito: "+e.getMessage());
 			e.printStackTrace(System.out);
 			risposta.setEsito("KO");
				return risposta;
 		}finally{
 			try{
 				db.close();
 			}catch(Exception e){}
 		}	
		
		return risposta;
	}

	@Override
	public java.lang.String registrazioneUtenteRPCL(java.lang.String nominativoUtente, java.lang.String indirizzoUtente, java.util.Date oraRegistrazioneUtente) throws java.rmi.RemoteException {
		String ora = "N.D.";
		if(oraRegistrazioneUtente!=null)
			ora = oraRegistrazioneUtente.toString();

		System.out.println("registrazioneUtenteRPCL (IBRIDO) nominativo(nome:"+nominativoUtente+") "
				+" [ora-registrazione:"+ora+"] indirizzo: "+indirizzoUtente);

		ValidazioneProperties valProp = ValidazioneProperties.getInstance();
 		DatabaseComponent db = null;
 		try{

 			if(valProp.isTraceArrived()){

 				// Informazioni di integrazione
 				IntegrationInfo info = IntegrationInfoRepository.removeNext();
 				if(info.getIdEGov()==null){
 					System.out.println("registrazioneUtenteRPCL (IBRIDO): IDEGov letto dall'header di integrazione is null");
 					return "KO";
 				}
 				if(info.getDestinatario()==null){
 					System.out.println("registrazioneUtenteRPCL (IBRIDO): Destinatario letto dall'header di integrazione is null");
 					return "KO";
 				}

 				db = new DatabaseComponent(valProp.getDataSource(),valProp.getJNDIContext_DataSource());
 				db.tracciaIsArrivedIntoDatabase(info.getIdEGov(),info.getDestinatario());
 			}
 		}catch(Exception e){
 			System.out.println("registrazioneUtenteRPCL (IBRIDO): trace into Tracciamento non riuscito: "+e.getMessage());
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
	public java.lang.String registrazioneUtenteRPCE(java.lang.String nominativoUtente, java.lang.String indirizzoUtente, java.util.Date oraRegistrazioneUtente) throws java.rmi.RemoteException {
		String ora = "N.D.";
		if(oraRegistrazioneUtente!=null)
			ora = oraRegistrazioneUtente.toString();

		System.out.println("registrazioneUtenteRPCE (IBRIDO) nominativo(nome:"+nominativoUtente+") "
				+" [ora-registrazione:"+ora+"] indirizzo: "+indirizzoUtente);

		ValidazioneProperties valProp = ValidazioneProperties.getInstance();
 		DatabaseComponent db = null;
 		try{

 			if(valProp.isTraceArrived()){

 				// Informazioni di integrazione
 				IntegrationInfo info = IntegrationInfoRepository.removeNext();
 				if(info.getIdEGov()==null){
 					System.out.println("registrazioneUtenteRPCE (IBRIDO): IDEGov letto dall'header di integrazione is null");
 					return "KO";
 				}
 				if(info.getDestinatario()==null){
 					System.out.println("registrazioneUtenteRPCE (IBRIDO): Destinatario letto dall'header di integrazione is null");
 					return "KO";
 				}

 				db = new DatabaseComponent(valProp.getDataSource(),valProp.getJNDIContext_DataSource());
 				db.tracciaIsArrivedIntoDatabase(info.getIdEGov(),info.getDestinatario());
 			}
 		}catch(Exception e){
 			System.out.println("registrazioneUtenteRPCE (IBRIDO): trace into Tracciamento non riuscito: "+e.getMessage());
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
