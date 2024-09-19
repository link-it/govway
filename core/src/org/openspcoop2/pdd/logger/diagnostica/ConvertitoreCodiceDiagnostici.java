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

package org.openspcoop2.pdd.logger.diagnostica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.xml.soap.SOAPFault;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.GestioneTokenAutenticazione;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.core.eccezione.details.utils.XMLUtils;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.core.transazioni.constants.TipoAPI;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeTokenClient;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.utils.MessageUtilities;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.autenticazione.GestoreAutenticazione;
import org.openspcoop2.pdd.core.connettori.ConnettoreBase;
import org.openspcoop2.pdd.core.connettori.ConnettoreUtils;
import org.openspcoop2.pdd.core.controllo_traffico.GeneratoreMessaggiErrore;
import org.openspcoop2.pdd.core.credenziali.engine.GestoreCredenzialiEngine;
import org.openspcoop2.pdd.core.token.PolicyGestioneToken;
import org.openspcoop2.pdd.core.trasformazioni.GestoreTrasformazioniUtilities;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.record.AbstractDatoRicostruzione;
import org.openspcoop2.pdd.logger.record.RuoloTransazione;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.sdk.constants.MessaggiFaultErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.properties.PropertiesReader;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;

/**     
 * ConvertitoreCodiceDiagnostici
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConvertitoreCodiceDiagnostici {

	protected org.openspcoop2.pdd.logger.MsgDiagnostico msgDiag;
	protected Transazione transazioneDTO = null;
	protected InformazioniRecordDiagnostici info;
	protected IProtocolFactory<?> protocolFactory;
	
	protected List<String> codiciModuliSimulabili = new ArrayList<>();
	protected	List<String> codiciModuliSimulabiliIntegrazione = new ArrayList<>();
	protected List<String> codiciModuliSimulabiliCooperazione = new ArrayList<>();

	protected OpenSPCoop2Message mFaultRicostruitoCooperazione = null;
	protected String mFaultRicostruitoCooperazioneDescrizioneWithDetails = null;
	protected String mFaultRicostruitoCooperazioneDescrizioneWithoutDetails = null;
	
	protected OpenSPCoop2Message mFaultRicostruitoIntegrazione = null;
	protected String mFaultRicostruitoIntegrazioneDescrizioneWithDetails = null;
	protected String mFaultRicostruitoIntegrazioneDescrizioneWithoutDetails = null;
	
	protected List<String> codiciModuliSimulabiliTracciamento = new ArrayList<>();
	
	protected boolean esitoAutenticazioneInCache = false;
	protected boolean esitoAutorizzazioneInCache = false;
	protected boolean esitoAutorizzazioneContenutiInCache = false;
	protected boolean esitoAutenticazioneTokenInCache = false;
	protected boolean esitoModiTokenAuthorizationInCache = false;
	protected boolean esitoModiTokenIntegrityInCache = false;
	protected boolean esitoModiTokenAuditInCache = false;
	
	protected boolean rispostaLettaDallaCache = false;
	
	protected String dettaglioAutenticazioneFallita = null;
	
	public static final String RICEZIONE_CONTENUTI_APPLICATIVI_CODICE_MODULO = "001"; // codice modulo
	public static final String RICEZIONE_CONTENUTI_APPLICATIVI_LOCAL_FORWARD_LOGINFO_CODICE = "001034"; // ricezioneContenutiApplicativi.localForward.logInfo
	public static final String RICEZIONE_CONTENUTI_APPLICATIVI_CONSEGNA_RISPOSTA_APPLICATIVA_OK_CODICE = "001005"; // ricezioneContenutiApplicativi.consegnaRispostaApplicativaOkEffettuata
	public static final String RICEZIONE_CONTENUTI_APPLICATIVI_CONSEGNA_RISPOSTA_APPLICATIVA_KO_CODICE = "001006"; // ricezioneContenutiApplicativi.consegnaRispostaApplicativaKoEffettuata
	public static final String RICEZIONE_CONTENUTI_APPLICATIVI_AUTENTICAZIONE_IN_CORSO_CODICE = "001050"; // ricezioneContenutiApplicativi.autenticazioneInCorso
		
	public static final String IMBUSTAMENTO_CODICE_MODULO = "002"; // codice modulo
	
	public static final String INOLTRO_BUSTE_CODICE_MODULO = "003"; // codice modulo
	public static final String INOLTRO_BUSTE_INOLTRO_CON_ERRORE_CODICE = "003008"; // inoltroBuste.inoltroConErrore
	public static final String INOLTRO_BUSTE_RICEZIONE_SOAP_FAULT_CODICE = "003013"; // inoltroBuste.ricezioneSoapFault
	
	public static final String RICEZIONE_BUSTE_CODICE_MODULO = "004"; // codice modulo
	public static final String RICEZIONE_BUSTE_AUTORIZZAZIONE_BUSTE_EFFETTUATA_CODICE = "004004"; // ricezioneBuste.autorizzazioneBusteEffettuata
	public static final String RICEZIONE_BUSTE_AUTENTICAZIONE_IN_CORSO_CODICE = "004074"; //ricezioneBuste.autenticazioneInCorso
	public static final String RICEZIONE_BUSTE_AUTENTICAZIONE_EFFETTUATA_CODICE = "004075"; //ricezioneBuste.autenticazioneEffettuata
	public static final String RICEZIONE_BUSTE_AUTENTICAZIONE_FALLITA_CODICE = "004076"; //ricezioneBuste.autenticazioneFallita
	public static final String RICEZIONE_BUSTE_AUTENTICAZIONE_FALLITA_OPZIONALE_CODICE = "004077"; //ricezioneBuste.autenticazioneFallita.opzionale
			
	public static final String SBUSTAMENTO_CODICE_MODULO = "005"; // codice modulo
	
	public static final String SBUSTAMENTO_RISPOSTE_CODICE_MODULO = "006"; // codice modulo
	
	public static final String CONSEGNA_CONTENUTI_APPLICATIVI_CODICE_MODULO = "007"; // codice modulo
	public static final String CONSEGNA_CONTENUTI_APPLICATIVI_RICEZIONE_SOAP_FAULT_CODICE = "007014"; // consegnaContenutiApplicativi.ricezioneSoapFault
	
	
	public ConvertitoreCodiceDiagnostici(Transazione transazioneDTO,
			Traccia tracciaRichiesta, Traccia tracciaRisposta,
			InformazioniRecordDiagnostici info,
			CredenzialeMittente credenzialeClientId) throws ProtocolException{
		this.init(null, tracciaRichiesta, tracciaRisposta, transazioneDTO, info, credenzialeClientId);
	}
	protected ConvertitoreCodiceDiagnostici() {
		// va chiamato init
	}
	protected void init(IProtocolFactory<?> protocolFactory, Traccia tracciaRichiesta, Traccia tracciaRisposta,
			Transazione transazioneDTO, InformazioniRecordDiagnostici info,
			CredenzialeMittente credenzialeClientId) throws ProtocolException{

		if(protocolFactory==null){
			protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(transazioneDTO.getProtocollo());
		}
		
		/**Logger log = protocolFactory.getLogger() != null ? protocolFactory.getLogger() : LoggerWrapperFactory.getLogger(ConvertitoreCodiceDiagnostici.class);*/
		
		String confDir = null;
		if(!MsgDiagnosticiProperties.initialize(null,confDir)){
			throw new ProtocolException("Inizializzazione MsgDiagnosticiProperties non riuscita");
		}
		MsgDiagnosticiProperties msgDiagProperties = MsgDiagnosticiProperties.getInstance();
		if(!msgDiagProperties.initializeMsgDiagnosticiPersonalizzati()){
			throw new ProtocolException("Inizializzazione MsgDiagnosticiProperties (msg personalizzati) non riuscita");
		}
		
		this.msgDiag = org.openspcoop2.pdd.logger.MsgDiagnostico.newInstance();
		this.msgDiag.setPrefixMsgPersonalizzati("");

		this.transazioneDTO = transazioneDTO;
		this.info = info;
		
		
		/* ---- Codici Simulabili ----- */
		
		//this.codiciModuliSimulabili.add("000"); all
		this.codiciModuliSimulabili.add(RICEZIONE_CONTENUTI_APPLICATIVI_CODICE_MODULO); // ricezioneContenutiApplicativi
		this.codiciModuliSimulabili.add(IMBUSTAMENTO_CODICE_MODULO); // imbustamentoBusta
		this.codiciModuliSimulabili.add(INOLTRO_BUSTE_CODICE_MODULO); // inoltroBuste
		this.codiciModuliSimulabili.add(RICEZIONE_BUSTE_CODICE_MODULO); // ricezioneBuste
		this.codiciModuliSimulabili.add(SBUSTAMENTO_CODICE_MODULO); // sbustamentoBusta
		this.codiciModuliSimulabili.add(SBUSTAMENTO_RISPOSTE_CODICE_MODULO); // sbustamentoRispostaBusta
		this.codiciModuliSimulabili.add(CONSEGNA_CONTENUTI_APPLICATIVI_CODICE_MODULO); // consegnaContenutiApplicativi
		this.codiciModuliSimulabili.add("008"); // integrationManager
		//this.codiciModuliSimulabili.add("009"); // tracciamento
		//this.codiciModuliSimulabili.add("010"); // timerGestoreRiscontriRicevute
		//this.codiciModuliSimulabili.add("011"); // timerGestoreMessaggi
		//this.codiciModuliSimulabili.add("012"); // timerGestoreMessaggiInconsistenti
		//this.codiciModuliSimulabili.add("013"); // timerGestoreRepositoryBuste
		//this.codiciModuliSimulabili.add("014"); // timerMonitoraggioRisorse
		//this.codiciModuliSimulabili.add("015"); // timerThreshold
		//this.codiciModuliSimulabili.add("100"); // openspcoopStartup
		
		this.codiciModuliSimulabiliIntegrazione.add(RICEZIONE_CONTENUTI_APPLICATIVI_CODICE_MODULO); // ricezioneContenutiApplicativi
		this.codiciModuliSimulabiliIntegrazione.add(IMBUSTAMENTO_CODICE_MODULO); // imbustamentoBusta
		this.codiciModuliSimulabiliIntegrazione.add(CONSEGNA_CONTENUTI_APPLICATIVI_CODICE_MODULO); // consegnaContenutiApplicativi
		
		this.codiciModuliSimulabiliCooperazione.add(INOLTRO_BUSTE_CODICE_MODULO); // inoltroBuste
		this.codiciModuliSimulabiliCooperazione.add(RICEZIONE_BUSTE_CODICE_MODULO); // ricezioneBuste
		this.codiciModuliSimulabiliCooperazione.add(SBUSTAMENTO_CODICE_MODULO); // sbustamentoBusta
		this.codiciModuliSimulabiliCooperazione.add(SBUSTAMENTO_RISPOSTE_CODICE_MODULO); // sbustamentoRispostaBusta
		
		this.codiciModuliSimulabiliTracciamento.add("009007"); // richiestaIngresso.inCorso
		this.codiciModuliSimulabiliTracciamento.add("009008"); // richiestaIngresso.completato
		this.codiciModuliSimulabiliTracciamento.add("009009"); // richiestaUscita.inCorso
		this.codiciModuliSimulabiliTracciamento.add("009010"); // richiestaUscita.completato
		this.codiciModuliSimulabiliTracciamento.add("009011"); // rispostaIngresso.inCorso
		this.codiciModuliSimulabiliTracciamento.add("009012"); // rispostaIngresso.completato
		this.codiciModuliSimulabiliTracciamento.add("009013"); // rispostaUscita.inCorso
		this.codiciModuliSimulabiliTracciamento.add("009014"); // rispostaUscita.completato
		this.codiciModuliSimulabiliTracciamento.add("009015"); // richiestaIngresso.inCorso (fileTrace)
		this.codiciModuliSimulabiliTracciamento.add("009016"); // richiestaIngresso.completato (fileTrace)
		this.codiciModuliSimulabiliTracciamento.add("009017"); // richiestaUscita.inCorso (fileTrace)
		this.codiciModuliSimulabiliTracciamento.add("009018"); // richiestaUscita.completato (fileTrace)
		this.codiciModuliSimulabiliTracciamento.add("009019"); // rispostaIngresso.inCorso (fileTrace)
		this.codiciModuliSimulabiliTracciamento.add("009020"); // rispostaIngresso.completato (fileTrace)
		this.codiciModuliSimulabiliTracciamento.add("009021"); // rispostaUscita.inCorso (fileTrace)
		this.codiciModuliSimulabiliTracciamento.add("009022"); // rispostaUscita.completato (fileTrace)
		
		
		/* ---- Dati identificativi ----- */
		
		IDSoggetto soggettoMittente = null;
		if(transazioneDTO.getTipoSoggettoFruitore()!=null && transazioneDTO.getNomeSoggettoFruitore()!=null){
			soggettoMittente = new IDSoggetto(transazioneDTO.getTipoSoggettoFruitore(),transazioneDTO.getNomeSoggettoFruitore());
		}
		IDServizio idServizio = null;
		try {
			if(transazioneDTO.getTipoServizio()!=null && transazioneDTO.getNomeServizio()!=null &&
					transazioneDTO.getTipoSoggettoErogatore()!=null && transazioneDTO.getNomeSoggettoErogatore()!=null) {
				idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(transazioneDTO.getTipoServizio(),transazioneDTO.getNomeServizio(), 
						transazioneDTO.getTipoSoggettoErogatore(),transazioneDTO.getNomeSoggettoErogatore(), 
						transazioneDTO.getVersioneServizio()); 
			}
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
		if(idServizio!=null) {
			idServizio.setAzione(transazioneDTO.getAzione());
		}
		

		/* ---- Protocollo ---- */
		this.protocolFactory = protocolFactory;
		String protocollo = protocolFactory.getProtocol();
		this.msgDiag.addKeywords(protocolFactory);


		/* ---- Buste ---- */

		// NOTA: se esiste una traccia di richiesta, si fornisce quella.
		// Altrimenti la si costruisce dalla transazione.
		Busta bustaRichiesta = null;
		if(tracciaRichiesta!=null && tracciaRichiesta.getBusta()!=null){
			bustaRichiesta = tracciaRichiesta.getBusta();
			this.msgDiag.addKeywords(tracciaRichiesta.getBusta(), true);
		}
		else{
			bustaRichiesta = new Busta(protocollo);
			bustaRichiesta.setID(transazioneDTO.getIdMessaggioRichiesta());
			ProfiloDiCollaborazione profilo = null;
			if(transazioneDTO.getProfiloCollaborazioneOp2()!=null){
				profilo = ProfiloDiCollaborazione.toProfiloDiCollaborazione(transazioneDTO.getProfiloCollaborazioneOp2());
				bustaRichiesta.setProfiloDiCollaborazione(profilo);
			}
			bustaRichiesta.setProfiloDiCollaborazioneValue(transazioneDTO.getProfiloCollaborazioneProt());
			bustaRichiesta.setTipoMittente(transazioneDTO.getTipoSoggettoFruitore());
			bustaRichiesta.setMittente(transazioneDTO.getNomeSoggettoFruitore());
			bustaRichiesta.setIdentificativoPortaMittente(transazioneDTO.getIdportaSoggettoFruitore());
			bustaRichiesta.setIndirizzoMittente(transazioneDTO.getIndirizzoSoggettoFruitore());
			bustaRichiesta.setTipoDestinatario(transazioneDTO.getTipoSoggettoErogatore());
			bustaRichiesta.setDestinatario(transazioneDTO.getNomeSoggettoErogatore());
			bustaRichiesta.setIdentificativoPortaDestinatario(transazioneDTO.getIdportaSoggettoErogatore());
			bustaRichiesta.setIndirizzoDestinatario(transazioneDTO.getIndirizzoSoggettoErogatore());
			bustaRichiesta.setTipoServizio(transazioneDTO.getTipoServizio());
			bustaRichiesta.setServizio(transazioneDTO.getNomeServizio());
			bustaRichiesta.setVersioneServizio(transazioneDTO.getVersioneServizio());
			bustaRichiesta.setAzione(transazioneDTO.getAzione());
			//bustaRichiesta.setRiferimentoMessaggio(null); // non posso saperlo se c'era l'id di richiesta o altro
			//bustaRichiesta.setSequenza(-1); // non posso saperlo se c'era nella richiesta o nella risposta 
			//bustaRichiesta.setScadenza(new Date()); //non posso saperlo se c'era nella richiesta o nella risposta 
			this.msgDiag.addKeywords(bustaRichiesta, true);
		}

		// NOTA: se esiste una traccia di richiesta, si fornisce quella.
		// Altrimenti la si costruisce dalla transazione.
		if(tracciaRisposta!=null && tracciaRisposta.getBusta()!=null){
			this.msgDiag.addKeywords(tracciaRisposta.getBusta(), false);
		}
		else{
			Busta bustaRisposta = new Busta(protocollo);
			bustaRisposta.setID(transazioneDTO.getIdMessaggioRisposta());
			ProfiloDiCollaborazione profilo = null;
			if(transazioneDTO.getProfiloCollaborazioneOp2()!=null){
				profilo = ProfiloDiCollaborazione.toProfiloDiCollaborazione(transazioneDTO.getProfiloCollaborazioneOp2());
				bustaRisposta.setProfiloDiCollaborazione(profilo);
			}
			bustaRisposta.setProfiloDiCollaborazioneValue(transazioneDTO.getProfiloCollaborazioneProt());
			bustaRisposta.setTipoMittente(transazioneDTO.getTipoSoggettoErogatore());
			bustaRisposta.setMittente(transazioneDTO.getNomeSoggettoErogatore());
			bustaRisposta.setIdentificativoPortaMittente(transazioneDTO.getIdportaSoggettoErogatore());
			bustaRisposta.setIndirizzoMittente(transazioneDTO.getIndirizzoSoggettoErogatore());
			bustaRisposta.setTipoDestinatario(transazioneDTO.getTipoSoggettoFruitore());
			bustaRisposta.setDestinatario(transazioneDTO.getNomeSoggettoFruitore());
			bustaRisposta.setIdentificativoPortaDestinatario(transazioneDTO.getIdportaSoggettoFruitore());
			bustaRisposta.setIndirizzoDestinatario(transazioneDTO.getIndirizzoSoggettoFruitore());
			bustaRisposta.setTipoServizio(transazioneDTO.getTipoServizio());
			bustaRisposta.setServizio(transazioneDTO.getNomeServizio());
			bustaRisposta.setVersioneServizio(transazioneDTO.getVersioneServizio());
			bustaRisposta.setAzione(transazioneDTO.getAzione());
			//bustaRichiesta.setRiferimentoMessaggio(null); // non posso saperlo se c'era l'id di richiesta o altro
			//bustaRichiesta.setSequenza(-1); // non posso saperlo se c'era nella richiesta o nella risposta 
			//bustaRichiesta.setScadenza(new Date()); //non posso saperlo se c'era nella richiesta o nella risposta 
			this.msgDiag.addKeywords(bustaRisposta, false);
		}

		
		/* ---- Servizi Applicativi ---- */
		String servizioApplicativoFruitore = transazioneDTO.getServizioApplicativoFruitore();
		if(servizioApplicativoFruitore!=null && !"".equals(servizioApplicativoFruitore)){
			this.msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, transazioneDTO.getServizioApplicativoFruitore());
		}
		else{
			this.msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE, CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO);
		}
		String servizioApplicativoErogatore = transazioneDTO.getServizioApplicativoErogatore();
		this.msgDiag.addKeyword(CostantiPdD.KEY_SA_EROGATORE, servizioApplicativoErogatore);

		
		/* ---- Servizi Applicativi via Token ---- */
		if(credenzialeClientId!=null && credenzialeClientId.getCredenziale()!=null) {
			String clientId = CredenzialeTokenClient.convertClientIdDBValueToOriginal(credenzialeClientId.getCredenziale());
			if(clientId!=null) {
				this.msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_CLIENT_ID, clientId);
			}
			try {
				IDServizioApplicativo idApplicativoToken = CredenzialeTokenClient.convertApplicationDBValueToOriginal(credenzialeClientId.getCredenziale());
				if(idApplicativoToken!=null) {
					this.msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_SERVIZIO_APPLICATIVO, idApplicativoToken.getNome()+NamingUtils.LABEL_DOMINIO+idApplicativoToken.getIdSoggettoProprietario().toString());
				}
			}catch(Exception e) {
				protocolFactory.getLogger().error(e.getMessage(),e);
			}
		}

		/* ---- Correlazione Applicativa ---- */
		this.msgDiag.addKeyword(CostantiPdD.KEY_ID_CORRELAZIONE_APPLICATIVA, transazioneDTO.getIdCorrelazioneApplicativa());


		/* ---- Ruolo (scenario, tipologia, tipoRicevuta, profiloAsincrono) ---- */
		RuoloTransazione ruoloTransazione = RuoloTransazione.toEnumConstant(transazioneDTO.getRuoloTransazione());
		String idAsincrono = transazioneDTO.getIdAsincrono();
		if(ruoloTransazione!=null){
			if(RuoloTransazione.INVOCAZIONE_ONEWAY.equals(ruoloTransazione)){				
				this.msgDiag.addKeyword(CostantiPdD.KEY_SCENARIO_COOPERAZIONE_GESTITO, Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO);
			}
			else if(RuoloTransazione.INVOCAZIONE_SINCRONA.equals(ruoloTransazione)){				
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIPOLOGIA_RISPOSTA_APPLICATIVA, "risposta sincrona");
				this.msgDiag.addKeyword(CostantiPdD.KEY_SCENARIO_COOPERAZIONE_GESTITO, Costanti.SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO);
			}
			else if(RuoloTransazione.INVOCAZIONE_ASINCRONA_SIMMETRICA.equals(ruoloTransazione)){				
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIPOLOGIA_RISPOSTA_APPLICATIVA, "ricevuta di una richiesta asincrona simmetrica");
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_RICEVUTA_ASINCRONA, "ricevuta di una richiesta asincrona simmetrica");
				this.msgDiag.addKeyword(CostantiPdD.KEY_SCENARIO_COOPERAZIONE_GESTITO, Costanti.SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO);
			}
			else if(RuoloTransazione.RISPOSTA_ASINCRONA_SIMMETRICA.equals(ruoloTransazione)){				
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIPOLOGIA_RISPOSTA_APPLICATIVA, "ricevuta di una risposta asincrona simmetrica");
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_RICEVUTA_ASINCRONA, "ricevuta di una risposta asincrona simmetrica");
				if(idAsincrono!=null){
					this.msgDiag.addKeyword(CostantiPdD.KEY_ID_CORRELAZIONE_ASINCRONA, idAsincrono);
				}
				this.msgDiag.addKeyword(CostantiPdD.KEY_SCENARIO_COOPERAZIONE_GESTITO, Costanti.SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA);
			}
			else if(RuoloTransazione.INVOCAZIONE_ASINCRONA_ASIMMETRICA.equals(ruoloTransazione)){				
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIPOLOGIA_RISPOSTA_APPLICATIVA, "ricevuta di una richiesta asincrona asimmetrica");
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_RICEVUTA_ASINCRONA, "ricevuta di una richiesta asincrona asimmetrica");
				this.msgDiag.addKeyword(CostantiPdD.KEY_SCENARIO_COOPERAZIONE_GESTITO, Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO);
			}
			else if(RuoloTransazione.RICHIESTA_STATO_ASINCRONA_ASIMMETRICA.equals(ruoloTransazione)){				
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIPOLOGIA_RISPOSTA_APPLICATIVA, "ricevuta di una risposta asincrona asimmetrica");
				this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_RICEVUTA_ASINCRONA, "ricevuta di una risposta asincrona asimmetrica contenente l'esito della richiesta stato");
				if(idAsincrono!=null){
					this.msgDiag.addKeyword(CostantiPdD.KEY_ID_CORRELAZIONE_ASINCRONA, idAsincrono);
				}
				this.msgDiag.addKeyword(CostantiPdD.KEY_SCENARIO_COOPERAZIONE_GESTITO, Costanti.SCENARIO_ASINCRONO_ASIMMETRICO_POLLING);
			}
		}

		String tipoMessaggio = "messaggio";
		//tipoMessaggio = "ricevuta asincrona"; non simulabile
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO_BUSTA, tipoMessaggio);

		
		/* ---- Behaviour ---- */
		// L'informazione non la si puo' salvare essendo i behaviour piu' di 1.
		// Inoltre contengono il carattere separatore e andrebbe gestita anche questa criticita
		this.msgDiag.addKeyword(CostantiPdD.KEY_DESCRIZIONE_BEHAVIOUR, "");

		
		/* ---- Router ---- */
		// router non verra' simulato
		this.msgDiag.addKeyword(CostantiPdD.KEY_DESTINATARIO_TRASMISSIONE, "");

		
		/* ---- Credenziali ---- */
		
		String credenziali = transazioneDTO.getCredenziali();
		
		// credenziali da gateway
		if(credenziali!=null && !"".equals(credenziali)){
			if(GestoreCredenzialiEngine.containsPrefixGatewayCredenziali(credenziali)) {
				String identitaGateway = GestoreCredenzialiEngine.readIdentitaGatewayCredenziali(credenziali);
				credenziali = GestoreCredenzialiEngine.erasePrefixGatewayCredenziali(credenziali);			
				this.msgDiag.addKeyword(CostantiPdD.KEY_IDENTITA_GESTORE_CREDENZIALI, identitaGateway);
				this.msgDiag.addKeyword(CostantiPdD.KEY_NUOVE_CREDENZIALI, normalizeCredenziali(credenziali,true,false));
			}
			else{
				this.msgDiag.addKeyword(CostantiPdD.KEY_NUOVE_CREDENZIALI, normalizeCredenziali(credenziali,true,false));
			}
			
			this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI, normalizeCredenziali(credenziali,true,true));
			this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_SA_FRUITORE, normalizeCredenziali(credenziali,true,true));
		}
		else{
			this.msgDiag.addKeyword(CostantiPdD.KEY_NUOVE_CREDENZIALI, "");
			this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI, "");
			this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_SA_FRUITORE, "");
		}
		
		
		/* ---- Operazione IM ---- */
		
		if(transazioneDTO.getOperazioneIm()!=null){
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_OPERAZIONE_IM, transazioneDTO.getOperazioneIm());
		}
		
		
		/* ---- Parametri IM ---- */
		
		if(transazioneDTO.getNomePorta()!=null){
			
			String messaggioPerIdAsincrono = "";
			if(idAsincrono!=null){
				messaggioPerIdAsincrono = " riferimentoMessaggio["+idAsincrono+"]";
			}
			
			String c = "";
			if(credenziali!=null){
				c = credenziali;
			}
	
			String param = "PD["+transazioneDTO.getNomePorta()+"]"+c+messaggioPerIdAsincrono;
			this.msgDiag.addKeyword(CostantiPdD.KEY_PARAMETRI_OPERAZIONE_IM, param);
		}
		
		

		/* ---- Autorizzazione Buste ---- */

		if(idServizio!=null) {
			if(soggettoMittente!=null){
				this.msgDiag.addKeyword(CostantiPdD.KEY_MITTENTE_E_SERVIZIO_DA_AUTORIZZARE, "fruitore ["+soggettoMittente.toString()+"] -> servizio ["+idServizio.toString()+"]");
			}
			else{
				this.msgDiag.addKeyword(CostantiPdD.KEY_MITTENTE_E_SERVIZIO_DA_AUTORIZZARE, "servizio ["+idServizio.toString()+"]");
			}
		}

		// KEY_CREDENZIALI_MITTENTE_MSG Gestite all'interno del metodo buildContextForSingleMsgDiagnostico 
		/**if(credenziali!=null && !"".equals(credenziali)) {
			this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, " credenzialiMittente "+credenziali);
		}
		else{
			this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, "");
		}*/
		if(servizioApplicativoFruitore!=null)
			this.msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE_MSG, " identitaServizioApplicativoFruitore ["+servizioApplicativoFruitore+"]");
		else
			this.msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE_MSG, "");
		this.msgDiag.addKeyword(CostantiPdD.KEY_SUBJECT_MESSAGE_SECURITY_MSG, ""); // non simulato

		this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS, ""); // non simulabile

		if(tracciaRisposta!=null && tracciaRisposta.getBusta()!=null && tracciaRisposta.getBusta().sizeListaRiscontri()==1){
			this.msgDiag.addKeyword(CostantiPdD.KEY_DATA_RISCONTRO, tracciaRisposta.getBusta().getRiscontro(0).getOraRegistrazione().toString());
			this.msgDiag.addKeyword(CostantiPdD.KEY_ID_BUSTA_RISCONTRATA, tracciaRisposta.getBusta().getRiscontro(0).getID());
		}

		
		
		/* ---- Integrazione ---- */

		Object o = info.getDato(MappingRicostruzioneDiagnostici.RESPONSE_FROM_CACHE).getDato();
		String infoResponseFromCache = (o!=null ? (String) o : null );
		if(CostantiMappingDiagnostici.IN_CACHE_TRUE.equals(infoResponseFromCache)) {
			this.rispostaLettaDallaCache = true;
		}
		else {
			this.rispostaLettaDallaCache = false;
		}
		
		
		if(PddRuolo.DELEGATA.equals(transazioneDTO.getPddRuolo())){
			this.msgDiag.addKeyword(CostantiPdD.KEY_PORTA_DELEGATA, transazioneDTO.getNomePorta());
		}
		else{
			this.msgDiag.addKeyword(CostantiPdD.KEY_PORTA_APPLICATIVA, transazioneDTO.getNomePorta());
		}

		o = info.getDato(MappingRicostruzioneDiagnostici.TIPO_CONNETTORE).getDato();
		String tipoConnettore = (o!=null ? (String) o : null );
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_CONNETTORE, tipoConnettore);
		
		if(transazioneDTO.getLocationConnettore()!=null) {
			
			String locationConnettore = transazioneDTO.getLocationConnettore();
			
			if(this.rispostaLettaDallaCache &&
				locationConnettore.contains(ConnettoreBase.LOCATION_CACHED_SEPARATOR_REQUEST_URL)) {
				int indexOf = locationConnettore.indexOf(ConnettoreBase.LOCATION_CACHED_SEPARATOR_REQUEST_URL);
				if(indexOf>0 && locationConnettore.length()>(indexOf+ConnettoreBase.LOCATION_CACHED_SEPARATOR_REQUEST_URL.length())) {
					locationConnettore = locationConnettore.substring(indexOf+ConnettoreBase.LOCATION_CACHED_SEPARATOR_REQUEST_URL.length());
				}
			}
			
			String url = null;
			String method = null;
			try {
				url = CostantiPdD.readUrlFromConnettoreRequest(locationConnettore);
				method = CostantiPdD.readMethodFromConnettoreRequest(locationConnettore);
			}catch(Exception e) {
				protocolFactory.getLogger().error(e.getMessage(),e);
			}
			if(url==null || StringUtils.isEmpty(url) || method==null || StringUtils.isEmpty(method)) {
				url = locationConnettore;
				method = transazioneDTO.getTipoRichiesta();
			}
						
			boolean tipoConnettoreHttp = false;
			if(tipoConnettore!=null && 
					(TipiConnettore.HTTP.getNome().equals(tipoConnettore) || TipiConnettore.HTTPS.getNome().equals(tipoConnettore)  || tipoConnettore.toLowerCase().startsWith("http"))
				) {
				tipoConnettoreHttp = true;	
			}
			
			HttpRequestMethod httpRequestMethod = null;
			if(method!=null && !StringUtils.isEmpty(method)) {
				httpRequestMethod = HttpRequestMethod.valueOf(method);
			}
			
			if( (TipoAPI.SOAP.getValoreAsInt() == transazioneDTO.getTipoApi()) || !tipoConnettoreHttp) {
				httpRequestMethod = null; // il diagnostico in soap o in un connettore non http non contiene l'http method
			}
			this.msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, ConnettoreUtils.formatLocation(httpRequestMethod, url));
		}
		
		
		/* ---- ControlloCongestione ---- */
		
		o = info.getDato(MappingRicostruzioneDiagnostici.MAX_THREADS_THRESHOLD).getDato();
		String maxThreadThreshold = (o!=null ? (String) o : null );
		this.msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_MAX_THREADS_THRESHOLD, maxThreadThreshold);
		
		o = info.getDato(MappingRicostruzioneDiagnostici.CONTROLLO_TRAFFICO_THRESHOLD).getDato();
		String controlloTrafficoThreshold = (o!=null ? (String) o : null );
		this.msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_CONTROLLO_TRAFFICO_THRESHOLD, controlloTrafficoThreshold);
		
		o = info.getDato(MappingRicostruzioneDiagnostici.ACTIVE_THREADS).getDato();
		String activeThreads = (o!=null ? (String) o : null );
		this.msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_ACTIVE_THREADS, activeThreads);
		
		o = info.getDato(MappingRicostruzioneDiagnostici.NUMERO_POLICY_CONFIGURATE).getDato();
		String numeroPolicy = (o!=null ? (String) o : null );
		this.msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY, numeroPolicy);
		
		o = info.getDato(MappingRicostruzioneDiagnostici.NUMERO_POLICY_DISABILITATE).getDato();
		String numeroPolicyDisabilitate = (o!=null ? (String) o : null );
		this.msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_DISABILITATE, numeroPolicyDisabilitate);
		
		o = info.getDato(MappingRicostruzioneDiagnostici.NUMERO_POLICY_FILTRATE).getDato();
		String numeroPolicyFiltrate = (o!=null ? (String) o : null );
		this.msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_FILTRATE, numeroPolicyFiltrate);

		o = info.getDato(MappingRicostruzioneDiagnostici.NUMERO_POLICY_NON_APPLICATE).getDato();
		String numeroPolicyNonApplicabili = (o!=null ? (String) o : null );
		this.msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_NON_APPLICATE, numeroPolicyNonApplicabili);
		
		o = info.getDato(MappingRicostruzioneDiagnostici.NUMERO_POLICY_RISPETTATE).getDato();
		String numeroPolicyRispettate = (o!=null ? (String) o : null );
		this.msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_RISPETTATE, numeroPolicyRispettate);
		
		o = info.getDato(MappingRicostruzioneDiagnostici.NUMERO_POLICY_VIOLATE).getDato();
		String numeroPolicyViolate = (o!=null ? (String) o : null );
		this.msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_VIOLATE, numeroPolicyViolate);
		
		o = info.getDato(MappingRicostruzioneDiagnostici.NUMERO_POLICY_VIOLATE_WARNING_ONLY).getDato();
		String numeroPolicyViolateWarningOnly = (o!=null ? (String) o : null );
		this.msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_VIOLATE_WARNING_ONLY, numeroPolicyViolateWarningOnly);

		o = info.getDato(MappingRicostruzioneDiagnostici.NUMERO_POLICY_IN_ERRORE).getDato();
		String numeroPolicyInErrore = (o!=null ? (String) o : null );
		this.msgDiag.addKeyword(GeneratoreMessaggiErrore.TEMPLATE_NUMERO_POLICY_IN_ERRORE, numeroPolicyInErrore);
			
		
		
	
		
		
		/* ---- Altro ---- */

		this.msgDiag.addKeyword(CostantiPdD.KEY_SCENARIO_COOPERAZIONE_GESTITO, ""); // non simulabile

		String infoSearch = "";
		if(bustaRichiesta!=null &&
			bustaRichiesta.getTipoServizio()!=null && bustaRichiesta.getServizio()!=null &&
			bustaRichiesta.getTipoDestinatario()!=null && bustaRichiesta.getDestinatario()!=null){
			infoSearch = bustaRichiesta.getTipoServizio() + "/"
					+ bustaRichiesta.getServizio() + " erogato dal Soggetto "
					+ bustaRichiesta.getTipoDestinatario() + "/"
					+ bustaRichiesta.getDestinatario();
			if (bustaRichiesta.getAzione() != null)
				infoSearch = infoSearch + " azione " + bustaRichiesta.getAzione();
			if (idAsincrono != null) {
				infoSearch = "Servizio correlato " + infoSearch;
			} else {
				infoSearch = "Servizio " + infoSearch;
			}
			infoSearch = "Ricerca nel registro dei servizi di: " + infoSearch;
			if (idAsincrono != null)
				infoSearch = infoSearch + " (idServizioCorrelato: "+ idAsincrono + ")";
		}
		this.msgDiag.addKeyword(CostantiPdD.KEY_INFO_SERVIZIO_BUSTA, infoSearch);

			
		o = info.getDato(MappingRicostruzioneDiagnostici.TIPO_AUTENTICAZIONE).getDato();
		String tipoAutenticazione = (o!=null ? (String) o : null );
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_AUTENTICAZIONE, tipoAutenticazione);
				
		o = info.getDato(MappingRicostruzioneDiagnostici.TIPO_AUTORIZZAZIONE).getDato();
		String tipoAutorizzazione = (o!=null ? (String) o : null );
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_AUTORIZZAZIONE_BUSTE, tipoAutorizzazione);
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_AUTORIZZAZIONE, tipoAutorizzazione);
		
		o = info.getDato(MappingRicostruzioneDiagnostici.TIPO_AUTORIZZAZIONE_CONTENUTI).getDato();
		String tipoAutorizzazioneContenuti = (o!=null ? (String) o : null );
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_AUTORIZZAZIONE_CONTENUTO, tipoAutorizzazioneContenuti);
		
		
		o = info.getDato(MappingRicostruzioneDiagnostici.TIPO_VALIDAZIONE_CONTENUTI).getDato();
		String tipoTradottoOpenSPCoop = null;
		String tipoValidazioneContenuti = (o!=null ? (String) o : null );
		if(tipoValidazioneContenuti!=null && tipoValidazioneContenuti.length()>0){
			char tipoValidazione = tipoValidazioneContenuti.charAt(0);
			if(CostantiMappingDiagnostici.TIPO_VALIDAZIONE_CONTENUTI_OPENSPCOOP == tipoValidazione){
				tipoTradottoOpenSPCoop = CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.getValue();
			}
			else if(CostantiMappingDiagnostici.TIPO_VALIDAZIONE_CONTENUTI_INTERFACE == tipoValidazione){
				tipoTradottoOpenSPCoop = CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.getValue();
			}
			else if(CostantiMappingDiagnostici.TIPO_VALIDAZIONE_CONTENUTI_XSD == tipoValidazione){
				tipoTradottoOpenSPCoop = CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD.getValue();
			}
			if(tipoValidazioneContenuti.length()>1){
				char mtom = tipoValidazioneContenuti.charAt(1);
				if(CostantiMappingDiagnostici.TIPO_VALIDAZIONE_CONTENUTI_MTOM == mtom){
					tipoTradottoOpenSPCoop =  tipoTradottoOpenSPCoop + CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_PRINT_SEPARATOR +  
							CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_CON_MTOM;
				}
				if(tipoValidazioneContenuti.length()>2){
					char warn = tipoValidazioneContenuti.charAt(2);
					if(CostantiMappingDiagnostici.TIPO_VALIDAZIONE_CONTENUTI_WARN == warn){
						tipoTradottoOpenSPCoop =  tipoTradottoOpenSPCoop + CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_PRINT_SEPARATOR +  
								CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_IN_WARNING_MODE;
					}
				}
			}
		}
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_VALIDAZIONE_CONTENUTI, tipoTradottoOpenSPCoop);
		this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS_VALIDAZIONE_CONTENUTI,"");
		
		
		o = info.getDato(MappingRicostruzioneDiagnostici.TIPO_PROCESSAMENTO_MTOM_RICHIESTA).getDato();
		tipoTradottoOpenSPCoop = null;
		String tipoProcessamentoMTOMRichiesta = (o!=null ? (String) o : null );
		if(tipoProcessamentoMTOMRichiesta!=null && tipoProcessamentoMTOMRichiesta.length()==1){
			char tipoProcessamento = tipoProcessamentoMTOMRichiesta.charAt(0);
			if(CostantiMappingDiagnostici.TIPO_MTOM_PROCESSAMENTO_DISABLE == tipoProcessamento){
				tipoTradottoOpenSPCoop = MTOMProcessorType.DISABLE.getValue();
			}
			else if(CostantiMappingDiagnostici.TIPO_MTOM_PROCESSAMENTO_PACKAGING == tipoProcessamento){
				tipoTradottoOpenSPCoop = MTOMProcessorType.PACKAGING.getValue();
			}
			else if(CostantiMappingDiagnostici.TIPO_MTOM_PROCESSAMENTO_UNPACKAGING == tipoProcessamento){
				tipoTradottoOpenSPCoop = MTOMProcessorType.UNPACKAGING.getValue();
			}
			else if(CostantiMappingDiagnostici.TIPO_MTOM_PROCESSAMENTO_VERIFY == tipoProcessamento){
				tipoTradottoOpenSPCoop = MTOMProcessorType.VERIFY.getValue();
			}
		}
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_PROCESSAMENTO_MTOM_RICHIESTA, tipoTradottoOpenSPCoop);
		
		
		o = info.getDato(MappingRicostruzioneDiagnostici.TIPO_PROCESSAMENTO_MTOM_RISPOSTA).getDato();
		tipoTradottoOpenSPCoop = null;
		String tipoProcessamentoMTOMRisposta = (o!=null ? (String) o : null );
		if(tipoProcessamentoMTOMRisposta!=null && tipoProcessamentoMTOMRisposta.length()==1){
			char tipoProcessamento = tipoProcessamentoMTOMRisposta.charAt(0);
			if(CostantiMappingDiagnostici.TIPO_MTOM_PROCESSAMENTO_DISABLE == tipoProcessamento){
				tipoTradottoOpenSPCoop = MTOMProcessorType.DISABLE.getValue();
			}
			else if(CostantiMappingDiagnostici.TIPO_MTOM_PROCESSAMENTO_PACKAGING == tipoProcessamento){
				tipoTradottoOpenSPCoop = MTOMProcessorType.PACKAGING.getValue();
			}
			else if(CostantiMappingDiagnostici.TIPO_MTOM_PROCESSAMENTO_UNPACKAGING == tipoProcessamento){
				tipoTradottoOpenSPCoop = MTOMProcessorType.UNPACKAGING.getValue();
			}
			else if(CostantiMappingDiagnostici.TIPO_MTOM_PROCESSAMENTO_VERIFY == tipoProcessamento){
				tipoTradottoOpenSPCoop = MTOMProcessorType.VERIFY.getValue();
			}
		}
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_PROCESSAMENTO_MTOM_RISPOSTA, tipoTradottoOpenSPCoop);
		
		
		o = info.getDato(MappingRicostruzioneDiagnostici.TIPO_PROCESSAMENTO_MESSAGE_SECURITY_RICHIESTA).getDato();
		tipoTradottoOpenSPCoop = null;
		String tipoProcessamentoMessageSecurityRichiesta = (o!=null ? (String) o : null );
		if(tipoProcessamentoMessageSecurityRichiesta!=null && tipoProcessamentoMessageSecurityRichiesta.length()>0){
			tipoTradottoOpenSPCoop = buildSicurezzaMessaggio(tipoProcessamentoMessageSecurityRichiesta);
		}
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RICHIESTA, tipoTradottoOpenSPCoop);
		
		
		o = info.getDato(MappingRicostruzioneDiagnostici.TIPO_PROCESSAMENTO_MESSAGE_SECURITY_RISPOSTA).getDato();
		tipoTradottoOpenSPCoop = null;
		String tipoProcessamentoMessageSecurityRisposta = (o!=null ? (String) o : null );
		if(tipoProcessamentoMessageSecurityRisposta!=null && tipoProcessamentoMessageSecurityRisposta.length()>0){
			tipoTradottoOpenSPCoop = buildSicurezzaMessaggio(tipoProcessamentoMessageSecurityRisposta);
		}
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RISPOSTA, tipoTradottoOpenSPCoop);
		
		
		o = info.getDato(MappingRicostruzioneDiagnostici.AUTENTICAZIONE_IN_CACHE).getDato();
		String infoAutenticazioneInCache = (o!=null ? (String) o : null );
		if(CostantiMappingDiagnostici.IN_CACHE_TRUE.equals(infoAutenticazioneInCache)) {
			this.esitoAutenticazioneInCache = true;
		}
		else {
			this.esitoAutenticazioneInCache = false;
		}
		
		
		o = info.getDato(MappingRicostruzioneDiagnostici.AUTORIZZAZIONE_IN_CACHE).getDato();
		String infoAutorizzazioneInCache = (o!=null ? (String) o : null );
		if(CostantiMappingDiagnostici.IN_CACHE_TRUE.equals(infoAutorizzazioneInCache)) {
			this.esitoAutorizzazioneInCache = true;
		}
		else {
			this.esitoAutorizzazioneInCache = false;
		}
		
		
		o = info.getDato(MappingRicostruzioneDiagnostici.AUTORIZZAZIONE_CONTENUTI_IN_CACHE).getDato();
		String infoAutorizzazioneContenutiInCache = (o!=null ? (String) o : null );
		if(CostantiMappingDiagnostici.IN_CACHE_TRUE.equals(infoAutorizzazioneContenutiInCache)) {
			this.esitoAutorizzazioneContenutiInCache = true;
		}
		else {
			this.esitoAutorizzazioneContenutiInCache = false;
		}
		

		o = info.getDato(MappingRicostruzioneDiagnostici.TOKEN_POLICY).getDato();
		String tokenPolicy = (o!=null ? (String) o : null );
		this.msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_POLICY_GESTIONE, tokenPolicy);
		
		o = info.getDato(MappingRicostruzioneDiagnostici.TOKEN_POLICY_ACTIONS).getDato();
		tipoTradottoOpenSPCoop = null;
		String tokenPolicyActions = (o!=null ? (String) o : null );
		if(tokenPolicyActions!=null && tokenPolicyActions.length()>0){
			tipoTradottoOpenSPCoop = buildTokenPolicyValidationActions(tokenPolicyActions);
		}
		this.msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_POLICY_AZIONI, tipoTradottoOpenSPCoop);
		
		
		o = info.getDato(MappingRicostruzioneDiagnostici.TOKEN_POLICY_AUTENTCAZIONE).getDato();
		tipoTradottoOpenSPCoop = null;
		String tokenPolicyAuthn = (o!=null ? (String) o : null );
		if(tokenPolicyAuthn!=null && tokenPolicyAuthn.length()>0){
			tipoTradottoOpenSPCoop = buildTokenPolicyAuthnActions(tokenPolicyAuthn);
		}
		this.msgDiag.addKeyword(CostantiPdD.KEY_TOKEN_AUTHN_CHECK, tipoTradottoOpenSPCoop);
		
		
		o = info.getDato(MappingRicostruzioneDiagnostici.TIPO_TRASFORMAZIONE_RICHIESTA).getDato();
		tipoTradottoOpenSPCoop = null;
		String tipoTrasformazioneRichiesta = (o!=null ? (String) o : null );
		if(tipoTrasformazioneRichiesta!=null && tipoTrasformazioneRichiesta.length()>0){
			tipoTradottoOpenSPCoop = buildTipoTrasformazione(tipoTrasformazioneRichiesta);
		}
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_TRASFORMAZIONE_RICHIESTA, tipoTradottoOpenSPCoop);
		
		
		o = info.getDato(MappingRicostruzioneDiagnostici.TIPO_TRASFORMAZIONE_RISPOSTA).getDato();
		tipoTradottoOpenSPCoop = null;
		String tipoTrasformazioneRisposta = (o!=null ? (String) o : null );
		if(tipoTrasformazioneRisposta!=null && tipoTrasformazioneRisposta.length()>0){
			tipoTradottoOpenSPCoop = buildTipoTrasformazione(tipoTrasformazioneRisposta);
		}
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_TRASFORMAZIONE_RISPOSTA, tipoTradottoOpenSPCoop);
		
		
		AbstractDatoRicostruzione<?> oDatoAutenticazioneTokenInCache = info.getDato(MappingRicostruzioneDiagnostici.AUTENTICAZIONE_TOKEN_IN_CACHE);
		if(oDatoAutenticazioneTokenInCache!=null) {
			o = oDatoAutenticazioneTokenInCache.getDato();
			String infoAutenticazioneTokenInCache = (o!=null ? (String) o : null );
			if(CostantiMappingDiagnostici.IN_CACHE_TRUE.equals(infoAutenticazioneTokenInCache)) {
				this.esitoAutenticazioneTokenInCache = true;
			}
			else {
				this.esitoAutenticazioneTokenInCache = false;
			}
		}else {
			this.esitoAutenticazioneTokenInCache = false;
		}
		
		AbstractDatoRicostruzione<?> oDatoAutenticazioneFallitaMotivazione = info.getDato(MappingRicostruzioneDiagnostici.AUTENTICAZIONE_FALLITA_MOTIVAZIONE);
		if(oDatoAutenticazioneFallitaMotivazione!=null) {
			o = oDatoAutenticazioneFallitaMotivazione.getDato();
			tipoTradottoOpenSPCoop = null;
			String tipoAutenticazioneFallitaMotivazione = (o!=null ? (String) o : null );
			if(tipoAutenticazioneFallitaMotivazione!=null && tipoAutenticazioneFallitaMotivazione.length()>0){
				tipoTradottoOpenSPCoop = buildTipoAutenticazioneFallitaMotivazione(tipoAutenticazioneFallitaMotivazione);
			}
			if(tipoTradottoOpenSPCoop!=null) {
				this.dettaglioAutenticazioneFallita = tipoTradottoOpenSPCoop;
			}		
		}
			
		if(!PddRuolo.DELEGATA.equals(transazioneDTO.getPddRuolo()) &&
				Costanti.MODIPA_PROTOCOL_NAME.equals(protocollo) &&
				CostantiConfigurazione.AUTENTICAZIONE_TOKEN.equals(tipoAutorizzazione)) {
			// casoSpecialeAutorizzazioneTokenModi
			this.msgDiag.addKeyword(CostantiPdD.KEY_SA_FRUITORE_MSG, "");
		}

		
		
		AbstractDatoRicostruzione<?> oDatoModiTokenAuthorizationInCache = info.getDato(MappingRicostruzioneDiagnostici.MODI_TOKEN_AUTHORIZATION_IN_CACHE);
		if(oDatoModiTokenAuthorizationInCache!=null) {
			o = oDatoModiTokenAuthorizationInCache.getDato();
			String infoInCache = (o!=null ? (String) o : null );
			if(CostantiMappingDiagnostici.IN_CACHE_TRUE.equals(infoInCache)) {
				this.esitoModiTokenAuthorizationInCache = true;
			}
			else {
				this.esitoModiTokenAuthorizationInCache = false;
			}
		}else {
			this.esitoModiTokenAuthorizationInCache = false;
		}
		
		
		AbstractDatoRicostruzione<?> oDatoModiTokenIntegrityInCache = info.getDato(MappingRicostruzioneDiagnostici.MODI_TOKEN_INTEGRITY_IN_CACHE);
		if(oDatoModiTokenIntegrityInCache!=null) {
			o = oDatoModiTokenIntegrityInCache.getDato();
			String infoInCache = (o!=null ? (String) o : null );
			if(CostantiMappingDiagnostici.IN_CACHE_TRUE.equals(infoInCache)) {
				this.esitoModiTokenIntegrityInCache = true;
			}
			else {
				this.esitoModiTokenIntegrityInCache = false;
			}
		}else {
			this.esitoModiTokenIntegrityInCache = false;
		}
		
		AbstractDatoRicostruzione<?> oDatoModiTokenAuditInCache = info.getDato(MappingRicostruzioneDiagnostici.MODI_TOKEN_AUDIT_IN_CACHE);
		if(oDatoModiTokenAuditInCache!=null) {
			o = oDatoModiTokenAuditInCache.getDato();
			String infoInCache = (o!=null ? (String) o : null );
			if(CostantiMappingDiagnostici.IN_CACHE_TRUE.equals(infoInCache)) {
				this.esitoModiTokenAuditInCache = true;
			}
			else {
				this.esitoModiTokenAuditInCache = false;
			}
		}else {
			this.esitoModiTokenAuditInCache = false;
		}
		
		
		
		if(info.sizeMetaDati()>=CostantiMappingDiagnostici.LENGHT_DATI_SIMULATI_VERSIONE_ATTUALE){
			
			// altri...
			
		}
		
	}

	private String normalizeCredenziali(String credenziali, boolean addParentesi, boolean addFinalSpace) {
		String newC = credenziali + "";
		try {
			if(newC.endsWith(" ")){
				newC = newC.substring(0,newC.length()-1);
			}
			if(newC.contains("SSL-Issuer ")) {
				newC = newC.substring(0,newC.indexOf("SSL-Issuer "));
				while(newC.endsWith(" ") || newC.endsWith("\n")) {
					newC = newC.substring(0,newC.length()-1);
				}
			}
			String c = null;
			if(addParentesi) {
				c = "( "+newC+" )";
			}
			else {
				c= newC;
			}
			if(addFinalSpace) {
				c = c + " ";
			}
			return c;
		}catch(Exception e) {
			String msgError = "Interpretazione credenziali '"+credenziali+"' non riuscita: ("+newC+") "+e.getMessage();
			LoggerWrapperFactory.getLogger(ConvertitoreCodiceDiagnostici.class).error(msgError, e);
			return credenziali;
		}
	}
	
	private String buildSicurezzaMessaggio(String tmp){
		
		StringBuilder tipoTradottoOpenSPCoop = new StringBuilder();
		
		char engine = tmp.charAt(0);
		addSicurezzaMessaggioEngine(engine,  tipoTradottoOpenSPCoop);
		
		tipoTradottoOpenSPCoop.append(SecurityConstants.TIPO_SECURITY_ENGINE_SEPARATOR);
		
		for (int i = 1; i < tmp.length(); i++) {
		
			String actionCode = tmp.charAt(i)+"";
			for (String keySecurity : CostantiMappingDiagnostici.getMapSecurityAction().keySet()) {
				String actionSecurityCode = CostantiMappingDiagnostici.getMapSecurityAction().get(keySecurity);
				if(actionSecurityCode.equals(actionCode)){
					if(i>1){
						tipoTradottoOpenSPCoop.append(SecurityConstants.TIPO_SECURITY_ACTION_SEPARATOR);
					}
					tipoTradottoOpenSPCoop.append(keySecurity);
					break;
				}
			}
			
		}
		
		if(tipoTradottoOpenSPCoop.length()>0) {
			return tipoTradottoOpenSPCoop.toString();
		}
		return null;
	}
	private void addSicurezzaMessaggioEngine(char engine, StringBuilder tipoTradottoOpenSPCoop) {
		if(CostantiMappingDiagnostici.TIPO_SECURITY_ENGINE_WSS4J == engine){
			tipoTradottoOpenSPCoop.append(SecurityConstants.SECURITY_ENGINE_WSS4J);
		}
		else if(CostantiMappingDiagnostici.TIPO_SECURITY_ENGINE_DSS == engine){
			tipoTradottoOpenSPCoop.append(SecurityConstants.SECURITY_ENGINE_DSS);
		}
		else if(CostantiMappingDiagnostici.TIPO_SECURITY_ENGINE_JOSE == engine){
			tipoTradottoOpenSPCoop.append(SecurityConstants.SECURITY_ENGINE_JOSE);
		}
		else if(CostantiMappingDiagnostici.TIPO_SECURITY_ENGINE_XML == engine){
			tipoTradottoOpenSPCoop.append(SecurityConstants.SECURITY_ENGINE_XML);
		}
	}
	
	private String buildTokenPolicyValidationActions(String tmp) {
		
		PolicyGestioneToken pgt = new PolicyGestioneToken();
		
		if(tmp.length()==1 && (CostantiMappingDiagnostici.GESTIONE_TOKEN_VALIDATION_ACTION_NONE+"").equals(tmp)) {
			return pgt.getLabelAzioniGestioneToken();
		}
		
		for (int i = 0; i < tmp.length(); i++) {
			
			String actionCode = tmp.charAt(i)+"";
			if((CostantiMappingDiagnostici.GESTIONE_TOKEN_VALIDATION_ACTION_JWT+"").equals(actionCode)) {
				pgt.setValidazioneJWT(true);
			}
			else if((CostantiMappingDiagnostici.GESTIONE_TOKEN_VALIDATION_ACTION_INTROSPECTION+"").equals(actionCode)) {
				pgt.setIntrospection(true);
			}
			else if((CostantiMappingDiagnostici.GESTIONE_TOKEN_VALIDATION_ACTION_USER_INFO+"").equals(actionCode)) {
				pgt.setUserInfo(true);
			}
			
		}
		
		return pgt.getLabelAzioniGestioneToken();
		
	}
	
	private String buildTokenPolicyAuthnActions(String tmp) {
		
		GestioneTokenAutenticazione gestore = new GestioneTokenAutenticazione();
		
		for (int i = 0; i < tmp.length(); i++) {
			
			String actionCode = tmp.charAt(i)+"";
			if((CostantiMappingDiagnostici.AUTENTICAZIONE_TOKEN_ISSUER+"").equals(actionCode)) {
				gestore.setIssuer(StatoFunzionalita.ABILITATO);
			}
			else if((CostantiMappingDiagnostici.AUTENTICAZIONE_TOKEN_SUBJECT+"").equals(actionCode)) {
				gestore.setSubject(StatoFunzionalita.ABILITATO);
			}
			else if((CostantiMappingDiagnostici.AUTENTICAZIONE_TOKEN_CLIENTID+"").equals(actionCode)) {
				gestore.setClientId(StatoFunzionalita.ABILITATO);
			}
			else if((CostantiMappingDiagnostici.AUTENTICAZIONE_TOKEN_USERNAME+"").equals(actionCode)) {
				gestore.setUsername(StatoFunzionalita.ABILITATO);
			}
			else if((CostantiMappingDiagnostici.AUTENTICAZIONE_TOKEN_EMAIL+"").equals(actionCode)) {
				gestore.setEmail(StatoFunzionalita.ABILITATO);
			}
			
		}
		
		return GestoreAutenticazione.getLabel(gestore);
		
	}
	
	private String buildTipoTrasformazione(String tmp) {
		
		if(CostantiMappingDiagnostici.TIPO_TRASFORMAZIONE_NESSUNA.equals(tmp)){
			return GestoreTrasformazioniUtilities.TIPO_TRASFORMAZIONE_NESSUNA;
		}
		
		StringBuilder bf = new StringBuilder();
		
		for (int i = 0; i < tmp.length(); i++) {
			
			if(i>0) {
				bf.append(GestoreTrasformazioniUtilities.TIPO_TRASFORMAZIONE_SEPARATOR);
			}
			
			String actionCode = tmp.charAt(i)+"";
			addTipoTrasformazioneAction(actionCode, bf);
			
		}
		
		if(bf.length()>0) {
			return bf.toString();
		}
		
		return null;
		
	}
	private void addTipoTrasformazioneAction(String actionCode, StringBuilder bf) {
		if((CostantiMappingDiagnostici.TIPO_TRASFORMAZIONE_CONVERSIONE_SOAP+"").equals(actionCode)) {
			bf.append(GestoreTrasformazioniUtilities.TIPO_TRASFORMAZIONE_CONVERSIONE_SOAP);
		}
		else if((CostantiMappingDiagnostici.TIPO_TRASFORMAZIONE_CONVERSIONE_REST+"").equals(actionCode)) {
			bf.append(GestoreTrasformazioniUtilities.TIPO_TRASFORMAZIONE_CONVERSIONE_REST);
		}
		else if((CostantiMappingDiagnostici.TIPO_TRASFORMAZIONE_CONVERSIONE_METHOD+"").equals(actionCode)) {
			bf.append(GestoreTrasformazioniUtilities.TIPO_TRASFORMAZIONE_CONVERSIONE_METHOD);
		}
		else if((CostantiMappingDiagnostici.TIPO_TRASFORMAZIONE_CONVERSIONE_PATH+"").equals(actionCode)) {
			bf.append(GestoreTrasformazioniUtilities.TIPO_TRASFORMAZIONE_CONVERSIONE_PATH);
		}
		else if((CostantiMappingDiagnostici.TIPO_TRASFORMAZIONE_CONVERSIONE_HEADERS+"").equals(actionCode)) {
			bf.append(GestoreTrasformazioniUtilities.TIPO_TRASFORMAZIONE_CONVERSIONE_HEADERS);
		}
		else if((CostantiMappingDiagnostici.TIPO_TRASFORMAZIONE_CONVERSIONE_QUERY_PARAMETERS+"").equals(actionCode)) {
			bf.append(GestoreTrasformazioniUtilities.TIPO_TRASFORMAZIONE_CONVERSIONE_QUERY_PARAMETERS);
		}
		else if((CostantiMappingDiagnostici.TIPO_TRASFORMAZIONE_CONVERSIONE_RETURN_CODE+"").equals(actionCode)) {
			bf.append(GestoreTrasformazioniUtilities.TIPO_TRASFORMAZIONE_CONVERSIONE_RETURN_CODE);
		}
		else if((CostantiMappingDiagnostici.TIPO_TRASFORMAZIONE_NESSUNA+"").equals(actionCode)) {
			bf.append(GestoreTrasformazioniUtilities.TIPO_TRASFORMAZIONE_NESSUNA);
		}
		else {
			for (String keySecurity : CostantiMappingDiagnostici.getMapTipiConversione().keySet()) {
				String actionSecurityCode = CostantiMappingDiagnostici.getMapTipiConversione().get(keySecurity);
				if(actionSecurityCode.equals(actionCode)){
					bf.append(keySecurity);
					break;
				}
			}
		}
	}
	
	private String buildTipoAutenticazioneFallitaMotivazione(String tmp) {
		
		if(CostantiMappingDiagnostici.TIPO_AUTENTICAZIONE_FALLITA_MOTIVAZIONE_CREDENZIALI_NON_FORNITE.equals(tmp)){
			return CostantiProtocollo.CREDENZIALI_NON_FORNITE;
		}
		else if(CostantiMappingDiagnostici.TIPO_AUTENTICAZIONE_FALLITA_MOTIVAZIONE_CREDENZIALI_FORNITE_NON_CORRETE.equals(tmp)){
			return CostantiProtocollo.CREDENZIALI_FORNITE_NON_CORRETTE;
		}
				
		return null;
		
	}
	
	private void buildFaultInfo(Logger log) throws Exception {
		
		buildFaultInfoIntegrazione(log);
		
		buildFaultInfoCooperazione(log);
		
	}
	private void buildFaultInfoIntegrazione(Logger log) throws Exception {
		if(this.mFaultRicostruitoIntegrazione==null &&
				this.transazioneDTO.getFaultIntegrazione()!=null && 
				!"".equals(this.transazioneDTO.getFaultIntegrazione())){
			if(StringUtils.isNotEmpty(this.transazioneDTO.getFormatoFaultIntegrazione())){
				MessageType msgType = MessageType.valueOf(this.transazioneDTO.getFormatoFaultIntegrazione());
				OpenSPCoop2MessageParseResult parseResult = OpenSPCoop2MessageFactory.getDefaultMessageFactory().createMessage(msgType, MessageRole.FAULT, 
						MessageUtilities.getDefaultContentType(msgType),this.transazioneDTO.getFaultIntegrazione().getBytes()); 
				this.mFaultRicostruitoIntegrazione = parseResult.getMessage_throwParseException();
			}
			buildFaultInfoIntegrazioneDetails(log);
		}
	}
	private void buildFaultInfoIntegrazioneDetails(Logger log) throws MessageException, MessageNotSupportedException {
		if(this.mFaultRicostruitoIntegrazione!=null) {
			if(ServiceBinding.SOAP.equals(this.mFaultRicostruitoIntegrazione.getServiceBinding())) {
				OpenSPCoop2SoapMessage soapMsg = this.mFaultRicostruitoIntegrazione.castAsSoap();
				if(soapMsg.getSOAPBody()!=null && soapMsg.getSOAPBody().hasFault()){
					this.mFaultRicostruitoIntegrazioneDescrizioneWithDetails = SoapUtils.safe_toString(soapMsg.getFactory(), soapMsg.getSOAPBody().getFault(), log);
					this.mFaultRicostruitoIntegrazioneDescrizioneWithoutDetails = SoapUtils.safe_toString(soapMsg.getFactory(), soapMsg.getSOAPBody().getFault(), false, log);
				}
			}
			else {
				OpenSPCoop2RestMessage<?> restMsg = this.mFaultRicostruitoIntegrazione.castAsRest();
				if(restMsg.isFault()){
					this.mFaultRicostruitoIntegrazioneDescrizioneWithDetails = restMsg.getContentAsString();
					this.mFaultRicostruitoIntegrazioneDescrizioneWithoutDetails = restMsg.getContentAsString();
				}
			}
		}
	}
	private void buildFaultInfoCooperazione(Logger log) throws Exception {
		if(this.mFaultRicostruitoCooperazione==null &&
				this.transazioneDTO.getFaultCooperazione()!=null && 
				!"".equals(this.transazioneDTO.getFaultCooperazione())){
			if(StringUtils.isNotEmpty(this.transazioneDTO.getFormatoFaultCooperazione())){
				MessageType msgType = MessageType.valueOf(this.transazioneDTO.getFormatoFaultCooperazione());
				OpenSPCoop2MessageParseResult parseResult = OpenSPCoop2MessageFactory.getDefaultMessageFactory().createMessage(msgType, MessageRole.FAULT, 
						MessageUtilities.getDefaultContentType(msgType),this.transazioneDTO.getFaultCooperazione().getBytes()); 
				this.mFaultRicostruitoCooperazione = parseResult.getMessage_throwParseException();
			}
			buildFaultInfoCooperazioneDetails(log);
		}
	}
	private void buildFaultInfoCooperazioneDetails(Logger log) throws MessageException, MessageNotSupportedException {
		if(this.mFaultRicostruitoCooperazione!=null) {
			if(ServiceBinding.SOAP.equals(this.mFaultRicostruitoCooperazione.getServiceBinding())) {
				OpenSPCoop2SoapMessage soapMsg = this.mFaultRicostruitoCooperazione.castAsSoap();
				if(soapMsg.getSOAPBody()!=null && soapMsg.getSOAPBody().hasFault()){
					this.mFaultRicostruitoCooperazioneDescrizioneWithDetails = SoapUtils.safe_toString(soapMsg.getFactory(), soapMsg.getSOAPBody().getFault(), log);
					this.mFaultRicostruitoCooperazioneDescrizioneWithoutDetails = SoapUtils.safe_toString(soapMsg.getFactory(), soapMsg.getSOAPBody().getFault(), false, log);
				}
			}
			else {
				OpenSPCoop2RestMessage<?> restMsg = this.mFaultRicostruitoCooperazione.castAsRest();
				if(restMsg.isFault()){
					this.mFaultRicostruitoCooperazioneDescrizioneWithDetails = restMsg.getContentAsString();
					this.mFaultRicostruitoCooperazioneDescrizioneWithoutDetails = restMsg.getContentAsString();
				}
			}
		}
	}
	
	protected void buildFault(Logger log, String codice, String codiceModulo) throws Exception{
		
		if(codice!=null) {
			// nop
		}
		
		this.msgDiag.getKeywordLogPersonalizzati().remove(CostantiPdD.KEY_SOAP_FAULT);
		this.msgDiag.addKeyword(CostantiPdD.KEY_SOAP_FAULT, "");
		
		if(this.codiciModuliSimulabiliIntegrazione.contains(codiceModulo)){
			buildFaultInfo(log);
			// aggiorno key soap fault per il diagnostico in questione
			if(this.mFaultRicostruitoIntegrazioneDescrizioneWithDetails!=null){
				this.msgDiag.getKeywordLogPersonalizzati().remove(CostantiPdD.KEY_SOAP_FAULT);
				this.msgDiag.addKeyword(CostantiPdD.KEY_SOAP_FAULT, this.mFaultRicostruitoIntegrazioneDescrizioneWithDetails);
			}
		}
		else if(this.codiciModuliSimulabiliCooperazione.contains(codiceModulo)){
			buildFaultInfo(log);
			// aggiorno key soap fault per il diagnostico in questione
			if(this.mFaultRicostruitoCooperazioneDescrizioneWithDetails!=null){
				this.msgDiag.getKeywordLogPersonalizzati().remove(CostantiPdD.KEY_SOAP_FAULT);
				this.msgDiag.addKeyword(CostantiPdD.KEY_SOAP_FAULT, this.mFaultRicostruitoCooperazioneDescrizioneWithDetails);
			}
		}
	}
	
	protected void buildContextForSingleMsgDiagnostico(String codice, String codiceModulo, 
			boolean dynamic, String dynamicType, String dynamicValue, Logger log) throws Exception{
				
		
		if(codice.equals("001013") || codice.equals("001056") || codice.equals("001058") || 
				codice.equals(RICEZIONE_BUSTE_AUTENTICAZIONE_EFFETTUATA_CODICE) || codice.equals(RICEZIONE_BUSTE_AUTENTICAZIONE_FALLITA_CODICE) || codice.equals(RICEZIONE_BUSTE_AUTENTICAZIONE_FALLITA_OPZIONALE_CODICE)) { 
			PdDContext pddContextDevNull = new PdDContext();
			CostantiPdD.addKeywordInCache(this.msgDiag, this.esitoAutenticazioneInCache, pddContextDevNull, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTENTICAZIONE);
		}
		else if(codice.equals("001015") || codice.equals("001045") ||
				codice.equals(RICEZIONE_BUSTE_AUTORIZZAZIONE_BUSTE_EFFETTUATA_CODICE) || codice.equals("004005")) { 
			PdDContext pddContextDevNull = new PdDContext();
			CostantiPdD.addKeywordInCache(this.msgDiag, this.esitoAutorizzazioneInCache, pddContextDevNull, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTORIZZAZIONE);
		}
		else if(codice.equals("001016") || codice.equals("001048") ||
				codice.equals("004045") || codice.equals("004046")) {
			PdDContext pddContextDevNull = new PdDContext();
			CostantiPdD.addKeywordInCache(this.msgDiag, this.esitoAutorizzazioneContenutiInCache, pddContextDevNull, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTORIZZAZIONE_CONTENUTI);
		}
		else if(codice.equals("001126") || codice.equals("001127") || codice.equals("001128") || 
				codice.equals("004149") || codice.equals("004150") || codice.equals("004151")) { 
			PdDContext pddContextDevNull = new PdDContext();
			CostantiPdD.addKeywordInCache(this.msgDiag, this.esitoAutenticazioneTokenInCache, pddContextDevNull, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTENTICAZIONE_TOKEN);
		}
		else if(codice.equals("001133") || codice.equals("004159") ) { 
			PdDContext pddContextDevNull = new PdDContext();
			CostantiPdD.addKeywordInCache(this.msgDiag, this.esitoModiTokenAuthorizationInCache, pddContextDevNull, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_MODI_TOKEN_AUTHORIZATION);
		}
		else if(codice.equals("001139") || codice.equals("004165") ) { 
			PdDContext pddContextDevNull = new PdDContext();
			CostantiPdD.addKeywordInCache(this.msgDiag, this.esitoModiTokenIntegrityInCache, pddContextDevNull, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_MODI_TOKEN_INTEGRITY);
		}
		else if(codice.equals("001145") ) { 
			PdDContext pddContextDevNull = new PdDContext();
			CostantiPdD.addKeywordInCache(this.msgDiag, this.esitoModiTokenAuditInCache, pddContextDevNull, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_MODI_TOKEN_AUDIT);
		}
		
		if(
			(codice.equals("001058") || codice.equals(RICEZIONE_BUSTE_AUTENTICAZIONE_FALLITA_OPZIONALE_CODICE) )
			&& 
			(this.dettaglioAutenticazioneFallita!=null) 
			){
			PdDContext pddContextDevNull = new PdDContext();
			CostantiPdD.addKeywordAutenticazioneFallita(this.msgDiag, this.dettaglioAutenticazioneFallita, pddContextDevNull, CostantiPdD.KEY_INFO_IN_CACHE_FUNZIONE_AUTENTICAZIONE_FALLITA);
		}
		
		if(
			(codice.equals("003007") || codice.equals("007012") || codice.equals("007028")) 
				&&
			(this.rispostaLettaDallaCache) 
			){
			this.msgDiag.addKeyword(CostantiPdD.KEY_LOCATION, ConnettoreBase.LOCATION_CACHED);
		}
		
		/*
		 * Il messaggio contiene solo i singoli dettagli del soapFault.
		 * es. Risposta applicativa (SOAPFault faultCode[ns0:Server.OpenSPCoopExampleFault] faultActor[OpenSPCoopTrace] faultString[Fault ritornato dalla servlet di trace, esempio di OpenSPCoop]) consegnata al servizio applicativo con codice di trasporto ...
		 */
		if(codice.equals(RICEZIONE_CONTENUTI_APPLICATIVI_CONSEGNA_RISPOSTA_APPLICATIVA_OK_CODICE) || codice.equals(RICEZIONE_CONTENUTI_APPLICATIVI_CONSEGNA_RISPOSTA_APPLICATIVA_KO_CODICE) || codice.equals("001008") || codice.equals("001031") || codice.equals("001033") ){
			buildFaultInfo(log);
			if(this.mFaultRicostruitoIntegrazioneDescrizioneWithoutDetails!=null) {
				String descrizioneSoapFault = " ("+this.mFaultRicostruitoIntegrazioneDescrizioneWithoutDetails+")";
				this.msgDiag.getKeywordLogPersonalizzati().remove(CostantiPdD.KEY_SOAP_FAULT);
				this.msgDiag.addKeyword(CostantiPdD.KEY_SOAP_FAULT, descrizioneSoapFault);
			}
		}
		if(codice.equals("004011") || codice.equals("004012") || codice.equals("004014")){
			buildFaultInfo(log);
			if(this.mFaultRicostruitoCooperazioneDescrizioneWithoutDetails!=null){
				String descrizioneSoapFault = " ("+this.mFaultRicostruitoCooperazioneDescrizioneWithoutDetails+")";
				this.msgDiag.getKeywordLogPersonalizzati().remove(CostantiPdD.KEY_SOAP_FAULT);
				this.msgDiag.addKeyword(CostantiPdD.KEY_SOAP_FAULT, descrizioneSoapFault);
			}
		}
		
		if(codice.equals("003024") ){
			buildFaultInfo(log);
			if(this.mFaultRicostruitoCooperazione!=null){
				this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS_VALIDAZIONE_CONTENUTI,CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_SOAP_FAULT_PRESENTE);
			}
			else{
				if(this.msgDiag.getKeywordLogPersonalizzati().containsKey(CostantiPdD.KEY_SCENARIO_COOPERAZIONE_GESTITO)){
					String v = this.msgDiag.getKeywordLogPersonalizzati().get(CostantiPdD.KEY_SCENARIO_COOPERAZIONE_GESTITO);
					if(Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(v)){
						this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS_VALIDAZIONE_CONTENUTI,CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_SOAP_BODY_NON_PRESENTE);
					}
				}
			}
		}
		if(codice.equals("007024") ){
			buildFaultInfo(log);
			if(this.mFaultRicostruitoIntegrazione!=null){
				this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS_VALIDAZIONE_CONTENUTI,CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_SOAP_FAULT_PRESENTE);
			}
			else{
				if(this.msgDiag.getKeywordLogPersonalizzati().containsKey(CostantiPdD.KEY_SCENARIO_COOPERAZIONE_GESTITO)){
					String v = this.msgDiag.getKeywordLogPersonalizzati().get(CostantiPdD.KEY_SCENARIO_COOPERAZIONE_GESTITO);
					if(Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO.equals(v)){
						this.msgDiag.addKeyword(CostantiPdD.KEY_DETAILS_VALIDAZIONE_CONTENUTI,CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_SOAP_BODY_NON_PRESENTE);
					}
				}
			}
		}
		
		/*
		 * Il messaggi che indica l'utilizzo della funzionalita' di LocalForward richiede il nome della PA
		 * Tale nome non e' conosciuto nel contesto di PortaDelegata delle Transazione.
		 * Comunque non aggiunge una informazione importante, potendo essere recuperata dai dati sul servizio e azione invocata
		 **/
		if(codice.equals(RICEZIONE_CONTENUTI_APPLICATIVI_LOCAL_FORWARD_LOGINFO_CODICE)){
			this.msgDiag.addKeyword(CostantiPdD.KEY_PORTA_APPLICATIVA, "");
		}
		
		// autenticazione dei soggetti su PA
		String credenziali = this.msgDiag.getKeywordLogPersonalizzati().get(CostantiPdD.KEY_CREDENZIALI);
		if(codice.equals(RICEZIONE_BUSTE_AUTENTICAZIONE_IN_CORSO_CODICE) || //ricezioneBuste.autenticazioneInCorso
				codice.equals(RICEZIONE_BUSTE_AUTENTICAZIONE_EFFETTUATA_CODICE) || //ricezioneBuste.autenticazioneEffettuata
				codice.equals(RICEZIONE_BUSTE_AUTENTICAZIONE_FALLITA_CODICE) || //ricezioneBuste.autenticazioneFallita
				codice.equals(RICEZIONE_BUSTE_AUTENTICAZIONE_FALLITA_OPZIONALE_CODICE)  //ricezioneBuste.autenticazioneFallita.opzionale
				) {
			if(credenziali!=null && !"".equals(credenziali)) {
				this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, credenziali);
			}
			else{
				this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, "");
			}
		}
		else {
			if(credenziali!=null && !"".equals(credenziali)) {
				this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, " credenzialiMittente "+credenziali);
			}
			else{
				this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, "");
			}
		}
		
		if(codice.equals(RICEZIONE_BUSTE_AUTENTICAZIONE_EFFETTUATA_CODICE)){
			this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, ""); // per evitare di visualizzarle anche nei successivi diagnostici
			this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI, "");
		}
		else if(codice.equals("001056")){
			this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_SA_FRUITORE, ""); // per evitare di visualizzarle anche nei successivi diagnostici
		}
		
		// Le credenziali del servizio applicativo non vengono più visualizzate se è stata effettuata l'autenticazione
		// Questo processo avviene dal diagnosticio con codice 001003 in poi sulla porta delegata
		if(codice.equals("001003")){
			String autenticazione = this.msgDiag.getKeywordLogPersonalizzati().get(CostantiPdD.KEY_TIPO_AUTENTICAZIONE);
			if(autenticazione!=null && !"".equals(autenticazione) && !"-".equals(autenticazione) && !"none".equals(autenticazione)) {
				// in caso di autenticazione effettuata questo diagnostico non contiene le credenziali
				this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_SA_FRUITORE, "");
			}
		}
		
		// Le credenziali del soggetto non vengono più visualizzate se è stata effettuata l'autenticazione
		// Questo processo avviene dal diagnosticio con codice 004001 in poi sulla porta applicativa
		if(codice.equals("004001")){
			String autenticazione = this.msgDiag.getKeywordLogPersonalizzati().get(CostantiPdD.KEY_TIPO_AUTENTICAZIONE);
			if(autenticazione!=null && !"".equals(autenticazione) && !"-".equals(autenticazione) && !"none".equals(autenticazione)) {
				// in caso di autenticazione effettuata questo diagnostico non contiene le credenziali
				this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI, "");
				this.msgDiag.addKeyword(CostantiPdD.KEY_CREDENZIALI_MITTENTE_MSG, "");
			}
		}
			
		String codiceConsegnaRichiesta = null;
		String codiceConsegnaRisposta = null;
		if( RICEZIONE_CONTENUTI_APPLICATIVI_CODICE_MODULO.equals(codiceModulo)  // ricezioneContenutiApplicativi
				||
				RICEZIONE_BUSTE_CODICE_MODULO.equals(codiceModulo)  // ricezioneBuste
				
				){
			Object o = this.info.getDato(MappingRicostruzioneDiagnostici.CODICE_TRASPORTO_RISPOSTA).getDato();
			codiceConsegnaRisposta = (o!=null ? (String) o : null );
			this.msgDiag.addKeyword(CostantiPdD.KEY_CODICE_CONSEGNA, codiceConsegnaRisposta);
		}
		else if (INOLTRO_BUSTE_CODICE_MODULO.equals(codiceModulo)  // inoltroBuste
				||
				CONSEGNA_CONTENUTI_APPLICATIVI_CODICE_MODULO.equals(codiceModulo)  // consegnaContenutiApplicativi
				){
			Object o = this.info.getDato(MappingRicostruzioneDiagnostici.CODICE_TRASPORTO_RICHIESTA).getDato();
			codiceConsegnaRichiesta = (o!=null ? (String) o : null );
			this.msgDiag.addKeyword(CostantiPdD.KEY_CODICE_CONSEGNA, codiceConsegnaRichiesta);
			
			String erroreConsegna = "errore di trasporto, codice "+codiceConsegnaRichiesta;
			if(this.mFaultRicostruitoCooperazione!=null || this.mFaultRicostruitoIntegrazione!=null){
				
				boolean faultProtocollo = false;
				if(codice.equals(INOLTRO_BUSTE_INOLTRO_CON_ERRORE_CODICE)){
					try{
						EsitoTransazioneName name = EsitoTransazioneName.convertoTo(EsitiProperties.getInstanceFromProtocolName(log, this.transazioneDTO.getProtocollo()).getEsitoName(this.transazioneDTO.getEsito()));
						if(EsitoTransazioneName.ERRORE_PROTOCOLLO.equals(name)){
							faultProtocollo = true;
						}
					}catch(Exception e){
						log.error("Errore durante la comprensione dell'esito ["+this.transazioneDTO.getEsito()+"]: "+e.getMessage(),e);
					}
				}
				
				if(faultProtocollo){
					
					boolean set = false;
					OpenSPCoop2Message msg = null;
					if(INOLTRO_BUSTE_CODICE_MODULO.equals(codiceModulo)){
						msg = this.mFaultRicostruitoCooperazione;
					}
					else if(CONSEGNA_CONTENUTI_APPLICATIVI_CODICE_MODULO.equals(codiceModulo)){
						msg = this.mFaultRicostruitoIntegrazione;
					}
					if(msg!=null && ServiceBinding.SOAP.equals(msg.getServiceBinding())){
						SOAPFault soapFault = null;
						if(msg.castAsSoap().getSOAPBody()!=null){
							soapFault = msg.castAsSoap().getSOAPBody().getFault();
						}
						if(soapFault!=null && soapFault.getFaultString()!=null){
							if( soapFault.getFaultString().equals(this.protocolFactory.createTraduttore().toString(MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE)) ){
								set = true;
								erroreConsegna = soapFault.getFaultString();
								this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO_BUSTA_ERRORE, CostantiPdD.TIPO_MESSAGGIO_BUSTA_ERRORE_INTESTAZIONE);
							}
							else if( soapFault.getFaultString().equals(this.protocolFactory.createTraduttore().toString(MessaggiFaultErroreCooperazione.FAULT_STRING_PROCESSAMENTO)) ){
								set = true;
								erroreConsegna = soapFault.getFaultString();
								this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_MESSAGGIO_BUSTA_ERRORE, CostantiPdD.TIPO_MESSAGGIO_BUSTA_ERRORE_PROCESSAMENTO);
							}
						}
						
						if(set){
							// abbiamo un soap fault. Controllo il detail per i successivi diagnostici
							DettaglioEccezione dettaglioEccezione = XMLUtils.getDettaglioEccezione(log,msg);
							if(dettaglioEccezione!=null){
								this.msgDiag.addKeyword(CostantiPdD.KEY_OPENSPCOOP2_DETAILS, XMLUtils.toString(dettaglioEccezione));
							}
						}
					}
					if(!set){
						if(INOLTRO_BUSTE_CODICE_MODULO.equals(codiceModulo) && this.mFaultRicostruitoCooperazioneDescrizioneWithDetails!=null){
							erroreConsegna = erroreConsegna + " (" +this.mFaultRicostruitoCooperazioneDescrizioneWithDetails+ ")";
						}
						if(CONSEGNA_CONTENUTI_APPLICATIVI_CODICE_MODULO.equals(codiceModulo) && this.mFaultRicostruitoIntegrazioneDescrizioneWithDetails!=null){
							erroreConsegna = erroreConsegna + " (" +this.mFaultRicostruitoIntegrazioneDescrizioneWithDetails+ ")";
						}
					}
				}
				else{
					if(INOLTRO_BUSTE_CODICE_MODULO.equals(codiceModulo) && this.mFaultRicostruitoCooperazioneDescrizioneWithDetails!=null){
						erroreConsegna = erroreConsegna + " (" +this.mFaultRicostruitoCooperazioneDescrizioneWithDetails+ ")";
					}
					if(CONSEGNA_CONTENUTI_APPLICATIVI_CODICE_MODULO.equals(codiceModulo) && this.mFaultRicostruitoIntegrazioneDescrizioneWithDetails!=null){
						erroreConsegna = erroreConsegna + " (" +this.mFaultRicostruitoIntegrazioneDescrizioneWithDetails+ ")";
					}
				}
			}
			this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, erroreConsegna);
		}
		else{
			this.msgDiag.addKeyword(CostantiPdD.KEY_CODICE_CONSEGNA, null);
		}
		
		if(dynamic){
			DynamicExtendedInfoDiagnosticoType type = DynamicExtendedInfoDiagnosticoType.getEnum(dynamicType);
			switch (type) {
			case NON_RICOSTRUIBILE:
				// nop
				break;
			case POLICY_CONTROLLO_TRAFFICO:
				Map<String, String> map = DynamicExtendedInfoDiagnosticoControlloTraffico.convertToProperties(dynamicValue);
				if(map!=null && map.size()>0){
					this.msgDiag.getKeywordLogPersonalizzati().putAll(map);
				}
				break;
			default:
				break;
			}
		}
	}
	
	
	

	
	// ***************** BUILD FROM DATI SIMULATI ********************
	
	public List<MsgDiagnostico> build(Logger log) throws UtilsException{
		
		List<MsgDiagnostico> msgDiagnosticiNormali = new ArrayList<>();
		if(this.info.getDiagnostici()!=null && !this.info.getDiagnostici().isEmpty()){
			for (InfoDiagnostico infoMsgDiag : this.info.getDiagnostici()) {
				
				MsgDiagnostico m = new MsgDiagnostico();
				
				String codice = infoMsgDiag.getCode();
				String codiceModulo = codice.substring(0, 3);
				
				try{
					this.buildFault(log, codice, codiceModulo);
					this.buildContextForSingleMsgDiagnostico(codice,codiceModulo,false,
							null,null,
							log);
				}catch(Throwable t){
					throw new UtilsException("build context error: "+t.getMessage(),t);
				}
				
				this.setDatiEngine(m, codice, codiceModulo, infoMsgDiag, null);
				
				msgDiagnosticiNormali.add(m);
			}
		}
		
		if(this.info.getDiagnosticiExt()!=null && !this.info.getDiagnosticiExt().isEmpty()){
			List<MsgDiagnostico> msgDiagnosticiCompleted = new ArrayList<>();
			
			List<String> positions = new ArrayList<>();
			Map<String, InfoDiagnostico> mapPositionToInfoDiagnostico = new HashMap<>();
			Map<String, DynamicExtendedInfoDiagnostico> mapPositionToDynamicExtendedInfoDiagnostico = new HashMap<>();
			
			// nota: devo ordinarli per position, poichè vengono serializzati prima i diag ext di un tipo, poi di un altro.
			// esempio prima i CT (controlloTraffico) poi quelli non ricostruibili
			
			for (int i = 0; i < this.info.getDiagnosticiExt().size(); i++) {
				
				InfoDiagnostico infoMsgDiag = this.info.getDiagnosticiExt().get(i);
				DynamicExtendedInfoDiagnostico d = this.info.getDatiExt().get(i);
				
				String keyPosition = null;
				if(d.getDiagnosticPosition()<10){
					keyPosition = "000"+d.getDiagnosticPosition();
				}
				else if(d.getDiagnosticPosition()<100){
					keyPosition = "00"+d.getDiagnosticPosition();
				}
				else if(d.getDiagnosticPosition()<1000){
					keyPosition = "0"+d.getDiagnosticPosition();
				}
				else{
					keyPosition = ""+d.getDiagnosticPosition();
				}
				
				positions.add(keyPosition);
				mapPositionToInfoDiagnostico.put(keyPosition, infoMsgDiag);
				mapPositionToDynamicExtendedInfoDiagnostico.put(keyPosition, d);
				
			}
			
			Collections.sort(positions);
			
			for (int i = 0; i < positions.size(); i++) {
			
				String keyPosition = positions.get(i);
				InfoDiagnostico infoMsgDiag = mapPositionToInfoDiagnostico.remove(keyPosition);
				DynamicExtendedInfoDiagnostico d = mapPositionToDynamicExtendedInfoDiagnostico.remove(keyPosition);
				
				MsgDiagnostico m = new MsgDiagnostico();
				
				String codice = infoMsgDiag.getCode();
				String codiceModulo = codice.substring(0, 3);
				
				try{
					this.buildFault(log, codice, codiceModulo);
					this.buildContextForSingleMsgDiagnostico(codice,codiceModulo,true,
							d.getType().getValue(),d.getValue(),
							log);
				}catch(Throwable t){
					throw new UtilsException("build context error: "+t.getMessage(),t);
				}
				
				this.setDatiEngine(m, codice, codiceModulo, infoMsgDiag, d);
				
				while(msgDiagnosticiCompleted.size()<d.getDiagnosticPosition() && !msgDiagnosticiNormali.isEmpty()){
					msgDiagnosticiCompleted.add(msgDiagnosticiNormali.remove(0));
				}
				
				msgDiagnosticiCompleted.add(m);
			}
			
			while(!msgDiagnosticiNormali.isEmpty()){
				msgDiagnosticiCompleted.add(msgDiagnosticiNormali.remove(0));
			}
			
			return msgDiagnosticiCompleted;
			
		}
		else{
			return msgDiagnosticiNormali;
		}
	}
	
	private void setDatiEngine(MsgDiagnostico msgDiag,String codice, String codiceModulo,
			InfoDiagnostico infoMsgDiag,
			DynamicExtendedInfoDiagnostico d) throws UtilsException{
		
		PropertiesReader pr = MsgDiagnosticiProperties.getMappingCodiceToKeywordMsgDiagnosticiPersonalizzati();
		PropertiesReader prSeverita = MsgDiagnosticiProperties.getLivelliMsgDiagnosticiPersonalizzati();
		
		MsgDiagnosticiProperties msgDiagProp = MsgDiagnosticiProperties.getInstance();
		boolean setSeverita = true;
		if(msgDiagProp.getCodiceDiagnosticoDebugHigh().equals(codice)){
			setSeverita = false;
			msgDiag.setSeverita(LogLevels.SEVERITA_DEBUG_HIGH);
		}
		else if(msgDiagProp.getCodiceDiagnosticoDebugMedium().equals(codice)){
			setSeverita = false;
			msgDiag.setSeverita(LogLevels.SEVERITA_DEBUG_MEDIUM);
		}
		
		String nomeCompletoDiagnostico = pr.getValue(codice);
		
		msgDiag.setCodice(codice);
		
		msgDiag.setGdo(infoMsgDiag.getGdo());
		
		msgDiag.setIdBusta(this.transazioneDTO.getIdMessaggioRichiesta());
		msgDiag.setIdBustaRisposta(this.transazioneDTO.getIdMessaggioRisposta());
		
		msgDiag.setIdFunzione(this.getIdentificativoFunzione(codiceModulo));
		
		IDSoggetto idDominio = new IDSoggetto(this.transazioneDTO.getPddTipoSoggetto(), this.transazioneDTO.getPddNomeSoggetto(), this.transazioneDTO.getPddCodice());
		msgDiag.setIdSoggetto(idDominio);
		
		if(d!=null && DynamicExtendedInfoDiagnosticoType.NON_RICOSTRUIBILE.equals(d.getType())){
			msgDiag.setMessaggio(DynamicExtendedInfoDiagnosticoNonRicostruibileUtils.convertToHumanMessage(d.getValue()));
		}else{
			msgDiag.setMessaggio(this.msgDiag.getMessaggio_replaceKeywords(nomeCompletoDiagnostico));
		}
		
		String idTransazionePresente = msgDiag.getProperty(CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID_TRANSAZIONE);
		if(idTransazionePresente==null){
			msgDiag.addProperty(CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID_TRANSAZIONE, this.transazioneDTO.getIdTransazione());
		}
		
		msgDiag.setProtocollo(this.transazioneDTO.getProtocollo());
			
		if(setSeverita){
			String sev = (String) prSeverita.get(nomeCompletoDiagnostico);
			int sevI = 0;
			try{
				sevI = Integer.parseInt(sev);
			}catch(Exception e){
				throw new UtilsException("Errore durante il parsing della severità ["+sev+"] per il diagnostico ["+nomeCompletoDiagnostico+"] code ["+codice+"]");
			}
			msgDiag.setSeverita( (sevI) / 10000);
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	// *********** U T I L S **********************

	private String getIdentificativoFunzione(String codice) throws UtilsException{
		PropertiesReader pr = MsgDiagnosticiProperties.getCodiciIdentificativiFunzione();
		Enumeration<?> keys = pr.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = pr.getValue(key);
			/**System.out.println("code["+key+"] name["+value+"]");*/
			if(value.equals(codice)){
				return key;
			}
		}
		/**return null;*/
		// Fix: non devo ritornare null sennò poi la serializzazione va in errore
		return "core";
	}
}
