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



package org.openspcoop2.utils;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**	
 * Contiene la definizione di una eccezione lanciata dalle classi del package org.openspcoop.utils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class UtilsMultiException extends Exception {

	private List<Throwable> exceptions = new ArrayList<>();
	
	public List<Throwable> getExceptions() {
		return this.exceptions;
	}
	
	public UtilsMultiException(String message, Throwable cause)
	{
		super(message, cause);
		this.exceptions.add(cause);
	}
	public UtilsMultiException(Throwable cause)
	{
		super(cause);
		this.exceptions.add(cause);
	}
	public UtilsMultiException(String message, Throwable ... cause)
	{
		super(message, (cause!=null && cause.length>0) ? cause[0] :  null);
		if(cause!=null && cause.length>0) {
			for (int i = 0; i < cause.length; i++) {
				this.exceptions.add(cause[i]);		
			}
		}
	}
	public UtilsMultiException(Throwable ...cause)
	{
		super((cause!=null && cause.length>0) ? cause[0] :  null);
		if(cause!=null && cause.length>0) {
			for (int i = 0; i < cause.length; i++) {
				this.exceptions.add(cause[i]);		
			}
		}
	}
	
	@Override
	public void printStackTrace(PrintStream s) {
		if(this.exceptions!=null && !this.exceptions.isEmpty()) {
			//for (int i = 0; i < this.exceptions.size(); i++) {
			for (int i = (this.exceptions.size()-1); i >= 0; i--) {
				if(i>0) {
					s.print("\n");
				}
				s.print("MultiException at position "+(i+1)+": \n");
				this.exceptions.get(i).printStackTrace(s);
			}
		}
		else {
			super.printStackTrace(s);
		}
	}

	@Override
	public void printStackTrace(PrintWriter s) {
		if(this.exceptions!=null && !this.exceptions.isEmpty()) {
			//for (int i = 0; i < this.exceptions.size(); i++) {
			for (int i = (this.exceptions.size()-1); i >= 0; i--) {
				if(i>0) {
					s.print("\n");
				}
				s.print("MultiException at position "+(i+1)+": \n");
				this.exceptions.get(i).printStackTrace(s);
			}
		}
		else {
			super.printStackTrace(s);
		}
	}
	
	@Override
	public StackTraceElement[] getStackTrace() {
		if(this.exceptions!=null && !this.exceptions.isEmpty()) {
			List<StackTraceElement> listStackTraceElement = new ArrayList<>();
			//for (int i = 0; i < this.exceptions.size(); i++) {
			for (int i = (this.exceptions.size()-1); i >= 0; i--) {
				StackTraceElement sElement = new StackTraceElement(UtilsMultiException.class.getName(), "Position_"+(i+1), "MultiException_"+(i+1), (i+1));
				listStackTraceElement.add(sElement);
				StackTraceElement[] tmp = this.exceptions.get(i).getStackTrace();
				if(tmp!=null && tmp.length>0) {
					for (int j = 0; j < tmp.length; j++) {
						listStackTraceElement.add(tmp[j]);		
					}
				}
			}
			return listStackTraceElement.toArray(new StackTraceElement[1]);
		}
		else {
			return super.getStackTrace();
		}
	}

	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public UtilsMultiException() {
		super();
	}
	public UtilsMultiException(String msg) {
		super(msg);
	}
}

