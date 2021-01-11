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

package org.openspcoop2.protocol.engine.archive;

import org.slf4j.Logger;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.controllo_traffico.dao.jdbc.JDBCServiceManager;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.protocol.basic.registry.ConfigIntegrationReader;
import org.openspcoop2.protocol.basic.registry.RegistryReader;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoImport;
import org.openspcoop2.protocol.sdk.archive.ArchiveMode;
import org.openspcoop2.protocol.sdk.archive.ArchiveModeType;
import org.openspcoop2.protocol.sdk.archive.IArchive;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.resources.Loader;

/**
 *  Importer
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Importer {

	public static void main(String[] args) throws Exception {
		
		Logger log = LoggerWrapperFactory.getLogger(Importer.class);
		
		// Lettura file
		String archiveFile = args[0];
		byte[] archiveBytes = FileSystemUtilities.readBytesFromFile(archiveFile);
		
		// Protocollo
		String protocollo = args[1];
		
		// Mode
		String mode = args[2]; // es. cnipa, openspcoop
		ArchiveMode archiveMode = new ArchiveMode(mode);
		
		// tipoArchivio
		String type = args[3]; // es. *, apc, aps ...
		ArchiveModeType archiveModeType = new ArchiveModeType(type);
		
		// validateDocuments
		boolean validateDocuments = Boolean.parseBoolean(args[4]); 
		
		// userLogin
		String userLogin = args[5];
		
		// nomePddOperativa
		String nomePddOperativa = args[6];
		
		// tipoPddDefault
		String tipoPddDefault = args[7];
		
		// updateAbilitato
		boolean updateAbilitato = Boolean.parseBoolean(args[8]); 
		boolean importPolicyConfig = Boolean.parseBoolean(args[9]); 
		boolean importConfig = Boolean.parseBoolean(args[10]); 

		// other
		boolean isShowCorrelazioneAsincronaInAccordi = true;
		boolean isShowGestioneWorkflowStatoDocumenti = true;
		boolean isShowAccordiColonnaAzioni = true;
		boolean isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto = false;
		boolean isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto = false;
		
		// Inizializzazione ProtocolFactoryManager
		ConfigurazionePdD configPdD = new ConfigurazionePdD();
		configPdD.setLoader(new Loader());
		configPdD.setLog(log);
		ProtocolFactoryManager.initializeSingleProtocol(log, configPdD, protocollo);
		
		
		// Inizializzo Reader
		DriverRegistroServiziDB driverRegistroServizi = null; 
		DriverConfigurazioneDB driverConfigurazione = null;
		JDBCServiceManager serviceManager = null;
		// TODO INIT
		RegistryReader archiveRegistryReader = new RegistryReader(driverRegistroServizi,log);
		ConfigIntegrationReader archiveConfigIntegrationReader = new ConfigIntegrationReader(driverConfigurazione,log);
		
		
		// trasformazione in archivio openspcoop2
		IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
		IArchive archiveEngine = pf.createArchive();
		Archive archive = archiveEngine.importArchive(archiveBytes, archiveMode, archiveModeType, archiveRegistryReader, archiveConfigIntegrationReader, validateDocuments,null);
		
		
		// validazione
		ArchiveValidator validator = new ArchiveValidator(archiveRegistryReader);
		ImportInformationMissingCollection importInformationMissingCollection = new ImportInformationMissingCollection();
		validator.validateArchive(archive, protocollo, validateDocuments, importInformationMissingCollection, userLogin, 
				isShowCorrelazioneAsincronaInAccordi, false);
		// TODO: Gestore correttamente le eccezioni
		
		
		// Import
		ArchiveEngine importerEngine = new ArchiveEngine(driverRegistroServizi, driverConfigurazione, serviceManager);
		ImporterArchiveUtils importerArchiveUtils = 
				new ImporterArchiveUtils(importerEngine, log, userLogin, nomePddOperativa, tipoPddDefault,
						isShowGestioneWorkflowStatoDocumenti, updateAbilitato,
						importPolicyConfig, importConfig);
		ArchiveEsitoImport result = importerArchiveUtils.importArchive(archive, userLogin, 
				isShowAccordiColonnaAzioni,
				isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto, 
				isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto);
		String resultAsString = archiveEngine.toString(result, archiveMode);
		log.info(resultAsString);
		

	}

}
