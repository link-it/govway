/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCore;

/**
 * Connettori
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Connettori {
	private static final List<String> lista = new ArrayList<String>();

	public static void initialize(Logger log) throws Exception {
		initialize(log, false, null, null);
	}
	public static void initialize(Logger log, boolean initForApi, String confDir, String protocolloDefault) throws Exception {
		/**
		 * Aggiungo i connettori di default e quelli che leggo nel db
		 */
		for (TipiConnettore t : TipiConnettore.values()) {
			if(!TipiConnettore.CUSTOM.equals(t)){
				Connettori.lista.add(t.getNome());
			}
		}

		// aggiungo i connettori presenti nel db
		try{
			Connettori.readConnettoriFromDB(initForApi, confDir, protocolloDefault);
		}catch(Exception e){
			log.error("Caricamento connettori non riuscito",e);
			throw new Exception(e.getMessage(),e);
		}
	}

	/**
	 * Controlla se il parametro passato e' un connettore
	 * 
	 * @param nome
	 * @return true se e' un connettore
	 */
	public static boolean contains(String nome) {
		return Connettori.lista.contains(nome);
	}

	private static boolean readConnettoriFromDB(boolean initForApi, String confDir, String protocolloDefault) throws Exception {
		try {
			ConnettoriCore core = null;
			if(initForApi) {
				core = new ConnettoriCore(initForApi, confDir, protocolloDefault);
			}else {
				core = new ConnettoriCore();
			}
			List<String> tmpConnettori = core.connettoriList();
			Iterator<String> it = tmpConnettori.iterator();
			while (it.hasNext())
				Connettori.lista.add(it.next());

			ControlStationLogger.getPddConsoleCoreLogger().info("Connettori: caricati " + Connettori.lista.size() + " connettori.");
		} catch (Exception e) {
			ControlStationLogger.getPddConsoleCoreLogger().error("Connettori: " + e.getMessage());
		} 

		return true;
	}

	public static List<String> getList() {
		return new ArrayList<String>(Connettori.lista);
	}
}
