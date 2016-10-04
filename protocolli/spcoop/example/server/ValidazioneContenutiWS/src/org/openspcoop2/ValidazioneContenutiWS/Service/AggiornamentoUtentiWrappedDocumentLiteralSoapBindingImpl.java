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
 * AggiornamentoUtentiWrappedDocumentLiteralSoapBindingImpl.java
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
public class AggiornamentoUtentiWrappedDocumentLiteralSoapBindingImpl implements org.openspcoop2.ValidazioneContenutiWS.Service.AggiornamentoUtentiWrappedDocumentLiteral{
	@Override
	public void notificaAggiornamentoUtenteWDL(org.openspcoop2.ValidazioneContenutiWS.Service.types.NotificaAggiornamentoUtenteWDLRequest argomentoWrapped) throws java.rmi.RemoteException {
		if(argomentoWrapped!=null){
			String nomePrecedente = null;
			String valueNominativo = null;
			if(argomentoWrapped.getAggiornamentoNominativo()!=null){
				nomePrecedente = argomentoWrapped.getAggiornamentoNominativo().getNomePrecedente();
				valueNominativo = argomentoWrapped.getAggiornamentoNominativo().get_value();
			}

			System.out.println("notificaAggiornamentoUtenteWDL nominativo(nomePrecedente:"+nomePrecedente+" nome:"+valueNominativo+") "
					+"indirizzo: "+argomentoWrapped.getIndirizzo());

		}else{
			System.out.println("notificaAggiornamentoUtenteWDL: Argomento wrapped is null");
		}

		ValidazioneProperties valProp = ValidazioneProperties.getInstance();
		DatabaseComponent db = null;
		try{

			if(valProp.isTraceArrived()){

				// Informazioni di integrazione
				IntegrationInfo info = IntegrationInfoRepository.removeNext();
				if(info.getIdEGov()==null){
					System.out.println("notificaAggiornamentoUtenteWDL: IDEGov letto dall'header di integrazione is null");
				}
				if(info.getDestinatario()==null){
					System.out.println("notificaAggiornamentoUtenteWDL: Destinatario letto dall'header di integrazione is null");
				}
				
				db = new DatabaseComponent(valProp.getDataSource(),valProp.getJNDIContext_DataSource());
				db.tracciaIsArrivedIntoDatabase(info.getIdEGov(),info.getDestinatario());
			}
		}catch(Exception e){
			System.out.println("notificaAggiornamentoUtenteWDL: trace into Tracciamento non riuscito: "+e.getMessage());
 			e.printStackTrace(System.out);
		}finally{
			try{
				db.close();
			}catch(Exception e){}
		}
	}

	@Override
	public org.openspcoop2.ValidazioneContenutiWS.Service.types.AggiornamentoUtenteWDLResponse aggiornamentoUtenteWDL(org.openspcoop2.ValidazioneContenutiWS.Service.types.AggiornamentoUtenteWDLRequest argomentoWrapped) throws java.rmi.RemoteException {
		org.openspcoop2.ValidazioneContenutiWS.Service.types.AggiornamentoUtenteWDLResponse risposta =
			new org.openspcoop2.ValidazioneContenutiWS.Service.types.AggiornamentoUtenteWDLResponse();
		risposta.setOraRegistrazione(new java.util.Date());

		if(argomentoWrapped!=null){
			String nomePrecedente = null;
			String valueNominativo = null;
			if(argomentoWrapped.getAggiornamentoNominativo()!=null){
				nomePrecedente = argomentoWrapped.getAggiornamentoNominativo().getNomePrecedente();
				valueNominativo = argomentoWrapped.getAggiornamentoNominativo().get_value();
			}

			System.out.println("aggiornamentoUtenteWDL nominativo(nomePrecedente:"+nomePrecedente+" nome:"+valueNominativo+") "
					+"indirizzo: "+argomentoWrapped.getIndirizzo());

			risposta.setEsito("OK");
		}else{
			System.out.println("richiestaAggiornamentoUtenteAsincronoSimmetricoWDL: Argomento wrapped is null");
			risposta.setEsito("KO");
		}

		ValidazioneProperties valProp = ValidazioneProperties.getInstance();
		DatabaseComponent db = null;
		try{

			if(valProp.isTraceArrived()){

				// Informazioni di integrazione
				IntegrationInfo info = IntegrationInfoRepository.removeNext();
				if(info.getIdEGov()==null){
					System.out.println("aggiornamentoUtenteWDL: IDEGov letto dall'header di integrazione is null");
					risposta.setEsito("KO");
					return risposta;
				}
				if(info.getDestinatario()==null){
					System.out.println("aggiornamentoUtenteWDL: Destinatario letto dall'header di integrazione is null");
					risposta.setEsito("KO");
					return risposta;
				}
				
				db = new DatabaseComponent(valProp.getDataSource(),valProp.getJNDIContext_DataSource());
				db.tracciaIsArrivedIntoDatabase(info.getIdEGov(),info.getDestinatario());
			}
		}catch(Exception e){
			System.out.println("aggiornamentoUtenteWDL: trace into Tracciamento non riuscito: "+e.getMessage());
 			e.printStackTrace(System.out);
			risposta.setEsito("KO");
		}finally{
			try{
				db.close();
			}catch(Exception e){}
		}
		
		return risposta;
	}

}
