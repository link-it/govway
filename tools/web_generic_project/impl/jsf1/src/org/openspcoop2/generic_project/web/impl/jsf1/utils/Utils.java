/*
 * OpenSPCoop - Customizable API Gateway 
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
public class Utils extends org.openspcoop2.generic_project.web.core.Utils {

	private static Utils instance = null;

	public static Utils getInstance(){

		if(Utils.instance == null)
			init();


		return Utils.instance;
	}

	private synchronized static void init(){
		if(Utils.instance == null)
			Utils.instance = new Utils();
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
