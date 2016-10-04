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
package org.openspcoop2.generic_project.web.exception;

import org.openspcoop2.generic_project.web.factory.FactoryException;
import org.openspcoop2.generic_project.web.factory.WebGenericProjectFactoryManager;


/***
 * 
 * Classe Base che definisce delle eccezioni con dei messaggi userfriendly.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public class BaseException extends Exception {

	private String resourceBundleKey = null;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

	public BaseException(){
		super();
		this.resourceBundleKey = null;
	}

	public BaseException(String msg){
		super(msg);
		this.resourceBundleKey = null;
	}
	public BaseException(Throwable t){
		super(t);
		this.resourceBundleKey = null;
	}
	public BaseException(String msg,Throwable t){ 
		super(msg, t);
		this.resourceBundleKey = null;
	}

	@Override
	public String getMessage() {
		StringBuilder sb =  new StringBuilder();
		try {
			
			sb.append(WebGenericProjectFactoryManager.getInstance().getDefaultFactory().getUtils().getMessageFromCommonsResourceBundle(this.resourceBundleKey));

			if(sb.length() > 0)
				sb.append(": ");

			String tmp  = WebGenericProjectFactoryManager.getInstance().getDefaultFactory().getUtils().getMessageFromResourceBundle(super.getMessage());


			if(tmp != null && !tmp.startsWith("?? key ") && !tmp.endsWith(" not found ??"))
				sb.append(tmp);
			else 
				sb.append(super.getMessage());

			
		} catch (FactoryException e) {
			sb =  new StringBuilder();
			sb.append(super.getMessage());
		}
		
		return sb.toString();
	}

	public String getResourceBundleKey() {
		return this.resourceBundleKey;
	}

	public void setResourceBundleKey(String resourceBundleKey) {
		this.resourceBundleKey = resourceBundleKey;
	}
}
