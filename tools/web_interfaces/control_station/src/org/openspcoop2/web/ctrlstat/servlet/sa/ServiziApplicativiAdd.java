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


package org.openspcoop2.web.ctrlstat.servlet.sa;

import java.util.ArrayList;
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
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.InvocazionePortaGestioneErrore;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.FaultIntegrazioneTipo;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.constants.TipologiaFruizione;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddTipologia;
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
import org.openspcoop2.web.lib.users.dao.InterfaceType;
import org.openspcoop2.web.lib.users.dao.User;

/**
 * servizioApplicativoAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ServiziApplicativiAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_USA_ID_SOGGETTO , session);


	
		try {
			Boolean contaListe = ServletUtils.getContaListeFromSession(session);
			Boolean singlePdD = (Boolean) session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD);

			ServiziApplicativiCore saCore = new ServiziApplicativiCore();
			PddCore pddCore = new PddCore(saCore);
			SoggettiCore soggettiCore = new SoggettiCore(saCore);
			
			ServiziApplicativiHelper saHelper = new ServiziApplicativiHelper(request, pd, session);
			ConnettoriHelper connettoriHelper = new ConnettoriHelper(request, pd, session);
			
			String ruoloFruitore = null; //request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLO_FRUITORE);
			String ruoloErogatore = null; //request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLO_EROGATORE);
			String ruoloSA = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLO_SA);
			if(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_RUOLO_FRUITORE.equals(ruoloSA)){
				ruoloFruitore = TipologiaFruizione.NORMALE.getValue();
				ruoloErogatore = TipologiaErogazione.DISABILITATO.getValue();
			}
			else if(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_RUOLO_EROGATORE.equals(ruoloSA)){
				ruoloErogatore = TipologiaErogazione.TRASPARENTE.getValue();
				ruoloFruitore = TipologiaFruizione.DISABILITATO.getValue();
			}
			else{
				ruoloErogatore = TipologiaErogazione.DISABILITATO.getValue();
				ruoloFruitore = TipologiaFruizione.DISABILITATO.getValue();
			}
//			if(ServletUtils.isCheckBoxEnabled(ruoloFruitore)){
//				ruoloFruitore = TipologiaFruizione.NORMALE.getValue();
//			}
//			else{
//				ruoloFruitore = TipologiaFruizione.DISABILITATO.getValue();
//			}
//			if(ServletUtils.isCheckBoxEnabled(ruoloErogatore)){
//				ruoloErogatore = TipologiaErogazione.TRASPARENTE.getValue();
//			}
//			else{
//				ruoloErogatore = TipologiaErogazione.DISABILITATO.getValue();
//			}
			
			String nome = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME);
			String provider = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			String fault = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT);
			String tipoauthSA = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SA);
			if (tipoauthSA == null) {
				tipoauthSA = ServiziApplicativiCostanti.DEFAULT_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE;
			}
			String utenteSA = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_USERNAME_SA);
			String passwordSA = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_PASSWORD_SA);
			String confpwSA = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_CONFERMA_PASSWORD_SA);
			String subjectSA = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_SUBJECT_SA);
			
			String sbustamentoInformazioniProtocolloRisposta = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RISPOSTA);

			
			String sbustamento = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP);
			String sbustamentoInformazioniProtocolloRichiesta = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RICHIESTA);
			String getmsg = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX);
			
			String invrifRichiesta = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RICHIESTA);
			
			String risprif = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_PER_RIFERIMENTO);
			
			
			
			String tipoauthRichiesta = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_CONNETTORE);
			@SuppressWarnings("unused")
			String confpwRichiesta = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_CONFERMA_PASSWORD_CONNETTORE);
			String subjectRichiesta = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_SUBJECT_CONNETTORE);
			
			String endpointtype = connettoriHelper.readEndPointType();
			if(endpointtype==null){
				boolean interfacciaAvanzata = InterfaceType.AVANZATA.equals(ServletUtils.getUserFromSession(session).getInterfaceType());
				if(interfacciaAvanzata){
					endpointtype = TipiConnettore.DISABILITATO.toString();
				}
				else{
					endpointtype = TipiConnettore.HTTP.toString();
				}
			}
			String tipoconn = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			String autenticazioneHttp = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
			String user = null;
			String password = null;
			
			String connettoreDebug = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);
			
			// http
			String url = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
			if(TipiConnettore.HTTP.toString().equals(endpointtype)){
				user = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_USERNAME);
				password = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_PASSWORD);
			}
			
			// jms
			String nomeCodaJMS = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
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
			String httpstipologia = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE);
			String httpshostverifyS = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			boolean httpshostverify = ServletUtils.isCheckBoxEnabled(httpshostverifyS);
			String httpspath = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
			String httpstipo = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			String httpspwd = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			String httpsalgoritmo = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			String httpsstatoS = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			boolean httpsstato = ServletUtils.isCheckBoxEnabled(httpsstatoS);
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
			
			Boolean isConnettoreCustomUltimaImmagineSalvata = null;
			
			Connettore connisTmp = null;
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(connisTmp, ConnettoreServletType.SERVIZIO_APPLICATIVO_ADD, saCore, 
							request, session, (endpointtype==null), endpointtype); // uso endpointtype per capire se è la prima volta che entro
			
			long soggLong = -1;
			// se ho fatto la add 
			if(useIdSogg)
				if(provider != null && !provider.equals("")){
				soggLong = Long.parseLong(provider);
			}
			
			// Preparo il menu
			saHelper.makeMenu();

			String superUser = ServletUtils.getUserLoginFromSession(session);

			// Prendo la lista di soggetti e la metto in un array
			String[] soggettiList = null;
			String[] soggettiListLabel = null;
			
			String protocollo = saCore.getProtocolloDefault();
			List<String> tipiSoggettiCompatibiliGestitiProtocollo = soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);
			long providerTmp = -1;
			
			if(saCore.isRegistroServiziLocale()){
				List<Soggetto> list = null;
				if(saCore.isVisioneOggettiGlobale(superUser)){
					list = soggettiCore.soggettiRegistroList(null,new Search(true));
				}else{
					list = soggettiCore.soggettiRegistroList(superUser,new Search(true)); 
				}
				
				
				// Filtro per pdd esterne
				if(singlePdD){
					List<Soggetto> listFiltrata = new ArrayList<Soggetto>();
					for (Soggetto soggetto : list) {
						// Se la PdD non e' selezionata e' come se fosse un soggetto con pdd esterna.
						if(soggetto.getPortaDominio()!=null){
							PdDControlStation pdd = null;
							if("-".equals(soggetto.getPortaDominio())==false)
								pdd = pddCore.getPdDControlStation(soggetto.getPortaDominio());
	
							boolean pddEsterna = false;
							if( (pdd==null) || PddTipologia.ESTERNO.toString().equals(pdd.getTipo())){
								pddEsterna = true;
							}
							if(!pddEsterna){
								listFiltrata.add(soggetto);
							}
						}
					}
					list = listFiltrata;
				}
				if (list.size() > 0) {
					soggettiList = new String[list.size()];
					soggettiListLabel = new String[list.size()];
					int i = 0;
					for (Soggetto soggetto : list) {
						if(tipiSoggettiCompatibiliGestitiProtocollo.contains(soggetto.getTipo())){
							if(providerTmp < 0)
								providerTmp = soggetto.getId();
						}
						soggettiList[i] = soggetto.getId().toString();
						soggettiListLabel[i] = soggetto.getTipo() + "/" + soggetto.getNome();
						i++;
					}
				}
			}
			else{
				List<org.openspcoop2.core.config.Soggetto> list = null;
				if(saCore.isVisioneOggettiGlobale(superUser)){
					list = soggettiCore.soggettiList(null,new Search(true));
				}else{
					list = soggettiCore.soggettiList(superUser,new Search(true)); 
				}
				
				soggettiList = new String[list.size()];
				soggettiListLabel = new String[list.size()];
				int i = 0;
				for (org.openspcoop2.core.config.Soggetto soggetto : list) {
					if(tipiSoggettiCompatibiliGestitiProtocollo.contains(soggetto.getTipo())){
						if(providerTmp < 0)
							providerTmp = soggetto.getId();
					}
					soggettiList[i] = soggetto.getId().toString();
					soggettiListLabel[i] = soggetto.getTipo() + "/" + soggetto.getNome();
					i++;
				}
			}
			
			// se il provider non e' stato scelto seleziono il primo nella lista che appartiene al protocollo di default della console.
			String tipoENomeSoggetto = "";
			String nomeProtocollo = protocollo;
			if(provider == null){
				provider = providerTmp +"";
			}else {
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(Long.parseLong(provider)); 
				tipoENomeSoggetto = soggetto.getTipo() + "/" + soggetto.getNome();
				nomeProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(soggetto.getTipo());
			}
			
			
			
			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if(ServletUtils.isEditModeInProgress(request)){
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletAdd(pd, ServiziApplicativiCostanti.LABEL_SERVIZIO_APPLICATIVO);
				
				
				if(useIdSogg){
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
							new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI + tipoENomeSoggetto,
									ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST,
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,provider)),								
									new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null)
							);
				}else {
					ServletUtils.setPageDataTitle_ServletAdd(pd, ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI);
				}

				if(ruoloFruitore==null || "".equals(ruoloFruitore)){
					ruoloFruitore = TipologiaFruizione.DISABILITATO.getValue();
				}
				if(ruoloErogatore==null || "".equals(ruoloErogatore)){
					ruoloErogatore = TipologiaErogazione.DISABILITATO.getValue();
				}
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = saHelper.addServizioApplicativoToDati(dati, (nome != null ? nome : ""), null, (fault != null ? fault : ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_FAULT_SOAP), 
						TipoOperazione.ADD, 0, contaListe,soggettiList,
						soggettiListLabel,provider,utenteSA,passwordSA,confpwSA,subjectSA,tipoauthSA,null,null,null,null,sbustamentoInformazioniProtocolloRisposta,
						ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ADD,null,nomeProtocollo,
						ruoloFruitore,ruoloErogatore,
						sbustamento, sbustamentoInformazioniProtocolloRichiesta, getmsg,
						invrifRichiesta, risprif,
						endpointtype, autenticazioneHttp, url, nomeCodaJMS, tipo,
						user, password, initcont, urlpgk,
						provurl, connfact, sendas, 
						httpsurl, httpstipologia, httpshostverify,
						httpspath, httpstipo, httpspwd,
						httpsalgoritmo, httpsstato, httpskeystore,
						httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey,
						httpspwdprivatekey, httpsalgoritmokey,
						tipoconn, connettoreDebug,
						isConnettoreCustomUltimaImmagineSalvata, listExtendedConnettore);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = saHelper.servizioApplicativoCheckData(TipoOperazione.ADD, soggettiList, -1, ruoloFruitore, ruoloErogatore,
					listExtendedConnettore);
			if (!isOk) {
				
				// setto la barra del titolo
				if(useIdSogg){
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
							new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI + tipoENomeSoggetto,
									ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST,
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,provider)),								
									new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null)
							);
				}else {
					ServletUtils.setPageDataTitle_ServletAdd(pd, ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI);
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = saHelper.addServizioApplicativoToDati(dati, (nome != null ? nome : ""), null, (fault != null ? fault : ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_FAULT_SOAP),
						TipoOperazione.ADD, 0, contaListe,soggettiList,
						soggettiListLabel,provider,utenteSA,passwordSA,confpwSA,subjectSA,tipoauthSA,null,null,null,null,sbustamentoInformazioniProtocolloRisposta,
						ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ADD,null,nomeProtocollo,
						ruoloFruitore,ruoloErogatore,
						sbustamento, sbustamentoInformazioniProtocolloRichiesta, getmsg,
						invrifRichiesta, risprif,
						endpointtype, autenticazioneHttp, url, nomeCodaJMS, tipo,
						user, password, initcont, urlpgk,
						provurl, connfact, sendas, 
						httpsurl, httpstipologia, httpshostverify,
						httpspath, httpstipo, httpspwd,
						httpsalgoritmo, httpsstato, httpskeystore,
						httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey,
						httpspwdprivatekey, httpsalgoritmokey,
						tipoconn, connettoreDebug,
						isConnettoreCustomUltimaImmagineSalvata, listExtendedConnettore);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, ForwardParams.ADD());
			}

			// Inserisco il servizioApplicativo nel db
			try {
				// con.setAutoCommit(false);

				int idProv = Integer.parseInt(provider);

				if (tipoauthSA.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC)) {
					subjectSA = "";
				}
				if (tipoauthSA.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SSL)) {
					utenteSA = "";
					passwordSA = "";
				}
				if (tipoauthSA.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA)) {
					utenteSA = "";
					passwordSA = "";
					subjectSA = "";
				}

				Soggetto soggettoRegistro = null;
				org.openspcoop2.core.config.Soggetto soggettoConfig = null;
				if(saCore.isRegistroServiziLocale()){
					soggettoRegistro = soggettiCore.getSoggettoRegistro(idProv);
				}else{
					soggettoConfig = soggettiCore.getSoggetto(idProv);
				}

				User userSession = ServletUtils.getUserFromSession(session);
				
				Credenziali credenziali = new Credenziali();
				credenziali.setSubject("");
				//credenziali.setTipo(CredenzialeTipo.NONE);
				credenziali.setTipo(null);
				credenziali.setUser("");
				credenziali.setPassword("");

				ServizioApplicativo sa = new ServizioApplicativo();
				sa.setNome(nome);

				if(ruoloFruitore==null){
					ruoloFruitore = TipologiaFruizione.DISABILITATO.getValue();
				}
				if(ruoloErogatore==null){
					ruoloErogatore = TipologiaErogazione.DISABILITATO.getValue();
				}
				sa.setTipologiaFruizione(ruoloFruitore);
				sa.setTipologiaErogazione(ruoloErogatore);
				
				if(saCore.isRegistroServiziLocale()){
					sa.setIdSoggetto(soggettoRegistro.getId());
					sa.setNomeSoggettoProprietario(soggettoRegistro.getNome());
					sa.setTipoSoggettoProprietario(soggettoRegistro.getTipo());
				}else{
					sa.setIdSoggetto(soggettoConfig.getId());
					sa.setNomeSoggettoProprietario(soggettoConfig.getNome());
					sa.setTipoSoggettoProprietario(soggettoConfig.getTipo());
				}

				// *** risposta asinc ***
				RispostaAsincrona rispostaAsinc = new RispostaAsincrona();
				rispostaAsinc.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
				rispostaAsinc.setCredenziali(credenziali);
				rispostaAsinc.setGetMessage(CostantiConfigurazione.DISABILITATO);

				sa.setRispostaAsincrona(rispostaAsinc);

				// *** Invocazione servizio ***
				InvocazioneServizio invServizio = new InvocazioneServizio();
				if(InterfaceType.AVANZATA.equals(userSession.getInterfaceType()) || 
						TipologiaErogazione.DISABILITATO.getValue().equals(ruoloErogatore)){
					invServizio.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
					invServizio.setCredenziali(credenziali);
					invServizio.setGetMessage(CostantiConfigurazione.DISABILITATO);
				}
				else{
					if(sbustamento==null){
						invServizio.setSbustamentoSoap(CostantiConfigurazione.DISABILITATO);
					}else{
						invServizio.setSbustamentoSoap(StatoFunzionalita.toEnumConstant(sbustamento));
					}
					if(sbustamentoInformazioniProtocolloRichiesta==null){
						invServizio.setSbustamentoInformazioniProtocollo(CostantiConfigurazione.ABILITATO);
					}else{
						invServizio.setSbustamentoInformazioniProtocollo(StatoFunzionalita.toEnumConstant(sbustamentoInformazioniProtocolloRichiesta));
					}
					invServizio.setGetMessage(StatoFunzionalita.toEnumConstant(getmsg));
					invServizio.setInvioPerRiferimento(StatoFunzionalita.toEnumConstant(invrifRichiesta));
					invServizio.setRispostaPerRiferimento(StatoFunzionalita.toEnumConstant(risprif));
					Credenziali cis = null;
					if (tipoauthRichiesta!=null && !tipoauthRichiesta.equals(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_NONE.toString())) {
										
						if (cis == null) {
							cis = new Credenziali();
						}
						if(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA.equals(tipoauthRichiesta)){
							//cis.setTipo(CredenzialeTipo.toEnumConstant(CostantiConfigurazione.AUTENTICAZIONE_NONE));
							cis.setTipo(null);
						}else
							cis.setTipo(CredenzialeTipo.toEnumConstant(tipoauthRichiesta));
						if (tipoauthRichiesta.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC)) {
							cis.setUser(user);
							cis.setPassword(password);
						} else {
							cis.setUser("");
							cis.setPassword("");
						}
						if (tipoauthRichiesta.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SSL)) {
							cis.setSubject(subjectRichiesta);
						} else {
							cis.setSubject("");
						}
						invServizio.setCredenziali(cis);
					}
					else if(endpointtype.equals(TipiConnettore.JMS.toString())){
						if (cis == null) {
							cis = new Credenziali();
						}
						if(user!=null && password!=null){
							cis.setTipo(CredenzialeTipo.BASIC);
							cis.setUser(user);
							cis.setPassword(password);
						}
						else{
							cis.setTipo(null);
							cis.setUser(user);
							cis.setPassword(password);
						}
						cis.setSubject(null);
						invServizio.setCredenziali(cis);
					}
					else {
						invServizio.setCredenziali(null);
					}
					Connettore connis = invServizio.getConnettore();
					if(connis==null){
						connis = new Connettore();
					}
					String oldConnT = connis.getTipo();
					if ( (connis.getCustom()!=null && connis.getCustom()) && !connis.getTipo().equals(TipiConnettore.HTTPS.toString()))
						oldConnT = TipiConnettore.CUSTOM.toString();
					connettoriHelper.fillConnettore(connis, connettoreDebug, endpointtype, oldConnT, tipoconn, url,
							nomeCodaJMS, tipo, user, password,
							initcont, urlpgk, provurl, connfact,
							sendas, httpsurl, httpstipologia,
							httpshostverify, httpspath, httpstipo,
							httpspwd, httpsalgoritmo, httpsstato,
							httpskeystore, httpspwdprivatekeytrust,
							httpspathkey, httpstipokey,
							httpspwdkey, httpspwdprivatekey,
							httpsalgoritmokey,
							listExtendedConnettore);
					invServizio.setConnettore(connis);
				}
				sa.setInvocazioneServizio(invServizio);

				// *** Invocazione Porta ***
				InvocazionePorta invocazionePorta = new InvocazionePorta();
				credenziali = new Credenziali();
				if (tipoauthSA.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA)) {
					//credenziali.setTipo(CredenzialeTipo.toEnumConstant(CostantiConfigurazione.AUTENTICAZIONE_NONE));
					credenziali.setTipo(null);
				}else{
					credenziali.setTipo(CredenzialeTipo.toEnumConstant(tipoauthSA));
				}
				credenziali.setUser(utenteSA);
				credenziali.setPassword(passwordSA);
				credenziali.setSubject(subjectSA);
				invocazionePorta.addCredenziali(credenziali);
				
				if(InterfaceType.AVANZATA.equals(userSession.getInterfaceType())) {
					if(credenziali.getTipo()!=null){
						sa.setTipologiaFruizione(TipologiaFruizione.NORMALE.getValue());
					}
				}
				
				InvocazionePortaGestioneErrore ipge = new InvocazionePortaGestioneErrore();
				ipge.setFault(FaultIntegrazioneTipo.toEnumConstant(fault));
				invocazionePorta.setGestioneErrore(ipge);
				
				invocazionePorta.setSbustamentoInformazioniProtocollo(StatoFunzionalita.toEnumConstant(sbustamentoInformazioniProtocolloRisposta));

				sa.setInvocazionePorta(invocazionePorta);
				
				saCore.performCreateOperation(superUser, saHelper.smista(), sa);

			} catch (Exception ex) {
				return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), ex, pd, session, gd, mapping, 
						ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, ForwardParams.ADD());
			}

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<ServizioApplicativo> lista = null;

			if(!useIdSogg){
				int idLista = Liste.SERVIZIO_APPLICATIVO;
				ricerca = saHelper.checkSearchParameters(idLista, ricerca);
				
				if(saCore.isVisioneOggettiGlobale(superUser)){
					lista = saCore.soggettiServizioApplicativoList(null, ricerca);
				}else{
					lista = saCore.soggettiServizioApplicativoList(superUser, ricerca);
				}
			}else {
				int idLista = Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO;
				ricerca = saHelper.checkSearchParameters(idLista, ricerca);
				lista = saCore.soggettiServizioApplicativoList(ricerca,soggLong);
			}

			saHelper.prepareServizioApplicativoList(ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, ForwardParams.ADD());
		} 

	}

}
