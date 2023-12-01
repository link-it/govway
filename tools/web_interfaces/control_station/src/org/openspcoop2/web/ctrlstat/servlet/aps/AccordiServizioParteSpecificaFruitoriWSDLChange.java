/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
import java.util.List;
import java.util.Properties;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * serviziFruitoriWSDLChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaFruitoriWSDLChange extends Action {


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

			AccordiServizioParteSpecificaFruitoriWSDLChangeStrutsBean strutsBean = new AccordiServizioParteSpecificaFruitoriWSDLChangeStrutsBean();
			
			strutsBean.consoleOperationType = ConsoleOperationType.CHANGE;
			
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			
			// Preparo il menu
			apsHelper.makeMenu();
			
			strutsBean.editMode = apsHelper.getParametroEditMode(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
			
			strutsBean.id = apsHelper.getParametroLong(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			strutsBean.tipo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO);
			strutsBean.wsdl = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL);

			strutsBean.idSoggettoErogatoreDelServizio = apsHelper.getParametroLong(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);
			if ((strutsBean.idSoggettoErogatoreDelServizio == null) || strutsBean.idSoggettoErogatoreDelServizio.equals("")) {
				PageData oldPD = ServletUtils.getPageDataFromSession(request, session);

				strutsBean.idSoggettoErogatoreDelServizio = oldPD.getHidden(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);
			}

			strutsBean.idSoggettoFruitore = apsHelper.getParametroLong(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE);
			
			if(apsHelper.isMultipart()){
				strutsBean.decodeRequestValidazioneDocumenti = true;
			}

			if(ServletUtils.isEditModeInProgress(strutsBean.editMode)){
				// primo accesso alla servlet
				strutsBean.validazioneDocumenti = true;
			}else{
				if(!strutsBean.decodeRequestValidazioneDocumenti){
					String tmpValidazioneDocumenti = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
					if(Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_ENABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
						strutsBean.validazioneDocumenti = true;
					}else{
						strutsBean.validazioneDocumenti = false;
					}
				}
			}

			long idFruitoreLong = Integer.parseInt(strutsBean.id);

			// Prendo il wsdl attuale del fruitore
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(apsCore);

			Fruitore myFru = apsCore.getServizioFruitore(idFruitoreLong);
			long idServizioLong = myFru.getIdServizio();
			long idSoggettoFruitoreLong = myFru.getIdSoggetto();
			String oldwsdl = "";
			byte[] wsdlbyte = null;
			String tipologiaDocumentoScaricare = null; 
			if(strutsBean.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_EROGATORE)){
				wsdlbyte = myFru.getByteWsdlImplementativoErogatore();
				tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_EROGATORE;
			}
			if(strutsBean.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_FRUITORE)){
				wsdlbyte = myFru.getByteWsdlImplementativoFruitore();
				tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_FRUITORE;
			}
			if (wsdlbyte != null)
				oldwsdl = new String(wsdlbyte);

			// Prendo il nome e il tipo del servizio
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idServizioLong);
			String titleServizio = apsHelper.getLabelIdServizio(asps);
			
			// Prendo Accordo di servizio parte comune
			AccordoServizioParteComune as = apcCore.getAccordoServizioFull(idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
			ServiceBinding serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());

			// Mi calcolo IDServizio, che servirà per recuperare il fruitore una
			// volta che sarà stato rimosso/aggiunto e se ne sarà perso l'id
			IDServizio ids = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);

			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(as.getSoggettoReferente().getTipo());
			
			Soggetto mySogg2 = soggettiCore.getSoggettoRegistro(idSoggettoFruitoreLong);
			String tmpTitle = apsHelper.getLabelNomeSoggetto(protocollo, mySogg2.getTipo() , mySogg2.getNome());
			
			String label = null;
			if(apcCore.isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(protocollo,serviceBinding)){
				if(strutsBean.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_EROGATORE)){
					label = AccordiServizioParteSpecificaCostanti.LABEL_APS_WSDL_IMPLEMENTATIVO_EROGATORE_DI + tmpTitle;
				}
				if(strutsBean.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_FRUITORE)){
					label = AccordiServizioParteSpecificaCostanti.LABEL_APS_WSDL_IMPLEMENTATIVO_FRUITORE_DI + tmpTitle;
				}
			}else{
				label = AccordiServizioParteSpecificaCostanti.LABEL_APS_WSDL_IMPLEMENTATIVO_DI + tmpTitle;
			}
			
			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			Parameter parameterFruitoreChange = new Parameter(tmpTitle, 
					AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE ,
					new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ idServizioLong),
					new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, ""+ strutsBean.id),
					new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+ strutsBean.idSoggettoErogatoreDelServizio),
					new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE, ""+ strutsBean.idSoggettoFruitore)
					);
			if (ServletUtils.isEditModeInProgress(strutsBean.editMode)) {
				List<Parameter> lstParm = new ArrayList<>();

				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + titleServizio, 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST ,
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ idServizioLong),
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+ strutsBean.idSoggettoErogatoreDelServizio)
						));
				lstParm.add(parameterFruitoreChange);
				lstParm.add(new Parameter(label , null));

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, strutsBean.id, null, null, dati);

				dati = apsHelper.addFruitoreWSDLToDati(TipoOperazione.OTHER, strutsBean.tipo, strutsBean.idSoggettoErogatoreDelServizio , strutsBean.idSoggettoFruitore,
						oldwsdl, strutsBean.validazioneDocumenti, myFru, dati,
						idServizioLong+"", tipologiaDocumentoScaricare, false, label);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,
						AccordiServizioParteSpecificaCostanti.TIPO_OPERAZIONE_WSDL_CHANGE);
			}

			// Controlli sui campi immessi
			boolean isOk = apsHelper.accordiParteSpecificaFruitoreWSDLCheckData(pd, strutsBean.tipo, strutsBean.wsdl, myFru, asps, as, strutsBean.validazioneDocumenti);
			if (!isOk) {
				List<Parameter> lstParm = new ArrayList<>();

				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + titleServizio, 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST ,
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ idServizioLong),
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+ strutsBean.idSoggettoErogatoreDelServizio)
						));
				lstParm.add(parameterFruitoreChange);
				lstParm.add(new Parameter(label , null));

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, strutsBean.id, null, null, dati);

				dati = apsHelper.addFruitoreWSDLToDati(TipoOperazione.OTHER, strutsBean.tipo, strutsBean.idSoggettoErogatoreDelServizio , strutsBean.idSoggettoFruitore,
						oldwsdl, strutsBean.validazioneDocumenti, myFru, dati,
						idServizioLong+"", tipologiaDocumentoScaricare, false, label);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI, 
						AccordiServizioParteSpecificaCostanti.TIPO_OPERAZIONE_WSDL_CHANGE);
			}

			// Modifico i dati del wsdl del fruitore nel db

			if (strutsBean.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_EROGATORE))
				myFru.setByteWsdlImplementativoErogatore(strutsBean.wsdl != null && !strutsBean.wsdl.equals("") ? strutsBean.wsdl.getBytes() : null);
			if (strutsBean.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_FRUITORE))
				myFru.setByteWsdlImplementativoFruitore(strutsBean.wsdl != null && !strutsBean.wsdl.equals("") ? strutsBean.wsdl.getBytes() : null);

			// Elimino il vecchio fruitore ed aggiungo il nuovo
			for (int i = 0; i < asps.sizeFruitoreList(); i++) {
				Fruitore tmpFru = asps.getFruitore(i);
				if (tmpFru.getId().longValue() == myFru.getId().longValue()) {
					asps.removeFruitore(i);
					break;
				}
			}

			asps.addFruitore(myFru);
			apsCore.setDataAggiornamentoFruitore(myFru);
			
			String superUser =  ServletUtils.getUserLoginFromSession(session);
			apsCore.performUpdateOperation(superUser, apsHelper.smista(), asps);


			// Prendo il nuovo id del fruitore
			strutsBean.id = "" + apsCore.getServizioFruitore(ids, idSoggettoFruitoreLong);
			
			// aggiorno valore nella url
			parameterFruitoreChange = new Parameter(tmpTitle, 
					AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE ,
					new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ idServizioLong),
					new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, ""+ strutsBean.id),
					new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+ strutsBean.idSoggettoErogatoreDelServizio),
					new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE, ""+ strutsBean.idSoggettoFruitore)
					);

			// setto la barra del titolo
			List<Parameter> lstParm = new ArrayList<>();

			lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
			lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + titleServizio, 
					AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST ,
					new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ idServizioLong),
					new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+ strutsBean.idSoggettoErogatoreDelServizio)
					));
			lstParm.add(parameterFruitoreChange);
			lstParm.add(new Parameter(label , null));

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParm );
						
			protocollo = apsCore.getProtocolloAssociatoTipoServizio(asps.getTipo());
			
			strutsBean.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			strutsBean.consoleDynamicConfiguration =  strutsBean.protocolFactory.createDynamicConfigurationConsole();
			strutsBean.registryReader = soggettiCore.getRegistryReader(strutsBean.protocolFactory); 
			strutsBean.configRegistryReader = soggettiCore.getConfigIntegrationReader(strutsBean.protocolFactory);
			IDServizio idAps = apsHelper.getIDServizioFromValues(asps.getTipo(), asps.getNome(), asps.getTipoSoggettoErogatore(),asps.getNomeSoggettoErogatore(), asps.getVersione()+"");
			idAps.setPortType(asps.getPortType());
			strutsBean.consoleConfiguration = strutsBean.consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteSpecifica(strutsBean.consoleOperationType, apsHelper, 
					strutsBean.registryReader, strutsBean.configRegistryReader, idAps );
					
			List<ProtocolProperty> oldProtocolPropertyList = as.getProtocolPropertyList();
			strutsBean.protocolProperties = apsHelper.estraiProtocolPropertiesDaRequest(strutsBean.consoleConfiguration, strutsBean.consoleOperationType);
			ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(strutsBean.protocolProperties, oldProtocolPropertyList, strutsBean.consoleOperationType);
			
			Properties propertiesProprietario = new Properties();
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO, strutsBean.id);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO, ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_FRUITORE);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO, tmpTitle);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE,
					URLEncoder.encode(parameterFruitoreChange.getValue(), "UTF-8"));
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO, protocollo);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_ACCORDO, "");

			// preparo i campi
			List<DataElement> dati = new ArrayList<>();

			dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, strutsBean.id, null, null, dati);


			dati = apsHelper.addFruitoreWSDLToDati(TipoOperazione.OTHER, strutsBean.tipo, strutsBean.idSoggettoErogatoreDelServizio , strutsBean.idSoggettoFruitore,
					strutsBean.wsdl, strutsBean.validazioneDocumenti, myFru, dati,
					idServizioLong+"", tipologiaDocumentoScaricare, true, label);

			pd.setMessage("Modifica effettuata con successo", Costanti.MESSAGE_TYPE_INFO);
			
			pd.setDati(dati);

			pd.disableEditMode(); // altrimenti un successivo invio per aggiornare wsdl (o eliminarlo) non funziona.
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,
					AccordiServizioParteSpecificaCostanti.TIPO_OPERAZIONE_WSDL_CHANGE);
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,
					AccordiServizioParteSpecificaCostanti.TIPO_OPERAZIONE_WSDL_CHANGE);
		}  
	}
 
}
