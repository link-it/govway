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
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.InvocazionePortaGestioneErrore;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.FaultIntegrazioneTipo;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.constants.TipologiaFruizione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
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
 * servizioApplicativoChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ServiziApplicativiChange extends Action {

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

			ServiziApplicativiHelper saHelper = new ServiziApplicativiHelper(request, pd, session);
			ConnettoriHelper connettoriHelper = new ConnettoriHelper(request, pd, session);
			
			String id = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID);
			int idServizioApplicativo = Integer.parseInt(id);
			
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
//			if(ServletUtils.isCheckBoxEnabled(ruoloFruitore)){
//				ruoloFruitore = TipologiaFruizione.NORMALE.getValue();
//			}
//			else if(ruoloFruitore!=null && !"".equals(ruoloFruitore)){
//				ruoloFruitore = TipologiaFruizione.DISABILITATO.getValue();
//			}
//			
//			if(ServletUtils.isCheckBoxEnabled(ruoloErogatore)){
//				ruoloErogatore = TipologiaErogazione.TRASPARENTE.getValue();
//			}
//			else if(ruoloErogatore!=null && !"".equals(ruoloErogatore)){
//				ruoloErogatore = TipologiaErogazione.DISABILITATO.getValue();
//			}
			
			//String nome = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME);
			String provider = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			String fault = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT);
			
			String tipoauthSA = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SA);
			String utenteSA = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_USERNAME_SA);
			String passwordSA = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_PASSWORD_SA);
			String confpwSA = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_CONFERMA_PASSWORD_SA);
			String subjectSA = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_SUBJECT_SA);
			
			String sbustamentoInformazioniProtocolloRisposta = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RISPOSTA);
			
			String faultactor = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT_ACTOR);
			String genericfault = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT_GENERIC_CODE);
			String prefixfault = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT_PREFIX);
			
			String invrifRisposta = request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RISPOSTA);

			
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
			
			
			
			
			Long soggLong = 0L;
			if(provider != null){
				soggLong = Long.parseLong(provider);
			}
			
			// Preparo il menu
			saHelper.makeMenu();

			ServiziApplicativiCore saCore = new ServiziApplicativiCore();
			SoggettiCore soggettiCore = new SoggettiCore(saCore);

			// Prendo il nome e il provider del servizioApplicativo
			ServizioApplicativo sa = saCore.getServizioApplicativo(idServizioApplicativo);
			String nome = sa.getNome();
			int idProv = sa.getIdSoggetto().intValue();

			InvocazionePorta ip = sa.getInvocazionePorta();
			InvocazionePortaGestioneErrore ipge = null;
			Credenziali credenziali = null;
			if (ip != null) {
				ipge = ip.getGestioneErrore();
				if(ip.sizeCredenzialiList()>0)
					credenziali = ip.getCredenziali(0);
			}
			
			InvocazioneServizio is = sa.getInvocazioneServizio();
			Credenziali cis = is.getCredenziali();
			Connettore connis = is.getConnettore();
			List<Property> cp = connis.getPropertyList();
			
			Boolean isConnettoreCustomUltimaImmagineSalvata = connis.getCustom();
			
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(connis, ConnettoreServletType.SERVIZIO_APPLICATIVO_CHANGE, saCore, 
							request, session, (endpointtype==null), endpointtype); // uso endpointtype per capire se è la prima volta che entro
			
			if (endpointtype == null) {
				if(connis!=null){
					if ((connis.getCustom()!=null && connis.getCustom()) && !TipiConnettore.HTTPS.toString().equals(connis.getTipo())) {
						endpointtype = TipiConnettore.CUSTOM.toString();
						tipoconn = connis.getTipo();
					} else
						endpointtype = connis.getTipo();
				}
			}
			if(endpointtype==null){
				endpointtype=TipiConnettore.DISABILITATO.toString();
			}
			
			String nomeProtocollo = null;
			String tipoENomeSoggetto = null;
			if(saCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(idProv);
				tipoENomeSoggetto = soggetto.getTipo() + "/" + soggetto.getNome();
				nomeProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(soggetto.getTipo());
			}
			else{
				Soggetto soggetto = soggettiCore.getSoggetto(idProv);
				tipoENomeSoggetto = soggetto.getTipo() + "/" + soggetto.getNome();
				nomeProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(soggetto.getTipo());
			}

			// Se nomehid = null, devo visualizzare la pagina per la modifica
			// dati
			if(ServletUtils.isEditModeInProgress(request)){
				
				// setto la barra del titolo
//				ServletUtils.setPageDataTitle_ServletChange(pd, ServiziApplicativiCostanti.LABEL_SERVIZIO_APPLICATIVO,
//						ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST,nome);
				
				if(useIdSogg){
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
							new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI + tipoENomeSoggetto,
									ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST,
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,provider)),								
									new Parameter(nome, null)
							);
				}else {
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST),
							new Parameter(nome, null)
							);
				}
				

				if (fault == null)
					if (ipge != null){
						if(ipge.getFault()!=null)
							fault = ipge.getFault().toString();
					}
				
				if (faultactor == null) {
					if (ipge != null)
						faultactor = ipge.getFaultActor();
				}
				if (genericfault == null) {
					if (ipge != null){
						if(ipge.getGenericFaultCode()!=null)
							genericfault = ipge.getGenericFaultCode().toString();
					}
					if ((genericfault == null) || "".equals(genericfault)) {
						genericfault = CostantiConfigurazione.DISABILITATO.toString();
					}
				}
				if (prefixfault == null) {
					if (ipge != null)
						prefixfault = ipge.getPrefixFaultCode();
				}
				if (invrifRisposta == null) {
					if (ip != null){
						if(ip.getInvioPerRiferimento()!=null)
							invrifRisposta = ip.getInvioPerRiferimento().toString();
					}
					if ((invrifRisposta == null) || "".equals(invrifRisposta)) {
						invrifRisposta = CostantiConfigurazione.DISABILITATO.toString();
					}
				}
				if (sbustamentoInformazioniProtocolloRisposta == null) {
					if (ip != null){
						if(ip.getSbustamentoInformazioniProtocollo()!=null)
							sbustamentoInformazioniProtocolloRisposta = ip.getSbustamentoInformazioniProtocollo().toString();
					}
					if ((sbustamentoInformazioniProtocolloRisposta == null) || "".equals(sbustamentoInformazioniProtocolloRisposta)) {
						sbustamentoInformazioniProtocolloRisposta = CostantiConfigurazione.ABILITATO.toString();
					}
				}
				
				
				if (tipoauthSA == null){
					if (credenziali != null){
						if(credenziali.getTipo()!=null)
							tipoauthSA = credenziali.getTipo().toString();
						utenteSA = credenziali.getUser();
						passwordSA = credenziali.getPassword();
						confpwSA = passwordSA;
						subjectSA = credenziali.getSubject();
					}
				}
				if (tipoauthSA == null) {
					tipoauthSA = ServiziApplicativiCostanti.DEFAULT_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE;
				}

				if(ruoloFruitore==null){
					ruoloFruitore=sa.getTipologiaFruizione();
				}
				if(ruoloErogatore==null){
					ruoloErogatore=sa.getTipologiaErogazione();
				}
				
				
				if (sbustamento == null) {
					if(is.getSbustamentoSoap()!=null)
						sbustamento = is.getSbustamentoSoap().toString();
				}
				if (sbustamentoInformazioniProtocolloRichiesta == null) {
					if(is.getSbustamentoInformazioniProtocollo()!=null)
						sbustamentoInformazioniProtocolloRichiesta = is.getSbustamentoInformazioniProtocollo().toString();
				}
				if (getmsg == null){
					if(is.getGetMessage()!=null)
						getmsg = is.getGetMessage().toString();
				}
				
				if (invrifRichiesta == null) {
					if(is.getInvioPerRiferimento()!=null)
						invrifRichiesta = is.getInvioPerRiferimento().toString();
					if ((invrifRichiesta == null) || "".equals(invrifRichiesta)) {
						invrifRichiesta = CostantiConfigurazione.DISABILITATO.toString();
					}
				}
				if (risprif == null) {
					if(is.getRispostaPerRiferimento()!=null)
						risprif = is.getRispostaPerRiferimento().toString();
					if ((risprif == null) || "".equals(risprif)) {
						risprif = CostantiConfigurazione.DISABILITATO.toString();
					}
				}
				
				if ((tipoauthRichiesta == null) && (cis != null)) {
					if(cis.getTipo()!=null)
						tipoauthRichiesta = cis.getTipo().toString();
				}
				if ((user == null) && (cis != null)) {
					user = cis.getUser();
					password = cis.getPassword();
					confpwRichiesta = password;
				}
				if ((subjectRichiesta == null) && (cis != null)) {
					subjectRichiesta = cis.getSubject();
				}
				if (endpointtype == null) {
					if ((connis.getCustom()!=null && connis.getCustom()) && !connis.getTipo().equals(TipiConnettore.HTTPS.toString())) {
						endpointtype = TipiConnettore.CUSTOM.toString();
						tipoconn = connis.getTipo();
					} else
						endpointtype = connis.getTipo();
				}
				if(endpointtype==null){
					endpointtype=TipiConnettore.DISABILITATO.toString();
				}

				Map<String, String> props = null;
				if(connis!=null)
					props = connis.getProperties();
				
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
				
				autenticazioneHttp = connettoriHelper.getAutenticazioneHttp(autenticazioneHttp, endpointtype, user);
				
				for (int i = 0; i < connis.sizePropertyList(); i++) {
					Property singlecp = cp.get(i);
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_HTTP_LOCATION)) {
						if (url == null) {
							url = singlecp.getValore();
						}
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_NOME)) {
						if (nome == null) {
							nome = singlecp.getValore();
						}
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_TIPO)) {
						if (tipo == null) {
							tipo = singlecp.getValore();
						}
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY)) {
						if (connfact == null) {
							connfact = singlecp.getValore();
						}
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_SEND_AS)) {
						if (sendas == null) {
							sendas = singlecp.getValore();
						}
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL)) {
						if (initcont == null) {
							initcont = singlecp.getValore();
						}
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG)) {
						if (urlpgk == null) {
							urlpgk = singlecp.getValore();
						}
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL)) {
						if (provurl == null) {
							provurl = singlecp.getValore();
						}
					}
				}

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
					httpshostverifyS = "true";
					httpshostverify = true;
				}
				
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = saHelper.addServizioApplicativoToDati(dati, nome, tipoENomeSoggetto, fault, 
						TipoOperazione.CHANGE, idServizioApplicativo, contaListe,null,null,provider
						,utenteSA,passwordSA,confpwSA,subjectSA,tipoauthSA,faultactor,genericfault,prefixfault,invrifRisposta,
						sbustamentoInformazioniProtocolloRisposta,
						ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE,id,nomeProtocollo,
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
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = saHelper.servizioApplicativoCheckData(TipoOperazione.CHANGE, null, idProv, ruoloFruitore, ruoloErogatore,
					listExtendedConnettore);
			if (!isOk) {
				
				// setto la barra del titolo
//				ServletUtils.setPageDataTitle_ServletChange(pd, ServiziApplicativiCostanti.LABEL_SERVIZIO_APPLICATIVO,
//						ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST,nome);
				
				if(useIdSogg){
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
							new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI + tipoENomeSoggetto,
									ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST,
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,provider)),								
									new Parameter(nome, null)
							);
				}else {
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST),
							new Parameter(nome, null)
							);
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = saHelper.addServizioApplicativoToDati(dati, nome, tipoENomeSoggetto, fault, 
						TipoOperazione.CHANGE, idServizioApplicativo, contaListe,null,null,provider
						,utenteSA,passwordSA,confpwSA,subjectSA,tipoauthSA,faultactor,genericfault,prefixfault,invrifRisposta,
						sbustamentoInformazioniProtocolloRisposta,
						ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE,id,nomeProtocollo,
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
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, ForwardParams.CHANGE());
			}

			User userSession = ServletUtils.getUserFromSession(session);
			
			if(ruoloFruitore==null){
				ruoloFruitore = TipologiaFruizione.DISABILITATO.getValue();
			}
			if(ruoloErogatore==null){				
				if(sa.getTipologiaErogazione()!=null){
					ruoloErogatore = sa.getTipologiaErogazione();
				}else{
					ruoloErogatore = TipologiaErogazione.DISABILITATO.getValue();
				}
			}
			sa.setTipologiaFruizione(ruoloFruitore);
			sa.setTipologiaErogazione(ruoloErogatore);
			
			
			// *** Invocazione Porta ***
			if(InterfaceType.AVANZATA.equals(userSession.getInterfaceType()) ||
					!TipologiaFruizione.DISABILITATO.getValue().equals(ruoloFruitore)){
			
				if (ipge == null)
					ipge = new InvocazionePortaGestioneErrore();
				ipge.setFault(FaultIntegrazioneTipo.toEnumConstant(fault));
				if (fault.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_FAULT_XML)) {
					ipge.setFaultActor("");
				} else {
					ipge.setFaultActor(faultactor);
				}
				ipge.setGenericFaultCode(StatoFunzionalita.toEnumConstant(genericfault));
				ipge.setPrefixFaultCode(prefixfault);
				if (ip == null)
					ip = new InvocazionePorta();
				ip.setGestioneErrore(ipge);
				if (credenziali == null)
					credenziali = new Credenziali();
				credenziali.setTipo(CredenzialeTipo.toEnumConstant(tipoauthSA));
				if (tipoauthSA.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC)) {
					credenziali.setUser(utenteSA);
					credenziali.setPassword(passwordSA);
				} else {
					credenziali.setUser("");
					credenziali.setPassword("");
				}
				if (tipoauthSA.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SSL)) {
					credenziali.setSubject(subjectSA);
				} else {
					credenziali.setSubject("");
				}
				ip.addCredenziali(credenziali);
				
				if(InterfaceType.AVANZATA.equals(userSession.getInterfaceType())) {
					if(credenziali.getTipo()!=null){
						sa.setTipologiaFruizione(TipologiaFruizione.NORMALE.getValue());
					}
					else{
						sa.setTipologiaFruizione(TipologiaFruizione.DISABILITATO.getValue());
					}
				}
				
				ip.setSbustamentoInformazioniProtocollo(StatoFunzionalita.toEnumConstant(sbustamentoInformazioniProtocolloRisposta));
				
				ip.setInvioPerRiferimento(StatoFunzionalita.toEnumConstant(invrifRisposta));
				
				sa.setInvocazionePorta(ip);

			}
			else if(InterfaceType.STANDARD.equals(userSession.getInterfaceType()) &&
					TipologiaFruizione.DISABILITATO.getValue().equals(ruoloFruitore)){
				
				InvocazionePorta invocazionePorta = new InvocazionePorta();
				credenziali = new Credenziali();
				
				if(CostantiConfigurazione.ABILITATO.equals(getmsg)){
					if (tipoauthSA==null || tipoauthSA.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA)) {
						//credenziali.setTipo(CredenzialeTipo.toEnumConstant(CostantiConfigurazione.AUTENTICAZIONE_NONE));
						credenziali.setTipo(null);
					}else{
						credenziali.setTipo(CredenzialeTipo.toEnumConstant(tipoauthSA));
					}
					credenziali.setUser(utenteSA);
					credenziali.setPassword(passwordSA);
					credenziali.setSubject(subjectSA);
				}
				else{
					credenziali.setTipo(null);
					credenziali.setUser(null);
					credenziali.setPassword(null);
					credenziali.setSubject(null);
				}
				invocazionePorta.addCredenziali(credenziali);
								
				InvocazionePortaGestioneErrore ipgeTmp = new InvocazionePortaGestioneErrore();
				ipgeTmp.setFault(FaultIntegrazioneTipo.SOAP);
				invocazionePorta.setGestioneErrore(ipgeTmp);
				
				invocazionePorta.setSbustamentoInformazioniProtocollo(StatoFunzionalita.ABILITATO);

				sa.setInvocazionePorta(invocazionePorta);				
			}
			
			
			// *** Invocazione Servizio ***
			if(InterfaceType.STANDARD.equals(userSession.getInterfaceType()) &&
					!TipologiaErogazione.DISABILITATO.getValue().equals(ruoloErogatore)){
			
				if(sbustamento==null){
					is.setSbustamentoSoap(CostantiConfigurazione.DISABILITATO);
				}else{
					is.setSbustamentoSoap(StatoFunzionalita.toEnumConstant(sbustamento));
				}
				if(sbustamentoInformazioniProtocolloRichiesta==null){
					is.setSbustamentoInformazioniProtocollo(CostantiConfigurazione.ABILITATO);
				}else{
					is.setSbustamentoInformazioniProtocollo(StatoFunzionalita.toEnumConstant(sbustamentoInformazioniProtocolloRichiesta));
				}
				is.setGetMessage(StatoFunzionalita.toEnumConstant(getmsg));
				is.setInvioPerRiferimento(StatoFunzionalita.toEnumConstant(invrifRichiesta));
				is.setRispostaPerRiferimento(StatoFunzionalita.toEnumConstant(risprif));
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
					is.setCredenziali(cis);
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
					is.setCredenziali(cis);
				}
				else {
					is.setCredenziali(null);
				}
				String oldConnT = connis.getTipo();
				if ((connis.getCustom()!=null && connis.getCustom()) && !connis.getTipo().equals(TipiConnettore.HTTPS.toString()))
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
				is.setConnettore(connis);
				
				sa.setInvocazioneServizio(is);
			}
			else if(InterfaceType.STANDARD.equals(userSession.getInterfaceType()) &&
					TipologiaErogazione.DISABILITATO.getValue().equals(ruoloErogatore)){
				is.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
				is.setCredenziali(cis);
				is.setGetMessage(CostantiConfigurazione.DISABILITATO);
				if(is.getConnettore()!=null){
					is.getConnettore().setTipo(TipiConnettore.DISABILITATO.toString());
				}
				sa.setInvocazioneServizio(is);					
			}
			
			String userLogin = ServletUtils.getUserLoginFromSession(session);
			saCore.performUpdateOperation(userLogin, saHelper.smista(), sa);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			

			List<ServizioApplicativo> lista = null;

			if(!useIdSogg){
				int idLista = Liste.SERVIZIO_APPLICATIVO;

				ricerca = saHelper.checkSearchParameters(idLista, ricerca);
				
				if(saCore.isVisioneOggettiGlobale(userLogin)){
					lista = saCore.soggettiServizioApplicativoList(null, ricerca);
				}else{
					lista = saCore.soggettiServizioApplicativoList(userLogin, ricerca);
				}
			}else {
				int idLista = Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO;

				ricerca = saHelper.checkSearchParameters(idLista, ricerca);
				
				lista = saCore.soggettiServizioApplicativoList(ricerca,soggLong);
			}

			saHelper.prepareServizioApplicativoList(ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, ForwardParams.CHANGE());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, ForwardParams.CHANGE());
		}
	}
}
