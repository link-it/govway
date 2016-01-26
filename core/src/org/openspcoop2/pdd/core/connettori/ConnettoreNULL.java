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




package org.openspcoop2.pdd.core.connettori;

import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.protocol.sdk.Busta;


/**
 * Classe utilizzata per effettuare consegne di messaggi Soap, attraverso
 * l'invocazione di un server http. 
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ConnettoreNULL extends ConnettoreBase {

	/** Logger utilizzato per debug. */
	private ConnettoreLogger logger = null;
	
	public final static String LOCATION = "openspcoop2://dev/null";
    
	/** Proprieta' del connettore */
	private java.util.Hashtable<String,String> properties;
	
	/** Proprieta' urlBased che deve gestire il connettore */
	private java.util.Properties propertiesUrlBased;

	/** Busta */
	private Busta busta;
	
	/** Indicazione se siamo in modalita' debug */
	private boolean debug = false;

	/** Identificativo */
	private String idMessaggio;


	/* ********  METODI  ******** */

	/**
	 * Si occupa di effettuare la consegna.
	 *
	 * @param request Messaggio da Consegnare
	 * @return true in caso di consegna con successo, false altrimenti
	 * 
	 */
	@Override
	public boolean send(ConnettoreMsg request){

		if(request==null){
			this.errore = "Messaggio da consegnare is Null (ConnettoreMsg)";
			return false;
		}
		
		// Raccolta parametri per costruttore logger
		this.properties = request.getConnectorProperties();
		if(this.properties == null)
			this.errore = "Proprieta' del connettore non definite";
//		if(this.properties.size() == 0)
//			this.errore = "Proprieta' del connettore non definite";
		// - Busta
		this.busta = request.getBusta();
		if(this.busta!=null)
			this.idMessaggio=this.busta.getID();
		// - Debug mode
		if(this.properties.get(CostantiConnettori.CONNETTORE_DEBUG)!=null){
			if("true".equalsIgnoreCase(this.properties.get(CostantiConnettori.CONNETTORE_DEBUG).trim()))
				this.debug = true;
		}
	
		// Logger
		this.logger = new ConnettoreLogger(this.debug, this.idMessaggio, this.getPddContext());
				
		// Raccolta altri parametri
		
		// Context per invocazioni handler
		this.outRequestContext = request.getOutRequestContext();
		this.msgDiagnostico = request.getMsgDiagnostico();
		this.propertiesUrlBased = request.getPropertiesUrlBased();
		
		this.codice = 200;
		
		try{
			
			// SIMULAZIONE WRITE_TO
			
			org.apache.commons.io.output.NullOutputStream nullOutputStream = new org.apache.commons.io.output.NullOutputStream();
			request.getRequestMessage().writeTo(nullOutputStream,true);
			nullOutputStream.flush();
			nullOutputStream.close();
		
		}catch(Exception e){
			this.eccezioneProcessamento = e;
			this.logger.error("Riscontrato errore durante la writeTo",e);
			this.errore = "Riscontrato errore durante la writeTo: " +this.readExceptionMessageFromException(e);
			return false;
		}
		
		try{
			
			/* ------------  PostOutRequestHandler ------------- */
			this.postOutRequest();
			
		}catch(Exception e){
			this.eccezioneProcessamento = e;
			this.logger.error("Riscontrato errore durante la gestione PostOutRequestHandler",e);
			this.errore = "Riscontrato errore durante la gestione PostOutRequestHandler: " +this.readExceptionMessageFromException(e);
			return false;
		}
			
//		org.openspcoop2.utils.io.notifier.NotifierInputStreamParams notifierInputStreamParams = null;
//		try{
//			
//			/* ------------  PreInResponseHandler ------------- */
//			this.preInResponse();
//			
//			// Lettura risposta parametri NotifierInputStream per la risposta
//			if(this.preInResponseContext!=null){
//				notifierInputStreamParams = this.preInResponseContext.getNotifierInputStreamParams();
//			}
//			
//		}catch(Exception e){
//			this.eccezioneProcessamento = e;
//			ConnettoreNULL.log.error("Riscontrato errore durante la gestione PreInResponseHandler",e);
//			this.errore = "Riscontrato errore durante la gestione PreInResponseHandler: " +e.getMessage();
//			return false;
//		}
		
		try{
					
			this.responseMsg = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(request.getRequestMessage().getVersioneSoap());
			
		}catch(Exception e){
			this.eccezioneProcessamento = e;
			this.logger.error("Riscontrato errore durante la generazione di un msg SoapVuoto",e);
			this.errore = "Riscontrato errore durante la generazione di un msg SoapVuoto: " +this.readExceptionMessageFromException(e);
			return false;
		}
		
		return true;
	}

    /**
     * Ritorna l'informazione su dove il connettore sta spedendo il messaggio
     * 
     * @return location di inoltro del messaggio
     */
    @Override
	public String getLocation(){
		if(this.propertiesUrlBased != null && this.propertiesUrlBased.size()>0){
			return ConnettoreUtils.buildLocationWithURLBasedParameter(this.propertiesUrlBased, LOCATION);
		}
		else{
			return LOCATION;
		}
    }
    
}





