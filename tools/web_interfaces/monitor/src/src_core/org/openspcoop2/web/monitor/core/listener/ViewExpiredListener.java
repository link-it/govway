package org.openspcoop2.web.monitor.core.listener;

import javax.faces.application.Application;
import javax.faces.application.ViewExpiredException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;

import org.openspcoop2.web.monitor.core.logger.LoggerManager;

/***
 * 
 * Filtro che gestisce l'eccezione di tipo javax.faces.application.ViewExpiredException
 * 
 * 
 * @author pintori
 *
 */
public class ViewExpiredListener  implements PhaseListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerManager.getPddMonitorCoreLogger();

	@Override
	public void beforePhase(PhaseEvent event) {
		FacesContext facesCtx = event.getFacesContext();
		ExternalContext extCtx = facesCtx.getExternalContext();
		HttpSession session = (HttpSession)extCtx.getSession(false);
		String vR = facesCtx.getViewRoot() != null ? facesCtx.getViewRoot().getId() : "";
		boolean newSession = (session == null)|| (session.isNew());
		boolean postback = !extCtx.getRequestParameterMap().isEmpty();
		boolean timedout = postback && newSession;
		
		
//		log.debug("La View richiesta e' valida: ["+(!timedout)+"]"); 
		if(timedout) {
			ViewExpiredListener.log.debug("La View richiesta e' non valida: ["+timedout+"]"); 
			Application app = facesCtx.getApplication();
			ViewHandler viewHandler = app.getViewHandler();
			UIViewRoot view = viewHandler.createView(facesCtx,"/public/timeoutPage.jsf");
			facesCtx.setViewRoot(view);
			facesCtx.renderResponse();
			try {
				viewHandler.renderView(facesCtx, view);
				facesCtx.responseComplete();
			} catch(Throwable t) {
				
				throw new ViewExpiredException("View " + vR +" could not be restored.",view.getViewId()); 
			}
		}
	}

	@Override
	public void afterPhase(PhaseEvent event) {

	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}
} 

