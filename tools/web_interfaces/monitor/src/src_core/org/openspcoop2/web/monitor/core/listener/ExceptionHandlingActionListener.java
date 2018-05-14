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
