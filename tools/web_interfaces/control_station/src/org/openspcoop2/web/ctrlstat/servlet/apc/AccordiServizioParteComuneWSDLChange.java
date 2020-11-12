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

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
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
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.beans.ResourceSintetica;
import org.openspcoop2.core.registry.driver.BeanUtilities;
import org.openspcoop2.core.registry.driver.FiltroRicercaGruppi;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
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
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
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
 * accordiWSDLChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComuneWSDLChange extends Action {

	private String id, tipo, wsdl,tipoAccordo;
	private boolean validazioneDocumenti = true;
	private boolean decodeRequestValidazioneDocumenti = false;
	private String editMode = null;
	private BinaryParameter wsdlservcorr, wsdldef, wsdlserv, wsdlconc, wsblconc, wsblserv, wsblservcorr;
	
	private boolean aggiornaEsistenti = true;
	private boolean decodeRequestAggiornaEsistenti= false;
	
	private boolean eliminaNonPresentiNuovaInterfaccia = true;
	private boolean decodeRequestEliminaNonPresentiNuovaInterfaccia= false;
	
	// Protocol Properties
	private IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
	private ConsoleConfiguration consoleConfiguration =null;
	private ProtocolProperties protocolProperties = null;
	private IProtocolFactory<?> protocolFactory= null;
	private IRegistryReader registryReader = null; 
	private IConfigIntegrationReader configRegistryReader = null; 
	private ConsoleOperationType consoleOperationType = null;
	
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
		IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();

		boolean isSupportoProfiloAsincrono = false;
		
		this.consoleOperationType = ConsoleOperationType.CHANGE;
		
		try {
			ApiHelper apcHelper = new ApiHelper(request, pd, session);
			
			boolean isModalitaAvanzata = apcHelper.isModalitaAvanzata();
			
			String actionConfirm = apcHelper.getParameter(Costanti.PARAMETRO_ACTION_CONFIRM);

			this.editMode = apcHelper.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);

			this.id = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			this.tipo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_WSDL);
			this.wsdl = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL);
			this.tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(this.tipoAccordo))
				this.tipoAccordo = null;

			if(apcHelper.isMultipart()){
				this.decodeRequestValidazioneDocumenti = true;
				String tmpValidazioneDocumenti = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_VALIDAZIONE_DOCUMENTI);
				this.validazioneDocumenti = ServletUtils.isCheckBoxEnabled(tmpValidazioneDocumenti);
				
				this.decodeRequestAggiornaEsistenti = true;
				String aggiornaEsistentiS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_UPDATE_WSDL_AGGIORNA);
				this.aggiornaEsistenti = ServletUtils.isCheckBoxEnabled(aggiornaEsistentiS);
				
				this.decodeRequestEliminaNonPresentiNuovaInterfaccia = true;
				String eliminaNonPresentiNuovaInterfacciaS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_UPDATE_WSDL_ELIMINA);
				this.eliminaNonPresentiNuovaInterfaccia = ServletUtils.isCheckBoxEnabled(eliminaNonPresentiNuovaInterfacciaS);
			}
	
			//rimuovo eventuali tracce della procedura 
			if(actionConfirm == null){
				session.removeAttribute(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CHANGE_TMP);
			}else {
				// se passo da qui sto tornando dalla maschera di conferma ripristino il wsdl dalla sessione 
					this.wsdl = ServletUtils.getObjectFromSession(session, String.class, 
							AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CHANGE_TMP);
					session.removeAttribute(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CHANGE_TMP);
			}
			
			if(ServletUtils.isEditModeInProgress(this.editMode)){// && apcHelper.isEditModeInProgress()){
				// primo accesso alla servlet
				this.validazioneDocumenti = true;
				this.aggiornaEsistenti = true;
				this.eliminaNonPresentiNuovaInterfaccia = true;
			}else{
				if(!this.decodeRequestValidazioneDocumenti){
					String tmpValidazioneDocumenti = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_VALIDAZIONE_DOCUMENTI);
					this.validazioneDocumenti = ServletUtils.isCheckBoxEnabled(tmpValidazioneDocumenti);
				}
				if(!this.decodeRequestAggiornaEsistenti){
					String aggiornaEsistentiS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_UPDATE_WSDL_AGGIORNA);
					this.aggiornaEsistenti = ServletUtils.isCheckBoxEnabled(aggiornaEsistentiS);
				}
				if(!this.decodeRequestEliminaNonPresentiNuovaInterfaccia){
					String eliminaNonPresentiNuovaInterfacciaS = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_UPDATE_WSDL_ELIMINA);
					this.eliminaNonPresentiNuovaInterfaccia = ServletUtils.isCheckBoxEnabled(eliminaNonPresentiNuovaInterfacciaS);
				}
			}

			int idAcc = 0;
			try {
				idAcc = Integer.parseInt(this.id);
			} catch (Exception e) {
			}
			
			String apiGestioneParziale = apcHelper.getParameter(ApiCostanti.PARAMETRO_APC_API_GESTIONE_PARZIALE);
			if(apiGestioneParziale == null) {
				apiGestioneParziale = "";
			}
			boolean isGestioneAllegati = apiGestioneParziale.equals(ApiCostanti.VALORE_PARAMETRO_APC_API_GESTIONE_SPECIFICA_INTERFACCE);

			// Preparo il menu
			apcHelper.makeMenu();

			// Prendo il nome e il wsdl attuale dell'accordo
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(apcCore);
			SoggettiCore soggettiCore = new SoggettiCore(apcCore);
			AccordiCooperazioneCore acCore = new AccordiCooperazioneCore(apcCore);
			GruppiCore gruppiCore = new GruppiCore(apcCore);
			ConfigurazioneCore confCore = new ConfigurazioneCore(apcCore);
			
			// carico i canali
			CanaliConfigurazione gestioneCanali = confCore.getCanaliConfigurazione(false);
			List<CanaleConfigurazione> canaleList = gestioneCanali != null ? gestioneCanali.getCanaleList() : new ArrayList<>();
			boolean gestioneCanaliEnabled = gestioneCanali != null && org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(gestioneCanali.getStato());

			// Flag per controllare il mapping automatico di porttype e operation 
			boolean enableAutoMapping = apcCore.isEnableAutoMappingWsdlIntoAccordo();
			boolean enableAutoMapping_estraiXsdSchemiFromWsdlTypes = apcCore.isEnableAutoMappingWsdlIntoAccordo_estrazioneSchemiInWsdlTypes();

			AccordoServizioParteComune as = apcCore.getAccordoServizioFull(Long.valueOf(idAcc));
			boolean asWithAllegati = apcHelper.asWithAllegatiXsd(as);
			String uriAS = idAccordoFactory.getUriFromAccordo(as);
			String labelASTitle = apcHelper.getLabelIdAccordo(as); 

			IdSoggetto idSoggettoReferente = as.getSoggettoReferente();
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(idSoggettoReferente.getTipo());
		
			
			ServiceBinding serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());
			MessageType messageType = apcCore.toMessageMessageType(as.getMessageType());
			org.openspcoop2.protocol.manifest.constants.InterfaceType formatoSpecifica = apcCore.formatoSpecifica2InterfaceType(as.getFormatoSpecifica());
			String gruppi = "";
			// leggo i gruppi dall'accordo
			if(as.getGruppi() != null) {
				List<String> nomiGruppi = as.getGruppi().getGruppoList().stream().flatMap(e-> Stream.of(e.getNome())).collect(Collectors.toList());
				gruppi = StringUtils.join(nomiGruppi, ",");
			}
			
			String canale = as.getCanale();
			String canaleStato =  AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_DEFAULT;
			if(canale == null) {
				canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_DEFAULT;
			} else {
				canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_RIDEFINITO;
			}
			
			isSupportoProfiloAsincrono = acCore.isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(protocollo,serviceBinding);
			
			// fromato specifica default se e' null
			if(formatoSpecifica == null) {
				if(serviceBinding != null) {
					switch(serviceBinding) {
					case REST:
						formatoSpecifica = org.openspcoop2.protocol.manifest.constants.InterfaceType.toEnumConstant(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_INTERFACE_TYPE_REST);
						break;
					case SOAP:
					default:
						formatoSpecifica = org.openspcoop2.protocol.manifest.constants.InterfaceType.toEnumConstant(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_INTERFACE_TYPE_SOAP);
						break;
					}
				}
			}
			
			FiltroRicercaGruppi filtroRicerca = new FiltroRicercaGruppi();
			filtroRicerca.setServiceBinding(apcCore.fromMessageServiceBinding(serviceBinding));
			List<String> elencoGruppi = gruppiCore.getAllGruppi(filtroRicerca);
			
			String oldwsdl = "";
			byte[] wsdlbyte = null;
			String label = null;
			String tipologiaDocumentoScaricare = null;
			boolean facilityUnicoWSDL_interfacciaStandard = false;
			if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_DEFINITORIO)) {
				wsdlbyte = as.getByteWsdlDefinitorio();
				label = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_DEFINITORIO;
				tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_DEFINITORIO;
			}
			if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CONCETTUALE)) {
				wsdlbyte = as.getByteWsdlConcettuale();
				label = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_CONCETTUALE;
				tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_CONCETTUALE;
				
				switch (serviceBinding) {
				case REST:
					label = apcHelper.getLabelWSDLFromFormatoSpecifica(formatoSpecifica);
					break;
				case SOAP:
				default:
					// per ora non faccio nulla
					break;
				}
				
			}
			if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE)) {
				wsdlbyte = as.getByteWsdlLogicoErogatore();
				if(isModalitaAvanzata){
					if(isSupportoProfiloAsincrono)
						label = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_EROGATORE;
					else 
						label = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_LOGICO;
				} else {
					label = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL;
					facilityUnicoWSDL_interfacciaStandard = true;
				}
				tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_LOGICO_EROGATORE;
			}
			if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_FRUITORE)) {
				wsdlbyte = as.getByteWsdlLogicoFruitore();
				label = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_FRUITORE;
				tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_LOGICO_FRUITORE;
			}
			if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE)) {
				wsdlbyte = as.getByteSpecificaConversazioneConcettuale();
				label = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE;
				tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_SPECIFICA_CONVERSAZIONE_CONCETTUALE;
			}
			if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE)) {
				wsdlbyte = as.getByteSpecificaConversazioneErogatore();
				label = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE;
				tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_SPECIFICA_CONVERSAZIONE_LOGICO_EROGATORE;
			}
			if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE)) {
				wsdlbyte = as.getByteSpecificaConversazioneFruitore();
				label = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE;
				tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_SPECIFICA_CONVERSAZIONE_LOGICO_FRUITORE;
			}
			if (wsdlbyte != null) {
				oldwsdl = new String(wsdlbyte);
			}

			boolean used = true;
			
			Boolean isModalitaVistaApiCustom = ServletUtils.getBooleanAttributeFromSession(ApiCostanti.SESSION_ATTRIBUTE_VISTA_APC_API, session, false);
			if(!isModalitaVistaApiCustom) {
				label = MessageFormat.format("{0} di {1}", label, labelASTitle);
			}

			IDAccordo idAccordoOLD = idAccordoFactory.getIDAccordoFromValues(as.getNome(),BeanUtilities.getSoggettoReferenteID(as.getSoggettoReferente()),as.getVersione());
			Parameter pIdAccordo = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, this.id+"");
			Parameter pNomeAccordo = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, as.getNome());
			Parameter pTipoAccordo = AccordiServizioParteComuneUtilities.getParametroAccordoServizio(this.tipoAccordo);

			String tipoProtocollo = null;
			// controllo se l'accordo e' utilizzato da qualche asps
			List<AccordoServizioParteSpecifica> asps = apsCore.serviziByAccordoFilterList(idAccordoOLD);
			used = asps != null && asps.size() > 0;

			// lista dei protocolli supportati
			List<String> listaTipiProtocollo = apcCore.getProtocolli(session);

			// se il protocollo e' null (primo accesso ) lo ricavo dall'accordo di servizio
			if(tipoProtocollo == null){
				tipoProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(as.getSoggettoReferente().getTipo());
			}

			List<String> tipiSoggettiGestitiProtocollo = soggettiCore.getTipiSoggettiGestitiProtocollo(tipoProtocollo);
			String servletNameApcChange = AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_CHANGE;
			Parameter parameterApcChange = new Parameter(labelASTitle, servletNameApcChange, pIdAccordo, pNomeAccordo, pTipoAccordo);

			List<Parameter> listaParams = apcHelper.getTitoloApc(TipoOperazione.OTHER, as, this.tipoAccordo, labelASTitle, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_CHANGE, isGestioneAllegati); 
			listaParams.add(new Parameter(label,null));
			
			if(apcHelper.isEditModeInProgress() && ServletUtils.isEditModeInProgress(this.editMode)){

				// setto la barra del titolo				
				ServletUtils.setPageDataTitle(pd, listaParams);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				apcHelper.addAccordiWSDLChangeToDati(dati, this.id,this.tipoAccordo,this.tipo,label,
						oldwsdl,as.getStatoPackage(),this.validazioneDocumenti,tipologiaDocumentoScaricare,
						serviceBinding, this.aggiornaEsistenti, this.eliminaNonPresentiNuovaInterfaccia);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, 
						AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, AccordiServizioParteComuneCostanti.TIPO_OPERAZIONE_WSDL_CHANGE);
			}

			// Controlli sui campi immessi
			boolean isOk = apcHelper.accordiWSDLCheckData(pd,this.tipo, this.wsdl,as,this.validazioneDocumenti, protocollo);
			if (!isOk) {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, listaParams);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				apcHelper.addAccordiWSDLChangeToDati(dati, this.id,this.tipoAccordo,this.tipo,label,
						oldwsdl,as.getStatoPackage(),this.validazioneDocumenti,tipologiaDocumentoScaricare,
						serviceBinding, this.aggiornaEsistenti, this.eliminaNonPresentiNuovaInterfaccia);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, AccordiServizioParteComuneCostanti.TIPO_OPERAZIONE_WSDL_CHANGE);
			}

			// creo parametri binari finti per i wsdl 
			this.wsdldef = new BinaryParameter();
			this.wsdlconc = new BinaryParameter();
			this.wsdlserv = new BinaryParameter();
			this.wsdlservcorr = new BinaryParameter();
			this.wsblconc = new BinaryParameter();
			this.wsblserv = new BinaryParameter();
			this.wsblservcorr = new BinaryParameter();

			// il wsdl definitorio rimane fuori dal nuovo comportamento quindi il flusso della pagina continua come prima
			if (!this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_DEFINITORIO)) {
				// se sono state definiti dei port type ed e' la prima volta che ho passato i controlli 
				//Informo l'utente che potrebbe sovrascrivere i servizi definiti tramite l'aggiornamento del wsdl
				// Questa Modalita' e' controllata tramite la proprieta' isenabledAutoMappingWsdlIntoAccordo
				// e se non e' un reset
				if(enableAutoMapping && (this.wsdl != null) && !this.wsdl.trim().replaceAll("\n", "").equals("") ){
					if(actionConfirm == null){
						if(as.sizePortTypeList() > 0 || as.sizeResourceList()>0 ){
							
							// salvo il wsdl che ha inviato l'utente
							ServletUtils.setObjectIntoSession(session, this.wsdl, AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CHANGE_TMP);

							// setto la barra del titolo
							ServletUtils.setPageDataTitle(pd, listaParams);

							// preparo i campi
							Vector<DataElement> dati = new Vector<DataElement>();

							dati.addElement(ServletUtils.getDataElementForEditModeInProgress());

							// salvo lo stato dell'invio
							apcHelper.addAccordiWSDLChangeToDati(dati, this.id,this.tipoAccordo,this.tipo,label,
									oldwsdl,as.getStatoPackage(),this.validazioneDocumenti,tipologiaDocumentoScaricare,
									serviceBinding, this.aggiornaEsistenti, this.eliminaNonPresentiNuovaInterfaccia);

							pd.setDati(dati);

							String uriAccordo = idAccordoFactory.getUriFromIDAccordo(idAccordoOLD);
							String oggetto = null;
							String nuoviOggetti = null;
							String updateOggetti = null;
							String warnUpdateOggetti = null;
							if(as.sizePortTypeList() > 0) {
								oggetto = as.sizePortTypeList()+" servizi";
								nuoviOggetti = "nuovi servizi e/o azioni";
								updateOggetti = "dei servizi e delle azioni";
								warnUpdateOggetti = "I servizi e le azioni";
							}
							else {
								oggetto = as.sizeResourceList()+" risorse";
								nuoviOggetti = "nuove risorse";
								updateOggetti = "delle risorse";
								warnUpdateOggetti = "Le risorse";
							}
							String msg = "Attenzione, l'accordo ["+uriAccordo+"] contiene la definizione di "+oggetto+" e "+(as.sizeAllegatoList()+as.sizeSpecificaSemiformaleList())+" allegati. <BR/>"+
									"Il caricamento della specifica comporter&agrave;:<BR/>"+
									"- l'aggiornamento degli allegati esistenti e l'eventuale creazione di nuovi allegati;<BR/>"+
									"- l'eventuale creazione di "+nuoviOggetti+""+
									(this.aggiornaEsistenti? ";<BR/>- l'aggiornamento "+updateOggetti+" esistenti" : "")+
									(this.eliminaNonPresentiNuovaInterfaccia? ";<BR/>- l'eliminazione "+updateOggetti+" non presenti nella nuova interfaccia" : "")+
									".<BR/><BR/>"+
									(!this.aggiornaEsistenti ? warnUpdateOggetti+" esistenti non verranno aggiornati.<BR/>" : "")+ 
									(!this.eliminaNonPresentiNuovaInterfaccia ? warnUpdateOggetti+" non presenti nella nuova interfaccia verranno mantenute.<BR/>" : "")+ 
									"<BR/>Procedere con l'operazione richiesta?";
							
							String pre = Costanti.HTML_MODAL_SPAN_PREFIX;
							String post = Costanti.HTML_MODAL_SPAN_SUFFIX;
							pd.setMessage(pre + msg + post, Costanti.MESSAGE_TYPE_CONFIRM);

							// Bottoni
							String[][] bottoni = { 
									{ Costanti.LABEL_MONITOR_BUTTON_ANNULLA, 
										Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_PREFIX +
										Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_SUFFIX

									},
									{ Costanti.LABEL_MONITOR_BUTTON_CONFERMA,
										Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_PREFIX +
										Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_SUFFIX }};

							pd.setBottoni(bottoni );

							ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

							return ServletUtils.getStrutsForwardEditModeInProgress(mapping, 
									AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, AccordiServizioParteComuneCostanti.TIPO_OPERAZIONE_WSDL_CHANGE);
						}
					} 
				}
			}
			
			// Arrivo qui quando l'utente ha schiacciato Ok nella maschera di conferma, oppure l'accordo non aveva servizi, o sono in un wsdl definitorio.
			List<IDResource> risorseEliminate = new ArrayList<IDResource>();
			List<IDPortType> portTypeEliminati = new ArrayList<IDPortType>();
			List<IDPortTypeAzione> operationEliminate = new ArrayList<IDPortTypeAzione>();
			AccordiServizioParteComuneUtilities.updateInterfacciaAccordoServizioParteComune(this.tipo, this.wsdl, as,
					enableAutoMapping, this.validazioneDocumenti, enableAutoMapping_estraiXsdSchemiFromWsdlTypes, facilityUnicoWSDL_interfacciaStandard,
					tipoProtocollo, 
					apcCore,
					this.aggiornaEsistenti, this.eliminaNonPresentiNuovaInterfaccia,
					risorseEliminate, portTypeEliminati, operationEliminate);
			
			String newLine = org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE;
			StringBuilder inUsoMessage = new StringBuilder();
						
			if(portTypeEliminati!=null && !portTypeEliminati.isEmpty()) {
				boolean normalizeObjectIds = !apcHelper.isModalitaCompleta();
				for (IDPortType idPortType : portTypeEliminati) {
					HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
					boolean portTypeInUso = apcCore.isPortTypeInUso(idPortType,whereIsInUso,normalizeObjectIds);
					if (portTypeInUso) {
						inUsoMessage.append(DBOggettiInUsoUtils.toString(idPortType, whereIsInUso, true, newLine));
						inUsoMessage.append(newLine);
					} 
				}
				if(operationEliminate!=null && !operationEliminate.isEmpty()) {
					for (IDPortTypeAzione idOperazione : operationEliminate) {
						
						// controllo che non esista già nella lista dei port type precedentemente segnalati
						boolean find = false;
						for (IDPortType idPortType : portTypeEliminati) {
							if(idPortType.equals(idOperazione.getIdPortType())) {
								find = true;
								break;
							}
						}
						if(find) {
							continue;
						}
						
						HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
						boolean operazioneInUso = apcCore.isOperazioneInUso(idOperazione,whereIsInUso,normalizeObjectIds);
						if (operazioneInUso) {
							inUsoMessage.append(DBOggettiInUsoUtils.toString(idOperazione, whereIsInUso, true, newLine));
							inUsoMessage.append(newLine);
						} 
					}
				}
			}
			if(inUsoMessage.length()==0) {
				if(risorseEliminate!=null && !risorseEliminate.isEmpty()) {
					
					AccordoServizioParteComuneSintetico asSintetico = apcCore.getAccordoServizioSintetico(Long.valueOf(idAcc));
					
					boolean normalizeObjectIds = !apcHelper.isModalitaCompleta();
					for (IDResource idRisorsa : risorseEliminate) {
						HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
						boolean operazioneInUso = apcCore.isRisorsaInUso(idRisorsa,whereIsInUso,normalizeObjectIds);
						if (operazioneInUso) {

							// traduco nomeRisorsa in path
							String methodPath = null;
							if(asSintetico.getResource()!=null) {
								for (int j = 0; j < asSintetico.getResource().size(); j++) {
									ResourceSintetica risorsa = asSintetico.getResource().get(j);
									if (idRisorsa.getNome().equals(risorsa.getNome())) {
										methodPath = NamingUtils.getLabelResource(risorsa);
										break;
									}
								}
							}
							if(methodPath==null) {
								methodPath = idRisorsa.getNome();
							}
							
							inUsoMessage.append(DBOggettiInUsoUtils.toString(idRisorsa, methodPath, whereIsInUso, true, newLine));
							inUsoMessage.append(newLine);
						} 
					}
				}
			}
			if(inUsoMessage.length()>0) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, listaParams);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				apcHelper.addAccordiWSDLChangeToDati(dati, this.id,this.tipoAccordo,this.tipo,label,
						oldwsdl,as.getStatoPackage(),this.validazioneDocumenti,tipologiaDocumentoScaricare,
						serviceBinding, this.aggiornaEsistenti, this.eliminaNonPresentiNuovaInterfaccia);

				pd.setMessage("Non è possibile procedere con l'aggiornamento dell'interfaccia a causa di dipendenze verso oggetti che verrebbero eliminati.<BR/><BR/>"+inUsoMessage.toString());
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, AccordiServizioParteComuneCostanti.TIPO_OPERAZIONE_WSDL_CHANGE);
			}

			
			// effettuo le operazioni
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);
			
			//se sono in modalita' standard
			if(apcHelper.isModalitaStandard()) {
				apcHelper.prepareApiChange(TipoOperazione.OTHER, as); 
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				return ServletUtils.getStrutsForwardEditModeFinished(mapping, ApiCostanti.OBJECT_NAME_APC_API, ForwardParams.CHANGE());
			}
			

			// visualizzo il form di modifica accordo come in accordiChange
			// setto la barra del titolo
			listaParams = apcHelper.getTitoloApc(TipoOperazione.OTHER, as, this.tipoAccordo, labelASTitle, null, isGestioneAllegati); 
			ServletUtils.setPageDataTitle(pd, listaParams);

			String descr = as.getDescrizione();
			// controllo profilo collaborazione
			String profcoll = AccordiServizioParteComuneHelper.convertProfiloCollaborazioneDB2View(as.getProfiloCollaborazione());

			String filtrodup = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getFiltroDuplicati());
			String confric = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConfermaRicezione());
			String idcoll = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdCollaborazione());
			String idRifRichiesta = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdRiferimentoRichiesta());
			String consord = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConsegnaInOrdine());
			String scadenza = as.getScadenza();
			boolean showUtilizzoSenzaAzione = as.sizeAzioneList() > 0;// se
			// ci
			// sono
			// azioni
			// allora
			// visualizzo
			// il
			// checkbox
			boolean utilizzoSenzaAzione = as.getUtilizzoSenzaAzione();


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

			String referente=null;
			if(as.getSoggettoReferente()==null){
				referente= "-";
			}else{
				referente = "" + soggettiCore.getSoggettoRegistro(new IDSoggetto(as.getSoggettoReferente().getTipo(),as.getSoggettoReferente().getNome())).getId();
			}
			String versione = null;
			if(as.getVersione()!=null){
				versione = as.getVersione().intValue()+"";
			}
			boolean isServizioComposto = as.getServizioComposto()!=null ? true : false;
			String accordoCooperazioneId = "";
			if(isServizioComposto){
				accordoCooperazioneId = ""+as.getServizioComposto().getIdAccordoCooperazione();
			}else{
				accordoCooperazioneId="-";
			}
			String statoPackage = as.getStatoPackage();
			
			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tipoProtocollo);
			this.consoleDynamicConfiguration =  this.protocolFactory.createDynamicConfigurationConsole();
			this.registryReader = soggettiCore.getRegistryReader(this.protocolFactory); 
			this.configRegistryReader = soggettiCore.getConfigIntegrationReader(this.protocolFactory);
			this.consoleConfiguration = this.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE) ? 
					this.consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteComune(this.consoleOperationType, apcHelper, 
							this.registryReader, this.configRegistryReader, idAccordoOLD)
					: this.consoleDynamicConfiguration.getDynamicConfigAccordoServizioComposto(this.consoleOperationType, apcHelper, 
							this.registryReader, this.configRegistryReader, idAccordoOLD);
					
			List<ProtocolProperty> oldProtocolPropertyList = as.getProtocolPropertyList();
			this.protocolProperties = apcHelper.estraiProtocolPropertiesDaRequest(this.consoleConfiguration, this.consoleOperationType);
			ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(this.protocolProperties, oldProtocolPropertyList, this.consoleOperationType);
			
			Properties propertiesProprietario = new Properties();
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO, this.id);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO, ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_ACCORDO_SERVIZIO_PARTE_COMUNE);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO, uriAS);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE,
					URLEncoder.encode(parameterApcChange.getValue(), "UTF-8"));
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO, tipoProtocollo);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_ACCORDO, this.tipoAccordo);
			
			
			serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());
			messageType = apcCore.toMessageMessageType(as.getMessageType());
			formatoSpecifica = apcCore.formatoSpecifica2InterfaceType(as.getFormatoSpecifica());
			
			filtroRicerca = new FiltroRicercaGruppi();
			filtroRicerca.setServiceBinding(apcCore.fromMessageServiceBinding(serviceBinding));
			elencoGruppi = gruppiCore.getAllGruppi(filtroRicerca);
			
			if(as.getGruppi() != null) {
				List<String> nomiGruppi = as.getGruppi().getGruppoList().stream().flatMap(e-> Stream.of(e.getNome())).collect(Collectors.toList());
				gruppi = StringUtils.join(nomiGruppi, ",");
			} else 
				gruppi = "";
			
			canale = as.getCanale();
			
			if(canale == null) {
				canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_DEFAULT;
			} else {
				canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_RIDEFINITO;
			}

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			dati.addElement(ServletUtils.getDataElementForEditModeFinished());

			dati = apcHelper.addAccordiToDati(dati, as.getNome(), descr, profcoll, this.wsdldef, this.wsdlconc, this.wsdlserv, this.wsdlservcorr,this.wsblconc,this.wsblserv,this.wsblservcorr, 
					filtrodup, confric, idcoll, idRifRichiesta, consord, scadenza, this.id, TipoOperazione.CHANGE, 
					showUtilizzoSenzaAzione, utilizzoSenzaAzione,referente,versione,providersList,providersListLabel,
					(as.getPrivato()!=null && as.getPrivato()),isServizioComposto,accordiCooperazioneEsistenti,accordiCooperazioneEsistentiLabel,
					accordoCooperazioneId,statoPackage,statoPackage,this.tipoAccordo,this.validazioneDocumenti, 
					tipoProtocollo,listaTipiProtocollo,used,asWithAllegati,this.protocolFactory,serviceBinding,messageType,formatoSpecifica,gruppi, elencoGruppi,
					false, -1, false, -1,
					false, canaleStato, canale, canaleList, gestioneCanaliEnabled);

			// aggiunta campi custom
			dati = apcHelper.addProtocolPropertiesToDatiRegistry(dati, this.consoleConfiguration,this.consoleOperationType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
			pd.setDati(dati);

			// setto la baseurl per il redirect (alla servlet accordiChange)
			// se viene premuto invio
			gd = generalHelper.initGeneralData(request,AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_CHANGE);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, AccordiServizioParteComuneCostanti.TIPO_OPERAZIONE_WSDL_CHANGE);	

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, AccordiServizioParteComuneCostanti.TIPO_OPERAZIONE_WSDL_CHANGE);
		}
	}
}
