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

package org.openspcoop2.message.xml;

import java.io.File;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.validation.Schema;

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.XMLException;
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
public class ValidatoreXSD extends  org.openspcoop2.utils.xml.AbstractValidatoreXSD {
	
	
	@Override
	public byte[] getAsByte(Node nodeXML) throws XMLException {
		try{
			return OpenSPCoop2MessageFactory.getAsByte(nodeXML,false);
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



	@Override
	public AbstractXMLUtils getXMLUtils() {
		return XMLUtils.getInstance();
	}
}
