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
	private static IUniqueIdentifierGenerator uniqueIdentifierGenerator;
	
	/** Generazione UID Disabiltiata */
	private static boolean generazioneUIDDisabilitata = false;
	public static boolean isGenerazioneUIDDisabilitata() {
		return UniqueIdentifierManager.generazioneUIDDisabilitata;
	}
	
	public static void disabilitaGenerazioneUID(){
		UniqueIdentifierManager.generazioneUIDDisabilitata=true;
	}
	
	
	public static synchronized void inizializzaUniqueIdentifierManager(String className,Object ... o)throws UniqueIdentifierException{
		
		if(UniqueIdentifierManager.uniqueIdentifierGenerator==null){
			try{
				UniqueIdentifierManager.uniqueIdentifierGenerator = (IUniqueIdentifierGenerator) Loader.getInstance().newInstance(className);
				UniqueIdentifierManager.uniqueIdentifierGenerator.init(o);
			}catch(Exception e){
				throw new UniqueIdentifierException("Riscontrato errore durante il caricamento del data manager specificato [class:"+className+"]: "+e.getMessage(),e);
			}
		}
	}
	
	public static IUniqueIdentifier newUniqueIdentifier() throws UniqueIdentifierException{
		if(UniqueIdentifierManager.generazioneUIDDisabilitata){
			return null;
		}
		try{
			if(UniqueIdentifierManager.uniqueIdentifierGenerator==null){
				Logger log = LoggerWrapperFactory.getLogger(UniqueIdentifierManager.class);
				log.error("UniqueIdentifierManager non inizializzato");
				UniqueIdentifierManager.inizializzaUniqueIdentifierManager("org.openspcoop.utils.id.ClusterIdentifier");
			}
			return UniqueIdentifierManager.uniqueIdentifierGenerator.newID();
		}catch(Exception e){
			throw new UniqueIdentifierException("UniqueIdentifierManager.newID() non riuscita",e);		
		}
	}
	
	public static IUniqueIdentifier convertFromString(String value) throws UniqueIdentifierException{
		if(UniqueIdentifierManager.generazioneUIDDisabilitata){
			return null;
		}
		try{
			if(UniqueIdentifierManager.uniqueIdentifierGenerator==null){
				Logger log = LoggerWrapperFactory.getLogger(UniqueIdentifierManager.class);
				log.error("UniqueIdentifierManager non inizializzato");
				UniqueIdentifierManager.inizializzaUniqueIdentifierManager("org.openspcoop.utils.id.ClusterIdentifier");
			}
			return UniqueIdentifierManager.uniqueIdentifierGenerator.convertFromString(value);
		}catch(Exception e){
			throw new UniqueIdentifierException("UniqueIdentifierManager.convertFromString() non riuscita",e);		
		}
	}
}
