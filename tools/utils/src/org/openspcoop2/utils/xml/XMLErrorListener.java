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



package org.openspcoop2.utils.xml;

import java.io.PrintStream;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

/**
 * Classe utilizzabile per ottenere gli errori generati dal parser
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class XMLErrorListener implements ErrorListener {

	private boolean throwWarningException = true;
	private boolean throwErrorException = true;
	private boolean throwFatalErrorException = true;
	private boolean printStackTraceWarningException = false;
	private boolean printStackTraceErrorException = false;
	private boolean printStackTraceFatalErrorException = false;
	
	private PrintStream warningOutputStream = System.out;
	private PrintStream errorOutputStream = System.err;
	private PrintStream fatalErrorOutputStream = System.err;
	
	XMLErrorListener(){}
	XMLErrorListener(boolean throwWarningException,boolean throwErrorException,boolean throwFatalErrorException){
		this(throwWarningException,throwErrorException,throwFatalErrorException,false,false,false);
	}
	XMLErrorListener(boolean throwWarningException,boolean throwErrorException,boolean throwFatalErrorException,
			boolean printStackTraceWarningException,boolean printStackTraceErrorException, boolean printStackTraceFatalErrorException){
		
	}
	
	@Override
	public void warning(TransformerException exception)
			throws TransformerException {
		
		if(this.printStackTraceWarningException){
			this.warningOutputStream.append("WARNING: "+exception.getMessage());
			exception.printStackTrace(this.warningOutputStream);
		}
		if(this.throwWarningException)
			throw exception;	
		
	}
	@Override
	public void error(TransformerException exception)
			throws TransformerException {
		
		if(this.printStackTraceErrorException){
			this.errorOutputStream.append("ERROR: "+exception.getMessage());
			exception.printStackTrace(this.errorOutputStream);
		}
		if(this.throwErrorException)
			throw exception;
		
	}
	@Override
	public void fatalError(TransformerException exception)
			throws TransformerException {
		
		if(this.printStackTraceFatalErrorException){
			this.fatalErrorOutputStream.append("FATAL ERROR: "+exception.getMessage());
			exception.printStackTrace(this.fatalErrorOutputStream);
		}
		if(this.throwFatalErrorException)
			throw exception;
		
	}

}
