/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.generic_project.web.impl.jsf1.utils;

import java.util.Locale;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;


/***
 * 
 * Utils Fornisce una serie di utilities.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Jsf1Utils extends org.openspcoop2.generic_project.web.core.Utils {

	private static Jsf1Utils instance = null;

	public static Jsf1Utils getInstance(){

		if(Jsf1Utils.instance == null)
			init();


		return Jsf1Utils.instance;
	}

	private synchronized static void init(){
		if(Jsf1Utils.instance == null)
			Jsf1Utils.instance = new Jsf1Utils();
	}

	@Override
	protected   ClassLoader getCurrentClassLoader(Object defaultObject){

		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		if(loader == null){
			loader = defaultObject.getClass().getClassLoader();
		}

		return loader;
	}

	@Override
	public Locale getLocale() {
		Locale locale = null;

		try{
			FacesContext currentInstance = FacesContext.getCurrentInstance();
			if(currentInstance != null){
				UIViewRoot viewRoot = currentInstance.getViewRoot();
				if(viewRoot != null)
					locale = viewRoot.getLocale();
			}
		}catch(Exception e){}

		if(locale == null){
			locale = Locale.ITALY;
		}

		return locale;
	}
	
}
