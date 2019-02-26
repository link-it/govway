 /*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.generic_project.web.impl.jsf1.listener;


import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.openspcoop2.generic_project.web.impl.jsf1.mbean.LoginBean;


/**
* HttpSessionCheckListener Fornisce il supporto per il controllo dello stato della sessione dell'utente.
* 
* @author Pintori Giuliano (pintori@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class HttpSessionCheckListener implements HttpSessionListener {

//	private static Logger log = LoggerManager.getGUILogger();
	
	@Override
	public void sessionCreated(HttpSessionEvent e) {
//		log.debug("session "+e.getSession().getId()+" created.");
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent e) {
//		log.debug("session "+e.getSession().getId()+" destroyed.");
		try{
			cleanUp(e.getSession());
		}catch (Exception ex) {
//			log.error("errore durante le operazioni di clean-up",ex);
		}
	}
	
	private void cleanUp(HttpSession session) throws Exception{
		//recuper utente dalla sessione se esiste
		LoginBean lb = (LoginBean)session.getAttribute("loginBean");
		if(lb!=null){
//			log.debug("remove user "+lb.getUsername()+" from session");
			session.setAttribute("loginBean", null);
		}else{
//			log.debug("no login info found in session, nothing to do.");
		}
	}
	
}
