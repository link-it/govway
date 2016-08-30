/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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



package org.openspcoop2.testsuite.core;

import org.openspcoop2.utils.Utilities;

/**
 * Eccezioni lanciate dalla TestSuite
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class TestSuiteException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String message;
	private String position;
	private Exception exception;
	public TestSuiteException(Exception e,String position){
		super(_getMessage(position,null,e),e);
		this.exception = e;
		this.position=position;
	}
	public TestSuiteException(String position,Exception e){
		super(_getMessage(position,null,e),e);
		this.exception = e;
	    this.position=position;
	}
	
	public TestSuiteException(String message){
		super(message);
		this.message=message;
	}
	
	public TestSuiteException(String msg,String pos){
		super(_getMessage(pos,msg,null));
		this.message=msg;
		this.position=pos;
	}

	
	@Override
	public String getMessage(){
		return _getMessage(this.position,this.message, this.exception);
	}
	private static String _getMessage(String position,String message,Exception exception){
		StringBuffer bf = new StringBuffer();
		
		if(position!=null){
			bf.append(position);
		}
		
		if(message!=null){
			if(bf.length()>0){
				bf.append(" - ");
			}
			bf.append(message);
		}
		else if(exception!=null){
			if(bf.length()>0){
				bf.append(" - ");
			}
			bf.append(Utilities.getInnerNotEmptyMessageException(exception).getMessage());
		}
		return bf.toString();
	}
}
