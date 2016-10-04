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

package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.PortaDelegataServizio;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddTipologia;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.InterfaceType;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;

/**
 * serviziChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaChange extends Action {



	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);

		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();

		try {
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			ConnettoriHelper connettoriHelper = new ConnettoriHelper(request, pd, session);

			String id = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			// int idInt = Integer.parseInt(id);
			String nomeservizio = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO);
			String tiposervizio = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO);
			String accordo = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO);
			//			String accordoLabel = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO_LABEL);
			String servcorr = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_SERVIZIO_CORRELATO);
			// String servpub = request.getParameter("servpub");
			//String endpointtype = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE );
			
			String endpointtype = connettoriHelper.readEndPointType();
			String tipoconn = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO );
			String autenticazioneHttp = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
			String user = null;
			String password = null;
			
			String connettoreDebug = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);
			
			// http
			String url = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL  );
			if(TipiConnettore.HTTP.toString().equals(endpointtype)){
				user = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_USERNAME);
				password = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_PASSWORD);
			}
			
			// jms
			String nome = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			String tipo = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			String initcont = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			String urlpgk = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			String provurl = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			String connfact = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			String sendas = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
			if(TipiConnettore.JMS.toString().equals(endpointtype)){
				user = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
				password = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			}
			
			// https
			String httpsurl = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_URL);
			String httpstipologia = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE );
			String httpshostverifyS = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			boolean httpshostverify = false;
			if (httpshostverifyS != null && httpshostverifyS.equals(Costanti.CHECK_BOX_ENABLED))
				httpshostverify = true;
			String httpspath = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION );
			String httpstipo = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			String httpspwd = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			String httpsalgoritmo = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			String httpsstatoS = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			boolean httpsstato = false;
			if (httpsstatoS != null && httpsstatoS.equals(Costanti.CHECK_BOX_ENABLED))
				httpsstato = true;
			String httpskeystore = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			String httpspwdprivatekeytrust = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			String httpspathkey = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			String httpstipokey = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			String httpspwdkey = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			String httpspwdprivatekey = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			String httpsalgoritmokey = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			if(TipiConnettore.HTTPS.toString().equals(endpointtype)){
				user = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_USERNAME);
				password = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_PASSWORD);
			}
			
			
			String profilo = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROFILO);
			
			Boolean privato = (request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PRIVATO) != null)
					&& Costanti.CHECK_BOX_ENABLED.equals(request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PRIVATO)) ? true : false;
			String portType = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE);
			String descrizione = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_DESCRIZIONE);
			String statoPackage = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE);
			
			String nome_aps = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_APS);
			String versione = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE);
			String backToStato = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_RIPRISTINA_STATO);
			String actionConfirm = request.getParameter(Costanti.PARAMETRO_ACTION_CONFIRM);

			boolean validazioneDocumenti = true;
			String tmpValidazioneDocumenti = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
			if(ServletUtils.isEditModeInProgress(request)){
				// primo accesso alla servlet
				if(tmpValidazioneDocumenti!=null){
					if(Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_ENABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
						validazioneDocumenti = true;
					}
					else if("false".equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_DISABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
						validazioneDocumenti = false;
					}
				}else{
					validazioneDocumenti = true;
				}
			}else{
				if(Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_ENABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
					validazioneDocumenti = true;
				}
				else{
					validazioneDocumenti = false;
				}
			}

			// Preparo il menu
			apsHelper.makeMenu();

			String[] ptList = null;
			// Prendo nome, tipo e provider dal db
			// Prendo la lista di soggetti e la metto in un array
			// Prendo la lista di accordi e la metto in un array
			String tmpTitle = tiposervizio + "/" + nomeservizio;
			String provider = "", provString = "";
			String[] soggettiList = null;
			String[] soggettiListLabel = null;
			String[] accordiList = null;
			String[] accordiListLabel = null;
			AccordiServizioParteSpecificaCore apsCore = null;
			SoggettiCore soggettiCore = null;
			AccordiServizioParteComuneCore apcCore = null;
			PorteApplicativeCore porteApplicativeCore = null;
			PorteDelegateCore porteDelegateCore = null;
			String nomeSoggettoErogatore = "";
			String tipoSoggettoErogatore = "";
			AccordoServizioParteSpecifica asps = null;
			Servizio servizio = null;

			String oldnomeaccordo = null;
			String oldversioneaccordo = null;
			String oldtiposervizio = null;
			String oldnomeservizio = null;
			String oldtiposoggetto = null;
			String oldnomesoggetto = null;
			String oldStatoPackage = null;

			boolean accordoPrivato = false;
			String uriAccordo = null;

			Soggetto soggettoErogatoreID = null;
			String protocollo = null;
			List<String> versioniProtocollo = null;
			List<String> tipiSoggettiCompatibiliAccordo = null;
			List<String> tipiServizioCompatibiliAccordo = null;

			apsCore = new AccordiServizioParteSpecificaCore();
			soggettiCore = new SoggettiCore(apsCore);
			apcCore = new AccordiServizioParteComuneCore(apsCore);
			porteApplicativeCore = new PorteApplicativeCore(apsCore);
			porteDelegateCore = new PorteDelegateCore(apsCore);
			PddCore pddCore = new PddCore(apsCore);

			PermessiUtente pu = ServletUtils.getUserFromSession(session).getPermessi();

			boolean generaPACheckSoggetto = true;
			boolean [] permessi = new boolean[2];
			permessi[0] = pu.isServizi();
			permessi[1] = pu.isAccordiCooperazione();
			List<AccordoServizioParteComune> lista =  
					AccordiServizioParteComuneUtilities.accordiListFromPermessiUtente(apcCore, userLogin, new Search(true), permessi);

			if (lista.size() > 0) {
				accordiList = new String[lista.size()];
				accordiListLabel = new String[lista.size()];
				int i = 0;
				for (AccordoServizioParteComune as : lista) {
					accordiList[i] = as.getId().toString();
					IDSoggetto soggettoReferente = null;
					int idReferente = -1;
					if(as.getSoggettoReferente()!=null && as.getSoggettoReferente().getId()!=null)
						idReferente = as.getSoggettoReferente().getId().intValue();

					if(idReferente>0){
						Soggetto sRef = soggettiCore.getSoggettoRegistro(idReferente);
						soggettoReferente = new IDSoggetto();
						soggettoReferente.setTipo(sRef.getTipo());
						soggettoReferente.setNome(sRef.getNome());
					}
					accordiListLabel[i] = idAccordoFactory.getUriFromValues(as.getNome(),soggettoReferente,as.getVersione());
					i++;
				}
			}
			//				}

			// Servizio
			asps = apsCore.getAccordoServizioParteSpecifica(Long.parseLong(id));
			servizio = asps.getServizio();

			Boolean isConnettoreCustomUltimaImmagineSalvata = servizio.getConnettore().getCustom();
			
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(servizio.getConnettore(), ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_CHANGE, apsCore, 
							request, session, (endpointtype==null), endpointtype); // uso endpointtype per capire se è la prima volta che entro
			
			// Lista port-type associati all'accordo di servizio
			// se l'accordo e' selezionato allora prendo quello selezionato
			// altrimenti il primo
			// della lista
			AccordoServizioParteComune as = null;
			if (accordo != null && !"".equals(accordo)) {
				as = apcCore.getAccordoServizio(Long.parseLong(accordo));
			} else {
				as = apcCore.getAccordoServizio(idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
				portType = asps.getPortType();
			}

			// Lista di Accordi Compatibili
			List<AccordoServizioParteComune> asParteComuneCompatibili = null;
			try{
				asParteComuneCompatibili = apsCore.findAccordiParteComuneBySoggettoAndNome(as.getNome(), 
						new IDSoggetto(as.getSoggettoReferente().getTipo(), as.getSoggettoReferente().getNome()));
			}catch(Exception e){
				ControlStationCore.logError("Errore durante la ricerca degli accordi parte comune compatibili", e);
			}
			
			// Versione
			protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(as.getSoggettoReferente().getTipo());
			versioniProtocollo = apsCore.getVersioniProtocollo(protocollo);
			tipiSoggettiCompatibiliAccordo = soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);
			tipiServizioCompatibiliAccordo = apsCore.getTipiServiziGestitiProtocollo(protocollo);

			// calcolo soggetti compatibili con accordi
			List<Soggetto> list = null;
			if(apsCore.isVisioneOggettiGlobale(userLogin)){
				list = soggettiCore.soggettiRegistroList(null, new Search(true));
			}else{
				list = soggettiCore.soggettiRegistroList(userLogin, new Search(true));
			}

			if (list.size() > 0) {
				List<String> soggettiListTmp = new ArrayList<String>();
				List<String> soggettiListLabelTmp = new ArrayList<String>();
				for (Soggetto soggetto : list) {
					if(tipiSoggettiCompatibiliAccordo.contains(soggetto.getTipo())){
						soggettiListTmp.add(soggetto.getId().toString());
						soggettiListLabelTmp.add(soggetto.getTipo() + "/" + soggetto.getNome());
					}
				}
				soggettiList = soggettiListTmp.toArray(new String[1]);
				soggettiListLabel = soggettiListLabelTmp.toArray(new String[1]);
			}


			// if (provider != null && !provider.equals(""))
			// {
			// long idErogatore = Long.parseLong(provider);
			// Soggetto soggetto =
			// core.getSoggettoRegistro(idErogatore);
			// nomeSoggettoErogatore = soggetto.getNome();
			// tipoSoggettoErogatore = soggetto.getTipo();
			// provString = tipoSoggettoErogatore+"/"+nomeSoggettoErogatore;
			// }

			nomeSoggettoErogatore = servizio.getNomeSoggettoErogatore();
			tipoSoggettoErogatore = servizio.getTipoSoggettoErogatore();
			oldnomeaccordo = asps.getNome();
			oldversioneaccordo = asps.getVersione();
			oldtiposervizio = servizio.getTipo();
			oldnomeservizio = servizio.getNome();
			oldtiposoggetto = servizio.getTipoSoggettoErogatore();
			oldnomesoggetto = servizio.getNomeSoggettoErogatore();
			provString = tipoSoggettoErogatore + "/" + nomeSoggettoErogatore;
			oldStatoPackage = asps.getStatoPackage();		

			// aggiorno tmpTitle
			String tmpVersione = asps.getVersione();
			if(apsCore.isShowVersioneAccordoServizioParteSpecifica()==false){
				tmpVersione = null;
			}
			tmpTitle = idAccordoFactory.getUriFromValues(asps.getNome(), 
					tipoSoggettoErogatore, nomeSoggettoErogatore, 
					tmpVersione);

			soggettoErogatoreID = soggettiCore.getSoggettoRegistro(new IDSoggetto(tipoSoggettoErogatore,nomeSoggettoErogatore));


			if(as!=null){
				accordoPrivato = as.getPrivato()!=null && as.getPrivato();
				uriAccordo = idAccordoFactory.getUriFromAccordo(as);

				if( apsCore.isShowCorrelazioneAsincronaInAccordi() ){
					if (portType != null && !"".equals(portType) && !"-".equals(portType)){
						PortType pt = null;
						for(int i=0; i<as.sizePortTypeList(); i++){
							if(portType.equals(as.getPortType(i).getNome())){
								pt = as.getPortType(i);
								break;
							}
						}
						boolean servizioCorrelato = false;
						if(pt!=null){
							for(int i=0; i<pt.sizeAzioneList(); i++){
								Operation op = pt.getAzione(i);
								if(op.getCorrelataServizio()!=null && !pt.getNome().equals(op.getCorrelataServizio()) && op.getCorrelata()!=null){
									servizioCorrelato = true;
									break;
								}
							}
						}
						if(servizioCorrelato){
							servcorr=Costanti.CHECK_BOX_ENABLED;
						}
						else{
							servcorr=Costanti.CHECK_BOX_DISABLED;
						}							
					}
				}
			}

			List<PortType> portTypes = apcCore.accordiPorttypeList(as.getId().intValue(), new Search(true));
			if (portTypes.size() > 0) {
				ptList = new String[portTypes.size() + 1];
				ptList[0] = "-";
				int i = 1;
				for (Iterator<PortType> iterator = portTypes.iterator(); iterator.hasNext();) {
					PortType portType2 = iterator.next();
					ptList[i] = portType2.getNome();
					i++;
				}
			}

			//se passo dal link diretto di ripristino stato imposto il nuovo stato
			if(backToStato != null)
				statoPackage = backToStato;


			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if (ServletUtils.isEditModeInProgress(request)) {
				// setto la barra del titolo
				List<Parameter> lstParm = new ArrayList<Parameter>();

				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				lstParm.add(new Parameter(tmpTitle, null));

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );


				nomeSoggettoErogatore = servizio.getNomeSoggettoErogatore();
				tipoSoggettoErogatore = servizio.getTipoSoggettoErogatore();
				provString = tipoSoggettoErogatore + "/" + nomeSoggettoErogatore;
				if (servcorr == null) {
					if(TipologiaServizio.CORRELATO.equals(servizio.getTipologiaServizio()))
						servcorr = Costanti.CHECK_BOX_ENABLED;
					else
						servcorr = Costanti.CHECK_BOX_DISABLED;
				}
				if (accordo == null)
					accordo = asps.getIdAccordo().toString();
				if (profilo == null)
					profilo = asps.getVersioneProtocollo();
				if (descrizione == null)
					descrizione = asps.getDescrizione();
				if(statoPackage==null)
					statoPackage = asps.getStatoPackage();

				if(nome_aps==null)
					nome_aps=asps.getNome();
				if(versione==null)
					versione=asps.getVersione();

				if(tiposervizio==null){
					if(asps.getServizio()!=null){
						tiposervizio = asps.getServizio().getTipo();
					}
				}
				if(nomeservizio==null){
					if(asps.getServizio()!=null){
						nomeservizio = asps.getServizio().getNome();
					}
				}


				// Controllo se il soggetto erogare appartiene ad una pdd di tipo operativo.
				IDSoggetto idSoggettoEr = new IDSoggetto( tipoSoggettoErogatore,  nomeSoggettoErogatore);
				Soggetto soggetto = soggettiCore.getSoggettoRegistro(idSoggettoEr );

				if (soggetto.getPortaDominio() != null) {
					String nomePdd = soggetto.getPortaDominio();

					PdDControlStation portaDominio = pddCore.getPdDControlStation(nomePdd);

					if(portaDominio.getTipo().equals(PddTipologia.ESTERNO.toString()))
						generaPACheckSoggetto = false;

				} else {
					// se non ho una porta di domini non devo generare la porta applicativa
					generaPACheckSoggetto  =false;
				}


				Connettore connettore = servizio.getConnettore();

				// if(endpointtype==null || endpointtype.equals(""))
				// endpointtype = connettore.getTipo();
				//			
				// Map<String,String> properties =
				// connettore.getProperties();

				if ((endpointtype == null) || (url == null) || (nome == null)) {
					Map<String, String> props = connettore.getProperties();

					if (endpointtype == null) {
						if ((connettore.getCustom()!=null && connettore.getCustom()) && !connettore.getTipo().equals(CostantiDB.CONNETTORE_TIPO_HTTPS)) {
							endpointtype = ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM;
							tipoconn = connettore.getTipo();
						} else
							endpointtype = connettore.getTipo();
					}
					
					if(connettoreDebug==null && props!=null){
						String v = props.get(CostantiDB.CONNETTORE_DEBUG);
						if(v!=null){
							if("true".equals(v)){
								connettoreDebug = Costanti.CHECK_BOX_ENABLED;
							}
							else{
								connettoreDebug = Costanti.CHECK_BOX_DISABLED;
							}
						}
					}
					
					if (url == null) {
						url = props.get(CostantiDB.CONNETTORE_HTTP_LOCATION);
					}
					if (nome == null) {
						nome = props.get(CostantiDB.CONNETTORE_JMS_NOME);
						user = props.get(CostantiDB.CONNETTORE_USER);
						password = props.get(CostantiDB.CONNETTORE_PWD);
						tipo = props.get(CostantiDB.CONNETTORE_JMS_TIPO);
						initcont = props.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL);
						urlpgk = props.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG);
						provurl = props.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL);
						connfact = props.get(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY);
						sendas = props.get(CostantiDB.CONNETTORE_JMS_SEND_AS);
					}
					
					autenticazioneHttp = connettoriHelper.getAutenticazioneHttp(autenticazioneHttp, endpointtype, user);
					
					if (httpsurl == null) {
						httpsurl = props.get(CostantiDB.CONNETTORE_HTTPS_LOCATION);
						httpstipologia = props.get(CostantiDB.CONNETTORE_HTTPS_SSL_TYPE);
						httpshostverifyS = props.get(CostantiDB.CONNETTORE_HTTPS_HOSTNAME_VERIFIER);
						if(httpshostverifyS!=null){
							httpshostverify = Boolean.valueOf(httpshostverifyS);
						}
						httpspath = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
						httpstipo = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_TYPE);
						httpspwd = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
						httpsalgoritmo = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
						httpspwdprivatekeytrust = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_PASSWORD);
						httpspathkey = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_LOCATION);
						httpstipokey = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_TYPE);
						httpspwdkey = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
						httpspwdprivatekey = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_PASSWORD);
						httpsalgoritmokey = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
						if (httpspathkey == null) {
							httpsstato = false;
							httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
						} else {
							httpsstato = true;
							if (httpspathkey.equals(httpspath) &&
									httpstipokey.equals(httpstipo) &&
									httpspwdkey.equals(httpspwd))
								httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
							else
								httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI;
						}
					}
				}

				// default
				if(httpsalgoritmo==null || "".equals(httpsalgoritmo)){
					httpsalgoritmo = TrustManagerFactory.getDefaultAlgorithm();
				}
				if(httpsalgoritmokey==null || "".equals(httpsalgoritmokey)){
					httpsalgoritmokey = KeyManagerFactory.getDefaultAlgorithm();
				}
				if(httpstipologia==null || "".equals(httpstipologia)){
					httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_SSLV3_TYPE;
				}
				if(httpshostverifyS==null || "".equals(httpshostverifyS)){
					httpshostverifyS = Costanti.CHECK_BOX_ENABLED_TRUE;
					httpshostverify = true;
				}

				portType = (portType != null && !"".equals(portType) ? portType : asps.getPortType());

				privato = asps.getPrivato()!=null && asps.getPrivato();

				if(backToStato == null){
					// preparo i campi
					Vector<DataElement> dati = new Vector<DataElement>();
					dati.addElement(ServletUtils.getDataElementForEditModeFinished());

					dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, null, null, dati);

					dati = apsHelper.addServiziToDati(dati, nomeservizio, tiposervizio, provider, provString,
							soggettiList, soggettiListLabel, accordo, accordiList, accordiListLabel, servcorr, "", "", TipoOperazione.CHANGE, 
							id, tipiServizioCompatibiliAccordo, profilo, portType, ptList,  privato,uriAccordo, descrizione, 
							soggettoErogatoreID.getId(),statoPackage,oldStatoPackage,nome_aps
							,versione,versioniProtocollo,validazioneDocumenti,
							null,null,null,protocollo,generaPACheckSoggetto,asParteComuneCompatibili);

					dati = connettoriHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp,  null,
							url,nome, tipo, user, password, initcont, urlpgk,
							provurl, connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,TipoOperazione.CHANGE,
							httpsurl, httpstipologia, httpshostverify,
							httpspath, httpstipo, httpspwd, httpsalgoritmo,
							httpsstato, httpskeystore,
							httpspwdprivatekeytrust, httpspathkey,
							httpstipokey, httpspwdkey, httpspwdprivatekey,
							httpsalgoritmokey, tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, id,
							nomeservizio, tiposervizio, null, null, null,
							null, oldStatoPackage, true,
							isConnettoreCustomUltimaImmagineSalvata, listExtendedConnettore);

					pd.setDati(dati);

					if(apsCore.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(asps.getStatoPackage())){
						pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
					}

					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
							ForwardParams.CHANGE());
				}
			}

			if (InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(session).getInterfaceType())) {
				// il nome del servizio e' quello del porttype selezionato
				nomeservizio = portType;
			}

			// Controlli sui campi immessi
			boolean isOk = apsHelper.serviziCheckData(TipoOperazione.CHANGE, soggettiList,
					accordiList, servizio.getNome(), servizio.getTipo(),
					nomeservizio, tiposervizio, provider,
					nomeSoggettoErogatore, tipoSoggettoErogatore, accordo,
					servcorr, endpointtype, url, nome, tipo, user, password,
					initcont, urlpgk, provurl, connfact, sendas, "", "", id,
					profilo, portType,accordoPrivato,privato,
					httpsurl, httpstipologia, httpshostverify,
					httpspath, httpstipo, httpspwd, httpsalgoritmo,
					httpsstato, httpskeystore,
					httpspwdprivatekeytrust, httpspathkey,
					httpstipokey, httpspwdkey, httpspwdprivatekey,
					httpsalgoritmokey, tipoconn,nome_aps,versione,validazioneDocumenti,null,backToStato,autenticazioneHttp,
					listExtendedConnettore);
			if (!isOk) {
				// setto la barra del titolo
				List<Parameter> lstParm = new ArrayList<Parameter>();

				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				lstParm.add(new Parameter(tmpTitle, null));

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				portType = (portType != null && !"".equals(portType) ? portType : asps.getPortType());

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, null, null, dati);

				dati = apsHelper.addServiziToDati(dati, nomeservizio, tiposervizio, provider, provString, soggettiList, 
						soggettiListLabel, accordo, accordiList, accordiListLabel, servcorr, "", "", TipoOperazione.CHANGE, 
						id, tipiServizioCompatibiliAccordo, profilo, portType, ptList, privato,uriAccordo, descrizione, soggettoErogatoreID.getId(),
						statoPackage,oldStatoPackage,nome_aps,versione,versioniProtocollo,validazioneDocumenti,
						null,null,null,protocollo,generaPACheckSoggetto,asParteComuneCompatibili);

				dati = connettoriHelper.addEndPointToDati(dati, connettoreDebug,  endpointtype, autenticazioneHttp, null, 
						url, nome,
						tipo, user, password, initcont, urlpgk, provurl,
						connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,TipoOperazione.CHANGE, httpsurl, httpstipologia,
						httpshostverify, httpspath, httpstipo, httpspwd,
						httpsalgoritmo, httpsstato, httpskeystore,
						httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, httpspwdprivatekey,
						httpsalgoritmokey, tipoconn,AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, id,
						nomeservizio, tiposervizio, null, null, null,
						null, oldStatoPackage, true,
						isConnettoreCustomUltimaImmagineSalvata, listExtendedConnettore);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
						ForwardParams.CHANGE());

			}

			// I dati dell'utente sono validi, se ha scelto di modificare lo stato da finale ad operativo visualizzo la schermata di conferma
			if( actionConfirm == null){
				if(  backToStato != null){
					// setto la barra del titolo
					List<Parameter> lstParm = new ArrayList<Parameter>();

					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
					lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
					lstParm.add(new Parameter(tmpTitle, null));

					// setto la barra del titolo
					ServletUtils.setPageDataTitle(pd, lstParm );

					portType = (portType != null && !"".equals(portType) ? portType : asps.getPortType());

					// preparo i campi
					Vector<DataElement> dati = new Vector<DataElement>();

					dati.addElement(ServletUtils.getDataElementForEditModeFinished());
					
					dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, null, null, dati);
					
					dati = apsHelper.addServiziToDatiAsHidden(dati, nomeservizio, tiposervizio, provider, provString, soggettiList, 
							soggettiListLabel, accordo, accordiList, accordiListLabel, servcorr, "", "", TipoOperazione.CHANGE, 
							id, tipiServizioCompatibiliAccordo, profilo, portType, ptList, privato,uriAccordo, descrizione, soggettoErogatoreID.getId(),
							statoPackage,oldStatoPackage,nome_aps,versione,versioniProtocollo,validazioneDocumenti,
							null,null,null,protocollo,generaPACheckSoggetto);

					dati = connettoriHelper.addEndPointToDatiAsHidden(dati, endpointtype, url, nome,
							tipo, user, password, initcont, urlpgk, provurl,
							connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,TipoOperazione.CHANGE, httpsurl, httpstipologia,
							httpshostverify, httpspath, httpstipo, httpspwd,
							httpsalgoritmo, httpsstato, httpskeystore,
							httpspwdprivatekeytrust, httpspathkey,
							httpstipokey, httpspwdkey, httpspwdprivatekey,
							httpsalgoritmokey, tipoconn,AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, id,
							nomeservizio, tiposervizio, null, null, null,
							null, oldStatoPackage);
					
					
					String msg = "&Egrave; stato richiesto di ripristinare lo stato dell''accordo [{0}] in operativo. Tale operazione permetter&agrave; successive modifiche all''accordo. Vuoi procedere?";
					
					pd.setMessage(MessageFormat.format(msg, uriAccordo));
					
					pd.setDati(dati);
					
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

					return ServletUtils.getStrutsForwardEditModeConfirm(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
							ForwardParams.CHANGE());
				}
			}

			// Modifico i dati del servizio nel db

			asps = apsCore.getAccordoServizioParteSpecifica(Long.parseLong(id));
			servizio = asps.getServizio();

			// idErogatoreServizio
			Soggetto soggettoErogatore = soggettiCore.getSoggettoRegistro(new IDSoggetto(servizio.getTipoSoggettoErogatore(), servizio.getNomeSoggettoErogatore()));

			// Old dati for update
			servizio.setOldNomeForUpdate(servizio.getNome());
			servizio.setOldTipoForUpdate(servizio.getTipo());

			// nuovi valori
			servizio.setNome(nomeservizio);
			servizio.setTipo(tiposervizio);
			asps.setDescrizione(descrizione);
			if ("-".equals(profilo) == false)
				asps.setVersioneProtocollo(profilo);
			else
				asps.setVersioneProtocollo(null);

			asps.setPrivato(privato);

			if (portType != null && !"".equals(portType) && !"-".equals(portType))
				asps.setPortType(portType);
			else
				asps.setPortType(null);

			// Connettore
			Connettore newConnettore = new Connettore();
			newConnettore.setId(servizio.getConnettore().getId());
			newConnettore.setNome(servizio.getConnettore().getNome());
			if (endpointtype.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM))
				newConnettore.setTipo(tipoconn);
			else
				newConnettore.setTipo(endpointtype);

			String oldConnT = servizio.getConnettore().getTipo();
			if ((servizio.getConnettore().getCustom()!=null && servizio.getConnettore().getCustom()) && !servizio.getConnettore().getTipo().equals(CostantiDB.CONNETTORE_TIPO_HTTPS)){
				oldConnT = ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM;
				// mantengo vecchie proprieta connettore custom
				for(int i=0; i<servizio.getConnettore().sizePropertyList(); i++){
					newConnettore.addProperty(servizio.getConnettore().getProperty(i));
				}
			}
			connettoriHelper.fillConnettore(newConnettore, connettoreDebug, endpointtype, oldConnT,
					tipoconn, url, nome,
					tipo, user, password, initcont, urlpgk, provurl,
					connfact, sendas, httpsurl, httpstipologia,
					httpshostverify, httpspath, httpstipo, httpspwd,
					httpsalgoritmo, httpsstato, httpskeystore,
					httpspwdprivatekeytrust, httpspathkey,
					httpstipokey, httpspwdkey, httpspwdprivatekey,
					httpsalgoritmokey,
					listExtendedConnettore);

			servizio.setConnettore(newConnettore);

			// Accordo
			as = apcCore.getAccordoServizio(Long.parseLong(accordo));
			asps.setAccordoServizioParteComune(idAccordoFactory.getUriFromAccordo(as));
			asps.setIdAccordo(as.getId());

			servizio.setTipologiaServizio(((servcorr != null) && servcorr.equals(Costanti.CHECK_BOX_ENABLED)) ? TipologiaServizio.CORRELATO : TipologiaServizio.NORMALE);

			servizio.setOldNomeSoggettoErogatoreForUpdate(oldnomesoggetto);
			servizio.setOldTipoSoggettoErogatoreForUpdate(oldtiposoggetto);
			servizio.setOldNomeForUpdate(oldnomeservizio);
			servizio.setOldTipoForUpdate(oldtiposervizio);

			// Accordo di servizio parte specifica
			asps.setNome(nome_aps);
			if(apsCore.isShowPulsantiImportExport()){
				asps.setVersione(versione);
			}else{
				asps.setVersione("1");
			}
			
			asps.setOldNomeAccordoForUpdate(oldnomeaccordo);
			asps.setOldVersioneAccordoForUpdate(oldversioneaccordo);

			// stato
			asps.setStatoPackage(statoPackage);


			// Check stato
			if(apsCore.isShowGestioneWorkflowStatoDocumenti()){

				try{
					apsCore.validaStatoAccordoServizioParteSpecifica(asps);
				}catch(ValidazioneStatoPackageException validazioneException){

					// Setto messaggio di errore
					pd.setMessage(validazioneException.toString());

					// setto la barra del titolo
					List<Parameter> lstParm = new ArrayList<Parameter>();

					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
					lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
					lstParm.add(new Parameter(tmpTitle, null));

					// setto la barra del titolo
					ServletUtils.setPageDataTitle(pd, lstParm );

					portType = (portType != null && !"".equals(portType) ? portType : asps.getPortType());

					// preparo i campi
					Vector<DataElement> dati = new Vector<DataElement>();

					dati.addElement(ServletUtils.getDataElementForEditModeFinished());

					dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, null, null, dati);

					dati = apsHelper.addServiziToDati(dati, nomeservizio, tiposervizio, provider, provString, soggettiList,
							soggettiListLabel, accordo, accordiList, accordiListLabel, servcorr, "", "", TipoOperazione.CHANGE, 
							id, tipiServizioCompatibiliAccordo, profilo, portType, ptList, privato,uriAccordo, descrizione, 
							soggettoErogatoreID.getId(),statoPackage,oldStatoPackage,nome_aps,versione,versioniProtocollo,validazioneDocumenti,
							null,null,null,protocollo,generaPACheckSoggetto,asParteComuneCompatibili);

					dati = connettoriHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, null, 
							url,
							nome, tipo, user, password, initcont, urlpgk,
							provurl, connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,TipoOperazione.CHANGE, httpsurl,
							httpstipologia, httpshostverify, httpspath,
							httpstipo, httpspwd, httpsalgoritmo, httpsstato,
							httpskeystore, httpspwdprivatekeytrust,
							httpspathkey, httpstipokey, httpspwdkey,
							httpspwdprivatekey, httpsalgoritmokey, tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, id,
							nomeservizio, tiposervizio, null, null, null,
							null,
							oldStatoPackage, true,
							isConnettoreCustomUltimaImmagineSalvata, listExtendedConnettore);

					pd.setDati(dati);

					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardGeneralError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
							ForwardParams.CHANGE());
				}
			}


			String superUser = ServletUtils.getUserLoginFromSession(session);

			// Se sono cambiati il tipo o il nome allora devo aggiornare
			// anche le porte delegate e porte applicative
			List<PortaDelegata> listaPD = new ArrayList<PortaDelegata>();
			List<PortaApplicativa> listaPA = new ArrayList<PortaApplicativa>();
			// Lista di id per tenere traccia delle Porte Delegate inserite
			List<Long> idListPD = new ArrayList<Long>();
			List<Long> idListPA = new ArrayList<Long>();
			if (!servizio.getNome().equals(servizio.getOldNomeForUpdate()) || !servizio.getTipo().equals(servizio.getOldTipoForUpdate())) {
				List<PortaDelegata> tmpListPD = null;
				// recupero lo porte delegate per location
				// e aggiorno il nome e la location
				String locationPrefix = "";
				String locationSuffix = "/" + servizio.getTipoSoggettoErogatore() + servizio.getNomeSoggettoErogatore() + "/" + servizio.getOldTipoForUpdate() + servizio.getOldNomeForUpdate();
				for (Fruitore fruitore : asps.getFruitoreList()) {
					locationPrefix = fruitore.getTipo() + fruitore.getNome();
					String location = locationPrefix + locationSuffix;
					tmpListPD = porteDelegateCore.porteDelegateWithLocationList(location);
					for (PortaDelegata tmpPorta : tmpListPD) {
						Long idPorta = tmpPorta.getId();
						if (!idListPD.contains(idPorta)) {
							// new locationSuffix
							String newLocationSuffix = "/" + servizio.getTipoSoggettoErogatore() + servizio.getNomeSoggettoErogatore() + "/" + servizio.getTipo() + servizio.getNome();
							String newLocation = locationPrefix + newLocationSuffix;
							idListPD.add(idPorta);
							tmpPorta.setOldNomeForUpdate(tmpPorta.getNome());
							tmpPorta.setNome(newLocation);
							tmpPorta.setLocation(newLocation);
							// aggiorno la descrizione della porta
							String descrizionePD = tmpPorta.getDescrizione();
							if (descrizionePD != null && !descrizionePD.equals("")) {
								// pattern descrizione: Invocazione
								// servizio(.*)erogato da(.*) (old
								// tipo/nome soggetto)
								String descrRegex = "Invocazione servizio(.*)erogato da(.*)";
								if (descrizionePD.matches(descrRegex)) {
									descrizionePD = descrizionePD.replaceFirst((servizio.getOldTipoForUpdate() + servizio.getOldNomeForUpdate()), (servizio.getTipo() + servizio.getNome()));
								}

								tmpPorta.setDescrizione(descrizionePD);
							}
							// aggiorno anche il servizio
							PortaDelegataServizio servizioPD = tmpPorta.getServizio();
							servizioPD.setTipo(servizio.getTipo());
							servizioPD.setNome(servizio.getNome());
							tmpPorta.setServizio(servizioPD);

							/*
							 * CONTROLLO PATTERN AZIONE inoltre va
							 * controllato anche il pattern dell'azione
							 * in caso il pattern azione fosse URLBASED
							 * e fosse quello di default allora va
							 * cambiato
							 */
							String regex = "(.*)\\/(.*)\\/(.*)";
							PortaDelegataAzione pdAzione = tmpPorta.getAzione();
							PortaDelegataAzioneIdentificazione identificazione = pdAzione != null ? pdAzione.getIdentificazione() : null;
							String patterAzione = pdAzione != null ? (pdAzione.getPattern() != null ? pdAzione.getPattern() : "") : "";
							String patternAzionePrefix = ".*";
							String patternAzioneSuffix = "/([^/|^?]*).*";
							// se identificazione urlbased procedo con i
							// controlli
							if (PortaDelegataAzioneIdentificazione.URL_BASED.equals(identificazione)) {
								if (patterAzione.startsWith(patternAzionePrefix) && patterAzione.endsWith(patternAzioneSuffix)) {
									int startidx = patternAzionePrefix.length();
									int endidx = patterAzione.lastIndexOf(patternAzioneSuffix);
									String tmp = patterAzione.substring(startidx, endidx);
									// a questo punto ottengo una
									// stringa del tipo
									// (fruitore)/(erogatore)/(servizio)
									// se rispetta la regex allora vuol
									// dire che il pattern azione e'
									// quello di default
									// e devo effettuare i cambiamenti
									if (tmp.matches(regex)) {
										// il nuovo pattern sara' come
										// quello della location di
										// default
										String newPatternAzione = patternAzionePrefix + newLocation + patternAzioneSuffix;
										pdAzione.setPattern(newPatternAzione);
										tmpPorta.setAzione(pdAzione);

									}
								}
							}// fine controllo azione

							listaPD.add(tmpPorta);

						}
					}
				}
				// recupero le porte delegate per id servizio
				// aggiorno il tipo e il nome servizio
				tmpListPD = porteDelegateCore.getPorteDelegateWithServizio(asps.getId());
				for (PortaDelegata tmpPorta : tmpListPD) {
					Long idPorta = tmpPorta.getId();
					// se la porta non e' gia in lista
					if (!idListPD.contains(idPorta)) {
						idListPD.add(idPorta);
						tmpPorta.setOldNomeForUpdate(tmpPorta.getNome());
						PortaDelegataServizio servizioPD = tmpPorta.getServizio();
						servizioPD.setTipo(servizio.getTipo());
						servizioPD.setNome(servizio.getNome());
						tmpPorta.setServizio(servizioPD);
						listaPD.add(tmpPorta);
					}
				}

				// recupero le porte delegate per tipo e nome servizio
				// aggiorno il tipo e il nome servizio
				tmpListPD = porteDelegateCore.getPorteDelegateWithServizio(new Long(0), servizio.getOldTipoForUpdate(), servizio.getOldNomeForUpdate(), soggettoErogatore.getId(), servizio.getTipoSoggettoErogatore(), servizio.getNomeSoggettoErogatore());
				for (PortaDelegata tmpPorta : tmpListPD) {
					Long idPorta = tmpPorta.getId();
					if (!idListPD.contains(idPorta)) {
						idListPD.add(idPorta);
						PortaDelegataServizio servizioPD = tmpPorta.getServizio();
						servizioPD.setTipo(servizio.getTipo());
						servizioPD.setNome(servizio.getNome());
						tmpPorta.setServizio(servizioPD);
						listaPD.add(tmpPorta);
					}

				}

				// recupero le porte applicative per id
				List<PortaApplicativa> tmpListPA = porteApplicativeCore.porteAppWithIdServizio(asps.getId());
				for (PortaApplicativa portaApplicativa : tmpListPA) {
					Long idPA = portaApplicativa.getId();
					if (!idListPA.contains(idPA)) {
						
						idListPA.add(idPA);
						
						PortaApplicativaServizio paServizio = portaApplicativa.getServizio();
						paServizio.setNome(servizio.getNome());
						paServizio.setTipo(servizio.getTipo());
						portaApplicativa.setServizio(paServizio);
						
						String nomePA = portaApplicativa.getNome();
						// se il nome e' quello di default cioe' (erogatore)/(servizio)
						String regex = "(.*)\\/(.*)";
						if (nomePA.matches(regex)) {

							String[] val = nomePA.split("\\/");
							String pat1 = val[0];
							String pat2 = val[1];

							// servizio
							if (pat2.equals(servizio.getOldTipoForUpdate() + servizio.getOldNomeForUpdate())) {
								pat2 = servizio.getTipo() + servizio.getNome();
							}

							String newNome = pat1 + "/" + pat2;
							
							portaApplicativa.setNome(newNome);
							portaApplicativa.setOldNomeForUpdate(nomePA);

							// modifica della descrizione
							String descrizionePA = portaApplicativa.getDescrizione();
							if (descrizionePA != null && !descrizionePA.equals("")) {

								// caso 1
								// pattern descrizione: Invocazione
								// servizio(.*)erogato da(.*) (pat1)
								String descrRegex = "Servizio(.*)erogato da(.*)";
								if (descrizionePA.matches(descrRegex)) {
									descrizionePA = descrizionePA.replaceFirst((servizio.getOldTipoForUpdate() + servizio.getOldNomeForUpdate()), (servizio.getTipo() + servizio.getNome()));
								}

								portaApplicativa.setDescrizione(descrizionePA);
							}
						}// fine controllo nome
						
						listaPA.add(portaApplicativa);
					}
				}
				// recupero porte applicative per tipo/nome servizio
				tmpListPA = porteApplicativeCore.porteAppWithTipoNomeServizio(soggettoErogatore.getId(), servizio.getOldTipoForUpdate(), servizio.getOldNomeForUpdate());
				for (PortaApplicativa portaApplicativa : tmpListPA) {
					Long idPA = portaApplicativa.getId();
					if (!idListPA.contains(idPA)) {
						
						idListPA.add(idPA);
												
						PortaApplicativaServizio paServizio = portaApplicativa.getServizio();
						paServizio.setNome(servizio.getNome());
						paServizio.setTipo(servizio.getTipo());
						portaApplicativa.setServizio(paServizio);
						
						String nomePA = portaApplicativa.getNome();
						// se il nome e' quello di default cioe' (erogatore)/(servizio)
						String regex = "(.*)\\/(.*)";
						if (nomePA.matches(regex)) {

							String[] val = nomePA.split("\\/");
							String pat1 = val[0];
							String pat2 = val[1];

							// servizio
							if (pat2.equals(servizio.getOldTipoForUpdate() + servizio.getOldNomeForUpdate())) {
								pat2 = servizio.getTipo() + servizio.getNome();
							}

							String newNome = pat1 + "/" + pat2;
							
							portaApplicativa.setNome(newNome);
							portaApplicativa.setOldNomeForUpdate(nomePA);

							// modifica della descrizione
							String descrizionePA = portaApplicativa.getDescrizione();
							if (descrizionePA != null && !descrizionePA.equals("")) {

								// caso 1
								// pattern descrizione: Invocazione
								// servizio(.*)erogato da(.*) (pat1)
								String descrRegex = "Servizio(.*)erogato da(.*)";
								if (descrizionePA.matches(descrRegex)) {
									descrizionePA = descrizionePA.replaceFirst((servizio.getOldTipoForUpdate() + servizio.getOldNomeForUpdate()), (servizio.getTipo() + servizio.getNome()));
								}

								portaApplicativa.setDescrizione(descrizionePA);
							}
						}// fine controllo nome
						
						listaPA.add(portaApplicativa);
					}
				}
			}

			List<Object> oggettiDaAggiornare = new ArrayList<Object>();

			// Aggiorno il servizio
			oggettiDaAggiornare.add(asps);

			// Aggiorno le eventuali porte delegate
			for (PortaDelegata portaDelegata : listaPD) {
				oggettiDaAggiornare.add(portaDelegata);
			}

			// aggiorno le eventuali porte applicative
			for (PortaApplicativa portaApplicativa : listaPA) {
				oggettiDaAggiornare.add(portaApplicativa);
			}

			// Se ho cambiato i dati significativi del servizio devo effettuare anche l'update degli accordi di servizio
			// che includono questi servizi come servizi componenti.
			if(  (servizio.getTipo().equals(servizio.getOldTipoForUpdate()) == false)  ||
					(servizio.getNome().equals(servizio.getOldNomeForUpdate()) == false) ||
					(servizio.getTipoSoggettoErogatore().equals(servizio.getOldTipoSoggettoErogatoreForUpdate()) == false) ||
					(servizio.getNomeSoggettoErogatore().equals(servizio.getOldNomeSoggettoErogatoreForUpdate()) == false) ){

				IDServizio idServizioOLD = new IDServizio(servizio.getOldTipoSoggettoErogatoreForUpdate(),servizio.getOldNomeSoggettoErogatoreForUpdate(),
						servizio.getOldTipoForUpdate(),servizio.getOldNomeForUpdate());
				List<AccordoServizioParteComune> ass = apcCore.accordiServizio_serviziComponenti(idServizioOLD);
				for(int i=0; i<ass.size(); i++){
					AccordoServizioParteComune accordoServizioComposto = ass.get(i);
					if(accordoServizioComposto.getServizioComposto()!=null){
						for(int j=0;j<accordoServizioComposto.getServizioComposto().sizeServizioComponenteList();j++){
							if(accordoServizioComposto.getServizioComposto().getServizioComponente(j).getTipoSoggetto().equals(servizio.getOldTipoSoggettoErogatoreForUpdate()) &&
									accordoServizioComposto.getServizioComposto().getServizioComponente(j).getNomeSoggetto().equals(servizio.getOldNomeSoggettoErogatoreForUpdate()) &&
									accordoServizioComposto.getServizioComposto().getServizioComponente(j).getTipo().equals(servizio.getOldTipoForUpdate()) &&
									accordoServizioComposto.getServizioComposto().getServizioComponente(j).getNome().equals(servizio.getOldNomeForUpdate())){
								accordoServizioComposto.getServizioComposto().getServizioComponente(j).setTipoSoggetto(servizio.getTipoSoggettoErogatore());
								accordoServizioComposto.getServizioComposto().getServizioComponente(j).setNomeSoggetto(servizio.getNomeSoggettoErogatore());
								accordoServizioComposto.getServizioComposto().getServizioComponente(j).setTipo(servizio.getTipo());
								accordoServizioComposto.getServizioComposto().getServizioComponente(j).setNome(servizio.getNome());
							}
						}
						oggettiDaAggiornare.add(accordoServizioComposto);
						//System.out.println("As SERVIZIO COMPONENTE ["+IDAccordo.getUriFromAccordo(accordoServizioComposto)+"]");
					}
				}
			}


			// eseguo l'aggiornamento
			apsCore.performUpdateOperation(superUser, apsHelper.smista(), oggettiDaAggiornare.toArray());

			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			//				PermessiUtente pu = ServletUtils.getUserFromSession(session).getPermessi();
			//				
			//				boolean [] permessi = new boolean[2];
			//				permessi[0] = pu.isServizi();
			//				permessi[1] = pu.isAccordiCooperazione();

			List<AccordoServizioParteSpecifica> listaServizi = null;
			if(apsCore.isVisioneOggettiGlobale(superUser)){
				listaServizi = apsCore.soggettiServizioList(null, ricerca,permessi);
			}else{
				listaServizi = apsCore.soggettiServizioList(superUser, ricerca,permessi);
			}

			apsHelper.prepareServiziList(ricerca, listaServizi);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
					ForwardParams.CHANGE());
		}  

	}
}