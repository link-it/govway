/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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


package org.openspcoop2.web.monitor.allarmi.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.openspcoop2.web.monitor.allarmi.bean.AllarmiSearchForm;
import org.openspcoop2.web.monitor.allarmi.dao.AllarmiService;
import org.openspcoop2.web.monitor.allarmi.dao.IAllarmiService;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.status.BaseSondaPdd;
import org.openspcoop2.web.monitor.core.status.BaseStatus;
import org.openspcoop2.web.monitor.core.status.ISondaPdd;
import org.openspcoop2.web.monitor.core.status.IStatus;
import org.openspcoop2.web.monitor.core.status.SondaStatus;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

/**     
 * AllarmeStatus
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllarmeStatus extends BaseSondaPdd implements ISondaPdd{


	private transient AllarmiSearchForm searchForm = null;
	private transient IAllarmiService allarmiService = null;
	/**
	 * 
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String PATH_ALLARMI = "/pages/list/statoAllarmi.jsf";

	private int totOk = 0;
	private int totAllWarn = 0;
	private int totAllErr = 0;
	private int totWarnAkc = 0;
	private int totErrAck = 0;
	private int totWarnNoAck = 0;
	private int totErrNoAck = 0;
	private int totaleAllarmi = 0;
	private String requestContextPath;
	private transient AllarmiSearchForm searchFormPaginaStatoAllarmi;
	private boolean allarmiAssociazioneAcknowledgedStatoAllarme;
	

	public AllarmeStatus(String nome, Logger log, Properties prop) throws Exception{
		super(nome, log, prop);


		// Search form allarmi contiene gia' la configurazione utente
		this.searchForm = new AllarmiSearchForm();

		this.allarmiService = new AllarmiService();
		((AllarmiService)this.allarmiService).setSearch(this.searchForm);

	}
	@Override
	protected void init() throws Exception {
		try{
			this.log.debug("Init Sonda AllarmeStatus in corso...");
			this.listaStatus = new ArrayList<IStatus>();
			this.allarmiAssociazioneAcknowledgedStatoAllarme = PddMonitorProperties.getInstance(this.log).isAllarmiAssociazioneAcknowledgedStatoAllarme();

			// In questa implementazione c'e' solo lo stato generale degli allarmi.
			IStatus statoAllarmi = new BaseStatus();
			statoAllarmi.setNome("Dettagli");
			this.listaStatus.add(statoAllarmi);
			FacesContext currentInstance = FacesContext.getCurrentInstance();
			ExternalContext externalContext = currentInstance.getExternalContext();
			this.requestContextPath = externalContext.getRequestContextPath();
			ApplicationContext context = FacesContextUtils.getWebApplicationContext(currentInstance);
			this.searchFormPaginaStatoAllarmi = (AllarmiSearchForm) context.getBean("searchFormAllarmi");
			this.log.debug("Init Sonda AllarmeStatus completato.");

		}catch(Exception e){
			this.log.error("Si e' verificato un errore durante l'Init AllarmeStatus Status: " + e.getMessage(),e); 
			throw e;
		}
	}

	@Override
	public List<IStatus> updateStato() throws Exception {
				
		for (IStatus status : this.listaStatus) {
			try{
				
				// aggiorna lo stato:

				// totale Ok
				this.totOk = this.allarmiService.getCountAllarmiByStato("Ok",null).intValue();
				if(this.allarmiAssociazioneAcknowledgedStatoAllarme){
					// totale  Warn Ack
					this.totWarnAkc = this.allarmiService.getCountAllarmiByStato("Warn",1).intValue();
					// totale  Warn No Ack
					this.totWarnNoAck = this.allarmiService.getCountAllarmiByStato("Warn",0).intValue();
					// totale  Error Ack
					this.totErrAck = this.allarmiService.getCountAllarmiByStato("Error",1).intValue();
					// totale  Error No Ack
					this.totErrNoAck = this.allarmiService.getCountAllarmiByStato("Error",0).intValue();
					// totale allarmi
					this.totaleAllarmi = this.totOk + this.totWarnAkc + this.totWarnNoAck + this.totErrAck + this.totErrNoAck;
				}
				else{
					// totale  Warn
					this.totAllWarn = this.allarmiService.getCountAllarmiByStato("Warn",null).intValue();
					//totale Error
					this.totAllErr = this.allarmiService.getCountAllarmiByStato("Error",null).intValue();
					// totale allarmi
					this.totaleAllarmi = this.totOk + this.totAllWarn + this.totAllErr;
				}

				StringBuilder sb = new StringBuilder();
				sb.append("Allarmi attivi con stato Ok: ").append(this.totOk);
				if(this.allarmiAssociazioneAcknowledgedStatoAllarme){
					sb.append(", Warning: ").append(this.totWarnNoAck);
					sb.append(", Warning (Ack): ").append(this.totWarnAkc);
					sb.append(", Error: ").append(this.totErrNoAck);
					sb.append(", Error (Ack): ").append(this.totErrAck);
				}
				else{
					sb.append(", Warning: ").append(this.totAllWarn);
					sb.append(", Error: ").append(this.totAllErr);
				}
				sb.append(".");

				status.setStato(this.getStatoSondaPdd());
				status.setDescrizione(sb.toString());
			}catch(Exception e){
				this.log.error("Si e' verificato un errore durante l'updateStato: " + e.getMessage(),e); 
				status.setStato(SondaStatus.ERROR); 
				status.setDescrizione(e.getMessage());
			}

		}

		return this.listaStatus;
	}
	@Override
	public String getMessaggioStatoSondaPdd() throws Exception {
		StringBuilder sb = new StringBuilder();

		if(this.totaleAllarmi != -1){
			if(this.totaleAllarmi == this.totOk){
				sb.append("Non risultano anomalie");
			}
			else{
				if(this.allarmiAssociazioneAcknowledgedStatoAllarme){
					if(this.totErrNoAck>0){
						sb.append("Il sistema ha rilevato condizioni di errore");
					}
					else if(this.totWarnNoAck>0){
						sb.append("Il sistema ha rilevato condizioni di warning"); // warning
					}
					else{
						sb.append("Risultano anomalie in fase di gestione");
					}
				}
				else{
					if(this.totAllErr<=0){
						sb.append("Il sistema ha rilevato condizioni di warning"); // warning
					}
					else{
						sb.append("Il sistema ha rilevato condizioni di errore");
					}		
				}
			}
		}else 
			sb.append("La sonda non è funzionante");

		return sb.toString();
	}

	@Override
	public String getLinkDettaglio() throws Exception {
		String path = null;

		if(this.totaleAllarmi != -1){
			if(this.totaleAllarmi == this.totOk){
				path = null;
			}
			else{
				path = this.requestContextPath + PATH_ALLARMI;
			}
		}else 
			path = null;

		// Simulazione del click dal menu' di sx
		if(path != null){
			if(this.searchFormPaginaStatoAllarmi!= null)
				this.searchFormPaginaStatoAllarmi.ripulisci();
		}

		return path;
	}

	@Override
	public SondaStatus getStatoSondaPdd() throws Exception {
		// stato non verificato
		if(this.totaleAllarmi == -1 || this.totOk == -1)
			return SondaStatus.ERROR;
		if(this.allarmiAssociazioneAcknowledgedStatoAllarme){
			if(this.totErrAck == -1 || this.totErrNoAck == -1 || this.totWarnAkc == -1 || this.totWarnNoAck == -1)
				return SondaStatus.ERROR;
		}
		else{
			if(this.totAllErr == -1 || this.totAllWarn == -1)
				return SondaStatus.ERROR;
		}
		
		if(this.allarmiAssociazioneAcknowledgedStatoAllarme){
			if(this.totErrNoAck > 0){
				return SondaStatus.ERROR;
			}else
				// parzialmente in errore
				if(this.totWarnNoAck > 0){
					return  SondaStatus.WARNING;
				}else {
					return SondaStatus.OK;
				}
		}
		else{
			// Tutti in errore
			if(this.totAllErr > 0){
				return SondaStatus.ERROR;
			}else
				// parzialmente in errore
				if(this.totAllWarn > 0){
					return  SondaStatus.WARNING;
				}else {
					return SondaStatus.OK;
				}
		}
	}

	@Override
	public void reset() throws Exception {
		super.reset();

		this.totOk = -1;
		this.totAllErr = -1;
		this.totAllWarn = -1;
		this.totaleAllarmi = -1;
		this.totErrAck = -1;
		this.totErrNoAck = -1;
		this.totWarnAkc = -1;
		this.totWarnNoAck = -1;
	}
}
