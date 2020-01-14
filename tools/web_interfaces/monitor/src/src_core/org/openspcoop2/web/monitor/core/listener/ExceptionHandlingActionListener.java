/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import com.sun.faces.application.ActionListenerImpl;

/**
 * ExceptionHandlingActionListener
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ExceptionHandlingActionListener extends ActionListenerImpl
		implements ActionListener {

	@Override
	public void processAction(ActionEvent ae) {
		try{
			super.processAction(ae);
		}catch (Exception e) {
			
			
			
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			
			
			FacesMessage fm = new FacesMessage();
			fm.setSummary(e.getMessage());
			fm.setDetail(sw.toString());
			fm.setSeverity(FacesMessage.SEVERITY_FATAL);
			
			try{
				pw.close();
				sw.close();
			}catch (Exception ex) {
				//ignore
			}
			
		    FacesContext facesContext = FacesContext.getCurrentInstance();
		    Application application = facesContext.getApplication();
		    
		    facesContext.addMessage("GE", fm);
		    
		    NavigationHandler navigationHandler =  application.getNavigationHandler();
		    navigationHandler.handleNavigation(facesContext, null,"exceptionOccured");
		    facesContext.renderResponse();
		}
	}
}
