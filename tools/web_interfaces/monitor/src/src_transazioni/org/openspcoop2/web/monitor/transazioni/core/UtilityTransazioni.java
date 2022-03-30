/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.transazioni.core;

import java.io.OutputStream;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.core.transazioni.DumpHeaderAllegato;
import org.openspcoop2.core.transazioni.DumpHeaderTrasporto;
import org.openspcoop2.core.transazioni.DumpMultipartHeader;
import org.openspcoop2.core.transazioni.constants.RuoloTransazione;
import org.openspcoop2.core.transazioni.constants.TipoAPI;
import org.openspcoop2.core.transazioni.utils.TransactionContentUtils;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.xml.XMLUtils;
import org.openspcoop2.web.monitor.core.constants.TipologiaRicerca;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneApplicativoServerBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioniSearchForm;
import org.openspcoop2.web.monitor.transazioni.core.contents.ContentType;
import org.openspcoop2.web.monitor.transazioni.core.contents.RisorsaType;
import org.openspcoop2.web.monitor.transazioni.core.header.HeaderType;
import org.openspcoop2.web.monitor.transazioni.core.manifest.DiagnosticaSerializationType;
import org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType;
import org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType.Api;
import org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType.Api.Tags;
import org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType.Digest;
import org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType.Duplicati;
import org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType.Profilo;
import org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType.ProfiloAsincrono;
import org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType.ProfiloAsincrono.ServizioCorrelato;
import org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType.Servizio;
import org.openspcoop2.web.monitor.transazioni.core.manifest.RuoloType;
import org.openspcoop2.web.monitor.transazioni.core.manifest.SoggettoType;
import org.openspcoop2.web.monitor.transazioni.core.manifest.TipoPortaType;
import org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType.Contesto;
import org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType.CorrelazioneApplicativa;
import org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType.DatiIntegrazione;
import org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType.Diagnostica;
import org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType.DimensioneMessaggi;
import org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType.Dominio;
import org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType.Esito;
import org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType.IntegrationManager;
import org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType.TempiAttraversamento;
import org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType.DatiConsegna;
import org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType.DatiConsegna.UltimoErrore;
import org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType.DatiIntegrationManager;
import org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType.Esito.DettaglioEsito;
import org.openspcoop2.web.monitor.transazioni.core.search.FiltroContenuti;
import org.openspcoop2.web.monitor.transazioni.core.search.FiltroContenuti.Risorsa;
import org.openspcoop2.web.monitor.transazioni.core.search.RicercaPersonalizzata;
import org.openspcoop2.web.monitor.transazioni.core.search.TipologiaRicercaTransazioniType;
import org.openspcoop2.web.monitor.transazioni.core.search.TipologiaTransazioniType;
import org.openspcoop2.web.monitor.transazioni.core.search.TransazioneType.IdApplicativo;
import org.openspcoop2.web.monitor.transazioni.core.search.TransazioneType.Periodo;
import org.openspcoop2.web.monitor.transazioni.core.search.TransazioneType.SoggettoDestinatario;
import org.openspcoop2.web.monitor.transazioni.core.search.TransazioneType.SoggettoLocale;
import org.openspcoop2.web.monitor.transazioni.core.search.TransazioneType.SoggettoMittente;
import org.openspcoop2.web.monitor.transazioni.core.search.TransazioneType.SoggettoRemoto;
import org.openspcoop2.web.monitor.transazioni.core.search.TransazioneType.TransazioniIdentificate;

/**
 * UtilityTransazioni
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class UtilityTransazioni {
	private final static double KB = 1024;
	private final static double MB = 1048576;

	private final static String FRUIZIONE = "Fruizione";
	private final static String EROGAZIONE = "Erogazione";
	private final static String ROUTER = "Router";
	private final static String IM = "I.M.";

	public final static String DIAGNOSTIC_WITH_DYNAMIC_INFO_DIAG_SEPARATOR = MsgDiagnosticiProperties.DIAGNOSTIC_WITH_DYNAMIC_INFO_DIAG_SEPARATOR;
	public final static String DIAGNOSTIC_WITH_DYNAMIC_INFO_TYPE_SEPARATOR = MsgDiagnosticiProperties.DIAGNOSTIC_WITH_DYNAMIC_INFO_TYPE_SEPARATOR;
	
	/**
	 * Formatta una stringa contenente il riepilogo della transazione
	 * 
	 * ------------------------------------------------------------------------
	 * Tipologia: tipo_porta Protocollo: protocollo PdD (ID):
	 * identificativo_porta PdD (Soggetto): tipo_soggetto/nome_soggetto Esito:
	 * esito Fruitore (PdD ID): identificativo_porta fruitore Fruitore
	 * (Soggetto): tipo_soggetto_fruitore/soggetto_fruitore Erogatore (PdD ID):
	 * identificativo_porta erogatore Erogatore (Soggetto):
	 * tipo_soggetto_erogatore/soggetto_erogatore Servizio:
	 * tipo_servizio/nome_servizio Versione Servizio: versione_servizio Azione:
	 * azione ID Messaggio richiesta: idegov_richiesta ID Messaggio risposta:
	 * idegov_risposta Profilo: ruolo ID collaborazione: id_collaborazione ID
	 * applicativa richiesta: id_correlazione_applicativa ID applicativa
	 * risposta: id_correlazione_applicativa risposta Servizio applicativo:
	 * servizio_applicativo ----
	 * ----------------------------------------------------------------------
	 * --------------------------
	 */
	public static String getHeaderTransazione(TransazioneBean t) {
		StringBuilder sb = new StringBuilder();

		if (StringUtils.isNotEmpty(t.getPddRuolo().toString())) {
			sb.append("Tipologia: ");
			switch (t.getPddRuolo()) {
			case APPLICATIVA:
				sb.append(UtilityTransazioni.EROGAZIONE);
				break;
			case DELEGATA:
				sb.append(UtilityTransazioni.FRUIZIONE);
				break;
			case INTEGRATION_MANAGER:
				sb.append(UtilityTransazioni.IM);
				break;
			case ROUTER:
				sb.append(UtilityTransazioni.ROUTER);
				break;
			}

			sb.append("\n");
		}

		if (StringUtils.isNotEmpty(t.getProtocollo())) {
			sb.append("Protocollo: ");
			sb.append(t.getProtocollo());
			sb.append("\n");
		}

		if (StringUtils.isNotEmpty(t.getPddCodice())) {
			sb.append("PdD (ID): ");
			sb.append(t.getPddCodice());
			sb.append("\n");
		}

		if (StringUtils.isNotEmpty(t.getPddTipoSoggetto())
				&& StringUtils.isNotEmpty(t.getPddNomeSoggetto())) {
			sb.append("PdD (Soggetto): ");
			sb.append(t.getPddTipoSoggetto()).append("/")
					.append(t.getPddNomeSoggetto());
			sb.append("\n");
		}

		if (t.getEsito() >= 0) {
			sb.append("Esito: ");

			switch (t.getEsito()) {
			case 0:
				sb.append("Ok");
				break;
			case 1:
				sb.append("Errore Transazione Protocollo");
				break;
			case 2:
				sb.append("Errore Transazione Applicativa");
				break;
			case 4:
				sb.append("Errore Processamento PDD 4XX");
				break;
			case 5:
				sb.append("Errore Processamento PDD 5XX");
				break;

			case 6:
				sb.append("Autenticazione Fallita [IM]");
				break;

			case 7:
				sb.append("Autorizzazione Fallita [IM]");
				break;

			case 8:
				sb.append("Messaggi non Presenti [IM]");
				break;

			case 9:
				sb.append("Messaggio non Trovato [IM]");
				break;
			default:
				sb.append("Errore Generico");
				break;
			}
			sb.append("\n");
		}

		if (StringUtils.isNotEmpty(t.getIdportaSoggettoFruitore())) {
			sb.append("Fruitore (PdD ID): ");
			sb.append(t.getIdportaSoggettoFruitore());
			sb.append("\n");
		}

		if (StringUtils.isNotEmpty(t.getTipoSoggettoFruitore())) {
			sb.append("Fruitore (Soggetto): ");
			sb.append(t.getTipoSoggettoFruitore());
			sb.append("/");
			sb.append(t.getNomeSoggettoFruitore());
			sb.append("\n");
		}

		if (StringUtils.isNotEmpty(t.getIdportaSoggettoErogatore())) {
			sb.append("Erogatore (PdD ID): ");
			sb.append(t.getIdportaSoggettoErogatore());
			sb.append("\n");
		}

		if (StringUtils.isNotEmpty(t.getTipoSoggettoErogatore())) {
			sb.append("Erogatore (Soggetto): ");
			sb.append(t.getTipoSoggettoErogatore());
			sb.append("/");
			sb.append(t.getNomeSoggettoErogatore());
			sb.append("\n");
		}

		if (StringUtils.isNotEmpty(t.getTipoServizio())) {
			sb.append("Servizio: ");
			sb.append(t.getTipoServizio());
			sb.append("/");
			sb.append(t.getNomeServizio());
			sb.append("\n");
		}

		if (StringUtils.isNotEmpty("" + t.getVersioneServizio())) {
			sb.append("Versione Servizio: ");
			sb.append(t.getVersioneServizio());
			sb.append("\n");
		}

		if (StringUtils.isNotEmpty(t.getAzione())) {
			sb.append("Azione: ");
			sb.append(t.getAzione());
			sb.append("\n");
		}
		if (StringUtils.isNotEmpty(t.getIdMessaggioRichiesta())) {
			sb.append("ID Messaggio richiesta: ");
			sb.append(t.getIdMessaggioRichiesta());
			sb.append("\n");
		}
		if (StringUtils.isNotEmpty(t.getIdMessaggioRisposta())) {
			sb.append("ID Messaggio risposta: ");
			sb.append(t.getIdMessaggioRisposta());
			sb.append("\n");
		}
		if (t.getRuoloTransazione() >= 0) {
			RuoloTransazione ruolo = RuoloTransazione.toEnumConstant(t.getRuoloTransazione());
			if(ruolo!=null){
				sb.append("Profilo: ");
				sb.append(ruolo.name());
				sb.append("\n");
			}
		}
		if (StringUtils.isNotEmpty(t.getIdCollaborazione())) {
			sb.append("ID collaborazione: ");
			sb.append(t.getIdCollaborazione());
			sb.append("\n");
		}
		if (StringUtils.isNotEmpty(t.getIdCorrelazioneApplicativa())) {
			sb.append("ID Applicativa Richiesta: ");
			sb.append(t.getIdCorrelazioneApplicativa());
			sb.append("\n");
		}
		if (StringUtils.isNotEmpty(t.getIdCorrelazioneApplicativaRisposta())) {
			sb.append("ID Applicativa Risposta: ");
			sb.append(t.getIdCorrelazioneApplicativaRisposta());
			sb.append("\n");
		}

		if (StringUtils.isNotEmpty(t.getServizioApplicativoFruitore())) {
			sb.append("Servizio applicativo fruitore: ");
			sb.append(t.getServizioApplicativoFruitore());
			sb.append("\n");
		}
		if (StringUtils.isNotEmpty(t.getServizioApplicativoErogatore())) {
			sb.append("Servizio applicativo erogatore: ");
			sb.append(t.getServizioApplicativoErogatore());
			sb.append("\n");
		}

		if (sb.length() > 0) {
			sb.insert(0, "<!-- \n");
			sb.append(" -->\n\n");
		}

		return sb.toString();
	}

	public static void writeManifestTransazione(TransazioneBean t, OutputStream out)
			throws Exception {
		PddMonitorProperties monitorProperties = PddMonitorProperties.getInstance(LoggerManager.getPddMonitorCoreLogger());
		UtilityTransazioni.writeManifestTransazione(t, out, monitorProperties.isAttivoTransazioniDataAccettazione());
	}
	public static void writeManifestTransazione(TransazioneBean t, OutputStream out, boolean isAttivoTransazioniDataAccettazione)
			throws Exception {
		/*
		 * <transazione xmlns="http://www.spcoopit.it/PdS/Transaction/Manifest">
		 * <tipo-porta>tipo_porta</tipo-porta>
		 * <identificativo-porta>identificativo_porta</identificativo-porta>
		 * <esito>ESITO_CALCOLATO_IN_FORMA_LEGGIBILE_DA_UMANO</esito>
		 * <nome-protocollo> <fruitore tipo=”tipo”>nome</fruitore> <erogatore
		 * tipo=”tipo”>nome</erogatore> <servizio tipo=”tipo”>nome</servizio>
		 * <azione>nome</azione>
		 * <id-messaggio-richiesta>ID</id-messaggio-richiesta>
		 * <id-messaggio-risposta>ID</id-messaggio-risposta>
		 * <profilo>VALORE_CALCOLATO_DA_ENUM</profilo>
		 * <id-collaborazione>COLLABORAZIONE</id-collaborazione>
		 * <id-correlazione-applicativa>ID</id-correlazione-applicativa>
		 * <profilo-asincrono id=”Campo id_asincrono” servizio-correlato=”Campo
		 * omonimo”/> <duplicati richiesta=”” risposta=””/> </nome-protocollo>
		 * <servizio-applicativo>nome</servizio-applicativo>
		 * <tempi-attraversamento richiesta_ingresso=”data in forma yyyy-mm-dd
		 * hh:MM:ss.SSS” richiesta_uscita=”xxx” risposta_ingresso=”xxx”
		 * risposta_uscita=”xxx” /> <dimensione-messaggi
		 * richiesta_ingresso=”DimensioneFormattataPrecisa: es 10.23MB”
		 * richiesta_uscita=”xxx” risposta_ingresso=”xxx” risposta_uscita=”xxx”
		 * /> <integration-manager operazione=”campo operazione_im” /> <location
		 * richiesta=”” risposta=””/> </transazione>
		 */

		JAXBContext jc = JAXBContext
				.newInstance(org.openspcoop2.web.monitor.transazioni.core.manifest.ObjectFactory.class.getPackage().getName());

		Marshaller marshaller = jc.createMarshaller();
		// marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		org.openspcoop2.web.monitor.transazioni.core.manifest.ObjectFactory objFactory = new org.openspcoop2.web.monitor.transazioni.core.manifest.ObjectFactory();

		org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType transazione = new org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType();

		// Stato di una transazione (marca la transazione con uno stato tramite la configurazione plugin)
		if(t.getStato()!=null && StringUtils.isNotEmpty(t.getStato())){
			transazione.setStato(UtilityTransazioni.escapeXmlValue(t.getStato()));
		}
		
		// Ruolo della Transazione
		/*
		 *  sconosciuto (-1)
		 *	invocazioneOneway (1)
		 *	invocazioneSincrona (2)
		 *	invocazioneAsincronaSimmetrica (3)
		 *	rispostaAsincronaSimmetrica (4)
		 *	invocazioneAsincronaAsimmetrica (5)
		 *	richiestaStatoAsincronaAsimmetrica (6)
		 *	integrationManager (7)
		 **/
		if(t.getRuoloTransazione()==-1){
			transazione.setRuolo(RuoloType.SCONOSCIUTO);
		}
		else if(t.getRuoloTransazione()==1){
			transazione.setRuolo(RuoloType.INVOCAZIONE_ONEWAY);
		}
		else if(t.getRuoloTransazione()==2){
			transazione.setRuolo(RuoloType.INVOCAZIONE_SINCRONA);
		}
		else if(t.getRuoloTransazione()==3){
			transazione.setRuolo(RuoloType.INVOCAZIONE_ASINCRONA_SIMMETRICA);
		}
		else if(t.getRuoloTransazione()==4){
			transazione.setRuolo(RuoloType.RISPOSTA_ASINCRONA_SIMMETRICA);
		}
		else if(t.getRuoloTransazione()==5){
			transazione.setRuolo(RuoloType.INVOCAZIONE_ASINCRONA_ASIMMETRICA);
		}
		else if(t.getRuoloTransazione()==6){
			transazione.setRuolo(RuoloType.RICHIESTA_STATO_ASINCRONA_ASIMMETRICA);
		}
		else if(t.getRuoloTransazione()==7){
			transazione.setRuolo(RuoloType.INTEGRATION_MANAGER);
		}
		
		
		EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),t.getProtocollo());
		
		// Esito di una transazione
		if (t.getEsito() >= 0) {
			// <esito>ESITO_CALCOLATO_IN_FORMA_LEGGIBILE_DA_UMANO</esito>

			Esito esito = new Esito();
			esito.setCodice(new BigInteger(t.getEsito()+""));
			esito.setValue(UtilityTransazioni.escapeXmlValue(esitiProperties.getEsitoLabel(t.getEsito())));
			if(t.getConsegneMultipleInCorso()>0) {
				esito.setConsegneMultiple(new BigInteger(t.getConsegneMultipleInCorso()+""));
			}
			transazione.setEsito(esito);
		}
		
		if(t.getEsitoContesto()!=null){
			
			Contesto contesto = new Contesto();
			contesto.setCodice(t.getEsitoContesto());
			contesto.setValue(UtilityTransazioni.escapeXmlValue(esitiProperties.getEsitoTransactionContextLabel(t.getEsitoContesto())));
			transazione.setContesto(contesto);
		}
		
		
		
        /*
        * Protocollo della transazione
        * - nome del protocollo
        * - soggetto fruitore
        * - soggetto erogatore
        * - servizio
        * - azione
        * - identificativi messaggi
        * - altre informazioni di protocollo
        * - identificativi asincroni
        * - digest
        * - duplicati
    	*/
		UtilityTransazioni.fillProtocollo(t,transazione);
		
		
		// Tempi di latenza
		if (t.getDataIngressoRichiesta() != null
				|| t.getDataUscitaRichiesta() != null
				|| t.getDataIngressoRisposta() != null
				|| t.getDataUscitaRisposta() != null) {

			TempiAttraversamento tempi = new TempiAttraversamento();

			try{
				if(isAttivoTransazioniDataAccettazione){
					if (t.getDataAccettazioneRichiesta() != null) {
						tempi.setRichiestaAccettazione(XMLUtils.getInstance().toGregorianCalendar(t.getDataAccettazioneRichiesta()));
					}
				}
				
				if (t.getDataIngressoRichiesta() != null) {
					tempi.setRichiestaIngresso(XMLUtils.getInstance().toGregorianCalendar(t.getDataIngressoRichiesta()));
				}
	
				if (t.getDataUscitaRichiesta() != null) {
					tempi.setRichiestaUscita(XMLUtils.getInstance().toGregorianCalendar(t.getDataUscitaRichiesta()));
				}
	
				if(isAttivoTransazioniDataAccettazione){
					if (t.getDataAccettazioneRisposta() != null) {
						tempi.setRispostaAccettazione(XMLUtils.getInstance().toGregorianCalendar(t.getDataAccettazioneRisposta()));
					}
				}
				
				if (t.getDataIngressoRisposta() != null) {
					tempi.setRispostaIngresso(XMLUtils.getInstance().toGregorianCalendar(t.getDataIngressoRisposta()));
				}
	
				if (t.getDataUscitaRisposta() != null) {
					tempi.setRispostaUscita(XMLUtils.getInstance().toGregorianCalendar(t.getDataUscitaRisposta()));
				}
				
				if(t.getDataUscitaRisposta()!=null && t.getDataIngressoRichiesta()!=null){
					long latenza = t.getDataUscitaRisposta().getTime() - t.getDataIngressoRichiesta().getTime();
					if(latenza>=0)
						tempi.setLatenzaTotale(latenza);
				}
				if(t.getDataUscitaRichiesta()!=null && t.getDataIngressoRisposta()!=null){
					long latenza = t.getDataIngressoRisposta().getTime() - t.getDataUscitaRichiesta().getTime();
					if(latenza>=0)
						tempi.setLatenzaServizio(latenza);
				}
				
				if(tempi.getLatenzaTotale()!=null && tempi.getLatenzaServizio()!=null){
					long latenza = tempi.getLatenzaTotale()-tempi.getLatenzaServizio();
					if(latenza>=0)
						tempi.setLatenzaPorta(latenza);
				}
				
				
			}catch(Exception e){
				throw new JAXBException(e.getMessage(),e);
			}

			transazione.setTempiAttraversamento(tempi);
		}
		
		
		// Dimensione messaggi gestiti
		if (t.getRichiestaIngressoBytes() != null
				|| t.getRichiestaUscitaBytes() != null
				|| t.getRispostaIngressoBytes() != null
				|| t.getRispostaUscitaBytes() != null) {

			DimensioneMessaggi dm = new DimensioneMessaggi();

			if (t.getRichiestaIngressoBytes() != null) {
				dm.setRichiestaIngresso(t.getRichiestaIngressoBytes());
			}

			if (t.getRichiestaUscitaBytes() != null) {
				dm.setRichiestaUscita(t.getRichiestaUscitaBytes());
			}

			if (t.getRispostaIngressoBytes() != null) {
				dm.setRispostaIngresso(t.getRispostaIngressoBytes());
			}

			if (t.getRispostaUscitaBytes() != null) {
				dm.setRispostaUscita(t.getRispostaUscitaBytes());
			}

			transazione.setDimensioneMessaggi(dm);
		}
		
		
		// Dati PdD
		if( 
				(t.getPddCodice()!=null && StringUtils.isNotEmpty(t.getPddCodice())) 
				||
				(t.getPddTipoSoggetto()!=null && StringUtils.isNotEmpty(t.getPddTipoSoggetto())) 
				||
				(t.getPddNomeSoggetto()!=null && StringUtils.isNotEmpty(t.getPddNomeSoggetto())) 
				||
				(t.getPddRuolo()!=null)
				||
				(t.getClusterId()!=null) 
			){
			Dominio dominio = new Dominio();
			if( 
					(t.getPddCodice()!=null && StringUtils.isNotEmpty(t.getPddCodice())) 
					||
					(t.getPddTipoSoggetto()!=null && StringUtils.isNotEmpty(t.getPddTipoSoggetto())) 
					||
					(t.getPddNomeSoggetto()!=null && StringUtils.isNotEmpty(t.getPddNomeSoggetto())) 
				){
				SoggettoType soggetto = new SoggettoType();
				if( 
						(t.getPddTipoSoggetto()!=null && StringUtils.isNotEmpty(t.getPddTipoSoggetto())) 
					){
					soggetto.setTipo(UtilityTransazioni.escapeXmlValue(t.getPddTipoSoggetto()));
				}
				if( 
						(t.getPddNomeSoggetto()!=null && StringUtils.isNotEmpty(t.getPddNomeSoggetto())) 
					){
					soggetto.setValue(UtilityTransazioni.escapeXmlValue(t.getPddNomeSoggetto()));
				}
				if( 
						(t.getPddCodice()!=null && StringUtils.isNotEmpty(t.getPddCodice())) 
					){
					soggetto.setIdentificativoPorta(UtilityTransazioni.escapeXmlValue(t.getPddCodice()));
				}
				dominio.setSoggetto(soggetto);
			}
			if( 
					(t.getPddRuolo()!=null) 
				){
				switch (t.getPddRuolo()) {
				case DELEGATA:
					dominio.setRuolo(TipoPortaType.DELEGATA);
					break;
				case APPLICATIVA:
					dominio.setRuolo(TipoPortaType.APPLICATIVA);
					break;
				case INTEGRATION_MANAGER:
					dominio.setRuolo(TipoPortaType.INTEGRATION_MANAGER);
					break;
				case ROUTER:
					dominio.setRuolo(TipoPortaType.ROUTER);
					break;
				}
			}
			dominio.setClusterId(t.getClusterId());
			transazione.setDominio(dominio);
		}
		
		
		// informazioni di diagnostica
		UtilityTransazioni.fillDiagnostica(t, transazione);
		
		
		// informazioni di integrazione (correlazione applicativa)		
		if( 
				(t.getIdCorrelazioneApplicativa()!=null && StringUtils.isNotEmpty(t.getIdCorrelazioneApplicativa())) 
				||
				(t.getIdCorrelazioneApplicativaRisposta()!=null && StringUtils.isNotEmpty(t.getIdCorrelazioneApplicativaRisposta())) 
			){
			CorrelazioneApplicativa correlazioneApplicativa = new CorrelazioneApplicativa();
			if( 
					(t.getIdCorrelazioneApplicativa()!=null && StringUtils.isNotEmpty(t.getIdCorrelazioneApplicativa())) 
				){
				correlazioneApplicativa.setIdRichiesta(UtilityTransazioni.escapeXmlValue(t.getIdCorrelazioneApplicativa()));
			}
			if( 
					(t.getIdCorrelazioneApplicativaRisposta()!=null && StringUtils.isNotEmpty(t.getIdCorrelazioneApplicativaRisposta())) 
				){
				correlazioneApplicativa.setIdRisposta(UtilityTransazioni.escapeXmlValue(t.getIdCorrelazioneApplicativaRisposta()));
			}
			transazione.setCorrelazioneApplicativa(correlazioneApplicativa);
		}
		
		
		// informazioni di integrazione (servizio applicativo)		
		if ( t.getServizioApplicativoFruitore()!=null && StringUtils.isNotEmpty(t.getServizioApplicativoFruitore())) {
			transazione.setServizioApplicativoFruitore(UtilityTransazioni.escapeXmlValue(t.getServizioApplicativoFruitore()));
		}
		if ( t.getServizioApplicativoErogatore()!=null && StringUtils.isNotEmpty(t.getServizioApplicativoErogatore())) {
			transazione.setServizioApplicativoErogatore(UtilityTransazioni.escapeXmlValue(t.getServizioApplicativoErogatore()));
		}
		
		// informazioni di integrazione (I.M.)
		if (t.getOperazioneIm()!=null && StringUtils.isNotEmpty(t.getOperazioneIm())) {
			IntegrationManager im = new IntegrationManager();
			im.setOperazione(UtilityTransazioni.escapeXmlValue(t.getOperazioneIm()));
			transazione.setIntegrationManager(im);
		}
		
		// location
		if( 
				(t.getLocationRichiesta()!=null && StringUtils.isNotEmpty(t.getLocationRichiesta())) 
				||
				(t.getLocationRisposta()!=null && StringUtils.isNotEmpty(t.getLocationRisposta())) 
				||
				(t.getNomePorta()!=null && StringUtils.isNotEmpty(t.getNomePorta())) 
				||
				(t.getCredenziali()!=null && StringUtils.isNotEmpty(t.getCredenziali())) 
				||
				(t.getLocationConnettore()!=null && StringUtils.isNotEmpty(t.getLocationConnettore())) 
			){
			DatiIntegrazione datiIntegrazione = new DatiIntegrazione();
			if( 
					(t.getLocationRichiesta()!=null && StringUtils.isNotEmpty(t.getLocationRichiesta())) 
				){
				datiIntegrazione.setIndirizzoRichiesta(UtilityTransazioni.escapeXmlValue(t.getLocationRichiesta()));
			}
			if( 
					(t.getLocationRisposta()!=null && StringUtils.isNotEmpty(t.getLocationRisposta())) 
				){
				datiIntegrazione.setIndirizzoRisposta(UtilityTransazioni.escapeXmlValue(t.getLocationRisposta()));
			}
			if( 
					(t.getNomePorta()!=null && StringUtils.isNotEmpty(t.getNomePorta())) 
				){
				datiIntegrazione.setNomePorta(UtilityTransazioni.escapeXmlValue(t.getNomePorta()));
			}
			if( 
					(t.getCredenziali()!=null && StringUtils.isNotEmpty(t.getCredenziali())) 
				){
				datiIntegrazione.setCredenziali(UtilityTransazioni.escapeXmlValue(t.getCredenziali()));
			}
			if( 
					(t.getLocationConnettore()!=null && StringUtils.isNotEmpty(t.getLocationConnettore())) 
				){
				datiIntegrazione.setConnettore(UtilityTransazioni.escapeXmlValue(t.getLocationConnettore()));
			}
			if( 
					(t.getUrlInvocazione()!=null && StringUtils.isNotEmpty(t.getUrlInvocazione())) 
				){
				datiIntegrazione.setUrlInvocazione(UtilityTransazioni.escapeXmlValue(t.getUrlInvocazione()));
			}
			if( 
					(t.getSocketClientAddress()!=null && StringUtils.isNotEmpty(t.getSocketClientAddress())) 
				){
				datiIntegrazione.setIndirizzoClient(UtilityTransazioni.escapeXmlValue(t.getSocketClientAddress()));
			}
			if( 
					(t.getTransportClientAddress()!=null && StringUtils.isNotEmpty(t.getTransportClientAddress())) 
				){
				datiIntegrazione.setXForwardedFor(UtilityTransazioni.escapeXmlValue(t.getTransportClientAddress()));
			}
			if( 
					(t.getTipoRichiesta()!=null && StringUtils.isNotEmpty(t.getTipoRichiesta())) 
				){
				datiIntegrazione.setTipoRichiesta(UtilityTransazioni.escapeXmlValue(t.getTipoRichiesta()));
			}
			if( 
					(t.getCodiceRispostaIngresso()!=null && StringUtils.isNotEmpty(t.getCodiceRispostaIngresso())) 
				){
				datiIntegrazione.setCodiceRispostaIngresso(UtilityTransazioni.escapeXmlValue(t.getCodiceRispostaIngresso()));
			}
			if( 
					(t.getCodiceRispostaUscita()!=null && StringUtils.isNotEmpty(t.getCodiceRispostaUscita())) 
				){
				datiIntegrazione.setCodiceRispostaUscita(UtilityTransazioni.escapeXmlValue(t.getCodiceRispostaUscita()));
			}
			
			if(StringUtils.isNotEmpty(t.getTrasportoMittenteLabel())) {
				datiIntegrazione.setIdentificativoAutenticato(UtilityTransazioni.escapeXmlValue(t.getTrasportoMittenteLabel()));
			}
			if(StringUtils.isNotEmpty(t.getTokenIssuerLabel())) {
				datiIntegrazione.setTokenIssuer(UtilityTransazioni.escapeXmlValue(t.getTokenIssuerLabel()));
			}
			if(StringUtils.isNotEmpty(t.getTokenClientIdLabel())) {
				datiIntegrazione.setTokenClientId(UtilityTransazioni.escapeXmlValue(t.getTokenClientIdLabel()));
			}
			if(StringUtils.isNotEmpty(t.getTokenSubjectLabel())) {
				datiIntegrazione.setTokenSubject(UtilityTransazioni.escapeXmlValue(t.getTokenSubjectLabel()));
			}
			if(StringUtils.isNotEmpty(t.getTokenUsernameLabel())) {
				datiIntegrazione.setTokenUsername(UtilityTransazioni.escapeXmlValue(t.getTokenUsernameLabel()));
			}
			if(StringUtils.isNotEmpty(t.getTokenMailLabel())) {
				datiIntegrazione.setTokenEMail(UtilityTransazioni.escapeXmlValue(t.getTokenMailLabel()));
			}
			
			
			transazione.setDatiIntegrazione(datiIntegrazione);
		}
		
		// eventi di gestione
		if( 
				(t.getEventiGestione()!=null && StringUtils.isNotEmpty(t.getEventiGestione())) 
			){
			transazione.setEventiGestione(UtilityTransazioni.escapeXmlValue(t.getEventiGestione()));
		}
		
		
		// uuid
		transazione.setUuid(t.getIdTransazione());
		

		marshaller.marshal(objFactory.createTransazione(transazione), out);
	}

	private static void fillProtocollo(TransazioneBean t,org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType transazione) throws Exception{
		
		ProtocolloType protocollo = null;

		// Soggetto Fruitore
		if( 
				(t.getTipoSoggettoFruitore()!=null && StringUtils.isNotEmpty(t.getTipoSoggettoFruitore())) 
				||
				(t.getNomeSoggettoFruitore()!=null && StringUtils.isNotEmpty(t.getNomeSoggettoFruitore())) 
				||
				(t.getIdportaSoggettoFruitore()!=null && StringUtils.isNotEmpty(t.getIdportaSoggettoFruitore())) 
				||
				(t.getIndirizzoSoggettoFruitore()!=null && StringUtils.isNotEmpty(t.getIndirizzoSoggettoFruitore())) 
			){
			if (protocollo == null) {
				protocollo = new ProtocolloType();
			}
			SoggettoType soggetto = new SoggettoType();
			if( 
					(t.getTipoSoggettoFruitore()!=null && StringUtils.isNotEmpty(t.getTipoSoggettoFruitore())) 
				){
				soggetto.setTipo(UtilityTransazioni.escapeXmlValue(t.getTipoSoggettoFruitore()));
			}
			if( 
					(t.getNomeSoggettoFruitore()!=null && StringUtils.isNotEmpty(t.getNomeSoggettoFruitore())) 
				){
				soggetto.setValue(UtilityTransazioni.escapeXmlValue(t.getNomeSoggettoFruitore()));
			}
			if( 
					(t.getIdportaSoggettoFruitore()!=null && StringUtils.isNotEmpty(t.getIdportaSoggettoFruitore())) 
				){
				soggetto.setIdentificativoPorta(UtilityTransazioni.escapeXmlValue(t.getIdportaSoggettoFruitore()));
			}
			if( 
					(t.getIndirizzoSoggettoFruitore()!=null && StringUtils.isNotEmpty(t.getIndirizzoSoggettoFruitore())) 
				){
				soggetto.setIndirizzo(UtilityTransazioni.escapeXmlValue(t.getIndirizzoSoggettoFruitore()));
			}
			protocollo.setFruitore(soggetto);
		}
		
		
		// Soggetto Erogatore
		if( 
				(t.getTipoSoggettoErogatore()!=null && StringUtils.isNotEmpty(t.getTipoSoggettoErogatore())) 
				||
				(t.getNomeSoggettoErogatore()!=null && StringUtils.isNotEmpty(t.getNomeSoggettoErogatore())) 
				||
				(t.getIdportaSoggettoErogatore()!=null && StringUtils.isNotEmpty(t.getIdportaSoggettoErogatore())) 
				||
				(t.getIndirizzoSoggettoErogatore()!=null && StringUtils.isNotEmpty(t.getIndirizzoSoggettoErogatore())) 
			){
			if (protocollo == null) {
				protocollo = new ProtocolloType();
			}
			SoggettoType soggetto = new SoggettoType();
			if( 
					(t.getTipoSoggettoErogatore()!=null && StringUtils.isNotEmpty(t.getTipoSoggettoErogatore())) 
				){
				soggetto.setTipo(UtilityTransazioni.escapeXmlValue(t.getTipoSoggettoErogatore()));
			}
			if( 
					(t.getNomeSoggettoErogatore()!=null && StringUtils.isNotEmpty(t.getNomeSoggettoErogatore())) 
				){
				soggetto.setValue(UtilityTransazioni.escapeXmlValue(t.getNomeSoggettoErogatore()));
			}
			if( 
					(t.getIdportaSoggettoErogatore()!=null && StringUtils.isNotEmpty(t.getIdportaSoggettoErogatore())) 
				){
				soggetto.setIdentificativoPorta(UtilityTransazioni.escapeXmlValue(t.getIdportaSoggettoErogatore()));
			}
			if( 
					(t.getIndirizzoSoggettoErogatore()!=null && StringUtils.isNotEmpty(t.getIndirizzoSoggettoErogatore())) 
				){
				soggetto.setIndirizzo(UtilityTransazioni.escapeXmlValue(t.getIndirizzoSoggettoErogatore()));
			}
			protocollo.setErogatore(soggetto);
		}
		
		// Servizio
		if( 
				(t.getTipoServizio()!=null && StringUtils.isNotEmpty(t.getTipoServizio())) 
				||
				(t.getNomeServizio()!=null && StringUtils.isNotEmpty(t.getNomeServizio())) 
				||
				(t.getVersioneServizio()>0) 
			){
			if (protocollo == null) {
				protocollo = new ProtocolloType();
			}
			Servizio servizio = new Servizio();
			if( 
					(t.getTipoServizio()!=null && StringUtils.isNotEmpty(t.getTipoServizio())) 
				){
				servizio.setTipo(UtilityTransazioni.escapeXmlValue(t.getTipoServizio()));
			}
			if( 
					(t.getNomeServizio()!=null && StringUtils.isNotEmpty(t.getNomeServizio())) 
				){
				servizio.setValue(UtilityTransazioni.escapeXmlValue(t.getNomeServizio()));
			}
			if( 
					(t.getVersioneServizio()>0) 
				){
				servizio.setVersione(t.getVersioneServizio()+"");
			}
			protocollo.setServizio(servizio);
		}
		
		// azione
		if( 
				(t.getAzione()!=null && StringUtils.isNotEmpty(t.getAzione())) 
			){
			if (protocollo == null) {
				protocollo = new ProtocolloType();
			}
			protocollo.setAzione(UtilityTransazioni.escapeXmlValue(t.getAzione()));
		}
		
		// api
		TipoAPI tipoApi = null;
		if(t.getTipoApi()>0) {
			tipoApi = TipoAPI.toEnumConstant(t.getTipoApi());
		}
		if(
			(tipoApi!=null) 
			||
			(t.getUriAccordoServizio()!=null && !"".equals(t.getUriAccordoServizio()))
			) {
			Api api = new Api();
			if(tipoApi!=null) {
				api.setTipo(tipoApi.name());
			}
			if((t.getUriAccordoServizio()!=null && !"".equals(t.getUriAccordoServizio()))) {
				api.setNome(t.getUriAccordoServizio());
			}
			if(t.getGruppi()!=null && !"".equals(t.getGruppi())){
				Tags tags = new Tags();
				String tmp = t.getGruppi().trim();
				if(tmp.contains(",")){
					String [] split = tmp.split(",");
					if(split!=null && split.length>0){
						for (int i = 0; i < split.length; i++) {
							tags.getTag().add(split[i].trim());
						}
						
					}
				}
				else {
					tags.getTag().add(tmp);
				}
				api.setTags(tags);
			}
			protocollo.setApi(api);
		}
		
		// Identificativi Messaggi
		if( 
				(t.getIdMessaggioRichiesta()!=null && StringUtils.isNotEmpty(t.getIdMessaggioRichiesta())) 
				||
				(t.getIdMessaggioRisposta()!=null && StringUtils.isNotEmpty(t.getIdMessaggioRisposta())) 
				||
				(t.getDataIdMsgRichiesta()!=null) 
				||
				(t.getDataIdMsgRisposta()!=null) 
			){
			if (protocollo == null) {
				protocollo = new ProtocolloType();
			}
			if( 
					(t.getIdMessaggioRichiesta()!=null && StringUtils.isNotEmpty(t.getIdMessaggioRichiesta())) 
				){
				protocollo.setIdMessaggioRichiesta(UtilityTransazioni.escapeXmlValue(t.getIdMessaggioRichiesta()));
			}
			if( 
					(t.getIdMessaggioRisposta()!=null && StringUtils.isNotEmpty(t.getIdMessaggioRisposta())) 
				){
				protocollo.setIdMessaggioRisposta(UtilityTransazioni.escapeXmlValue(t.getIdMessaggioRisposta()));
			}
			try{
				if( 
						(t.getDataIdMsgRichiesta()!=null) 
					){
					protocollo.setDataIdMsgRichiesta(XMLUtils.getInstance().toGregorianCalendar(t.getDataIdMsgRichiesta()));
				}
				if( 
						(t.getDataIdMsgRisposta()!=null) 
					){
					protocollo.setDataIdMsgRisposta(XMLUtils.getInstance().toGregorianCalendar(t.getDataIdMsgRisposta()));
				}
			}catch(Exception e){
				throw new JAXBException(e.getMessage(),e);
			}
		}
		
		// altre informazioni
		if( 
				(t.getProfiloCollaborazioneProt()!=null && StringUtils.isNotEmpty(t.getProfiloCollaborazioneProt())) 
				||
				(t.getProfiloCollaborazioneOp2()!=null && StringUtils.isNotEmpty(t.getProfiloCollaborazioneOp2())) 
				||
				(t.getIdCollaborazione()!=null && StringUtils.isNotEmpty(t.getIdCollaborazione())) 
				||
				(t.getUriAccordoServizio()!=null && StringUtils.isNotEmpty(t.getUriAccordoServizio())) 
			){
			if (protocollo == null) {
				protocollo = new ProtocolloType();
			}
			if( 
					(t.getProfiloCollaborazioneProt()!=null && StringUtils.isNotEmpty(t.getProfiloCollaborazioneProt())) 
					||
					(t.getProfiloCollaborazioneOp2()!=null && StringUtils.isNotEmpty(t.getProfiloCollaborazioneOp2())) 
				){
				Profilo profilo = new Profilo();
				if( 
						(t.getProfiloCollaborazioneProt()!=null && StringUtils.isNotEmpty(t.getProfiloCollaborazioneProt())) 
					){
					profilo.setValue(UtilityTransazioni.escapeXmlValue(t.getProfiloCollaborazioneProt()));
				}
				if( 
						(t.getProfiloCollaborazioneOp2()!=null && StringUtils.isNotEmpty(t.getProfiloCollaborazioneOp2())) 
					){
					profilo.setCodice(UtilityTransazioni.escapeXmlValue(t.getProfiloCollaborazioneOp2()));
				}
				protocollo.setProfilo(profilo);
			}
			if( 
					(t.getIdCollaborazione()!=null && StringUtils.isNotEmpty(t.getIdCollaborazione())) 
				){
				protocollo.setIdCollaborazione(UtilityTransazioni.escapeXmlValue(t.getIdCollaborazione()));
			}
			if( 
					(t.getUriAccordoServizio()!=null && StringUtils.isNotEmpty(t.getUriAccordoServizio())) 
				){
				protocollo.setUriAccordoServizio(UtilityTransazioni.escapeXmlValue(t.getUriAccordoServizio()));
			}
		}
		
		// Identificativo asincrono
		if( 
				(t.getIdAsincrono()!=null && StringUtils.isNotEmpty(t.getIdAsincrono())) 
				||
				(t.getTipoServizioCorrelato()!=null && StringUtils.isNotEmpty(t.getTipoServizioCorrelato())) 
				||
				(t.getNomeServizioCorrelato()!=null && StringUtils.isNotEmpty(t.getNomeServizioCorrelato())) 
			){
			if (protocollo == null) {
				protocollo = new ProtocolloType();
			}
			ProfiloAsincrono profiloAsincrono = new ProfiloAsincrono();
			if( 
					(t.getIdAsincrono()!=null && StringUtils.isNotEmpty(t.getIdAsincrono())) 
				){
				profiloAsincrono.setIdCorrelazione(UtilityTransazioni.escapeXmlValue(t.getIdAsincrono()));
			}
			if( 
					(t.getTipoServizioCorrelato()!=null && StringUtils.isNotEmpty(t.getTipoServizioCorrelato())) 
					||
					(t.getNomeServizioCorrelato()!=null && StringUtils.isNotEmpty(t.getNomeServizioCorrelato())) 
				){
				ServizioCorrelato servizioCorrelato = new ServizioCorrelato();
				if( 
						(t.getTipoServizioCorrelato()!=null && StringUtils.isNotEmpty(t.getTipoServizioCorrelato())) 
					){
					servizioCorrelato.setTipo(UtilityTransazioni.escapeXmlValue(t.getTipoServizioCorrelato()));
				}
				if( 
						(t.getNomeServizioCorrelato()!=null && StringUtils.isNotEmpty(t.getNomeServizioCorrelato())) 
					){
					servizioCorrelato.setValue(UtilityTransazioni.escapeXmlValue(t.getNomeServizioCorrelato()));
				}
				profiloAsincrono.setServizioCorrelato(servizioCorrelato);
			}
			protocollo.setProfiloAsincrono(profiloAsincrono);
		}
		
		// Digest
		if( 
				(t.getDigestRichiesta()!=null && StringUtils.isNotEmpty(t.getDigestRichiesta())) 
				||
				(t.getDigestRisposta()!=null && StringUtils.isNotEmpty(t.getDigestRisposta())) 
			){
			if (protocollo == null) {
				protocollo = new ProtocolloType();
			}
			Digest digest = new Digest();
			if( 
					(t.getDigestRichiesta()!=null && StringUtils.isNotEmpty(t.getDigestRichiesta())) 
				){
				digest.setRichiesta(t.getDigestRichiesta());
			}
			if( 
					(t.getDigestRisposta()!=null && StringUtils.isNotEmpty(t.getDigestRisposta())) 
				){
				digest.setRisposta(t.getDigestRisposta());
			}
			protocollo.setDigest(digest);
		}
		
		// Duplicati
		if (t.getDuplicatiRichiesta() >= 0 || t.getDuplicatiRisposta() >= 0) {
			if (protocollo == null) {
				protocollo = new ProtocolloType();
			}
			Duplicati duplicati = new Duplicati();
			if (t.getDuplicatiRichiesta() >= 0) {
				duplicati.setRichiesta(t.getDuplicatiRichiesta());
			}
			if (t.getDuplicatiRisposta() >= 0) {
				duplicati.setRisposta(t.getDuplicatiRisposta());
			}
			protocollo.setDuplicati(duplicati);
		}
		
		
		if (protocollo != null) {
			protocollo.setName(t.getProtocollo());
			transazione.setProtocollo(protocollo);
		}
		
	}
	
	private static String buildInfoDiagnosticaOptimized(String info){
		if(info==null){
			return null;
		}
		String [] split = info.split(UtilityTransazioni.DIAGNOSTIC_WITH_DYNAMIC_INFO_DIAG_SEPARATOR);
		if(split!=null && split.length>0){
			Map<String, Integer> counters = new HashMap<String, Integer>();
			for (int i = 0; i < split.length; i++) {
				if(split[i]!=null && !"".equals(split[i].trim())){
					String [] tmp = split[i].trim().split(UtilityTransazioni.DIAGNOSTIC_WITH_DYNAMIC_INFO_TYPE_SEPARATOR);
					if(tmp!=null && tmp.length>0){
						if(tmp[0]!=null){
							String tipo = tmp[0].trim();
							if(!"".equals(tipo)){
								Integer count = counters.remove(tipo);
								if(count==null){
									count = 1;
								}
								else{
									count++;
								}
								counters.put(tipo, count);
							}
						}
					}
				}
			}
			if(counters.size()>0){
				StringBuilder bf = new StringBuilder();
				Iterator<String> tipi = counters.keySet().iterator();
				while (tipi.hasNext()) {
					String tipo = tipi.next();
					Integer count = counters.get(tipo);
					if(bf.length()>0){
						bf.append(", ");
					}
					bf.append(tipo).append(":").append(count);
				}
				return bf.toString();
			}
		}
		return null;
	}
	
	private static void fillDiagnostica(TransazioneBean t,org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType transazione){
		String errorDiagnosticaMsg = " ... \nPer maggiori dettagli consultare i log della PdD";
		
		Diagnostica diagnostica = new Diagnostica();
		if(t.getDiagnostici()!=null && StringUtils.isNotEmpty(t.getDiagnostici())){
			// Codici ammessi definiti in it.link.pdd.interceptor.monitor.driver.diagnostici.CostantiMapping			
			if(t.getDiagnostici().startsWith("-")){
				diagnostica.setMessaggi(DiagnosticaSerializationType.OPTIMIZED_NOT_FOUND);
			}
			else if(t.getDiagnostici().startsWith("N")){
				diagnostica.setMessaggi(DiagnosticaSerializationType.OPTIMIZED_ERROR);
				diagnostica.setMessaggiErroreSerializzazione(t.getDiagnostici()+errorDiagnosticaMsg);
			}
			else if(t.getDiagnostici().startsWith("R")){
				diagnostica.setMessaggi(DiagnosticaSerializationType.OPTIMIZED);
			}
			else {
				diagnostica.setMessaggi(DiagnosticaSerializationType.OPTIMIZED_ERROR);
				diagnostica.setMessaggiErroreSerializzazione(t.getDiagnostici()+errorDiagnosticaMsg);
			}
			diagnostica.setMessaggiInfoSerializzazione(UtilityTransazioni.buildInfoDiagnosticaOptimized(t.getDiagnosticiExt()));
		}
		else{
			diagnostica.setMessaggi(DiagnosticaSerializationType.STANDARD);
		}
		if(t.getTracciaRichiesta()!=null && StringUtils.isNotEmpty(t.getTracciaRichiesta())){
			// Codici ammessi definiti in it.link.pdd.interceptor.monitor.driver.tracciamento.CostantiMapping			
			if(t.getTracciaRichiesta().startsWith("-")){
				diagnostica.setTracciaRichiesta(DiagnosticaSerializationType.OPTIMIZED_NOT_FOUND);
			}
			else if(t.getTracciaRichiesta().startsWith("N")){
				diagnostica.setTracciaRichiesta(DiagnosticaSerializationType.OPTIMIZED_ERROR);
				diagnostica.setTracciaRichiestaErroreSerializzazione(t.getTracciaRichiesta()+errorDiagnosticaMsg);
			}
			else if(t.getTracciaRichiesta().startsWith("R")){
				diagnostica.setTracciaRichiesta(DiagnosticaSerializationType.OPTIMIZED);
			}
			else {
				diagnostica.setTracciaRichiesta(DiagnosticaSerializationType.OPTIMIZED_ERROR);
				diagnostica.setTracciaRichiestaErroreSerializzazione(t.getTracciaRichiesta()+errorDiagnosticaMsg);
			}
		}
		else{
			diagnostica.setTracciaRichiesta(DiagnosticaSerializationType.STANDARD);
		}
		if(t.getTracciaRisposta()!=null && StringUtils.isNotEmpty(t.getTracciaRisposta())){
			// Codici ammessi definiti in it.link.pdd.interceptor.monitor.driver.tracciamento.CostantiMapping			
			if(t.getTracciaRisposta().startsWith("-")){
				diagnostica.setTracciaRisposta(DiagnosticaSerializationType.OPTIMIZED_NOT_FOUND);
			}
			else if(t.getTracciaRisposta().startsWith("N")){
				diagnostica.setTracciaRisposta(DiagnosticaSerializationType.OPTIMIZED_ERROR);
				diagnostica.setTracciaRispostaErroreSerializzazione(t.getTracciaRisposta()+errorDiagnosticaMsg);
			}
			else if(t.getTracciaRisposta().startsWith("R")){
				diagnostica.setTracciaRisposta(DiagnosticaSerializationType.OPTIMIZED);
			}
			else {
				diagnostica.setTracciaRisposta(DiagnosticaSerializationType.OPTIMIZED_ERROR);
				diagnostica.setTracciaRispostaErroreSerializzazione(t.getTracciaRisposta()+errorDiagnosticaMsg);
			}
		}
		else{
			diagnostica.setTracciaRisposta(DiagnosticaSerializationType.STANDARD);
		}
		transazione.setDiagnostica(diagnostica);
	}
	
	public static String getExtension(String formato) {
		MessageType messageType= MessageType.BINARY;
		if(StringUtils.isNotEmpty(formato)) {
			messageType = MessageType.valueOf(formato);
		}

		switch (messageType) {
		case BINARY:
		case MIME_MULTIPART:
			return "bin";
		case JSON:
			return "json";
		case SOAP_11:
		case SOAP_12:
		case XML:
			return "xml";
		}
		
		return "bin";
	}
	
	public static void writeManifestTransazioneApplicativoServer(TransazioneBean t, TransazioneApplicativoServerBean tAS, OutputStream out)
			throws Exception {
		PddMonitorProperties monitorProperties = PddMonitorProperties.getInstance(LoggerManager.getPddMonitorCoreLogger());
		UtilityTransazioni.writeManifestTransazioneApplicativoServer(t, tAS, out, monitorProperties.isAttivoTransazioniDataAccettazione());
	}
	public static void writeManifestTransazioneApplicativoServer(TransazioneBean tr, TransazioneApplicativoServerBean tAS, OutputStream out, boolean isAttivoTransazioniDataAccettazione)
			throws Exception {
		

		JAXBContext jc = JAXBContext
				.newInstance(org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ObjectFactory.class.getPackage().getName());

		Marshaller marshaller = jc.createMarshaller();
		// marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ObjectFactory objFactory = new org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ObjectFactory();

		org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType consegna = new org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType();

		// Esito
		org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType.Esito esito = new org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType.Esito();
		EsitiProperties esitiProperties = EsitiProperties.getInstance(LoggerManager.getPddMonitorCoreLogger(),tr.getProtocollo());
		if (tAS.getDettaglioEsito() >= 0) {
			// <esito>ESITO_CALCOLATO_IN_FORMA_LEGGIBILE_DA_UMANO</esito>
			DettaglioEsito dettaglioEsito = new DettaglioEsito();
			dettaglioEsito.setCodice(new BigInteger(tAS.getDettaglioEsito()+""));
			dettaglioEsito.setValue(UtilityTransazioni.escapeXmlValue(esitiProperties.getEsitoLabel(tAS.getDettaglioEsito())));
			esito.setDettaglioEsito(dettaglioEsito);
		}
		esito.setConsegnaTerminata(tAS.getConsegnaTerminata());
		if(tAS.getDataMessaggioScaduto()!=null) {
			esito.setDataMessaggioScaduto(XMLUtils.getInstance().toGregorianCalendar(tAS.getDataMessaggioScaduto()));
		}
		esito.setConsegnaTrasparente(tAS.isConsegnaTrasparente());
		esito.setConsegnaIntegrationManager(tAS.isConsegnaIntegrationManager());
		consegna.setEsito(esito);
		
		// Tempi di latenza
		if (tAS.getDataUscitaRichiesta() != null
				|| tAS.getDataIngressoRisposta() != null) {

			org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType.TempiAttraversamento tempi = new org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType.TempiAttraversamento();

			try{
				if(isAttivoTransazioniDataAccettazione){
					if (tAS.getDataAccettazioneRichiesta() != null) {
						tempi.setRichiestaAccettazione(XMLUtils.getInstance().toGregorianCalendar(tAS.getDataAccettazioneRichiesta()));
					}
				}
				
				if (tAS.getDataUscitaRichiesta() != null) {
					tempi.setRichiestaUscita(XMLUtils.getInstance().toGregorianCalendar(tAS.getDataUscitaRichiesta()));
				}
	
				if(isAttivoTransazioniDataAccettazione){
					if (tAS.getDataAccettazioneRisposta() != null) {
						tempi.setRispostaAccettazione(XMLUtils.getInstance().toGregorianCalendar(tAS.getDataAccettazioneRisposta()));
					}
				}
				
				if (tAS.getDataIngressoRisposta() != null) {
					tempi.setRispostaIngresso(XMLUtils.getInstance().toGregorianCalendar(tAS.getDataIngressoRisposta()));
				}
	
				if(tAS.getDataUscitaRichiesta()!=null && tAS.getDataIngressoRisposta()!=null){
					long latenza = tAS.getDataIngressoRisposta().getTime() - tAS.getDataUscitaRichiesta().getTime();
					if(latenza>=0)
						tempi.setLatenzaServizio(latenza);
				}
				
			}catch(Exception e){
				throw new JAXBException(e.getMessage(),e);
			}
			consegna.setTempiAttraversamento(tempi);
		}
		
		// Dimensione messaggi gestiti
		if (tAS.getRichiestaUscitaBytes() != null
				|| tAS.getRispostaIngressoBytes() != null) {

			org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType.DimensioneMessaggi dm = new org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType.DimensioneMessaggi();

			if (tAS.getRichiestaUscitaBytes() != null) {
				dm.setRichiestaUscita(tAS.getRichiestaUscitaBytes());
			}

			if (tAS.getRispostaIngressoBytes() != null) {
				dm.setRispostaIngresso(tAS.getRispostaIngressoBytes());
			}

			consegna.setDimensioneMessaggi(dm);
		}
		
		// Dati Consegna
		if(tAS.isConsegnaTrasparente()) {
			DatiConsegna datiConsegna = new DatiConsegna();
			if(tAS.getUltimoErrore()!=null && !"".equals(tAS.getUltimoErrore())) {
				
				UltimoErrore ultimoErrore = new UltimoErrore();
				ultimoErrore.setDettaglio(UtilityTransazioni.escapeXmlValue(tAS.getUltimoErrore()));
				
				if(tAS.getDettaglioEsitoUltimoErrore()>=0) {
					org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType.DatiConsegna.UltimoErrore.DettaglioEsito dettaglioEsito = new org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType.DatiConsegna.UltimoErrore.DettaglioEsito();
					dettaglioEsito.setCodice(new BigInteger(tAS.getDettaglioEsitoUltimoErrore()+""));
					dettaglioEsito.setValue(UtilityTransazioni.escapeXmlValue(esitiProperties.getEsitoLabel(tAS.getDettaglioEsitoUltimoErrore())));
					ultimoErrore.setDettaglioEsito(dettaglioEsito);
				}
				
				if(tAS.getLocationUltimoErrore()!=null && !"".equals(tAS.getLocationUltimoErrore())) {
					ultimoErrore.setConnettore(UtilityTransazioni.escapeXmlValue(tAS.getLocationUltimoErrore()));
				}
				if(tAS.getCodiceRispostaUltimoErrore()!=null && !"".equals(tAS.getCodiceRispostaUltimoErrore())) {
					ultimoErrore.setCodiceRisposta(UtilityTransazioni.escapeXmlValue(tAS.getCodiceRispostaUltimoErrore()));
				}
				if (tAS.getDataUltimoErrore() != null) {
					ultimoErrore.setData(XMLUtils.getInstance().toGregorianCalendar(tAS.getDataUltimoErrore()));
				}
				if(tAS.getClusterIdUltimoErrore()!=null && !"".equals(tAS.getClusterIdUltimoErrore())) {
					ultimoErrore.setClusterId(UtilityTransazioni.escapeXmlValue(tAS.getClusterIdUltimoErrore()));
				}
				
				datiConsegna.setUltimoErrore(ultimoErrore);
			}
			if(tAS.getLocationConnettore()!=null && !"".equals(tAS.getLocationConnettore())) {
				datiConsegna.setConnettore(UtilityTransazioni.escapeXmlValue(tAS.getLocationConnettore()));
			}
			if(tAS.getCodiceRisposta()!=null && !"".equals(tAS.getCodiceRisposta())) {
				datiConsegna.setCodiceRisposta(UtilityTransazioni.escapeXmlValue(tAS.getCodiceRisposta()));
			}
			if (tAS.getDataPrimoTentativo() != null) {
				datiConsegna.setDataPrimoTentativo(XMLUtils.getInstance().toGregorianCalendar(tAS.getDataPrimoTentativo()));
			}
			if (tAS.getNumeroTentativi()>=0) {
				datiConsegna.setNumeroTentativi(new BigInteger(tAS.getNumeroTentativi()+""));
			}
			if(tAS.getClusterIdConsegna()!=null && !"".equals(tAS.getClusterIdConsegna())) {
				datiConsegna.setClusterIdConsegna(UtilityTransazioni.escapeXmlValue(tAS.getClusterIdConsegna()));
			}
			consegna.setDatiConsegna(datiConsegna);
		}

		// dati-integration-manager
		if(tAS.isConsegnaIntegrationManager()) {
			DatiIntegrationManager datiIM = new DatiIntegrationManager();
			
			if (tAS.getDataPrimoPrelievoIm() != null) {
				datiIM.setDataPrimoPrelievo(XMLUtils.getInstance().toGregorianCalendar(tAS.getDataPrimoPrelievoIm()));
			}
			if (tAS.getDataPrelievoIm() != null) {
				datiIM.setDataPrelievo(XMLUtils.getInstance().toGregorianCalendar(tAS.getDataPrelievoIm()));
			}
			if (tAS.getNumeroPrelieviIm()>=0) {
				datiIM.setNumeroPrelievi(new BigInteger(tAS.getNumeroPrelieviIm()+""));
			}
			if(tAS.getClusterIdPrelievoIm()!=null && !"".equals(tAS.getClusterIdPrelievoIm())) {
				datiIM.setClusterIdPrelievo(UtilityTransazioni.escapeXmlValue(tAS.getClusterIdPrelievoIm()));
			}
			if (tAS.getDataEliminazioneIm() != null) {
				datiIM.setDataEliminazione(XMLUtils.getInstance().toGregorianCalendar(tAS.getDataEliminazioneIm()));
			}
			if(tAS.getClusterIdEliminazioneIm()!=null && !"".equals(tAS.getClusterIdEliminazioneIm())) {
				datiIM.setClusterIdEliminazione(UtilityTransazioni.escapeXmlValue(tAS.getClusterIdEliminazioneIm()));
			}
			consegna.setDatiIntegrationManager(datiIM);
		}
		
		// dati consegna
		consegna.setIdTransazione(UtilityTransazioni.escapeXmlValue(tAS.getIdTransazione()));
		consegna.setServizioApplicativoErogatore(UtilityTransazioni.escapeXmlValue(tAS.getServizioApplicativoErogatore()));
		if(tAS.getConnettoreNome()!=null && !"".equals(tAS.getConnettoreNome())) {
			consegna.setNomeConnettore(UtilityTransazioni.escapeXmlValue(tAS.getConnettoreNome()));
		}
		if (tAS.getDataRegistrazione() != null) {
			consegna.setDataRegistrazione(XMLUtils.getInstance().toGregorianCalendar(tAS.getDataRegistrazione()));
		}
		if(tAS.getIdentificativoMessaggio()!=null && !"".equals(tAS.getIdentificativoMessaggio())) {
			consegna.setIdentificativoMessaggio(UtilityTransazioni.escapeXmlValue(tAS.getIdentificativoMessaggio()));
		}
		if(tAS.getClusterIdPresaInCarico()!=null && !"".equals(tAS.getClusterIdPresaInCarico())) {
			consegna.setClusterIdInCoda(UtilityTransazioni.escapeXmlValue(tAS.getClusterIdPresaInCarico()));
		}
		
		marshaller.marshal(objFactory.createConsegna(consegna), out);
	}
	
	@SuppressWarnings("unused")
	private static String convertSize(Long value) {
		MessageFormat mf = new MessageFormat("{0,number,#.##}");
		Double len = null;
		String res = "";

		// il valore e' in byte
		len = value.doubleValue();
		long d = Math.round(len / UtilityTransazioni.KB);
		if (d <= 1) {
			// byte
			Object[] objs = { len };
			res = mf.format(objs);
			res += " B";
		} else if (d > 1 && d < 1000) {
			// kilo byte
			Object[] objs = { len / UtilityTransazioni.KB };
			res = mf.format(objs);
			res += " KB";
		} else {
			// mega byte
			Object[] objs = { len / UtilityTransazioni.MB };
			res = mf.format(objs);
			res += " MB";
		}

		return res;
	}

	private static String escapeXmlValue(String value) {
		return StringEscapeUtils.escapeXml(value);
	}
	
	public static void writeSearchFilterXml(TransazioniSearchForm searchForm,
			OutputStream out) throws Exception {
		UtilityTransazioni.writeSearchFilterXml(searchForm, null, null, out);
	}

	public static void writeSearchFilterXml(TransazioniSearchForm searchForm,
			List<String> idTransazioni, Integer totTransazioni, OutputStream out)
			throws Exception {

		/*
		 * <transazione
		 * xmlns="http://www.spcoopit.it/PdS/Transaction/SearchFilter"> <filtro
		 * nome=”nome1” valore=”valore1” /> <filtro nome=”nome2”
		 * valore=”valore2” /> .... <filtro nome=”nomeN” valore=”valoreN” />
		 * </transazione>
		 */

		JAXBContext jc = JAXBContext
				.newInstance(org.openspcoop2.web.monitor.transazioni.core.search.ObjectFactory.class.getPackage().getName());

		Marshaller marshaller = jc.createMarshaller();

		org.openspcoop2.web.monitor.transazioni.core.search.ObjectFactory objFactory = new org.openspcoop2.web.monitor.transazioni.core.search.ObjectFactory();

		org.openspcoop2.web.monitor.transazioni.core.search.TransazioneType transazione = new org.openspcoop2.web.monitor.transazioni.core.search.TransazioneType();

		// tipologia ricerca spcoop/im
		if (searchForm.getTipologiaTransazioneSPCoop() == null)
			transazione
					.setTipologiaTransazioni(TipologiaTransazioniType.PROXY_TRASPARENTE_INTEGRATION_MANAGER);
		else if (searchForm.getTipologiaTransazioneSPCoop())
			transazione
					.setTipologiaTransazioni(TipologiaTransazioniType.PROXY_TRASPARENTE);
		else
			transazione
					.setTipologiaTransazioni(TipologiaTransazioniType.INTEGRATION_MANAGER);

		// tipologia ricerca
		if(searchForm.getTipologiaRicercaEnum()==null) {
			transazione.setTipologiaRicercaTransazioni(TipologiaRicercaTransazioniType.EROGAZIONE_FRUIZIONE);
		}
		else {
			switch (searchForm.getTipologiaRicercaEnum()) {
			case ingresso:
				transazione.setTipologiaRicercaTransazioni(TipologiaRicercaTransazioniType.EROGAZIONE);
				break;
			case uscita:
				transazione.setTipologiaRicercaTransazioni(TipologiaRicercaTransazioniType.FRUIZIONE);
				break;
			case all:
			default:
				transazione.setTipologiaRicercaTransazioni(TipologiaRicercaTransazioniType.EROGAZIONE_FRUIZIONE);
				break;
			}
		}

		// periodo
		// data inizio data fine
		Periodo periodo = new Periodo();
		periodo.setDataInizio(XMLUtils.getInstance().toGregorianCalendar(searchForm.getDataInizio()));		
		periodo.setDataFine(XMLUtils.getInstance().toGregorianCalendar(searchForm.getDataFine()));
		transazione.setPeriodo(periodo);

		// protocollo
		if (StringUtils.isNotEmpty(searchForm.getProtocollo())) {
			transazione.setProtocollo(UtilityTransazioni
					.escapeXmlValue(searchForm.getProtocollo()));
		}

		// soggetto locale
		if (StringUtils.isNotEmpty(searchForm.getSoggettoLocale())) {
			SoggettoLocale soggettoLocale = new SoggettoLocale();

			soggettoLocale.setTipo(searchForm.getTipoSoggettoLocale());
			soggettoLocale.setValue(UtilityTransazioni
					.escapeXmlValue(searchForm.getSoggettoLocale()));
			transazione.setSoggettoLocale(soggettoLocale);
		}

		// soggetto remoto se selezionata tipologia erogazione/fruizione
		if (TipologiaRicerca.all.equals(searchForm.getTipologiaRicercaEnum()) && StringUtils.isNotEmpty(searchForm.getTrafficoPerSoggetto())) {
			SoggettoRemoto soggettoRemoto = new SoggettoRemoto();

			soggettoRemoto.setTipo(searchForm.getTipoTrafficoPerSoggetto());
			soggettoRemoto.setValue(UtilityTransazioni
					.escapeXmlValue(searchForm.getTrafficoPerSoggetto()));

			transazione.setSoggettoRemoto(soggettoRemoto);
		}
		// soggetto mittente se selezionata tipologia erogazione
		if (TipologiaRicerca.ingresso.equals(searchForm.getTipologiaRicercaEnum()) && StringUtils.isNotEmpty(searchForm.getNomeMittente())) {
			SoggettoMittente soggettoMittente = new SoggettoMittente();

			soggettoMittente.setTipo(searchForm.getTipoMittente());
			soggettoMittente.setValue(UtilityTransazioni
					.escapeXmlValue(searchForm.getNomeMittente()));

			transazione.setSoggettoMittente(soggettoMittente);
		}
		// soggetto destinatario se selezionata tipologia fruizione
		if (TipologiaRicerca.uscita.equals(searchForm.getTipologiaRicercaEnum()) && StringUtils.isNotEmpty(searchForm.getNomeDestinatario())) {
			SoggettoDestinatario soggettoDestinatario = new SoggettoDestinatario();

			soggettoDestinatario.setTipo(searchForm.getTipoDestinatario());
			soggettoDestinatario.setValue(UtilityTransazioni
					.escapeXmlValue(searchForm.getNomeDestinatario()));

			transazione.setSoggettoDestinatario(soggettoDestinatario);
		}
		// servizio applicativo
		if (StringUtils.isNotEmpty(searchForm.getServizioApplicativo())) {
			transazione.setServizioApplicativo(UtilityTransazioni
					.escapeXmlValue(searchForm.getServizioApplicativo()));
		}
		// servizio
		if (StringUtils.isNotEmpty(searchForm.getNomeServizio())) {
			transazione.setServizio(UtilityTransazioni
					.escapeXmlValue(searchForm.getNomeServizio()));
		}
		// azione
		if (StringUtils.isNotEmpty(searchForm.getNomeAzione())) {
			transazione.setAzione(UtilityTransazioni.escapeXmlValue(searchForm
					.getNomeAzione()));

		}
		
		// esito
		
		EsitoUtils esitoUtils = new EsitoUtils(LoggerManager.getPddMonitorCoreLogger(),searchForm.getProtocollo());
		
		org.openspcoop2.web.monitor.transazioni.core.search.TransazioneType.Esito esito = new org.openspcoop2.web.monitor.transazioni.core.search.TransazioneType.Esito();
		Integer esitoNumber = null;
		if(EsitoUtils.ALL_VALUE!=searchForm.getEsitoDettaglio()){
			esitoNumber = searchForm.getEsitoDettaglio();
		}
		else{
			esitoNumber = searchForm.getEsitoGruppo();
		}
		esito.setCodice(new BigInteger(esitoNumber+""));
		esito.setValue(UtilityTransazioni.escapeXmlValue(esitoUtils.getEsitoLabelFromValue(esitoNumber,false)));
		transazione.setEsito(esito);
		
		org.openspcoop2.web.monitor.transazioni.core.search.TransazioneType.Contesto contesto = new org.openspcoop2.web.monitor.transazioni.core.search.TransazioneType.Contesto();
		contesto.setCodice(searchForm.getEsitoContesto());
		contesto.setValue(UtilityTransazioni.escapeXmlValue(esitoUtils.getEsitoContestoLabelFromValue(searchForm.getEsitoContesto())));
		transazione.setContesto(contesto);

		// id-egov
		if (StringUtils.isNotEmpty(searchForm.getIdEgov())) {
			transazione.setIdMessaggio(searchForm.getIdEgov());
		}

		// eventi gestione
		if (StringUtils.isNotEmpty(searchForm.getEvento())) {
			transazione.setEvento(searchForm.getEvento());
		}
		
		// id-applicativo (like equals)
		if (StringUtils.isNotEmpty(searchForm.getIdCorrelazioneApplicativa())) {
			IdApplicativo idApplicativo = new IdApplicativo();
			idApplicativo.setModalita(searchForm
					.getCorrelazioneApplicativaMatchingType().toString());
			idApplicativo.setValue(searchForm.getIdCorrelazioneApplicativa());

			transazione.setIdApplicativo(idApplicativo);
		}

		// filtro-contenuti
		FiltroContenuti filtroContenuti = null;
		if (StringUtils.isNotEmpty(searchForm.getNomeStato())) {
			if(filtroContenuti==null){
				filtroContenuti = new FiltroContenuti();
			}
			filtroContenuti.setStato(searchForm.getNomeStato());
		}
		if (StringUtils.isNotEmpty(searchForm.getNomeRisorsa()) && StringUtils.isNotEmpty(searchForm.getValoreRisorsa())) {
			if(filtroContenuti==null){
				filtroContenuti = new FiltroContenuti();
			}
			Risorsa risorsa = new Risorsa();
			risorsa.setNome(searchForm.getNomeRisorsa());
			risorsa.setValue(searchForm.getValoreRisorsa());
			filtroContenuti.setRisorsa(risorsa);
		}
		if(searchForm.getRicercaSelezionata()!=null && searchForm.getRicercaSelezionata().getPlugin()!=null){
			if(filtroContenuti==null){
				filtroContenuti = new FiltroContenuti();
			}
			RicercaPersonalizzata ricerca = new RicercaPersonalizzata();
			ricerca.setTipo(searchForm.getRicercaSelezionata().getPlugin().getTipo());
			ricerca.setClassName(searchForm.getRicercaSelezionata().getPlugin().getClassName());
			ricerca.setLabel(searchForm.getRicercaSelezionata().getPlugin().getLabel());
			if(searchForm.getRicercaSelezionataParameters()!=null && searchForm.getRicercaSelezionataParameters().size()>0){
				for (Parameter<?> searchParam : searchForm.getRicercaSelezionataParameters()) {
					org.openspcoop2.web.monitor.transazioni.core.search.RicercaPersonalizzata.Parameter par = new org.openspcoop2.web.monitor.transazioni.core.search.RicercaPersonalizzata.Parameter();
					par.setTipo(searchParam.getType().getValue());
					par.setNome(searchParam.getId());
					par.setValue(searchParam.getValueAsString());
					ricerca.getParameter().add(par);
				}
			}
			filtroContenuti.setRicercaPersonalizzata(ricerca);
		}
		transazione.setFiltroContenuti(filtroContenuti);
		
		if (idTransazioni != null && totTransazioni != null) {
			TransazioniIdentificate transazioniIdentificate = new TransazioniIdentificate();

			transazioniIdentificate.setSelezionate(idTransazioni.size());
			transazioniIdentificate.setTotale(totTransazioni);

			transazione.setTransazioniIdentificate(transazioniIdentificate);

		}

		marshaller.marshal(objFactory.createTransazione(transazione), out);
	}

	
	public static void writeHeadersTrasportoXml(
			List<DumpHeaderTrasporto> headers, OutputStream out, boolean asProperties)
			throws Exception {

		if(asProperties) {
			StringBuilder bf = new StringBuilder();
			if (headers != null) {
				for (DumpHeaderTrasporto dumpHeaderTrasporto : headers) {
					if(bf.length()>0) {
						bf.append("\n");
					}
					bf.append(dumpHeaderTrasporto.getNome());
					bf.append("=");
					bf.append(dumpHeaderTrasporto.getValore());
				}
			}
			if(bf.length()>0) {
				out.write(bf.toString().getBytes());
			}
		}
		else {
			JAXBContext jc = JAXBContext
					.newInstance(org.openspcoop2.web.monitor.transazioni.core.header.ObjectFactory.class.getPackage().getName());
	
			Marshaller marshaller = jc.createMarshaller();
	
			org.openspcoop2.web.monitor.transazioni.core.header.ObjectFactory objFactory = new org.openspcoop2.web.monitor.transazioni.core.header.ObjectFactory();
	
			org.openspcoop2.web.monitor.transazioni.core.header.TransazioneType transazione = new org.openspcoop2.web.monitor.transazioni.core.header.TransazioneType();
	
			if (headers != null) {
				for (DumpHeaderTrasporto dumpHeaderTrasporto : headers) {
					HeaderType header = new HeaderType();
					header.setNome(dumpHeaderTrasporto.getNome());
					header.setValore(dumpHeaderTrasporto.getValore());
	
					transazione.getHeader().add(header);
				}
			}
	
			marshaller.marshal(objFactory.createTransazione(transazione), out);
		}
	}
	
	public static void writeMultipartHeaderXml(
			List<DumpMultipartHeader> headers, OutputStream out, boolean asProperties)
			throws Exception {

		if(asProperties) {
			StringBuilder bf = new StringBuilder();
			if (headers != null) {
				for (DumpMultipartHeader dumpHeaderTrasporto : headers) {
					if(bf.length()>0) {
						bf.append("\n");
					}
					bf.append(dumpHeaderTrasporto.getNome());
					bf.append("=");
					bf.append(dumpHeaderTrasporto.getValore());
				}
			}
			if(bf.length()>0) {
				out.write(bf.toString().getBytes());
			}
		}
		else {
			JAXBContext jc = JAXBContext
					.newInstance(org.openspcoop2.web.monitor.transazioni.core.header.ObjectFactory.class.getPackage().getName());
	
			Marshaller marshaller = jc.createMarshaller();
	
			org.openspcoop2.web.monitor.transazioni.core.header.ObjectFactory objFactory = new org.openspcoop2.web.monitor.transazioni.core.header.ObjectFactory();
	
			org.openspcoop2.web.monitor.transazioni.core.header.TransazioneType transazione = new org.openspcoop2.web.monitor.transazioni.core.header.TransazioneType();
	
			if (headers != null) {
				for (DumpMultipartHeader dumpHeaderTrasporto : headers) {
					HeaderType header = new HeaderType();
					header.setNome(dumpHeaderTrasporto.getNome());
					header.setValore(dumpHeaderTrasporto.getValore());
	
					transazione.getHeader().add(header);
				}
			}
	
			marshaller.marshal(objFactory.createTransazione(transazione), out);
		}
	}
	
	public static void writeAllegatoHeaderXml(
			List<DumpHeaderAllegato> headers, OutputStream out, boolean asProperties)
			throws Exception {

		if(asProperties) {
			StringBuilder bf = new StringBuilder();
			if (headers != null) {
				for (DumpHeaderAllegato dumpHeaderTrasporto : headers) {
					if(bf.length()>0) {
						bf.append("\n");
					}
					bf.append(dumpHeaderTrasporto.getNome());
					bf.append("=");
					bf.append(dumpHeaderTrasporto.getValore());
				}
			}
			if(bf.length()>0) {
				out.write(bf.toString().getBytes());
			}
		}
		else {
			JAXBContext jc = JAXBContext
					.newInstance(org.openspcoop2.web.monitor.transazioni.core.header.ObjectFactory.class.getPackage().getName());
	
			Marshaller marshaller = jc.createMarshaller();
	
			org.openspcoop2.web.monitor.transazioni.core.header.ObjectFactory objFactory = new org.openspcoop2.web.monitor.transazioni.core.header.ObjectFactory();
	
			org.openspcoop2.web.monitor.transazioni.core.header.TransazioneType transazione = new org.openspcoop2.web.monitor.transazioni.core.header.TransazioneType();
	
			if (headers != null) {
				for (DumpHeaderAllegato dumpHeaderTrasporto : headers) {
					HeaderType header = new HeaderType();
					header.setNome(dumpHeaderTrasporto.getNome());
					header.setValore(dumpHeaderTrasporto.getValore());
	
					transazione.getHeader().add(header);
				}
			}
	
			marshaller.marshal(objFactory.createTransazione(transazione), out);
		}
	}

	public static void writeContenutiXml(List<DumpContenuto> contenuti,
			OutputStream out, boolean asProperties) throws Exception {
		
		if(asProperties) {
			StringBuilder bf = new StringBuilder();
			if (contenuti != null) {
				for (DumpContenuto dumpContenuto : contenuti) {
					if(bf.length()>0) {
						bf.append("\n");
					}
					bf.append(dumpContenuto.getNome());
					bf.append("=");
					bf.append(TransactionContentUtils.getDumpContenutoValue(dumpContenuto));
				}
			}
			if(bf.length()>0) {
				out.write(bf.toString().getBytes());
			}
		}
		else {
			ContentType content = new ContentType();
	
			JAXBContext jc = JAXBContext
					.newInstance(org.openspcoop2.web.monitor.transazioni.core.contents.ObjectFactory.class.getPackage().getName());
	
			Marshaller marshaller = jc.createMarshaller();
	
			org.openspcoop2.web.monitor.transazioni.core.contents.ObjectFactory objFactory = new org.openspcoop2.web.monitor.transazioni.core.contents.ObjectFactory();
	
			if (contenuti != null) {
				for (DumpContenuto dumpContenuto : contenuti) {
					RisorsaType risorsa = new RisorsaType();
	
					risorsa.setNome(dumpContenuto.getNome());
					risorsa.setValore(TransactionContentUtils.getDumpContenutoValue(dumpContenuto));
	
					content.getRisorsa().add(risorsa);
				}
			}
	
			marshaller.marshal(objFactory.createTransazione(content), out);
		}

	}

}
