package org.openspcoop2.web.monitor.core.utils;

import java.util.Locale;
import java.util.Properties;

import javax.faces.context.FacesContext;

import org.slf4j.Logger;

public class AbstractLocalePropertiesManager extends AbstractPropertiesManager{
	
	//private  HashMap<String, String> map = null;
	Properties map = null;
		
	/**
	 * Inizializza la mappa delle property
	 * legge il default resource bundle 'bundleName'
	 * dopodiche effettua l'ovverride delle properties eventualmente definite all'esterno dell'applicazione
	 * utilizzando lo stesso algoritmo di lookup di openspcoop
	 */
	protected AbstractLocalePropertiesManager(String bundleName,Logger log,String variabile,String nomeFile,String confDir){
		
		super(bundleName, log, variabile, nomeFile, confDir, getLocale());
		
	}
	
	private static Locale getLocale(){
		FacesContext ctx = FacesContext.getCurrentInstance();
		Locale locale = Locale.getDefault();
		if(ctx!=null){
			locale = ctx.getViewRoot().getLocale();
		}
		return locale;
	}
}
