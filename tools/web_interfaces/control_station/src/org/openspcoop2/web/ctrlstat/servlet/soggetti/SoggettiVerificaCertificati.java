/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.web.ctrlstat.servlet.soggetti;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.CredenzialiSoggetto;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.web.ctrlstat.core.CertificateChecker;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * SoggettiVerificaCertificati
 * 
 * @author Andrea Poli (poli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class SoggettiVerificaCertificati extends Action {
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			
			SoggettiHelper soggettiHelper = new SoggettiHelper(request, pd, session);
			
			String id = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID);
			int idSogg = Integer.parseInt(id);
			String nomeprov = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME);
			String tipoprov = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO);
			
			String alias = soggettiHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER);
			
			// Preparo il menu
			soggettiHelper.makeMenu();
			
			SoggettiCore soggettiCore = new SoggettiCore();
			ConfigurazioneCore confCore = new ConfigurazioneCore(soggettiCore);
			PddCore pddCore = new PddCore(soggettiCore);
			
			String verificaCertificatiFromLista = soggettiHelper.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CERTIFICATI_FROM_LISTA);
			boolean arrivoDaLista = "true".equalsIgnoreCase(verificaCertificatiFromLista);
			
			// Prendo la lista di aliases
			List<String> aliases = confCore.getJmxPdD_aliases();
			if(aliases==null || aliases.size()<=0){
				throw new Exception("Pagina non prevista, la sezione configurazione non permette di accedere a questa pagina, se la configurazione non e' corretta");
			}
			
			Soggetto soggettoRegistry = null;
			org.openspcoop2.core.config.Soggetto soggettoConfig = null;
			
			if(soggettiCore.isRegistroServiziLocale()){
				soggettoRegistry = soggettiCore.getSoggettoRegistro(idSogg);// core.getSoggettoRegistro(new
				// IDSoggetto(tipoprov,nomeprov));
			}

			soggettoConfig = soggettiCore.getSoggetto(idSogg);// core.getSoggetto(new
			// IDSoggetto(tipoprov,nomeprov));
			
			if(soggettiCore.isRegistroServiziLocale()){
				nomeprov = soggettoRegistry.getNome();
				tipoprov = soggettoRegistry.getTipo();
			}
			else{
				nomeprov = soggettoConfig.getNome();
				tipoprov = soggettoConfig.getTipo();
			}

			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoprov);
			
			
			List<Parameter> parametersServletSoggettoChange = new ArrayList<Parameter>();
			Parameter pIdSoggetto = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID, id);
			Parameter pNomeSoggetto = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME, nomeprov);
			Parameter pTipoSoggetto = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO, tipoprov);
			parametersServletSoggettoChange.add(pIdSoggetto);
			parametersServletSoggettoChange.add(pNomeSoggetto);
			parametersServletSoggettoChange.add(pTipoSoggetto);
			
			// setto la barra del titolo
			List<Parameter> listParameter = new ArrayList<Parameter>();
			listParameter.add(new Parameter(SoggettiCostanti.LABEL_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
//			listParameter.add(new Parameter(soggettiHelper.getLabelNomeSoggetto(protocollo, soggettoRegistry.getTipo() , soggettoRegistry.getNome()),
//							SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE, parametersServletSoggettoChange.toArray(new Parameter[parametersServletSoggettoChange.size()])));
			
			String labelSoggetto = soggettiHelper.getLabelNomeSoggetto(protocollo, soggettoRegistry.getTipo() , soggettoRegistry.getNome());
			
			if(arrivoDaLista) {
				String labelVerifica = SoggettiCostanti.LABEL_SOGGETTI_VERIFICA_CERTIFICATI_DI + labelSoggetto;
				listParameter.add(new Parameter(labelVerifica, null));
			}
			else {
				listParameter.add(new Parameter(labelSoggetto, 
						SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE, 
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID,soggettoRegistry.getId()+""),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME,soggettoRegistry.getNome()),
						new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO,soggettoRegistry.getTipo())));
				String labelVerifica = SoggettiCostanti.LABEL_SOGGETTI_VERIFICA_CERTIFICATI;
				listParameter.add(new Parameter(labelVerifica, null));
			}
			
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, listParameter );
			
			Vector<DataElement> dati = new Vector<DataElement>();
			dati.addElement(ServletUtils.getDataElementForEditModeFinished());
			
			
			// -- raccolgo dati
			
			boolean ssl = false;
			boolean sslManuale = false;
			int countSsl = 0;
			for (int i = 0; i < soggettoRegistry.sizeCredenzialiList(); i++) {
				CredenzialiSoggetto c = soggettoRegistry.getCredenziali(i);
				if(org.openspcoop2.core.registry.constants.CredenzialeTipo.SSL.equals(c.getTipo())) {
					if(c.getCertificate()!=null) {
						ssl = true;
						countSsl++;
					}
					else {
						sslManuale=true;
					}
				}
			}
			boolean piuCertificatiAssociatiEntita = countSsl>1;
			
			boolean verificaCertificatiEffettuata = false;
			
			if(!ssl) {
				if(sslManuale) {
					pd.setMessage(CostantiControlStation.LABEL_VERIFICA_CERTIFICATI_PRESENTE_SOLO_CONFIGURAZIONE_MANUALE,
							Costanti.MESSAGE_TYPE_INFO);
				}
				else {
					pd.setMessage(CostantiControlStation.LABEL_VERIFICA_CERTIFICATI_NON_PRESENTI,
							Costanti.MESSAGE_TYPE_INFO);
				}
				
				pd.disableEditMode();
				
				verificaCertificatiEffettuata = true;
			}
			else {
				
				boolean sceltaClusterId = soggettiCore.isVerificaCertificati_sceltaClusterId();
				
				if(aliases.size()==1 || alias!=null || !sceltaClusterId) {
					
					if(alias==null && !sceltaClusterId) {
						alias = CostantiControlStation.LABEL_VERIFICA_CONNETTORE_TUTTI_I_NODI;
					}
			
					// -- verifica						
					List<String> aliases_for_check = new ArrayList<>();
					boolean all = false;
					if(aliases.size()==1) {
						aliases_for_check.add(aliases.get(0));
					}
					else if(CostantiControlStation.LABEL_VERIFICA_CONNETTORE_TUTTI_I_NODI.equals(alias)) {
						aliases_for_check.addAll(aliases);
						all = true;
					}
					else {
						aliases_for_check.add(alias);
					}
					
					CertificateChecker certificateChecker = null;
					if(all) {
						certificateChecker = soggettiCore.getJmxPdD_certificateChecker();
					}
					else {
						certificateChecker = soggettiCore.newJmxPdD_certificateChecker(aliases_for_check);
					}
					StringBuilder sbDetailsError = new StringBuilder(); 
					
					int sogliaWarningGiorni = soggettiCore.getVerificaCertificati_warning_expirationDays();
					
					
					String posizioneErrore = null;
					String extraErrore = null;
					
					// verifica sl
					StringBuilder sbDetailsWarning_ssl = new StringBuilder();
					String posizioneWarning_ssl = null;
					if(ssl) {
						certificateChecker.checkSoggetto(sbDetailsError, sbDetailsWarning_ssl,
						    ssl, soggettoRegistry, 
						    sogliaWarningGiorni);
						if(sbDetailsError.length()>0) {
							posizioneErrore = labelSoggetto;
						}
						else if(sbDetailsWarning_ssl.length()>0) {
							posizioneWarning_ssl = labelSoggetto;
						}
					}
					
					// analisi warning
					String warning = null;
					String posizioneWarning = null;
					String extraWarning = null;
					if(sbDetailsError.length()<=0) {
						if(sbDetailsWarning_ssl.length()>0) {
							warning = sbDetailsWarning_ssl.toString();
							posizioneWarning = posizioneWarning_ssl;
						}
					}
					
					// esito
					List<String> formatIds = new ArrayList<String>();
					soggettiCore.formatVerificaCertificatiEsito(pd, formatIds, 
							(sbDetailsError.length()>0 ? sbDetailsError.toString() : null), extraErrore, posizioneErrore,
							warning, extraWarning, posizioneWarning,
							piuCertificatiAssociatiEntita);
							
					pd.disableEditMode();
					
					verificaCertificatiEffettuata = true;
					
				} else {
					
					DataElement deTestConnettivita = new DataElement();
					deTestConnettivita.setType(DataElementType.TITLE);
					deTestConnettivita.setLabel(SoggettiCostanti.LABEL_SOGGETTI_VERIFICA_CERTIFICATI);
					dati.add(deTestConnettivita);
					
					soggettiHelper.addVerificaCertificatoSceltaAlias(aliases, dati);
				}
			}
			
			pd.setLabelBottoneInvia(CostantiControlStation.LABEL_CONFIGURAZIONE_VERIFICA_CONNETTORE_BOTTONE);
			
			dati = soggettiHelper.addSoggettoHiddenToDati(dati, id,nomeprov, tipoprov);
			
			DataElement	de = new DataElement();
			de.setValue(arrivoDaLista+"");
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_VERIFICA_CERTIFICATI_FROM_LISTA);
			dati.addElement(de);
			
			pd.setDati(dati);

			if(verificaCertificatiEffettuata) {
				
				// verifica richiesta dal link nella lista, torno alla lista
				if(arrivoDaLista) {
				
					String filterDominioInterno = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_FILTER_DOMINIO_INTERNO);
					boolean forceFilterDominioInterno = false;
					if("true".equalsIgnoreCase(filterDominioInterno)) {
						forceFilterDominioInterno = true;
					}
					
					boolean multiTenant = soggettiCore.isMultitenant();
					
					String userLogin = ServletUtils.getUserLoginFromSession(session);	
					
					Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
					
					int idLista = Liste.SOGGETTI;
					
					// poiche' esistono filtri che hanno necessita di postback salvo in sessione
					List<Soggetto> lista = null;
					if(soggettiCore.isRegistroServiziLocale()){
						if(!ServletUtils.isSearchDone(soggettiHelper)) {
							lista = ServletUtils.getRisultatiRicercaFromSession(session, idLista,  Soggetto.class);
						}
					}
					
					ricerca = soggettiHelper.checkSearchParameters(idLista, ricerca);
					
					if(forceFilterDominioInterno) {
						ricerca.addFilter(idLista, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE);
					}
					else if(!multiTenant && !soggettiHelper.isModalitaCompleta()) {
						ricerca.addFilter(idLista, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE);
					}
					
					if(soggettiCore.isRegistroServiziLocale()){
						if(lista==null) {
							if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
								lista = soggettiCore.soggettiRegistroList(null, ricerca);
							}else{
								lista = soggettiCore.soggettiRegistroList(userLogin, ricerca);
							}
						}
						
						if(!soggettiHelper.isPostBackFilterElement()) {
							ServletUtils.setRisultatiRicercaIntoSession(session, idLista, lista); // salvo poiche' esistono filtri che hanno necessita di postback
						}
						
						soggettiHelper.prepareSoggettiList(lista, ricerca);
					}
					else{
						List<org.openspcoop2.core.config.Soggetto> listaConfig = null;
						if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
							listaConfig = soggettiCore.soggettiList(null, ricerca);
						}else{
							listaConfig = soggettiCore.soggettiList(userLogin, ricerca);
						}
						soggettiHelper.prepareSoggettiConfigList(listaConfig, ricerca);
					}
					
					ServletUtils.setSearchObjectIntoSession(session, ricerca);
					
					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
					return ServletUtils.getStrutsForwardEditModeFinished(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI_VERIFICA_CERTIFICATI, CostantiControlStation.TIPO_OPERAZIONE_VERIFICA_CERTIFICATI);
					
				}
				// verifica richiesta dal dettaglio, torno al dettaglio
				else { 

					// setto la barra del titolo
					ServletUtils.setPageDataTitle_ServletChange(pd, SoggettiCostanti.LABEL_SOGGETTI, 
							SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST, 
							soggettiHelper.getLabelNomeSoggetto(protocollo, tipoprov , nomeprov));

					String portadom = null, descr= null, versioneProtocollo= null,pdd= null, codiceIpa= null, pd_url_prefix_rewriter= null,pa_url_prefix_rewriter= null,dominio = null;
					boolean isRouter = false,privato= false; 
					String tipoauthSoggetto = null;
					String utenteSoggetto = null;
					String passwordSoggetto = null;
					String subjectSoggetto = null;
					String issuerSoggetto = null;
					String principalSoggetto = null;
					String multipleApiKey = null;
					String appId = null;
					String apiKey = null;
					BinaryParameter tipoCredenzialiSSLFileCertificato = soggettiHelper.getBinaryParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO);
					String tipoCredenzialiSSLSorgente = null;
					String tipoCredenzialiSSLAliasCertificato = null;
					String tipoCredenzialiSSLAliasCertificatoSubject= null;
					String tipoCredenzialiSSLAliasCertificatoIssuer= null;
					String tipoCredenzialiSSLAliasCertificatoType= null;
					String tipoCredenzialiSSLAliasCertificatoVersion= null;
					String tipoCredenzialiSSLAliasCertificatoSerialNumber= null;
					String tipoCredenzialiSSLAliasCertificatoSelfSigned= null;
					String tipoCredenzialiSSLAliasCertificatoNotBefore= null;
					String tipoCredenzialiSSLAliasCertificatoNotAfter = null;
					String tipoCredenzialiSSLVerificaTuttiICampi = null;
					String tipoCredenzialiSSLConfigurazioneManualeSelfSigned= null;
					org.openspcoop2.utils.certificate.ArchiveType tipoCredenzialiSSLTipoArchivio = org.openspcoop2.utils.certificate.ArchiveType.CER; 
					String tipoCredenzialiSSLFileCertificatoPassword = null;
					List<String> listaAliasEstrattiCertificato = new ArrayList<String>();
					
					String tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD;
					
					String changepwd = null;
					
					boolean isSupportatoAutenticazioneSoggetti = soggettiCore.isSupportatoAutenticazioneSoggetti(protocollo);
					boolean isSupportatoCodiceIPA = soggettiCore.isSupportatoCodiceIPA(protocollo); 
					boolean isSupportatoIdentificativoPorta = soggettiCore.isSupportatoIdentificativoPorta(protocollo);
					
					String nomePddGestioneLocale = null;
					if(pddCore.isGestionePddAbilitata(soggettiHelper)==false){
						nomePddGestioneLocale = pddCore.getNomePddOperativa();
						if(nomePddGestioneLocale==null) {
							throw new Exception("Non è stata rilevata una pdd di tipologia 'operativo'");
						}
					}
					
					if(soggettiCore.isRegistroServiziLocale()){
						portadom = soggettoRegistry.getIdentificativoPorta();
						descr = soggettoRegistry.getDescrizione();
						pdd = soggettoRegistry.getPortaDominio();
												
						if(pddCore.isGestionePddAbilitata(soggettiHelper)==false){
							if(pddCore.isPddEsterna(pdd)) {
								dominio = SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE;
							}
							else {
								dominio = SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE;
							}
						}
						versioneProtocollo = soggettoRegistry.getVersioneProtocollo();
						privato = soggettoRegistry.getPrivato()!=null && soggettoRegistry.getPrivato();
						codiceIpa = soggettoRegistry.getCodiceIpa();
						
						if(isSupportatoAutenticazioneSoggetti){
							if (tipoauthSoggetto == null){
								CredenzialiSoggetto credenziali = null;
								if(soggettoRegistry.sizeCredenzialiList()>0) {
									credenziali = soggettoRegistry.getCredenziali(0);
								}
								if (credenziali != null){
									if(credenziali.getTipo()!=null)
										tipoauthSoggetto = credenziali.getTipo().toString();
									utenteSoggetto = credenziali.getUser();
									passwordSoggetto = credenziali.getPassword();
									if(tipoauthSoggetto!=null && ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC.equals(tipoauthSoggetto)){
										tipoCredenzialiSSLVerificaTuttiICampi = credenziali.isCertificateStrictVerification() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
									}
									else if(tipoauthSoggetto!=null && ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY.equals(tipoauthSoggetto)){
										tipoCredenzialiSSLVerificaTuttiICampi = credenziali.isCertificateStrictVerification() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
										multipleApiKey = credenziali.isAppId() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
										appId = credenziali.getUser();
										apiKey = credenziali.getPassword();
									}
									principalSoggetto = credenziali.getUser();
									
									if(credenziali.getCertificate() != null) {
										tipoCredenzialiSSLFileCertificato.setValue(credenziali.getCertificate());
										tipoCredenzialiSSLSorgente = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO;
										tipoCredenzialiSSLVerificaTuttiICampi = credenziali.isCertificateStrictVerification() ? Costanti.CHECK_BOX_ENABLED :Costanti.CHECK_BOX_DISABLED;
										
										try {
											Certificate cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLFileCertificato.getValue());
											tipoCredenzialiSSLAliasCertificatoIssuer = cSelezionato.getCertificate().getIssuer().getNameNormalized();
											tipoCredenzialiSSLAliasCertificatoSubject = cSelezionato.getCertificate().getSubject().getNameNormalized();
											tipoCredenzialiSSLAliasCertificatoSelfSigned = cSelezionato.getCertificate().isSelfSigned() ? CostantiControlStation.LABEL_SI : CostantiControlStation.LABEL_NO;
											tipoCredenzialiSSLAliasCertificatoSerialNumber = cSelezionato.getCertificate().getSerialNumber() + "";
											tipoCredenzialiSSLAliasCertificatoType = cSelezionato.getCertificate().getType();
											tipoCredenzialiSSLAliasCertificatoVersion = cSelezionato.getCertificate().getVersion() + "";
											SimpleDateFormat sdf = new SimpleDateFormat(SoggettiChange.CERTIFICATE_FORMAT);
											tipoCredenzialiSSLAliasCertificatoNotBefore = sdf.format(cSelezionato.getCertificate().getNotBefore());
											tipoCredenzialiSSLAliasCertificatoNotAfter = sdf.format(cSelezionato.getCertificate().getNotAfter());
										}catch(UtilsException e) {
											pd.setMessage("Il Certificato selezionato non &egrave; valido: "+e.getMessage());
											tipoCredenzialiSSLAliasCertificato = "";
											tipoCredenzialiSSLAliasCertificatoSubject= "";
											tipoCredenzialiSSLAliasCertificatoIssuer= "";
											tipoCredenzialiSSLAliasCertificatoType= "";
											tipoCredenzialiSSLAliasCertificatoVersion= "";
											tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
											tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
											tipoCredenzialiSSLAliasCertificatoNotBefore= "";
											tipoCredenzialiSSLAliasCertificatoNotAfter = "";
										}
									} else {
										subjectSoggetto = credenziali.getSubject();
										issuerSoggetto = credenziali.getIssuer();
										tipoCredenzialiSSLSorgente = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE;
										tipoCredenzialiSSLConfigurazioneManualeSelfSigned = ( subjectSoggetto != null && subjectSoggetto.equals(issuerSoggetto)) ? Costanti.CHECK_BOX_ENABLED :Costanti.CHECK_BOX_DISABLED;
									}
								}
							}
							if (tipoauthSoggetto == null) {
								tipoauthSoggetto = ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA;
							}
						}

					}
					else{
						portadom = soggettoConfig.getIdentificativoPorta();
						descr = soggettoConfig.getDescrizione();
						isRouter = soggettoConfig.getRouter();
					}

					pd_url_prefix_rewriter = soggettoConfig.getPdUrlPrefixRewriter();
					pa_url_prefix_rewriter = soggettoConfig.getPaUrlPrefixRewriter();
					
					boolean isPddEsterna = pddCore.isPddEsterna(pdd);
					if(isSupportatoAutenticazioneSoggetti){
						if(isPddEsterna){
							if(tipoauthSoggetto==null && ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA.equals(tipoauthSoggetto)){
								tipoauthSoggetto = soggettiCore.getAutenticazione_generazioneAutomaticaPorteApplicative();
							}
						}
					}
					
					IDSoggetto idSoggetto = new IDSoggetto(tipoprov,nomeprov);
					
					List<String> tipiSoggetti = soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);
					
					List<String> versioniProtocollo = null;
					if(soggettiHelper.isModalitaAvanzata()){
						versioniProtocollo = soggettiCore.getVersioniProtocollo(protocollo);
					}else {
						versioniProtocollo = new ArrayList<String>();
						versioneProtocollo = soggettiCore.getVersioneDefaultProtocollo(protocollo);
						versioniProtocollo.add(versioneProtocollo);
					}

					List<String> listaTipiProtocollo = soggettiCore.getProtocolli(session);
									
					boolean pddOperativa = false;
					if(soggettoRegistry.getPortaDominio()!=null && !"".equals(soggettoRegistry.getPortaDominio())){
						PdDControlStation pddCtrlstat = pddCore.getPdDControlStation(soggettoRegistry.getPortaDominio());
						pddOperativa = PddTipologia.OPERATIVO.toString().equals(pddCtrlstat.getTipo());
					}
					
					String [] pddList = null;
					if(!pddOperativa && soggettoRegistry.getPortaDominio()!=null) {
						pddList = new String[1];
						pddList[0] = soggettoRegistry.getPortaDominio();
					}
					String [] pddEsterneList = null;
					
					org.openspcoop2.core.registry.Connettore connettore = null;
					if(soggettiCore.isRegistroServiziLocale()){
						connettore = soggettoRegistry.getConnettore();
					}
					
					int numPA = 0, numPD = 0;
					
					ProtocolProperties protocolProperties = null;
					List<ProtocolProperty> oldProtocolPropertyList = null;
					IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
					IRegistryReader registryReader = null;
					IConfigIntegrationReader configRegistryReader = null;
					ConsoleConfiguration consoleConfiguration = null;
					ConsoleOperationType consoleOperationType = ConsoleOperationType.CHANGE;
					try{
						IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
						consoleDynamicConfiguration = protocolFactory.createDynamicConfigurationConsole();
						registryReader = soggettiCore.getRegistryReader(protocolFactory); 
						configRegistryReader = soggettiCore.getConfigIntegrationReader(protocolFactory);
						consoleConfiguration = consoleDynamicConfiguration.getDynamicConfigSoggetto(consoleOperationType, soggettiHelper, 
								registryReader, configRegistryReader, idSoggetto);
						Soggetto soggetto = registryReader.getSoggetto(idSoggetto);
						oldProtocolPropertyList = soggetto.getProtocolPropertyList();
						protocolProperties = soggettiHelper.estraiProtocolPropertiesDaRequest(consoleConfiguration, consoleOperationType);
					}catch(RegistryNotFound r){}
					
					Properties propertiesProprietario = new Properties();
					propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO, id);
					propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO, ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_SOGGETTO);
					propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO, tipoprov + "/" + nomeprov);
					propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE, URLEncoder.encode( SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE + "?" + request.getQueryString(), "UTF-8"));
					propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO, protocollo);
					
					boolean visualizzaModificaCertificato = false;
					boolean visualizzaAddCertificato = false;
					Integer numeroCertificati = soggettoRegistry.sizeCredenzialiList();
					if(soggettoRegistry.sizeCredenzialiList()>0) {
						visualizzaAddCertificato = true;
						if(soggettoRegistry.sizeCredenzialiList() == 1) {  // se ho definito solo un certificato c'e' il link diretto alla modifica
							visualizzaModificaCertificato = true;
						}
					}
					
					String servletCredenzialiList = SoggettiCostanti.SERVLET_NAME_SOGGETTI_CREDENZIALI_LIST;
					String servletCredenzialiAdd = SoggettiCostanti.SERVLET_NAME_SOGGETTI_CREDENZIALI_ADD;
					List<Parameter> parametersServletCredenzialiList = new ArrayList<Parameter>();
					parametersServletCredenzialiList.add(pIdSoggetto);
					parametersServletCredenzialiList.add(pNomeSoggetto);
					parametersServletCredenzialiList.add(pTipoSoggetto);
					
					dati.addElement(ServletUtils.getDataElementForEditModeFinished());

					// update della configurazione 
					consoleDynamicConfiguration.updateDynamicConfigSoggetto(consoleConfiguration, consoleOperationType, soggettiHelper, protocolProperties, 
							registryReader, configRegistryReader, idSoggetto); 
					
					dati = soggettiHelper.addSoggettiToDati(TipoOperazione.CHANGE, dati, nomeprov, tipoprov, portadom, descr, 
							isRouter, tipiSoggetti, versioneProtocollo, privato, codiceIpa, versioniProtocollo,
							isSupportatoCodiceIPA, isSupportatoIdentificativoPorta,
							pddList,pddEsterneList,nomePddGestioneLocale,pdd,id,nomeprov,tipoprov,connettore,
							numPD,pd_url_prefix_rewriter,numPA,pa_url_prefix_rewriter,listaTipiProtocollo,protocollo,
							isSupportatoAutenticazioneSoggetti,utenteSoggetto,passwordSoggetto,subjectSoggetto,principalSoggetto,tipoauthSoggetto,
							isPddEsterna,null,dominio,tipoCredenzialiSSLSorgente, tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato, tipoCredenzialiSSLFileCertificatoPassword, listaAliasEstrattiCertificato, 
							tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLAliasCertificatoSubject, tipoCredenzialiSSLAliasCertificatoIssuer,
							tipoCredenzialiSSLAliasCertificatoType, tipoCredenzialiSSLAliasCertificatoVersion, tipoCredenzialiSSLAliasCertificatoSerialNumber, 
							tipoCredenzialiSSLAliasCertificatoSelfSigned, tipoCredenzialiSSLAliasCertificatoNotBefore, tipoCredenzialiSSLAliasCertificatoNotAfter, 
							tipoCredenzialiSSLVerificaTuttiICampi, tipoCredenzialiSSLConfigurazioneManualeSelfSigned, issuerSoggetto,tipoCredenzialiSSLWizardStep,
							changepwd,
							multipleApiKey, appId, apiKey, 
							visualizzaModificaCertificato, visualizzaAddCertificato, servletCredenzialiList, parametersServletCredenzialiList, numeroCertificati, servletCredenzialiAdd, soggettoRegistry.sizeProprietaList());

					// aggiunta campi custom
					dati = soggettiHelper.addProtocolPropertiesToDatiRegistry(dati, consoleConfiguration,consoleOperationType, protocolProperties,oldProtocolPropertyList,propertiesProprietario);
					
					pd.setDati(dati);
					
					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
					return ServletUtils.getStrutsForwardEditModeFinished(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.CHANGE());
					
				}
			}
			else {
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				return ServletUtils.getStrutsForwardEditModeFinished(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI_VERIFICA_CERTIFICATI, ForwardParams.OTHER(""));
			}
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI_VERIFICA_CERTIFICATI, ForwardParams.OTHER(""));
		}  
	}
}
