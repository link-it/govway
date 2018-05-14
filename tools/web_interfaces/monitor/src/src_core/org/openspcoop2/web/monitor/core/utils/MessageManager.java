package org.openspcoop2.web.monitor.core.utils;

import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;

public class MessageManager {
	
	private static PddMonitorProperties props;
		
	public String getMessage(String key) throws Exception {

		// use standard JSF Resource Bundle mechanism
		//return getMessageFromJSFBundle(key);

		// use the default Java ResourceBund;e mechanism
		 return getMessageFromResourceBundle(key);
	}

	
	
	/**
	 * Inizializza la mappa delle property
	 * legge il default resource bundle 
	 * dopodiche effettua l'ovverride delle properties eventualmente definite all'esterno dell'applicazione
	 * utilizzando lo stesso algoritmo di lookup di openspcoop
	 * @throws Exception 
	 */
	private static void init() throws Exception{
		MessageManager.props = PddMonitorProperties.getInstance( LoggerManager.getPddMonitorCoreLogger());
	}
	
	private String getMessageFromResourceBundle(String key) throws Exception {
		String message = "";
		if(MessageManager.props==null) 
			MessageManager.init();
		
		message = MessageManager.props.getProperty(key,false,false);
		
		return message;

	}


	@SuppressWarnings("unused")
	private String getMessageFromJSFBundle(String key) {
		return (String)MessageManager.resolveExpression("#{msgbundle['" + key + "']}");
	}

	public static ClassLoader getCurrentLoader(Object fallbackClass) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null)
			loader = fallbackClass.getClass().getClassLoader();
		return loader;
	}

	// from JSFUtils in Oracle ADF 11g Storefront Demo
	public static Object resolveExpression(String expression) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Application app = facesContext.getApplication();
		ExpressionFactory elFactory = app.getExpressionFactory();
		ELContext elContext = facesContext.getELContext();
		ValueExpression valueExp =
			elFactory.createValueExpression(elContext, expression,
					Object.class);
		return valueExp.getValue(elContext);
	}
}
