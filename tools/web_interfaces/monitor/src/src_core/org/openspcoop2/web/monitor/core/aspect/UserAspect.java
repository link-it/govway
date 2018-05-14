package org.openspcoop2.web.monitor.core.aspect;

import it.link.pdd.core.auditing.Audit;
import it.link.pdd.core.auditing.constants.OperationType;
import it.link.pdd.core.auditing.constants.Status;
import it.link.pdd.core.utenti.Utente;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;

import org.slf4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class UserAspect {

	private static Logger log =  LoggerManager.getPddMonitorAuditLogger();
	
	
	public void around(ProceedingJoinPoint pjp, Utente user) throws Throwable{
		
		Audit audit = new Audit();
		
		audit.setUsername(Utility.getLoggedUser().getUsername());
		audit.setStatus(Status.REQUESTING);
		audit.setType(user.getId()!= -1 ? OperationType.UPDATE.name() : OperationType.CREATE.name());
		audit.setObjectId(pjp.toShortString());
		
		if(pjp.toShortString().contains("delete"))
			audit.setType(OperationType.DELETE.name());
		
		if(!audit.getType().equals(OperationType.CREATE.name()))
			audit.setExecutionDetails("id:["+user.getId()+"]");

		UserAspect.log.info(audit.toString());
		try{
		
			pjp.proceed();
					
			audit.setStatus(Status.COMPLETED);
			audit.setExecutionDetails("id:["+user.getId()+"]");
			
			
		}catch (Exception e) {
			audit.setStatus(Status.ERROR);
			audit.setExecutionDetails(e.getMessage());
			UserAspect.log.info(audit.toString());
			throw e;
		}
		
		
		UserAspect.log.info(audit.toString());
	}
}
