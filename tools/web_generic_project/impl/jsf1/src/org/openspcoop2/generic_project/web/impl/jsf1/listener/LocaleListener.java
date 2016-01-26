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

import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.openspcoop2.generic_project.web.impl.jsf1.mbean.LoginBean;

/****
* LocaleListener Filtro JSF per mantenere correttamente impostata la lingua scelta dall'utente nell'interfaccia.
* 
* @author Pintori Giuliano (pintori@link.it)
* @author $Author$
* @version $Rev$, $Date$
 *
 */
public class LocaleListener implements PhaseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4570951716292644070L;

	@Override
	public void afterPhase(PhaseEvent event) {
	}

	@Override
	public void beforePhase(PhaseEvent event) {
		if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			try{
				FacesContext fc = event.getFacesContext();

				ExternalContext ec = fc.getExternalContext();
				LoginBean lb = (LoginBean)ec.getSessionMap().get("loginBean");

				UIViewRoot vr = fc.getViewRoot();

				// Set del locale corrente
				if(vr!= null && lb != null)
					vr.setLocale(lb.getCurrentLocal());


			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}
}
