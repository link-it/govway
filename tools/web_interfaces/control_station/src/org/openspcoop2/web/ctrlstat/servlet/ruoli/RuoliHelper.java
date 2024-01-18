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
package org.openspcoop2.web.ctrlstat.servlet.ruoli;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.costanti.InUsoType;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ExporterUtils;
import org.openspcoop2.web.lib.mvc.AreaBottoni;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * RuoliHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RuoliHelper extends ConsoleHelper{

	public RuoliHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
	}
	public RuoliHelper(ControlStationCore core, HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(core, request, pd,  session);
	}

	public List<DataElement> addRuoloToDati(TipoOperazione tipoOP, Long ruoloId, String nome, String descrizione, String tipologia,
			String nomeEsterno, String contesto, List<DataElement> dati, String oldNomeRuolo) throws DriverConfigurazioneException {
		
		Ruolo ruolo = null;
		if(TipoOperazione.CHANGE.equals(tipoOP) && oldNomeRuolo!=null && StringUtils.isNotEmpty(oldNomeRuolo)){
			ruolo = this.ruoliCore.getRuolo(oldNomeRuolo);
		}
		
		if(TipoOperazione.CHANGE.equals(tipoOP)){
		
			List<Parameter> listaParametriChange = new ArrayList<>();
			Parameter pId = new Parameter(RuoliCostanti.PARAMETRO_RUOLO_ID, ruoloId+"");
			listaParametriChange.add(pId);
			
			// In Uso Button
			this.addComandoInUsoButton(nome,
					nome,
					InUsoType.RUOLO);
			
			
			// se e' abilitata l'opzione reset cache per elemento, visualizzo il comando nell'elenco dei comandi disponibili nella lista
			if(this.core.isElenchiVisualizzaComandoResetCacheSingoloElemento()){
				listaParametriChange.add(new Parameter(CostantiControlStation.PARAMETRO_ELIMINA_ELEMENTO_DALLA_CACHE, "true"));
				this.pd.addComandoResetCacheElementoButton(RuoliCostanti.SERVLET_NAME_RUOLI_CHANGE, listaParametriChange);
			}		
					
			// Proprieta Button
			if(ruolo!=null && this.existsProprietaOggetto(ruolo.getProprietaOggetto(), ruolo.getDescrizione())) {
				this.addComandoProprietaOggettoButton(nome,
						nome, InUsoType.RUOLO);
			}
		}
		
		
		DataElement de = new DataElement();
		de.setLabel(RuoliCostanti.LABEL_RUOLO);
		de.setType(DataElementType.TITLE);
		dati.add(de);
		
		if(ruoloId!=null){
			de = new DataElement();
			de.setLabel(RuoliCostanti.PARAMETRO_RUOLO_ID);
			de.setValue(ruoloId.longValue()+"");
			de.setType(DataElementType.HIDDEN);
			de.setName(RuoliCostanti.PARAMETRO_RUOLO_ID);
			de.setSize( getSize());
			dati.add(de);
		}
		
		de = new DataElement();
		de.setLabel(RuoliCostanti.LABEL_PARAMETRO_RUOLO_NOME);
		de.setValue(nome);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(RuoliCostanti.PARAMETRO_RUOLO_NOME);
		de.setSize( getSize());
		de.setRequired(true);
		dati.add(de);

		de = new DataElement();
		de.setLabel(RuoliCostanti.LABEL_PARAMETRO_RUOLO_DESCRIZIONE);
		de.setValue(descrizione);
		de.setType(DataElementType.TEXT_AREA);
		de.setRows(2);
		de.setName(RuoliCostanti.PARAMETRO_RUOLO_DESCRIZIONE);
		de.setSize( getSize());
		dati.add(de);

		de = new DataElement();
		de.setLabel(RuoliCostanti.LABEL_PARAMETRO_RUOLO_TIPOLOGIA);
		de.setType(DataElementType.SELECT);
		de.setName(RuoliCostanti.PARAMETRO_RUOLO_TIPOLOGIA);
		de.setLabels(RuoliCostanti.getRuoliTipologiaLabel());
		de.setValues(RuoliCostanti.getRuoliTipologia());
		de.setSelected(tipologia);
		de.setPostBack(true);
		dati.add(de);
		
		RuoloTipologia ruoloTipologia = null;
		if(tipologia!=null) {
			try {
				ruoloTipologia = RuoloTipologia.toEnumConstant(tipologia,false);
			}catch(Exception e) {
				// ignore
			}
		}
		de = new DataElement();
		de.setLabel(RuoliCostanti.LABEL_PARAMETRO_RUOLO_NOME_ESTERNO);
		de.setValue(nomeEsterno);
		if(ruoloTipologia!=null && (RuoloTipologia.QUALSIASI.equals(ruoloTipologia) || RuoloTipologia.ESTERNO.equals(ruoloTipologia))) {
			de.setType(DataElementType.TEXT_EDIT);
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		de.setName(RuoliCostanti.PARAMETRO_RUOLO_NOME_ESTERNO);
		de.setSize( getSize());
		dati.add(de);

		de = new DataElement();
		de.setLabel(RuoliCostanti.LABEL_PARAMETRO_RUOLO_CONTESTO);
		de.setType(DataElementType.SELECT);
		de.setName(RuoliCostanti.PARAMETRO_RUOLO_CONTESTO);
		de.setLabels(RuoliCostanti.getRuoliContestoUtilizzoLabel());
		de.setValues(RuoliCostanti.getRuoliContestoUtilizzo());
		de.setSelected(contesto);
		dati.add(de);
	
		return dati;
	}
	
	
	// Controlla i dati del registro
	public boolean ruoloCheckData(TipoOperazione tipoOp, Ruolo ruolo) throws Exception {

		try{

			String nome = this.getParameter(RuoliCostanti.PARAMETRO_RUOLO_NOME);
			String descrizione = this.getParameter(RuoliCostanti.PARAMETRO_RUOLO_DESCRIZIONE);
			String nomeEsterno = this.getParameter(RuoliCostanti.PARAMETRO_RUOLO_NOME_ESTERNO);
			
			// Campi obbligatori
			if (nome.equals("")) {
				String tmpElenco = "";
				if (nome.equals("")) {
					tmpElenco = RuoliCostanti.LABEL_PARAMETRO_RUOLO_NOME;
				}
				this.pd.setMessage("Dati incompleti. &Egrave; necessario indicare: " + tmpElenco);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((nome.indexOf(" ") != -1) ) {
				this.pd.setMessage("Non inserire spazi nel campo '"+RuoliCostanti.LABEL_PARAMETRO_RUOLO_NOME+"'");
				return false;
			}
			if(!this.checkNCName(nome, RuoliCostanti.LABEL_PARAMETRO_RUOLO_NOME)){
				return false;
			}
			if(!this.checkLength255(nome, RuoliCostanti.LABEL_PARAMETRO_RUOLO_NOME)) {
				return false;
			}
			
			if(descrizione!=null && !"".equals(descrizione) &&
				!this.checkLength4000(descrizione, RuoliCostanti.LABEL_PARAMETRO_RUOLO_DESCRIZIONE)) {
				return false;
			}
			if(nomeEsterno!=null && !"".equals(nomeEsterno) &&
				!this.checkLength255(nomeEsterno, RuoliCostanti.LABEL_PARAMETRO_RUOLO_NOME_ESTERNO)) {
				return false;
			}

			// Se tipoOp = add, controllo che il registro non sia gia' stato
			// registrata
			if (tipoOp.equals(TipoOperazione.ADD)) {
				
				if(this.ruoliCore.existsRuolo(nome)){
					this.pd.setMessage("Un ruolo con nome '" + nome + "' risulta gi&agrave; stato registrato");
					return false;
				}
				
			}
			else{
				
				if(!ruolo.getNome().equals(nome) &&
					this.ruoliCore.existsRuolo(nome)){ // e' stato modificato ilnome
					this.pd.setMessage("Un ruolo con nome '" + nome + "' risulta gi&agrave; stato registrato");
					return false;
				}
				
			}
				
			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	
	// Prepara la lista di ruoli
	
	public static final int POSIZIONE_FILTRO_PROTOCOLLO = 3; // parte da 0, e' alla quarta posizione se visualizzato
	
	public void prepareRuoliList(ISearch ricerca, List<Ruolo> lista) throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.request, this.session, RuoliCostanti.OBJECT_NAME_RUOLI);
			
			boolean modalitaCompleta = this.isModalitaCompleta();
			
			if(!modalitaCompleta) {
				this.pd.setCustomListViewName(RuoliCostanti.RUOLI_NOME_VISTA_CUSTOM_LISTA);
			}

			int idLista = Liste.RUOLI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

						
			String filterRuoloTipologia = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_RUOLO_TIPOLOGIA);
			this.addFilterRuoloTipologia(filterRuoloTipologia, false);
			
			String filterRuoloContesto = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_RUOLO_CONTESTO);
			this.addFilterRuoloContesto(filterRuoloContesto, false);
						
			String filterApiContesto = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_API_CONTESTO);
			this.addFilterApiContestoRuoli(filterApiContesto, true);
						
			// NOTA: ATTENZIONE!!! se sei agggiunge o elimina un filtro prima del protocollo indicato sotto, correggere la variabile POSIZIONE_FILTRO_PROTOCOLLO in questa classe
			
			String filterProtocollo = null;
			String filterSoggetto = null;
			boolean profiloSelezionato = false;
			if(filterApiContesto!=null && 
					//!"".equals(filterApiContesto) &&
					!CostantiControlStation.DEFAULT_VALUE_PARAMETRO_API_CONTESTO_QUALSIASI.equals(filterApiContesto)) {
				
				filterProtocollo = addFilterProtocol(ricerca, idLista, true);

				String protocollo = filterProtocollo;
				if(protocollo==null) {
					// significa che e' stato selezionato un protocollo nel menu in alto a destra
					List<String> protocolli = this.core.getProtocolli(this.request, this.session);
					if(protocolli!=null && protocolli.size()==1) {
						protocollo = protocolli.get(0);
					}
				}
				
				if( (filterProtocollo!=null && 
						//!"".equals(filterProtocollo) &&
						!CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PROTOCOLLO_QUALSIASI.equals(filterProtocollo))
						||
					(filterProtocollo==null && protocollo!=null)
						) {
					profiloSelezionato = true;
				}
				
				if(Filtri.FILTRO_API_CONTESTO_VALUE_SOGGETTI.equals(filterApiContesto)) {
					filterSoggetto = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SOGGETTO);
					boolean soloSoggettiOperativi = false;
					this.addFilterSoggetto(filterSoggetto,protocollo,soloSoggettiOperativi,true);
				}
				else {
					if( profiloSelezionato && 
							(!this.isSoggettoMultitenantSelezionato())) {
						
						filterSoggetto = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SOGGETTO);
						boolean soloSoggettiOperativi = true;
						this.addFilterSoggetto(filterSoggetto,protocollo,soloSoggettiOperativi,true);
					}
					else {
						filterSoggetto=this.getSoggettoMultitenantSelezionato();
					}
				}
			}
			
			String filterGruppo = null;
			if(filterApiContesto!=null && 
					//!"".equals(filterApiContesto) &&
					!CostantiControlStation.DEFAULT_VALUE_PARAMETRO_API_CONTESTO_QUALSIASI.equals(filterApiContesto) &&
					!Filtri.FILTRO_API_CONTESTO_VALUE_APPLICATIVI.equals(filterApiContesto) &&
					!Filtri.FILTRO_API_CONTESTO_VALUE_SOGGETTI.equals(filterApiContesto)) {
				
				filterGruppo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_GRUPPO);
				addFilterGruppo(filterProtocollo, filterGruppo, true);
				
			}
			else {
				SearchUtils.clearFilter(ricerca, idLista, Filtri.FILTRO_GRUPPO);
			}
			
			if(profiloSelezionato &&
					filterApiContesto!=null && 
					//!"".equals(filterApiContesto) &&
					!CostantiControlStation.DEFAULT_VALUE_PARAMETRO_API_CONTESTO_QUALSIASI.equals(filterApiContesto)  &&
					!Filtri.FILTRO_API_CONTESTO_VALUE_APPLICATIVI.equals(filterApiContesto)) {
				String filterApiImplementazione = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_API_IMPLEMENTAZIONE);
				this.addFilterApiImplementazione(filterProtocollo, filterSoggetto, filterGruppo, filterApiContesto, filterApiImplementazione, false);
			}
			else {
				SearchUtils.clearFilter(ricerca, idLista, Filtri.FILTRO_API_IMPLEMENTAZIONE);
			}
			
			if(profiloSelezionato &&
					filterApiContesto!=null && 
					//!"".equals(filterApiContesto) &&
					!CostantiControlStation.DEFAULT_VALUE_PARAMETRO_API_CONTESTO_QUALSIASI.equals(filterApiContesto)  &&
					Filtri.FILTRO_API_CONTESTO_VALUE_APPLICATIVI.equals(filterApiContesto)) {
				String filterApplicativo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SERVIZIO_APPLICATIVO);
				this.addFilterApplicativo(filterProtocollo, filterSoggetto, filterApplicativo, false);
			}
			else {
				SearchUtils.clearFilter(ricerca, idLista, Filtri.FILTRO_SERVIZIO_APPLICATIVO);
			}
			
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(RuoliCostanti.LABEL_RUOLI, RuoliCostanti.SERVLET_NAME_RUOLI_LIST));
			}
			else{
				ServletUtils.setPageDataTitle(this.pd,
						new Parameter(RuoliCostanti.LABEL_RUOLI, RuoliCostanti.SERVLET_NAME_RUOLI_LIST),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			// controllo eventuali risultati ricerca
			this.pd.setSearchLabel(RuoliCostanti.LABEL_PARAMETRO_RUOLO_NOME);
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, RuoliCostanti.LABEL_RUOLI, search);
			}

			// setto le label delle colonne
			this.setLabelColonne(modalitaCompleta);

			// preparo i dati
			List<List<DataElement>> dati = new ArrayList<>();

			if (lista != null) {
				Iterator<Ruolo> it = lista.iterator();
				while (it.hasNext()) {
					List<DataElement> e = modalitaCompleta ? this.creaEntry(it) : this.creaEntryCustom(it);
					dati.add(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);
			
			// preparo bottoni
			if(lista!=null && !lista.isEmpty() &&
				this.core.isShowPulsantiImportExport()) {

				ExporterUtils exporterUtils = new ExporterUtils(this.archiviCore);
				if(exporterUtils.existsAtLeastOneExportMode(org.openspcoop2.protocol.sdk.constants.ArchiveType.RUOLO, this.request, this.session)){

					List<AreaBottoni> bottoni = new ArrayList<>();

					AreaBottoni ab = new AreaBottoni();
					List<DataElement> otherbott = new ArrayList<>();
					DataElement de = new DataElement();
					de.setValue(RuoliCostanti.LABEL_RUOLI_ESPORTA_SELEZIONATI);
					de.setOnClick(RuoliCostanti.LABEL_RUOLI_ESPORTA_SELEZIONATI_ONCLICK);
					de.setDisabilitaAjaxStatus();
					otherbott.add(de);
					ab.setBottoni(otherbott);
					bottoni.add(ab);

					this.pd.setAreaBottoni(bottoni);

				}

			}
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	private List<DataElement> creaEntry(Iterator<Ruolo> it) {
		Ruolo ruolo = it.next();
		List<DataElement> e = new ArrayList<>();

		DataElement de = new DataElement();
		Parameter pId = new Parameter(RuoliCostanti.PARAMETRO_RUOLO_ID, ruolo.getId()+"");
		de.setUrl(
				RuoliCostanti.SERVLET_NAME_RUOLI_CHANGE , pId);
		de.setToolTip(ruolo.getDescrizione());
		de.setValue(ruolo.getNome());
		de.setIdToRemove(ruolo.getNome());
		de.setToolTip(ruolo.getDescrizione());
		de.setSize(this.core.getElenchiMenuIdentificativiLunghezzaMassima());
		e.add(de);

		de = new DataElement();
		if(RuoloTipologia.INTERNO.getValue().equals(ruolo.getTipologia().getValue())){
			de.setValue(RuoliCostanti.RUOLI_TIPOLOGIA_LABEL_INTERNO);
		}
		else if(RuoloTipologia.ESTERNO.getValue().equals(ruolo.getTipologia().getValue())){
			de.setValue(RuoliCostanti.RUOLI_TIPOLOGIA_LABEL_ESTERNO);
		}
		else{
			de.setValue(RuoliCostanti.RUOLI_TIPOLOGIA_LABEL_QUALSIASI);
		}
		
		e.add(de);
		
		de = new DataElement();
		if(RuoloContesto.PORTA_APPLICATIVA.getValue().equals(ruolo.getContestoUtilizzo().getValue())){
			de.setValue(RuoliCostanti.RUOLI_CONTESTO_UTILIZZO_LABEL_EROGAZIONE);
		}
		else if(RuoloContesto.PORTA_DELEGATA.getValue().equals(ruolo.getContestoUtilizzo().getValue())){
			de.setValue(RuoliCostanti.RUOLI_CONTESTO_UTILIZZO_LABEL_FRUIZIONE);
		}
		else{
			de.setValue(RuoliCostanti.RUOLI_CONTESTO_UTILIZZO_LABEL_QUALSIASI);
		}
		e.add(de);
		return e;
	}
	
	private List<DataElement> creaEntryCustom(Iterator<Ruolo> it) {
		Ruolo ruolo = it.next();
		List<DataElement> e = new ArrayList<>();

		// TITOLO (nome)

		DataElement de = new DataElement();
		Parameter pId = new Parameter(RuoliCostanti.PARAMETRO_RUOLO_ID, ruolo.getId()+"");
		de.setUrl(
				RuoliCostanti.SERVLET_NAME_RUOLI_CHANGE , pId);
		de.setToolTip(ruolo.getDescrizione());
		de.setValue(ruolo.getNome());
		de.setIdToRemove(ruolo.getNome());
		de.setToolTip(ruolo.getDescrizione());
		de.setType(DataElementType.TITLE);
		e.add(de);
		
		// Metadati (tipologia e contesto)

		de = new DataElement();
		
		String tipologiaRuoloLabel = "";
		if(RuoloTipologia.INTERNO.getValue().equals(ruolo.getTipologia().getValue())){
			tipologiaRuoloLabel = RuoliCostanti.RUOLI_TIPOLOGIA_LABEL_INTERNO;
		}
		else if(RuoloTipologia.ESTERNO.getValue().equals(ruolo.getTipologia().getValue())){
			tipologiaRuoloLabel = RuoliCostanti.RUOLI_TIPOLOGIA_LABEL_ESTERNO;
		}
		else{
			tipologiaRuoloLabel = RuoliCostanti.RUOLI_TIPOLOGIA_LABEL_QUALSIASI;
		}
		
		String contestoRuoloLabel = "";
		if(RuoloContesto.PORTA_APPLICATIVA.getValue().equals(ruolo.getContestoUtilizzo().getValue())){
			contestoRuoloLabel = RuoliCostanti.RUOLI_CONTESTO_UTILIZZO_LABEL_EROGAZIONE;
		}
		else if(RuoloContesto.PORTA_DELEGATA.getValue().equals(ruolo.getContestoUtilizzo().getValue())){
			contestoRuoloLabel = RuoliCostanti.RUOLI_CONTESTO_UTILIZZO_LABEL_FRUIZIONE;
		}
		else{
			contestoRuoloLabel = RuoliCostanti.RUOLI_CONTESTO_UTILIZZO_LABEL_QUALSIASI;
		}
		
		String identificativoEsternoLabelPrefix = "";
		if(ruolo.getNomeEsterno()!=null) {
			identificativoEsternoLabelPrefix = MessageFormat.format(RuoliCostanti.MESSAGE_METADATI_RUOLO_IDENTIFICATIVO_ESTERNO, ruolo.getNomeEsterno());
		}
		
		de.setValue(identificativoEsternoLabelPrefix+MessageFormat.format(RuoliCostanti.MESSAGE_METADATI_RUOLO_TIPO_E_CONTESTO, tipologiaRuoloLabel, contestoRuoloLabel));
		de.setType(DataElementType.SUBTITLE);
		e.add(de);
		
		List<Parameter> listaParametriChange = new ArrayList<>();
		listaParametriChange.add(pId);
		listaParametriChange.add(new Parameter(CostantiControlStation.PARAMETRO_RESET_CACHE_FROM_LISTA, "true"));
		
		// In Uso Button
		this.addInUsoButton(e, ruolo.getNome(), ruolo.getNome(), InUsoType.RUOLO);
		
		// se e' abilitata l'opzione reset cache per elemento, visualizzo il comando nell'elenco dei comandi disponibili nella lista
		if(this.core.isElenchiVisualizzaComandoResetCacheSingoloElemento()){
			this.addComandoResetCacheButton(e, ruolo.getNome(), RuoliCostanti.SERVLET_NAME_RUOLI_CHANGE, listaParametriChange);
		}
		
		// Proprieta Button
		/**if(this.existsProprietaOggetto(ruolo.getProprietaOggetto(), ruolo.getDescrizione())) {
		 * ** la lista non riporta le proprietà. Ma esistono e poi sarà la servlet a gestirlo
		 */
		this.addProprietaOggettoButton(e, ruolo.getNome(), ruolo.getNome(), InUsoType.RUOLO);
				
		return e;
	}
	
	private void setLabelColonne(boolean modalitaCompleta) {

		if(!modalitaCompleta) {
			String[] labels = {
					RuoliCostanti.LABEL_RUOLI
			};
			this.pd.setLabels(labels);
		} else {
			String[] labels = {
					RuoliCostanti.LABEL_PARAMETRO_RUOLO_NOME,
					RuoliCostanti.LABEL_PARAMETRO_RUOLO_TIPOLOGIA,
					RuoliCostanti.LABEL_PARAMETRO_RUOLO_CONTESTO,
			};
			this.pd.setLabels(labels);
		}
	}
}
