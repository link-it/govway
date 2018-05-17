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
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Node;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author: bussu $
 * @version $ Rev: 12563 $, $Date: 16 mag 2018 $
 * 
 */
public abstract class AbstractXml2Json implements IXml2Json{

	private XMLInputFactory inputFactory; 

	public AbstractXml2Json() {
		this.inputFactory = XMLInputFactory.newInstance();
	}

	protected abstract XMLOutputFactory getOutputFactory();

	@Override
	public String xml2json(String xmlString) throws Exception {
		XMLEventReader reader = null;
		try {
			reader = this.inputFactory.createXMLEventReader(new StringReader(xmlString));
			StringWriter stringWriter = new StringWriter();
			xml2json(reader, stringWriter);
			return stringWriter.toString();
		} catch (XMLStreamException e) {
			throw new Exception(e);
		} finally {
			if(reader != null)try {reader.close();} catch(Exception e) {}
		}
	}

	@Override
	public String xml2json(Node node) throws Exception {
		XMLEventReader reader = null;
		try {
			reader = this.inputFactory.createXMLEventReader(new DOMSource(node));
			StringWriter stringWriter = new StringWriter();
			xml2json(reader, stringWriter);
			return stringWriter.toString();
		} catch (XMLStreamException e) {
			throw new Exception(e);
		} finally {
			if(reader != null)try {reader.close();} catch(Exception e) {}
		}
	}

	private void xml2json(XMLEventReader reader, Writer writer) throws Exception {
		XMLEventWriter eventWriter = null;
		try {
			eventWriter = this.getOutputFactory().createXMLEventWriter(writer);
			eventWriter.add(reader);
		} catch (XMLStreamException e) {
			throw new Exception(e);
		} finally {
			if(eventWriter != null)try {eventWriter.close();} catch(Exception e) {}
		}
	}

}
