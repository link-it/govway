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


package org.openspcoop2.web.ctrlstat.servlet.archivi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.protocol.engine.archive.ImportInformationMissingCollection;
import org.openspcoop2.protocol.engine.archive.ImportInformationMissingException;
import org.openspcoop2.protocol.information_missing.Wizard;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveModeType;
import org.openspcoop2.protocol.sdk.archive.ImportMode;
import org.openspcoop2.protocol.sdk.archive.MapPlaceholder;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * Importer
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public final class Importer extends Action {

	private String filePath;
	private String protocollo;
	private String importMode;
	private String importType;
	
	private String importInformationMissingObjectId = null;
	private String importInformationMissingCollectionFilePath = null;
	
	// Campi per lo specifico missingObjectId in corso
	private Class<?> importInformationMissing_classObject = null;
	private Object importInformationMissing_object = null;
	private String importInformationMissing_soggettoInput = null;
	private String importInformationMissing_versioneInput = null;
	private List<PortType> importInformationMissing_portTypes = null;
	private String importInformationMissing_modalitaAcquisizioneInformazioniProtocollo = null;
	private String importInformationMissing_portTypeImplementedInput = null;
	private String importInformationMissing_accordoServizioParteComuneInput = null;
	private String importInformationMissing_accordoCooperazioneInput = null;
	private InvocazioneServizio importInformationMissing_invocazioneServizio = null;
	private Connettore importInformationMissing_connettore = null;
	private Credenziali importInformationMissing_credenziali = null;
	
	private MapPlaceholder importInformationMissing_globalPlaceholder = new MapPlaceholder();
	
	private boolean validazioneDocumenti = true;
	private boolean updateEnabled = false;
	private boolean importDeletePolicyConfig = false;
	private boolean importConfig = false;

	private int step = 0;
	
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			
			String userLogin = ServletUtils.getUserLoginFromSession(session);
			
			ArchiviHelper archiviHelper = new ArchiviHelper(request, pd, session);
			ArchiviCore archiviCore = new ArchiviCore();
			
			// TODO: In caso di ControlStation remota, gestire a quale pdd 'operativa' associare i soggetti senza 'pdd'
			PddCore pddCore = new PddCore(archiviCore);
			if(pddCore.existsPddOperativa()==false){
				
				archiviHelper.makeMenu();
				
				pd.setMessage("Non è possibile completare l'operazione: non è stata rilevata una Porta di Dominio operativa");
				
				pd.disableEditMode();
				
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeFinished(mapping, ArchiviCostanti.OBJECT_NAME_ARCHIVI_IMPORT, 
						ArchiviCostanti.TIPO_OPERAZIONE_IMPORT);				
			}
			String nomePddOperativa = pddCore.getNomePddOperativa();
			
			ImporterUtils importerUtils = new ImporterUtils(archiviCore);
					
			
			// Eventuale PostBack element name che ha scaturito l'evento
			String postBackElementName = archiviHelper.getPostBackElementName();
			
			
			// Indicazione se devo effettuare una delete od una import
			boolean deleter = false;
			String parametroModalitaFunzionamento = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORTER_MODALITA);
			if(parametroModalitaFunzionamento!=null){
				deleter = ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORTER_MODALITA_ELIMINA.equals(parametroModalitaFunzionamento.trim());
			}
			else{
				// default
				parametroModalitaFunzionamento = ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORTER_MODALITA_IMPORT;
			}
			DataElement modalitaDataElement = new DataElement();
			modalitaDataElement.setLabel(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORTER_MODALITA);
			modalitaDataElement.setValue(parametroModalitaFunzionamento);
			modalitaDataElement.setType(DataElementType.HIDDEN);
			modalitaDataElement.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORTER_MODALITA);
			
			
			String labelBottone = ArchiviCostanti.LABEL_ARCHIVI_IMPORT;
			if(deleter){
				labelBottone = ArchiviCostanti.LABEL_ARCHIVI_ELIMINA;	
			}
			
			// parametri vari
			this.filePath = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PACKAGE_FILE_PATH);
			if(this.filePath==null || "".equals(this.filePath)){
				this.step = 0;
			}
			
			// protocolli supportati
			List<String> protocolli = archiviCore.getProtocolli(session);
			if(protocolli.size()>1){
				protocolli = new ArrayList<String>();
				protocolli.add(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO_UNDEFINDED);
				protocolli.addAll(archiviCore.getProtocolli(session));
			}
			this.protocollo = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO);
			if("".equals(this.protocollo) || ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO_UNDEFINDED.equals(this.protocollo)){
				this.protocollo = null;
			}
			// Volutamente per default si vogliono tutti!
//			if(this.protocollo==null){
//				this.protocollo = archiviCore.getProtocolloDefault();
//			}
			
			
			
			// show Protocols se esiste più di un importMode per qualche protocollo
			Hashtable<String, String> importModesMapRispettoATuttiIProtocolli = importerUtils.getImportModesWithProtocol(protocolli);
			boolean showProtocols = importModesMapRispettoATuttiIProtocolli!=null && importModesMapRispettoATuttiIProtocolli.size()>1;
			
			
			
			// import modes
			List<String> protocolliForModes = new ArrayList<String>();
			if(this.protocollo!=null){
				protocolliForModes.add(this.protocollo);
			}else{
				protocolliForModes.addAll(protocolli);
			}
			Hashtable<String, String> importModesMap = importerUtils.getImportModesWithProtocol(protocolliForModes);
			List<ImportMode> importModes = new ArrayList<ImportMode>();
			for (String imp : importModesMap.keySet()) {
				importModes.add(new ImportMode(imp));
			}
			this.importMode = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPOLOGIA_ARCHIVIO);
			if(this.importMode!=null){
				// verifico che esista nei modes disponibili per i protocolli selezionati
				if(importModes.contains(new ImportMode(this.importMode))==false){
					this.importMode = null;
				}
			}
			if(this.importMode==null){
				if(importModes.contains(org.openspcoop2.protocol.basic.Costanti.OPENSPCOOP_IMPORT_ARCHIVE_MODE)){
					this.importMode = org.openspcoop2.protocol.basic.Costanti.OPENSPCOOP_IMPORT_ARCHIVE_MODE.toString();
				}else{
					this.importMode = importModes.get(0).toString();
				}
			}
			ImportMode archiveMode = null;
			if(this.importMode!=null){
				archiveMode = new ImportMode(this.importMode);
			}
			
			
			// import types
			String protocolloEffettivo = importModesMap.get(this.importMode);
			//System.out.println("PROTOCOLLO EFFETTIVO: "+protocolloEffettivo);
			List<ArchiveModeType> importTypes = null;
			if(this.importMode!=null){
				importTypes = importerUtils.getImportModeTypes(archiveMode, protocolloEffettivo);
			}
			this.importType = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO);
			if(this.importType!=null){
				// verifico che esista nei tyoes disponibili per il mode selezionato
				if(importTypes.contains(new ArchiveModeType(this.importType))==false){
					this.importType = null;
				}
			}
			if(this.importType==null){
				this.importType = importTypes.get(0).toString();
			}
			ArchiveModeType archiveModeType = null;
			if(this.importType!=null){
				archiveModeType = new ArchiveModeType(this.importType);
			}
			
			
			// validazione
			String tmpValidazioneDocumenti = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_VALIDAZIONE_DOCUMENTI);
			if(archiviHelper.isEditModeInProgress() && tmpValidazioneDocumenti==null){
				// primo accesso alla servlet
				this.validazioneDocumenti = true;
			}
			else{
				if(ServletUtils.isCheckBoxEnabled(tmpValidazioneDocumenti)){
					this.validazioneDocumenti = true;
				}
				else{
					this.validazioneDocumenti = false;
				}
			}
			
			// updateEnabled
			String tmpUpdateEnabled = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_UPDATE_ENABLED);
			if(archiviHelper.isEditModeInProgress() && tmpUpdateEnabled==null){
				// primo accesso alla servlet
				this.updateEnabled = false;
			}
			else{
				if(ServletUtils.isCheckBoxEnabled(tmpUpdateEnabled)){
					this.updateEnabled = true;
				}
				else{
					this.updateEnabled = false;
				}
			}
			
			// importPolicyConfig
			String tmpImportDeletePolicyConfig = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_DELETE_POLICY_CONFIG_ENABLED);
			if(archiviHelper.isEditModeInProgress() && tmpImportDeletePolicyConfig==null){
				// primo accesso alla servlet
				this.importDeletePolicyConfig = false;
			}
			else{
				if(ServletUtils.isCheckBoxEnabled(tmpImportDeletePolicyConfig)){
					this.importDeletePolicyConfig = true;
				}
				else{
					this.importDeletePolicyConfig = false;
				}
			}
			
			// importConfig
			String tmpImportConfig = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_CONFIG_ENABLED);
			if(archiviHelper.isEditModeInProgress() && tmpImportConfig==null){
				// primo accesso alla servlet
				this.importConfig = false;
			}
			else{
				if(ServletUtils.isCheckBoxEnabled(tmpImportConfig)){
					this.importConfig = true;
				}
				else{
					this.importConfig = false;
				}
			}
			
			
			// importInformationMissing: objectClass
			String tmpClass = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_CLASS);
			if(tmpClass!=null && !"".equals(tmpClass)){
				this.importInformationMissing_classObject = Class.forName(tmpClass);
				String absolutePath = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_FILE_PATH);
				if(absolutePath==null || "".equals(absolutePath)){
					throw new Exception("Parametro ["+ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_FILE_PATH
							+"] non trovato, nonostante sia presente il parametro ["+ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_CLASS
							+"] con valore: "+tmpClass);
				}
				this.importInformationMissing_object = importerUtils.readImportInformationMissingObjectFile(absolutePath, this.importInformationMissing_classObject);
			}else{
				this.importInformationMissing_classObject = null;
				this.importInformationMissing_object = null;
			}
			// importInformationMissing: soggetto
			this.importInformationMissing_soggettoInput = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_SOGGETTO_INPUT);
			// importInformationMissing: versione
			this.importInformationMissing_versioneInput = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_VERSIONE_INPUT);
			// Import Information Missing collection
			this.importInformationMissingCollectionFilePath = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_COLLECTION_FILE_PATH);
			// Import Information Missing object id
			this.importInformationMissingObjectId = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_ID);
			// importInformationMissing: servizi, modalita' di acquisizione
			this.importInformationMissing_modalitaAcquisizioneInformazioniProtocollo = 
					archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT);
			// importInformationMissing: portTypeImplemented
			this.importInformationMissing_portTypeImplementedInput = 
					archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_PORT_TYPE_IMPLEMENTED_INPUT);
			// importInformationMissing: accordoServizioParteComune
			this.importInformationMissing_accordoServizioParteComuneInput = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_SERVIZIO_PARTE_COMUNE_INPUT);
			// importInformationMissing: accordoCooperazione
			this.importInformationMissing_accordoCooperazioneInput = archiviHelper.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_COOPERAZIONE_INPUT);
						
			
	
			
					
			// File da caricare
			FormFile ff = importerUtils.readFormFile(this.filePath, form);
		
			archiviHelper.makeMenu();
			
			// Se severita == null, devo visualizzare la pagina con il pulsante
			if(archiviHelper.isEditModeInProgress() && 
					this.importInformationMissing_modalitaAcquisizioneInformazioniProtocollo == null){
				
				// setto la barra del titolo
				String nomeFunzionalita = ArchiviCostanti.LABEL_ARCHIVI_IMPORT;
				if(deleter){
					nomeFunzionalita = ArchiviCostanti.LABEL_ARCHIVI_ELIMINA;	
				}
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(nomeFunzionalita,null));

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(modalitaDataElement);
				
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				archiviHelper.addImportToDati(dati, this.validazioneDocumenti, this.updateEnabled,
						this.importDeletePolicyConfig, this.importConfig,
						showProtocols, protocolli, this.protocollo, 
						importModes, this.importMode, 
						importTypes, this.importType,
						deleter);
				
				pd.setDati(dati);

				pd.setLabelBottoneInvia(labelBottone);
				
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, ArchiviCostanti.OBJECT_NAME_ARCHIVI_IMPORT, 
						ArchiviCostanti.TIPO_OPERAZIONE_IMPORT);
			}

			// Controlli sui campi immessi
			boolean isOk = archiviHelper.importCheckData(ff, importerUtils, protocolloEffettivo, archiveMode, archiveModeType);
						
			// Controlli su eventuali import information missing
			boolean convertForGetException = false;
			if(isOk && this.importInformationMissingObjectId!=null){
				isOk = archiviHelper.importInformationMissingCheckData(this.importInformationMissing_soggettoInput, this.importInformationMissing_versioneInput,
						this.importInformationMissing_modalitaAcquisizioneInformazioniProtocollo, postBackElementName,
						this.importInformationMissing_portTypeImplementedInput,
						this.importInformationMissing_accordoServizioParteComuneInput,
						this.importInformationMissing_accordoCooperazioneInput);
				if(!isOk){
					convertForGetException = true;
				}
			}
			
			// ImportInformationMissing: portTypes per accordi di servizio parte comune (profilo, correlazione per le varie azioni)
			if(this.importInformationMissing_modalitaAcquisizioneInformazioniProtocollo!=null && 
					!"".equals(this.importInformationMissing_modalitaAcquisizioneInformazioniProtocollo)){
				try{
					this.importInformationMissing_portTypes = 
							archiviHelper.readInformazioniProtocolloServiziAzioni(this.importInformationMissing_modalitaAcquisizioneInformazioniProtocollo, 
									protocolloEffettivo, this.importInformationMissing_object);
				}catch(Exception e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
					convertForGetException = false; // forzo la non ricostruzione. C'e' un errore strutturale sul package
				}
			}
						
			// ImportInformationMissingInvocazioneServizio
			if(isOk){
				try{
					if(!archiviHelper.isEditModeInProgress()){
						this.importInformationMissing_invocazioneServizio = archiviHelper.readInvocazioneServizio();
					}
					else{
						this.importInformationMissing_invocazioneServizio = null;
					}
				}catch(Exception e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
					convertForGetException = false; // forzo la non ricostruzione. C'e' un errore strutturale sul package
				}
			}
			
			// ImportInformationMissingConnettore
			if(isOk){
				try{
					if(!archiviHelper.isEditModeInProgress()){
						this.importInformationMissing_connettore = archiviHelper.readConnettore();
					}
					else{
						this.importInformationMissing_connettore = null;
					}
				}catch(Exception e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
					convertForGetException = false; // forzo la non ricostruzione. C'e' un errore strutturale sul package
				}
			}
			
			// ImportInformationMissingCredenziali
			if(isOk){
				try{
					if(!archiviHelper.isEditModeInProgress()){
						this.importInformationMissing_credenziali = archiviHelper.readCredenzialiSA();
					}
					else{
						this.importInformationMissing_credenziali = null;
					}
				}catch(Exception e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
					convertForGetException = false; // forzo la non ricostruzione. C'e' un errore strutturale sul package
				}
			}
			
			// MapPlaceHolder			
			MapPlaceholder importInformationMissing_placeholder = null;
			if(isOk){
				try{
					if(!archiviHelper.isEditModeInProgress()){
						importInformationMissing_placeholder = archiviHelper.readPlaceholder();
						if(importInformationMissing_placeholder!=null){
							this.importInformationMissing_globalPlaceholder.putAll(importInformationMissing_placeholder);
						}
					}
				}catch(Exception e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
					convertForGetException = false; // forzo la non ricostruzione. C'e' un errore strutturale sul package
				}
			}
			
			// Requisiti
			HashMap<String, String> mapRequisitiInput = null;
			HashMap<String, String> mapRequisitiInputStepIncrement = null;
			if(isOk) {
				try{
					if(!archiviHelper.isEditModeInProgress()){
						mapRequisitiInput = archiviHelper.readRequisitiInput();
						mapRequisitiInputStepIncrement = archiviHelper.readRequisitiStepIncrementInput();
					}
				}catch(Exception e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
					convertForGetException = false; // forzo la non ricostruzione. C'e' un errore strutturale sul package
				}
			}
			
			// ImportInformationMissingCollection
			ImportInformationMissingCollection importInformationMissingCollection = null;	
			if(this.importInformationMissingCollectionFilePath!=null){
				importInformationMissingCollection = importerUtils.readImportInformationMissingCollectionFile(this.importInformationMissingCollectionFilePath);
			}
			if(isOk){
				importInformationMissingCollection = 
						importerUtils.updateInformationMissingCheckData(mapRequisitiInput, mapRequisitiInputStepIncrement,
								this.importInformationMissing_soggettoInput, 
								this.importInformationMissing_versioneInput, 
								this.importInformationMissing_portTypes,
								this.importInformationMissing_portTypeImplementedInput,
								this.importInformationMissing_accordoServizioParteComuneInput,
								this.importInformationMissing_accordoCooperazioneInput,
								this.importInformationMissing_invocazioneServizio,
								this.importInformationMissing_connettore,
								this.importInformationMissing_credenziali,
								importInformationMissing_placeholder,
								this.importInformationMissingCollectionFilePath, 
								this.importInformationMissingObjectId,
								importInformationMissingCollection);
			}
			
			
			// Step
			if(!archiviHelper.isEditModeInProgress() && isOk){
				this.step++;
			}
						
			// trasformazione in archivio openspcoop2
			Archive archive = null;
			if(isOk || convertForGetException){
				try{
					archive = archiviCore.convert(ff.getFileData(), archiveModeType, archiveMode, protocolloEffettivo,this.validazioneDocumenti,
							this.importInformationMissing_globalPlaceholder);
				}catch(Exception e){
					ControlStationCore.logError(e.getMessage(), e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}
			
			// check requisiti wizard
			if(isOk){
				if(archive!=null && archive.getInformationMissing()!=null && archive.getInformationMissing().getWizard()!=null){
					try{
						isOk = archiviHelper.checkRequiisitiWizard(archive.getInformationMissing().getWizard());
					}catch(Exception e){
						ControlStationCore.logError(e.getMessage(), e);
						pd.setMessage(e.getMessage());
						isOk = false;
					}
				}
			}
			
			// validazione
			ImportInformationMissingException importInformationMissingException = null;
			if(isOk || convertForGetException){
				try{
					archiviCore.validateArchive(archive, protocolloEffettivo, 
							this.validazioneDocumenti, importInformationMissingCollection, userLogin, deleter);
				}catch(ImportInformationMissingException e){
					//ControlStationCore.logError(importInformationMissingException.getMessage(), importInformationMissingException);
					importInformationMissingException = e;
					isOk = false;
				}
				catch(Exception e){
					ControlStationCore.logError(e.getMessage(), e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}
			
			// finalizeArchive
			if(isOk){
				if(archive!=null){
					try{
						archiviCore.finalize(archive, archiveModeType, archiveMode, protocolloEffettivo,this.validazioneDocumenti,
								this.importInformationMissing_globalPlaceholder);
					}catch(Exception e){
						ControlStationCore.logError(e.getMessage(), e);
						pd.setMessage(e.getMessage());
						isOk = false;
					}
				}
			}
						
			if (!isOk) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(ArchiviCostanti.LABEL_ARCHIVI_IMPORT,null));

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(modalitaDataElement);
				
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				if(importInformationMissingException!=null){
				
					// Indicazione se ho letto i dati sull'InvocazioneServizio, Connettore
					boolean readedDatiConnettori = this.importInformationMissing_invocazioneServizio!=null || 
							this.importInformationMissing_connettore!=null || this.importInformationMissing_credenziali!=null;
					
					Wizard wizard = null;
					if(archive.getInformationMissing()!=null){
						wizard = archive.getInformationMissing().getWizard();
					}
					
					archiviHelper.addImportInformationMissingToDati(dati, importerUtils, ff, 
							this.protocollo, this.importMode, protocolloEffettivo, this.importType, 
							this.validazioneDocumenti, this.updateEnabled,
							this.importDeletePolicyConfig, this.importConfig,
							importInformationMissingCollection, importInformationMissingException, 
							this.importInformationMissing_modalitaAcquisizioneInformazioniProtocollo,this.importInformationMissing_portTypes,
							protocolliForModes,readedDatiConnettori,
							wizard,this.step,
							deleter);
					
					pd.setLabelBottoneInvia(ArchiviCostanti.LABEL_ARCHIVI_AVANTI);
				}
				else{
					archiviHelper.addImportToDati(dati, this.validazioneDocumenti, this.updateEnabled,
							this.importDeletePolicyConfig, this.importConfig,
							showProtocols, protocolli, this.protocollo, 
							importModes, this.importMode, 
							importTypes, this.importType,
							deleter);
					
					pd.setLabelBottoneInvia(labelBottone);
				}
				
				pd.setDati(dati);

				if(pd.getMessage()!=null){
					if(importInformationMissingException==null){
						pd.setMessage(ArchiviCostanti.LABEL_IMPORT_ERROR_HEADER+StringEscapeUtils.escapeHtml(pd.getMessage()));
					}
				}
				
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ArchiviCostanti.OBJECT_NAME_ARCHIVI_IMPORT, 
						ArchiviCostanti.TIPO_OPERAZIONE_IMPORT);
			}
						
			

			
			
			
			Vector<DataElement> dati = new Vector<DataElement>();

			dati.addElement(ServletUtils.getDataElementForEditModeFinished());
			
			/*
			System.out.println("IMPORT COME PROTOCOL["+protocolloEffettivo+"] MODE["+this.importMode+"] TIPO["+
					this.importType+"]");
			
			if(importInformationMissingCollection!=null){
				System.out.println("SIZE COLLECTION ["+importInformationMissingCollection.size()+"]");
				Enumeration<String> keys = importInformationMissingCollection.keys();
				while (keys.hasMoreElements()) {
					String key = (String) keys.nextElement();
					ImportInformationMissing i = importInformationMissingCollection.get(key);
					System.out.println("COLLECTION ["+key
							+"] soggetti["+i.getSoggetto()+"] versione["+i.getVersione()
							+"] portTypes["+i.getPortTypes().size()+"]");
				}
			}
			*/
			
			String esito = null;
			if(deleter){
				esito = archiviCore.deleteArchive(archive, archiveMode, protocolloEffettivo, 
						userLogin, archiviHelper.smista(),
						this.importDeletePolicyConfig);
			}else{
				esito = archiviCore.importArchive(archive, archiveMode, protocolloEffettivo, 
						userLogin, archiviHelper.smista(), 
						this.updateEnabled, this.importDeletePolicyConfig, this.importConfig, nomePddOperativa,
						archiviHelper);
			}
									
			dati.addElement(modalitaDataElement);
			
			DataElement de = new DataElement();
			de.setLabel("Riepilogo Configurazioni Effettuate");
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel("");
			de.setType(DataElementType.TEXT_AREA_NO_EDIT);
			de.setValue(esito);
			de.setName("Resoconto");
			de.setRows(30);
			de.setCols(130);
			dati.addElement(de);
			
			if(esito!=null && !"".equals(esito)){
				
				// Salvo resoconto per fornirlo alla servlet
				session.setAttribute(ArchiviCostanti.PARAMETRO_DOWNLOAD_RESOCONTO_VALORE, esito);
				
				DataElement saveAs = new DataElement();
				saveAs.setValue(ArchiviCostanti.LABEL_DOWNLOAD);
				saveAs.setType(DataElementType.LINK);
				saveAs.setUrl(ArchiviCostanti.SERVLET_NAME_RESOCONTO_EXPORT);
				saveAs.setDisabilitaAjaxStatus();
				dati.add(saveAs);
			}
			
			pd.disableEditMode();
	
			pd.setDati(dati);
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ArchiviCostanti.OBJECT_NAME_ARCHIVI_IMPORT, 
					ArchiviCostanti.TIPO_OPERAZIONE_IMPORT);
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ArchiviCostanti.OBJECT_NAME_ARCHIVI_IMPORT,
					ArchiviCostanti.TIPO_OPERAZIONE_IMPORT);
		}
		
	}
	
}
