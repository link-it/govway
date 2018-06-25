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
package org.openspcoop2.web.monitor.core.status;

import org.openspcoop2.web.monitor.core.status.PddStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.HttpUtilsException;
import org.slf4j.Logger;

/**
 * SondaPddStatus
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class SondaPddStatus extends BaseSondaPdd implements ISondaPdd{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	public SondaPddStatus(String identificativo, Logger log, Properties prop) throws Exception{
		super(identificativo, log, prop);
	}

	@Override
	protected void init() throws Exception {
		try{
			this.log.debug("Init Sonda Pdd Status in corso...");
			this.listaStatus = new ArrayList<IStatus>();

			for (String namePdD : this.getListaPdDMonitorate_StatusPdD()) {
				String url =   this.propertiesSonda.getProperty(namePdD+".url"); //"pdd."+

				PddStatus pddStat = new PddStatus();
				pddStat.setNome(namePdD);
				pddStat.setUrl(url);

				this.listaStatus.add(pddStat);
			}

			this.log.debug("Init Sonda Pdd Status completato.");

		}catch(Exception e){
			this.log.error("Si e' verificato un errore durante l'Init Sonda Pdd Status: " + e.getMessage(),e); 
			throw e;
		}
	}



	@Override
	public List<IStatus> updateStato() throws Exception {

		for (IStatus pddBean : this.listaStatus) {

			try{
				HttpUtilities.check(((PddStatus) pddBean).getUrl());
				pddBean.setStato(SondaStatus.OK);
				pddBean.setDescrizione(null);
			}catch(Exception e){
				if(e instanceof HttpUtilsException) {
					HttpUtilsException http = (HttpUtilsException) e;
					if(http.getReturnCode()>=200 && http.getReturnCode()<=299) {
						pddBean.setStato(SondaStatus.WARNING);
					}
					else {
						pddBean.setStato(SondaStatus.ERROR);
					}
				}
				else {
					pddBean.setStato(SondaStatus.ERROR);
				}
				pddBean.setDescrizione(e.getMessage());
			}

		}

		return this.listaStatus;
	}

	public List<String> getListaPdDMonitorate_StatusPdD() throws Exception{
		List<String> lista = new ArrayList<String>();
		String tmp = this.propertiesSonda.getProperty("nodi");
		if(tmp!=null && !"".equals(tmp)){
			String[]split = tmp.split(",");
			for (int i = 0; i < split.length; i++) {
				lista.add(split[i].trim());
			}
		}
		return lista;
	}

	@Override
	public String getMessaggioStatoSondaPdd() throws Exception {
		int tot_ok = this.getTotOk();
		// Tutti in errore
		if(tot_ok == 0){
			if(this.listaStatus.size()==1)
				return "Il Gateway non è funzionante";
			else
				return "Nessuno dei "+this.listaStatus.size()+" nodi del Gateway è funzionante";
		}else 
			// parzialmente in errore
			if(tot_ok< this.listaStatus.size()){
				return  (this.listaStatus.size() - tot_ok) + " su " +this.listaStatus.size()+ " nodi del Gateway non sono funzionanti";
			}else {
				// tutti ok
				if(this.listaStatus.size()==1)
					return "Il Gateway è funzionante";
				else
					return "Tutti i "+this.listaStatus.size()+" nodi del Gateway sono funzionanti";
			}
	}


	public int getTotOk() throws Exception {
		int totOk = 0;
		try{
			for (IStatus pddBean : this.listaStatus) {
				if(pddBean.getStato().equals(SondaStatus.OK)) 
					totOk ++;
			}
		}catch(Exception e){

		}
		return totOk;
	}

	@Override
	public SondaStatus getStatoSondaPdd() throws Exception {
		int tot_ok = this.getTotOk();
		// Tutti in errore
		if(tot_ok == 0){
			boolean findWarning = false;
			try{
				for (IStatus pddBean : this.listaStatus) {
					if(pddBean.getStato().equals(SondaStatus.WARNING)) { 
						findWarning = true;
						break;
					}
				}
				if(findWarning) {
					return SondaStatus.WARNING;
				}
				else {
					return SondaStatus.ERROR;
				}
			}catch(Exception e){
				return SondaStatus.ERROR;
			}
		}else
			// parzialmente in errore
			if(tot_ok< this.listaStatus.size()){
				return  SondaStatus.WARNING;
			}else {
				return SondaStatus.OK;
			}
	}

}
