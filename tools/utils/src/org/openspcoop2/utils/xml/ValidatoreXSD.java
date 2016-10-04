/*
 * OpenSPCoop - Customizable API Gateway 
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

import java.io.File;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.validation.Schema;

import org.slf4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * ValidatoreXSD
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ValidatoreXSD extends AbstractValidatoreXSD {

	@Override
	public byte[] getAsByte(Node nodeXML) throws XMLException {
		try{
			return XMLUtils.getInstance().toByteArray(nodeXML);
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}
	

	public ValidatoreXSD(Logger log, File... file) throws Exception {
		super(log, file);
		
	}

	public ValidatoreXSD(Logger log, File file) throws Exception {
		super(log, file);
		
	}

	public ValidatoreXSD(Logger log, InputStream... inputStream)
			throws Exception {
		super(log, inputStream);
		
	}

	public ValidatoreXSD(Logger log, InputStream inputStream) throws Exception {
		super(log, inputStream);
		
	}

	public ValidatoreXSD(Logger log, LSResourceResolver lsResourceResolver,
			File... file) throws Exception {
		super(log, lsResourceResolver, file);
		
	}

	public ValidatoreXSD(Logger log, LSResourceResolver lsResourceResolver,
			File file) throws Exception {
		super(log, lsResourceResolver, file);
		
	}

	public ValidatoreXSD(Logger log, LSResourceResolver lsResourceResolver,
			InputStream... inputStream) throws Exception {
		super(log, lsResourceResolver, inputStream);
		
	}

	public ValidatoreXSD(Logger log, LSResourceResolver lsResourceResolver,
			InputStream inputStream) throws Exception {
		super(log, lsResourceResolver, inputStream);
		
	}

	public ValidatoreXSD(Logger log, LSResourceResolver lsResourceResolver,
			Node... schema) throws Exception {
		super(log, lsResourceResolver, schema);
		
	}

	public ValidatoreXSD(Logger log, LSResourceResolver lsResourceResolver,
			Node schema) throws Exception {
		super(log, lsResourceResolver, schema);
		
	}

	public ValidatoreXSD(Logger log, LSResourceResolver lsResourceResolver,
			Source... source) throws Exception {
		super(log, lsResourceResolver, source);
		
	}

	public ValidatoreXSD(Logger log, LSResourceResolver lsResourceResolver,
			Source source) throws Exception {
		super(log, lsResourceResolver, source);
		
	}

	public ValidatoreXSD(Logger log, LSResourceResolver lsResourceResolver,
			String... url) throws Exception {
		super(log, lsResourceResolver, url);
		
	}

	public ValidatoreXSD(Logger log, LSResourceResolver lsResourceResolver,
			String url) throws Exception {
		super(log, lsResourceResolver, url);
		
	}

	public ValidatoreXSD(Logger log, Node... schema) throws Exception {
		super(log, schema);
		
	}

	public ValidatoreXSD(Logger log, Node schema) throws Exception {
		super(log, schema);
		
	}

	public ValidatoreXSD(Logger log, Source... source) throws Exception {
		super(log, source);
		
	}

	public ValidatoreXSD(Logger log, Source source) throws Exception {
		super(log, source);
		
	}

	public ValidatoreXSD(Logger log, String schemaFactory, File... file)
			throws Exception {
		super(log, schemaFactory, file);
		
	}

	public ValidatoreXSD(Logger log, String schemaFactory, File file)
			throws Exception {
		super(log, schemaFactory, file);
		
	}

	public ValidatoreXSD(Logger log, String schemaFactory,
			InputStream... inputStream) throws Exception {
		super(log, schemaFactory, inputStream);
		
	}

	public ValidatoreXSD(Logger log, String schemaFactory,
			InputStream inputStream) throws Exception {
		super(log, schemaFactory, inputStream);
		
	}

	public ValidatoreXSD(Logger log, String schemaFactory,
			LSResourceResolver lsResourceResolver, File... file)
			throws Exception {
		super(log, schemaFactory, lsResourceResolver, file);
		
	}

	public ValidatoreXSD(Logger log, String schemaFactory,
			LSResourceResolver lsResourceResolver, File file) throws Exception {
		super(log, schemaFactory, lsResourceResolver, file);
		
	}

	public ValidatoreXSD(Logger log, String schemaFactory,
			LSResourceResolver lsResourceResolver, InputStream... inputStream)
			throws Exception {
		super(log, schemaFactory, lsResourceResolver, inputStream);
		
	}

	public ValidatoreXSD(Logger log, String schemaFactory,
			LSResourceResolver lsResourceResolver, InputStream inputStream)
			throws Exception {
		super(log, schemaFactory, lsResourceResolver, inputStream);
		
	}

	public ValidatoreXSD(Logger log, String schemaFactory,
			LSResourceResolver lsResourceResolver, Node... schema)
			throws Exception {
		super(log, schemaFactory, lsResourceResolver, schema);
		
	}

	public ValidatoreXSD(Logger log, String schemaFactory,
			LSResourceResolver lsResourceResolver, Node schema)
			throws Exception {
		super(log, schemaFactory, lsResourceResolver, schema);
		
	}

	public ValidatoreXSD(Logger log, String schemaFactory,
			LSResourceResolver lsResourceResolver, Source... source)
			throws Exception {
		super(log, schemaFactory, lsResourceResolver, source);
		
	}

	public ValidatoreXSD(Logger log, String schemaFactory,
			LSResourceResolver lsResourceResolver, Source source)
			throws Exception {
		super(log, schemaFactory, lsResourceResolver, source);
		
	}

	public ValidatoreXSD(Logger log, String schemaFactory,
			LSResourceResolver lsResourceResolver, String... url)
			throws Exception {
		super(log, schemaFactory, lsResourceResolver, url);
		
	}

	public ValidatoreXSD(Logger log, String schemaFactory,
			LSResourceResolver lsResourceResolver, String url) throws Exception {
		super(log, schemaFactory, lsResourceResolver, url);
		
	}

	public ValidatoreXSD(Logger log, String schemaFactory, Node... schema)
			throws Exception {
		super(log, schemaFactory, schema);
		
	}

	public ValidatoreXSD(Logger log, String schemaFactory, Node schema)
			throws Exception {
		super(log, schemaFactory, schema);
		
	}

	public ValidatoreXSD(Logger log, String schemaFactory, Source... source)
			throws Exception {
		super(log, schemaFactory, source);
		
	}

	public ValidatoreXSD(Logger log, String schemaFactory, Source source)
			throws Exception {
		super(log, schemaFactory, source);
		
	}

	public ValidatoreXSD(Logger log, String schemaFactory, String... url)
			throws Exception {
		super(log, schemaFactory, url);
		
	}

	public ValidatoreXSD(Logger log, String schemaFactory, String url)
			throws Exception {
		super(log, schemaFactory, url);
		
	}

	public ValidatoreXSD(Logger log, String... url) throws Exception {
		super(log, url);
		
	}

	public ValidatoreXSD(Logger log, String url) throws Exception {
		super(log, url);
		
	}

	public ValidatoreXSD(Schema schema) throws Exception {
		super(schema);
		
	}



}
