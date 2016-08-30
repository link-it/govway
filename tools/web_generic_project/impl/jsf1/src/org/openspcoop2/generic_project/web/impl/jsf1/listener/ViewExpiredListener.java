/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/***
 * 
 * Controlla la validita' della vista richiesta dall'utente.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public class ViewExpiredListener  implements PhaseListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerWrapperFactory.getLogger(ViewExpiredListener.class);

	@Override
	public void beforePhase(PhaseEvent event) {
		FacesContext facesCtx = event.getFacesContext();
		ExternalContext extCtx = facesCtx.getExternalContext();
		HttpSession session = (HttpSession)extCtx.getSession(false);
		String vR = facesCtx.getViewRoot() != null ? facesCtx.getViewRoot().getId() : "";
		boolean newSession = (session == null)|| (session.isNew());
		boolean postback = !extCtx.getRequestParameterMap().isEmpty();
		boolean timedout = postback && newSession;
		
		log.debug("La View richiesta e' non valida: ["+timedout+"]"); 
		if(timedout) {
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

