/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.pdd.services.connector;

import java.io.IOException;

import org.openspcoop2.utils.Utilities;

/**	
 * Contiene la definizione di una eccezione lanciata dai NodeReceiver e NodeSender
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class ConnectorException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	public ConnectorException() {
	}
	public ConnectorException(String msg) {
		super(msg);
	}
	
	public ConnectorException(Throwable cause) {
		super(cause);
	}

	public ConnectorException(String message, Throwable cause) {
		super(message, cause);
	}
	
	private String getInnerMessage(){
		try{
			 if(Utilities.existsInnerException(this, java.net.SocketException.class)){
				 Throwable t = Utilities.getInnerException(this, java.net.SocketException.class);
				 if(t!=null){
					 String msg = t.getMessage();
					 if(msg!=null && !"".equals(msg) && !"null".equals(msg)){
						 return msg;
					 }
				 }
			 }
			 else if(Utilities.existsInnerException(this, java.nio.channels.ClosedChannelException.class)){
				 Throwable t = Utilities.getInnerException(this, java.nio.channels.ClosedChannelException.class);
				 if(t!=null){
					 String msg = t.getMessage();
					 if(msg!=null && !"".equals(msg) && !"null".equals(msg)){
						 return msg;
					 }
				 }
			 }
			 else {
				 Throwable t = Utilities.getLastInnerException(this);
				 if(t!=null && t instanceof IOException) {
					 String msg = t.getMessage();
					 if(msg!=null && !"".equals(msg) && !"null".equals(msg)){
						 return msg;
					 }
				 }
			 }
		}catch(Throwable t){
			t.getMessage(); // per debug
		}
		return null;
	}
	
	 @Override
	public String getMessage() {
		String msg = this.getInnerMessage();
		if(msg!=null){
			return msg;
		}
		return super.getMessage();
	}
	@Override
	public String toString() {
		String msg = this.getInnerMessage();
		if(msg!=null){
			return msg;
		}
		return super.toString();
	}
}