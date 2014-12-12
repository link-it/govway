/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
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

import javax.xml.soap.SOAPFault;

/**
 * Eccezioni lanciate dalla TestSuite
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@SuppressWarnings("serial")
public class TestSuiteException extends RuntimeException {
	
	public final static String fault="FaultMessage";
	
	String message;
	String position;
	String code;
	SOAPFault faultSOAP;
	public TestSuiteException(Exception e,String str){
	    this.message=e.getMessage();
	    this.position=str;
	    //setLog();
	}
	
	public TestSuiteException(String code){
		this.code=code;
	}
	
	public TestSuiteException(String msg,String pos){
		this.message=msg;
		this.position=pos;
	}
/*	private void setLog(){
	//	ClientWS.log.warn("nella posizione "+position+" si e' verificato un' eccezione    :"+ message);
	}*/
    

	public String getCode(){
		return this.code;
	}
	
	@Override
	public String getMessage(){
		if(this.code!=null)return this.code;
		return this.position+"         "+this.message;
	}
}
