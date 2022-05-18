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

package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast;

import java.util.Map.Entry;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.slf4j.Logger;

import com.hazelcast.core.Offloadable;
import com.hazelcast.map.EntryProcessor;

/**     
 *  UpdateDatiRequestProcessor
 *
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UpdateDatiRequestProcessor implements EntryProcessor<IDUnivocoGroupByPolicy, DatiCollezionati, DatiCollezionati>, Offloadable {
	
	private static final long serialVersionUID = 1L;
	private final ActivePolicy activePolicy;

	public UpdateDatiRequestProcessor(ActivePolicy policy) {
		this.activePolicy = policy;			
	}
	
	@Override
	public DatiCollezionati  process(Entry<IDUnivocoGroupByPolicy, DatiCollezionati> entry) {
		//System.out.println("<"+idTransazione+"> registerStartRequest distribuita");
		if(entry.getValue() == null) {
			System.out.println("<"/*+idTransazione*/+">updateDatiStartRequestApplicabile Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+entry.getKey().toString()+"]");
			return null;
		} else {
			OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
			Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(op2Properties.isControlloTrafficoDebug());

			entry.getValue().updateDatiStartRequestApplicabile(log, this.activePolicy);
			entry.setValue(entry.getValue());
			return entry.getValue();
		}
	}

	@Override
	public String getExecutorName() {
		return "hz:offloadable";
	}
}