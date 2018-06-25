/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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

package org.openspcoop2.web.ctrlstat.gestori;

import org.slf4j.Logger;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;

/**
 *
 * GestoreConsistenzaDati
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class GestoreConsistenzaDati implements Runnable {

	public static boolean gestoreConsistenzaDatiInEsecuzione = false;
	public static boolean gestoreConsistenzaDatiEseguitoConErrore = false;
	
	private Logger log = null;

	private boolean initForceMapping;
	
	private boolean stop = false;
	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public GestoreConsistenzaDati(boolean initForceMapping) {
		this.log = ControlStationLogger.getPddConsoleCoreLogger();
		this.initForceMapping = initForceMapping;
	}

	@Override
	public void run() {

		if(gestoreConsistenzaDatiInEsecuzione){
			this.log.info("Gestore Consistenza Dati risulta già avviato");
			return;
		}
		
		gestoreConsistenzaDatiInEsecuzione = true;
		
		String statoOperazione = "";
		try{

			// Controllo inizializzazione risorse
			// L'inizializzazione del core attende anche che venga inizializzato il datasource
			ControlStationCore core = null;
			if(!this.stop){
				core = new ControlStationCore();
			}

			// Mapping Erogazione
			if(!this.stop){
				statoOperazione = "[Inizializzazione Mapping Erogazione] ";
				this.log.debug("Controllo Consistenza Dati Mapping Erogazione-PA ....");
				core.initMappingErogazione(this.initForceMapping,this.log);
				this.log.debug("Controllo Consistenza Dati Mapping Erogazione-PA completato con successo");
			}

			// Mapping Fruizione
			if(!this.stop){
				statoOperazione = "[Inizializzazione Mapping Fruizione] ";
				this.log.debug("Controllo Consistenza Dati Mapping Fruizione-PD ....");
				core.initMappingFruizione(this.initForceMapping,this.log);
				this.log.debug("Controllo Consistenza Dati Mapping Fruizione-PD completato con successo");
			}
			
			this.log.info("Attività di Controllo Consistenza Dati completato con successo.");

		}catch(Exception e){
			gestoreConsistenzaDatiEseguitoConErrore = true;
			this.log.error(statoOperazione+e.getMessage(),e);
			throw new RuntimeException(e.getMessage(),e);
		}finally{
			gestoreConsistenzaDatiInEsecuzione = false;
		}

	}

}
