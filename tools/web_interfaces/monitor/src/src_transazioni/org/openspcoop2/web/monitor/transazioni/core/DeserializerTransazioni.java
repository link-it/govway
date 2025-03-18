/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.diagnostica.utils.XMLUtils;
import org.openspcoop2.core.diagnostica.utils.XMLUtilsException;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.core.transazioni.DumpHeaderAllegato;
import org.openspcoop2.core.transazioni.DumpHeaderTrasporto;
import org.openspcoop2.core.transazioni.DumpMultipartHeader;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.core.transazioni.constants.TipoAPI;
import org.openspcoop2.core.transazioni.utils.TransactionContentUtils;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.protocol.basic.diagnostica.DiagnosticSerializer;
import org.openspcoop2.protocol.basic.tracciamento.TracciaSerializer;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.XMLRootElement;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticSerializer;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaSerializer;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XMLException;
import org.openspcoop2.utils.xml.XPathException;
import org.openspcoop2.utils.xml.XPathNotFoundException;
import org.openspcoop2.utils.xml.XPathNotValidException;
import org.openspcoop2.utils.xml.XPathReturnType;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneApplicativoServerBean;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.core.contents.RisorsaType;
import org.openspcoop2.web.monitor.transazioni.core.header.HeaderType;
import org.openspcoop2.web.monitor.transazioni.core.manifest.RuoloType;
import org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType.Contesto;
import org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType.CorrelazioneApplicativa;
import org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType.DatiIntegrazione;
import org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType.DimensioneMessaggi;
import org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType.Esito;
import org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType.IntegrationManager;
import org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType.ServizioApplicativoToken;
import org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType.TempiAttraversamento;
import org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType.DatiConsegna;
import org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType.DatiConsegna.UltimoErrore;
import org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType.DatiIntegrationManager;
import org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType.Esito.DettaglioEsito;
import org.openspcoop2.web.monitor.transazioni.mbean.TracciaBean;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

/**
 * DeserializerTransazioni
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DeserializerTransazioni {

	private DeserializerTransazioni() {}

	public static final String DIAGNOSTIC_WITH_DYNAMIC_INFO_DIAG_SEPARATOR = MsgDiagnosticiProperties.DIAGNOSTIC_WITH_DYNAMIC_INFO_DIAG_SEPARATOR;
	public static final String DIAGNOSTIC_WITH_DYNAMIC_INFO_TYPE_SEPARATOR = MsgDiagnosticiProperties.DIAGNOSTIC_WITH_DYNAMIC_INFO_TYPE_SEPARATOR;


	public static TransazioneBean readManifestTransazione(byte[] fileContent) throws UtilsException, JAXBException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		PddMonitorProperties monitorProperties = PddMonitorProperties.getInstance(LoggerManager.getPddMonitorCoreLogger());
		return readManifestTransazione(fileContent,monitorProperties.isAttivoTransazioniDataAccettazione(), monitorProperties.isDataUscitaRispostaUseDateAfterResponseSent());
	}

	public static TransazioneBean readManifestTransazione(byte[] fileContent, boolean isAttivoTransazioniDataAccettazione, boolean dataUscitaRispostaValorizzataDopoSpedizioneRisposta) throws JAXBException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
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

		JAXBContext jc = JAXBContext.newInstance(org.openspcoop2.web.monitor.transazioni.core.manifest.ObjectFactory.class.getPackage().getName());
		
		org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType transazione = estraiOggettoJAXB(fileContent, jc, org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType.class);

		TransazioneBean transazioneBean = new TransazioneBean();

		// Stato di una transazione (marca la transazione con uno stato tramite la configurazione plugin)
		if(StringUtils.isNotEmpty(transazione.getStato())){
			transazioneBean.setStato(UtilityTransazioni.escapeXmlValue(transazione.getStato()));
		}

		fillRuoloTransazione(transazione, transazioneBean);

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
		DeserializerTransazioni.fillProtocollo(transazioneBean,transazione);

		// Esito di una transazione
		Esito esito = transazione.getEsito();
		if (esito != null && esito.getCodice().intValue() >= 0) {
			transazioneBean.setEsito(esito.getCodice().intValue());
			if(esito.getConsegneMultiple() != null) {
				transazioneBean.setConsegneMultipleInCorso(esito.getConsegneMultiple().intValue());
			}
		}

		if(transazione.getContesto()!=null){
			Contesto contesto = transazione.getContesto();
			String code = contesto.getCodice();
			transazioneBean.setEsitoContesto(code);
		}

		estraiTempiAttraversamentoTransazione(isAttivoTransazioniDataAccettazione, dataUscitaRispostaValorizzataDopoSpedizioneRisposta, transazione, transazioneBean);


		estraiDimensioneMessaggiTransazione(transazione, transazioneBean);


		estraiDatiDominioTransazione(transazione, transazioneBean);


		estraiDatiIntegrazioneCorrelazioneApplicativaTransazione(transazione, transazioneBean);

		// informazioni di integrazione (I.M.)
		if (transazione.getIntegrationManager() != null) {
			IntegrationManager im = transazione.getIntegrationManager();
			transazioneBean.setOperazioneIm(UtilityTransazioni.escapeXmlValue(im.getOperazione()));
		}

		estraiDatiIntegrazioneTransazione(transazione, transazioneBean);

		// applicativo token
		if(transazione.getServizioApplicativoToken() != null) {
			ServizioApplicativoToken sa = transazione.getServizioApplicativoToken();
			IDServizioApplicativo tokenClient = new IDServizioApplicativo();
			tokenClient.setNome(sa.getNome());
			IDSoggetto idSoggettoProprietario = new IDSoggetto();
			idSoggettoProprietario.setTipo(sa.getTipoSoggetto());
			idSoggettoProprietario.setNome(sa.getNomeSoggetto());
			tokenClient.setIdSoggettoProprietario(idSoggettoProprietario );
			transazioneBean.setTokenClient(tokenClient );
		}

		// eventi di gestione
		if(StringUtils.isNotEmpty(transazione.getEventiGestione())){
			transazioneBean.setEventiLabel(UtilityTransazioni.escapeXmlValue(transazione.getEventiGestione()));
		}

		// uuid
		transazioneBean.setIdTransazione(transazione.getUuid());

		return transazioneBean;
	}

	private static void estraiDatiIntegrazioneCorrelazioneApplicativaTransazione(org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType transazione,
			TransazioneBean transazioneBean) {
		// informazioni di integrazione (correlazione applicativa)		
		if(transazione.getCorrelazioneApplicativa() != null) {
			CorrelazioneApplicativa correlazioneApplicativa = transazione.getCorrelazioneApplicativa();
			if(StringUtils.isNotEmpty(correlazioneApplicativa.getIdRichiesta())){
				transazioneBean.setIdCorrelazioneApplicativa(UtilityTransazioni.escapeXmlValue(correlazioneApplicativa.getIdRichiesta()));
			}
			if(StringUtils.isNotEmpty(correlazioneApplicativa.getIdRisposta())){
				transazioneBean.setIdCorrelazioneApplicativaRisposta(UtilityTransazioni.escapeXmlValue(correlazioneApplicativa.getIdRisposta()));
			}
		}


		// informazioni di integrazione (servizio applicativo)		
		if (StringUtils.isNotEmpty(transazione.getServizioApplicativoFruitore())) {
			transazioneBean.setServizioApplicativoFruitore(UtilityTransazioni.escapeXmlValue(transazione.getServizioApplicativoFruitore()));
		}
		if (StringUtils.isNotEmpty(transazione.getServizioApplicativoErogatore())) {
			transazioneBean.setServizioApplicativoErogatore(UtilityTransazioni.escapeXmlValue(transazione.getServizioApplicativoErogatore()));
		}
	}

	private static void estraiDimensioneMessaggiTransazione(org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType transazione,
			TransazioneBean transazioneBean) {
		// Dimensione messaggi gestiti
		if (transazione.getDimensioneMessaggi() != null) {

			DimensioneMessaggi dm = transazione.getDimensioneMessaggi();

			if (dm.getRichiestaIngresso() != null) {
				transazioneBean.setRichiestaIngressoBytes(dm.getRichiestaIngresso());
			}

			if (dm.getRichiestaUscita() != null) {
				transazioneBean.setRichiestaUscitaBytes(dm.getRichiestaUscita());
			}

			if (dm.getRispostaIngresso() != null) {
				transazioneBean.setRispostaIngressoBytes(dm.getRispostaIngresso());
			}

			if (dm.getRispostaUscita() != null) {
				transazioneBean.setRispostaUscitaBytes(dm.getRispostaUscita());
			}
		}
	}

	private static void estraiDatiDominioTransazione(org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType transazione,
			TransazioneBean transazioneBean) {
		// Dati PdD
		if(transazione.getDominio()!=null){
			org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType.Dominio dominio = transazione.getDominio();
			if(dominio.getSoggetto() != null) {
				org.openspcoop2.web.monitor.transazioni.core.manifest.SoggettoType soggetto = dominio.getSoggetto();
				if(StringUtils.isNotEmpty(soggetto.getTipo())){
					transazioneBean.setPddTipoSoggetto(UtilityTransazioni.escapeXmlValue(soggetto.getTipo()));
				}
				if(StringUtils.isNotEmpty(soggetto.getValue())){
					transazioneBean.setPddNomeSoggetto(UtilityTransazioni.escapeXmlValue(soggetto.getValue()));
				}
				if(StringUtils.isNotEmpty(soggetto.getIdentificativoPorta())){
					transazioneBean.setPddCodice(UtilityTransazioni.escapeXmlValue(soggetto.getIdentificativoPorta()));
				}
				dominio.setSoggetto(soggetto);
			}
			if(dominio.getRuolo()!=null){
				switch (dominio.getRuolo()) {
				case DELEGATA:
					transazioneBean.setPddRuolo(PddRuolo.DELEGATA);
					break;
				case APPLICATIVA:
					transazioneBean.setPddRuolo(PddRuolo.APPLICATIVA);
					break;
				case INTEGRATION_MANAGER:
					transazioneBean.setPddRuolo(PddRuolo.INTEGRATION_MANAGER);
					break;
				case ROUTER:
					transazioneBean.setPddRuolo(PddRuolo.ROUTER);
					break;
				}
			}
			transazioneBean.setClusterId(dominio.getClusterId());
		}
	}

	private static void estraiDatiIntegrazioneTransazione(org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType transazione,
			TransazioneBean transazioneBean) {
		// location
		if(transazione.getDatiIntegrazione() != null) { 
			final DatiIntegrazione datiIntegrazione = transazione.getDatiIntegrazione();
			if((datiIntegrazione.getIndirizzoRichiesta()!=null && StringUtils.isNotEmpty(datiIntegrazione.getIndirizzoRichiesta()))){
				transazioneBean.setLocationRichiesta(UtilityTransazioni.escapeXmlValue(datiIntegrazione.getIndirizzoRichiesta()));
			}
			if((datiIntegrazione.getIndirizzoRisposta()!=null && StringUtils.isNotEmpty(datiIntegrazione.getIndirizzoRisposta()))){
				transazioneBean.setLocationRisposta(UtilityTransazioni.escapeXmlValue(datiIntegrazione.getIndirizzoRisposta()));
			}
			if((datiIntegrazione.getNomePorta()!=null && StringUtils.isNotEmpty(datiIntegrazione.getNomePorta()))){
				transazioneBean.setNomePorta(UtilityTransazioni.escapeXmlValue(datiIntegrazione.getNomePorta()));
			}
			if((datiIntegrazione.getCredenziali()!=null && StringUtils.isNotEmpty(datiIntegrazione.getCredenziali()))){
				transazioneBean.setCredenziali(UtilityTransazioni.escapeXmlValue(datiIntegrazione.getCredenziali()));
			}
			if((datiIntegrazione.getConnettore()!=null && StringUtils.isNotEmpty(datiIntegrazione.getConnettore()))){
				transazioneBean.setLocationConnettore(UtilityTransazioni.escapeXmlValue(datiIntegrazione.getConnettore()));
			}
			if((datiIntegrazione.getUrlInvocazione()!=null && StringUtils.isNotEmpty(datiIntegrazione.getUrlInvocazione()))){
				transazioneBean.setUrlInvocazione(UtilityTransazioni.escapeXmlValue(datiIntegrazione.getUrlInvocazione()));
			}
			if((datiIntegrazione.getIndirizzoClient()!=null && StringUtils.isNotEmpty(datiIntegrazione.getIndirizzoClient()))){
				transazioneBean.setSocketClientAddress(UtilityTransazioni.escapeXmlValue(datiIntegrazione.getIndirizzoClient()));
			}
			if((datiIntegrazione.getXForwardedFor()!=null && StringUtils.isNotEmpty(datiIntegrazione.getXForwardedFor()))){
				transazioneBean.setTransportClientAddress(UtilityTransazioni.escapeXmlValue(datiIntegrazione.getXForwardedFor()));
			}
			if((datiIntegrazione.getTipoRichiesta()!=null && StringUtils.isNotEmpty(datiIntegrazione.getTipoRichiesta()))){
				transazioneBean.setTipoRichiesta(UtilityTransazioni.escapeXmlValue(datiIntegrazione.getTipoRichiesta()));
			}
			if((datiIntegrazione.getCodiceRispostaIngresso()!=null && StringUtils.isNotEmpty(datiIntegrazione.getCodiceRispostaIngresso()))){
				transazioneBean.setCodiceRispostaIngresso(UtilityTransazioni.escapeXmlValue(datiIntegrazione.getCodiceRispostaIngresso()));
			}
			if((datiIntegrazione.getCodiceRispostaUscita()!=null && StringUtils.isNotEmpty(datiIntegrazione.getCodiceRispostaUscita()))){
				transazioneBean.setCodiceRispostaUscita(UtilityTransazioni.escapeXmlValue(datiIntegrazione.getCodiceRispostaUscita()));
			}

			if(StringUtils.isNotEmpty(datiIntegrazione.getIdentificativoAutenticato())) {
				transazioneBean.setTrasportoMittenteLabel(UtilityTransazioni.escapeXmlValue(datiIntegrazione.getIdentificativoAutenticato()));
			}
			if(StringUtils.isNotEmpty(datiIntegrazione.getTokenIssuer())) {
				transazioneBean.setTokenIssuerLabel(UtilityTransazioni.escapeXmlValue(datiIntegrazione.getTokenIssuer()));
			}
			if(StringUtils.isNotEmpty(datiIntegrazione.getTokenClientId())) {
				transazioneBean.setTokenClientIdLabel(UtilityTransazioni.escapeXmlValue(datiIntegrazione.getTokenClientId()));
			}
			if(StringUtils.isNotEmpty(datiIntegrazione.getTokenSubject())) {
				transazioneBean.setTokenSubjectLabel(UtilityTransazioni.escapeXmlValue(datiIntegrazione.getTokenSubject()));
			}
			if(StringUtils.isNotEmpty(datiIntegrazione.getTokenUsername())) {
				transazioneBean.setTokenUsernameLabel(UtilityTransazioni.escapeXmlValue(datiIntegrazione.getTokenUsername()));
			}
			if(StringUtils.isNotEmpty(datiIntegrazione.getTokenEMail())) {
				transazioneBean.setTokenMailLabel(UtilityTransazioni.escapeXmlValue(datiIntegrazione.getTokenEMail()));
			}
		}
	}

	private static void estraiTempiAttraversamentoTransazione(boolean isAttivoTransazioniDataAccettazione,
			boolean dataUscitaRispostaValorizzataDopoSpedizioneRisposta,
			org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType transazione,
			TransazioneBean transazioneBean) throws JAXBException {
		// Tempi di latenza
		if (transazione.getTempiAttraversamento() != null) {
			TempiAttraversamento tempi = transazione.getTempiAttraversamento();

			try{
				if(isAttivoTransazioniDataAccettazione && tempi.getRichiestaAccettazione() != null) {
					transazioneBean.setDataAccettazioneRichiesta(tempi.getRichiestaAccettazione().toGregorianCalendar().getTime());
				}

				if (tempi.getRichiestaIngresso() != null) {
					transazioneBean.setDataIngressoRichiesta(tempi.getRichiestaIngresso().toGregorianCalendar().getTime());
				}

				if (tempi.getRichiestaIngressoAcquisita() != null) {
					transazioneBean.setDataIngressoRichiestaStream(tempi.getRichiestaIngressoAcquisita().toGregorianCalendar().getTime());
				}

				if (tempi.getRichiestaUscita() != null) {
					transazioneBean.setDataUscitaRichiesta(tempi.getRichiestaUscita().toGregorianCalendar().getTime());
				}

				if (tempi.getRichiestaUscitaConsegnata() != null) {
					transazioneBean.setDataUscitaRichiestaStream(tempi.getRichiestaUscitaConsegnata().toGregorianCalendar().getTime());
				}

				if(isAttivoTransazioniDataAccettazione && tempi.getRispostaAccettazione() != null) {
					transazioneBean.setDataAccettazioneRisposta(tempi.getRispostaAccettazione().toGregorianCalendar().getTime());
				}

				if (tempi.getRispostaIngresso() != null) {
					transazioneBean.setDataIngressoRisposta(tempi.getRispostaIngresso().toGregorianCalendar().getTime());
				}

				if (tempi.getRispostaIngressoAcquisita() != null) {
					transazioneBean.setDataIngressoRispostaStream(tempi.getRispostaIngressoAcquisita().toGregorianCalendar().getTime());
				}

				if(dataUscitaRispostaValorizzataDopoSpedizioneRisposta) {
					if (tempi.getRispostaUscita() != null) {
						transazioneBean.setDataUscitaRispostaStream(tempi.getRispostaUscita().toGregorianCalendar().getTime());
					}

					if (tempi.getRispostaUscitaConsegnata() != null) {
						transazioneBean.setDataUscitaRisposta(tempi.getRispostaUscitaConsegnata().toGregorianCalendar().getTime());
					}
				}
				else {
					if (tempi.getRispostaUscita() != null) {
						transazioneBean.setDataUscitaRisposta(tempi.getRispostaUscita().toGregorianCalendar().getTime());
					}

					if (tempi.getRispostaUscitaConsegnata() != null) {
						transazioneBean.setDataUscitaRispostaStream(tempi.getRispostaUscitaConsegnata().toGregorianCalendar().getTime());
					}
				}

				transazioneBean.setLatenzaTotale(tempi.getLatenzaTotale());
				transazioneBean.setLatenzaServizio(tempi.getLatenzaServizio());
				transazioneBean.setLatenzaPorta(tempi.getLatenzaPorta());
				
				// data accettazione in realta' e' obbligatoria
				if(transazioneBean.getDataAccettazioneRichiesta() == null) {
					transazioneBean.setDataAccettazioneRichiesta(transazioneBean.getDataIngressoRichiesta());
				}
				if(transazioneBean.getDataAccettazioneRisposta() == null) {
					transazioneBean.setDataAccettazioneRisposta(transazioneBean.getDataUscitaRisposta());
				}
			}catch(Exception e){
				throw new JAXBException(e.getMessage(),e);
			}
		}
	}

	private static void fillRuoloTransazione(org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType transazione,
			TransazioneBean transazioneBean) {
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
		if(RuoloType.SCONOSCIUTO.equals(transazione.getRuolo())){
			transazioneBean.setRuoloTransazione(-1);
		}
		else if(RuoloType.INVOCAZIONE_ONEWAY.equals(transazione.getRuolo())){
			transazioneBean.setRuoloTransazione(1);
		}
		else if(RuoloType.INVOCAZIONE_SINCRONA.equals(transazione.getRuolo())){
			transazioneBean.setRuoloTransazione(2);
		}
		else if(RuoloType.INVOCAZIONE_ASINCRONA_SIMMETRICA.equals(transazione.getRuolo())){
			transazioneBean.setRuoloTransazione(3);
		}
		else if(RuoloType.RISPOSTA_ASINCRONA_SIMMETRICA.equals(transazione.getRuolo())){
			transazioneBean.setRuoloTransazione(4);
		}
		else if(RuoloType.INVOCAZIONE_ASINCRONA_ASIMMETRICA.equals(transazione.getRuolo())){
			transazioneBean.setRuoloTransazione(5);
		}
		else if(RuoloType.RICHIESTA_STATO_ASINCRONA_ASIMMETRICA.equals(transazione.getRuolo())){
			transazioneBean.setRuoloTransazione(6);
		}
		else if(RuoloType.INTEGRATION_MANAGER.equals(transazione.getRuolo())){
			transazioneBean.setRuoloTransazione(7);
		}
	}

	private static void fillProtocollo(TransazioneBean transazioneDest,org.openspcoop2.web.monitor.transazioni.core.manifest.TransazioneType transazioneSrc) {

		org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType protocollo = transazioneSrc.getProtocollo();

		if(protocollo != null) {
			transazioneDest.setProtocollo(protocollo.getName());

			estraiProtocolloSoggettoFruitore(transazioneDest, protocollo);
			
			estraiProtocolloSoggettoErogatore(transazioneDest, protocollo);

			estraiProtocolloServizio(transazioneDest, protocollo);
			
			estraiProtocolloAPI(transazioneDest, protocollo);

			estraiProtocolloOperazione(transazioneDest, protocollo);

			// Identificativi Messaggi
			if(StringUtils.isNotEmpty(protocollo.getIdMessaggioRichiesta())){
				transazioneDest.setIdMessaggioRichiesta(UtilityTransazioni.escapeXmlValue(protocollo.getIdMessaggioRichiesta()));
			}
			if(StringUtils.isNotEmpty(protocollo.getIdMessaggioRisposta())){
				transazioneDest.setIdMessaggioRisposta(UtilityTransazioni.escapeXmlValue(protocollo.getIdMessaggioRisposta()));
			}
			if((protocollo.getDataIdMsgRichiesta()!=null)){
				transazioneDest.setDataIdMsgRichiesta(protocollo.getDataIdMsgRichiesta().toGregorianCalendar().getTime());
			}
			if((protocollo.getDataIdMsgRisposta()!=null)){
				transazioneDest.setDataIdMsgRisposta(protocollo.getDataIdMsgRisposta().toGregorianCalendar().getTime());
			}

			estraiProtocolloProfilo(transazioneDest, protocollo);

			if(StringUtils.isNotEmpty(protocollo.getIdCollaborazione())){
				transazioneDest.setIdCollaborazione(UtilityTransazioni.escapeXmlValue(protocollo.getIdCollaborazione()));
			}
			if(StringUtils.isNotEmpty(protocollo.getUriAccordoServizio())){
				transazioneDest.setUriAccordoServizio(UtilityTransazioni.escapeXmlValue(protocollo.getUriAccordoServizio()));
			}

			estraiProtocolloProfiloAsincrono(transazioneDest, protocollo);

			estraiProtocolloDigest(transazioneDest, protocollo);

			estratiProtocolloDuplicati(transazioneDest, protocollo);
		}
	}

	private static void estraiProtocolloProfilo(TransazioneBean transazioneDest,
			org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType protocollo) {
		// altre informazioni
		if(protocollo.getProfilo() != null) { 
			org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType.Profilo profilo = protocollo.getProfilo();
			if((profilo.getValue()!=null && StringUtils.isNotEmpty(profilo.getValue()))){
				transazioneDest.setProfiloCollaborazioneProt(UtilityTransazioni.escapeXmlValue(profilo.getValue()));
			}
			if((profilo.getCodice()!=null && StringUtils.isNotEmpty(profilo.getCodice()))){
				transazioneDest.setProfiloCollaborazioneOp2(UtilityTransazioni.escapeXmlValue(profilo.getCodice()));
			}
		}
	}

	private static void estratiProtocolloDuplicati(TransazioneBean transazioneDest,
			org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType protocollo) {
		// Duplicati
		if (protocollo.getDuplicati() != null) {
			org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType.Duplicati duplicati = protocollo.getDuplicati();
			if (duplicati.getRichiesta() != null) {
				transazioneDest.setDuplicatiRichiesta(duplicati.getRichiesta());
			}
			if (duplicati.getRisposta() != null) {
				transazioneDest.setDuplicatiRisposta(duplicati.getRisposta());
			}
		}
	}

	private static void estraiProtocolloDigest(TransazioneBean transazioneDest,
			org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType protocollo) {
		// Digest
		if(protocollo.getDigest() != null) { 
			org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType.Digest digest = protocollo.getDigest();
			if((digest.getRichiesta()!=null && StringUtils.isNotEmpty(digest.getRichiesta()))){
				transazioneDest.setDigestRichiesta(digest.getRichiesta());
			}
			if((digest.getRisposta()!=null && StringUtils.isNotEmpty(digest.getRisposta()))){
				transazioneDest.setDigestRisposta(digest.getRisposta());
			}
		}
	}

	private static void estraiProtocolloProfiloAsincrono(TransazioneBean transazioneDest,
			org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType protocollo) {
		// Identificativo asincrono
		if(protocollo.getProfiloAsincrono() != null) {
			org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType.ProfiloAsincrono profiloAsincrono = protocollo.getProfiloAsincrono();
			if((profiloAsincrono.getIdCorrelazione()!=null && StringUtils.isNotEmpty(profiloAsincrono.getIdCorrelazione()))){
				transazioneDest.setIdAsincrono(UtilityTransazioni.escapeXmlValue(profiloAsincrono.getIdCorrelazione()));
			}
			if(profiloAsincrono.getServizioCorrelato() != null){
				org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType.ProfiloAsincrono.ServizioCorrelato servizioCorrelato = profiloAsincrono.getServizioCorrelato();
				if((servizioCorrelato.getTipo()!=null && StringUtils.isNotEmpty(servizioCorrelato.getTipo()))){
					transazioneDest.setTipoServizioCorrelato(UtilityTransazioni.escapeXmlValue(servizioCorrelato.getTipo()));
				}
				if((servizioCorrelato.getValue()!=null && StringUtils.isNotEmpty(servizioCorrelato.getValue()))){
					transazioneDest.setNomeServizioCorrelato(UtilityTransazioni.escapeXmlValue(servizioCorrelato.getValue()));
				}
			}
		}
	}

	private static void estraiProtocolloOperazione(TransazioneBean transazioneDest,
			org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType protocollo) {
		// azione
		if((protocollo.getAzione()!=null && StringUtils.isNotEmpty(protocollo.getAzione()))){
			transazioneDest.setAzione(UtilityTransazioni.escapeXmlValue(protocollo.getAzione()));
			transazioneDest.setOperazioneLabel(transazioneDest.getAzione());
		}
	}

	private static void estraiProtocolloAPI(TransazioneBean transazioneDest,
			org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType protocollo) {
		// api
		if(protocollo.getApi()!=null) {
			org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType.Api api = protocollo.getApi();
			if(api.getTipo() !=null) {
				if(TipoAPI.SOAP.name().equals(api.getTipo())) {
					transazioneDest.setTipoApi(TipoAPI.SOAP.getValoreAsInt());
				} else {
					transazioneDest.setTipoApi(TipoAPI.REST.getValoreAsInt());
				}
			}
			if(StringUtils.isNotEmpty(api.getNome())) {
				transazioneDest.setUriAccordoServizio(api.getNome());
			}
			if(api.getTags() !=null && api.getTags().getTag() !=null){
				List<String> tags = api.getTags().getTag();

				StringBuilder sb = new StringBuilder();
				for (String tag : tags) {
					if(sb.length()>0) {
						sb.append(",");
					}
					sb.append(tag);
				}

				transazioneDest.setGruppi(sb.toString());
			}
		}
	}

	private static void estraiProtocolloServizio(TransazioneBean transazioneDest,
			org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType protocollo) {
		// Servizio
		if(protocollo.getServizio() != null){ 
			org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType.Servizio servizio = protocollo.getServizio();
			if((servizio.getTipo()!=null && StringUtils.isNotEmpty(servizio.getTipo()))){
				transazioneDest.setTipoServizio(UtilityTransazioni.escapeXmlValue(servizio.getTipo()));
			}
			if((servizio.getValue()!=null && StringUtils.isNotEmpty(servizio.getValue()))){
				transazioneDest.setNomeServizio(UtilityTransazioni.escapeXmlValue(servizio.getValue()));
			}
			if((servizio.getVersione()!=null && StringUtils.isNotEmpty(servizio.getVersione()))){
				transazioneDest.setVersioneServizio(Integer.parseInt(servizio.getVersione()));
			}
		}
	}

	private static void estraiProtocolloSoggettoErogatore(TransazioneBean transazioneDest,
			org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType protocollo) {
		// Soggetto Erogatore
		if(protocollo.getErogatore() != null){ 
			org.openspcoop2.web.monitor.transazioni.core.manifest.SoggettoType soggetto = protocollo.getErogatore();
			if((soggetto.getTipo()!=null && StringUtils.isNotEmpty(soggetto.getTipo()))){
				transazioneDest.setTipoSoggettoErogatore(UtilityTransazioni.escapeXmlValue(soggetto.getTipo()));
			}
			if((soggetto.getValue()!=null && StringUtils.isNotEmpty(soggetto.getValue()))){
				transazioneDest.setNomeSoggettoErogatore(UtilityTransazioni.escapeXmlValue(soggetto.getValue()));
			}
			if((soggetto.getIdentificativoPorta()!=null && StringUtils.isNotEmpty(soggetto.getIdentificativoPorta()))){
				transazioneDest.setIdportaSoggettoErogatore(UtilityTransazioni.escapeXmlValue(soggetto.getIdentificativoPorta()));
			}
			if((soggetto.getIndirizzo()!=null && StringUtils.isNotEmpty(soggetto.getIndirizzo()))){
				transazioneDest.setIndirizzoSoggettoErogatore(UtilityTransazioni.escapeXmlValue(soggetto.getIndirizzo()));
			}
		}
	}

	private static void estraiProtocolloSoggettoFruitore(TransazioneBean transazioneDest,
			org.openspcoop2.web.monitor.transazioni.core.manifest.ProtocolloType protocollo) {
		// Soggetto Fruitore
		if(protocollo.getFruitore() != null){ 
			org.openspcoop2.web.monitor.transazioni.core.manifest.SoggettoType soggetto = protocollo.getFruitore();
			if((soggetto.getTipo()!=null && StringUtils.isNotEmpty(soggetto.getTipo()))){
				transazioneDest.setTipoSoggettoFruitore(UtilityTransazioni.escapeXmlValue(soggetto.getTipo()));
			}
			if((soggetto.getValue()!=null && StringUtils.isNotEmpty(soggetto.getValue()))){
				transazioneDest.setNomeSoggettoFruitore(UtilityTransazioni.escapeXmlValue(soggetto.getValue()));
			}
			if((soggetto.getIdentificativoPorta()!=null && StringUtils.isNotEmpty(soggetto.getIdentificativoPorta()))){
				transazioneDest.setIdportaSoggettoFruitore(UtilityTransazioni.escapeXmlValue(soggetto.getIdentificativoPorta()));
			}
			if((soggetto.getIndirizzo()!=null && StringUtils.isNotEmpty(soggetto.getIndirizzo()))){
				transazioneDest.setIndirizzoSoggettoFruitore(UtilityTransazioni.escapeXmlValue(soggetto.getIndirizzo()));
			}
		}
	}

	public static TransazioneApplicativoServerBean readManifestTransazioneApplicativoServer(byte[] fileContent) throws JAXBException, UtilsException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		PddMonitorProperties monitorProperties = PddMonitorProperties.getInstance(LoggerManager.getPddMonitorCoreLogger());
		return readManifestTransazioneApplicativoServer(fileContent, monitorProperties.isAttivoTransazioniDataAccettazione());
	}

	public static TransazioneApplicativoServerBean readManifestTransazioneApplicativoServer(byte[] fileContent, boolean isAttivoTransazioniDataAccettazione) throws JAXBException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		JAXBContext jc = JAXBContext.newInstance(org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ObjectFactory.class.getPackage().getName());
		
		org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType consegna = estraiOggettoJAXB(fileContent, jc, org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType.class);

		TransazioneApplicativoServerBean transazioneApplicativoServerBean = new TransazioneApplicativoServerBean();

		// Esito
		org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType.Esito esito = consegna.getEsito();

		if(esito != null){
			if (esito.getDettaglioEsito() !=null) {
				// <esito>ESITO_CALCOLATO_IN_FORMA_LEGGIBILE_DA_UMANO</esito>
				DettaglioEsito dettaglioEsito = esito.getDettaglioEsito();
				if(dettaglioEsito.getCodice()!=null) {
					transazioneApplicativoServerBean.setDettaglioEsito(dettaglioEsito.getCodice().intValue());
				}
			}
			transazioneApplicativoServerBean.setConsegnaTerminata(esito.isConsegnaTerminata());
			if(esito.getDataMessaggioScaduto()!=null) {
				transazioneApplicativoServerBean.setDataMessaggioScaduto(esito.getDataMessaggioScaduto().toGregorianCalendar().getTime());
			}

			transazioneApplicativoServerBean.setConsegnaTrasparente(esito.isConsegnaTrasparente());
			transazioneApplicativoServerBean.setConsegnaIntegrationManager(esito.isConsegnaIntegrationManager());
		}

		estraiTempiAttraversamentoConsegna(isAttivoTransazioniDataAccettazione, consegna, transazioneApplicativoServerBean);

		// Dimensione messaggi gestiti
		if (consegna.getDimensioneMessaggi() != null) {

			org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType.DimensioneMessaggi dm = consegna.getDimensioneMessaggi();

			transazioneApplicativoServerBean.setRichiestaUscitaBytes(dm.getRichiestaUscita());
			transazioneApplicativoServerBean.setRispostaIngressoBytes(dm.getRispostaIngresso());
		}

		estraiDatiConsegna(consegna, transazioneApplicativoServerBean);

		estraiDatiIMConsegna(consegna, transazioneApplicativoServerBean);

		// dati consegna
		transazioneApplicativoServerBean.setIdTransazione(UtilityTransazioni.escapeXmlValue(consegna.getIdTransazione()));
		transazioneApplicativoServerBean.setServizioApplicativoErogatore(UtilityTransazioni.escapeXmlValue(consegna.getServizioApplicativoErogatore()));
		if(StringUtils.isNotEmpty(consegna.getNomeConnettore())) {
			transazioneApplicativoServerBean.setConnettoreNome(UtilityTransazioni.escapeXmlValue(consegna.getNomeConnettore()));
		}
		if (consegna.getDataRegistrazione() != null) {
			transazioneApplicativoServerBean.setDataRegistrazione(consegna.getDataRegistrazione().toGregorianCalendar().getTime());
		}
		if(StringUtils.isNotEmpty(consegna.getIdentificativoMessaggio())) {
			transazioneApplicativoServerBean.setIdentificativoMessaggio(UtilityTransazioni.escapeXmlValue(consegna.getIdentificativoMessaggio()));
		}
		if(StringUtils.isNotEmpty(consegna.getClusterIdInCoda())) {
			transazioneApplicativoServerBean.setClusterIdPresaInCarico(UtilityTransazioni.escapeXmlValue(consegna.getClusterIdInCoda()));
		}

		return transazioneApplicativoServerBean;
	}

	private static void estraiTempiAttraversamentoConsegna(boolean isAttivoTransazioniDataAccettazione,
			org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType consegna,
			TransazioneApplicativoServerBean transazioneApplicativoServerBean) {
		// Tempi di latenza
		if (consegna.getTempiAttraversamento() != null) {

			org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType.TempiAttraversamento tempi = consegna.getTempiAttraversamento();

			if(isAttivoTransazioniDataAccettazione && tempi.getRichiestaAccettazione() != null) {
				transazioneApplicativoServerBean.setDataAccettazioneRichiesta(tempi.getRichiestaAccettazione().toGregorianCalendar().getTime());
			}

			if (tempi.getRichiestaUscita() != null) {
				transazioneApplicativoServerBean.setDataUscitaRichiesta(tempi.getRichiestaUscita().toGregorianCalendar().getTime());
			}

			if (tempi.getRichiestaUscitaConsegnata() != null) {
				transazioneApplicativoServerBean.setDataUscitaRichiestaStream(tempi.getRichiestaUscitaConsegnata().toGregorianCalendar().getTime());
			}

			if(isAttivoTransazioniDataAccettazione && tempi.getRispostaAccettazione() != null) {
				transazioneApplicativoServerBean.setDataAccettazioneRisposta(tempi.getRispostaAccettazione().toGregorianCalendar().getTime());
			}

			if (tempi.getRispostaIngresso() != null) {
				transazioneApplicativoServerBean.setDataIngressoRisposta(tempi.getRispostaIngresso().toGregorianCalendar().getTime());
			}

			if (tempi.getRispostaIngressoAcquisita() != null) {
				transazioneApplicativoServerBean.setDataIngressoRispostaStream(tempi.getRispostaIngressoAcquisita().toGregorianCalendar().getTime());
			}
		}
	}

	private static void estraiDatiIMConsegna(org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType consegna,
			TransazioneApplicativoServerBean transazioneApplicativoServerBean) {
		// dati-integration-manager
		if(consegna.getDatiIntegrationManager() != null) {
			DatiIntegrationManager datiIM = consegna.getDatiIntegrationManager();

			transazioneApplicativoServerBean.setConsegnaIntegrationManager(true);

			if (datiIM.getDataPrimoPrelievo() != null) {
				transazioneApplicativoServerBean.setDataPrimoPrelievoIm(datiIM.getDataPrimoPrelievo().toGregorianCalendar().getTime());
			}
			if (datiIM.getDataPrelievo() != null) {
				transazioneApplicativoServerBean.setDataPrelievoIm(datiIM.getDataPrelievo().toGregorianCalendar().getTime());
			}
			if (datiIM.getNumeroPrelievi() != null) {
				transazioneApplicativoServerBean.setNumeroPrelieviIm(datiIM.getNumeroPrelievi().intValue());
			}
			if(datiIM.getClusterIdPrelievo()!=null && !"".equals(datiIM.getClusterIdPrelievo())) {
				transazioneApplicativoServerBean.setClusterIdPrelievoIm(UtilityTransazioni.escapeXmlValue(datiIM.getClusterIdPrelievo()));
			}
			if (datiIM.getDataEliminazione() != null) {
				transazioneApplicativoServerBean.setDataEliminazioneIm(datiIM.getDataEliminazione().toGregorianCalendar().getTime());
			}
			if(datiIM.getClusterIdEliminazione()!=null && !"".equals(datiIM.getClusterIdEliminazione())) {
				transazioneApplicativoServerBean.setClusterIdEliminazioneIm(UtilityTransazioni.escapeXmlValue(datiIM.getClusterIdEliminazione()));
			}
		}
	}

	private static void estraiDatiConsegna(org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType consegna,
			TransazioneApplicativoServerBean transazioneApplicativoServerBean) {
		// Dati Consegna
		if(consegna.getDatiConsegna() != null) {

			DatiConsegna datiConsegna = consegna.getDatiConsegna();

			estraiDatiUltimoErroreConsegna(transazioneApplicativoServerBean, datiConsegna);

			if(datiConsegna.getConnettore()!=null && !"".equals(datiConsegna.getConnettore())) {
				transazioneApplicativoServerBean.setLocationConnettore(UtilityTransazioni.escapeXmlValue(datiConsegna.getConnettore()));
			}
			if(datiConsegna.getCodiceRisposta()!=null && !"".equals(datiConsegna.getCodiceRisposta())) {
				transazioneApplicativoServerBean.setCodiceRisposta(UtilityTransazioni.escapeXmlValue(datiConsegna.getCodiceRisposta()));
			}
			if (datiConsegna.getDataPrimoTentativo() != null) {
				transazioneApplicativoServerBean.setDataPrimoTentativo(datiConsegna.getDataPrimoTentativo().toGregorianCalendar().getTime());
			}
			if (datiConsegna.getNumeroTentativi() != null) {
				transazioneApplicativoServerBean.setNumeroTentativi(datiConsegna.getNumeroTentativi().intValue());
			}
			if(datiConsegna.getClusterIdConsegna()!=null && !"".equals(datiConsegna.getClusterIdConsegna())) {
				transazioneApplicativoServerBean.setClusterIdConsegna(UtilityTransazioni.escapeXmlValue(datiConsegna.getClusterIdConsegna()));
			}
		}
	}

	private static void estraiDatiUltimoErroreConsegna(TransazioneApplicativoServerBean transazioneApplicativoServerBean,
			DatiConsegna datiConsegna) {
		if(datiConsegna.getUltimoErrore()!=null) {

			UltimoErrore ultimoErrore = datiConsegna.getUltimoErrore();

			transazioneApplicativoServerBean.setUltimoErrore(ultimoErrore.getDettaglio()); 

			if(ultimoErrore.getDettaglioEsito()!=null) {
				org.openspcoop2.web.monitor.transazioni.core.manifest_consegna.ConsegnaType.DatiConsegna.UltimoErrore.DettaglioEsito dettaglioEsito = ultimoErrore.getDettaglioEsito();
				if (dettaglioEsito.getCodice() != null) {
					transazioneApplicativoServerBean.setDettaglioEsitoUltimoErrore(dettaglioEsito.getCodice().intValue());
				}
			}

			if(StringUtils.isNotEmpty(ultimoErrore.getConnettore())) {
				transazioneApplicativoServerBean.setLocationUltimoErrore(UtilityTransazioni.escapeXmlValue(ultimoErrore.getConnettore()));
			}
			if(StringUtils.isNotEmpty(ultimoErrore.getCodiceRisposta())) {
				transazioneApplicativoServerBean.setCodiceRispostaUltimoErrore(UtilityTransazioni.escapeXmlValue(ultimoErrore.getCodiceRisposta()));
			}
			if (ultimoErrore.getData() != null) {
				transazioneApplicativoServerBean.setDataUltimoErrore(ultimoErrore.getData().toGregorianCalendar().getTime());
			}
			if(StringUtils.isNotEmpty(ultimoErrore.getClusterId())) {
				transazioneApplicativoServerBean.setClusterIdUltimoErrore(UtilityTransazioni.escapeXmlValue(ultimoErrore.getClusterId()));
			}
		}
	}

	public static List<DumpHeaderTrasporto> readHeadersTrasportoXml(byte[] fileContent, boolean asProperties) throws IOException, JAXBException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		List<DumpHeaderTrasporto> headers = null;

		if(asProperties) {
			headers = new ArrayList<>();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileContent)))) {
				String line;
				// Leggi riga per riga
				while ((line = reader.readLine()) != null) {
					// Ignora righe vuote
					if (line.trim().isEmpty()) {
						continue;
					}
					// Dividi la riga in nome e valore
					String[] parts = line.split("=");
					if (parts.length == 2) {
						String nome = parts[0].trim();  // Nome del header
						String valore = parts[1].trim();  // Valore del header

						// Aggiungi un nuovo DumpHeaderTrasporto alla lista
						DumpHeaderTrasporto dumpHeaderTrasporto = new DumpHeaderTrasporto();
						dumpHeaderTrasporto.setNome(nome);
						dumpHeaderTrasporto.setValore(valore);
						headers.add(dumpHeaderTrasporto);
					}
				}
			}
		}
		else {
			JAXBContext jc = JAXBContext.newInstance(org.openspcoop2.web.monitor.transazioni.core.header.ObjectFactory.class.getPackage().getName());
			
			org.openspcoop2.web.monitor.transazioni.core.header.TransazioneType transazione = estraiOggettoJAXB(fileContent, jc, org.openspcoop2.web.monitor.transazioni.core.header.TransazioneType.class);

			List<HeaderType> headersTransazione = transazione.getHeader();
			if(headersTransazione != null) {
				headers = new ArrayList<>();
				for (org.openspcoop2.web.monitor.transazioni.core.header.HeaderType headerType : headersTransazione) {
					DumpHeaderTrasporto header = new DumpHeaderTrasporto();
					header.setNome(headerType.getNome());
					header.setValore(headerType.getValore());

					headers.add(header);
				}
			}
		}
		return headers;
	}

	public static List<DumpMultipartHeader> readMultipartHeaderXml(byte[] fileContent, boolean asProperties) throws IOException, JAXBException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		List<DumpMultipartHeader> headers = null;

		if(asProperties) {
			headers = new ArrayList<>();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileContent)))) {
				String line;
				// Leggi riga per riga
				while ((line = reader.readLine()) != null) {
					// Ignora righe vuote
					if (line.trim().isEmpty()) {
						continue;
					}
					// Dividi la riga in nome e valore
					String[] parts = line.split("=");
					if (parts.length == 2) {
						String nome = parts[0].trim();  // Nome del header
						String valore = parts[1].trim();  // Valore del header

						// Aggiungi un nuovo DumpMultipartHeader alla lista
						DumpMultipartHeader dumpHeaderTrasporto = new DumpMultipartHeader();
						dumpHeaderTrasporto.setNome(nome);
						dumpHeaderTrasporto.setValore(valore);
						headers.add(dumpHeaderTrasporto);
					}
				}
			}
		}
		else {
			JAXBContext jc = JAXBContext.newInstance(org.openspcoop2.web.monitor.transazioni.core.header.ObjectFactory.class.getPackage().getName());
			
			org.openspcoop2.web.monitor.transazioni.core.header.TransazioneType transazione = estraiOggettoJAXB(fileContent, jc, org.openspcoop2.web.monitor.transazioni.core.header.TransazioneType.class);

			List<HeaderType> headersTransazione = transazione.getHeader();
			if(headersTransazione != null) {
				headers = new ArrayList<>();
				for (org.openspcoop2.web.monitor.transazioni.core.header.HeaderType headerType : headersTransazione) {
					DumpMultipartHeader header = new DumpMultipartHeader();
					header.setNome(headerType.getNome());
					header.setValore(headerType.getValore());

					headers.add(header);
				}
			}
		}
		return headers;
	}

	public static List<DumpHeaderAllegato> readAllegatoHeaderXml(byte[] fileContent, boolean asProperties) throws IOException, JAXBException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		List<DumpHeaderAllegato> headers = null;

		if(asProperties) {
			headers = new ArrayList<>();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileContent)))) {
				String line;
				// Leggi riga per riga
				while ((line = reader.readLine()) != null) {
					// Ignora righe vuote
					if (line.trim().isEmpty()) {
						continue;
					}
					// Dividi la riga in nome e valore
					String[] parts = line.split("=");
					if (parts.length == 2) {
						String nome = parts[0].trim();  // Nome del header
						String valore = parts[1].trim();  // Valore del header

						// Aggiungi un nuovo DumpHeaderTrasporto alla lista
						DumpHeaderAllegato dumpHeaderTrasporto = new DumpHeaderAllegato();
						dumpHeaderTrasporto.setNome(nome);
						dumpHeaderTrasporto.setValore(valore);
						headers.add(dumpHeaderTrasporto);
					}
				}
			}
		}
		else {
			JAXBContext jc = JAXBContext.newInstance(org.openspcoop2.web.monitor.transazioni.core.header.ObjectFactory.class.getPackage().getName());
			
			org.openspcoop2.web.monitor.transazioni.core.header.TransazioneType transazione = estraiOggettoJAXB(fileContent, jc, org.openspcoop2.web.monitor.transazioni.core.header.TransazioneType.class);

			List<HeaderType> headersTransazione = transazione.getHeader();
			if(headersTransazione != null) {
				headers = new ArrayList<>();
				for (org.openspcoop2.web.monitor.transazioni.core.header.HeaderType headerType : headersTransazione) {
					DumpHeaderAllegato header = new DumpHeaderAllegato();
					header.setNome(headerType.getNome());
					header.setValore(headerType.getValore());

					headers.add(header);
				}
			}
		}
		return headers;
	}

	public static List<DumpContenuto> readContenutiXml(byte[] fileContent, boolean asProperties) throws IOException, JAXBException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		List<DumpContenuto> headers = null;

		if(asProperties) {
			headers = new ArrayList<>();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileContent)))) {
				String line;
				// Leggi riga per riga
				while ((line = reader.readLine()) != null) {
					// Ignora righe vuote
					if (line.trim().isEmpty()) {
						continue;
					}
					// Dividi la riga in nome e valore
					String[] parts = line.split("=");
					if (parts.length == 2) {
						String nome = parts[0].trim();  // Nome del header
						String valore = parts[1].trim();  // Valore del header

						// Aggiungi un nuovo DumpHeaderTrasporto alla lista
						DumpContenuto dumpHeaderTrasporto = new DumpContenuto();
						dumpHeaderTrasporto.setNome(nome);
						TransactionContentUtils.setDumpContenutoValue(dumpHeaderTrasporto, valore);
						headers.add(dumpHeaderTrasporto);
					}
				}
			}
		}
		else {
			JAXBContext jc = JAXBContext.newInstance(org.openspcoop2.web.monitor.transazioni.core.contents.ObjectFactory.class.getPackage().getName());

			org.openspcoop2.web.monitor.transazioni.core.contents.ContentType transazione = estraiOggettoJAXB(fileContent, jc, org.openspcoop2.web.monitor.transazioni.core.contents.ContentType.class);

			List<RisorsaType> headersTransazione = transazione.getRisorsa();
			if(headersTransazione != null) {
				headers = new ArrayList<>();
				for (org.openspcoop2.web.monitor.transazioni.core.contents.RisorsaType headerType : headersTransazione) {
					DumpContenuto header = new DumpContenuto();
					header.setNome(headerType.getNome());
					TransactionContentUtils.setDumpContenutoValue(header, headerType.getValore());

					headers.add(header);
				}
			}
		}
		return headers;
	}

	private static <T> T estraiOggettoJAXB(byte[] fileContent, JAXBContext jc, Class<T> clazz) throws JAXBException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if (fileContent == null) {
			return clazz.getDeclaredConstructor().newInstance(); 
		}
		
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Object unmarshal = null;
		try(ByteArrayInputStream bais = new ByteArrayInputStream(fileContent)){
			unmarshal = unmarshaller.unmarshal(bais);
		}finally{
			// do nothing
		}
		T transazione = null;
		if(unmarshal instanceof JAXBElement<?>){
			transazione = clazz.cast(((JAXBElement<?>)unmarshal).getValue());
		}
		else{
			transazione =  clazz.cast(unmarshal);
		}
		return transazione;
	}

	public static Map<String, String> readManifestContenutiToMap(byte[] fileContent) throws IOException {
		Map<String, String> dataMap = new HashMap<>();

		// Usa BufferedReader per leggere il file
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileContent)))) {
			String line;
			while ((line = br.readLine()) != null) {
				// Salta le righe vuote o commenti, se presenti
				if (line.trim().isEmpty()) {
					continue;
				}

				// Trova la posizione del delimitatore "="
				int delimiterIndex = line.indexOf("=");

				if (delimiterIndex != -1) {
					String key = line.substring(0, delimiterIndex).trim();
					String value = line.substring(delimiterIndex + 1).trim();

					// Aggiungi la coppia chiave/valore alla mappa
					dataMap.put(key, value);
				}
			}
		}

		return dataMap;
	}

	public static List<MsgDiagnostico> readMsgDiagnostici(byte[] fileContent, TransazioneBean tr, TransazioneApplicativoServerBean tAS, boolean exportDiagnosticiUseProtocolSerialization) 
			throws IOException, JAXBException, ProtocolException, ParserConfigurationException, SAXException, XMLUtilsException, TransformerException, XMLException, XPathException, XPathNotValidException {
		List<MsgDiagnostico> msgs = new ArrayList<>();

		try {

			Element element = org.openspcoop2.utils.xml.XMLUtils.getInstance().newElement(eliminaIntestazioniXmlNonNecessarie(fileContent));
			AbstractXPathExpressionEngine xPathEngine = new org.openspcoop2.utils.xml.XPathExpressionEngine();
			DynamicNamespaceContext dnc = new DynamicNamespaceContext();
			dnc.findPrefixNamespace(element);
			NodeList nodeList;
			try {
				String protocollo = tAS != null ? tAS.getProtocollo() : tr.getProtocollo();
				IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
				IDiagnosticSerializer diagnosticoBuilder = null;
				if(exportDiagnosticiUseProtocolSerialization) {
					diagnosticoBuilder = pf.createDiagnosticSerializer();
				}
				else {
					diagnosticoBuilder = new DiagnosticSerializer(pf);
				}
				XMLRootElement xmlRootElement = diagnosticoBuilder.getXMLRootElement();

				String rootExpression = "/" + xmlRootElement.getPrefix() + ":" + xmlRootElement.getLocalName();
				Node rootNode = (Node) xPathEngine.getMatchPattern(element, dnc, rootExpression ,XPathReturnType.NODE);

				nodeList = rootNode.getChildNodes();

				// Ottieni tutti gli elementi "messaggio-diagnostico"
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					if(XMLUtils.isMessaggioDiagnostico(node)) {
						// Ottieni il byte array dal nodo
						byte[] byteArray = nodeToByteArray(node);

						MsgDiagnostico msg = new MsgDiagnostico(); 
						msg.setMessaggioDiagnostico(XMLUtils.toMessaggioDiagnosticoFromXml(byteArray));

						msgs.add(msg);
					}
				}
			} catch (XPathNotFoundException e) {
				// non trovato, restituisce lista vuota
				e.printStackTrace();
			}
		}finally {
			//donothing
		}

		return msgs;
	}

	public static List<TracciaBean> readTracce(byte[] fileContent, TransazioneBean tr, boolean exportTracceUseProtocolSerialization) 
			throws IOException, SAXException, ParserConfigurationException, XMLException, ProtocolException, XPathException, XPathNotValidException, TransformerException, org.openspcoop2.core.tracciamento.utils.XMLUtilsException 
	{
		List<TracciaBean> msgs = new ArrayList<>();

		try {

			Element element = org.openspcoop2.utils.xml.XMLUtils.getInstance().newElement(eliminaIntestazioniXmlNonNecessarie(fileContent));
			AbstractXPathExpressionEngine xPathEngine = new org.openspcoop2.utils.xml.XPathExpressionEngine();
			DynamicNamespaceContext dnc = new DynamicNamespaceContext();
			dnc.findPrefixNamespace(element);
			NodeList nodeList;
			try {
				String protocollo = tr.getProtocollo();
				ServiceBinding tipoApi = null;
				if(TipoAPI.REST.getValoreAsInt() == tr.getTipoApi()) {
					tipoApi = ServiceBinding.REST;
				}
				else if(TipoAPI.SOAP.getValoreAsInt() == tr.getTipoApi()) {
					tipoApi = ServiceBinding.SOAP;
				}

				IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
				ITracciaSerializer tracciaBuilder = null;
				if(exportTracceUseProtocolSerialization) {
					tracciaBuilder = pf.createTracciaSerializer();
				}
				else {
					tracciaBuilder = new TracciaSerializer(pf);
				}
				XMLRootElement xmlRootElement = tracciaBuilder.getXMLRootElement();

				String rootExpression = "/" + xmlRootElement.getPrefix() + ":" + xmlRootElement.getLocalName();
				Node rootNode = (Node) xPathEngine.getMatchPattern(element, dnc, rootExpression ,XPathReturnType.NODE);

				nodeList = rootNode.getChildNodes();

				// Ottieni tutti gli elementi "messaggio-diagnostico"
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					if(org.openspcoop2.core.tracciamento.utils.XMLUtils.isTraccia(node)) {
						// Ottieni il byte array dal nodo
						byte[] byteArray = nodeToByteArray(node);

						Traccia t = new Traccia(org.openspcoop2.core.tracciamento.utils.XMLUtils.toTracciaFromXml(byteArray));

						msgs.add(new TracciaBean(t, tipoApi));
					}
				}
			} catch (XPathNotFoundException e) {
				// non trovato, restituisce lista vuota
				e.printStackTrace();
			}
		}finally {
			//donothing
		}

		return msgs;
	}

	// Metodo per serializzare un nodo in un byte array
	private static byte[] nodeToByteArray(Node node) throws TransformerException, XMLException {
		// Crea un TransformerFactory e un Transformer
		TransformerFactory transformerFactory = org.openspcoop2.utils.xml.XMLUtils.getInstance().getTransformerFactory();
		Transformer transformer = transformerFactory.newTransformer();

		// Crea un ByteArrayOutputStream
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		// Crea una StreamResult con l'OutputStream
		StreamResult result = new StreamResult(byteArrayOutputStream);

		// Trasforma il nodo in un byte array
		DOMSource source = new DOMSource(node);
		transformer.transform(source, result);

		// Ottieni il byte array dal ByteArrayOutputStream
		return byteArrayOutputStream.toByteArray();
	}

	public static byte[] eliminaIntestazioniXmlNonNecessarie(byte[] inputBytes) {
		// Converte l'array di byte in Stringa usando la codifica UTF-8
		String xmlContent = new String(inputBytes, StandardCharsets.UTF_8).trim();
		String header = "";

		// Salvo l'intestazione XML se presente all'inizio
		if (xmlContent.startsWith("<?")) {
			int endHeader = xmlContent.indexOf("?>");
			if (endHeader != -1) {
				header = xmlContent.substring(0, endHeader + 2);
				// Rimuove l'intestazione dal contenuto per poterla poi reinserire
				xmlContent = xmlContent.substring(endHeader + 2);
			}
		}

		// Rimuovo tutte le eventuali altre dichiarazioni XML presenti nel resto del contenuto
		xmlContent = xmlContent.replaceAll("<\\?xml[^>]+\\?>", "").trim();

		// Combina l'intestazione con il contenuto "pulito"
		String cleanedXml = header + "\n" + xmlContent;

		// Restituisce il risultato come array di byte
		return cleanedXml.getBytes(StandardCharsets.UTF_8);
	}

}
