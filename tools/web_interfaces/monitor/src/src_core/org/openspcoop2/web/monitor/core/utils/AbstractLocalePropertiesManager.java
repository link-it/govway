/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.utils;

import java.util.Locale;
import java.util.Properties;

import javax.faces.context.FacesContext;

import org.slf4j.Logger;

/**
 * AbstractLocalePropertiesManager
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
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
