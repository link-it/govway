/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.GruppiAccordo;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.GruppoAccordo;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.driver.FiltroRicercaGruppi;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.manifest.constants.InterfaceType;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.ac.AccordiCooperazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.gruppi.GruppiCore;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
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

	private String nome, descr, profcoll, 
	 filtrodup, confric, idcoll, idRifRichiesta, consord, scadenza,
	referente,versione,accordoCooperazione;
	private boolean privato, isServizioComposto;// showPrivato;
	private String statoPackage;
	private String tipoAccordo;
	private boolean validazioneDocumenti = true;
	private boolean decodeRequestValidazioneDocumenti = false;

	private ServiceBinding serviceBinding = null;
	private MessageType messageType = null;
	private InterfaceType interfaceType = null;
	
	private String tipoProtocollo;
	// Non utilizzato veniva sempre impostato false, lo lascio commentato in questo punto perche' veniva passato nella decode della richiesta multipart... 
	//	private boolean isBackwardCompatibilityAccordo11 = false;

	private String editMode = null;
	// Protocol Properties
	private IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
	private ConsoleConfiguration consoleConfiguration =null;
	private ProtocolProperties protocolProperties = null;
	private IProtocolFactory<?> protocolFactory = null;
	private IRegistryReader registryReader = null; 
	private IConfigIntegrationReader configRegistryReader = null; 
	private ConsoleOperationType consoleOperationType = null;
	
	private String gruppi, canale, canaleStato;
	
	private boolean nuovaVersione;

	private BinaryParameter wsdlservcorr, wsdldef, wsdlserv, wsdlconc, wsblconc, wsblserv, wsblservcorr;

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);

		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		this.consoleOperationType = ConsoleOperationType.ADD;
		
		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.ADD; 

		try {
			Boolean isModalitaVistaApiCustom = ServletUtils.getBooleanAttributeFromSession(ApiCostanti.SESSION_ATTRIBUTE_VISTA_APC_API, session, false);
			ApiHelper apcHelper = new ApiHelper(request, pd, session);
			
			this.editMode = apcHelper.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
			this.nome = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME);
			this.descr = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_DESCRIZIONE);
			this.profcoll = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROFILO_COLLABORAZIONE);
			
			
			this.wsdldef = apcHelper.getBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_DEFINITORIO);
			this.wsdlconc = apcHelper.getBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CONCETTUALE);
			this.wsdlserv = apcHelper.getBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE);
			this.wsdlservcorr = apcHelper.getBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_FRUITORE);

			this.wsblconc = apcHelper.getBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE);
			this.wsblserv = apcHelper.getBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE);
			this.wsblservcorr = apcHelper.getBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE);

			this.filtrodup = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_FILTRO_DUPLICATI);
			this.confric = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_CONFERMA_RICEZIONE);
			this.idcoll = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_COLLABORAZIONE);
			this.idRifRichiesta = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID_RIFERIMENTO_RICHIESTA);
			this.consord = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_CONSEGNA_ORDINE);
			this.scadenza = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SCADENZA);
			this.referente = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_REFERENTE);
			this.versione = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_VERSIONE);
			
			String serviceBindingS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SERVICE_BINDING);
			this.serviceBinding = StringUtils.isNotEmpty(serviceBindingS) ? ServiceBinding.valueOf(serviceBindingS) : null;
			String messageProcessorS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_MESSAGE_TYPE);
			this.messageType = (StringUtils.isNotEmpty(messageProcessorS) && !messageProcessorS.equals(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_MESSAGE_TYPE_DEFAULT)) ? MessageType.valueOf(messageProcessorS) : null;
			String formatoSpecificaS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_INTERFACE_TYPE);
			this.interfaceType = StringUtils.isNotEmpty(formatoSpecificaS) ? InterfaceType.toEnumConstant(formatoSpecificaS) : null;
			
			this.gruppi = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_GRUPPI);
			this.canale = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_CANALE);
			this.canaleStato = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_CANALE_STATO);

			// patch per version spinner fino a che non si trova un modo piu' elegante
			// Veniva sempre impostato false, lo lascio commentato in questo punto perche' veniva passato nella decode della richiesta multipart...
			//			if(this.isBackwardCompatibilityAccordo11){
			//				if("0".equals(this.versione))
			//					this.versione = "";
			//			}

			String priv = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PRIVATO);
			this.privato = ServletUtils.isCheckBoxEnabled(priv);

			String isServComp = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_IS_SERVIZIO_COMPOSTO);
			this.isServizioComposto = ServletUtils.isCheckBoxEnabled(isServComp);

			this.accordoCooperazione = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ACCORDO_COOPERAZIONE);
			this.statoPackage = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_STATO_PACKAGE);

			this.tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(this.tipoAccordo))
				this.tipoAccordo = null;
			if(this.tipoAccordo!=null){
				if(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_PARTE_COMUNE.equals(this.tipoAccordo)){
					this.isServizioComposto = false;
				}else if(AccordiServizioParteComuneCostanti.PARAMETRO_VALORE_APC_TIPO_ACCORDO_SERVIZIO_COMPOSTO.equals(this.tipoAccordo)){
					this.isServizioComposto = true;
				}
			}

			//			String ct = request.getContentType();
			//			if ((ct != null) && (ct.indexOf(Costanti.MULTIPART) != -1)) {
			if (apcHelper.isMultipart()) {
				//this.decodeRequest(request,ch.core.isBackwardCompatibilityAccordo11());
				//				this.decodeRequestValidazioneDocumenti = false; // init
				//				this.decodeRequest(request,false);

				//Quando trovava la linea corrispondente a AccordiServizioParteComuneCostanti.PARAMETRO_APC_VALIDAZIONE_DOCUMENTI  
				//eseguiva questo codice 
				this.decodeRequestValidazioneDocumenti = true;
				String tmpValidazioneDocumenti = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_VALIDAZIONE_DOCUMENTI);
				this.validazioneDocumenti = ServletUtils.isCheckBoxEnabled(tmpValidazioneDocumenti);
			}

			if(ServletUtils.isEditModeInProgress(this.editMode)){// && apcHelper.isEditModeInProgress()){
				// primo accesso alla servlet
				this.validazioneDocumenti = true;
			}else{
				if(!this.decodeRequestValidazioneDocumenti){
					String tmpValidazioneDocumenti = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_VALIDAZIONE_DOCUMENTI);
					this.validazioneDocumenti = ServletUtils.isCheckBoxEnabled(tmpValidazioneDocumenti);
				}
			}

			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			SoggettiCore soggettiCore = new SoggettiCore(apcCore);
			AccordiCooperazioneCore acCore = new AccordiCooperazioneCore(apcCore);
			GruppiCore gruppiCore = new GruppiCore(apcCore);
			ConfigurazioneCore confCore = new ConfigurazioneCore(apcCore);
			String labelAccordoServizio = AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(this.tipoAccordo);
			
			// carico i canali
			CanaliConfigurazione gestioneCanali = confCore.getCanaliConfigurazione(false);
			List<CanaleConfigurazione> canaleList = gestioneCanali != null ? gestioneCanali.getCanaleList() : new ArrayList<>();
			boolean gestioneCanaliEnabled = gestioneCanali != null && org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(gestioneCanali.getStato());
			
			// Tipi protocollo supportati
			// Controllo comunque quelli operativi, almeno uno deve esistere
			List<String> listaTipiProtocollo = apcCore.getProtocolliByFilter(session, true, PddTipologia.OPERATIVO, false, this.isServizioComposto);

			// primo accesso 
			this.tipoProtocollo =  apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROTOCOLLO);
			if(this.tipoProtocollo == null){
				this.tipoProtocollo = apcCore.getProtocolloDefault(session, listaTipiProtocollo);
			}
			
			String nuovaVersioneTmp = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_API_NUOVA_VERSIONE);
			this.nuovaVersione = ServletUtils.isCheckBoxEnabled(nuovaVersioneTmp);
			int gestioneNuovaVersione_min = 1;
			boolean nuovaVersioneRidefinisciInterfaccia = true;
			long gestioneNuovaVersione_oldIdApc = -1;
			List<ProtocolProperty> gestioneNuovaVersione_oldProtocolProperties = null;
			if(this.nuovaVersione) {
				
				String nuovaVersioneTmp_minVersion = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_API_NUOVA_VERSIONE_MIN);
				if(!StringUtils.isEmpty(nuovaVersioneTmp_minVersion)) {
					gestioneNuovaVersione_min = Integer.parseInt(nuovaVersioneTmp_minVersion);
				}
								
				String tmpIdPrecedenteAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
				if(!StringUtils.isEmpty(tmpIdPrecedenteAccordo)) {
					
					// ci entro solamente la prima volta al "click" sulla nuova versione
					
					nuovaVersioneRidefinisciInterfaccia = true; // default
					
					// fisso alcuni valori
					long idAccordoPrec = Long.valueOf(tmpIdPrecedenteAccordo);
					gestioneNuovaVersione_oldIdApc = idAccordoPrec;
					AccordoServizioParteComune aspc = apcCore.getAccordoServizioFull(idAccordoPrec);
					gestioneNuovaVersione_oldProtocolProperties = aspc.getProtocolPropertyList();
					IDAccordo idAccordoPrecedente = IDAccordoFactory.getInstance().getIDAccordoFromAccordo(aspc);
					this.nome = aspc.getNome();
					this.referente = soggettiCore.getSoggetto(aspc.getSoggettoReferente().toIDSoggetto()).getId().longValue()+"";
					this.tipoProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(aspc.getSoggettoReferente().getTipo());
					this.serviceBinding = apcCore.toMessageServiceBinding(aspc.getServiceBinding());
					if(aspc.getMessageType()!=null) {
						this.messageType = apcCore.toMessageMessageType(aspc.getMessageType());
					}
					if(aspc.getServizioComposto()!=null && aspc.getServizioComposto().getAccordoCooperazione()!=null && !"".equals(aspc.getServizioComposto().getAccordoCooperazione())) {
						IDAccordoCooperazione idAccordoCooperazione = IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromUri(aspc.getServizioComposto().getAccordoCooperazione());
						this.accordoCooperazione = acCore.getAccordoCooperazione(idAccordoCooperazione).getId().longValue()+"";
						this.isServizioComposto = true;
					}
					if(this.interfaceType==null) {
						this.interfaceType = apcCore.formatoSpecifica2InterfaceType(aspc.getFormatoSpecifica());
					}
					if(this.descr==null || StringUtils.isEmpty(this.descr)) {
						this.descr = aspc.getDescrizione();
					}
					if(this.gruppi==null || StringUtils.isEmpty(this.gruppi)) {
						if(aspc.getGruppi()!=null && aspc.getGruppi().getGruppoList()!=null && !aspc.getGruppi().getGruppoList().isEmpty()) {
							List<String> nomiGruppi = aspc.getGruppi().getGruppoList().stream().flatMap(e-> Stream.of(e.getNome())).collect(Collectors.toList());
							this.gruppi = StringUtils.join(nomiGruppi, ",");
						}
					}
					if(this.profcoll==null || StringUtils.isEmpty(this.profcoll)) {
						this.profcoll = AccordiServizioParteComuneHelper.convertProfiloCollaborazioneDB2View(aspc.getProfiloCollaborazione());
					}
					if(this.filtrodup==null || StringUtils.isEmpty(this.filtrodup)) {
						this.filtrodup = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(aspc.getFiltroDuplicati());
					}
					if(this.confric==null || StringUtils.isEmpty(this.confric)) {
						this.confric = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(aspc.getConfermaRicezione());
					}
					if(this.idcoll==null || StringUtils.isEmpty(this.idcoll)) {
						this.idcoll = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(aspc.getIdCollaborazione());
					}
					if(this.idRifRichiesta==null || StringUtils.isEmpty(this.idRifRichiesta)) {
						this.idRifRichiesta = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(aspc.getIdRiferimentoRichiesta());
					}
					if(this.consord==null || StringUtils.isEmpty(this.consord)) {
						this.consord = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(aspc.getConsegnaInOrdine());
					}
					if(this.scadenza==null || StringUtils.isEmpty(this.scadenza)) {
						this.scadenza = aspc.getScadenza() != null ? aspc.getScadenza() : "";
					}
					
					gestioneNuovaVersione_min = apcCore.getAccordoServizioParteComuneNextVersion(idAccordoPrecedente);
					if(this.versione==null || StringUtils.isEmpty(this.versione)) {
						this.versione = gestioneNuovaVersione_min+"";
					}
					if(this.canale==null || StringUtils.isEmpty(this.canale)) {
						this.canale = aspc.getCanale();
					}
					if(this.canaleStato==null || StringUtils.isEmpty(this.canaleStato)) {
						if(this.canale == null) {
							this.canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_DEFAULT;
						} else {
							this.canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_RIDEFINITO;
						}
					} 
				}
				else {
					String nuovaVersioneRidefinisciInterfaccia_tmp = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_API_NUOVA_VERSIONE_RIDEFINISCI_INTERFACCIA);
					nuovaVersioneRidefinisciInterfaccia = ServletUtils.isCheckBoxEnabled(nuovaVersioneRidefinisciInterfaccia_tmp);
				
					String nuovaVersioneOldIdApc_tmp = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_API_NUOVA_VERSIONE_OLD_ID_APC);
					gestioneNuovaVersione_oldIdApc = Long.valueOf(nuovaVersioneOldIdApc_tmp);
				}
			}
			
			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(this.tipoProtocollo);
			this.consoleDynamicConfiguration =  this.protocolFactory.createDynamicConfigurationConsole();
			this.registryReader = soggettiCore.getRegistryReader(this.protocolFactory); 
			this.configRegistryReader = soggettiCore.getConfigIntegrationReader(this.protocolFactory);
			
			// service binding 
			if(this.serviceBinding == null) {
				this.serviceBinding  = soggettiCore.getDefaultServiceBinding(this.protocolFactory);
			}
			
			FiltroRicercaGruppi filtroRicerca = new FiltroRicercaGruppi();
			filtroRicerca.setServiceBinding(apcCore.fromMessageServiceBinding(this.serviceBinding));
			List<String> elencoGruppi = gruppiCore.getAllGruppi(filtroRicerca);

			boolean showReferente = false;
			if(this.isServizioComposto){
				showReferente = true;
			}
			else {
				showReferente = apcCore.isSupportatoSoggettoReferente(this.tipoProtocollo);
			}
			
			// ID Accordo Null per default
			IDAccordo idApc = null;			
			this.consoleConfiguration = this.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE) ? 
					this.consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteComune(this.consoleOperationType, apcHelper, 
							this.registryReader, this.configRegistryReader, idApc)
					: this.consoleDynamicConfiguration.getDynamicConfigAccordoServizioComposto(this.consoleOperationType, apcHelper, 
							this.registryReader, this.configRegistryReader, idApc);
					
			this.protocolProperties = apcHelper.estraiProtocolPropertiesDaRequest(this.consoleConfiguration, this.consoleOperationType);
			
			if(this.nuovaVersione && gestioneNuovaVersione_oldProtocolProperties!=null && !gestioneNuovaVersione_oldProtocolProperties.isEmpty()){
				ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(this.protocolProperties, gestioneNuovaVersione_oldProtocolProperties, this.consoleOperationType);
			}

			// Flag per controllare il mapping automatico di porttype e operation
			boolean enableAutoMapping = apcCore.isEnableAutoMappingWsdlIntoAccordo();
			boolean enableAutoMapping_estraiXsdSchemiFromWsdlTypes = apcCore.isEnableAutoMappingWsdlIntoAccordo_estrazioneSchemiInWsdlTypes();

			
			
			//Carico la lista dei tipi di soggetti gestiti dal protocollo
			List<String> tipiSoggettiGestitiProtocollo = soggettiCore.getTipiSoggettiGestitiProtocollo(this.tipoProtocollo);

			// Preparo il menu
			apcHelper.makeMenu();
				
			if(listaTipiProtocollo.size()<=0) {
				boolean msg = true;
				if(this.isServizioComposto) {
					List<String> listaTipiProtocolloSenzaAccordiCooperazione = apcCore.getProtocolliByFilter(session, true, PddTipologia.OPERATIVO, false, false);
					if(listaTipiProtocolloSenzaAccordiCooperazione.size()>0) {
						pd.setMessage("Non risultano registrati accordi di cooperazione", Costanti.MESSAGE_TYPE_INFO);
						msg = false;
					}
				}
				if(msg) {
					if(soggettiCore.isGestionePddAbilitata(apcHelper)) {
						pd.setMessage("Non risultano registrati soggetti associati a porte di dominio di tipo operativo", Costanti.MESSAGE_TYPE_INFO);
					}
					else {
						pd.setMessage("Non risultano registrati soggetti di dominio interno", Costanti.MESSAGE_TYPE_INFO);
					}
				}
				pd.disableEditMode();

				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, 
						ForwardParams.ADD());
			}
			
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
						//soggettiListLabelTmp.add(soggetto.getTipo() + "/" + soggetto.getNome());
						soggettiListLabelTmp.add(apcHelper.getLabelNomeSoggetto(this.tipoProtocollo, soggetto.getTipo() , soggetto.getNome() ));
					}
				}
			}
			providersList = soggettiListTmp.toArray(new String[1]);
			providersListLabel = soggettiListLabelTmp.toArray(new String[1]);

			String[] accordiCooperazioneEsistenti=null;
			String[] accordiCooperazioneEsistentiLabel=null;

 
			String postBackElementName = apcHelper.getParameter(Costanti.POSTBACK_ELEMENT_NAME);

			// Controllo se ho modificato il protocollo, resetto il referente e il service binding
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROTOCOLLO)){
					this.referente = null;
					apcHelper.deleteBinaryParameters(this.wsdlconc,this.wsdldef,this.wsdlserv,this.wsdlservcorr,this.wsblconc,this.wsblserv,this.wsblservcorr);
					apcHelper.deleteProtocolPropertiesBinaryParameters(this.wsdlconc,this.wsdldef,this.wsdlserv,this.wsdlservcorr,this.wsblconc,this.wsblserv,this.wsblservcorr);
					this.serviceBinding  = soggettiCore.getDefaultServiceBinding(this.protocolFactory);
					this.interfaceType = null;
					this.messageType = null;
					
					this.wsdldef = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_DEFINITORIO);
					this.wsdlconc = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CONCETTUALE);
					this.wsdlserv = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE);
					this.wsdlservcorr = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_FRUITORE);

					this.wsblconc = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE);
					this.wsblserv = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE);
					this.wsblservcorr = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE);
					
					filtroRicerca = new FiltroRicercaGruppi();
					filtroRicerca.setServiceBinding(apcCore.fromMessageServiceBinding(this.serviceBinding));
					elencoGruppi = gruppiCore.getAllGruppi(filtroRicerca);
				}
				else if(postBackElementName.equalsIgnoreCase(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SERVICE_BINDING)){
					this.interfaceType = null;
					this.messageType = null;
					
					this.wsdldef = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_DEFINITORIO);
					this.wsdlconc = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CONCETTUALE);
					this.wsdlserv = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE);
					this.wsdlservcorr = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_FRUITORE);

					this.wsblconc = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE);
					this.wsblserv = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE);
					this.wsblservcorr = apcHelper.newBinaryParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE);
					
					filtroRicerca = new FiltroRicercaGruppi();
					filtroRicerca.setServiceBinding(apcCore.fromMessageServiceBinding(this.serviceBinding));
					elencoGruppi = gruppiCore.getAllGruppi(filtroRicerca);
				}
			}

			org.openspcoop2.core.registry.Soggetto soggettoReferente = null;
			try{
				soggettoReferente = soggettiCore.getSoggettoRegistro(Long.valueOf(this.referente));
			}catch(Exception e){}
			List<String> tipiSoggettiCompatibili = new ArrayList<String>();
			if(soggettoReferente!=null){
				String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(soggettoReferente.getTipo());
				tipiSoggettiCompatibili = soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);
			}

			if(this.isServizioComposto) {
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
						accordiCooperazioneEsistentiLabel[i] = apcHelper.getLabelIdAccordoCooperazione(acCore.getAccordoCooperazione(singleAC.getId())); 
						i++;
					}
				} else {
					accordiCooperazioneEsistenti = new String[1];
					accordiCooperazioneEsistentiLabel = new String[1];
					accordiCooperazioneEsistenti[0]="-";
					accordiCooperazioneEsistentiLabel[0]="-";
				}
			}
			
			// message type resta null
			// fromato specifica
			if(this.interfaceType == null) {
				if(this.serviceBinding != null) {
					switch(this.serviceBinding) {
					case REST:
						this.interfaceType = InterfaceType.toEnumConstant(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_INTERFACE_TYPE_REST);
						break;
					case SOAP:
					default:
						this.interfaceType = InterfaceType.toEnumConstant(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_INTERFACE_TYPE_SOAP);
						break;
					}
				}
			}

			// Se nome = null, devo visualizzare la pagina per l'inserimento dati
			String servletNameApcList = isModalitaVistaApiCustom ? ApiCostanti.SERVLET_NAME_APC_API_LIST : AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST; 
			Parameter pTipoAccordo = AccordiServizioParteComuneUtilities.getParametroAccordoServizio(this.tipoAccordo);
			List<Parameter> listaParams = new ArrayList<>();
			listaParams.add(new Parameter(labelAccordoServizio, servletNameApcList, pTipoAccordo));
			listaParams.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI,null));
			
			
			if(ServletUtils.isEditModeInProgress(this.editMode)){ // && apcHelper.isEditModeInProgress()){

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, listaParams);

				if(this.nome==null){
					this.nome = "";
					this.accordoCooperazione = "";
					this.confric  = "";
					this.consord = "";
					this.descr = "";
					this.filtrodup = "yes";
					this.idcoll = "";
					this.idRifRichiesta = "";
					this.profcoll = "sincrono";
					
					if(this.serviceBinding != null) {
						switch(this.serviceBinding) {
						case REST:
							if( this.protocolFactory.createProtocolConfiguration().isSupportato(this.serviceBinding, ProfiloDiCollaborazione.ONEWAY)) {
								this.profcoll = "oneway"; // es. as4
							}
							else {
								this.profcoll = "sincrono";
							}
							break;
						case SOAP:
						default:
							break;
						}
					}
					
					this.accordoCooperazione = "-1";
					this.scadenza= "";
					this.privato = false;
					if(this.tipoProtocollo == null){
						this.tipoProtocollo = apcCore.getProtocolloDefault(session, listaTipiProtocollo);
					}
					this.referente= "";

					if(this.tipoAccordo!=null){
						if("apc".equals(this.tipoAccordo)){
							this.isServizioComposto = false;
						}else if("asc".equals(this.tipoAccordo)){
							this.isServizioComposto = true;
						}
					}else{
						this.isServizioComposto=false;
					}
					if(apcHelper.isShowGestioneWorkflowStatoDocumenti()){
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
					
					this.gruppi = "";
					this.canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_DEFAULT;
					this.canale = "";
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				// update della configurazione 
				if(this.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE))
					this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteComune(this.consoleConfiguration,
						this.consoleOperationType, apcHelper, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idApc);
				else 
					this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioComposto(this.consoleConfiguration,
							this.consoleOperationType, apcHelper, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idApc);

				dati = apcHelper.addAccordiToDati(dati, this.nome, this.descr, this.profcoll, this.wsdldef, this.wsdlconc, this.wsdlserv, this.wsdlservcorr, this.wsblconc,this.wsblserv,this.wsblservcorr,
						this.filtrodup, this.confric, this.idcoll, this.idRifRichiesta, this.consord, this.scadenza, "0", tipoOp, 
						false, true, this.referente, this.versione, providersList, providersListLabel, 
						this.privato, this.isServizioComposto, accordiCooperazioneEsistenti, accordiCooperazioneEsistentiLabel, 
						this.accordoCooperazione, this.statoPackage, this.statoPackage, this.tipoAccordo, this.validazioneDocumenti, 
						this.tipoProtocollo, listaTipiProtocollo,false,false,this.protocolFactory,
						this.serviceBinding,this.messageType,this.interfaceType, this.gruppi, elencoGruppi,
						this.nuovaVersione, gestioneNuovaVersione_min, nuovaVersioneRidefinisciInterfaccia, gestioneNuovaVersione_oldIdApc,
						false, this.canaleStato, this.canale, canaleList, gestioneCanaliEnabled);

				// aggiunta campi custom
				dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties);

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
			boolean isOk = apcHelper.accordiCheckData(tipoOp, this.nome, this.descr, this.profcoll, 
					this.wsdldef, this.wsdlconc, this.wsdlserv, this.wsdlservcorr, 
					this.filtrodup, this.confric, this.idcoll, this.idRifRichiesta, this.consord, 
					this.scadenza, "0",this.referente, this.versione,this.accordoCooperazione,this.privato,visibilitaAccordoCooperazione,null,
					this.wsblconc,this.wsblserv,this.wsblservcorr, this.validazioneDocumenti, this.tipoProtocollo,null,this.serviceBinding,this.messageType,this.interfaceType,
					showReferente, this.gruppi, this.canaleStato, this.canale, gestioneCanaliEnabled);

			// updateDynamic
			if(isOk) {
				if(this.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE))
					this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteComune(this.consoleConfiguration,
						this.consoleOperationType, apcHelper, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idApc);
				else 
					this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioComposto(this.consoleConfiguration,
							this.consoleOperationType, apcHelper, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idApc);
			}
			
			// Validazione base dei parametri custom 
			if(isOk){
				try{
					apcHelper.validaProtocolProperties(this.consoleConfiguration, this.consoleOperationType, this.protocolProperties);
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}

			// Valido i parametri custom se ho gia' passato tutta la validazione prevista
			if(isOk){
				try{
					idApc = apcHelper.getIDAccordoFromValues(this.nome, this.referente, this.versione, visibilitaAccordoCooperazione);
					//validazione campi dinamici
					if(this.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE))
						this.consoleDynamicConfiguration.validateDynamicConfigAccordoServizioParteComune(this.consoleConfiguration, this.consoleOperationType, apcHelper, this.protocolProperties, 
								this.registryReader, this.configRegistryReader, idApc);
					else 
						this.consoleDynamicConfiguration.validateDynamicConfigAccordoServizioComposto(this.consoleConfiguration, this.consoleOperationType, apcHelper, this.protocolProperties, 
								this.registryReader, this.configRegistryReader, idApc);
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}

			if (!isOk) {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, listaParams);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				// update della configurazione 
				if(this.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE))
					this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteComune(this.consoleConfiguration,
						this.consoleOperationType, apcHelper, this.protocolProperties, 
						this.registryReader, this.configRegistryReader, idApc);
				else 
					this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioComposto(this.consoleConfiguration,
							this.consoleOperationType, apcHelper, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idApc);

				dati = apcHelper.addAccordiToDati(dati, this.nome, this.descr, this.profcoll, this.wsdldef, this.wsdlconc, this.wsdlserv, this.wsdlservcorr, this.wsblconc,this.wsblserv,this.wsblservcorr,
						this.filtrodup, this.confric, this.idcoll, this.idRifRichiesta, this.consord, this.scadenza, "0", tipoOp, 
						false, true,this.referente, this.versione, providersList, providersListLabel,
						this.privato, this.isServizioComposto, accordiCooperazioneEsistenti, accordiCooperazioneEsistentiLabel, 
						this.accordoCooperazione, this.statoPackage, this.statoPackage, this.tipoAccordo, this.validazioneDocumenti, 
						this.tipoProtocollo, listaTipiProtocollo,false,false,this.protocolFactory,
						this.serviceBinding,this.messageType,this.interfaceType, this.gruppi, elencoGruppi,
						this.nuovaVersione, gestioneNuovaVersione_min, nuovaVersioneRidefinisciInterfaccia, gestioneNuovaVersione_oldIdApc,
						false, this.canaleStato, this.canale, canaleList, gestioneCanaliEnabled);

				// aggiunta campi custom
				dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.ADD());
			}
			
			List<Object> objectToCreate = new ArrayList<>();

			AccordoServizioParteComune as = new AccordoServizioParteComune();

			// preparo l'oggetto
			as.setNome(this.nome);
			as.setDescrizione(this.descr);

			// profilo collaborazione
			// controllo profilo collaborazione
			this.profcoll = AccordiServizioParteComuneHelper.convertProfiloCollaborazioneView2DB(this.profcoll);
			as.setProfiloCollaborazione(ProfiloCollaborazione.toEnumConstant(this.profcoll));

			String wsdlconcS = this.wsdlconc.getValue() != null ? new String(this.wsdlconc.getValue()) : null; 
			as.setByteWsdlConcettuale(wsdlconcS != null && !wsdlconcS.trim().replaceAll("\n", "").equals("") ? wsdlconcS.trim().getBytes() : null);
			String wsdldefS = this.wsdldef.getValue() != null ? new String(this.wsdldef.getValue()) : null; 
			as.setByteWsdlDefinitorio(wsdldefS != null && !wsdldefS.trim().replaceAll("\n", "").equals("") ? wsdldefS.trim().getBytes() : null);
			String wsdlservS = this.wsdlserv.getValue() != null ? new String(this.wsdlserv.getValue()) : null; 
			as.setByteWsdlLogicoErogatore(wsdlservS != null && !wsdlservS.trim().replaceAll("\n", "").equals("") ? wsdlservS.trim().getBytes() : null);
			String wsdlservcorrS = this.wsdlservcorr.getValue() != null ? new String(this.wsdlservcorr.getValue()) : null; 
			as.setByteWsdlLogicoFruitore(wsdlservcorrS != null && !wsdlservcorrS.trim().replaceAll("\n", "").equals("") ? wsdlservcorrS.trim().getBytes() : null);

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
			as.setIdRiferimentoRichiesta(StatoFunzionalita.toEnumConstant(AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoView2DB(this.idRifRichiesta)));
			if ((this.scadenza != null) && (!"".equals(this.scadenza)))
				as.setScadenza(this.scadenza);
			as.setSuperUser(userLogin);
			as.setUtilizzoSenzaAzione(true);// default true
			String wsblconcS = this.wsblconc.getValue() != null ? new String(this.wsblconc.getValue()) : null; 
			as.setByteSpecificaConversazioneConcettuale((wsblconcS != null) && !wsblconcS.trim().replaceAll("\n", "").equals("") ? wsblconcS.trim().getBytes() : null);
			String wsblservS = this.wsblserv.getValue() != null ? new String(this.wsblserv.getValue()) : null;
			as.setByteSpecificaConversazioneErogatore((wsblservS != null) && !wsblservS.trim().replaceAll("\n", "").equals("") ? wsblservS.trim().getBytes() : null);
			String wsblservcorrS = this.wsblservcorr.getValue() != null ? new String(this.wsblservcorr.getValue()) : null;
			as.setByteSpecificaConversazioneFruitore((wsblservcorrS != null) && !wsblservcorrS.trim().replaceAll("\n", "").equals("") ? wsblservcorrS.trim().getBytes() : null);
			
			// servicebinding / messagetype / formatospecifica
			as.setServiceBinding(apcCore.fromMessageServiceBinding(this.serviceBinding));
			as.setMessageType(apcCore.fromMessageMessageType(this.messageType));
			as.setFormatoSpecifica(apcCore.interfaceType2FormatoSpecifica(this.interfaceType));
			
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
				if(!showReferente) {
					// Recupero Soggetto Default
					IDSoggetto idSoggetto = apcCore.getSoggettoOperativoDefault(userLogin, this.tipoProtocollo);
					Soggetto s = soggettiCore.getSoggetto(idSoggetto);
					IdSoggetto assr = new IdSoggetto();
					assr.setTipo(s.getTipo());
					assr.setNome(s.getNome());
					as.setSoggettoReferente(assr);
				}
				else {
					as.setSoggettoReferente(null);
				}
			}
			if(this.versione!=null){
				as.setVersione(Integer.parseInt(this.versione));
			}
			as.setPrivato(this.privato ? Boolean.TRUE : Boolean.FALSE);

			if(this.accordoCooperazione!=null && !"".equals(this.accordoCooperazione) && !"-".equals(this.accordoCooperazione)){
				AccordoServizioParteComuneServizioComposto assc = new AccordoServizioParteComuneServizioComposto();
				assc.setIdAccordoCooperazione(Long.parseLong(this.accordoCooperazione));
				as.setServizioComposto(assc);
			}

			// stato
			as.setStatoPackage(this.statoPackage);

			// gruppi
			if(StringUtils.isNotEmpty(this.gruppi)) {
				if(as.getGruppi() == null)
					as.setGruppi(new GruppiAccordo());
			
				List<String> nomiGruppi = Arrays.asList(this.gruppi.split(","));
				
				for (String nomeGruppo : nomiGruppi) {
					if(!gruppiCore.existsGruppo(nomeGruppo)) {
						Gruppo nuovoGruppo = new Gruppo();
						nuovoGruppo.setNome(nomeGruppo);
						objectToCreate.add(nuovoGruppo);
					}
					
					GruppoAccordo gruppoAccordo = new GruppoAccordo();
					gruppoAccordo.setNome(nomeGruppo);
					as.getGruppi().addGruppo(gruppoAccordo );
				}
			}
			
			// canale
			if(gestioneCanaliEnabled) {
				if(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_RIDEFINITO.equals(this.canaleStato)) {
					as.setCanale(this.canale);
				} else {
					as.setCanale(null);
				}
			} else {
				as.setCanale(null);
			}

			if(this.nuovaVersione && !nuovaVersioneRidefinisciInterfaccia) {
				IDAccordo idAcc = apcCore.getIdAccordoServizio(gestioneNuovaVersione_oldIdApc);
				AccordoServizioParteComune aspcOld = apcCore.getAccordoServizioFull(idAcc, true); // devo leggere anche gli allegati
				as.setAllegatoList(aspcOld.getAllegatoList());
				as.setSpecificaSemiformaleList(aspcOld.getSpecificaSemiformaleList());
				as.setAzioneList(aspcOld.getAzioneList());
				as.setPortTypeList(aspcOld.getPortTypeList());
				as.setResourceList(aspcOld.getResourceList());
				as.setByteWsdlDefinitorio(aspcOld.getByteWsdlDefinitorio());
				as.setByteWsdlConcettuale(aspcOld.getByteWsdlConcettuale());
				as.setByteWsdlLogicoErogatore(aspcOld.getByteWsdlLogicoErogatore());
				as.setByteWsdlLogicoFruitore(aspcOld.getByteWsdlLogicoFruitore());
				as.setByteSpecificaConversazioneConcettuale(aspcOld.getByteSpecificaConversazioneConcettuale());
				as.setByteSpecificaConversazioneErogatore(aspcOld.getByteSpecificaConversazioneErogatore());
				as.setByteSpecificaConversazioneFruitore(aspcOld.getByteSpecificaConversazioneFruitore());
			}
			

			// Check stato
			if(apcHelper.isShowGestioneWorkflowStatoDocumenti()){

				try{
					boolean utilizzoAzioniDiretteInAccordoAbilitato = apcCore.isShowAccordiColonnaAzioni();
					apcCore.validaStatoAccordoServizio(as, utilizzoAzioniDiretteInAccordoAbilitato, true);
				}catch(ValidazioneStatoPackageException validazioneException){

					// Setto messaggio di errore
					pd.setMessage(validazioneException.toString());

					// setto la barra del titolo
					ServletUtils.setPageDataTitle(pd, listaParams);

					// preparo i campi
					Vector<DataElement> dati = new Vector<DataElement>();

					dati.addElement(ServletUtils.getDataElementForEditModeFinished());

					// update della configurazione 
					if(this.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE))
						this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteComune(this.consoleConfiguration,
							this.consoleOperationType, apcHelper, this.protocolProperties, 
							this.registryReader, this.configRegistryReader, idApc);
					else 
						this.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioComposto(this.consoleConfiguration,
								this.consoleOperationType, apcHelper, this.protocolProperties, 
								this.registryReader, this.configRegistryReader, idApc);

					dati = apcHelper.addAccordiToDati(dati, this.nome, this.descr, this.profcoll, this.wsdldef, this.wsdlconc, this.wsdlserv, this.wsdlservcorr, this.wsblconc,this.wsblserv,this.wsblservcorr,
							this.filtrodup, this.confric, this.idcoll, this.idRifRichiesta, this.consord, this.scadenza, "0", tipoOp, 
							false, true,this.referente, this.versione, providersList, providersListLabel,
							this.privato, this.isServizioComposto, accordiCooperazioneEsistenti, accordiCooperazioneEsistentiLabel, 
							this.accordoCooperazione, this.statoPackage, this.statoPackage, this.tipoAccordo, this.validazioneDocumenti, 
							this.tipoProtocollo, listaTipiProtocollo,false,false,this.protocolFactory,
							this.serviceBinding,this.messageType,this.interfaceType, this.gruppi, elencoGruppi,
							this.nuovaVersione, gestioneNuovaVersione_min, nuovaVersioneRidefinisciInterfaccia, gestioneNuovaVersione_oldIdApc,
							false, this.canaleStato, this.canale, canaleList, gestioneCanaliEnabled);

					// aggiunta campi custom
					dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties);

					pd.setDati(dati);

					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.ADD());

				}
			}

			// Automapping
			if(!this.nuovaVersione || nuovaVersioneRidefinisciInterfaccia) {
				AccordiServizioParteComuneUtilities.mapppingAutomaticoInterfaccia(as, apcCore, 
						enableAutoMapping, this.validazioneDocumenti, enableAutoMapping_estraiXsdSchemiFromWsdlTypes, facilityUnicoWSDL_interfacciaStandard, 
						this.tipoProtocollo, this.interfaceType);
			}

			//imposto properties custom
			as.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(this.protocolProperties, this.consoleOperationType,null));
			
			objectToCreate.add(as);
			// effettuo le operazioni
			apcCore.performCreateOperation(userLogin, apcHelper.smista(), objectToCreate.toArray(new Object[objectToCreate.size()]));
			
			// cancello i file temporanei
			apcHelper.deleteBinaryParameters(this.wsdlconc,this.wsdldef,this.wsdlserv,this.wsdlservcorr,this.wsblconc,this.wsblserv,this.wsblservcorr);
			apcHelper.deleteBinaryProtocolPropertiesTmpFiles(this.protocolProperties);

			// Verifico stato
			boolean incomplete = apcHelper.setMessageWarningStatoConsistenzaAccordo(true, as);
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			if(incomplete || apcCore.isSetSearchAfterAdd()) {
				apcCore.setSearchAfterAdd(Liste.ACCORDI, as.getNome(), session, ricerca);
			}
			
			List<AccordoServizioParteComuneSintetico> lista = AccordiServizioParteComuneUtilities.accordiList(apcCore, userLogin, ricerca, this.tipoAccordo);

			if(isModalitaVistaApiCustom) {
				apcHelper.prepareApiList(lista, ricerca, this.tipoAccordo); 
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				return ServletUtils.getStrutsForwardEditModeFinished(mapping, ApiCostanti.OBJECT_NAME_APC_API, ForwardParams.ADD());
			}
			
			apcHelper.prepareAccordiList(lista, ricerca, this.tipoAccordo);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, ForwardParams.ADD());
		} 

	}
}
