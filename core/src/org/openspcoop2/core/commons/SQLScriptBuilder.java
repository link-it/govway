/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.commons;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
 * SQLScriptBuilder
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SQLScriptBuilder {

	private static final String UPGRADE_PREFIX = "upgrade_";
	
	public static void main(String[] args) throws Exception {
		// Metodo utilizzato dal setup antinstaller
		
		String sqlSourceDir = args[0];
		
		String sqlDestDir = args[1];
		
		String sqlScriptName = args[2];
		
		String modalitaInstallazione = args[3];
		
		boolean splitDdlDml = true;
		
		String versionePrecedente = null;
		String versioneAttuale = null;
		String tipoDatabase = null;
		String configurazioneUpgrade = null;
		boolean configurazioneUpgradeRuntime = true;
		boolean configurazioneUpgradeConfig = true;
		boolean configurazioneUpgradeTracce = true;
		boolean configurazioneUpgradeStatistiche = true;
		boolean configurazioneUpgradeMonitoraggio = true;
		if(args.length>4){
			versionePrecedente = args[4];
			versioneAttuale = args[5];
			tipoDatabase = args[6];
			if("aggiornamento".equals(modalitaInstallazione) && args.length>7){
				configurazioneUpgrade = args[7];
				File f = new File(configurazioneUpgrade);
				if(f.exists()) {
					try(FileInputStream fin = new FileInputStream(f)){
						Properties p = new Properties();
						p.load(fin);
						configurazioneUpgradeRuntime = readBooleanProperty(p, "upgrade.runtime");
						configurazioneUpgradeConfig = readBooleanProperty(p, "upgrade.configurazione");
						configurazioneUpgradeTracce = readBooleanProperty(p, "upgrade.tracciamento");
						configurazioneUpgradeStatistiche = readBooleanProperty(p, "upgrade.statistiche");
						configurazioneUpgradeMonitoraggio = readBooleanProperty(p, "upgrade.monitoraggio");
					}
				}
			}
		}
		
		// NOTA: Non far stampare niente, viene usato come meccanismo di check per vedere se l'esecuzione e' andata a buon fine
		/**System.out.println("Modalita ["+modalitaInstallazione+"]");*/
		
		if("nuova".equals(modalitaInstallazione)){
			buildSqlNuovaInstallazione(new File(sqlSourceDir),new File(sqlDestDir),sqlScriptName,splitDdlDml);
		}
		else if("aggiornamento".equals(modalitaInstallazione)){
			
			/**System.out.println("versionePrecedente ["+versionePrecedente+"]");
			System.out.println("versioneAttuale ["+versioneAttuale+"]");
			System.out.println("tipoDatabase ["+tipoDatabase+"]");*/
			
			buildSqlAggiornamento(new File(sqlSourceDir),new File(sqlDestDir),sqlScriptName,versionePrecedente,
					versioneAttuale, tipoDatabase,
					configurazioneUpgradeRuntime,
					configurazioneUpgradeConfig,
					configurazioneUpgradeTracce,
					configurazioneUpgradeStatistiche,
					configurazioneUpgradeMonitoraggio);
		}
		else{
			throw new CoreException("Modalità installazione ["+modalitaInstallazione+"] sconosciuta");
		}
		
	}
	
	private static boolean readBooleanProperty(Properties p, String name) throws CoreException {
		String tmp = p.getProperty(name);
		if(tmp==null) {
			throw new CoreException("Configurazione Upgrade non corretta, proprietà ["+name+"] non presente");
		}
		tmp = tmp.trim();
		try {
			return Boolean.valueOf(tmp);
		}catch(Exception e) {
			throw new CoreException("Configurazione Upgrade non corretta, proprietà ["+name+"] non corretta: "+e.getMessage(),e);
		}
	}
	
	private static void buildSqlNuovaInstallazione(File sqlSourceDir, File sqlDestDir, String sqlScriptName,
			boolean splitDdlDml) throws CoreException, UtilsException, IOException {
		
		if(!sqlSourceDir.exists()){
			throw new CoreException("Source dir ["+sqlSourceDir.getAbsolutePath()+"] not exists");
		}
		if(!sqlSourceDir.canRead()){
			throw new CoreException("Source dir ["+sqlSourceDir.getAbsolutePath()+"] cannot read");
		}
		
		if(!sqlDestDir.exists()){
			throw new CoreException("Dest dir ["+sqlDestDir.getAbsolutePath()+"] not exists");
		}
		if(!sqlDestDir.canWrite()){
			throw new CoreException("Dest dir ["+sqlDestDir.getAbsolutePath()+"] cannot write");
		}
		
		int prefix = 0;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		
		// BASE
		File [] f = sqlSourceDir.listFiles();
		if(f!=null) {
			for (int i = 0; i < f.length; i++) {
				if(f[i].isFile()) {
					createSqlEngine(sqlSourceDir, f[i].getName(), 
							sqlDestDir, sqlScriptName, prefix, bout,
							splitDdlDml);
				}
			}
		}
		
		File dest = new File(sqlDestDir, sqlScriptName);
		
		bout.flush();
		bout.close();
		FileSystemUtilities.writeFile(dest, bout.toByteArray());
		
		if(splitDdlDml){
			
			/**System.out.println("Split init ...");*/
			
			File destInit = new File(sqlDestDir, dest.getName().replace(".sql", "_init.sql"));
			splitFileForDDLtoDML(dest, dest, destInit);
		}
	
	}
	
	private static void buildSqlAggiornamento(File sqlSourceDir, File sqlDestDir, String sqlScriptName,
			String precedenteVersione, String versioneAttuale, String tipoDatabase,
			boolean configurazioneUpgradeRuntime,
			boolean configurazioneUpgradeConfig,
			boolean configurazioneUpgradeTracce,
			boolean configurazioneUpgradeStatistiche,
			boolean configurazioneUpgradeMonitoraggio) throws CoreException, IOException, UtilsException {


		if(!sqlSourceDir.exists()){
			throw new CoreException("Source dir ["+sqlSourceDir.getAbsolutePath()+"] not exists");
		}
		if(!sqlSourceDir.canRead()){
			throw new CoreException("Source dir ["+sqlSourceDir.getAbsolutePath()+"] cannot read");
		}

		if(!sqlDestDir.exists()){
			throw new CoreException("Dest dir ["+sqlDestDir.getAbsolutePath()+"] not exists");
		}
		if(!sqlDestDir.canWrite()){
			throw new CoreException("Dest dir ["+sqlDestDir.getAbsolutePath()+"] cannot write");
		}



		if(precedenteVersione==null){
			throw new CoreException("Precedente versione non fornita");
		}
		if(!precedenteVersione.contains(".")){
			throw new CoreException("Precedente versione in un formato non corretto ["+precedenteVersione+"] ('.' not found)");
		}
		int indexOfFirstPoint = precedenteVersione.indexOf(".");
		if(indexOfFirstPoint<=0){
			throw new CoreException("Precedente versione in un formato non corretto ["+precedenteVersione+"] ('.' not found with index)");
		}
		String productVersionString = precedenteVersione.substring(0, indexOfFirstPoint);
		int productVersion = -1;
		try{
			productVersion = Integer.parseInt(productVersionString);
		}catch(Exception e){
			throw new CoreException("Precedente versione in un formato non corretto ["+precedenteVersione+"] (productVersion:"+productVersionString+"): "+e.getMessage(),e);
		}
		int indexOfSecondPoint = precedenteVersione.indexOf(".",indexOfFirstPoint+1);
		if(indexOfSecondPoint<=0 || indexOfSecondPoint<=indexOfFirstPoint){
			throw new CoreException("Precedente versione in un formato non corretto ["+precedenteVersione+"] (second '.' not found)");
		}
		String majorVersionString = precedenteVersione.substring(indexOfFirstPoint+1, indexOfSecondPoint);
		int majorVersion = -1;
		try{
			majorVersion = Integer.parseInt(majorVersionString);
		}catch(Exception e){
			throw new CoreException("Precedente versione in un formato non corretto ["+precedenteVersione+"] (majorVersion:"+majorVersionString+"): "+e.getMessage(),e);
		}
		if(precedenteVersione.length()<=indexOfSecondPoint){
			throw new CoreException("Precedente versione in un formato non corretto ["+precedenteVersione+"] (length)");
		}
		String minorVersionString = precedenteVersione.substring(indexOfSecondPoint+1,precedenteVersione.length());
		int minorVersion = -1;
		try{
			minorVersion = Integer.parseInt(minorVersionString);
		}catch(Exception e){
			/**throw new CoreException("Precedente versione in un formato non corretto ["+precedenteVersione+"] (minorVersion:"+minorVersionString+"): "+e.getMessage(),e);*/
			// Potrebbe essere una BUILD VERSION
			if(minorVersionString.contains("_")){
				String newMinor = minorVersionString.split("_")[0];
				try{
					minorVersion = Integer.parseInt(newMinor);
				}catch(Exception eInternal){
					throw new CoreException("Precedente versione in un formato non corretto ["+precedenteVersione+"] (minorVersion:"+minorVersionString+" minorVersionBuildNumber:"+newMinor+"): "+eInternal.getMessage(),eInternal);
				}
			}
			else{
				throw new CoreException("Precedente versione in un formato non corretto ["+precedenteVersione+"] (minorVersion:"+minorVersionString+"): "+e.getMessage(),e);
			}

		}
		/**System.out.println(productVersion+"."+majorVersion+"."+minorVersion);*/


		int tmpMajorVersion = majorVersion;
		int tmpMinorVersion = minorVersion;

		if(tipoDatabase==null){
			throw new CoreException("TipoDatabase non fornito");
		}
		TipiDatabase tipiDatabase = TipiDatabase.toEnumConstant(tipoDatabase);
		if(TipiDatabase.DEFAULT.equals(tipiDatabase)) {
			throw new CoreException("TipoDatabase fornito ["+tipoDatabase+"] non valido");
		}

		// Set per tracciare i file SQL già processati (usando il checksum SHA-256 del contenuto per gestire i duplicati)
		Set<String> processedSqlFiles = new HashSet<>();

		// Pre-popola il Set con i checksum delle patch presenti nelle versioni precedenti o uguali alla versione di partenza
		// Questo evita di applicare patch duplicate quando si parte da una versione intermedia
		populateProcessedFilesFromPreviousVersions(sqlSourceDir, productVersion, majorVersion, minorVersion, tipoDatabase, processedSqlFiles);

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		File infoVersion = null;

		while(true){

			String actualVersion = productVersion+"."+tmpMajorVersion+"."+tmpMinorVersion;
			String nextVersion = productVersion+"."+tmpMajorVersion+"."+(tmpMinorVersion+1);
			File version = new File(sqlSourceDir,UPGRADE_PREFIX+actualVersion+"_to_"+nextVersion);
			/**System.out.println("CHECK ["+version.getAbsolutePath()+"] ["+version.exists()+"]");*/
			if(version.exists()){
				if(!version.canRead()){
					throw new CoreException("Source version dir ["+version.getAbsolutePath()+"] cannot read");
				}

				File tmp = createSqlAggiornamentoEngine(version, sqlDestDir, bout, nextVersion, tipoDatabase,
						configurazioneUpgradeRuntime,
						configurazioneUpgradeConfig,
						configurazioneUpgradeTracce,
						configurazioneUpgradeStatistiche,
						configurazioneUpgradeMonitoraggio,
						processedSqlFiles);
				if(tmp!=null) {
					infoVersion = tmp;
				}

				tmpMinorVersion++;
			}
			else{
				// check upgrade to major version +1
				actualVersion = productVersion+"."+tmpMajorVersion+".x";
				nextVersion = productVersion+"."+(tmpMajorVersion+1)+".0";
				version = new File(sqlSourceDir,UPGRADE_PREFIX+actualVersion+"_to_"+nextVersion);
				/**System.out.println("CHECK UPGRADE ["+version.getAbsolutePath()+"] ["+version.exists()+"]");*/
				if(version.exists()){
					if(!version.canRead()){
						throw new CoreException("Source version dir ["+version.getAbsolutePath()+"] cannot read");
					}

					File tmp = createSqlAggiornamentoEngine(version, sqlDestDir, bout, nextVersion, tipoDatabase,
							configurazioneUpgradeRuntime,
							configurazioneUpgradeConfig,
							configurazioneUpgradeTracce,
							configurazioneUpgradeStatistiche,
							configurazioneUpgradeMonitoraggio,
							processedSqlFiles);
					if(tmp!=null) {
						infoVersion = tmp;
					}

					tmpMajorVersion++;
					tmpMinorVersion=0;
				}
				else{
					break;
				}
			}

		}
		
		if(infoVersion!=null) {
			byte[] content = FileSystemUtilities.readBytesFromFile(infoVersion);
			bout.write("\n\n".getBytes());
			bout.write(content);
		}
		
		bout.flush();
		bout.close();
		
		String destFileScriptSql = sqlScriptName.replace(".sql", "_upgrade_"+versioneAttuale+".sql");
		
		FileSystemUtilities.writeFile(new File(sqlDestDir, destFileScriptSql), bout.toByteArray());
		
	}
	
	private static File createSqlAggiornamentoEngine(File sqlVersionSourceDir, File sqlDestDir, ByteArrayOutputStream bout , String nextVersion, String tipoDatabase,
			boolean configurazioneUpgradeRuntime,
			boolean configurazioneUpgradeConfig,
			boolean configurazioneUpgradeTracce,
			boolean configurazioneUpgradeStatistiche,
			boolean configurazioneUpgradeMonitoraggio,
			Set<String> processedSqlFiles) throws IOException, CoreException, UtilsException {

		if(sqlDestDir!=null) {
			// nop
		}
		
		File sqlVersionSourceDirDatabase = new File(sqlVersionSourceDir, tipoDatabase);

		File[] files = sqlVersionSourceDirDatabase.listFiles();
		if(files!=null && files.length>0) {

			boolean writeUpgrade = false;
			boolean atLeastOneFileProcessed = false;

			Arrays.sort(files); // sono ordinati per data
			for(File upgradeFile : files) {

				if(upgradeFile.getName().contains("-runtimePdD-")) {
					// runtime
					if(!configurazioneUpgradeRuntime) {
						continue;
					}
				}
				else if(upgradeFile.getName().contains("-archiviComunicazioni-")) {
					// tracce
					if(!configurazioneUpgradeTracce) {
						continue;
					}
				}
				else if(upgradeFile.getName().contains("-informazioniStatistiche-")) {
					// statistiche
					if(!configurazioneUpgradeStatistiche) {
						continue;
					}
				}
				else if(upgradeFile.getName().contains("-monitoraggio-")) {
					// monitoraggio
					if(!configurazioneUpgradeMonitoraggio) {
						continue;
					}
				}
				else {
					// configurazione (configurazionePdD, registroServizi)
					if(!configurazioneUpgradeConfig) {
						continue;
					}
				}

				// Verifica se il file è già stato processato (gestione patch duplicate)
				// Usa il checksum del contenuto per identificare file identici
				String fileChecksum = calculateFileChecksum(upgradeFile);
				if(processedSqlFiles.contains(fileChecksum)) {
					// File già processato in una versione precedente (stesso contenuto), skip
					continue;
				}

				if(!writeUpgrade) {
					if(bout!=null){
						if(bout.size()>0){
							bout.write("\n\n".getBytes());
						}
						bout.write("-- Upgrade to ".getBytes());
						bout.write(nextVersion.getBytes());
						bout.write("\n".getBytes());
					}
					writeUpgrade = true;
				}

				createSqlAggiornamentoEngine(upgradeFile, bout);

				// Aggiungi il checksum del file al set dei file processati
				processedSqlFiles.add(fileChecksum);
				atLeastOneFileProcessed = true;
			}

			// Se la directory esiste ma nessun file è stato processato (tutti duplicati),
			// scrivi comunque il commento di upgrade
			if(!atLeastOneFileProcessed && bout!=null) {
				if(bout.size()>0){
					bout.write("\n\n".getBytes());
				}
				bout.write("-- Upgrade to ".getBytes());
				bout.write(nextVersion.getBytes());
				bout.write("\n".getBytes());
			}

		}

		File sqlVersionSourceDirInfoVersioneUpgrade = new File(sqlVersionSourceDir, "info-patch.sql");
		if(sqlVersionSourceDirInfoVersioneUpgrade.exists()) {
			return sqlVersionSourceDirInfoVersioneUpgrade;
		}

		return null;
	}
	
	
	private static void createSqlEngine(File sqlSourceDir,String sourceFile,File sqlDestDir, String destFile,
			int prefix,ByteArrayOutputStream bout,
			boolean splitDdlDml) throws UtilsException, IOException {
	
		File src = new File(sqlSourceDir, sourceFile);
		if(bout!=null){
			byte[] b = FileSystemUtilities.readBytesFromFile(src);
			if(bout.size()>0){
				bout.write("\n\n".getBytes());
			}
			bout.write(b);
		}
		else{
			File dest = new File(sqlDestDir, parsePrefix(prefix)+destFile);
			FileSystemUtilities.copy(src, dest);
			if(splitDdlDml){
				File destInit = new File(sqlDestDir, dest.getName().replace(".sql", "_init.sql"));
				splitFileForDDLtoDML(dest, dest, destInit);
			}
		}
	}
	
	private static void createSqlAggiornamentoEngine(File upgradeFile,ByteArrayOutputStream bout) throws CoreException, UtilsException, IOException{
		if(bout==null) {
			throw new CoreException("Param bout is null");
		}
		byte[] b = FileSystemUtilities.readBytesFromFile(upgradeFile);
		if(bout.size()>0){
			bout.write("\n\n".getBytes());
		}
		bout.write(b);
	}
	
	private static String parsePrefix(int prefix){
		if(prefix<10){
			return "0"+prefix+"_";
		}
		else{
			return prefix+"_";
		}
	}

	private static String calculateFileChecksum(File file) throws CoreException {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] fileBytes = FileSystemUtilities.readBytesFromFile(file);
			byte[] hashBytes = digest.digest(fileBytes);

			// Converti hash in stringa esadecimale
			StringBuilder hexString = new StringBuilder();
			for (byte b : hashBytes) {
				String hex = Integer.toHexString(0xff & b);
				if(hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (Exception e) {
			throw new CoreException("Errore nel calcolo del checksum del file ["+file.getAbsolutePath()+"]: "+e.getMessage(), e);
		}
	}

	private static void populateProcessedFilesFromPreviousVersions(File sqlSourceDir, int productVersion,
			int startMajorVersion, int startMinorVersion, String tipoDatabase, Set<String> processedSqlFiles)
			throws CoreException {

		// Scansiona tutte le directory di upgrade dalla versione 0.0 fino ALLA PRECEDENTE della versione di riferimento
		// per popolare il Set con i checksum delle patch già presenti nella versione di partenza
		//
		// IMPORTANTE:
		// - Se parto da 3.3.17, includo nel Set le patch fino a upgrade_3.3.16_to_3.3.17 (esclusa upgrade_3.3.17_to_3.3.18)
		// - Se parto da 3.3.18, includo nel Set le patch fino a upgrade_3.3.17_to_3.3.18 (esclusa upgrade_3.3.x_to_3.4.0)
		//
		// La semantica è: upgrade_X.Y.Z_to_X.Y.(Z+1) fa parte della versione X.Y.(Z+1), non della X.Y.Z
		//
		// BASELINE VERSION: Ogni directory di upgrade può contenere un file baseline-version.txt
		// che specifica quale versione di base dati considerare come riferimento.
		// Esempio: upgrade_3.4.0_to_3.4.1/baseline-version.txt contenente "3.3.17" indica che la 3.4.0
		// è uscita dopo la 3.3.17 ma prima della 3.3.18, quindi partendo da 3.4.0 le patch della 3.3.18
		// NON devono essere considerate già applicate.

		// Controlla se esiste un file baseline-version.txt nella prima directory da processare (quella dell'upgrade in corso)
		int[] baselineVersion = readBaselineVersionFromCurrentUpgradeDir(sqlSourceDir, productVersion,
				startMajorVersion, startMinorVersion);
		int effectiveMajorVersion = baselineVersion[0];
		int effectiveMinorVersion = baselineVersion[1];

		int tmpMajorVersion = 0;
		int tmpMinorVersion = 0;
		boolean continueScanning = true;

		while(continueScanning) {
			// Verifico se ho raggiunto la versione di riferimento (baseline o versione di partenza)
			if(tmpMajorVersion == effectiveMajorVersion && tmpMinorVersion == effectiveMinorVersion) {
				// Ho raggiunto la versione di riferimento, esco SENZA processarla
				continueScanning = false;
				continue;
			}

			boolean processed = scanMinorVersionUpgrade(sqlSourceDir, productVersion, tmpMajorVersion, tmpMinorVersion,
					tipoDatabase, processedSqlFiles);

			if(processed) {
				tmpMinorVersion++;
			}
			else {
				// Non ho trovato upgrade_X.Y.Z_to_X.Y.(Z+1)
				// Potrebbe esserci upgrade_X.Y.x_to_X.(Y+1).0, ma questa directory
				// NON va inclusa nel pre-popolamento se la versione di partenza è X.Y.Z

				// Se ho già superato la versione di riferimento, esco
				if(tmpMajorVersion > effectiveMajorVersion ||
						(tmpMajorVersion == effectiveMajorVersion && tmpMinorVersion > effectiveMinorVersion)) {
					continueScanning = false;
				}
				else {
					// Provo la prossima minor version
					tmpMinorVersion++;
					if(tmpMinorVersion > 200) { // Safety check per evitare loop infiniti
						// Passo alla major version successiva
						tmpMajorVersion++;
						tmpMinorVersion = 0;
						if(tmpMajorVersion > effectiveMajorVersion) {
							continueScanning = false;
						}
					}
				}
			}
		}
	}

	private static int[] readBaselineVersionFromCurrentUpgradeDir(File sqlSourceDir, int productVersion,
			int startMajorVersion, int startMinorVersion) throws CoreException {

		// Determina la PRIMA directory di upgrade da processare (quella che parte dalla versione di partenza)
		// Questa è la directory dell'upgrade in corso che può contenere un file baseline-version.txt
		// con la versione di riferimento per il pre-popolamento

		File currentUpgradeDir;

		// La prima directory è upgrade_X.Y.Z_to_X.Y.(Z+1) oppure upgrade_X.Y.x_to_X.(Y+1).0
		String fromVersion = productVersion+"."+startMajorVersion+"."+startMinorVersion;
		String toVersion = productVersion+"."+startMajorVersion+"."+(startMinorVersion+1);
		currentUpgradeDir = new File(sqlSourceDir, UPGRADE_PREFIX+fromVersion+"_to_"+toVersion);

		// Se non esiste, potrebbe essere un upgrade major
		if(!currentUpgradeDir.exists()) {
			fromVersion = productVersion+"."+startMajorVersion+".x";
			toVersion = productVersion+"."+(startMajorVersion+1)+".0";
			currentUpgradeDir = new File(sqlSourceDir, UPGRADE_PREFIX+fromVersion+"_to_"+toVersion);
		}

		// Cerca il file baseline-version.txt nella directory dell'upgrade corrente
		File baselineFile = new File(currentUpgradeDir, "baseline-version.txt");
		if(!baselineFile.exists()) {
			// Nessuna baseline specificata, usa la versione di partenza come riferimento per il pre-popolamento
			return new int[]{startMajorVersion, startMinorVersion};
		}

		// Leggi e parse della baseline version
		return parseBaselineVersionFile(baselineFile, productVersion);
	}

	private static int[] parseBaselineVersionFile(File baselineFile, int productVersion) throws CoreException {

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(baselineFile));
			String baselineVersionString = br.readLine();

			if(baselineVersionString == null || baselineVersionString.trim().isEmpty()) {
				throw new CoreException("File baseline-version.txt ["+baselineFile.getAbsolutePath()+"] vuoto o non contiene una versione valida");
			}

			baselineVersionString = baselineVersionString.trim();

			// Parse della baseline version (formato atteso: X.Y.Z, estraiamo Y e Z)
			if(!baselineVersionString.contains(".")) {
				throw new CoreException("Baseline version ["+baselineVersionString+"] in formato non corretto: '.' non trovato");
			}

			int firstDot = baselineVersionString.indexOf(".");
			int secondDot = baselineVersionString.indexOf(".", firstDot + 1);

			if(secondDot <= firstDot) {
				throw new CoreException("Baseline version ["+baselineVersionString+"] in formato non corretto: secondo '.' non trovato");
			}

			// Verifica che il product version corrisponda
			String productVersionString = baselineVersionString.substring(0, firstDot);
			int baselineProductVersion;
			try {
				baselineProductVersion = Integer.parseInt(productVersionString);
			} catch (Exception e) {
				throw new CoreException("Baseline version ["+baselineVersionString+"] product version ["+productVersionString+"] non valida: "+e.getMessage(), e);
			}

			if(baselineProductVersion != productVersion) {
				throw new CoreException("Baseline version ["+baselineVersionString+"] ha product version ["+baselineProductVersion+"] diversa da quella attesa ["+productVersion+"]");
			}

			String majorString = baselineVersionString.substring(firstDot + 1, secondDot);
			String minorString = baselineVersionString.substring(secondDot + 1);

			int baselineMajor;
			int baselineMinor;

			try {
				baselineMajor = Integer.parseInt(majorString);
			} catch (Exception e) {
				throw new CoreException("Baseline version ["+baselineVersionString+"] major version ["+majorString+"] non valida: "+e.getMessage(), e);
			}

			try {
				baselineMinor = Integer.parseInt(minorString);
			} catch (Exception e) {
				throw new CoreException("Baseline version ["+baselineVersionString+"] minor version ["+minorString+"] non valida: "+e.getMessage(), e);
			}

			return new int[]{baselineMajor, baselineMinor};

		} catch (CoreException ce) {
			throw ce;
		} catch (Exception e) {
			throw new CoreException("Errore nella lettura del file baseline-version.txt ["+baselineFile.getAbsolutePath()+"]: "+e.getMessage(), e);
		} finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch (Throwable t) {
				// ignore
			}
		}
	}

	private static boolean scanMinorVersionUpgrade(File sqlSourceDir, int productVersion, int tmpMajorVersion,
			int tmpMinorVersion, String tipoDatabase, Set<String> processedSqlFiles) throws CoreException {

		String actualVersion = productVersion+"."+tmpMajorVersion+"."+tmpMinorVersion;
		String nextVersion = productVersion+"."+tmpMajorVersion+"."+(tmpMinorVersion+1);
		File version = new File(sqlSourceDir,UPGRADE_PREFIX+actualVersion+"_to_"+nextVersion);

		if(version.exists() && version.canRead()) {
			addChecksumsFromDirectory(version, tipoDatabase, processedSqlFiles);
			return true;
		}
		return false;
	}

	private static void addChecksumsFromDirectory(File versionDir, String tipoDatabase, Set<String> processedSqlFiles)
			throws CoreException {

		File sqlVersionSourceDirDatabase = new File(versionDir, tipoDatabase);
		File[] files = sqlVersionSourceDirDatabase.listFiles();
		if(files!=null && files.length>0) {
			for(File upgradeFile : files) {
				String fileChecksum = calculateFileChecksum(upgradeFile);
				processedSqlFiles.add(fileChecksum);
			}
		}
	}
	
	private static void splitFileForDDLtoDML(File sqlFile,File sqlFileDest,File sqlFileInitDest) throws IOException, UtilsException{
		
		boolean dmlOpen = false;
		
		ByteArrayOutputStream boutDDL = new ByteArrayOutputStream();
		ByteArrayOutputStream boutDML = new ByteArrayOutputStream();
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(sqlFile));
		    String line;
		    while ((line = br.readLine()) != null) {
		       // process the line.
		    	
		    	if(dmlOpen){
		    		boutDML.write(line.getBytes());
		    		boutDML.write("\n".getBytes());
		    		if(line.contains(";")){
		    			dmlOpen = false; // finish
		    		}
		    	}
		    	else{
			    	if(isDML(line)){
			    		dmlOpen = true; // start
			    		boutDML.write(line.getBytes());
			    		boutDML.write("\n".getBytes());
			    		if(line.contains(";")){
			    			dmlOpen = false; // finish (in una unica riga)
			    		}
			    	}
			    	else{
			    		boutDDL.write(line.getBytes());
			    		boutDDL.write("\n".getBytes());
			    	}
		    	}
		    	
		    }
		}finally {
			try {
				if(br!=null)
					br.close();
			}catch(Throwable t) {
				// ignore
			}
		}
	    
	    /**System.out.println("DDL["+boutDDL.size()+"] DML["+boutDML.size()+"]");*/
	    
	    if(boutDDL.size()>0){
	    	FileSystemUtilities.writeFile(sqlFileDest, boutDDL.toByteArray());
	    }
	    if(boutDML.size()>0){
	    	FileSystemUtilities.writeFile(sqlFileInitDest, boutDML.toByteArray());
	    }
		
	}
	private static boolean isDML(String line){
		
		String tmp = line+"";
		tmp = tmp.trim().toLowerCase();
		
		if(tmp.startsWith("insert ")){
			
			/** check hsql, es: INSERT INTO db_info_init_seq VALUES (NEXT VALUE FOR seq_db_info);*/
			if(tmp.contains("_init_seq ") && tmp.contains("next value ") ){
				return false;

			}
			
			// insert on tracce_ext_protocol_info
			return !(tmp.contains("insert on "));

		} 
		else if(tmp.startsWith("update ")){
			
			return true;
			
		}
		else if(tmp.startsWith("delete ")){
			
			// ON DELETE CASCADE
			return !(tmp.contains("on delete cascade"));
			
		}
		return false;
		
	}
	
}
