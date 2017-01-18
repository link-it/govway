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


package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
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
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesCostanti;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesUtilities;
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

	private String   id, tipo, wsdl, idSoggettoErogatoreDelServizio;
	private boolean validazioneDocumenti = true;
	private boolean decodeRequestValidazioneDocumenti = false;
	private String editMode = null;
	
	// Protocol Properties
	private IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
	private ConsoleConfiguration consoleConfiguration =null;
	private ProtocolProperties protocolProperties = null;
	private IProtocolFactory<?> protocolFactory= null;
	private IRegistryReader registryReader = null; 
	private ConsoleOperationType consoleOperationType = null;
	private ConsoleInterfaceType consoleInterfaceType = null;
	
//	private BinaryParameter wsdlimpler, wsdlimplfru;

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
			this.consoleInterfaceType = ProtocolPropertiesUtilities.getTipoInterfaccia(session); 
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			
			this.editMode = apsHelper.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
			this.id = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			this.tipo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO);
			this.wsdl = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL);

			this.idSoggettoErogatoreDelServizio = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);
			if ((this.idSoggettoErogatoreDelServizio == null) || this.idSoggettoErogatoreDelServizio.equals("")) {
				PageData oldPD = ServletUtils.getPageDataFromSession(session);

				this.idSoggettoErogatoreDelServizio = oldPD.getHidden(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);
			}

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

			int idFru = Integer.parseInt(this.id);

			// Preparo il menu
			apsHelper.makeMenu();

			// Prendo il wsdl attuale del fruitore
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(apsCore);

			Fruitore myFru = apsCore.getServizioFruitore(idFru);
			int idServ = myFru.getIdServizio().intValue();
			int tmpProv = myFru.getIdSoggetto().intValue();
			String oldwsdl = "";
			byte[] wsdlbyte = null;
			String tipologiaDocumentoScaricare = null; 
			if(this.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_EROGATORE)){
				wsdlbyte = myFru.getByteWsdlImplementativoErogatore();
				tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_EROGATORE;
			}if(this.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_FRUITORE)){
				wsdlbyte = myFru.getByteWsdlImplementativoFruitore();
				tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_FRUITORE;
			}
			if (wsdlbyte != null)
				oldwsdl = new String(wsdlbyte);

			// Prendo il nome e il tipo del servizio
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idServ);
			String uriServizio = IDServizioFactory.getInstance().getUriFromAccordo(asps);
			
			// Prendo Accordo di servizio parte comune
			AccordoServizioParteComune as = apcCore.getAccordoServizio(idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune()));

			// Mi calcolo IDServizio, che servirà per recuperare il fruitore una
			// volta che sarà stato rimosso/aggiunto e se ne sarà perso l'id
			IDServizio ids = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);

			Soggetto mySogg2 = soggettiCore.getSoggettoRegistro(tmpProv);
			String tmpTitle = mySogg2.getTipo() + "/" + mySogg2.getNome();

			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			Parameter parameterFruitoreChange = new Parameter(tmpTitle, 
					AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE ,
					new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ idServ),
					new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, ""+ this.id),
					new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+ this.idSoggettoErogatoreDelServizio)
					);
			if (ServletUtils.isEditModeInProgress(this.editMode)) {
				List<Parameter> lstParm = new ArrayList<Parameter>();

				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + uriServizio, 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST ,
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ idServ),
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+ this.idSoggettoErogatoreDelServizio)
						));
				lstParm.add(parameterFruitoreChange);
				if(this.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_EROGATORE))
					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_WSDL_IMPLEMENTATIVO_EROGATORE_DI + tmpTitle , null));
				if(this.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_FRUITORE))
					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_WSDL_IMPLEMENTATIVO_FRUITORE_DI + tmpTitle , null));

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, this.id, null, null, dati);

				dati = apsHelper.addFruitoreWSDLToDati(TipoOperazione.OTHER, this.tipo, this.idSoggettoErogatoreDelServizio ,
						oldwsdl, this.validazioneDocumenti, myFru, dati,
						idServ+"", tipologiaDocumentoScaricare, false);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,
						AccordiServizioParteSpecificaCostanti.TIPO_OPERAZIONE_WSDL_CHANGE);
			}

			// Controlli sui campi immessi
			boolean isOk = apsHelper.accordiParteSpecificaFruitoreWSDLCheckData(pd, this.tipo, this.wsdl, myFru, asps, as, this.validazioneDocumenti);
			if (!isOk) {
				List<Parameter> lstParm = new ArrayList<Parameter>();

				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
				lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + uriServizio, 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST ,
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ idServ),
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+ this.idSoggettoErogatoreDelServizio)
						));
				lstParm.add(parameterFruitoreChange);
				if(this.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_EROGATORE))
					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_WSDL_IMPLEMENTATIVO_EROGATORE_DI + tmpTitle , null));
				if(this.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_FRUITORE))
					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_WSDL_IMPLEMENTATIVO_FRUITORE_DI + tmpTitle , null));

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, this.id, null, null, dati);

				dati = apsHelper.addFruitoreWSDLToDati(TipoOperazione.OTHER, this.tipo, this.idSoggettoErogatoreDelServizio ,
						oldwsdl, this.validazioneDocumenti, myFru, dati,
						idServ+"", tipologiaDocumentoScaricare, false);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI, 
						AccordiServizioParteSpecificaCostanti.TIPO_OPERAZIONE_WSDL_CHANGE);
			}

			// Modifico i dati del wsdl del fruitore nel db

			if (this.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_EROGATORE))
				myFru.setByteWsdlImplementativoErogatore(this.wsdl != null && !this.wsdl.equals("") ? this.wsdl.getBytes() : null);
			if (this.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_FRUITORE))
				myFru.setByteWsdlImplementativoFruitore(this.wsdl != null && !this.wsdl.equals("") ? this.wsdl.getBytes() : null);

			// Elimino il vecchio fruitore ed aggiungo il nuovo
			for (int i = 0; i < asps.sizeFruitoreList(); i++) {
				Fruitore tmpFru = asps.getFruitore(i);
				if (tmpFru.getId().longValue() == myFru.getId().longValue()) {
					asps.removeFruitore(i);
					break;
				}
			}

			asps.addFruitore(myFru);
			String superUser =  ServletUtils.getUserLoginFromSession(session);
			apsCore.performUpdateOperation(superUser, apsHelper.smista(), asps);


			// Prendo il nuovo id del fruitore
			this.id = "" + apsCore.getServizioFruitore(ids, tmpProv);
			
			// aggiorno valore nella url
			parameterFruitoreChange = new Parameter(tmpTitle, 
					AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE ,
					new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ idServ),
					new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, ""+ this.id),
					new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+ this.idSoggettoErogatoreDelServizio)
					);

			// setto la barra del titolo
			List<Parameter> lstParm = new ArrayList<Parameter>();

			lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, null));
			lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
			lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + uriServizio, 
					AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST ,
					new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ idServ),
					new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+ this.idSoggettoErogatoreDelServizio)
					));
			lstParm.add(parameterFruitoreChange);
			if(this.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_EROGATORE))
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_WSDL_IMPLEMENTATIVO_EROGATORE_DI + tmpTitle , null));
			if(this.tipo.equals(AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_PARAMETRO_WSDL_IMPL_FRUITORE))
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_WSDL_IMPLEMENTATIVO_FRUITORE_DI + tmpTitle , null));

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParm );
			
//			this.wsdlimpler = new BinaryParameter();
//			this.wsdlimplfru = new BinaryParameter();
			
			String protocollo = apsCore.getProtocolloAssociatoTipoServizio(asps.getTipo());
			
			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			this.consoleDynamicConfiguration =  this.protocolFactory.createDynamicConfigurationConsole();
			this.registryReader = soggettiCore.getRegistryReader(this.protocolFactory); 
			IDServizio idAps = apsHelper.getIDServizioFromValues(asps.getTipo(), asps.getNome(), asps.getTipoSoggettoErogatore(),asps.getNomeSoggettoErogatore(), asps.getVersione()+"");
			this.consoleConfiguration = this.consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteSpecifica(this.consoleOperationType, this.consoleInterfaceType, this.registryReader, idAps );
					
			List<ProtocolProperty> oldProtocolPropertyList = as.getProtocolPropertyList();
			this.protocolProperties = apsHelper.estraiProtocolPropertiesDaRequest(this.consoleConfiguration, this.consoleOperationType);
			ProtocolPropertiesUtils.mergeProtocolProperties(this.protocolProperties, oldProtocolPropertyList, this.consoleOperationType);
			
			Properties propertiesProprietario = new Properties();
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO, this.id);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO, ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_FRUITORE);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO, tmpTitle);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE,
					URLEncoder.encode(parameterFruitoreChange.getValue(), "UTF-8"));
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO, protocollo);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_ACCORDO, "");

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, this.id, null, null, dati);


			dati = apsHelper.addFruitoreWSDLToDati(TipoOperazione.OTHER, this.tipo, this.idSoggettoErogatoreDelServizio ,
					this.wsdl, this.validazioneDocumenti, myFru, dati,
					idServ+"", tipologiaDocumentoScaricare, true);

			pd.setMessage("Modifica effettuata con successo");
			
			pd.setDati(dati);

			pd.disableEditMode(); // altrimenti un successivo invio per aggiornare wsdl (o eliminarlo) non funziona.
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,
					AccordiServizioParteSpecificaCostanti.TIPO_OPERAZIONE_WSDL_CHANGE);
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,
					AccordiServizioParteSpecificaCostanti.TIPO_OPERAZIONE_WSDL_CHANGE);
		}  
	}
 
}
