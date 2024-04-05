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
package org.openspcoop2.web.ctrlstat.servlet.ac;

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
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoCooperazionePartecipanti;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.protocol.sdk.validator.ValidazioneResult;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
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
 * AccordiCooperazioneHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordiCooperazioneHelper  extends ConsoleHelper {

	public AccordiCooperazioneHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
	}
	public AccordiCooperazioneHelper(ControlStationCore core, HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(core, request, pd,  session);
	}
	
	// Controlla i dati degli Accordi cooperazione
	public boolean accordiCooperazioneCheckData(TipoOperazione tipoOp, String nome, String descr, String id, String referente, String versione,boolean visibilitaAccordoCooperazione,
			IDAccordoCooperazione idAccordoOLD)
					throws Exception {

		try{

			int idInt = 0;
			if (tipoOp.equals(TipoOperazione.CHANGE)) {
				idInt = Integer.parseInt(id);
			}
			if (referente == null) {
				referente = "";
			}

//			String gestioneWSBL = ServletUtils.getGestioneWSBLFromSession(this.session);
//			if(gestioneWSBL == null){
//				gestioneWSBL = Costanti.CHECK_BOX_DISABLED;
//			}

			// Campi obbligatori
			if (nome==null || nome.equals("")) {
				this.pd.setMessage("Dati incompleti. &Egrave; necessario indicare un Nome");
				return false;
			}
			if(referente==null || referente.equals("") || referente.equals("-")){
				this.pd.setMessage("Dati incompleti. &Egrave; necessario indicare un Soggetto Referente");
				return false;
			}
			//if(this.core.isBackwardCompatibilityAccordo11()==false){
			if (versione==null || versione.equals("")) {
				this.pd.setMessage("Dati incompleti. &Egrave; necessario indicare una Versione dell'accordo");
				return false;
			}
			//}

//			if (gestioneWSBL.equals("yes") && (referente == null || "".equals(referente))) {
//				this.pd.setMessage("Dati incompleti. &Egrave; necessario indicare un Soggetto referente");
//				return false;
//			}

			// Controllo che non ci siano spazi nei campi di testo
			if (nome.indexOf(" ") != -1) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}

			// Il nome deve contenere solo lettere e numeri e '_' '-' '.'
			if(this.checkNCName(nome,AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_NOME)==false){
				return false;
			}
			
			// lunghezza
			if(this.checkLength255(nome, AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_NOME)==false) {
				return false;
			}
			if(descr!=null && !"".equals(descr)) {
				if(this.checkLength255(descr, AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_DESCRIZIONE)==false) {
					return false;
				}
			}

			// La versione deve contenere solo lettere e numeri e '.'
//			if (gestioneWSBL.equals("yes")) {
				/*if (!versione.equals("") && !this.procToCall.isOk("^[1-9]+[\\.][0-9]+[0-9A-Za-z]*$", versione)  && !this.procToCall.isOk("^[0-9]+$",versione)) {
						this.pd.setMessage("La versione dev'essere scritto come MajorVersion[.MinorVersion*] (MajorVersion [1-9]) (MinorVersion [0-9]) (* [0-9A-Za-z]) ");
						return false;
					}*/
				if (!versione.equals("") && !this.checkNumber(versione,AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_VERSIONE,false)) {
					return false;
				}
//			}

			// Controllo che il referente appartenga alla lista di
			// providers disponibili
			//IDSoggetto soggettoReferente = null;
			//if (gestioneWSBL.equals("yes")) {
			Soggetto sRef = null;
			IDSoggetto idSoggettoReferente = null;
			if(referente!=null && !referente.equals("") && !referente.equals("-")){
				boolean trovatoProv = this.soggettiCore.existsSoggetto(Integer.parseInt(referente));
				if (!trovatoProv) {
					this.pd.setMessage("Il Soggetto referente dev'essere scelto tra quelli definiti nel pannello Soggetti");
					return false;
				}else{
					sRef = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(referente));
					// Visibilita rispetto all'accordo
					boolean visibile = false;
					if(visibilitaAccordoCooperazione==visibile){
						if(sRef.getPrivato()!=null && sRef.getPrivato()==true){
							this.pd.setMessage("Non e' possibile utilizzare un soggetto referente con visibilita' privata, in un accordo di cooperazione con visibilita' pubblica.");
							return false;
						}
					}
					idSoggettoReferente = new IDSoggetto(sRef.getTipo(), sRef.getNome());
				}
			}
			//	}

			// Controllo che non esistano altri accordi con stesso nome
			// Se tipoOp = change, devo fare attenzione a non escludere nome
			// del servizio selezionato
			int idAcc = 0;
			Integer versioneInt = null;
			if(versione!=null){
				versioneInt = Integer.parseInt(versione);
			}
			IDAccordoCooperazione idAccordo = this.idAccordoCooperazioneFactory.getIDAccordoFromValues(nome,idSoggettoReferente,versioneInt);
			boolean esisteAC = this.acCore.existsAccordoCooperazione(idAccordo);
			AccordoCooperazione ac = null;
			if (esisteAC) {
				ac = this.acCore.getAccordoCooperazione(idAccordo);
				idAcc = ac.getId().intValue();
			}
			if ((idAcc != 0) && (tipoOp.equals(TipoOperazione.ADD) || (tipoOp.equals(TipoOperazione.CHANGE) && (idInt != idAcc)))) {
				this.pd.setMessage("Esiste gi&agrave; un accordo (versione "+versione+") con nome " + nome);
				return false;
			}

			// Controllo visibilita soggetti partecipanti
			if (tipoOp.equals(TipoOperazione.CHANGE)) {

				ac = this.acCore.getAccordoCooperazione(idAccordoOLD);

				if(ac.getPrivato()==null || ac.getPrivato()==false){
					if(ac.getElencoPartecipanti()!=null){
						AccordoCooperazionePartecipanti partecipanti = ac.getElencoPartecipanti();
						for(int i=0;i<partecipanti.sizeSoggettoPartecipanteList(); i++){
							Soggetto sPartecipante = this.soggettiCore.getSoggettoRegistro(partecipanti.getSoggettoPartecipante(i).getIdSoggetto().intValue());
							if(sPartecipante.getPrivato()!=null && sPartecipante.getPrivato()){
								this.pd.setMessage("Non e' possibile impostare una visibilita' pubblica all'accordo di cooperazione, poiche' possiede un soggetto parcepante ["+sPartecipante.getTipo()+"/"+sPartecipante.getNome()
										+"] con visibilita' privata.");
								return false;
							}
						}
					}
				}
			}
			
			AccordoCooperazione accordoCooperazione = new AccordoCooperazione();
			accordoCooperazione.setDescrizione(descr);
			accordoCooperazione.setNome(nome);
			if(versione!=null){
				accordoCooperazione.setVersione(Integer.parseInt(versione));
			}
			if(sRef!=null){
				IdSoggetto soggettoReferente = new IdSoggetto();
				soggettoReferente.setTipo(sRef.getTipo());
				soggettoReferente.setNome(sRef.getNome());
				accordoCooperazione.setSoggettoReferente(soggettoReferente);
			}
			
			String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(accordoCooperazione.getSoggettoReferente().getTipo());

			ValidazioneResult v = this.acCore.validazione(accordoCooperazione, this.soggettiCore);
			if(v.isEsito()==false){
				this.pd.setMessage("[validazione-"+protocollo+"] "+v.getMessaggioErrore());
				if(v.getException()!=null)
					this.log.error("[validazione-"+protocollo+"] "+v.getMessaggioErrore(),v.getException());
				else
					this.log.error("[validazione-"+protocollo+"] "+v.getMessaggioErrore());
				return false;
			}	

			return true;
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Prepara la lista di accordi cooperazione
	public void prepareAccordiCooperazioneList(List<AccordoCooperazione> lista, ISearch ricerca) throws Exception {
		try {
			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

			ServletUtils.addListElementIntoSession(this.request, this.session, AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE);

			int idLista = Liste.ACCORDI_COOPERAZIONE;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			addFilterProtocol(ricerca, idLista);
			
			if(this.isShowGestioneWorkflowStatoDocumenti()){
				if(this.core.isGestioneWorkflowStatoDocumentiVisualizzaStatoLista()) {
					String filterStatoAccordo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_STATO_ACCORDO);
					this.addFilterStatoAccordo(filterStatoAccordo,false);
				}
			}
			
			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<>();

			lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST));
			if (search.equals("")) {
				this.pd.setSearchDescription("");
			} else {
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// controllo eventuali risultati ricerca
			this.pd.setSearchLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_NOME);
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, search);
			}

			boolean showProtocolli = this.core.countProtocolli(this.request, this.session)>1;
			
//			String gestioneWSBL = ServletUtils.getGestioneWSBLFromSession(this.session);
//			if(gestioneWSBL == null){
//				gestioneWSBL = Costanti.CHECK_BOX_DISABLED;
//			}

			// setto le label delle colonne
			int totEl = 3;
			if(this.isShowGestioneWorkflowStatoDocumenti()) {
				if(this.core.isGestioneWorkflowStatoDocumentiVisualizzaStatoLista()) {
					totEl++;
				}
			}

			// protocolli
			if( showProtocolli ) {
				totEl++;
			}
				
			String[] labels = new String[totEl];
			labels[0] = AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_NOME;
			//labels[1] = "Descrizione";

			int index = 1;

			if( showProtocolli ) {
				labels[index] = AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO_COMPACT;
				index++;
			}
			
			if(this.isShowGestioneWorkflowStatoDocumenti()){
				if(this.core.isGestioneWorkflowStatoDocumentiVisualizzaStatoLista()) {
					labels[index] = AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_STATO;
					index++;
				}
			}

			labels[index] = AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_PARTECIPANTI;
			index++;
			labels[index] = AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_ALLEGATI;
			index++;

			this.pd.setLabels(labels);

			// preparo i dati
			List<List<DataElement>> dati = new ArrayList<>();

			if (lista != null) {
				Iterator<AccordoCooperazione> it = lista.iterator();
				AccordoCooperazione accordoCooperazione = null;
				while (it.hasNext()) {
					accordoCooperazione = it.next();
					List<DataElement> e = new ArrayList<>();

					String protocollo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(accordoCooperazione.getSoggettoReferente().getTipo());
					
					DataElement de = new DataElement();
					de.setUrl(
							AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_CHANGE,
							new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID, accordoCooperazione.getId() + ""),
							new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME, accordoCooperazione.getNome())
							);
					//String uriAccordo = this.idAccordoCooperazioneFactory.getUriFromAccordo(accordoCooperazione);
					de.setValue(this.getLabelIdAccordoCooperazione(accordoCooperazione));
					de.setIdToRemove("" + accordoCooperazione.getId());
					de.setToolTip(accordoCooperazione.getDescrizione());
					e.add(de);

					/*de = new DataElement();
						de.setValue(accordoCooperazione.getDescrizione());
						e.add(de);*/

					if(showProtocolli) {
						de = new DataElement();
						de.setValue(this.getLabelProtocollo(protocollo));
						e.add(de);
					}
					

					if(this.isShowGestioneWorkflowStatoDocumenti()){
						if(this.core.isGestioneWorkflowStatoDocumentiVisualizzaStatoLista()) {
							de = new DataElement();
							de.setValue(StatiAccordo.upper(accordoCooperazione.getStatoPackage()));
							e.add(de);
						}
					}

					de = new DataElement();
					de.setUrl(
							AccordiCooperazioneCostanti.SERVLET_NAME_AC_PARTECIPANTI_LIST,
							new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID, accordoCooperazione.getId() + ""),
							new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME, accordoCooperazione.getNome())
							);
					if (contaListe) {
						int numP = 0;
						if(accordoCooperazione.getElencoPartecipanti()!=null){
							numP = accordoCooperazione.getElencoPartecipanti().sizeSoggettoPartecipanteList();
						}
						ServletUtils.setDataElementVisualizzaLabel(de, (long)numP);
					} else {
						ServletUtils.setDataElementVisualizzaLabel(de);
					}
					e.add(de);

					de = new DataElement();
					de.setUrl(
							AccordiCooperazioneCostanti.SERVLET_NAME_AC_ALLEGATI_LIST,
							new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID, accordoCooperazione.getId() + ""),
							new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME, accordoCooperazione.getNome())
							);
					if (contaListe) {
						// BugFix OP-674
						//List<org.openspcoop2.core.registry.Documento> tmpLista = this.acCore.accordiCoopAllegatiList(accordoCooperazione.getId().intValue(), new Search(true));
						ConsoleSearch searchForCount = new ConsoleSearch(true,1);
						this.acCore.accordiCoopAllegatiList(accordoCooperazione.getId().intValue(), searchForCount);
						//int num = tmpLista.size();
						int num = searchForCount.getNumEntries(Liste.ACCORDI_COOP_ALLEGATI);
						ServletUtils.setDataElementVisualizzaLabel(de, (long)  num );

					} else {
						ServletUtils.setDataElementVisualizzaLabel(de);
					}
					e.add(de);

					dati.add(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);

			// preparo bottoni
			if(lista!=null && lista.size()>0){
				if (this.core.isShowPulsantiImportExport()) {
					
					ExporterUtils exporterUtils = new ExporterUtils(this.archiviCore);
					if(exporterUtils.existsAtLeastOneExportMode(ArchiveType.ACCORDO_COOPERAZIONE, this.request, this.session)){
					
						List<AreaBottoni> bottoni = new ArrayList<>();
	
						AreaBottoni ab = new AreaBottoni();
						List<DataElement> otherbott = new ArrayList<>();
						DataElement de = new DataElement();
						de.setValue(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_ESPORTA_SELEZIONATI);
						de.setOnClick(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_ESPORTA_SELEZIONATI_CLICK_EVENT);
						de.setDisabilitaAjaxStatus();
						otherbott.add(de);
						ab.setBottoni(otherbott);
						bottoni.add(ab);
	
						this.pd.setAreaBottoni(bottoni);
						
					}
				}
			}
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	// Prepara la lista di partecipanti degli accordi cooperazione
	public	void prepareAccordiPartecipantiList(AccordoCooperazione ac)
			throws Exception {
		try {

			String id = this.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID);
			ServletUtils.addListElementIntoSession(this.request, this.session, AccordiCooperazioneCostanti.OBJECT_NAME_AC_PARTECIPANTI,
					new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID, id));

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<>();

			this.pd.setSearchDescription("");
			lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST));
			lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_PARTECIPANTI_DI + this.idAccordoCooperazioneFactory.getUriFromAccordo(ac), null));

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// setto le label delle colonne
			String[] labels = { AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_PARTECIPANTE };
			this.pd.setLabels(labels);

			// preparo i dati
			List<List<DataElement>> dati = new ArrayList<>();

			if(ac.getElencoPartecipanti()!=null){
				AccordoCooperazionePartecipanti partecipanti = ac.getElencoPartecipanti();
				for (int i = 0; i < partecipanti.sizeSoggettoPartecipanteList(); i++) {
					IdSoggetto acep = partecipanti.getSoggettoPartecipante(i);

					List<DataElement> e = new ArrayList<>();

					DataElement de = new DataElement();
					de.setValue(acep.getTipo()+"/"+acep.getNome());
					de.setIdToRemove(""+acep.getId());
					e.add(de);

					dati.add(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(true);
			this.pd.setSearch("off");
		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public List<DataElement> addAccordiCooperazioneToDati(List<DataElement> dati, String nome, String descr,
			String id, TipoOperazione tipoOp, String referente, String versione, String[] providersList, String[] providersListLabel,
			boolean privato , String stato, String oldStato,
			String tipoProtocollo, List<String> listaTipiProtocollo, boolean used) throws Exception {

		boolean modificheAbilitate = false;
		if( tipoOp.equals(TipoOperazione.ADD) ){
			modificheAbilitate = true;
		}else if(this.isShowGestioneWorkflowStatoDocumenti()==false){
			modificheAbilitate = true;
		}else if(StatiAccordo.finale.toString().equals(oldStato)==false){
			modificheAbilitate = true;
		}


		DataElement de = new DataElement();

		if(TipoOperazione.CHANGE.equals(tipoOp)){
			de.setLabel(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID);
			de.setValue(id);
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID);
			dati.add(de);

			de = new DataElement();
			de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO);

			if(listaTipiProtocollo != null && listaTipiProtocollo.size() > 1){
			if(!used && modificheAbilitate){
				de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO);
				de.setValues(listaTipiProtocollo);
				de.setSelected(tipoProtocollo);
				de.setType(DataElementType.SELECT);
				de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO);
				de.setPostBack(true);
			}else {
				de.setValue(tipoProtocollo);
				de.setType(DataElementType.TEXT);
				de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO);
			}
			}else {
				de.setValue(tipoProtocollo);
				de.setType(DataElementType.HIDDEN);
				de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO );
			}
			de.setSize(this.getSize());
			dati.add(de);
		}

		// Gestione del tipo protocollo per la maschera add
		if(TipoOperazione.ADD.equals(tipoOp)){
			de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO);
			if(listaTipiProtocollo != null && listaTipiProtocollo.size() > 1 && modificheAbilitate){
				de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO);
				de.setValues(listaTipiProtocollo);
				de.setLabels(this.getLabelsProtocolli(listaTipiProtocollo));
				de.setSelected(tipoProtocollo);
				de.setType(DataElementType.SELECT);
				de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO);
				de.setPostBack(true);
			}else {
				
				DataElement deLABEL = new DataElement();
				deLABEL.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO);
				deLABEL.setType(DataElementType.TEXT);
				deLABEL.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO+CostantiControlStation.PARAMETRO_SUFFIX_LABEL);
				deLABEL.setValue(this.getLabelProtocollo(tipoProtocollo));
				dati.add(deLABEL);
				
				de.setValue(tipoProtocollo);
				de.setType(DataElementType.HIDDEN);
				de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO );
			}
			de.setSize(this.getSize());
			dati.add(de);
		}

		de = new DataElement();
		de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_NOME);
		de.setValue(nome);
		if( modificheAbilitate ){
			de.setType(DataElementType.TEXT_EDIT);
			de.setRequired(true);
		}else{
			de.setType(DataElementType.TEXT);
		}
		//} else {
		//	de.setType(DataElementType.TEXT);
		//}
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME);
		de.setSize(getSize());
		dati.add(de);

		de = new DataElement();
		de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_DESCRIZIONE);
		de.setValue(descr);
		de.setType(DataElementType.TEXT_EDIT);
		if( !modificheAbilitate && StringUtils.isBlank(descr))
			de.setValue("");
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_DESCRIZIONE);
		de.setSize(getSize());
		dati.add(de);

		de = new DataElement();
		de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_SOGGETTO_REFERENTE);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_REFERENTE);
		if (tipoOp.equals(TipoOperazione.ADD)) {

//			if (gestioneWSBL.equals(Costanti.CHECK_BOX_ENABLED)) {
				// if (tipoOp.equals("add")) {
				de.setType(DataElementType.SELECT);
				de.setValues(providersList);
				de.setLabels(providersListLabel);
				if (referente != null && !"".equals(referente)) {
					de.setSelected(referente);
				}else{
					de.setSelected("-");
				}
				//if(this.core.isBackwardCompatibilityAccordo11()==false){
				de.setRequired(true);
				//}
				// } else {
				// de.setType(DataElementType.HIDDEN);
				// de.setValue(referente);
				// }
//			} else {
//				de.setType(DataElementType.HIDDEN);
//				de.setValue(referente);
//			}
		}else{
			de.setType(DataElementType.HIDDEN);
			de.setValue(referente);
			dati.add(de);

			de = new DataElement();
			de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_SOGGETTO_REFERENTE);
			de.setName(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_SOGGETTO_REFERENTE_12);
			de.setType(DataElementType.TEXT);
			if (referente != null && !"".equals(referente) && !"-".equals(referente)) {
				Soggetto sogg = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(referente));
				de.setValue(this.getLabelNomeSoggetto(tipoProtocollo, sogg.getTipo(), sogg.getNome()));
			}else{
				de.setValue("-");
			}
		}		
		dati.add(de);

		if( modificheAbilitate ){
			de = this.getVersionDataElement(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_VERSIONE, 
					AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_VERSIONE, 
					versione, false);
		}
		else {
			de = new DataElement();
			de.setValue(versione);
			de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_VERSIONE);
			de.setType(DataElementType.TEXT);
		}
		dati.add(de);

		de = new DataElement();
		de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_PRIVATO);
		de.setValue(privato ? Costanti.CHECK_BOX_ENABLED : "");
		de.setSelected(privato ? Costanti.CHECK_BOX_ENABLED : "");
		if (this.core.isShowFlagPrivato() && modificheAbilitate && this.isModalitaAvanzata()) {
			de.setType(DataElementType.CHECKBOX);
		} else {
			de.setType(DataElementType.HIDDEN);
		}
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PRIVATO);
		de.setSize(getSize());
		dati.add(de);

		if(this.core.isShowFlagPrivato() && !modificheAbilitate && this.isModalitaAvanzata()){
			de = new DataElement();
			de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_VISIBILITA_ACCORDO);
			de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PRIVATO_LABEL);
			if(privato){
				de.setValue(AccordiCooperazioneCostanti.DEFAULT_VALUE_PARAMETRO_ACCORDI_COOPERAZIONE_VISIBILITA_ACCORDO_PRIVATA);
			}else{
				de.setValue(AccordiCooperazioneCostanti.DEFAULT_VALUE_PARAMETRO_ACCORDI_COOPERAZIONE_VISIBILITA_ACCORDO_PUBBLICA);
			}
			dati.add(de);
		}


		de = new DataElement();
		de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_STATO);
		if(this.isShowGestioneWorkflowStatoDocumenti()){
			if( tipoOp.equals(TipoOperazione.ADD)){
				
				DataElement deLabel = new DataElement();
				deLabel.setType(DataElementType.TEXT);
				deLabel.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_STATO);
				deLabel.setValue(StatiAccordo.upper(StatiAccordo.bozza.toString()));
				deLabel.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_STATO+CostantiControlStation.PARAMETRO_SUFFIX_LABEL);
				dati.add(deLabel);
				
				de.setType(DataElementType.HIDDEN);
				de.setValue(StatiAccordo.bozza.toString());
			} else if(StatiAccordo.finale.toString().equals(oldStato)==false ){
				de.setType(DataElementType.SELECT);
				de.setValues(StatiAccordo.toArray());
				de.setLabels(StatiAccordo.toLabel());
				de.setSelected(stato);
			}else{
				
				DataElement deLabel = new DataElement();
				deLabel.setType(DataElementType.TEXT);
				deLabel.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_STATO);
				deLabel.setValue(StatiAccordo.upper(StatiAccordo.finale.toString()));
				deLabel.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_STATO+CostantiControlStation.PARAMETRO_SUFFIX_LABEL);
				dati.add(deLabel);
				
				de.setType(DataElementType.HIDDEN);
				de.setValue(StatiAccordo.finale.toString());
			}
		}else{
			de.setType(DataElementType.HIDDEN);
			de.setValue(StatiAccordo.finale.toString());
		}
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_STATO);
		dati.add(de);


		if (tipoOp.equals(TipoOperazione.ADD) == false) {

			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(AccordiCooperazioneCostanti.SERVLET_NAME_AC_PARTECIPANTI_LIST,
					new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID, id+""),
					new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME, nome));

			if(contaListe){
				// BugFix OP-674
				//int num = this.acCore.accordiCoopPartecipantiList(Long.parseLong(id), new Search(true)).size();
				ConsoleSearch searchForCount = new ConsoleSearch(true,1);
				this.acCore.accordiCoopPartecipantiList(Long.parseLong(id), searchForCount);
				int num = searchForCount.getNumEntries(Liste.ACCORDI_COOP_PARTECIPANTI);
				de.setValue(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_SOGGETTI_PARTECIPANTI +"("+num+")");
			}else{
				de.setValue(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_SOGGETTI_PARTECIPANTI);
			}
			dati.add(de);

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(
					AccordiCooperazioneCostanti.SERVLET_NAME_AC_ALLEGATI_LIST,
					new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID, id+""),
					new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME, nome));
			if(contaListe){
				// BugFix OP-674
				//int num = this.acCore.accordiCoopAllegatiList(Integer.parseInt(id), new Search(true)).size();
				ConsoleSearch searchForCount = new ConsoleSearch(true,1);
				this.acCore.accordiCoopAllegatiList(Integer.parseInt(id), searchForCount);
				int num = searchForCount.getNumEntries(Liste.ACCORDI_COOP_ALLEGATI);
				de.setValue(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_ALLEGATI +"("+num+")");
			}else{
				de.setValue(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_ALLEGATI);
			}
			dati.add(de);

		}

		return dati;
	}

	public List<DataElement> addAccordiCooperazioneToDatiAsHidden(List<DataElement> dati, String nome, String descr,
			String id, TipoOperazione tipoOp, String referente, String versione, String[] providersList, String[] providersListLabel,
			boolean privato , String stato, String oldStato,
			String tipoProtocollo, List<String> listaTipiProtocollo, boolean used) throws Exception {

//		boolean modificheAbilitate = false;
//		if( tipoOp.equals(TipoOperazione.ADD) ){
//			modificheAbilitate = true;
//		}else if(this.isShowGestioneWorkflowStatoDocumenti()==false){
//			modificheAbilitate = true;
//		}else if(StatiAccordo.finale.toString().equals(oldStato)==false){
//			modificheAbilitate = true;
//		}

		DataElement de = new DataElement();
		de.setValue(tipoProtocollo);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO);
		de.setSize(this.getSize());
		dati.add(de);

		if(TipoOperazione.CHANGE.equals(tipoOp)){
			de = new DataElement();
			de.setLabel(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID);
			de.setValue(id);
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID);
			dati.add(de);
		}

		de = new DataElement();
		de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_NOME);
		de.setValue(nome);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME);
		de.setSize(getSize());
		dati.add(de);

		de = new DataElement();
		de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_DESCRIZIONE);
		de.setValue(descr);
		de.setType(DataElementType.HIDDEN);
		de.setValue(descr);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_DESCRIZIONE);
		de.setSize(getSize());
		dati.add(de);

		de = new DataElement();
		de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_SOGGETTO_REFERENTE);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_REFERENTE);
		if (referente != null && !"".equals(referente) && !"-".equals(referente)) {
			Soggetto sogg = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(referente));
			de.setValue("" +sogg.getId());
		}else{
			de.setValue("-");
		}
		de.setType(DataElementType.HIDDEN);
		dati.add(de);

		de = new DataElement();
		de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_VERSIONE);
		de.setValue(versione);
		de.setType(DataElementType.HIDDEN);
		//		if( modificheAbilitate ){
		//			//			if (gestioneWSBL.equals(Costanti.CHECK_BOX_ENABLED)){
		//			de.setType(DataElementType.TEXT_EDIT);
		//			//if(this.core.isBackwardCompatibilityAccordo11()==false){
		//			//de.setRequired(true);
		//			// version spinner parte da 1
		//
		//			ServletUtils.setObjectIntoSession(request, session, AccordiCooperazioneCostanti.DEFAULT_VALUE_PARAMETRO_ACCORDI_COOPERAZIONE_VERSION,
		//					AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_VERSION);
		//			/*}else{
		//						// version spinner parte da 0
		//						if(versione==null || "".equals(versione)){
		//							de.setValue("0");
		//						}
		//						ServletUtils.setObjectIntoSession(request, session, "optional", "version");
		//					}*/
		//			//			}else
		//			//				de.setType(DataElementType.HIDDEN);
		//		}else{
		//			de.setType(DataElementType.TEXT);
		//		}
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_VERSIONE);
		dati.add(de);

		de = new DataElement();
		de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_PRIVATO);
		de.setValue(privato ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED);
		de.setType(DataElementType.HIDDEN);
		//		de.setSelected(privato ? Costanti.CHECK_BOX_ENABLED : "");
		//		if (this.core.isShowFlagPrivato() && modificheAbilitate && !InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
		//			de.setType(DataElementType.CHECKBOX);
		//		} else {
		//			de.setType(DataElementType.HIDDEN);
		//		}
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PRIVATO);
		de.setSize(getSize());
		dati.add(de);

		//		if(this.core.isShowFlagPrivato() && !modificheAbilitate && !InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())){
		de = new DataElement();
		de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_VISIBILITA_ACCORDO);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PRIVATO_LABEL);
		de.setType(DataElementType.HIDDEN);
		if(privato){
			de.setValue(AccordiCooperazioneCostanti.DEFAULT_VALUE_PARAMETRO_ACCORDI_COOPERAZIONE_VISIBILITA_ACCORDO_PRIVATA);
		}else{
			de.setValue(AccordiCooperazioneCostanti.DEFAULT_VALUE_PARAMETRO_ACCORDI_COOPERAZIONE_VISIBILITA_ACCORDO_PUBBLICA);
		}
		dati.add(de);
		//		}


		de = new DataElement();
		de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_STATO);

		//		if(this.isShowGestioneWorkflowStatoDocumenti()){
//			String[] stati = StatiAccordo.toArray();
//			if( tipoOp.equals(TipoOperazione.ADD)){
//				de.setType(DataElementType.TEXT);
//				de.setValue(StatiAccordo.bozza.toString());
//			} else if(StatiAccordo.finale.toString().equals(oldStato)==false ){
//				de.setType(DataElementType.SELECT);
//				de.setValues(stati);
//				de.setSelected(stato);
//			}else{
//				de.setType(DataElementType.TEXT);
//				de.setValue(StatiAccordo.finale.toString());
//			}
//		}else{
//			de.setType(DataElementType.HIDDEN);
//			de.setValue(StatiAccordo.finale.toString());
//		}
		de.setType(DataElementType.HIDDEN);
		de.setValue(stato);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_STATO);
		dati.add(de);

		return dati;
	}

	public void prepareAccordiCoopPartecipantiList(AccordoCooperazione ac,List<IDSoggetto> lista, ISearch ricerca)
			throws Exception {
		try {
			String id = this.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID);
			ServletUtils.addListElementIntoSession(this.request, this.session, AccordiCooperazioneCostanti.OBJECT_NAME_AC_PARTECIPANTI,
					new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID, id));
			
			int idLista = Liste.ACCORDI_COOP_PARTECIPANTI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			String titleAS = this.getLabelIdAccordoCooperazione(ac);
			
			// setto la barra del titolo
			this.pd.setSearchDescription("");
			List<Parameter> lstParam = new ArrayList<>();
			lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST));
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_PARTECIPANTI_DI + titleAS, null));
			}
			else{
				lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_PARTECIPANTI_DI + titleAS, 
						AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// controllo eventuali risultati ricerca
			this.pd.setSearchLabel(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_PARTECIPANTE);
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_PARTECIPANTI, search);
			}
			
			
			// setto le label delle colonne
			String[] labels = { AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_PARTECIPANTE };
			this.pd.setLabels(labels);

			// preparo i dati
			List<List<DataElement>> dati = new ArrayList<>();

			for (int i = 0; i < lista.size(); i++) {
				IDSoggetto idSO = lista.get(i);

				List<DataElement> e = new ArrayList<>();

				DataElement de = new DataElement();
				de.setValue(this.getLabelNomeSoggetto(this.soggettiCore.getProtocolloAssociatoTipoSoggetto(idSO.getTipo()), idSO.getTipo(),idSO.getNome()));
				de.setIdToRemove(""+idSO.getTipo()+"/"+idSO.getNome());
				e.add(de);

				dati.add(e);
			}

			this.pd.setDati(dati);

			if(this.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(ac.getStatoPackage())){
				this.pd.setAddButton(false);
				this.pd.setRemoveButton(false);
				this.pd.setSelect(false);
			}else{
				this.pd.setAddButton(true);
				this.pd.setRemoveButton(true);
				this.pd.setSelect(true);
			}

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public void prepareAccordiCoopAllegatiList(AccordoCooperazione ac, ISearch ricerca, List<Documento> lista) throws Exception {
		try {
			ServletUtils.addListElementIntoSession(this.request, this.session, AccordiCooperazioneCostanti.OBJECT_NAME_AC_ALLEGATI,
					new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID, ""+ac.getId()));


			int idLista = Liste.ACCORDI_COOP_ALLEGATI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			String titleAS = this.getLabelIdAccordoCooperazione(ac);
			
			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<>();

			lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST));
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_ALLEGATI_DI  
						+ titleAS, null));
			} else {
				lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_ALLEGATI_DI  
						+ titleAS,
						AccordiCooperazioneCostanti.SERVLET_NAME_AC_ALLEGATI_LIST, 
						new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID, ac.getId()+ "")
						));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// controllo eventuali risultati ricerca
			this.pd.setSearchLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_NOME);
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_ALLEGATI, search);
			}

			// setto le label delle colonne
			String[] labels = { 
					AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_NOME,
					AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_RUOLO  ,
					AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_TIPO  ,
					AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_DOCUMENTO   
					};
			this.pd.setLabels(labels);

			// preparo i dati
			List<List<DataElement>> dati = new ArrayList<>();

			if (lista != null) {
				Iterator<Documento> it = lista.iterator();
				while (it.hasNext()) {
					Documento doc = it.next();

					List<DataElement> e = new ArrayList<>();

					DataElement de = new DataElement();
					de.setUrl(
							AccordiCooperazioneCostanti.SERVLET_NAME_AC_ALLEGATI_CHANGE,
							new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID_ALLEGATO,doc.getId()+""),
							new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID_ACCORDO,ac.getId()+""),
							new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME_DOCUMENTO,doc.getFile())
							
						 
							);
					de.setValue(doc.getFile());
					de.setIdToRemove(""+doc.getId());
					e.add(de);

					de = new DataElement();
					de.setValue(doc.getRuolo());
					e.add(de);

					de = new DataElement();
					de.setValue(doc.getTipo());
					e.add(de);

					de = new DataElement();
					if(this.core.isShowAllegati()) {
						de.setUrl(
								AccordiCooperazioneCostanti.SERVLET_NAME_AC_ALLEGATI_VIEW,
	
								new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID_ALLEGATO,doc.getId()+""),
								new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID_ACCORDO,ac.getId()+""),
								new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME_DOCUMENTO,doc.getFile())
								);
						ServletUtils.setDataElementVisualizzaLabel(de);
					}
					else {
						Parameter pTipoDoc = new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_TIPO_DOCUMENTO, "ac");
						de.setUrl(
								ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT,
								new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID_ALLEGATO,doc.getId()+""),
								new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID_ACCORDO,ac.getId()+""),
								new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME_DOCUMENTO,doc.getFile()),
								pTipoDoc
								);
						de.setValue(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_DOWNLOAD.toLowerCase());
						de.setDisabilitaAjaxStatus();
					}
					e.add(de);

					dati.add(e);
				}
			}

			this.pd.setDati(dati);

			if(this.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(ac.getStatoPackage())){
				this.pd.setAddButton(false);
				this.pd.setRemoveButton(false);
				this.pd.setSelect(false);
			}else{
				this.pd.setAddButton(true);
				this.pd.setRemoveButton(true);
				this.pd.setSelect(true);
			}

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public List<DataElement>  addAllegatiToDati(TipoOperazione tipoOp, String idAllegato, String idAccordo,
			Documento doc, StringBuilder contenutoAllegato, String errore,
			List<DataElement> dati, String statoPackage, boolean editMode) {
		DataElement de = new DataElement();

		de.setValue(idAllegato);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID_ALLEGATO);
		dati.add(de);

		de = new DataElement();
		de.setValue(idAccordo);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID_ACCORDO);
		dati.add(de);

		de = new DataElement();
		de.setType(DataElementType.TITLE);
		de.setLabel(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_ALLEGATO);
		dati.add(de);
		
		de = new DataElement();
		de.setValue(doc.getRuolo());
		de.setLabel("Ruolo");
		de.setType(DataElementType.TEXT);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_RUOLO);
		de.setSize(getSize());
		dati.add(de);

		de = new DataElement();
		de.setValue(doc.getFile());
		de.setLabel("Nome");
		de.setType(DataElementType.TEXT);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME_DOCUMENTO);
		de.setSize(getSize());
		dati.add(de);

		de = new DataElement();
		de.setValue(doc.getTipo());
		de.setLabel("Tipo");
		de.setType(DataElementType.TEXT);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_TIPO_FILE);
		de.setSize(getSize());
		dati.add(de);

		if(tipoOp.equals(TipoOperazione.OTHER)){
			if(this.core.isShowAllegati()) {
				if(errore!=null){
					de = new DataElement();
					de.setValue(errore);
					de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_DOCUMENTO);
					de.setType(DataElementType.TEXT);
					de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_DOCUMENTO  );
					de.setSize( getSize());
					dati.add(de);
				}
				else{
					de = new DataElement();
					de.setLabel("");//AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_DOCUMENTO);
					de.setType(DataElementType.TEXT_AREA_NO_EDIT);
					de.setValue(contenutoAllegato.toString());
					de.setRows(CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_API_SIZE);
					de.setCols(CostantiControlStation.LABEL_PARAMETRO_TEXT_AREA_API_COLUMNS);
					dati.add(de);
				}
			}
			
			DataElement saveAs = new DataElement();
			saveAs.setValue(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_DOWNLOAD);
			saveAs.setType(DataElementType.LINK);
			Parameter pIdAccordo = new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID_ACCORDO, idAccordo);
			Parameter pIdAllegato = new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID_ALLEGATO, idAllegato);
			Parameter pTipoDoc = new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_TIPO_DOCUMENTO, "ac");
			//			String params = "idAccordo="+idServizio+"&idAllegato="+idAllegato+"&tipoDocumento=asps";
			saveAs.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, pIdAccordo, pIdAllegato, pTipoDoc);
			saveAs.setDisabilitaAjaxStatus();
			dati.add(saveAs);
		}

		if(tipoOp.equals(TipoOperazione.CHANGE)){
			if(editMode){
				if(this.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(statoPackage)){
					this.pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
				}
				else{	
					de = new DataElement();
					de.setType(DataElementType.FILE);
					de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_DOCUMENTO);
					de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_THE_FILE);
					de.setSize(getSize());
					dati.add(de);
				}
			}else{
				de = new DataElement();
				de.setType(DataElementType.FILE);
				de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_DOCUMENTO);
				de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_THE_FILE);
				de.setSize(getSize());
				dati.add(de);
			}
		}

		return dati;
	}

	public List<DataElement> addAllegatoToDati(TipoOperazione tipoOp, String idAccordo, String ruolo,
			String[] ruoli, String[] tipiAmmessi, String[] tipiAmmessiLabel,
			List<DataElement> dati) {
		DataElement de = new DataElement();

		de.setValue(idAccordo);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID);
		dati.add(de);

		de = new DataElement();
		de.setType(DataElementType.TITLE);
		de.setLabel(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_ALLEGATO);
		dati.add(de);
		
		de = new DataElement();
		de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_RUOLO);
		de.setType(DataElementType.SELECT);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_RUOLO);
		de.setValues(ruoli);
		//				de.setOnChange("CambiaTipoDocumento('accordiCoopAllegati')");
		de.setPostBack(true);
		de.setSelected(ruolo!=null ? ruolo : "");
		de.setSize( getSize());
		dati.add(de);

		if(tipiAmmessi!=null){
			de = new DataElement();
			de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_TIPO);
			de.setType(DataElementType.SELECT);
			de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_TIPO_FILE);
			de.setValues(tipiAmmessi);
			de.setLabels(tipiAmmessiLabel);
			de.setSize( getSize());
			dati.add(de);
		}

		de = new DataElement();
		de.setValue(idAccordo);
		de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_DOCUMENTO);
		de.setType(DataElementType.FILE);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_THE_FILE);
		de.setSize( getSize());
		dati.add(de);

		return dati;
	}

	public List<DataElement> addPartecipanteToDati(TipoOperazione tipoOp, String id,
			String[] partecipantiNonInseriti,String[] partecipantiNonInseritiLabels, List<DataElement> dati) {
		
		DataElement de = new DataElement();
		de.setLabel(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_PARTECIPANTE);
		de.setType(DataElementType.TITLE);
		dati.add(de);
		
		de = new DataElement();
		de.setValue(id);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID);
		dati.add(de);

		de = new DataElement();
		de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_PARTECIPANTE);
		de.setValues(partecipantiNonInseriti );
		de.setLabels(partecipantiNonInseritiLabels );
		de.setType(DataElementType.SELECT);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PARTECIPANTE);
		de.setSize(getSize());
		dati.add(de);

		return dati;
	}

}
