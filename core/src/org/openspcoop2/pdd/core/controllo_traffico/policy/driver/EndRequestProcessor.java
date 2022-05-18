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


package org.openspcoop2.pdd.core.controllo_traffico.policy.driver;

import java.util.List;
import java.util.Map.Entry;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.slf4j.Logger;

import com.hazelcast.core.Offloadable;
import com.hazelcast.map.EntryProcessor;


/**
 * 
 * @author Francesco Scarlato
 * 
 * // L'EntryProcessor si esegue sul proprietario della chiave.
 *  Implementando l'interfaccia Offloadable eseguiamo il task su un ExecutorService a parte, sbloccando altre operazioni
 *	 sulle chiavi della stessa partizione.
 *  Utilizza il default ExecutionService.OFFLOADABLE_EXECUTOR, in questo modo solo la chiave viene lockata e non la partizione intera
 *
 */
public class EndRequestProcessor implements EntryProcessor<IDUnivocoGroupByPolicy, DatiCollezionati, Boolean>, Offloadable {
	
	private static final long serialVersionUID = 1L;
	private final ActivePolicy activePolicy;
	
	private final MisurazioniTransazione dati;
	private final boolean isApplicabile;
	private final boolean isViolata;

	public EndRequestProcessor(ActivePolicy policy, MisurazioniTransazione dati, boolean isApplicabile, boolean isViolata) {
		this.activePolicy = policy;
		this.dati = dati;
		this.isApplicabile = isApplicabile;
		this.isViolata = isViolata;
	}
	
	@Override
	public Boolean  process(Entry<IDUnivocoGroupByPolicy, DatiCollezionati> entry) {
		//System.out.println("<"+idTransazione+"> registerStartRequest distribuita");
		DatiCollezionati datiCollezionati = entry.getValue();
		if(datiCollezionati == null) {
			System.out.println("<"/*+idTransazione*/+">updateDatiStartRequestApplicabile Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+entry.getKey().toString()+"]");
			return false;
		}
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(op2Properties.isControlloTrafficoDebug());

		
		if(this.isApplicabile) {
			datiCollezionati.registerEndRequest(log, this.activePolicy, this.dati);
			List<Integer> esitiCodeOk = null;
			List<Integer> esitiCodeKo_senzaFaultApplicativo = null;
			List<Integer> esitiCodeFaultApplicativo = null;
			try {
				// In queste tre di sotto pare il logger non venga utilizzato
				esitiCodeOk = EsitiProperties.getInstance(log,this.dati.getProtocollo()).getEsitiCodeOk_senzaFaultApplicativo();
				esitiCodeKo_senzaFaultApplicativo = EsitiProperties.getInstance(log,this.dati.getProtocollo()).getEsitiCodeKo_senzaFaultApplicativo();
				esitiCodeFaultApplicativo = EsitiProperties.getInstance(log,this.dati.getProtocollo()).getEsitiCodeFaultApplicativo();
				datiCollezionati.updateDatiEndRequestApplicabile(
						log, 	// logger
						this.activePolicy, this.dati,
						esitiCodeOk,esitiCodeKo_senzaFaultApplicativo, esitiCodeFaultApplicativo, 
						this.isViolata);
			}catch(Exception e) {
				//throw new PolicyException(e.getMessage(),e); TODO: Ristabilire il comportamento corretto in questo caso, bisogna sollevare una eccezione nel nodo che ha chiamato questo processor
				System.out.println("<"/*+idTransazione*/+">EndRequestProcessor, errore sulla policy con dati identificativi ["+entry.getKey().toString()+"]: " + e.getMessage());
				return false;
			}
			
			entry.setValue(datiCollezionati);
		} else {
			datiCollezionati.registerEndRequest(null, this.activePolicy, this.dati);
			entry.setValue(datiCollezionati);
		}
		
	return true;
}
	
	@Override
	public String getExecutorName() {
		return "hz:offloadable";
	}
}