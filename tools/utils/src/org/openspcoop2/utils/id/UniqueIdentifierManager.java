/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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


package org.openspcoop2.utils.id;


import org.slf4j.Logger;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.Loader;

/**
 * UniqueIdentifierManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UniqueIdentifierManager {

	/** Istanza per creare gli id */
	private static boolean useThreadLocal = false;
	private static String className_threadLocal = null;
	private static Object [] args_threadLocal = null;
    private static final ThreadLocal<IUniqueIdentifierGenerator> uniqueIdentifierGenerator_threadLocal =
            new ThreadLocal<IUniqueIdentifierGenerator>() {
				@Override
				protected IUniqueIdentifierGenerator initialValue() {
                	try{
                		IUniqueIdentifierGenerator uniqueIdentifierGenerator = (IUniqueIdentifierGenerator) Loader.getInstance().newInstance(className_threadLocal);
        				uniqueIdentifierGenerator.init(args_threadLocal);
        				return uniqueIdentifierGenerator;
        			}catch(Exception e){
        				throw new RuntimeException("Riscontrato errore durante il caricamento del manager specificato [class:"+className_threadLocal+"]: "+e.getMessage(),e);
        			}
                }
                
        };
	
	private static IUniqueIdentifierGenerator uniqueIdentifierGenerator_staticInstance;
	private static boolean bufferSupported_staticInstance;
	
	/** Generazione UID Disabiltiata */
	private static boolean generazioneUIDDisabilitata = false;
	public static boolean isGenerazioneUIDDisabilitata() {
		return UniqueIdentifierManager.generazioneUIDDisabilitata;
	}
	
	public static void disabilitaGenerazioneUID(){
		UniqueIdentifierManager.generazioneUIDDisabilitata=true;
	}
	
	public static boolean isInitialized() {
		if(!UniqueIdentifierManager.generazioneUIDDisabilitata) {
			if(useThreadLocal) {
				return className_threadLocal!=null;
			}
			else {
				return uniqueIdentifierGenerator_staticInstance!=null;
			}
		}
		return false;
	}
	
	public static synchronized void inizializzaUniqueIdentifierManager(boolean useThreadLocal, String className,Object ... o)throws UniqueIdentifierException{
		inizializzaUniqueIdentifierManager(false, useThreadLocal, className, o);
	}
	public static synchronized void inizializzaUniqueIdentifierManager(boolean forceInitManager, boolean useThreadLocal, String className,Object ... o)throws UniqueIdentifierException{
		UniqueIdentifierManager.useThreadLocal = useThreadLocal;
		if(useThreadLocal) {
			if(UniqueIdentifierManager.className_threadLocal==null || forceInitManager){
				UniqueIdentifierManager.className_threadLocal=className;
				UniqueIdentifierManager.args_threadLocal=o;
				
				try{
            		IUniqueIdentifierGenerator uniqueIdentifierGenerator = (IUniqueIdentifierGenerator) Loader.getInstance().newInstance(className);
            		UniqueIdentifierManager.bufferSupported_staticInstance = uniqueIdentifierGenerator.isBufferSupperted();
    			}catch(Exception e){
    				throw new RuntimeException("Riscontrato errore durante il caricamento del manager specificato [class:"+className_threadLocal+"]: "+e.getMessage(),e);
    			}
			}
		}
		else {
			if(UniqueIdentifierManager.uniqueIdentifierGenerator_staticInstance==null || forceInitManager){
				try{
					UniqueIdentifierManager.uniqueIdentifierGenerator_staticInstance = (IUniqueIdentifierGenerator) Loader.getInstance().newInstance(className);
					UniqueIdentifierManager.uniqueIdentifierGenerator_staticInstance.init(o);
					UniqueIdentifierManager.bufferSupported_staticInstance = UniqueIdentifierManager.uniqueIdentifierGenerator_staticInstance.isBufferSupperted();
				}catch(Exception e){
					throw new UniqueIdentifierException("Riscontrato errore durante il caricamento del manager specificato [class:"+className+"]: "+e.getMessage(),e);
				}
			}
		}
		
	}
	
	public static boolean isBufferSupported() {
		return UniqueIdentifierManager.bufferSupported_staticInstance;
	}
	
	public static IUniqueIdentifier newUniqueIdentifier() throws UniqueIdentifierException{
		if(UniqueIdentifierManager.generazioneUIDDisabilitata){
			return null;
		}
		try{
			if(useThreadLocal) {
				return UniqueIdentifierManager.uniqueIdentifierGenerator_threadLocal.get().newID();
			}
			else {
				if(UniqueIdentifierManager.uniqueIdentifierGenerator_staticInstance==null){
					Logger log = LoggerWrapperFactory.getLogger(UniqueIdentifierManager.class);
					log.error("UniqueIdentifierManager non inizializzato");
					UniqueIdentifierManager.inizializzaUniqueIdentifierManager(false, "org.openspcoop.utils.id.ClusterIdentifier");
				}
				return UniqueIdentifierManager.uniqueIdentifierGenerator_staticInstance.newID();
			}
		}catch(Exception e){
			throw new UniqueIdentifierException("UniqueIdentifierManager.newID() non riuscita",e);		
		}
	}
	
	public static IUniqueIdentifier newUniqueIdentifier(boolean useBuffer) throws UniqueIdentifierException{
		if(UniqueIdentifierManager.generazioneUIDDisabilitata){
			return null;
		}
		try{
			if(useThreadLocal) {
				return UniqueIdentifierManager.uniqueIdentifierGenerator_threadLocal.get().newID(useBuffer);
			}
			else {
				if(UniqueIdentifierManager.uniqueIdentifierGenerator_staticInstance==null){
					Logger log = LoggerWrapperFactory.getLogger(UniqueIdentifierManager.class);
					log.error("UniqueIdentifierManager non inizializzato");
					UniqueIdentifierManager.inizializzaUniqueIdentifierManager(false, "org.openspcoop.utils.id.ClusterIdentifier");
				}
				return UniqueIdentifierManager.uniqueIdentifierGenerator_staticInstance.newID(useBuffer);
			}
		}catch(Exception e){
			throw new UniqueIdentifierException("UniqueIdentifierManager.newID(useBuffer) non riuscita",e);		
		}
	}
	
	public static IUniqueIdentifier convertFromString(String value) throws UniqueIdentifierException{
		if(UniqueIdentifierManager.generazioneUIDDisabilitata){
			return null;
		}
		try{
			if(useThreadLocal) {
				return UniqueIdentifierManager.uniqueIdentifierGenerator_threadLocal.get().convertFromString(value);
			}
			else {
				if(UniqueIdentifierManager.uniqueIdentifierGenerator_staticInstance==null){
					Logger log = LoggerWrapperFactory.getLogger(UniqueIdentifierManager.class);
					log.error("UniqueIdentifierManager non inizializzato");
					UniqueIdentifierManager.inizializzaUniqueIdentifierManager(false, "org.openspcoop.utils.id.ClusterIdentifier");
				}
				return UniqueIdentifierManager.uniqueIdentifierGenerator_staticInstance.convertFromString(value);
			}
		}catch(Exception e){
			throw new UniqueIdentifierException("UniqueIdentifierManager.convertFromString() non riuscita",e);		
		}
	}
	
	public static void removeThreadLocal() throws UniqueIdentifierException{
		if(useThreadLocal) {
			UniqueIdentifierManager.uniqueIdentifierGenerator_threadLocal.remove();
		}
	}
}
