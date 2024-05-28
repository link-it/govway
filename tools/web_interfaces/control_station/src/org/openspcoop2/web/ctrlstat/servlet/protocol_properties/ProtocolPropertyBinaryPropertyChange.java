/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.web.ctrlstat.servlet.protocol_properties;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.AbstractConsoleItem;
import org.openspcoop2.protocol.sdk.properties.AbstractProperty;
import org.openspcoop2.protocol.sdk.properties.BinaryConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BinaryProperty;
import org.openspcoop2.protocol.sdk.properties.BooleanProperty;
import org.openspcoop2.protocol.sdk.properties.NumberProperty;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesFactory;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.properties.StringProperty;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ProtocolPropertyBinaryPropertyChange
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProtocolPropertyBinaryPropertyChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);
		

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);

		String label = "";

		TipoOperazione tipoOp = TipoOperazione.CHANGE;
		List<ProtocolProperty> oldProtocolPropertyListRegistry = null;
		List<org.openspcoop2.core.config.ProtocolProperty> oldProtocolPropertyListConfig = null;

		ProtocolPropertiesStrutsBean strutsBean = new ProtocolPropertiesStrutsBean();
		
		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		strutsBean.consoleOperationType = ConsoleOperationType.CHANGE;
		
		BinaryParameter oldContenutoDocumento = null; 
		String tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PROTOCOL_PROPERTY_BINARY;

		try{
			ProtocolPropertiesHelper ppHelper = new ProtocolPropertiesHelper(request, pd, session);
			
			strutsBean.id = ppHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_ID);
			strutsBean.idProprietario = ppHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO);
			String tipoProprietarioS = ppHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO);
			if(tipoProprietarioS!= null)
				strutsBean.tipoProprietario = ProprietariProtocolProperty.valueOf(tipoProprietarioS);
			boolean propertyRegistry = ppHelper.isProtocolPropertiesRegistry(strutsBean.tipoProprietario);
			strutsBean.nome = ppHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME);
			strutsBean.nomeProprietario = ppHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO);
			strutsBean.nomeParentProprietario = ppHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PARENT_PROPRIETARIO);
			strutsBean.urlChange = ppHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE);
			strutsBean.editMode = ppHelper.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
			strutsBean.contenutoDocumento = ppHelper.getBinaryParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_CONTENUTO_DOCUMENTO);
			strutsBean.protocollo = ppHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO);
			strutsBean.tipoAccordo = ppHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_ACCORDO);

			ProtocolPropertiesCore ppCore = new ProtocolPropertiesCore();

			strutsBean.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(strutsBean.protocollo);
			strutsBean.consoleDynamicConfiguration =  strutsBean.protocolFactory.createDynamicConfigurationConsole();
			strutsBean.registryReader = ppCore.getRegistryReader(strutsBean.protocolFactory); 
			strutsBean.configRegistryReader = ppCore.getConfigIntegrationReader(strutsBean.protocolFactory);

			long idProtocolPropertyLong = Long.parseLong(strutsBean.id);

			// prelevo l'oggetto proprietario
			Object oggettoProprietario = ppHelper.getOggettoProprietario(strutsBean.idProprietario, strutsBean.nomeProprietario, strutsBean.nomeParentProprietario, strutsBean.tipoProprietario, strutsBean.tipoAccordo);

			// prelevo l'id oggetto proprietario
			Object idOggettoProprietario = ppHelper.getIdOggettoProprietario(oggettoProprietario, strutsBean.idProprietario, strutsBean.nomeProprietario, strutsBean.nomeParentProprietario, strutsBean.tipoProprietario, strutsBean.tipoAccordo);

			strutsBean.consoleConfiguration = ppHelper.getConsoleDynamicConfiguration(idOggettoProprietario, strutsBean.idProprietario, strutsBean.nomeProprietario, strutsBean.nomeParentProprietario, strutsBean.tipoProprietario, strutsBean.tipoAccordo,
					strutsBean.consoleOperationType,  
					strutsBean.registryReader, strutsBean.configRegistryReader, strutsBean.consoleDynamicConfiguration);

			strutsBean.protocolProperties = ppHelper.estraiProtocolPropertiesDaRequest(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, strutsBean.nome, strutsBean.contenutoDocumento);

			if(propertyRegistry) {
				oldProtocolPropertyListRegistry = ppHelper.getProtocolPropertiesRegistry(oggettoProprietario, strutsBean.id, strutsBean.nome, strutsBean.idProprietario, strutsBean.nomeProprietario, strutsBean.nomeParentProprietario, strutsBean.tipoProprietario, strutsBean.tipoAccordo);
				ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(strutsBean.protocolProperties, oldProtocolPropertyListRegistry, strutsBean.consoleOperationType);
			}
			else {
				oldProtocolPropertyListConfig = ppHelper.getProtocolPropertiesConfig(oggettoProprietario, strutsBean.id, strutsBean.nome, strutsBean.idProprietario, strutsBean.nomeProprietario, strutsBean.nomeParentProprietario, strutsBean.tipoProprietario, strutsBean.tipoAccordo);
				ProtocolPropertiesUtils.mergeProtocolPropertiesConfig(strutsBean.protocolProperties, oldProtocolPropertyListConfig, strutsBean.consoleOperationType);
			}

			// vecchio valore della property sul DB 
			ProtocolProperty protocolPropertyRegistry = null; 
			org.openspcoop2.core.config.ProtocolProperty protocolPropertyConfig = null; 

			// binaryConsoleItem
			AbstractConsoleItem<?> binaryConsoleItem = ProtocolPropertiesUtils.getAbstractConsoleItem(strutsBean.consoleConfiguration.getConsoleItem(), strutsBean.nome); 

			label = binaryConsoleItem.getLabel();
			
			boolean readOnly = false;
			String noteAggiornamento = null;
			boolean showContent = true;
			BinaryConsoleItem b = null;
			if(binaryConsoleItem instanceof BinaryConsoleItem) {
				b = (BinaryConsoleItem) binaryConsoleItem;
				readOnly = b.isReadOnly();
				showContent = b.isShowContent();
				noteAggiornamento = b.getNoteUpdate();
			}
			
			// Preparo il menu
			ppHelper.makeMenu();

			// se la property e' presente sul db allora la leggo
			if(idProtocolPropertyLong > 0){
				if(propertyRegistry) {
					protocolPropertyRegistry = ProtocolPropertiesUtils.getProtocolPropertyRegistry(strutsBean.nome, oldProtocolPropertyListRegistry);
				}
				else {
					protocolPropertyConfig = ProtocolPropertiesUtils.getProtocolPropertyConfig(strutsBean.nome, oldProtocolPropertyListConfig);
				}
			}

			oldContenutoDocumento = new BinaryParameter();
			if(protocolPropertyRegistry != null){
				// carico il vecchio contenuto della property
				oldContenutoDocumento.setValue(protocolPropertyRegistry.getByteFile());
				oldContenutoDocumento.setFilename(protocolPropertyRegistry.getFile());
				oldContenutoDocumento.setName(strutsBean.nome); 
			}
			else if(protocolPropertyConfig != null){
				// carico il vecchio contenuto della property
				oldContenutoDocumento.setValue(protocolPropertyConfig.getByteFile());
				oldContenutoDocumento.setFilename(protocolPropertyConfig.getFile());
				oldContenutoDocumento.setName(strutsBean.nome); 
			}

			StringBuilder contenutoDocumentoStringBuilder = new StringBuilder();
			byte[] oldValue =  oldContenutoDocumento.getValue() ;
			if(readOnly && oldValue==null) {
				// il valore deve essere preso da default value
				oldValue = b.getDefaultValue();
				oldContenutoDocumento.setValue(oldValue);
				if(oldContenutoDocumento.getFilename()==null) {
					oldContenutoDocumento.setFilename(binaryConsoleItem.getLabel());
				}
			}
			String errore = null;
			if(showContent) {
				errore = Utilities.getTestoVisualizzabile(oldValue,contenutoDocumentoStringBuilder);
			}
			
			// Parametri Barra titolo TIPO PROPRIETARIO / LABEL OGGETTO / GESTIONE DOCUMENTO
			List<Parameter> lstParam = ppHelper.getTitolo(oggettoProprietario, strutsBean.tipoProprietario,  strutsBean.id, strutsBean.nome, strutsBean.idProprietario, strutsBean.nomeProprietario,strutsBean.nomeParentProprietario,strutsBean.urlChange, strutsBean.tipoAccordo);
			lstParam.add(new Parameter(label,null));

			if(ServletUtils.isEditModeInProgress(strutsBean.editMode)){

				// setto la barra del titolo	TIPO PROPRIETARIO / LABEL OGGETTO / GESTIONE DOCUMENTO			
				ServletUtils.setPageDataTitle(pd,lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = ppHelper.addProtocolPropertyChangeToDati(tipoOp, dati, strutsBean.protocollo, strutsBean.id, strutsBean.nome, strutsBean.idProprietario,strutsBean.tipoProprietario,strutsBean.tipoAccordo,strutsBean.nomeProprietario,strutsBean.nomeParentProprietario,strutsBean.urlChange, label,
						oldContenutoDocumento,contenutoDocumentoStringBuilder,errore,tipologiaDocumentoScaricare,binaryConsoleItem, readOnly, noteAggiornamento);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, 
						ProtocolPropertiesCostanti.OBJECT_NAME_PP, ProtocolPropertiesCostanti.TIPO_OPERAZIONE_BINARY_PROPERTY_CHANGE);
			}

			// update del valore della singola property
			AbstractProperty<?> abstractPropertyById = ProtocolPropertiesUtils.getAbstractPropertyById(strutsBean.protocolProperties, binaryConsoleItem.getId());
			if(abstractPropertyById != null){
				BinaryProperty bp = (BinaryProperty) abstractPropertyById;
				bp.setValue(strutsBean.contenutoDocumento.getValue());
				bp.setFileName(strutsBean.contenutoDocumento.getFilename());
				bp.setFileId(strutsBean.contenutoDocumento.getId()); 
			}

			// validazione del contenuto
			boolean isOk = true;

			try{
				ppHelper.validaProtocolPropertyBinaria(strutsBean.nome,strutsBean.consoleConfiguration, strutsBean.consoleOperationType, strutsBean.protocolProperties);
			}catch(ProtocolException e){
				ControlStationCore.getLog().error(e.getMessage(),e);
				pd.setMessage(e.getMessage());
				isOk = false;
			}
			
			// Valido i parametri custom se ho gia' passato tutta la validazione prevista
			if(isOk){
				try{
					
					// Rendo in chiaro eventuali proprietà cifrate, per consentirne la validazione
					ProtocolProperties protocolPropertiesCloned = new ProtocolProperties();
					if(strutsBean.protocolProperties!=null && strutsBean.protocolProperties.sizeProperties()>0) {
						for (int i = 0; i < strutsBean.protocolProperties.sizeProperties(); i++) {
							AbstractProperty<?> pp = strutsBean.protocolProperties.getProperty(i);
							if(pp instanceof BinaryProperty) {
								BinaryProperty bp = (BinaryProperty) pp;
								BinaryProperty bpCloned = ProtocolPropertiesFactory.newProperty(bp.getId(), ppCore.getDriverBYOKUtilities().unwrap(bp.getValue()), bp.getFileName(), bp.getFileId());
								protocolPropertiesCloned.addProperty(bpCloned);
							}
							else if(pp instanceof StringProperty) {
								StringProperty sp = (StringProperty) pp;
								StringProperty spCloned = ProtocolPropertiesFactory.newProperty(sp.getId(), ppCore.getDriverBYOKUtilities().unwrap(sp.getValue()));
								protocolPropertiesCloned.addProperty(spCloned);
							}
							else if(pp instanceof BooleanProperty) {
								BooleanProperty bp = (BooleanProperty) pp;
								BooleanProperty bpCloned = ProtocolPropertiesFactory.newProperty(bp.getId(), bp.getValue());
								protocolPropertiesCloned.addProperty(bpCloned);
							}
							else if(pp instanceof NumberProperty) {
								NumberProperty np = (NumberProperty) pp;
								NumberProperty npCloned = ProtocolPropertiesFactory.newProperty(np.getId(), np.getValue());
								protocolPropertiesCloned.addProperty(npCloned);
							}
						}
					}
					
					ppHelper.validateDynamicConfig(strutsBean.consoleDynamicConfiguration, idOggettoProprietario, strutsBean.tipoAccordo, strutsBean.tipoProprietario, strutsBean.consoleConfiguration, strutsBean.consoleOperationType, 
							protocolPropertiesCloned,/** strutsBean.protocolProperties,*/ 
							strutsBean.registryReader, strutsBean.configRegistryReader);
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}
			
			if (!isOk) {

				// setto la barra del titolo	TIPO PROPRIETARIO / LABEL OGGETTO / GESTIONE DOCUMENTO			
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				contenutoDocumentoStringBuilder = new StringBuilder();
				errore = null;
				if(showContent) {
					errore = Utilities.getTestoVisualizzabile(strutsBean.contenutoDocumento.getValue(),contenutoDocumentoStringBuilder);
				}

				dati = ppHelper.addProtocolPropertyChangeToDati(tipoOp, dati, strutsBean.protocollo, strutsBean.id, strutsBean.nome, strutsBean.idProprietario,strutsBean.tipoProprietario,strutsBean.tipoAccordo,strutsBean.nomeProprietario,strutsBean.nomeParentProprietario,strutsBean.urlChange, label,
						strutsBean.contenutoDocumento,contenutoDocumentoStringBuilder,errore,tipologiaDocumentoScaricare,binaryConsoleItem, readOnly, noteAggiornamento);
				
				pd.setDati(dati);
				
				//cancello il file temporaneo
				ppHelper.deleteBinaryProtocolPropertyTmpFiles(strutsBean.protocolProperties, strutsBean.nome, ProtocolPropertiesCostanti.PARAMETRO_PP_CONTENUTO_DOCUMENTO);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						ProtocolPropertiesCostanti.OBJECT_NAME_PP, ProtocolPropertiesCostanti.TIPO_OPERAZIONE_BINARY_PROPERTY_CHANGE);
			}

			// salvataggio delle properties con hack in ADD per far settare il valore della property binary
			if(propertyRegistry) {
				List<ProtocolProperty> protocolPropertiesAggiornate = ProtocolPropertiesUtils.toProtocolPropertiesRegistry(strutsBean.protocolProperties, ConsoleOperationType.ADD, oldProtocolPropertyListRegistry);
				ppHelper.salvaPropertiesRegistry(userLogin, ppHelper.smista(), oggettoProprietario, protocolPropertiesAggiornate, strutsBean.id, strutsBean.nome, strutsBean.idProprietario, strutsBean.nomeProprietario, strutsBean.nomeParentProprietario, strutsBean.tipoProprietario, strutsBean.tipoAccordo);
			}
			else {
				List<org.openspcoop2.core.config.ProtocolProperty> protocolPropertiesAggiornate = ProtocolPropertiesUtils.toProtocolPropertiesConfig(strutsBean.protocolProperties, ConsoleOperationType.ADD, oldProtocolPropertyListConfig);
				ppHelper.salvaPropertiesConfig(userLogin, ppHelper.smista(), oggettoProprietario, protocolPropertiesAggiornate, strutsBean.id, strutsBean.nome, strutsBean.idProprietario, strutsBean.nomeProprietario, strutsBean.nomeParentProprietario, strutsBean.tipoProprietario, strutsBean.tipoAccordo);
			}
				
			//cancello il file temporaneo
			ppHelper.deleteBinaryProtocolPropertyTmpFiles(strutsBean.protocolProperties, strutsBean.nome, ProtocolPropertiesCostanti.PARAMETRO_PP_CONTENUTO_DOCUMENTO);
			
			// mostro la visualizzazione del file
			
			// setto la barra del titolo	TIPO PROPRIETARIO / LABEL OGGETTO / GESTIONE DOCUMENTO	
			// Aggiorno la barra perche' possono essere cambiati gli id delle breadcrumbs a seconda del proprietario
			lstParam = ppHelper.getTitolo(oggettoProprietario, strutsBean.tipoProprietario,  strutsBean.id, strutsBean.nome, strutsBean.idProprietario, strutsBean.nomeProprietario,strutsBean.nomeParentProprietario,strutsBean.urlChange, strutsBean.tipoAccordo,true);
			lstParam.add(new Parameter(label,null));
			
			ServletUtils.setPageDataTitle(pd, lstParam);

			// preparo i campi
			List<DataElement> dati = new ArrayList<>();

			dati.add(ServletUtils.getDataElementForEditModeFinished());
			
			// ricaricare il valore dell'id property
			oggettoProprietario = ppHelper.getOggettoProprietario(strutsBean.idProprietario, strutsBean.nomeProprietario, strutsBean.nomeParentProprietario, strutsBean.tipoProprietario, strutsBean.tipoAccordo);
			
			if(propertyRegistry) {
				oldProtocolPropertyListRegistry = ppHelper.getProtocolPropertiesRegistry(oggettoProprietario, strutsBean.id, strutsBean.nome, strutsBean.idProprietario, strutsBean.nomeProprietario, strutsBean.nomeParentProprietario, strutsBean.tipoProprietario, strutsBean.tipoAccordo);
				ProtocolProperty protocolProperty = ProtocolPropertiesUtils.getProtocolPropertyRegistry(strutsBean.nome, oldProtocolPropertyListRegistry);
				strutsBean.id = null;
				if(protocolProperty != null)
					strutsBean.id = ""+ protocolProperty.getId();
			}
			else {
				oldProtocolPropertyListConfig = ppHelper.getProtocolPropertiesConfig(oggettoProprietario, strutsBean.id, strutsBean.nome, strutsBean.idProprietario, strutsBean.nomeProprietario, strutsBean.nomeParentProprietario, strutsBean.tipoProprietario, strutsBean.tipoAccordo);
				org.openspcoop2.core.config.ProtocolProperty protocolProperty = ProtocolPropertiesUtils.getProtocolPropertyConfig(strutsBean.nome, oldProtocolPropertyListConfig);
				strutsBean.id = null;
				if(protocolProperty != null)
					strutsBean.id = ""+ protocolProperty.getId();
			}
			
			contenutoDocumentoStringBuilder = new StringBuilder();
			errore = null;
			if(showContent) {
				errore = Utilities.getTestoVisualizzabile(strutsBean.contenutoDocumento.getValue(),contenutoDocumentoStringBuilder);
			}

			dati = ppHelper.addProtocolPropertyChangeToDati(tipoOp, dati, strutsBean.protocollo, strutsBean.id, strutsBean.nome, strutsBean.idProprietario,strutsBean.tipoProprietario,strutsBean.tipoAccordo,strutsBean.nomeProprietario,strutsBean.nomeParentProprietario,strutsBean.urlChange, label,
					strutsBean.contenutoDocumento,contenutoDocumentoStringBuilder,errore,tipologiaDocumentoScaricare,binaryConsoleItem, readOnly, noteAggiornamento);

			pd.setDati(dati);
			
			// setto la baseurl per il redirect (alla servlet XXXChange) ,strutsBean.urlChange
			// se viene premuto invio
			gd = generalHelper.initGeneralData(request);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ProtocolPropertiesCostanti.OBJECT_NAME_PP, ProtocolPropertiesCostanti.TIPO_OPERAZIONE_BINARY_PROPERTY_CHANGE);	

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ProtocolPropertiesCostanti.OBJECT_NAME_PP, ProtocolPropertiesCostanti.TIPO_OPERAZIONE_BINARY_PROPERTY_CHANGE);
		}
	}
}
