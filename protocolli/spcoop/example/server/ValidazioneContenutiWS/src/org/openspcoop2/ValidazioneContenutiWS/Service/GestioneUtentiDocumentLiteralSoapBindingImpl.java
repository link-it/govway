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
 * GestioneUtentiDocumentLiteralSoapBindingImpl.java
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
public class GestioneUtentiDocumentLiteralSoapBindingImpl implements org.openspcoop2.ValidazioneContenutiWS.Service.GestioneUtentiDocumentLiteral{
	@Override
	public java.lang.String registrazioneUtenteDL(java.lang.String nominativoUtente, java.lang.String indirizzoUtente, java.util.Date oraRegistrazioneUtente) throws java.rmi.RemoteException {
		String ora = "";
		if(oraRegistrazioneUtente!=null)
			ora = " [ora-registrazione:"+oraRegistrazioneUtente.toString()+"]";
		String indirizzo = "";
		if(indirizzoUtente!=null)
			indirizzo = " indirizzo: "+indirizzoUtente;

		System.out.println("registrazioneUtenteDL nominativo(nome:"+nominativoUtente+")"
				+ora+indirizzo);

		ValidazioneProperties valProp = ValidazioneProperties.getInstance();
		DatabaseComponent db = null;
		try{

			if(valProp.isTraceArrived()){

				// Informazioni di integrazione
				IntegrationInfo info = IntegrationInfoRepository.removeNext();
				if(info.getIdEGov()==null){
					System.out.println("registrazioneUtenteDL: IDEGov letto dall'header di integrazione is null");
					return "KO";
				}
				if(info.getDestinatario()==null){
					System.out.println("registrazioneUtenteDL: Destinatario letto dall'header di integrazione is null");
					return "KO";
				}

				db = new DatabaseComponent(valProp.getDataSource(),valProp.getJNDIContext_DataSource());
				db.tracciaIsArrivedIntoDatabase(info.getIdEGov(),info.getDestinatario());
			}
		}catch(Exception e){
			System.out.println("registrazioneUtenteDL: trace into Tracciamento non riuscito: "+e.getMessage());
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
	public java.lang.String eliminazioneUtenteDL(java.lang.String nominativoUtente) throws java.rmi.RemoteException {
		System.out.println("eliminazioneUtenteDL nominativo(nome:"+nominativoUtente+") ");

		ValidazioneProperties valProp = ValidazioneProperties.getInstance();
		DatabaseComponent db = null;
		try{

			if(valProp.isTraceArrived()){

				// Informazioni di integrazione
				IntegrationInfo info = IntegrationInfoRepository.removeNext();
				if(info.getIdEGov()==null){
					System.out.println("eliminazioneUtenteDL: IDEGov letto dall'header di integrazione is null");
					return "KO";
				}
				if(info.getDestinatario()==null){
					System.out.println("eliminazioneUtenteDL: Destinatario letto dall'header di integrazione is null");
					return "KO";
				}

				db = new DatabaseComponent(valProp.getDataSource(),valProp.getJNDIContext_DataSource());
				db.tracciaIsArrivedIntoDatabase(info.getIdEGov(),info.getDestinatario());
			}
		}catch(Exception e){
			System.out.println("eliminazioneUtenteDL: trace into Tracciamento non riuscito: "+e.getMessage());
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
