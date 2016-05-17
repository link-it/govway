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


package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.ac.AccordiCooperazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * accordiAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComuneAdd extends Action {

	private String nome, descr, profcoll, wsdldef, wsdlconc, wsdlserv,
	wsdlservcorr, filtrodup, confric, idcoll, consord, scadenza,
	referente,versione,accordoCooperazione,
	wsblconc, wsblserv, wsblservcorr;
	private boolean privato, isServizioComposto;// showPrivato;
	private String statoPackage;
	private String tipoAccordo;
	private boolean validazioneDocumenti = true;
	private boolean decodeRequestValidazioneDocumenti = false;
	private String editMode = null;
	private String tipoProtocollo;

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

		try {

			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);

			this.editMode = null;

			this.nome = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME);
			this.descr = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_DESCRIZIONE);
			this.profcoll = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROFILO_COLLABORAZIONE);
			this.wsdldef = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_DEFINITORIO);
			this.wsdlconc = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CONCETTUALE);
			this.wsdlserv = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE);
			this.wsdlservcorr = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_FRUITORE);

			this.wsblconc = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE);
			this.wsblserv = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE);
			this.wsblservcorr = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE);

			this.filtrodup = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_FILTRO_DUPLICATI);
			this.confric = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_CONFERMA_RICEZIONE);
			this.idcoll = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_COLLABORAZIONE);
			this.consord = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_CONSEGNA_ORDINE);
			this.scadenza = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SCADENZA);
			this.referente = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_REFERENTE);
			this.versione = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_VERSIONE);

			this.tipoProtocollo = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROTOCOLLO);
			// patch per version spinner fino a che non si trova un modo piu' elegante
			/*if(ch.core.isBackwardCompatibilityAccordo11()){
				if("0".equals(this.versione))
					this.versione = "";
			}*/
			String priv = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PRIVATO);
			this.privato = ServletUtils.isCheckBoxEnabled(priv);

			String isServComp = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_IS_SERVIZIO_COMPOSTO);
			this.isServizioComposto = ServletUtils.isCheckBoxEnabled(isServComp);

			this.accordoCooperazione = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ACCORDO_COOPERAZIONE);
			this.statoPackage = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_STATO_PACKAGE);

			this.tipoAccordo = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(this.tipoAccordo))
				this.tipoAccordo = null;
			if(this.tipoAccordo!=null){
				if(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE.equals(this.tipoAccordo)){
					this.isServizioComposto = false;
				}else if(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO.equals(this.tipoAccordo)){
					this.isServizioComposto = true;
				}
			}

			// String utilizzo=request.getParameter(AccordiServizioParteComuneCostanti."utilizzoSenzaAzione");
			// boolean utilizzoSenzaAzione = true;// utilizzo!=null &&
			// utilizzo.equals("yes") ? true :
			// false;

			// boolean decodeReq = false;
			String ct = request.getContentType();
			if ((ct != null) && (ct.indexOf(Costanti.MULTIPART) != -1)) {
				// decodeReq = true;
				//this.decodeRequest(request,ch.core.isBackwardCompatibilityAccordo11());
				this.decodeRequestValidazioneDocumenti = false; // init
				this.decodeRequest(request,false);
			}

			if(ServletUtils.isEditModeInProgress(this.editMode) && ServletUtils.isEditModeInProgress(request)){
				// primo accesso alla servlet
				this.validazioneDocumenti = true;
			}else{
				if(!this.decodeRequestValidazioneDocumenti){
					String tmpValidazioneDocumenti = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_VALIDAZIONE_DOCUMENTI);
					this.validazioneDocumenti = ServletUtils.isCheckBoxEnabled(tmpValidazioneDocumenti);
				}
			}


			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			SoggettiCore soggettiCore = new SoggettiCore(apcCore);
			AccordiCooperazioneCore acCore = new AccordiCooperazioneCore(apcCore);
			String labelAccordoServizio = AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(this.tipoAccordo);

			// Flag per controllare il mapping automatico di porttype e operation
			boolean enableAutoMapping = apcCore.isEnableAutoMappingWsdlIntoAccordo();
			boolean enableAutoMapping_estraiXsdSchemiFromWsdlTypes = apcCore.isEnableAutoMappingWsdlIntoAccordo_estrazioneSchemiInWsdlTypes();

			// Tipi protocollo supportati
			List<String> listaTipiProtocollo = apcCore.getProtocolli();
			// primo accesso inizializzo con il protocollo di default
			if(this.tipoProtocollo == null){
				this.tipoProtocollo = apcCore.getProtocolloDefault();
			}

			//Carico la lista dei tipi di soggetti gestiti dal protocollo
			List<String> tipiSoggettiGestitiProtocollo = soggettiCore.getTipiSoggettiGestitiProtocollo(this.tipoProtocollo);

			// Preparo il menu
			apcHelper.makeMenu();

			List<Soggetto> listaSoggetti=null;
			if(apcCore.isVisioneOggettiGlobale(userLogin)){
				listaSoggetti = soggettiCore.soggettiList(null, new Search(true));
			}else{
				listaSoggetti = soggettiCore.soggettiList(userLogin, new Search(true));
			}
			String[] providersList = null;
			String[] providersListLabel = null;

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

			String[] accordiCooperazioneEsistenti=null;
			String[] accordiCooperazioneEsistentiLabel=null;


			String postBackElementName = ServletUtils.getPostBackElementName(request);

			// Controllo se ho modificato il protocollo, resetto il referente
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROTOCOLLO)){
					this.referente = null;
				}
			}

			org.openspcoop2.core.registry.Soggetto soggettoReferente = null;
			try{
				soggettoReferente = soggettiCore.getSoggettoRegistro(Integer.parseInt(this.referente));
			}catch(Exception e){}
			List<String> tipiSoggettiCompatibili = new ArrayList<String>();
			if(soggettoReferente!=null){
				String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(soggettoReferente.getTipo());
				tipiSoggettiCompatibili = soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);
			}

			List<AccordoCooperazione> listaTmp = null;
			if(apcCore.isVisioneOggettiGlobale(userLogin)){
				listaTmp = acCore.accordiCooperazioneList(null, new Search(true));
			}else{
				listaTmp = acCore.accordiCooperazioneList(userLogin, new Search(true));
			}
			List<AccordoCooperazione> listaAccordoCooperazione = new ArrayList<AccordoCooperazione>();
			for (AccordoCooperazione accordoCooperazione : listaTmp) {
				if(accordoCooperazione.getSoggettoReferente()!=null){
					if(tipiSoggettiCompatibili!=null && tipiSoggettiCompatibili.contains(accordoCooperazione.getSoggettoReferente().getTipo())){
						listaAccordoCooperazione.add(accordoCooperazione);
					}
				}
			}
			if (listaAccordoCooperazione != null && listaAccordoCooperazione.size() > 0) {
				accordiCooperazioneEsistenti = new String[listaAccordoCooperazione.size()+1];
				accordiCooperazioneEsistentiLabel = new String[listaAccordoCooperazione.size()+1];
				int i = 1;
				accordiCooperazioneEsistenti[0]="-";
				accordiCooperazioneEsistentiLabel[0]="-";
				Iterator<AccordoCooperazione> itL = listaAccordoCooperazione.iterator();
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

			// Se nome = null, devo visualizzare la pagina per l'inserimento dati
			if(ServletUtils.isEditModeInProgress(this.editMode) && ServletUtils.isEditModeInProgress(request)){

				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletAdd(pd, labelAccordoServizio);

				if(this.nome==null){
					this.nome = "";
					this.accordoCooperazione = "";
					this.confric  = "";
					this.consord = "";
					this.descr = "";
					this.filtrodup = "yes";
					this.idcoll = "";
					this.profcoll = "oneway";
					this.referente= "";
					this.accordoCooperazione = "-1";
					this.scadenza= "";
					this.privato = false;
					this.tipoProtocollo = apcCore.getProtocolloDefault();

					if(this.tipoAccordo!=null){
						if("apc".equals(this.tipoAccordo)){
							this.isServizioComposto = false;
						}else if("asc".equals(this.tipoAccordo)){
							this.isServizioComposto = true;
						}
					}else{
						this.isServizioComposto=false;
					}
					if(apcCore.isShowGestioneWorkflowStatoDocumenti()){
						if(this.statoPackage==null)
							this.statoPackage=StatiAccordo.bozza.toString();
					}else{
						this.statoPackage=StatiAccordo.finale.toString();
					}
					//if(core.isBackwardCompatibilityAccordo11()){
					//	this.versione="0";
					//}else{
					this.versione="1";
					//}
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = apcHelper.addAccordiToDati(dati, this.nome, this.descr, this.profcoll, "", "", "", "", 
						this.filtrodup, this.confric, this.idcoll, this.consord, this.scadenza, "0", TipoOperazione.ADD, 
						false, true, this.referente, this.versione, providersList, providersListLabel, 
						this.privato, this.isServizioComposto, accordiCooperazioneEsistenti, accordiCooperazioneEsistentiLabel, 
						this.accordoCooperazione, this.statoPackage, this.statoPackage, this.tipoAccordo, this.validazioneDocumenti, 
						this.tipoProtocollo, listaTipiProtocollo,false,false);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC,
						ForwardParams.ADD());
			}

			boolean visibilitaAccordoCooperazione=false;
			if("-".equals(this.accordoCooperazione)==false && "".equals(this.accordoCooperazione)==false  && this.accordoCooperazione!=null){
				AccordoCooperazione ac = acCore.getAccordoCooperazione(Long.parseLong(this.accordoCooperazione));
				visibilitaAccordoCooperazione=ac.getPrivato()!=null && ac.getPrivato();
			}

			// Controlli sui campi immessi
			boolean isOk = apcHelper.accordiCheckData(TipoOperazione.ADD, this.nome, this.descr, this.profcoll, 
					this.wsdldef, this.wsdlconc, this.wsdlserv, this.wsdlservcorr, 
					this.filtrodup, this.confric, this.idcoll, this.consord, 
					this.scadenza, "0",this.referente, this.versione,this.accordoCooperazione,this.privato,visibilitaAccordoCooperazione,null,
					this.wsblconc,this.wsblserv,this.wsblservcorr, this.validazioneDocumenti, this.tipoProtocollo,null);
			if (!isOk) {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletAdd(pd, labelAccordoServizio);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = apcHelper.addAccordiToDati(dati, this.nome, this.descr, this.profcoll, this.wsdldef, this.wsdlconc, this.wsdlserv, this.wsdlservcorr, 
						this.filtrodup, this.confric, this.idcoll, this.consord, this.scadenza, "0", TipoOperazione.ADD, 
						false, true,this.referente, this.versione, providersList, providersListLabel,
						this.privato, this.isServizioComposto, accordiCooperazioneEsistenti, accordiCooperazioneEsistentiLabel, 
						this.accordoCooperazione, this.statoPackage, this.statoPackage, this.tipoAccordo, this.validazioneDocumenti, 
						this.tipoProtocollo, listaTipiProtocollo,false,false);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.ADD());
			}

			AccordoServizioParteComune as = new AccordoServizioParteComune();

			// preparo l'oggetto
			as.setNome(this.nome);
			as.setDescrizione(this.descr);

			// profilo collaborazione
			// controllo profilo collaborazione
			this.profcoll = AccordiServizioParteComuneHelper.convertProfiloCollaborazioneView2DB(this.profcoll);
			as.setProfiloCollaborazione(ProfiloCollaborazione.toEnumConstant(this.profcoll));

			as.setByteWsdlConcettuale(this.wsdlconc != null && !this.wsdlconc.trim().replaceAll("\n", "").equals("") ? this.wsdlconc.trim().getBytes() : null);
			as.setByteWsdlDefinitorio(this.wsdldef != null && !this.wsdldef.trim().replaceAll("\n", "").equals("") ? this.wsdldef.trim().getBytes() : null);
			as.setByteWsdlLogicoErogatore(this.wsdlserv != null && !this.wsdlserv.trim().replaceAll("\n", "").equals("") ? this.wsdlserv.trim().getBytes() : null);
			as.setByteWsdlLogicoFruitore(this.wsdlservcorr != null && !this.wsdlservcorr.trim().replaceAll("\n", "").equals("") ? this.wsdlservcorr.trim().getBytes() : null);

			// Se un utente ha impostato solo il logico erogatore (avviene automaticamente nel caso non venga visualizzato il campo concettuale)
			// imposto lo stesso wsdl anche per il concettuale. Tanto Rappresenta la stessa informazione, ma e' utile per lo stato dell'accordo
			boolean facilityUnicoWSDL_interfacciaStandard = false;
			if(as.getByteWsdlLogicoErogatore()!=null && as.getByteWsdlLogicoFruitore()==null && as.getByteWsdlConcettuale()==null){
				as.setByteWsdlConcettuale(as.getByteWsdlLogicoErogatore());
				facilityUnicoWSDL_interfacciaStandard = true;
			}
			
			// Conversione Abilitato/Disabilitato
			as.setFiltroDuplicati(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(this.filtrodup)));
			as.setConfermaRicezione(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(this.confric)));
			as.setConsegnaInOrdine(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(this.consord)));
			as.setIdCollaborazione(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(this.idcoll)));
			if ((this.scadenza != null) && (!"".equals(this.scadenza)))
				as.setScadenza(this.scadenza);
			as.setSuperUser(userLogin);
			as.setUtilizzoSenzaAzione(true);// default true
			as.setByteSpecificaConversazioneConcettuale((this.wsblconc != null) && !this.wsblconc.trim().replaceAll("\n", "").equals("") ? this.wsblconc.trim().getBytes() : null);
			as.setByteSpecificaConversazioneErogatore((this.wsblserv != null) && !this.wsblserv.trim().replaceAll("\n", "").equals("") ? this.wsblserv.trim().getBytes() : null);
			as.setByteSpecificaConversazioneFruitore((this.wsblservcorr != null) && !this.wsblservcorr.trim().replaceAll("\n", "").equals("") ? this.wsblservcorr.trim().getBytes() : null);
			if(this.referente!=null && !"".equals(this.referente) && !"-".equals(this.referente)){
				int idRef = 0;
				try {
					idRef = Integer.parseInt(this.referente);
				} catch (Exception e) {
				}
				if (idRef != 0) {
					int idReferente = Integer.parseInt(this.referente);
					Soggetto s = soggettiCore.getSoggetto(idReferente);			
					IdSoggetto assr = new IdSoggetto();
					assr.setTipo(s.getTipo());
					assr.setNome(s.getNome());
					as.setSoggettoReferente(assr);
				}
			}else{
				as.setSoggettoReferente(null);
			}
			as.setVersione(this.versione);
			as.setPrivato(this.privato ? Boolean.TRUE : Boolean.FALSE);

			if(this.accordoCooperazione!=null && !"".equals(this.accordoCooperazione) && !"-".equals(this.accordoCooperazione)){
				AccordoServizioParteComuneServizioComposto assc = new AccordoServizioParteComuneServizioComposto();
				assc.setIdAccordoCooperazione(Long.parseLong(this.accordoCooperazione));
				as.setServizioComposto(assc);
			}

			// stato
			as.setStatoPackage(this.statoPackage);



			// Check stato
			if(apcCore.isShowGestioneWorkflowStatoDocumenti()){

				try{
					boolean utilizzoAzioniDiretteInAccordoAbilitato = apcCore.isShowAccordiColonnaAzioni();
					apcCore.validaStatoAccordoServizio(as, utilizzoAzioniDiretteInAccordoAbilitato);
				}catch(ValidazioneStatoPackageException validazioneException){

					// Setto messaggio di errore
					pd.setMessage(validazioneException.toString());

					// setto la barra del titolo
					ServletUtils.setPageDataTitle_ServletAdd(pd, labelAccordoServizio);

					// preparo i campi
					Vector<DataElement> dati = new Vector<DataElement>();

					dati.addElement(ServletUtils.getDataElementForEditModeFinished());

					dati = apcHelper.addAccordiToDati(dati, this.nome, this.descr, this.profcoll, this.wsdldef, this.wsdlconc, this.wsdlserv, this.wsdlservcorr, 
							this.filtrodup, this.confric, this.idcoll, this.consord, this.scadenza, "0", TipoOperazione.ADD, 
							false, true,this.referente, this.versione, providersList, providersListLabel,
							this.privato, this.isServizioComposto, accordiCooperazioneEsistenti, accordiCooperazioneEsistentiLabel, 
							this.accordoCooperazione, this.statoPackage, this.statoPackage, this.tipoAccordo, this.validazioneDocumenti, 
							this.tipoProtocollo, listaTipiProtocollo,false,false);

					pd.setDati(dati);

					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.ADD());

				}
			}

			// Automapping
			if(enableAutoMapping){
				if(as.getByteWsdlConcettuale() != null || as.getByteWsdlLogicoErogatore() != null || as.getByteWsdlLogicoFruitore() != null) {
					apcCore.mappingAutomatico(this.tipoProtocollo, as);
					if(enableAutoMapping_estraiXsdSchemiFromWsdlTypes){
						Hashtable<String, byte[]> schemiAggiuntiInQuestaOperazione = new Hashtable<String, byte[]>();
						if(as.getByteWsdlConcettuale() != null){ 
							apcCore.estraiSchemiFromWSDLTypesAsAllegati(as, as.getByteWsdlConcettuale(),AccordiServizioParteComuneCostanti.TIPO_WSDL_CONCETTUALE, schemiAggiuntiInQuestaOperazione);
						}
						if(facilityUnicoWSDL_interfacciaStandard){
							// è stato utilizzato il concettuale. Lo riporto nel logico
							if(as.getByteWsdlConcettuale()!=null){
								as.setByteWsdlLogicoErogatore(as.getByteWsdlConcettuale());
							}
						}
						else{
							if(as.getByteWsdlLogicoErogatore() != null){
								apcCore.estraiSchemiFromWSDLTypesAsAllegati(as, as.getByteWsdlLogicoErogatore(),AccordiServizioParteComuneCostanti.TIPO_WSDL_EROGATORE, schemiAggiuntiInQuestaOperazione);
							}
							if(as.getByteWsdlLogicoFruitore() != null){
								apcCore.estraiSchemiFromWSDLTypesAsAllegati(as, as.getByteWsdlLogicoFruitore(),AccordiServizioParteComuneCostanti.TIPO_WSDL_FRUITORE, schemiAggiuntiInQuestaOperazione);
							}
						}
					}
					try{
						// Se ho fatto il mapping controllo la validita' di quanto prodotto
						as.setStatoPackage(StatiAccordo.operativo.toString());
						boolean utilizzoAzioniDiretteInAccordoAbilitato = apcCore.isShowAccordiColonnaAzioni();
						apcCore.validaStatoAccordoServizio(as, utilizzoAzioniDiretteInAccordoAbilitato);
					}catch(ValidazioneStatoPackageException validazioneException){
						// Se l'automapping non ha prodotto ne porttype ne operatin rimetto lo stato a bozza
						as.setStatoPackage(StatiAccordo.bozza.toString());
					}
				}
			}

			// effettuo le operazioni
			apcCore.performCreateOperation(userLogin, apcHelper.smista(), as);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<AccordoServizioParteComune> lista = AccordiServizioParteComuneUtilities.accordiList(apcCore, userLogin, ricerca, this.tipoAccordo);

			apcHelper.prepareAccordiList(lista, ricerca, this.tipoAccordo);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.ADD());
		} 

	}

	public void decodeRequest(HttpServletRequest request,boolean isBackwardCompatibilityAccordo11) throws Exception {
		try {
			ServletInputStream in = request.getInputStream();
			BufferedReader dis = new BufferedReader(new InputStreamReader(in));
			String line = dis.readLine();
			while (line != null) {
				if (line.indexOf("\""+Costanti.DATA_ELEMENT_EDIT_MODE_NAME+"\"") != -1) {
					line = dis.readLine();
					this.editMode = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME+"\"") != -1) {
					line = dis.readLine();
					this.nome = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROTOCOLLO+"\"") != -1) {
					line = dis.readLine();
					this.tipoProtocollo = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteComuneCostanti.PARAMETRO_APC_PRIVATO+"\"") != -1) {
					line = dis.readLine();
					this.privato = "yes".equals(dis.readLine().trim());
				}
				if (line.indexOf("\""+AccordiServizioParteComuneCostanti.PARAMETRO_APC_IS_SERVIZIO_COMPOSTO+"\"") != -1) {
					line = dis.readLine();
					this.isServizioComposto = "yes".equals(dis.readLine().trim());
				}
				if (line.indexOf("\""+AccordiServizioParteComuneCostanti.PARAMETRO_APC_ACCORDO_COOPERAZIONE+"\"") != -1) {
					line = dis.readLine();
					this.accordoCooperazione = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteComuneCostanti.PARAMETRO_APC_DESCRIZIONE+"\"") != -1) {
					line = dis.readLine();
					this.descr = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteComuneCostanti.PARAMETRO_APC_REFERENTE+"\"") != -1) {
					line = dis.readLine();
					this.referente = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteComuneCostanti.PARAMETRO_APC_VERSIONE+"\"") != -1) {
					line = dis.readLine();
					this.versione = dis.readLine();
					// patch per version spinner fino a che non si trova un modo piu' elegante
					if(isBackwardCompatibilityAccordo11){
						if("0".equals(this.versione))
							this.versione = "";
					}
				}
				if (line.indexOf("\""+AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROFILO_COLLABORAZIONE+"\"") != -1) {
					line = dis.readLine();
					this.profcoll = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteComuneCostanti.PARAMETRO_APC_FILTRO_DUPLICATI+"\"") != -1) {
					line = dis.readLine();
					this.filtrodup = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteComuneCostanti.PARAMETRO_APC_CONFERMA_RICEZIONE+"\"") != -1) {
					line = dis.readLine();
					this.confric = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteComuneCostanti.PARAMETRO_APC_COLLABORAZIONE+"\"") != -1) {
					line = dis.readLine();
					this.idcoll = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteComuneCostanti.PARAMETRO_APC_CONSEGNA_ORDINE+"\"") != -1) {
					line = dis.readLine();
					this.consord = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteComuneCostanti.PARAMETRO_APC_SCADENZA+"\"") != -1) {
					line = dis.readLine();
					this.scadenza = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteComuneCostanti.PARAMETRO_APC_VALIDAZIONE_DOCUMENTI+"\"") != -1) {
					this.decodeRequestValidazioneDocumenti = true;
					line = dis.readLine();
					String tmpValidazioneDocumenti = dis.readLine();
					if(ServletUtils.isCheckBoxEnabled(tmpValidazioneDocumenti)){
						this.validazioneDocumenti = true;
					}
					else{
						this.validazioneDocumenti = false;
					}
				}
				if (line.indexOf("\""+AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_DEFINITORIO+"\"") != -1) {
					int startId = line.indexOf(Costanti.MULTIPART_FILENAME);
					startId = startId + 10;
					// int endId = line.lastIndexOf("\"");
					// String tmpNomeFile = line.substring(startId, endId);
					line = dis.readLine();
					line = dis.readLine();
					this.wsdldef = "";
					while (!line.startsWith(Costanti.MULTIPART_START) || (line.startsWith(Costanti.MULTIPART_START) && ((line.indexOf(Costanti.MULTIPART_BEGIN) != -1) || (line.indexOf(Costanti.MULTIPART_END) != -1)))) {
						if("".equals(this.wsdldef))
							this.wsdldef = line;
						else
							this.wsdldef = this.wsdldef + "\n" + line;
						line = dis.readLine();
					}
				}
				if (line.indexOf("\""+AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CONCETTUALE+"\"") != -1) {
					int startId = line.indexOf(Costanti.MULTIPART_FILENAME);
					startId = startId + 10;
					// int endId = line.lastIndexOf("\"");
					// String tmpNomeFile = line.substring(startId, endId);
					line = dis.readLine();
					line = dis.readLine();
					this.wsdlconc = "";
					while (!line.startsWith(Costanti.MULTIPART_START) || (line.startsWith(Costanti.MULTIPART_START) && ((line.indexOf(Costanti.MULTIPART_BEGIN) != -1) || (line.indexOf(Costanti.MULTIPART_END) != -1)))) {
						if("".equals(this.wsdlconc))
							this.wsdlconc = line;
						else
							this.wsdlconc = this.wsdlconc + "\n" + line;
						line = dis.readLine();
					}
				}
				if (line.indexOf("\""+AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE+"\"") != -1) {
					int startId = line.indexOf(Costanti.MULTIPART_FILENAME);
					startId = startId + 10;
					// int endId = line.lastIndexOf("\"");
					// String tmpNomeFile = line.substring(startId, endId);
					line = dis.readLine();
					line = dis.readLine();
					this.wsdlserv = "";
					while (!line.startsWith(Costanti.MULTIPART_START) || (line.startsWith(Costanti.MULTIPART_START) && ((line.indexOf(Costanti.MULTIPART_BEGIN) != -1) || (line.indexOf(Costanti.MULTIPART_END) != -1)))) {
						if("".equals(this.wsdlserv))
							this.wsdlserv = line;
						else
							this.wsdlserv = this.wsdlserv + "\n" + line;
						line = dis.readLine();
					}
				}
				if (line.indexOf("\""+AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_FRUITORE+"\"") != -1) {
					int startId = line.indexOf(Costanti.MULTIPART_FILENAME);
					startId = startId + 10;
					// int endId = line.lastIndexOf("\"");
					// String tmpNomeFile = line.substring(startId, endId);
					line = dis.readLine();
					line = dis.readLine();
					this.wsdlservcorr = "";
					while (!line.startsWith(Costanti.MULTIPART_START) || (line.startsWith(Costanti.MULTIPART_START) && ((line.indexOf(Costanti.MULTIPART_BEGIN) != -1) || (line.indexOf(Costanti.MULTIPART_END) != -1)))) {
						if("".equals(this.wsdlservcorr))
							this.wsdlservcorr = line;
						else
							this.wsdlservcorr = this.wsdlservcorr + "\n" + line;
						line = dis.readLine();
					}
				}
				if (line.indexOf("\""+AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE+"\"") != -1) {
					int startId = line.indexOf(Costanti.MULTIPART_FILENAME);
					startId = startId + 10;
					// int endId = line.lastIndexOf("\"");
					// String tmpNomeFile = line.substring(startId, endId);
					line = dis.readLine();
					line = dis.readLine();
					this.wsblconc = "";
					while (!line.startsWith(Costanti.MULTIPART_START) || (line.startsWith(Costanti.MULTIPART_START) && ((line.indexOf(Costanti.MULTIPART_BEGIN) != -1) || (line.indexOf(Costanti.MULTIPART_END) != -1)))) {
						if("".equals(this.wsblconc))
							this.wsblconc = line;
						else
							this.wsblconc = this.wsblconc + "\n" + line;
						line = dis.readLine();
					}
				}
				if (line.indexOf("\""+AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE+"\"") != -1) {
					int startId = line.indexOf(Costanti.MULTIPART_FILENAME);
					startId = startId + 10;
					// int endId = line.lastIndexOf("\"");
					// String tmpNomeFile = line.substring(startId, endId);
					line = dis.readLine();
					line = dis.readLine();
					this.wsblserv = "";
					while (!line.startsWith(Costanti.MULTIPART_START) || (line.startsWith(Costanti.MULTIPART_START) && ((line.indexOf(Costanti.MULTIPART_BEGIN) != -1) || (line.indexOf(Costanti.MULTIPART_END) != -1)))) {
						if("".equals(this.wsblserv))
							this.wsblserv = line;
						else
							this.wsblserv = this.wsblserv + "\n" + line;
						line = dis.readLine();
					}
				}
				if (line.indexOf("\""+AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE+"\"") != -1) {
					int startId = line.indexOf(Costanti.MULTIPART_FILENAME);
					startId = startId + 10;
					// int endId = line.lastIndexOf("\"");
					// String tmpNomeFile = line.substring(startId, endId);
					line = dis.readLine();
					line = dis.readLine();
					this.wsblservcorr = "";
					while (!line.startsWith(Costanti.MULTIPART_START) || (line.startsWith(Costanti.MULTIPART_START) && ((line.indexOf(Costanti.MULTIPART_BEGIN) != -1) || (line.indexOf(Costanti.MULTIPART_END) != -1)))) {
						if("".equals(this.wsblservcorr))
							this.wsblservcorr = line;
						else
							this.wsblservcorr = this.wsblservcorr + "\n" + line;
						line = dis.readLine();
					}
				}
				if (line.indexOf("\""+AccordiServizioParteComuneCostanti.PARAMETRO_APC_STATO_PACKAGE+"\"") != -1) {
					line = dis.readLine();
					this.statoPackage = dis.readLine();
				}
				if (line.indexOf("\""+AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO+"\"") != -1) {
					line = dis.readLine();
					this.tipoAccordo = dis.readLine();
				}

				line = dis.readLine();
			}
			in.close();
		} catch (IOException ioe) {
			throw new Exception(ioe);
		}
	}
}
