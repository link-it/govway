/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

package org.openspcoop2.utils.rest;

import java.io.File;
import java.net.URI;

import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiSchema;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * IValidator
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IApiReader {
	
	public void init(Logger log,String content, ApiReaderConfig config) throws ProcessingException;
	public void init(Logger log,String content, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException;
	
	public void init(Logger log,byte[] content, ApiReaderConfig config) throws ProcessingException;
	public void init(Logger log,byte[] content, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException;
	
	public void init(Logger log,File file, ApiReaderConfig config) throws ProcessingException;
	public void init(Logger log,File file, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException;
	
	public void init(Logger log,URI uri, ApiReaderConfig config) throws ProcessingException;
	public void init(Logger log,URI uri, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException;
	
	public void init(Logger log,Document doc, ApiReaderConfig config) throws ProcessingException;
	public void init(Logger log,Document doc, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException;
	
	public void init(Logger log,Element element, ApiReaderConfig config) throws ProcessingException;
	public void init(Logger log,Element element, ApiReaderConfig config, ApiSchema ... schema) throws ProcessingException;
	
	public Api read() throws ProcessingException;
		
}
