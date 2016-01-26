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
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.PortaDelegataServizio;
import org.openspcoop2.core.config.PortaDelegataSoggettoErogatore;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import org.openspcoop2.core.id.IDAccordo;
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
 * porteDelegateAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteDelegateAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			String nomePD = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA);
			//			String idPorta = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			//			String nomePorta = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA);
			String idsogg = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String descr = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_DESCRIZIONE);
			String urlinv = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_URL_DI_INVOCAZIONE);
			String autenticazione = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE);
			String nomeauth = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_AUTENTICAZIONE);
			String autorizzazione = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE);
			String nomeautor = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_AUTORIZZAZIONE);
			String soggid = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SOGGETTO_ID);
			String tiposp = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SP);
			String modesp = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_SP);
			String sp = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SP);
			String servid = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO_ID);
			String tiposervizio = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_SERVIZIO);
			String modeservizio = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_SERVIZIO);
			String servizio = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO);
			String azid = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE_ID);
			String modeaz = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_AZIONE);
			String azione = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE);
			String stateless = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_STATELESS);
			String localForward = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD);
			String gestBody = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_GESTIONE_BODY);
			String gestManifest = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_GESTIONE_MANIFEST);
			String ricsim = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_SIMMETRICA);
			String ricasim = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_ASIMMETRICA);
			String xsd = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_XSD);
			String tipoValidazione = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE);
			String autorizzazioneContenuti = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI);
			String forceWsdlBased = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_FORCE_WSDL_BASED);
			String applicaMTOM = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_APPLICA_MTOM);

			if (modesp == null) {
				modesp = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT;
				tiposp = "";
				sp = "";
				modeservizio = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT;
				tiposervizio = "";
				servizio = "";
				modeaz = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT;
				azione = "";
			}
			if (!modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)
					&& modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				modeservizio = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT;
			}
			if (!modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) 
					&& modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				modeaz = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT;
			}
			if (!modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)
					&& modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				modeaz = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_USER_INPUT;
			}
			if (!modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && (sp == null)) {
				sp = "";
				tiposp = "";
			}
			if (!modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && (servizio == null)) {
				servizio = "";
				tiposervizio = "";
			}
			if ((modeaz != null) && !modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT) && (azione == null)) {
				azione = "";
			}

			// Informazioni sul numero di ServiziApplicativi, Correlazione Applicativa e stato Message-Security
			int numSA =0;
			String statoMessageSecurity  = "";
			String statoMTOM  = "";
			int numCorrelazioneReq =0; 
			int numCorrelazioneRes =0;

			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// Prendo nome e tipo del soggetto
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore( );
			SoggettiCore soggettiCore = new SoggettiCore(porteDelegateCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteDelegateCore);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);

			if(porteDelegateCore.isShowPortaDelegataUrlInvocazione()==false){
				urlinv = null;
			}
			
			String tmpTitle = null, tipoSoggetto = null;
			if(porteDelegateCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggInt);
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
				tipoSoggetto = soggetto.getTipo();
			}else{
				org.openspcoop2.core.config.Soggetto tmpSogg = soggettiCore.getSoggetto(soggInt);
				tmpTitle = tmpSogg.getTipo() + "/" + tmpSogg.getNome();
				tipoSoggetto = tmpSogg.getTipo();
			}


			// Se modesp = register-input, prendo la lista di tutti i soggetti
			// e la metto in un array
			String[] soggettiList = null;
			String[] soggettiListLabel = null;

			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoSoggetto);
			List<String> tipiSoggettiCompatibiliAccordo = soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);
			List<String> tipiServizioCompatibiliAccordo = apsCore.getTipiServiziGestitiProtocollo(protocollo);

			if (modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
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

			// Se modeservizio = register-input, prendo la lista di tutti i servizi
			// e la metto in un array
			String[] serviziList = null;
			String[] serviziListLabel = null;
			if (modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				if ( (soggid != null && !"".equals(soggid) && soggid.contains("/")) ) {
					IDSoggetto idSoggetto = new IDSoggetto(soggid.split("/")[0], soggid.split("/")[1]);
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
					(soggid != null && !"".equals(soggid) && soggid.contains("/"))
					) {
				idSoggetto = new IDSoggetto(soggid.split("/")[0], soggid.split("/")[1]);
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
				if (servS!=null ) {
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

			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (ServletUtils.isEditModeInProgress(request)) {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTI, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
						new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + tmpTitle, 
								PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST,
								new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
								),
								new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null)
						);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				if (nomePD == null) {
					nomePD = "";
				}
				if (descr == null) {
					descr = "";
				}
				if (urlinv == null) {
					urlinv = "";
				}
				if (autenticazione == null) {
					autenticazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE_SSL;
				}
				if (autorizzazione == null) {
					autorizzazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE_OPENSPCOOP;
				}
				if (stateless == null) {
					stateless = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_STATELESS_DEFAULT;
				}
				if (localForward == null) {
					localForward = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_LOCAL_FORWARD_DISABILITATO;
				}
				if (gestBody == null) {
					gestBody =  PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_BODY_NONE;
				}
				if (gestManifest == null) {
					gestManifest = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_MANIFEST_DEFAULT;
				}
				if (ricsim == null) {
					ricsim =  PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_SIMMETRICA_ABILITATO;
				}
				if (ricasim == null) {
					ricasim = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_RICEVUTA_ASINCRONA_ASIMMETRICA_ABILITATO;
				}

				if (xsd == null) {
					if(porteDelegateCore.isSinglePdD()){
						Configurazione config = porteDelegateCore.getConfigurazioneGenerale();
						if(config.getValidazioneContenutiApplicativi()!=null){
							if(config.getValidazioneContenutiApplicativi().getStato()!=null){
								xsd = config.getValidazioneContenutiApplicativi().getStato().toString();
							}
							if(config.getValidazioneContenutiApplicativi().getTipo()!=null){
								tipoValidazione = config.getValidazioneContenutiApplicativi().getTipo().toString();
							}
							if(StatoFunzionalita.ABILITATO.equals(config.getValidazioneContenutiApplicativi().getAcceptMtomMessage())){
								applicaMTOM = Costanti.CHECK_BOX_ENABLED_ABILITATO;
							}
						}
					}
				}

				if (xsd == null) {
					xsd = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_XSD_DISABILITATO;
				}
				if (tipoValidazione == null) {
					tipoValidazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_VALIDAZIONE_XSD;
				}

				if (applicaMTOM == null) {
					applicaMTOM = "";
				}

				// i pattern sono i nomi
				dati = porteDelegateHelper.addPorteDelegateToDati(TipoOperazione.ADD, 
						PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_NEW_ID,
						nomePD,
						dati, PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_NEW_ID,
						descr, urlinv, autenticazione, autorizzazione,
						modesp, soggid, soggettiList, soggettiListLabel,
						sp, tiposp, sp, modeservizio, servid, serviziList,
						serviziListLabel, servizio, tiposervizio, servizio,
						modeaz, azid, azioniListLabel, azioniList, azione,
						azione, numAzioni,  stateless, localForward, ricsim, ricasim,
						xsd, tipoValidazione, 0, "", gestBody, gestManifest,
						null, nomeauth, nomeautor,autorizzazioneContenuti,idsogg,protocollo,numSA,statoMessageSecurity,statoMTOM,numCorrelazioneReq,numCorrelazioneRes,
						forceWsdlBased,applicaMTOM,false);

				pd.setDati(dati);


				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE, ForwardParams.ADD());
				//				(mapping.findForward("AddPortaDelegataForm"));
			}
			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.porteDelegateCheckData(TipoOperazione.ADD, "");
			if (!isOk) {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTI, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
						new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + tmpTitle,
								PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST,
								new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
								),
								new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null)
						);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteDelegateHelper.addPorteDelegateToDati(TipoOperazione.ADD,
						PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_NEW_ID,
						nomePD,
						dati, PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_NEW_ID,
						descr, urlinv, autenticazione, autorizzazione,
						modesp, soggid, soggettiList, soggettiListLabel,
						sp, tiposp, sp, modeservizio, servid, serviziList,
						serviziListLabel, servizio, tiposervizio, servizio,
						modeaz, azid, azioniListLabel, azioniList, azione,
						azione, numAzioni, stateless, localForward, ricsim, ricasim,
						xsd, tipoValidazione, 0, "", gestBody, gestManifest,
						null, nomeauth, nomeautor,autorizzazioneContenuti ,idsogg,protocollo,
						numSA,statoMessageSecurity,statoMTOM,numCorrelazioneReq,numCorrelazioneRes,
						forceWsdlBased,applicaMTOM,false);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE, ForwardParams.ADD());
			}

			// Inserisco la porta delegata nel db
			if (modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				sp = soggid.split("/")[1];
				tiposp = soggid.split("/")[0];
			}
			if (modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				servizio = servid.split("/")[1];
				tiposervizio = servid.split("/")[0];
			}
			if ((modeaz != null) && modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				azione = "";
			} else {
				azid = "";
			}

			PortaDelegata portaDelegata = new PortaDelegata();
			portaDelegata.setNome(nomePD);
			portaDelegata.setDescrizione(descr);
			if(porteDelegateCore.isShowPortaDelegataUrlInvocazione()==false){
				portaDelegata.setLocation(nomePD);
			}
			else if(urlinv!=null && !"".equals(urlinv)){
				portaDelegata.setLocation(urlinv);
			}
			if (autenticazione == null ||
					!autenticazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTENTICAZIONE_CUSTOM))
				portaDelegata.setAutenticazione(autenticazione);
			else
				portaDelegata.setAutenticazione(nomeauth);
			if (autorizzazione == null || 
					!autorizzazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_AUTORIZZAZIONE_CUSTOM))
				portaDelegata.setAutorizzazione(autorizzazione);
			else
				portaDelegata.setAutorizzazione(nomeautor);
			if (stateless !=null && !stateless.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_STATELESS_DEFAULT))
				portaDelegata.setStateless(StatoFunzionalita.toEnumConstant(stateless));
			if (PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_BODY_ALLEGA.equals(gestBody))
				portaDelegata.setAllegaBody(StatoFunzionalita.toEnumConstant(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_ABILITATO));
			else
				portaDelegata.setAllegaBody(StatoFunzionalita.toEnumConstant(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_DISABILITATO));
			if (PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_GEST_BODY_SCARTA.equals(gestBody))
				portaDelegata.setScartaBody(StatoFunzionalita.toEnumConstant(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_ABILITATO));
			else
				portaDelegata.setScartaBody(StatoFunzionalita.toEnumConstant(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_DISABILITATO));
			if (gestManifest !=null && !gestManifest.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_STATELESS_DEFAULT))
				portaDelegata.setGestioneManifest(StatoFunzionalita.toEnumConstant(gestManifest));
			portaDelegata.setRicevutaAsincronaSimmetrica(StatoFunzionalita.toEnumConstant(ricsim));
			portaDelegata.setRicevutaAsincronaAsimmetrica(StatoFunzionalita.toEnumConstant(ricasim));
			portaDelegata.setLocalForward(StatoFunzionalita.toEnumConstant(localForward));

			PortaDelegataSoggettoErogatore pdSogg = new PortaDelegataSoggettoErogatore();
			IDSoggetto idSoggettoErogatore = new IDSoggetto(tiposp, sp);
			if (modesp.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				pdSogg.setId(soggettiCore.getSoggettoRegistro(idSoggettoErogatore).getId());
				if(pdSogg.getId()<=0){
					pdSogg.setId(-2l);
				}
			}
			pdSogg.setNome(sp);
			pdSogg.setTipo(tiposp);
			pdSogg.setIdentificazione(IdentificazioneView.view2db_soggettoErogatore(modesp));
			// se l'identificazioe e' urlbased/contentbased ho il pattern nella
			// variabile utilizzata per il nome
			pdSogg.setPattern(sp);

			portaDelegata.setSoggettoErogatore(pdSogg);

			PortaDelegataServizio pdServizio = new PortaDelegataServizio();
			AccordoServizioParteSpecifica asps = null;
			idServizio = null;
			if (modeservizio.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_REGISTER_INPUT)) {
				idServizio = new IDServizio(idSoggettoErogatore, tiposervizio, servizio);
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
			if (((modeaz != null) &&
					(!azione.equals("") || 
							modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_AZ_INPUT_BASED) ||
							modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_AZ_SOAP_ACTION_BASED) ||
							modeaz.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODE_WSDL_BASED))) ||
							!azid.equals("")) {
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
							throw new Exception("Accordo di servizio parte specifica ["+idServizio.toString()+"] possiede un port type ["+asps.getPortType()+"] che non risulta essere registrato nell'accordo di servizio parte comune ["+asps.getAccordoServizioParteComune()+"]");
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
				}else {
					pdAzione.setForceWsdlBased(null);
				}

				portaDelegata.setAzione(pdAzione);
			}

			// soggetto proprietario
			SoggettoCtrlStat soggettoCS = soggettiCore.getSoggettoCtrlStat(soggInt);
			portaDelegata.setIdSoggetto(soggettoCS.getId());
			portaDelegata.setTipoSoggettoProprietario(soggettoCS.getTipo());
			portaDelegata.setNomeSoggettoProprietario(soggettoCS.getNome());

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

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteDelegateCore.performCreateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.PORTE_DELEGATE_BY_SOGGETTO;
			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
			List<PortaDelegata> lista = porteDelegateCore.porteDelegateList(soggInt, ricerca);

			porteDelegateHelper.preparePorteDelegateList(ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE, ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE, 
					ForwardParams.ADD());
		}  
	}
}
