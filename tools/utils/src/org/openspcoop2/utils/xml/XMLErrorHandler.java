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



package org.openspcoop2.utils.xml;

import java.io.PrintStream;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Classe utilizzabile per ottenere gli errori generati dal parser
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class XMLErrorHandler implements ErrorHandler {

	private boolean throwWarningException = true;
	private boolean throwErrorException = true;
	private boolean throwFatalErrorException = true;
	private boolean printStackTraceWarningException = false;
	private boolean printStackTraceErrorException = false;
	private boolean printStackTraceFatalErrorException = false;
	
	private PrintStream warningOutputStream = System.out;
	private PrintStream errorOutputStream = System.err;
	private PrintStream fatalErrorOutputStream = System.err;
	
	XMLErrorHandler(){}
	XMLErrorHandler(boolean throwWarningException,boolean throwErrorException,boolean throwFatalErrorException){
		this(throwWarningException,throwErrorException,throwFatalErrorException,false,false,false);
	}
	XMLErrorHandler(boolean throwWarningException,boolean throwErrorException,boolean throwFatalErrorException,
			boolean printStackTraceWarningException,boolean printStackTraceErrorException, boolean printStackTraceFatalErrorException){
		this.throwWarningException = throwWarningException;
		this.throwErrorException = throwErrorException;
		this.throwFatalErrorException = throwFatalErrorException;
		this.printStackTraceWarningException = printStackTraceWarningException;
		this.printStackTraceErrorException = printStackTraceErrorException;
		this.printStackTraceFatalErrorException = printStackTraceFatalErrorException;
	}
	
	@Override
	public void warning(SAXParseException exception) throws SAXException {
		if(this.printStackTraceWarningException){
			this.warningOutputStream.append("WARNING: "+exception.getMessage());
			exception.printStackTrace(this.warningOutputStream);
		}
		if(this.throwWarningException)
			throw exception;
	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
		if(this.printStackTraceErrorException){
			this.errorOutputStream.append("ERROR: "+exception.getMessage());
			exception.printStackTrace(this.errorOutputStream);
		}
		if(this.throwErrorException)
			throw exception;
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		if(this.printStackTraceFatalErrorException){
			this.fatalErrorOutputStream.append("FATAL ERROR: "+exception.getMessage());
			exception.printStackTrace(this.fatalErrorOutputStream);
		}
		if(this.throwFatalErrorException)
			throw exception;
	}

}
