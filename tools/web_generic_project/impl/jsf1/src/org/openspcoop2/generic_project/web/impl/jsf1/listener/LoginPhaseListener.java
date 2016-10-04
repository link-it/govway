/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.generic_project.web.impl.jsf1.listener;

import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.generic_project.web.impl.jsf1.filter.PrincipalFilter;
import org.openspcoop2.generic_project.web.impl.jsf1.mbean.LoginBean;
import org.openspcoop2.generic_project.web.impl.jsf1.utils.Utils;


/**
* LoginPhaseListener controlla se le pagine richieste dall'utente sono visualizzabili anche senza autenticazione. 
* 
* @author Pintori Giuliano (pintori@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class LoginPhaseListener implements PhaseListener {

	private static final long serialVersionUID = -7545768911858875216L;
	
	private static String[] allowedPages = {"login","timeout","error"};
		
	@Override
	public void afterPhase(PhaseEvent event) {
		try{

			FacesContext fc = event.getFacesContext();
			
			ExternalContext ec = fc.getExternalContext();
			LoginBean lb = (LoginBean)ec.getSessionMap().get("loginBean");
	        //controllo se sono nella pagina di login
			boolean isLogged = lb == null ? false : lb.getIsLoggedIn();
			
			UIViewRoot vr = fc.getViewRoot();
			
			// Set del locale corrente
			if(vr!= null && lb != null)
				vr.setLocale(lb.getCurrentLocal());
			//se il viewroot e' == null allora vuol dire che ho perso tutte le informazioni
			//sullo stato jsf quindi devo riautenticarmi
	        boolean allowedPage = vr!=null ? isAllowedPage(vr.getViewId()) : false;
	        
	        //Controllo se sto andando alla pagina di errore e se c'e' un errore da visualizzare
	        if(allowedPage && StringUtils.contains(vr.getViewId(), "error")){
	        	String msg = (String) ec.getSessionMap().get(PrincipalFilter.PRINCIPAL_ERROR_MSG);
	        	if(msg != null){
	        		// 
	        		ec.getRequestMap().put(PrincipalFilter.PRINCIPAL_SHOW_FORM, "true");
	        		
	        		// rimozione messaggio
	        		ec.getSessionMap().remove(PrincipalFilter.PRINCIPAL_ERROR_MSG);
	        		// stampa messaggi
	        		addError(fc, msg);
	        	}
	        }
	        
	        //se non sono nella pagina di login e se non sono loggato
	        if (!allowedPage && !isLogged) {
	        	addError(fc, Utils.getInstance().getMessageFromResourceBundle("login.form.autenticazioneRichiesta", "commonsMessages"));
	            NavigationHandler nh = fc.getApplication().getNavigationHandler();
	            nh.handleNavigation(fc, null, "login");
	        }
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isAllowedPage(String viewId){
		
		for (int i = 0; i < allowedPages.length; i++) {
			String page = allowedPages[i];
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
