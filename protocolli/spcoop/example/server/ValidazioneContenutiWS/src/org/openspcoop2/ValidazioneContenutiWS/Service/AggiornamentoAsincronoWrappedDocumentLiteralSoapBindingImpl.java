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
 * AggiornamentoAsincronoWrappedDocumentLiteralSoapBindingImpl.java
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
public class AggiornamentoAsincronoWrappedDocumentLiteralSoapBindingImpl implements org.openspcoop2.ValidazioneContenutiWS.Service.AggiornamentoAsincronoWrappedDocumentLiteral{
	@Override
	public org.openspcoop2.ValidazioneContenutiWS.Service.types.RichiestaAggiornamentoUtenteAsincronoSimmetricoWDLResponse richiestaAggiornamentoUtenteAsincronoSimmetricoWDL(org.openspcoop2.ValidazioneContenutiWS.Service.types.RichiestaAggiornamentoUtenteAsincronoSimmetricoWDLRequest argomentoWrapped) throws java.rmi.RemoteException {
		org.openspcoop2.ValidazioneContenutiWS.Service.types.RichiestaAggiornamentoUtenteAsincronoSimmetricoWDLResponse risposta =
			new org.openspcoop2.ValidazioneContenutiWS.Service.types.RichiestaAggiornamentoUtenteAsincronoSimmetricoWDLResponse();


		if(argomentoWrapped!=null){
			String ora = "N.D.";
			if(argomentoWrapped.getOraRegistrazione()!=null)
				ora = argomentoWrapped.getOraRegistrazione().toString();

			System.out.println("richiestaAggiornamentoUtenteAsincronoSimmetricoWDL nominativo(nome:"+argomentoWrapped.getNominativo()+") "
					+" [ora-registrazione:"+ora+"] indirizzo: "+argomentoWrapped.getIndirizzo());

			risposta.setAckRichiestaAsincrona("OK");
		}else{
			System.out.println("richiestaAggiornamentoUtenteAsincronoSimmetricoWDL: Argomento wrapped is null");
			risposta.setAckRichiestaAsincrona("KO");
		}


		ValidazioneProperties valProp = ValidazioneProperties.getInstance();
		DatabaseComponent db = null;
		try{

			if(valProp.isTraceArrived()){

				// Informazioni di integrazione
				IntegrationInfo info = IntegrationInfoRepository.removeNext();
				if(info.getIdEGov()==null){
					System.out.println("richiestaAggiornamentoUtenteAsincronoSimmetricoWDL: IDEGov letto dall'header di integrazione is null");
					risposta.setAckRichiestaAsincrona("KO");
					return risposta;
				}
				if(info.getDestinatario()==null){
					System.out.println("richiestaAggiornamentoUtenteAsincronoSimmetricoWDL: Destinatario letto dall'header di integrazione is null");
					risposta.setAckRichiestaAsincrona("KO");
					return risposta;
				}
				
				db = new DatabaseComponent(valProp.getDataSource(),valProp.getJNDIContext_DataSource());
				db.tracciaIsArrivedIntoDatabase(info.getIdEGov(),info.getDestinatario());
			}
		}catch(Exception e){
			System.out.println("richiestaAggiornamentoUtenteAsincronoSimmetricoWDL: trace into Tracciamento non riuscito: "+e.getMessage());
 			e.printStackTrace(System.out);
			risposta.setAckRichiestaAsincrona("KO");
		}finally{
			try{
				db.close();
			}catch(Exception e){}
		}


		return risposta;

	}

	@Override
	public org.openspcoop2.ValidazioneContenutiWS.Service.types.RichiestaAggiornamentoUtenteAsincronoAsimmetricoWDLResponse richiestaAggiornamentoUtenteAsincronoAsimmetricoWDL(org.openspcoop2.ValidazioneContenutiWS.Service.types.RichiestaAggiornamentoUtenteAsincronoAsimmetricoWDLRequest argomentoWrapped) throws java.rmi.RemoteException {
		org.openspcoop2.ValidazioneContenutiWS.Service.types.RichiestaAggiornamentoUtenteAsincronoAsimmetricoWDLResponse risposta =
			new org.openspcoop2.ValidazioneContenutiWS.Service.types.RichiestaAggiornamentoUtenteAsincronoAsimmetricoWDLResponse();


		if(argomentoWrapped!=null){
			String ora = "N.D.";
			if(argomentoWrapped.getOraRegistrazione()!=null)
				ora = argomentoWrapped.getOraRegistrazione().toString();

			System.out.println("richiestaAggiornamentoUtenteAsincronoAsimmetricoWDL nominativo(nome:"+argomentoWrapped.getNominativo()+") "
					+" [ora-registrazione:"+ora+"] indirizzo: "+argomentoWrapped.getIndirizzo());

			risposta.setAckRichiestaAsincrona("OK");
		}else{
			System.out.println("richiestaAggiornamentoUtenteAsincronoAsimmetricoWDL: Argomento wrapped is null");
			risposta.setAckRichiestaAsincrona("KO");
		}

		ValidazioneProperties valProp = ValidazioneProperties.getInstance();
		DatabaseComponent db = null;
		try{

			if(valProp.isTraceArrived()){

				// Informazioni di integrazione
				IntegrationInfo info = IntegrationInfoRepository.removeNext();
				if(info.getIdEGov()==null){
					System.out.println("richiestaAggiornamentoUtenteAsincronoAsimmetricoWDL: IDEGov letto dall'header di integrazione is null");
					risposta.setAckRichiestaAsincrona("KO");
					return risposta;
				}
				if(info.getDestinatario()==null){
					System.out.println("richiestaAggiornamentoUtenteAsincronoAsimmetricoWDL: Destinatario letto dall'header di integrazione is null");
					risposta.setAckRichiestaAsincrona("KO");
					return risposta;
				}
				
				db = new DatabaseComponent(valProp.getDataSource(),valProp.getJNDIContext_DataSource());
				db.tracciaIsArrivedIntoDatabase(info.getIdEGov(),info.getDestinatario());
			}
		}catch(Exception e){
			System.out.println("richiestaAggiornamentoUtenteAsincronoAsimmetricoWDL: trace into Tracciamento non riuscito: "+e.getMessage());
 			e.printStackTrace(System.out);
			risposta.setAckRichiestaAsincrona("KO");
		}finally{
			try{
				db.close();
			}catch(Exception e){}
		}
		
		return risposta;

	}

}
