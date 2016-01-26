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


package org.openspcoop2.web.ctrlstat.servlet.archivi;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.logger.DriverTracciamento;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.Allegato;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Riscontro;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * TracciamentoTesto
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class TracciamentoTesto extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		// Inizializzo PageData
		PageData pd = generalHelper.initPageData();


		try {
			ArchiviHelper archiviHelper = new ArchiviHelper(request, pd, session);
			
			String nomeDs = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DATASOURCE);
			ArchiviCore archiviCore = new ArchiviCore();
			DriverTracciamento driver = archiviCore.getDriverTracciamento(nomeDs);
			
			String codicePorta = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CODICE_PORTA);
			String nomeSoggettoPorta = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_SOGGETTO_PORTA);
			String tipoSoggettoPorta = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_SOGGETTO_PORTA);
			IDSoggetto idSoggettoPorta = new IDSoggetto(tipoSoggettoPorta, nomeSoggettoPorta, codicePorta);
			
			String idMessaggio = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO);
			
			String datainizio = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_INIZIO);
			String datafine = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_FINE);
			String profcoll = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROFILO_COLLABORAZIONE);
			String tipo_servizio = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_SERVIZIO);
			String servizio = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_SERVIZIO);
			String tipo_mittente = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_MITTENTE);
			String nome_mittente = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_MITTENTE);
			String tipo_destinatario = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_DESTINATARIO);
			String nome_destinatario = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DESTINATARIO);
			String nome_azione = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_AZIONE);
			String correlazioneApplicativa = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA);
			String protocollo = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO);
			String identificativoMessaggioSearch = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO_SEARCH);

			Parameter pDs = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DATASOURCE, (nomeDs==null ? "" : nomeDs));
			Parameter pProfColl = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROFILO_COLLABORAZIONE,profcoll);
			Parameter pDataI = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_INIZIO,(datainizio == null ? "" : datainizio) );
			Parameter pDataF = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_FINE,(datafine == null ? "" : datafine) );
			Parameter pTipoM = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_MITTENTE,(tipo_mittente == null ? "" : tipo_mittente) );
			Parameter pNomeM = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_MITTENTE, (nome_mittente == null ? "" : nome_mittente));
			Parameter pTipoD = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_DESTINATARIO, (tipo_destinatario == null ? "" : tipo_destinatario) );
			Parameter pNomeD = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DESTINATARIO,(nome_destinatario == null ? "" : nome_destinatario) );
			Parameter pTipoS = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_SERVIZIO, (tipo_servizio == null ? "" : tipo_servizio) );
			Parameter pNomeS = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_SERVIZIO,(servizio == null ? "" : servizio) );
			Parameter pAzione = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_AZIONE,(nome_azione == null ? "" : nome_azione));
			Parameter pCorrAppl = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA,(correlazioneApplicativa == null ? "" : correlazioneApplicativa));
			Parameter pProt = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO, (protocollo == null ? "" : protocollo));
			Parameter pIdentMsg = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO_SEARCH, (identificativoMessaggioSearch == null ? "" : identificativoMessaggioSearch));
			
			Parameter pEditMode = new Parameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME, Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_END);
			
			Traccia traccia = driver.getTraccia(idMessaggio, idSoggettoPorta);

			// Timestamp gdo=traccia.getGdo();
			Busta busta = traccia.getBusta();

			// String raw=TracciamentoUtilities.toString(traccia,true);
			// String output=StringEscapeUtils.escapeXml(raw);

			// Preparo il menu
			archiviHelper.makeMenu();

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, 
					new Parameter(ArchiviCostanti.LABEL_REPORTISTICA,null),
					new Parameter(ArchiviCostanti.LABEL_TRACCIAMENTO,ArchiviCostanti.SERVLET_NAME_ARCHIVI_TRACCIAMENTO,
							pDs, pProfColl, pDataI, pDataF, pTipoM, pNomeM, pTipoD, pNomeD, pTipoS, pNomeS, pAzione, pCorrAppl, pProt, pIdentMsg),
					new Parameter(ArchiviCostanti.LABEL_TRACCE,ArchiviCostanti.SERVLET_NAME_ARCHIVI_TRACCIAMENTO, pEditMode, 
							pDs, pProfColl, pDataI, pDataF, pTipoM, pNomeM, pTipoD, pNomeD, pTipoS, pNomeS, pAzione, pCorrAppl, pProt, pIdentMsg),
					new Parameter(ArchiviCostanti.LABEL_TRACCIA+" "+idMessaggio,null));

			String TIPO_NOME_PATTERN_BR = "<b>"+ArchiviCostanti.LABEL_TIPO_MESSAGGIO+":</b> {0} <br><b>"+ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_NOME_DATASOURCE+":</b> {1}";
			String CORRELAZIONE_PATTERN_BR = "<b>"+ArchiviCostanti.LABEL_RICHIESTA+":</b> {0} <br><b>"+ArchiviCostanti.LABEL_RISPOSTA+":</b> {1}";
			//String TIPO_NOME_PATTERN = "<b>Tipo:</b> {0} <b>Nome:</b> {1}";
			//String TIPO_NOME_IND_TELEMATICO_PATTERN = "<b>Tipo:</b> {0} <b>Nome:</b> {1} <b>Indirizzo Telematico:</b> {2}";
			
			String SOGGETTO_PATTERN = "<b>"+ArchiviCostanti.LABEL_TIPO_SOGGETTO+
					":</b> {0} <br><b>"+ArchiviCostanti.LABEL_NOME_SOGGETTO+
					":</b> {1} <br><b>"+ArchiviCostanti.LABEL_ID_PORTA_SOGGETTO+
					":</b> {2} <br><b>"+ArchiviCostanti.LABEL_INDIRIZZO_SOGGETTO+":</b> {3}";
			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			DataElement de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_DATI_PER_PROTOCOLLO+traccia.getProtocollo());
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			IProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(traccia.getProtocollo());
			ITraduttore traduttore = protocolFactory.createTraduttore();
			
			// ************ mittente ****************
			String tipoMittente = busta.getTipoMittente()!=null ? busta.getTipoMittente() : "-";
			String nomeMittente = busta.getMittente()!=null ? busta.getMittente() : "-";
			String idPortaMittente = busta.getIdentificativoPortaMittente()!=null ? busta.getIdentificativoPortaMittente() : "-";
			String indirizzoMittente = busta.getIndirizzoMittente()!=null ? busta.getIndirizzoMittente() : "-";
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_MITTENTE);
			de.setValue(MessageFormat.format(SOGGETTO_PATTERN, tipoMittente,nomeMittente,idPortaMittente,indirizzoMittente));
			dati.add(de);
			
			
			// ************ destinatario ************ 
			String tipoDestinatario = busta.getTipoDestinatario()!=null ? busta.getTipoDestinatario() : "-";
			String nomeDestinatario = busta.getDestinatario()!=null ? busta.getDestinatario() : "-";
			String idPortaDestinatario = busta.getIdentificativoPortaDestinatario()!=null ? busta.getIdentificativoPortaDestinatario() : "-";
			String indirizzoDestinatario = busta.getIndirizzoDestinatario()!=null ? busta.getIndirizzoDestinatario() : "-";
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_DESTINATARIO);
			de.setValue(MessageFormat.format(SOGGETTO_PATTERN, tipoDestinatario,nomeDestinatario,idPortaDestinatario,indirizzoDestinatario));
			dati.add(de);
			
						
			// ******** profilo collaborazione ************
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_PROFILO_COLLABORAZIONE);
			if(busta.getProfiloDiCollaborazioneValue()!=null)
				de.setValue(busta.getProfiloDiCollaborazioneValue());
			else if(busta.getProfiloDiCollaborazione()!=null)
				de.setValue(traduttore.toString(busta.getProfiloDiCollaborazione()));
			else
				de.setValue("-");
			dati.add(de);
			// servizio correlato
			if(busta.getTipoServizioCorrelato()!=null || busta.getServizioCorrelato()!=null){
				de = new DataElement();
				de.setLabel(ArchiviCostanti.LABEL_SERVIZIO_CORRELATO);
				de.setValue(MessageFormat.format(TIPO_NOME_PATTERN_BR, 
						this.getValue(busta.getTipoServizioCorrelato()), 
						this.getValue(busta.getServizioCorrelato())));
				dati.add(de);
			}
			
			// ******* collaborazione **************
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_COLLABORAZIONE);
			de.setValue(this.getValue(busta.getCollaborazione()));
			dati.add(de);
			
			// ******** servizio ************
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_SERVIZIO);
			de.setValue(MessageFormat.format(TIPO_NOME_PATTERN_BR, this.getValue(busta.getTipoServizio()), this.getValue(busta.getServizio())));
			dati.add(de);

			// ********* azione *************
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_AZIONE);
			de.setValue(this.getValue(busta.getAzione()));
			dati.add(de);

			// *********** identificativi ***************
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_ID_MESSAGGIO);
			de.setValue(this.getValue(busta.getID()));
			dati.add(de);

			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_ID_MESSAGGIO_RISPOSTA);
			de.setValue(this.getValue(busta.getRiferimentoMessaggio()));
			dati.add(de);
			
			// ********** date *******************
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_SORGENTE_TEMPORALE);
			if(busta.getTipoOraRegistrazioneValue()!=null)
				de.setValue(busta.getTipoOraRegistrazioneValue());
			else if(busta.getTipoOraRegistrazione()!=null)
				de.setValue(traduttore.toString(busta.getTipoOraRegistrazione()));
			else
				de.setValue("-");
			dati.add(de);
			
			SimpleDateFormat formatter = new SimpleDateFormat(ArchiviCostanti.FORMATO_DATA); // SimpleDateFormat non e' thread-safe
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_ORA_REGISTRAZIONE);
			if(busta.getOraRegistrazione()!=null)
				de.setValue(formatter.format(busta.getOraRegistrazione()));
			else
				de.setValue("-");
			dati.add(de);
			// scadenza
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_SCADENZA);
			if (busta.getScadenza() != null) {
				de.setValue(formatter.format(busta.getScadenza()));
			}else{
				de.setValue("-");
			}
			dati.add(de);
			
			
			// ************ profilo trasmissione ******************
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_PROFILO_TRASMISSIONE);
			String inoltro = null;
			if(busta.getInoltroValue()!=null){
				inoltro = busta.getInoltroValue();
			}else if(busta.getInoltro()!=null){
				inoltro = traduttore.toString(busta.getInoltro());
			}else{
				inoltro = "-";
			}
			de.setValue("<b>"+ArchiviCostanti.LABEL_PROFILO_TRASMISSIONE_INOLTRO+":</b> " + inoltro + 
					" <br><b>"+ArchiviCostanti.LABEL_PROFILO_TRASMISSIONE_CONFERMA_RICEZIONE+":</b> " + busta.isConfermaRicezione());
			dati.add(de);

			// *********** sequenza ****************
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_SEQUENZA);
			if (busta.getSequenza() > 0) {
				de.setValue(busta.getSequenza() + "");
			}else{
				de.setValue("-");
			}
			dati.add(de);
			
			// *********** digest ****************
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_DIGEST);
			de.setValue(this.getValue(busta.getDigest()));
			dati.add(de);
			
			
			// ************ lista riscontri ****************
			if (busta.sizeListaRiscontri() > 0) {

				de = new DataElement();
				de.setType(DataElementType.TITLE);
				de.setLabel(ArchiviCostanti.LABEL_LISTA_RISCONTRI);
				dati.add(de);

				for (int i = 0; i < busta.sizeListaRiscontri(); i++) {
					Riscontro riscontro = busta.getRiscontro(i);
					de = new DataElement();
					de.setLabel(this.getValue(riscontro.getID()));
					
					String tipoOraRegistrazione = null;
					if(riscontro.getTipoOraRegistrazioneValue(protocolFactory)!=null){
						tipoOraRegistrazione = riscontro.getTipoOraRegistrazioneValue(protocolFactory);
					}
					else if(riscontro.getTipoOraRegistrazione()!=null){
						tipoOraRegistrazione = traduttore.toString(riscontro.getTipoOraRegistrazione());
					}else{
						tipoOraRegistrazione = "-";
					}
					
					String oraRegistrazione = null;
					if(riscontro.getOraRegistrazione()!=null){
						oraRegistrazione = formatter.format(riscontro.getOraRegistrazione());
					}else{
						oraRegistrazione = "-";
					}
					
					de.setValue("<b>"+ArchiviCostanti.LABEL_SORGENTE_TEMPORALE+"</b>:" + tipoOraRegistrazione 
							+ "<br><b>"+ArchiviCostanti.LABEL_ORA_REGISTRAZIONE+"</b>:" + oraRegistrazione);
					dati.add(de);
				}
			}

			// lista trasmissioni
			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(ArchiviCostanti.LABEL_LISTA_TRASMISSIONI);
			dati.add(de);
			if (busta.sizeListaTrasmissioni() > 0) {

				for (int i = 0; i < busta.sizeListaTrasmissioni(); i++) {
					Trasmissione trasm = busta.getTrasmissione(i);
					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_REGISTRAZIONE+": " + formatter.format(trasm.getOraRegistrazione()));

					String tipoTrOrigine = trasm.getTipoOrigine()!=null ? trasm.getTipoOrigine() : "-";
					String nomeTrOrigine = trasm.getOrigine()!=null ? trasm.getOrigine() : "-";
					String idPortaTrOrigine = trasm.getIdentificativoPortaOrigine()!=null ? trasm.getIdentificativoPortaOrigine() : "-";
					String indirizzoTrOrigine = trasm.getIndirizzoOrigine()!=null ? trasm.getIndirizzoOrigine() : "-";
					String origine = MessageFormat.format(SOGGETTO_PATTERN, tipoTrOrigine,nomeTrOrigine,idPortaTrOrigine,indirizzoTrOrigine);
					
					String tipoTrDestinazione = trasm.getTipoDestinazione()!=null ? trasm.getTipoDestinazione() : "-";
					String nomeTrDestinazione = trasm.getDestinazione()!=null ? trasm.getDestinazione() : "-";
					String idPortaTrDestinazione = trasm.getIdentificativoPortaDestinazione()!=null ? trasm.getIdentificativoPortaDestinazione() : "-";
					String indirizzoTrDestinazione = trasm.getIndirizzoDestinazione()!=null ? trasm.getIndirizzoDestinazione() : "-";
					String destinazione = MessageFormat.format(SOGGETTO_PATTERN, tipoTrDestinazione,nomeTrDestinazione,idPortaTrDestinazione,indirizzoTrDestinazione);
					
					String sorgenteTemporale =  null;
					if(trasm.getTempoValue(protocolFactory)!=null)
						sorgenteTemporale = trasm.getTempoValue(protocolFactory);
					else if(trasm.getTempo()!=null)
						sorgenteTemporale = traduttore.toString(trasm.getTempo());
					else
						sorgenteTemporale = "-";
					String sorgenteTemporaleConLabel =  "<b>"+ArchiviCostanti.LABEL_SORGENTE_TEMPORALE+":</b>"+sorgenteTemporale;
					
					String oraRegistrazione = null;
					if(trasm.getOraRegistrazione()!=null)
						oraRegistrazione = formatter.format(trasm.getOraRegistrazione());
					else
						oraRegistrazione = "-";
					
					String oraRegistrazioneConLabel =  "<b>"+ArchiviCostanti.LABEL_ORA_REGISTRAZIONE+":</b>"+oraRegistrazione;
					
					de.setValue(origine + "<br>" + destinazione + "<br>" + sorgenteTemporaleConLabel + "<br>" + oraRegistrazioneConLabel);
					
					dati.add(de);
				}
			} else {
				de = new DataElement();
				de.setLabel("-");
				de.setValue("-");
				dati.add(de);
			}

			// lista eccezioni
			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(ArchiviCostanti.LABEL_LISTA_ECCEZIONI);
			dati.add(de);
			if (busta.sizeListaEccezioni() > 0) {
				for (int i = 0; i < busta.sizeListaEccezioni(); i++) {
					Eccezione eccezione = busta.getEccezione(i);
					de = new DataElement();
										
					de.setLabel(ArchiviCostanti.LABEL_ECCEZIONE_RILEVANZA+": " + this.getValue(eccezione.getRilevanzaValue(protocolFactory)));
					de.setValue("<b>"+ArchiviCostanti.LABEL_ECCEZIONE_CODICE+":</b>" + this.getValue(eccezione.getCodiceEccezioneValue(protocolFactory) )
							+ " <b>"+ArchiviCostanti.LABEL_ECCEZIONE_CONTESTO+":</b>" + this.getValue(eccezione.getContestoCodificaValue(protocolFactory)) 
							+ "<br><b>"+ArchiviCostanti.LABEL_ECCEZIONE_POSIZIONE+":</b>" + this.getValue(eccezione.getDescrizione(protocolFactory)));
					dati.add(de);
				}
			} else {
				de = new DataElement();
				de.setLabel("-");
				de.setValue("-");
				dati.add(de);
			}

			
			
			
			
			
			// *********** Properties generiche ****************
			
			if(busta.sizeProperties()>0){
				
				de = new DataElement();
				de.setLabel(ArchiviCostanti.LABEL_LISTA_INFO_PROTOCOLLO);
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
				
				java.util.ArrayList<String> listKeys = new ArrayList<String>();
				String [] propertiesNames = busta.getPropertiesNames();
				if(propertiesNames!=null){
					for (int i = 0; i < propertiesNames.length; i++) {
						listKeys.add(propertiesNames[i]);
					}
				}
				java.util.Collections.sort(listKeys);
				for (String key : listKeys) {
					de = new DataElement();
					de.setLabel(key);
					de.setValue(busta.getProperty(key));
					dati.addElement(de);
				}
			}
		
			
			
			
			// *********** Informazioni Aggiuntive ****************
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_LISTA_INFO_AGGIUNTIVE);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
						
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_RUOLO_PDD);
			de.setValue(traccia.getTipoPdD().toString());
			dati.add(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_ID_PORTA_SOGGETTO);
			de.setValue(traccia.getIdSoggetto().getCodicePorta());
			dati.add(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_SOGGETTO_PORTA);
			de.setValue(MessageFormat.format(TIPO_NOME_PATTERN_BR, traccia.getIdSoggetto().getTipo(), 
					traccia.getIdSoggetto().getNome()));
			dati.add(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_TIPO_MESSAGGIO_2);
			de.setValue(traccia.getTipoMessaggio().getTipo());
			dati.add(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_ESITO_ELABORAZIONE);
			if(traccia.getEsitoElaborazioneMessaggioTracciato()!=null){
				de.setValue(traccia.getEsitoElaborazioneMessaggioTracciato().getEsito().toString());
			}else{
				de.setValue("-");
			}
			dati.add(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_DETTAGLIO_ESITO_ELABORAZIONE);
			de.setValue(org.apache.commons.lang.StringEscapeUtils.escapeXml(this.getValue(traccia.getEsitoElaborazioneMessaggioTracciato().getDettaglio())).replaceAll("\n", "<br/>"));
			dati.add(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_SERVIZIO_APPLICATIVO_FRUITORE);
			de.setValue(this.getValue(busta.getServizioApplicativoFruitore()));
			dati.add(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_SERVIZIO_APPLICATIVO_EROGATORE);
			de.setValue(this.getValue(busta.getServizioApplicativoErogatore()));
			dati.add(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_CORRELAZIONE_APPLICATIVA);
			de.setValue(MessageFormat.format(CORRELAZIONE_PATTERN_BR, this.getValue(traccia.getCorrelazioneApplicativa()),
					this.getValue(traccia.getCorrelazioneApplicativaRisposta())));
			dati.add(de);
					
			if(traccia.getLocation()!=null){
					de = new DataElement();
					if(traccia.getLocation().startsWith(ArchiviCostanti.PARAMETRO_TRACCIA_LOCATION_IN)){
						de.setLabel(ArchiviCostanti.LABEL_TRACCIA_LOCATION_IN);
						de.setValue(traccia.getLocation().substring(3,traccia.getLocation().length()));
					}else if(traccia.getLocation().startsWith(ArchiviCostanti.PARAMETRO_TRACCIA_LOCATION_OUT)){
						de.setLabel(ArchiviCostanti.LABEL_TRACCIA_LOCATION_OUT);
						de.setValue(traccia.getLocation());
						de.setValue(traccia.getLocation().substring(4,traccia.getLocation().length()));
					}else{
						de.setLabel(ArchiviCostanti.LABEL_TRACCIA_LOCATION);
						de.setValue(traccia.getLocation());
					}
					dati.add(de);
			}
			
			
			// lista allegati
			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(ArchiviCostanti.LABEL_LISTA_INFO_ALLEGATI);
			dati.add(de);
			if (traccia.sizeListaAllegati() > 0) {
				for (int i = 0; i < traccia.sizeListaAllegati(); i++) {
					Allegato allegato = traccia.getAllegato(i);
					de = new DataElement();
										
					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_ALLEGATO_CONTENT_ID);
					de.setValue(StringEscapeUtils.escapeHtml(this.getValue(allegato.getContentId())));
					dati.add(de);
					
					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_ALLEGATO_CONTENT_LOCATION);
					de.setValue(StringEscapeUtils.escapeHtml(this.getValue(allegato.getContentLocation())));
					dati.add(de);
					
					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_ALLEGATO_CONTENT_TYPE);
					de.setValue(StringEscapeUtils.escapeHtml(this.getValue(allegato.getContentType())));
					dati.add(de);
					
					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_DIGEST);
					de.setValue(StringEscapeUtils.escapeHtml(this.getValue(allegato.getDigest())));
					dati.add(de);
				}
			} else {
				de = new DataElement();
				de.setLabel("-");
				de.setValue("-");
				dati.add(de);
			}
			
			pd.disableEditMode();
			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForward(mapping, ArchiviCostanti.OBJECT_NAME_ARCHIVI_TRACCIAMENTO_TESTO, 
					ArchiviCostanti.TIPO_OPERAZIONE_TRACCIAMENTO_TESTO);

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ArchiviCostanti.OBJECT_NAME_ARCHIVI_TRACCIAMENTO_TESTO,
					ArchiviCostanti.TIPO_OPERAZIONE_TRACCIAMENTO_TESTO);
		} 
	}

	
	private String getValue(String v){
		if(v==null){
			return "-";
		}
		else{
			return v;
		}
	}
}
