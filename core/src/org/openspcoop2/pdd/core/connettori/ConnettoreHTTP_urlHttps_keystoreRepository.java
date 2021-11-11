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

package org.openspcoop2.pdd.core.connettori;

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.ObjectUtils.Null;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.protocol.sdk.Busta;

/**     
 * ConnettoreHTTP_urlHttps_keystoreRepository
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTP_urlHttps_keystoreRepository {

	protected static ConnettoreHTTPSProperties readSSLContext(boolean debug, ConnettoreLogger logger, Busta busta, boolean fruizioni) throws Exception {
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		
		// Soggetto Fruitore
		if(busta==null){
			if(debug){
				logger.debug("Dati busta non presenti");
			}
			return null;
		}
		String tipoNomeSoggetto = null;
		if(fruizioni) {
			if(busta.getTipoMittente()==null){
				if(debug){
					logger.debug("Dati busta tipo-mittente non presente");
				}
				return null;
			}
			if(busta.getMittente()==null){
				if(debug){
					logger.debug("Dati busta nome-mittente non presente");
				}
				return null;
			}
			tipoNomeSoggetto = busta.getTipoMittente()+busta.getMittente()+".properties";
		}
		else {
			if(busta.getTipoDestinatario()==null){
				if(debug){
					logger.debug("Dati busta tipo-destinatario non presente");
				}
				return null;
			}
			if(busta.getDestinatario()==null){
				if(debug){
					logger.debug("Dati busta nome-destinatario non presente");
				}
				return null;
			}
			tipoNomeSoggetto = busta.getTipoDestinatario()+busta.getDestinatario()+".properties";
		}
		
		
		// Cerco repository https
		File fileRepository = null;
		if(fruizioni) {
			fileRepository = op2Properties.getConnettoreHttp_urlHttps_repository_inoltroBuste();
		}
		else {
			fileRepository = op2Properties.getConnettoreHttp_urlHttps_repository_consegnaContenutiApplicativi();
		}
		if(debug){
			logger.debug("Cerco file di properties https in ["+fileRepository.getAbsolutePath()+"] ...");
		}
		if(fileRepository.exists()==false){
			if(debug){
				logger.debug("Repository dir ["+fileRepository.getAbsolutePath()+"] per configurazioni HTTPS non esistente");
			}
			return null;
		}
		if(fileRepository.isDirectory()==false){
			logger.error("Repository dir ["+fileRepository.getAbsolutePath()+"] per configurazioni HTTPS non è una directory");
			return null;
		}
		if(fileRepository.canRead()==false){
			logger.error("Repository dir ["+fileRepository.getAbsolutePath()+"] per configurazioni HTTPS non accessibile");
			return null;
		}
		
		// Cerco file per soggetto
		
		if(op2Properties.isConnettoreHttp_urlHttps_cacheEnabled()){
			return getConnettoreHTTPSPropertiesFromCache(fileRepository, tipoNomeSoggetto, debug, logger, op2Properties.getConnettoreHttp_urlHttps_cacheSize(), fruizioni);
		}
		else{
			return getConnettoreHTTPSPropertiesFromRepository(fileRepository, tipoNomeSoggetto, debug, logger, fruizioni);
		}

		
		// Esempio di file properties
		/*
		 trustStoreLocation=/etc/govway/keys/soggetto1.jks
		 trustStorePassword=123456
	   	 trustManagementAlgorithm=PKIX
	     trustStoreType=JKS
	    
	     keyStoreLocation=/etc/govway/keys/soggetto1.jks
	     keyStorePassword=123456
	     keyManagementAlgorithm=SunX509
	     keyStoreType=JKS
	     keyPassword=123456
	    
	     hostnameVerifier=true;
	     sslType=SSLv3
	    */
		
		
	}
	
	
	
	private static Map<String, Object> mapConnettoreHTTPSProperties = new ConcurrentHashMap<String, Object>();
	
	private static String getKeyCache(String tipoNomeSoggetto, boolean fruizioni) {
		return (fruizioni? "fruizione_" : "erogazione_") + tipoNomeSoggetto;
	}
	
	private static org.openspcoop2.utils.Semaphore semaphore = new org.openspcoop2.utils.Semaphore("ConnettoreHTTP_urlHttps_keystoreRepository");
	private static Object _getConnettoreHTTPSPropertiesFromCache(File fileRepository,String tipoNomeSoggetto,
			boolean debug, ConnettoreLogger logger, int size, boolean fruizioni) throws Exception{
		
		semaphore.acquire("_getConnettoreHTTPSPropertiesFromCache");
		try {
			String keyCache = getKeyCache(tipoNomeSoggetto, fruizioni);
			String debugRole = (fruizioni? "fruizione" : "erogazione");
			
			if(mapConnettoreHTTPSProperties.containsKey(keyCache)){
				return mapConnettoreHTTPSProperties.get(keyCache);
			}
			
			int dimensioneMassimaCache = size;
			if(mapConnettoreHTTPSProperties.size()==(dimensioneMassimaCache)){
				// la cache ha raggiunto la sua dimensione massima, prima di aggiungere la nuova informazione la svuoto.
				logger.warn("CacheConnettori ha raggiunto la sua capienza massima ["+dimensioneMassimaCache+"]. Viene svuotata interamente");
				mapConnettoreHTTPSProperties.clear();
			}
			
			ConnettoreHTTPSProperties https = getConnettoreHTTPSPropertiesFromRepository(fileRepository, tipoNomeSoggetto, debug, logger, fruizioni);
			if(https==null){
				if(debug){
					logger.debug("Indicazione configurazione https ("+debugRole+") non presente per soggetto "+tipoNomeSoggetto+" aggiunta in cache");
				}
				mapConnettoreHTTPSProperties.put(keyCache, org.apache.commons.lang.ObjectUtils.NULL);
			}
			else{
				if(debug){
					logger.debug("Configurazione https ("+debugRole+") per soggetto "+tipoNomeSoggetto+" aggiunta in cache");
				}
				mapConnettoreHTTPSProperties.put(keyCache, https);
			}
			
			return https;
		}finally {
			semaphore.release("_getConnettoreHTTPSPropertiesFromCache");
		}
	}
	
	private static ConnettoreHTTPSProperties getConnettoreHTTPSPropertiesFromCache(File fileRepository,String tipoNomeSoggetto,
			boolean debug, ConnettoreLogger logger, int size, boolean fruizioni) throws Exception{
		
		String debugRole = (fruizioni? "fruizione" : "erogazione");
		
		Object o = null;
		if(debug){
			logger.debug("Cerco configurazione https ("+debugRole+") per soggetto "+tipoNomeSoggetto+" in cache ...");
		}
		String keyCache = getKeyCache(tipoNomeSoggetto, fruizioni);
		if(mapConnettoreHTTPSProperties.containsKey(keyCache)){
			o = mapConnettoreHTTPSProperties.get(keyCache);
			if(debug){
				logger.debug("Configurazione https ("+debugRole+") per soggetto "+tipoNomeSoggetto+" trovata in cache (tipo:"+o.getClass().getName()+")");
			}
		}
		else{
			if(debug){
				logger.debug("Configurazione https ("+debugRole+") per soggetto "+tipoNomeSoggetto+" non presente in cache, recupero ...");
			}
			o = _getConnettoreHTTPSPropertiesFromCache(fileRepository,tipoNomeSoggetto,debug,logger,size, fruizioni);
		}
		if(o==null || o instanceof Null){
			if(debug){
				logger.debug("Configurazione https ("+debugRole+") per soggetto "+tipoNomeSoggetto+" non esistente");
			}
			return null;
		}
		else{
			if(debug){
				logger.debug("Configurazione https ("+debugRole+") per soggetto "+tipoNomeSoggetto+" recuperata");
			}
			return (ConnettoreHTTPSProperties) o;
		}
	}
	
	private static ConnettoreHTTPSProperties getConnettoreHTTPSPropertiesFromRepository(File fileRepository, String tipoNomeSoggetto, 
			boolean debug, ConnettoreLogger logger, boolean fruizioni) throws Exception{
		
		String debugRole = (fruizioni? "fruizione" : "erogazione");
		
		File fSoggetto = new File(fileRepository, tipoNomeSoggetto);
		if(fSoggetto.exists()==false){
			if(debug){
				logger.debug("Configurazione ("+debugRole+") ["+fSoggetto.getAbsolutePath()+"] non esistente");
			}
			return null;
		}
		if(fSoggetto.isFile()==false){
			logger.error("Configurazione ("+debugRole+") ["+fSoggetto.getAbsolutePath()+"] non è un file");
			return null;
		}
		if(fSoggetto.canRead()==false){
			logger.error("Configurazione ("+debugRole+") ["+fSoggetto.getAbsolutePath()+"] non accessibile");
			return null;
		}
		
		// Carico file
		if(debug){
			logger.debug("Configurazione https ("+debugRole+") per soggetto "+tipoNomeSoggetto+", carico file di properties ["+fSoggetto.getAbsolutePath()+"] ...");
		}
		Properties p = new Properties();
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(fSoggetto);
			p.load(fin);
		}finally{
			try{
				if(fin!=null){
					fin.close();
				}
			}catch(Exception eClose){}
		}
		Map<String, String> pMap = new HashMap<String, String>();
		Enumeration<?> enP = p.keys();
		while (enP.hasMoreElements()) {
			String key = (String) enP.nextElement();
			pMap.put(key, p.getProperty(key));
		}
		if(debug){
			logger.debug("Configurazione https ("+debugRole+") per soggetto "+tipoNomeSoggetto+", caricato file di properties ["+fSoggetto.getAbsolutePath()+"]");
		}
		return ConnettoreHTTPSProperties.readProperties(pMap);
	}
	
	
	
}
