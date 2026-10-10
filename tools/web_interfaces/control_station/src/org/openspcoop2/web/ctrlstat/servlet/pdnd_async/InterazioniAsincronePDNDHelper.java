/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.pdnd_async;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.core.pdnd.async.FaseInterazioneAsincronaPDND;
import org.openspcoop2.pdd.core.pdnd.async.InterazioneAsincronaPDND;
import org.openspcoop2.pdd.core.pdnd.async.InterazioniAsincronePDNDFiltro;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.remote_stores.RemoteStoresCostanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.PageDataException;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * InterazioniAsincronePDNDHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InterazioniAsincronePDNDHelper extends ConsoleHelper{

	private static final String FORMATO_DATA = "yyyy-MM-dd HH:mm:ss.SSS";
	
	public InterazioniAsincronePDNDHelper(HttpServletRequest request, PageData pd, HttpSession session) {
		super(request, pd,  session);
	}
	public InterazioniAsincronePDNDHelper(ControlStationCore core, HttpServletRequest request, PageData pd, HttpSession session) {
		super(core, request, pd,  session);
	}
	
	
	/* **** Filtri **** */
	
	public static String getSorgente(ConsoleSearch ricerca, List<String> sorgenti) {
		String sorgente = SearchUtils.getFilter(ricerca, Liste.PDND_INTERAZIONI_ASYNC, Filtri.FILTRO_PDND_ASYNC_SORGENTE);
		if(StringUtils.isEmpty(sorgente) || !sorgenti.contains(sorgente)) {
			sorgente = sorgenti.get(0);
		}
		return sorgente;
	}
	
	public static InterazioniAsincronePDNDFiltro buildFiltro(ConsoleSearch ricerca) {
		int idLista = Liste.PDND_INTERAZIONI_ASYNC;
		
		InterazioniAsincronePDNDFiltro filtro = new InterazioniAsincronePDNDFiltro();
		
		String interactionId = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PDND_ASYNC_INTERACTION_ID);
		if(StringUtils.isNotEmpty(interactionId)) {
			filtro.setInteractionId(interactionId.trim());
		}
		
		String ruolo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PDND_ASYNC_RUOLO);
		if(StringUtils.isNotEmpty(ruolo)) {
			filtro.setRuolo(TipoPdD.toTipoPdD(ruolo));
		}
		
		String fase = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PDND_ASYNC_FASE);
		if(StringUtils.isNotEmpty(fase)) {
			filtro.setFase(FaseInterazioneAsincronaPDND.toFase(fase));
		}
		
		String stato = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PDND_ASYNC_STATO);
		if(InterazioniAsincronePDNDCostanti.VALUE_STATO_SCADUTE.equals(stato)) {
			filtro.setScadute(true);
			filtro.setDataRiferimentoScadenza(DateManager.getDate());
		}
		else if(InterazioniAsincronePDNDCostanti.VALUE_STATO_ATTIVE.equals(stato)) {
			filtro.setScadute(false);
			filtro.setDataRiferimentoScadenza(DateManager.getDate());
		}
		
		filtro.setOffset(ricerca.getIndexIniziale(idLista));
		filtro.setLimit(ricerca.getPageSize(idLista));
		return filtro;
	}
	
	private void addFiltri(ConsoleSearch ricerca, String sorgente, List<String> sorgenti, List<String> labelSorgenti) throws PageDataException {
		int idLista = Liste.PDND_INTERAZIONI_ASYNC;
		
		// sorgente dati: select list solo se ne è presente più di una
		if(sorgenti.size()>1) {
			this.pd.addFilter(Filtri.FILTRO_PDND_ASYNC_SORGENTE, InterazioniAsincronePDNDCostanti.LABEL_SORGENTE_DATI, sorgente, 
					sorgenti.toArray(new String[0]), labelSorgenti.toArray(new String[0]), true, this.getSize());
		}
		else {
			this.pd.addHiddenFilter(Filtri.FILTRO_PDND_ASYNC_SORGENTE, sorgente, this.getSize());
		}
		
		String interactionId = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PDND_ASYNC_INTERACTION_ID);
		this.pd.addTextSingleLineFilter(Filtri.FILTRO_PDND_ASYNC_INTERACTION_ID, InterazioniAsincronePDNDCostanti.LABEL_INTERACTION_ID, interactionId, this.getSize());
		
		String ruolo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PDND_ASYNC_RUOLO);
		String[] valuesRuolo = {InterazioniAsincronePDNDCostanti.VALUE_QUALSIASI, TipoPdD.DELEGATA.getTipo(), TipoPdD.APPLICATIVA.getTipo()};
		String[] labelsRuolo = {InterazioniAsincronePDNDCostanti.LABEL_QUALSIASI, InterazioniAsincronePDNDCostanti.LABEL_RUOLO_FRUITORE, InterazioniAsincronePDNDCostanti.LABEL_RUOLO_EROGATORE};
		this.pd.addFilter(Filtri.FILTRO_PDND_ASYNC_RUOLO, InterazioniAsincronePDNDCostanti.LABEL_RUOLO, ruolo, valuesRuolo, labelsRuolo, false, this.getSize());
		
		String fase = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PDND_ASYNC_FASE);
		List<String> valuesFase = new ArrayList<>();
		List<String> labelsFase = new ArrayList<>();
		valuesFase.add(InterazioniAsincronePDNDCostanti.VALUE_QUALSIASI);
		labelsFase.add(InterazioniAsincronePDNDCostanti.LABEL_QUALSIASI);
		for (FaseInterazioneAsincronaPDND f : FaseInterazioneAsincronaPDND.values()) {
			valuesFase.add(f.getValore());
			labelsFase.add(f.getLabelConValore());
		}
		this.pd.addFilter(Filtri.FILTRO_PDND_ASYNC_FASE, InterazioniAsincronePDNDCostanti.LABEL_FASE, fase, 
				valuesFase.toArray(new String[0]), labelsFase.toArray(new String[0]), false, this.getSize());
		
		String stato = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PDND_ASYNC_STATO);
		String[] valuesStato = {InterazioniAsincronePDNDCostanti.VALUE_QUALSIASI, InterazioniAsincronePDNDCostanti.VALUE_STATO_ATTIVE, InterazioniAsincronePDNDCostanti.VALUE_STATO_SCADUTE};
		String[] labelsStato = {InterazioniAsincronePDNDCostanti.LABEL_QUALSIASI, InterazioniAsincronePDNDCostanti.LABEL_STATO_ATTIVE, InterazioniAsincronePDNDCostanti.LABEL_STATO_SCADUTE};
		this.pd.addFilter(Filtri.FILTRO_PDND_ASYNC_STATO, InterazioniAsincronePDNDCostanti.LABEL_STATO, stato, valuesStato, labelsStato, false, this.getSize());
	}
	
	
	
	/* **** Elenco **** */
	
	public void prepareInterazioniAsincronePDNDList(ConsoleSearch ricerca, List<InterazioneAsincronaPDND> lista, 
			String sorgente, List<String> sorgenti, List<String> labelSorgenti) {
		try {
			Parameter pSorgente = new Parameter(InterazioniAsincronePDNDCostanti.PARAMETRO_PDND_INTERAZIONI_ASYNC_SORGENTE, sorgente);
			ServletUtils.addListElementIntoSession(this.request, this.session, InterazioniAsincronePDNDCostanti.OBJECT_NAME_PDND_INTERAZIONI_ASYNC, pSorgente);

			int idLista = Liste.PDND_INTERAZIONI_ASYNC;
			this.pd.setIndex(ricerca.getIndexIniziale(idLista));
			this.pd.setPageSize(ricerca.getPageSize(idLista));
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			this.pd.nascondiTextFilterAutomatico();
			
			addFiltri(ricerca, sorgente, sorgenti, labelSorgenti);

			ServletUtils.setPageDataTitle(this.pd, 
					new Parameter(RemoteStoresCostanti.LABEL_CACHE_PDND, RemoteStoresCostanti.SERVLET_NAME_CACHE_PDND),
					new Parameter(InterazioniAsincronePDNDCostanti.LABEL_PDND_INTERAZIONI_ASYNC, InterazioniAsincronePDNDCostanti.SERVLET_NAME_PDND_INTERAZIONI_ASYNC_LIST));

			String[] labels = {
					InterazioniAsincronePDNDCostanti.LABEL_DATA_START,
					InterazioniAsincronePDNDCostanti.LABEL_INTERACTION_ID,
					InterazioniAsincronePDNDCostanti.LABEL_RUOLO,
					InterazioniAsincronePDNDCostanti.LABEL_API,
					InterazioniAsincronePDNDCostanti.LABEL_FASE,
					InterazioniAsincronePDNDCostanti.LABEL_STATO
			};
			this.pd.setLabels(labels);

			Date now = DateManager.getDate();
			List<List<DataElement>> dati = new ArrayList<>();
			if (lista != null) {
				for (InterazioneAsincronaPDND interazione : lista) {
					dati.add(creaEntry(interazione, pSorgente, now));
				}
			}
			this.pd.setDati(dati);
			this.pd.setAddButton(false);
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
		}
	}
	
	private List<DataElement> creaEntry(InterazioneAsincronaPDND interazione, Parameter pSorgente, Date now) {
		SimpleDateFormat formatter = DateUtils.getDefaultDateTimeFormatter(FORMATO_DATA);
		List<DataElement> e = new ArrayList<>();
		
		DataElement de = new DataElement();
		Parameter pId = new Parameter(InterazioniAsincronePDNDCostanti.PARAMETRO_PDND_INTERAZIONI_ASYNC_ID, interazione.getId()+"");
		de.setUrl(InterazioniAsincronePDNDCostanti.SERVLET_NAME_PDND_INTERAZIONI_ASYNC_CHANGE, pId, pSorgente);
		de.setValue(formatter.format(interazione.getDataStart()));
		de.setIdToRemove(interazione.getId() + "");
		e.add(de);
		
		de = new DataElement();
		de.setValue(interazione.getInteractionId());
		de.setSize(this.core.getElenchiMenuIdentificativiLunghezzaMassima());
		e.add(de);
		
		de = new DataElement();
		de.setValue(getLabelRuolo(interazione.getRuolo()));
		e.add(de);
		
		de = new DataElement();
		de.setValue(getLabelServizio(interazione.getServizio()));
		de.setToolTip(InterazioniAsincronePDNDCostanti.LABEL_FRUITORE+": "+getLabelSoggetto(interazione.getFruitore()));
		de.setSize(this.core.getElenchiMenuIdentificativiLunghezzaMassima());
		e.add(de);
		
		de = new DataElement();
		de.setValue(interazione.getFase()!=null ? interazione.getFase().getValore() : InterazioniAsincronePDNDCostanti.VALUE_NON_PRESENTE);
		e.add(de);
		
		de = new DataElement();
		de.setValue(getLabelStato(interazione, now));
		if(interazione.getDataScadenza()!=null) {
			de.setToolTip(InterazioniAsincronePDNDCostanti.LABEL_DATA_SCADENZA+": "+formatter.format(interazione.getDataScadenza()));
		}
		e.add(de);
		
		return e;
	}
	
	
	
	/* **** Dettaglio **** */
	
	public List<DataElement> addInterazioneAsincronaPDNDToDati(InterazioneAsincronaPDND interazione, String sorgente, List<DataElement> dati) {
		
		this.pd.disableEditMode();
		
		SimpleDateFormat formatter = DateUtils.getDefaultDateTimeFormatter(FORMATO_DATA);
		Date now = DateManager.getDate();
		
		addHidden(dati, InterazioniAsincronePDNDCostanti.PARAMETRO_PDND_INTERAZIONI_ASYNC_ID, interazione.getId()+"");
		addHidden(dati, InterazioniAsincronePDNDCostanti.PARAMETRO_PDND_INTERAZIONI_ASYNC_SORGENTE, sorgente);
		
		// Interazione
		addSubtitle(dati, InterazioniAsincronePDNDCostanti.LABEL_SEZIONE_INTERAZIONE);
		addText(dati, InterazioniAsincronePDNDCostanti.LABEL_INTERACTION_ID, interazione.getInteractionId());
		addText(dati, InterazioniAsincronePDNDCostanti.LABEL_RUOLO, getLabelRuolo(interazione.getRuolo()));
		addText(dati, InterazioniAsincronePDNDCostanti.LABEL_FASE, interazione.getFase()!=null ? interazione.getFase().getLabelConValore() : null);
		addText(dati, InterazioniAsincronePDNDCostanti.LABEL_STATO, getLabelStato(interazione, now));
		addText(dati, InterazioniAsincronePDNDCostanti.LABEL_FRUITORE, getLabelSoggetto(interazione.getFruitore()));
		addText(dati, InterazioniAsincronePDNDCostanti.LABEL_EROGATORE, interazione.getServizio()!=null ? getLabelSoggetto(interazione.getServizio().getSoggettoErogatore()) : null);
		addText(dati, InterazioniAsincronePDNDCostanti.LABEL_API, getLabelServizio(interazione.getServizio()));
		if(interazione.getServizioCallback()!=null) {
			addText(dati, InterazioniAsincronePDNDCostanti.LABEL_API_CALLBACK, getLabelServizio(interazione.getServizioCallback()));
		}
		addText(dati, InterazioniAsincronePDNDCostanti.LABEL_ID_TRANSAZIONE_START, interazione.getIdTransazioneStart());
		
		// Token
		addSubtitle(dati, InterazioniAsincronePDNDCostanti.LABEL_SEZIONE_TOKEN);
		addText(dati, InterazioniAsincronePDNDCostanti.LABEL_PURPOSE_ID, interazione.getPurposeId());
		addText(dati, InterazioniAsincronePDNDCostanti.LABEL_CONSUMER_ID, interazione.getConsumerId());
		addText(dati, InterazioniAsincronePDNDCostanti.LABEL_CLIENT_ID, interazione.getClientId());
		addTextArea(dati, InterazioniAsincronePDNDCostanti.LABEL_URL_CALLBACK, interazione.getUrlCallback());
		String entityNumber = toString(interazione.getEntityNumber());
		if(entityNumber==null && TipoPdD.DELEGATA.equals(interazione.getRuolo()) && interazione.getDataCallback()!=null) {
			entityNumber = InterazioniAsincronePDNDCostanti.VALUE_ENTITY_NUMBER_NON_DISPONIBILE_FRUITORE;
		}
		addText(dati, InterazioniAsincronePDNDCostanti.LABEL_ENTITY_NUMBER, entityNumber);
		
		// Parametri API
		addSubtitle(dati, InterazioniAsincronePDNDCostanti.LABEL_SEZIONE_PARAMETRI_API);
		addText(dati, InterazioniAsincronePDNDCostanti.LABEL_TEMPO_MAX_CALLBACK, toSecondi(interazione.getTempoMaxCallback()));
		addText(dati, InterazioniAsincronePDNDCostanti.LABEL_TEMPO_DISPONIBILITA, toSecondi(interazione.getTempoDisponibilita()));
		addText(dati, InterazioniAsincronePDNDCostanti.LABEL_CONFERMA_RICHIESTA, interazione.isConfermaRichiesta() ? InterazioniAsincronePDNDCostanti.VALUE_CONFERMA_RICHIESTA_ABILITATA : InterazioniAsincronePDNDCostanti.VALUE_CONFERMA_RICHIESTA_DISABILITATA);
		addText(dati, InterazioniAsincronePDNDCostanti.LABEL_LIMITE_ENTITA, toString(interazione.getLimiteEntita()));
		
		// Date
		addSubtitle(dati, InterazioniAsincronePDNDCostanti.LABEL_SEZIONE_DATE);
		addText(dati, InterazioniAsincronePDNDCostanti.LABEL_DATA_START, format(formatter, interazione.getDataStart()));
		addText(dati, InterazioniAsincronePDNDCostanti.LABEL_DATA_CALLBACK, format(formatter, interazione.getDataCallback()));
		addText(dati, InterazioniAsincronePDNDCostanti.LABEL_DATA_GET_RESOURCE, format(formatter, interazione.getDataGetResource()));
		addText(dati, InterazioniAsincronePDNDCostanti.LABEL_NUMERO_GET_RESOURCE, interazione.getNumeroGetResource()+"");
		addText(dati, InterazioniAsincronePDNDCostanti.LABEL_DATA_CONFIRMATION, format(formatter, interazione.getDataConfirmation()));
		addText(dati, InterazioniAsincronePDNDCostanti.LABEL_DATA_SCADENZA, format(formatter, interazione.getDataScadenza()));
		addText(dati, InterazioniAsincronePDNDCostanti.LABEL_DATA_AGGIORNAMENTO, format(formatter, interazione.getDataAggiornamento()));
		
		return dati;
	}
	
	private static void addHidden(List<DataElement> dati, String name, String value) {
		DataElement de = new DataElement();
		de.setName(name);
		de.setValue(value);
		de.setType(DataElementType.HIDDEN);
		dati.add(de);
	}
	private static void addSubtitle(List<DataElement> dati, String label) {
		DataElement de = new DataElement();
		de.setType(DataElementType.SUBTITLE);
		de.setLabel(label);
		dati.add(de);
	}
	private static void addText(List<DataElement> dati, String label, String value) {
		DataElement de = new DataElement();
		de.setLabel(label);
		de.setName(toName(label));
		de.setValue(value!=null ? value : InterazioniAsincronePDNDCostanti.VALUE_NON_PRESENTE);
		de.setType(DataElementType.TEXT);
		dati.add(de);
	}
	private static String toName(String label) {
		return "pdndAsync_"+label.replaceAll("[^A-Za-z0-9]", "");
	}
	private static String toSecondi(Long v) {
		return v!=null ? v+InterazioniAsincronePDNDCostanti.SUFFIX_SECONDI : null;
	}
	private static void addTextArea(List<DataElement> dati, String label, String value) {
		DataElement de = new DataElement();
		de.setLabel(label);
		de.setName(toName(label));
		de.setValue(value!=null ? value : InterazioniAsincronePDNDCostanti.VALUE_NON_PRESENTE);
		de.setType(DataElementType.TEXT_AREA_NO_EDIT);
		dati.add(de);
	}
	
	
	
	/* **** Utilities **** */
	
	private static String getLabelRuolo(TipoPdD ruolo) {
		if(TipoPdD.DELEGATA.equals(ruolo)) {
			return InterazioniAsincronePDNDCostanti.LABEL_RUOLO_FRUITORE;
		}
		else if(TipoPdD.APPLICATIVA.equals(ruolo)) {
			return InterazioniAsincronePDNDCostanti.LABEL_RUOLO_EROGATORE;
		}
		return InterazioniAsincronePDNDCostanti.VALUE_NON_PRESENTE;
	}
	
	public static String getLabelStato(InterazioneAsincronaPDND interazione, Date now) {
		if(interazione.isConfermata()) {
			return InterazioniAsincronePDNDCostanti.LABEL_STATO_CONFERMATA;
		}
		if(interazione.getDataScadenza()!=null && interazione.getDataScadenza().before(now)) {
			return InterazioniAsincronePDNDCostanti.LABEL_STATO_SCADUTA;
		}
		if(interazione.isCallbackRicevuta()) {
			return InterazioniAsincronePDNDCostanti.LABEL_STATO_RISORSA_DISPONIBILE;
		}
		return InterazioniAsincronePDNDCostanti.LABEL_STATO_ATTESA_CALLBACK;
	}
	
	private String getLabelSoggetto(IDSoggetto idSoggetto) {
		if(idSoggetto==null) {
			return InterazioniAsincronePDNDCostanti.VALUE_NON_PRESENTE;
		}
		try {
			return this.getLabelNomeSoggetto(idSoggetto);
		}catch(Exception e) {
			return idSoggetto.toString();
		}
	}
	private String getLabelServizio(IDServizio idServizio) {
		if(idServizio==null) {
			return InterazioniAsincronePDNDCostanti.VALUE_NON_PRESENTE;
		}
		try {
			return this.getLabelIdServizio(idServizio);
		}catch(Exception e) {
			return idServizio.toString();
		}
	}
	
	private static String format(SimpleDateFormat formatter, Date d) {
		return d!=null ? formatter.format(d) : null;
	}
	private static String toString(Number n) {
		return n!=null ? n.toString() : null;
	}
}
