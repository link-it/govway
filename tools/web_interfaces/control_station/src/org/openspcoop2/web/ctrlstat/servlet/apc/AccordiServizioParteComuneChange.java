/*
 * OpenSPCoop - Customizable API Gateway 
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


package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataServizioIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataSoggettoErogatoreIdentificazione;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.driver.BeanUtilities;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.dao.Ruolo;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.ac.AccordiCooperazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * accordiChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComuneChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);

		IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();

		AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);

		String id = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
		int idAcc = 0;
		try {
			idAcc = Integer.parseInt(id);
		} catch (Exception e) {
		}
		String descr = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_DESCRIZIONE);
		String profcoll = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROFILO_COLLABORAZIONE);
		String filtrodup = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_FILTRO_DUPLICATI);
		String confric = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_CONFERMA_RICEZIONE);
		String idcoll = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_COLLABORAZIONE);
		String consord = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_CONSEGNA_ORDINE);
		String scadenza = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SCADENZA);
		String utilizzo = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_UTILIZZO_SENZA_AZIONE);
		boolean utilizzoSenzaAzione = ServletUtils.isCheckBoxEnabled(utilizzo);
		boolean showUtilizzoSenzaAzione = false;
		String oldProfiloCollaborazione = "";
		String referente = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_REFERENTE);
		String versione = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_VERSIONE);
		// patch per version spinner fino a che non si trova un modo piu' elegante
		/*if(ch.core.isBackwardCompatibilityAccordo11()){
			if("0".equals(versione))
				versione = "";
		}*/
		String privatoS = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PRIVATO);
		boolean privato = ServletUtils.isCheckBoxEnabled(privatoS);
		String isServizioCompostoS = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_IS_SERVIZIO_COMPOSTO);
		boolean isServizioComposto = ServletUtils.isCheckBoxEnabled(isServizioCompostoS);
		String accordoCooperazioneId = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ACCORDO_COOPERAZIONE);
		String statoPackage = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_STATO_PACKAGE);
		String tipoAccordo = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
		String tipoProtocollo = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROTOCOLLO);
		String actionConfirm = request.getParameter(Costanti.PARAMETRO_ACTION_CONFIRM);
		String backToStato = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RIPRISTINA_STATO);

		if("".equals(tipoAccordo))
			tipoAccordo = null;
		if(tipoAccordo!=null){
			if(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE.equals(tipoAccordo)){
				isServizioComposto = false;
			}else if(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO.equals(tipoAccordo)){
				isServizioComposto = true;
			}
		}
		boolean validazioneDocumenti = true;
		String tmpValidazioneDocumenti = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_VALIDAZIONE_DOCUMENTI);

		if(ServletUtils.isEditModeInProgress(request) ){

			// primo accesso alla servlet

			if(tmpValidazioneDocumenti!=null){
				validazioneDocumenti = ServletUtils.isCheckBoxEnabled(tmpValidazioneDocumenti);
			}else{
				validazioneDocumenti = true;
			}
		}else{
			validazioneDocumenti = ServletUtils.isCheckBoxEnabled(tmpValidazioneDocumenti);
		}

		// Preparo il menu
		apcHelper.makeMenu();

		// Prendo il nome dell'accordo
		String nome = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME);

		AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
		AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(apcCore);
		SoggettiCore soggettiCore = new SoggettiCore(apcCore);
		PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apcCore);
		PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apcCore);
		AccordiCooperazioneCore acCore = new AccordiCooperazioneCore(apcCore);
		String labelAccordoServizio = AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo);

		Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

		AccordoServizioParteComune as = apcCore.getAccordoServizio(idAcc);
		boolean asWithAllegati = (as.sizeAllegatoList()>0 || as.sizeSpecificaSemiformaleList()>0 || as.getByteWsdlDefinitorio()!=null);

		String[] providersList = null;
		String[] providersListLabel = null;
		String[] accordiCooperazioneEsistenti=null;
		String[] accordiCooperazioneEsistentiLabel=null;
		List<String> listaTipiProtocollo = null;

		boolean used = false;


		IDAccordo idAccordoOLD = idAccordoFactory.getIDAccordoFromValues(as.getNome(),BeanUtilities.getSoggettoReferenteID(as.getSoggettoReferente()),as.getVersione());

		try {

			// controllo se l'accordo e' utilizzato da qualche asps
			List<AccordoServizioParteSpecifica> asps = apsCore.serviziByAccordoFilterList(idAccordoOLD);
			used = asps != null && asps.size() > 0;

			// lista dei protocolli supportati
			listaTipiProtocollo = apcCore.getProtocolli();

			// se il protocollo e' null (primo accesso ) lo ricavo dall'accordo di servizio
			if(tipoProtocollo == null){
				if(as!=null && as.getSoggettoReferente()!=null){
					tipoProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(as.getSoggettoReferente().getTipo());
				}
				else{
					tipoProtocollo = apsCore.getProtocolloDefault();
				}
			}

			List<String> tipiSoggettiGestitiProtocollo = soggettiCore.getTipiSoggettiGestitiProtocollo(tipoProtocollo);
			List<Soggetto> listaSoggetti=null;

			if(apcCore.isVisioneOggettiGlobale(userLogin)){
				listaSoggetti = soggettiCore.soggettiList(null, new Search(true));
			}else{
				listaSoggetti = soggettiCore.soggettiList(userLogin, new Search(true));
			}

			List<String> soggettiListTmp = new ArrayList<String>();
			List<String> soggettiListLabelTmp = new ArrayList<String>();
			soggettiListTmp.add("-");
			soggettiListLabelTmp.add("-");

			if (listaSoggetti.size() > 0) {
				for (Soggetto soggetto : listaSoggetti) {
					if(tipiSoggettiGestitiProtocollo.contains(soggetto.getTipo())){
						soggettiListTmp.add(soggetto.getId().toString());
						soggettiListLabelTmp.add(soggetto.getTipo() + "/" + soggetto.getNome());
					}
				}
			}
			providersList = soggettiListTmp.toArray(new String[1]);
			providersListLabel = soggettiListLabelTmp.toArray(new String[1]);

			List<AccordoCooperazione> lista = null;
			if(apcCore.isVisioneOggettiGlobale(userLogin)){
				lista = acCore.accordiCooperazioneList(null, new Search(true));
			}else{
				lista = acCore.accordiCooperazioneList(userLogin, new Search(true));
			}
			if (lista != null && lista.size() > 0) {
				accordiCooperazioneEsistenti = new String[lista.size()+1];
				accordiCooperazioneEsistentiLabel = new String[lista.size()+1];
				int i = 1;
				accordiCooperazioneEsistenti[0]="-";
				accordiCooperazioneEsistentiLabel[0]="-";
				Iterator<AccordoCooperazione> itL = lista.iterator();
				while (itL.hasNext()) {
					AccordoCooperazione singleAC = itL.next();
					accordiCooperazioneEsistenti[i] = "" + singleAC.getId();
					accordiCooperazioneEsistentiLabel[i] = idAccordoCooperazioneFactory.getUriFromAccordo(acCore.getAccordoCooperazione(singleAC.getId()));
					i++;
				}
			} else {
				accordiCooperazioneEsistenti = new String[1];
				accordiCooperazioneEsistentiLabel = new String[1];
				accordiCooperazioneEsistenti[0]="-";
				accordiCooperazioneEsistentiLabel[0]="-";
			}

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
		}

		//se passo dal link diretto di ripristino stato imposto il nuovo stato
		if(backToStato != null)
			statoPackage = backToStato;

		String uriAS = idAccordoFactory.getUriFromIDAccordo(idAccordoOLD);
		String oldStatoPackage = as.getStatoPackage();			

		// Se idhid = null, devo visualizzare la pagina per la modifica dati
		if(ServletUtils.isEditModeInProgress(request)){

			try {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, labelAccordoServizio,
						AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
								AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
								AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue(),
								uriAS);

				if(descr==null){
					//inizializzazione default
					descr = as.getDescrizione();

					if(tipoAccordo!=null){
						if("apc".equals(tipoAccordo)){
							isServizioComposto = false;
						}else if("asc".equals(tipoAccordo)){
							isServizioComposto = true;
						}
					}else{
						isServizioComposto = as.getServizioComposto()!=null ? true : false;
					}
					if(isServizioComposto){
						accordoCooperazioneId = ""+as.getServizioComposto().getIdAccordoCooperazione();
					}else{
						accordoCooperazioneId="-";
					}
				}

				int idReferente = -1;
				try{
					idReferente = Integer.parseInt(referente);
				}catch(Exception e){}
				if(idReferente<=0 && !"-".equals(referente)){
					IdSoggetto assr = as.getSoggettoReferente();
					if (assr != null) {
						Soggetto s = soggettiCore.getSoggetto(new IDSoggetto(assr.getTipo(),assr.getNome()));
						referente = "" + s.getId();
					}else{
						referente = "-";
					}
				}

				if(versione == null)
					versione = as.getVersione();

				// controllo profilo collaborazione
				if(profcoll == null)
					profcoll = AccordiServizioParteComuneHelper.convertProfiloCollaborazioneDB2View(as.getProfiloCollaborazione());

				if(filtrodup == null)
					filtrodup = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getFiltroDuplicati());
				if(confric == null)
					confric = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConfermaRicezione());
				if(idcoll == null)
					idcoll = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdCollaborazione());
				if(consord == null)
					consord = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConsegnaInOrdine());
				if(scadenza == null)
					scadenza = as.getScadenza() != null ? as.getScadenza() : "";
					if(request.getParameter("privato")==null){
						privato = as.getPrivato()!=null && as.getPrivato();
					}

					showUtilizzoSenzaAzione = as.sizeAzioneList() > 0;// se ci
					// sono
					// azioni
					// allora
					// visualizzo
					// il
					// checkbox
					if(utilizzo==null)
						utilizzoSenzaAzione = as.getUtilizzoSenzaAzione();	

					if(statoPackage==null)
						statoPackage = as.getStatoPackage();

			} catch (Exception ex) {
				return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), ex, pd, session, gd, mapping, 
						AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
			}


			if( backToStato == null){
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = apcHelper.addAccordiToDati(dati, nome, descr, profcoll, "", "", "", "", 
						filtrodup, confric, idcoll, consord, scadenza, id, TipoOperazione.CHANGE, 
						showUtilizzoSenzaAzione, utilizzoSenzaAzione,referente,versione,providersList,providersListLabel,
						privato,isServizioComposto,accordiCooperazioneEsistenti,accordiCooperazioneEsistentiLabel,
						accordoCooperazioneId,statoPackage,oldStatoPackage, tipoAccordo, validazioneDocumenti,
						tipoProtocollo, listaTipiProtocollo,used,asWithAllegati);

				pd.setDati(dati);

				if(apcCore.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(as.getStatoPackage())){
					pd.disableEditMode();
				}

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);


				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
			}
		}

		boolean visibilitaAccordoCooperazione=false;
		if("-".equals(accordoCooperazioneId)==false && "".equals(accordoCooperazioneId)==false  && accordoCooperazioneId!=null){
			AccordoCooperazione ac = acCore.getAccordoCooperazione(Long.parseLong(accordoCooperazioneId));
			visibilitaAccordoCooperazione=ac.getPrivato()!=null && ac.getPrivato();
		}

		// Controlli sui campi immessi
		boolean isOk = apcHelper.accordiCheckData(TipoOperazione.CHANGE, nome, descr, profcoll, 
				"", "", "", "", 
				filtrodup, confric, idcoll, consord, 
				scadenza, id,referente,versione,accordoCooperazioneId,privato,visibilitaAccordoCooperazione,idAccordoOLD, 
				"", "", "", validazioneDocumenti,tipoProtocollo,backToStato);
		if (!isOk) {

			// setto la barra del titolo
			ServletUtils.setPageDataTitle_ServletChange(pd, labelAccordoServizio,
					AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue(),
							uriAS);

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			dati.addElement(ServletUtils.getDataElementForEditModeFinished());

			dati = apcHelper.addAccordiToDati(dati, nome, descr, profcoll, "", "", "", "", 
					filtrodup, confric, idcoll, consord, scadenza, id, TipoOperazione.CHANGE, 
					showUtilizzoSenzaAzione, utilizzoSenzaAzione,referente,versione,providersList,providersListLabel,
					privato,isServizioComposto,accordiCooperazioneEsistenti,accordiCooperazioneEsistentiLabel,
					accordoCooperazioneId,statoPackage,oldStatoPackage, tipoAccordo, validazioneDocumenti,
					tipoProtocollo, listaTipiProtocollo,used,asWithAllegati);

			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
		}

		// I dati dell'utente sono validi, lo informo che l'accordo e' utilizzato da asps 
		if( actionConfirm == null){
			if(used || backToStato != null){
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, labelAccordoServizio,
						AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
								AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
								AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue(),
								uriAS);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeInProgress());

				dati = apcHelper.addAccordiToDatiAsHidden(dati, nome, descr, profcoll, "", "", "", "", 
						filtrodup, confric, idcoll, consord, scadenza, id, TipoOperazione.CHANGE, 
						showUtilizzoSenzaAzione, utilizzoSenzaAzione,referente,versione,providersList,providersListLabel,
						privato,isServizioComposto,accordiCooperazioneEsistenti,accordiCooperazioneEsistentiLabel,
						accordoCooperazioneId,statoPackage,oldStatoPackage, tipoAccordo, validazioneDocumenti,tipoProtocollo, listaTipiProtocollo,used);

				pd.setDati(dati);

				String uriAccordo = idAccordoFactory.getUriFromIDAccordo(idAccordoOLD);
				String msg = ""; 
				if(used)
					msg = "Attenzione, esistono delle erogazioni che riferiscono l''accordo [{0}] che si sta modificando, continuare?";

				if(backToStato != null){
					msg = "&Egrave; stato richiesto di ripristinare lo stato dell''accordo [{0}] in operativo. Tale operazione permetter&agrave; successive modifiche all''accordo. Vuoi procedere?";
				}

				pd.setMessage(MessageFormat.format(msg, uriAccordo));

				String[][] bottoni = { 
						{ Costanti.LABEL_MONITOR_BUTTON_ANNULLA, 
							Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_PREFIX +
							Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_SUFFIX

						},
						{ Costanti.LABEL_MONITOR_BUTTON_OK,
							Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_PREFIX +
							Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_SUFFIX }};

				pd.setBottoni(bottoni );

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeConfirm(mapping, 
						AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());	
			}
		}

		oldProfiloCollaborazione = AccordiServizioParteComuneHelper.convertProfiloCollaborazioneDB2View(as.getProfiloCollaborazione());

		as.setNome(nome);
		as.setDescrizione(descr);
		as.setConfermaRicezione(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(confric)));
		as.setConsegnaInOrdine(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(consord)));
		as.setFiltroDuplicati(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(filtrodup)));
		as.setIdCollaborazione(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(idcoll)));
		as.setProfiloCollaborazione(ProfiloCollaborazione.toEnumConstant(AccordiServizioParteComuneHelper.convertProfiloCollaborazioneView2DB(profcoll)));
		as.setScadenza(scadenza);
		as.setUtilizzoSenzaAzione(as.sizeAzioneList() > 0 ? utilizzoSenzaAzione : true);
		if(referente!=null && !"".equals(referente) && !"-".equals(referente)){
			int idRef = 0;
			try {
				idRef = Integer.parseInt(referente);
			} catch (Exception e) {
			}
			if (idRef != 0) {
				int idReferente = Integer.parseInt(referente);
				Soggetto s = soggettiCore.getSoggetto(idReferente);			
				IdSoggetto assr = new IdSoggetto();
				assr.setTipo(s.getTipo());
				assr.setNome(s.getNome());
				as.setSoggettoReferente(assr);
			}
		}else{
			as.setSoggettoReferente(null);
		}
		as.setVersione(versione);
		as.setPrivato(privato ? Boolean.TRUE : Boolean.FALSE);

		if(accordoCooperazioneId!=null && !"".equals(accordoCooperazioneId) && !"-".equals(accordoCooperazioneId)){
			AccordoServizioParteComuneServizioComposto assc = as.getServizioComposto();
			if(assc==null) assc = new AccordoServizioParteComuneServizioComposto();
			assc.setIdAccordoCooperazione(Long.parseLong(accordoCooperazioneId));
			as.setServizioComposto(assc);
		}else{
			as.setServizioComposto(null);
		}

		as.setOldIDAccordoForUpdate(idAccordoOLD);



		// stato
		as.setStatoPackage(statoPackage);



		// Check stato
		if(apcCore.isShowGestioneWorkflowStatoDocumenti()){

			try{
				boolean utilizzoAzioniDiretteInAccordoAbilitato = apcCore.isShowAccordiColonnaAzioni();
				apcCore.validaStatoAccordoServizio(as, utilizzoAzioniDiretteInAccordoAbilitato);
			}catch(ValidazioneStatoPackageException validazioneException){

				// Setto messaggio di errore
				pd.setMessage(validazioneException.toString());

				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, labelAccordoServizio,
						AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
								AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
								AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue(),
								uriAS);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = apcHelper.addAccordiToDati(dati, nome, descr, profcoll, "", "", "", "", 
						filtrodup, confric, idcoll, consord, scadenza, id, TipoOperazione.CHANGE, 
						showUtilizzoSenzaAzione, utilizzoSenzaAzione,referente,versione,providersList,providersListLabel,
						privato,isServizioComposto,accordiCooperazioneEsistenti,accordiCooperazioneEsistentiLabel,
						accordoCooperazioneId,statoPackage,oldStatoPackage, tipoAccordo, validazioneDocumenti,
						tipoProtocollo, listaTipiProtocollo,used,asWithAllegati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
			}
		}





		// Modifico i dati dell'accordo nel db
		try {

			// Controllo su cambio del profilo di collaborazione.
			/*
			 * Nel caso in cui si voglia cambiare il profilo di collaborazione
			 * dell'accordo in uno diverso da oneway bisogna controllare che non
			 * esistano Porte Applicative, associate ai Servizi che implementano
			 * l'Accordo, con piu di un Servizio Applicativo associato in questo
			 * caso e' possibile cambiare il profilo da oneway in un altro
			 * 
			 * Nota: Se un Accordo contiene azioni allora bisogna tener conto
			 * che l'azione puo aver ridefinito il profilo e quindi vanno prese
			 * le Porte Applicative associate ai Servizi che implementano
			 * l'Accordo il quale sia senza azioni oppure con azioni con profilo
			 * di default
			 */
			String newProfiloCollaborazione = AccordiServizioParteComuneHelper.convertProfiloCollaborazioneDB2View(as.getProfiloCollaborazione());
			// controllo se profilo e' cambiato e se e' cambiato da oneway ad
			// altro devo effettuare i controlli
			if (!oldProfiloCollaborazione.equals(newProfiloCollaborazione) && oldProfiloCollaborazione.equals(CostantiRegistroServizi.ONEWAY)) {

				ArrayList<String> nomiAzioniDaEscludere = new ArrayList<String>();
				// imposto le azioni per il filtro
				for (int i = 0; i < as.sizeAzioneList(); i++) {
					Azione azione = as.getAzione(i);
					String profiloAzione = azione.getProfAzione();
					// se il profilo dell'azione e' ridefinito allora dovro
					// escludere le porte delegate che hanno questa azione
					if (profiloAzione != null && profiloAzione.equals(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO)) {
						nomiAzioniDaEscludere.add(azione.getNome());
					}

				}

				// recupero i servizi che implementano questo accordo
				List<AccordoServizioParteSpecifica> listaServizi = apsCore.serviziWithIdAccordoList(as.getId());
				// per ogni servizio trovato prendo le porte applicative
				for (AccordoServizioParteSpecifica servizio : listaServizi) {
					List<PortaApplicativa> listPA = porteApplicativeCore.porteAppWithIdServizio(servizio.getId());
					// per ogni porta applicativa controllo se ha piu di un
					// servizio applicativo associato
					for (PortaApplicativa portaApplicativa : listPA) {

						// controllo se escludere o no il controllo di questa
						// porta applicativa in base
						// al nome dell'azione
						PortaApplicativaAzione azione = portaApplicativa.getAzione();
						String nomeAzione = azione != null ? azione.getNome() : null;
						// se il nome e' presente tra quelli da escludere
						// allora salto il controllo di questa porta
						if (nomiAzioniDaEscludere.contains(nomeAzione))
							continue;

						// nessuna esclusione allora controllo se ha piu di un
						// servizio applicativo
						if (portaApplicativa.sizeServizioApplicativoList() > 1) {
							// trovata porta applicativa con piu di un servizio
							// applicativo associato
							String msg = "Impossibile cambiare il profilo di collaborazione da {0} a {1} " + "in quanto esiste almeno una Porta Applicativa [" + portaApplicativa.getNome() + "] con piu' di un Servizio Applicativo associato.";
							pd.setMessage(MessageFormat.format(msg, oldProfiloCollaborazione, newProfiloCollaborazione));

							// setto la barra del titolo
							ServletUtils.setPageDataTitle_ServletChange(pd, labelAccordoServizio,
									AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
											AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
											AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue(),
											uriAS);

							// preparo i campi
							Vector<DataElement> dati = new Vector<DataElement>();

							dati.addElement(ServletUtils.getDataElementForEditModeFinished());

							dati = apcHelper.addAccordiToDati(dati, nome, descr, profcoll, "", "", "", "", 
									filtrodup, confric, idcoll, consord, scadenza, id, TipoOperazione.CHANGE, 
									showUtilizzoSenzaAzione, utilizzoSenzaAzione,referente,versione,providersList,providersListLabel,
									privato,isServizioComposto,accordiCooperazioneEsistenti,accordiCooperazioneEsistentiLabel,
									accordoCooperazioneId,statoPackage,oldStatoPackage, tipoAccordo, validazioneDocumenti,
									tipoProtocollo, listaTipiProtocollo,used,asWithAllegati);

							pd.setDati(dati);

							ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

							return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
						}
					}

				}

			}

			Object[] operazioniDaEffettuare = new Object[1]; // almeno 1, l'accordo esiste!
			Vector<Object> operazioniList = new Vector<Object>();
			operazioniList.add(as);


			IDAccordo idNEW = idAccordoFactory.getIDAccordoFromAccordo(as);
			if(idNEW.equals(idAccordoOLD)==false){

				String newURI = idAccordoFactory.getUriFromAccordo(as);

				// Cerco i servizi in cui devo cambiare la URI dell'accordo
				List<AccordoServizioParteSpecifica> servizi = apsCore.serviziByAccordoFilterList(idAccordoOLD);
				if(servizi!=null && servizi.size()>0){
					while(servizi.size()>0){
						AccordoServizioParteSpecifica s = servizi.remove(0);
						s.setAccordoServizioParteComune(newURI);
						operazioniList.add(s);
					}
				}

				// Cerco le porte delegate create automaticamente per il ruolo associato ad un servizio applicativo
				List<PortaDelegata> porteDelegate = porteDelegateCore.getPorteDelegateByAccordoRuolo(idAccordoOLD);
				if(porteDelegate!=null && porteDelegate.size()>0){

					IDAccordo idAccordoCorrelatoOLD = idAccordoFactory.getIDAccordoFromValues(idAccordoOLD.getNome()+"Correlato", idAccordoOLD.getSoggettoReferente(), idAccordoOLD.getVersione());
					IDAccordo idAccordoCorrelatoNEW = idAccordoFactory.getIDAccordoFromValues(idNEW.getNome()+"Correlato", idNEW.getSoggettoReferente(), idNEW.getVersione());

					while(porteDelegate.size()>0){
						PortaDelegata portaDelegata = porteDelegate.remove(0);

						String oldName = portaDelegata.getNome();
						boolean changeLocationOnly = false;
						if(portaDelegata.getLocation()!=null && (portaDelegata.getLocation().equals(oldName)==false) ){
							changeLocationOnly = true;
							oldName = portaDelegata.getLocation();
						}

						String newName = null;
						String nomeRuoloNEW = null;
						String nomeRuoloOLD = null;
						String correlatoOLD = Ruolo.getNomeRuoloByIDAccordo(idAccordoCorrelatoOLD);
						String vecchioOLD = Ruolo.getNomeRuoloByIDAccordo(idAccordoOLD);
						if( oldName.indexOf(correlatoOLD) >= 0 ){
							// Il ruolo e' Correlato
							nomeRuoloNEW = Ruolo.getNomeRuoloByIDAccordo(idAccordoCorrelatoNEW);
							nomeRuoloOLD = correlatoOLD;
						}else if( oldName.indexOf(vecchioOLD) >= 0){
							// Il ruolo non e' Correlato
							nomeRuoloNEW = Ruolo.getNomeRuoloByIDAccordo(idNEW);
							nomeRuoloOLD = vecchioOLD;
						}else{
							// porta delegata da non considerare. Non dovrei mai entrare in questo caso
							continue;
						}

						// nome e location PD
						portaDelegata.setOldNomeForUpdate(portaDelegata.getNome());
						newName = oldName.replace(nomeRuoloOLD, nomeRuoloNEW);
						if(changeLocationOnly==false)
							portaDelegata.setNome(newName);
						portaDelegata.setLocation(newName);

						// descrizione
						if(portaDelegata.getDescrizione()!=null){
							portaDelegata.setDescrizione(portaDelegata.getDescrizione().replace(nomeRuoloOLD, nomeRuoloNEW));
						}

						// Soggetto erogatore
						if(portaDelegata.getSoggettoErogatore()!=null && 
								portaDelegata.getSoggettoErogatore().getPattern()!=null &&
								PortaDelegataSoggettoErogatoreIdentificazione.URL_BASED.equals(portaDelegata.getSoggettoErogatore().getIdentificazione()) ){
							portaDelegata.getSoggettoErogatore().setPattern(portaDelegata.getSoggettoErogatore().getPattern().replace(nomeRuoloOLD, nomeRuoloNEW));
						}else{
							// porta delegata da non considerare. La porta delegata non rispetti i criteri automatici di creazione.
							continue;
						}

						// Servizio
						if(portaDelegata.getServizio()!=null && 
								portaDelegata.getServizio().getNome()!=null &&
								PortaDelegataServizioIdentificazione.STATIC.equals(portaDelegata.getServizio().getIdentificazione()) ){
							portaDelegata.getServizio().setNome(portaDelegata.getServizio().getNome().replace(nomeRuoloOLD, nomeRuoloNEW));
						}else{
							// porta delegata da non considerare. La porta delegata non rispetti i criteri automatici di creazione.
							continue;
						}

						// Azione
						if(portaDelegata.getAzione()!=null && 
								portaDelegata.getAzione().getPattern()!=null &&
								PortaDelegataAzioneIdentificazione.URL_BASED.equals(portaDelegata.getAzione().getIdentificazione()) ){
							portaDelegata.getAzione().setPattern(portaDelegata.getAzione().getPattern().replace(nomeRuoloOLD, nomeRuoloNEW));
						}else{
							// porta delegata da non considerare. La porta delegata non rispetti i criteri automatici di creazione.
							continue;
						}

						operazioniList.add(portaDelegata);

					}
				}
			}


			operazioniDaEffettuare = operazioniList.toArray(operazioniDaEffettuare);
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), operazioniDaEffettuare);

			// preparo lista
			List<AccordoServizioParteComune> lista = AccordiServizioParteComuneUtilities.accordiList(apcCore, userLogin, ricerca, tipoAccordo);
			//			if(apcCore.isVisioneOggettiGlobale(userLogin)){
			//				lista = apcCore.accordiServizioParteComuneList(null, ricerca);
			//			}else{
			//				lista = apcCore.accordiServizioParteComuneList(userLogin, ricerca);
			//			}
			apcHelper.prepareAccordiList(lista, ricerca, tipoAccordo);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());	

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.CHANGE());
		} 

	}
}
