package org.openspcoop2.web.monitor.core.mbean;

import org.slf4j.Logger;

import it.link.pdd.core.auditing.Audit;
import it.link.pdd.core.auditing.Configuration;
import org.openspcoop2.web.monitor.core.dao.IService;
import org.openspcoop2.web.monitor.core.dao.IAuditingService;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;

public class AuditingBean extends PdDBaseBean<Audit, Integer, IService<Audit, Integer>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private transient Logger log =  LoggerManager.getPddMonitorCoreLogger();
	
	private Configuration configuration;
	
	public Configuration getConfiguration() {
		
		if(this.configuration!=null)
			return this.configuration; 
			
		this.configuration = ((IAuditingService)this.service).readConfiguration();
		
		return this.configuration;
	}
	
	
	public String salva(){
		try{
			
			((IAuditingService)this.service).saveConfiguration(this.configuration);
			
			MessageUtils.addInfoMsg("Salvataggio effettuato correttamente.");
			
		}catch(Exception e){
			MessageUtils.addErrorMsg("Si e' verificato un errore. Riprovare successivamente.");
			this.log.error(e.getMessage(), e);
		}
		
		
		return null;
	}
}
