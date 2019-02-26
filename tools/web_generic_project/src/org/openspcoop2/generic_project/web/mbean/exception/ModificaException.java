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
package org.openspcoop2.generic_project.web.mbean.exception;

import org.openspcoop2.generic_project.web.exception.BaseException;

/***
 * 
 * Eccezione che viene lanciata quando c'&egrave; un errore durante l'esecuzione della funzionalit&agrave; modifica.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public class ModificaException extends BaseException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ModificaException(){
		super();
		this.setResourceBundleKey("MODIFICA_ERROR");
	}

	public ModificaException(String msg){
		super(msg);
		this.setResourceBundleKey("MODIFICA_ERROR");
	}
	public ModificaException(Throwable t){
		super(t);
		this.setResourceBundleKey("MODIFICA_ERROR");
	}
	public ModificaException(String msg,Throwable t){ 
		super(msg, t);
		this.setResourceBundleKey("MODIFICA_ERROR");
	}

}
