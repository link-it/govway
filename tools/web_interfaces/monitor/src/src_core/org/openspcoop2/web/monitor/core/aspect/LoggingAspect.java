package org.openspcoop2.web.monitor.core.aspect;

import it.link.pdd.core.auditing.Audit;
import it.link.pdd.core.auditing.constants.Status;
import org.openspcoop2.web.monitor.core.auditing.OperationType;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;

import org.slf4j.Logger;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class LoggingAspect {

	private static Logger log =  LoggerManager.getPddMonitorAuditLogger();
	
	
	public void after(){
		Audit audit = new Audit();
		
		audit.setUsername(Utility.getLoggedUser().getUsername());
		audit.setStatus(Status.COMPLETED);
		audit.setType(OperationType.LOGIN.name());
		audit.setObjectId("login");	
		LoggingAspect.log.info(audit.toString());
	}
	
}
