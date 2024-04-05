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
package org.openspcoop2.web.ctrlstat.servlet.scope;

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
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.constants.ScopeContesto;
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
 * ScopeHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ScopeHelper extends ConsoleHelper{
	
	private static boolean mostraFiltroScopeTipologia = false;

	public ScopeHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
	}
	public ScopeHelper(ControlStationCore core, HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(core, request, pd,  session);
	}

	public List<DataElement> addScopeToDati(TipoOperazione tipoOP, Long scopeId, String nome, String descrizione, String tipologia,
			String nomeEsterno, String contesto, List<DataElement> dati, String oldNomeScope) throws DriverConfigurazioneException {
		
		Scope scope = null;
		if(TipoOperazione.CHANGE.equals(tipoOP) && oldNomeScope!=null && StringUtils.isNotEmpty(oldNomeScope)){
			scope = this.scopeCore.getScope(oldNomeScope);
		}
		
		if(TipoOperazione.CHANGE.equals(tipoOP)){
			
			List<Parameter> listaParametriChange = new ArrayList<>();
			Parameter pId = new Parameter(ScopeCostanti.PARAMETRO_SCOPE_ID, scopeId+"");
			listaParametriChange.add(pId);
			
			// In Uso Button
			this.addComandoInUsoButton(nome,
					nome,
					InUsoType.SCOPE);
			
			
			// se e' abilitata l'opzione reset cache per elemento, visualizzo il comando nell'elenco dei comandi disponibili nella lista
			if(this.core.isElenchiVisualizzaComandoResetCacheSingoloElemento()){
				listaParametriChange.add(new Parameter(CostantiControlStation.PARAMETRO_ELIMINA_ELEMENTO_DALLA_CACHE, "true"));
				this.pd.addComandoResetCacheElementoButton(ScopeCostanti.SERVLET_NAME_SCOPE_CHANGE, listaParametriChange);
			}		
					
			// Proprieta Button
			if(scope!=null && this.existsProprietaOggetto(scope.getProprietaOggetto(), scope.getDescrizione())) {
				this.addComandoProprietaOggettoButton(nome,
						nome, InUsoType.SCOPE);
			}
		}
		
		DataElement de = new DataElement();
		de.setLabel(ScopeCostanti.LABEL_SCOPE);
		de.setType(DataElementType.TITLE);
		dati.add(de);
		
		if(scopeId!=null){
			de = new DataElement();
			de.setLabel(ScopeCostanti.PARAMETRO_SCOPE_ID);
			de.setValue(scopeId.longValue()+"");
			de.setType(DataElementType.HIDDEN);
			de.setName(ScopeCostanti.PARAMETRO_SCOPE_ID);
			de.setSize( getSize());
			dati.add(de);
		}
		
		de = new DataElement();
		de.setLabel(ScopeCostanti.LABEL_PARAMETRO_SCOPE_NOME);
		de.setValue(nome);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ScopeCostanti.PARAMETRO_SCOPE_NOME);
		de.setSize( getSize());
		de.setRequired(true);
		dati.add(de);

		de = new DataElement();
		de.setLabel(ScopeCostanti.LABEL_PARAMETRO_SCOPE_DESCRIZIONE);
		de.setValue(descrizione);
		de.setType(DataElementType.TEXT_AREA);
		de.setRows(2);
		de.setName(ScopeCostanti.PARAMETRO_SCOPE_DESCRIZIONE);
		de.setSize( getSize());
		dati.add(de);

		de = new DataElement();
		de.setLabel(ScopeCostanti.LABEL_PARAMETRO_SCOPE_TIPOLOGIA);
		de.setType(DataElementType.SELECT);
		de.setName(ScopeCostanti.PARAMETRO_SCOPE_TIPOLOGIA);
		
		de.setValue(tipologia);
		if(mostraFiltroScopeTipologia)
			de.setType(DataElementType.TEXT_EDIT);
		else 
			de.setType(DataElementType.HIDDEN);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(ScopeCostanti.LABEL_PARAMETRO_SCOPE_NOME_ESTERNO);
		de.setValue(nomeEsterno);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ScopeCostanti.PARAMETRO_SCOPE_NOME_ESTERNO);
		de.setSize( getSize());
		dati.add(de);

		de = new DataElement();
		de.setLabel(ScopeCostanti.LABEL_PARAMETRO_SCOPE_CONTESTO);
		de.setType(DataElementType.SELECT);
		de.setName(ScopeCostanti.PARAMETRO_SCOPE_CONTESTO);
		de.setLabels(ScopeCostanti.getScopeContestoUtilizzoLabel());
		de.setValues(ScopeCostanti.getScopeContestoUtilizzo());
		de.setSelected(contesto);
		dati.add(de);
	
		return dati;
	}
	
	
	// Controlla i dati del registro
	public boolean scopeCheckData(TipoOperazione tipoOp, Scope scope) throws Exception {

		try{

			String nome = this.getParameter(ScopeCostanti.PARAMETRO_SCOPE_NOME);
			String descrizione = this.getParameter(ScopeCostanti.PARAMETRO_SCOPE_DESCRIZIONE);
			String nomeEsterno = this.getParameter(ScopeCostanti.PARAMETRO_SCOPE_NOME_ESTERNO);
			
			// Campi obbligatori
			if (nome.equals("")) {
				String tmpElenco = "";
				if (nome.equals("")) {
					tmpElenco = ScopeCostanti.LABEL_PARAMETRO_SCOPE_NOME;
				}
				this.pd.setMessage("Dati incompleti. &Egrave; necessario indicare: " + tmpElenco);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((nome.indexOf(" ") != -1) ) {
				this.pd.setMessage("Non inserire spazi nel campo '"+ScopeCostanti.LABEL_PARAMETRO_SCOPE_NOME+"'");
				return false;
			}
			if(!this.checkNCName(nome, ScopeCostanti.LABEL_PARAMETRO_SCOPE_NOME)){
				return false;
			}
			if(!this.checkLength255(nome, ScopeCostanti.LABEL_PARAMETRO_SCOPE_NOME)) {
				return false;
			}
			
			if(descrizione!=null && !"".equals(descrizione) &&
				!this.checkLength4000(descrizione, ScopeCostanti.LABEL_PARAMETRO_SCOPE_DESCRIZIONE)) {
				return false;
			}
			if(nomeEsterno!=null && !"".equals(nomeEsterno) &&
				!this.checkLength255(nomeEsterno, ScopeCostanti.LABEL_PARAMETRO_SCOPE_NOME_ESTERNO)) {
				return false;
			}

			// Se tipoOp = add, controllo che il registro non sia gia' stato
			// registrata
			if (tipoOp.equals(TipoOperazione.ADD)) {
				
				if(this.scopeCore.existsScope(nome)){
					this.pd.setMessage("Un scope con nome '" + nome + "' risulta gi&agrave; stato registrato");
					return false;
				}
				
			}
			else{
				
				if(!scope.getNome().equals(nome) &&
					this.scopeCore.existsScope(nome)){ // e' stato modificato ilnome
					this.pd.setMessage("Un scope con nome '" + nome + "' risulta gi&agrave; stato registrato");
					return false;
				}
				
			}
				
			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	
	// Prepara la lista di scope
	
	public static int POSIZIONE_FILTRO_PROTOCOLLO = 2; // parte da 0, e' alla quarta posizione se visualizzato
	static {
		if(mostraFiltroScopeTipologia) {
			POSIZIONE_FILTRO_PROTOCOLLO = 3;
		}
	}
	
	public void prepareScopeList(ISearch ricerca, List<Scope> lista)
			throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.request, this.session, ScopeCostanti.OBJECT_NAME_SCOPE);
			
			boolean modalitaCompleta = this.isModalitaCompleta();
			
			if(!modalitaCompleta) {
				this.pd.setCustomListViewName(ScopeCostanti.SCOPE_NOME_VISTA_CUSTOM_LISTA);
			}

			int idLista = Liste.SCOPE;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			if(mostraFiltroScopeTipologia) {
				String filterScopeTipologia = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SCOPE_TIPOLOGIA);
				this.addFilterScopeTipologia(filterScopeTipologia, false);
			}
			
			String filterScopeContesto = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SCOPE_CONTESTO);
			this.addFilterScopeContesto(filterScopeContesto, false);
			
			String filterApiContesto = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_API_CONTESTO);
			this.addFilterApiContesto(filterApiContesto, true);
						
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
			
			String filterGruppo = null;
			if(filterApiContesto!=null && 
					//!"".equals(filterApiContesto) &&
					!CostantiControlStation.DEFAULT_VALUE_PARAMETRO_API_CONTESTO_QUALSIASI.equals(filterApiContesto)) {
				
				filterGruppo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_GRUPPO);
				addFilterGruppo(filterProtocollo, filterGruppo, true);
				
			}
			else {
				SearchUtils.clearFilter(ricerca, idLista, Filtri.FILTRO_GRUPPO);
			}
			
			if(profiloSelezionato &&
					filterApiContesto!=null && 
					//!"".equals(filterApiContesto) &&
					!CostantiControlStation.DEFAULT_VALUE_PARAMETRO_API_CONTESTO_QUALSIASI.equals(filterApiContesto)) {
				String filterApiImplementazione = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_API_IMPLEMENTAZIONE);
				this.addFilterApiImplementazione(filterProtocollo, filterSoggetto, filterGruppo, filterApiContesto, filterApiImplementazione, false);
			}
			else {
				SearchUtils.clearFilter(ricerca, idLista, Filtri.FILTRO_API_IMPLEMENTAZIONE);
			}
			
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				ServletUtils.setPageDataTitle(this.pd, 
						new Parameter(ScopeCostanti.LABEL_SCOPE, ScopeCostanti.SERVLET_NAME_SCOPE_LIST));
			}
			else{
				ServletUtils.setPageDataTitle(this.pd,
						new Parameter(ScopeCostanti.LABEL_SCOPE, ScopeCostanti.SERVLET_NAME_SCOPE_LIST),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			// controllo eventuali risultati ricerca
			this.pd.setSearchLabel(ScopeCostanti.LABEL_PARAMETRO_SCOPE_NOME);
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, ScopeCostanti.LABEL_SCOPE, search);
			}

			// setto le label delle colonne
			this.setLabelColonne(modalitaCompleta);

			// preparo i dati
			List<List<DataElement>> dati = new ArrayList<>();

			if (lista != null) {
				Iterator<Scope> it = lista.iterator();
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
				if(exporterUtils.existsAtLeastOneExportMode(org.openspcoop2.protocol.sdk.constants.ArchiveType.SCOPE, this.request, this.session)){

					List<AreaBottoni> bottoni = new ArrayList<>();

					AreaBottoni ab = new AreaBottoni();
					List<DataElement> otherbott = new ArrayList<>();
					DataElement de = new DataElement();
					de.setValue(ScopeCostanti.LABEL_SCOPE_ESPORTA_SELEZIONATI);
					de.setOnClick(ScopeCostanti.LABEL_SCOPE_ESPORTA_SELEZIONATI_ONCLICK);
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
	private List<DataElement> creaEntry(Iterator<Scope> it) {
		Scope scope = it.next();

		List<DataElement> e = new ArrayList<>();

		DataElement de = new DataElement();
		Parameter pId = new Parameter(ScopeCostanti.PARAMETRO_SCOPE_ID, scope.getId()+"");
		de.setUrl(
				ScopeCostanti.SERVLET_NAME_SCOPE_CHANGE , pId);
		de.setToolTip(scope.getDescrizione());
		de.setValue(scope.getNome());
		de.setIdToRemove(scope.getNome());
		de.setToolTip(scope.getDescrizione());
		de.setSize(this.core.getElenchiMenuIdentificativiLunghezzaMassima());
		e.add(de);
		
		if(mostraFiltroScopeTipologia){
			de = new DataElement();
			de.setValue(scope.getTipologia());
			e.add(de);
		}
		
		de = new DataElement();
		if(ScopeContesto.PORTA_APPLICATIVA.getValue().equals(scope.getContestoUtilizzo().getValue())){
			de.setValue(ScopeCostanti.SCOPE_CONTESTO_UTILIZZO_LABEL_EROGAZIONE);
		}
		else if(ScopeContesto.PORTA_DELEGATA.getValue().equals(scope.getContestoUtilizzo().getValue())){
			de.setValue(ScopeCostanti.SCOPE_CONTESTO_UTILIZZO_LABEL_FRUIZIONE);
		}
		else{
			de.setValue(ScopeCostanti.SCOPE_CONTESTO_UTILIZZO_LABEL_QUALSIASI);
		}
		e.add(de);
		return e;
	}
	private void setLabelColonne(boolean modalitaCompleta) {
		if(!modalitaCompleta) {
			String[] labels = {
					ScopeCostanti.LABEL_SCOPE
			};
			this.pd.setLabels(labels);
		} else {
			List<String> listLabels= new ArrayList<>();
			listLabels.add(ScopeCostanti.LABEL_PARAMETRO_SCOPE_NOME);
			if(mostraFiltroScopeTipologia){
				listLabels.add(ScopeCostanti.LABEL_PARAMETRO_SCOPE_TIPOLOGIA);
			}
			listLabels.add(ScopeCostanti.LABEL_PARAMETRO_SCOPE_CONTESTO);
			
			String[] labels = listLabels.toArray(new String[listLabels.size()]);
			this.pd.setLabels(labels);
		}
	}
	
	private List<DataElement> creaEntryCustom(Iterator<Scope> it) {
		Scope scope = it.next();

		List<DataElement> e = new ArrayList<>();

		// Titolo (nome)
		DataElement de = new DataElement();
		Parameter pId = new Parameter(ScopeCostanti.PARAMETRO_SCOPE_ID, scope.getId()+"");
		de.setUrl(
				ScopeCostanti.SERVLET_NAME_SCOPE_CHANGE , pId);
		de.setToolTip(scope.getDescrizione());
		de.setValue(scope.getNome());
		de.setIdToRemove(scope.getNome());
		de.setToolTip(scope.getDescrizione());
		de.setType(DataElementType.TITLE);
		e.add(de);
		
		
		de = new DataElement();
		
		String contestoLabel = "";
		if(ScopeContesto.PORTA_APPLICATIVA.getValue().equals(scope.getContestoUtilizzo().getValue())){
			contestoLabel = ScopeCostanti.SCOPE_CONTESTO_UTILIZZO_LABEL_EROGAZIONE;
		}
		else if(ScopeContesto.PORTA_DELEGATA.getValue().equals(scope.getContestoUtilizzo().getValue())){
			contestoLabel = ScopeCostanti.SCOPE_CONTESTO_UTILIZZO_LABEL_FRUIZIONE;
		}
		else{
			contestoLabel = ScopeCostanti.SCOPE_CONTESTO_UTILIZZO_LABEL_QUALSIASI;
		}
		
		String identificativoEsternoLabelPrefix = "";
		if(scope.getNomeEsterno()!=null) {
			identificativoEsternoLabelPrefix = MessageFormat.format(ScopeCostanti.MESSAGE_METADATI_SCOPE_IDENTIFICATIVO_ESTERNO, scope.getNomeEsterno());
		}
				
		if(mostraFiltroScopeTipologia){
			de.setValue(identificativoEsternoLabelPrefix+MessageFormat.format(ScopeCostanti.MESSAGE_METADATI_SCOPE_CON_TIPO, contestoLabel, scope.getTipologia()));
		} else {
			de.setValue(identificativoEsternoLabelPrefix+MessageFormat.format(ScopeCostanti.MESSAGE_METADATI_SCOPE_SOLO_CONTESTO, contestoLabel));
		}
		de.setType(DataElementType.SUBTITLE);
		e.add(de);
		
		List<Parameter> listaParametriChange = new ArrayList<>();
		listaParametriChange.add(pId);
		listaParametriChange.add(new Parameter(CostantiControlStation.PARAMETRO_RESET_CACHE_FROM_LISTA, "true"));

		// In Uso Button
		this.addInUsoButton(e, scope.getNome(), scope.getNome(), InUsoType.SCOPE);
		
		// se e' abilitata l'opzione reset cache per elemento, visualizzo il comando nell'elenco dei comandi disponibili nella lista
		if(this.core.isElenchiVisualizzaComandoResetCacheSingoloElemento()){
			this.addComandoResetCacheButton(e, scope.getNome(), ScopeCostanti.SERVLET_NAME_SCOPE_CHANGE, listaParametriChange);
		}
		
		// Proprieta Button
		/**if(this.existsProprietaOggetto(scope.getProprietaOggetto(), scope.getDescrizione())) {
		 * ** la lista non riporta le proprietà. Ma esistono e poi sarà la servlet a gestirlo
		 */
		this.addProprietaOggettoButton(e, scope.getNome(), scope.getNome(), InUsoType.SCOPE);
		
		return e;
	}
}
