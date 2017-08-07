/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.xml.ValidatoreXSD;
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
	   SOAPHeaderElement wsaTO      ->     http://<tipoSoggettoErogatore>_<nomeSoggettoErogatore>.openspcoop2.org/servizi/<tipoServizio>_<nomeServizio>/<versioneServizio>
	   SOAPHeaderElement wsaFROM    ->     http://[<nomeServizioApplicativoFruitore>.]<tipoSoggettoFruitore>_<nomeSoggettoFruitore>.openspcoop2.org
	   SOAPHeaderElement wsaAction  ->     http://<tipoSoggettoErogatore>_<nomeSoggettoErogatore>.openspcoop2.org/servizi/<tipoServizio>_<nomeServizio>/<versioneServizio>/<nomeAzione>
	   SOAPHeaderElement wsaID      ->     uuid:<IDBusta> in caso di Messaggio di Protocollo (restituzione di una risposto lato PD o in caso di consegna tramite PA
										   uuid:<IDApplicativo> in caso di Messaggio di Integrazione (invocazione lato PD o lettura risposta lato PA, es. per correlazione applicativa)
	   SOAPHeaderElement wsaRelatesTo = null; // uuid:<IDBusta> equivale al riferimento messaggio

	 */
	private final static String WSA_TO_FORMAT = "http://<tipoSoggettoErogatore>_<nomeSoggettoErogatore>.openspcoop2.org/servizi/<tipoServizio>_<nomeServizio>/<versioneServizio>";
	private final static String WSA_FROM_FORMAT = "http://[<nomeServizioApplicativoFruitore>.]<tipoSoggettoFruitore>_<nomeSoggettoFruitore>.openspcoop2.org";
	private final static String WSA_ACTION_FORMAT = "http://<tipoSoggettoErogatore>_<nomeSoggettoErogatore>.openspcoop2.org/servizi/<tipoServizio>_<nomeServizio>/<versioneServizio>/<nomeAzione>";
	private final static String WSA_ID_FORMAT = "uuid:<id>";
	private final static String WSA_RELATES_TO_FORMAT = "uuid:<id>";
	
	public final static String WSA_NAMESPACE = "http://www.w3.org/2005/08/addressing";
	public final static String WSA_PREFIX = "wsa";
	public final static String WSA_RELATIONSHIP_TYPE_REPLY = "http://www.w3.org/2005/08/addressing/reply";
	public final static String WSA_SOAP_HEADER_TO = "To";
	public final static String WSA_SOAP_HEADER_FROM = "From";
	public final static String WSA_SOAP_HEADER_ACTION = "Action";
	public final static String WSA_SOAP_HEADER_ID = "MessageID";
	public final static String WSA_SOAP_HEADER_RELATES_TO = "RelatesTo";
	public final static String WSA_SOAP_HEADER_RELATES_TO_ATTRIBUTE = "RelationshipType";

	public final static String WSA_SOAP_HEADER_EPR_ADDRESS = "Address";
	
	public final static boolean INTERPRETA_COME_ID_BUSTA = true;
	public final static boolean INTERPRETA_COME_ID_APPLICATIVO = false;
	
	public final static boolean BUILD_VALUE_AS_EPR = true;
	public final static boolean BUILD_VALUE_RAW = false;
	
	
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
		if(wsaValue.startsWith("http://")==false)
			throw new HeaderIntegrazioneException(type+" Value is not valid: "+format);
		if(wsaValue.contains(".openspcoop2.org/servizi/")==false)
			throw new HeaderIntegrazioneException(type+" Value is not valid: "+format);
		
		wsaValue = wsaValue.substring(7, wsaValue.length());
		
		// soggetto
		int indexSoggetto = wsaValue.indexOf(".openspcoop2.org/servizi/");
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
		String servizio = wsaValue.substring((indexSoggetto+".openspcoop2.org/servizi/".length()), wsaValue.length());
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
		
		String tmpVersioneServizio = nomeServizio.substring(indexVersioneServizio+1);
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
		return "http://"+tipoSoggettoErogatore+"_"+nomeSoggettoErogatore+".openspcoop2.org/servizi/"+tipoServizio+"_"+nomeServizio+"/"+versioneServizio;
	}
	public static SOAPHeaderElement buildWSATo(OpenSPCoop2SoapMessage msg,String actor,String tipoSoggettoErogatore,String nomeSoggettoErogatore,String tipoServizio,String nomeServizio,Integer versioneServizio) throws Exception{
		QName name =  new QName(UtilitiesIntegrazioneWSAddressing.WSA_NAMESPACE,UtilitiesIntegrazioneWSAddressing.WSA_SOAP_HEADER_TO,UtilitiesIntegrazioneWSAddressing.WSA_PREFIX);
		SOAPHeaderElement header = UtilitiesIntegrazioneWSAddressing.buildHeaderElement(msg,name,
				UtilitiesIntegrazioneWSAddressing.buildDatiWSATo(tipoSoggettoErogatore,nomeSoggettoErogatore,tipoServizio,nomeServizio,versioneServizio),actor,BUILD_VALUE_RAW);
		return header;
	}
	
	
	public static void readDatiWSAFrom(String wsaFrom,HeaderIntegrazione integrazione) throws HeaderIntegrazioneException{
		if(wsaFrom==null)
			throw new HeaderIntegrazioneException("WSAFrom value is null");
		wsaFrom = wsaFrom.trim();
		if(wsaFrom.startsWith("http://")==false)
			throw new HeaderIntegrazioneException("WSAFrom Value is not valid: "+WSA_FROM_FORMAT);
		if(wsaFrom.contains(".openspcoop2.org")==false)
			throw new HeaderIntegrazioneException("WSAFrom Value is not valid: "+WSA_FROM_FORMAT);
		
		wsaFrom = wsaFrom.substring(7, wsaFrom.length());
		
		int indexSoggetto = wsaFrom.indexOf(".openspcoop2.org");
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
			String[] split = tipoSoggetto.split(".");
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
			return "http://"+tipoSoggetto+"_"+nomeSoggetto+".openspcoop2.org";
		else
			return "http://"+servizioApplicativoFruitore+"."+tipoSoggetto+"_"+nomeSoggetto+".openspcoop2.org";
	}
	public static SOAPHeaderElement buildWSAFrom(OpenSPCoop2SoapMessage msg,String actor,String servizioApplicativoFruitore,String tipoSoggetto,String nomeSoggetto) throws Exception{
		QName name =  new QName(UtilitiesIntegrazioneWSAddressing.WSA_NAMESPACE,UtilitiesIntegrazioneWSAddressing.WSA_SOAP_HEADER_FROM,UtilitiesIntegrazioneWSAddressing.WSA_PREFIX);
		SOAPHeaderElement header = UtilitiesIntegrazioneWSAddressing.buildHeaderElement(msg,name,UtilitiesIntegrazioneWSAddressing.buildDatiWSAFrom(servizioApplicativoFruitore,tipoSoggetto,nomeSoggetto),actor,BUILD_VALUE_AS_EPR);
		return header;
	}
	
	
	public static void readDatiWSAAction(String wsaAction,HeaderIntegrazione integrazione) throws HeaderIntegrazioneException{	
		_readDatiWSAToOrAction(wsaAction, WSA_ACTION_FORMAT, integrazione);
	}
	public static String buildDatiWSAAction(String tipoSoggettoErogatore,String nomeSoggettoErogatore,String tipoServizio,String nomeServizio,Integer versioneServizio,String azione){
		return "http://"+tipoSoggettoErogatore+"_"+nomeSoggettoErogatore+".openspcoop2.org/servizi/"+tipoServizio+"_"+nomeServizio+"/"+versioneServizio+"/"+azione;
	}
	public static SOAPHeaderElement buildWSAAction(OpenSPCoop2SoapMessage msg,String actor,String tipoSoggettoErogatore,String nomeSoggettoErogatore,String tipoServizio,String nomeServizio,Integer versioneServizio,String azione) throws Exception{
		QName name =  new QName(UtilitiesIntegrazioneWSAddressing.WSA_NAMESPACE,UtilitiesIntegrazioneWSAddressing.WSA_SOAP_HEADER_ACTION,UtilitiesIntegrazioneWSAddressing.WSA_PREFIX);
		SOAPHeaderElement header = UtilitiesIntegrazioneWSAddressing.buildHeaderElement(msg,name,UtilitiesIntegrazioneWSAddressing.buildDatiWSAAction(tipoSoggettoErogatore,nomeSoggettoErogatore,tipoServizio,nomeServizio,versioneServizio,azione),actor,BUILD_VALUE_RAW);
		return header;
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
		QName name =  new QName(UtilitiesIntegrazioneWSAddressing.WSA_NAMESPACE,UtilitiesIntegrazioneWSAddressing.WSA_SOAP_HEADER_ID,UtilitiesIntegrazioneWSAddressing.WSA_PREFIX);
		SOAPHeaderElement header = UtilitiesIntegrazioneWSAddressing.buildHeaderElement(msg,name,UtilitiesIntegrazioneWSAddressing.buildDatiWSAID(wsaID),actor,BUILD_VALUE_RAW);
		return header;
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
		QName name =  new QName(UtilitiesIntegrazioneWSAddressing.WSA_NAMESPACE,UtilitiesIntegrazioneWSAddressing.WSA_SOAP_HEADER_RELATES_TO,UtilitiesIntegrazioneWSAddressing.WSA_PREFIX);
		SOAPHeaderElement header = UtilitiesIntegrazioneWSAddressing.buildHeaderElement(msg,name,UtilitiesIntegrazioneWSAddressing.buildDatiWSARelatesTo(id),actor,BUILD_VALUE_RAW);
		return header;
	}
	
	private static SOAPHeaderElement buildHeaderElement(OpenSPCoop2SoapMessage msg,QName name,String value,String actor,boolean epr) throws Exception{
		
		SOAPHeader hdr = msg.getSOAPHeader();
		if(hdr==null){
			hdr = msg.getSOAPPart().getEnvelope().addHeader(); 
		}
		SOAPHeaderElement element = msg.newSOAPHeaderElement(hdr, name); 
		element.setActor(actor);
		element.setMustUnderstand(false);
		
		if(epr==false){
			element.setValue(value);
		}
		else{
			QName nameAddressEPR =  new QName(WSA_NAMESPACE,WSA_SOAP_HEADER_EPR_ADDRESS,WSA_PREFIX);
			element.addChildElement(nameAddressEPR).setValue(value);
		}
		return element;
		
	}
	
	
	
	private static UtilitiesIntegrazioneWSAddressing utilitiesIntegrazione = null;
	public static UtilitiesIntegrazioneWSAddressing getInstance(Logger log){
		if(UtilitiesIntegrazioneWSAddressing.utilitiesIntegrazione==null){
			UtilitiesIntegrazioneWSAddressing.initialize(log);
		}
		return UtilitiesIntegrazioneWSAddressing.utilitiesIntegrazione;
	}

	private static synchronized void initialize(Logger log){
		if(UtilitiesIntegrazioneWSAddressing.utilitiesIntegrazione==null){
			UtilitiesIntegrazioneWSAddressing.utilitiesIntegrazione = new UtilitiesIntegrazioneWSAddressing(log);
		}
	}


	
	
	
	
	// ***** INSTANCE *****
	
	private ValidatoreXSD validatoreXSD = null;
	
	private UtilitiesIntegrazioneWSAddressing(Logger log){
		try{
			this.validatoreXSD = new ValidatoreXSD(log,UtilitiesIntegrazione.class.getResourceAsStream("/ws-addr.xsd"));
		}catch(Exception e){
			log.error("ws-addr.xsd, errore durante la costruzione del validatore xsd: "+e.getMessage(),e);
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
			
			java.util.Iterator<?> it = header.examineAllHeaderElements();
			while( it.hasNext()  ){
				// Test Header Element
				SOAPHeaderElement headerElement = (SOAPHeaderElement) it.next();
				
				//Controllo Namespace
				String namespace = headerElement.getNamespaceURI();
				String actorCheck = SoapUtils.getSoapActor(headerElement, message.getMessageType());
				
				if(actorIntegrazione.equals(actorCheck) && UtilitiesIntegrazioneWSAddressing.WSA_NAMESPACE.equals(namespace)){
					log.debug("Trovato header WSAddressing ["+headerElement.getLocalName()+"]");
					
					if(UtilitiesIntegrazioneWSAddressing.WSA_SOAP_HEADER_TO.equals(headerElement.getLocalName())){
						wsaTO = headerElement;
					}
					else if(UtilitiesIntegrazioneWSAddressing.WSA_SOAP_HEADER_FROM.equals(headerElement.getLocalName())){
						wsaFROM = headerElement;
					}
					else if(UtilitiesIntegrazioneWSAddressing.WSA_SOAP_HEADER_ACTION.equals(headerElement.getLocalName())){
						wsaAction = headerElement;
					}
					else if(UtilitiesIntegrazioneWSAddressing.WSA_SOAP_HEADER_ID.equals(headerElement.getLocalName())){
						wsaID = headerElement;
					}
					else if(UtilitiesIntegrazioneWSAddressing.WSA_SOAP_HEADER_RELATES_TO.equals(headerElement.getLocalName())){
						wsaRelatesTo = headerElement;
					}
					else{
						log.debug("Header non conosciuto");
					}
				}
				
			}
			if(wsaTO==null && wsaFROM==null  &&  wsaAction==null && wsaID==null  &&  wsaRelatesTo==null){
				log.debug("Header di integrazione non presente");
				return;
			}
			
			// ValidazioneXSD
			log.debug("Validazione XSD...");
			validaElementoWSA(this.validatoreXSD,wsaTO,log,message);
			validaElementoWSA(this.validatoreXSD,wsaFROM,log,message);
			validaElementoWSA(this.validatoreXSD,wsaAction,log,message);
			validaElementoWSA(this.validatoreXSD,wsaID,log,message);
			validaElementoWSA(this.validatoreXSD,wsaRelatesTo,log,message);
			log.debug("Validazione XSD effettuate");
			
			// delete
			if(wsaTO!=null){
				log.debug("Read dati da WSATo...");
				UtilitiesIntegrazioneWSAddressing.readDatiWSATo(wsaTO.getValue(), integrazione);
			}
			if(wsaFROM!=null){
				log.debug("Read dati da WSAFrom...");
				Iterator<?> itFROM = wsaFROM.getChildElements();
				while (itFROM.hasNext()) {
					Object o = itFROM.next();
					if(o!=null && (o instanceof SOAPElement) ){
						SOAPElement s = (SOAPElement) o;
						if(WSA_SOAP_HEADER_EPR_ADDRESS.equals(s.getLocalName())){
							UtilitiesIntegrazioneWSAddressing.readDatiWSAFrom(s.getValue(), integrazione);
							break;
						}
					}
				}
			}
			if(wsaAction!=null){
				log.debug("Read dati da WSAAction...");
				UtilitiesIntegrazioneWSAddressing.readDatiWSAAction(wsaAction.getValue(), integrazione);
			}
			if(wsaID!=null){
				log.debug("Read dati da WSAId...");
				UtilitiesIntegrazioneWSAddressing.readDatiWSAID(wsaID.getValue(), integrazione, interpretaIDComeIDBusta);
			}	
			if(wsaRelatesTo!=null){
				log.debug("Read dati da WSARelatesTo...");
				UtilitiesIntegrazioneWSAddressing.readDatiWSARelatesTo(wsaRelatesTo.getValue(), integrazione);
			}
		
		}catch(Exception e){
			throw new HeaderIntegrazioneException("UtilitiesIntegrazione, lettura dell'header soap non riuscita: "+e.getMessage(),e);
		}
	}

	private void validaElementoWSA(ValidatoreXSD validatoreXSD,SOAPHeaderElement headerElement,Logger log,OpenSPCoop2SoapMessage msg) throws Exception{
		if(headerElement!=null){
			log.debug("Validazione XSD ["+headerElement.getLocalName()+"]...");
			// validazione XSD
			if(validatoreXSD==null)
				throw new Exception("Validatore XSD non istanziato");
			validatoreXSD.valida(new java.io.ByteArrayInputStream(msg.getAsByte(headerElement, false)));
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
			java.util.Iterator<?> it = header.examineAllHeaderElements();
			while( it.hasNext()  ){
				
				SOAPHeaderElement headerElement = (SOAPHeaderElement) it.next();
				
				//Controllo Namespace
				String namespace = headerElement.getNamespaceURI();
				
				if(UtilitiesIntegrazioneWSAddressing.WSA_NAMESPACE.equals(namespace)){
					log.debug("Trovato header WSAddressing ["+headerElement.getLocalName()+"]");
					
					if(UtilitiesIntegrazioneWSAddressing.WSA_SOAP_HEADER_TO.equals(headerElement.getLocalName())){
						wsaTO = headerElement;
					}
					else if(UtilitiesIntegrazioneWSAddressing.WSA_SOAP_HEADER_FROM.equals(headerElement.getLocalName())){
						wsaFROM = headerElement;
					}
					else if(UtilitiesIntegrazioneWSAddressing.WSA_SOAP_HEADER_ACTION.equals(headerElement.getLocalName())){
						wsaAction = headerElement;
					}
					else if(UtilitiesIntegrazioneWSAddressing.WSA_SOAP_HEADER_ID.equals(headerElement.getLocalName())){
						wsaID = headerElement;
					}
					else if(UtilitiesIntegrazioneWSAddressing.WSA_SOAP_HEADER_RELATES_TO.equals(headerElement.getLocalName())){
						wsaRelatesTo = headerElement;
					}
					else{
						log.debug("Header non conosciuto");
					}
				}

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
							if(WSA_SOAP_HEADER_EPR_ADDRESS.equals(s.getLocalName())){
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
			
			SOAPHeaderElement wsaTO = null; 
			SOAPHeaderElement wsaFROM = null; 
			SOAPHeaderElement wsaAction = null; 
			SOAPHeaderElement wsaID = null; 
			SOAPHeaderElement wsaRelatesTo = null; 
			
			java.util.Iterator<?> it = header.examineAllHeaderElements();
			while( it.hasNext()  ){
				// Test Header Element
				SOAPHeaderElement headerElement = (SOAPHeaderElement) it.next();
				
				//Controllo Namespace
				String namespace = headerElement.getNamespaceURI();
				String actorCheck = SoapUtils.getSoapActor(headerElement, message.getMessageType());
				
				if(actorIntegrazione.equals(actorCheck) && UtilitiesIntegrazioneWSAddressing.WSA_NAMESPACE.equals(namespace)){
					log.debug("Trovato header WSAddressing ["+headerElement.getLocalName()+"]");
					
					if(UtilitiesIntegrazioneWSAddressing.WSA_SOAP_HEADER_TO.equals(headerElement.getLocalName())){
						wsaTO = headerElement;
					}
					else if(UtilitiesIntegrazioneWSAddressing.WSA_SOAP_HEADER_FROM.equals(headerElement.getLocalName())){
						wsaFROM = headerElement;
					}
					else if(UtilitiesIntegrazioneWSAddressing.WSA_SOAP_HEADER_ACTION.equals(headerElement.getLocalName())){
						wsaAction = headerElement;
					}
					else if(UtilitiesIntegrazioneWSAddressing.WSA_SOAP_HEADER_ID.equals(headerElement.getLocalName())){
						wsaID = headerElement;
					}
					else if(UtilitiesIntegrazioneWSAddressing.WSA_SOAP_HEADER_RELATES_TO.equals(headerElement.getLocalName())){
						wsaRelatesTo = headerElement;
					}
					else{
						log.debug("Header non conosciuto");
					}
				}
				
				
			}
			if(wsaTO==null && wsaFROM==null  &&  wsaAction==null && wsaID==null  &&  wsaRelatesTo==null){
				log.debug("Header di integrazione non presente");
				return;
			}
			
			// delete
			if(wsaTO!=null){
				header.removeChild(wsaTO);
			}
			if(wsaFROM!=null){
				header.removeChild(wsaFROM);
			}
			if(wsaAction!=null){
				header.removeChild(wsaAction);
			}
			if(wsaID!=null){
				header.removeChild(wsaID);
			}	
			if(wsaRelatesTo!=null){
				header.removeChild(wsaRelatesTo);
			}
		
		}catch(Exception e){
			throw new HeaderIntegrazioneException("UtilitiesIntegrazione, lettura dell'header soap non riuscita: "+e.getMessage(),e);
		}
	}
}
