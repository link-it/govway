/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.serialization.FilteredObject;
import org.openspcoop2.utils.serialization.IDBuilder;
import org.openspcoop2.web.lib.audit.AuditException;
import org.openspcoop2.web.lib.audit.costanti.Costanti;
import org.openspcoop2.web.lib.audit.costanti.StatoOperazione;
import org.openspcoop2.web.lib.audit.costanti.TipoOperazione;
import org.openspcoop2.web.lib.audit.dao.Appender;
import org.openspcoop2.web.lib.audit.dao.Binary;
import org.openspcoop2.web.lib.audit.dao.Configurazione;
import org.openspcoop2.web.lib.audit.dao.Filtro;
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
public class AuditAppender {

	/* Configurazione */
	private static Configurazione configurazioneAuditing;
	private static IDBuilder idBuilder;
	private static HashMap<String,IAuditAppender> appenders = new HashMap<String,IAuditAppender>();
	
	public synchronized void initializeAudit(Configurazione configurazioneAuditing,
			IDBuilder idBuilder) throws AuditException{
		
		try{
		
			AuditAppender.appenders.clear();	
			
			AuditAppender.configurazioneAuditing= configurazioneAuditing;
			AuditAppender.idBuilder = idBuilder;
			
			for(int i=0; i<AuditAppender.configurazioneAuditing.sizeAppender(); i++){
				
				// Istanzio appender
				Appender appenderConf = AuditAppender.configurazioneAuditing.getAppender(i);
				Class<?> c = Class.forName(appenderConf.getClassName());
				IAuditAppender appender = (IAuditAppender) c.newInstance();
				
				// Inizializzo appender
				Properties propertiesAppender = new Properties();
				for(int j=0; j<appenderConf.sizeProperties(); j++){
					propertiesAppender.put(appenderConf.getProperty(j).getName(), 
							appenderConf.getProperty(j).getValue());
				}
				appender.initAppender(appenderConf.getNome(),propertiesAppender);
				
				AuditAppender.appenders.put(appenderConf.getNome(),appender);
			}
			
		}catch(Exception e){
			throw new AuditException("InizializzazioneFallita: "+e.getMessage(),e);
		}
	}
	
	public void updateConfigurazioneAuditing(Configurazione configurazioneAuditing) throws AuditException{
		try{
			synchronized(AuditAppender.configurazioneAuditing){
			
				AuditAppender.configurazioneAuditing = configurazioneAuditing;
				
				AuditAppender.appenders.clear();
				
				for(int i=0; i<AuditAppender.configurazioneAuditing.sizeAppender(); i++){
					
					// Istanzio appender
					Appender appenderConf = AuditAppender.configurazioneAuditing.getAppender(i);
					Class<?> c = Class.forName(appenderConf.getClassName());
					IAuditAppender appender = (IAuditAppender) c.newInstance();
					
					// Inizializzo appender
					Properties propertiesAppender = new Properties();
					for(int j=0; j<appenderConf.sizeProperties(); j++){
						propertiesAppender.put(appenderConf.getProperty(j).getName(), 
								appenderConf.getProperty(j).getValue());
					}
					appender.initAppender(appenderConf.getNome(),propertiesAppender);
					
					AuditAppender.appenders.put(appenderConf.getNome(),appender);
				}
			}
		}catch(Exception e){
			throw new AuditException("AggiornamentoFallito: "+e.getMessage(),e);
		}
	}

	
	
	public IDOperazione registraOperazioneInFaseDiElaborazione(TipoOperazione tipoOperazione,Object object,String user,String interfaceMsg) throws AuditException,AuditDisabilitatoException{
		
		if(AuditAppender.configurazioneAuditing.isAuditEngineEnabled()==false){
			throw new AuditDisabilitatoException("Audit engine disabilitato");
		}
		
		try{
			
				
			Operation operation = new Operation();
			operation.setTipologia(tipoOperazione.toString());
			operation.setUtente(user);
			operation.setStato(StatoOperazione.requesting.toString());
			operation.setTimeRequest(DateManager.getDate());
			operation.setTimeExecute(DateManager.getDate());
			operation.setInterfaceMsg(interfaceMsg);
			
			if(object!=null){
				
				operation.setTipoOggetto(AuditAppender.idBuilder.getSimpleName(object));
				operation.setObjectId(AuditAppender.idBuilder.toID(object));
				operation.setObjectOldId(AuditAppender.idBuilder.toOldID(object));
				operation.setObjectClass(object.getClass().getName());
				
			}else{
				throw new AuditException("Object riguardante l'operazione non definito");
			}

			
			// Filtro operazioni
			org.openspcoop2.utils.serialization.Filter listFilter = new org.openspcoop2.utils.serialization.Filter();
			String objectDetails = filtraOperazione(operation,object,listFilter);
			
			if(objectDetails!=null){
				operation.setObjectDetails(objectDetails);
				
				// Aggiunto binaries filtrati
				for(int i=0; i<listFilter.sizeFilteredObjects(); i++){
					FilteredObject filteredObject = listFilter.getFilteredObject(i);
					
					Binary binary = new Binary();
					binary.setBinaryId(filteredObject.getId());
					binary.setChecksum(BigInteger.valueOf(filteredObject.getChecksum()));
					operation.addBinary(binary);
				}
			}
				
			
			// Appender
			IDOperazione idOperazione = new IDOperazione();
			if(AuditAppender.appenders!=null && AuditAppender.appenders.keySet()!=null){
				Iterator<String> iterator = AuditAppender.appenders.keySet().iterator();
				while(iterator.hasNext()){
					
					String appenderName = iterator.next();
					IAuditAppender appender =  AuditAppender.appenders.get(appenderName);
									
					Object idOperazioneAppender =  appender.registraOperazioneInFaseDiElaborazione(operation);
					idOperazione.addIdOperazione(appenderName, idOperazioneAppender);
				}
				return idOperazione;
			}
			else{
				throw new Exception("Appender non forniti");
			}
				
			
		}catch(AuditDisabilitatoException e){
			throw e;
		}catch(Exception e){
			throw new AuditException("registraOperazioneInFaseDiElaborazione error: "+e.getMessage(),e);
		}
		
	}
	
	
	
	public void registraOperazioneAccesso(TipoOperazione tipoOperazione,String user,String interfaceMsg) throws AuditException,AuditDisabilitatoException{
		
		if(AuditAppender.configurazioneAuditing.isAuditEngineEnabled()==false){
			throw new AuditDisabilitatoException("Audit engine disabilitato");
		}
		
		try{
			
				
			Operation operation = new Operation();
			operation.setTipologia(tipoOperazione.toString());
			operation.setUtente(user);
			operation.setStato(StatoOperazione.completed.toString());
			operation.setTimeRequest(DateManager.getDate());
			operation.setTimeExecute(DateManager.getDate());
			operation.setInterfaceMsg(interfaceMsg);
					
			
			// Filtro operazioni
			filtraOperazione(operation);
			
			
			// Appender
			IDOperazione idOperazione = new IDOperazione();
			if(AuditAppender.appenders!=null && AuditAppender.appenders.keySet()!=null){
				Iterator<String> iterator = AuditAppender.appenders.keySet().iterator();
				while(iterator.hasNext()){
					
					String appenderName = iterator.next();
					IAuditAppender appender =  AuditAppender.appenders.get(appenderName);
									
					Object idOperazioneAppender =  appender.registraOperazioneInFaseDiElaborazione(operation);
					idOperazione.addIdOperazione(appenderName, idOperazioneAppender);
				}
			}
			else{
				throw new Exception("Appender non forniti");
			}
				
			
		}catch(AuditDisabilitatoException e){
			throw e;
		}catch(Exception e){
			throw new AuditException("registraOperazioneInFaseDiElaborazione error: "+e.getMessage(),e);
		}
		
	}
	
	
	
	public void registraOperazioneCompletataConSuccesso(IDOperazione idOperazione,String interfaceMsg) throws AuditException,AuditDisabilitatoException{
		
		if(AuditAppender.configurazioneAuditing.isAuditEngineEnabled()==false){
			throw new AuditDisabilitatoException("Audit engine disabilitato");
		}
		
		try{
			
			if(idOperazione==null){
				throw new AuditException("Identificativo dell'operazione per cui cambiare stato non fornito");
			}
			
			if(AuditAppender.appenders!=null && AuditAppender.appenders.keySet()!=null){
				Iterator<String> iterator = AuditAppender.appenders.keySet().iterator();
				while(iterator.hasNext()){
					
					String appenderName = iterator.next();
					IAuditAppender appender =  AuditAppender.appenders.get(appenderName);
					Object id = idOperazione.getIdOperazione(appenderName);
					if(id==null){
						throw new AuditException("Identificativo dell'operazione per l'appender["+appenderName+"] non fornito");
					}
					if(id instanceof Operation){
						Operation op = (Operation) id;
						op.setInterfaceMsg(interfaceMsg);
					}
					
					appender.registraOperazioneCompletataConSuccesso(id);
				}
			}
			else{
				throw new Exception("Appender non forniti");
			}
				
			
		}catch(AuditDisabilitatoException e){
			throw e;
		}catch(Exception e){
			throw new AuditException("registraOperazioneCompletataConSuccesso error: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	public void registraOperazioneTerminataConErrore(IDOperazione idOperazione,String motivoErrore,String interfaceMsg) throws AuditException,AuditDisabilitatoException{
		
		if(AuditAppender.configurazioneAuditing.isAuditEngineEnabled()==false){
			throw new AuditDisabilitatoException("Audit engine disabilitato");
		}
		
		try{
			
			if(idOperazione==null){
				throw new AuditException("Identificativo dell'operazione per cui cambiare stato non fornito");
			}
						
			if(AuditAppender.appenders!=null && AuditAppender.appenders.keySet()!=null){
				Iterator<String> iterator = AuditAppender.appenders.keySet().iterator();
				while(iterator.hasNext()){
					
					String appenderName = iterator.next();
					IAuditAppender appender =  AuditAppender.appenders.get(appenderName);
					Object id = idOperazione.getIdOperazione(appenderName);
					if(id==null){
						throw new AuditException("Identificativo dell'operazione per l'appender["+appenderName+"] non fornito");
					}		
					if(id instanceof Operation){
						Operation op = (Operation) id;
						op.setInterfaceMsg(interfaceMsg);
					}
									
					appender.registraOperazioneTerminataConErrore(id,motivoErrore);
				}
			}
			else{
				throw new Exception("Appender non forniti");
			}
				
			
		}catch(AuditDisabilitatoException e){
			throw e;
		}catch(Exception e){
			throw new AuditException("registraOperazioneCompletataConSuccesso error: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	// -------------- UTILITY -------------------
	
	private String serializeJsonObject(Object o,org.openspcoop2.utils.serialization.Filter listFilter) throws AuditException{
		try{
			listFilter.addFilterByValue(byte[].class);
			org.openspcoop2.utils.serialization.JSonSerializer serializer = 
				new org.openspcoop2.utils.serialization.JSonSerializer(listFilter,AuditAppender.idBuilder);
			return  serializer.getObject(o);		
		}catch(Exception e){
			throw new AuditException("serializeJsonObject error: "+e.getMessage(),e);
		}
	}
	
	private String serializeXMLObject(Object o,org.openspcoop2.utils.serialization.Filter listFilter) throws AuditException{
		try{
			listFilter.addFilterByValue(byte[].class);
			org.openspcoop2.utils.serialization.XMLSerializer serializer = 
				new org.openspcoop2.utils.serialization.XMLSerializer(listFilter,AuditAppender.idBuilder);
			return  serializer.getObject(o);		
		}catch(Exception e){
			throw new AuditException("serializeXMLObject error: "+e.getMessage(),e);
		}
	}
	
	private void filtraOperazione(Operation operation) throws AuditException,AuditDisabilitatoException{
		this.filtraOperazione(operation, null, null);
	}
	private String filtraOperazione(Operation operation,Object object,org.openspcoop2.utils.serialization.Filter listFilter) throws AuditException,AuditDisabilitatoException{
		
		try{
		
			ArrayList<Filtro> filtri = AuditAppender.configurazioneAuditing.getFiltri();
			boolean auditEnabledDefault = AuditAppender.configurazioneAuditing.isAuditEnabled();
			boolean dumpEnabledDefault = AuditAppender.configurazioneAuditing.isDumpEnabled();
			String objectDetails = null;
			
			for(int i=0; i<filtri.size(); i++){
				
				Filtro filtro = filtri.get(i);
				
				//System.out.println("ANALIZZO FILTRO["+i+"]: "+filtro.toString());
				
				if(filtro.getUsername()==null &&
						filtro.getTipoOperazione()==null &&
						filtro.getTipoOggettoInModifica()==null &&
						filtro.getStatoOperazione()==null &&
						filtro.getDump()==null){
					throw new AuditException("Filtro("+i+1+") non valido: nessun meccanismo di filtro definito");
				}
				
				if(filtro.getUsername()!=null){
					// Se e' definito un username nel filtro controllo che corrisponda
					if(filtro.getUsername().equals(operation.getUtente())==false){
						continue;
					}
				}
				
				if(filtro.getTipoOperazione()!=null){
					// Se e' definito un tipo di operazione nel filtro controllo che corrisponda
					if(filtro.getTipoOperazione().toString().equals(operation.getTipologia())==false){
						continue;
					}
				}
				
				if(filtro.getTipoOggettoInModifica()!=null){
					// Se e' definito un TipoOggettoInModifica nel filtro controllo che corrisponda
					if(filtro.getTipoOggettoInModifica().equals(operation.getTipoOggetto())==false){
						continue;
					}
				}
				
				if(filtro.getStatoOperazione()!=null){
					// Se e' definito uno stato dell'operazione nel filtro controllo che corrisponda
					if(filtro.getStatoOperazione().toString().equals(operation.getStato())==false){
						continue;
					}
				}
				
				if(filtro.getDump()!=null){
					
					if(object==null){
						continue; // Questo filtro per essere matchato deve essere controllato con un oggetto
					}
					
					if(objectDetails==null){
						// Serializzo l'oggetto
						objectDetails = this.serialize(object, listFilter);
					}
					
					// Se e' definito un filtro sul contenuto dell'operazione nel filtro controllo che corrisponda
					if(filtro.isDumpExprRegular()){
						if(RegularExpressionEngine.getStringMatchPattern(objectDetails, filtro.getDump())==null){
							continue;
						}
					}else{
						if(objectDetails.contains(filtro.getDump())==false){
							continue;
						}
					}
				}
								
				// Se arriviamo a questo punto significa che l'operazione soddisfa il filtro.
				if(filtro.isAuditEnabled()==false){
					throw new AuditDisabilitatoException("Audit disabilitato, criterio adottato in base al filtro numero "+(i+1));
				}
				if(filtro.isDumpEnabled()){
					if(object!=null){
						// Se non avevo gia' serializzato prima per la ricerca tramite contenuto
						if(objectDetails==null){
							// Serializzo l'oggetto
							objectDetails = this.serialize(object, listFilter);
						}
					}
				}else{
					if(objectDetails!=null){
						objectDetails=null; // era stato utilizzato per effettuare filtro sui contenuti ma non deve essere dumpato
					}
				}
								
				return objectDetails; // regola matcha
			}
			
			// Se nessun filtro sopra viene matchato, si applica il criterio di default
			if(auditEnabledDefault == false){
				throw new AuditDisabilitatoException("Audit disabilitato nella configurazione generale (Non vi sono filtri che creano eccezioni)");
			}
			if(dumpEnabledDefault){
				if(object!=null){
					// Se non avevo gia' serializzato prima per la ricerca tramite contenuto
					if(objectDetails==null){
						// Serializzo l'oggetto
						objectDetails = this.serialize(object, listFilter);
					}
				}
			}else{
				if(objectDetails!=null){
					objectDetails=null; // era stato utilizzato per effettuare filtro sui contenuti ma non deve essere dumpato
				}
			}
			
			return objectDetails;
			
		}catch(AuditDisabilitatoException e){
			throw e;
		}catch(Exception e){
			throw new AuditException("filtraOperazione error: "+e.getMessage(),e);
		}
	}
	
	
	private String serialize(Object object,org.openspcoop2.utils.serialization.Filter listFilter)throws AuditException{
		if(Costanti.DUMP_JSON_FORMAT.equals(AuditAppender.configurazioneAuditing.getDumpFormat())){
			return this.serializeJsonObject(object, listFilter);
		}
		else if(Costanti.DUMP_XML_FORMAT.equals(AuditAppender.configurazioneAuditing.getDumpFormat())){
			return this.serializeXMLObject(object, listFilter);
		}else {
			throw new AuditException("Tipo di formattazione non conosciuta: "+AuditAppender.configurazioneAuditing.getDumpFormat());
		}
	}
}
