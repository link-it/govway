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
package org.openspcoop2.web.monitor.core.listener;



import org.openspcoop2.web.monitor.core.bean.AbstractLoginBean;
import org.openspcoop2.web.monitor.core.bean.LoginBean;

import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
 * LoginPhaseListener
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class LoginPhaseListener implements PhaseListener {

	public static final String PRINCIPAL_ERROR_MSG = "principalErrorMsg";
	public static final String PRINCIPAL_SHOW_FORM = "principalShowForm";
	
	private static final long serialVersionUID = -7545768911858875216L;
	private static Logger log = LoggerWrapperFactory.getLogger(LoginPhaseListener.class);
	
	private static String[] allowedPages = {"login","timeout","error"};
		
	@Override
	public void afterPhase(PhaseEvent event) {
		try{

			FacesContext fc = event.getFacesContext();
			
			ExternalContext ec = fc.getExternalContext();
			LoginBean lb = (LoginBean)ec.getSessionMap().get(AbstractLoginBean.LOGIN_BEAN_SESSION_ATTRIBUTE_NAME);
	        //controllo se sono nella pagina di login
			boolean isLogged = lb == null ? false : lb.isLoggedIn();
			
			UIViewRoot vr = fc.getViewRoot();
			//se il viewroot e' == null allora vuol dire ke ho perso tutte le informazioni sulla
			//sullo stato jsf quindi devo riautenticarmi
	        boolean allowedPage = vr!=null ? isAllowedPage(vr.getViewId()) : false;
	        
	        String msg = null;
	      //Controllo se sto andando alla pagina di errore e se c'e' un errore da visualizzare
	        if(allowedPage && StringUtils.contains(vr.getViewId(), "error")){
	        	msg = (String) ec.getSessionMap().get(LoginPhaseListener.PRINCIPAL_ERROR_MSG);
	        	if(msg != null){
	        		// 
	        		ec.getRequestMap().put(LoginPhaseListener.PRINCIPAL_SHOW_FORM, "true");
	        		
	        		// rimozione messaggio
	        		ec.getSessionMap().remove(LoginPhaseListener.PRINCIPAL_ERROR_MSG);
	        		HttpSession session = (HttpSession) ec.getSession(false);
	        		if(session!= null)
	        			session.invalidate();
	        		// stampa messaggi
	        		addError(fc, msg);
	        	}
	        }
	        
	        //se non sono nella pagina di login e se non sono loggato
	        if (!allowedPage && !isLogged) {
	        	addError(fc, (msg != null ? msg : "Autenticazione richiesta."));
	            NavigationHandler nh = fc.getApplication().getNavigationHandler();
	            nh.handleNavigation(fc, null, "login");
	        }
		}catch (Exception e) {
			LoginPhaseListener.log.error(e.getMessage(),e);
		}
	}

	private boolean isAllowedPage(String viewId){
		
		for (int i = 0; i < LoginPhaseListener.allowedPages.length; i++) {
			String page = LoginPhaseListener.allowedPages[i];
			//controllo se la pagina richiesta e' tra quelle permesse
			if(StringUtils.contains(viewId, page))
				return true;
		}
		return false;
	}
	
	@Override
	public void beforePhase(PhaseEvent event) {
		
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

	private void addError(FacesContext context, String message)  
	{  
		FacesMessage fMessage = new FacesMessage(message);  
		if (fMessage != null)  
		{  
			FacesContext facesContext = FacesContext.getCurrentInstance();  
			fMessage.setSeverity(FacesMessage.SEVERITY_ERROR);  
			facesContext.addMessage(null, fMessage);  
		}  
	}
	
}
