/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.dynamic;

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.utils.regexp.RegExpException;
import org.openspcoop2.utils.regexp.RegExpNotFoundException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.slf4j.Logger;

/**
 * URLRegExpExtractor
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class URLRegExpExtractor implements Serializable {

	private static final long serialVersionUID = 1L;

	private transient Logger log;
	
	private String url;
	
	public URLRegExpExtractor(String url, Logger log) {
		this.url = url;
		this.log = log;
	}
	
	public boolean match(String pattern) throws DynamicException {
		String v = read(pattern);
		return v!=null && !"".equals(v);
	}
	
	private String getMessaggioNotFound(String tipo,String pattern, RegExpNotFoundException e) {
		return "Estrazione ("+tipo+") '"+pattern+"' non ha trovato risultati: "+e.getMessage();
	}
	private String getMessaggioErrore(String tipo,String pattern, Exception e) {
		return "Estrazione ("+tipo+") '"+pattern+"' fallita: "+e.getMessage();
	}
	
	public String read(String pattern) throws DynamicException {
		String valore = null;
		try {
			valore = RegularExpressionEngine.getStringMatchPattern(this.url, pattern);
		}
		catch(RegExpNotFoundException e){
			this.log.debug(getMessaggioNotFound("read",pattern, e),e);
		}
		catch(RegExpException e){
			throw new DynamicException(e.getMessage(),e);
		}
		catch(Exception e){
			throw new DynamicException(getMessaggioErrore("read", pattern, e),e);
		}
		return valore;
	}
	
	public List<String> readList(String pattern) throws DynamicException {
		List<String> valore = null;
		try {
			valore = RegularExpressionEngine.getAllStringMatchPattern(this.url, pattern);
		}
		catch(RegExpNotFoundException e){
			this.log.debug(getMessaggioNotFound("readList",pattern, e),e);
		}
		catch(RegExpException e){
			throw new DynamicException(e.getMessage(),e);
		}
		catch(Exception e){
			throw new DynamicException(getMessaggioErrore("readList", pattern, e),e);
		}
		return valore;
	}
	
	
	
	public boolean found(String pattern) throws DynamicException {
		String v = find(pattern);
		return v!=null && !"".equals(v);
	}
	
	public String find(String pattern) throws DynamicException {
		String valore = null;
		try {
			valore = RegularExpressionEngine.getStringFindPattern(this.url, pattern);
		}
		catch(RegExpNotFoundException e){
			this.log.debug(getMessaggioNotFound("find",pattern, e),e);
		}
		catch(RegExpException e){
			throw new DynamicException(e.getMessage(),e);
		}
		catch(Exception e){
			throw new DynamicException(getMessaggioErrore("find", pattern, e),e);
		}
		return valore;
	}
	
	public List<String> findAll(String pattern) throws DynamicException {
		List<String> valore = null;
		try {
			valore = RegularExpressionEngine.getAllStringFindPattern(this.url, pattern);
		}
		catch(RegExpNotFoundException e){
			this.log.debug(getMessaggioNotFound("findAll",pattern, e),e);
		}
		catch(RegExpException e){
			throw new DynamicException(e.getMessage(),e);
		}
		catch(Exception e){
			throw new DynamicException(getMessaggioErrore("findAll", pattern, e),e);
		}
		return valore;
	}
}
