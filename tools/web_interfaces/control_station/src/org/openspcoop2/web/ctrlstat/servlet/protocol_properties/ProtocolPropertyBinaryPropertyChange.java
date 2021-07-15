/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

import java.util.List;
import java.util.Vector;

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
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.AbstractConsoleItem;
import org.openspcoop2.protocol.sdk.properties.AbstractProperty;
import org.openspcoop2.protocol.sdk.properties.BinaryConsoleItem;
import org.openspcoop2.protocol.sdk.properties.BinaryProperty;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
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

	private String id, nome, idProprietario,  nomeProprietario, nomeParentProprietario, urlChange, editMode, protocollo, tipoAccordo;

//	private byte[] byteDocumento;
	private ProprietariProtocolProperty tipoProprietario = null;
	// Protocol Properties
	private IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
	private ConsoleConfiguration consoleConfiguration =null;
	private ProtocolProperties protocolProperties = null;
	private IProtocolFactory<?> protocolFactory= null;
	private IRegistryReader registryReader = null;
	private IConfigIntegrationReader configRegistryReader = null; 
	private ConsoleOperationType consoleOperationType = null;
	
	private BinaryParameter contenutoDocumento= null;

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

		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		this.consoleOperationType = ConsoleOperationType.CHANGE;
		
		BinaryParameter oldContenutoDocumento = null; 
		String tipologiaDocumentoScaricare = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_DOCUMENTO_PROTOCOL_PROPERTY_BINARY;

		try{
			ProtocolPropertiesHelper ppHelper = new ProtocolPropertiesHelper(request, pd, session);
			
			this.id = ppHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_ID);
			this.idProprietario = ppHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO);
			String tipoProprietarioS = ppHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO);
			if(tipoProprietarioS!= null)
				this.tipoProprietario = ProprietariProtocolProperty.valueOf(tipoProprietarioS);
			boolean propertyRegistry = ppHelper.isProtocolPropertiesRegistry(this.tipoProprietario);
			this.nome = ppHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME);
			this.nomeProprietario = ppHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO);
			this.nomeParentProprietario = ppHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PARENT_PROPRIETARIO);
			this.urlChange = ppHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE);
			this.editMode = ppHelper.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
			this.contenutoDocumento = ppHelper.getBinaryParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_CONTENUTO_DOCUMENTO);
			this.protocollo = ppHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO);
			this.tipoAccordo = ppHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_ACCORDO);

			ProtocolPropertiesCore ppCore = new ProtocolPropertiesCore();

			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(this.protocollo);
			this.consoleDynamicConfiguration =  this.protocolFactory.createDynamicConfigurationConsole();
			this.registryReader = ppCore.getRegistryReader(this.protocolFactory); 
			this.configRegistryReader = ppCore.getConfigIntegrationReader(this.protocolFactory);

			int idProtocolProperty = 0;
			try {
				idProtocolProperty = Integer.parseInt(this.id);
			} catch (Exception e) {
			}

			// prelevo l'oggetto proprietario
			Object oggettoProprietario = ppHelper.getOggettoProprietario(this.idProprietario, this.nomeProprietario, this.nomeParentProprietario, this.tipoProprietario, this.tipoAccordo);

			// prelevo l'id oggetto proprietario
			Object idOggettoProprietario = ppHelper.getIdOggettoProprietario(oggettoProprietario, this.idProprietario, this.nomeProprietario, this.nomeParentProprietario, this.tipoProprietario, this.tipoAccordo);

			this.consoleConfiguration = ppHelper.getConsoleDynamicConfiguration(idOggettoProprietario, this.idProprietario, this.nomeProprietario, this.nomeParentProprietario, this.tipoProprietario, this.tipoAccordo,
					this.consoleOperationType,  
					this.registryReader, this.configRegistryReader, this.consoleDynamicConfiguration);

			this.protocolProperties = ppHelper.estraiProtocolPropertiesDaRequest(this.consoleConfiguration, this.consoleOperationType, this.nome, this.contenutoDocumento);

			if(propertyRegistry) {
				oldProtocolPropertyListRegistry = ppHelper.getProtocolPropertiesRegistry(oggettoProprietario, this.id, this.nome, this.idProprietario, this.nomeProprietario, this.nomeParentProprietario, this.tipoProprietario, this.tipoAccordo);
				ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(this.protocolProperties, oldProtocolPropertyListRegistry, this.consoleOperationType);
			}
			else {
				oldProtocolPropertyListConfig = ppHelper.getProtocolPropertiesConfig(oggettoProprietario, this.id, this.nome, this.idProprietario, this.nomeProprietario, this.nomeParentProprietario, this.tipoProprietario, this.tipoAccordo);
				ProtocolPropertiesUtils.mergeProtocolPropertiesConfig(this.protocolProperties, oldProtocolPropertyListConfig, this.consoleOperationType);
			}

			// vecchio valore della property sul DB 
			ProtocolProperty protocolPropertyRegistry = null; 
			org.openspcoop2.core.config.ProtocolProperty protocolPropertyConfig = null; 

			// binaryConsoleItem
			AbstractConsoleItem<?> binaryConsoleItem = ProtocolPropertiesUtils.getAbstractConsoleItem(this.consoleConfiguration.getConsoleItem(), this.nome); 

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
			if(idProtocolProperty > 0){
				if(propertyRegistry) {
					protocolPropertyRegistry = ProtocolPropertiesUtils.getProtocolPropertyRegistry(this.nome, oldProtocolPropertyListRegistry);
				}
				else {
					protocolPropertyConfig = ProtocolPropertiesUtils.getProtocolPropertyConfig(this.nome, oldProtocolPropertyListConfig);
				}
			}

			oldContenutoDocumento = new BinaryParameter();
			if(protocolPropertyRegistry != null){
				// carico il vecchio contenuto della property
				oldContenutoDocumento.setValue(protocolPropertyRegistry.getByteFile());
				oldContenutoDocumento.setFilename(protocolPropertyRegistry.getFile());
				oldContenutoDocumento.setName(this.nome); 
			}
			else if(protocolPropertyConfig != null){
				// carico il vecchio contenuto della property
				oldContenutoDocumento.setValue(protocolPropertyConfig.getByteFile());
				oldContenutoDocumento.setFilename(protocolPropertyConfig.getFile());
				oldContenutoDocumento.setName(this.nome); 
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
			List<Parameter> lstParam = ppHelper.getTitolo(oggettoProprietario, this.tipoProprietario,  this.id, this.nome, this.idProprietario, this.nomeProprietario,this.nomeParentProprietario,this.urlChange, this.tipoAccordo);
			lstParam.add(new Parameter(label,null));

			if(ServletUtils.isEditModeInProgress(this.editMode)){

				// setto la barra del titolo	TIPO PROPRIETARIO / LABEL OGGETTO / GESTIONE DOCUMENTO			
				ServletUtils.setPageDataTitle(pd,lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = ppHelper.addProtocolPropertyChangeToDati(tipoOp, dati, this.protocollo, this.id, this.nome, this.idProprietario,this.tipoProprietario,this.tipoAccordo,this.nomeProprietario,this.nomeParentProprietario,this.urlChange, label,
						oldContenutoDocumento,contenutoDocumentoStringBuilder,errore,tipologiaDocumentoScaricare,binaryConsoleItem, readOnly, noteAggiornamento);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, 
						ProtocolPropertiesCostanti.OBJECT_NAME_PP, ProtocolPropertiesCostanti.TIPO_OPERAZIONE_BINARY_PROPERTY_CHANGE);
			}

			// update del valore della singola property
			AbstractProperty<?> abstractPropertyById = ProtocolPropertiesUtils.getAbstractPropertyById(this.protocolProperties, binaryConsoleItem.getId());
			if(abstractPropertyById != null){
				BinaryProperty bp = (BinaryProperty) abstractPropertyById;
				bp.setValue(this.contenutoDocumento.getValue());
				bp.setFileName(this.contenutoDocumento.getFilename());
				bp.setFileId(this.contenutoDocumento.getId()); 
			}

			// validazione del contenuto
			boolean isOk = true;

			try{
				ppHelper.validaProtocolPropertyBinaria(this.nome,this.consoleConfiguration, this.consoleOperationType, this.protocolProperties);
			}catch(ProtocolException e){
				ControlStationCore.getLog().error(e.getMessage(),e);
				pd.setMessage(e.getMessage());
				isOk = false;
			}
			
			// Valido i parametri custom se ho gia' passato tutta la validazione prevista
			if(isOk){
				try{
					ppHelper.validateDynamicConfig(this.consoleDynamicConfiguration, idOggettoProprietario, this.tipoAccordo, this.tipoProprietario, this.consoleConfiguration, this.consoleOperationType, this.protocolProperties, 
							this.registryReader, this.configRegistryReader);
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
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				contenutoDocumentoStringBuilder = new StringBuilder();
				errore = null;
				if(showContent) {
					errore = Utilities.getTestoVisualizzabile(this.contenutoDocumento.getValue(),contenutoDocumentoStringBuilder);
				}

				dati = ppHelper.addProtocolPropertyChangeToDati(tipoOp, dati, this.protocollo, this.id, this.nome, this.idProprietario,this.tipoProprietario,this.tipoAccordo,this.nomeProprietario,this.nomeParentProprietario,this.urlChange, label,
						this.contenutoDocumento,contenutoDocumentoStringBuilder,errore,tipologiaDocumentoScaricare,binaryConsoleItem, readOnly, noteAggiornamento);
				
				pd.setDati(dati);
				
				//cancello il file temporaneo
				ppHelper.deleteBinaryProtocolPropertyTmpFiles(this.protocolProperties, this.nome, ProtocolPropertiesCostanti.PARAMETRO_PP_CONTENUTO_DOCUMENTO);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						ProtocolPropertiesCostanti.OBJECT_NAME_PP, ProtocolPropertiesCostanti.TIPO_OPERAZIONE_BINARY_PROPERTY_CHANGE);
			}

			// salvataggio delle properties con hack in ADD per far settare il valore della property binary
			if(propertyRegistry) {
				List<ProtocolProperty> protocolPropertiesAggiornate = ProtocolPropertiesUtils.toProtocolPropertiesRegistry(this.protocolProperties, ConsoleOperationType.ADD, oldProtocolPropertyListRegistry);
				ppHelper.salvaPropertiesRegistry(userLogin, ppHelper.smista(), oggettoProprietario, protocolPropertiesAggiornate, this.id, this.nome, this.idProprietario, this.nomeProprietario, this.nomeParentProprietario, this.tipoProprietario, this.tipoAccordo);
			}
			else {
				List<org.openspcoop2.core.config.ProtocolProperty> protocolPropertiesAggiornate = ProtocolPropertiesUtils.toProtocolPropertiesConfig(this.protocolProperties, ConsoleOperationType.ADD, oldProtocolPropertyListConfig);
				ppHelper.salvaPropertiesConfig(userLogin, ppHelper.smista(), oggettoProprietario, protocolPropertiesAggiornate, this.id, this.nome, this.idProprietario, this.nomeProprietario, this.nomeParentProprietario, this.tipoProprietario, this.tipoAccordo);
			}
				
			//cancello il file temporaneo
			ppHelper.deleteBinaryProtocolPropertyTmpFiles(this.protocolProperties, this.nome, ProtocolPropertiesCostanti.PARAMETRO_PP_CONTENUTO_DOCUMENTO);
			
			// mostro la visualizzazione del file
			
			// setto la barra del titolo	TIPO PROPRIETARIO / LABEL OGGETTO / GESTIONE DOCUMENTO	
			// Aggiorno la barra perche' possono essere cambiati gli id delle breadcrumbs a seconda del proprietario
			lstParam = ppHelper.getTitolo(oggettoProprietario, this.tipoProprietario,  this.id, this.nome, this.idProprietario, this.nomeProprietario,this.nomeParentProprietario,this.urlChange, this.tipoAccordo,true);
			lstParam.add(new Parameter(label,null));
			
			ServletUtils.setPageDataTitle(pd, lstParam);

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			dati.addElement(ServletUtils.getDataElementForEditModeFinished());
			
			// ricaricare il valore dell'id property
			oggettoProprietario = ppHelper.getOggettoProprietario(this.idProprietario, this.nomeProprietario, this.nomeParentProprietario, this.tipoProprietario, this.tipoAccordo);
			
			if(propertyRegistry) {
				oldProtocolPropertyListRegistry = ppHelper.getProtocolPropertiesRegistry(oggettoProprietario, this.id, this.nome, this.idProprietario, this.nomeProprietario, this.nomeParentProprietario, this.tipoProprietario, this.tipoAccordo);
				ProtocolProperty protocolProperty = ProtocolPropertiesUtils.getProtocolPropertyRegistry(this.nome, oldProtocolPropertyListRegistry);
				this.id = null;
				if(protocolProperty != null)
					this.id = ""+ protocolProperty.getId();
			}
			else {
				oldProtocolPropertyListConfig = ppHelper.getProtocolPropertiesConfig(oggettoProprietario, this.id, this.nome, this.idProprietario, this.nomeProprietario, this.nomeParentProprietario, this.tipoProprietario, this.tipoAccordo);
				org.openspcoop2.core.config.ProtocolProperty protocolProperty = ProtocolPropertiesUtils.getProtocolPropertyConfig(this.nome, oldProtocolPropertyListConfig);
				this.id = null;
				if(protocolProperty != null)
					this.id = ""+ protocolProperty.getId();
			}
			
			contenutoDocumentoStringBuilder = new StringBuilder();
			errore = null;
			if(showContent) {
				errore = Utilities.getTestoVisualizzabile(this.contenutoDocumento.getValue(),contenutoDocumentoStringBuilder);
			}

			dati = ppHelper.addProtocolPropertyChangeToDati(tipoOp, dati, this.protocollo, this.id, this.nome, this.idProprietario,this.tipoProprietario,this.tipoAccordo,this.nomeProprietario,this.nomeParentProprietario,this.urlChange, label,
					this.contenutoDocumento,contenutoDocumentoStringBuilder,errore,tipologiaDocumentoScaricare,binaryConsoleItem, readOnly, noteAggiornamento);

			pd.setDati(dati);
			
			// setto la baseurl per il redirect (alla servlet XXXChange) ,this.urlChange
			// se viene premuto invio
			gd = generalHelper.initGeneralData(request);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ProtocolPropertiesCostanti.OBJECT_NAME_PP, ProtocolPropertiesCostanti.TIPO_OPERAZIONE_BINARY_PROPERTY_CHANGE);	

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ProtocolPropertiesCostanti.OBJECT_NAME_PP, ProtocolPropertiesCostanti.TIPO_OPERAZIONE_BINARY_PROPERTY_CHANGE);
		}
	}
}
