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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.basic.archive.BasicArchive;
import org.openspcoop2.protocol.engine.BasicProtocolFactory;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.archive.ImportInformationMissing;
import org.openspcoop2.protocol.engine.archive.ImportInformationMissingCollection;
import org.openspcoop2.protocol.engine.archive.ImportInformationMissingException;
import org.openspcoop2.protocol.engine.archive.ImporterInformationMissingUtils;
import org.openspcoop2.protocol.information_missing.Default;
import org.openspcoop2.protocol.information_missing.Description;
import org.openspcoop2.protocol.information_missing.DescriptionType;
import org.openspcoop2.protocol.information_missing.Input;
import org.openspcoop2.protocol.information_missing.Proprieta;
import org.openspcoop2.protocol.information_missing.ProprietaDefault;
import org.openspcoop2.protocol.information_missing.ProprietaRequisitoInput;
import org.openspcoop2.protocol.information_missing.RequisitoInput;
import org.openspcoop2.protocol.information_missing.Wizard;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.archive.ArchiveCascadeConfiguration;
import org.openspcoop2.protocol.sdk.archive.ArchiveMode;
import org.openspcoop2.protocol.sdk.archive.ArchiveModeType;
import org.openspcoop2.protocol.sdk.archive.ExportMode;
import org.openspcoop2.protocol.sdk.archive.ImportMode;
import org.openspcoop2.protocol.sdk.archive.MapPlaceholder;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.protocol.sdk.validator.ValidazioneResult;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.costanti.TipologiaConnettori;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.utenti.UtentiCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.InterfaceType;
import org.openspcoop2.web.lib.users.dao.User;

/**
 * ArchiviHelper
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiviHelper extends ServiziApplicativiHelper {

	public ArchiviHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
	}
	public ArchiviHelper(ControlStationCore core, HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(core, request, pd,  session);
	}


	public void addExportToDati(Vector<DataElement> dati,
			List<String> protocolliSelectList,String protocollo,
			List<ExportMode> exportModes,String exportMode,
			ArchiveType servletSourceExport, String objToExport, 
			String cascadePolicyConfig, String cascade,String configurazioneType,
			String cascadePdd,String cascadeRuoli,String cascadeScope, String cascadeSoggetti,
			String cascadeServiziApplicativi, String cascadePorteDelegate, String cascadePorteApplicative,
			String cascadeAc, String cascadeAspc, String cascadeAsc, String cascadeAsps, String cascadeFruizioni) throws Exception{
		
		DataElement dataElement = new DataElement();
		dataElement.setLabel(ArchiviCostanti.LABEL_ARCHIVI_EXPORT);
		dataElement.setType(DataElementType.TITLE);
		dati.add(dataElement);
		
		DataElement de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_PROTOCOLLO);
		// Non sembra necessario far vedere la lista dei protocolli anche se sono maggiore di uno.
		// Tanto poi gli export modes sono sempre tutti quelli SOLO compatibilit con TUTTI i protocolli degli oggetti selezionati.
//		if(protocolliSelectList.size()>=2){
//			de.setType(DataElementType.SELECT);
//			de.setValues(protocolliSelectList.toArray(new String[1]));
//			de.setLabels(protocolliSelectList.toArray(new String[1]));
//			de.setSelected(protocollo);
//		}else{
		de.setType(DataElementType.HIDDEN);
		de.setValue(protocollo);
//		}
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO);
		de.setSize(this.getSize());
		de.setPostBack(true);
		dati.addElement(de);
		
		de = new DataElement();
		de.setType(DataElementType.TEXT);
		de.setLabel(org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO);
		boolean tutti_protocolli = false;
		if(protocolliSelectList.size()<=1){
			de.setValue(this.getLabelProtocollo(protocollo));
		}
		else {
			de.setValue(UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL);
			tutti_protocolli = true;
		}
		dati.addElement(de);
		
		if(!tutti_protocolli && this.archiviCore.isMultitenant()) {
			de = new DataElement();
			de.setType(DataElementType.TEXT);
			de.setLabel(SoggettiCostanti.LABEL_SOGGETTO);
			if(this.isSoggettoMultitenantSelezionato()){
				IDSoggetto idSoggettoSelezionato = this.soggettiCore.convertSoggettoSelezionatoToID(this.getSoggettoMultitenantSelezionato());
				de.setValue(this.getLabelNomeSoggetto(idSoggettoSelezionato));
			}
			else {
				de.setValue(UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL);
			}
			dati.addElement(de);
		}

		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_TIPOLOGIA_ARCHIVIO);
		if(exportModes.size()>1){
			List<String> exportMode_tmp = new ArrayList<String>();
			for (ExportMode exp : exportModes) {
				exportMode_tmp.add(exp.toString());
			}
			de.setType(DataElementType.SELECT);
			de.setValues(exportMode_tmp.toArray(new String[1]));
			de.setLabels(exportMode_tmp.toArray(new String[1]));
			de.setSelected(exportMode);
		}else{
			de.setType(DataElementType.HIDDEN);
			de.setValue(exportMode);
		}
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPOLOGIA_ARCHIVIO);
		de.setSize(this.getSize());
		de.setPostBack(true);
		dati.addElement(de);
					
		if(ArchiveType.CONFIGURAZIONE.equals(servletSourceExport)){
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_EXPORT_TIPO_DUMP);
			de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_TIPO_DUMP);
			if(this.archiviCore.isExportArchive_configurazione_soloDumpCompleto() || 
					!org.openspcoop2.protocol.basic.Costanti.OPENSPCOOP_ARCHIVE_MODE_TYPE.getType().equals(exportMode)){
				// opzioni standard
				de.setType(DataElementType.HIDDEN);
				de.setValue(configurazioneType);
			}
			else{
				// opzioni di export avanzate
				de.setType(DataElementType.SELECT);
				de.setLabels(ArchiviCostanti.PARAMETRO_LABEL_ARCHIVI_EXPORT_TIPO_DUMP);
				de.setValues(ArchiviCostanti.PARAMETRO_VALORI_ARCHIVI_EXPORT_TIPO_DUMP);
				de.setSelected(configurazioneType);
				de.setPostBack(true); // serve solo a poter riesportare senza dover rientrare nella sezione configurazione una volta esportato
			}
			dati.addElement(de);
		}
		
		boolean cascadeEnabled = false;
		ArchiveCascadeConfiguration cascadeConfig = null;
		if(!ArchiveType.CONFIGURAZIONE.equals(servletSourceExport)){
			cascadeEnabled = this.archiviCore.isCascadeEnabled(exportModes, exportMode);
			cascadeConfig = this.archiviCore.getCascadeConfig(exportModes, exportMode);
			if(cascadeEnabled){
				if(configurazioneType!=null && ArchiveType.CONFIGURAZIONE.equals(ArchiveType.valueOf(configurazioneType))){
					cascadeEnabled = false; // nella configurazione non ha senso usare il cascade	
				}
			}
		}
		
		de = new DataElement();
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_POLICY_CONFIG);
		if(cascadeEnabled){
			de.setType(DataElementType.CHECKBOX);
			de.setPostBack(true);
		}
		else
			de.setType(DataElementType.HIDDEN);
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_POLICY_CONFIG_LEFT);
		de.setLabelRight(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_POLICY_CONFIG_RIGHT);
		de.setSelected(ServletUtils.isCheckBoxEnabled(cascadePolicyConfig));
		de.setValue(cascadePolicyConfig);
		dati.addElement(de);
		
		de = new DataElement();
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE);
		if(cascadeEnabled){
			de.setType(DataElementType.CHECKBOX);
			de.setPostBack(true);
		}
		else
			de.setType(DataElementType.HIDDEN);
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_LEFT);
		de.setLabelRight(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_RIGHT);
		de.setSelected(ServletUtils.isCheckBoxEnabled(cascade));
		de.setValue(cascade);
		dati.addElement(de);
		
		if(!this.archiviCore.isExportArchive_servizi_standard() && 
				ServletUtils.isCheckBoxEnabled(cascade) &&
				cascadeEnabled){
			
			de = new DataElement();
			de.setType(DataElementType.TITLE);
			de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_SEZIONE);
			dati.addElement(de);
			
			if(cascadeConfig.isCascadePdd()){
				de = new DataElement();
				de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_PDD);
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_PDD);
				de.setType(DataElementType.CHECKBOX);
				de.setPostBack(true); // serve solo a poter riesportare senza dover rientrare nella sezione configurazione una volta esportato
				de.setSelected(cascadePdd);
				dati.addElement(de);
			}
			
			if(cascadeConfig.isCascadeRuoli()){
				de = new DataElement();
				de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_RUOLI);
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_RUOLI);
				de.setType(DataElementType.CHECKBOX);
				de.setPostBack(true); // serve solo a poter riesportare senza dover rientrare nella sezione configurazione una volta esportato
				de.setSelected(cascadeRuoli);
				dati.addElement(de);
			}
			
			if(cascadeConfig.isCascadeScope()){
				de = new DataElement();
				de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_SCOPE);
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_SCOPE);
				de.setType(DataElementType.CHECKBOX);
				de.setPostBack(true); // serve solo a poter riesportare senza dover rientrare nella sezione configurazione una volta esportato
				de.setSelected(cascadeScope);
				dati.addElement(de);
			}
			
			if(cascadeConfig.isCascadeSoggetti()){
				de = new DataElement();
				de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_SOGGETTI);
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_SOGGETTI);
				de.setType(DataElementType.CHECKBOX);
				de.setPostBack(true); // serve solo a poter riesportare senza dover rientrare nella sezione configurazione una volta esportato
				de.setSelected(cascadeSoggetti);
				dati.addElement(de);
			}
			
			if(cascadeConfig.isCascadeServiziApplicativi()){
				de = new DataElement();
				de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_SERVIZI_APPLICATIVI);
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_SERVIZI_APPLICATIVI);
				de.setType(DataElementType.CHECKBOX);
				de.setPostBack(true); // serve solo a poter riesportare senza dover rientrare nella sezione configurazione una volta esportato
				de.setSelected(cascadeServiziApplicativi);
				dati.addElement(de);
			}
			if(cascadeConfig.isCascadePorteDelegate()){
				de = new DataElement();
				de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_PORTE_DELEGATE);
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_PORTE_DELEGATE);
				de.setType(DataElementType.CHECKBOX);
				de.setPostBack(true); // serve solo a poter riesportare senza dover rientrare nella sezione configurazione una volta esportato
				de.setSelected(cascadePorteDelegate);
				dati.addElement(de);
			}
			if(cascadeConfig.isCascadePorteApplicative()){
				de = new DataElement();
				de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_PORTE_APPLICATIVE);
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_PORTE_APPLICATIVE);
				de.setType(DataElementType.CHECKBOX);
				de.setPostBack(true); // serve solo a poter riesportare senza dover rientrare nella sezione configurazione una volta esportato
				de.setSelected(cascadePorteApplicative);
				dati.addElement(de);
			}
			
			if(cascadeConfig.isCascadeAccordoCooperazione()){
				de = new DataElement();
				de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_COOPERAZIONE);
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_COOPERAZIONE);
				de.setType(DataElementType.CHECKBOX);
				de.setPostBack(true); // serve solo a poter riesportare senza dover rientrare nella sezione configurazione una volta esportato
				de.setSelected(cascadeAc);
				dati.addElement(de);
			}
			if(cascadeConfig.isCascadeAccordoServizioParteComune()){
				de = new DataElement();
				de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_PARTE_COMUNE);
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_PARTE_COMUNE);
				de.setType(DataElementType.CHECKBOX);
				de.setPostBack(true); // serve solo a poter riesportare senza dover rientrare nella sezione configurazione una volta esportato
				de.setSelected(cascadeAspc);
				dati.addElement(de);
			}
			if(cascadeConfig.isCascadeAccordoServizioComposto()){
				de = new DataElement();
				de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_COMPOSTO);
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_COMPOSTO);
				de.setType(DataElementType.CHECKBOX);
				de.setPostBack(true); // serve solo a poter riesportare senza dover rientrare nella sezione configurazione una volta esportato
				de.setSelected(cascadeAsc);
				dati.addElement(de);
			}
			if(cascadeConfig.isCascadeAccordoServizioParteSpecifica()){
				de = new DataElement();
				de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_PARTE_SPECIFICA);
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_PARTE_SPECIFICA);
				de.setType(DataElementType.CHECKBOX);
				de.setPostBack(true); // serve solo a poter riesportare senza dover rientrare nella sezione configurazione una volta esportato
				de.setSelected(cascadeAsps);
				dati.addElement(de);
			}
			if(cascadeConfig.isCascadeFruizioni()){
				de = new DataElement();
				de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_FRUIZIONI);
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE_FRUIZIONI);
				de.setType(DataElementType.CHECKBOX);
				de.setPostBack(true); // serve solo a poter riesportare senza dover rientrare nella sezione configurazione una volta esportato
				de.setSelected(cascadeFruizioni);
				dati.addElement(de);
			}
		}	
		
		
		de = new DataElement();
		de.setType(DataElementType.HIDDEN);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_TIPO);
		de.setValue(servletSourceExport.toString());
		dati.addElement(de);
		
		de = new DataElement();
		de.setType(DataElementType.HIDDEN);
		de.setName(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
		de.setValue(objToExport);
		dati.addElement(de);
	}
	

	public boolean importCheckData(FormFile ff,ImporterUtils importerUtils,String protocollo,ArchiveMode importMode,ArchiveModeType importType) throws Exception {

		// check estensione
		if(ff.getFileName().contains(".")==false){
			this.pd.setMessage("Estensione di file non valida");
			return false;
		}
		String ext = ff.getFileName().substring(ff.getFileName().lastIndexOf('.')+1, ff.getFileName().length());
		List<String> validExts = importerUtils.getValidExtensions(importType, importMode, protocollo);
		if(validExts.contains(ext)==false){
			this.pd.setMessage("Estensione di file ["+ext+"] non supportata (tipologia-archivio:"+importMode+" ,tipo:"+importType+"). Sono supportati i seguenti formati: "+validExts.toString());
			return false;
		}

		return true;

	}
	
	public boolean checkRequiisitiWizard(Wizard wizard){
		
		if(wizard.getRequisiti()==null){
			return true;
		}
		
		StringBuilder bf = new StringBuilder();
		for (int i = 0; i < wizard.getRequisiti().sizeProtocolloList(); i++) {
			String protocollo = wizard.getRequisiti().getProtocollo(i).getNome();
			try{
				IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
				pf.toString();
			}catch(Exception e){
				this.log.error("Requisito["+i+"] protocollo ["+protocollo+"] non trovato: "+e.getMessage(),e);
				if(bf.length()>0){
					bf.append(",");
				}
				bf.append(protocollo);
			}
		}
		if(bf.length()>0){
			this.pd.setMessage("I seguenti plugin di protocollo, richiesti dall'archivio selezionato, non risultano essere installati: "+bf.toString());
			return false;
		}
		
		
		return true;
		
	}
	
	public boolean importInformationMissingCheckData(String importInformationMissing_soggettoInput,String importInformationMissing_versioneInput,
			String importInformationMissing_modalitaAcquisizioneInformazioniProtocollo, String postBackElementName,
			String importInformationMissing_portTypeImplementedInput,
			String importInformationMissing_accordoServizioParteComuneInput,
			String importInformationMissing_accordoCooperazioneInput) throws Exception{
				
		// check input
		int index = 0;
		String pRequisitoHidden = this.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_REQUISITO_INPUT_PROPRIETA_PREFIX_HIDDEN+index);
		while(pRequisitoHidden!=null || "".equals(pRequisitoHidden)){
			String pValue = this.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_REQUISITO_INPUT_PROPRIETA_PREFIX_VALUE+index);
			if(pValue==null || "".equals(pValue)){
				if("true".equalsIgnoreCase(pRequisitoHidden)) {
					this.pd.setMessage("Deve essere indicato un valore in tutti i campi obbligatori");
					return false;
				}
			}
				
			index++;
			pRequisitoHidden = this.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_REQUISITO_INPUT_PROPRIETA_PREFIX_HIDDEN+index);
		}
		
		// importInformationMissing: soggetto
		if(importInformationMissing_soggettoInput!=null && 
				!"".equals(importInformationMissing_soggettoInput)) {
			
			if(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_SOGGETTO_INPUT_UNDEFINDED.equals(importInformationMissing_soggettoInput)){
				this.pd.setMessage("Deve essere selezionato un soggetto tra quelli indicati");
				return false;
			}
			
			String[]splitSoggetto = importInformationMissing_soggettoInput.split("/");
			if(splitSoggetto==null || splitSoggetto.length!=2){
				this.pd.setMessage("Il soggetto indicato ["+importInformationMissing_soggettoInput+"] non risulta valido");
				return false;
			}
		}
		
		
		// importInformationMissing: versione
		if(importInformationMissing_versioneInput!=null){
			try{
				Integer.parseInt(importInformationMissing_versioneInput);
			}catch(Exception e){
				this.pd.setMessage("Versione indicata ["+importInformationMissing_versioneInput+"] non valida: "+e.getMessage());
				return false;
			}
		}
		
		// check input
		index = 0;
		String pHidden = this.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INPUT_PROPRIETA_PREFIX_HIDDEN+index);
		while(pHidden!=null || "".equals(pHidden)){
			String pValue = this.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INPUT_PROPRIETA_PREFIX_VALUE+index);
			if(pValue==null || "".equals(pValue)){
				this.pd.setMessage("Deve essere indicato un valore in tutti i campi");
				return false;
			}
				
			index++;
			pHidden = this.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INPUT_PROPRIETA_PREFIX_HIDDEN+index);
		}
		
		// check Informazioni configurazione profilo servizi
		if(ArchiviCostanti.LABEL_IMPORT_ERROR_INFORMAZIONI_PROTOCOLLO_MANCANTI_RICONOSCIMENTO_USER_INPUT.equals(importInformationMissing_modalitaAcquisizioneInformazioniProtocollo)){
			
			int contatoreServizio = 1;
			String servizioParam = ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_IS_DEFINED+contatoreServizio;
			String serviziTmpInput = this.getParameter(servizioParam);
			while(serviziTmpInput!=null && !"".equals(serviziTmpInput)){
			
				servizioParam = ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE+contatoreServizio;
				String nomeServizio = this.getParameter(servizioParam);
				if(nomeServizio==null || "".equals(nomeServizio)){
					this.pd.setMessage("Deve essere indicato il nome del servizio da associare ad ogni port-type esistente");
					return false;
				}
				
				int contatoreAzione = 1;
				String azioniTmpInput = this.getParameter(servizioParam+
						ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_IS_DEFINED+contatoreAzione);
				while(azioniTmpInput!=null && !"".equals(azioniTmpInput)){
					
					String nomeAzione = this.getParameter(servizioParam+
							ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION+contatoreAzione);
					if(nomeAzione==null || "".equals(nomeAzione)){
						this.pd.setMessage("Deve essere indicato il nome di ogni azione da associare alle operations dei port-types");
						return false;
					}
					
					String nomeProfilo = this.getParameter(servizioParam+
							ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_PROFILO+contatoreAzione);
					if(nomeProfilo==null || "".equals(nomeProfilo)){
						this.pd.setMessage("Deve essere indicato un profilo di collaborazione per ogni operation dei port-types");
						return false;
					}
					if(!CostantiRegistroServizi.ONEWAY.equals(nomeProfilo) &&
							!CostantiRegistroServizi.SINCRONO.equals(nomeProfilo) &&
							!CostantiRegistroServizi.ASINCRONO_ASIMMETRICO.equals(nomeProfilo) &&
							!CostantiRegistroServizi.ASINCRONO_SIMMETRICO.equals(nomeProfilo)){
						this.pd.setMessage("Deve essere indicato un profilo di collaborazione valido per ogni operation dei port-types. Trovato un profilo non valido: "+nomeProfilo);
						return false;
						
					}
					
					// NextAzione
					contatoreAzione++;
					azioniTmpInput = this.getParameter(servizioParam+
							ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_IS_DEFINED+contatoreAzione);
				}
				
				contatoreServizio++;
				servizioParam = ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_IS_DEFINED+contatoreServizio;
				serviziTmpInput = this.getParameter(servizioParam);
				
			}
			
		}
		
		if(postBackElementName!=null){
			if(postBackElementName.startsWith(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE) && 
					postBackElementName.contains(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_PROFILO)){
				// cambiato il profilo di collaborazione di una azione
				// Devo solo rigenerare le informazioni senza pero' fornire alcun messaggio
				return false;
			}
			else if(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT.equals(postBackElementName)){
				// cambiata la modalita' di acquisizione delle informazioni
				// Devo solo rigenerare le informazioni senza pero' fornire alcun messaggio
				return false;
			}
		}
		
		
		// Check portType implemented
		if(importInformationMissing_portTypeImplementedInput!=null && 
				!"".equals(importInformationMissing_portTypeImplementedInput)) {
			
			if(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_SOGGETTO_INPUT_UNDEFINDED.equals(importInformationMissing_portTypeImplementedInput)){
				this.pd.setMessage("Deve essere selezionato un servizio, da implementare, tra quelli indicati");
				return false;
			}
			
		}
		
		
		// importInformationMissing: accordoServizioParteComune
		if(importInformationMissing_accordoServizioParteComuneInput!=null && 
				!"".equals(importInformationMissing_accordoServizioParteComuneInput)) {
			
			if(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_INPUT_UNDEFINDED.equals(importInformationMissing_accordoServizioParteComuneInput)){
				this.pd.setMessage("Deve essere selezionato un'accordo tra quelli indicati");
				return false;
			}
			
			try{
				IDAccordoFactory.getInstance().getIDAccordoFromUri(importInformationMissing_accordoServizioParteComuneInput);
			}catch(Exception e){
				this.pd.setMessage("L'accordo indicato ["+importInformationMissing_accordoServizioParteComuneInput+"] non risulta valido");
				return false;	
			}
		}
		
		// importInformationMissing: accordoCooperazione
		if(importInformationMissing_accordoCooperazioneInput!=null && 
				!"".equals(importInformationMissing_accordoCooperazioneInput)) {
			
			if(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_INPUT_UNDEFINDED.equals(importInformationMissing_accordoCooperazioneInput)){
				this.pd.setMessage("Deve essere selezionato un'accordo tra quelli indicati");
				return false;
			}
			
			try{
				IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromUri(importInformationMissing_accordoCooperazioneInput);
			}catch(Exception e){
				this.pd.setMessage("L'accordo indicato ["+importInformationMissing_accordoCooperazioneInput+"] non risulta valido");
				return false;	
			}
		}
		
		// ImportInformationMissingInvocazioneServizio
		if(!this.isEditModeInProgress()){
			try{
				InvocazioneServizio is = this.readInvocazioneServizio();
				if(is!=null){
					ServiziApplicativiHelper saHelper = new ServiziApplicativiHelper(this.request, this.pd, this.session);
					
					String endpointtype = this.readEndPointType();
					List<ExtendedConnettore> listExtendedConnettore = 
							ServletExtendedConnettoreUtils.getExtendedConnettore(is.getConnettore(), ConnettoreServletType.WIZARD_CONFIG, this, false, endpointtype);
					
					boolean isOk = saHelper.servizioApplicativoEndPointCheckData(null, listExtendedConnettore, null);
					if (!isOk) {
						return false;
					}
					
					if(StatoFunzionalita.ABILITATO.equals(is.getGetMessage())){
						isOk = this.credenzialiCheckData(TipoOperazione.ADD, false, this.saCore.isApplicativiPasswordEncryptEnabled(), this.saCore.getApplicativiPasswordVerifier());
						if (!isOk) {
							return false;
						}
					}
				}
			}catch(Exception e){
				String message = "Rilevato ErroreGenerico durante la lettura dei dati di invocazione servizio relativo al servizio applicativo: "+e.getMessage();
				this.log.error(message,e);
				this.pd.setMessage(message);
				return false;	
			}
		}
		
		// ImportInformationMissingConnettore
		if(!this.isEditModeInProgress()){
			try{
				org.openspcoop2.core.registry.Connettore connettore = this.readConnettore();
				if(connettore!=null){
					
					String endpointtype = this.readEndPointType();
					List<ExtendedConnettore> listExtendedConnettore = 
							ServletExtendedConnettoreUtils.getExtendedConnettore(connettore, ConnettoreServletType.WIZARD_REGISTRY, this, false, endpointtype);
					
					boolean isOk = this.endPointCheckData(null, false, listExtendedConnettore);
					if (!isOk) {
						return false;
					}
				}
			}catch(Exception e){
				String message = "Rilevato ErroreGenerico durante la lettura dei dati del connettore: "+e.getMessage();
				this.log.error(message,e);
				this.pd.setMessage(message);
				return false;	
			}
		}
		
		// ImportInformationMissingCredenziali
		if(!this.isEditModeInProgress()){
			try{
				Credenziali credenziali = this.readCredenzialiSA();
				if(credenziali!=null){
					ConnettoriHelper connettoriHelper = new ConnettoriHelper(this.request, this.pd, this.session);
					boolean isOk = connettoriHelper.credenzialiCheckData(TipoOperazione.ADD, false, this.saCore.isApplicativiPasswordEncryptEnabled(), this.saCore.getApplicativiPasswordVerifier());
					if (!isOk) {
						return false;
					}
				}
			}catch(Exception e){
				String message = "Rilevato ErroreGenerico durante la lettura dei dati delle credenziali di accesso alla PdD del servizio applicativo: "+e.getMessage();
				this.log.error(message,e);
				this.pd.setMessage(message);
				return false;	
			}
		}
		
		return true;
	}

	public void addImportToDati(Vector<DataElement> dati,
			boolean validazioneDocumenti,boolean updateEnabled,
			boolean importDeletePolicyConfig, boolean importConfig,
			boolean showProtocols, List<String> protocolliSelectList,String protocollo,
			List<ImportMode> importModes,String importMode,
			List<ArchiveModeType> importTypes,String importType,
			boolean deleter) throws Exception{

		DataElement dataElement = new DataElement();
		if(deleter){
			dataElement.setLabel(ArchiviCostanti.LABEL_ARCHIVI_ELIMINA);
		}
		else{
			dataElement.setLabel(ArchiviCostanti.LABEL_ARCHIVI_IMPORT);
		}
		dataElement.setType(DataElementType.TITLE);
		dati.add(dataElement);
		
		DataElement de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_PROTOCOLLO);
		if(showProtocols && protocolliSelectList.size()>2){
			de.setType(DataElementType.SELECT);
			de.setValues(protocolliSelectList.toArray(new String[1]));
			List<String> protocolliSelectListLabels = new ArrayList<>();
			for (String p : protocolliSelectList) {
				if(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO_UNDEFINDED.equals(p)) {
					protocolliSelectListLabels.add(UtentiCostanti.LABEL_PARAMETRO_MODALITA_ALL);
				}
				else {
					protocolliSelectListLabels.add(this.getLabelProtocollo(p));
				}
			}
			de.setLabels(protocolliSelectListLabels);
			de.setSelected(protocollo);
		}else{
			de.setType(DataElementType.HIDDEN);
			de.setValue(protocollo);
		}
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO);
		de.setSize(this.getSize());
		de.setPostBack(true);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_TIPOLOGIA_ARCHIVIO);
		if(importModes.size()>1){
			// Lo vediamo sempre anche se solo con un valore, poiche' vogliamo vedere govlet e fuori dalla select list non e' bello graficamente.
			List<String> tmpArchivi = new ArrayList<String>();
			for (ImportMode imp : importModes) {
				tmpArchivi.add(imp.toString());
			}
			de.setType(DataElementType.SELECT);
			de.setValues(tmpArchivi.toArray(new String[1]));
			de.setLabels(tmpArchivi.toArray(new String[1]));
			de.setSelected(importMode);
		}else{
			de.setType(DataElementType.HIDDEN);
			//de.setType(DataElementType.TEXT);
			de.setValue(importMode);
		}
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPOLOGIA_ARCHIVIO);
		de.setSize(this.getSize());
		if(importModes.size()>1){
			de.setPostBack(true);
		}
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_TIPO);
		if(importTypes.size()>1){
			List<String> tmp = new ArrayList<String>();
			for (ArchiveModeType type : importTypes) {
				tmp.add(type.toString());
			}
			de.setType(DataElementType.SELECT);
			de.setValues(tmp.toArray(new String[1]));
			de.setLabels(tmp.toArray(new String[1]));
			de.setSelected(importType);
		}else{
			de.setType(DataElementType.HIDDEN);
			de.setValue(importType);
		}
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO);
		de.setSize(this.getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_VALIDAZIONE_DOCUMENTI_LEFT);
		de.setLabelRight(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_VALIDAZIONE_DOCUMENTI_RIGHT);
		de.setValue(""+validazioneDocumenti);
		if (this.isModalitaAvanzata()) {
			de.setType(DataElementType.CHECKBOX);
			de.setSelected(validazioneDocumenti);
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_VALIDAZIONE_DOCUMENTI);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_UPDATE_ENABLED_LEFT);
		de.setLabelRight(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_UPDATE_ENABLED_RIGHT);
		de.setValue(""+updateEnabled);
		//if (!InterfaceType.STANDARD.equals(user.getInterfaceType())) {
		if(deleter){
			de.setType(DataElementType.HIDDEN);
		}else{
			de.setType(DataElementType.CHECKBOX);
			de.setSelected(updateEnabled);
		}
		//}
		//else{
		//	de.setType("hidden");
		//}
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_UPDATE_ENABLED);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(deleter ? ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_DELETE_POLICY_CONFIG_LEFT : ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_POLICY_CONFIG_LEFT);
		de.setLabelRight(deleter ? ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_DELETE_POLICY_CONFIG_RIGHT : ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_POLICY_CONFIG_RIGHT);
		de.setValue(""+importDeletePolicyConfig);
		//if (!InterfaceType.STANDARD.equals(user.getInterfaceType())) {
//		if(deleter){
//			de.setType(DataElementType.HIDDEN);
//		}else{
			de.setType(DataElementType.CHECKBOX);
			de.setSelected(importDeletePolicyConfig);
//		}
		//}
		//else{
		//	de.setType("hidden");
		//}
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_DELETE_POLICY_CONFIG_ENABLED);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_CONFIG_LEFT);
		de.setLabelRight(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_CONFIG_RIGHT);
		de.setValue(""+importConfig);
		//if (!InterfaceType.STANDARD.equals(user.getInterfaceType())) {
		if(deleter){
			de.setType(DataElementType.HIDDEN);
		}else{
			de.setType(DataElementType.CHECKBOX);
			de.setSelected(importConfig);
		}
		//}
		//else{
		//	de.setType("hidden");
		//}
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_CONFIG_ENABLED);
		de.setSize(this.getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setValue("");
		de.setType(DataElementType.FILE);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_PACKAGE_FILE);
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_PACKAGE_FILE);
		de.setSize(this.getSize());
		dati.addElement(de);

	}

	public List<PortType> readInformazioniProtocolloServiziAzioni(String modalitaAcquisizione,String protocollo,Object object) throws Exception{
		
		AccordoServizioParteComune aspc = null;
		if(object==null){
			throw new Exception("Accordo non fornito come parametro 'object'");
		}
		if(!(object instanceof AccordoServizioParteComune)){
			throw new Exception("Accordo fornito come parametro 'object' non e' di tipo "+AccordoServizioParteComune.class.getName());
		}
		aspc = (AccordoServizioParteComune) object;
	
		// rimuovo eventuali port types	(sara' il protocollo a inserirli)
		while(aspc.sizePortTypeList()>0){
			aspc.removePortType(0);
		}
		
		// Uso il protocollo per riempire il package
		IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
		protocolFactory.createArchive().setProtocolInfo(aspc);
		// NOTA il codice sopra lo devo eseguire sempre e comunque per validare il WSDL.
		// Se il WSDL e' corrotto (es. wsdl senza port type viene sollevata una eccezione)
		
		if(ArchiviCostanti.LABEL_IMPORT_ERROR_INFORMAZIONI_PROTOCOLLO_MANCANTI_RICONOSCIMENTO_WSDL_MODE.equals(modalitaAcquisizione)){
		
			return aspc.getPortTypeList();
		}
		else{
			int contatoreServizio = 1;
			String servizioParam = ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE+contatoreServizio;
			String serviziTmpInput = this.getParameter(servizioParam);
			List<PortType> portTypes = new Vector<PortType>();
			while(serviziTmpInput!=null && !"".equals(serviziTmpInput)){
				//System.out.println("TROVATO SERVIZIO ["+contatoreServizio+"] = "+serviziTmpInput);
				
				PortType ptOpenSPCoop = new PortType();
				ptOpenSPCoop.setNome(serviziTmpInput);
				ptOpenSPCoop.setProfiloCollaborazione(ProfiloCollaborazione.toEnumConstant(AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_SINCRONO));
				ptOpenSPCoop.setProfiloPT(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
				ptOpenSPCoop.setFiltroDuplicati(CostantiRegistroServizi.ABILITATO);		
				
				// Azioni
				int contatoreAzione = 1;
				String azioniTmpInput = this.getParameter(servizioParam+
						ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION+contatoreAzione);
				while(azioniTmpInput!=null && !"".equals(azioniTmpInput)){
					//System.out.println("TROVATO AZIONE["+contatoreAzione+"] PER SERVIZIO ["+contatoreServizio+"] = "+azioniTmpInput);
					Operation operationOpenSPCoop = new Operation();
					operationOpenSPCoop.setNome(azioniTmpInput);
					operationOpenSPCoop.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
					operationOpenSPCoop.setFiltroDuplicati(CostantiRegistroServizi.ABILITATO);
					
					// profiloCollaborazione
					String profiliCollaborazioneTmpInput = this.getParameter(servizioParam+
							ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_PROFILO+contatoreAzione);
					operationOpenSPCoop.setProfiloCollaborazione(ProfiloCollaborazione.toEnumConstant(profiliCollaborazioneTmpInput));
					
					// correlazione
					if(AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO.equals(profiliCollaborazioneTmpInput) || 
							AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO.equals(profiliCollaborazioneTmpInput)){
						String servizioAzioneCorrelataTmpInput = this.getParameter(servizioParam+
								ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_SERVIZIO_CORRELATO+contatoreAzione);
						if(servizioAzioneCorrelataTmpInput!=null && !"".equals(servizioAzioneCorrelataTmpInput)){
							//System.out.println("CORRELAZIONE ["+operationOpenSPCoop.getNome()+"] ["+azioniCorrelataTmpInput+"]");
							operationOpenSPCoop.setCorrelataServizio(servizioAzioneCorrelataTmpInput);
						}
						String azioniCorrelataTmpInput = this.getParameter(servizioParam+
								ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_CORRELATA+contatoreAzione);
						if(azioniCorrelataTmpInput!=null && !"".equals(azioniCorrelataTmpInput)){
							//System.out.println("CORRELAZIONE ["+operationOpenSPCoop.getNome()+"] ["+azioniCorrelataTmpInput+"]");
							operationOpenSPCoop.setCorrelata(azioniCorrelataTmpInput);
						}
					}
					
					ptOpenSPCoop.addAzione(operationOpenSPCoop);
					
					// NextAzione
					contatoreAzione++;
					azioniTmpInput = this.getParameter(servizioParam+
							ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION+contatoreAzione);
				}
				
				// Next service
				contatoreServizio++;
				servizioParam = ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE+contatoreServizio;
				serviziTmpInput = this.getParameter(servizioParam);
				
				portTypes.add(ptOpenSPCoop);
			}
			
			return portTypes;
		}
	}
	
	
	public MapPlaceholder readPlaceholder() throws Exception{
		
		MapPlaceholder map = new MapPlaceholder();
		
		int index = 0;
		String pHidden = this.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INPUT_PROPRIETA_PREFIX_HIDDEN+index);
		while(pHidden!=null || "".equals(pHidden)){
			String pValue = this.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INPUT_PROPRIETA_PREFIX_VALUE+index);
			map.put(pHidden, pValue);
			
			index++;
			pHidden = this.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INPUT_PROPRIETA_PREFIX_HIDDEN+index);
		}
		
		if(map.size()<=0){
			return null;
		}
		return map;
		
	}
	
	public HashMap<String, String> readRequisitiInput() throws Exception{
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		int index = 0;
		String pHidden = this.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_REQUISITO_INPUT_PROPRIETA_PREFIX_HIDDEN+index);
		while(pHidden!=null || "".equals(pHidden)){
			String pName = this.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_REQUISITO_INPUT_PROPRIETA_PREFIX_NAME_HIDDEN+index);
			String pValue = this.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_REQUISITO_INPUT_PROPRIETA_PREFIX_VALUE+index);
			map.put(pName, pValue);
			
			index++;
			pHidden = this.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_REQUISITO_INPUT_PROPRIETA_PREFIX_HIDDEN+index);
		}
		
		if(map.size()<=0){
			return null;
		}
		return map;
		
	}
	public HashMap<String, String> readRequisitiStepIncrementInput() throws Exception{
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		int index = 0;
		String pHidden = this.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_REQUISITO_INPUT_PROPRIETA_PREFIX_HIDDEN+index);
		while(pHidden!=null || "".equals(pHidden)){
			String pName = this.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_REQUISITO_INPUT_PROPRIETA_PREFIX_NAME_HIDDEN+index);
			String pValue = this.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_REQUISITO_INPUT_PROPRIETA_PREFIX_STEP_INCREMENT_HIDDEN+index);
			map.put(pName, pValue);
			
			index++;
			pHidden = this.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_REQUISITO_INPUT_PROPRIETA_PREFIX_HIDDEN+index);
		}
		
		if(map.size()<=0){
			return null;
		}
		return map;
		
	}
	
	
	public org.openspcoop2.core.config.Credenziali readCredenzialiSA() throws Exception{
		org.openspcoop2.core.config.Credenziali cis = null;

		String tipoauth = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE);
		if (tipoauth == null) {
			//tipoauth = ServiziApplicativiCostanti.DEFAULT_CREDENZIALI_TIPO_AUTENTICAZIONE;
			return null;
		}
		String utente = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
		String password = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
		String subject = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT);
		String issuer = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_ISSUER);
		String principal = this.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL);
		
		if (tipoauth!=null && !tipoauth.equals(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_NONE.toString())) {
							
			if (cis == null) {
				cis = new org.openspcoop2.core.config.Credenziali();
			}
			if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA.equals(tipoauth)){
				//cis.setTipo(CredenzialeTipo.toEnumConstant(CostantiConfigurazione.AUTENTICAZIONE_NONE));
				cis.setTipo(null);
			}else
				cis.setTipo(CredenzialeTipo.toEnumConstant(tipoauth));
			
			cis.setUser("");
			cis.setPassword("");
			cis.setSubject("");
			
			if (tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC)) {
				cis.setUser(utente);
				cis.setPassword(password);
			}
			
			if (tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL)) {
				cis.setSubject(subject);
				if(StringUtils.isNotEmpty(issuer)) {
					cis.setIssuer(issuer);
				}
			}
			
			if (tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL)) {
				cis.setUser(principal);
			}
		}
		
		return  cis;
	}
	
	public org.openspcoop2.core.config.InvocazioneCredenziali readCredenzialiConnettore() throws Exception{
		org.openspcoop2.core.config.InvocazioneCredenziali cis = null;

		String tipoauth = this.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_TIPO_AUTENTICAZIONE);
		if (tipoauth == null) {
			tipoauth = ConnettoriCostanti.DEFAULT_AUTENTICAZIONE_TIPO;
		}
		String utente = this.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
		String password = this.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
		
		if (tipoauth!=null && tipoauth.equals(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.toString())) {
							
			if (cis == null) {
				cis = new org.openspcoop2.core.config.InvocazioneCredenziali();
			}
			cis.setUser(utente);
			cis.setPassword(password);
			
		}
		
		return  cis;
	}
	
	public InvocazioneServizio readInvocazioneServizio() throws Exception{
		
		InvocazioneServizio invServizio = new InvocazioneServizio();
		
		String sbustamento = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP);
		if(sbustamento==null){
			return null; // la presenza (in disabilitato o abilitato) garantisce la presenza
		}
		String sbustamentoInformazioniProtocolloRichiesta = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RICHIESTA);
		String getmsg = this.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX);
		
		invServizio.setSbustamentoSoap(StatoFunzionalita.toEnumConstant(sbustamento));
		invServizio.setSbustamentoInformazioniProtocollo(StatoFunzionalita.toEnumConstant(sbustamentoInformazioniProtocolloRichiesta));
		invServizio.setGetMessage(StatoFunzionalita.toEnumConstant(getmsg));

		org.openspcoop2.core.config.InvocazioneCredenziali invCr = this.readCredenzialiConnettore();
		invServizio.setCredenziali(invCr);
		if(invCr!=null) {
			invServizio.setAutenticazione(InvocazioneServizioTipoAutenticazione.BASIC);
		}
		
		TipologiaConnettori tipologiaConnettoriOriginale = null;
		try{
			tipologiaConnettoriOriginale = Utilities.getTipologiaConnettori(this.core);
			Utilities.setTipologiaConnettori(TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP);
		
			String endpointtype = this.readEndPointType();
			if(endpointtype==null){
				return null;
			}
	//		if(endpointtype==null){
	//			endpointtype = TipiConnettore.DISABILITATO.toString();
	//		}
			String tipoconn = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			//String autenticazioneHttp = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
			String user = null;
			String password = null;
			
			String connettoreDebug = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);
					
			// token policy
			String autenticazioneTokenS = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO);
			@SuppressWarnings("unused")
			boolean autenticazioneToken = ServletUtils.isCheckBoxEnabled(autenticazioneTokenS);
			String token_policy = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY);
			
			// proxy
			String proxy_enabled = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			String proxy_hostname = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			String proxy_port = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			String proxy_username = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			String proxy_password = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);
			
			// tempi risposta
			String tempiRisposta_enabled = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
			String tempiRisposta_connectionTimeout = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
			String tempiRisposta_readTimeout = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
			String tempiRisposta_tempoMedioRisposta = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA);
			
			// opzioni avanzate
			String transfer_mode = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
			String transfer_mode_chunk_size = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
			String redirect_mode = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
			String redirect_max_hop = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
			String opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(this, transfer_mode, redirect_mode);
			
			// http
			String url = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
			if(TipiConnettore.HTTP.toString().equals(endpointtype)){
				user = this.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = this.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
			
			// jms
			String nomeCodaJMS = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			String tipo = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			String initcont = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			String urlpgk = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			String provurl = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			String connfact = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			String sendas = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
			if(TipiConnettore.JMS.toString().equals(endpointtype)){
				user = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
				password = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			}
			
			// https
			String httpsurl = url;
			String httpstipologia = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE);
			String httpshostverifyS = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			boolean httpshostverify = ServletUtils.isCheckBoxEnabled(httpshostverifyS);
			String httpsTrustVerifyCertS = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS );
			boolean httpsTrustVerifyCert = ServletUtils.isCheckBoxEnabled(httpsTrustVerifyCertS);
			String httpspath = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
			String httpstipo = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			String httpspwd = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			String httpsalgoritmo = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			String httpsstatoS = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			boolean httpsstato = ServletUtils.isCheckBoxEnabled(httpsstatoS);
			String httpskeystore = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			String httpspwdprivatekeytrust = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			String httpspathkey = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			String httpstipokey = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			String httpspwdkey = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			String httpspwdprivatekey = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			String httpsalgoritmokey = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			String httpsKeyAlias = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_ALIAS_PRIVATE_KEY_KEYSTORE);
			String httpsTrustStoreCRLs = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL);
			if(TipiConnettore.HTTPS.toString().equals(endpointtype)){
				user = this.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = this.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
					
			// file
			String requestOutputFileName = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
			String requestOutputFileNameHeaders = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
			String requestOutputParentDirCreateIfNotExists = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
			String requestOutputOverwriteIfExists = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
			String responseInputMode = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
			String responseInputFileName = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
			String responseInputFileNameHeaders = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
			String responseInputDeleteAfterRead = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
			String responseInputWaitTime = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);
			
			
			Connettore connis = invServizio.getConnettore();
			if(connis==null){
				connis = new Connettore();
			}
			String oldConnT = TipiConnettore.DISABILITATO.getNome();
			
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(connis, ConnettoreServletType.WIZARD_CONFIG, this, false, endpointtype);
			
			this.fillConnettore(connis, connettoreDebug, endpointtype, oldConnT, tipoconn, url,
					nomeCodaJMS, tipo, user, password,
					initcont, urlpgk, provurl, connfact,
					sendas, httpsurl, httpstipologia, httpshostverify, 
					httpsTrustVerifyCert, httpspath, httpstipo,
					httpspwd, httpsalgoritmo, httpsstato,
					httpskeystore, httpspwdprivatekeytrust,
					httpspathkey, httpstipokey,
					httpspwdkey, httpspwdprivatekey,
					httpsalgoritmokey,
					httpsKeyAlias, httpsTrustStoreCRLs,
					proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
					tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
					opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
					requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
					responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
					token_policy,
					listExtendedConnettore);
			invServizio.setConnettore(connis);
		
		}finally{
			Utilities.setTipologiaConnettori(tipologiaConnettoriOriginale);
		}
			
		return invServizio;
	}
	
	public org.openspcoop2.core.registry.Connettore readConnettore() throws Exception{
		
		TipologiaConnettori tipologiaConnettoriOriginale = null;
		try{
			tipologiaConnettoriOriginale = Utilities.getTipologiaConnettori(this.core);
			Utilities.setTipologiaConnettori(TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP);
		
			String endpointtype = this.readEndPointType();
			if(endpointtype==null){
				return null;
			}
	//		if(endpointtype==null){
	//			endpointtype = TipiConnettore.DISABILITATO.toString();
	//		}
			String tipoconn = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			//String autenticazioneHttp = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
			String user = null;
			String password = null;
			
			String connettoreDebug = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);
					
			// token policy
			String autenticazioneTokenS = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO);
			@SuppressWarnings("unused")
			boolean autenticazioneToken = ServletUtils.isCheckBoxEnabled(autenticazioneTokenS);
			String token_policy = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY);
			
			// proxy
			String proxy_enabled = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			String proxy_hostname = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			String proxy_port = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			String proxy_username = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			String proxy_password = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);
			
			// tempi risposta
			String tempiRisposta_enabled = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
			String tempiRisposta_connectionTimeout = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
			String tempiRisposta_readTimeout = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
			String tempiRisposta_tempoMedioRisposta = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA);
			
			// opzioni avanzate
			String transfer_mode = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
			String transfer_mode_chunk_size = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
			String redirect_mode = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
			String redirect_max_hop = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
			String opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(this, transfer_mode, redirect_mode);
			
			// http
			String url = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
			if(TipiConnettore.HTTP.toString().equals(endpointtype)){
				user = this.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = this.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
			
			// jms
			String nomeCodaJMS = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			String tipo = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			String initcont = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			String urlpgk = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			String provurl = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			String connfact = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			String sendas = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
			if(TipiConnettore.JMS.toString().equals(endpointtype)){
				user = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
				password = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			}
			
			// https
			String httpsurl = url;
			String httpstipologia = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE);
			String httpshostverifyS = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			boolean httpshostverify = ServletUtils.isCheckBoxEnabled(httpshostverifyS);
			String httpsTrustVerifyCertS = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS );
			boolean httpsTrustVerifyCert = ServletUtils.isCheckBoxEnabled(httpsTrustVerifyCertS);
			String httpspath = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
			String httpstipo = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			String httpspwd = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			String httpsalgoritmo = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			String httpsstatoS = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			boolean httpsstato = ServletUtils.isCheckBoxEnabled(httpsstatoS);
			String httpskeystore = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			String httpspwdprivatekeytrust = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			String httpspathkey = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			String httpstipokey = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			String httpspwdkey = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			String httpspwdprivatekey = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			String httpsalgoritmokey = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			String httpsKeyAlias = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_ALIAS_PRIVATE_KEY_KEYSTORE);
			String httpsTrustStoreCRLs = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL);
			if(TipiConnettore.HTTPS.toString().equals(endpointtype)){
				user = this.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = this.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
						
			// file
			String requestOutputFileName = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
			String requestOutputFileNameHeaders = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
			String requestOutputParentDirCreateIfNotExists = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
			String requestOutputOverwriteIfExists = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
			String responseInputMode = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
			String responseInputFileName = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
			String responseInputFileNameHeaders = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
			String responseInputDeleteAfterRead = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
			String responseInputWaitTime = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);
			
						
			org.openspcoop2.core.registry.Connettore connettore = new org.openspcoop2.core.registry.Connettore();
			
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(connettore, ConnettoreServletType.WIZARD_REGISTRY, this, false, endpointtype);
			
			String oldConnT = TipiConnettore.DISABILITATO.getNome();
			this.fillConnettore(connettore, connettoreDebug, endpointtype, oldConnT, tipoconn, url,
					nomeCodaJMS, tipo, user, password,
					initcont, urlpgk, provurl, connfact,
					sendas, httpsurl, httpstipologia, httpshostverify, 
					httpsTrustVerifyCert, httpspath, httpstipo,
					httpspwd, httpsalgoritmo, httpsstato,
					httpskeystore, httpspwdprivatekeytrust,
					httpspathkey, httpstipokey,
					httpspwdkey, httpspwdprivatekey,
					httpsalgoritmokey,
					httpsKeyAlias, httpsTrustStoreCRLs,
					proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
					tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
					opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
					requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
					responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
					token_policy,
					listExtendedConnettore);
			
			return connettore;
			
		}finally{
			Utilities.setTipologiaConnettori(tipologiaConnettoriOriginale);
		}
	}
	
	public void addImportInformationMissingToDati(Vector<DataElement> dati,ImporterUtils importerUtils, FormFile ff,
			String protocolloSelect,String inputMode,String protocolloEffettivo,String inputType,
			boolean validazioneDocumenti,boolean updateEnabled,
			boolean importDeletePolicyConfig, boolean importConfig,
			ImportInformationMissingCollection importInformationMissingCollection,
			ImportInformationMissingException importInformationMissingException,
			String modalitaAcquisizioneInformazioniProtocollo,List<PortType> portTypesOpenSPCoop,
			List<String> protocolliForModes,
			boolean readedDatiConnettori,
			Wizard wizard, int step,
			boolean delete) throws Exception{
				
		
		String oldMessage = this.pd.getMessage();
		if(oldMessage!=null && !"".equals(oldMessage)){
			oldMessage = ArchiviCostanti.LABEL_IMPORT_ERRORE+oldMessage;
		}else{
			oldMessage = "";
		}
		boolean showIntestazioneArchivio = true;
		boolean showSection = true;
		if(wizard!=null){
			
			if ( importInformationMissingException!=null && importInformationMissingException.isMissingRequisitiInfoInput() ){
				this.pd.setMessage(wizard.getDescrizione()+oldMessage,Costanti.MESSAGE_TYPE_INFO);
			}
			else {
				int actualStep = step;
				int stepConfigurated = -1;
				if(wizard.getStep()>0){
					stepConfigurated = wizard.getStep();
				}
				if(delete) {
					if(wizard.getStepInDelete()>0){
						stepConfigurated = wizard.getStepInDelete();
					}
				}
				
				// verifico se sono stati raccolti dei requisiti
				if(importInformationMissingCollection!=null && importInformationMissingCollection.exists(org.openspcoop2.protocol.engine.constants.Costanti.REQUISITI_INPUT_RACCOLTI)) {
					ImportInformationMissing miss = importInformationMissingCollection.get(org.openspcoop2.protocol.engine.constants.Costanti.REQUISITI_INPUT_RACCOLTI);
					if(miss.getRequisitiInput()!=null && !miss.getRequisitiInput().isEmpty() &&
							miss.getRequisitiInputStepIncrement()!=null && !miss.getRequisitiInputStepIncrement().isEmpty()) {
						
						actualStep--; // il primo step non si conta
						
						Iterator<String> it = miss.getRequisitiInputStepIncrement().keySet().iterator();
						while (it.hasNext()) {
							String inputName = (String) it.next();
							String stepIncrementValue_incrementNumber_incrementCondition = miss.getRequisitiInputStepIncrement().get(inputName);
							int increment = -1;
							String stepIncrementValue = null;
							if(stepIncrementValue_incrementNumber_incrementCondition.contains(" ")) {
								String [] tmp = stepIncrementValue_incrementNumber_incrementCondition.split(" ");
								increment = Integer.valueOf(tmp[0]);
								stepIncrementValue = stepIncrementValue_incrementNumber_incrementCondition.substring((tmp[0]+" ").length());
							}
							String value = miss.getRequisitiInput().get(inputName);
							if(stepIncrementValue.equals(value)) {
								stepConfigurated = stepConfigurated + increment;
							}
						}
					}
				}
						
				String stepDescription = " (Fase "+actualStep;
				if(stepConfigurated>0){
					stepDescription+="/"+stepConfigurated;
				}
				stepDescription+=")";
				this.pd.setMessage(wizard.getDescrizione()+stepDescription+oldMessage,Costanti.MESSAGE_TYPE_INFO);
			}
			
			showIntestazioneArchivio = wizard.getIntestazioneOriginale();
			
			DataElement de = new DataElement();
			de.setLabel(importInformationMissingException.getIdObjectDescription());
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
			showSection = false;
			
		}else{
			this.pd.setMessage(ArchiviCostanti.LABEL_IMPORT_ERROR_INFORMAZIONI_MANCANTI+oldMessage);
		}
		

		
		
		
		
		
		
		
		
		
		
		
		
		
		String sessionId = this.request.getSession().getId();

		// hidden fields
		
		DataElement de = new DataElement();
		de.setValue(protocolloSelect);
		de.setType(DataElementType.HIDDEN);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		de = new DataElement();
		de.setValue(inputMode);
		de.setType(DataElementType.HIDDEN);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPOLOGIA_ARCHIVIO);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		de = new DataElement();
		de.setValue(inputType);
		de.setType(DataElementType.HIDDEN);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		de = new DataElement();
		de.setValue(""+validazioneDocumenti);
		de.setType(DataElementType.HIDDEN);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_VALIDAZIONE_DOCUMENTI);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		de = new DataElement();
		de.setValue(""+updateEnabled);
		de.setType(DataElementType.HIDDEN);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_UPDATE_ENABLED);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		de = new DataElement();
		de.setValue(""+importDeletePolicyConfig);
		de.setType(DataElementType.HIDDEN);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_DELETE_POLICY_CONFIG_ENABLED);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		de = new DataElement();
		de.setValue(""+importConfig);
		de.setType(DataElementType.HIDDEN);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_CONFIG_ENABLED);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		de = new DataElement();
		de.setValue(importInformationMissingException.getIdObject());
		de.setType(DataElementType.HIDDEN);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_ID);
		de.setSize(this.getSize());
		dati.addElement(de);

		File fileFormFile = importerUtils.writeFormFile(sessionId, ff);
		de = new DataElement();
		de.setValue(fileFormFile.getAbsolutePath());
		de.setType(DataElementType.HIDDEN);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_PACKAGE_FILE_PATH);
		dati.addElement(de);
		
		File fileImportInformationMissingCollection = null;
		if(importInformationMissingCollection!=null){
			fileImportInformationMissingCollection = importerUtils.writeImportInformationMissingCollectionFile(sessionId, importInformationMissingCollection);
			de = new DataElement();
			de.setValue(fileImportInformationMissingCollection.getAbsolutePath());
			de.setType(DataElementType.HIDDEN);
			de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_COLLECTION_FILE_PATH);
			dati.addElement(de);
		}
		
		de = new DataElement();
		if(importInformationMissingException!=null && importInformationMissingException.getClassObject()!=null){
			de.setValue(importInformationMissingException.getClassObject().getName());
		}
		else{
			de.setValue("");
		}
		de.setType(DataElementType.HIDDEN);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_CLASS);
		dati.addElement(de);
	
		File fileImportInformationMissingObject = null;
		if(importInformationMissingException!=null && importInformationMissingException.getObject()!=null){
			fileImportInformationMissingObject = importerUtils.writeImportInformationMissingObjectFile(sessionId, importInformationMissingException.getObject());
		}
		de = new DataElement();
		if(fileImportInformationMissingObject!=null){
			de.setValue(fileImportInformationMissingObject.getAbsolutePath());
		}
		else{
			de.setValue("");
		}
		de.setType(DataElementType.HIDDEN);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_FILE_PATH);
		dati.addElement(de);
		
		// inizio della grafica effettiva
		
		if(showIntestazioneArchivio){
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_ARCHIVIO);
			de.setType(DataElementType.TITLE);
			dati.addElement(de);
		}
		
		de = new DataElement();
		de.setValue(ff.getFileName());
		if(showIntestazioneArchivio){
			de.setType(DataElementType.TEXT);
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_PACKAGE_FILE_SIMPLE_NAME);
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_PACKAGE_FILE_SIMPLE_NAME);
		dati.addElement(de);

		de = new DataElement();
		de.setValue(importInformationMissingException.getIdObjectDescription());
		if(showIntestazioneArchivio){
			de.setType(DataElementType.TEXT);
		}
		else{
			de.setType(DataElementType.HIDDEN);
		}
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_ID_DESCRIPTION);
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_ID_DESCRIPTION);
		dati.addElement(de);

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// requisiti
		if ( importInformationMissingException.isMissingRequisitiInfoInput() ){
		
			RequisitoInput requisitoInput = importInformationMissingException.getMissingRequisitiInfoInputObject();
			
			if(showSection){
				de = new DataElement();
				de.setLabel(requisitoInput.getDescrizione());
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
			}
			
			HashMap<String, String> mapRequisitiInput = this.readRequisitiInput();
			if(mapRequisitiInput==null) {
				mapRequisitiInput = new HashMap<String, String>();
			}
			for (int i = 0; i < requisitoInput.sizeProprietaList(); i++) {
				ProprietaRequisitoInput p = requisitoInput.getProprieta(i);
				if(mapRequisitiInput.containsKey(p.getNome())==false) {
					if(p.getConditions()!=null) {
						if(ImporterInformationMissingUtils.checkConditions(p.getConditions(),mapRequisitiInput)==false) {
							continue;
						}
					}
					mapRequisitiInput.put(p.getNome(), p.getDefault());
				}
			}
			
			int indexParam = 0;
			
			for (int k = 0; k < requisitoInput.sizeProprietaList(); k++) {
				
				ProprietaRequisitoInput p = requisitoInput.getProprieta(k);
				
				if(delete && !p.isUseInDelete()) {
					continue;
				}
				
				if(p.getConditions()!=null) {
					if(ImporterInformationMissingUtils.checkConditions(p.getConditions(),mapRequisitiInput)==false) {
						continue;
					}
				}
								
				if(p.getHeader()!=null) {
					this.addDescriptionInformationMissingToDati(dati,  p.getHeader() );
				}
						
				boolean required = false;
				
				String valore = p.getDefault();
				if(mapRequisitiInput!=null && mapRequisitiInput.containsKey(p.getNome())) {
					valore = mapRequisitiInput.get(p.getNome());
				}
				
				de = new DataElement();
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_REQUISITO_INPUT_PROPRIETA_PREFIX_VALUE+indexParam);
				switch (p.getTipo()) {
				case HIDDEN:
					de.setType(DataElementType.HIDDEN);
					de.setValue(valore);
					break;
				case CHECKBOX:
					de.setType(DataElementType.CHECKBOX);
					de.setSelected(ServletUtils.isCheckBoxEnabled(valore));
					de.setPostBack(p.isReloadOnChange());
					break;
				case TEXTEDIT:
					de.setRequired(true);
					de.setType(DataElementType.TEXT_EDIT);
					de.setValue(valore);
					required = true;
					break;
				}
				de.setLabel(p.getLabel());
				de.setSize(this.getSize());
				dati.addElement(de);
				
				de = new DataElement();
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_REQUISITO_INPUT_PROPRIETA_PREFIX_HIDDEN+indexParam);
				de.setType(DataElementType.HIDDEN);
				de.setValue(required+"");
				dati.addElement(de);
				
				de = new DataElement();
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_REQUISITO_INPUT_PROPRIETA_PREFIX_NAME_HIDDEN+indexParam);
				de.setType(DataElementType.HIDDEN);
				de.setValue(p.getNome());
				dati.addElement(de);
				
				de = new DataElement();
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_REQUISITO_INPUT_PROPRIETA_PREFIX_STEP_INCREMENT_HIDDEN+indexParam);
				de.setType(DataElementType.HIDDEN);
				de.setValue(p.getStepIncrementCondition()==null ? "1 N.D." : p.getStepIncrement() + " "+p.getStepIncrementCondition());
				dati.addElement(de);
				
				if(p.getFooter()!=null) {
					this.addDescriptionInformationMissingToDati(dati,  p.getFooter() );
				}
				
				indexParam++;
			}
			
		}
				
		
		
		
		
		
		
		
		
		
		

		
		
		// soggetto - versione - accordoParteComune - accordoCooperazione
		
		if ( importInformationMissingException.isMissingInfoSoggetto() ||
				importInformationMissingException.isMissingInfoVersione() ||
				importInformationMissingException.isMissingInfoAccordoServizioParteComune() || 
				importInformationMissingException.isMissingInfoAccordoCooperazione() ) {
			
			String labelSection = ArchiviCostanti.LABEL_IMPORT_ERROR_INFORMAZIONI_IDENTIFICAZIONE_MANCANTI;
			String labelSoggettoDataElement = ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_SOGGETTO_INPUT;
			
			if ( importInformationMissingException.isMissingInfoSoggetto() &&
					!importInformationMissingException.isMissingInfoVersione() &&
					!importInformationMissingException.isMissingInfoAccordoServizioParteComune() && 
					!importInformationMissingException.isMissingInfoAccordoCooperazione() ) {
				labelSection = ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_SOGGETTO_INPUT;
				labelSoggettoDataElement = ArchiviCostanti.LABEL_NOME_SOGGETTO;
			}
			
			if(showSection){
				de = new DataElement();
				de.setLabel(labelSection);
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
			}

			if ( importInformationMissingException.getMissingInfoHeader()!=null ){
				this.addDescriptionInformationMissingToDati(dati,  importInformationMissingException.getMissingInfoHeader() );
			}
			
			if ( importInformationMissingException.isMissingInfoSoggetto() ){
			
				List<String> soggettiLabel = null;
				try{
					soggettiLabel = importerUtils.getIdSoggetti(protocolliForModes,inputMode,
							importInformationMissingException.getMissingInfoProtocollo(),
							importInformationMissingException.getMissingInfoSoggetto_tipoPdD(),
							wizard, this);
				}catch(Exception e){
					this.pd.setMessage(this.pd.getMessage()+ArchiviCostanti.LABEL_IMPORT_ERRORE+e.getMessage());
					this.pd.disableEditMode();
					return;
				}
				
				de = new DataElement();
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_SOGGETTO_INPUT);
				de.setType(DataElementType.SELECT);
				de.setLabel(labelSoggettoDataElement);
				de.setValues(soggettiLabel.toArray(new String[1]));
				
				List<String> labelSoggettiByProtocol = new ArrayList<>();
				for (String soggetto : soggettiLabel) {
					if(soggetto.contains("/")) {
						IDSoggetto idSoggetto = new IDSoggetto(soggetto.split("/")[0], soggetto.split("/")[1]);
						labelSoggettiByProtocol.add(this.getLabelNomeSoggetto(idSoggetto));
					}else {
						labelSoggettiByProtocol.add(soggetto);
					}
				}
				de.setLabels(labelSoggettiByProtocol.toArray(new String[1]));
			
				String selected = null;
				if(importInformationMissingException.getMissingInfoDefault()!=null && importInformationMissingException.getMissingInfoDefault().getValore()!=null) {
					if(soggettiLabel.contains(importInformationMissingException.getMissingInfoDefault().getValore())) {
						selected = importInformationMissingException.getMissingInfoDefault().getValore();
					}
				}
				if(selected==null) {
					if(soggettiLabel.size()>1)
						selected = ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_SOGGETTO_INPUT_UNDEFINDED;
					else
						selected = soggettiLabel.get(0);
				}
				de.setSelected(selected);
				
				de.setSize(this.getSize());
				dati.addElement(de);
				
			}
	
			if ( importInformationMissingException.isMissingInfoVersione() ){
			
				de = new DataElement();
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_VERSIONE_INPUT);
				de.setType(DataElementType.TEXT_EDIT);
				de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_VERSIONE_INPUT);
				Integer version = null;
				if(importInformationMissingException.getMissingInfoDefault()!=null && importInformationMissingException.getMissingInfoDefault().getValore()!=null) {
					try {
						version = Integer.valueOf(importInformationMissingException.getMissingInfoDefault().getValore());
					}catch(Exception e) {}
				}
				if(version==null) {
					version = 1;
				}
				de.setValue(version.intValue()+"");
				de.setSize(30);
				dati.addElement(de);
				
			}
			
			if ( importInformationMissingException.isMissingInfoAccordoServizioParteComune() ){
				
				List<String> accordiServizioParteComuneLabel = importerUtils.getIdAccordiServizioParteComune(protocolliForModes,inputMode,
						importInformationMissingException.getMissingInfoProtocollo(), this);
				
				de = new DataElement();
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_SERVIZIO_PARTE_COMUNE_INPUT);
				de.setType(DataElementType.SELECT);
				de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_SERVIZIO_PARTE_COMUNE_INPUT);
				de.setValues(accordiServizioParteComuneLabel.toArray(new String[1]));
				
				List<String> labelAccordiByProtocol = new ArrayList<>();
				for (String uriAccordo : accordiServizioParteComuneLabel) {
					IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(uriAccordo);
					labelAccordiByProtocol.add(this.getLabelIdAccordo(idAccordo));
				}
				de.setLabels(labelAccordiByProtocol.toArray(new String[1]));
				
				String selected = null;
				if(importInformationMissingException.getMissingInfoDefault()!=null && importInformationMissingException.getMissingInfoDefault().getValore()!=null) {
					if(accordiServizioParteComuneLabel.contains(importInformationMissingException.getMissingInfoDefault().getValore())) {
						selected = importInformationMissingException.getMissingInfoDefault().getValore();
					}
				}
				if(selected==null) {
					selected = ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_INPUT_UNDEFINDED;
				}
				de.setSelected(selected);

				de.setSize(this.getSize());
				dati.addElement(de);
				
			}
			
			if ( importInformationMissingException.isMissingInfoAccordoCooperazione() ){
				
				List<String> accordiCooperazioneLabel = importerUtils.getIdAccordiCooperazione(protocolliForModes,inputMode,
						importInformationMissingException.getMissingInfoProtocollo(), this);
				
				de = new DataElement();
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_COOPERAZIONE_INPUT);
				de.setType(DataElementType.SELECT);
				de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_COOPERAZIONE_INPUT);
				de.setValues(accordiCooperazioneLabel.toArray(new String[1]));
				
				List<String> labelAccordiByProtocol = new ArrayList<>();
				for (String uriAccordo : accordiCooperazioneLabel) {
					IDAccordoCooperazione idAccordo = IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromUri(uriAccordo);
					labelAccordiByProtocol.add(this.getLabelIdAccordoCooperazione(idAccordo));
				}
				de.setLabels(labelAccordiByProtocol.toArray(new String[1]));
				
				String selected = null;
				if(importInformationMissingException.getMissingInfoDefault()!=null && importInformationMissingException.getMissingInfoDefault().getValore()!=null) {
					if(accordiCooperazioneLabel.contains(importInformationMissingException.getMissingInfoDefault().getValore())) {
						selected = importInformationMissingException.getMissingInfoDefault().getValore();
					}
				}
				if(selected==null) {
					selected = ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_INPUT_UNDEFINDED;
				}
				de.setSelected(selected);
				
				de.setSize(this.getSize());
				dati.addElement(de);
				
			}
			
			if ( importInformationMissingException.getMissingInfoFooter()!=null ){
				this.addDescriptionInformationMissingToDati(dati,  importInformationMissingException.getMissingInfoFooter() );
			}
		}

		
		
		
		
		
		
		
		// input
		if ( importInformationMissingException.isMissingInfoInput() ){
		
			Input input = importInformationMissingException.getMissingInfoInputObject();
			
			if(showSection){
				de = new DataElement();
				de.setLabel(input.getDescrizione());
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
			}
			
			for (int i = 0; i < input.sizeProprietaList(); i++) {
				
				Proprieta p = input.getProprieta(i);
				
				if(delete && !p.isUseInDelete()) {
					continue;
				}
				
				if(p.getHeader()!=null) {
					this.addDescriptionInformationMissingToDati(dati,  p.getHeader() );
				}
				
				de = new DataElement();
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INPUT_PROPRIETA_PREFIX_HIDDEN+i);
				de.setType(DataElementType.HIDDEN);
				de.setValue(p.getPlaceholder());
				de.setSize(30);
				dati.addElement(de);
				
				de = new DataElement();
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INPUT_PROPRIETA_PREFIX_VALUE+i);
				de.setRequired(true);
				de.setType(DataElementType.TEXT_EDIT);
				de.setLabel(p.getNome());
				de.setValue(p.getDefault());
				de.setSize(this.getSize());
				dati.addElement(de);
				
				if(p.getFooter()!=null) {
					this.addDescriptionInformationMissingToDati(dati,  p.getFooter() );
				}
			}
			
		}
		
		
		
		
		
		
		// servizi parte comune
		
		if ( importInformationMissingException.isMissingInfoProfiliServizi() ){
			
			if(showSection){
				de = new DataElement();
				de.setLabel(ArchiviCostanti.LABEL_IMPORT_ERROR_INFORMAZIONI_PROTOCOLLO_MANCANTI);
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
			}

			if ( importInformationMissingException.getMissingInfoHeader()!=null ){
				this.addDescriptionInformationMissingToDati(dati,  importInformationMissingException.getMissingInfoHeader() );
			}
			
			String[] modalitaAcquisizione = { ArchiviCostanti.LABEL_IMPORT_ERROR_INFORMAZIONI_PROTOCOLLO_MANCANTI_RICONOSCIMENTO_WSDL_MODE, 
					ArchiviCostanti.LABEL_IMPORT_ERROR_INFORMAZIONI_PROTOCOLLO_MANCANTI_RICONOSCIMENTO_USER_INPUT };

			de = new DataElement();
			de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT);
			de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT);
			de.setType(DataElementType.SELECT);
			de.setValues(modalitaAcquisizione);
			de.setSelected(modalitaAcquisizioneInformazioniProtocollo);
			de.setPostBack(true);
			dati.addElement(de);
			
			if(modalitaAcquisizioneInformazioniProtocollo!=null && 
					ArchiviCostanti.LABEL_IMPORT_ERROR_INFORMAZIONI_PROTOCOLLO_MANCANTI_RICONOSCIMENTO_USER_INPUT.equals(modalitaAcquisizioneInformazioniProtocollo)){
			
				Object object = importInformationMissingException.getObject();
				AccordoServizioParteComune aspc = (AccordoServizioParteComune) object;
				ServiceBinding serviceBinding = this.apcCore.toMessageServiceBinding(aspc.getServiceBinding());						
				
				// Provo a comprendere il protocollo associato all'accordo di servizio parte comune se e' definito il soggetto referente
				// Serve per visualizzare i profili di collaborazione supportati
				String protocolloAccordo = null;
				if(aspc.getSoggettoReferente()!=null && aspc.getSoggettoReferente().getTipo()!=null){
					protocolloAccordo = this.soggettiCore.getProtocolloAssociatoTipoSoggetto(aspc.getSoggettoReferente().getTipo());
				}
				
				// Impostazione protocollo
				if(protocolloAccordo==null){
					protocolloAccordo = protocolloEffettivo; // uso l'impostazione associata al tipo in package scelto.
				}
				
				// read WSDL Base sfruttando il BasicArchive (non serve il protocollo)
				BasicArchive basicArchive = new BasicArchive(new BasicProtocolFactory(this.log));
				basicArchive.setProtocolInfo(aspc,ControlStationCore.getLog());
								
				if(aspc.sizePortTypeList()>0){
					
					// port types
					int contatoreServizio = 1;
					for (PortType ptWSDL : aspc.getPortTypeList()) {
						
						PortType ptOpenSPCoop = null;
						if(portTypesOpenSPCoop.size()>=contatoreServizio){
							ptOpenSPCoop = portTypesOpenSPCoop.get((contatoreServizio-1));
						}

						de = new DataElement();
						de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_TITLE.
								replace(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_TITLE_KEY, ptWSDL.getNome()));
						de.setType(DataElementType.TITLE);
						dati.addElement(de);
					
						de = new DataElement();
						de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_IS_DEFINED+contatoreServizio);
						de.setType(DataElementType.HIDDEN);
						de.setValue(CostantiConfigurazione.ABILITATO.toString());
						dati.addElement(de);	
	
						String nomeFieldServizio = ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE+contatoreServizio;
						de = new DataElement();
						de.setName(nomeFieldServizio);
						de.setType(DataElementType.TEXT_EDIT);
						if(ptOpenSPCoop!=null)
							de.setValue(ptOpenSPCoop.getNome());
						else
							de.setValue(ptWSDL.getNome());
						de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE);
						de.setSize(this.getSize());
						dati.addElement(de);						
						contatoreServizio++;
	
						//System.out.println("		  PortType="+pt.getQName().toString()+" ("+pt.getOperations().size()+" operations)");
						int contatoreAzione = 1;
						for (Operation opWSDL : ptWSDL.getAzioneList()) {
							
							Operation opOpenSPCoop = null;
							if(ptOpenSPCoop!=null){
								if(ptOpenSPCoop.sizeAzioneList()>=contatoreAzione){
									opOpenSPCoop = ptOpenSPCoop.getAzione((contatoreAzione-1));
								}
							}
	
							de = new DataElement();
							de.setType(DataElementType.SUBTITLE);
							de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_OPERATION_TITLE.
									replace(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_OPERATION_TITLE_KEY, opWSDL.getNome()));
							de.setSize(this.getSize());
							dati.addElement(de);

							de = new DataElement();
							de.setName(nomeFieldServizio+
									ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_IS_DEFINED+contatoreAzione);
							de.setType(DataElementType.HIDDEN);
							de.setValue(CostantiConfigurazione.ABILITATO.toString());
							dati.addElement(de);	
							
							de = new DataElement();
							de.setName(nomeFieldServizio+
									ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION+contatoreAzione);
							de.setType(DataElementType.TEXT_EDIT);
							if(opOpenSPCoop!=null)
								de.setValue(opOpenSPCoop.getNome());
							else
								de.setValue(opWSDL.getNome());
							de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION);
							de.setSize(this.getSize());
							dati.addElement(de);
	
							String profiloCollaborazione = null;
							if(opOpenSPCoop!=null){
								if(opOpenSPCoop.getProfiloCollaborazione()!=null){
									profiloCollaborazione = opOpenSPCoop.getProfiloCollaborazione().toString();
								}
							}else{
								if(opWSDL.getProfiloCollaborazione()!=null){
									profiloCollaborazione = opWSDL.getProfiloCollaborazione().toString();
								}
							}
	
							de = new DataElement();
							de.setName(nomeFieldServizio+
									ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_PROFILO+contatoreAzione);
							de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_PROFILO);
							de.setType(DataElementType.SELECT);
							de.setValues(this.core.getProfiliDiCollaborazioneSupportatiDalProtocollo(protocolloAccordo,serviceBinding));
							de.setSelected(profiloCollaborazione);
							if(this.archiviCore.isShowCorrelazioneAsincronaInAccordi()){
								de.setPostBack(true);
							}
							dati.addElement(de);
	
							if( AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO.equals(profiloCollaborazione) || 
									AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO.equals(profiloCollaborazione)  ){
									
								de = new DataElement();
								de.setName(nomeFieldServizio+
										ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_SERVIZIO_CORRELATO+contatoreAzione);
								de.setType(DataElementType.TEXT_EDIT);
								if(opOpenSPCoop!=null && opOpenSPCoop.getCorrelataServizio()!=null)
									de.setValue(opOpenSPCoop.getCorrelataServizio());
								else
									de.setValue("");
								de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_SERVIZIO_CORRELATO);
								de.setSize(this.getSize());
								dati.addElement(de);

								de = new DataElement();
								de.setName(nomeFieldServizio+
										ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_CORRELATA+contatoreAzione);
								de.setType(DataElementType.TEXT_EDIT);
								if(opOpenSPCoop!=null && opOpenSPCoop.getCorrelata()!=null)
									de.setValue(opOpenSPCoop.getCorrelata());
								else
									de.setValue("");
								de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_CORRELATA);
								de.setSize(this.getSize());
								dati.addElement(de);

							}
	
							contatoreAzione++;
	
						}
					}
				}
				else{
					throw new Exception("Informazioni di protocollo non definite e i wsdl presenti all'interno dell'archivio non possiedeno nemmeno un port type");
				}
			}
	
		}
		else{
			de = new DataElement();
			de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT);
			de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT);
			de.setType(DataElementType.HIDDEN);
			de.setValue("");
			dati.addElement(de);
		}
		
		
		
		
		
		// servizi parte comune
		
		if ( importInformationMissingException.isMismatchPortTypeRifServiziParteComune() ){
			
			if(showSection){
				de = new DataElement();
				de.setLabel(ArchiviCostanti.LABEL_IMPORT_ERROR_INFORMAZIONI_PORT_TYPE_RIFERITO);
				de.setType(DataElementType.TITLE);
				dati.addElement(de);
			}
			
			if ( importInformationMissingException.getMissingInfoHeader()!=null ){
				this.addDescriptionInformationMissingToDati(dati,  importInformationMissingException.getMissingInfoHeader() );
			}
			
			de = new DataElement();
			de.setType(DataElementType.NOTE);
			de.setValue(ArchiviCostanti.LABEL_IMPORT_ERROR_INFORMAZIONI_PORT_TYPE_RIFERITO_MESSAGGIO.
					replace(ArchiviCostanti.LABEL_IMPORT_ERROR_INFORMAZIONI_PORT_TYPE_RIFERITO_MESSAGGIO_KEY, 
							importInformationMissingException.getMismatchPortTypeRifServiziParteComune_nome()));
			dati.addElement(de);
			
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_PORT_TYPE_IMPLEMENTED_INPUT);
			de.setType(DataElementType.SELECT);
			List<String> listServizi = new ArrayList<String>();
			listServizi.add(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_SOGGETTO_INPUT_UNDEFINDED);
			listServizi.addAll(importInformationMissingException.getMismatchPortTypeRifServiziParteComune_serviziParteComune());
			de.setValues(listServizi);
			de.setSelected(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_SOGGETTO_INPUT_UNDEFINDED);
			de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_PORT_TYPE_IMPLEMENTED_INPUT);
			dati.addElement(de);

			if ( importInformationMissingException.getMissingInfoFooter()!=null ){
				this.addDescriptionInformationMissingToDati(dati,  importInformationMissingException.getMissingInfoFooter() );
			}
		}
		
		
		
		
		
		// invocazioneServizio
		
		if( importInformationMissingException.isMissingInfoInvocazioneServizio() ){
			
			if ( importInformationMissingException.getMissingInfoHeader()!=null ){
				this.addDescriptionInformationMissingToDati(dati,  importInformationMissingException.getMissingInfoHeader() );
			}
			
			InterfaceType originalInterfaceType = null;
			User userSession = null;
			try{
				
				// Impostazione protocollo
				String protocollo = importInformationMissingException.getMissingInfoProtocollo();
				if(protocollo==null){
					protocollo = protocolloEffettivo; // uso l'impostazione associata al tipo in package scelto.
					// NOTA: non devo impostare il protocollo. Serve solo per visualizzare l'info "Sbustamento Info Protocollo 'xxx'"
					// Se viene passato null non viene visualizzata la stringa 'xxx'.
					// Meglio cosi che fornire il tipo associato al package, che puo' non essere corretto nel casi di archivio openspcoop
					// che contiene diversi protocolli (e nella select list dei protocolli non e' stato scelto alcun protocollo)
				}
				
				InterfaceType interfaceType = InterfaceType.STANDARD; 
				TipologiaConnettori tipologiaConnettori = null;
				
				Default defaultProperties = importInformationMissingException.getMissingInfoDefault();
				if(defaultProperties!=null && defaultProperties.sizeProprietaList()>0) {
					for (ProprietaDefault p : defaultProperties.getProprietaList()) {
						if(ArchiviCostanti.DEFAULT_PROPERTY_INFORMATION_MISSING_MODALIA_INTERFACCIA.equalsIgnoreCase(p.getNome())) {
							if(ArchiviCostanti.DEFAULT_PROPERTY_INFORMATION_MISSING_MODALIA_INTERFACCIA_STANDARD.equalsIgnoreCase(p.getValore())) {
								interfaceType = InterfaceType.STANDARD; 
							}
							else if(ArchiviCostanti.DEFAULT_PROPERTY_INFORMATION_MISSING_MODALIA_INTERFACCIA_AVANZATA.equalsIgnoreCase(p.getValore())) {
								interfaceType = InterfaceType.AVANZATA; 
							}
						}
						else if(ArchiviCostanti.DEFAULT_PROPERTY_INFORMATION_MISSING_TIPO_CONNETTORE.equalsIgnoreCase(p.getNome())) {
							if(ArchiviCostanti.DEFAULT_PROPERTY_INFORMATION_MISSING_TIPO_CONNETTORE_HTTP.equalsIgnoreCase(p.getValore())) {
								tipologiaConnettori = TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP; 
							}
							else if(ArchiviCostanti.DEFAULT_PROPERTY_INFORMATION_MISSING_TIPO_CONNETTORE_QUALSIASI.equalsIgnoreCase(p.getValore())) {
								tipologiaConnettori = TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL; 
							}
						}
					}
				}
				
				userSession = ServletUtils.getUserFromSession(this.session);
				originalInterfaceType = userSession.getInterfaceType();
				// forzo standard
				userSession.setInterfaceType(interfaceType);
				this.setTipoInterfaccia(interfaceType);
			
				String sbustamento = null;
				String sbustamentoInformazioniProtocolloRichiesta = null;
				String getmsg = null;
				String getmsgUsername = null;
				String getmsgPassword = null;
				
				// gestito nel metodo getParameter: if(readedDatiConnettori==false){
				sbustamento = this.getParameter(readedDatiConnettori,defaultProperties,ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP);
				sbustamentoInformazioniProtocolloRichiesta = this.getParameter(readedDatiConnettori,defaultProperties,ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RICHIESTA);
				getmsg = this.getParameter(readedDatiConnettori,defaultProperties,ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX);
				getmsgUsername = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				getmsgPassword = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
				//}
				boolean erogazioneServizioApplicativoServerEnabled = false;
				boolean integrationManagerEnabled = !this.isModalitaStandard() && this.archiviCore.isIntegrationManagerEnabled();
				this.addEndPointToDati(dati,"","",sbustamento,sbustamentoInformazioniProtocolloRichiesta,
						getmsg,getmsgUsername,getmsgPassword,true,
						null,null,protocollo,false,true, showSection, null,null, null, erogazioneServizioApplicativoServerEnabled,
						null, false,
						integrationManagerEnabled,
						TipoOperazione.ADD, null, null);
							
				boolean forceEnabled = true; // non ha senso non fornire un connettore a meno che non vi sia la possibilita' di utilizzare l'integration manager
				boolean showSectionTitle = false;
				if(InterfaceType.STANDARD.equals(interfaceType)==false) {
					showSectionTitle = true;
					
					if(	getmsg!=null && CostantiConfigurazione.ABILITATO.toString().equals(getmsg) ) {
						forceEnabled = false;
					}
					else {
						forceEnabled = true;
					}
					
				}
				
				addDatiConnettore(dati, readedDatiConnettori, importInformationMissingException.getMissingInfoDefault(), showSectionTitle, 
						ConnettoreServletType.WIZARD_CONFIG, forceEnabled, tipologiaConnettori);
			}
			finally{
				// ripristino originale
				if(userSession!=null)
					userSession.setInterfaceType(originalInterfaceType);
			}
			
			if ( importInformationMissingException.getMissingInfoFooter()!=null ){
				this.addDescriptionInformationMissingToDati(dati,  importInformationMissingException.getMissingInfoFooter() );
			}
		}
		
		
		
		// connettore
		
		if( importInformationMissingException.isMissingInfoConnettore() ){
			
			if ( importInformationMissingException.getMissingInfoHeader()!=null ){
				this.addDescriptionInformationMissingToDati(dati,  importInformationMissingException.getMissingInfoHeader() );
			}
			
			InterfaceType originalInterfaceType = null;
			User userSession = null;
			try{
				InterfaceType interfaceType = InterfaceType.STANDARD; 
				TipologiaConnettori tipologiaConnettori = null;
				
				Default defaultProperties = importInformationMissingException.getMissingInfoDefault();
				if(defaultProperties!=null && defaultProperties.sizeProprietaList()>0) {
					for (ProprietaDefault p : defaultProperties.getProprietaList()) {
						if(ArchiviCostanti.DEFAULT_PROPERTY_INFORMATION_MISSING_MODALIA_INTERFACCIA.equalsIgnoreCase(p.getNome())) {
							if(ArchiviCostanti.DEFAULT_PROPERTY_INFORMATION_MISSING_MODALIA_INTERFACCIA_STANDARD.equalsIgnoreCase(p.getValore())) {
								interfaceType = InterfaceType.STANDARD; 
							}
							else if(ArchiviCostanti.DEFAULT_PROPERTY_INFORMATION_MISSING_MODALIA_INTERFACCIA_AVANZATA.equalsIgnoreCase(p.getValore())) {
								interfaceType = InterfaceType.AVANZATA; 
							}
						}
						else if(ArchiviCostanti.DEFAULT_PROPERTY_INFORMATION_MISSING_TIPO_CONNETTORE.equalsIgnoreCase(p.getNome())) {
							if(ArchiviCostanti.DEFAULT_PROPERTY_INFORMATION_MISSING_TIPO_CONNETTORE_HTTP.equalsIgnoreCase(p.getValore())) {
								tipologiaConnettori = TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP; 
							}
							else if(ArchiviCostanti.DEFAULT_PROPERTY_INFORMATION_MISSING_TIPO_CONNETTORE_QUALSIASI.equalsIgnoreCase(p.getValore())) {
								tipologiaConnettori = TipologiaConnettori.TIPOLOGIA_CONNETTORI_ALL; 
							}
						}
					}
				}
				
				userSession = ServletUtils.getUserFromSession(this.session);
				originalInterfaceType = userSession.getInterfaceType();
				// forzo standard
				userSession.setInterfaceType(interfaceType);
				this.setTipoInterfaccia(interfaceType);

				boolean forceEnabled = true; // non ha senso non fornire un connettore
				
				addDatiConnettore(dati, readedDatiConnettori, importInformationMissingException.getMissingInfoDefault(), showSection, 
						ConnettoreServletType.WIZARD_REGISTRY, forceEnabled, tipologiaConnettori);
			}
			finally{
				// ripristino originale
				if(userSession!=null)
					userSession.setInterfaceType(originalInterfaceType);
			}
			
			if ( importInformationMissingException.getMissingInfoFooter()!=null ){
				this.addDescriptionInformationMissingToDati(dati,  importInformationMissingException.getMissingInfoFooter() );
			}
		}
		
		
		
		// credenziali
		
		if( importInformationMissingException.isMissingInfoCredenziali() ){
			
			if ( importInformationMissingException.getMissingInfoHeader()!=null ){
				this.addDescriptionInformationMissingToDati(dati,  importInformationMissingException.getMissingInfoHeader() );
			}
			
			InterfaceType originalInterfaceType = null;
			User userSession = null;
			try{
				userSession = ServletUtils.getUserFromSession(this.session);
				originalInterfaceType = userSession.getInterfaceType();
				// forzo standard
				userSession.setInterfaceType(InterfaceType.STANDARD);
				this.setTipoInterfaccia(InterfaceType.STANDARD);
			
				this.addDatiCredenzialiAccesso(dati, readedDatiConnettori, importInformationMissingException.getMissingInfoDefault(), showSection);
			}
			finally{
				// ripristino originale
				if(userSession!=null)
					userSession.setInterfaceType(originalInterfaceType);
			}
			
			if ( importInformationMissingException.getMissingInfoFooter()!=null ){
				this.addDescriptionInformationMissingToDati(dati,  importInformationMissingException.getMissingInfoFooter() );
			}
		}
	}
	
	private void addDescriptionInformationMissingToDati(Vector<DataElement> dati, Description header) {
		
		for (DescriptionType item : header.getItemList()) {
			DataElement de = new DataElement();
			switch (item.getTipo()) {
			case TITLE:
				de.setType(DataElementType.TITLE);
				break;
			case SUBTITLE:
				de.setType(DataElementType.SUBTITLE);
				break;
			case NOTE:
				de.setType(DataElementType.NOTE);
				break;
			case TEXT:
				de.setType(DataElementType.TEXT);
				break;
			}
			if(item.getLabel()!=null) {
				de.setLabel(item.getLabel());
			}
			if(item.getValore()!=null) {
				de.setValue(item.getValore());
			}
			if(item.isBold()) {
				de.setBold(true);
			}
			dati.addElement(de);
		}

	}
	
	private void addDatiCredenzialiAccesso(Vector<DataElement> dati, boolean readedDatiConnettori, Default defaultProperties, boolean showSectionTitle) throws Exception{
				
		String tipoauth = null;
		String utente = null;
		String password  = null;
		String subject = null;
		String principal = null;
		String tipoCredenzialiSSLSorgente = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL);
		if(tipoCredenzialiSSLSorgente == null) {
			tipoCredenzialiSSLSorgente = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE;
		}
		String tipoCredenzialiSSLTipoArchivioS = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO);
		BinaryParameter tipoCredenzialiSSLFileCertificato = this.getBinaryParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO);
		List<String> listaAliasEstrattiCertificato = new ArrayList<String>();
		String tipoCredenzialiSSLFileCertificatoPassword = this.getParameter(readedDatiConnettori, defaultProperties, ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_PASSWORD);
		String tipoCredenzialiSSLAliasCertificato = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO);
		String tipoCredenzialiSSLAliasCertificatoSubject= this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SUBJECT);
		String tipoCredenzialiSSLAliasCertificatoIssuer= this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_ISSUER);
		String tipoCredenzialiSSLAliasCertificatoType= this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_TYPE);
		String tipoCredenzialiSSLAliasCertificatoVersion= this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_VERSION);
		String tipoCredenzialiSSLAliasCertificatoSerialNumber= this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SERIAL_NUMBER);
		String tipoCredenzialiSSLAliasCertificatoSelfSigned= this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SELF_SIGNED);
		String tipoCredenzialiSSLAliasCertificatoNotBefore= this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_NOT_BEFORE);
		String tipoCredenzialiSSLAliasCertificatoNotAfter =this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_NOT_AFTER); 
		String tipoCredenzialiSSLVerificaTuttiICampi = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI);
		String tipoCredenzialiSSLConfigurazioneManualeSelfSigned= this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_MANUALE_SELF_SIGNED);
		String issuer = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_ISSUER);	
		String tipoCredenzialiSSLStatoElaborazioneCertificato = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP);
		if (tipoCredenzialiSSLStatoElaborazioneCertificato == null) {
			tipoCredenzialiSSLStatoElaborazioneCertificato = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD_ARCHIVI;
		}
		//String oldTipoCredenzialiSSLStatoElaborazioneCertificato = tipoCredenzialiSSLStatoElaborazioneCertificato;
		
		// gestito nel metodo getParameter: if(readedDatiConnettori==false){
		tipoauth = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE);
		if (tipoauth == null) {
			tipoauth = ConnettoriCostanti.DEFAULT_AUTENTICAZIONE_TIPO;
		}
		utente = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
		password = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
		subject = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT);
		principal = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL);
		
		String changepwd = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CHANGE_PASSWORD);
		
		String multipleApiKey = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS);
		String appId = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID);
		String apiKey = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY);
		
		if(tipoauth==null || tipoauth.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA)){
			tipoauth = this.saCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
		} 
			
		// Credenziali di accesso
		if (utente == null) {
			utente = "";
		}
		if (password == null) {
			password = "";
		}
		if (subject == null) {
			subject = "";
		}
		if (principal == null) {
			principal = "";
		}
		if (issuer == null) {
			issuer = "";
		}
		org.openspcoop2.utils.certificate.ArchiveType tipoCredenzialiSSLTipoArchivio= null;
		if(tipoCredenzialiSSLTipoArchivioS == null) {
			tipoCredenzialiSSLTipoArchivio = org.openspcoop2.utils.certificate.ArchiveType.CER; 
		} else {
			tipoCredenzialiSSLTipoArchivio = org.openspcoop2.utils.certificate.ArchiveType.valueOf(tipoCredenzialiSSLTipoArchivioS);
		}
		if (tipoCredenzialiSSLConfigurazioneManualeSelfSigned == null) {
			tipoCredenzialiSSLConfigurazioneManualeSelfSigned = Costanti.CHECK_BOX_DISABLED;
		}
		if (tipoCredenzialiSSLVerificaTuttiICampi == null) {
			tipoCredenzialiSSLVerificaTuttiICampi = Costanti.CHECK_BOX_DISABLED;
		}
		if (tipoCredenzialiSSLAliasCertificato == null) {
			tipoCredenzialiSSLAliasCertificato = "";
		}
		
		// controllo dei postback
		
		//}true,endpointtype,true,false, prefix, true
		dati =	this.addCredenzialiToDati(dati, tipoauth, null, utente, password, subject, principal,
				ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ADD, showSectionTitle, null, false, true, null, true, 
				tipoCredenzialiSSLSorgente, tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato, tipoCredenzialiSSLFileCertificatoPassword, listaAliasEstrattiCertificato, 
				tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLAliasCertificatoSubject, tipoCredenzialiSSLAliasCertificatoIssuer,
				tipoCredenzialiSSLAliasCertificatoType, tipoCredenzialiSSLAliasCertificatoVersion, tipoCredenzialiSSLAliasCertificatoSerialNumber, 
				tipoCredenzialiSSLAliasCertificatoSelfSigned, tipoCredenzialiSSLAliasCertificatoNotBefore, tipoCredenzialiSSLAliasCertificatoNotAfter, 
				tipoCredenzialiSSLVerificaTuttiICampi, tipoCredenzialiSSLConfigurazioneManualeSelfSigned, issuer, tipoCredenzialiSSLStatoElaborazioneCertificato,
				changepwd, 
				multipleApiKey, appId, apiKey); 
	}
	
	private void addDatiConnettore(Vector<DataElement> dati, boolean readedDatiConnettori, Default defaultProperties,
			boolean showSectionTitle,ConnettoreServletType connettoreServletType,
			boolean forceEnabled, TipologiaConnettori tipologiaConnettoriInfoMissing) throws Exception{
		
		TipologiaConnettori tipologiaConnettoriOriginale = null;
		try{
			tipologiaConnettoriOriginale = Utilities.getTipologiaConnettori(this.core);
			if(tipologiaConnettoriInfoMissing!=null) {
				Utilities.setTipologiaConnettori(tipologiaConnettoriInfoMissing);
			}
			else {
				Utilities.setTipologiaConnettori(TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP);
			}
		
			String endpointtype = null;
			String tipoconn = null;
			String autenticazioneHttp = null;
			String user = null;
			String password = null;
			
			String connettoreDebug = null;
			
			// http
			String url = null;
			
			// jms
			String nomeCodaJMS = null;
			String tipo = null;
			String initcont = null;
			String urlpgk = null;
			String provurl = null;
			String connfact = null;
			String sendas = null;
			
			// https
			String httpsurl = null;
			String httpstipologia = null;
			String httpshostverifyS = null;
			boolean httpsTrustVerifyCert = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS;
			String httpspath = null;
			String httpstipo = null;
			String httpspwd = null;
			String httpsalgoritmo = null;
			String httpsstatoS = null;
			String httpskeystore = null;
			String httpspwdprivatekeytrust = null;
			String httpspathkey = null;
			String httpstipokey = null;
			String httpspwdkey = null;
			String httpspwdprivatekey = null;
			String httpsalgoritmokey = null;
			String httpsKeyAlias = null;
			String httpsTrustStoreCRLs = null;
			
			boolean httpshostverify = false;
			boolean httpsstato = false;
			
			String proxy_enabled = null;
			String proxy_hostname = null;
			String proxy_port = null;
			String proxy_username = null;
			String proxy_password = null;
			
			String tempiRisposta_enabled = null;
			String tempiRisposta_connectionTimeout = null; 
			String tempiRisposta_readTimeout = null;
			String tempiRisposta_tempoMedioRisposta = null;
			
			String transfer_mode = null;
			String transfer_mode_chunk_size = null;
			String redirect_mode = null;
			String redirect_max_hop = null;
			String opzioniAvanzate = null;
			
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

			// gestito nel metodo getParameter:  if(readedDatiConnettori==false){
			
			// NON POSSO USARE QUESTO METODO, SENNO NON LEGGO I PARMETRI DI DEFAULT: endpointtype = this.readEndPointType();
			endpointtype = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE);
			String endpointtype_check = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_CHECK);
			String endpointtype_ssl = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTPS);
			endpointtype = this.readEndPointType(endpointtype, endpointtype_check, endpointtype_ssl);
			if(endpointtype==null || (forceEnabled && TipiConnettore.DISABILITATO.getNome().equals(endpointtype)) ){
				endpointtype = TipiConnettore.HTTP.toString();
			}
			tipoconn = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			autenticazioneHttp = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
			
			connettoreDebug = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);
						
			// token policy
			String autenticazioneTokenS = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO);
			boolean autenticazioneToken = ServletUtils.isCheckBoxEnabled(autenticazioneTokenS);
			String token_policy = this.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY);
			
			// proxy
			proxy_enabled = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			proxy_hostname = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			proxy_port = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			proxy_username = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			proxy_password = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);
			
			// tempi risposta
			tempiRisposta_enabled = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
			tempiRisposta_connectionTimeout = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
			tempiRisposta_readTimeout = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
			tempiRisposta_tempoMedioRisposta = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA);
			
			// opzioni avanzate
			transfer_mode = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
			transfer_mode_chunk_size = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
			redirect_mode = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
			redirect_max_hop = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
			opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(this, transfer_mode, redirect_mode);
			
			// http
			url = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
			if(TipiConnettore.HTTP.toString().equals(endpointtype)){
				user = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
			
			// jms
			nomeCodaJMS = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			tipo = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			initcont = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			urlpgk = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			provurl = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			connfact = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			sendas = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
			if(TipiConnettore.JMS.toString().equals(endpointtype)){
				user = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
				password = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			}
			
			// https
			httpsurl = url;
			httpstipologia = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE);
			httpshostverifyS = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			httpshostverify = ServletUtils.isCheckBoxEnabled(httpshostverifyS);
			String httpsTrustVerifyCertS = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS );
			httpsTrustVerifyCert = ServletUtils.isCheckBoxEnabled(httpsTrustVerifyCertS);
			httpspath = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
			httpstipo = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			httpspwd = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			httpsalgoritmo = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			httpsstatoS = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			httpsstato = ServletUtils.isCheckBoxEnabled(httpsstatoS);
			httpskeystore = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			httpspwdprivatekeytrust = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			httpspathkey = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			httpstipokey = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			httpspwdkey = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			httpspwdprivatekey = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			httpsalgoritmokey = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			httpsKeyAlias = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_ALIAS_PRIVATE_KEY_KEYSTORE);
			httpsTrustStoreCRLs = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL);
			if(TipiConnettore.HTTPS.toString().equals(endpointtype)){
				user = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
			
			// file
			requestOutputFileName = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
			requestOutputFileNameHeaders = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
			requestOutputParentDirCreateIfNotExists = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
			requestOutputOverwriteIfExists = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
			responseInputMode = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
			responseInputFileName = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
			responseInputFileNameHeaders = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
			responseInputDeleteAfterRead = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
			responseInputWaitTime = this.getParameter(readedDatiConnettori,defaultProperties,ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);
			//}

			Boolean isConnettoreCustomUltimaImmagineSalvata = null;
						
			if(endpointtype==null || (forceEnabled && TipiConnettore.DISABILITATO.getNome().equals(endpointtype)) ){
				endpointtype = TipiConnettore.HTTP.toString();
			}
			
			Connettore conTmp = null;
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, connettoreServletType, this, false, endpointtype);

			dati = this.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, "",//ServiziApplicativiCostanti.LABEL_EROGATORE+" ",
					url, nomeCodaJMS,
					tipo, user, password, initcont, urlpgk, provurl,
					connfact, sendas, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, TipoOperazione.CHANGE, 
					httpsurl, httpstipologia, httpshostverify, 
					httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
					httpsalgoritmo, httpsstato, httpskeystore,
					httpspwdprivatekeytrust, httpspathkey,
					httpstipokey, httpspwdkey, 
					httpspwdprivatekey, httpsalgoritmokey,
					httpsKeyAlias, httpsTrustStoreCRLs,
					tipoconn, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT,
					"", "", null, null, null,null,
					null, null, showSectionTitle,
					isConnettoreCustomUltimaImmagineSalvata, 
					proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
					tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
					opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
					requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
					responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
					autenticazioneToken,token_policy,
					listExtendedConnettore, forceEnabled,
					null, false,false
					, false, false, null, null
					);
			
		}finally{
			// ripristino tipologia
			Utilities.setTipologiaConnettori(tipologiaConnettoriOriginale);
		}
	}

	
	
	public boolean accordiAllegatiCheckData(TipoOperazione tipoOperazione,FormFile formFile,Documento documento,ProprietariDocumento proprietario, IProtocolFactory<?> pf)
			throws Exception {

		try{

			String ruolo = this.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_RUOLO);

			// Campi obbligatori
			if (ruolo.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare il Tipo di documento");
				return false;
			}

			if(formFile==null || formFile.getFileName()!=null && "".equals(formFile.getFileName())){
				this.pd.setMessage("E' necessario selezionare un documento.");
				return false;
			}

			if(formFile==null || formFile.getFileSize()<=0){
				this.pd.setMessage("Il documento selezionato non puo essere vuoto.");
				return false;
			}

			if(documento.getTipo()==null || "".equals(documento.getTipo()) || documento.getTipo().length()>30 || formFile.getFileName().lastIndexOf(".")==-1){
				if(documento.getTipo()==null || "".equals(documento.getTipo()) || formFile.getFileName().lastIndexOf(".")==-1){
					this.pd.setMessage("L'estensione del documento non e' valida.");
				}else{
					this.pd.setMessage("L'estensione del documento non e' valida. La dimensione dell'estensione e' troppo lunga.");
				}
				return false;
			}

			boolean documentoUnivocoIndipendentementeTipo = true;
			if(this.archiviCore.existsDocumento(documento,proprietario,documentoUnivocoIndipendentementeTipo)){

				String tipo = documento.getTipo();
				String ruoloDoc = documento.getRuolo();
				String msgTipo = "(tipo: "+documento.getTipo()+") ";
				if(documentoUnivocoIndipendentementeTipo) {
					tipo = null;
					ruoloDoc = null;
					msgTipo = "";
				}
				
				Documento existing = this.archiviCore.getDocumento(documento.getFile(),tipo,ruoloDoc,documento.getIdProprietarioDocumento(),false,proprietario);
				if(existing.getId().longValue() == documento.getId().longValue())
					return true;

				if(RuoliDocumento.allegato.toString().equals(documento.getRuolo()) || documentoUnivocoIndipendentementeTipo)
					this.pd.setMessage("L'allegato con nome "+documento.getFile()+" "+msgTipo+"è già presente nella API.");
				else
					this.pd.setMessage("La specifica semiformale con nome "+documento.getFile()+" "+msgTipo+"è già presente nella API.");

				return false;
			}

			ValidazioneResult valida = pf.createValidazioneDocumenti().valida (documento);
			if(!valida.isEsito()) {
				this.pd.setMessage(valida.getMessaggioErrore());
				return false;
			}
			
			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	private String getParameter(boolean readedDatiConnettori, Default defaultProperties, String name) throws Exception {
		String value = null;
		if(readedDatiConnettori==false) {
			value = this.getParameter(name);
		}
		if(value==null) {
			if(defaultProperties!=null && defaultProperties.sizeProprietaList()>0) {
				for (ProprietaDefault defaultProperty : defaultProperties.getProprietaList()) {
					if(name.equals(defaultProperty.getNome())) {
						return defaultProperty.getValore();
					}
				}
			}
		}
		return value;
	}
	
	private BinaryParameter getBinaryParameter(boolean readedDatiConnettori, Default defaultProperties, String name) throws Exception {
		BinaryParameter toReturn = this.getBinaryParameter(name);
		byte [] value = null;
		if(readedDatiConnettori==false) {
			value = toReturn.getValue();
		}
		if(value==null) {
			if(defaultProperties!=null && defaultProperties.sizeProprietaList()>0) {
				for (ProprietaDefault defaultProperty : defaultProperties.getProprietaList()) {
					if(name.equals(defaultProperty.getNome())) {
						toReturn.setValue(defaultProperty.getValore().getBytes());
						break;
					}
				}
			}
		}
		return toReturn;
	}

}
