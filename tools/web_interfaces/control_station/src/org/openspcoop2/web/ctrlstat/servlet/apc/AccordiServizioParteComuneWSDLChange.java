/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
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
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.driver.BeanUtilities;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.ac.AccordiCooperazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesCostanti;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesUtilities;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.InterfaceType;

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
	
	// Protocol Properties
	private IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
	private ConsoleConfiguration consoleConfiguration =null;
	private ProtocolProperties protocolProperties = null;
	private IProtocolFactory<?> protocolFactory= null;
	private IRegistryReader registryReader = null; 
	private ConsoleOperationType consoleOperationType = null;
	private ConsoleInterfaceType consoleInterfaceType = null;

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

		boolean isModalitaAvanzata = ServletUtils.getUserFromSession(session).getInterfaceType().equals(InterfaceType.AVANZATA);
		boolean isSupportoProfiloAsincrono = false;
		
		this.consoleOperationType = ConsoleOperationType.CHANGE;
		this.consoleInterfaceType = ProtocolPropertiesUtilities.getTipoInterfaccia(session); 

		try {
			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);

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
			
			if(ServletUtils.isEditModeInProgress(this.editMode)){// && ServletUtils.isEditModeInProgress(request)){
				// primo accesso alla servlet
				this.validazioneDocumenti = true;
			}else{
				if(!this.decodeRequestValidazioneDocumenti){
					String tmpValidazioneDocumenti = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_VALIDAZIONE_DOCUMENTI);
					this.validazioneDocumenti = ServletUtils.isCheckBoxEnabled(tmpValidazioneDocumenti);
				}
			}

			int idAcc = 0;
			try {
				idAcc = Integer.parseInt(this.id);
			} catch (Exception e) {
			}

			// Preparo il menu
			apcHelper.makeMenu();

			// Prendo il nome e il wsdl attuale dell'accordo
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(apcCore);
			SoggettiCore soggettiCore = new SoggettiCore(apcCore);
			AccordiCooperazioneCore acCore = new AccordiCooperazioneCore(apcCore);

			// Flag per controllare il mapping automatico di porttype e operation
			boolean enableAutoMapping = apcCore.isEnableAutoMappingWsdlIntoAccordo();
			boolean enableAutoMapping_estraiXsdSchemiFromWsdlTypes = apcCore.isEnableAutoMappingWsdlIntoAccordo_estrazioneSchemiInWsdlTypes();

			AccordoServizioParteComune as = apcCore.getAccordoServizio(new Long(idAcc));
			boolean asWithAllegati = (as.sizeAllegatoList()>0 || as.sizeSpecificaSemiformaleList()>0 || as.getByteWsdlDefinitorio()!=null);
			String uriAS = idAccordoFactory.getUriFromAccordo(as);

			IdSoggetto idSoggettoReferente = as.getSoggettoReferente();
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(idSoggettoReferente.getTipo());
			isSupportoProfiloAsincrono = acCore.isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(protocollo );
			
			ServiceBinding serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());
			MessageType messageType = apcCore.toMessageMessageType(as.getMessageType());
			org.openspcoop2.protocol.manifest.constants.InterfaceType formatoSpecifica = apcCore.formatoSpecifica2InterfaceType(as.getFormatoSpecifica());

			String oldwsdl = "";
			byte[] wsdlbyte = null;
			String label = null;
			String tipologiaDocumentoScaricare = null;
			boolean facilityUnicoWSDL_interfacciaStandard = false;
			if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_DEFINITORIO)) {
				wsdlbyte = as.getByteWsdlDefinitorio();
				label = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_DEFINITORIO+" di " + uriAS;
				tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_DEFINITORIO;
			}
			if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CONCETTUALE)) {
				wsdlbyte = as.getByteWsdlConcettuale();
				label = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_CONCETTUALE+" di " + uriAS;
				tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_CONCETTUALE;
				
				switch (serviceBinding) {
				case REST:
					label = apcHelper.getLabelWSDLFromFormatoSpecifica(formatoSpecifica) +" di " + uriAS;
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
						label = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_EROGATORE+" di " + uriAS;
					else 
						label = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_LOGICO+" di " + uriAS;
				} else {
					label = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL+" di " + uriAS;
					facilityUnicoWSDL_interfacciaStandard = true;
				}
				tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_LOGICO_EROGATORE;
			}
			if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_FRUITORE)) {
				wsdlbyte = as.getByteWsdlLogicoFruitore();
				label = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_WSDL_FRUITORE+" di " + uriAS;
				tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_LOGICO_FRUITORE;
			}
			if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE)) {
				wsdlbyte = as.getByteSpecificaConversazioneConcettuale();
				label = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE+" di " + uriAS;
				tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_SPECIFICA_CONVERSAZIONE_CONCETTUALE;
			}
			if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE)) {
				wsdlbyte = as.getByteSpecificaConversazioneErogatore();
				label = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE+" di " + uriAS;
				tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_SPECIFICA_CONVERSAZIONE_LOGICO_EROGATORE;
			}
			if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE)) {
				wsdlbyte = as.getByteSpecificaConversazioneFruitore();
				label = AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE+" di " + uriAS;
				tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_SPECIFICA_CONVERSAZIONE_LOGICO_FRUITORE;
			}
			if (wsdlbyte != null) {
				oldwsdl = new String(wsdlbyte);
			}

			boolean used = true;


			IDAccordo idAccordoOLD = idAccordoFactory.getIDAccordoFromValues(as.getNome(),BeanUtilities.getSoggettoReferenteID(as.getSoggettoReferente()),as.getVersione());

			String tipoProtocollo = null;
			// controllo se l'accordo e' utilizzato da qualche asps
			List<AccordoServizioParteSpecifica> asps = apsCore.serviziByAccordoFilterList(idAccordoOLD);
			used = asps != null && asps.size() > 0;

			// lista dei protocolli supportati
			List<String> listaTipiProtocollo = apcCore.getProtocolli();

			// se il protocollo e' null (primo accesso ) lo ricavo dall'accordo di servizio
			if(tipoProtocollo == null){
				if(as!=null && as.getSoggettoReferente()!=null){
					tipoProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(as.getSoggettoReferente().getTipo());
				}
				else{
					tipoProtocollo = apcCore.getProtocolloDefault();
				}
			}

			List<String> tipiSoggettiGestitiProtocollo = soggettiCore.getTipiSoggettiGestitiProtocollo(tipoProtocollo);

			Parameter parameterApcChange = new Parameter(uriAS,
					AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_CHANGE+"?"+
							AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID+"="+this.id+"&"+
							AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME+"="+as.getNome()+"&"+
							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(this.tipoAccordo).getName()+"="+
							AccordiServizioParteComuneUtilities.getParametroAccordoServizio(this.tipoAccordo).getValue()
					);
			if(ServletUtils.isEditModeInProgress(request) && ServletUtils.isEditModeInProgress(this.editMode)){

				// setto la barra del titolo				
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(this.tipoAccordo),null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(this.tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(this.tipoAccordo).getValue()),
										parameterApcChange,
												new Parameter(label,null)
						);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				apcHelper.addAccordiWSDLChangeToDati(dati, this.id,this.tipoAccordo,this.tipo,label,
						oldwsdl,as.getStatoPackage(),this.validazioneDocumenti,tipologiaDocumentoScaricare);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, 
						AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, AccordiServizioParteComuneCostanti.TIPO_OPERAZIONE_WSDL_CHANGE);
			}

			// Controlli sui campi immessi
			boolean isOk = apcHelper.accordiWSDLCheckData(pd,this.tipo, this.wsdl,as,this.validazioneDocumenti);
			if (!isOk) {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(this.tipoAccordo),null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(this.tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(this.tipoAccordo).getValue()),
										parameterApcChange,
												new Parameter(label,null)
						);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				apcHelper.addAccordiWSDLChangeToDati(dati, this.id,this.tipoAccordo,this.tipo,label,
						oldwsdl,as.getStatoPackage(),this.validazioneDocumenti,tipologiaDocumentoScaricare);

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
			if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_DEFINITORIO)) {
				as.setByteWsdlDefinitorio(this.wsdl.getBytes());
			}
			else {
				// se sono state definiti dei port type ed e' la prima volta che ho passato i controlli 
				//Informo l'utente che potrebbe sovrascrivere i servizi definiti tramite l'aggiornamento del wsdl
				// Questa Modalita' e' controllata tramite la proprieta' isenabledAutoMappingWsdlIntoAccordo
				// e se non e' un reset
				if(enableAutoMapping && (this.wsdl != null) && !this.wsdl.trim().replaceAll("\n", "").equals("") ){
					if(actionConfirm == null){
						if(as.sizePortTypeList() > 0  ){
							
							// salvo il wsdl che ha inviato l'utente
							ServletUtils.setObjectIntoSession(session, this.wsdl, AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CHANGE_TMP);

							// setto la barra del titolo
							ServletUtils.setPageDataTitle(pd, 
									new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(this.tipoAccordo),null),
									new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
											AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
													AccordiServizioParteComuneUtilities.getParametroAccordoServizio(this.tipoAccordo).getName()+"="+
													AccordiServizioParteComuneUtilities.getParametroAccordoServizio(this.tipoAccordo).getValue()),
													parameterApcChange,
															new Parameter(label,null)
									);

							// preparo i campi
							Vector<DataElement> dati = new Vector<DataElement>();

							dati.addElement(ServletUtils.getDataElementForEditModeInProgress());

							// salvo lo stato dell'invio
							apcHelper.addAccordiWSDLChangeToDatiAsHidden(dati, this.id,this.tipoAccordo,this.tipo,label,
									null,as.getStatoPackage(),this.validazioneDocumenti);

							pd.setDati(dati);

							String uriAccordo = idAccordoFactory.getUriFromIDAccordo(idAccordoOLD);
							String msg = "Attenzione, l'accordo ["+uriAccordo+"] contiene la definizione di "+as.sizePortTypeList()+" servizi e "+(as.sizeAllegatoList()+as.sizeSpecificaSemiformaleList())+" allegati. <BR/>"+
									"Il caricamento del wsdl comporter&agrave; l'aggiornamento dei servizi/allegati esistenti e/o la creazione di nuovi servizi/allegati. Procedere?";
								
							pd.setMessage(msg, Costanti.MESSAGE_TYPE_INFO);

							// Bottoni
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

							return ServletUtils.getStrutsForwardEditModeConfirm(mapping, 
									AccordiServizioParteComuneCostanti.OBJECT_NAME_APC, AccordiServizioParteComuneCostanti.TIPO_OPERAZIONE_WSDL_CHANGE);
						}
					} else {
						
					}

					// Arrivo qui quando l'utente ha schiacciato Ok nella maschera di conferma, oppure l'accordo non aveva servizi 

					if((this.wsdl != null) && !this.wsdl.trim().replaceAll("\n", "").equals("") ){
						AccordoServizioParteComune asNuovo = new AccordoServizioParteComune();

						boolean fillXsd = false;
						String tipo = null;
						
						// decodifico quale wsdl/wsbl sto aggiornando
						if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CONCETTUALE)) {
							as.setByteWsdlConcettuale(this.wsdl.getBytes());

							asNuovo.setByteSpecificaConversazioneConcettuale(as.getByteSpecificaConversazioneConcettuale());
							asNuovo.setByteSpecificaConversazioneErogatore(as.getByteSpecificaConversazioneErogatore());
							asNuovo.setByteSpecificaConversazioneFruitore(as.getByteSpecificaConversazioneFruitore());
							asNuovo.setByteWsdlConcettuale(this.wsdl.getBytes());
							
							fillXsd = true;
							tipo=AccordiServizioParteComuneCostanti.TIPO_WSDL_CONCETTUALE;
						}
						if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE)) {
							as.setByteWsdlLogicoErogatore(this.wsdl.getBytes());

							asNuovo.setByteSpecificaConversazioneConcettuale(as.getByteSpecificaConversazioneConcettuale());
							asNuovo.setByteSpecificaConversazioneErogatore(as.getByteSpecificaConversazioneErogatore());
							asNuovo.setByteSpecificaConversazioneFruitore(as.getByteSpecificaConversazioneFruitore());
							asNuovo.setByteWsdlLogicoErogatore(this.wsdl.getBytes());
							
							fillXsd = true;
							if(facilityUnicoWSDL_interfacciaStandard){
								tipo=AccordiServizioParteComuneCostanti.TIPO_WSDL_CONCETTUALE;
							}
							else{
								tipo=AccordiServizioParteComuneCostanti.TIPO_WSDL_EROGATORE;
							}
							
						}
						if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_FRUITORE)) {
							as.setByteWsdlLogicoFruitore(this.wsdl.getBytes());

							asNuovo.setByteSpecificaConversazioneConcettuale(as.getByteSpecificaConversazioneConcettuale());
							asNuovo.setByteSpecificaConversazioneErogatore(as.getByteSpecificaConversazioneErogatore());
							asNuovo.setByteSpecificaConversazioneFruitore(as.getByteSpecificaConversazioneFruitore());
							asNuovo.setByteWsdlLogicoFruitore(this.wsdl.getBytes());
							
							fillXsd = true;
							tipo=AccordiServizioParteComuneCostanti.TIPO_WSDL_FRUITORE;
						}
						if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE)) {
							as.setByteSpecificaConversazioneConcettuale(this.wsdl.getBytes());

							asNuovo.setByteSpecificaConversazioneConcettuale(this.wsdl.getBytes());
							asNuovo.setByteWsdlConcettuale(as.getByteWsdlConcettuale());
							asNuovo.setByteWsdlLogicoErogatore(as.getByteWsdlLogicoErogatore());
							asNuovo.setByteWsdlLogicoFruitore(as.getByteWsdlLogicoFruitore());
						}
						if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE)) {
							as.setByteSpecificaConversazioneErogatore(this.wsdl.getBytes());

							asNuovo.setByteSpecificaConversazioneErogatore(this.wsdl.getBytes());
							asNuovo.setByteWsdlConcettuale(as.getByteWsdlConcettuale());
							asNuovo.setByteWsdlLogicoErogatore(as.getByteWsdlLogicoErogatore());
							asNuovo.setByteWsdlLogicoFruitore(as.getByteWsdlLogicoFruitore());
						}
						if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE)) {
							as.setByteSpecificaConversazioneFruitore(this.wsdl.getBytes());

							asNuovo.setByteSpecificaConversazioneFruitore(this.wsdl.getBytes());
							asNuovo.setByteWsdlConcettuale(as.getByteWsdlConcettuale());
							asNuovo.setByteWsdlLogicoErogatore(as.getByteWsdlLogicoErogatore());
							asNuovo.setByteWsdlLogicoFruitore(as.getByteWsdlLogicoFruitore());
						}

						// Genero la nuova definizione di port-type e operation
						apcCore.mappingAutomatico(tipoProtocollo, asNuovo);

						// se l'aggiornamento ha creato nuovi porttype o aggiornato i vecchi aggiorno la configurazione
						apcCore.popolaPorttypeOperationDaUnAltroASPC(as,asNuovo);
						
						// popolo gli allegati
						if(fillXsd && enableAutoMapping_estraiXsdSchemiFromWsdlTypes){
							apcCore.estraiSchemiFromWSDLTypesAsAllegati(as, this.wsdl.getBytes(), tipo, new Hashtable<String, byte[]> ());
							if(facilityUnicoWSDL_interfacciaStandard){
								// è stato utilizzato il concettuale. Lo riporto nel logico
								as.setByteWsdlLogicoErogatore(as.getByteWsdlConcettuale());
							}
						}
						
					}
				}else {
					// vecchio comportamento sovrascrivo i wsdl
					// Modifico i dati del wsdl dell'accordo nel db
					// anche in caso di reset del wsdl

					if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CONCETTUALE)) {
						as.setByteWsdlConcettuale(this.wsdl.getBytes());
					}
					if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE)) {
						as.setByteWsdlLogicoErogatore(this.wsdl.getBytes());
					}
					if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_FRUITORE)) {
						as.setByteWsdlLogicoFruitore(this.wsdl.getBytes());
					}
					if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_CONCETTUALE)) {
						as.setByteSpecificaConversazioneConcettuale(this.wsdl.getBytes());
					}
					if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_EROGATORE)) {
						as.setByteSpecificaConversazioneErogatore(this.wsdl.getBytes());
					}
					if (this.tipo.equals(AccordiServizioParteComuneCostanti.PARAMETRO_APC_SPECIFICA_CONVERSAZIONE_FRUITORE)) {
						as.setByteSpecificaConversazioneFruitore(this.wsdl.getBytes());
					}

				} 
			}

			// Se un utente ha impostato solo il logico erogatore (avviene automaticamente nel caso non venga visualizzato il campo concettuale)
			// imposto lo stesso wsdl anche per il concettuale. Tanto Rappresenta la stessa informazione, ma e' utile per lo stato dell'accordo
			if(as.getByteWsdlLogicoErogatore()!=null && as.getByteWsdlLogicoFruitore()==null && as.getByteWsdlConcettuale()==null){
				as.setByteWsdlConcettuale(as.getByteWsdlLogicoErogatore());
			}
			
			// effettuo le operazioni
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);

			// visualizzo il form di modifica accordo come in accordiChange
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, 
					new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(this.tipoAccordo),null),
					new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
							AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
									AccordiServizioParteComuneUtilities.getParametroAccordoServizio(this.tipoAccordo).getName()+"="+
									AccordiServizioParteComuneUtilities.getParametroAccordoServizio(this.tipoAccordo).getValue()),
									new Parameter(uriAS,null)
					);


			String descr = as.getDescrizione();
			// controllo profilo collaborazione
			String profcoll = AccordiServizioParteComuneHelper.convertProfiloCollaborazioneDB2View(as.getProfiloCollaborazione());

			String filtrodup = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getFiltroDuplicati());
			String confric = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConfermaRicezione());
			String idcoll = AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdCollaborazione());
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
			this.consoleConfiguration = this.tipoAccordo.equals(ProtocolPropertiesCostanti.PARAMETRO_VALORE_PP_TIPO_ACCORDO_PARTE_COMUNE) ? 
					this.consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteComune(this.consoleOperationType, this.consoleInterfaceType, this.registryReader, idAccordoOLD)
					: this.consoleDynamicConfiguration.getDynamicConfigAccordoServizioComposto(this.consoleOperationType, this.consoleInterfaceType, this.registryReader, idAccordoOLD);
					
			List<ProtocolProperty> oldProtocolPropertyList = as.getProtocolPropertyList();
			this.protocolProperties = apcHelper.estraiProtocolPropertiesDaRequest(this.consoleConfiguration, this.consoleOperationType);
			ProtocolPropertiesUtils.mergeProtocolProperties(this.protocolProperties, oldProtocolPropertyList, this.consoleOperationType);
			
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

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			dati.addElement(ServletUtils.getDataElementForEditModeFinished());

			dati = apcHelper.addAccordiToDati(dati, as.getNome(), descr, profcoll, this.wsdldef, this.wsdlconc, this.wsdlserv, this.wsdlservcorr,this.wsblconc,this.wsblserv,this.wsblservcorr, 
					filtrodup, confric, idcoll, consord, scadenza, this.id, TipoOperazione.CHANGE, 
					showUtilizzoSenzaAzione, utilizzoSenzaAzione,referente,versione,providersList,providersListLabel,
					(as.getPrivato()!=null && as.getPrivato()),isServizioComposto,accordiCooperazioneEsistenti,accordiCooperazioneEsistentiLabel,
					accordoCooperazioneId,statoPackage,statoPackage,this.tipoAccordo,this.validazioneDocumenti, 
					tipoProtocollo,listaTipiProtocollo,used,asWithAllegati,this.protocolFactory,serviceBinding,messageType,formatoSpecifica);

			// aggiunta campi custom
			dati = apcHelper.addProtocolPropertiesToDati(dati, this.consoleConfiguration,this.consoleOperationType, this.consoleInterfaceType, this.protocolProperties,oldProtocolPropertyList,propertiesProprietario);

			
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
