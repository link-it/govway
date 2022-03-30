/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.message.exception;

import org.openspcoop2.utils.LimitExceededIOException;
import org.openspcoop2.utils.LimitedInputStream;
import org.openspcoop2.utils.TimeoutIOException;
import org.openspcoop2.utils.TimeoutInputStream;
import org.openspcoop2.utils.Utilities;

import com.ctc.wstx.exc.WstxException;


/**
 * ParseExceptionUtils
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ParseExceptionUtils {

		
	// Parse-Exception
	
	public static ParseException buildParseException(Throwable e){
		
		ParseException pe = new ParseException();
		pe.setSourceException(e);
		
		if(e==null){
			pe.setParseException(new Exception("Occurs Parsing Error"));
			pe.setSourceException(new Exception("Occurs Parsing Error"));
			return pe;
		}
		
		if(TimeoutIOException.isTimeoutIOException(e) || Utilities.existsInnerMessageException(e, TimeoutInputStream.ERROR_MSG, true)) {
			
			Throwable timeoutException = null;
			if(e instanceof TimeoutIOException) {
				timeoutException = e;
			}
			else {
				timeoutException = Utilities.getInnerInstanceException(e, TimeoutIOException.class, false);
			}
			if(timeoutException==null && Utilities.existsInnerMessageException(e, TimeoutInputStream.ERROR_MSG, true)) {
				Throwable exceptionMessageTimeout = Utilities.getInnerMessageException(e, TimeoutInputStream.ERROR_MSG, true);
				if(exceptionMessageTimeout!=null) {
					timeoutException = new TimeoutIOException(exceptionMessageTimeout.getMessage(), exceptionMessageTimeout);
				}
			}
			if(timeoutException!=null) {
				pe.setParseException(timeoutException);
				pe.setSourceException(timeoutException);
				return pe;
			}
		}
		
		if(LimitExceededIOException.isLimitExceededIOException(e) || Utilities.existsInnerMessageException(e, LimitedInputStream.ERROR_MSG, true)) {
			
			Throwable limitedException = null;
			if(e instanceof LimitExceededIOException) {
				limitedException = e;
			}
			else {
				limitedException = Utilities.getInnerInstanceException(e, LimitExceededIOException.class, false);
			}
			if(limitedException==null && Utilities.existsInnerMessageException(e, LimitedInputStream.ERROR_MSG, true)) {
				Throwable exceptionMessageLimitExceeded = Utilities.getInnerMessageException(e, LimitedInputStream.ERROR_MSG, true);
				if(exceptionMessageLimitExceeded!=null) {
					limitedException = new LimitExceededIOException(exceptionMessageLimitExceeded.getMessage(), exceptionMessageLimitExceeded);
				}
			}
			if(limitedException!=null) {
				pe.setParseException(limitedException);
				pe.setSourceException(limitedException);
				return pe;
			}
		}
		
		Throwable tmp = ParseExceptionUtils.getParseException(e);
		if(tmp!=null){
			pe.setParseException(tmp);
			return pe;
		}
		
		pe.setParseException(getInnerNotEmptyMessageException(e));
		return pe;
	}
	
	public static Throwable getParseException(Throwable e){
		
		// Prima verifico presenza di eccezioni che sicuramente non evidenziano problemi di parsing
		if(e instanceof java.net.SocketException){
			return null;
		}
		else if(Utilities.existsInnerException(e, java.net.SocketException.class)){
			return null;
		}
		
		
		// Cerco eccezione di parsing
		
		boolean found = false;
		
		Throwable tmp = null;		
				
		if(tmp==null){
			if(Utilities.isExceptionInstanceOf("org.apache.axiom.om.OMException", e)){
				tmp = e;
			}
			else if(Utilities.existsInnerException(e, "org.apache.axiom.om.OMException")){
				tmp = Utilities.getInnerException(e, "org.apache.axiom.om.OMException");
			}
			if(tmp!=null){
				if( ! (tmp.getMessage()!=null && !"".equals(tmp.getMessage()) && !"null".equalsIgnoreCase(tmp.getMessage())) ){
					// cerco prossima eccezione, in questa c'è null come message
					tmp = null;
					found = true;
				}
			}
		}
		
		if(tmp==null){
			if(e instanceof WstxException){
				tmp = e;
			}
			else if(Utilities.existsInnerException(e, WstxException.class)){
				tmp = Utilities.getInnerException(e, WstxException.class);
			}
			if(tmp!=null){
				if( ! (tmp.getMessage()!=null && !"".equals(tmp.getMessage()) && !"null".equalsIgnoreCase(tmp.getMessage())) ){
					// cerco prossima eccezione, in questa c'è null come message
					tmp = null;
					found = true;
				}
			}
		}
		
		if(tmp==null){
			if(Utilities.isExceptionInstanceOf("com.ctc.wstx.exc.WstxIOException", e)){
				tmp = e;
			}
			else if(Utilities.existsInnerException(e, "com.ctc.wstx.exc.WstxIOException")){
				tmp = Utilities.getInnerException(e, "com.ctc.wstx.exc.WstxIOException");
			}
			if(tmp!=null){
				if( ! (tmp.getMessage()!=null && !"".equals(tmp.getMessage()) && !"null".equalsIgnoreCase(tmp.getMessage())) ){
					// cerco prossima eccezione, in questa c'è null come message
					tmp = null;
					found = true;
				}
			}
		}
		
		if(tmp==null){
			if(Utilities.isExceptionInstanceOf("com.ctc.wstx.exc.WstxParsingException", e)){
				tmp = e;
			}
			else if(Utilities.existsInnerException(e, "com.ctc.wstx.exc.WstxParsingException")){
				tmp = Utilities.getInnerException(e, "com.ctc.wstx.exc.WstxParsingException");
			}
			if(tmp!=null){
				if( ! (tmp.getMessage()!=null && !"".equals(tmp.getMessage()) && !"null".equalsIgnoreCase(tmp.getMessage())) ){
					// cerco prossima eccezione, in questa c'è null come message
					tmp = null;
					found = true;
				}
			}
		}
		
		if(tmp==null){
			if(Utilities.isExceptionInstanceOf("com.ctc.wstx.exc.WstxUnexpectedCharException", e)){
				tmp = e;
			}
			else if(Utilities.existsInnerException(e, "com.ctc.wstx.exc.WstxUnexpectedCharException")){
				tmp = Utilities.getInnerException(e, "com.ctc.wstx.exc.WstxUnexpectedCharException");
			}
			if(tmp!=null){
				if( ! (tmp.getMessage()!=null && !"".equals(tmp.getMessage()) && !"null".equalsIgnoreCase(tmp.getMessage())) ){
					// cerco prossima eccezione, in questa c'è null come message
					tmp = null;
					found = true;
				}
			}
		}
		
		if(tmp==null){
			if(Utilities.isExceptionInstanceOf("org.xml.sax.SAXParseException", e)){
				tmp = e;
			}
			else if(Utilities.existsInnerException(e, "org.xml.sax.SAXParseException")){
				tmp = Utilities.getInnerException(e, "org.xml.sax.SAXParseException");
			}
			if(tmp!=null){
				if( ! (tmp.getMessage()!=null && !"".equals(tmp.getMessage()) && !"null".equalsIgnoreCase(tmp.getMessage())) ){
					// cerco prossima eccezione, in questa c'è null come message
					tmp = null;
					found = true;
				}
			}
		}
	
		if(tmp==null){
			if(Utilities.isExceptionInstanceOf("javax.xml.stream.XMLStreamException", e)){
				tmp = e;
			}
			else if(Utilities.existsInnerException(e, "javax.xml.stream.XMLStreamException")){
				tmp = Utilities.getInnerException(e, "javax.xml.stream.XMLStreamException");
			}
			if(tmp!=null){
				if( ! (tmp.getMessage()!=null && !"".equals(tmp.getMessage()) && !"null".equalsIgnoreCase(tmp.getMessage())) ){
					// cerco prossima eccezione, in questa c'è null come message
					tmp = null;
					found = true;
				}
			}
		}
		
		if(tmp!=null){
			return tmp;
		}
		if(found){
			return getInnerNotEmptyMessageException(e);
		}
		return null;
		
	}
	
	public static boolean isEmpytMessageException(Throwable e){
		if(e.getMessage()==null ||
				"".equals(e.getMessage()) || 
				"null".equalsIgnoreCase(e.getMessage()) || 
				"com.ctc.wstx.exc.WstxIOException: null".equalsIgnoreCase(e.getMessage())){
			return true;
		}
		else{
			return false;
		}
	}
	
	public static Throwable getInnerNotEmptyMessageException(Throwable e){
		if(!isEmpytMessageException(e)){
			return e;
		}
		
		if(e.getCause()!=null){
			//System.out.println("INNER ["+e.getClass().getName()+"]...");
			return getInnerNotEmptyMessageException(e.getCause());
		}
		else{
			return e; // sono nella foglia, ritorno comunque questa eccezione
		}
	}

}
