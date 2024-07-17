/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.lib.audit.appender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.MessageSecurityFlow;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiProprieta;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.pdd.core.byok.DriverBYOKUtilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;
import org.openspcoop2.utils.serialization.FilteredObject;
import org.openspcoop2.utils.serialization.IDBuilder;
import org.openspcoop2.utils.serialization.SerializationConfig;
import org.openspcoop2.web.lib.audit.AuditException;
import org.openspcoop2.web.lib.audit.costanti.Costanti;
import org.openspcoop2.web.lib.audit.dao.Appender;
import org.openspcoop2.web.lib.audit.dao.Configurazione;
import org.openspcoop2.web.lib.audit.dao.Filtro;
import org.openspcoop2.web.lib.audit.log.Binary;
import org.openspcoop2.web.lib.audit.log.Operation;
import org.openspcoop2.web.lib.audit.log.constants.Stato;
import org.openspcoop2.web.lib.audit.log.constants.Tipologia;

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

	private static final String AUDIT_DISABILITATO = "Audit engine disabilitato";
	private static final String APPENDER_NON_FORNITI = "Appender non forniti";
	
	/* Configurazione */
	private static Configurazione configurazioneAuditing;
	private static IDBuilder idBuilder;
	private static HashMap<String,IAuditAppender> appenders = new HashMap<>();
	
	public void initializeAudit(Configurazione configurazioneAuditing,
			IDBuilder idBuilder) throws AuditException{
		initializeAuditEngine(configurazioneAuditing, idBuilder);
	}
	
	private static synchronized void initializeAuditEngine(Configurazione configurazioneAuditing,
			IDBuilder idBuilder) throws AuditException{
		
		try{
		
			AuditAppender.appenders.clear();	
			
			AuditAppender.configurazioneAuditing= configurazioneAuditing;
			AuditAppender.idBuilder = idBuilder;
			
			for(int i=0; i<AuditAppender.configurazioneAuditing.sizeAppender(); i++){
				
				// Istanzio appender
				Appender appenderConf = AuditAppender.configurazioneAuditing.getAppender(i);
				Class<?> c = Class.forName(appenderConf.getClassName());
				IAuditAppender appender = (IAuditAppender) ClassLoaderUtilities.newInstance(c);
				
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
					IAuditAppender appender = (IAuditAppender) ClassLoaderUtilities.newInstance(c);
					
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

	
	
	public IDOperazione registraOperazioneInFaseDiElaborazione(Tipologia tipoOperazione,Object object,String user,String interfaceMsg, boolean registrazioneBinari,
			DriverBYOKUtilities byok) throws AuditException,AuditDisabilitatoException{
		
		if(!AuditAppender.configurazioneAuditing.isAuditEngineEnabled()){
			throw new AuditDisabilitatoException(AUDIT_DISABILITATO);
		}
		
		try{
			
				
			Operation operation = new Operation();
			operation.setTipologia(tipoOperazione);
			operation.setUtente(user);
			operation.setStato(Stato.REQUESTING);
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
			String objectDetails = filtraOperazione(operation,object,listFilter, registrazioneBinari, byok);
			
			if(objectDetails!=null){
				operation.setObjectDetails(objectDetails);
				
				// Aggiunto binaries filtrati
				for(int i=0; i<listFilter.sizeFilteredObjects(); i++){
					FilteredObject filteredObject = listFilter.getFilteredObject(i);
					
					Binary binary = new Binary();
					binary.setBinaryId(filteredObject.getId());
					binary.setChecksum(filteredObject.getChecksum());
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
				throw new AuditException(APPENDER_NON_FORNITI);
			}
				
			
		}catch(AuditDisabilitatoException e){
			throw e;
		}catch(Exception e){
			throw new AuditException("registraOperazioneInFaseDiElaborazione error: "+e.getMessage(),e);
		}
		
	}
	
	
	
	public void registraOperazioneAccesso(Tipologia tipoOperazione,String user,String interfaceMsg, boolean registrazioneBinari,
			DriverBYOKUtilities byok) throws AuditException,AuditDisabilitatoException{
		
		if(!AuditAppender.configurazioneAuditing.isAuditEngineEnabled()){
			throw new AuditDisabilitatoException(AUDIT_DISABILITATO);
		}
		
		try{
			
				
			Operation operation = new Operation();
			operation.setTipologia(tipoOperazione);
			operation.setUtente(user);
			operation.setStato(Stato.COMPLETED);
			operation.setTimeRequest(DateManager.getDate());
			operation.setTimeExecute(DateManager.getDate());
			operation.setInterfaceMsg(interfaceMsg);
					
			
			// Filtro operazioni
			filtraOperazione(operation, registrazioneBinari, byok);
			
			
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
				throw new AuditException(APPENDER_NON_FORNITI);
			}
				
			
		}catch(AuditDisabilitatoException e){
			throw e;
		}catch(Exception e){
			throw new AuditException("registraOperazioneInFaseDiElaborazione error: "+e.getMessage(),e);
		}
		
	}
	
	
	
	public void registraOperazioneCompletataConSuccesso(IDOperazione idOperazione,String interfaceMsg) throws AuditException,AuditDisabilitatoException{
		
		if(!AuditAppender.configurazioneAuditing.isAuditEngineEnabled()){
			throw new AuditDisabilitatoException(AUDIT_DISABILITATO);
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
				throw new AuditException(APPENDER_NON_FORNITI);
			}
				
			
		}catch(Exception e){
			throw new AuditException("registraOperazioneCompletataConSuccesso error: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	public void registraOperazioneTerminataConErrore(IDOperazione idOperazione,String motivoErrore,String interfaceMsg) throws AuditException,AuditDisabilitatoException{
		
		if(!AuditAppender.configurazioneAuditing.isAuditEngineEnabled()){
			throw new AuditDisabilitatoException(AUDIT_DISABILITATO);
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
				throw new AuditException(APPENDER_NON_FORNITI);
			}
				
			
		}catch(Exception e){
			throw new AuditException("registraOperazioneCompletataConSuccesso error: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	// -------------- UTILITY -------------------
	
	private String serializeJsonObject(Object o,org.openspcoop2.utils.serialization.Filter listFilter, boolean registrazioneBinari) throws AuditException{
		try{
			if(!registrazioneBinari) {
				listFilter.addFilterByValue(byte[].class);
			}
			SerializationConfig config = new SerializationConfig();
			config.setFilter(listFilter);
			config.setIdBuilder(AuditAppender.idBuilder);
			config.setPrettyPrint(true);
			// Deprecato
/**			org.openspcoop2.utils.serialization.JSonSerializer serializer = 
				new org.openspcoop2.utils.serialization.JSonSerializer(config);*/
			org.openspcoop2.utils.serialization.JsonJacksonSerializer serializer = 
				new org.openspcoop2.utils.serialization.JsonJacksonSerializer(config);
			return  serializer.getObject(o);		
		}catch(Exception e){
			throw new AuditException("serializeJsonObject error: "+e.getMessage(),e);
		}
	}
	
	private String serializeXMLObject(Object o,org.openspcoop2.utils.serialization.Filter listFilter, boolean registrazioneBinari) throws AuditException{
		try{
			if(!registrazioneBinari) {
				listFilter.addFilterByValue(byte[].class);
			}
			SerializationConfig config = new SerializationConfig();
			config.setFilter(listFilter);
			config.setIdBuilder(AuditAppender.idBuilder);
			config.setPrettyPrint(true);
			org.openspcoop2.utils.serialization.XMLSerializer serializer = 
				new org.openspcoop2.utils.serialization.XMLSerializer(config);
			return  serializer.getObject(o);		
		}catch(Exception e){
			throw new AuditException("serializeXMLObject error: "+e.getMessage(),e);
		}
	}
	
	private void filtraOperazione(Operation operation, boolean registrazioneBinari,
			DriverBYOKUtilities byok) throws AuditException,AuditDisabilitatoException{
		this.filtraOperazione(operation, null, null, registrazioneBinari,
				byok);
	}
	private String filtraOperazione(Operation operation,Object object,org.openspcoop2.utils.serialization.Filter listFilter, boolean registrazioneBinari,
			DriverBYOKUtilities byok) throws AuditException,AuditDisabilitatoException{
		
		try{
		
			ArrayList<Filtro> filtri = AuditAppender.configurazioneAuditing.getFiltri();
			boolean auditEnabledDefault = AuditAppender.configurazioneAuditing.isAuditEnabled();
			boolean dumpEnabledDefault = AuditAppender.configurazioneAuditing.isDumpEnabled();
			String objectDetails = null;
			
			for(int i=0; i<filtri.size(); i++){
				
				Filtro filtro = filtri.get(i);
				
				/**System.out.println("ANALIZZO FILTRO["+i+"]: "+filtro.toString());*/
				
				if(filtro.getUsername()==null &&
						filtro.getTipoOperazione()==null &&
						filtro.getTipoOggettoInModifica()==null &&
						filtro.getStatoOperazione()==null &&
						filtro.getDump()==null){
					throw new AuditException("Filtro("+i+1+") non valido: nessun meccanismo di filtro definito");
				}
				
				if(filtro.getUsername()!=null &&
					// Se e' definito un username nel filtro controllo che corrisponda
					!filtro.getUsername().equals(operation.getUtente())){
					continue;
				}
				
				if(filtro.getTipoOperazione()!=null &&
					// Se e' definito un tipo di operazione nel filtro controllo che corrisponda
					!filtro.getTipoOperazione().equals(operation.getTipologia())){
					continue;
				}
				
				if(filtro.getTipoOggettoInModifica()!=null &&
					// Se e' definito un TipoOggettoInModifica nel filtro controllo che corrisponda
					!filtro.getTipoOggettoInModifica().equals(operation.getTipoOggetto())){
					continue;
				}
				
				if(filtro.getStatoOperazione()!=null &&
					// Se e' definito uno stato dell'operazione nel filtro controllo che corrisponda
					!filtro.getStatoOperazione().equals(operation.getStato())){
					continue;
				}
				
				if(filtro.getDump()!=null){
					
					if(object==null){
						continue; // Questo filtro per essere matchato deve essere controllato con un oggetto
					}
					
					if(objectDetails==null){
						// Serializzo l'oggetto
						objectDetails = this.serialize(object, listFilter, registrazioneBinari, byok);
					}
					
					// Se e' definito un filtro sul contenuto dell'operazione nel filtro controllo che corrisponda
					if(filtro.isDumpExprRegular()){
						if(RegularExpressionEngine.getStringMatchPattern(objectDetails, filtro.getDump())==null){
							continue;
						}
					}else{
						if(!objectDetails.contains(filtro.getDump())){
							continue;
						}
					}
				}
								
				// Se arriviamo a questo punto significa che l'operazione soddisfa il filtro.
				if(!filtro.isAuditEnabled()){
					throw new AuditDisabilitatoException("Audit disabilitato, criterio adottato in base al filtro numero "+(i+1));
				}
				if(filtro.isDumpEnabled()){
					if(object!=null &&
						// Se non avevo gia' serializzato prima per la ricerca tramite contenuto
						objectDetails==null){
						// Serializzo l'oggetto
						objectDetails = this.serialize(object, listFilter, registrazioneBinari, byok);
					}
				}else{
					if(objectDetails!=null){
						objectDetails=null; // era stato utilizzato per effettuare filtro sui contenuti ma non deve essere dumpato
					}
				}
								
				return objectDetails; // regola matcha
			}
			
			// Se nessun filtro sopra viene matchato, si applica il criterio di default
			if(!auditEnabledDefault){
				throw new AuditDisabilitatoException("Audit disabilitato nella configurazione generale (Non vi sono filtri che creano eccezioni)");
			}
			if(dumpEnabledDefault){
				if(object!=null &&
					// Se non avevo gia' serializzato prima per la ricerca tramite contenuto
					objectDetails==null){
					// Serializzo l'oggetto
					objectDetails = this.serialize(object, listFilter, registrazioneBinari, byok);
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
	
	private Object byok(DriverBYOKUtilities byok, Object object) throws UtilsException {
		if (object instanceof org.openspcoop2.core.config.Soggetto) {
			org.openspcoop2.core.config.Soggetto sCloned = (org.openspcoop2.core.config.Soggetto) ((org.openspcoop2.core.config.Soggetto) object).clone();
			if(sCloned.getConnettore()!=null && !sCloned.getConnettore().isEmpty()) {
				for (org.openspcoop2.core.config.Connettore connettore : sCloned.getConnettore()) {
					byokConnettore(byok, connettore);		
				}
			}
			return sCloned;
		}
		else if (object instanceof org.openspcoop2.core.registry.Soggetto) {
			org.openspcoop2.core.registry.Soggetto sCloned = (org.openspcoop2.core.registry.Soggetto) ((org.openspcoop2.core.registry.Soggetto) object).clone();
			if(sCloned.getConnettore()!=null) {
				byokConnettore(byok, sCloned.getConnettore());
			}
			return sCloned;
		}
		else if (object instanceof ServizioApplicativo) {
			ServizioApplicativo sCloned = (ServizioApplicativo) ((ServizioApplicativo) object).clone();
			return byok(byok, sCloned);
		}
		else if (object instanceof PortaDelegata) {
			PortaDelegata pdCloned = (PortaDelegata) ((PortaDelegata) object).clone();
			return byok(byok, pdCloned);
		}
		else if (object instanceof PortaApplicativa) {
			PortaApplicativa paCloned = (PortaApplicativa) ((PortaApplicativa) object).clone();
			return byok(byok, paCloned);
		}
		else if(object instanceof GenericProperties) {
			GenericProperties gpCloned = (GenericProperties) ((GenericProperties) object).clone();
			return byok(byok, gpCloned);
		}
		else if(object instanceof AccordoServizioParteSpecifica) {
			AccordoServizioParteSpecifica aspsCloned = (AccordoServizioParteSpecifica) ((AccordoServizioParteSpecifica) object).clone();
			return byok(byok, aspsCloned);
		}
		
		return object;
	}
	private Object byok(DriverBYOKUtilities byok, ServizioApplicativo sCloned) throws UtilsException {
		if(sCloned.getInvocazioneServizio()!=null && sCloned.getInvocazioneServizio().getConnettore()!=null) {
			byokConnettore(byok, sCloned.getInvocazioneServizio().getConnettore());
		}
		if(sCloned.getInvocazioneServizio()!=null && sCloned.getInvocazioneServizio().getAutenticazione()!=null &&
				org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione.BASIC.equals(sCloned.getInvocazioneServizio().getAutenticazione()) &&
				sCloned.getInvocazioneServizio().getCredenziali()!=null) {
			byokCredenzialiConnettore(byok, sCloned.getInvocazioneServizio().getCredenziali());
		}
		if(sCloned.getRispostaAsincrona()!=null && sCloned.getRispostaAsincrona().getConnettore()!=null) {
			byokConnettore(byok, sCloned.getRispostaAsincrona().getConnettore());
		}
		if(sCloned.getRispostaAsincrona()!=null && sCloned.getRispostaAsincrona().getAutenticazione()!=null &&
				org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione.BASIC.equals(sCloned.getRispostaAsincrona().getAutenticazione()) &&
				sCloned.getRispostaAsincrona().getCredenziali()!=null) {
			byokCredenzialiConnettore(byok, sCloned.getRispostaAsincrona().getCredenziali());
		}
		if(sCloned.sizeProtocolPropertyList()>0) {
			for (org.openspcoop2.core.config.ProtocolProperty p : sCloned.getProtocolProperty()) {
				if(isConfidentialProtocolProperties(p.getName())) {
					p.setValue(byok.wrap(p.getValue()));
				}
			}
		}
		return sCloned;
	}
	private void byokCredenzialiConnettore(DriverBYOKUtilities byok, org.openspcoop2.core.config.InvocazioneCredenziali credenziale) throws UtilsException {
		if(credenziale.getPassword()!=null) {
			credenziale.setPassword(byok.wrap(credenziale.getPassword()));
		}
	}
	private void byokConnettore(DriverBYOKUtilities byok, org.openspcoop2.core.config.Connettore connettore) throws UtilsException {
		if(connettore.sizePropertyList()>0) {
			for (org.openspcoop2.core.config.Property p : connettore.getPropertyList()) {
				if(isConfidentialConnettore(p.getNome())) {
					p.setValore(byok.wrap(p.getValore()));
				}
			}
		}	
	}
	private void byokConnettore(DriverBYOKUtilities byok, org.openspcoop2.core.registry.Connettore connettore) throws UtilsException {
		if(connettore.sizePropertyList()>0) {
			for (org.openspcoop2.core.registry.Property p : connettore.getPropertyList()) {
				if(isConfidentialConnettore(p.getNome())) {
					p.setValore(byok.wrap(p.getValore()));
				}
			}
		}	
	}
	private boolean isConfidentialConnettore(String nomeProprieta) {
		for (String nomeProprietaConfidential : CostantiConnettori.getConfidentials()) {
			if(nomeProprieta.equals(nomeProprietaConfidential)) {
				return true;
			}
		}
		return false;
	}
	private Object byok(DriverBYOKUtilities byok, PortaDelegata pdCloned) throws UtilsException {
		if(pdCloned.getMessageSecurity()!=null) {
			if(pdCloned.getMessageSecurity().getRequestFlow()!=null) {
				byok(byok, pdCloned.getMessageSecurity().getRequestFlow());
			}
			if(pdCloned.getMessageSecurity().getResponseFlow()!=null) {
				byok(byok, pdCloned.getMessageSecurity().getResponseFlow());
			}
		}
		return pdCloned;
	}
	private Object byok(DriverBYOKUtilities byok, PortaApplicativa paCloned) throws UtilsException {
		if(paCloned.getMessageSecurity()!=null) {
			if(paCloned.getMessageSecurity().getRequestFlow()!=null) {
				byok(byok, paCloned.getMessageSecurity().getRequestFlow());
			}
			if(paCloned.getMessageSecurity().getResponseFlow()!=null) {
				byok(byok, paCloned.getMessageSecurity().getResponseFlow());
			}
		}
		return paCloned;
	}
	private void byok(DriverBYOKUtilities byok, MessageSecurityFlow flow) throws UtilsException {
		if(flow.sizeParameterList()>0) {
			for (MessageSecurityFlowParameter p : flow.getParameterList()) {
				if(isConfidentialSecurity(p.getNome())) {
					p.setValore(byok.wrap(p.getValore()));
				}
			}
		}
	}
	private boolean isConfidentialSecurity(String nomeProprieta) {
		List<String> messageSecurityIds = CostantiProprieta.getMessageSecurityIds();
		if(messageSecurityIds!=null && !messageSecurityIds.isEmpty()) {
			for (String id : messageSecurityIds) {
				List<String> l =  CostantiProprieta.getMessageSecurityProperties(id);
				for (String nomeProprietaCheck : l) {
					if(nomeProprietaCheck.equals(nomeProprieta)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private Object byok(DriverBYOKUtilities byok, GenericProperties gp) throws UtilsException {
		if(gp.sizePropertyList()>0) {
			for (Property p : gp.getPropertyList()) {
				if(isConfidentialToken(gp.getTipo(), p.getNome())) {
					p.setValore(byok.wrap(p.getValore()));
				}
			}
		}
		return gp;
	}
	private boolean isConfidentialToken(String tipo, String nomeProprieta) {
		List<String> l = null;
		if(CostantiProprieta.TOKEN_VALIDATION_ID.equals(tipo)) {
			l = CostantiProprieta.getTokenValidationProperties();
		}
		else if(CostantiProprieta.TOKEN_NEGOZIAZIONE_ID.equals(tipo)) {
			l = CostantiProprieta.getTokenRetrieveProperties();
		}
		else if(CostantiProprieta.ATTRIBUTE_AUTHORITY_ID.equals(tipo)) {
			l = CostantiProprieta.getAttributeAuthorityProperties();
		}
		if(l!=null) {
			for (String nomeProprietaCheck : l) {
				if(nomeProprietaCheck.equals(nomeProprieta)) {
					return true;
				}
			}
		}
		return false;
	}
	private Object byok(DriverBYOKUtilities byok, AccordoServizioParteSpecifica asps) throws UtilsException {
		if(asps.sizeProtocolPropertyList()>0) {
			for (ProtocolProperty p : asps.getProtocolProperty()) {
				if(isConfidentialProtocolProperties(p.getName())) {
					p.setValue(byok.wrap(p.getValue()));
				}
			}
		}
		if(asps.sizeFruitoreList()>0) {
			for (Fruitore fruitore : asps.getFruitoreList()) {
				byok(byok, fruitore);
			}
		}
		if(asps.getConfigurazioneServizio()!=null && asps.getConfigurazioneServizio().getConnettore()!=null) {
			byokConnettore(byok, asps.getConfigurazioneServizio().getConnettore());
		}
		return asps;
	}
	private Object byok(DriverBYOKUtilities byok, Fruitore fruitore) throws UtilsException {
		if(fruitore.sizeProtocolPropertyList()>0) {
			for (ProtocolProperty p : fruitore.getProtocolProperty()) {
				if(isConfidentialProtocolProperties(p.getName())) {
					p.setValue(byok.wrap(p.getValue()));
				}
			}
		}
		if(fruitore.getConnettore()!=null) {
			byokConnettore(byok, fruitore.getConnettore());
		}
		return fruitore;
	}
	private boolean isConfidentialProtocolProperties(String nomeProprieta) {
		return CostantiDB.MODIPA_KEYSTORE_PASSWORD.equals(nomeProprieta) ||
				CostantiDB.MODIPA_KEY_PASSWORD.equals(nomeProprieta) ||
				CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD.equals(nomeProprieta) ||
				CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD.equals(nomeProprieta);
	}
	
	
	private String serialize(Object object,org.openspcoop2.utils.serialization.Filter listFilter, boolean registrazioneBinari,
			DriverBYOKUtilities byok)throws AuditException{
		if(byok!=null) {
			try {
				object = byok(byok, object);
			}catch(Exception e) {
				throw new AuditException(e.getMessage(),e);
			}
		}
		if(Costanti.DUMP_JSON_FORMAT.equals(AuditAppender.configurazioneAuditing.getDumpFormat())){
			return this.serializeJsonObject(object, listFilter, registrazioneBinari);
		}
		else if(Costanti.DUMP_XML_FORMAT.equals(AuditAppender.configurazioneAuditing.getDumpFormat())){
			return this.serializeXMLObject(object, listFilter, registrazioneBinari);
		}else {
			throw new AuditException("Tipo di formattazione non conosciuta: "+AuditAppender.configurazioneAuditing.getDumpFormat());
		}
	}
}
