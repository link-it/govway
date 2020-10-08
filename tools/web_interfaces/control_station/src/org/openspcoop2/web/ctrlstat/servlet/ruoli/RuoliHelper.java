/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ExporterUtils;
import org.openspcoop2.web.ctrlstat.servlet.utils.UtilsCostanti;
import org.openspcoop2.web.lib.mvc.AreaBottoni;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.Dialog;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.mvc.Dialog.BodyElement;

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

	public Vector<DataElement> addRuoloToDati(TipoOperazione tipoOP, Long ruoloId, String nome, String descrizione, String tipologia,
			String nomeEsterno, String contesto, Vector<DataElement> dati) {
		
		DataElement de = new DataElement();
		de.setLabel(RuoliCostanti.LABEL_RUOLO);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);
		
		de = new DataElement();
		if(ruoloId!=null){
			de = new DataElement();
			de.setLabel(RuoliCostanti.PARAMETRO_RUOLO_ID);
			de.setValue(ruoloId.longValue()+"");
			de.setType(DataElementType.HIDDEN);
			de.setName(RuoliCostanti.PARAMETRO_RUOLO_ID);
			de.setSize( getSize());
			dati.addElement(de);
		}
		
		de = new DataElement();
		de.setLabel(RuoliCostanti.LABEL_PARAMETRO_RUOLO_NOME);
		de.setValue(nome);
		//if(TipoOperazione.ADD.equals(tipoOP)){
		de.setType(DataElementType.TEXT_EDIT);
		//}
		//else{
		//	de.setType(DataElementType.TEXT);
		//}
		de.setName(RuoliCostanti.PARAMETRO_RUOLO_NOME);
		de.setSize( getSize());
		de.setRequired(true);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(RuoliCostanti.LABEL_PARAMETRO_RUOLO_DESCRIZIONE);
		de.setValue(descrizione);
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(RuoliCostanti.PARAMETRO_RUOLO_DESCRIZIONE);
		de.setSize( getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(RuoliCostanti.LABEL_PARAMETRO_RUOLO_TIPOLOGIA);
		de.setType(DataElementType.SELECT);
		de.setName(RuoliCostanti.PARAMETRO_RUOLO_TIPOLOGIA);
		de.setLabels(RuoliCostanti.RUOLI_TIPOLOGIA_LABEL);
		de.setValues(RuoliCostanti.RUOLI_TIPOLOGIA);
		de.setSelected(tipologia);
		de.setPostBack(true);
		dati.addElement(de);
		
		RuoloTipologia ruoloTipologia = null;
		if(tipologia!=null) {
			try {
				ruoloTipologia = RuoloTipologia.toEnumConstant(tipologia,false);
			}catch(Exception e) {}
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
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(RuoliCostanti.LABEL_PARAMETRO_RUOLO_CONTESTO);
		de.setType(DataElementType.SELECT);
		de.setName(RuoliCostanti.PARAMETRO_RUOLO_CONTESTO);
		de.setLabels(RuoliCostanti.RUOLI_CONTESTO_UTILIZZO_LABEL);
		de.setValues(RuoliCostanti.RUOLI_CONTESTO_UTILIZZO);
		de.setSelected(contesto);
		dati.addElement(de);
	
		return dati;
	}
	
	
	// Controlla i dati del registro
	public boolean ruoloCheckData(TipoOperazione tipoOp, Ruolo ruolo) throws Exception {

		try{

			String nome = this.getParameter(RuoliCostanti.PARAMETRO_RUOLO_NOME);
			String descrizione = this.getParameter(RuoliCostanti.PARAMETRO_RUOLO_DESCRIZIONE);
			String nomeEsterno = this.getParameter(RuoliCostanti.PARAMETRO_RUOLO_NOME_ESTERNO);
			//String tipologia = this.ruoliHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_RUOLO_TIPOLOGIA);
			//String contesto = this.ruoliHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_RUOLO_CONTESTO);
			
			// Campi obbligatori
			if (nome.equals("")) {
				String tmpElenco = "";
				if (nome.equals("")) {
					tmpElenco = RuoliCostanti.LABEL_PARAMETRO_RUOLO_NOME;
				}
				this.pd.setMessage("Dati incompleti. E' necessario indicare: " + tmpElenco);
				return false;
			}

			// Controllo che non ci siano spazi nei campi di testo
			if ((nome.indexOf(" ") != -1) ) {
				this.pd.setMessage("Non inserire spazi nel campo '"+RuoliCostanti.LABEL_PARAMETRO_RUOLO_NOME+"'");
				return false;
			}
			if(this.checkNCName(nome, RuoliCostanti.LABEL_PARAMETRO_RUOLO_NOME)==false){
				return false;
			}
			if(this.checkLength255(nome, RuoliCostanti.LABEL_PARAMETRO_RUOLO_NOME)==false) {
				return false;
			}
			
			if(descrizione!=null && !"".equals(descrizione)) {
				if(this.checkLength255(descrizione, RuoliCostanti.LABEL_PARAMETRO_RUOLO_DESCRIZIONE)==false) {
					return false;
				}
			}
			if(nomeEsterno!=null && !"".equals(nomeEsterno)) {
				if(this.checkLength255(nomeEsterno, RuoliCostanti.LABEL_PARAMETRO_RUOLO_NOME_ESTERNO)==false) {
					return false;
				}
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
				
				if(ruolo.getNome().equals(nome)==false){
					// e' stato modificato ilnome
					
					// e' stato implementato l'update
//					java.util.HashMap<org.openspcoop2.core.commons.ErrorsHandlerCostant, List<String>> whereIsInUso = new java.util.HashMap<org.openspcoop2.core.commons.ErrorsHandlerCostant, List<String>>();
//					boolean ruoloInUso = this.confCore.isRuoloInUso(ruolo.getNome(),whereIsInUso);
//					if (ruoloInUso) {
//						String msg = "";
//						msg += org.openspcoop2.core.commons.DBOggettiInUsoUtils.toString(new org.openspcoop2.core.id.IDRuolo(ruolo.getNome()), whereIsInUso, true, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE,
//								" non modificabile in '"+nome+"' perch&egrave; risulta utilizzato:");
//						msg += org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE;
//						this.pd.setMessage(msg);
//						return false;
//					} 
//					
					if(this.ruoliCore.existsRuolo(nome)){
						this.pd.setMessage("Un ruolo con nome '" + nome + "' risulta gi&agrave; stato registrato");
						return false;
					}
					
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
			ServletUtils.addListElementIntoSession(this.session, RuoliCostanti.OBJECT_NAME_RUOLI);
			
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
			if(filterApiContesto!=null && !"".equals(filterApiContesto) &&
					!CostantiControlStation.DEFAULT_VALUE_PARAMETRO_API_CONTESTO_QUALSIASI.equals(filterApiContesto)) {
				
				filterProtocollo = addFilterProtocol(ricerca, idLista, true);

				String protocollo = filterProtocollo;
				if(protocollo==null) {
					// significa che e' stato selezionato un protocollo nel menu in alto a destra
					List<String> protocolli = this.core.getProtocolli(this.session);
					if(protocolli!=null && protocolli.size()==1) {
						protocollo = protocolli.get(0);
					}
				}
				
				if( (filterProtocollo!=null && !"".equals(filterProtocollo) &&
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
			if(filterApiContesto!=null && !"".equals(filterApiContesto) &&
					!CostantiControlStation.DEFAULT_VALUE_PARAMETRO_API_CONTESTO_QUALSIASI.equals(filterApiContesto) &&
					!Filtri.FILTRO_API_CONTESTO_VALUE_APPLICATIVI.equals(filterApiContesto) &&
					!Filtri.FILTRO_API_CONTESTO_VALUE_SOGGETTI.equals(filterApiContesto)) {
				
				filterGruppo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_GRUPPO);
				addFilterGruppo(filterGruppo, true);
				
			}
			else {
				SearchUtils.clearFilter(ricerca, idLista, Filtri.FILTRO_GRUPPO);
			}
			
			if(profiloSelezionato &&
					filterApiContesto!=null && !"".equals(filterApiContesto) &&
					!CostantiControlStation.DEFAULT_VALUE_PARAMETRO_API_CONTESTO_QUALSIASI.equals(filterApiContesto)  &&
					!Filtri.FILTRO_API_CONTESTO_VALUE_APPLICATIVI.equals(filterApiContesto)) {
				String filterApiImplementazione = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_API_IMPLEMENTAZIONE);
				this.addFilterApiImplementazione(filterProtocollo, filterSoggetto, filterGruppo, filterApiContesto, filterApiImplementazione, false);
			}
			else {
				SearchUtils.clearFilter(ricerca, idLista, Filtri.FILTRO_API_IMPLEMENTAZIONE);
			}
			
			if(profiloSelezionato &&
					filterApiContesto!=null && !"".equals(filterApiContesto) &&
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
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<Ruolo> it = lista.iterator();
				while (it.hasNext()) {
					Vector<DataElement> e = modalitaCompleta ? this.creaEntry(it) : this.creaEntryCustom(it);
					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);
			
			// preparo bottoni
			if(lista!=null && lista.size()>0){
				if (this.core.isShowPulsantiImportExport()) {

					ExporterUtils exporterUtils = new ExporterUtils(this.archiviCore);
					if(exporterUtils.existsAtLeastOneExportMpde(org.openspcoop2.protocol.sdk.constants.ArchiveType.RUOLO, this.session)){

						Vector<AreaBottoni> bottoni = new Vector<AreaBottoni>();

						AreaBottoni ab = new AreaBottoni();
						Vector<DataElement> otherbott = new Vector<DataElement>();
						DataElement de = new DataElement();
						de.setValue(RuoliCostanti.LABEL_RUOLI_ESPORTA_SELEZIONATI);
						de.setOnClick(RuoliCostanti.LABEL_RUOLI_ESPORTA_SELEZIONATI_ONCLICK);
						de.setDisabilitaAjaxStatus();
						otherbott.addElement(de);
						ab.setBottoni(otherbott);
						bottoni.addElement(ab);

						this.pd.setAreaBottoni(bottoni);

					}

				}
			}
			
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	private Vector<DataElement> creaEntry(Iterator<Ruolo> it) {
		Ruolo ruolo = it.next();
		Vector<DataElement> e = new Vector<DataElement>();

		DataElement de = new DataElement();
		Parameter pId = new Parameter(RuoliCostanti.PARAMETRO_RUOLO_ID, ruolo.getId()+"");
		de.setUrl(
				RuoliCostanti.SERVLET_NAME_RUOLI_CHANGE , pId);
		de.setToolTip(ruolo.getDescrizione());
		de.setValue(ruolo.getNome());
		de.setIdToRemove(ruolo.getNome());
		de.setToolTip(ruolo.getDescrizione());
		de.setSize(this.core.getElenchiMenuIdentificativiLunghezzaMassima());
		e.addElement(de);

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
		
		e.addElement(de);
		
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
		e.addElement(de);
		return e;
	}
	
	private Vector<DataElement> creaEntryCustom(Iterator<Ruolo> it) {
		Ruolo ruolo = it.next();
		Vector<DataElement> e = new Vector<DataElement>();

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
		e.addElement(de);
		
		// Metadati (tipologia e contesto)

		String tipologiaRuoloLabel = "";
		de = new DataElement();
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
		
		de.setValue(MessageFormat.format(RuoliCostanti.MESSAGE_METADATI_RUOLO_TIPO_E_CONTESTO, tipologiaRuoloLabel, contestoRuoloLabel));
		de.setType(DataElementType.SUBTITLE);
		e.addElement(de);
		
		// TODO 
//			de = new DataElement();
//			de.setType(DataElementType.IMAGE);
//			DataElementInfo dInfoUtilizzo = new DataElementInfo(SoggettiCostanti.LABEL_SOGGETTO);
//			dInfoUtilizzo.setBody("Il soggetto " + this.getLabelNomeSoggetto(protocollo, elem.getTipo(), elem.getNome()) + " gestisce...");
//			de.setInfo(dInfoUtilizzo);
//			de.setToolTip("Visualizza Info");
//			e.addElement(de);
		
		// In Uso
		de = new DataElement();
		de.setType(DataElementType.IMAGE);
		de.setToolTip(CostantiControlStation.LABEL_IN_USO_TOOLTIP);
		Dialog deDialog = new Dialog();
		deDialog.setIcona(Costanti.ICON_USO);
		deDialog.setTitolo(ruolo.getNome());
		deDialog.setHeaderRiga1(CostantiControlStation.LABEL_IN_USO_BODY_HEADER_RISULTATI);
		
		// Inserire sempre la url come primo elemento del body
		BodyElement bodyElementURL = new Dialog().new BodyElement();
		bodyElementURL.setType(DataElementType.HIDDEN);
		bodyElementURL.setName(UtilsCostanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_URL);
		Parameter pIdOggetto = new Parameter(UtilsCostanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_ID_OGGETTO, ruolo.getNome());
		Parameter pTipoOggetto = new Parameter(UtilsCostanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_OGGETTO, org.openspcoop2.protocol.sdk.constants.ArchiveType.RUOLO.toString());
		Parameter pTipoRisposta = new Parameter(UtilsCostanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA, UtilsCostanti.VALUE_PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA_TEXT);
		bodyElementURL.setUrl(UtilsCostanti.SERVLET_NAME_INFORMAZIONI_UTILIZZO_OGGETTO, pIdOggetto,pTipoOggetto,pTipoRisposta);
		deDialog.addBodyElement(bodyElementURL);
		
		BodyElement bodyElement = new Dialog().new BodyElement();
		bodyElement.setType(DataElementType.TEXT_AREA);
		bodyElement.setLabel("");
		bodyElement.setValue("");
		bodyElement.setRows(15);
		deDialog.addBodyElement(bodyElement );
		
		de.setDialog(deDialog );
		e.addElement(de);
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
