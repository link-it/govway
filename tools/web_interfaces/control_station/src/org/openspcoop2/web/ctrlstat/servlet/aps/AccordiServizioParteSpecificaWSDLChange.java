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


package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.db.IDAccordoDB;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
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
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;

/**
 * serviziWSDLChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaWSDLChange extends Action {

	private String   id, tipo, wsdl;
	private boolean validazioneDocumenti = true;
	private boolean decodeRequestValidazioneDocumenti = false;
	private String editMode = null;
	
	// Protocol Properties
	private IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
	private ConsoleConfiguration consoleConfiguration =null;
	private ProtocolProperties protocolProperties = null;
	private IProtocolFactory<?> protocolFactory= null;
	private IRegistryReader registryReader = null; 
	private IConfigIntegrationReader configRegistryReader = null; 
	private ConsoleOperationType consoleOperationType = null;
	
	private BinaryParameter wsdlimpler, wsdlimplfru;

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();

		try {
			
			this.consoleOperationType = ConsoleOperationType.CHANGE;
						
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			
			this.editMode = apsHelper.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
			this.id = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			this.tipo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO);
			this.wsdl = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL);

			if(apsHelper.isMultipart()){
				this.decodeRequestValidazioneDocumenti = true;
			}

			if(ServletUtils.isEditModeInProgress(this.editMode)){
				// primo accesso alla servlet
				this.validazioneDocumenti = true;
			}else{
				if(!this.decodeRequestValidazioneDocumenti){
					String tmpValidazioneDocumenti = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
					if(Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_ENABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
						this.validazioneDocumenti = true;
					}else{
						this.validazioneDocumenti = false;
					}
				}
			}

			int idServ = Integer.parseInt(this.id);

			String tipologia = ServletUtils.getObjectFromSession(session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
					gestioneFruitori = true;
				}
			}
			
			// Preparo il menu
			apsHelper.makeMenu();

			// Prendo il nome, il tipo e il wsdl attuale del servizio
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(apsCore);
			ConfigurazioneCore confCore = new ConfigurazioneCore(apsCore);
			
			// carico i canali
			CanaliConfigurazione gestioneCanali = confCore.getCanaliConfigurazione(false);
			List<CanaleConfigurazione> canaleList = gestioneCanali != null ? gestioneCanali.getCanaleList() : new ArrayList<>();
			boolean gestioneCanaliEnabled = gestioneCanali != null && org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(gestioneCanali.getStato());
			String canale = apsHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CANALI_CANALE);
			String canaleStato = apsHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO);

			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idServ);
			String nomeservizio = asps.getNome();
			String tiposervizio = asps.getTipo();
			Integer versioneservizio = asps.getVersione();
		
			String tmpTitle = apsHelper.getLabelIdServizio(asps);

			// Prendo Accordo di servizio parte comune
			AccordoServizioParteComune as = apcCore.getAccordoServizioFull(idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
			ServiceBinding serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());
			org.openspcoop2.protocol.manifest.constants.InterfaceType formatoSpecifica = apcCore.formatoSpecifica2InterfaceType(as.getFormatoSpecifica());
			
			List<String> versioniProtocollo = null;
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(as.getSoggettoReferente().getTipo());
			versioniProtocollo = apsCore.getVersioniProtocollo(protocollo);

			String label = null;
			if(apcCore.isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(protocollo,serviceBinding)){
				if(this.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_EROGATORE)){
					label = AccordiServizioParteSpecificaCostanti.LABEL_APS_WSDL_IMPLEMENTATIVO_EROGATORE_DI + tmpTitle;
				}
				if(this.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_FRUITORE)){
					label = AccordiServizioParteSpecificaCostanti.LABEL_APS_WSDL_IMPLEMENTATIVO_FRUITORE_DI + tmpTitle;
				}
			}else{
				label = AccordiServizioParteSpecificaCostanti.LABEL_APS_WSDL_IMPLEMENTATIVO_DI + tmpTitle;
			}
			
			String oldwsdl = "";
			byte[] wsdlbyte = null;
			String tipologiaDocumentoScaricare = null; 
			if (this.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_EROGATORE)) {
				wsdlbyte = asps.getByteWsdlImplementativoErogatore();
				tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_EROGATORE;
			}
			if (this.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_FRUITORE)) {
				wsdlbyte = asps.getByteWsdlImplementativoFruitore();
				tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_FRUITORE;
			}
			if (wsdlbyte != null) {
				oldwsdl = new String(wsdlbyte);
			}
			Soggetto soggettoErogatoreID = soggettiCore.getSoggettoRegistro(new IDSoggetto(asps.getTipoSoggettoErogatore(),asps.getNomeSoggettoErogatore()));


			Parameter parameterAPSChange = new Parameter( tmpTitle, 
					AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE ,
					new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+this.id),
					new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, nomeservizio),
					new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, tiposervizio),
					new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE, versioneservizio+""),
					new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, soggettoErogatoreID.getId()+"")
					
			//				,
			//						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, nomeservizio),
			//						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, tiposervizio)
					);
			
			String tipoSoggettoFruitore = null;
			String nomeSoggettoFruitore = null;
			@SuppressWarnings("unused")
			IDSoggetto idSoggettoFruitore = null;
			Parameter pTipoSoggettoFruitore = null;
			Parameter pNomeSoggettoFruitore = null;
			if(gestioneFruitori) {
				tipoSoggettoFruitore = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE);
				nomeSoggettoFruitore = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE);
				idSoggettoFruitore = new IDSoggetto(tipoSoggettoFruitore, nomeSoggettoFruitore);
				pTipoSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE, tipoSoggettoFruitore);
				pNomeSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE, nomeSoggettoFruitore);
			}
			
			List<Parameter> lstParm = new ArrayList<Parameter>();
			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, session);
			if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
				if(gestioneFruitori) {
					lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_FRUIZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
				} else {
					lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_EROGAZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
				}
				List<Parameter> listErogazioniChange = new ArrayList<>();
				Parameter pIdServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId()+ "");
				Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
				Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
				listErogazioniChange.add(pIdServizio);
				listErogazioniChange.add(pNomeServizio);
				listErogazioniChange.add(pTipoServizio);
				if(gestioneFruitori) {
					listErogazioniChange.add(pNomeSoggettoFruitore);
					listErogazioniChange.add(pTipoSoggettoFruitore);
				}
				lstParm.add(new Parameter(tmpTitle, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_CHANGE, 
						listErogazioniChange.toArray(new Parameter[1])));
				
				lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_MODIFICA_SERVIZIO_INFO_GENERALI, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, 
						listErogazioniChange.toArray(new Parameter[1])));
				
			} else {
				if(gestioneFruitori) {
					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				}
				else {
					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				}
				
				lstParm.add(parameterAPSChange);
			}
			
			lstParm.add(new Parameter(label , null));
			

			if (ServletUtils.isEditModeInProgress(this.editMode) && apsHelper.isEditModeInProgress()) {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, this.id, null, null, null,
						null, tipoSoggettoFruitore, nomeSoggettoFruitore, dati);

				dati = apsHelper.addWSDLToDati(TipoOperazione.OTHER, apsHelper.getSize(), asps, oldwsdl, this.tipo, this.validazioneDocumenti,
						dati, tipologiaDocumentoScaricare, label);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
						AccordiServizioParteSpecificaCostanti.TIPO_OPERAZIONE_WSDL_CHANGE);
			}

			// Controlli sui campi immessi
			boolean isOk = apsHelper.accordiParteSpecificaWSDLCheckData(pd, this.tipo, this.wsdl, asps, as, this.validazioneDocumenti);
			if (!isOk) {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, this.id, null, null, null,
						null, tipoSoggettoFruitore, nomeSoggettoFruitore, dati);

				dati = apsHelper.addWSDLToDati(TipoOperazione.OTHER,  apsHelper.getSize(), asps, oldwsdl, this.tipo, this.validazioneDocumenti, 
						dati, tipologiaDocumentoScaricare, label);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
						AccordiServizioParteSpecificaCostanti.TIPO_OPERAZIONE_WSDL_CHANGE);
			}

			// Modifico i dati del wsdl del servizio nel db
			if (this.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_EROGATORE)) {
				asps.setByteWsdlImplementativoErogatore(this.wsdl.getBytes());
			}
			if (this.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_FRUITORE)) {
				asps.setByteWsdlImplementativoFruitore(this.wsdl.getBytes());
			}

			// effettuo le operazioni
			String superUser = ServletUtils.getUserLoginFromSession(session);
			apsCore.performUpdateOperation(superUser, apsHelper.smista(), asps);

			// visualizzo la schermata di modifica del servizio
			// setto la barra del titolo
			
			Parameter p = null;
			if(gestioneFruitori) {
				p = new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FRUITORI, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST);
			}
			else {
				p = new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST);
			}
			
			ServletUtils.setPageDataTitle(pd, 
					p,
					new Parameter(tmpTitle, null)					);

			AccordoServizioParteSpecifica aspsT = apsCore.getAccordoServizioParteSpecifica(Long.parseLong(this.id));

			String[] ptList = null;
			String[] soggettiList = null;
			String[] soggettiListLabel = null;
			String[] accordiList = null;
			String[] accordiListLabel = null;
			// soggetti
			List<Soggetto> list = soggettiCore.soggettiRegistroList("", new Search(true));
			List<String> tipiServizi = apsCore.getTipiServiziGestitiProtocollo(protocollo,serviceBinding);
			if (list.size() > 0) {
				soggettiList = new String[list.size()];
				soggettiListLabel = new String[list.size()];
				int i = 0;
				for (Soggetto soggetto : list) {
					soggettiList[i] = soggetto.getId().toString();
					soggettiListLabel[i] = soggetto.getTipo() + "/" + soggetto.getNome();
					i++;
				}
			}

			// accordi
			PermessiUtente pu = ServletUtils.getUserFromSession(session).getPermessi();

			boolean [] permessi = new boolean[2];
			permessi[0] = pu.isServizi();
			permessi[1] = pu.isAccordiCooperazione();
			List<IDAccordoDB> listIdAccordi =  
					AccordiServizioParteComuneUtilities.idAccordiListFromPermessiUtente(apcCore, superUser, new Search(true), permessi, false, false);

			//				List<AccordoServizioParteComune> listAccordi = apcCore.accordiList("", new Search(true));
			if (listIdAccordi.size() > 0) {
				accordiList = new String[listIdAccordi.size()];
				accordiListLabel = new String[listIdAccordi.size()];
				int i = 0;
				for (IDAccordoDB accordoServizio : listIdAccordi) {
					accordiList[i] = accordoServizio.getId().toString();
					accordiListLabel[i] = accordoServizio.getNome();
					i++;
				}
			}

			String nomeSoggettoErogatore = aspsT.getNomeSoggettoErogatore();
			String tipoSoggettoErogatore = aspsT.getTipoSoggettoErogatore();
			String servcorr = "";
			if (TipologiaServizio.CORRELATO.equals(aspsT.getTipologiaServizio()))
				servcorr = Costanti.CHECK_BOX_ENABLED;
			else
				servcorr = Costanti.CHECK_BOX_DISABLED;
			String accordo = aspsT.getIdAccordo().toString();
			String profilo = aspsT.getVersioneProtocollo();
			String portType = aspsT.getPortType();
			String descrizione = aspsT.getDescrizione();
			String statoPackage = aspsT.getStatoPackage();
			
			Connettore connettore = aspsT.getConfigurazioneServizio().getConnettore();

			// if(endpointtype==null || endpointtype.equals(""))
			// endpointtype = connettore.getTipo();
			//			
			// Map<String,String> properties =
			// connettore.getProperties();
			String connettoreDebug = null;
			String endpointtype = null;
			String tipoconn = null;
			String autenticazioneHttp = null;
			String url = null;
			String nome = null;
			String initcont = null;
			String urlpgk = null;
			String provurl = null;
			String connfact = null;
			String sendas = null;
			String provider = "";
			String user = "";
			String password = "";
			String httpsurl = null;
			String httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TYPE ;
			boolean httpshostverify = true;
			boolean httpsTrustVerifyCert = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS;
			String httpspath = "";
			String httpstipo = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE;
			String httpspwd = "";
			String httpsalgoritmo = "";
			boolean httpsstato = false;
			String httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
			String httpspwdprivatekeytrust = "";
			String httpspathkey = "";
			String httpstipokey = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE;
			String httpspwdkey = "";
			String httpspwdprivatekey = "";
			String httpsalgoritmokey = "";
			String httpsKeyAlias = null;
			String httpsTrustStoreCRLs = null;
			boolean autenticazioneToken = false;
			String token_policy = null;
			String proxy_enabled = null, proxy_hostname  = null,proxy_port  = null,proxy_username  = null,proxy_password = null;
			String tempiRisposta_enabled = null, tempiRisposta_connectionTimeout = null, tempiRisposta_readTimeout = null, tempiRisposta_tempoMedioRisposta = null;
			String transfer_mode = null, transfer_mode_chunk_size = null, redirect_mode = null, redirect_max_hop = null, opzioniAvanzate = null;
			// file
			String requestOutputFileName = null;
			String requestOutputFileNameHeaders = null;
			String requestOutputParentDirCreateIfNotExists = null;
			String requestOutputOverwriteIfExists = null;
			String responseInputMode = null;
			String responseInputFileName = null;
			String responseInputFileNameHeaders = null;
			String responseInputDeleteAfterRead = null;
			String responseInputWaitTime = null;
			
			String servizioApplicativoServer = null;
			boolean servizioApplicativoServerEnabled = false;
			if ((endpointtype == null) || (url == null) || (nome == null)) {
				Map<String, String> props = connettore.getProperties();

				if (endpointtype == null) {
					if ((connettore.getCustom()!=null && connettore.getCustom()) && 
							!connettore.getTipo().equals(CostantiDB.CONNETTORE_TIPO_HTTPS) && 
							!connettore.getTipo().equals(CostantiDB.CONNETTORE_TIPO_FILE)) {
						endpointtype = ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM;
						tipoconn = connettore.getTipo();
					} else
						endpointtype = connettore.getTipo();
				}
				autenticazioneHttp = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
				String userTmp = props.get(CostantiDB.CONNETTORE_USER);
				if(userTmp!=null && !"".equals(userTmp)){
					user = userTmp;
				}
				String passwordTmp = props.get(CostantiDB.CONNETTORE_PWD);
				if(passwordTmp!=null && !"".equals(passwordTmp)){
					password = passwordTmp;
				}
				autenticazioneHttp = apsHelper.getAutenticazioneHttp(autenticazioneHttp, endpointtype, user);
				
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
								
				// proxy
				if(proxy_enabled==null && props!=null){
					String v = props.get(CostantiDB.CONNETTORE_PROXY_TYPE);
					if(v!=null && !"".equals(v)){
						proxy_enabled = Costanti.CHECK_BOX_ENABLED_TRUE;
						
						// raccolgo anche altre proprietà
						v = props.get(CostantiDB.CONNETTORE_PROXY_HOSTNAME);
						if(v!=null && !"".equals(v)){
							proxy_hostname = v.trim();
						}
						v = props.get(CostantiDB.CONNETTORE_PROXY_PORT);
						if(v!=null && !"".equals(v)){
							proxy_port = v.trim();
						}
						v = props.get(CostantiDB.CONNETTORE_PROXY_USERNAME);
						if(v!=null && !"".equals(v)){
							proxy_username = v.trim();
						}
						v = props.get(CostantiDB.CONNETTORE_PROXY_PASSWORD);
						if(v!=null && !"".equals(v)){
							proxy_password = v.trim();
						}
					}
				}
				
				// tempiRisposta
				if(tempiRisposta_enabled == null ||
						tempiRisposta_connectionTimeout==null || "".equals(tempiRisposta_connectionTimeout) 
						|| 
						tempiRisposta_readTimeout==null || "".equals(tempiRisposta_readTimeout) 
						|| 
						tempiRisposta_tempoMedioRisposta==null || "".equals(tempiRisposta_tempoMedioRisposta) ){
					
					ConfigurazioneCore configCore = new ConfigurazioneCore(soggettiCore);
					ConfigurazioneGenerale configGenerale = configCore.getConfigurazioneControlloTraffico();
					
					if( props!=null ) {
						if(tempiRisposta_connectionTimeout==null || "".equals(tempiRisposta_connectionTimeout) ) {
							String v = props.get(CostantiDB.CONNETTORE_CONNECTION_TIMEOUT);
							if(v!=null && !"".equals(v)){
								tempiRisposta_connectionTimeout = v.trim();
								tempiRisposta_enabled =  Costanti.CHECK_BOX_ENABLED_TRUE;
							}
							else {
								tempiRisposta_connectionTimeout = configGenerale.getTempiRispostaFruizione().getConnectionTimeout().intValue()+"";
							}
						}
							
						if(tempiRisposta_readTimeout==null || "".equals(tempiRisposta_readTimeout) ) {
							String v = props.get(CostantiDB.CONNETTORE_READ_CONNECTION_TIMEOUT);
							if(v!=null && !"".equals(v)){
								tempiRisposta_readTimeout = v.trim();
								tempiRisposta_enabled =  Costanti.CHECK_BOX_ENABLED_TRUE;
							}
							else {
								tempiRisposta_readTimeout = configGenerale.getTempiRispostaFruizione().getReadTimeout().intValue()+"";
							}
						}
						
						if(tempiRisposta_tempoMedioRisposta==null || "".equals(tempiRisposta_tempoMedioRisposta) ) {
							String v = props.get(CostantiDB.CONNETTORE_TEMPO_MEDIO_RISPOSTA);
							if(v!=null && !"".equals(v)){
								tempiRisposta_tempoMedioRisposta = v.trim();
								tempiRisposta_enabled =  Costanti.CHECK_BOX_ENABLED_TRUE;
							}
							else {
								tempiRisposta_tempoMedioRisposta = configGenerale.getTempiRispostaFruizione().getTempoMedioRisposta().intValue()+"";
							}
						}
					}
					else {
						if(tempiRisposta_connectionTimeout==null || "".equals(tempiRisposta_connectionTimeout) ) {
							tempiRisposta_connectionTimeout = configGenerale.getTempiRispostaFruizione().getConnectionTimeout().intValue()+"";
						}
						if(tempiRisposta_readTimeout==null || "".equals(tempiRisposta_readTimeout) ) {
							tempiRisposta_readTimeout = configGenerale.getTempiRispostaFruizione().getReadTimeout().intValue()+"";
						}
						if(tempiRisposta_tempoMedioRisposta==null || "".equals(tempiRisposta_tempoMedioRisposta) ) {
							tempiRisposta_tempoMedioRisposta = configGenerale.getTempiRispostaFruizione().getTempoMedioRisposta().intValue()+"";
						}
					}
				}
				
				// opzioni avanzate
				if(transfer_mode==null && props!=null){
					String v = props.get(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE);
					if(v!=null && !"".equals(v)){
						
						transfer_mode = v.trim();
						
						if(TransferLengthModes.TRANSFER_ENCODING_CHUNKED.getNome().equals(transfer_mode)){
							// raccolgo anche altra proprietà correlata
							v = props.get(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE_CHUNK_SIZE);
							if(v!=null && !"".equals(v)){
								transfer_mode_chunk_size = v.trim();
							}
						}
						
					}
				}
				
				if(redirect_mode==null && props!=null){
					String v = props.get(CostantiDB.CONNETTORE_HTTP_REDIRECT_FOLLOW);
					if(v!=null && !"".equals(v)){
						
						if("true".equalsIgnoreCase(v.trim()) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v.trim())){
							redirect_mode = CostantiConfigurazione.ABILITATO.getValue();
						}
						else{
							redirect_mode = CostantiConfigurazione.DISABILITATO.getValue();
						}					
						
						if(CostantiConfigurazione.ABILITATO.getValue().equals(redirect_mode)){
							// raccolgo anche altra proprietà correlata
							v = props.get(CostantiDB.CONNETTORE_HTTP_REDIRECT_MAX_HOP);
							if(v!=null && !"".equals(v)){
								redirect_max_hop = v.trim();
							}
						}
						
					}
				}
				
				if(token_policy==null && props!=null){
					String v = props.get(CostantiDB.CONNETTORE_TOKEN_POLICY);
					if(v!=null && !"".equals(v)){
						token_policy = v;
						autenticazioneToken = true;
					}
				}
				
				opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(apsHelper, transfer_mode, redirect_mode);
				
				// http
				if (url == null) {
					url = props.get(CostantiDB.CONNETTORE_HTTP_LOCATION);
				}
				
				// jms
				if (nome == null) {
					nome = props.get(CostantiDB.CONNETTORE_JMS_NOME);
					this.tipo = props.get(CostantiDB.CONNETTORE_JMS_TIPO);
					initcont = props.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL);
					urlpgk = props.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG);
					provurl = props.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL);
					connfact = props.get(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY);
					sendas = props.get(CostantiDB.CONNETTORE_JMS_SEND_AS);
					
				}
				if (httpsurl == null) {
					httpsurl = props.get(CostantiDB.CONNETTORE_HTTPS_LOCATION);
					httpstipologia = props.get(CostantiDB.CONNETTORE_HTTPS_SSL_TYPE);
					httpshostverify = Boolean.valueOf(props.get(CostantiDB.CONNETTORE_HTTPS_HOSTNAME_VERIFIER));
					String httpsTrustVerifyCertS = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_ALL_CERTS);
					if(httpsTrustVerifyCertS!=null){
						httpsTrustVerifyCert = !Boolean.valueOf(httpsTrustVerifyCertS);
					}
					else {
						httpsTrustVerifyCert = true; // backward compatibility
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
					httpsKeyAlias = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_ALIAS);
					httpsTrustStoreCRLs = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_CRLs);
					if (httpspathkey == null) {
						httpsstato = false;
						httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
					} else {
						httpsstato = true;
						if (httpspathkey.equals(httpspath) &&
								httpstipokey.equals(httpstipo) &&
								httpspwdkey.equals(httpspwd) &&
								httpsalgoritmokey.equals(httpsalgoritmo))
							httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
						else
							httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI;
					}
				}
				
				// file
				if(responseInputMode==null && props!=null){
					
					requestOutputFileName = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE);	
					requestOutputFileNameHeaders = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS);	
					String v = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
					if(v!=null && !"".equals(v)){
						if("true".equalsIgnoreCase(v) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v) ){
							requestOutputParentDirCreateIfNotExists = Costanti.CHECK_BOX_ENABLED_TRUE;
						}
					}					
					v = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE);
					if(v!=null && !"".equals(v)){
						if("true".equalsIgnoreCase(v) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v) ){
							requestOutputOverwriteIfExists = Costanti.CHECK_BOX_ENABLED_TRUE;
						}
					}	
					
					v = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_MODE);
					if(v!=null && !"".equals(v)){
						if("true".equalsIgnoreCase(v) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v) ){
							responseInputMode = CostantiConfigurazione.ABILITATO.getValue();
						}
					}
					if(CostantiConfigurazione.ABILITATO.getValue().equals(responseInputMode)){						
						responseInputFileName = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE);
						responseInputFileNameHeaders = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE_HEADERS);
						v = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE_DELETE_AFTER_READ);
						if(v!=null && !"".equals(v)){
							if("true".equalsIgnoreCase(v) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v) ){
								responseInputDeleteAfterRead = Costanti.CHECK_BOX_ENABLED_TRUE;
							}
						}						
						responseInputWaitTime = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);						
					}
					
				}
			}

			Boolean isConnettoreCustomUltimaImmagineSalvata = connettore.getCustom();
			
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(connettore, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_CHANGE, apsHelper, 
							true, null);
			
			// Lista port-type associati all'accordo di servizio
			// se l'accordo e' selezionato allora prendo quello selezionato
			// altrimenti il primo
			// della lista
			if (accordo != null && !"".equals(accordo)) {
				as = apcCore.getAccordoServizioFull(Long.parseLong(accordo));
			} else {
				as = apcCore.getAccordoServizioFull(idAccordoFactory.getIDAccordoFromUri(aspsT.getAccordoServizioParteComune()));
			}
			
			String canaleAPI = as != null ? as.getCanale() : null;  

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
			
			this.wsdlimpler = new BinaryParameter();
			this.wsdlimplfru = new BinaryParameter();
			
			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			this.consoleDynamicConfiguration =  this.protocolFactory.createDynamicConfigurationConsole();
			this.registryReader = soggettiCore.getRegistryReader(this.protocolFactory); 
			this.configRegistryReader = soggettiCore.getConfigIntegrationReader(this.protocolFactory);
			IDServizio idAps = apsHelper.getIDServizioFromValues(tiposervizio, nomeservizio, tipoSoggettoErogatore,nomeSoggettoErogatore, versioneservizio+"");
			idAps.setPortType(asps.getPortType());
			this.consoleConfiguration = this.consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteSpecifica(this.consoleOperationType, apsHelper, 
					this.registryReader, this.configRegistryReader, idAps );
					
			List<ProtocolProperty> oldProtocolPropertyList = as.getProtocolPropertyList();
			this.protocolProperties = apsHelper.estraiProtocolPropertiesDaRequest(this.consoleConfiguration, this.consoleOperationType);
			ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(this.protocolProperties, oldProtocolPropertyList, this.consoleOperationType);
			
			Properties propertiesProprietario = new Properties();
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO, this.id);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO, ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_ACCORDO_SERVIZIO_PARTE_SPECIFICA);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO, tmpTitle);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE,
					URLEncoder.encode(parameterAPSChange.getValue(), "UTF-8"));
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO, protocollo);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_ACCORDO, "");
			
			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, this.id, null, null, dati);

			dati = apsHelper.addServiziToDati(dati, nomeservizio, tiposervizio, null, null, 
					provider, tipoSoggettoErogatore , nomeSoggettoErogatore, soggettiList,
					soggettiListLabel, accordo, serviceBinding, formatoSpecifica, 
					accordiList, accordiListLabel, servcorr, this.wsdlimpler, this.wsdlimplfru,
					TipoOperazione.CHANGE, this.id, tipiServizi, profilo, portType, ptList,
					(aspsT.getPrivato()!=null && aspsT.getPrivato()),idAccordoFactory.getUriFromAccordo(as),
					descrizione,soggettoErogatoreID.getId(), statoPackage,statoPackage,
					versioneservizio.intValue()+"", versioniProtocollo,this.validazioneDocumenti,
					null,null,true,null,
					null,
					null,null,null,null,null,null,false,
					null,null,null,null,null,null,null,
					protocollo, null,
					null, null, null, tipoSoggettoFruitore, nomeSoggettoFruitore,
					null, null, null, null, null,
					null, null, null, null,
					null,null,null,null,null,null,null,null,null,
					null,null, null,
					null,null,null,null,null,
					null,null,
					null,null,null,null,false, canaleStato, canaleAPI, canale, canaleList, gestioneCanaliEnabled);

			dati = apsHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, null, 
					url, nome,
					this.tipo, user, password, initcont, urlpgk, provurl,
					connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,TipoOperazione.CHANGE, 
					httpsurl, httpstipologia, httpshostverify, 
					httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
					httpsalgoritmo, httpsstato, httpskeystore,
					httpspwdprivatekeytrust, httpspathkey,
					httpstipokey, httpspwdkey, 
					httpspwdprivatekey, httpsalgoritmokey,
					httpsKeyAlias, httpsTrustStoreCRLs,
					tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, this.id,
					nomeservizio, tiposervizio, versioneservizio.intValue()+"", null, null, null, null, true,
					isConnettoreCustomUltimaImmagineSalvata, 
					proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
					tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
					opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
					requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
					responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
					autenticazioneToken, token_policy,
					listExtendedConnettore, false,
					protocollo,false,false
					, false, servizioApplicativoServerEnabled, servizioApplicativoServer, null
					);

			pd.setDati(dati);


			// imposto la baseurl per il redirect
			gd = generalHelper.initGeneralData(request,AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
					AccordiServizioParteSpecificaCostanti.TIPO_OPERAZIONE_WSDL_CHANGE);
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
					AccordiServizioParteSpecificaCostanti.TIPO_OPERAZIONE_WSDL_CHANGE);
		}  
	}
}
