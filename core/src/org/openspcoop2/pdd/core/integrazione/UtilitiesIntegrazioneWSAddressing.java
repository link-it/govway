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



package org.openspcoop2.pdd.core.integrazione;

import java.util.Iterator;

import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPHeader;
import jakarta.xml.soap.SOAPHeaderElement;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.soap.wsaddressing.Costanti;
import org.openspcoop2.message.soap.wsaddressing.WSAddressingHeader;
import org.openspcoop2.message.soap.wsaddressing.WSAddressingUtilities;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.slf4j.Logger;


/**
 * Classe contenenti utilities per le integrazioni.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UtilitiesIntegrazioneWSAddressing {

	// ***** STATIC *****
	
	/**
	   SOAPHeaderElement wsaTO        ->     http://<providerType>_<provider>.govway.org/services/<serviceType>_<service>/<serviceVersion>
	   SOAPHeaderElement wsaFROM      ->     http://[<application>.]<senderType>_<sender>.govway.org
	   SOAPHeaderElement wsaAction    ->     http://<providerType>_<provider>.govway.org/services/<serviceType>_<service>/<serviceVersion>/<action>
	   SOAPHeaderElement wsaID        ->     uuid:<messageId> in caso di Messaggio di Protocollo (restituzione di una risposto lato PD o in caso di consegna tramite PA
										     uuid:<applicationMessageId> in caso di Messaggio di Integrazione (invocazione lato PD o lettura risposta lato PA, es. per correlazione applicativa)
	   SOAPHeaderElement wsaRelatesTo ->     uuid:<messageId> equivale al riferimento messaggio
	 */
	private static final String WSA_TO_FORMAT = "http://<providerType>_<provider>.govway.org/services/<serviceType>_<service>/<serviceVersion>";
	private static final String WSA_FROM_FORMAT = "http://[<application>.]<senderType>_<sender>.govway.org";
	private static final String WSA_ACTION_FORMAT = "http://<providerType>_<provider>.govway.org/services/<serviceType>_<service>/<serviceVersion>/<action>";
	private static final String WSA_ID_FORMAT = "uuid:<id>";
	private static final String WSA_RELATES_TO_FORMAT = "uuid:<id>";
	
	public static final boolean INTERPRETA_COME_ID_BUSTA = true;
	public static final boolean INTERPRETA_COME_ID_APPLICATIVO = false;
	
	private static final boolean MUST_UNDERSTAND = false;
	
	
	public static void _readDatiWSAToOrAction(String wsaValue,String format, HeaderIntegrazione integrazione) throws HeaderIntegrazioneException{
		
		String type = null;
		boolean action = false;
		if(WSA_TO_FORMAT.equals(format)){
			type = "WSAddressingTo";	
		}
		else if(WSA_ACTION_FORMAT.equals(format)){
			type = "WSAddressingAction";	
			action = true;
		}
		else{
			throw new HeaderIntegrazioneException("Format ["+format+"] Not Supported");
		}
		
		if(wsaValue==null)
			throw new HeaderIntegrazioneException(type+" value is null");
		wsaValue = wsaValue.trim();
		if(wsaValue.startsWith(org.openspcoop2.utils.Costanti.PROTOCOL_HTTP_PREFIX)==false)
			throw new HeaderIntegrazioneException(type+" Value is not valid: "+format);
		if(wsaValue.contains(".govway.org/services/")==false)
			throw new HeaderIntegrazioneException(type+" Value is not valid: "+format);
		
		wsaValue = wsaValue.substring(7, wsaValue.length());
		
		// soggetto
		int indexSoggetto = wsaValue.indexOf(".govway.org/services/");
		String soggetto = wsaValue.substring(0, indexSoggetto);
		if(soggetto==null){
			throw new HeaderIntegrazioneException(type+" Value is not valid (Soggetto non identificabile): "+format);
		}
		if(soggetto.contains("_")==false){
			throw new HeaderIntegrazioneException(type+" Value is not valid (Soggetto non identificabile, '_' non trovato ): "+format);
		}
		String [] soggetto_split = soggetto.split("_");
		if(soggetto_split==null || soggetto_split.length<2){
			throw new HeaderIntegrazioneException(type+" Value is not valid (Soggetto non identificabile, formato errato ): "+format);
		}
		
		String tipoSoggetto = soggetto_split[0];
		if(tipoSoggetto!=null){
			tipoSoggetto = tipoSoggetto.trim();
		}
		if(tipoSoggetto==null || "".equals(tipoSoggetto)){
			throw new HeaderIntegrazioneException(type+" Value is not valid (TipoSoggetto non identificabile): "+format);
		}
		
		String nomeSoggetto = soggetto.substring((tipoSoggetto+"_").length());
		if(nomeSoggetto!=null){
			nomeSoggetto = nomeSoggetto.trim();
		}
		if(nomeSoggetto==null || "".equals(nomeSoggetto)){
			throw new HeaderIntegrazioneException(type+" Value is not valid (NomeSoggetto non identificabile): "+format);
		}

		if(!action){
			integrazione.getBusta().setTipoDestinatario(tipoSoggetto);
			integrazione.getBusta().setDestinatario(nomeSoggetto);
		}
		
		// servizio
		String servizio = wsaValue.substring((indexSoggetto+".govway.org/services/".length()), wsaValue.length());
		if(servizio==null){
			throw new HeaderIntegrazioneException(type+" Value is not valid (Servizio non identificabile): "+format);
		}
		if(servizio.contains("_")==false){
			throw new HeaderIntegrazioneException(type+" Value is not valid (Servizio non identificabile, '_' non trovato ): "+format);
		}
		String [] servizio_split = servizio.split("_");
		if(servizio_split==null || servizio_split.length<2){
			throw new HeaderIntegrazioneException(type+" Value is not valid (Servizio non identificabile, formato errato ): "+format);
		}
		String tipoServizio = servizio_split[0];
		if(tipoServizio!=null){
			tipoServizio = tipoServizio.trim();
		}
		if(tipoServizio==null || "".equals(tipoServizio)){
			throw new HeaderIntegrazioneException(type+" Value is not valid (TipoServizio non identificabile): "+format);
		}
		
		String nomeVersioneServizio = servizio.substring((tipoServizio+"_").length());
		if(nomeVersioneServizio!=null){
			nomeVersioneServizio = nomeVersioneServizio.trim();
		}
		if(nomeVersioneServizio==null || "".equals(nomeVersioneServizio)){
			throw new HeaderIntegrazioneException(type+" Value is not valid (Nome e VersioneServizio non identificabile): "+format);
		}
		if(nomeVersioneServizio.contains("/")==false){
			throw new HeaderIntegrazioneException(type+" Value is not valid (Nome e VersioneServizio non identificabile, '/' non trovato ): "+format);
		}
		
		if(action){
			// gli slash sono due
			// nomeVersioneServizio contiene anche l'azione
			
			int indexAzione = nomeVersioneServizio.lastIndexOf("/");
			if(indexAzione<=0){
				throw new HeaderIntegrazioneException(type+" Value is not valid (Azione non identificabile, '/' non trovato, index("+indexAzione+") ): "+format);
			}
			
			String azione = nomeVersioneServizio.substring(indexAzione+1);
			if(azione!=null){
				azione = azione.trim();
			}
			if(azione==null  || "".equals(azione)){
				throw new HeaderIntegrazioneException(type+" Value is not valid (Azione non identificabile): "+format);
			}
			
			integrazione.getBusta().setAzione(azione);
			
			nomeVersioneServizio =  nomeVersioneServizio.substring(0, indexAzione);
			if(nomeVersioneServizio!=null){
				nomeVersioneServizio = nomeVersioneServizio.trim();
			}
			if(nomeVersioneServizio==null || "".equals(nomeVersioneServizio)){
				throw new HeaderIntegrazioneException(type+" Value is not valid (Nome e VersioneServizio non identificabile non identificabile prima dell'Azione): "+format);
			}
		}
		
			
		int indexVersioneServizio = nomeVersioneServizio.lastIndexOf("/");
		if(indexVersioneServizio<=0){
			throw new HeaderIntegrazioneException(type+" Value is not valid (VersioneServizio non identificabile, '/' non trovato, index("+indexVersioneServizio+") ): "+format);
		}
		
		String nomeServizio = nomeVersioneServizio.substring(0, indexVersioneServizio);
		if(nomeServizio!=null){
			nomeServizio = nomeServizio.trim();
		}
		if(nomeServizio==null || "".equals(nomeServizio)){
			throw new HeaderIntegrazioneException(type+" Value is not valid (NomeServizio non identificabile): "+format);
		}
		
		String tmpVersioneServizio = nomeVersioneServizio.substring(indexVersioneServizio+1);
		if(tmpVersioneServizio!=null){
			tmpVersioneServizio = tmpVersioneServizio.trim();
		}
		if(tmpVersioneServizio==null  || "".equals(tmpVersioneServizio)){
			throw new HeaderIntegrazioneException(type+" Value is not valid (VersioneServizio non identificabile): "+format);
		}
		Integer versioneServizio = null;
		try{
			versioneServizio = Integer.parseInt(tmpVersioneServizio);
		}catch(Exception e){
			throw new HeaderIntegrazioneException(type+" Value is not valid (VersioneServizio non identificabile, formato errato "+e.getMessage()+"): "+format);
		}
		
		if(!action){
			integrazione.getBusta().setTipoServizio(tipoServizio);
			integrazione.getBusta().setServizio(nomeServizio);
			integrazione.getBusta().setVersioneServizio(versioneServizio);
		}
		
	}
	
	
	public static void readDatiWSATo(String wsaTO,HeaderIntegrazione integrazione) throws HeaderIntegrazioneException{
		_readDatiWSAToOrAction(wsaTO, WSA_TO_FORMAT, integrazione);
	}
	public static String buildDatiWSATo(String tipoSoggettoErogatore,String nomeSoggettoErogatore,String tipoServizio,String nomeServizio,Integer versioneServizio){
		return org.openspcoop2.utils.Costanti.PROTOCOL_HTTP_PREFIX+tipoSoggettoErogatore+"_"+nomeSoggettoErogatore+".govway.org/services/"+tipoServizio+"_"+nomeServizio+"/"+versioneServizio;
	}
	public static SOAPHeaderElement buildWSATo(OpenSPCoop2SoapMessage msg,String actor,String tipoSoggettoErogatore,String nomeSoggettoErogatore,String tipoServizio,String nomeServizio,Integer versioneServizio) throws Exception{
		return WSAddressingUtilities.buildWSATo(msg, actor, MUST_UNDERSTAND,
				UtilitiesIntegrazioneWSAddressing.buildDatiWSATo(tipoSoggettoErogatore,nomeSoggettoErogatore,tipoServizio,nomeServizio,versioneServizio));
	}
	
	
	public static void readDatiWSAFrom(String wsaFrom,HeaderIntegrazione integrazione) throws HeaderIntegrazioneException{
		if(wsaFrom==null)
			throw new HeaderIntegrazioneException("WSAFrom value is null");
		wsaFrom = wsaFrom.trim();
		if(wsaFrom.startsWith(org.openspcoop2.utils.Costanti.PROTOCOL_HTTP_PREFIX)==false)
			throw new HeaderIntegrazioneException("WSAFrom Value is not valid: "+WSA_FROM_FORMAT);
		if(wsaFrom.contains(".govway.org")==false)
			throw new HeaderIntegrazioneException("WSAFrom Value is not valid: "+WSA_FROM_FORMAT);
		
		wsaFrom = wsaFrom.substring(7, wsaFrom.length());
		
		int indexSoggetto = wsaFrom.indexOf(".govway.org");
		String soggetto = wsaFrom.substring(0, indexSoggetto);
		if(soggetto==null){
			throw new HeaderIntegrazioneException("WSAFrom Value is not valid (Soggetto non identificabile): "+WSA_FROM_FORMAT);
		}	
		if(soggetto.contains("_")==false){
			throw new HeaderIntegrazioneException("WSAFrom Value is not valid (Soggetto non identificabile, '_' non trovato ): "+WSA_FROM_FORMAT);
		}
		String [] soggetto_split = soggetto.split("_");
		if(soggetto_split==null || soggetto_split.length<2){
			throw new HeaderIntegrazioneException("WSAFrom Value is not valid (Soggetto non identificabile, formato errato ): "+WSA_FROM_FORMAT);
		}
		
		String tipoSoggetto = soggetto_split[0];
		if(tipoSoggetto!=null){
			tipoSoggetto = tipoSoggetto.trim();
		}
		if(tipoSoggetto==null || "".equals(tipoSoggetto)){
			throw new HeaderIntegrazioneException("WSAFrom Value is not valid (TipoSoggetto non identificabile): "+WSA_FROM_FORMAT);
		}
		
		String nomeSoggetto = soggetto.substring((tipoSoggetto+"_").length());
		if(nomeSoggetto!=null){
			nomeSoggetto = nomeSoggetto.trim();
		}
		if(nomeSoggetto==null || "".equals(nomeSoggetto)){
			throw new HeaderIntegrazioneException("WSAFrom Value is not valid (NomeSoggetto non identificabile): "+WSA_FROM_FORMAT);
		}
		
		if(tipoSoggetto.contains(".")){
			String[] split = tipoSoggetto.split("\\.");
			tipoSoggetto = split[1];
			String sa = split[0];
			if(tipoSoggetto==null){
				throw new HeaderIntegrazioneException("WSAFrom Value is not valid (TipoSoggetto non identificabile dopo parsing ServizioApplicativo): "+WSA_FROM_FORMAT);
			}	
			if(sa==null){
				throw new HeaderIntegrazioneException("WSAFrom Value is not valid (ServizioApplicativo non identificabile): "+WSA_FROM_FORMAT);
			}
			tipoSoggetto = tipoSoggetto.trim();
			sa = sa.trim();
			integrazione.setServizioApplicativo(sa);
		}
		
		integrazione.getBusta().setTipoMittente(tipoSoggetto);
		integrazione.getBusta().setMittente(nomeSoggetto);
	}
	public static String buildDatiWSAFrom(String servizioApplicativoFruitore,String tipoSoggetto,String nomeSoggetto){
		if(servizioApplicativoFruitore==null)
			return org.openspcoop2.utils.Costanti.PROTOCOL_HTTP_PREFIX+tipoSoggetto+"_"+nomeSoggetto+".govway.org";
		else
			return org.openspcoop2.utils.Costanti.PROTOCOL_HTTP_PREFIX+servizioApplicativoFruitore+"."+tipoSoggetto+"_"+nomeSoggetto+".govway.org";
	}
	public static SOAPHeaderElement buildWSAFrom(OpenSPCoop2SoapMessage msg,String actor,String servizioApplicativoFruitore,String tipoSoggetto,String nomeSoggetto) throws Exception{
		return WSAddressingUtilities.buildWSAFrom(msg, actor, MUST_UNDERSTAND,
				UtilitiesIntegrazioneWSAddressing.buildDatiWSAFrom(servizioApplicativoFruitore,tipoSoggetto,nomeSoggetto));
	}
	
	
	public static void readDatiWSAAction(String wsaAction,HeaderIntegrazione integrazione) throws HeaderIntegrazioneException{	
		_readDatiWSAToOrAction(wsaAction, WSA_ACTION_FORMAT, integrazione);
	}
	public static String buildDatiWSAAction(String tipoSoggettoErogatore,String nomeSoggettoErogatore,String tipoServizio,String nomeServizio,Integer versioneServizio,String azione){
		return org.openspcoop2.utils.Costanti.PROTOCOL_HTTP_PREFIX+tipoSoggettoErogatore+"_"+nomeSoggettoErogatore+".govway.org/services/"+tipoServizio+"_"+nomeServizio+"/"+versioneServizio+"/"+azione;
	}
	public static SOAPHeaderElement buildWSAAction(OpenSPCoop2SoapMessage msg,String actor,String tipoSoggettoErogatore,String nomeSoggettoErogatore,String tipoServizio,String nomeServizio,Integer versioneServizio,String azione) throws Exception{
		return WSAddressingUtilities.buildWSAAction(msg, actor, MUST_UNDERSTAND,
				UtilitiesIntegrazioneWSAddressing.buildDatiWSAAction(tipoSoggettoErogatore,nomeSoggettoErogatore,tipoServizio,nomeServizio,versioneServizio,azione));
	}
	
	
	public static void readDatiWSAID(String wsaID,HeaderIntegrazione integrazione,boolean interpretaComeIDBusta) throws HeaderIntegrazioneException{
		if(wsaID==null)
			throw new HeaderIntegrazioneException("WSAID value is null");
		wsaID = wsaID.trim();
		if(wsaID.startsWith("uuid:")==false)
			throw new HeaderIntegrazioneException("WSAID Value is not valid: "+WSA_ID_FORMAT);
		
		wsaID = wsaID.substring(5, wsaID.length());
		
		if(interpretaComeIDBusta){
			integrazione.getBusta().setID(wsaID);
		}else{
			integrazione.setIdApplicativo(wsaID);
		}
	}
	public static String buildDatiWSAID(String id){
		return "uuid:"+id;
	}
	public static SOAPHeaderElement buildWSAID(OpenSPCoop2SoapMessage msg,String actor,String wsaID) throws Exception{
		return WSAddressingUtilities.buildWSAID(msg, actor, MUST_UNDERSTAND,
				UtilitiesIntegrazioneWSAddressing.buildDatiWSAID(wsaID));
	}
	
	
	public static void readDatiWSARelatesTo(String wsaRelatesTo,HeaderIntegrazione integrazione) throws HeaderIntegrazioneException{
		if(wsaRelatesTo==null)
			throw new HeaderIntegrazioneException("WSARelatesTo value is null");
		wsaRelatesTo = wsaRelatesTo.trim();
		if(wsaRelatesTo.startsWith("uuid:")==false)
			throw new HeaderIntegrazioneException("WSARelatesTo Value is not valid: "+WSA_RELATES_TO_FORMAT);
		
		wsaRelatesTo = wsaRelatesTo.substring(5, wsaRelatesTo.length());
		
		integrazione.getBusta().setRiferimentoMessaggio(wsaRelatesTo);
		
	}
	public static String buildDatiWSARelatesTo(String id){
		return "uuid:"+id;
	}
	public static SOAPHeaderElement buildWSARelatesTo(OpenSPCoop2SoapMessage msg,String actor, String id) throws Exception{
		return WSAddressingUtilities.buildWSARelatesTo(msg, actor, MUST_UNDERSTAND,
				UtilitiesIntegrazioneWSAddressing.buildDatiWSARelatesTo(id));
	}

	
	
	private static UtilitiesIntegrazioneWSAddressing utilitiesIntegrazione = null;
	public static UtilitiesIntegrazioneWSAddressing getInstance(Logger log){
		if(UtilitiesIntegrazioneWSAddressing.utilitiesIntegrazione==null){
			// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED': l'istanza viene creata allo startup
			synchronized (UtilitiesIntegrazioneWSAddressing.class) {
				UtilitiesIntegrazioneWSAddressing.initialize(log);
			}
		}
		return UtilitiesIntegrazioneWSAddressing.utilitiesIntegrazione;
	}

	private static synchronized void initialize(Logger log){
		if(UtilitiesIntegrazioneWSAddressing.utilitiesIntegrazione==null){
			UtilitiesIntegrazioneWSAddressing.utilitiesIntegrazione = new UtilitiesIntegrazioneWSAddressing(log);
		}
	}


	
	
	
	
	// ***** INSTANCE *****
	
	private UtilitiesIntegrazioneWSAddressing(Logger log){
		if(log!=null) {
			// unused
		}
	}
	
	public void readHeader(OpenSPCoop2SoapMessage message,HeaderIntegrazione integrazione,
			boolean interpretaIDComeIDBusta,String actorIntegrazione) throws HeaderIntegrazioneException{

		try{

			if(actorIntegrazione==null)
				throw new Exception("Actor non definito");
			
			Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
			
			SOAPHeader header = message.getSOAPHeader();
			if(header==null){
				OpenSPCoop2Logger.getLoggerOpenSPCoopCore().debug("SOAPHeader non presente");
				return;
			}
			
			SOAPHeaderElement wsaTO = null; 
			SOAPHeaderElement wsaFROM = null; 
			SOAPHeaderElement wsaAction = null; 
			SOAPHeaderElement wsaID = null; 
			SOAPHeaderElement wsaRelatesTo = null; 
			
			WSAddressingUtilities wsaddressingUtilities = new WSAddressingUtilities(log);
			WSAddressingHeader hdrReaded = wsaddressingUtilities.read(message, actorIntegrazione, false);
			if(hdrReaded!=null) {
				wsaTO = hdrReaded.getTo();
				wsaFROM = hdrReaded.getFrom();
				wsaAction = hdrReaded.getAction();
				wsaID = hdrReaded.getId();
				wsaRelatesTo = hdrReaded.getRelatesTo();
			}
			if(wsaTO==null && wsaFROM==null  &&  wsaAction==null && wsaID==null  &&  wsaRelatesTo==null){
				log.debug("Header di integrazione non presente");
				return;
			}
			
			// ValidazioneXSD
			log.debug("Validazione XSD...");
			wsaddressingUtilities.validate(message, hdrReaded);
			log.debug("Validazione XSD effettuate");
			
			// delete
			if(wsaTO!=null){
				log.debug("Read dati da WSATo...");
				UtilitiesIntegrazioneWSAddressing.readDatiWSATo(hdrReaded.getToValue(), integrazione);
			}
			if(wsaFROM!=null){
				log.debug("Read dati da WSAFrom...");
				UtilitiesIntegrazioneWSAddressing.readDatiWSAFrom(hdrReaded.getFromValue(), integrazione);
			}
			if(wsaAction!=null){
				log.debug("Read dati da WSAAction...");
				UtilitiesIntegrazioneWSAddressing.readDatiWSAAction(hdrReaded.getActionValue(), integrazione);
			}
			if(wsaID!=null){
				log.debug("Read dati da WSAId...");
				UtilitiesIntegrazioneWSAddressing.readDatiWSAID(hdrReaded.getIdValue(), integrazione, interpretaIDComeIDBusta);
			}	
			if(wsaRelatesTo!=null){
				log.debug("Read dati da WSARelatesTo...");
				UtilitiesIntegrazioneWSAddressing.readDatiWSARelatesTo(hdrReaded.getRelatesToValue(), integrazione);
			}
		
		}catch(Exception e){
			throw new HeaderIntegrazioneException("UtilitiesIntegrazione, lettura dell'header soap non riuscita: "+e.getMessage(),e);
		}
	}
	
	
	public void updateHeader(OpenSPCoop2SoapMessage message,IDSoggetto soggettoFruitore,IDServizio idServizio,
			String idBusta,String servizioApplicativo,
			String correlazioneApplicativa, String idTransazione,String actorIntegrazione) throws Exception{
		updateHeader(message, soggettoFruitore, idServizio, idBusta, null, 
				servizioApplicativo, correlazioneApplicativa, idTransazione, actorIntegrazione);
	}
	public void updateHeader(OpenSPCoop2SoapMessage message,IDSoggetto soggettoFruitore,IDServizio idServizio,
			String idBusta,String idBustaRisposta,String servizioApplicativo,
			String correlazioneApplicativa, String idTransazione,String actorIntegrazione) throws Exception{
		
		HeaderIntegrazione integrazione = new HeaderIntegrazione(idTransazione);
		integrazione.setIdApplicativo(correlazioneApplicativa);
		integrazione.setServizioApplicativo(servizioApplicativo);
		HeaderIntegrazioneBusta busta = new HeaderIntegrazioneBusta();
		busta.setTipoMittente(soggettoFruitore.getTipo());
		busta.setMittente(soggettoFruitore.getNome());
		busta.setTipoDestinatario(idServizio.getSoggettoErogatore().getTipo());
		busta.setDestinatario(idServizio.getSoggettoErogatore().getNome());
		busta.setTipoServizio(idServizio.getTipo());
		busta.setServizio(idServizio.getNome());
		busta.setVersioneServizio(idServizio.getVersione());
		busta.setAzione(idServizio.getAzione());
		if(idBustaRisposta==null){
			busta.setID(idBusta);
		}
		else{
			busta.setID(idBustaRisposta);
			busta.setRiferimentoMessaggio(idBusta);
		}
		integrazione.setBusta(busta);
		
		this.updateHeader(message, integrazione, actorIntegrazione);
	}
		
	public void updateHeader(OpenSPCoop2SoapMessage message,HeaderIntegrazione integrazione,String actorIntegrazione) throws Exception{
		
		SOAPHeader header = message.getSOAPHeader();
		
		SOAPHeaderElement wsaTO = null; 
		SOAPHeaderElement wsaFROM = null; 
		SOAPHeaderElement wsaAction = null; 
		SOAPHeaderElement wsaID = null; 
		SOAPHeaderElement wsaRelatesTo = null; 
		
		Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		
		if(header==null){
			
			// Creo soap header
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().debug("SOAPHeader non presente: add soapHeader");
			header = message.getSOAPPart().getEnvelope().addHeader();
			
		}else{

			// cerco soap di integrazione
			
			WSAddressingUtilities wsaddressingUtilities = new WSAddressingUtilities(log);
			WSAddressingHeader hdrReaded = wsaddressingUtilities.read(message, actorIntegrazione, false);
			if(hdrReaded!=null) {
				wsaTO = hdrReaded.getTo();
				wsaFROM = hdrReaded.getFrom();
				wsaAction = hdrReaded.getAction();
				wsaID = hdrReaded.getId();
				wsaRelatesTo = hdrReaded.getRelatesTo();
			}
			
		}

		if(integrazione.getBusta()!=null){
			
			HeaderIntegrazioneBusta hBusta = integrazione.getBusta();
				
			if(hBusta.getDestinatario()!=null && hBusta.getServizio()!=null){
				
				// To
				if(wsaTO!=null){
					// aggiorno
					wsaTO.setValue(UtilitiesIntegrazioneWSAddressing.buildDatiWSATo(hBusta.getTipoDestinatario(), hBusta.getDestinatario(), 
							hBusta.getTipoServizio() , hBusta.getServizio(), hBusta.getVersioneServizio()));
				}
				else{
					wsaTO = UtilitiesIntegrazioneWSAddressing.buildWSATo(message,actorIntegrazione,hBusta.getTipoDestinatario(), hBusta.getDestinatario(), 
							hBusta.getTipoServizio() , hBusta.getServizio(), hBusta.getVersioneServizio());
					//header.addChildElement(wsaTO);
					message.addHeaderElement(header, wsaTO);
				}
				
				
				// Action
				if(hBusta.getAzione()!=null){
					if(wsaAction!=null){
						// aggiorno
						wsaAction.setValue(UtilitiesIntegrazioneWSAddressing.buildDatiWSAAction(hBusta.getTipoDestinatario(), hBusta.getDestinatario(), 
								hBusta.getTipoServizio() , hBusta.getServizio(), hBusta.getVersioneServizio(), hBusta.getAzione()));
					}
					else{
						wsaAction = UtilitiesIntegrazioneWSAddressing.buildWSAAction(message,actorIntegrazione,hBusta.getTipoDestinatario(), hBusta.getDestinatario(), 
								hBusta.getTipoServizio() , hBusta.getServizio(), hBusta.getVersioneServizio(), hBusta.getAzione());
						//header.addChildElement(wsaTO);
						message.addHeaderElement(header, wsaTO);
					}
				}
			}
			
			if(hBusta.getMittente()!=null){
				if(wsaFROM!=null){
					// aggiorno
					Iterator<?> itFROM = wsaFROM.getChildElements();
					while (itFROM.hasNext()) {
						Object o = itFROM.next();
						if(o!=null && (o instanceof SOAPElement) ){
							SOAPElement s = (SOAPElement) o;
							if(Costanti.WSA_SOAP_HEADER_EPR_ADDRESS.equals(s.getLocalName())){
								s.setValue(buildDatiWSAFrom(integrazione.getServizioApplicativo(),hBusta.getTipoMittente(),hBusta.getMittente()));
								break;
							}
						}
					}
				}
				else{
					wsaFROM = UtilitiesIntegrazioneWSAddressing.buildWSAFrom(message,actorIntegrazione,integrazione.getServizioApplicativo(),hBusta.getTipoMittente(),hBusta.getMittente());
					//header.addChildElement(wsaFROM);
					message.addHeaderElement(header, wsaFROM);
				}
			}
				
			if(hBusta.getID()!=null){
				if(wsaID!=null){
					// aggiorno
					wsaID.setValue(UtilitiesIntegrazioneWSAddressing.buildDatiWSAID(hBusta.getID()));
				}
				else{
					wsaID = UtilitiesIntegrazioneWSAddressing.buildWSAID(message,actorIntegrazione,hBusta.getID());
					//header.addChildElement(wsaID);
					message.addHeaderElement(header, wsaID);
				}
			}
			
			if(hBusta.getRiferimentoMessaggio()!=null || hBusta.getIdCollaborazione()!=null){
				String rif = hBusta.getRiferimentoMessaggio();
				if(rif==null){
					rif = hBusta.getIdCollaborazione();
				}
				if(wsaRelatesTo!=null){
					// aggiorno
					wsaRelatesTo.setValue(UtilitiesIntegrazioneWSAddressing.buildDatiWSARelatesTo(rif));
				}
				else{
					wsaRelatesTo = UtilitiesIntegrazioneWSAddressing.buildWSARelatesTo(message,actorIntegrazione,rif);
					//header.addChildElement(wsaRelatesTo);
					message.addHeaderElement(header, wsaRelatesTo);
				}
			}
		}
	}

	
	public void deleteHeader(OpenSPCoop2SoapMessage message,String actorIntegrazione) throws HeaderIntegrazioneException{

		try{

			if(actorIntegrazione==null)
				throw new Exception("Actor non definito");
			
			Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
			
			SOAPHeader header = message.getSOAPHeader();
			if(header==null){
				OpenSPCoop2Logger.getLoggerOpenSPCoopCore().debug("SOAPHeader non presente");
				return;
			}
			
			WSAddressingUtilities wsaddressingUtilities = new WSAddressingUtilities(log);
			wsaddressingUtilities.delete(message, actorIntegrazione);
		
		}catch(Exception e){
			throw new HeaderIntegrazioneException("UtilitiesIntegrazione, lettura dell'header soap non riuscita: "+e.getMessage(),e);
		}
	}
}
