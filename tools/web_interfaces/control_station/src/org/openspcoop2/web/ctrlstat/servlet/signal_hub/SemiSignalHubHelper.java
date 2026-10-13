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
package org.openspcoop2.web.ctrlstat.servlet.signal_hub;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.DigestServiceParams;
import org.openspcoop2.pdd.config.DigestServiceParamsDriver;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.remote_stores.RemoteStoresCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * SemiSignalHubHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SemiSignalHubHelper extends ConsoleHelper{

	private static final String FORMATO_DATA = "yyyy-MM-dd HH:mm:ss.SSS";
	private static final String FORMATO_DATA_SINTETICA = "yyyy-MM-dd HH:mm";
	private static final String FORMATO_DATA_SECONDI = "yyyy-MM-dd HH:mm:ss";
	
	public SemiSignalHubHelper(HttpServletRequest request, PageData pd, HttpSession session) {
		super(request, pd,  session);
	}
	public SemiSignalHubHelper(ControlStationCore core, HttpServletRequest request, PageData pd, HttpSession session) {
		super(core, request, pd,  session);
	}
	
	
	
	/* **** Filtri **** */
	
	/**
	 * Ritorna il soggetto erogatore su cui filtrare: quello selezionato in alto a destra, se presente, altrimenti quello scelto nei filtri
	 */
	public IDSoggetto getFiltroSoggettoErogatore(ConsoleSearch ricerca) {
		String soggetto = null;
		if(this.isSoggettoMultitenantSelezionato()) {
			soggetto = this.getSoggettoMultitenantSelezionato();
		}
		else {
			soggetto = SearchUtils.getFilter(ricerca, Liste.SIGNAL_HUB_SEMI, Filtri.FILTRO_SOGGETTO);
		}
		if(soggetto==null || "".equals(soggetto) || !soggetto.contains("/")) {
			return null;
		}
		return new IDSoggetto(soggetto.split("/")[0], soggetto.split("/")[1]);
	}
	
	private void addFiltri(ConsoleSearch ricerca) throws DriverControlStationException {
		// il filtro sul soggetto viene proposto solamente se non e' stato selezionato un soggetto in alto a destra
		if(!this.isSoggettoMultitenantSelezionato()) {
			String filterSoggetto = SearchUtils.getFilter(ricerca, Liste.SIGNAL_HUB_SEMI, Filtri.FILTRO_SOGGETTO);
			this.addFilterSoggetto(filterSoggetto, CostantiLabel.MODIPA_PROTOCOL_NAME, true, false);
		}
	}
	
	
	
	/* **** Elenco **** */
	
	public void prepareSemiSignalHubList(ConsoleSearch ricerca, List<DigestServiceParams> lista) {
		try {
			ServletUtils.addListElementIntoSession(this.request, this.session, SemiSignalHubCostanti.OBJECT_NAME_SIGNAL_HUB_SEMI);

			int idLista = Liste.SIGNAL_HUB_SEMI;
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);
			this.pd.setIndex(ricerca.getIndexIniziale(idLista));
			this.pd.setPageSize(ricerca.getPageSize(idLista));
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));
			
			addFiltri(ricerca);

			if (search.equals("")) {
				this.pd.setSearchDescription("");
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(RemoteStoresCostanti.LABEL_CACHE_PDND, RemoteStoresCostanti.SERVLET_NAME_CACHE_PDND),
						new Parameter(SemiSignalHubCostanti.LABEL_SIGNAL_HUB_SEMI, SemiSignalHubCostanti.SERVLET_NAME_SIGNAL_HUB_SEMI_LIST));
			}
			else {
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(RemoteStoresCostanti.LABEL_CACHE_PDND, RemoteStoresCostanti.SERVLET_NAME_CACHE_PDND),
						new Parameter(SemiSignalHubCostanti.LABEL_SIGNAL_HUB_SEMI, SemiSignalHubCostanti.SERVLET_NAME_SIGNAL_HUB_SEMI_LIST),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
				ServletUtils.enabledPageDataSearch(this.pd, SemiSignalHubCostanti.LABEL_SIGNAL_HUB_SEMI, search);
			}

			String[] labels = {
					SemiSignalHubCostanti.LABEL_EROGAZIONE,
					SemiSignalHubCostanti.LABEL_DATA_GENERAZIONE,
					SemiSignalHubCostanti.LABEL_SCADENZA,
					SemiSignalHubCostanti.LABEL_ALGORITMO,
					SemiSignalHubCostanti.LABEL_STATO
			};
			this.pd.setLabels(labels);

			SimpleDateFormat formatter = DateUtils.getDefaultDateTimeFormatter(FORMATO_DATA);
			Instant now = Instant.now();
			int cacheRefreshSeconds = SemiSignalHubCore.getSeedCacheRefreshSeconds();
			List<List<DataElement>> dati = new ArrayList<>();
			if (lista != null) {
				for (DigestServiceParams seme : lista) {
					dati.add(creaEntry(seme, formatter, now, cacheRefreshSeconds));
				}
			}
			this.pd.setDati(dati);
			// i semi non sono eliminabili: la cancellazione causerebbe la generazione di un nuovo seme senza l'invio del SEEDUPDATE
			this.pd.setAddButton(false);
			this.pd.setRemoveButton(false);
			this.pd.setSelect(false);
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
		}
	}
	
	private List<DataElement> creaEntry(DigestServiceParams seme, SimpleDateFormat formatter, Instant now, int cacheRefreshSeconds) {
		List<DataElement> e = new ArrayList<>();
		
		DataElement de = new DataElement();
		Parameter pIdServizio = new Parameter(SemiSignalHubCostanti.PARAMETRO_SIGNAL_HUB_SEMI_ID_SERVIZIO, seme.getIdServizioRef()+"");
		de.setUrl(SemiSignalHubCostanti.SERVLET_NAME_SIGNAL_HUB_SEMI_CHANGE, pIdServizio);
		de.setValue(getLabelServizio(seme.getIdServizio()));
		de.setToolTip(SemiSignalHubCostanti.LABEL_EROGATORE+": "+getLabelSoggetto(seme.getIdServizio().getSoggettoErogatore()));
		de.setSize(this.core.getElenchiMenuIdentificativiLunghezzaMassima());
		e.add(de);
		
		de = new DataElement();
		de.setValue(format(formatter, seme.getDataRegistrazione()));
		e.add(de);
		
		de = new DataElement();
		de.setValue(format(formatter, getScadenzaNaturale(seme)));
		e.add(de);
		
		de = new DataElement();
		de.setValue(getLabelAlgoritmo(seme));
		e.add(de);
		
		de = new DataElement();
		de.setValue(getLabelStato(seme, true, now));
		String tooltipStato = getTooltipStato(seme, now, cacheRefreshSeconds);
		if(tooltipStato!=null) {
			de.setToolTip(tooltipStato);
		}
		e.add(de);
		
		return e;
	}
	
	
	
	/* **** Dettaglio **** */
	
	public List<DataElement> addSemiSignalHubToDati(long idServizioRef, List<DigestServiceParams> storico, String eServiceId, String[] ultimoSignalId,
			boolean confermaRotazione, List<DataElement> dati) {
		
		this.pd.disableEditMode();
		
		SimpleDateFormat formatter = DateUtils.getDefaultDateTimeFormatter(FORMATO_DATA);
		Instant now = Instant.now();
		
		DigestServiceParams inUso = storico.get(0);
		
		addHidden(dati, SemiSignalHubCostanti.PARAMETRO_SIGNAL_HUB_SEMI_ID_SERVIZIO, idServizioRef+"");
		if(confermaRotazione) {
			// necessari alla conferma dell'operazione
			addHidden(dati, SemiSignalHubCostanti.PARAMETRO_SIGNAL_HUB_SEMI_ID_SEME, inUso.getId()+"");
			addHidden(dati, SemiSignalHubCostanti.PARAMETRO_SIGNAL_HUB_SEMI_ROTAZIONE, Costanti.CHECK_BOX_ENABLED_TRUE);
		}
		
		// Seme in uso
		addTitle(dati, SemiSignalHubCostanti.LABEL_SEZIONE_SEME_IN_USO);
		addText(dati, "inUso_erogazione", SemiSignalHubCostanti.LABEL_EROGAZIONE, getLabelServizioSenzaErogatore(inUso.getIdServizio()));
		addText(dati, "inUso_erogatore", SemiSignalHubCostanti.LABEL_EROGATORE, getLabelSoggetto(inUso.getIdServizio().getSoggettoErogatore()));
		addText(dati, "inUso_stato", SemiSignalHubCostanti.LABEL_STATO, getLabelStato(inUso, true, now));
		String sostituzione = getDescrizioneSostituzione(inUso, now, SemiSignalHubCore.getSeedCacheRefreshSeconds());
		if(sostituzione!=null) {
			addText(dati, "inUso_sostituzione", SemiSignalHubCostanti.LABEL_SOSTITUZIONE, sostituzione);
		}
		addText(dati, "inUso_generazione", SemiSignalHubCostanti.LABEL_DATA_GENERAZIONE, format(formatter, inUso.getDataRegistrazione()));
		addText(dati, "inUso_durata", SemiSignalHubCostanti.LABEL_DURATA, getLabelDurata(inUso));
		addText(dati, "inUso_scadenza", SemiSignalHubCostanti.LABEL_SCADENZA, format(formatter, getScadenzaNaturale(inUso)));
		if(inUso.getDataRichiestaRotazione()!=null) {
			addText(dati, "inUso_richiestaRotazione", SemiSignalHubCostanti.LABEL_DATA_RICHIESTA_ROTAZIONE, format(formatter, inUso.getDataRichiestaRotazione()));
		}
		addText(dati, "inUso_eServiceId", SemiSignalHubCostanti.LABEL_ESERVICE_ID, eServiceId);
		addText(dati, "inUso_algoritmo", SemiSignalHubCostanti.LABEL_ALGORITMO, getLabelAlgoritmo(inUso));
		addText(dati, "inUso_dimensione", SemiSignalHubCostanti.LABEL_DIMENSIONE, getLabelDimensione(inUso));
		addText(dati, "inUso_signalId", SemiSignalHubCostanti.LABEL_SIGNAL_ID, getLabelSignalId(inUso));
		// ultimo signalId generato per l'e-service (presente solo se le basi dati runtime sono consultabili);
		// la sorgente dati in cui e' stato trovato viene indicata solamente se ne sono configurate piu' di una
		if(ultimoSignalId!=null) {
			addText(dati, "inUso_ultimoSignalId", SemiSignalHubCostanti.LABEL_ULTIMO_SIGNAL_ID, ultimoSignalId[0]);
			if(ultimoSignalId.length>1 && ultimoSignalId[1]!=null) {
				addText(dati, "inUso_ultimoSignalIdSorgente", SemiSignalHubCostanti.LABEL_SORGENTE_DATI, ultimoSignalId[1]);
			}
		}
		
		// azione di rotazione forzata, disponibile se non gia' richiesta
		if(inUso.getDataRichiestaRotazione()==null && !confermaRotazione) {
			DataElement de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(SemiSignalHubCostanti.SERVLET_NAME_SIGNAL_HUB_SEMI_CHANGE, 
					new Parameter(SemiSignalHubCostanti.PARAMETRO_SIGNAL_HUB_SEMI_ID_SERVIZIO, idServizioRef+""),
					new Parameter(SemiSignalHubCostanti.PARAMETRO_SIGNAL_HUB_SEMI_ROTAZIONE, Costanti.CHECK_BOX_ENABLED_TRUE));
			de.setValue(SemiSignalHubCostanti.LABEL_FORZA_ROTAZIONE);
			dati.add(de);
		}
		
		// Semi precedenti conservati nello storico
		if(storico.size()>1) {
			addTitle(dati, SemiSignalHubCostanti.LABEL_SEZIONE_SEMI_PRECEDENTI);
			for (int i = 1; i < storico.size(); i++) {
				DigestServiceParams seme = storico.get(i);
				// il seme e' stato sostituito da quello generato successivamente
				DigestServiceParams successivo = storico.get(i-1);
				String prefix = "precedente"+i+"_";
				// ogni seme precedente viene riportato in un gruppo distinto, identificato dal SEEDUPDATE che lo ha introdotto
				addSubtitle(dati, isPrimoSeme(seme) ? 
						SemiSignalHubCostanti.LABEL_SEME_PRECEDENTE_PRIMO : 
						SemiSignalHubCostanti.LABEL_SEME_PRECEDENTE_PREFIX+seme.getSerialNumber());
				addText(dati, prefix+"generazione", SemiSignalHubCostanti.LABEL_DATA_GENERAZIONE, format(formatter, seme.getDataRegistrazione()));
				addText(dati, prefix+"sostituzione", SemiSignalHubCostanti.LABEL_SOSTITUITO_IL, format(formatter, successivo.getDataRegistrazione()));
				addText(dati, prefix+"algoritmo", SemiSignalHubCostanti.LABEL_ALGORITMO, getLabelAlgoritmo(seme));
				addText(dati, prefix+"dimensione", SemiSignalHubCostanti.LABEL_DIMENSIONE, getLabelDimensione(seme));
				addText(dati, prefix+"durata", SemiSignalHubCostanti.LABEL_DURATA, getLabelDurata(seme));
			}
		}
		
		return dati;
	}
	
	public String getMessaggioConfermaRotazione(DigestServiceParams inUso, int cacheRefreshSeconds) {
		// la rotazione forzata diventa effettiva trascorso l'intervallo di aggiornamento della cache dei nodi
		Instant effettiva = Instant.now().plusSeconds(cacheRefreshSeconds);
		Instant scadenzaNaturale = getScadenzaNaturale(inUso);
		if(scadenzaNaturale.isBefore(effettiva)) {
			effettiva = scadenzaNaturale;
		}
		return String.format(SemiSignalHubCostanti.MESSAGGIO_CONFERMA_ROTAZIONE, getLabelServizio(inUso.getIdServizio()), 
				format(DateUtils.getDefaultDateTimeFormatter(FORMATO_DATA_SECONDI), effettiva));
	}
	
	public static String getMessaggioRotazioneRichiesta(DigestServiceParams inUso, int cacheRefreshSeconds) {
		return String.format(SemiSignalHubCostanti.MESSAGGIO_ROTAZIONE_RICHIESTA, 
				format(DateUtils.getDefaultDateTimeFormatter(FORMATO_DATA_SECONDI), getSostituzione(inUso, cacheRefreshSeconds)));
	}
	
	private static void addHidden(List<DataElement> dati, String name, String value) {
		DataElement de = new DataElement();
		de.setName(name);
		de.setValue(value);
		de.setType(DataElementType.HIDDEN);
		dati.add(de);
	}
	private static void addTitle(List<DataElement> dati, String label) {
		DataElement de = new DataElement();
		de.setType(DataElementType.TITLE);
		de.setLabel(label);
		dati.add(de);
	}
	private static void addSubtitle(List<DataElement> dati, String label) {
		DataElement de = new DataElement();
		de.setType(DataElementType.SUBTITLE);
		de.setLabel(label);
		dati.add(de);
	}
	private static void addText(List<DataElement> dati, String name, String label, String value) {
		DataElement de = new DataElement();
		de.setLabel(label);
		de.setName("signalHubSemi_"+name);
		de.setValue(value!=null ? value : SemiSignalHubCostanti.VALUE_NON_PRESENTE);
		de.setType(DataElementType.TEXT);
		dati.add(de);
	}
	
	
	
	/* **** Utilities **** */
	
	private static Instant getScadenzaNaturale(DigestServiceParams seme) {
		return seme.getDataRegistrazione().plus(Duration.ofDays(seme.getDurata()));
	}
	
	/**
	 * Istante dopo il quale il seme verra' sostituito: la scadenza naturale o, se precedente, quella derivante da una rotazione forzata
	 */
	public static Instant getSostituzione(DigestServiceParams seme, int cacheRefreshSeconds) {
		return DigestServiceParamsDriver.getScadenza(seme, cacheRefreshSeconds);
	}
	
	public static String getLabelStato(DigestServiceParams seme, boolean inUso, Instant now) {
		if(!inUso) {
			return SemiSignalHubCostanti.LABEL_STATO_SOSTITUITO;
		}
		if(seme.getDataRichiestaRotazione()!=null) {
			return SemiSignalHubCostanti.LABEL_STATO_ROTAZIONE_RICHIESTA;
		}
		if(!now.isBefore(getScadenzaNaturale(seme))) {
			return SemiSignalHubCostanti.LABEL_STATO_SCADUTO;
		}
		return SemiSignalHubCostanti.LABEL_STATO_VALIDO;
	}
	
	/**
	 * Descrizione estesa dello stato, riportata nel tooltip dell'elenco; null se il seme e' valido
	 */
	private static String getTooltipStato(DigestServiceParams seme, Instant now, int cacheRefreshSeconds) {
		SimpleDateFormat formatter = DateUtils.getDefaultDateTimeFormatter(FORMATO_DATA_SECONDI);
		if(seme.getDataRichiestaRotazione()!=null) {
			return String.format(SemiSignalHubCostanti.TOOLTIP_STATO_ROTAZIONE_RICHIESTA, 
					format(formatter, seme.getDataRichiestaRotazione()), format(formatter, getSostituzione(seme, cacheRefreshSeconds)));
		}
		if(!now.isBefore(getScadenzaNaturale(seme))) {
			return SemiSignalHubCostanti.TOOLTIP_STATO_SCADUTO;
		}
		return null;
	}
	
	/**
	 * Indica quando il seme verra' sostituito, riportato nel dettaglio; null se il seme e' valido
	 */
	private static String getDescrizioneSostituzione(DigestServiceParams seme, Instant now, int cacheRefreshSeconds) {
		if(seme.getDataRichiestaRotazione()!=null) {
			return String.format(SemiSignalHubCostanti.DESCRIZIONE_SOSTITUZIONE_ROTAZIONE_RICHIESTA, 
					format(DateUtils.getDefaultDateTimeFormatter(FORMATO_DATA_SECONDI), getSostituzione(seme, cacheRefreshSeconds)));
		}
		if(!now.isBefore(getScadenzaNaturale(seme))) {
			return SemiSignalHubCostanti.DESCRIZIONE_SOSTITUZIONE_SCADUTO;
		}
		return null;
	}
	
	public static String getDescrizioneSemeInUso(DigestServiceParams seme, int cacheRefreshSeconds) {
		SimpleDateFormat formatter = DateUtils.getDefaultDateTimeFormatter(FORMATO_DATA_SINTETICA);
		String generazione = format(formatter, seme.getDataRegistrazione());
		if(seme.getDataRichiestaRotazione()!=null) {
			return String.format(SemiSignalHubCostanti.DESCRIZIONE_SEME_ROTAZIONE_RICHIESTA, generazione, format(formatter, getSostituzione(seme, cacheRefreshSeconds)));
		}
		Instant scadenza = getScadenzaNaturale(seme);
		if(!Instant.now().isBefore(scadenza)) {
			return String.format(SemiSignalHubCostanti.DESCRIZIONE_SEME_SCADUTO, generazione, format(formatter, scadenza));
		}
		return String.format(SemiSignalHubCostanti.DESCRIZIONE_SEME_VALIDO, generazione, format(formatter, scadenza));
	}
	
	private static boolean isPrimoSeme(DigestServiceParams seme) {
		// il primo seme generato non e' associato ad alcun segnale SEEDUPDATE
		return seme.getSerialNumber()==null || seme.getSerialNumber().longValue()<=0;
	}
	private static String getLabelSignalId(DigestServiceParams seme) {
		if(isPrimoSeme(seme)) {
			return SemiSignalHubCostanti.VALUE_PRIMO_SEME;
		}
		return seme.getSerialNumber().toString();
	}
	private static String getLabelAlgoritmo(DigestServiceParams seme) {
		return seme.getDigestAlgorithm()!=null ? seme.getDigestAlgorithm().getAlgorithmName() : null;
	}
	private static String getLabelDimensione(DigestServiceParams seme) {
		return seme.getSeedSize()!=null ? seme.getSeedSize()+SemiSignalHubCostanti.SUFFIX_BYTE : null;
	}
	private static String getLabelDurata(DigestServiceParams seme) {
		return seme.getDurata()!=null ? seme.getDurata()+SemiSignalHubCostanti.SUFFIX_GIORNI : null;
	}
	
	public String getLabelSoggetto(IDSoggetto idSoggetto) {
		if(idSoggetto==null) {
			return SemiSignalHubCostanti.VALUE_NON_PRESENTE;
		}
		try {
			return this.getLabelNomeSoggetto(idSoggetto);
		}catch(Exception e) {
			return idSoggetto.toString();
		}
	}
	public String getLabelServizioSenzaErogatore(IDServizio idServizio) {
		if(idServizio==null) {
			return SemiSignalHubCostanti.VALUE_NON_PRESENTE;
		}
		try {
			return this.getLabelIdServizioSenzaErogatore(idServizio);
		}catch(Exception e) {
			return idServizio.getNome()+" v"+idServizio.getVersione();
		}
	}
	public String getLabelServizio(IDServizio idServizio) {
		if(idServizio==null) {
			return SemiSignalHubCostanti.VALUE_NON_PRESENTE;
		}
		try {
			return this.getLabelIdServizio(idServizio);
		}catch(Exception e) {
			return idServizio.toString();
		}
	}
	
	private static String format(SimpleDateFormat formatter, Instant d) {
		return d!=null ? formatter.format(Date.from(d)) : null;
	}
}
