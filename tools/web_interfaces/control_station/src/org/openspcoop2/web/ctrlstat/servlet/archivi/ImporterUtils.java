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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.archive.ImportInformationMissing;
import org.openspcoop2.protocol.engine.archive.ImportInformationMissingCollection;
import org.openspcoop2.protocol.information_missing.Wizard;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.ArchiveMode;
import org.openspcoop2.protocol.sdk.archive.ArchiveModeType;
import org.openspcoop2.protocol.sdk.archive.IArchive;
import org.openspcoop2.protocol.sdk.archive.ImportMode;
import org.openspcoop2.protocol.sdk.archive.MapPlaceholder;
import org.openspcoop2.utils.serialization.JavaDeserializer;
import org.openspcoop2.utils.serialization.JavaSerializer;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationNotFound;
import org.openspcoop2.web.ctrlstat.servlet.FileUploadForm;
import org.openspcoop2.web.ctrlstat.servlet.ac.AccordiCooperazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;

/**
 * ImporterUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ImporterUtils {

	private SoggettiCore soggettiCore = null;
	private AccordiServizioParteComuneCore aspcCore = null;
	private AccordiCooperazioneCore acCore = null;
	private PddCore pddCore = null;
	public ImporterUtils(ArchiviCore archiviCore) throws Exception{
		this.soggettiCore = new SoggettiCore(archiviCore);
		this.aspcCore = new AccordiServizioParteComuneCore(archiviCore);
		this.acCore = new AccordiCooperazioneCore(archiviCore);
		this.pddCore = new PddCore(archiviCore);
	}

	public List<String> getIdSoggetti(List<String> protocolli,String importMode, 
			String protocolloMissingInput,String tipoPdDMissingInput, Wizard wizard ) throws DriverRegistroServiziNotFound, DriverRegistroServiziException, ProtocolException, DriverControlStationException, DriverControlStationNotFound{
		List<String> listIdSoggetti = new ArrayList<String>();
		
		List<String> protocolliDaScorrere = new ArrayList<String>();
		if(wizard!=null && wizard.getRequisiti()!=null && wizard.getRequisiti().sizeProtocolloList()>0){
			for (int i = 0; i < wizard.getRequisiti().sizeProtocolloList(); i++) {
				protocolliDaScorrere.add(wizard.getRequisiti().getProtocollo(i).getNome());
			}
		}
		else{
			protocolliDaScorrere.addAll(protocolli);
		}
		
		for (int i = 0; i < protocolliDaScorrere.size(); i++) {
			String protocollo = protocolliDaScorrere.get(i);
			if(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO_UNDEFINDED.equals(protocollo) == false){
				
				IProtocolFactory pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
				IArchive archiveEngine = pf.createArchive();
				List<ImportMode> importModesByProtocol = archiveEngine.getImportModes();
				if(importModesByProtocol.contains(new ImportMode(importMode))){
				
					List<String> tipi = this.soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);
					for (int j = 0; j < tipi.size(); j++) {
						String tipo = tipi.get(j);
						FiltroRicercaSoggetti filtro = new FiltroRicercaSoggetti();
						filtro.setTipo(tipo);
						try{
							List<IDSoggetto> idSoggetti = this.soggettiCore.getAllIdSoggettiRegistro(filtro);
							for (int k = 0; k < idSoggetti.size(); k++) {
								IDSoggetto id = idSoggetti.get(k);
								listIdSoggetti.add(id.getTipo()+"/"+id.getNome());
							}
						}catch(DriverRegistroServiziNotFound dNotFound){}
					}
					
				}
			}
		}
		
		if(protocolloMissingInput==null && tipoPdDMissingInput==null){
			
			if(listIdSoggetti.size()<=0){
				throw new ProtocolException("Non risulta configurato alcun soggetto. Tale configurazione è richiesta per procedere con l'importazione");
			}
			
			if(listIdSoggetti.size()!=1)
				listIdSoggetti.add(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_SOGGETTO_INPUT_UNDEFINDED);
			java.util.Collections.sort(listIdSoggetti);
			return listIdSoggetti;
		}
		
		// filtro per protocollo e/o tipoPdD
		List<String> listIdSoggettiFiltrati = new ArrayList<String>();
		for (String tipoNome : listIdSoggetti) {
			String [] tmp = tipoNome.split("/");
			String tipo = tmp[0];
			String nome = tmp[1];
			IDSoggetto idSoggetto = new IDSoggetto(tipo, nome);
			if(protocolloMissingInput!=null){
				if(!protocolloMissingInput.equals(this.soggettiCore.getProtocolloAssociatoTipoSoggetto(tipo))){
					continue;
				}
			}
			if(tipoPdDMissingInput!=null){
				Soggetto sog = this.soggettiCore.getSoggettoRegistro(idSoggetto);
				if(sog.getPortaDominio()==null){
					continue;
				}
				PdDControlStation pdd = this.pddCore.getPdDControlStation(sog.getPortaDominio());
				if(!tipoPdDMissingInput.equals(pdd.getTipo())){
					continue;
				}
			}
			listIdSoggettiFiltrati.add(tipoNome);
		}
		
		String criteri = "";
		if(protocolloMissingInput!=null){
			criteri+=" protocollo:"+protocolloMissingInput;
		}
		if(tipoPdDMissingInput!=null){
			criteri+=" tipoPdDAssociata:"+tipoPdDMissingInput;
		}
		
		if(listIdSoggettiFiltrati.size()<=0){
			throw new ProtocolException("Non risulta configurato alcun soggetto che soddisfa i seguenti requisiti: "+criteri+". Tale configurazione è richiesta per procedere con l'importazione");
		}
		
		if(listIdSoggettiFiltrati.size()!=1)
			listIdSoggettiFiltrati.add(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_SOGGETTO_INPUT_UNDEFINDED);
		java.util.Collections.sort(listIdSoggettiFiltrati);
		return listIdSoggettiFiltrati;
	}
	
	public List<String> getIdAccordiServizioParteComune(List<String> protocolli,String importMode) throws DriverRegistroServiziNotFound, DriverRegistroServiziException, ProtocolException{
		List<String> listIdAccordiServizioParteComune = new ArrayList<String>();
		listIdAccordiServizioParteComune.add(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_INPUT_UNDEFINDED);
		for (int i = 0; i < protocolli.size(); i++) {
			String protocollo = protocolli.get(i);
			if(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO_UNDEFINDED.equals(protocollo) == false){
				
				IProtocolFactory pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
				IArchive archiveEngine = pf.createArchive();
				List<ImportMode> importModesByProtocol = archiveEngine.getImportModes();
				if(importModesByProtocol.contains(new ImportMode(importMode))){
				
					List<String> tipi = this.soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);
					for (int j = 0; j < tipi.size(); j++) {
						String tipo = tipi.get(j);
						FiltroRicercaAccordi filtro = new FiltroRicercaAccordi();
						filtro.setTipoSoggettoReferente(tipo);
						try{
							List<IDAccordo> idAccordi = this.aspcCore.getAllIdAccordiServizio(filtro);
							for (int k = 0; k < idAccordi.size(); k++) {
								listIdAccordiServizioParteComune.add(IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordi.get(k)));
							}
						}catch(DriverRegistroServiziNotFound dNotFound){}
					}
					
				}
			}
		}
		
		java.util.Collections.sort(listIdAccordiServizioParteComune);
		
		return listIdAccordiServizioParteComune;
	}
	
	public List<String> getIdAccordiCooperazione(List<String> protocolli,String importMode) throws DriverRegistroServiziNotFound, DriverRegistroServiziException, ProtocolException{
		List<String> listIdAccordiCooperazione = new ArrayList<String>();
		listIdAccordiCooperazione.add(ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_INPUT_UNDEFINDED);
		for (int i = 0; i < protocolli.size(); i++) {
			String protocollo = protocolli.get(i);
			if(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO_UNDEFINDED.equals(protocollo) == false){
				
				IProtocolFactory pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
				IArchive archiveEngine = pf.createArchive();
				List<ImportMode> importModesByProtocol = archiveEngine.getImportModes();
				if(importModesByProtocol.contains(new ImportMode(importMode))){
				
					List<String> tipi = this.soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);
					for (int j = 0; j < tipi.size(); j++) {
						String tipo = tipi.get(j);
						FiltroRicercaAccordi filtro = new FiltroRicercaAccordi();
						filtro.setTipoSoggettoReferente(tipo);
						try{							
							List<IDAccordoCooperazione> idAccordi = this.acCore.getAllIdAccordiCooperazione(filtro);
							for (int k = 0; k < idAccordi.size(); k++) {
								listIdAccordiCooperazione.add(IDAccordoCooperazioneFactory.getInstance().getUriFromIDAccordo(idAccordi.get(k)));
							}
						}catch(DriverRegistroServiziNotFound dNotFound){}
					}
					
				}
			}
		}
		
		java.util.Collections.sort(listIdAccordiCooperazione);
		
		return listIdAccordiCooperazione;
	}

	public Hashtable<String,String> getImportModesWithProtocol(List<String> protocolli) throws ProtocolException{

		Hashtable<String,String> importModes = new Hashtable<String,String>();
		for (int i = 0; i < protocolli.size(); i++) {
			String protocolName = protocolli.get(i);
			if(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO_UNDEFINDED.equals(protocolName)==false){
				IProtocolFactory pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocolName);
				IArchive archiveEngine = pf.createArchive();
				List<ImportMode> importModesByProtocol = archiveEngine.getImportModes();
				for (ImportMode imp : importModesByProtocol) {
					if(importModes.containsKey(imp.toString())==false){
						importModes.put(imp.toString(),protocolName);
					}
				}
			}
		}

		return importModes;
	}

	public List<ArchiveModeType> getImportModeTypes(ArchiveMode mode,String protocol) throws ProtocolException{

		IProtocolFactory pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocol);
		IArchive archiveEngine = pf.createArchive();
		return archiveEngine.getMappingTypesExtensions(mode).getAllTypes();

	}

	public List<String> getValidExtensions(ArchiveModeType type,ArchiveMode mode,String protocol) throws ProtocolException{

		IProtocolFactory pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocol);
		IArchive archiveEngine = pf.createArchive();
		return archiveEngine.getMappingTypesExtensions(mode).mappingTypeToExts(type);

	}

	private void writeSerializeObject(File tmpF,Object object) throws Exception{
		JavaSerializer javaSerializer = new JavaSerializer();
		FileOutputStream fout = null;
		try{
			fout = new FileOutputStream(tmpF);
			javaSerializer.writeObject(object, fout);
		}finally{
			try{
				if(fout!=null){
					fout.flush();
					fout.close();
				}
			}catch(Exception eClose){}
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T readObject(File f,Class<T> c,boolean deleteFile) throws Exception{
		FileInputStream fis = null;
		JavaDeserializer javaDeserializer = new JavaDeserializer();
		try{
			fis = new FileInputStream(f);
			return (T) javaDeserializer.readObject(fis, c);
		}finally{
			try{
				if(fis!=null){
					fis.close();
				}
			}catch(Exception eClose){}
			if(deleteFile){
				try{
					f.delete();
				}catch(Exception e){}
			}
		}
	}

	public File writeFormFile(String sessionId,FormFile ff) throws Exception{
		File tmpF = File.createTempFile(sessionId+"-"+ff.getFileName(), "tmp");
		this.writeSerializeObject(tmpF, ff);
		return tmpF;
	}

	public FormFile readFormFile(String filePath,ActionForm form) throws Exception{

		FormFile ff = null;
		if(StringUtils.isNotEmpty(filePath)){
			File f = new File(filePath);
			ff = this.readObject(f, FormFile.class, true);
		}else{
			FileUploadForm fileUpload = (FileUploadForm) form;
			ff = fileUpload.getTheFile();
		}
		return ff;
	}

	public File writeImportInformationMissingCollectionFile(String sessionId,ImportInformationMissingCollection importInformationMissingCollection) throws Exception{
		File tmpF = File.createTempFile(sessionId+"-importInformationMissingCollection", "tmp");
		this.writeSerializeObject(tmpF, importInformationMissingCollection);
		return tmpF;
	}
	public File writeImportInformationMissingObjectFile(String sessionId,Object object) throws Exception{
		File tmpF = File.createTempFile(sessionId+"-importInformationMissingObject", "tmp");
		this.writeSerializeObject(tmpF, object);
		return tmpF;
	}

	public ImportInformationMissingCollection readImportInformationMissingCollectionFile(String filePath) throws Exception{

		if(StringUtils.isNotEmpty(filePath)){
			File f = new File(filePath);
			return this.readObject(f, ImportInformationMissingCollection.class, true);
		}
		return null;
	}
	public <T> T readImportInformationMissingObjectFile(String filePath,Class<T> classObject) throws Exception{

		if(StringUtils.isNotEmpty(filePath)){
			File f = new File(filePath);
			return this.readObject(f, classObject, true);
		}
		return null;
	}

	public ImportInformationMissingCollection updateInformationMissingCheckData(String importInformationMissing_soggettoInput,String importInformationMissing_versioneInput,
			List<PortType> importInformationMissing_portTypes, String importInformationMissing_portTypeImplementedInput,
			String importInformationMissing_accordoServizioParteComuneInput,String importInformationMissing_accordoCooperazioneInput,
			InvocazioneServizio importInformationMissing_invocazioneServizio, Connettore importInformationMissing_connettore,
			Credenziali importInformationMissing_credenziali, MapPlaceholder importInformationMissing_placeholder,
			String importInformationMissingCollectionFilePath,String importInformationMissingObjectId,
			ImportInformationMissingCollection importInformationMissingCollection) throws Exception{

		// importInformationMissing: soggetto
		IDSoggetto idSoggetto = null;
		if(importInformationMissing_soggettoInput!=null && 
				!"".equals(importInformationMissing_soggettoInput) &&
				!ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_SOGGETTO_INPUT_UNDEFINDED.equals(importInformationMissing_soggettoInput) ) {		
			String[]splitSoggetto = importInformationMissing_soggettoInput.split("/");
			idSoggetto = new IDSoggetto(splitSoggetto[0].trim(), splitSoggetto[1].trim());
		}


		// importInformationMissing: versione
		Integer versione = null;
		if(importInformationMissing_versioneInput!=null){
			try{
				versione = Integer.parseInt(importInformationMissing_versioneInput);
			}catch(Exception e){}
		}

		// importInformationMissing: accordoServizioParteComune
		IDAccordo idAccordoServizioParteComune = null;
		if(importInformationMissing_accordoServizioParteComuneInput!=null && 
				!"".equals(importInformationMissing_accordoServizioParteComuneInput) &&
				!ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_INPUT_UNDEFINDED.equals(importInformationMissing_accordoServizioParteComuneInput) ) {		
			idAccordoServizioParteComune = IDAccordoFactory.getInstance().getIDAccordoFromUri(importInformationMissing_accordoServizioParteComuneInput.trim());
		}
		
		// importInformationMissing: accordoCooperazione
		IDAccordoCooperazione idAccordoCooperazione = null;
		if(importInformationMissing_accordoCooperazioneInput!=null && 
				!"".equals(importInformationMissing_accordoCooperazioneInput) &&
				!ArchiviCostanti.PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_INPUT_UNDEFINDED.equals(importInformationMissing_accordoCooperazioneInput) ) {		
			idAccordoCooperazione = IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromUri(importInformationMissing_accordoCooperazioneInput.trim());
		}
		
		// Import Information Missing
		ImportInformationMissing importInformationMissing = null;
		if(importInformationMissingObjectId!=null){
			if(importInformationMissingCollection!=null){
				importInformationMissing = importInformationMissingCollection.remove(importInformationMissingObjectId);
			}else{
				importInformationMissingCollection = new ImportInformationMissingCollection();
			}
			if(importInformationMissing==null){
				importInformationMissing = new ImportInformationMissing();
			}
			if(idSoggetto!=null){
				importInformationMissing.setSoggetto(idSoggetto);
			}
			if(versione!=null){
				importInformationMissing.setVersione(versione);	
			}
			if(importInformationMissing_portTypes!=null){
				importInformationMissing.setPortTypes(importInformationMissing_portTypes);
			}
			if(importInformationMissing_portTypeImplementedInput!=null){
				importInformationMissing.setPortTypeImplemented(importInformationMissing_portTypeImplementedInput);
			}
			if(idAccordoServizioParteComune!=null){
				importInformationMissing.setIdAccordoServizioParteComune(idAccordoServizioParteComune);
			}
			if(idAccordoCooperazione!=null){
				importInformationMissing.setIdAccordoCooperazione(idAccordoCooperazione);
			}
			if(importInformationMissing_invocazioneServizio!=null){
				importInformationMissing.setInvocazioneServizio(importInformationMissing_invocazioneServizio);
			}
			if(importInformationMissing_connettore!=null){
				importInformationMissing.setConnettore(importInformationMissing_connettore);
			}
			if(importInformationMissing_credenziali!=null){
				importInformationMissing.setCredenziali(importInformationMissing_credenziali);
			}
			if(importInformationMissing_placeholder!=null){
				importInformationMissing.setInputPlaceholder(importInformationMissing_placeholder);
			}
			importInformationMissingCollection.add(importInformationMissingObjectId, importInformationMissing);
		}

		return importInformationMissingCollection;
	}
	


}
