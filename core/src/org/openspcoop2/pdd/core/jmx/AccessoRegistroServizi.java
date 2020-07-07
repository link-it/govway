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


package org.openspcoop2.pdd.core.jmx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ReflectionException;

import org.slf4j.Logger;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.connettori.ConnettoreCheck;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;


/**
 * Implementazione JMX per la gestione dell'accesso al registro dei servizi 
 *   
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccessoRegistroServizi extends NotificationBroadcasterSupport implements DynamicMBean {

	/** Nomi proprieta' */
	public final static String REGISTRI_SERVIZI = "registriServizi";

	/** Nomi metodi' */
	public final static String CHECK_CONNETTORE_BY_ID = "checkConnettoreById";
	public final static String CHECK_CONNETTORE_BY_NOME = "checkConnettoreByNome";
	public final static String GET_CERTIFICATI_CONNETTORE_BY_ID = "getCertificatiConnettoreById";
	public final static String GET_CERTIFICATI_CONNETTORE_BY_NOME = "getCertificatiConnettoreByNome";
	
	/** Attributi */
	private boolean cacheAbilitata = false;
	private String[] registriServizi = null;
	
	/** getAttribute */
	@Override
	public Object getAttribute(String attributeName) throws AttributeNotFoundException,MBeanException,ReflectionException{
		
		if( (attributeName==null) || (attributeName.equals("")) )
			throw new IllegalArgumentException("Il nome dell'attributo e' nullo o vuoto");
		
		if(attributeName.equals(JMXUtils.CACHE_ATTRIBUTE_ABILITATA))
			return this.cacheAbilitata;
		
		if(attributeName.equals(AccessoRegistroServizi.REGISTRI_SERVIZI))
			return this.registriServizi;
		
		throw new AttributeNotFoundException("Attributo "+attributeName+" non trovato");
	}
	
	/** getAttributes */
	@Override
	public AttributeList getAttributes(String [] attributesNames){
		
		if(attributesNames==null)
			throw new IllegalArgumentException("Array nullo");
		
		AttributeList list = new AttributeList();
		for (int i=0; i<attributesNames.length; i++){
			try{
				list.add(new Attribute(attributesNames[i],getAttribute(attributesNames[i])));
			}catch(JMException ex){}
		}
		return list;
	}
	
	/** setAttribute */
	@Override
	public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException{
		
		if( attribute==null )
			throw new IllegalArgumentException("Il nome dell'attributo e' nullo");
		
		try{
			
			if(attribute.getName().equals(JMXUtils.CACHE_ATTRIBUTE_ABILITATA)){
				boolean v = (Boolean) attribute.getValue();
				if(v){
					// la cache DEVE essere abilitata
					if(!this.cacheAbilitata){
						this.abilitaCache();
					}
				}
				else{
					// la cache DEVE essere disabilitata
					if(this.cacheAbilitata){
						this.disabilitaCache();
					}
				}
			}
						
			else if(attribute.getName().equals(AccessoRegistroServizi.REGISTRI_SERVIZI))
				this.registriServizi = (String[]) attribute.getValue();
			
			else
				throw new AttributeNotFoundException("Attributo "+attribute.getName()+" non trovato");
			
		}catch(ClassCastException ce){
			throw new InvalidAttributeValueException("il tipo "+attribute.getValue().getClass()+" dell'attributo "+attribute.getName()+" non e' valido");
		}catch(JMException j){
			throw new MBeanException(j);
		}
		
	}
	
	/** setAttributes */
	@Override
	public AttributeList setAttributes(AttributeList list){
		
		if(list==null)
			throw new IllegalArgumentException("Lista degli attributi e' nulla");
		
		AttributeList ret = new AttributeList();
		Iterator<?> it = ret.iterator();
		
		while(it.hasNext()){
			try{
				Attribute attribute = (Attribute) it.next();
				setAttribute(attribute);
				ret.add(attribute);
			}catch(JMException ex){}
		}
		
		return ret;
		
	}
	
	/** invoke */
	@Override
	public Object invoke(String actionName, Object[]params, String[]signature) throws MBeanException,ReflectionException{
		
		if( (actionName==null) || (actionName.equals("")) )
			throw new IllegalArgumentException("Nessuna operazione definita");
		
		if(actionName.equals(JMXUtils.CACHE_METHOD_NAME_RESET)){
			return this.resetCache();
		}
		
		if(actionName.equals(JMXUtils.CACHE_METHOD_NAME_PREFILL)){
			return this.prefillCache();
		}
		
		if(actionName.equals(JMXUtils.CACHE_METHOD_NAME_PRINT_STATS)){
			return this.printStatCache();
		}
				
		if(actionName.equals(JMXUtils.CACHE_METHOD_NAME_DISABILITA)){
			return this.disabilitaCacheConEsito();
		}
				
		if(actionName.equals(JMXUtils.CACHE_METHOD_NAME_ABILITA)){
			if(params.length != 4)
				throw new MBeanException(new Exception("[AbilitaCache] Lunghezza parametri non corretta: "+params.length));
			
			Long param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (Long)params[0];
				if(param1<0){
					param1 = null;
				}
			}
			
			Boolean param2 = null;
			if(params[1]!=null && !"".equals(params[1])){
				param2 = (Boolean)params[1];
			}
			
			Long param3 = null;
			if(params[2]!=null && !"".equals(params[2])){
				param3 = (Long)params[2];
				if(param3<0){
					param3 = null;
				}
			}
			
			Long param4 = null;
			if(params[3]!=null && !"".equals(params[3])){
				param4 = (Long)params[3];
				if(param4<0){
					param4 = null;
				}
			}
					
			return this.abilitaCache(param1, param2, param3, param4 );
		}
		
		if(actionName.equals(JMXUtils.CACHE_METHOD_NAME_LIST_KEYS)){
			return this.listKeysCache();
		}
		
		if(actionName.equals(JMXUtils.CACHE_METHOD_NAME_GET_OBJECT)){
			
			if(params.length != 1)
				throw new MBeanException(new Exception("["+JMXUtils.CACHE_METHOD_NAME_GET_OBJECT+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			return this.getObjectCache(param1);
		}
		
		if(actionName.equals(JMXUtils.CACHE_METHOD_NAME_REMOVE_OBJECT)){
			
			if(params.length != 1)
				throw new MBeanException(new Exception("["+JMXUtils.CACHE_METHOD_NAME_REMOVE_OBJECT+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			
			return this.removeObjectCache(param1);
		}
		
		if(actionName.equals(CHECK_CONNETTORE_BY_ID)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+CHECK_CONNETTORE_BY_ID+"] Lunghezza parametri non corretta: "+params.length));
			
			Long param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				if(params[0] instanceof Long) {
					param1 = (Long)params[0];
				}
				else {
					param1 = Long.valueOf(params[0].toString());
				}
				
				if(param1<0){
					param1 = null;
				}
			}
			return this.checkConnettoreById(param1);
		}
		
		if(actionName.equals(CHECK_CONNETTORE_BY_NOME)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+CHECK_CONNETTORE_BY_NOME+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			return this.checkConnettoreByNome(param1);
		}
		
		if(actionName.equals(GET_CERTIFICATI_CONNETTORE_BY_ID)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+GET_CERTIFICATI_CONNETTORE_BY_ID+"] Lunghezza parametri non corretta: "+params.length));
			
			Long param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				if(params[0] instanceof Long) {
					param1 = (Long)params[0];
				}
				else {
					param1 = Long.valueOf(params[0].toString());
				}
				
				if(param1<0){
					param1 = null;
				}
			}
			return this.getCertificatiConnettoreById(param1);
		}
		
		if(actionName.equals(GET_CERTIFICATI_CONNETTORE_BY_NOME)){
			if(params.length != 1)
				throw new MBeanException(new Exception("["+GET_CERTIFICATI_CONNETTORE_BY_NOME+"] Lunghezza parametri non corretta: "+params.length));
			
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			return this.getCertificatiConnettoreByNome(param1);
		}
		
		throw new UnsupportedOperationException("Operazione "+actionName+" sconosciuta");
	}
	
	/* MBean info */
	@Override
	public MBeanInfo getMBeanInfo(){
				
		// Descrizione della classe nel MBean
		String className = this.getClass().getName();
		String description = "Risorsa per la configurazione dell'accesso al registri dei servizi ("+OpenSPCoop2Properties.getInstance().getVersione()+")";

		// MetaData per l'attributo abilitaCache
		MBeanAttributeInfo cacheAbilitataVAR = JMXUtils.MBEAN_ATTRIBUTE_INFO_CACHE_ABILITATA;
		
		// MetaData per l'attributo registriServizi
		MBeanAttributeInfo registriServiziVAR 
			= new MBeanAttributeInfo(AccessoRegistroServizi.REGISTRI_SERVIZI,String[].class.getName(),
						"Elenco dei registri dei servizi utilizzati a RunTime da GovWay",
							JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'operazione resetCache
		MBeanOperationInfo resetCacheOP = JMXUtils.MBEAN_OPERATION_RESET_CACHE;
		
		// MetaData per l'operazione prefillCache
		MBeanOperationInfo prefillCacheOP = JMXUtils.MBEAN_OPERATION_PREFILL_CACHE;
		
		// MetaData per l'operazione printStatCache
		MBeanOperationInfo printStatCacheOP = JMXUtils.MBEAN_OPERATION_PRINT_STATS_CACHE;
		
		// MetaData per l'operazione disabilitaCache
		MBeanOperationInfo disabilitaCacheOP = JMXUtils.MBEAN_OPERATION_DISABILITA_CACHE;
		
		// MetaData per l'operazione abilitaCache con parametri
		MBeanOperationInfo abilitaCacheParametriOP = JMXUtils.MBEAN_OPERATION_ABILITA_CACHE_CON_PARAMETRI;
		
		// MetaData per l'operazione listKeysCache
		MBeanOperationInfo listKeysCacheOP = JMXUtils.MBEAN_OPERATION_LIST_KEYS_CACHE; 

		// MetaData per l'operazione getObjectCache
		MBeanOperationInfo getObjectCacheOP = JMXUtils.MBEAN_OPERATION_GET_OBJECT_CACHE;
		
		// MetaData per l'operazione removeObjectCache
		MBeanOperationInfo removeObjectCacheOP = JMXUtils.MBEAN_OPERATION_REMOVE_OBJECT_CACHE;
		
		// MetaData per l'operazione checkConettoreById
		MBeanOperationInfo checkConnettoreById 
		= new MBeanOperationInfo(CHECK_CONNETTORE_BY_ID,"Verifica la raggiungibilità del connettore con id fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("idConnettore",long.class.getName(),"Identificativo del connettore"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione checkConettoreByNome
		MBeanOperationInfo checkConnettoreByNome 
		= new MBeanOperationInfo(CHECK_CONNETTORE_BY_NOME,"Verifica la raggiungibilità del connettore con nome fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomeConnettore",String.class.getName(),"Nome del connettore"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione getCertificatiConnettoreById
		MBeanOperationInfo getCertificatiConnettoreById 
		= new MBeanOperationInfo(GET_CERTIFICATI_CONNETTORE_BY_ID,"Recupera i certificati server del connettore con id fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("idConnettore",long.class.getName(),"Identificativo del connettore"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione getCertificatiConnettoreByNome
		MBeanOperationInfo getCertificatiConnettoreByNome 
		= new MBeanOperationInfo(GET_CERTIFICATI_CONNETTORE_BY_NOME,"Recupera i certificati server del connettore con nome fornito come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("nomeConnettore",String.class.getName(),"Nome del connettore"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
		
		// Mbean costruttore
		MBeanConstructorInfo defaultConstructor = new MBeanConstructorInfo("Default Constructor","Crea e inizializza una nuova istanza del MBean",null);

		// Lista attributi
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[]{cacheAbilitataVAR,registriServiziVAR};
		
		// Lista Costruttori
		MBeanConstructorInfo[] constructors = new MBeanConstructorInfo[]{defaultConstructor};
		
		// Lista operazioni
		List<MBeanOperationInfo> listOperation = new ArrayList<>();
		listOperation.add(resetCacheOP);
		if(this.openspcoopProperties.isConfigurazioneCache_RegistryPrefill()){
			listOperation.add(prefillCacheOP);
		}
		listOperation.add(printStatCacheOP);
		listOperation.add(disabilitaCacheOP);
		listOperation.add(abilitaCacheParametriOP);
		listOperation.add(listKeysCacheOP);
		listOperation.add(getObjectCacheOP);
		listOperation.add(removeObjectCacheOP);
		listOperation.add(checkConnettoreById);
		listOperation.add(checkConnettoreByNome);
		listOperation.add(getCertificatiConnettoreById);
		listOperation.add(getCertificatiConnettoreByNome);
		MBeanOperationInfo[] operations = listOperation.toArray(new MBeanOperationInfo[1]);
		
		return new MBeanInfo(className,description,attributes,constructors,operations,null);
	}
	
	/* Variabili per la gestione JMX */
	private Logger log;
	org.openspcoop2.pdd.config.ConfigurazionePdDManager configReader = null;
	org.openspcoop2.pdd.config.OpenSPCoop2Properties openspcoopProperties = null;
	
	/* Costruttore */
	public AccessoRegistroServizi(){
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		this.configReader = org.openspcoop2.pdd.config.ConfigurazionePdDManager.getInstance();
		this.openspcoopProperties = org.openspcoop2.pdd.config.OpenSPCoop2Properties.getInstance();
		
		// Registro
		this.cacheAbilitata =  this.configReader.getAccessoRegistroServizi().getCache() != null;
		
		if(this.configReader.getAccessoRegistroServizi().sizeRegistroList()>0){
			this.registriServizi = new String[this.configReader.getAccessoRegistroServizi().sizeRegistroList()];
			for(int i=0; i<this.configReader.getAccessoRegistroServizi().sizeRegistroList(); i++){
				AccessoRegistroRegistro registro = this.configReader.getAccessoRegistroServizi().getRegistro(i);
				String autenticazione = "";
				if(registro.getUser()!=null || registro.getPassword()!=null)
					autenticazione=" (user:"+registro.getUser()+" password:"+registro.getPassword()+")";
					
				this.registriServizi[i] = "Registro "+registro.getNome()+" di tipo "+registro.getTipo() +","+autenticazione+" location:"+registro.getLocation();
			}
		}
	}
	
	public boolean isCacheAbilitata() {
		return this.cacheAbilitata;
	}
	
	/* Metodi di management JMX */
	public String resetCache(){
		try{
			if(this.cacheAbilitata==false)
				throw new Exception("Cache non abilitata");
			org.openspcoop2.protocol.registry.RegistroServiziReader.resetCache();
			return JMXUtils.MSG_RESET_CACHE_EFFETTUATO_SUCCESSO;
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String prefillCache(){
		try{
			if(this.cacheAbilitata==false)
				throw new Exception("Cache non abilitata");
			org.openspcoop2.protocol.registry.RegistroServiziReader.prefillCache(this.openspcoopProperties.getCryptConfigAutenticazioneSoggetti());
			return JMXUtils.MSG_PREFILL_CACHE_EFFETTUATO_SUCCESSO;
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String printStatCache(){
		try{
			if(this.cacheAbilitata==false)
				throw new Exception("Cache non abilitata");
			return org.openspcoop2.protocol.registry.RegistroServiziReader.printStatsCache("\n");
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public void abilitaCache() throws JMException{
		try{
			org.openspcoop2.protocol.registry.RegistroServiziReader.abilitaCache();
			this.cacheAbilitata = true;
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
		}
	}
	
	public String abilitaCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond){
		try{
			org.openspcoop2.protocol.registry.RegistroServiziReader.abilitaCache(dimensioneCache,algoritmoCacheLRU,itemIdleTime,itemLifeSecond,
					this.openspcoopProperties.getCryptConfigAutenticazioneSoggetti());
			this.cacheAbilitata = true;
			return JMXUtils.MSG_ABILITAZIONE_CACHE_EFFETTUATA;
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
		
	
	public void disabilitaCache() throws JMException{
		try{
			org.openspcoop2.protocol.registry.RegistroServiziReader.disabilitaCache();
			this.cacheAbilitata = false;
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			throw new JMException(e.getMessage());
		}
	}
	public String disabilitaCacheConEsito() {
		try{
			disabilitaCache();
			return JMXUtils.MSG_DISABILITAZIONE_CACHE_EFFETTUATA;
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String listKeysCache(){
		try{
			if(this.cacheAbilitata==false)
				throw new Exception("Cache non abilitata");
			return org.openspcoop2.protocol.registry.RegistroServiziReader.listKeysCache("\n");
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getObjectCache(String key){
		try{
			if(this.cacheAbilitata==false)
				throw new Exception("Cache non abilitata");
			return org.openspcoop2.protocol.registry.RegistroServiziReader.getObjectCache(key);
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String removeObjectCache(String key){
		try{
			if(this.cacheAbilitata==false)
				throw new Exception("Cache non abilitata");
			org.openspcoop2.protocol.registry.RegistroServiziReader.removeObjectCache(key);
			return JMXUtils.MSG_RIMOZIONE_CACHE_EFFETTUATA;
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String checkConnettoreById(long idConnettore) {
		try{
			ConnettoreCheck.check(idConnettore, true);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String checkConnettoreByNome(String nomeConnettore) {
		try{
			ConnettoreCheck.check(nomeConnettore, true);
			return JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO;
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getCertificatiConnettoreById(long idConnettore) {
		try{
			return ConnettoreCheck.getCertificati(idConnettore, true);
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getCertificatiConnettoreByNome(String nomeConnettore) {
		try{
			return ConnettoreCheck.getCertificati(nomeConnettore, true);
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
}
