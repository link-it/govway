/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.archivi;

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
			
			String nomeDs = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DATASOURCE);
			ArchiviCore archiviCore = new ArchiviCore();
			DriverTracciamento driver = archiviCore.getDriverTracciamento(nomeDs);
			
			String codicePorta = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CODICE_PORTA);
			String nomeSoggettoPorta = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_SOGGETTO_PORTA);
			String tipoSoggettoPorta = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_SOGGETTO_PORTA);
			IDSoggetto idSoggettoPorta = new IDSoggetto(tipoSoggettoPorta, nomeSoggettoPorta, codicePorta);
			
			String idMessaggio = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO);
			
			String datainizio = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_INIZIO);
			String datafine = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_FINE);
			String profcoll = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROFILO_COLLABORAZIONE);
			String tipo_servizio = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_SERVIZIO);
			String servizio = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_SERVIZIO);
			String tipo_mittente = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_MITTENTE);
			String nome_mittente = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_MITTENTE);
			String tipo_destinatario = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_DESTINATARIO);
			String nome_destinatario = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DESTINATARIO);
			String nome_azione = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_AZIONE);
			String correlazioneApplicativa = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA);
			String protocollo = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO);
			String identificativoMessaggioSearch = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO_SEARCH);

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
					new Parameter(ArchiviCostanti.LABEL_TRACCIAMENTO,ArchiviCostanti.SERVLET_NAME_ARCHIVI_TRACCIAMENTO,
							pDs, pProfColl, pDataI, pDataF, pTipoM, pNomeM, pTipoD, pNomeD, pTipoS, pNomeS, pAzione, pCorrAppl, pProt, pIdentMsg),
					new Parameter(ArchiviCostanti.LABEL_TRACCE,ArchiviCostanti.SERVLET_NAME_ARCHIVI_TRACCIAMENTO, pEditMode, 
							pDs, pProfColl, pDataI, pDataF, pTipoM, pNomeM, pTipoD, pNomeD, pTipoS, pNomeS, pAzione, pCorrAppl, pProt, pIdentMsg),
					new Parameter(ArchiviCostanti.LABEL_TRACCIA+" "+idMessaggio,null));

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			DataElement de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_DATI_PER_PROTOCOLLO+traccia.getProtocollo());
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(traccia.getProtocollo());
			ITraduttore traduttore = protocolFactory.createTraduttore();
			
			// ************ mittente ****************
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_MITTENTE);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
			
			String tipoMittente = busta.getTipoMittente()!=null ? busta.getTipoMittente() : "-";
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_TIPO_SOGGETTO);
			de.setType(DataElementType.TEXT);
			de.setValue(tipoMittente);
			dati.addElement(de);
			
			String nomeMittente = busta.getMittente()!=null ? busta.getMittente() : "-";
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_NOME_SOGGETTO);
			de.setType(DataElementType.TEXT);
			de.setValue(nomeMittente);
			dati.addElement(de);
			
			String idPortaMittente = busta.getIdentificativoPortaMittente()!=null ? busta.getIdentificativoPortaMittente() : "-";
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_ID_PORTA_SOGGETTO);
			de.setType(DataElementType.TEXT);
			de.setValue(idPortaMittente);
			dati.addElement(de);
			
			String indirizzoMittente = busta.getIndirizzoMittente()!=null ? busta.getIndirizzoMittente() : "-";
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_INDIRIZZO_SOGGETTO);
			de.setType(DataElementType.TEXT);
			de.setValue(indirizzoMittente);
			dati.addElement(de);
			
			
			// ************ destinatario ************ 
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_DESTINATARIO);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
			
			String tipoDestinatario = busta.getTipoDestinatario()!=null ? busta.getTipoDestinatario() : "-";
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_TIPO_SOGGETTO);
			de.setType(DataElementType.TEXT);
			de.setValue(tipoDestinatario);
			dati.addElement(de);
			
			String nomeDestinatario = busta.getDestinatario()!=null ? busta.getDestinatario() : "-";
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_NOME_SOGGETTO);
			de.setType(DataElementType.TEXT);
			de.setValue(nomeDestinatario);
			dati.addElement(de);
			
			String idPortaDestinatario = busta.getIdentificativoPortaDestinatario()!=null ? busta.getIdentificativoPortaDestinatario() : "-";
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_ID_PORTA_SOGGETTO);
			de.setType(DataElementType.TEXT);
			de.setValue(idPortaDestinatario);
			dati.addElement(de);
			
			String indirizzoDestinatario = busta.getIndirizzoDestinatario()!=null ? busta.getIndirizzoDestinatario() : "-";
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_INDIRIZZO_SOGGETTO);
			de.setType(DataElementType.TEXT);
			de.setValue(indirizzoDestinatario);
			dati.addElement(de);
			
			
			// ******** servizio e azione ************
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_SERVIZIO);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_TIPO_SOGGETTO);
			de.setType(DataElementType.TEXT);
			de.setValue(this.getValue(busta.getTipoServizio()));
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_NOME_SOGGETTO);
			de.setType(DataElementType.TEXT);
			de.setValue(this.getValue(busta.getServizio()));
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_VERSIONE);
			de.setType(DataElementType.TEXT);
			de.setValue(busta.getVersioneServizio()>0 ? busta.getVersioneServizio()+"" : "-");
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_AZIONE);
			de.setType(DataElementType.TEXT);
			de.setValue(this.getValue(busta.getAzione()));
			dati.add(de);
			
						
			// ******** profilo collaborazione ************
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_PROFILO_COLLABORAZIONE);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_NOME_SOGGETTO);
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
				de.setLabel(ArchiviCostanti.LABEL_TIPO_SERVIZIO_CORRELATO);
				de.setType(DataElementType.TEXT);
				de.setValue(this.getValue(busta.getTipoServizioCorrelato()));
				dati.addElement(de);
				
				de = new DataElement();
				de.setLabel(ArchiviCostanti.LABEL_NOME_SERVIZIO_CORRELATO);
				de.setType(DataElementType.TEXT);
				de.setValue(this.getValue(busta.getServizioCorrelato()));
				dati.addElement(de);
				
			}
			

			// *********** identificativi ***************
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IDENTIFICATIVI);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_ID_MESSAGGIO);
			de.setType(DataElementType.TEXT);
			de.setValue(this.getValue(busta.getID()));
			dati.add(de);

			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_ID_MESSAGGIO_RISPOSTA);
			de.setType(DataElementType.TEXT);
			de.setValue(this.getValue(busta.getRiferimentoMessaggio()));
			dati.add(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_COLLABORAZIONE);
			de.setType(DataElementType.TEXT);
			de.setValue(this.getValue(busta.getCollaborazione()));
			dati.add(de);
			
			
			// ********** date *******************
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_INFORMAZIONI_TEMPORALI);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
			
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
			
						
			// ************ profilo trasmissione / sequenza / digest ******************
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_ALTRE_INFORMAZIONI);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_PROFILO_TRASMISSIONE_INOLTRO);
			String inoltro = null;
			if(busta.getInoltroValue()!=null){
				inoltro = busta.getInoltroValue();
			}else if(busta.getInoltro()!=null){
				inoltro = traduttore.toString(busta.getInoltro());
			}else{
				inoltro = "-";
			}
			de.setType(DataElementType.TEXT);
			de.setValue(inoltro);
			dati.add(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_PROFILO_TRASMISSIONE_CONFERMA_RICEZIONE);
			de.setType(DataElementType.TEXT);
			de.setValue(busta.isConfermaRicezione()+"");
			dati.add(de);

			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_SEQUENZA);
			if (busta.getSequenza() > 0) {
				de.setValue(busta.getSequenza() + "");
			}else{
				de.setValue("-");
			}
			dati.add(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_DIGEST);
			de.setValue(this.getValue(busta.getDigest()));
			dati.add(de);
			
			
			// ************ lista riscontri ****************
			if (busta.sizeListaRiscontri() > 0) {

				for (int i = 0; i < busta.sizeListaRiscontri(); i++) {
					
					Riscontro riscontro = busta.getRiscontro(i);
					
					de = new DataElement();
					de.setType(DataElementType.TITLE);
					de.setLabel(ArchiviCostanti.LABEL_RISCONTRO+" "+formatter.format(riscontro.getOraRegistrazione()));
					dati.add(de);
					
					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_ID_MESSAGGIO);
					de.setType(DataElementType.TEXT);
					de.setValue(riscontro.getID());
					dati.addElement(de);
										
					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_INFORMAZIONI_TEMPORALI);
					de.setType(DataElementType.SUBTITLE);
					dati.addElement(de);

					String tipoOraRegistrazione = null;
					if(riscontro.getTipoOraRegistrazioneValue(protocolFactory)!=null){
						tipoOraRegistrazione = riscontro.getTipoOraRegistrazioneValue(protocolFactory);
					}
					else if(riscontro.getTipoOraRegistrazione()!=null){
						tipoOraRegistrazione = traduttore.toString(riscontro.getTipoOraRegistrazione());
					}else{
						tipoOraRegistrazione = "-";
					}
					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_SORGENTE_TEMPORALE);
					de.setValue(tipoOraRegistrazione);
					dati.add(de);
					
					String oraRegistrazione = null;
					if(riscontro.getOraRegistrazione()!=null){
						oraRegistrazione = formatter.format(riscontro.getOraRegistrazione());
					}else{
						oraRegistrazione = "-";
					}
					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_ORA_REGISTRAZIONE);
					de.setValue(oraRegistrazione);
					dati.add(de);

				}
			}

			// lista trasmissioni
			if (busta.sizeListaTrasmissioni() > 0) {

				for (int i = 0; i < busta.sizeListaTrasmissioni(); i++) {
					Trasmissione trasm = busta.getTrasmissione(i);
					
					de = new DataElement();
					de.setType(DataElementType.TITLE);
					de.setLabel(ArchiviCostanti.LABEL_TRASMISSIONE+" "+formatter.format(trasm.getOraRegistrazione()));
					dati.add(de);
					

					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_MITTENTE);
					de.setType(DataElementType.SUBTITLE);
					dati.addElement(de);
					
					String tipoTrOrigine = trasm.getTipoOrigine()!=null ? trasm.getTipoOrigine() : "-";
					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_TIPO_SOGGETTO);
					de.setType(DataElementType.TEXT);
					de.setValue(tipoTrOrigine);
					dati.addElement(de);
					
					String nomeTrOrigine = trasm.getOrigine()!=null ? trasm.getOrigine() : "-";
					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_NOME_SOGGETTO);
					de.setType(DataElementType.TEXT);
					de.setValue(nomeTrOrigine);
					dati.addElement(de);
					
					String idPortaTrOrigine = trasm.getIdentificativoPortaOrigine()!=null ? trasm.getIdentificativoPortaOrigine() : "-";
					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_ID_PORTA_SOGGETTO);
					de.setType(DataElementType.TEXT);
					de.setValue(idPortaTrOrigine);
					dati.addElement(de);
					
					String indirizzoTrOrigine = trasm.getIndirizzoOrigine()!=null ? trasm.getIndirizzoOrigine() : "-";
					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_INDIRIZZO_SOGGETTO);
					de.setType(DataElementType.TEXT);
					de.setValue(indirizzoTrOrigine);
					dati.addElement(de);
					
									
					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_DESTINATARIO);
					de.setType(DataElementType.SUBTITLE);
					dati.addElement(de);
					
					String tipoTrDestinazione = trasm.getTipoDestinazione()!=null ? trasm.getTipoDestinazione() : "-";
					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_TIPO_SOGGETTO);
					de.setType(DataElementType.TEXT);
					de.setValue(tipoTrDestinazione);
					dati.addElement(de);
					
					String nomeTrDestinazione = trasm.getDestinazione()!=null ? trasm.getDestinazione() : "-";
					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_NOME_SOGGETTO);
					de.setType(DataElementType.TEXT);
					de.setValue(nomeTrDestinazione);
					dati.addElement(de);
					
					String idPortaTrDestinazione = trasm.getIdentificativoPortaDestinazione()!=null ? trasm.getIdentificativoPortaDestinazione() : "-";
					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_ID_PORTA_SOGGETTO);
					de.setType(DataElementType.TEXT);
					de.setValue(idPortaTrDestinazione);
					dati.addElement(de);
					
					String indirizzoTrDestinazione = trasm.getIndirizzoDestinazione()!=null ? trasm.getIndirizzoDestinazione() : "-";
					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_INDIRIZZO_SOGGETTO);
					de.setType(DataElementType.TEXT);
					de.setValue(indirizzoTrDestinazione);
					dati.addElement(de);
					
					
					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_INFORMAZIONI_TEMPORALI);
					de.setType(DataElementType.SUBTITLE);
					dati.addElement(de);

					String sorgenteTemporale =  null;
					if(trasm.getTempoValue(protocolFactory)!=null)
						sorgenteTemporale = trasm.getTempoValue(protocolFactory);
					else if(trasm.getTempo()!=null)
						sorgenteTemporale = traduttore.toString(trasm.getTempo());
					else
						sorgenteTemporale = "-";
					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_SORGENTE_TEMPORALE);
					de.setValue(sorgenteTemporale);
					dati.add(de);
					
					String oraRegistrazione = null;
					if(trasm.getOraRegistrazione()!=null)
						oraRegistrazione = formatter.format(trasm.getOraRegistrazione());
					else
						oraRegistrazione = "-";
					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_ORA_REGISTRAZIONE);
					de.setValue(oraRegistrazione);
					dati.add(de);
					
				}
			} else {
				de = new DataElement();
				de.setLabel("-");
				de.setValue("-");
				dati.add(de);
			}

			// lista eccezioni
			if (busta.sizeListaEccezioni() > 0) {
				for (int i = 0; i < busta.sizeListaEccezioni(); i++) {
					Eccezione eccezione = busta.getEccezione(i);
					
					de = new DataElement();
					de.setType(DataElementType.TITLE);
					de.setLabel(ArchiviCostanti.LABEL_ECCEZIONE+" "+this.getValue(eccezione.getCodiceEccezioneValue(protocolFactory)));
					dati.add(de);

					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_ECCEZIONE_RILEVANZA);
					de.setType(DataElementType.TEXT);
					de.setValue(this.getValue(eccezione.getRilevanzaValue(protocolFactory)));
					dati.addElement(de);
					
					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_ECCEZIONE_CODICE);
					de.setType(DataElementType.TEXT);
					de.setValue(this.getValue(eccezione.getCodiceEccezioneValue(protocolFactory)));
					dati.addElement(de);
					
					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_ECCEZIONE_CONTESTO);
					de.setType(DataElementType.TEXT);
					de.setValue(this.getValue(eccezione.getContestoCodificaValue(protocolFactory)));
					dati.addElement(de);
					
					de = new DataElement();
					de.setLabel(ArchiviCostanti.LABEL_ECCEZIONE_POSIZIONE);
					de.setValue(this.getValue(eccezione.getDescrizione(protocolFactory)));
					de.setType(DataElementType.TEXT_AREA_NO_EDIT);
					de.setRows(6);
					de.setCols(80);
					dati.addElement(de);
					
				}
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
					if(key.length()>20){
						de.setLabel(key.substring(0, 18)+"...");
						de.setNote(key);
					}else{
						de.setLabel(key);
					}
					de.setToolTip(key);
					de.setType(DataElementType.TEXT);
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
			de.setLabel(ArchiviCostanti.LABEL_DOMINIO);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_RUOLO_PDD);
			de.setType(DataElementType.TEXT);
			de.setValue(traccia.getTipoPdD().toString());
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_TIPO_SOGGETTO);
			de.setType(DataElementType.TEXT);
			de.setValue(traccia.getIdSoggetto().getTipo());
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_NOME_SOGGETTO);
			de.setType(DataElementType.TEXT);
			de.setValue(traccia.getIdSoggetto().getNome());
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_ID_PORTA_SOGGETTO);
			de.setType(DataElementType.TEXT);
			de.setValue(traccia.getIdSoggetto().getCodicePorta());
			dati.addElement(de);
			
			if(traccia.getLocation()!=null){
				de = new DataElement();
				de.setLabel(ArchiviCostanti.LABEL_TRACCIA_LOCATION_LABEL);
				if(traccia.getLocation().startsWith(ArchiviCostanti.PARAMETRO_TRACCIA_LOCATION_IN)){
					de.setNote(ArchiviCostanti.LABEL_TRACCIA_LOCATION_IN);
					de.setValue(traccia.getLocation().substring(3,traccia.getLocation().length()));
				}else if(traccia.getLocation().startsWith(ArchiviCostanti.PARAMETRO_TRACCIA_LOCATION_OUT)){
					de.setNote(ArchiviCostanti.LABEL_TRACCIA_LOCATION_OUT);
					de.setValue(traccia.getLocation());
					de.setValue(traccia.getLocation().substring(4,traccia.getLocation().length()));
				}else{
					de.setNote(ArchiviCostanti.LABEL_TRACCIA_LOCATION);
					de.setValue(traccia.getLocation());
				}
				dati.add(de);
			}

			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_DETTAGLI_ELABORAZIONE);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
			
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
			String dettaglio = this.getValue(traccia.getEsitoElaborazioneMessaggioTracciato().getDettaglio());
			de.setValue(org.apache.commons.lang.StringEscapeUtils.escapeXml(dettaglio));
			if(dettaglio!=null && !"-".equals(dettaglio)){
				de.setType(DataElementType.TEXT_AREA_NO_EDIT);
				de.setRows(6);
				de.setCols(80);
			}
			else{
				de.setType(DataElementType.TEXT);
			}
			dati.add(de);
			
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_SERVIZI_APPLICATIVI);
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
			
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
			de.setType(DataElementType.SUBTITLE);
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_CORRELAZIONE_APPLICATIVA_RICHIESTA);
			de.setValue(this.getValue(traccia.getCorrelazioneApplicativa()));
			dati.add(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_CORRELAZIONE_APPLICATIVA_RISPOSTA);
			de.setValue(this.getValue(traccia.getCorrelazioneApplicativaRisposta()));
			dati.add(de);
			
			

			
			// lista allegati

			if (traccia.sizeListaAllegati() > 0) {
				for (int i = 0; i < traccia.sizeListaAllegati(); i++) {
					Allegato allegato = traccia.getAllegato(i);
					
					de = new DataElement();
					de.setType(DataElementType.TITLE);
					if(allegato.getContentId()!=null){
						de.setLabel(ArchiviCostanti.LABEL_ALLEGATO+" "+allegato.getContentId());
					}
					else if(allegato.getContentLocation()!=null){
						de.setLabel(ArchiviCostanti.LABEL_ALLEGATO+" "+allegato.getContentLocation());
					}
					else if(allegato.getContentLocation()!=null){
						de.setLabel(ArchiviCostanti.LABEL_ALLEGATO+" "+(i+1));
					}
					dati.add(de);
					
							
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
