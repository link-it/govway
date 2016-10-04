/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.upload.FormFile;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.TipiConnettore;
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
import org.openspcoop2.pdd.logger.DriverMsgDiagnostici;
import org.openspcoop2.pdd.logger.DriverTracciamento;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.protocol.basic.archive.BasicArchive;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.archive.ImportInformationMissingCollection;
import org.openspcoop2.protocol.engine.archive.ImportInformationMissingException;
import org.openspcoop2.protocol.information_missing.Input;
import org.openspcoop2.protocol.information_missing.Proprieta;
import org.openspcoop2.protocol.information_missing.Wizard;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.archive.ArchiveCascadeConfiguration;
import org.openspcoop2.protocol.sdk.archive.ArchiveMode;
import org.openspcoop2.protocol.sdk.archive.ArchiveModeType;
import org.openspcoop2.protocol.sdk.archive.ExportMode;
import org.openspcoop2.protocol.sdk.archive.ImportMode;
import org.openspcoop2.protocol.sdk.archive.MapPlaceholder;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiNotFoundException;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnosticiConPaginazione;
import org.openspcoop2.protocol.sdk.diagnostica.InformazioniProtocollo;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoNotFoundException;
import org.openspcoop2.protocol.sdk.tracciamento.FiltroRicercaTracceConPaginazione;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.costanti.TipologiaConnettori;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiHelper;
import org.openspcoop2.web.lib.mvc.AreaBottoni;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
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
public class ArchiviHelper extends ConsoleHelper {

	public ArchiviHelper(HttpServletRequest request, PageData pd, 
			HttpSession session) throws Exception {
		super(request, pd,  session);
	}


	public void addExportToDati(Vector<DataElement> dati,
			List<String> protocolliSelectList,String protocollo,
			List<ExportMode> exportModes,String exportMode,
			ArchiveType servletSourceExport, String objToExport, 
			String cascade,String configurazioneType,
			String cascadePdd,String cascadeSoggetti,
			String cascadeServiziApplicativi, String cascadePorteDelegate, String cascadePorteApplicative,
			String cascadeAc, String cascadeAspc, String cascadeAsc, String cascadeAsps, String cascadeFruizioni){
		
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
			if(this.archiviCore.isExportArchivi_standard()){
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
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_EXPORT_CASCADE);
		if(cascadeEnabled){
			de.setType(DataElementType.CHECKBOX);
			de.setPostBack(true);
		}
		else
			de.setType(DataElementType.HIDDEN);
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE);
		de.setSelected(ServletUtils.isCheckBoxEnabled(cascade));
		de.setValue(cascade);
		dati.addElement(de);
		
		if(!this.archiviCore.isExportArchivi_standard() && 
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
		
		StringBuffer bf = new StringBuffer();
		for (int i = 0; i < wizard.getRequisiti().sizeProtocolloList(); i++) {
			String protocollo = wizard.getRequisiti().getProtocollo(i).getNome();
			try{
				IProtocolFactory pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
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
			String importInformationMissing_accordoCooperazioneInput){
				
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
		int index = 0;
		String pHidden = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INPUT_PROPRIETA_PREFIX_HIDDEN+index);
		while(pHidden!=null || "".equals(pHidden)){
			String pValue = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INPUT_PROPRIETA_PREFIX_VALUE+index);
			if(pValue==null || "".equals(pValue)){
				this.pd.setMessage("Deve essere indicato un valore in tutti i campi");
				return false;
			}
				
			index++;
			pHidden = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INPUT_PROPRIETA_PREFIX_HIDDEN+index);
		}
		
		// check Informazioni configurazione profilo servizi
		if(ArchiviCostanti.LABEL_IMPORT_ERROR_INFORMAZIONI_PROTOCOLLO_MANCANTI_RICONOSCIMENTO_USER_INPUT.equals(importInformationMissing_modalitaAcquisizioneInformazioniProtocollo)){
			
			int contatoreServizio = 1;
			String servizioParam = ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_IS_DEFINED+contatoreServizio;
			String serviziTmpInput = this.request.getParameter(servizioParam);
			while(serviziTmpInput!=null && !"".equals(serviziTmpInput)){
			
				servizioParam = ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE+contatoreServizio;
				String nomeServizio = this.request.getParameter(servizioParam);
				if(nomeServizio==null || "".equals(nomeServizio)){
					this.pd.setMessage("Deve essere indicato il nome del servizio da associare ad ogni port-type esistente");
					return false;
				}
				
				int contatoreAzione = 1;
				String azioniTmpInput = this.request.getParameter(servizioParam+
						ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_IS_DEFINED+contatoreAzione);
				while(azioniTmpInput!=null && !"".equals(azioniTmpInput)){
					
					String nomeAzione = this.request.getParameter(servizioParam+
							ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION+contatoreAzione);
					if(nomeAzione==null || "".equals(nomeAzione)){
						this.pd.setMessage("Deve essere indicato il nome di ogni azione da associare alle operations dei port-types");
						return false;
					}
					
					String nomeProfilo = this.request.getParameter(servizioParam+
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
					azioniTmpInput = this.request.getParameter(servizioParam+
							ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_IS_DEFINED+contatoreAzione);
				}
				
				contatoreServizio++;
				servizioParam = ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_IS_DEFINED+contatoreServizio;
				serviziTmpInput = this.request.getParameter(servizioParam);
				
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
		if(!ServletUtils.isEditModeInProgress(this.request)){
			try{
				InvocazioneServizio is = this.readInvocazioneServizio();
				if(is!=null){
					ServiziApplicativiHelper saHelper = new ServiziApplicativiHelper(this.request, this.pd, this.session);
					
					ConnettoriHelper connettoriHelper = new ConnettoriHelper(this.request, this.pd, this.session);
					String endpointtype = connettoriHelper.readEndPointType();
					List<ExtendedConnettore> listExtendedConnettore = 
							ServletExtendedConnettoreUtils.getExtendedConnettore(is.getConnettore(), ConnettoreServletType.WIZARD_CONFIG, this.core, 
									this.request, this.session, false, endpointtype);
					
					boolean isOk = saHelper.servizioApplicativoEndPointCheckData(listExtendedConnettore);
					if (!isOk) {
						return false;
					}
					
					if(StatoFunzionalita.ABILITATO.equals(is.getGetMessage())){
						isOk = saHelper.servizioApplicativoCredenzialiCheckData();
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
		if(!ServletUtils.isEditModeInProgress(this.request)){
			try{
				org.openspcoop2.core.registry.Connettore connettore = this.readConnettore();
				if(connettore!=null){
					
					ConnettoriHelper connettoriHelper = new ConnettoriHelper(this.request, this.pd, this.session);
					String endpointtype = connettoriHelper.readEndPointType();
					List<ExtendedConnettore> listExtendedConnettore = 
							ServletExtendedConnettoreUtils.getExtendedConnettore(connettore, ConnettoreServletType.WIZARD_REGISTRY, this.core, 
									this.request, this.session, false, endpointtype);
					
					boolean isOk = connettoriHelper.endPointCheckData(listExtendedConnettore);
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
		if(!ServletUtils.isEditModeInProgress(this.request)){
			try{
				Credenziali credenziali = this.readCredenzialiSA();
				if(credenziali!=null){
					ServiziApplicativiHelper saHelper = new ServiziApplicativiHelper(this.request, this.pd, this.session);
					boolean isOk = saHelper.servizioApplicativoCredenzialiCheckData();
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
			List<String> protocolliSelectList,String protocollo,
			List<ImportMode> importModes,String importMode,
			List<ArchiveModeType> importTypes,String importType,
			boolean deleter){

		User user = ServletUtils.getUserFromSession(this.session);

		DataElement de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_PROTOCOLLO);
		if(protocolliSelectList.size()>2){
			de.setType(DataElementType.SELECT);
			de.setValues(protocolliSelectList.toArray(new String[1]));
			de.setLabels(protocolliSelectList.toArray(new String[1]));
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
			List<String> tmp = new ArrayList<String>();
			for (ImportMode imp : importModes) {
				tmp.add(imp.toString());
			}
			de.setType(DataElementType.SELECT);
			de.setValues(tmp.toArray(new String[1]));
			de.setLabels(tmp.toArray(new String[1]));
			de.setSelected(importMode);
		}else{
			de.setType(DataElementType.HIDDEN);
			de.setValue(importMode);
		}
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPOLOGIA_ARCHIVIO);
		de.setSize(this.getSize());
		de.setPostBack(true);
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
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_VALIDAZIONE_DOCUMENTI);
		de.setValue(""+validazioneDocumenti);
		if (!InterfaceType.STANDARD.equals(user.getInterfaceType())) {
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
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_UPDATE_ENABLED);
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
		IProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
		protocolFactory.createArchive().setProtocolInfo(aspc);
		// NOTA il codice sopra lo devo eseguire sempre e comunque per validare il WSDL.
		// Se il WSDL e' corrotto (es. wsdl senza port type viene sollevata una eccezione)
		
		if(ArchiviCostanti.LABEL_IMPORT_ERROR_INFORMAZIONI_PROTOCOLLO_MANCANTI_RICONOSCIMENTO_WSDL_MODE.equals(modalitaAcquisizione)){
		
			return aspc.getPortTypeList();
		}
		else{
			int contatoreServizio = 1;
			String servizioParam = ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE+contatoreServizio;
			String serviziTmpInput = this.request.getParameter(servizioParam);
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
				String azioniTmpInput = this.request.getParameter(servizioParam+
						ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION+contatoreAzione);
				while(azioniTmpInput!=null && !"".equals(azioniTmpInput)){
					//System.out.println("TROVATO AZIONE["+contatoreAzione+"] PER SERVIZIO ["+contatoreServizio+"] = "+azioniTmpInput);
					Operation operationOpenSPCoop = new Operation();
					operationOpenSPCoop.setNome(azioniTmpInput);
					operationOpenSPCoop.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
					operationOpenSPCoop.setFiltroDuplicati(CostantiRegistroServizi.ABILITATO);
					
					// profiloCollaborazione
					String profiliCollaborazioneTmpInput = this.request.getParameter(servizioParam+
							ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_PROFILO+contatoreAzione);
					operationOpenSPCoop.setProfiloCollaborazione(ProfiloCollaborazione.toEnumConstant(profiliCollaborazioneTmpInput));
					
					// correlazione
					if(AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO.equals(profiliCollaborazioneTmpInput) || 
							AccordiServizioParteComuneCostanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO.equals(profiliCollaborazioneTmpInput)){
						String servizioAzioneCorrelataTmpInput = this.request.getParameter(servizioParam+
								ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_SERVIZIO_CORRELATO+contatoreAzione);
						if(servizioAzioneCorrelataTmpInput!=null && !"".equals(servizioAzioneCorrelataTmpInput)){
							//System.out.println("CORRELAZIONE ["+operationOpenSPCoop.getNome()+"] ["+azioniCorrelataTmpInput+"]");
							operationOpenSPCoop.setCorrelataServizio(servizioAzioneCorrelataTmpInput);
						}
						String azioniCorrelataTmpInput = this.request.getParameter(servizioParam+
								ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_CORRELATA+contatoreAzione);
						if(azioniCorrelataTmpInput!=null && !"".equals(azioniCorrelataTmpInput)){
							//System.out.println("CORRELAZIONE ["+operationOpenSPCoop.getNome()+"] ["+azioniCorrelataTmpInput+"]");
							operationOpenSPCoop.setCorrelata(azioniCorrelataTmpInput);
						}
					}
					
					ptOpenSPCoop.addAzione(operationOpenSPCoop);
					
					// NextAzione
					contatoreAzione++;
					azioniTmpInput = this.request.getParameter(servizioParam+
							ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION+contatoreAzione);
				}
				
				// Next service
				contatoreServizio++;
				servizioParam = ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE+contatoreServizio;
				serviziTmpInput = this.request.getParameter(servizioParam);
				
				portTypes.add(ptOpenSPCoop);
			}
			
			return portTypes;
		}
	}
	
	
	public MapPlaceholder readPlaceholder(){
		
		MapPlaceholder map = new MapPlaceholder();
		
		int index = 0;
		String pHidden = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INPUT_PROPRIETA_PREFIX_HIDDEN+index);
		while(pHidden!=null || "".equals(pHidden)){
			String pValue = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INPUT_PROPRIETA_PREFIX_VALUE+index);
			map.put(pHidden, pValue);
			
			index++;
			pHidden = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INPUT_PROPRIETA_PREFIX_HIDDEN+index);
		}
		
		if(map.size()<=0){
			return null;
		}
		return map;
		
	}
	
	
	public org.openspcoop2.core.config.Credenziali readCredenzialiSA(){
		org.openspcoop2.core.config.Credenziali cis = null;

		String tipoauth = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SA);
		if (tipoauth == null) {
			//tipoauth = ServiziApplicativiCostanti.DEFAULT_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE;
			return null;
		}
		String utente = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_USERNAME_SA);
		String password = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_PASSWORD_SA);
		//String confpw = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_CONFERMA_PASSWORD_SA);
		String subject = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_SUBJECT_SA);
		
		if (tipoauth!=null && !tipoauth.equals(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_NONE.toString())) {
							
			if (cis == null) {
				cis = new org.openspcoop2.core.config.Credenziali();
			}
			if(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA.equals(tipoauth)){
				//cis.setTipo(CredenzialeTipo.toEnumConstant(CostantiConfigurazione.AUTENTICAZIONE_NONE));
				cis.setTipo(null);
			}else
				cis.setTipo(CredenzialeTipo.toEnumConstant(tipoauth));
			if (tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC)) {
				cis.setUser(utente);
				cis.setPassword(password);
			} else {
				cis.setUser("");
				cis.setPassword("");
			}
			if (tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SSL)) {
				cis.setSubject(subject);
			} else {
				cis.setSubject("");
			}
		}
		
		return  cis;
	}
	
	public org.openspcoop2.core.config.Credenziali readCredenzialiConnettore(){
		org.openspcoop2.core.config.Credenziali cis = null;

		String tipoauth = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_CONNETTORE);
		if (tipoauth == null) {
			tipoauth = ServiziApplicativiCostanti.DEFAULT_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE;
		}
		String utente = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_USERNAME_CONNETTORE);
		String password = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_PASSWORD_CONNETTORE);
		//String confpw = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_CONFERMA_PASSWORD_CONNETTORE);
		String subject = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_SUBJECT_CONNETTORE);
		
		if (tipoauth!=null && !tipoauth.equals(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_NONE.toString())) {
							
			if (cis == null) {
				cis = new org.openspcoop2.core.config.Credenziali();
			}
			if(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA.equals(tipoauth)){
				//cis.setTipo(CredenzialeTipo.toEnumConstant(CostantiConfigurazione.AUTENTICAZIONE_NONE));
				cis.setTipo(null);
			}else
				cis.setTipo(CredenzialeTipo.toEnumConstant(tipoauth));
			if (tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_BASIC)) {
				cis.setUser(utente);
				cis.setPassword(password);
			} else {
				cis.setUser("");
				cis.setPassword("");
			}
			if (tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SSL)) {
				cis.setSubject(subject);
			} else {
				cis.setSubject("");
			}
		}
		
		return  cis;
	}
	
	public InvocazioneServizio readInvocazioneServizio() throws Exception{
		
		InvocazioneServizio invServizio = new InvocazioneServizio();
		
		String sbustamento = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP);
		if(sbustamento==null){
			return null; // la presenza (in disabilitato o abilitato) garantisce la presenza
		}
		String sbustamentoInformazioniProtocolloRichiesta = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RICHIESTA);
		String getmsg = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX);
		
		invServizio.setSbustamentoSoap(StatoFunzionalita.toEnumConstant(sbustamento));
		invServizio.setSbustamentoInformazioniProtocollo(StatoFunzionalita.toEnumConstant(sbustamentoInformazioniProtocolloRichiesta));
		invServizio.setGetMessage(StatoFunzionalita.toEnumConstant(getmsg));

		invServizio.setCredenziali(this.readCredenzialiConnettore());
		
		ConnettoriHelper connettoriHelper = new ConnettoriHelper(this.request, this.pd, this.session);
		
		TipologiaConnettori tipologiaConnettoriOriginale = null;
		try{
			tipologiaConnettoriOriginale = Utilities.getTipologiaConnettori(this.core);
			Utilities.setTipologiaConnettori(TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP);
		
			String endpointtype = connettoriHelper.readEndPointType();
			if(endpointtype==null){
				return null;
			}
	//		if(endpointtype==null){
	//			endpointtype = TipiConnettore.DISABILITATO.toString();
	//		}
			String tipoconn = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			//String autenticazioneHttp = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
			String user = null;
			String password = null;
			
			String connettoreDebug = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);
			
			// http
			String url = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
			if(TipiConnettore.HTTP.toString().equals(endpointtype)){
				user = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_USERNAME);
				password = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_PASSWORD);
			}
			
			// jms
			String nomeCodaJMS = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			String tipo = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			String initcont = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			String urlpgk = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			String provurl = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			String connfact = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			String sendas = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
			if(TipiConnettore.JMS.toString().equals(endpointtype)){
				user = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
				password = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			}
			
			// https
			String httpsurl = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_URL);
			String httpstipologia = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE);
			String httpshostverifyS = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			boolean httpshostverify = ServletUtils.isCheckBoxEnabled(httpshostverifyS);
			String httpspath = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
			String httpstipo = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			String httpspwd = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			String httpsalgoritmo = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			String httpsstatoS = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			boolean httpsstato = ServletUtils.isCheckBoxEnabled(httpsstatoS);
			String httpskeystore = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			String httpspwdprivatekeytrust = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			String httpspathkey = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			String httpstipokey = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			String httpspwdkey = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			String httpspwdprivatekey = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			String httpsalgoritmokey = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			if(TipiConnettore.HTTPS.toString().equals(endpointtype)){
				user = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_USERNAME);
				password = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_PASSWORD);
			}
			
			Connettore connis = invServizio.getConnettore();
			if(connis==null){
				connis = new Connettore();
			}
			String oldConnT = TipiConnettore.DISABILITATO.getNome();
			
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(connis, ConnettoreServletType.WIZARD_CONFIG, this.core, 
							this.request, this.session, false, endpointtype);
			
			connettoriHelper.fillConnettore(connis, connettoreDebug, endpointtype, oldConnT, tipoconn, url,
					nomeCodaJMS, tipo, user, password,
					initcont, urlpgk, provurl, connfact,
					sendas, httpsurl, httpstipologia,
					httpshostverify, httpspath, httpstipo,
					httpspwd, httpsalgoritmo, httpsstato,
					httpskeystore, httpspwdprivatekeytrust,
					httpspathkey, httpstipokey,
					httpspwdkey, httpspwdprivatekey,
					httpsalgoritmokey,
					listExtendedConnettore);
			invServizio.setConnettore(connis);
		
		}finally{
			Utilities.setTipologiaConnettori(tipologiaConnettoriOriginale);
		}
			
		return invServizio;
	}
	
	public org.openspcoop2.core.registry.Connettore readConnettore() throws Exception{
		
		ConnettoriHelper connettoriHelper = new ConnettoriHelper(this.request, this.pd, this.session);
		
		TipologiaConnettori tipologiaConnettoriOriginale = null;
		try{
			tipologiaConnettoriOriginale = Utilities.getTipologiaConnettori(this.core);
			Utilities.setTipologiaConnettori(TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP);
		
			String endpointtype = connettoriHelper.readEndPointType();
			if(endpointtype==null){
				return null;
			}
	//		if(endpointtype==null){
	//			endpointtype = TipiConnettore.DISABILITATO.toString();
	//		}
			String tipoconn = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			//String autenticazioneHttp = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
			String user = null;
			String password = null;
			
			String connettoreDebug = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);
			
			// http
			String url = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
			if(TipiConnettore.HTTP.toString().equals(endpointtype)){
				user = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_USERNAME);
				password = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_PASSWORD);
			}
			
			// jms
			String nomeCodaJMS = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			String tipo = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			String initcont = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			String urlpgk = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			String provurl = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			String connfact = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			String sendas = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
			if(TipiConnettore.JMS.toString().equals(endpointtype)){
				user = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
				password = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			}
			
			// https
			String httpsurl = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_URL);
			String httpstipologia = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE);
			String httpshostverifyS = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			boolean httpshostverify = ServletUtils.isCheckBoxEnabled(httpshostverifyS);
			String httpspath = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
			String httpstipo = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			String httpspwd = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			String httpsalgoritmo = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			String httpsstatoS = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			boolean httpsstato = ServletUtils.isCheckBoxEnabled(httpsstatoS);
			String httpskeystore = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			String httpspwdprivatekeytrust = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			String httpspathkey = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			String httpstipokey = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			String httpspwdkey = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			String httpspwdprivatekey = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			String httpsalgoritmokey = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			if(TipiConnettore.HTTPS.toString().equals(endpointtype)){
				user = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_USERNAME);
				password = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_PASSWORD);
			}
						
			org.openspcoop2.core.registry.Connettore connettore = new org.openspcoop2.core.registry.Connettore();
			
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(connettore, ConnettoreServletType.WIZARD_REGISTRY, this.core, 
							this.request, this.session, false, endpointtype);
			
			String oldConnT = TipiConnettore.DISABILITATO.getNome();
			connettoriHelper.fillConnettore(connettore, connettoreDebug, endpointtype, oldConnT, tipoconn, url,
					nomeCodaJMS, tipo, user, password,
					initcont, urlpgk, provurl, connfact,
					sendas, httpsurl, httpstipologia,
					httpshostverify, httpspath, httpstipo,
					httpspwd, httpsalgoritmo, httpsstato,
					httpskeystore, httpspwdprivatekeytrust,
					httpspathkey, httpstipokey,
					httpspwdkey, httpspwdprivatekey,
					httpsalgoritmokey,
					listExtendedConnettore);
			
			return connettore;
			
		}finally{
			Utilities.setTipologiaConnettori(tipologiaConnettoriOriginale);
		}
	}
	
	public void addImportInformationMissingToDati(Vector<DataElement> dati,ImporterUtils importerUtils, FormFile ff,
			String protocolloSelect,String inputMode,String protocolloEffettivo,String inputType,boolean validazioneDocumenti,boolean updateEnabled,
			ImportInformationMissingCollection importInformationMissingCollection,
			ImportInformationMissingException importInformationMissingException,
			String modalitaAcquisizioneInformazioniProtocollo,List<PortType> portTypesOpenSPCoop,
			List<String> protocolliForModes,
			boolean readedDatiConnettori,
			Wizard wizard, int step) throws Exception{
		
		String oldMessage = this.pd.getMessage();
		if(oldMessage!=null && !"".equals(oldMessage)){
			oldMessage = ArchiviCostanti.LABEL_IMPORT_ERRORE+oldMessage;
		}else{
			oldMessage = "";
		}
		boolean showIntestazioneArchivio = true;
		boolean showSection = true;
		if(wizard!=null){
			String stepDescription = " (Fase "+step;
			if(wizard.getStep()>0){
				stepDescription+="/"+wizard.getStep();
			}
			stepDescription+=")";
			this.pd.setMessage(wizard.getDescrizione()+stepDescription+oldMessage);
			
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
		de.setType("hidden");
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_VALIDAZIONE_DOCUMENTI);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		de = new DataElement();
		de.setValue(""+updateEnabled);
		de.setType("hidden");
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_UPDATE_ENABLED);
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

			if ( importInformationMissingException.isMissingInfoSoggetto() ){
			
				List<String> soggettiLabel = null;
				try{
					soggettiLabel = importerUtils.getIdSoggetti(protocolliForModes,inputMode,
							importInformationMissingException.getMissingInfoSoggetto_protocollo(),
							importInformationMissingException.getMissingInfoSoggetto_tipoPdD(),
							wizard);
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
				de.setLabels(soggettiLabel.toArray(new String[1]));
				if(soggettiLabel.size()>1)
					de.setSelected(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_SOGGETTO_INPUT_UNDEFINDED);
				else
					de.setSelected(soggettiLabel.get(0));
				de.setSize(this.getSize());
				dati.addElement(de);
				
			}
	
			if ( importInformationMissingException.isMissingInfoVersione() ){
			
				de = new DataElement();
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_VERSIONE_INPUT);
				de.setType(DataElementType.TEXT_EDIT);
				de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_VERSIONE_INPUT);
				de.setValue("1");
				de.setSize(30);
				dati.addElement(de);
				
			}
			
			if ( importInformationMissingException.isMissingInfoAccordoServizioParteComune() ){
				
				List<String> accordiServizioParteComuneLabel = importerUtils.getIdAccordiServizioParteComune(protocolliForModes,inputMode);
				
				de = new DataElement();
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_SERVIZIO_PARTE_COMUNE_INPUT);
				de.setType(DataElementType.SELECT);
				de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_SERVIZIO_PARTE_COMUNE_INPUT);
				de.setValues(accordiServizioParteComuneLabel.toArray(new String[1]));
				de.setLabels(accordiServizioParteComuneLabel.toArray(new String[1]));
				de.setSelected(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_INPUT_UNDEFINDED);
				de.setSize(this.getSize());
				dati.addElement(de);
				
			}
			
			if ( importInformationMissingException.isMissingInfoAccordoCooperazione() ){
				
				List<String> accordiCooperazioneLabel = importerUtils.getIdAccordiCooperazione(protocolliForModes,inputMode);
				
				de = new DataElement();
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_COOPERAZIONE_INPUT);
				de.setType(DataElementType.SELECT);
				de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_COOPERAZIONE_INPUT);
				de.setValues(accordiCooperazioneLabel.toArray(new String[1]));
				de.setLabels(accordiCooperazioneLabel.toArray(new String[1]));
				de.setSelected(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_INPUT_UNDEFINDED);
				de.setSize(this.getSize());
				dati.addElement(de);
				
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
				BasicArchive basicArchive = new BasicArchive(null);
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
							de.setType(DataElementType.TEXT);
							de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_OPERATION_TITLE.
									replace(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_OPERATION_TITLE_KEY, opWSDL.getNome()));
							de.setValue(" ");
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
							de.setValues(this.core.getProfiliDiCollaborazioneSupportatiDalProtocollo(protocolloAccordo));
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

		}
		
		
		
		
		
		// invocazioneServizio
		
		if( importInformationMissingException.isMissingInfoInvocazioneServizio() ){
			
			InterfaceType originalInterfaceType = null;
			User userSession = null;
			try{
				
				// Impostazione protocollo
				String protocollo = importInformationMissingException.getMissingInfoSoggetto_protocollo();
				if(protocollo==null){
					//protocollo = protocolloEffettivo; // uso l'impostazione associata al tipo in package scelto.
					// NOTA: non devo impostare il protocollo. Serve solo per visualizzare l'info "Sbustamento Info Protocollo 'xxx'"
					// Se viene passato null non viene visualizzata la stringa 'xxx'.
					// Meglio cosi che fornire il tipo associato al package, che puo' non essere corretto nel casi di archivio openspcoop
					// che contiene diversi protocolli (e nella select list dei protocolli non e' stato scelto alcun protocollo)
				}
				
				userSession = ServletUtils.getUserFromSession(this.session);
				originalInterfaceType = userSession.getInterfaceType();
				// forzo standard
				userSession.setInterfaceType(InterfaceType.STANDARD);
			
				ServiziApplicativiHelper saHelper = new ServiziApplicativiHelper(this.request, this.pd, this.session);
				ConnettoriHelper connettoriHelper = new ConnettoriHelper(this.request, this.pd, this.session);
				
				String sbustamento = null;
				String sbustamentoInformazioniProtocolloRichiesta = null;
				String getmsg = null;
				
				if(readedDatiConnettori==false){
					sbustamento = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP);
					sbustamentoInformazioniProtocolloRichiesta = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RICHIESTA);
					getmsg = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX);
				}
				
				saHelper.addEndPointToDati(dati,"","",sbustamento,sbustamentoInformazioniProtocolloRichiesta,
						getmsg,null,null,protocollo,false,true, showSection);
				
				if(CostantiConfigurazione.ABILITATO.equals(getmsg)){
					
//					String tipoauth = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SA);
//					if (tipoauth == null) {
//						tipoauth = ServiziApplicativiCostanti.DEFAULT_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE;
//					}
//					String utente = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_USERNAME_SA);
//					String password = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_PASSWORD_SA);
//					String confpw = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_CONFERMA_PASSWORD_SA);
//					String subject = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_SUBJECT_SA);
//					
//					if(tipoauth==null || tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA)){
//						tipoauth = this.saCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
//					} 
//						
//					// Credenziali di accesso
//					if (utente == null) {
//						utente = "";
//					}
//					if (password == null) {
//						password = "";
//					}
//					if (confpw == null) {
//						confpw = "";
//					}
//					if (subject == null) {
//						subject = "";
//					}
//					dati = connettoriHelper.addCredenzialiToDati(dati, tipoauth, utente, password, confpw, subject, 
//							ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ADD, true, null, false, true, null);
					this.addDatiCredenzialiAccesso(dati, connettoriHelper, readedDatiConnettori, true);
					
				}
				
				addDatiConnettore(dati, connettoriHelper, readedDatiConnettori, true, ConnettoreServletType.WIZARD_CONFIG);
			}
			finally{
				// ripristino originale
				if(userSession!=null)
					userSession.setInterfaceType(originalInterfaceType);
			}
		}
		
		
		
		// connettore
		
		if( importInformationMissingException.isMissingInfoConnettore() ){
			
			InterfaceType originalInterfaceType = null;
			User userSession = null;
			try{
				userSession = ServletUtils.getUserFromSession(this.session);
				originalInterfaceType = userSession.getInterfaceType();
				// forzo standard
				userSession.setInterfaceType(InterfaceType.STANDARD);
			
				ConnettoriHelper connettoriHelper = new ConnettoriHelper(this.request, this.pd, this.session);
				
				addDatiConnettore(dati, connettoriHelper, readedDatiConnettori, showSection, ConnettoreServletType.WIZARD_REGISTRY);
			}
			finally{
				// ripristino originale
				if(userSession!=null)
					userSession.setInterfaceType(originalInterfaceType);
			}
		}
		
		
		
		// credenziali
		
		if( importInformationMissingException.isMissingInfoCredenziali() ){
			
			InterfaceType originalInterfaceType = null;
			User userSession = null;
			try{
				userSession = ServletUtils.getUserFromSession(this.session);
				originalInterfaceType = userSession.getInterfaceType();
				// forzo standard
				userSession.setInterfaceType(InterfaceType.STANDARD);
			
				ConnettoriHelper connettoriHelper = new ConnettoriHelper(this.request, this.pd, this.session);
				
				this.addDatiCredenzialiAccesso(dati, connettoriHelper, readedDatiConnettori, showSection);
			}
			finally{
				// ripristino originale
				if(userSession!=null)
					userSession.setInterfaceType(originalInterfaceType);
			}
		}
	}
	
	private void addDatiCredenzialiAccesso(Vector<DataElement> dati,ConnettoriHelper connettoriHelper, boolean readedDatiConnettori, boolean showSectionTitle){
				
		String tipoauth = null;
		String utente = null;
		String password  = null;
		String confpw = null;
		String subject = null;
		
		if(readedDatiConnettori==false){
			tipoauth = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_SA);
			if (tipoauth == null) {
				tipoauth = ServiziApplicativiCostanti.DEFAULT_SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE;
			}
			utente = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_USERNAME_SA);
			password = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_PASSWORD_SA);
			confpw = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_CONFERMA_PASSWORD_SA);
			subject = this.request.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_AUTENTICAZIONE_SUBJECT_SA);
			
			if(tipoauth==null || tipoauth.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_TIPO_AUTENTICAZIONE_NESSUNA)){
				tipoauth = this.saCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
			} 
				
			// Credenziali di accesso
			if (utente == null) {
				utente = "";
			}
			if (password == null) {
				password = "";
			}
			if (confpw == null) {
				confpw = "";
			}
			if (subject == null) {
				subject = "";
			}
		}
		dati = connettoriHelper.addCredenzialiToDati(dati, tipoauth, utente, password, confpw, subject, 
				ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ADD, showSectionTitle, null, false, true, null);
	}
	
	private void addDatiConnettore(Vector<DataElement> dati,ConnettoriHelper connettoriHelper, boolean readedDatiConnettori,
			boolean showSectionTitle,ConnettoreServletType connettoreServletType) throws Exception{
		
		TipologiaConnettori tipologiaConnettoriOriginale = null;
		try{
			tipologiaConnettoriOriginale = Utilities.getTipologiaConnettori(this.core);
			Utilities.setTipologiaConnettori(TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP);
		
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
			
			boolean httpshostverify = false;
			boolean httpsstato = false;
			
			if(readedDatiConnettori==false){
			
				endpointtype = connettoriHelper.readEndPointType();
				if(endpointtype==null){
					endpointtype = TipiConnettore.DISABILITATO.toString();
				}
				tipoconn = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
				autenticazioneHttp = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
				
				connettoreDebug = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);
				
				// http
				url = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
				if(TipiConnettore.HTTP.toString().equals(endpointtype)){
					user = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_USERNAME);
					password = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_PASSWORD);
				}
				
				// jms
				nomeCodaJMS = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
				tipo = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
				initcont = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
				urlpgk = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
				provurl = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
				connfact = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
				sendas = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
				if(TipiConnettore.JMS.toString().equals(endpointtype)){
					user = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
					password = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
				}
				
				// https
				httpsurl = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_URL);
				httpstipologia = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE);
				httpshostverifyS = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
				httpshostverify = ServletUtils.isCheckBoxEnabled(httpshostverifyS);
				httpspath = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
				httpstipo = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
				httpspwd = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
				httpsalgoritmo = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
				httpsstatoS = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
				httpsstato = ServletUtils.isCheckBoxEnabled(httpsstatoS);
				httpskeystore = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
				httpspwdprivatekeytrust = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
				httpspathkey = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
				httpstipokey = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
				httpspwdkey = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
				httpspwdprivatekey = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
				httpsalgoritmokey = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
				if(TipiConnettore.HTTPS.toString().equals(endpointtype)){
					user = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_USERNAME);
					password = this.request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_AUTENTICAZIONE_PASSWORD);
				}
				
			}

			Boolean isConnettoreCustomUltimaImmagineSalvata = null;
						
			if(endpointtype==null){
				endpointtype = TipiConnettore.DISABILITATO.toString();
			}
			
			Connettore conTmp = null;
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, connettoreServletType, this.core, 
							this.request, this.session, false, endpointtype);
			
			dati = connettoriHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, "",//ServiziApplicativiCostanti.LABEL_EROGATORE+" ",
					url, nomeCodaJMS,
					tipo, user, password, initcont, urlpgk, provurl,
					connfact, sendas, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, TipoOperazione.CHANGE, httpsurl, httpstipologia,
					httpshostverify, httpspath, httpstipo, httpspwd,
					httpsalgoritmo, httpsstato, httpskeystore,
					httpspwdprivatekeytrust, httpspathkey,
					httpstipokey, httpspwdkey, httpspwdprivatekey,
					httpsalgoritmokey, tipoconn, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT,
					"", "", null, null, null,
					null, null, showSectionTitle,
					isConnettoreCustomUltimaImmagineSalvata, listExtendedConnettore);
			
		}finally{
			// ripristino tipologia
			Utilities.setTipologiaConnettori(tipologiaConnettoriOriginale);
		}
	}

	
	
	public boolean accordiAllegatiCheckData(TipoOperazione tipoOperazione,FormFile formFile,Documento documento,ProprietariDocumento proprietario)
			throws Exception {

		try{

			String ruolo = this.request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_RUOLO);

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

			if(this.archiviCore.existsDocumento(documento,proprietario)){

				Documento existing = this.archiviCore.getDocumento(documento.getFile(),documento.getTipo(),documento.getRuolo(),documento.getIdProprietarioDocumento(),false,proprietario);
				if(existing.getId() == documento.getId())
					return true;

				if(RuoliDocumento.allegato.toString().equals(documento.getRuolo()))
					this.pd.setMessage("L'allegato con nome "+documento.getFile()+" (tipo: "+documento.getTipo()+") e' gia' presente nell'accordo.");
				else
					this.pd.setMessage("La specifica semiformale con nome "+documento.getFile()+" (tipo: "+documento.getTipo()+") e' gia' presente nell'accordo.");

				return false;
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public void addTracciamentoToDati(Vector<Object> dati,
			String [] datasourceList, String nomeDs,
			String datainizio,String datafine, String [] protocolli, String profcoll,
			String [] tipiLabel, String [] tipiServiziLabel, String indexValue){

		User user = ServletUtils.getUserFromSession(this.session);

		if (!InterfaceType.STANDARD.equals(user.getInterfaceType())) {
			if (this.core.isTracce_showSorgentiDatiDatabase()) {
				// Sorgente dati
				DataElement de = new DataElement();
				de.setLabel(ArchiviCostanti.LABEL_SORGENTE_DATI);
				de.setType(DataElementType.TITLE);
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_NOME_DATASOURCE);
				de.setType(DataElementType.SELECT);
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DATASOURCE);
				de.setValues(datasourceList);
				if (nomeDs != null) {
					de.setSelected(nomeDs);
				}
				dati.addElement(de);
			}
		}

		// Titolo Filter
		DataElement de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_FILTRO_RICERCA);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_DATA_INIZIO);
		de.setValue(datainizio != null ? datainizio : "");
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_INIZIO);
		de.setSize(this.getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_DATA_FINE);
		de.setValue(datafine != null ? datafine : "");
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_FINE);
		de.setSize(this.getSize());
		dati.addElement(de);
		
		// Identificativo Messaggio
		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_ID_MESSAGGIO);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO_SEARCH);
		de.setType(DataElementType.TEXT_EDIT);
		de.setValue(this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO_SEARCH) != null ? 
				this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO_SEARCH) : "");
		de.setSize(this.getSize());
		dati.addElement(de);

		// CorrelazioneApplicativa
		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA);
		de.setType(DataElementType.TEXT_EDIT);
		de.setValue(this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA) != null ? 
				this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA) : "");
		de.setSize(this.getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_PROTOCOLLO);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO);
		de.setType(DataElementType.SELECT);
		de.setValues(protocolli);
		de.setSelected(this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO) != null ? 
				this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO) : "-");
		de.setSize(this.getSize());
		de.setPostBack(true);
		dati.addElement(de);

		// Titolo Filter
		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_INFORMAZIONI_PROTOCOLLO);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);			

		String[] tipoProfcoll = { ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ONEWAY, 
				ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_SINCRONO,
				ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,
				ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,
				ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ANY };
		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_PROFILO_COLLABORAZIONE);
		de.setType(DataElementType.SELECT);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_PROFILO_COLLABORAZIONE);
		de.setValues(tipoProfcoll);
		de.setSelected(profcoll != null ? profcoll : ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ANY);
		dati.addElement(de);

		// Mittente
		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_SOGGETTO_MITTENTE);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_TIPO_MITTENTE);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_MITTENTE);
		de.setType(DataElementType.SELECT);
		de.setValues(tipiLabel);
		de.setSelected(this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_MITTENTE) != null ? 
				this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_MITTENTE) : "-");
		de.setSize(this.getSize());
		dati.addElement(de);

		// Soggetto Mittente (fruitore)
		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_NOME_MITTENTE);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_MITTENTE);
		de.setType(DataElementType.TEXT_EDIT);
		de.setValue(this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_MITTENTE) != null ? 
				this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_MITTENTE) : "");
		de.setSize(this.getSize());
		// de.setValues(idSoggetti);
		// de.setLabels(labelSoggetti);
		dati.addElement(de);

		// Destinatario
		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_SOGGETTO_DESTINATARIO);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		/*if (InterfaceType.STANDARD.equals(user.getInterfaceType())) {
			de = new DataElement();
			de.setLabel("Tipo Destinatario");
			de.setName("tipo_destinatario");
			de.setType("hidden");
			de.setValue("-");
			de.setSize(gh.getSize());
			dati.addElement(de);
		} else {*/
		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_TIPO_DESTINATARIO);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_DESTINATARIO);
		de.setType(DataElementType.SELECT);
		de.setSelected(this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_DESTINATARIO) != null ? 
				this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_DESTINATARIO) : "-");
		de.setValues(tipiLabel);
		de.setSize(this.getSize());
		dati.addElement(de);
		//}

		// Soggetto Destinatario (erogatore)
		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_NOME_DESTINATARIO);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DESTINATARIO);
		de.setValue(this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DESTINATARIO) != null ? 
				this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DESTINATARIO) : "");
		de.setType(DataElementType.TEXT_EDIT);
		de.setSize(this.getSize());
		// de.setValues(idSoggetti);
		// de.setLabels(labelSoggetti);
		dati.addElement(de);

		// Servizio
		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_SERVIZIO);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		/*if (InterfaceType.STANDARD.equals(user.getInterfaceType())) {
			de = new DataElement();
			de.setLabel("Tipo Servizio");
			de.setName("tipo_servizio");
			de.setType("hidden");
			de.setValue("-");
			dati.addElement(de);
		} else {*/
		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_TIPO_SERVIZIO);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_SERVIZIO);
		de.setType(DataElementType.SELECT);
		de.setSelected(this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_SERVIZIO) != null ? 
				this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_SERVIZIO) : "-");
		de.setValues(tipiServiziLabel);
		de.setSize(this.getSize());
		dati.addElement(de);
		//}


		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_NOME_SERVIZIO);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_SERVIZIO);
		de.setType(DataElementType.TEXT_EDIT);
		de.setSize(this.getSize());
		de.setValue(this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_SERVIZIO) != null ? 
				this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_SERVIZIO) : "");
		dati.addElement(de);

		// Azione
		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_AZIONE);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_AZIONE);
		de.setType(DataElementType.TEXT_EDIT);
		de.setSize(this.getSize());
		de.setValue(this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_AZIONE) != null ? 
				this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_AZIONE) : "");
		dati.addElement(de);

		de = new DataElement();
		de.setValue(indexValue);
		de.setType(DataElementType.HIDDEN);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_INDEX);
		dati.addElement(de);
	}


	public boolean tracciamentoCheckData() throws Exception {

		try{

			String datainizio = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_INIZIO);
			String datafine = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_FINE);
			String profcoll = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROFILO_COLLABORAZIONE);
			// String servizio = this.request.getParameter("servizio");
			String nomeDs = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DATASOURCE);

			String correlazioneApplicativa = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA);
			String idMessaggio = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO_SEARCH);
			
			/*
			 * // Campi obbligatori if (datainizio.equals("") ||
			 * datafine.equals("")) { String tmpElenco = ""; if
			 * (datainizio.equals("")) { tmpElenco = "Inizio intervallo"; } if
			 * (datafine.equals("")) { if (tmpElenco.equals("")) { tmpElenco =
			 * "Fine intervallo"; } else { tmpElenco = tmpElenco +
			 * ", Fine intervallo"; } }
			 * this.pd.setMessage("Dati incompleti. E' necessario indicare: " +
			 * tmpElenco); return false; }
			 */

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			if (!profcoll.equals(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO) && 
					!profcoll.equals(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO) && 
					!profcoll.equals(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_SINCRONO) && 
					!profcoll.equals(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ONEWAY) && 
					!profcoll.equals(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ANY)) {
				this.pd.setMessage("Il profilo di collaborazione dev'essere "+ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO
						+", "+ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO
						+", "+ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_SINCRONO
						+", "+ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ONEWAY
						+" o "+ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ANY);
				return false;
			}

			// Controlli sulle date
			if ( (datainizio == null || "".equals(datainizio)) 
					&&
					(correlazioneApplicativa == null || "".equals(correlazioneApplicativa)) 
					&&
					(idMessaggio == null || "".equals(idMessaggio)) ) {
				this.pd.setMessage("Deve essere indicato almeno uno dei seguenti criteri di ricerca: Intervallo Iniziale, ID Messaggio, ID Applicativo");
				return false;
			}
			if (!"".equals(datainizio)) {
				boolean dataInOk = true;
				if (datainizio.length() != 10) {
					dataInOk = false;
				} else {
					String anno = datainizio.substring(0, 4);
					String mese = datainizio.substring(5, 7);
					String giorno = datainizio.substring(8, 10);
					if (!RegularExpressionEngine.isMatch(anno,"^[0-9]+$")) {
						dataInOk = false;
					} else if (!datainizio.substring(4, 5).equals("-")) {
						dataInOk = false;
					} else if (!RegularExpressionEngine.isMatch(mese,"^[0-9]+$")) {
						dataInOk = false;
					} else if (!datainizio.substring(7, 8).equals("-")) {
						dataInOk = false;
					} else if (!RegularExpressionEngine.isMatch(giorno,"^[0-9]+$")) {
						dataInOk = false;
					} else {
						int annoI = Integer.parseInt(anno);
						int meseI = Integer.parseInt(mese);
						int giornoI = Integer.parseInt(giorno);
						if ((meseI == 1) || (meseI == 3) || (meseI == 5) || (meseI == 7) || (meseI == 8) || (meseI == 10) || (meseI == 12)) {
							if ((giornoI == 0) || (giornoI > 31)) {
								dataInOk = false;
							}
						} else {
							if ((meseI == 4) || (meseI == 6) || (meseI == 9) || (meseI == 11)) {
								if ((giornoI == 0) || (giornoI > 30)) {
									dataInOk = false;
								}
							} else {
								if (meseI == 2) {
									int bisestile = annoI / 4;
									if (annoI != bisestile * 4) {
										if ((giornoI == 0) || (giornoI > 28)) {
											dataInOk = false;
										}
									} else if ((giornoI == 0) || (giornoI > 29)) {
										dataInOk = false;
									}
								} else {
									dataInOk = false;
								}
							}
						}
					}
				}
				if (!dataInOk) {
					this.pd.setMessage("Inizio intervallo espresso in una forma sbagliata. Esprimerlo come aaaa-mm-gg");
					return false;
				}
			}

			if (!"".equals(datafine)) {
				boolean dataFineOk = true;
				if (datafine.length() != 10) {
					dataFineOk = false;
				} else {
					String anno = datafine.substring(0, 4);
					String mese = datafine.substring(5, 7);
					String giorno = datafine.substring(8, 10);
					if (!RegularExpressionEngine.isMatch(anno,"^[0-9]+$")) {
						dataFineOk = false;
					} else if (!datafine.substring(4, 5).equals("-")) {
						dataFineOk = false;
					} else if (!RegularExpressionEngine.isMatch(mese,"^[0-9]+$")) {
						dataFineOk = false;
					} else if (!datafine.substring(7, 8).equals("-")) {
						dataFineOk = false;
					} else if (!RegularExpressionEngine.isMatch(giorno,"^[0-9]+$")) {
						dataFineOk = false;
					} else {
						int annoI = Integer.parseInt(anno);
						int meseI = Integer.parseInt(mese);
						int giornoI = Integer.parseInt(giorno);
						if ((meseI == 1) || (meseI == 3) || (meseI == 5) || (meseI == 7) || (meseI == 8) || (meseI == 10) || (meseI == 12)) {
							if ((giornoI == 0) || (giornoI > 31)) {
								dataFineOk = false;
							}
						} else {
							if ((meseI == 4) || (meseI == 6) || (meseI == 9) || (meseI == 11)) {
								if ((giornoI == 0) || (giornoI > 30)) {
									dataFineOk = false;
								}
							} else {
								if (meseI == 2) {
									int bisestile = annoI / 4;
									if (annoI != bisestile * 4) {
										if ((giornoI == 0) || (giornoI > 28)) {
											dataFineOk = false;
										}
									} else if ((giornoI == 0) || (giornoI > 29)) {
										dataFineOk = false;
									}
								} else {
									dataFineOk = false;
								}
							}
						}
					}
				}
				if (!dataFineOk) {
					this.pd.setMessage("Fine intervallo espresso in una forma sbagliata. Esprimerlo come aaaa-mm-gg");
					return false;
				}
			}

			if (!InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
				if (this.core.isMsgDiagnostici_showSorgentiDatiDatabase()) {
					if(nomeDs==null || "".equals(nomeDs)){
						this.pd.setMessage("E' necessario selezionare una sorgente dati.");
						return false;
					}
				}
			}


			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public boolean tracciamentoStatoList(String nomeDsDiag,boolean forceChange, ISearch ricerca) throws Exception {

		try {

			DriverTracciamento driverTracciamento = this.archiviCore.getDriverTracciamento(nomeDsDiag, forceChange);

			ServletUtils.addListElementIntoSession(this.session, ArchiviCostanti.OBJECT_NAME_ARCHIVI_TRACCIAMENTO);

			int idLista = Liste.TRACCE;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			if (this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_INDEX) != null) {
				offset = Integer.parseInt(this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_INDEX));
				ricerca.setIndexIniziale(idLista, offset);
			}
			if (this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PAGE_SIZE) != null) {
				limit = Integer.parseInt(this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PAGE_SIZE));
				ricerca.setPageSize(idLista, limit);
			}

			String search = ServletUtils.getSearchFromSession(ricerca, idLista);
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, ArchiviCostanti.LABEL_TRACCE, search);
			}
			if (this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_SEARCH) != null) {
				search = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_SEARCH);
				search = search.trim();
				if (search.equals("")) {
					ricerca.setSearchString(idLista, org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED);
				} else {
					ricerca.setSearchString(idLista, search);
				}
			}
			//NOTA: Eliminare il codice sotto se si vuole accendere la ricerca. Deve pero' essere usato poi il valore di search in un qualche filtro 
			this.pd.setSearch(org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED);
			this.pd.setSearchDescription(null);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);

			String nomeDs = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DATASOURCE);
			String datainizio = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_INIZIO);
			String datafine = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_FINE);
			String profcoll = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROFILO_COLLABORAZIONE);
			String tipo_servizio = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_SERVIZIO);
			String servizio = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_SERVIZIO);
			String tipo_mittente = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_MITTENTE);
			String nome_mittente = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_MITTENTE);
			String tipo_destinatario = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_DESTINATARIO);
			String nome_destinatario = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DESTINATARIO);
			String nome_azione = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_AZIONE);
			String correlazioneApplicativa = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA);
			String protocollo = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO);
			String identificativoMessaggio = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO_SEARCH);

//			// setto la barra del titolo
//			String params = "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DATASOURCE+"=" + (nomeDs==null ? "" : nomeDs) 
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_PROFILO_COLLABORAZIONE+"=" + profcoll  
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_INIZIO+"=" + (datainizio == null ? "" : datainizio) 
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_FINE+"=" + (datafine == null ? "" : datafine) 
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_MITTENTE+"=" + (tipo_mittente == null ? "" : tipo_mittente) 
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_MITTENTE+"=" + (nome_mittente == null ? "" : nome_mittente) 
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_DESTINATARIO+"=" + (tipo_destinatario == null ? "" : tipo_destinatario) 
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DESTINATARIO+"=" + (nome_destinatario == null ? "" : nome_destinatario) 
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_SERVIZIO+"=" + (tipo_servizio == null ? "" : tipo_servizio) 
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_SERVIZIO+"=" + (servizio == null ? "" : servizio) 
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_AZIONE+"=" + (nome_azione == null ? "" : nome_azione)
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA+"=" + (correlazioneApplicativa == null ? "" : correlazioneApplicativa)
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO+"=" + (protocollo == null ? "" : protocollo)
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO+"=" + (identificativoMessaggio == null ? "" : identificativoMessaggio);
			
			Parameter pDs = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DATASOURCE, (nomeDs==null ? "" : nomeDs));
			Parameter pProfColl = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROFILO_COLLABORAZIONE,profcoll);
			Parameter pDataI = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_INIZIO,(datainizio == null ? "" : datainizio) );
			Parameter pDataF = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_FINE,(datafine == null ? "" : datafine) );
			Parameter pTipoM = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_MITTENTE,(tipo_mittente == null ? "" : tipo_mittente) );
			Parameter pNomeM = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_MITTENTE, (nome_mittente == null ? "" : nome_mittente));
			Parameter pTipoD = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_DESTINATARIO, (tipo_destinatario == null ? "" : tipo_destinatario) );
			Parameter pNomeD = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DESTINATARIO,(nome_destinatario == null ? "" : nome_destinatario) );
			Parameter pTipoS = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_SERVIZIO, (tipo_servizio == null ? "" : tipo_servizio) );
			Parameter pNomeS = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_SERVIZIO,(servizio == null ? "" : servizio) );
			Parameter pAzione = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_AZIONE,(nome_azione == null ? "" : nome_azione));
			Parameter pCorrAppl = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA,(correlazioneApplicativa == null ? "" : correlazioneApplicativa));
			Parameter pProt = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO, (protocollo == null ? "" : protocollo));
			Parameter pIdentMsg = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO_SEARCH, (identificativoMessaggio == null ? "" : identificativoMessaggio));

			 
			
//					params = URLEncoder.encode(params, "UTF-8");
			
			

			ServletUtils.setPageDataTitle(this.pd, 
					new Parameter(ArchiviCostanti.LABEL_REPORTISTICA,null),
					new Parameter(ArchiviCostanti.LABEL_TRACCIAMENTO,ArchiviCostanti.SERVLET_NAME_ARCHIVI_TRACCIAMENTO,
							pDs, pProfColl, pDataI, pDataF, pTipoM, pNomeM, pTipoD, pNomeD, pTipoS, pNomeS, pAzione, pCorrAppl, pProt, pIdentMsg),
					new Parameter(ArchiviCostanti.LABEL_TRACCE,null));


			// setto le label delle colonne
			String[] labels = { ArchiviCostanti.LABEL_DATA,
					ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_PROTOCOLLO, 
					ArchiviCostanti.LABEL_MITTENTE, 
					ArchiviCostanti.LABEL_DESTINATARIO, 
					ArchiviCostanti.LABEL_SERVIZIO_AZIONE,
					ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_ID_MESSAGGIO,
					ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_ID_MESSAGGIO_RISPOSTA_BREVE,
					ArchiviCostanti.LABEL_TIPO_MESSAGGIO};
			this.pd.setLabels(labels);


			// preparo i dati
			Vector<Object> dati = new Vector<Object>();

			// setto dei campi hidden con i valori della query

			Hashtable<String, String> campiHidden = new Hashtable<String, String>();
			campiHidden.put(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_INIZIO, datainizio);
			campiHidden.put(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_FINE, datafine);
			campiHidden.put(ArchiviCostanti.PARAMETRO_ARCHIVI_PROFILO_COLLABORAZIONE, profcoll);
			campiHidden.put(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_SERVIZIO, servizio);
			this.pd.setHidden(campiHidden);

			if (tipo_destinatario == null || "".equals(tipo_destinatario) || "-".equals(tipo_destinatario))
				tipo_destinatario = null;
			if (nome_destinatario == null || "".equals(nome_destinatario))
				nome_destinatario = null;
			if (tipo_servizio == null || "".equals(tipo_servizio) || "-".equals(tipo_servizio))
				tipo_servizio = null;
			if (servizio == null || "".equals(servizio))
				servizio = null;
			if (tipo_mittente == null || "".equals(tipo_mittente) || "-".equals(tipo_mittente))
				tipo_mittente = null;
			if (nome_mittente == null || "".equals(nome_mittente))
				nome_mittente = null;
			if (nome_azione == null || "".equals(nome_azione))
				nome_azione = null;
			if (correlazioneApplicativa == null || "".equals(correlazioneApplicativa))
				correlazioneApplicativa = null;
			if (protocollo == null || "".equals(protocollo) || "-".equals(protocollo))
				protocollo = null;
			if (identificativoMessaggio == null || "".equals(identificativoMessaggio))
				identificativoMessaggio = null;

			// Conversione Data
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd"); // SimpleDateFormat non e' thread-safe
			Date dataInizioData = !"".equals(datainizio) ? simpleDateFormat.parse(datainizio) : null;
			Date dataFineData = !"".equals(datafine) ? simpleDateFormat.parse(datafine) : null;

			// bug fix
			// data fine inidica l intera giornata quinid aggiungo 1 giorno alla
			// data
			// inserita
			if (dataFineData != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataFineData);
				cal.add(Calendar.DAY_OF_WEEK, 1);
				dataFineData = cal.getTime();
			}

			ProfiloDiCollaborazione myProfColl = null;
			if (profcoll.equals(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO)) {
				myProfColl = ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO;
			}
			if (profcoll.equals(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO)) {
				myProfColl = ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO;
			}
			if (profcoll.equals(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_SINCRONO)) {
				myProfColl = ProfiloDiCollaborazione.SINCRONO;
			}
			if (profcoll.equals(ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ONEWAY)) {
				myProfColl = ProfiloDiCollaborazione.ONEWAY;
			}

			String userLogin = (String) ServletUtils.getUserLoginFromSession(this.session);
			List<IDSoggetto> filtroSoggetti = null;
			if(this.core.isVisioneOggettiGlobale(userLogin)==false){
				filtroSoggetti = this.soggettiCore.getSoggettiWithSuperuser(userLogin);
			}

			FiltroRicercaTracceConPaginazione filtro = new FiltroRicercaTracceConPaginazione();
			if (dataInizioData != null)
				filtro.setMinDate(dataInizioData);
			if (dataFineData != null)
				filtro.setMaxDate(dataFineData);

			org.openspcoop2.protocol.sdk.tracciamento.InformazioniProtocollo informazioniProtocollo = null;

			IDSoggetto idMittente = null;
			if (tipo_mittente != null){
				if(idMittente==null){
					idMittente = new IDSoggetto();
				}
				idMittente.setTipo(tipo_mittente);
			}
			if (nome_mittente != null){
				if(idMittente==null){
					idMittente = new IDSoggetto();
				}
				idMittente.setNome(nome_mittente);
			}
			if(idMittente!=null){
				if(informazioniProtocollo==null){
					informazioniProtocollo = new org.openspcoop2.protocol.sdk.tracciamento.InformazioniProtocollo();
				}
				informazioniProtocollo.setMittente(idMittente);
			}


			IDSoggetto idDestinatario = null;
			if (tipo_destinatario != null){
				if(idDestinatario==null){
					idDestinatario = new IDSoggetto();
				}
				idDestinatario.setTipo(tipo_destinatario);
			}
			if (nome_destinatario != null){
				if(idDestinatario==null){
					idDestinatario = new IDSoggetto();
				}
				idDestinatario.setNome(nome_destinatario);
			}
			if(idDestinatario!=null){
				if(informazioniProtocollo==null){
					informazioniProtocollo = new org.openspcoop2.protocol.sdk.tracciamento.InformazioniProtocollo();
				}
				informazioniProtocollo.setDestinatario(idDestinatario);
			}


			if (tipo_servizio != null){
				if(informazioniProtocollo==null){
					informazioniProtocollo = new org.openspcoop2.protocol.sdk.tracciamento.InformazioniProtocollo();
				}
				informazioniProtocollo.setTipoServizio(tipo_servizio);
			}
			if (servizio != null){
				if(informazioniProtocollo==null){
					informazioniProtocollo = new org.openspcoop2.protocol.sdk.tracciamento.InformazioniProtocollo();
				}
				informazioniProtocollo.setServizio(servizio);
			}
			if (nome_azione != null){
				if(informazioniProtocollo==null){
					informazioniProtocollo = new org.openspcoop2.protocol.sdk.tracciamento.InformazioniProtocollo();
				}
				informazioniProtocollo.setAzione(nome_azione);
			}


			if (!ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ANY.equals(profcoll)){
				if(informazioniProtocollo==null){
					informazioniProtocollo = new org.openspcoop2.protocol.sdk.tracciamento.InformazioniProtocollo();
				}
				informazioniProtocollo.setProfiloCollaborazioneEngine(myProfColl);
			}

			filtro.setInformazioniProtocollo(informazioniProtocollo);


			if(filtroSoggetti!=null && filtroSoggetti.size()>0){
				filtro.setFiltroSoggetti(filtroSoggetti);
			}

			if (correlazioneApplicativa != null){
				filtro.setIdCorrelazioneApplicativa(correlazioneApplicativa);
				filtro.setIdCorrelazioneApplicativaRisposta(correlazioneApplicativa);
				filtro.setIdCorrelazioneApplicativaOrMatch(true);
			}

			if (protocollo != null){
				filtro.setProtocollo(protocollo);
			}
			
			// Identificativo messaggio
			if (identificativoMessaggio != null && !"".equals(identificativoMessaggio)) {
				filtro.setIdBusta(identificativoMessaggio);
			}

			int count = driverTracciamento.countTracce(filtro);
			this.pd.setNumEntries(count);


			// ricavo le entries
			if (limit == 0) {
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			}
			filtro.setLimit(limit);
			filtro.setOffset(offset);
			filtro.setAsc(false);

			List<Traccia> listaTracce = null;
			try{
				listaTracce = driverTracciamento.getTracce(filtro);
			}catch(DriverTracciamentoNotFoundException dNot){
				listaTracce = new ArrayList<Traccia>();
			}

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss"); // SimpleDateFormat non e' thread-safe
			boolean setExportButton = false;
			for (Traccia traccia : listaTracce) {

				if (!setExportButton)
					setExportButton = true;

				Vector<DataElement> e = new Vector<DataElement>();

				DataElement de = new DataElement();

				// data
				de = new DataElement();
				de.setValue(formatter.format(traccia.getGdo()));
				e.addElement(de);

				// Protocollo
				de = new DataElement();
				de.setValue(traccia.getProtocollo());
				e.addElement(de);

				Busta busta = traccia.getBusta();

				// Mittente
				de = new DataElement();
				de.setValue(busta.getTipoMittente() + "/" + busta.getMittente());
				e.addElement(de);
				// Destinatario
				de = new DataElement();
				de.setValue(busta.getTipoDestinatario() + "/" + busta.getDestinatario());
				e.addElement(de);

				// Servizio[@Azione]
				String tipo_servizio_db = busta.getTipoServizio();
				String servizio_db = busta.getServizio();
				String azione_db = busta.getAzione();
				de = new DataElement();
				if(tipo_servizio_db!=null && servizio_db!=null)
					de.setValue(tipo_servizio_db + "/" + servizio_db + (azione_db != null ? "[@" + azione_db + "]" : ""));
				else
					de.setValue("");
				e.addElement(de);

				Parameter pCodPorta = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CODICE_PORTA,  traccia.getIdSoggetto().getCodicePorta());
				Parameter pNomeSoggPorta = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_SOGGETTO_PORTA, traccia.getIdSoggetto().getNome() );
				Parameter pTipoSoggPorta = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_SOGGETTO_PORTA, traccia.getIdSoggetto().getTipo() );
				Parameter pIdMsg = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO,  busta.getID());
				// id
				de = new DataElement();
				ServletUtils.setDataElementVisualizzaLabel(de);
				de.setToolTip(busta.getID());
				Parameter pUrl = new Parameter("", ArchiviCostanti.SERVLET_NAME_ARCHIVI_TRACCIAMENTO_TESTO, pDs, pProfColl, pDataI, pDataF, pTipoM, pNomeM, pTipoD,
						pNomeD, pTipoS, pNomeS, pAzione, pCorrAppl, pProt, pIdentMsg, pCodPorta, pNomeSoggPorta, pTipoSoggPorta, pIdMsg);
				de.setUrl(pUrl.getValue());
				e.addElement(de);

				// riferimento messaggio
				Parameter pRifMesg = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO, busta.getRiferimentoMessaggio());
				de = new DataElement();
				de.setValue(busta.getRiferimentoMessaggio() != null ? "visualizza" : "-");
				if (busta.getRiferimentoMessaggio() != null)
					de.setToolTip(busta.getRiferimentoMessaggio());
				if (busta.getRiferimentoMessaggio() != null){
					Parameter pUrl2 = new Parameter("", ArchiviCostanti.SERVLET_NAME_ARCHIVI_TRACCIAMENTO_TESTO, pDs, pProfColl, pDataI, pDataF, pTipoM, pNomeM, pTipoD,
							pNomeD, pTipoS, pNomeS, pAzione, pCorrAppl, pProt, pCodPorta, pNomeSoggPorta, pTipoSoggPorta, pRifMesg,pIdentMsg);
					de.setUrl(pUrl2.getValue());
				}
				e.addElement(de);
				// tipo
				// riferimento messaggio
				de = new DataElement();
				de.setValue(traccia.getTipoMessaggio().toString());
				e.addElement(de);

				dati.addElement(e);
			}

			this.pd.setSelect(false);

			// area bottoni
			if (setExportButton) {
				Parameter pExport = new Parameter("", ArchiviCostanti.SERVLET_NAME_TRACCE_EXPORT, pDs, pProfColl, pDataI, pDataF, pTipoM, pNomeM, pTipoD, pNomeD, pTipoS, pNomeS, pAzione, pCorrAppl, pProt, pIdentMsg);
				
				Vector<AreaBottoni> areaBottoni = new Vector<AreaBottoni>();
				DataElement saveAs = new DataElement();
				saveAs.setValue(ArchiviCostanti.LABEL_ESPORTA_XML);
				saveAs.setOnClick("Export('"+pExport.getValue() + "')");
				Vector<DataElement> bottoni = new Vector<DataElement>();
				bottoni.add(saveAs);
				AreaBottoni area = new AreaBottoni();
				area.setBottoni(bottoni);
				areaBottoni.add(area);

				this.pd.setAreaBottoni(areaBottoni);
			}

			this.pd.setDati(dati);

			
			String params = ServletUtils.getParametersAsString(false, pDs, pProfColl, pDataI, pDataF, pTipoM, pNomeM, pTipoD, pNomeD, pTipoS, pNomeS, pAzione, pCorrAppl, pProt, pIdentMsg);
			this.request.setAttribute(ArchiviCostanti.PARAMETRI_ARCHIVI, params);

			return false;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}






	public void addDiagnosticaTestoToDati(Vector<DataElement> dati,MsgDiagnostico msgDiag, 
			String idMessaggio, String idMessaggioRisposta, String idporta){

		// Informazioni
		DataElement de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_DETTAGLIO_DIAGNOSTICO);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_PROTOCOLLO);
		de.setValue(msgDiag.getProtocollo());
		de.setType(DataElementType.TEXT);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO);
		dati.addElement(de);

		if(idMessaggio!=null && !"".equals(idMessaggio)){
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_ID_MESSAGGIO);
			de.setValue(idMessaggio);
			de.setType(DataElementType.TEXT);
			de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO);
			dati.addElement(de);
		}

		if(idMessaggioRisposta!=null && !"".equals(idMessaggioRisposta)){
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_ID_MESSAGGIO_RISPOSTA);
			de.setValue(idMessaggioRisposta);
			de.setType(DataElementType.TEXT);
			de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO_RISPOSTA);
			dati.addElement(de);
		}

		if(idporta!=null && !"".equals(idporta)){
			de = new DataElement();
			de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_ID_PORTA);
			de.setValue(idporta);
			de.setType(DataElementType.TEXT);
			de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_PORTA);
			dati.addElement(de);
		}

		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_MESSAGGIO);
		if(msgDiag.getMessaggio()!=null && !"".equals(msgDiag.getMessaggio())){
			de.setValue(org.apache.commons.lang.StringEscapeUtils.escapeXml(msgDiag.getMessaggio()));
		}else{
			de.setValue(msgDiag.getMessaggio());
		}
		de.setType(DataElementType.TEXT);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_MESSAGGIO);
		dati.addElement(de);
	}


	public void addDiagnosticaToDati(Vector<DataElement> dati,
			String[]datasourceList,String nomeDs,
			String datainizio,String datafine, String severita, String idfunzione,
			String[] protocolli,String [] tipiLabel, String [] tipiServiziLabel, String indexValue){

		User user = ServletUtils.getUserFromSession(this.session);

		if (!InterfaceType.STANDARD.equals(user.getInterfaceType())) {
			if (this.core.isMsgDiagnostici_showSorgentiDatiDatabase()) {
				// Sorgente dati
				DataElement de = new DataElement();
				de.setLabel(ArchiviCostanti.LABEL_SORGENTE_DATI);
				de.setType(DataElementType.TITLE);
				dati.addElement(de);

				de = new DataElement();
				de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_NOME_DATASOURCE);
				de.setType(DataElementType.SELECT);
				de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DATASOURCE);
				de.setValues(datasourceList);
				if (nomeDs != null) {
					de.setSelected(nomeDs);
				}
				dati.addElement(de);
			}
		}

		// Titolo Filter
		DataElement de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_FILTRO_RICERCA);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_DATA_INIZIO);
		de.setValue(datainizio != null ? datainizio : "");
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_INIZIO);
		de.setSize(this.getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_DATA_FINE);
		de.setValue(datafine != null ? datafine : "");
		de.setType(DataElementType.TEXT_EDIT);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_FINE);
		de.setSize(this.getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_DIAGNOSTICI_SEVERITA);
		de.setType(DataElementType.SELECT);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_DIAGNOSTICI_SEVERITA);
		de.setValues(ArchiviCostanti.PARAMETRO_VALORI_ARCHIVI_DIAGNOSTICA_SEVERITA);
		de.setSelected(severita != null ? severita : ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_DIAGNOSTICA_SEVERITA_DEFAULT);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_ID_FUNZIONE);
		de.setType(DataElementType.SELECT);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_FUNZIONE);
		de.setValues(ArchiviCostanti.PARAMETRO_VALORI_ARCHIVI_ID_FUNZIONE);
		de.setSelected(idfunzione != null ? idfunzione : "-");
		dati.addElement(de);
		
		// Identificativo Messaggio
		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_ID_MESSAGGIO);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO_SEARCH);
		de.setType(DataElementType.TEXT_EDIT);
		de.setValue(this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO_SEARCH) != null ? 
				this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO_SEARCH) : "");
		de.setSize(this.getSize());
		dati.addElement(de);

		// CorrelazioneApplicativa
		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA);
		de.setType(DataElementType.TEXT_EDIT);
		de.setValue(this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA) != null ? 
				this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA) : "");
		de.setSize(this.getSize());
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_PROTOCOLLO);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO);
		de.setType(DataElementType.SELECT);
		de.setValues(protocolli);
		de.setSelected(this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO) != null ? 
				this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO) : "-");
		de.setSize(this.getSize());
		de.setPostBack(true);
		dati.addElement(de);

		// Mittente
		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_SOGGETTO_MITTENTE);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_TIPO_MITTENTE);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_MITTENTE);
		de.setType(DataElementType.SELECT);
		de.setValues(tipiLabel);
		de.setSelected(this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_MITTENTE) != null ? 
				this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_MITTENTE) : "-");
		de.setSize(this.getSize());
		dati.addElement(de);

		// Soggetto Mittente (fruitore)
		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_NOME_MITTENTE);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_MITTENTE);
		de.setType(DataElementType.TEXT_EDIT);
		de.setValue(this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_MITTENTE) != null ? this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_MITTENTE) : "");
		de.setSize(this.getSize());
		// de.setValues(idSoggetti);
		// de.setLabels(labelSoggetti);
		dati.addElement(de);

		// Destinatario
		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_SOGGETTO_DESTINATARIO);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		/*if (InterfaceType.STANDARD.equals(user.getInterfaceType())) {
			de = new DataElement();
			de.setLabel("Tipo Destinatario");
			de.setName("tipo_destinatario");
			de.setType(DataElementType.HIDDEN);
			de.setValue("-");
			de.setSize(this.getSize());
			dati.addElement(de);
		} else {*/
		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_TIPO_DESTINATARIO);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_DESTINATARIO);
		de.setType(DataElementType.SELECT);
		de.setSelected(this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_DESTINATARIO) != null ? 
				this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_DESTINATARIO) : "-");
		de.setValues(tipiLabel);
		de.setSize(this.getSize());
		dati.addElement(de);
		//}

		// Soggetto Destinatario (erogatore)
		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_NOME_DESTINATARIO);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DESTINATARIO);
		de.setValue(this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DESTINATARIO) != null ? 
				this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DESTINATARIO) : "");
		de.setType(DataElementType.TEXT_EDIT);
		de.setSize(this.getSize());
		// de.setValues(idSoggetti);
		// de.setLabels(labelSoggetti);
		dati.addElement(de);

		// Servizio
		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_SERVIZIO);
		de.setType(DataElementType.TITLE);
		dati.addElement(de);

		/*if (InterfaceType.STANDARD.equals(user.getInterfaceType())) {
			de = new DataElement();
			de.setLabel("Tipo Servizio");
			de.setName("tipo_servizio");
			de.setType(DataElementType.HIDDEN);
			de.setValue("-");
			dati.addElement(de);
		} else {*/
		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_TIPO_SERVIZIO);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_SERVIZIO);
		de.setType(DataElementType.SELECT);
		de.setSelected(this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_SERVIZIO) != null ? 
				this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_SERVIZIO) : "-");
		de.setValues(tipiServiziLabel);
		de.setSize(this.getSize());
		dati.addElement(de);
		//}

		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_NOME_SERVIZIO);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_SERVIZIO);
		de.setType(DataElementType.TEXT_EDIT);
		de.setSize(this.getSize());
		de.setValue(this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_SERVIZIO) != null ? 
				this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_SERVIZIO) : "");
		dati.addElement(de);

		// Azione
		de = new DataElement();
		de.setLabel(ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_AZIONE);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_AZIONE);
		de.setType(DataElementType.TEXT_EDIT);
		de.setSize(this.getSize());
		de.setValue(this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_AZIONE) != null ? 
				this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_AZIONE) : "");
		dati.addElement(de);

		de = new DataElement();
		de.setValue(indexValue);
		de.setType(DataElementType.HIDDEN);
		de.setName(ArchiviCostanti.PARAMETRO_ARCHIVI_INDEX);
		dati.addElement(de);

	}

	// Controlla i dati della diagnostica
	boolean diagnosticaCheckData() throws Exception {

		try{

			String severita = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DIAGNOSTICI_SEVERITA);
			String idfunzione = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_FUNZIONE);
			String datainizio = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_INIZIO);
			String datafine = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_FINE);
			String nomeDs = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DATASOURCE);
			
			String correlazioneApplicativa = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA);
			String idMessaggio = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO_SEARCH);

			// Campi obbligatori
			/*
			 * if (datainizio.equals("") || datafine.equals("")) { String tmpElenco
			 * = ""; if (datainizio.equals("")) { tmpElenco = "Inizio intervallo"; }
			 * if (datafine.equals("")) { if (tmpElenco.equals("")) { tmpElenco =
			 * "Fine intervallo"; } else { tmpElenco = tmpElenco +
			 * ", Fine intervallo"; } }
			 * this.pd.setMessage("Dati incompleti. E' necessario indicare: " +
			 * tmpElenco); return false; }
			 */

			// Controllo che i campi "select" abbiano uno dei valori ammessi
			boolean foundSeverita = false;
			StringBuffer bfSeverita = new StringBuffer();
			for (int i = 0; i < ArchiviCostanti.PARAMETRO_VALORI_ARCHIVI_DIAGNOSTICA_SEVERITA.length; i++) {
				if(i>0){
					bfSeverita.append(",");
				}
				bfSeverita.append(ArchiviCostanti.PARAMETRO_VALORI_ARCHIVI_DIAGNOSTICA_SEVERITA[i]);
				if(ArchiviCostanti.PARAMETRO_VALORI_ARCHIVI_DIAGNOSTICA_SEVERITA[i].equals(severita)){
					foundSeverita=true;
				}
			}
			if (!foundSeverita) {
				this.pd.setMessage("Livello severit&agrave; dev'essere uno dei seguenti valori: "+bfSeverita.toString());
				return false;
			}


			boolean foundIdFunzione = false;
			StringBuffer bfIdFunzione = new StringBuffer();
			for (int i = 0; i < ArchiviCostanti.PARAMETRO_VALORI_ARCHIVI_ID_FUNZIONE.length; i++) {
				if(i>0){
					bfIdFunzione.append(",");
				}
				bfSeverita.append(ArchiviCostanti.PARAMETRO_VALORI_ARCHIVI_ID_FUNZIONE[i]);
				if(ArchiviCostanti.PARAMETRO_VALORI_ARCHIVI_ID_FUNZIONE[i].equals(idfunzione)){
					foundIdFunzione=true;
				}
			}
			if (!idfunzione.equals("-") && !foundIdFunzione) {
				this.pd.setMessage("Identificativo funzione dev'essere selezionato tra le opzioni indicate: "+bfIdFunzione.toString());
				return false;
			}

			// Controlli sulle date
			if ( (datainizio == null || "".equals(datainizio)) 
					&&
					(correlazioneApplicativa == null || "".equals(correlazioneApplicativa)) 
					&&
					(idMessaggio == null || "".equals(idMessaggio)) ) {
				this.pd.setMessage("Deve essere indicato almeno uno dei seguenti criteri di ricerca: Intervallo Iniziale, ID Messaggio, ID Applicativo");
				return false;
			}
			if (datainizio != null && !"".equals(datainizio)) {
				boolean dataInOk = true;
				if (datainizio.length() != 10) {
					dataInOk = false;
				} else {
					String anno = datainizio.substring(0, 4);
					String mese = datainizio.substring(5, 7);
					String giorno = datainizio.substring(8, 10);
					if (!RegularExpressionEngine.isMatch(anno,"^[0-9]+$")) {
						dataInOk = false;
					} else if (!datainizio.substring(4, 5).equals("-")) {
						dataInOk = false;
					} else if (!RegularExpressionEngine.isMatch(mese,"^[0-9]+$")) {
						dataInOk = false;
					} else if (!datainizio.substring(7, 8).equals("-")) {
						dataInOk = false;
					} else if (!RegularExpressionEngine.isMatch(giorno,"^[0-9]+$")) {
						dataInOk = false;
					} else {
						int annoI = Integer.parseInt(anno);
						int meseI = Integer.parseInt(mese);
						int giornoI = Integer.parseInt(giorno);
						if ((meseI == 1) || (meseI == 3) || (meseI == 5) || (meseI == 7) || (meseI == 8) || (meseI == 10) || (meseI == 12)) {
							if ((giornoI == 0) || (giornoI > 31)) {
								dataInOk = false;
							}
						} else {
							if ((meseI == 4) || (meseI == 6) || (meseI == 9) || (meseI == 11)) {
								if ((giornoI == 0) || (giornoI > 30)) {
									dataInOk = false;
								}
							} else {
								if (meseI == 2) {
									int bisestile = annoI / 4;
									if (annoI != bisestile * 4) {
										if ((giornoI == 0) || (giornoI > 28)) {
											dataInOk = false;
										}
									} else if ((giornoI == 0) || (giornoI > 29)) {
										dataInOk = false;
									}
								} else {
									dataInOk = false;
								}
							}
						}
					}
				}
				if (!dataInOk) {
					this.pd.setMessage("Inizio intervallo espresso in una forma sbagliata. Esprimerlo come aaaa-mm-gg");
					return false;
				}
			}

			if (datafine != null && !"".equals(datafine)) {
				boolean dataFineOk = true;
				if (datafine.length() != 10) {
					dataFineOk = false;
				} else {
					String anno = datafine.substring(0, 4);
					String mese = datafine.substring(5, 7);
					String giorno = datafine.substring(8, 10);
					if (!RegularExpressionEngine.isMatch(anno,"^[0-9]+$")) {
						dataFineOk = false;
					} else if (!datafine.substring(4, 5).equals("-")) {
						dataFineOk = false;
					} else if (!RegularExpressionEngine.isMatch(mese,"^[0-9]+$")) {
						dataFineOk = false;
					} else if (!datafine.substring(7, 8).equals("-")) {
						dataFineOk = false;
					} else if (!RegularExpressionEngine.isMatch(giorno,"^[0-9]+$")) {
						dataFineOk = false;
					} else {
						int annoI = Integer.parseInt(anno);
						int meseI = Integer.parseInt(mese);
						int giornoI = Integer.parseInt(giorno);
						if ((meseI == 1) || (meseI == 3) || (meseI == 5) || (meseI == 7) || (meseI == 8) || (meseI == 10) || (meseI == 12)) {
							if ((giornoI == 0) || (giornoI > 31)) {
								dataFineOk = false;
							}
						} else {
							if ((meseI == 4) || (meseI == 6) || (meseI == 9) || (meseI == 11)) {
								if ((giornoI == 0) || (giornoI > 30)) {
									dataFineOk = false;
								}
							} else {
								if (meseI == 2) {
									int bisestile = annoI / 4;
									if (annoI != bisestile * 4) {
										if ((giornoI == 0) || (giornoI > 28)) {
											dataFineOk = false;
										}
									} else if ((giornoI == 0) || (giornoI > 29)) {
										dataFineOk = false;
									}
								} else {
									dataFineOk = false;
								}
							}
						}
					}
				}
				if (!dataFineOk) {
					this.pd.setMessage("Fine intervallo espresso in una forma sbagliata. Esprimerlo come aaaa-mm-gg");
					return false;
				}
			}

			if(!InterfaceType.STANDARD.equals(ServletUtils.getUserFromSession(this.session).getInterfaceType())) {
				if (this.core.isMsgDiagnostici_showSorgentiDatiDatabase()) {
					if(nomeDs==null || "".equals(nomeDs)){
						this.pd.setMessage("E' necessario selezionare una sorgente dati.");
						return false;
					}
				}
			}

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	boolean diagnosticaStatoList(String nomeDsDiag,boolean forceChange, ISearch ricerca) throws Exception {
		try {
			DriverMsgDiagnostici driverMSGDiagnostici = this.core.getDriverMSGDiagnostici(nomeDsDiag, forceChange);

			ServletUtils.addListElementIntoSession(this.session, ArchiviCostanti.OBJECT_NAME_ARCHIVI_DIAGNOSTICA);

			int idLista = Liste.MESSAGGI_DIAGNOSTICI;
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);
			this.pd.setNumEntries(ricerca.getNumEntries(idLista));

			if (this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_INDEX) != null) {
				offset = Integer.parseInt(this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_INDEX));
				ricerca.setIndexIniziale(idLista, offset);
			}
			if (this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PAGE_SIZE) != null) {
				limit = Integer.parseInt(this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PAGE_SIZE));
				ricerca.setPageSize(idLista, limit);
			}
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(this.pd, ArchiviCostanti.LABEL_MESSAGGI, search);
			}
			if (this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_SEARCH) != null) {
				search = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_SEARCH);
				search = search.trim();
				if (search.equals("")) {
					ricerca.setSearchString(idLista, org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED);
				} else {
					ricerca.setSearchString(idLista, search);
				}
			}

			this.pd.setIndex(offset);
			this.pd.setPageSize(limit);

			String nomeDs = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DATASOURCE);
			String datainizio = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_INIZIO);
			String datafine = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_FINE);
			String severita = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DIAGNOSTICI_SEVERITA);
			String idfunzione = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_FUNZIONE);
			String tipo_servizio = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_SERVIZIO);
			String servizio = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_SERVIZIO);
			String tipo_mittente = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_MITTENTE);
			String nome_mittente = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_MITTENTE);
			String tipo_destinatario = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_DESTINATARIO);
			String nome_destinatario = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DESTINATARIO);
			String nome_azione = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_AZIONE);
			String correlazioneApplicativa = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA);
			String protocollo = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO);
			String identificativoMessaggio = this.request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO_SEARCH);
			
			
			Parameter pDs = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DATASOURCE, (nomeDs==null ? "" : nomeDs));
			Parameter pSev = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DIAGNOSTICI_SEVERITA,severita);
			Parameter pIdFun = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_FUNZIONE,idfunzione);
			Parameter pDataI = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_INIZIO,(datainizio == null ? "" : datainizio) );
			Parameter pDataF = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_FINE,(datafine == null ? "" : datafine) );
			Parameter pTipoM = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_MITTENTE,(tipo_mittente == null ? "" : tipo_mittente) );
			Parameter pNomeM = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_MITTENTE, (nome_mittente == null ? "" : nome_mittente));
			Parameter pTipoD = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_DESTINATARIO, (tipo_destinatario == null ? "" : tipo_destinatario) );
			Parameter pNomeD = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DESTINATARIO,(nome_destinatario == null ? "" : nome_destinatario) );
			Parameter pTipoS = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_SERVIZIO, (tipo_servizio == null ? "" : tipo_servizio) );
			Parameter pNomeS = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_SERVIZIO,(servizio == null ? "" : servizio) );
			Parameter pAzione = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_AZIONE,(nome_azione == null ? "" : nome_azione));
			Parameter pCorrAppl = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA,(correlazioneApplicativa == null ? "" : correlazioneApplicativa));
			Parameter pProt = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO, (protocollo == null ? "" : protocollo));
			Parameter pIdentMsg = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO_SEARCH, (identificativoMessaggio == null ? "" : identificativoMessaggio));

			// setto la barra del titolo
//			String params = "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DATASOURCE+"=" + (nomeDs==null ? "" : nomeDs) 
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_DIAGNOSTICI_SEVERITA+"=" + severita 
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_ID_FUNZIONE+"=" + idfunzione 
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_INIZIO+"=" + (datainizio == null ? "" : datainizio) 
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_FINE+"=" + (datafine == null ? "" : datafine) 
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_MITTENTE+"=" + (tipo_mittente == null ? "" : tipo_mittente) 
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_MITTENTE+"=" + (nome_mittente == null ? "" : nome_mittente) 
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_DESTINATARIO+"=" + (tipo_destinatario == null ? "" : tipo_destinatario) 
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DESTINATARIO+"=" + (nome_destinatario == null ? "" : nome_destinatario) 
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_TIPO_SERVIZIO+"=" + (tipo_servizio == null ? "" : tipo_servizio) 
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_SERVIZIO+"=" + (servizio == null ? "" : servizio) 
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_AZIONE+"=" + (nome_azione == null ? "" : nome_azione)
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA+"=" + (correlazioneApplicativa == null ? "" : correlazioneApplicativa)
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO+"=" + (protocollo == null ? "" : protocollo)
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_SEARCH+"=" + (search == null ? "" : search)
//					+ "&"+ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO+"=" + (identificativoMessaggio == null ? "" : identificativoMessaggio);

			ServletUtils.setPageDataTitle(this.pd, 
					new Parameter(ArchiviCostanti.LABEL_REPORTISTICA,null),
					new Parameter(ArchiviCostanti.LABEL_DIAGNOSTICA,ArchiviCostanti.SERVLET_NAME_ARCHIVI_DIAGNOSTICA,
							pDs, pSev,pIdFun, pDataI, pDataF, pTipoM, pNomeM, pTipoD, pNomeD, pTipoS, pNomeS, pAzione, pCorrAppl, pProt, pIdentMsg),
					new Parameter(ArchiviCostanti.LABEL_MESSAGGI,null));


			// setto le label delle colonne
			String[] labels = { ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_PROTOCOLLO, 
					ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_DIAGNOSTICI_SEVERITA, 
					ArchiviCostanti.LABEL_PARAMETRO_ARCHIVI_ID_FUNZIONE,
					ArchiviCostanti.LABEL_DATA,
					ArchiviCostanti.LABEL_DETTAGLIO };
			this.pd.setLabels(labels);

			// preparo i dati
			Vector<Object> dati = new Vector<Object>();

			// setto dei campi hidden con i valori della query
			/*
			 * String severita = this.request.getParameter("severita"); String
			 * idfunzione = this.request.getParameter("idfunzione"); String
			 * datainizio = this.request.getParameter("datainizio"); String
			 * datafine = this.request.getParameter("datafine");
			 */
			Hashtable<String, String> campiHidden = new Hashtable<String, String>();
			campiHidden.put(ArchiviCostanti.PARAMETRO_ARCHIVI_DIAGNOSTICI_SEVERITA, severita);
			campiHidden.put(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_FUNZIONE, idfunzione);
			campiHidden.put(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_INIZIO, datainizio);
			campiHidden.put(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_FINE, datafine);
			this.pd.setHidden(campiHidden);

			if (tipo_destinatario == null || "".equals(tipo_destinatario) || "-".equals(tipo_destinatario))
				tipo_destinatario = null;
			if (nome_destinatario == null || "".equals(nome_destinatario))
				nome_destinatario = null;
			if (tipo_servizio == null || "".equals(tipo_servizio) || "-".equals(tipo_servizio))
				tipo_servizio = null;
			if (servizio == null || "".equals(servizio))
				servizio = null;
			if (tipo_mittente == null || "".equals(tipo_mittente) || "-".equals(tipo_mittente))
				tipo_mittente = null;
			if (nome_mittente == null || "".equals(nome_mittente))
				nome_mittente = null;
			if (nome_azione == null || "".equals(nome_azione))
				nome_azione = null;
			if (correlazioneApplicativa == null || "".equals(correlazioneApplicativa))
				correlazioneApplicativa = null;
			if (identificativoMessaggio == null || "".equals(identificativoMessaggio))
				identificativoMessaggio = null;
			
			// Conversione Data
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd"); // SimpleDateFormat non e' thread-safe
			Date dataInizioData = !"".equals(datainizio) ? simpleDateFormat.parse(datainizio) : null;
			Date dataFineData = !"".equals(datafine) ? simpleDateFormat.parse(datafine) : null;

			// bug fix
			// data fine inidica l intera giornata quinid aggiungo 1 giorno alla
			// data
			// inserita
			if (dataFineData != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataFineData);
				cal.add(Calendar.DAY_OF_WEEK, 1);
				dataFineData = cal.getTime();
			}

			FiltroRicercaDiagnosticiConPaginazione filter = new FiltroRicercaDiagnosticiConPaginazione();

			if (severita == null || "".equals(severita)) {
				severita = ArchiviCostanti.PARAMETRO_VALORE_ARCHIVI_DIAGNOSTICA_SEVERITA_DEFAULT;
			}
			filter.setSeverita(new Integer(severita));

			if (idfunzione != null && !"-".equals(idfunzione) && !"".equals(idfunzione)) {
				filter.setIdFunzione(idfunzione);
			}

			if (dataInizioData != null) {
				filter.setDataInizio(dataInizioData);
			}
			if (dataFineData != null) {
				filter.setDataFine(dataFineData);
			}

			InformazioniProtocollo informazioniProtocollo = null;

			// setto tipo soggetto mittente se specificato
			if ((tipo_mittente != null && !"-".equals(tipo_mittente)) || (nome_mittente != null && !"".equals(nome_mittente))) {
				if ("-".equals(tipo_mittente))
					tipo_mittente = "";
				if (informazioniProtocollo == null)
					informazioniProtocollo = new InformazioniProtocollo();
				IDSoggetto mittente = new IDSoggetto(tipo_mittente, nome_mittente);
				informazioniProtocollo.setFruitore(mittente);// il mittente e' il fruitore

			}
			// setto tipo soggetto erogatore se specificato
			if ((tipo_destinatario != null && !"-".equals(tipo_destinatario)) || (nome_destinatario != null && !"".equals(nome_destinatario))) {
				if ("-".equals(tipo_destinatario))
					tipo_destinatario = "";
				if (informazioniProtocollo == null)
					informazioniProtocollo = new InformazioniProtocollo();
				IDSoggetto destinatario = new IDSoggetto(tipo_destinatario, nome_destinatario);
				informazioniProtocollo.setErogatore(destinatario);// il destinatario e' l
				// erogatore
			}
			// setto tipo servizio se specificato
			if ((tipo_servizio != null && !"-".equals(tipo_servizio)) || (servizio != null && !"".equals(servizio))) {
				if ("-".equals(tipo_servizio))
					tipo_servizio = "";
				if (informazioniProtocollo == null)
					informazioniProtocollo = new InformazioniProtocollo();

				// se erogatore (destinatario) e' stato specificato allora setto
				if (informazioniProtocollo != null && informazioniProtocollo.getErogatore() != null) {
					informazioniProtocollo.setErogatore(informazioniProtocollo.getErogatore());
				}

				informazioniProtocollo.setTipoServizio(tipo_servizio);
				informazioniProtocollo.setServizio(servizio);

			}

			// azione
			if (nome_azione != null && !"".equals(nome_azione)) {
				if (informazioniProtocollo == null)
					informazioniProtocollo = new InformazioniProtocollo();
				informazioniProtocollo.setAzione(nome_azione);
			}

			// correlazioneApplicativa
			if (correlazioneApplicativa != null && !"".equals(correlazioneApplicativa)) {
				filter.setCorrelazioneApplicativa(correlazioneApplicativa);
				filter.setCorrelazioneApplicativaRisposta(correlazioneApplicativa);
				filter.setCorrelazioneApplicativaOrMatch(true);
			}

			// protocollo
			if(protocollo!=null && !"".equals(protocollo) && !"-".equals(protocollo)){
				filter.setProtocollo(protocollo);
			}

			if (informazioniProtocollo != null) {
				filter.setInformazioniProtocollo(informazioniProtocollo);
				filter.setRicercaSoloMessaggiCorrelatiInformazioniProtocollo(true);
			}

			String userLogin = (String) ServletUtils.getUserLoginFromSession(this.session);
			if(this.core.isVisioneOggettiGlobale(userLogin)==false){
				filter.setFiltroSoggetti(this.soggettiCore.getSoggettiWithSuperuser(userLogin));
			}

			if (search!=null && !search.equals("")) {
				filter.setMessaggioCercatoInternamenteTestoDiagnostico(search);
			}
			
			// Identificativo messaggio
			if (identificativoMessaggio != null && !"".equals(identificativoMessaggio)) {
				filter.setIdBustaRichiesta(identificativoMessaggio);
			}

			filter.setAsc(false); // in modo da far vedere i piu' recenti.

			filter.setLimit(limit);
			filter.setOffset(offset);

			// ricavo il numero di entries totale
			// int numentry = 0;
			long count = driverMSGDiagnostici.countMessaggiDiagnostici(filter);

			this.pd.setNumEntries((int) count);

			List<MsgDiagnostico> lista = null;
			try{
				lista = driverMSGDiagnostici.getMessaggiDiagnostici(filter);
			}catch(DriverMsgDiagnosticiNotFoundException dNotFound){
				lista = new ArrayList<MsgDiagnostico>();
			}

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss"); // SimpleDateFormat non e' thread-safe

			Iterator<MsgDiagnostico> it = lista.iterator();
			while (it.hasNext()) {

				MsgDiagnostico msg = it.next();
				Vector<DataElement> e = new Vector<DataElement>();

				DataElement de = new DataElement();
				de.setValue(msg.getProtocollo());
				e.addElement(de);


				de = new DataElement();
				de.setValue(LogLevels.toOpenSPCoop2(msg.getSeverita()));
				e.addElement(de);

				de = new DataElement();
				de.setValue(msg.getIdFunzione());
				e.addElement(de);

				de = new DataElement();
				de.setValue(formatter.format(msg.getGdo().getTime()));
				e.addElement(de);

				de = new DataElement();
				String tmpMsg = msg.getMessaggio() == null ? "" : msg.getMessaggio();
				int lungMsg = tmpMsg.length();
				if (lungMsg < 70) {
					de.setValue(tmpMsg + "...");
				} else {
					de.setValue(tmpMsg.substring(0, 70) + "...");
				}
				String idMessaggio = "";
				if(msg.getIdBusta()!=null){
					idMessaggio=msg.getIdBusta();
					de.setToolTip(msg.getIdBusta());
				}
				String idMessaggioRisposta = "";
				if(msg.getIdBustaRisposta()!=null){
					idMessaggioRisposta = msg.getIdBustaRisposta();
				}
				String idporta = "";
				if(msg.getIdSoggetto()!=null){
					idporta = msg.getIdSoggetto().getCodicePorta();
				}	
				
				Parameter pID = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID, "" + msg.getId());
				Parameter pIdMsg = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO, idMessaggio );
				Parameter pIdMsgRisp = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO_RISPOSTA,idMessaggioRisposta );
				Parameter pIdPorta = new Parameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_PORTA,  idporta);
				
				Parameter pUrl = new Parameter("", ArchiviCostanti.SERVLET_NAME_ARCHIVI_DIAGNOSTICA_TESTO,
						pDs, pSev,pIdFun, pDataI, pDataF, pTipoM, pNomeM, pTipoD, pNomeD, pTipoS, pNomeS, pAzione, pCorrAppl, pProt, pID, pIdMsg, pIdMsgRisp, pIdPorta,pIdentMsg);
				
				de.setUrl(pUrl.getValue());
//						ArchiviCostanti.SERVLET_NAME_ARCHIVI_DIAGNOSTICA_TESTO+
//						"?"+ArchiviCostanti.PARAMETRO_ARCHIVI_ID+"=" + msg.getId()+
//						"&"+ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO+"="+idMessaggio+
//						"&"+ArchiviCostanti.PARAMETRO_ARCHIVI_ID_MESSAGGIO_RISPOSTA+"="+idMessaggioRisposta+
//						"&"+ArchiviCostanti.PARAMETRO_ARCHIVI_ID_PORTA+"="+idporta + params);
				e.addElement(de);

				dati.addElement(e);
			}

			// area bottoni
			if (lista.size() > 0) {
				
				Parameter pExport = new Parameter("", ArchiviCostanti.SERVLET_NAME_MESSAGGI_DIAGNOSTICI_EXPORT,
						pDs, pSev,pIdFun, pDataI, pDataF, pTipoM, pNomeM, pTipoD, pNomeD, pTipoS, pNomeS, pAzione, pCorrAppl, pProt, pIdentMsg);
				
				Vector<AreaBottoni> areaBottoni = new Vector<AreaBottoni>();
				DataElement saveAs = new DataElement();
				saveAs.setValue(ArchiviCostanti.LABEL_ESPORTA_XML);
				saveAs.setOnClick("Export('"+pExport.getValue()+ "')");
				Vector<DataElement> bottoni = new Vector<DataElement>();
				bottoni.add(saveAs);
				AreaBottoni area = new AreaBottoni();
				area.setBottoni(bottoni);
				areaBottoni.add(area);

				this.pd.setAreaBottoni(areaBottoni);
			}

			this.pd.setSelect(false);
			this.pd.setDati(dati);

			String params = ServletUtils.getParametersAsString(false,  pDs, pSev,pIdFun, pDataI, pDataF, pTipoM, pNomeM, pTipoD, pNomeD, pTipoS, pNomeS, pAzione, pCorrAppl, pProt, pIdentMsg);
			this.request.setAttribute(ArchiviCostanti.PARAMETRI_ARCHIVI, params);

			return false;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

}
