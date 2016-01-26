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
package org.openspcoop2.web.ctrlstat.servlet.ac;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoCooperazionePartecipanti;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.protocol.sdk.validator.ValidazioneResult;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.web.ctrlstat.core.Search;
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
import org.openspcoop2.web.lib.users.dao.InterfaceType;

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


	// Controlla i dati degli Accordi cooperazione
	public boolean accordiCooperazioneCheckData(TipoOperazione tipoOp, String nome, String descr, String id, String referente, String versione,boolean visibilitaAccordoCooperazione,
			IDAccordoCooperazione idAccordoOLD)
					throws Exception {

		try{

			// String userLogin = (String) this.session.getAttribute("Login");

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
				this.pd.setMessage("Dati incompleti. E' necessario indicare un Nome");
				return false;
			}
			if(referente==null || referente.equals("") || referente.equals("-")){
				this.pd.setMessage("Dati incompleti. E' necessario indicare un Soggetto Referente");
				return false;
			}
			//if(this.core.isBackwardCompatibilityAccordo11()==false){
			if (versione==null || versione.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare una Versione dell'accordo");
				return false;
			}
			//}

//			if (gestioneWSBL.equals("yes") && (referente == null || "".equals(referente))) {
//				this.pd.setMessage("Dati incompleti. E' necessario indicare un Soggetto referente");
//				return false;
//			}

			// Controllo che non ci siano spazi nei campi di testo
			if (nome.indexOf(" ") != -1) {
				this.pd.setMessage("Non inserire spazi nei campi di testo");
				return false;
			}

			// Il nome deve contenere solo lettere e numeri e '_' '-' '.'
			if (!RegularExpressionEngine.isMatch(nome,"^[0-9A-Za-z_\\-\\.]+$")) {
				this.pd.setMessage("Il nome dell'accordo di servizio dev'essere formato solo caratteri, cifre, '_' , '-' e '.'");
				return false;
			}

			// La versione deve contenere solo lettere e numeri e '.'
//			if (gestioneWSBL.equals("yes")) {
				/*if (!versione.equals("") && !this.procToCall.isOk("^[1-9]+[\\.][0-9]+[0-9A-Za-z]*$", versione)  && !this.procToCall.isOk("^[0-9]+$",versione)) {
						this.pd.setMessage("La versione dev'essere scritto come MajorVersion[.MinorVersion*] (MajorVersion [1-9]) (MinorVersion [0-9]) (* [0-9A-Za-z]) ");
						return false;
					}*/
				if (!versione.equals("") && !RegularExpressionEngine.isMatch(versione,"^[1-9]+[0-9]*$")) {
					this.pd.setMessage("La versione dev'essere rappresentata da un numero intero");
					return false;
				}
//			}

			// Controllo che il referente appartenga alla lista di
			// providers disponibili
			//IDSoggetto soggettoReferente = null;
			//if (gestioneWSBL.equals("yes")) {
			Soggetto sRef = null;
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
				}
			}
			//	}

			// Controllo che non esistano altri accordi con stesso nome
			// Se tipoOp = change, devo fare attenzione a non escludere nome
			// del servizio selezionato
			int idAcc = 0;
			IDAccordoCooperazione idAccordo = this.idAccordoCooperazioneFactory.getIDAccordoFromValues(nome,versione);
			boolean esisteAC = this.acCore.existsAccordoCooperazione(idAccordo);
			AccordoCooperazione ac = null;
			if (esisteAC) {
				ac = this.acCore.getAccordoCooperazione(idAccordo);
				idAcc = ac.getId().intValue();
			}
			if ((idAcc != 0) && (tipoOp.equals(TipoOperazione.ADD) || (tipoOp.equals(TipoOperazione.CHANGE) && (idInt != idAcc)))) {
				if(this.idAccordoCooperazioneFactory.versioneNonDefinita(versione))
					this.pd.setMessage("Esiste gi&agrave; un accordo con nome " + nome);
				else
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
			accordoCooperazione.setVersione(versione);
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

			ServletUtils.addListElementIntoSession(this.session, AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE);

			int idLista = Liste.ACCORDI_COOPERAZIONE;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, null));
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, null));
			} else {
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Accordi cooperazione contenenti la stringa '" + search + "'");
			}

//			String gestioneWSBL = ServletUtils.getGestioneWSBLFromSession(this.session);
//			if(gestioneWSBL == null){
//				gestioneWSBL = Costanti.CHECK_BOX_DISABLED;
//			}

			// setto le label delle colonne
			int totEl = 3;
			if(this.core.isShowGestioneWorkflowStatoDocumenti())
				totEl++;
//			if (gestioneWSBL.equals(Costanti.CHECK_BOX_ENABLED))
				totEl++;

			String[] labels = new String[totEl];
			labels[0] = AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_NOME;
			//labels[1] = "Descrizione";

			int index = 1;

//			if (gestioneWSBL.equals(Costanti.CHECK_BOX_ENABLED)) {
				labels[index] = AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_REFERENTE;
				index++;
//			}

			if(this.core.isShowGestioneWorkflowStatoDocumenti()){
				labels[index] = AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_STATO;
				index++;
			}

			labels[index] = AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_PARTECIPANTI;
			index++;
			labels[index] = AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_ALLEGATI;
			index++;

			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<AccordoCooperazione> it = lista.iterator();
				AccordoCooperazione accordoCooperazione = null;
				while (it.hasNext()) {
					accordoCooperazione = it.next();
					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(
							AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_CHANGE,
							new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID, accordoCooperazione.getId() + ""),
							new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME, accordoCooperazione.getNome())
							);
					String uriAccordo = this.idAccordoCooperazioneFactory.getUriFromAccordo(accordoCooperazione);
					de.setValue(uriAccordo);
					de.setIdToRemove("" + accordoCooperazione.getId());
					de.setToolTip(accordoCooperazione.getDescrizione());
					e.addElement(de);

					/*de = new DataElement();
						de.setValue(accordoCooperazione.getDescrizione());
						e.addElement(de);*/

//					if (gestioneWSBL.equals(Costanti.CHECK_BOX_ENABLED)) {
						de = new DataElement();
						IdSoggetto acsr = accordoCooperazione.getSoggettoReferente();
						if (acsr != null)
							de.setValue(acsr.getTipo() + "/" + acsr.getNome());
						e.addElement(de);
//					}

					if(this.core.isShowGestioneWorkflowStatoDocumenti()){
						de = new DataElement();
						de.setValue(accordoCooperazione.getStatoPackage());
						e.addElement(de);
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
					e.addElement(de);

					de = new DataElement();
					de.setUrl(
							AccordiCooperazioneCostanti.SERVLET_NAME_AC_ALLEGATI_LIST,
							new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID, accordoCooperazione.getId() + ""),
							new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME, accordoCooperazione.getNome())
							);
					if (contaListe) {
						List<org.openspcoop2.core.registry.Documento> tmpLista = this.acCore.accordiCoopAllegatiList(accordoCooperazione.getId().intValue(), new Search(true));
						ServletUtils.setDataElementVisualizzaLabel(de, (long)  tmpLista.size() );

					} else {
						ServletUtils.setDataElementVisualizzaLabel(de);
					}
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);
			this.pd.setAddButton(this.core.isShowPulsanteAggiungiElenchi());

			// preparo bottoni
			if(lista!=null && lista.size()>0){
				if (this.core.isShowPulsantiImportExport()) {
					
					ExporterUtils exporterUtils = new ExporterUtils(this.archiviCore);
					if(exporterUtils.existsAtLeastOneExportMpde(ArchiveType.ACCORDO_COOPERAZIONE)){
					
						Vector<AreaBottoni> bottoni = new Vector<AreaBottoni>();
	
						AreaBottoni ab = new AreaBottoni();
						Vector<DataElement> otherbott = new Vector<DataElement>();
						DataElement de = new DataElement();
						de.setValue(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_ESPORTA_SELEZIONATI);
						de.setOnClick(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_ESPORTA_SELEZIONATI_CLICK_EVENT);
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

	// Prepara la lista di partecipanti degli accordi cooperazione
	public	void prepareAccordiPartecipantiList(AccordoCooperazione ac)
			throws Exception {
		try {

			String id = this.request.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID);
			ServletUtils.addListElementIntoSession(this.session, AccordiCooperazioneCostanti.OBJECT_NAME_AC_PARTECIPANTI,
					new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID, id));

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, null));
			this.pd.setSearchDescription("");
			lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST));
			lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_PARTECIPANTI_DI + this.idAccordoCooperazioneFactory.getUriFromAccordo(ac), null));

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// setto le label delle colonne
			String[] labels = { AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_PARTECIPANTE };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if(ac.getElencoPartecipanti()!=null){
				AccordoCooperazionePartecipanti partecipanti = ac.getElencoPartecipanti();
				for (int i = 0; i < partecipanti.sizeSoggettoPartecipanteList(); i++) {
					IdSoggetto acep = partecipanti.getSoggettoPartecipante(i);

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setValue(acep.getTipo()+"/"+acep.getNome());
					de.setIdToRemove(""+acep.getId());
					e.addElement(de);

					dati.addElement(e);
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

	public Vector<DataElement> addAccordiCooperazioneToDati(Vector<DataElement> dati, String nome, String descr,
			String id, TipoOperazione tipoOp, String referente, String versione, String[] providersList, String[] providersListLabel,
			boolean privato , String stato, String oldStato,
			String tipoProtocollo, List<String> listaTipiProtocollo, boolean used) throws Exception {

		boolean modificheAbilitate = false;
		if( tipoOp.equals(TipoOperazione.ADD) ){
			modificheAbilitate = true;
		}else if(this.core.isShowGestioneWorkflowStatoDocumenti()==false){
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
			dati.addElement(de);

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
			dati.addElement(de);
		}

		// Gestione del tipo protocollo per la maschera add
		if(TipoOperazione.ADD.equals(tipoOp)){
			de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO);
			if(listaTipiProtocollo != null && listaTipiProtocollo.size() > 1 && modificheAbilitate){
				de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO);
				de.setValues(listaTipiProtocollo);
				de.setSelected(tipoProtocollo);
				de.setType(DataElementType.SELECT);
				de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO);
				de.setPostBack(true);
			}else {
				de.setValue(tipoProtocollo);
				de.setType(DataElementType.HIDDEN);
				de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO );
			}
			de.setSize(this.getSize());
			dati.addElement(de);
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
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_DESCRIZIONE);
		de.setValue(descr);
		de.setType(DataElementType.TEXT_EDIT);
		if( !modificheAbilitate && (descr==null || "".equals(descr)) )
			de.setValue(" ");
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_DESCRIZIONE);
		de.setSize(getSize());
		dati.addElement(de);

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
			dati.addElement(de);

			de = new DataElement();
			de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_SOGGETTO_REFERENTE);
			de.setName(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_SOGGETTO_REFERENTE_12);
			de.setType(DataElementType.TEXT);
			if (referente != null && !"".equals(referente) && !"-".equals(referente)) {
				Soggetto sogg = this.soggettiCore.getSoggettoRegistro(Integer.parseInt(referente));
				de.setValue(sogg.getTipo()+"/"+sogg.getNome());
			}else{
				de.setValue("-");
			}
		}		
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_VERSIONE);
		de.setValue(versione);
		if( modificheAbilitate ){
//			if (gestioneWSBL.equals(Costanti.CHECK_BOX_ENABLED)){
				de.setType(DataElementType.TEXT_EDIT);
				//if(this.core.isBackwardCompatibilityAccordo11()==false){
				//de.setRequired(true);
				// version spinner parte da 1

				this.session.setAttribute(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_VERSION, 
						AccordiCooperazioneCostanti.DEFAULT_VALUE_PARAMETRO_ACCORDI_COOPERAZIONE_VERSION);
				/*}else{
						// version spinner parte da 0
						if(versione==null || "".equals(versione)){
							de.setValue("0");
						}
						this.session.setAttribute("version", "optional");
					}*/
//			}else
//				de.setType(DataElementType.HIDDEN);
		}else{
			de.setType(DataElementType.TEXT);
		}
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_VERSIONE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_PRIVATO);
		de.setValue(privato ? Costanti.CHECK_BOX_ENABLED : "");
		de.setSelected(privato ? Costanti.CHECK_BOX_ENABLED : "");
		if (this.core.isShowFlagPrivato() && modificheAbilitate && !InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
			de.setType(DataElementType.CHECKBOX);
		} else {
			de.setType(DataElementType.HIDDEN);
		}
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PRIVATO);
		de.setSize(getSize());
		dati.addElement(de);

		if(this.core.isShowFlagPrivato() && !modificheAbilitate && !InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())){
			de = new DataElement();
			de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_VISIBILITA_ACCORDO);
			de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PRIVATO_LABEL);
			if(privato){
				de.setValue(AccordiCooperazioneCostanti.DEFAULT_VALUE_PARAMETRO_ACCORDI_COOPERAZIONE_VISIBILITA_ACCORDO_PRIVATA);
			}else{
				de.setValue(AccordiCooperazioneCostanti.DEFAULT_VALUE_PARAMETRO_ACCORDI_COOPERAZIONE_VISIBILITA_ACCORDO_PUBBLICA);
			}
			dati.addElement(de);
		}


		de = new DataElement();
		de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_STATO);
		if(this.core.isShowGestioneWorkflowStatoDocumenti()){
			String[] stati = StatiAccordo.toArray();
			if( tipoOp.equals(TipoOperazione.ADD)){
				de.setType(DataElementType.TEXT);
				de.setValue(StatiAccordo.bozza.toString());
			} else if(StatiAccordo.finale.toString().equals(oldStato)==false ){
				de.setType(DataElementType.SELECT);
				de.setValues(stati);
				de.setSelected(stato);
			}else{
				de.setType(DataElementType.TEXT);
				de.setValue(StatiAccordo.finale.toString());
			}
		}else{
			de.setType(DataElementType.HIDDEN);
			de.setValue(StatiAccordo.finale.toString());
		}
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_STATO);
		dati.addElement(de);


		if (tipoOp.equals(TipoOperazione.ADD) == false) {

			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(AccordiCooperazioneCostanti.SERVLET_NAME_AC_PARTECIPANTI_LIST,
					new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID, id+""),
					new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME, nome));

			if(contaListe){
				int num = this.acCore.accordiCoopPartecipantiList(Long.parseLong(id), new Search(true)).size();
				de.setValue(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_SOGGETTI_PARTECIPANTI +"("+num+")");
			}else{
				de.setValue(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_SOGGETTI_PARTECIPANTI);
			}
			dati.addElement(de);

			de = new DataElement();
			de.setType(DataElementType.LINK);
			de.setUrl(
					AccordiCooperazioneCostanti.SERVLET_NAME_AC_ALLEGATI_LIST,
					new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID, id+""),
					new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME, nome));
			if(contaListe){
				int num = this.acCore.accordiCoopAllegatiList(Integer.parseInt(id), new Search(true)).size();
				de.setValue(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_ALLEGATI +"("+num+")");
			}else{
				de.setValue(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_ALLEGATI);
			}
			dati.addElement(de);

		}

		return dati;
	}

	public Vector<DataElement> addAccordiCooperazioneToDatiAsHidden(Vector<DataElement> dati, String nome, String descr,
			String id, TipoOperazione tipoOp, String referente, String versione, String[] providersList, String[] providersListLabel,
			boolean privato , String stato, String oldStato,
			String tipoProtocollo, List<String> listaTipiProtocollo, boolean used) throws Exception {

//		boolean modificheAbilitate = false;
//		if( tipoOp.equals(TipoOperazione.ADD) ){
//			modificheAbilitate = true;
//		}else if(this.core.isShowGestioneWorkflowStatoDocumenti()==false){
//			modificheAbilitate = true;
//		}else if(StatiAccordo.finale.toString().equals(oldStato)==false){
//			modificheAbilitate = true;
//		}

		DataElement de = new DataElement();
		de.setValue(tipoProtocollo);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO);
		de.setSize(this.getSize());
		dati.addElement(de);

		if(TipoOperazione.CHANGE.equals(tipoOp)){
			de = new DataElement();
			de.setLabel(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID);
			de.setValue(id);
			de.setType(DataElementType.HIDDEN);
			de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID);
			dati.addElement(de);
		}

		de = new DataElement();
		de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_NOME);
		de.setValue(nome);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME);
		de.setSize(getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_DESCRIZIONE);
		de.setValue(descr);
		de.setType(DataElementType.HIDDEN);
		de.setValue(descr);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_DESCRIZIONE);
		de.setSize(getSize());
		dati.addElement(de);

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
		dati.addElement(de);

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
		//			this.session.setAttribute(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_VERSION, 
		//					AccordiCooperazioneCostanti.DEFAULT_VALUE_PARAMETRO_ACCORDI_COOPERAZIONE_VERSION);
		//			/*}else{
		//						// version spinner parte da 0
		//						if(versione==null || "".equals(versione)){
		//							de.setValue("0");
		//						}
		//						this.session.setAttribute("version", "optional");
		//					}*/
		//			//			}else
		//			//				de.setType(DataElementType.HIDDEN);
		//		}else{
		//			de.setType(DataElementType.TEXT);
		//		}
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_VERSIONE);
		dati.addElement(de);

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
		dati.addElement(de);

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
		dati.addElement(de);
		//		}


		de = new DataElement();
		de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_STATO);

		//		if(this.core.isShowGestioneWorkflowStatoDocumenti()){
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
		dati.addElement(de);


//		if (tipoOp.equals(TipoOperazione.ADD) == false) {
//
//			Boolean contaListe = ServletUtils.getContaListeFromSession(this.session);
//
//			de = new DataElement();
//			de.setType(DataElementType.LINK);
//			de.setUrl(AccordiCooperazioneCostanti.SERVLET_NAME_AC_PARTECIPANTI_LIST,
//					new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID, id+""),
//					new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME, nome));
//
//			if(contaListe){
//				int num = this.acCore.accordiCoopPartecipantiList(Long.parseLong(id), new Search(true)).size();
//				de.setValue(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_SOGGETTI_PARTECIPANTI +"("+num+")");
//			}else{
//				de.setValue(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_SOGGETTI_PARTECIPANTI);
//			}
//			dati.addElement(de);
//
//			de = new DataElement();
//			de.setType(DataElementType.LINK);
//			de.setUrl(
//					AccordiCooperazioneCostanti.SERVLET_NAME_AC_ALLEGATI_LIST,
//					new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID, id+""),
//					new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME, nome));
//			if(contaListe){
//				int num = this.acCore.accordiCoopAllegatiList(Integer.parseInt(id), new Search(true)).size();
//				de.setValue(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_ALLEGATI +"("+num+")");
//			}else{
//				de.setValue(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_ALLEGATI);
//			}
//			dati.addElement(de);
//
//		}

		return dati;
	}

	public void prepareAccordiCoopPartecipantiList(AccordoCooperazione ac,List<IDSoggetto> lista, ISearch ricerca)
			throws Exception {
		try {
			String id = this.request.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID);
			ServletUtils.addListElementIntoSession(this.session, AccordiCooperazioneCostanti.OBJECT_NAME_AC_PARTECIPANTI,
					new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID, id));
			
			int idLista = Liste.ACCORDI_COOP_PARTECIPANTI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();
			lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, null));
			this.pd.setSearchDescription("");
			lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST));
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_PARTECIPANTI_DI + this.idAccordoCooperazioneFactory.getUriFromAccordo(ac), null));
			}
			else{
				lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_PARTECIPANTI_DI + this.idAccordoCooperazioneFactory.getUriFromAccordo(ac), 
						AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Soggetti Partecipanti contenenti la stringa '" + search + "'");
			}
			
			
			// setto le label delle colonne
			String[] labels = { AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_PARTECIPANTE };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			for (int i = 0; i < lista.size(); i++) {
				IDSoggetto idSO = lista.get(i);

				Vector<DataElement> e = new Vector<DataElement>();

				DataElement de = new DataElement();
				de.setValue(idSO.getTipo()+"/"+idSO.getNome());
				de.setIdToRemove(""+idSO.getTipo()+"/"+idSO.getNome());
				e.addElement(de);

				dati.addElement(e);
			}

			this.pd.setDati(dati);

			if(this.core.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(ac.getStatoPackage())){
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
			ServletUtils.addListElementIntoSession(this.session, AccordiCooperazioneCostanti.OBJECT_NAME_AC_ALLEGATI,
					new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID, ""+ac.getId()));


			int idLista = Liste.ACCORDI_COOP_ALLEGATI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, null));
			lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,
					AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST));
			if (search.equals("")) {
				this.pd.setSearchDescription("");
				lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_ALLEGATI_DI  
						+ this.idAccordoCooperazioneFactory.getUriFromAccordo(ac), null));
			} else {
				lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_ALLEGATI_DI  
						+ this.idAccordoCooperazioneFactory.getUriFromAccordo(ac),
						AccordiCooperazioneCostanti.SERVLET_NAME_AC_ALLEGATI_LIST, 
						new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID, ac.getId()+ "")
						));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_RISULTATI_RICERCA, null));
			}

			ServletUtils.setPageDataTitle(this.pd, lstParam);

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				this.pd.setSearch("on");
				this.pd.setSearchDescription("Allegati contenenti la stringa '" + search + "'");
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
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<Documento> it = lista.iterator();
				while (it.hasNext()) {
					Documento doc = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					DataElement de = new DataElement();
					de.setUrl(
							AccordiCooperazioneCostanti.SERVLET_NAME_AC_ALLEGATI_CHANGE,
							new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID_ALLEGATO,doc.getId()+""),
							new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID_ACCORDO,ac.getId()+""),
							new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME_DOCUMENTO,doc.getFile())
							
						 
							);
					de.setValue(doc.getFile());
					de.setIdToRemove(""+doc.getId());
					e.addElement(de);

					de = new DataElement();
					de.setValue(doc.getRuolo());
					e.addElement(de);

					de = new DataElement();
					de.setValue(doc.getTipo());
					e.addElement(de);

					de = new DataElement();
					de.setUrl(
							AccordiCooperazioneCostanti.SERVLET_NAME_AC_ALLEGATI_VIEW,

							new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID_ALLEGATO,doc.getId()+""),
							new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID_ACCORDO,ac.getId()+""),
							new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME_DOCUMENTO,doc.getFile())
							);
					ServletUtils.setDataElementVisualizzaLabel(de);
					e.addElement(de);

					dati.addElement(e);
				}
			}

			this.pd.setDati(dati);

			if(this.core.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(ac.getStatoPackage())){
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

	public Vector<DataElement>  addAllegatiToDati(TipoOperazione tipoOp, String idAllegato, String idAccordo,
			Documento doc, StringBuffer contenutoAllegato, String errore,
			Vector<DataElement> dati, String statoPackage, boolean editMode) {
		DataElement de = new DataElement();

		de.setValue(idAllegato);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID_ALLEGATO);
		dati.addElement(de);

		de = new DataElement();
		de.setValue(idAccordo);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID_ACCORDO);
		dati.addElement(de);

		de = new DataElement();
		de.setValue(doc.getRuolo());
		de.setLabel("Ruolo");
		de.setType(DataElementType.TEXT);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_RUOLO);
		de.setSize(getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setValue(doc.getFile());
		de.setLabel("Nome");
		de.setType(DataElementType.TEXT);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME_DOCUMENTO);
		de.setSize(getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setValue(doc.getTipo());
		de.setLabel("Tipo");
		de.setType(DataElementType.TEXT);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_TIPO_FILE);
		de.setSize(getSize());
		dati.addElement(de);

		if(tipoOp.equals(TipoOperazione.OTHER)){
			if(errore!=null){
				de = new DataElement();
				de.setValue(errore);
				de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_DOCUMENTO);
				de.setType(DataElementType.TEXT);
				de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_DOCUMENTO  );
				de.setSize( getSize());
				dati.addElement(de);
			}
			else{
				de = new DataElement();
				de.setLabel("");//AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_DOCUMENTO);
				de.setType(DataElementType.TEXT_AREA_NO_EDIT);
				de.setValue(contenutoAllegato.toString());
				de.setRows(30);
				de.setCols(80);
				dati.addElement(de);
			}
			
			DataElement saveAs = new DataElement();
			saveAs.setValue(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_DOWNLOAD);
			saveAs.setType(DataElementType.LINK);
			Parameter pIdAccordo = new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID_ACCORDO, idAccordo);
			Parameter pIdAllegato = new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID_ALLEGATO, idAllegato);
			Parameter pTipoDoc = new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_TIPO_DOCUMENTO, "ac");
			//			String params = "idAccordo="+idServizio+"&idAllegato="+idAllegato+"&tipoDocumento=asps";
			saveAs.setUrl(ArchiviCostanti.SERVLET_NAME_DOCUMENTI_EXPORT, pIdAccordo, pIdAllegato, pTipoDoc);
			dati.add(saveAs);
		}

		if(tipoOp.equals(TipoOperazione.CHANGE)){
			if(editMode){
				if(this.acCore.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(statoPackage)){
					this.pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
				}
				else{	
					de = new DataElement();
					de.setType(DataElementType.FILE);
					de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_DOCUMENTO);
					de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_THE_FILE);
					de.setSize(getSize());
					dati.addElement(de);
				}
			}else{
				de = new DataElement();
				de.setType(DataElementType.FILE);
				de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_DOCUMENTO);
				de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_THE_FILE);
				de.setSize(getSize());
				dati.addElement(de);
			}
		}

		return dati;
	}

	public Vector<DataElement> addAllegatoToDati(TipoOperazione tipoOp, String idAccordo, String ruolo,
			String[] ruoli, String[] tipiAmmessi, String[] tipiAmmessiLabel,
			Vector<DataElement> dati) {
		DataElement de = new DataElement();

		de.setValue(idAccordo);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_RUOLO);
		de.setType(DataElementType.SELECT);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_RUOLO);
		de.setValues(ruoli);
		//				de.setOnChange("CambiaTipoDocumento('accordiCoopAllegati')");
		de.setPostBack(true);
		de.setSelected(ruolo!=null ? ruolo : "");
		de.setSize( getSize());
		dati.addElement(de);

		if(tipiAmmessi!=null){
			de = new DataElement();
			de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_TIPO);
			de.setType(DataElementType.SELECT);
			de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_TIPO_FILE);
			de.setValues(tipiAmmessi);
			de.setLabels(tipiAmmessiLabel);
			de.setSize( getSize());
			dati.addElement(de);
		}

		de = new DataElement();
		de.setValue(idAccordo);
		de.setLabel(AccordiCooperazioneCostanti.LABEL_PARAMETRO_ACCORDI_COOPERAZIONE_DOCUMENTO);
		de.setType(DataElementType.FILE);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_THE_FILE);
		de.setSize( getSize());
		dati.addElement(de);

		return dati;
	}

	public Vector<DataElement> addPartecipanteToDati(TipoOperazione tipoOp, String id,
			String[] partecipantiNonInseriti, Vector<DataElement> dati) {
		DataElement de = new DataElement();

		de.setValue(id);
		de.setType(DataElementType.HIDDEN);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_PARTECIPANTE);
		de.setValues(partecipantiNonInseriti );
		de.setType(DataElementType.SELECT);
		de.setName(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PARTECIPANTE);
		de.setSize(getSize());
		dati.addElement(de);

		return dati;
	}

}
