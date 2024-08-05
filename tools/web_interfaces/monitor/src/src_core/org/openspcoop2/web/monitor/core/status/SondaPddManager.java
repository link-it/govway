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
package org.openspcoop2.web.monitor.core.status;

import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.utils.resources.MapReader;

/**
 * SondaPddManager
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class SondaPddManager {

	public static final int STATO_OK = 0;
	public static final int STATO_WARN = 1;
	public static final int STATO_ERR = 2;

	private static SondaPddManager instance = null;
	private Logger log = null;
	private void logDebug(String msg) {
		if(this.log!=null) {
			this.log.debug(msg);
		}
	}
	private void logError(String msg, Exception e) {
		if(this.log!=null) {
			this.log.error(msg,e);
		}
	}
	private List<String> listaSonde;
	private MapReader<String, ISondaPdd> sondePdd = null;
	private boolean enable ;

	private static synchronized void init(Logger log) throws Exception{
		if(SondaPddManager.instance == null)
			SondaPddManager.instance = new SondaPddManager(log);
	}

	public static SondaPddManager getInstance(Logger log) throws Exception{
		if(SondaPddManager.instance == null)
			SondaPddManager.init(log);

		return SondaPddManager.instance;
	}

	public SondaPddManager(Logger log) throws Exception{
		this.log = log;
		Map<String, ISondaPdd> tmpModules = new HashMap<>();
		try{
			this.logDebug("Inizializzazione Sonda Pdd Manager in corso...");
			this.listaSonde = new ArrayList<>();

			// controllo se il monitoraggio e' attivo
			this.enable = PddMonitorProperties.getInstance(log).isStatusPdDEnabled();

			this.logDebug("Stato Monitoraggio: ["+(this.enable ? "attivo" : "non attivo")+"]");

			if(this.enable){
				this.listaSonde = PddMonitorProperties.getInstance(log).getListaSondePdd();

				for (String nomeSondaPdd : this.listaSonde) {

					this.logDebug("Caricamento sonda ["+nomeSondaPdd+"] in corso...");
					// prendo le proprieta specifiche della sonda definite nel file
					Properties propSonda = PddMonitorProperties.getInstance(this.log).getPropertiesSonda(nomeSondaPdd);

					String classeSonda = propSonda.getProperty("class");

					ISondaPdd moduleChecker = loadSondaPdd(nomeSondaPdd,classeSonda,propSonda); 

					this.logDebug("Caricamento sonda ["+nomeSondaPdd+"] completato.");

					tmpModules.put(nomeSondaPdd, moduleChecker);
				}

				if(tmpModules.size()<=0){
					/** throw new Exception("Non sono stati trovati moduli da controllare."); */
					logDebug("Non sono stati trovati moduli da controllare.");
				}

				this.sondePdd = new MapReader<>(tmpModules,true);
			}

			this.logDebug("Inizializzazione Sonda Pdd Manager completata.");
		}catch(Exception e){
			this.sondePdd = new MapReader<>(tmpModules,true);
			this.logError("Si e' verificato un errore durante l'inizializzazione Status Checker Manager:" + e.getMessage(), e);
			throw e;
		}
	}

	public List<ISondaPdd> getSondePdd() throws CoreException{
		List<ISondaPdd> lista = new ArrayList<>();

		if(this.listaSonde != null && !this.listaSonde.isEmpty() ){
			for (String sonda : this.listaSonde) {
				lista.add(getSondaByName(sonda));
			}		
		}
		return lista;
	}

	public ISondaPdd loadSondaPdd(String nomeSonda,String sondaClass, Properties propSonda) throws CoreException {
		try{
			Class<?> c = Class.forName(sondaClass);
			Constructor<?> constructor = c.getConstructor(String.class,Logger.class,Properties.class);
			return (ISondaPdd) constructor.newInstance(nomeSonda,this.log,propSonda);
		} catch (Exception e) {
			throw new CoreException("Impossibile caricare la sonda indicata ["+sondaClass+"] " + e, e);
		}
	}

	public ISondaPdd getSondaByName(String sondaPddName) throws CoreException {
		if(this.sondePdd.containsKey(sondaPddName)){
			return this.sondePdd.get(sondaPddName);
		}
		else{
			throw new CoreException("Check with name ["+sondaPddName+"] not found");
		}
	}

}
