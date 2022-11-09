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
	
	private OpenSPCoop2MessageFactory messageFactory;
	

	@Override
	public byte[] getAsByte(Node nodeXML) throws XMLException {
		try{
			return OpenSPCoop2MessageFactory.getAsByte(this.messageFactory, nodeXML,false);
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}

	@Override
	public AbstractXMLUtils getXMLUtils() {
		// il metodo viene usato anche nell'init dove l'assegnamento della messageFactory non e' stata ancora effettuata
		OpenSPCoop2MessageFactory messageFactoryParam = this.messageFactory;
		if(messageFactoryParam==null) {
			messageFactoryParam = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		}
		return MessageXMLUtils.getInstance(messageFactoryParam);
	}
	
	
	
	public ValidatoreXSD(Logger log, File... file) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, file);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory, Logger log, File... file) throws Exception {
		super(log, file);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, File file) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, file);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory, Logger log, File file) throws Exception {
		super(log, file);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, InputStream... inputStream) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, inputStream);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, InputStream... inputStream)
			throws Exception {
		super(log, inputStream);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, InputStream inputStream) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, inputStream);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, InputStream inputStream) throws Exception {
		super(log, inputStream);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, LSResourceResolver lsResourceResolver,
			File... file) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, lsResourceResolver, file);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, LSResourceResolver lsResourceResolver,
			File... file) throws Exception {
		super(log, lsResourceResolver, file);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, LSResourceResolver lsResourceResolver,
			File file) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, lsResourceResolver, file);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, LSResourceResolver lsResourceResolver,
			File file) throws Exception {
		super(log, lsResourceResolver, file);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, LSResourceResolver lsResourceResolver,
			InputStream... inputStream) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, lsResourceResolver, inputStream);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, LSResourceResolver lsResourceResolver,
			InputStream... inputStream) throws Exception {
		super(log, lsResourceResolver, inputStream);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, LSResourceResolver lsResourceResolver,
			InputStream inputStream) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, lsResourceResolver, inputStream);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, LSResourceResolver lsResourceResolver,
			InputStream inputStream) throws Exception {
		super(log, lsResourceResolver, inputStream);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, LSResourceResolver lsResourceResolver,
			Node... schema) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, lsResourceResolver, schema);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, LSResourceResolver lsResourceResolver,
			Node... schema) throws Exception {
		super(log, lsResourceResolver, schema);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, LSResourceResolver lsResourceResolver,
			Node schema) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, lsResourceResolver, schema);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, LSResourceResolver lsResourceResolver,
			Node schema) throws Exception {
		super(log, lsResourceResolver, schema);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, LSResourceResolver lsResourceResolver,
			Source... source) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, lsResourceResolver, source);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, LSResourceResolver lsResourceResolver,
			Source... source) throws Exception {
		super(log, lsResourceResolver, source);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, LSResourceResolver lsResourceResolver,
			Source source) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, lsResourceResolver, source);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, LSResourceResolver lsResourceResolver,
			Source source) throws Exception {
		super(log, lsResourceResolver, source);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, LSResourceResolver lsResourceResolver,
			String... url) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, lsResourceResolver, url);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, LSResourceResolver lsResourceResolver,
			String... url) throws Exception {
		super(log, lsResourceResolver, url);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, LSResourceResolver lsResourceResolver,
			String url) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, lsResourceResolver, url);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, LSResourceResolver lsResourceResolver,
			String url) throws Exception {
		super(log, lsResourceResolver, url);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, Node... schema) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, schema);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, Node... schema) throws Exception {
		super(log, schema);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, Node schema) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, schema);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, Node schema) throws Exception {
		super(log, schema);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, Source... source) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, source);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, Source... source) throws Exception {
		super(log, source);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, Source source) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, source);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, Source source) throws Exception {
		super(log, source);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, String schemaFactory, File... file) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, schemaFactory, file);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, String schemaFactory, File... file)
			throws Exception {
		super(log, schemaFactory, file);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, String schemaFactory, File file) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, schemaFactory, file);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, String schemaFactory, File file)
			throws Exception {
		super(log, schemaFactory, file);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, String schemaFactory, InputStream... inputStream) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, schemaFactory, inputStream);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, String schemaFactory,
			InputStream... inputStream) throws Exception {
		super(log, schemaFactory, inputStream);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, String schemaFactory, InputStream inputStream) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, schemaFactory, inputStream);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, String schemaFactory,
			InputStream inputStream) throws Exception {
		super(log, schemaFactory, inputStream);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, String schemaFactory, 
			LSResourceResolver lsResourceResolver, File... file) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, schemaFactory, lsResourceResolver, file);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, String schemaFactory,
			LSResourceResolver lsResourceResolver, File... file)
			throws Exception {
		super(log, schemaFactory, lsResourceResolver, file);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, String schemaFactory, 
			LSResourceResolver lsResourceResolver, File file) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, schemaFactory, lsResourceResolver, file);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, String schemaFactory,
			LSResourceResolver lsResourceResolver, File file) throws Exception {
		super(log, schemaFactory, lsResourceResolver, file);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, String schemaFactory, 
			LSResourceResolver lsResourceResolver, InputStream... inputStream) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, schemaFactory, lsResourceResolver, inputStream);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, String schemaFactory,
			LSResourceResolver lsResourceResolver, InputStream... inputStream)
			throws Exception {
		super(log, schemaFactory, lsResourceResolver, inputStream);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, String schemaFactory, 
			LSResourceResolver lsResourceResolver, InputStream inputStream) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, schemaFactory, lsResourceResolver, inputStream);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, String schemaFactory,
			LSResourceResolver lsResourceResolver, InputStream inputStream)
			throws Exception {
		super(log, schemaFactory, lsResourceResolver, inputStream);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, String schemaFactory, 
			LSResourceResolver lsResourceResolver, Node... schema) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, schemaFactory, lsResourceResolver, schema);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, String schemaFactory,
			LSResourceResolver lsResourceResolver, Node... schema)
			throws Exception {
		super(log, schemaFactory, lsResourceResolver, schema);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, String schemaFactory, 
			LSResourceResolver lsResourceResolver, Node schema) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, schemaFactory, lsResourceResolver, schema);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, String schemaFactory,
			LSResourceResolver lsResourceResolver, Node schema)
			throws Exception {
		super(log, schemaFactory, lsResourceResolver, schema);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, String schemaFactory, 
			LSResourceResolver lsResourceResolver, Source... source) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, schemaFactory, lsResourceResolver, source);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, String schemaFactory,
			LSResourceResolver lsResourceResolver, Source... source)
			throws Exception {
		super(log, schemaFactory, lsResourceResolver, source);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, String schemaFactory, 
			LSResourceResolver lsResourceResolver, Source source) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, schemaFactory, lsResourceResolver, source);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, String schemaFactory,
			LSResourceResolver lsResourceResolver, Source source)
			throws Exception {
		super(log, schemaFactory, lsResourceResolver, source);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, String schemaFactory, 
			LSResourceResolver lsResourceResolver, String... url) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, schemaFactory, lsResourceResolver, url);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, String schemaFactory,
			LSResourceResolver lsResourceResolver, String... url)
			throws Exception {
		super(log, schemaFactory, lsResourceResolver, url);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, String schemaFactory, 
			LSResourceResolver lsResourceResolver, String url) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, schemaFactory, lsResourceResolver, url);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, String schemaFactory,
			LSResourceResolver lsResourceResolver, String url) throws Exception {
		super(log, schemaFactory, lsResourceResolver, url);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, String schemaFactory, Node... schema) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, schemaFactory, schema);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, String schemaFactory, Node... schema)
			throws Exception {
		super(log, schemaFactory, schema);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, String schemaFactory, Node schema) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, schemaFactory, schema);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, String schemaFactory, Node schema)
			throws Exception {
		super(log, schemaFactory, schema);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, String schemaFactory, Source... source) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, schemaFactory, source);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, String schemaFactory, Source... source)
			throws Exception {
		super(log, schemaFactory, source);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, String schemaFactory, Source source) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, schemaFactory, source);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, String schemaFactory, Source source)
			throws Exception {
		super(log, schemaFactory, source);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, String schemaFactory, String... url) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, schemaFactory, url);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, String schemaFactory, String... url)
			throws Exception {
		super(log, schemaFactory, url);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, String schemaFactory, String url) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, schemaFactory, url);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, String schemaFactory, String url)
			throws Exception {
		super(log, schemaFactory, url);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, String... url) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, url);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, String... url) throws Exception {
		super(log, url);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Logger log, String url) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), log, url);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Logger log, String url) throws Exception {
		super(log, url);
		this.messageFactory = messageFactory;
	}

	public ValidatoreXSD(Schema schema) throws Exception {
		this(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), schema);
	}
	public ValidatoreXSD(OpenSPCoop2MessageFactory messageFactory,Schema schema) throws Exception {
		super(schema);
		this.messageFactory = messageFactory;
	}

}
