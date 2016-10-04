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


package org.openspcoop2.web.lib.audit.appender;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.CollectionProperties;
import org.openspcoop2.utils.resources.PropertiesUtilities;
import org.openspcoop2.utils.xml.JiBXUtils;
import org.openspcoop2.web.lib.audit.AuditException;
import org.openspcoop2.web.lib.audit.costanti.StatoOperazione;
import org.openspcoop2.web.lib.audit.dao.Operation;

/**
 * Appender per registrare operazione di audit
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class AuditLog4JAppender implements IAuditAppender {

	
    private String nomeAppender = null;
	private Logger logger = null;
	private boolean xml = true;
    
	@Override
	public void initAppender(String nomeAppender,Properties properties) throws AuditException{
	
		java.util.Properties loggerProperties = new java.util.Properties();
		try{
			this.nomeAppender = nomeAppender;
		
			String fileConfigurazione = properties.getProperty("fileConfigurazione");
			if(fileConfigurazione==null){
				throw new Exception("Proprieta' 'fileConfigurazione' non definita");
			}
			fileConfigurazione = fileConfigurazione.trim();
			
			String nomeFileLoaderInstance = properties.getProperty("nomeFileLoaderInstance");
			if(nomeFileLoaderInstance!=null){
				nomeFileLoaderInstance = nomeFileLoaderInstance.trim();
			}
			
			String nomeProprietaLoaderInstance = properties.getProperty("nomeProprietaLoaderInstance");
			if(nomeProprietaLoaderInstance!=null){
				nomeProprietaLoaderInstance = nomeProprietaLoaderInstance.trim();
			}
			
			String confDirectory = properties.getProperty("confDirectory");
			if(confDirectory!=null){
				confDirectory = confDirectory.trim();
			}
			
			
			
			// Configurazione file
			File fConf = new File(fileConfigurazione);
			if(fConf.exists()){
				loggerProperties.load(new java.io.FileInputStream(fConf));
			}else{
				InputStream is = null;
				try{
					try{
						is = AuditLog4JAppender.class.getResourceAsStream(fileConfigurazione);
					}catch(Exception e){
						if(fileConfigurazione.startsWith("/")){
							throw e;
						}
					}
					if(is==null && fileConfigurazione.startsWith("/")==false){
						try{
							is = AuditLog4JAppender.class.getResourceAsStream("/"+fileConfigurazione);
						}catch(Exception e){
							throw e;
						}
					}
					if(is==null){
						throw new Exception("InputStream ["+fileConfigurazione+"] non trovato");
					}
					loggerProperties.load(is);
				}finally{
					try{
						if(is!=null){
							is.close();
						}
					}catch(Exception eClose){}
				}
			}
			
			
			// File Local Implementation
			if(nomeFileLoaderInstance!=null && nomeProprietaLoaderInstance!=null){
				CollectionProperties loggerPropertiesRidefinito =  
						PropertiesUtilities.searchLocalImplementation(CostantiPdD.OPENSPCOOP2_LOCAL_HOME,LoggerWrapperFactory.getLogger(AuditLog4JAppender.class), 
								nomeProprietaLoaderInstance, nomeFileLoaderInstance, confDirectory);
				if(loggerPropertiesRidefinito!=null && loggerPropertiesRidefinito.size()>0){
					Enumeration<?> ridefinito = loggerPropertiesRidefinito.keys();
					while (ridefinito.hasMoreElements()) {
						String key = (String) ridefinito.nextElement();
						String value = (String) loggerPropertiesRidefinito.get(key);
						if(loggerProperties.containsKey(key)){
							//Object o = 
							loggerProperties.remove(key);
						}
						loggerProperties.put(key, value);
						//System.out.println("CHECK NUOVO VALORE: "+loggerProperties.get(key));
					}
				}
			}
			
			LoggerWrapperFactory.setLogConfiguration(loggerProperties,true);
			
			// Logger
			String category = properties.getProperty("category");
			if(category==null){
				throw new Exception("Proprieta' 'category' non definita");
			}
			category = category.trim();
			this.logger = LoggerWrapperFactory.getLogger(category);
			
			// xml format
			String xml = properties.getProperty("xml");
			if(xml==null){
				throw new Exception("Proprieta' 'xml' non definita");
			}
			xml = xml.trim();
			this.xml = Boolean.parseBoolean(xml);
			
			
		}catch(Exception e){
			throw new AuditException("Inizializzazione appender["+this.nomeAppender+"] non riuscita:"+e.getMessage(),e);
		}
		
	}
	
	@Override
	public Object registraOperazioneInFaseDiElaborazione(Operation operation) throws AuditException{
		try{
			if(this.xml){
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				JiBXUtils.objToXml(bout, Operation.class, operation);
				bout.flush();
				bout.close();
				this.logger.info(bout.toString());
			}else{
				if(operation.getInterfaceMsg()==null)
					throw new Exception("InterfaceMsg non fornito");
				else
					this.logger.info("<"+new Date()+"> "+ operation.getInterfaceMsg()+"\n");
			}
			return operation;
		}catch(Exception e){
			throw new AuditException("Appender["+this.nomeAppender+"] Errore durante la registrazione dell'operazione: "+e.getMessage(),e);
		}
	}
	
	@Override
	public void registraOperazioneCompletataConSuccesso(Object idOperation) throws AuditException{
		try{
			Operation operation = (Operation)idOperation;
			
			// Aggiorno stato
			operation.setStato(StatoOperazione.completed.toString());
			
			// Aggiorno tempo di esecuzione
			operation.setTimeExecute(DateManager.getDate());
		
			if(this.xml){
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				JiBXUtils.objToXml(bout, Operation.class, operation);
				bout.flush();
				bout.close();
				this.logger.info(bout.toString());
			}else{
				if(operation.getInterfaceMsg()==null)
					throw new Exception("InterfaceMsg non fornito");
				else
					this.logger.info("<"+new Date()+"> "+ operation.getInterfaceMsg()+"\n");
			}
			
		}catch(Exception e){
			throw new AuditException("Appender["+this.nomeAppender+"] Errore durante la registrazione dell'operazione: "+e.getMessage(),e);
		}
	}
	
	@Override
	public void registraOperazioneTerminataConErrore(Object idOperation,String motivoErrore) throws AuditException{
		try{
			Operation operation = (Operation)idOperation;
			
			// Aggiorno stato
			operation.setStato(StatoOperazione.error.toString());
			operation.setError(motivoErrore);
			
			// Aggiorno tempo di esecuzione
			operation.setTimeExecute(DateManager.getDate());
			
			if(this.xml){
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				JiBXUtils.objToXml(bout, Operation.class, operation);
				bout.flush();
				bout.close();
				this.logger.info(bout.toString());
			}else{
				if(operation.getInterfaceMsg()==null)
					throw new Exception("InterfaceMsg non fornito");
				else
					this.logger.info("<"+new Date()+"> "+ operation.getInterfaceMsg()+"\n");
			}
			
		}catch(Exception e){
			throw new AuditException("Appender["+this.nomeAppender+"] Errore durante la registrazione dell'operazione: "+e.getMessage(),e);
		}
	}
	
}
