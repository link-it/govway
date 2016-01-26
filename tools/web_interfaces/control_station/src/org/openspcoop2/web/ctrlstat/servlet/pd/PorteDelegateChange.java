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


package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.PortaDelegataServizio;
import org.openspcoop2.core.config.PortaDelegataSoggettoErogatore;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataServizioIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataSoggettoErogatoreIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.IdentificazioneView;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * porteDelegateChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteDelegateChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_USA_ID_SOGGETTO, session);

		try {

			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);

			String idPorta = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String nomePorta = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA);
			String idsogg = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String descr = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_DESCRIZIONE);
			String urlinv = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_URL_DI_INVOCAZIONE);
			String autenticazione = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE );
			String nomeauth = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_AUTENTICAZIONE );
			String autorizzazione = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE);
			String nomeautor = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_AUTORIZZAZIONE);
			String tipoSoggettoErogatore = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SP);
			String modeSoggettoErogatore = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_SP);
			String nomeSoggettoErogatore = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SP);
			String idSoggettoErogatore = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SOGGETTO_ID);
			String tiposervizio = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SERVIZIO);
			String modeservizio = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_SERVIZIO);
			String servizio = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO);
			String servid = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO_ID);
			String modeaz = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_AZIONE);
			String azione = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE);
			String azid = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE_ID);
			String integrazione = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_INTEGRAZIONE);
			String stateless = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_STATELESS);
			String localForward = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD );
			String gestBody = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_GESTIONE_BODY);
			String gestManifest = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_GESTIONE_MANIFEST);
			String ricsim = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_SIMMETRICA);
			String ricasim = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_ASIMMETRICA);
			String xsd = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_XSD);
			String tipoValidazione = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE);
			String scadcorr = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SCADENZA_CORRELAZIONE_APPLICATIVA);
			String autorizzazioneContenuti = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI);
			String forceWsdlBased = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_FORCE_WSDL_BASED);
			String applicaMTOM = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_APPLICA_MTOM);

			// check su oldNomePD
			PageData pdOld =  ServletUtils.getPageDataFromSession(session);
			String oldNomePD = pdOld.getHidden(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_OLD_NOME_PD);
			oldNomePD = (((oldNomePD != null) && !oldNomePD.equals("")) ? oldNomePD : nomePorta);
			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// Prendo nome e tipo del soggetto
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteDelegateCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteDelegateCore);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);

			if(porteDelegateCore.isShowPortaDelegataUrlInvocazione()==false){
				urlinv = null;
			}
			
			
			String tmpTitle = null;
			IDSoggetto ids = null;
			if(porteDelegateCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggInt);
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
				ids = new IDSoggetto(soggetto.getTipo(), soggetto.getNome());
			}else{
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(soggInt);
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
				ids = new IDSoggetto(soggetto.getTipo(), soggetto.getNome());
			}

			// Prendo la porta delegata
			IDPortaDelegata idpd = new IDPortaDelegata();
			idpd.setSoggettoFruitore(ids);
			idpd.setLocationPD(oldNomePD);
			PortaDelegata pde = porteDelegateCore.getPortaDelegata(idpd);

			// Prendo il numero di correlazioni applicative
			CorrelazioneApplicativa ca = pde.getCorrelazioneApplicativa();
			int numCorrApp = 0;
			if (ca != null) {
				numCorrApp = ca.sizeElementoList();
			}

			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(ids.getTipo());
			List<String> tipiSoggettiCompatibiliAccordo = soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);
			List<String> tipiServizioCompatibiliAccordo = apsCore.getTipiServiziGestitiProtocollo(protocollo);


			// Informazioni sul numero di ServiziApplicativi, Correlazione Applicativa e stato Message-Security
			int numSA =pde.sizeServizioApplicativoList();
			String statoMessageSecurity  = pde.getStatoMessageSecurity() ;

			// Stato MTOM
			boolean isMTOMAbilitatoReq = false;
			boolean isMTOMAbilitatoRes= false;
			if(pde.getMtomProcessor()!= null){
				if(pde.getMtomProcessor().getRequestFlow() != null){
					if(pde.getMtomProcessor().getRequestFlow().getMode() != null){
						MTOMProcessorType mode = pde.getMtomProcessor().getRequestFlow().getMode();
						if(!mode.equals(MTOMProcessorType.DISABLE))
							isMTOMAbilitatoReq = true;
					}
				}

				if(pde.getMtomProcessor().getResponseFlow() != null){
					if(pde.getMtomProcessor().getResponseFlow().getMode() != null){
						MTOMProcessorType mode = pde.getMtomProcessor().getResponseFlow().getMode();
						if(!mode.equals(MTOMProcessorType.DISABLE))
							isMTOMAbilitatoRes = true;
					}
				}
			}

			String statoMessageMTOM  = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_DISABILITATO;

			if(isMTOMAbilitatoReq || isMTOMAbilitatoRes)
				statoMessageMTOM  = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_ABILITATO;



			int numCorrelazioneReq =0; 
			if(pde.getCorrelazioneApplicativa() != null)
				numCorrelazioneReq  = pde.getCorrelazioneApplicativa().sizeElementoList();

			int numCorrelazioneRes =0;
			if(pde.getCorrelazioneApplicativaRisposta() != null)
				numCorrelazioneRes = pde.getCorrelazioneApplicativaRisposta().sizeElementoList();

			boolean riusoID = false;
			if(numCorrelazioneReq>0){
				for (int i = 0; i < numCorrelazioneReq; i++) {
					if(StatoFunzionalita.ABILITATO.equals(pde.getCorrelazioneApplicativa().getElemento(i).getRiusoIdentificativo())){
						riusoID = true;
						break;
					}
				}
			}

			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if (ServletUtils.isEditModeInProgress(request)) {
				if(useIdSogg){
					// setto la barra del titolo
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTI, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
							new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + tmpTitle,
									PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST,
									new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
									),
									new Parameter(oldNomePD, null)
							);
				}else {
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(PorteDelegateCostanti.LABEL_PORTE_DELEGATE, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST),
							new Parameter(oldNomePD, null)
							);
				}


				String patternErogatore = nomeSoggettoErogatore;
				String patternServizio = servizio;
				String patternAzione = azione;

				if (descr == null) {
					descr = pde.getDescrizione();
				}
				if (stateless == null) {
					if(pde.getStateless()!=null){
						stateless = pde.getStateless().toString();
					}
				}
				if (localForward == null) {
					if(pde.getLocalForward()!=null){
						localForward = pde.getLocalForward().toString();
					}
					if (localForward == null) {
						localForward = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD_DISABILITATO;
					}
				}
				if (gestBody == null) {
					String allegaBody = null;
					if(pde.getAllegaBody()!=null){
						allegaBody = pde.getAllegaBody().toString();
					}
					String scartaBody = null;
					if(pde.getScartaBody()!=null){
						scartaBody = pde.getScartaBody().toString();
					}
					if (PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_ABILITATO.equals(allegaBody) &&
							PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_DISABILITATO.equals(scartaBody))
						gestBody = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_BODY_ALLEGA;
					else if (PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_DISABILITATO.equals(allegaBody) &&
							PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_ABILITATO.equals(scartaBody))
						gestBody = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_BODY_SCARTA;
					else if (PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_DISABILITATO.equals(allegaBody) && 
							PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_DISABILITATO.equals(scartaBody))
						gestBody = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_BODY_NONE; 
				}
				if (gestManifest == null) {
					if(pde.getGestioneManifest()!=null)
						gestManifest = pde.getGestioneManifest().toString();
				}
				if (ricsim == null) {
					if(pde.getRicevutaAsincronaSimmetrica()!=null)
						ricsim = pde.getRicevutaAsincronaSimmetrica().toString();
					if ((ricsim == null) || "".equals(ricsim)) {
						ricsim = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_SIMMETRICA_ABILITATO;

					}
				}
				if (ricasim == null) {
					if(pde.getRicevutaAsincronaAsimmetrica()!=null)
						ricasim = pde.getRicevutaAsincronaAsimmetrica().toString();
					if ((ricasim == null) || "".equals(ricasim)) {
						ricasim = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_ASIMMETRICA_ABILITATO; 
					}
				}
				if (urlinv == null && porteDelegateCore.isShowPortaDelegataUrlInvocazione()) {
					urlinv = pde.getLocation();
				}
				if (autenticazione == null) {
					autenticazione = pde.getAutenticazione();
					if (autenticazione != null &&
							!autenticazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE_SSL) &&
							!autenticazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE_BASIC) &&
							!autenticazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE_NONE)) {
						nomeauth = autenticazione;
						autenticazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE_CUSTOM;
					}
				}
				if (autorizzazione == null) {
					autorizzazione = pde.getAutorizzazione();
					if (autorizzazione != null &&
							!autorizzazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE_OPENSPCOOP) &&
							!autorizzazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE_NONE)) {
						nomeautor = autorizzazione;
						autorizzazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE_CUSTOM;
					}
				}
				if (xsd == null) {
					ValidazioneContenutiApplicativi vx = pde.getValidazioneContenutiApplicativi();
					if (vx == null) {
						xsd = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_XSD_DISABILITATO;
					} else {
						if(vx.getStato()!=null)
							xsd = vx.getStato().toString();
						if ((xsd == null) || "".equals(xsd)) {
							xsd = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_XSD_DISABILITATO;
						}
					}
				}
				if (tipoValidazione == null) {
					ValidazioneContenutiApplicativi vx = pde.getValidazioneContenutiApplicativi();
					if (vx == null) {
						tipoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_XSD;
					} else {
						if(vx.getTipo()!=null)
							tipoValidazione = vx.getTipo().toString();
						if (tipoValidazione == null || "".equals(tipoValidazione)) {
							tipoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_XSD;
						}
					}
				}
				if (applicaMTOM == null) {
					ValidazioneContenutiApplicativi vx = pde.getValidazioneContenutiApplicativi();
					applicaMTOM = "";
					if (vx != null) {
						if(vx.getAcceptMtomMessage()!=null)
							if (vx.getAcceptMtomMessage().equals(StatoFunzionalita.ABILITATO)) 
								applicaMTOM = "yes";
					}
				}

				if(autorizzazioneContenuti==null){
					autorizzazioneContenuti = pde.getAutorizzazioneContenuto();
				}

				if (integrazione == null) {
					integrazione = pde.getIntegrazione();
				}

				if ((scadcorr == null) && (ca != null)) {
					scadcorr = ca.getScadenza();
				}
				if (modeSoggettoErogatore == null) {
					PortaDelegataSoggettoErogatore pdsse = pde.getSoggettoErogatore();
					tipoSoggettoErogatore = pdsse.getTipo();
					if(pdsse.getIdentificazione()!=null)
						modeSoggettoErogatore = pdsse.getIdentificazione().toString();
					nomeSoggettoErogatore = pdsse.getNome();
					int idSoggettoErogatoreInt = pdsse.getId().intValue();
					patternErogatore = pdsse.getPattern();
					idSoggettoErogatore = "" + idSoggettoErogatoreInt;
					// assegno costante
					if (modeSoggettoErogatore != null) {
						switch (PortaDelegataSoggettoErogatoreIdentificazione.toEnumConstant(modeSoggettoErogatore)) {
						case CONTENT_BASED:
							modeSoggettoErogatore = IdentificazioneView.CONTENT_BASED.toString();
							break;
						case INPUT_BASED:
							modeSoggettoErogatore = IdentificazioneView.INPUT_BASED.toString();
							break;
						case URL_BASED:
							modeSoggettoErogatore = IdentificazioneView.URL_BASED.toString();
							break;
						case STATIC:

							if ( (idSoggettoErogatoreInt == -2) || (idSoggettoErogatoreInt>0) ) {
								modeSoggettoErogatore = IdentificazioneView.REGISTER_INPUT.toString();
								idSoggettoErogatore = tipoSoggettoErogatore+"/"+nomeSoggettoErogatore;
							} else {
								modeSoggettoErogatore = IdentificazioneView.USER_INPUT.toString();
							}
							break;
						}
					}
				}
				if (modeservizio == null) {
					PortaDelegataServizio pds = pde.getServizio();
					tiposervizio = pds.getTipo();
					if(pds.getIdentificazione()!=null)
						modeservizio = pds.getIdentificazione().toString();
					patternServizio = pds.getPattern();
					servizio = pds.getNome();
					int servidInt = pds.getId().intValue();
					servid = "" + servidInt;
					// assegno costante
					if (modeservizio != null) {
						switch (PortaDelegataServizioIdentificazione.toEnumConstant(modeservizio)) {
						case CONTENT_BASED:
							modeservizio = IdentificazioneView.CONTENT_BASED.toString();
							break;
						case INPUT_BASED:
							modeservizio = IdentificazioneView.INPUT_BASED.toString();
							break;
						case URL_BASED:
							modeservizio = IdentificazioneView.URL_BASED.toString();
							break;
						case STATIC:

							if ( (servidInt == -2) || (servidInt>0) ) {
								modeservizio = IdentificazioneView.REGISTER_INPUT.toString();
								servid = tiposervizio+"/"+servizio;
							} else {
								modeservizio = IdentificazioneView.USER_INPUT.toString();
							}
							break;

						}
					}

				}
				if (modeaz == null) {
					PortaDelegataAzione pda = pde.getAzione();
					if (pda == null) {
						modeaz = "";
						azione = "";
						patternAzione = "";
					} else {
						if(pda.getIdentificazione()!=null)
							modeaz = pda.getIdentificazione().toString();
						azione = pda.getNome();
						patternAzione = pda.getPattern();
						azid = "" + pda.getId();
					}
					if ((modeaz == null) || "".equals(modeaz)) {
						modeaz = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_AZ_STATIC;
					}
					boolean useForceWSDLBased = true;
					// assegno costante
					if (modeaz != null) {
						switch (PortaDelegataAzioneIdentificazione.toEnumConstant(modeaz)) {
						case CONTENT_BASED:
							modeaz = IdentificazioneView.CONTENT_BASED.toString();
							break;
						case INPUT_BASED:
							modeaz = IdentificazioneView.INPUT_BASED.toString();
							break;
						case URL_BASED:
							modeaz = IdentificazioneView.URL_BASED.toString();
							break;
						case STATIC:
							useForceWSDLBased = false;
							int azidInt = 0;
							if (azid != null)
								azidInt = Integer.parseInt(azid);
							if ( (azidInt == -2) || (azidInt>0) ) {
								modeaz = IdentificazioneView.REGISTER_INPUT.toString();
								azid = azione;
							} else {
								modeaz = IdentificazioneView.USER_INPUT.toString();
							}
							break;
						case SOAP_ACTION_BASED:
							modeaz = IdentificazioneView.SOAP_ACTION_BASED.toString();
							break;
						case WSDL_BASED:
							useForceWSDLBased = false;
							modeaz = IdentificazioneView.WSDL_BASED.toString();
							break;
						}

						if(useForceWSDLBased){
							StatoFunzionalita forceWsdlBased2 = pda.getForceWsdlBased();

							if(forceWsdlBased2 != null && forceWsdlBased2.equals(StatoFunzionalita.ABILITATO)){
								forceWsdlBased = "yes";
							} else {
								forceWsdlBased = "";
							}
						}
					}
				}

				if (!modeSoggettoErogatore.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) &&
						PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT.equals(modeservizio)) {
					modeservizio = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT;
				}
				if (!modeSoggettoErogatore.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && 
						PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT.equals(modeaz)) {
					modeaz = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT;
				}
				if (!modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && 
						PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT.equals(modeaz)) {
					modeaz = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT;
				}
				if (!modeSoggettoErogatore.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && 
						(nomeSoggettoErogatore == null)) {
					nomeSoggettoErogatore = "";
				}
				if (!modeSoggettoErogatore.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && 
						(tipoSoggettoErogatore == null)) {
					tipoSoggettoErogatore = "";
				}
				if (!modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) &&
						(servizio == null)) {
					servizio = "";
				}
				if (!modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) &&
						(tiposervizio == null)) {
					tiposervizio = "";
				}
				if ((modeaz != null) && !modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && 
						(azione == null)) {
					azione = "";
				}

				// Se modesp = register-input, prendo la lista di tutti i
				// soggetti e la metto in un array



				String[] soggettiList = null;
				String[] soggettiListLabel = null;
				if (modeSoggettoErogatore.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
					List<IDSoggetto> list = null;
					try{
						list = soggettiCore.getAllIdSoggettiRegistro(new FiltroRicercaSoggetti());
					}catch(DriverRegistroServiziNotFound dNotFound){}
					if (list!=null && list.size() > 0) {

						List<String> soggettiListTmp = new ArrayList<String>();
						for (IDSoggetto soggetto : list) {
							if(tipiSoggettiCompatibiliAccordo.contains(soggetto.getTipo())){
								soggettiListTmp.add(soggetto.getTipo() + "/" + soggetto.getNome());
							}
						}
						Collections.sort(soggettiListTmp);
						soggettiList = soggettiListTmp.toArray(new String[1]);
						soggettiListLabel = soggettiList;
					}
				}

				// Se modeservizio = register-input, prendo la lista di tutti i
				// servizi e la metto in un array
				String[] serviziList = null;
				String[] serviziListLabel = null;
				if (modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
					if ((idSoggettoErogatore != null && !"".equals(idSoggettoErogatore) && idSoggettoErogatore.contains("/"))) {
						IDSoggetto idSoggetto = new IDSoggetto(idSoggettoErogatore.split("/")[0], idSoggettoErogatore.split("/")[1]);
						FiltroRicercaServizi filtro = new FiltroRicercaServizi();
						filtro.setTipoSoggettoErogatore(idSoggetto.getTipo());
						filtro.setNomeSoggettoErogatore(idSoggetto.getNome());
						List<IDServizio> list = null;
						try{
							list = apsCore.getAllIdServizi(filtro);
						}catch(DriverRegistroServiziNotFound dNotFound){}
						if(list!=null && list.size()>0){
							List<String> serviziListTmp = new ArrayList<String>();

							for (IDServizio idServizio : list) {
								if(tipiServizioCompatibiliAccordo.contains(idServizio.getTipoServizio())){
									serviziListTmp.add(idServizio.getTipoServizio() + "/" + idServizio.getServizio());
								}
							}

							Collections.sort(serviziListTmp);
							serviziList = serviziListTmp.toArray(new String[1]);
							serviziListLabel = serviziList;
						}
					}
				}
				
				IDSoggetto idSoggetto = null;
				IDServizio idServizio = null;
				AccordoServizioParteSpecifica servS = null;
				if (	(servid != null && !"".equals(servid) && servid.contains("/"))
						&& 
						(idSoggettoErogatore != null && !"".equals(idSoggettoErogatore) && idSoggettoErogatore.contains("/"))
						) {
					idSoggetto = new IDSoggetto(idSoggettoErogatore.split("/")[0], idSoggettoErogatore.split("/")[1]);
					idServizio = new IDServizio(idSoggetto, servid.split("/")[0], servid.split("/")[1]);
					try{
						servS = apsCore.getServizio(idServizio);
					}catch(DriverRegistroServiziNotFound dNotFound){
					}
					if(servS==null){
						// è cambiato il soggetto erogatore. non è più valido il servizio
						servid = null;
						idServizio = null;
						if(serviziList!=null && serviziList.length>0){
							servid = serviziList[0];
							idServizio = new IDServizio(idSoggetto, servid.split("/")[0], servid.split("/")[1]);
							try{
								servS = apsCore.getServizio(idServizio);
							}catch(DriverRegistroServiziNotFound dNotFound){
							}
							if(servS==null){
								servid = null;
								idServizio = null;
							}
						}
					}
				}

				// Se modeaz = register-input, prendo la lista delle azioni
				// associate a servid e la metto in un array
				String[] azioniList = null;
				String[] azioniListLabel = null;
				if ((modeaz != null) && modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
					if ( servS!=null ) {
						
						IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(servS.getAccordoServizioParteComune());
						AccordoServizioParteComune as = apcCore.getAccordoServizio(idAccordo);

						if(servS.getPortType()!=null){
							// Bisogna prendere le operations del port type
							PortType pt = null;
							for (int i = 0; i < as.sizePortTypeList(); i++) {
								if(as.getPortType(i).getNome().equals(servS.getPortType())){
									pt = as.getPortType(i);
									break;
								}
							}
							if(pt==null){
								throw new Exception("Servizio ["+idServizio.toString()+"] possiede il port type ["+servS.getPortType()+"] che non risulta essere registrato nell'accordo di servizio ["+servS.getAccordoServizioParteComune()+"]");
							}
							if(pt.sizeAzioneList()>0){
								azioniList = new String[pt.sizeAzioneList()];
								azioniListLabel = new String[pt.sizeAzioneList()];
								List<String> azioni = new ArrayList<String>();
								for (int i = 0; i < pt.sizeAzioneList(); i++) {
									if (azid == null) {
										azid = pt.getAzione(i).getNome();
									}
									azioni.add("" + pt.getAzione(i).getNome());
								}
								Collections.sort(azioni);
								for (int i = 0; i < azioni.size(); i++) {
									azioniList[i] = "" + azioni.get(i);
									azioniListLabel[i] = azioniList[i];
								}
							}
						}else{
							if(as.sizeAzioneList()>0){
								azioniList = new String[as.sizeAzioneList()];
								azioniListLabel = new String[as.sizeAzioneList()];
								List<String> azioni = new ArrayList<String>();
								for (int i = 0; i < as.sizeAzioneList(); i++) {
									if (azid == null) {
										azid = as.getAzione(i).getNome();
									}
									azioni.add(as.getAzione(i).getNome());
								}
								Collections.sort(azioni);
								for (int i = 0; i < azioni.size(); i++) {
									azioniList[i] = "" + azioni.get(i);
									azioniListLabel[i] = azioniList[i];
								}
							}
						}				
					}
				}
				int numAzioni = 0;
				if (azioniList != null)
					numAzioni = azioniList.length;

				// setto oldNomePD
				pd.addHidden(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_OLD_NOME_PD, oldNomePD);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteDelegateHelper.addPorteDelegateToDati(TipoOperazione.CHANGE, idsogg, nomePorta, dati,
						idPorta, descr, urlinv, autenticazione,
						autorizzazione, modeSoggettoErogatore,
						idSoggettoErogatore, soggettiList, soggettiListLabel,
						nomeSoggettoErogatore, tipoSoggettoErogatore,
						patternErogatore, modeservizio, servid, serviziList,
						serviziListLabel, servizio, tiposervizio,
						patternServizio, modeaz, azid, azioniListLabel,
						azioniList, azione, patternAzione, numAzioni,
						stateless, localForward, ricsim, ricasim, xsd,
						tipoValidazione, numCorrApp, scadcorr, gestBody,
						gestManifest,integrazione, nomeauth, nomeautor,autorizzazioneContenuti,
						idsogg,protocollo,numSA,statoMessageSecurity,statoMessageMTOM,
						numCorrelazioneReq,numCorrelazioneRes,
						forceWsdlBased,applicaMTOM,riusoID);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE, ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi

			boolean isOk = porteDelegateHelper.porteDelegateCheckData(TipoOperazione.CHANGE, oldNomePD);

			if (!isOk) {
				// setto la barra del titolo
				if(useIdSogg){
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTI, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
							new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + tmpTitle,
									PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST,
									new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)),								
									new Parameter(oldNomePD, null)
							);
				}else {
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(PorteDelegateCostanti.LABEL_PORTE_DELEGATE, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST),
							new Parameter(oldNomePD, null)
							);
				}

				if (!modeSoggettoErogatore.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) &&
						modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
					modeservizio = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT;
				}
				if (!modeSoggettoErogatore.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && 
						modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
					modeaz = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT;
				}
				if (!modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && 
						modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
					modeaz = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT;
				}

				// Se modesp = register-input, prendo la lista di tutti i
				// soggetti e la metto in un array
				String[] soggettiList = null;
				String[] soggettiListLabel = null;
				if (modeSoggettoErogatore.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
					List<IDSoggetto> list = null;
					try{
						list = soggettiCore.getAllIdSoggettiRegistro(new FiltroRicercaSoggetti());
					}catch(DriverRegistroServiziNotFound dNotFound){}
					if (list!=null && list.size() > 0) {

						List<String> soggettiListTmp = new ArrayList<String>();
						List<String> soggettiListLabelTmp = new ArrayList<String>();
						for (IDSoggetto soggetto : list) {
							if(tipiSoggettiCompatibiliAccordo.contains(soggetto.getTipo())){
								soggettiListTmp.add(soggetto.getTipo() + "/" + soggetto.getNome());
								soggettiListLabelTmp.add(soggetto.getTipo() + "/" + soggetto.getNome());
							}
						}
						soggettiList = soggettiListTmp.toArray(new String[1]);
						soggettiListLabel = soggettiListLabelTmp.toArray(new String[1]);
					}
				}

				// Se modeservizio = register-input, prendo la lista di tutti i servizi
				// e la metto in un array
				String[] serviziList = null;
				String[] serviziListLabel = null;
				if (modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
					if ((idSoggettoErogatore != null && !"".equals(idSoggettoErogatore) && idSoggettoErogatore.contains("/"))) {
						IDSoggetto idSoggetto = new IDSoggetto(idSoggettoErogatore.split("/")[0], idSoggettoErogatore.split("/")[1]);
						FiltroRicercaServizi filtro = new FiltroRicercaServizi();
						filtro.setTipoSoggettoErogatore(idSoggetto.getTipo());
						filtro.setNomeSoggettoErogatore(idSoggetto.getNome());
						List<IDServizio> list = null;
						try{
							list = apsCore.getAllIdServizi(filtro);
						}catch(DriverRegistroServiziNotFound dNotFound){}
						if(list!=null && list.size()>0){
							List<String> serviziListTmp = new ArrayList<String>();
							List<String> serviziListLabelTmp = new ArrayList<String>();

							for (IDServizio idServizio : list) {
								if(tipiServizioCompatibiliAccordo.contains(idServizio.getTipoServizio())){
									serviziListTmp.add(idServizio.getTipoServizio() + "/" + idServizio.getServizio());
									serviziListLabelTmp.add(idServizio.getTipoServizio() + "/" + idServizio.getServizio());
								}
							}

							serviziList = serviziListTmp.toArray(new String[1]);
							serviziListLabel =serviziListLabelTmp.toArray(new String[1]);
						}
					}
				}

				// Se modeaz = register-input, prendo la lista delle azioni
				// associate a servid e la metto in un array
				String[] azioniList = null;
				String[] azioniListLabel = null;
				if ((modeaz != null) && modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
					if (	(servid != null && !"".equals(servid) && servid.contains("/"))
							&& 
							(idSoggettoErogatore != null && !"".equals(idSoggettoErogatore) && idSoggettoErogatore.contains("/"))
							) {
						IDSoggetto idSoggetto = new IDSoggetto(idSoggettoErogatore.split("/")[0], idSoggettoErogatore.split("/")[1]);
						IDServizio idServizio = new IDServizio(idSoggetto, servid.split("/")[0], servid.split("/")[1]);
						AccordoServizioParteSpecifica servS = null;
						try{
							servS = apsCore.getServizio(idServizio);
						}catch(DriverRegistroServiziNotFound dNotFound){

						}
						if(servS==null){
							throw new Exception("Servizio ["+idServizio.toString()+"] non riscontrato");
						}
						IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(servS.getAccordoServizioParteComune());
						AccordoServizioParteComune as = apcCore.getAccordoServizio(idAccordo);

						if(servS.getPortType()!=null){
							// Bisogna prendere le operations del port type
							PortType pt = null;
							for (int i = 0; i < as.sizePortTypeList(); i++) {
								if(as.getPortType(i).getNome().equals(servS.getPortType())){
									pt = as.getPortType(i);
									break;
								}
							}
							if(pt==null){
								throw new Exception("Servizio ["+idServizio.toString()+"] possiede il port type ["+servS.getPortType()+"] che non risulta essere registrato nell'accordo di servizio ["+servS.getAccordoServizioParteComune()+"]");
							}
							if(pt.sizeAzioneList()>0){
								azioniList = new String[pt.sizeAzioneList()];
								azioniListLabel = new String[pt.sizeAzioneList()];
								for (int i = 0; i < pt.sizeAzioneList(); i++) {
									if (azid == null) {
										azid = pt.getAzione(i).getNome();
									}
									azioniList[i] = "" + pt.getAzione(i).getNome();
									azioniListLabel[i] = pt.getAzione(i).getNome();
								}
							}
						}else{
							if(as.sizeAzioneList()>0){
								azioniList = new String[as.sizeAzioneList()];
								azioniListLabel = new String[as.sizeAzioneList()];
								for (int i = 0; i < as.sizeAzioneList(); i++) {
									if (azid == null) {
										azid = as.getAzione(i).getNome();
									}
									azioniList[i] = "" + as.getAzione(i).getNome();
									azioniListLabel[i] = as.getAzione(i).getNome();
								}
							}
						}				
					}
				}
				int numAzioni = 0;
				if (azioniList != null)
					numAzioni = azioniList.length;

				// setto oldNomePD
				pd.addHidden(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_OLD_NOME_PD, oldNomePD);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				// i pattern sono i nomi

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteDelegateHelper.addPorteDelegateToDati(TipoOperazione.CHANGE, idsogg, nomePorta, dati,
						idPorta, descr, urlinv, autenticazione,
						autorizzazione, modeSoggettoErogatore,
						idSoggettoErogatore, soggettiList, soggettiListLabel,
						nomeSoggettoErogatore, tipoSoggettoErogatore,
						nomeSoggettoErogatore, modeservizio, servid,
						serviziList, serviziListLabel, servizio,
						tiposervizio, servizio, modeaz, azid,
						azioniListLabel, azioniList, azione, azione,
						numAzioni,  stateless, localForward, ricsim, ricasim,
						xsd, tipoValidazione, numCorrApp, scadcorr, gestBody,
						gestManifest,integrazione, nomeauth, nomeautor,autorizzazioneContenuti,
						idsogg,protocollo,numSA,statoMessageSecurity,statoMessageMTOM,
						numCorrelazioneReq,numCorrelazioneRes,forceWsdlBased,applicaMTOM,riusoID);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE,
						ForwardParams.CHANGE());

			}

			// Modifico i dati della porta delegata nel db

			if (modeSoggettoErogatore.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				nomeSoggettoErogatore = idSoggettoErogatore.split("/")[1];
				tipoSoggettoErogatore = idSoggettoErogatore.split("/")[0];
			}
			if (modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				servizio = servid.split("/")[1];
				tiposervizio = servid.split("/")[0];
			}
			if (modeaz != null) {
				if (modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
					azione = "";
				} else {
					azid = "";
				}
			}

			long idPA = pde.getId();

			PortaDelegata oldPD = porteDelegateCore.getPortaDelegata(idPA);

			PortaDelegata portaDelegata = (PortaDelegata) oldPD.clone();// new
			// PortaDelegata();
			portaDelegata.setId(idPA);

			portaDelegata.setNome(nomePorta);
			portaDelegata.setOldNomeForUpdate(oldPD.getNome());
			portaDelegata.setDescrizione(descr);
			if(porteDelegateCore.isShowPortaDelegataUrlInvocazione()==false){
				portaDelegata.setLocation(nomePorta);
			}
			else if(urlinv!=null && !"".equals(urlinv)){
				portaDelegata.setLocation(urlinv);
			}
			if (autenticazione == null || !autenticazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE_CUSTOM))
				portaDelegata.setAutenticazione(autenticazione);
			else
				portaDelegata.setAutenticazione(nomeauth);
			if (autorizzazione == null || !autorizzazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE_CUSTOM))
				portaDelegata.setAutorizzazione(autorizzazione);
			else
				portaDelegata.setAutorizzazione(nomeautor);
			if (stateless!=null && stateless.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_STATELESS_DEFAULT))
				portaDelegata.setStateless(null);
			else
				portaDelegata.setStateless(StatoFunzionalita.toEnumConstant(stateless));
			if (PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_BODY_ALLEGA.equals(gestBody))
				portaDelegata.setAllegaBody(StatoFunzionalita.toEnumConstant(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_ABILITATO));
			else
				portaDelegata.setAllegaBody(StatoFunzionalita.toEnumConstant(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_DISABILITATO));
			if (PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_BODY_SCARTA.equals(gestBody))
				portaDelegata.setScartaBody(StatoFunzionalita.toEnumConstant(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_ABILITATO));
			else
				portaDelegata.setScartaBody(StatoFunzionalita.toEnumConstant(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_DISABILITATO));
			if (gestManifest!=null && gestManifest.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_STATELESS_DEFAULT))
				portaDelegata.setGestioneManifest(null);
			else
				portaDelegata.setGestioneManifest(StatoFunzionalita.toEnumConstant(gestManifest));
			portaDelegata.setRicevutaAsincronaSimmetrica(StatoFunzionalita.toEnumConstant(ricsim));
			portaDelegata.setRicevutaAsincronaAsimmetrica(StatoFunzionalita.toEnumConstant(ricasim));
			portaDelegata.setLocalForward(StatoFunzionalita.toEnumConstant(localForward));

			PortaDelegataSoggettoErogatore pdSogg = new PortaDelegataSoggettoErogatore();
			IDSoggetto idSoggErogatore = new IDSoggetto(tipoSoggettoErogatore, nomeSoggettoErogatore);
			if (modeSoggettoErogatore.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				pdSogg.setId(soggettiCore.getSoggettoRegistro(idSoggErogatore).getId());
				if(pdSogg.getId()<=0){
					pdSogg.setId(-2l);
				}
			}
			pdSogg.setNome(nomeSoggettoErogatore);
			pdSogg.setTipo(tipoSoggettoErogatore);
			pdSogg.setIdentificazione(IdentificazioneView.view2db_soggettoErogatore(modeSoggettoErogatore));
			pdSogg.setPattern(nomeSoggettoErogatore);

			portaDelegata.setSoggettoErogatore(pdSogg);

			PortaDelegataServizio pdServizio = new PortaDelegataServizio();
			AccordoServizioParteSpecifica asps = null;
			IDServizio idServizio = null;
			if (modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				idServizio = new IDServizio(idSoggErogatore, tiposervizio, servizio);
				try{
					asps = apsCore.getServizio(idServizio);
					pdServizio.setId(asps.getId());
				}catch(DriverRegistroServiziNotFound dNotFound){
				}
				if(pdServizio.getId()<=0){
					pdServizio.setId(-2l);
				}
			}
			pdServizio.setNome(servizio);
			pdServizio.setTipo(tiposervizio);
			pdServizio.setIdentificazione(IdentificazioneView.view2db_servizio(modeservizio));
			pdServizio.setPattern(servizio);

			portaDelegata.setServizio(pdServizio);

			// se l azione e' settata allora creo il bean
			if ((modeaz != null) &&
					((azione != null) || (azid != null) || modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED) ||
							modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_SOAP_ACTION_BASED) ||
							modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_WSDL_BASED)) &&
							(!azione.equals("") || !azid.equals("") || 
									(modeaz != null && (modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_INPUT_BASED) || 
											modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_SOAP_ACTION_BASED) ||
											modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_WSDL_BASED))))) {
				PortaDelegataAzione pdAzione = new PortaDelegataAzione();

				if (modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
					azione = azid;
				}
				if (modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
					IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
					AccordoServizioParteComune as = apcCore.getAccordoServizio(idAccordo);

					if(asps.getPortType()!=null){
						// Bisogna prendere le operations del port type
						PortType pt = null;
						for (int i = 0; i < as.sizePortTypeList(); i++) {
							if(as.getPortType(i).getNome().equals(asps.getPortType())){
								pt = as.getPortType(i);
								break;
							}
						}
						if(pt==null){
							throw new Exception("Accordo servizio parte specifica ["+idServizio.toString()+"] possiede un port type ["+asps.getPortType()+"] che non risulta essere registrato nell'accordo di servizio parte comune ["+asps.getAccordoServizioParteComune()+"]");
						}
						if(pt.sizeAzioneList()>0){
							for (int i = 0; i < pt.sizeAzioneList(); i++) {
								if(pt.getAzione(i).getNome().equals(azione)){
									pdAzione.setId(pt.getAzione(i).getId());
									break;
								}
							}
						}
					}else{
						if(as.sizeAzioneList()>0){
							for (int i = 0; i < as.sizeAzioneList(); i++) {
								if(as.getAzione(i).getNome().equals(azione)){
									pdAzione.setId(as.getAzione(i).getId());
									break;
								}
							}
						}
					}

					if(pdAzione.getId()<=0){
						pdAzione.setId(-2l);
					}
				}
				pdAzione.setNome(azione);
				pdAzione.setIdentificazione(IdentificazioneView.view2db_azione(modeaz));
				pdAzione.setPattern(azione);

				//FORCE WSDL BASED
				if(modeaz != null && (!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) &&
						!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT) &&
						!modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_WSDL_BASED))){

					if(forceWsdlBased != null && (ServletUtils.isCheckBoxEnabled(forceWsdlBased))){
						pdAzione.setForceWsdlBased(StatoFunzionalita.ABILITATO);
					}else {
						pdAzione.setForceWsdlBased(StatoFunzionalita.DISABILITATO);
					}
				} else {
					pdAzione.setForceWsdlBased(null);
				}


				portaDelegata.setAzione(pdAzione);
			} else {
				portaDelegata.setAzione(null);
			}

			// soggetto proprietario
			SoggettoCtrlStat soggettoCS = soggettiCore.getSoggettoCtrlStat(soggInt);
			portaDelegata.setIdSoggetto(soggettoCS.getId());
			portaDelegata.setTipoSoggettoProprietario(soggettoCS.getTipo());
			portaDelegata.setNomeSoggettoProprietario(soggettoCS.getNome());
			portaDelegata.setOldNomeSoggettoProprietarioForUpdate(oldPD.getNomeSoggettoProprietario());
			portaDelegata.setOldTipoSoggettoProprietarioForUpdate(oldPD.getTipoSoggettoProprietario());

			// servizi applicativi
			// portaDelegata.setServizioApplicativoList(oldPD.getServizioApplicativoList());

			// ws sec
			// portaDelegata.setMessageSecurity(oldPD.getMessageSecurity());

			ValidazioneContenutiApplicativi vx = new ValidazioneContenutiApplicativi();
			vx.setStato(StatoFunzionalitaConWarning.toEnumConstant(xsd));
			vx.setTipo(ValidazioneContenutiApplicativiTipo.toEnumConstant(tipoValidazione));
			if(applicaMTOM != null){
				if(ServletUtils.isCheckBoxEnabled(applicaMTOM))
					vx.setAcceptMtomMessage(StatoFunzionalita.ABILITATO);
				else 
					vx.setAcceptMtomMessage(StatoFunzionalita.DISABILITATO);
			} else 
				vx.setAcceptMtomMessage(null);
			
			portaDelegata.setValidazioneContenutiApplicativi(vx);

			portaDelegata.setAutorizzazioneContenuto(autorizzazioneContenuti);

			// Cambio i dati della vecchia CorrelazioneApplicativa
			// Non ne creo una nuova, altrimenti mi perdo le vecchie entry
			if (ca != null)
				ca.setScadenza(scadcorr);
			portaDelegata.setCorrelazioneApplicativa(ca);

			portaDelegata.setIntegrazione(integrazione);

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);



			List<PortaDelegata> lista = null;
			if(useIdSogg){
				int idLista = Liste.PORTE_DELEGATE_BY_SOGGETTO;
				ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
				lista = porteDelegateCore.porteDelegateList(soggInt, ricerca);
			}else{ 
				int idLista = Liste.PORTE_DELEGATE;
				ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
				lista = porteDelegateCore.porteDelegateList(null, ricerca);
			}

			porteDelegateHelper.preparePorteDelegateList(ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE, ForwardParams.CHANGE());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE, 
					ForwardParams.CHANGE());
		}  
	}
}
