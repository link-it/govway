/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.CredenzialiSoggetto;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesCostanti;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesUtilities;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.utenti.UtentiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * soggettiChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class SoggettiChange extends Action {

	private String editMode = null;
	private String id, nomeprov , tipoprov, portadom, descr, versioneProtocollo,pdd, codiceIpa, pd_url_prefix_rewriter,pa_url_prefix_rewriter,protocollo,dominio;
	private boolean isRouter,privato; 

	// Protocol Properties
	private IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
	private ConsoleConfiguration consoleConfiguration =null;
	private ProtocolProperties protocolProperties = null;
	private IProtocolFactory<?> protocolFactory= null;
	private IRegistryReader registryReader = null; 
	private IConfigIntegrationReader configRegistryReader = null; 
	private ConsoleOperationType consoleOperationType = null;
	private ConsoleInterfaceType consoleInterfaceType = null;
	private String protocolPropertiesSet = null;

	private String tipoauthSoggetto = null;
	private String utenteSoggetto = null;
	private String passwordSoggetto = null;
	private String subjectSoggetto = null;
	private String principalSoggetto = null;
	
	private String modificaOperativo = null;
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		this.consoleOperationType = ConsoleOperationType.CHANGE;
		
		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.CHANGE; 
		List<ProtocolProperty> oldProtocolPropertyList = null;
		try {
			SoggettiHelper soggettiHelper = new SoggettiHelper(request, pd, session);
			this.consoleInterfaceType = ProtocolPropertiesUtilities.getTipoInterfaccia(soggettiHelper); 

			this.id = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_ID);
			int idSogg = Integer.parseInt(this.id);
			this.nomeprov = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_NOME);
			this.tipoprov = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_TIPO);
			this.portadom = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_CODICE_PORTA);
			this.descr = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DESCRIZIONE);
			this.versioneProtocollo = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_VERSIONE_PROTOCOLLO);
			this.pdd = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PDD);
			String is_router = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_IS_ROUTER);
			this.isRouter = ServletUtils.isCheckBoxEnabled(is_router);
			this.dominio = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO);
			String is_privato = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_IS_PRIVATO);
			this.privato = ServletUtils.isCheckBoxEnabled(is_privato);
			this.codiceIpa = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_CODICE_IPA);
			this.pd_url_prefix_rewriter = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PD_URL_PREFIX_REWRITER);
			this.pa_url_prefix_rewriter = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_PA_URL_PREFIX_REWRITER);

			this.editMode = soggettiHelper.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
			this.protocolPropertiesSet = soggettiHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_SET);
			
			this.tipoauthSoggetto = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE);
			this.utenteSoggetto = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
			this.passwordSoggetto = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			this.subjectSoggetto = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT);
			this.principalSoggetto = soggettiHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL);
			
			this.modificaOperativo = soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_MODIFICA_OPERATIVO);
	
			// Preparo il menu
			soggettiHelper.makeMenu();

			// Prendo i vecchi nome e tipo
			String oldnomeprov = "", oldtipoprov = "", oldpdd = null;

			Boolean contaListe = ServletUtils.getContaListeFromSession(session);
			String userLogin = ServletUtils.getUserLoginFromSession(session);

			Soggetto soggettoRegistry = null;
			org.openspcoop2.core.config.Soggetto soggettoConfig = null;
			List<String> tipiSoggetti = null;
			int numPA = 0, numPD = 0,numSA = 0;
			String[] pddList = null;
			String[] pddEsterneList = null;
			List<String> versioniProtocollo = null;

			SoggettiCore soggettiCore = new SoggettiCore();
			PddCore pddCore = new PddCore(soggettiCore);
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore(soggettiCore);
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(soggettiCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(soggettiCore);
			UtentiCore utentiCore = new UtentiCore(soggettiCore);

			String nomePddGestioneLocale = null;
			if(pddCore.isGestionePddAbilitata(soggettiHelper)==false){
				nomePddGestioneLocale = pddCore.getNomePddOperativa();
				if(nomePddGestioneLocale==null) {
					throw new Exception("Non è stata rilevata una pdd di tipologia 'operativo'");
				}
			}

			if(soggettiCore.isRegistroServiziLocale()){
				soggettoRegistry = soggettiCore.getSoggettoRegistro(idSogg);// core.getSoggettoRegistro(new
				// IDSoggetto(tipoprov,nomeprov));
			}

			soggettoConfig = soggettiCore.getSoggetto(idSogg);// core.getSoggetto(new
			// IDSoggetto(tipoprov,nomeprov));

			if(soggettiCore.isRegistroServiziLocale()){
				oldnomeprov = soggettoRegistry.getNome();
				oldtipoprov = soggettoRegistry.getTipo();
				oldpdd = soggettoRegistry.getPortaDominio();
			}
			else{
				oldnomeprov = soggettoConfig.getNome();
				oldtipoprov = soggettoConfig.getTipo();
			}

			// Tipi protocollo supportati
			List<String> listaTipiProtocollo = soggettiCore.getProtocolli(session);
			//tipiSoggetti = soggettiCore.getTipiSoggettiGestiti(versioneProtocollo); // all tipi soggetti gestiti
			// Nella change non e' piu' possibile cambiare il protocollo
			this.protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(this.tipoprov);
			tipiSoggetti = soggettiCore.getTipiSoggettiGestitiProtocollo(this.protocollo);

			if(soggettiHelper.isModalitaAvanzata()){
				versioniProtocollo = soggettiCore.getVersioniProtocollo(this.protocollo);
			}else {
				versioniProtocollo = new ArrayList<String>();
				this.versioneProtocollo = soggettiCore.getVersioneDefaultProtocollo(this.protocollo);
				versioniProtocollo.add(this.versioneProtocollo);
			}
			boolean isSupportatoAutenticazioneSoggetti = soggettiCore.isSupportatoAutenticazioneSoggetti(this.protocollo);

			boolean isSupportatoCodiceIPA = soggettiCore.isSupportatoCodiceIPA(this.protocollo); 
			boolean isSupportatoIdentificativoPorta = soggettiCore.isSupportatoIdentificativoPorta(this.protocollo);


			boolean isPddEsterna = pddCore.isPddEsterna(this.pdd);
			if(isSupportatoAutenticazioneSoggetti){
				if(isPddEsterna){
					if(this.tipoauthSoggetto==null && ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA.equals(this.tipoauthSoggetto)){
						this.tipoauthSoggetto = soggettiCore.getAutenticazione_generazioneAutomaticaPorteApplicative();
					}
				}
			}
			
			if (contaListe) {
				// Conto il numero di porte applicative
				IDSoggetto soggetto = new IDSoggetto(soggettoConfig.getTipo(),soggettoConfig.getNome());
				// BugFix OP-674
//				numPA = porteApplicativeCore.porteAppList(idSogg, new Search(true)).size();// soggettoConfig.sizePortaApplicativaList();
//				numPD = porteDelegateCore.porteDelegateList(idSogg, new Search(true)).size();// soggettoConfig.sizePortaDelegataList();
//				numSA = saCore.servizioApplicativoList(soggetto, new Search(true)).size();
				Search searchForCount = new Search(true,1);
				porteApplicativeCore.porteAppList(idSogg, searchForCount);
				numPA = searchForCount.getNumEntries(Liste.PORTE_APPLICATIVE_BY_SOGGETTO);
				porteDelegateCore.porteDelegateList(idSogg, searchForCount);
				numPD = searchForCount.getNumEntries(Liste.PORTE_DELEGATE_BY_SOGGETTO);
				saCore.servizioApplicativoList(soggetto, searchForCount);
				numSA = searchForCount.getNumEntries(Liste.SERVIZIO_APPLICATIVO);
			}

			if(soggettiCore.isSinglePdD()){
				if(soggettiCore.isRegistroServiziLocale()){
					// Prendo la lista di pdd e la metto in un array
					// In pratica se un soggetto e' associato ad una PdD Operativa,
					// e possiede gia' delle PD o PA o SA,
					// non e' piu' possibile cambiargli la porta di dominio in una esterna.

					boolean pddOperativa = false;
					if(soggettoRegistry.getPortaDominio()!=null && !"".equals(soggettoRegistry.getPortaDominio())){
						PdDControlStation pddCtrlstat = pddCore.getPdDControlStation(soggettoRegistry.getPortaDominio());
						pddOperativa = PddTipologia.OPERATIVO.toString().equals(pddCtrlstat.getTipo());
					}

					List<PdDControlStation> lista = new ArrayList<PdDControlStation>();
					if( (numPA<=0 && numPD<=0 && numSA<=0) || !pddOperativa ){
						
						List<String> pddEsterne = new ArrayList<>();
						pddEsterne.add("-");
						
						// aggiungo un elemento di comodo
						PdDControlStation tmp = new PdDControlStation();
						tmp.setNome("-");
						lista.add(tmp);
						// aggiungo gli altri elementi
						if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
							lista.addAll(pddCore.pddList(null, new Search(true)));
						}else{
							lista.addAll(pddCore.pddList(userLogin, new Search(true)));
						}
						pddList = new String[lista.size()];
						int i = 0;
						for (PdDControlStation pddTmp : lista) {
							pddList[i] = pddTmp.getNome();
							i++;
							
							if(PddTipologia.ESTERNO.toString().equals(pddTmp.getTipo())){
								pddEsterne.add(pddTmp.getNome());
							}
						}
						
						pddEsterneList = pddEsterne.toArray(new String[1]);
					}
					else{
						// non posso modificare la pdd. Lascio solo quella operativa
						pddList = new String[1];
						pddList[0] = soggettoRegistry.getPortaDominio();
					}
				}
			}

			org.openspcoop2.core.registry.Connettore connettore = null;
			if(soggettiCore.isRegistroServiziLocale()){
				connettore = soggettoRegistry.getConnettore();
			}


			IDSoggetto idSoggetto = new IDSoggetto(oldtipoprov,oldnomeprov);
			// Protocol Properties	
			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(this.protocollo);
			this.consoleDynamicConfiguration =  this.protocolFactory.createDynamicConfigurationConsole();
			this.registryReader = soggettiCore.getRegistryReader(this.protocolFactory); 
			this.configRegistryReader = soggettiCore.getConfigIntegrationReader(this.protocolFactory);
			this.consoleConfiguration = this.consoleDynamicConfiguration.getDynamicConfigSoggetto(this.consoleOperationType, this.consoleInterfaceType, 
					this.registryReader, this.configRegistryReader, idSoggetto);
			this.protocolProperties = soggettiHelper.estraiProtocolPropertiesDaRequest(this.consoleConfiguration, this.consoleOperationType);
			
			try{
				Soggetto soggetto = this.registryReader.getSoggetto(idSoggetto);
				oldProtocolPropertyList = soggetto.getProtocolPropertyList(); 
			}catch(RegistryNotFound r){}

			Properties propertiesProprietario = new Properties();
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO, this.id);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO, ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_SOGGETTO);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO, oldtipoprov + "/" + oldnomeprov);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE, URLEncoder.encode( SoggettiCostanti.SERVLET_NAME_SOGGETTI_CHANGE + "?" + request.getQueryString(), "UTF-8"));
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO, this.protocollo);
			
			// Se nomehid = null, devo visualizzare la pagina per la modifica dati
			if(ServletUtils.isEditModeInProgress(this.editMode)){

				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, SoggettiCostanti.LABEL_SOGGETTI, 
						SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST, 
						soggettiHelper.getLabelNomeSoggetto(this.protocollo, oldtipoprov , oldnomeprov));

				if (is_router == null) 
					this.isRouter = soggettoConfig.getRouter();
				if(soggettiCore.isRegistroServiziLocale()){
					if(this.portadom==null){
						this.portadom = soggettoRegistry.getIdentificativoPorta();
					}else{
						String old_protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(oldtipoprov);
						if(old_protocollo.equals(this.protocollo)==false){
							// e' cambiato il protocollo: setto a empty il portadom
							this.portadom = null;
						}
					}
					if(this.descr==null)
						this.descr = soggettoRegistry.getDescrizione();
					if(this.pdd==null)
						this.pdd = soggettoRegistry.getPortaDominio();
					if(pddCore.isGestionePddAbilitata(soggettiHelper)==false){
						if(this.dominio==null) {
							if(pddCore.isPddEsterna(this.pdd)) {
								this.dominio = SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE;
							}
							else {
								this.dominio = SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE;
							}
						}
						else {
							if(SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE.equals(this.dominio)) {
								this.pdd = nomePddGestioneLocale;
							}
							else {
								this.pdd = null;
							}
						}
					}
					if(this.versioneProtocollo==null)
						this.versioneProtocollo = soggettoRegistry.getVersioneProtocollo();
					if(soggettiHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_IS_PRIVATO) == null){
						this.privato = soggettoRegistry.getPrivato()!=null && soggettoRegistry.getPrivato();
					}
					if(this.codiceIpa==null){
						this.codiceIpa = soggettoRegistry.getCodiceIpa();
					}
					if(isSupportatoAutenticazioneSoggetti){
						if (this.tipoauthSoggetto == null){
							CredenzialiSoggetto credenziali = soggettoRegistry.getCredenziali();
							if (credenziali != null){
								if(credenziali.getTipo()!=null)
									this.tipoauthSoggetto = credenziali.getTipo().toString();
								this.utenteSoggetto = credenziali.getUser();
								this.passwordSoggetto = credenziali.getPassword();
								this.subjectSoggetto = credenziali.getSubject();
								this.principalSoggetto = credenziali.getUser();
							}
						}
						if (this.tipoauthSoggetto == null) {
							this.tipoauthSoggetto = ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA;
						}
					}

				}
				else{
					if(this.portadom==null){
						this.portadom = soggettoConfig.getIdentificativoPorta();
					}else{
						String old_protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(oldtipoprov);
						if(old_protocollo.equals(this.protocollo)==false){
							// e' cambiato il protocollo: setto a empty il portadom
							this.portadom = null;
						}
					}
					if(this.descr==null)
						this.descr = soggettoConfig.getDescrizione();
					if (is_router == null) 
						this.isRouter = soggettoConfig.getRouter();
				}

				if(this.pd_url_prefix_rewriter==null){
					this.pd_url_prefix_rewriter = soggettoConfig.getPdUrlPrefixRewriter();
				}
				if(this.pa_url_prefix_rewriter==null){
					this.pa_url_prefix_rewriter = soggettoConfig.getPaUrlPrefixRewriter();
				}
				if(this.tipoprov==null){
					this.tipoprov=oldtipoprov;
				}
				if(this.nomeprov==null){
					this.nomeprov=oldnomeprov;
				}
				idSoggetto = new IDSoggetto(this.tipoprov,this.nomeprov);

				try{
					Soggetto soggetto = this.registryReader.getSoggetto(idSoggetto);
					oldProtocolPropertyList = soggetto.getProtocolPropertyList(); 
				}catch(RegistryNotFound r){}
				
				// Inizializzazione delle properties da db al primo accesso alla pagina
				if(this.protocolPropertiesSet == null){
					ProtocolPropertiesUtils.mergeProtocolProperties(this.protocolProperties, oldProtocolPropertyList, this.consoleOperationType);
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());


				// update della configurazione 
				this.consoleDynamicConfiguration.updateDynamicConfigSoggetto(this.consoleConfiguration, this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idSoggetto); 

				dati = soggettiHelper.addSoggettiToDati(tipoOp,dati, this.nomeprov, this.tipoprov, this.portadom, this.descr, 
						this.isRouter, tipiSoggetti, this.versioneProtocollo, this.privato, this.codiceIpa, versioniProtocollo,
						isSupportatoCodiceIPA, isSupportatoIdentificativoPorta,
						pddList,pddEsterneList,nomePddGestioneLocale,this.pdd,this.id,oldnomeprov,oldtipoprov,connettore,
						numPD,this.pd_url_prefix_rewriter,numPA,this.pa_url_prefix_rewriter,listaTipiProtocollo,this.protocollo,
						isSupportatoAutenticazioneSoggetti,this.utenteSoggetto,this.passwordSoggetto,this.subjectSoggetto,this.principalSoggetto,this.tipoauthSoggetto,
						isPddEsterna,null,this.dominio);

				// aggiunta campi custom
				dati = soggettiHelper.addProtocolPropertiesToDati(dati, this.consoleConfiguration,this.consoleOperationType, this.consoleInterfaceType,this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);

				// aggiunta opzione per modifica soggetto da configurazione
				if(this.modificaOperativo!=null && !"".equals(this.modificaOperativo)) {
					DataElement de = new DataElement();
					de.setName(SoggettiCostanti.PARAMETRO_SOGGETTO_MODIFICA_OPERATIVO);
					de.setType(DataElementType.HIDDEN);
					de.setValue(this.modificaOperativo);
					dati.addElement(de);
				}
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.CHANGE());

			}

			// Controlli sui campi immessi
			boolean isOk = SoggettiUtilities.soggettiCheckData(
					soggettiCore, soggettiHelper,
					org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE,
					oldnomeprov, oldtipoprov, this.privato,
					tipoOp, this.id, this.tipoprov, this.nomeprov, this.codiceIpa, this.pd_url_prefix_rewriter, this.pa_url_prefix_rewriter,
					soggettoRegistry, isSupportatoAutenticazioneSoggetti, this.descr);

			// Validazione base dei parametri custom 
			if(isOk){
				try{
					soggettiHelper.validaProtocolProperties(this.consoleConfiguration, this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties);
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}

			// Valido i parametri custom se ho gia' passato tutta la validazione prevista
			if(isOk){
				try{
					//validazione campi dinamici
					this.consoleDynamicConfiguration.validateDynamicConfigSoggetto(this.consoleConfiguration, this.consoleOperationType, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idSoggetto); 
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}

			if (!isOk) {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle_ServletChange(pd, SoggettiCostanti.LABEL_SOGGETTI, 
						SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST, 
						soggettiHelper.getLabelNomeSoggetto(this.protocollo, oldtipoprov , oldnomeprov));

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				// update della configurazione 
				this.consoleDynamicConfiguration.updateDynamicConfigSoggetto(this.consoleConfiguration, this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idSoggetto); 

				dati = soggettiHelper.addSoggettiToDati(tipoOp,dati, this.nomeprov, this.tipoprov, this.portadom, this.descr, 
						this.isRouter, tipiSoggetti, this.versioneProtocollo, this.privato, this.codiceIpa, versioniProtocollo,
						isSupportatoCodiceIPA, isSupportatoIdentificativoPorta,
						pddList,pddEsterneList,nomePddGestioneLocale,this.pdd,this.id,oldnomeprov,oldtipoprov,connettore,
						numPD,this.pd_url_prefix_rewriter,numPA,this.pa_url_prefix_rewriter,listaTipiProtocollo,this.protocollo,
						isSupportatoAutenticazioneSoggetti,this.utenteSoggetto,this.passwordSoggetto,this.subjectSoggetto,this.principalSoggetto,this.tipoauthSoggetto,
						isPddEsterna,null,this.dominio);

				// aggiunta campi custom
				dati = soggettiHelper.addProtocolPropertiesToDati(dati, this.consoleConfiguration,this.consoleOperationType, this.consoleInterfaceType,this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.CHANGE());

			}

			// Modifico i dati del soggetto nel db

			// Se portadom = "", lo imposto
			// se identificativo porta e' di default allora aggiorno il nome
			String identificativoPortaCalcolato = null;
			String identificativoPortaAttuale = this.portadom;
			if(!soggettiCore.isRegistroServiziLocale()){
				identificativoPortaAttuale = soggettoConfig.getIdentificativoPorta();
			}
			idSoggetto = new IDSoggetto(this.tipoprov,this.nomeprov);
			if(this.portadom!=null && !this.portadom.equals("")){

				IDSoggetto oldSoggetto = new IDSoggetto(oldtipoprov,oldnomeprov);
				String oldIdentificativoPorta = soggettiCore.getIdentificativoPortaDefault(this.protocollo, oldSoggetto);
				if (oldIdentificativoPorta.equals(this.portadom)) {
					// gli identificativi porta sono rimasti invariati
					// setto l identificativo porta di default (in caso sia
					// cambiato il nome)
					identificativoPortaCalcolato = soggettiCore.getIdentificativoPortaDefault(this.protocollo, idSoggetto);
				} else {					
					// in questo caso ho cambiato l'identificativo porta
					// e il valore inserito nel campo va inserito
					identificativoPortaCalcolato = identificativoPortaAttuale;
				}
			}else{
				identificativoPortaCalcolato = soggettiCore.getIdentificativoPortaDefault(this.protocollo, idSoggetto);
			}

			if(soggettiCore.isRegistroServiziLocale()){

				soggettoRegistry.setIdentificativoPorta(identificativoPortaCalcolato);
				soggettoRegistry.setNome(this.nomeprov);
				soggettoRegistry.setTipo(this.tipoprov);
				if(oldtipoprov!=null && oldnomeprov!=null){
					soggettoRegistry.setOldIDSoggettoForUpdate(new IDSoggetto(oldtipoprov, oldnomeprov));
				}
				soggettoRegistry.setDescrizione(this.descr);
				soggettoRegistry.setVersioneProtocollo(this.versioneProtocollo);
				soggettoRegistry.setPrivato(this.privato);
				soggettoRegistry.setPortaDominio(this.pdd);

				if(isSupportatoAutenticazioneSoggetti){
					if(this.tipoauthSoggetto!=null && !"".equals(this.tipoauthSoggetto) && !ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA.equals(this.tipoauthSoggetto)){
						CredenzialiSoggetto credenziali = new CredenzialiSoggetto();
						credenziali.setTipo(CredenzialeTipo.toEnumConstant(this.tipoauthSoggetto));
						credenziali.setUser(this.utenteSoggetto);
						if(this.principalSoggetto!=null && !"".equals(this.principalSoggetto)){
							credenziali.setUser(this.principalSoggetto); // al posto di user
						}
						credenziali.setPassword(this.passwordSoggetto);
						credenziali.setSubject(this.subjectSoggetto);
						soggettoRegistry.setCredenziali(credenziali);
					}
					else{
						soggettoRegistry.setCredenziali(null);
					}
				}
				
			}

			if(soggettiCore.isRegistroServiziLocale()){
				if ((this.codiceIpa != null && !"".equals(this.codiceIpa))) {
					String oldCodiceIpa = soggettiCore.getCodiceIPADefault(this.protocollo, new IDSoggetto(oldtipoprov,oldnomeprov), false);
					if (oldCodiceIpa.equals(this.codiceIpa)) {
						// il codice ipa e' rimasto invariato
						// setto il codice ipa di default (in caso sia cambiato il nome)
						soggettoRegistry.setCodiceIpa(soggettiCore.getCodiceIPADefault(this.protocollo, new IDSoggetto(this.tipoprov,this.nomeprov), false));
					} else {
						// in questo caso ho cambiato il codice ipa e il valore inserito nel campo va inserito
						soggettoRegistry.setCodiceIpa(this.codiceIpa);
					}
				} else {
					this.codiceIpa = soggettiCore.getCodiceIPADefault(this.protocollo, new IDSoggetto(this.tipoprov,this.nomeprov), false);
					soggettoRegistry.setCodiceIpa(this.codiceIpa);
				}
			}
		
			if(pddCore.isGestionePddAbilitata(soggettiHelper)==false){
				if(SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE.equals(this.dominio)) {
					this.pdd = nomePddGestioneLocale;
				}
				else {
					this.pdd = null;
				}
			}
			
			if(soggettiCore.isRegistroServiziLocale()){
				if(soggettiCore.isSinglePdD()){
					if (this.pdd==null || this.pdd.equals("-"))
						soggettoRegistry.setPortaDominio(null);
					else
						soggettoRegistry.setPortaDominio(this.pdd);
				}else{
					soggettoRegistry.setPortaDominio(this.pdd);
				}
			}

			if(oldtipoprov!=null && oldnomeprov!=null){
				soggettoConfig.setOldIDSoggettoForUpdate(new IDSoggetto(oldtipoprov, oldnomeprov));
			}
			soggettoConfig.setDescrizione(this.descr);
			soggettoConfig.setTipo(this.tipoprov);
			soggettoConfig.setNome(this.nomeprov);
			soggettoConfig.setDescrizione(this.descr);
			soggettoConfig.setIdentificativoPorta(identificativoPortaCalcolato);
			soggettoConfig.setRouter(this.isRouter);
			soggettoConfig.setPdUrlPrefixRewriter(this.pd_url_prefix_rewriter);
			soggettoConfig.setPaUrlPrefixRewriter(this.pa_url_prefix_rewriter);

			//imposto properties custom
			soggettoRegistry.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolProperties(this.protocolProperties, this.consoleOperationType, oldProtocolPropertyList)); 

			SoggettoCtrlStat sog = new SoggettoCtrlStat(soggettoRegistry, soggettoConfig);
			sog.setOldNomeForUpdate(oldnomeprov);
			sog.setOldTipoForUpdate(oldtipoprov);

			// eseguo l'aggiornamento
			List<Object> listOggettiDaAggiornare = SoggettiUtilities.getOggettiDaAggiornare(soggettiCore, oldnomeprov, this.nomeprov, oldtipoprov, this.tipoprov, sog);
			soggettiCore.performUpdateOperation(userLogin, soggettiHelper.smista(), listOggettiDaAggiornare.toArray());

			// sistemo utenze dopo l'aggiornamento
			// se la pdd è diventata esterna o se sono cambiati i dati identificativi
			IDSoggetto idSoggettoSelezionato = new IDSoggetto(oldtipoprov, oldnomeprov);
			if(oldpdd!=null && !oldpdd.equals(this.pdd) && pddCore.isPddEsterna(this.pdd)) {
				utentiCore.modificaSoggettoUtilizzatoConsole(idSoggettoSelezionato.toString(), null); // annullo selezione
			}
			else if(!this.tipoprov.equals(oldtipoprov) || !this.nomeprov.equals(oldnomeprov)) {
				IDSoggetto idNuovoSoggettoSelezionato = new IDSoggetto(this.tipoprov, this.nomeprov);
				utentiCore.modificaSoggettoUtilizzatoConsole(idSoggettoSelezionato.toString(), idNuovoSoggettoSelezionato.toString()); // modifico i dati
			}
			
			// preparo lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			if(this.modificaOperativo!=null && !"".equals(this.modificaOperativo)) {
				
				Vector<DataElement> dati = new Vector<DataElement>();
				
				pd.setDati(dati);
				
				pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_SOGGETTO_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);

				pd.disableEditMode();
				
				//if(!pddCore.isPddEsterna(this.pdd)) {
				// sempre, anche quando passo da operativo ad esterno
				generalHelper = new GeneralHelper(session);
				gd = generalHelper.initGeneralData(request); // re-inizializzo per ricalcolare il menu in alto a destra
				//}
				
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeFinished(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_GENERALE,
						ConfigurazioneCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE_GENERALE);
				
			}
			else {
			
				if(soggettiCore.isRegistroServiziLocale()){
					List<Soggetto> listaSoggetti = null;
					if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
						listaSoggetti = soggettiCore.soggettiRegistroList(null, ricerca);
					}else{
						listaSoggetti = soggettiCore.soggettiRegistroList(userLogin, ricerca);
					}
					soggettiHelper.prepareSoggettiList(listaSoggetti, ricerca);
				}
				else{
					List<org.openspcoop2.core.config.Soggetto> listaSoggetti = null;
					if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
						listaSoggetti = soggettiCore.soggettiList(null, ricerca);
					}else{
						listaSoggetti = soggettiCore.soggettiList(userLogin, ricerca);
					}
					soggettiHelper.prepareSoggettiConfigList(listaSoggetti, ricerca);
				}

				//if(!pddCore.isPddEsterna(this.pdd)) {
				// sempre, anche quando passo da operativo ad esterno
				generalHelper = new GeneralHelper(session);
				gd = generalHelper.initGeneralData(request); // re-inizializzo per ricalcolare il menu in alto a destra
				//}
				
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeFinished(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.CHANGE());
				
			}

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.CHANGE());
		} 
	}
}
