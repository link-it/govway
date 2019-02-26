/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
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
	private transient Logger log = null;
	private List<String> listaSonde;
	private MapReader<String, ISondaPdd> sondePdd = null;
	private boolean enable ;

	private static synchronized void init(Logger log) throws Exception{
		if(SondaPddManager.instance == null)
			SondaPddManager.instance = new SondaPddManager(log);
	}

	public static SondaPddManager getInstance(Logger log) throws Exception{
		if(SondaPddManager.instance == null)
			init(log);

		return SondaPddManager.instance;
	}

	public SondaPddManager(Logger log) throws Exception{
		this.log = log;
		Hashtable<String, ISondaPdd> tmp_modules = new Hashtable<String, ISondaPdd>();
		try{
			this.log.debug("Inizializzazione Sonda Pdd Manager in corso...");
			this.listaSonde = new ArrayList<String>();

			// controllo se il monitoraggio e' attivo
			this.enable = PddMonitorProperties.getInstance(log).isStatusPdDEnabled();

			this.log.debug("Stato Monitoraggio: ["+(this.enable ? "attivo" : "non attivo")+"]");

			if(this.enable){
				this.listaSonde = PddMonitorProperties.getInstance(log).getListaSondePdd();

				for (String nomeSondaPdd : this.listaSonde) {

					this.log.debug("Caricamento sonda ["+nomeSondaPdd+"] in corso...");
					// prendo le proprieta specifiche della sonda definite nel file
					Properties propSonda = PddMonitorProperties.getInstance(this.log).getPropertiesSonda(nomeSondaPdd);

					String classeSonda = propSonda.getProperty("class");

					ISondaPdd moduleChecker = loadSondaPdd(nomeSondaPdd,classeSonda,propSonda); 

					this.log.debug("Caricamento sonda ["+nomeSondaPdd+"] completato.");

					tmp_modules.put(nomeSondaPdd, moduleChecker);
				}

				if(tmp_modules.size()<=0){
					//				throw new Exception("Non sono stati trovati moduli da controllare.");
					log.debug("Non sono stati trovati moduli da controllare.");
				}

				this.sondePdd = new MapReader<String, ISondaPdd>(tmp_modules,true);
			}

			this.log.debug("Inizializzazione Sonda Pdd Manager completata.");
		}catch(Exception e){
			this.sondePdd = new MapReader<String, ISondaPdd>(tmp_modules,true);
			this.log.error("Si e' verificato un errore durante l'inizializzazione Status Checker Manager:" + e.getMessage(), e);
			throw e;
		}
	}

	public List<ISondaPdd> getSondePdd() throws Exception{
		List<ISondaPdd> lista = new ArrayList<ISondaPdd>();

		if(this.listaSonde != null && this.listaSonde.size() > 0 ){
			for (String sonda : this.listaSonde) {
				lista.add(getSondaByName(sonda));
			}		
		}
		return lista;
	}

	public ISondaPdd loadSondaPdd(String nomeSonda,String sondaClass, Properties propSonda) throws Exception {
		try{
			Class<?> c = Class.forName(sondaClass);
			Constructor<?> constructor = c.getConstructor(String.class,Logger.class,Properties.class);
			ISondaPdd p = (ISondaPdd) constructor.newInstance(nomeSonda,this.log,propSonda);
			return  p;
		} catch (Exception e) {
			throw new Exception("Impossibile caricare la sonda indicata ["+sondaClass+"] " + e, e);
		}
	}

	public ISondaPdd getSondaByName(String sondaPddName) throws Exception {
		if(this.sondePdd.containsKey(sondaPddName)){
			return this.sondePdd.get(sondaPddName);
		}
		else{
			throw new Exception("WebGenericProjectFactory with name ["+sondaPddName+"] not found");
		}
	}

}
