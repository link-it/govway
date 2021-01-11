/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
/**
 * 
 */
package org.openspcoop2.utils.xml2json;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.xml.DOMSourceFix;
import org.w3c.dom.Node;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public abstract class AbstractXml2Json implements IXml2Json{

	private XMLInputFactory inputFactory; 

	public AbstractXml2Json() {
		this.inputFactory = XMLInputFactory.newInstance();
	}

	protected abstract XMLOutputFactory getOutputFactory();

	@Override
	public String xml2json(String xmlString) throws UtilsException {
		XMLEventReader reader = null;
		try {
			reader = this.inputFactory.createXMLEventReader(new StringReader(xmlString));
			StringWriter stringWriter = new StringWriter();
			xml2json(reader, stringWriter);
			return stringWriter.toString();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		} finally {
			if(reader != null)try {reader.close();} catch(Exception e) {}
		}
	}

	@Override
	public String xml2json(Node node) throws UtilsException {
		XMLEventReader reader = null;
		try {
			reader = this.inputFactory.createXMLEventReader(new DOMSourceFix(node));
			StringWriter stringWriter = new StringWriter();
			xml2json(reader, stringWriter);
			return stringWriter.toString();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		} finally {
			if(reader != null)try {reader.close();} catch(Exception e) {}
		}
	}

	private void xml2json(XMLEventReader reader, Writer writer) throws Exception {
		XMLEventWriter eventWriter = null;
		try {
			eventWriter = this.getOutputFactory().createXMLEventWriter(writer);
			eventWriter.add(reader);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		} finally {
			if(eventWriter != null)try {eventWriter.close();} catch(Exception e) {}
		}
	}

}
