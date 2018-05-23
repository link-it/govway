/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.util.Iterator;

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
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.jmx.JMXUtils;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.beans.ID;
import org.openspcoop2.core.controllo_traffico.beans.JMXConstants;
import org.openspcoop2.core.controllo_traffico.driver.IGestorePolicyAttive;
import org.openspcoop2.core.controllo_traffico.driver.PolicyNotFoundException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyShutdownException;
import org.openspcoop2.pdd.core.controllo_traffico.GestoreControlloTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.policy.GestoreCacheControlloTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.GestorePolicyAttive;


/**
 * Implementazione JMX per la gestione del Controllo del Traffico
 *   
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ControlloTraffico extends NotificationBroadcasterSupport implements DynamicMBean {
		
	/** Attributi */
	private String statoControlloMaxThreads;
	public long maxThreads = 0;
	public long activeThreads = 0;
	public boolean pddCongestionata = false;

	// cache informazioni statistiche
	public boolean cacheAbilitata = false; 
	
	/** getAttribute */
	@Override
	public Object getAttribute(String attributeName) throws AttributeNotFoundException,MBeanException,ReflectionException{
		return _getAttribute(attributeName, true);
	}
	public Object _getAttribute(String attributeName, boolean refresh) throws AttributeNotFoundException,MBeanException,ReflectionException{
		
		if(refresh){
			this.refreshDati();
		}
		
		if( (attributeName==null) || (attributeName.equals("")) )
			throw new IllegalArgumentException("Il nome dell'attributo e' nullo o vuoto");
				
		if(attributeName.equals(JMXConstants.CC_ATTRIBUTE_MAX_THREADS_STATO))
			return this.statoControlloMaxThreads;
		
		if(attributeName.equals(JMXConstants.CC_ATTRIBUTE_MAX_THREADS_SOGLIA))
			return this.maxThreads;
		
		if(attributeName.equals(JMXConstants.CC_ATTRIBUTE_ACTIVE_THREADS))
			return this.activeThreads;
		
		if(attributeName.equals(JMXConstants.CC_ATTRIBUTE_PDD_CONGESTIONATA))
			return this.pddCongestionata;
				
		if(attributeName.equals(JMXUtils.CACHE_ATTRIBUTE_ABILITATA))
			return this.cacheAbilitata;
		
		throw new AttributeNotFoundException("Attributo "+attributeName+" non trovato");
	}
	
	/** getAttributes */
	@Override
	public AttributeList getAttributes(String [] attributesNames){
		
		try{
			this.refreshDati();
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
		
		if(attributesNames==null)
			throw new IllegalArgumentException("Array nullo");
		
		AttributeList list = new AttributeList();
		for (int i=0; i<attributesNames.length; i++){
			try{
				list.add(new Attribute(attributesNames[i],_getAttribute(attributesNames[i],false)));
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
			
			if(attribute.getName().equals(JMXConstants.CC_ATTRIBUTE_MAX_THREADS_STATO)){
				this.statoControlloMaxThreads = (String) attribute.getValue();
			}
			
			if(attribute.getName().equals(JMXConstants.CC_ATTRIBUTE_MAX_THREADS_SOGLIA)){
				this.maxThreads = (Long) attribute.getValue();
			}
			
			if(attribute.getName().equals(JMXConstants.CC_ATTRIBUTE_ACTIVE_THREADS)){
				this.activeThreads = (Long) attribute.getValue();
			}
						
			if(attribute.getName().equals(JMXConstants.CC_ATTRIBUTE_PDD_CONGESTIONATA)){
				this.pddCongestionata = (Boolean) attribute.getValue();
			}
			
			if(attribute.getName().equals(JMXUtils.CACHE_ATTRIBUTE_ABILITATA)){
				boolean v = (Boolean) attribute.getValue();
				if(v){
					this.abilitaCache();
				}else{
					this.disabilitaCache();
				}
			}
			
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
		
		
		// Policy
		
		if(actionName.equals(JMXConstants.CC_METHOD_NAME_GET_ALL_ID_POLICY)){
			return this.getAllIdPolicy();
		}
		
		if(actionName.equals(JMXConstants.CC_METHOD_NAME_GET_STATO_POLICY)){
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			return this.getStatoPolicy(param1);
		}
		
		if(actionName.equals(JMXConstants.CC_METHOD_NAME_REMOVE_ALL_POLICY)){
			return this.removeAllPolicies();
		}
		
		if(actionName.equals(JMXConstants.CC_METHOD_NAME_RESET_ALL_POLICY_COUNTERS)){
			return this.resetAllPolicies();
		}
		
		if(actionName.equals(JMXConstants.CC_METHOD_NAME_REMOVE_POLICY)){
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			return this.removePolicy(param1);
		}
		
		if(actionName.equals(JMXConstants.CC_METHOD_NAME_RESET_POLICY_COUNTERS)){
			String param1 = null;
			if(params[0]!=null && !"".equals(params[0])){
				param1 = (String)params[0];
			}
			return this.resetPolicy(param1);
		}
		
		
		// Cache Controllo del Traffico
		if(actionName.equals(JMXUtils.CACHE_METHOD_NAME_RESET)){
			return this.resetCache();
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
				
		throw new UnsupportedOperationException("Operazione "+actionName+" sconosciuta");
	}
	
	/* MBean info */
	@Override
	public MBeanInfo getMBeanInfo(){
				
		// Descrizione della classe nel MBean
		String className = this.getClass().getName();
		String description = "Risorsa per la gestione del Controllo del Traffico attivo sulla Porta di Dominio "+OpenSPCoop2Properties.getInstance().getVersione();
		
		// *** Attributi ***
		
		// MetaData per l'attributo maxThreads (stato)
		MBeanAttributeInfo maxThreadsStatoVAR = new MBeanAttributeInfo(JMXConstants.CC_ATTRIBUTE_MAX_THREADS_STATO,String.class.getName(),
						"Stato del controllo sul numero massimo di threads attivi",
						JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo maxThreads (soglia)
		MBeanAttributeInfo maxThreadsSogliaVAR = new MBeanAttributeInfo(JMXConstants.CC_ATTRIBUTE_MAX_THREADS_SOGLIA,long.class.getName(),
						"Numero massimo di threads attivi",
						JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo activeThreads
		MBeanAttributeInfo activeThreadsVAR = new MBeanAttributeInfo(JMXConstants.CC_ATTRIBUTE_ACTIVE_THREADS,long.class.getName(),
						"Numero di threads attivi sulla Porta di Dominio",
						JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		// MetaData per l'attributo pddCongestionata
		MBeanAttributeInfo pddCongestionataVAR = new MBeanAttributeInfo(JMXConstants.CC_ATTRIBUTE_PDD_CONGESTIONATA,boolean.class.getName(),
						"Indicazione se la PdD risulta congestionata dalle richieste",
						JMXUtils.JMX_ATTRIBUTE_READABLE,!JMXUtils.JMX_ATTRIBUTE_WRITABLE,!JMXUtils.JMX_ATTRIBUTE_IS_GETTER);
		
		
		// MetaData per l'attributo abilitaCache
		MBeanAttributeInfo cacheAbilitataVAR = JMXUtils.MBEAN_ATTRIBUTE_INFO_CACHE_ABILITATA;
		
		// *** Operazioni ***
		
		// MetaData per l'operazione  getAllIdPolicy
		MBeanOperationInfo getAllIdPolicyOP = new MBeanOperationInfo(JMXConstants.CC_METHOD_NAME_GET_ALL_ID_POLICY,
				"Ritorna gli identificativi delle policy attivate tramite Rate Limiting",
				null,
				//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
				String.class.getName(),
				MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione  getStatoThreadsAttiviPortaDelegata
		MBeanOperationInfo getStatoPolicyOP = new MBeanOperationInfo(JMXConstants.CC_METHOD_NAME_GET_STATO_POLICY,
				"Ritorna lo stato della policy identificata dal parametro",
				new MBeanParameterInfo[]{new MBeanParameterInfo("id",String.class.getName(),"identificativo della policy")},
				String.class.getName(),
				MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione  removeAllPolicies
		MBeanOperationInfo removeAllPoliciesOP = new MBeanOperationInfo(JMXConstants.CC_METHOD_NAME_REMOVE_ALL_POLICY,
				"Elimina le istanza attive di tutte le policy",
				null,
				//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
				String.class.getName(),
				MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione  removePolicy
		MBeanOperationInfo removePolicyOP = new MBeanOperationInfo(JMXConstants.CC_METHOD_NAME_REMOVE_POLICY,
				"Elimina l'istanza attiva della policy di Rate Limiting identificata dal parametro",
				new MBeanParameterInfo[]{new MBeanParameterInfo("id",String.class.getName(),"identificativo della policy")},
				String.class.getName(),
				MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione  removeAllPolicies
		MBeanOperationInfo resetAllPoliciesOP = new MBeanOperationInfo(JMXConstants.CC_METHOD_NAME_RESET_ALL_POLICY_COUNTERS,
				"Effettua i reset dei contatori di tutte le policy attivate tramite Rate Limiting",
				null,
				//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
				String.class.getName(),
				MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione  removePolicy
		MBeanOperationInfo resetPolicyOP = new MBeanOperationInfo(JMXConstants.CC_METHOD_NAME_RESET_POLICY_COUNTERS,
				"Effettua i reset dei contatori presenti nella policy di Rate Limiting identificata dal parametro",
				new MBeanParameterInfo[]{new MBeanParameterInfo("id",String.class.getName(),"identificativo della policy")},
				String.class.getName(),
				MBeanOperationInfo.ACTION);
		
		// MetaData per l'operazione resetCache
		MBeanOperationInfo resetCacheOP = JMXUtils.MBEAN_OPERATION_RESET_CACHE;
		
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
		
		
		// *** Costruttore ***
		
		// Mbean costruttore
		MBeanConstructorInfo defaultConstructor = new MBeanConstructorInfo("Default Constructor","Crea e inizializza una nuova istanza del MBean",null);

		// Lista attributi
		MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[]{maxThreadsStatoVAR, maxThreadsSogliaVAR, activeThreadsVAR, 
				pddCongestionataVAR, 
				cacheAbilitataVAR};
		
		// Lista Costruttori
		MBeanConstructorInfo[] constructors = new MBeanConstructorInfo[]{defaultConstructor};
		
		// Lista operazioni
		MBeanOperationInfo[] operations = new MBeanOperationInfo[]{
				getAllIdPolicyOP, getStatoPolicyOP, removeAllPoliciesOP, removePolicyOP, resetAllPoliciesOP, resetPolicyOP,
				resetCacheOP,printStatCacheOP,disabilitaCacheOP,abilitaCacheParametriOP,listKeysCacheOP,getObjectCacheOP,removeObjectCacheOP};
		
		return new MBeanInfo(className,description,attributes,constructors,operations,null);
	}
	
	/* Variabili per la gestione JMX */
	private Logger log;
	private GestoreControlloTraffico gestoreControlloCongestione;
	private IGestorePolicyAttive gestorePolicyAttive;
	
	/* Costruttore */
	public ControlloTraffico() throws Exception{

		boolean force = true;
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(force);  // per gli errori

		this.refreshDati();
		
		this.cacheAbilitata = GestoreCacheControlloTraffico.isCacheAbilitata();
			
		
	}
	
	private void refreshDati() throws MBeanException{
		ConfigurazioneGenerale configurazioneGenerale = null;
		try{
		
			ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance();
			Object o = configPdDManager.getSingleExtendedInfoConfigurazione(ID.CONFIGURAZIONE_GENERALE_CONTROLLO_TRAFFICO);
			if(o!=null){
				configurazioneGenerale = (ConfigurazioneGenerale) o;
			}
			
			if(configurazioneGenerale.getControlloTraffico().isControlloMaxThreadsEnabled()) {
				if(configurazioneGenerale.getControlloTraffico().isControlloMaxThreadsWarningOnly()) {
					this.statoControlloMaxThreads = CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.getValue();
				}
				else {
					this.statoControlloMaxThreads = CostantiConfigurazione.STATO_CON_WARNING_ABILITATO.getValue();
				}
			}
			else {
				this.statoControlloMaxThreads = CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO.getValue();
			}
			
			this.maxThreads = configurazioneGenerale.getControlloTraffico().getControlloMaxThreadsSoglia();
		
			this.gestorePolicyAttive = GestorePolicyAttive.getInstance();
			
			this.gestoreControlloCongestione = GestoreControlloTraffico.getInstance();
			this.activeThreads = this.gestoreControlloCongestione.sizeActiveThreads();
			
			this.pddCongestionata = this.gestoreControlloCongestione.isPortaDominioCongestionata(this.maxThreads,configurazioneGenerale.getControlloTraffico().getControlloCongestioneThreshold());
			
		}catch(Exception e){
			try{
				this.log.error("Configurazione non disponibile: "+e.getMessage(),e);
			}catch(Exception eLog){}
			throw new MBeanException(e, "Configurazione non disponibile: "+e.getMessage());
		}
	}
	
	
	public boolean isCacheAbilitata() {
		return this.cacheAbilitata;
	}
	
	/* Metodi di management JMX */

	public String getAllIdPolicy(){
		try{
			if(this.gestorePolicyAttive==null)
				throw new Exception("GestorePolicyAttive non abilitato");
			return this.gestorePolicyAttive.printKeysPolicy("\n");
		}catch(PolicyShutdownException notFound){
			return "GestorePolicyAttive in shutdown; controllare lo stato della Porta di Dominio";
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getStatoPolicy(String id){
		try{
			if(this.gestorePolicyAttive==null)
				throw new Exception("GestorePolicyAttive non abilitato");
			if(id==null)
				throw new Exception("Parametro non fornito");
			try{
				return this.gestorePolicyAttive.printInfoPolicy(id, "================================================================");
			}catch(PolicyNotFoundException notFound){
				return "Informazioni sulla Policy non disponibili; non sono ancora transitate sulla Porta di Dominio richieste che soddisfano i criteri di filtro impostati";
			}catch(PolicyShutdownException notFound){
				return "GestorePolicyAttive in shutdown; controllare lo stato della Porta di Dominio";
			}
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String removeAllPolicies(){
		try{
			if(this.gestorePolicyAttive==null)
				throw new Exception("GestorePolicyAttive non abilitato");
			this.gestorePolicyAttive.removeAllActiveThreadsPolicy();
			return "Operazione 'remove' effettuata con successo";
		}catch(PolicyShutdownException notFound){
			return "GestorePolicyAttive in shutdown; controllare lo stato della Porta di Dominio";
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String resetAllPolicies(){
		try{
			if(this.gestorePolicyAttive==null)
				throw new Exception("GestorePolicyAttive non abilitato");
			this.gestorePolicyAttive.resetCountersAllActiveThreadsPolicy();
			return JMXUtils.MSG_RESET_CACHE_EFFETTUATO_SUCCESSO;
		}catch(PolicyShutdownException notFound){
			return "GestorePolicyAttive in shutdown; controllare lo stato della Porta di Dominio";
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String removePolicy(String id){
		try{
			if(this.gestorePolicyAttive==null)
				throw new Exception("GestorePolicyAttive non abilitato");
			if(id==null)
				throw new Exception("Parametro non fornito");
			this.gestorePolicyAttive.removeActiveThreadsPolicy(id);
			return "Operazione 'remove' effettuata con successo";
		}catch(PolicyShutdownException notFound){
			return "GestorePolicyAttive in shutdown; controllare lo stato della Porta di Dominio";
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String resetPolicy(String id){
		try{
			if(this.gestorePolicyAttive==null)
				throw new Exception("GestorePolicyAttive non abilitato");
			if(id==null)
				throw new Exception("Parametro non fornito");
			this.gestorePolicyAttive.resetCountersActiveThreadsPolicy(id);
			return JMXUtils.MSG_RESET_CACHE_EFFETTUATO_SUCCESSO;
		}catch(PolicyShutdownException notFound){
			return "GestorePolicyAttive in shutdown; controllare lo stato della Porta di Dominio";
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String resetCache(){
		try{
			if(this.cacheAbilitata==false)
				throw new Exception("Cache non abilitata");
			GestoreCacheControlloTraffico.resetCache();
			return JMXUtils.MSG_RESET_CACHE_EFFETTUATO_SUCCESSO;
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String printStatCache(){
		try{
			if(this.cacheAbilitata==false)
				throw new Exception("Cache non abilitata");
			return GestoreCacheControlloTraffico.printStatsCache("\n");
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public void abilitaCache() throws JMException{
		try{
			GestoreCacheControlloTraffico.abilitaCache();
			this.cacheAbilitata = true;
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
		}
	}
	
	public String abilitaCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond){
		try{
			GestoreCacheControlloTraffico.abilitaCache(dimensioneCache,algoritmoCacheLRU,itemIdleTime,itemLifeSecond,this.log);
			this.cacheAbilitata = true;
			return JMXUtils.MSG_ABILITAZIONE_CACHE_EFFETTUATA;
		}catch(Throwable e){
			this.log.error(e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
		
	
	public void disabilitaCache() throws JMException{
		try{
			GestoreCacheControlloTraffico.disabilitaCache();
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
			return GestoreCacheControlloTraffico.listKeysCache("\n");
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String getObjectCache(String key){
		try{
			if(this.cacheAbilitata==false)
				throw new Exception("Cache non abilitata");
			return GestoreCacheControlloTraffico.getObjectCache(key);
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}
	
	public String removeObjectCache(String key){
		try{
			if(this.cacheAbilitata==false)
				throw new Exception("Cache non abilitata");
			GestoreCacheControlloTraffico.removeObjectCache(key);
			return JMXUtils.MSG_RIMOZIONE_CACHE_EFFETTUATA;
		}catch(Throwable e){
			this.log.error(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage(),e);
			return JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
		}
	}

}
