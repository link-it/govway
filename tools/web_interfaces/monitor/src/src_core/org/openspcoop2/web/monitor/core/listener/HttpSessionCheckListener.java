package org.openspcoop2.web.monitor.core.listener;



import org.openspcoop2.web.monitor.core.bean.LoginBean;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

public class HttpSessionCheckListener implements HttpSessionListener {

	private static Logger log = LoggerWrapperFactory.getLogger(HttpSessionCheckListener.class);
	
	@Override
	public void sessionCreated(HttpSessionEvent e) {
		HttpSessionCheckListener.log.debug("session "+e.getSession().getId()+" created.");
		
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent e) {
		HttpSessionCheckListener.log.debug("session "+e.getSession().getId()+" destroyed.");
		try{
			cleanUp(e.getSession());
		}catch (Exception ex) {
			HttpSessionCheckListener.log.error("errore durante le operazioni di clean-up",ex);
		}
	}
	
	private void cleanUp(HttpSession session) throws Exception{
		//recuper utente dalla sessione se esiste
		LoginBean lb = (LoginBean)session.getAttribute(LoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME);
		if(lb!=null){
			HttpSessionCheckListener.log.debug("remove user "+lb.getUsername()+" from session");
			session.setAttribute(LoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME, null);
		}else{
			HttpSessionCheckListener.log.debug("no login info found in session, nothing to do.");
		}
	}
	
}
