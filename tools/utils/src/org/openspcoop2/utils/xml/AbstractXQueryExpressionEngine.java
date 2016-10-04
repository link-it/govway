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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.output.CountingOutputStream;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.s9api.DOMDestination;
import net.sf.saxon.s9api.Destination;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import net.sf.saxon.s9api.XdmNode;

/**
 * Classe utilizzabile per ricerche effettuate tramite espressioni XQuery
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractXQueryExpressionEngine {

	private static final boolean DEFAULT_RESULT_AS_XML = false;
	
	
	/* ***** CONFIG ***** */
	
	private static Configuration saxonConfig = null;
	public static void initXQueryConfiguration() throws XQueryException{
		_initXQueryConfiguration(null);
	}
	public static void initXQueryConfiguration(String fileConfig) throws XQueryException{
		if(fileConfig==null){
			throw new XQueryException("Configuration file is null");
		}
		initXQueryConfiguration(new File(fileConfig));
	}
	public static void initXQueryConfiguration(File fileConfig) throws XQueryException{
		if(fileConfig==null){
			throw new XQueryException("Configuration file is null");
		}
		if(fileConfig.exists()==false){
			throw new XQueryException("Configuration file ["+fileConfig.getAbsolutePath()+"] not exists");
		}
		if(fileConfig.canRead()==false){
			throw new XQueryException("Configuration file ["+fileConfig.getAbsolutePath()+"] cannot read");
		}
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(fileConfig);
			_initXQueryConfiguration(fin);
		}
		catch(XQueryException e){
			throw e;
		}
		catch(Exception e){
			throw new XQueryException(e.getMessage(),e);
		}
		finally{
			try{
				fin.close();
			}catch(Exception eClose){}
		}
	}
	public static void initXQueryConfiguration(InputStream isConfig) throws XQueryException{
		if(isConfig==null){
			throw new XQueryException("Configuration stream is null");
		}
		_initXQueryConfiguration(isConfig);
	}
	private static synchronized void _initXQueryConfiguration(InputStream isConfig) throws XQueryException{
		try{
			if(AbstractXQueryExpressionEngine.saxonConfig==null){
				if(isConfig==null){
					saxonConfig = Configuration.newConfiguration();
				}
				else{
					StreamSource s = new StreamSource(isConfig);
					saxonConfig = Configuration.readConfiguration(s);
				}
			}
		}catch(Exception e){
			throw new XQueryException("Inizializzazione XQueryConfiguration non riuscita",e);
		}
	}
	
	
	/* ***** PROCESSOR ***** */
	
	private static Processor saxonProcessor = null;
	// An XQueryCompiler may be used repeatedly to compile multiple queries. Any changes made to the XQueryCompiler (that is, to the static context) 
	// do not affect queries that have already been compiled. An XQueryCompiler may be used concurrently in multiple threads, 
	// but it should not then be modified once initialized.
	private static XQueryCompiler saxonCompiler = null;
	public static Processor getXQueryProcessor() throws XQueryException{
		if(AbstractXQueryExpressionEngine.saxonProcessor==null){
			AbstractXQueryExpressionEngine.initXQueryProcessor();
		}
		return AbstractXQueryExpressionEngine.saxonProcessor;
	}
	public static XQueryCompiler getXQueryCompiler() throws XQueryException{
		if(AbstractXQueryExpressionEngine.saxonProcessor==null){
			AbstractXQueryExpressionEngine.initXQueryProcessor();
		}
		return AbstractXQueryExpressionEngine.saxonCompiler;
	}
	
	public static synchronized void initXQueryProcessor() throws XQueryException{
		try{
			if(AbstractXQueryExpressionEngine.saxonProcessor==null){
				if(AbstractXQueryExpressionEngine.saxonConfig==null){
					initXQueryConfiguration();
				}
				AbstractXQueryExpressionEngine.saxonProcessor = new Processor(AbstractXQueryExpressionEngine.saxonConfig);
				AbstractXQueryExpressionEngine.saxonCompiler = saxonProcessor.newXQueryCompiler();
			}
		}catch(Exception e){
			throw new XQueryException("Inizializzazione XQueryFactory non riuscita",e);
		}
	}
	
	
	
	@SuppressWarnings("unused")
	private static Logger logger = LoggerWrapperFactory.getLogger(AbstractXQueryExpressionEngine.class);
	public static void setLogger(Logger logger) {
		AbstractXQueryExpressionEngine.logger = logger;
	}
	
	
	
	/* ***** ABSTRACT METHOD ***** */
	
	public abstract AbstractXMLUtils getXMLUtils();
	public abstract Element readXPathElement(Element contenutoAsElement);
	

	
		
	
	/* ***** ENGINE ***** */
	
	private XdmNode _buildXdmNode(Object xdmNodeParam) throws XQueryException{
		if(xdmNodeParam==null){
			throw new XQueryException("Parameter xdmNodeParam is null");
		}
		Source source = null;
		InputStream isFile = null;
		try{
			if(xdmNodeParam instanceof Element){
				source = new DOMSource((this.readXPathElement((Element)xdmNodeParam)).getOwnerDocument()); // il builder sotto necessita di un document
			}
			else if(xdmNodeParam instanceof Document){
				source = new DOMSource((Document)xdmNodeParam);
			}
			else if(xdmNodeParam instanceof Node){
				source = new DOMSource(((Node)xdmNodeParam).getOwnerDocument()); // il builder sotto necessita di un document
			}
			else if(xdmNodeParam instanceof File){
				isFile = new FileInputStream((File)xdmNodeParam);
				InputSource eis = new InputSource(isFile);
				source = new SAXSource(eis);
			}
			else if(xdmNodeParam instanceof InputStream){
				InputSource eis = new InputSource((InputStream)xdmNodeParam);
				source = new SAXSource(eis);
			}
			else if(xdmNodeParam instanceof Reader){
				InputSource eis = new InputSource((Reader)xdmNodeParam);
				source = new SAXSource(eis);
			}
			else if(xdmNodeParam instanceof String){
				source = new DOMSource((Document)this.getXMLUtils().newDocument(((String)xdmNodeParam).getBytes()));
			}
			else{
				throw new XQueryException("Type Parameter '"+xdmNodeParam.getClass().getName()+"' not supported");
			}
			
			// Sharing of a DocumentBuilder across multiple threads is not recommended
			DocumentBuilder builder = AbstractXQueryExpressionEngine.getXQueryProcessor().newDocumentBuilder();
			
			if(isFile!=null){
				return new WrapperFileXdmNode(builder.build(source), isFile);
			}
			else{
				return builder.build(source);
			}
		}
		catch(Exception e){
			throw new XQueryException(e.getMessage(),e);
		}
	}
	
	private WrapperDestination _buildDestination(Object destinationParam, boolean xml) throws XQueryException{
		if(destinationParam==null){
			throw new XQueryException("Parameter destination is null");
		}
		WrapperDestination wrapper = new WrapperDestination();
		Destination destination = null;
		try{
			if(destinationParam instanceof Node){
				wrapper.node = (Node)destinationParam;
				destination =  new DOMDestination(wrapper.node);
			}
			else if(destinationParam instanceof File){
				wrapper.file = (File)destinationParam;
				destination = AbstractXQueryExpressionEngine.getXQueryProcessor().newSerializer(wrapper.file);
			}
			else if(destinationParam instanceof OutputStream){
				wrapper.cout = new CountingOutputStream((OutputStream)destinationParam);
				destination = AbstractXQueryExpressionEngine.getXQueryProcessor().newSerializer(wrapper.cout);
			}
			else if(destinationParam instanceof Writer){
				wrapper.writer = (Writer)destinationParam;
				destination = AbstractXQueryExpressionEngine.getXQueryProcessor().newSerializer(wrapper.writer);
			}
			else{
				throw new XQueryException("Type Parameter '"+destinationParam.getClass().getName()+"' not supported");
			}
			if(destination instanceof Serializer){
				if(xml){
					((Serializer)destination).setOutputProperty(Serializer.Property.METHOD, "xml");
					((Serializer)destination).setOutputProperty(Serializer.Property.INDENT, "yes");
					((Serializer)destination).setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "yes");
					((Serializer)destination).setOutputProperty(Serializer.Property.SAXON_INDENT_SPACES, "2");
				}
			}
			wrapper.destination = destination;
			return wrapper;
		}
		catch(Exception e){
			throw new XQueryException(e.getMessage(),e);
		}
	}
	
	private XQueryEvaluator _buildEvaluator(Object xquery) throws XQueryException, XQueryNotValidException{
		if(xquery==null){
			throw new XQueryException("Parameter xquery is null");
		}
		XQueryExecutable exp = null;
		try{
			if(xquery instanceof String){
				exp = AbstractXQueryExpressionEngine.getXQueryCompiler().compile((String)xquery);
			}
			else if(xquery instanceof File){
				exp = AbstractXQueryExpressionEngine.getXQueryCompiler().compile((File)xquery);
			}
			else if(xquery instanceof InputStream){
				exp = AbstractXQueryExpressionEngine.getXQueryCompiler().compile((InputStream)xquery);
			}
			else if(xquery instanceof Reader){
				exp = AbstractXQueryExpressionEngine.getXQueryCompiler().compile((Reader)xquery);
			}
			else{
				throw new XQueryException("Type Parameter '"+xquery.getClass().getName()+"' not supported");
			}
			return exp.load();
		}
		catch(XQueryException e){
			throw e;
		}
		catch(Exception e){
			throw new XQueryNotValidException(e.getMessage(),e);
		}
	}
	
	
	private void _evaluate(XdmNode doc, XQueryEvaluator evaluator,WrapperDestination destination) throws XQueryException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			evaluator.setContextItem(doc);
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e);
		}
		try{
			evaluator.setContextItem(doc);
			evaluator.run(destination.destination);
			
			if(destination.cout!=null){
				destination.cout.flush();
				destination.cout.close();
				if(destination.cout.getByteCount()<=0){
					throw new XQueryEvaluateNotFoundException("Not write bytes in output stream destination");
				}
			}
			else if(destination.file!=null){
				if(destination.file.length()<=0){
					throw new XQueryEvaluateNotFoundException("File destination is empty");
				}
			}
			else if(destination.node!=null){
				if( (destination.node.getChildNodes()==null || destination.node.getChildNodes().getLength()<=0) && destination.node.hasAttributes()==false ){
					throw new XQueryEvaluateNotFoundException("Node destination is empty");
				}
			}
			else if(destination.writer!=null){
				// not implemented check
			}
		}
		catch(XQueryEvaluateNotFoundException notFound){
			throw notFound;
		}
		catch(Exception e){
			throw new XQueryEvaluateException(e.getMessage(),e);
		}
		finally{
			try{
				if(doc instanceof WrapperFileXdmNode){
					((WrapperFileXdmNode)doc).is.close();
				}
			}catch(Exception eClose){}
		}
	}
	
	
	
	/* ***** PUBLIC METHOD ***** */
	
	public void validate(Object xquery) throws XQueryException, XQueryNotValidException{
		this._buildEvaluator(xquery);
	}
	
	
	
	/* ***** PUBLIC METHOD (SRC as Node) ***** */
	
	// Destination: String
	public String evaluate(Node source, String xquery) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		return this.evaluate(source, xquery, DEFAULT_RESULT_AS_XML);
	}
	public String evaluate(Node source, String xquery, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.evaluate(source, xquery, bout,resultAsXml);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(XQueryException e){
			throw e;
		}catch(XQueryNotValidException e){
			throw e;
		}catch(XQueryEvaluateException e){
			throw e;
		}catch(XQueryEvaluateNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e); 
		}
	}
	public String evaluate(Node source, File xquery) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		return this.evaluate(source, xquery, DEFAULT_RESULT_AS_XML);
	}
	public String evaluate(Node source, File xquery, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.evaluate(source, xquery, bout, resultAsXml);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(XQueryException e){
			throw e;
		}catch(XQueryNotValidException e){
			throw e;
		}catch(XQueryEvaluateException e){
			throw e;
		}catch(XQueryEvaluateNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e); 
		}
	}
	public String evaluate(Node source, InputStream xquery) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		return this.evaluate(source, xquery, DEFAULT_RESULT_AS_XML);
	}
	public String evaluate(Node source, InputStream xquery, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.evaluate(source, xquery, bout, resultAsXml);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(XQueryException e){
			throw e;
		}catch(XQueryNotValidException e){
			throw e;
		}catch(XQueryEvaluateException e){
			throw e;
		}catch(XQueryEvaluateNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e); 
		}
	}
	public String evaluate(Node source, Reader xquery) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		return this.evaluate(source, xquery, DEFAULT_RESULT_AS_XML);
	}
	public String evaluate(Node source, Reader xquery, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.evaluate(source, xquery, bout, resultAsXml);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(XQueryException e){
			throw e;
		}catch(XQueryNotValidException e){
			throw e;
		}catch(XQueryEvaluateException e){
			throw e;
		}catch(XQueryEvaluateNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e); 
		}
	}
	
	// Destination: OutputStream
	public void evaluate(Node source, String xquery, OutputStream out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Node source, String xquery, OutputStream out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Node source, File xquery, OutputStream out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Node source, File xquery, OutputStream out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Node source, InputStream xquery, OutputStream out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Node source, InputStream xquery, OutputStream out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Node source, Reader xquery, OutputStream out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Node source, Reader xquery, OutputStream out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	
	// Destination: Writer
	public void evaluate(Node source, String xquery, Writer out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Node source, String xquery, Writer out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Node source, File xquery, Writer out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Node source, File xquery, Writer out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Node source, InputStream xquery, Writer out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Node source, InputStream xquery, Writer out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Node source, Reader xquery, Writer out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Node source, Reader xquery, Writer out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	
	// Destination: File
	public void evaluate(Node source, String xquery, File out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Node source, String xquery, File out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Node source, File xquery, File out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Node source, File xquery, File out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Node source, InputStream xquery, File out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Node source, InputStream xquery, File out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Node source, Reader xquery, File out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Node source, Reader xquery, File out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	
	// Destination: Node
	public void evaluate(Node source, String xquery, Node out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Node source, String xquery, Node out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Node source, File xquery, Node out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Node source, File xquery, Node out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Node source, InputStream xquery, Node out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Node source, InputStream xquery, Node out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Node source, Reader xquery, Node out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Node source, Reader xquery, Node out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	
	
	
	
	/* ***** PUBLIC METHOD (SRC as Document) ***** */
	
	// Destination: String
	public String evaluate(Document source, String xquery) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		return this.evaluate(source, xquery, DEFAULT_RESULT_AS_XML);
	}
	public String evaluate(Document source, String xquery, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.evaluate(source, xquery, bout,resultAsXml);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(XQueryException e){
			throw e;
		}catch(XQueryNotValidException e){
			throw e;
		}catch(XQueryEvaluateException e){
			throw e;
		}catch(XQueryEvaluateNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e); 
		}
	}
	public String evaluate(Document source, File xquery) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		return this.evaluate(source, xquery, DEFAULT_RESULT_AS_XML);
	}
	public String evaluate(Document source, File xquery, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.evaluate(source, xquery, bout, resultAsXml);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(XQueryException e){
			throw e;
		}catch(XQueryNotValidException e){
			throw e;
		}catch(XQueryEvaluateException e){
			throw e;
		}catch(XQueryEvaluateNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e); 
		}
	}
	public String evaluate(Document source, InputStream xquery) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		return this.evaluate(source, xquery, DEFAULT_RESULT_AS_XML);
	}
	public String evaluate(Document source, InputStream xquery, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.evaluate(source, xquery, bout, resultAsXml);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(XQueryException e){
			throw e;
		}catch(XQueryNotValidException e){
			throw e;
		}catch(XQueryEvaluateException e){
			throw e;
		}catch(XQueryEvaluateNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e); 
		}
	}
	public String evaluate(Document source, Reader xquery) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		return this.evaluate(source, xquery, DEFAULT_RESULT_AS_XML);
	}
	public String evaluate(Document source, Reader xquery, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.evaluate(source, xquery, bout, resultAsXml);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(XQueryException e){
			throw e;
		}catch(XQueryNotValidException e){
			throw e;
		}catch(XQueryEvaluateException e){
			throw e;
		}catch(XQueryEvaluateNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e); 
		}
	}
	
	// Destination: OutputStream
	public void evaluate(Document source, String xquery, OutputStream out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Document source, String xquery, OutputStream out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Document source, File xquery, OutputStream out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Document source, File xquery, OutputStream out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Document source, InputStream xquery, OutputStream out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Document source, InputStream xquery, OutputStream out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Document source, Reader xquery, OutputStream out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Document source, Reader xquery, OutputStream out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	
	// Destination: Writer
	public void evaluate(Document source, String xquery, Writer out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Document source, String xquery, Writer out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Document source, File xquery, Writer out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Document source, File xquery, Writer out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Document source, InputStream xquery, Writer out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Document source, InputStream xquery, Writer out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Document source, Reader xquery, Writer out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Document source, Reader xquery, Writer out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	
	// Destination: File
	public void evaluate(Document source, String xquery, File out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Document source, String xquery, File out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Document source, File xquery, File out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Document source, File xquery, File out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Document source, InputStream xquery, File out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Document source, InputStream xquery, File out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Document source, Reader xquery, File out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Document source, Reader xquery, File out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	
	// Destination: Node
	public void evaluate(Document source, String xquery, Node out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Document source, String xquery, Node out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Document source, File xquery, Node out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Document source, File xquery, Node out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Document source, InputStream xquery, Node out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Document source, InputStream xquery, Node out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Document source, Reader xquery, Node out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Document source, Reader xquery, Node out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	
	
	
	/* ***** PUBLIC METHOD (SRC as InputStream) ***** */
	
	// Destination: String
	public String evaluate(InputStream source, String xquery) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		return this.evaluate(source, xquery, DEFAULT_RESULT_AS_XML);
	}
	public String evaluate(InputStream source, String xquery, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.evaluate(source, xquery, bout,resultAsXml);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(XQueryException e){
			throw e;
		}catch(XQueryNotValidException e){
			throw e;
		}catch(XQueryEvaluateException e){
			throw e;
		}catch(XQueryEvaluateNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e); 
		}
	}
	public String evaluate(InputStream source, File xquery) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		return this.evaluate(source, xquery, DEFAULT_RESULT_AS_XML);
	}
	public String evaluate(InputStream source, File xquery, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.evaluate(source, xquery, bout, resultAsXml);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(XQueryException e){
			throw e;
		}catch(XQueryNotValidException e){
			throw e;
		}catch(XQueryEvaluateException e){
			throw e;
		}catch(XQueryEvaluateNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e); 
		}
	}
	public String evaluate(InputStream source, InputStream xquery) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		return this.evaluate(source, xquery, DEFAULT_RESULT_AS_XML);
	}
	public String evaluate(InputStream source, InputStream xquery, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.evaluate(source, xquery, bout, resultAsXml);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(XQueryException e){
			throw e;
		}catch(XQueryNotValidException e){
			throw e;
		}catch(XQueryEvaluateException e){
			throw e;
		}catch(XQueryEvaluateNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e); 
		}
	}
	public String evaluate(InputStream source, Reader xquery) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		return this.evaluate(source, xquery, DEFAULT_RESULT_AS_XML);
	}
	public String evaluate(InputStream source, Reader xquery, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.evaluate(source, xquery, bout, resultAsXml);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(XQueryException e){
			throw e;
		}catch(XQueryNotValidException e){
			throw e;
		}catch(XQueryEvaluateException e){
			throw e;
		}catch(XQueryEvaluateNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e); 
		}
	}
	
	// Destination: OutputStream
	public void evaluate(InputStream source, String xquery, OutputStream out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(InputStream source, String xquery, OutputStream out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(InputStream source, File xquery, OutputStream out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(InputStream source, File xquery, OutputStream out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(InputStream source, InputStream xquery, OutputStream out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(InputStream source, InputStream xquery, OutputStream out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(InputStream source, Reader xquery, OutputStream out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(InputStream source, Reader xquery, OutputStream out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	
	// Destination: Writer
	public void evaluate(InputStream source, String xquery, Writer out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(InputStream source, String xquery, Writer out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(InputStream source, File xquery, Writer out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(InputStream source, File xquery, Writer out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(InputStream source, InputStream xquery, Writer out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(InputStream source, InputStream xquery, Writer out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(InputStream source, Reader xquery, Writer out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(InputStream source, Reader xquery, Writer out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	
	// Destination: File
	public void evaluate(InputStream source, String xquery, File out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(InputStream source, String xquery, File out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(InputStream source, File xquery, File out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(InputStream source, File xquery, File out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(InputStream source, InputStream xquery, File out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(InputStream source, InputStream xquery, File out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(InputStream source, Reader xquery, File out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(InputStream source, Reader xquery, File out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	
	// Destination: Node
	public void evaluate(InputStream source, String xquery, Node out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(InputStream source, String xquery, Node out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(InputStream source, File xquery, Node out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(InputStream source, File xquery, Node out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(InputStream source, InputStream xquery, Node out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(InputStream source, InputStream xquery, Node out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(InputStream source, Reader xquery, Node out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(InputStream source, Reader xquery, Node out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	
	
	
	
	/* ***** PUBLIC METHOD (SRC as Reader) ***** */
	
	// Destination: String
	public String evaluate(Reader source, String xquery) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		return this.evaluate(source, xquery, DEFAULT_RESULT_AS_XML);
	}
	public String evaluate(Reader source, String xquery, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.evaluate(source, xquery, bout,resultAsXml);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(XQueryException e){
			throw e;
		}catch(XQueryNotValidException e){
			throw e;
		}catch(XQueryEvaluateException e){
			throw e;
		}catch(XQueryEvaluateNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e); 
		}
	}
	public String evaluate(Reader source, File xquery) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		return this.evaluate(source, xquery, DEFAULT_RESULT_AS_XML);
	}
	public String evaluate(Reader source, File xquery, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.evaluate(source, xquery, bout, resultAsXml);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(XQueryException e){
			throw e;
		}catch(XQueryNotValidException e){
			throw e;
		}catch(XQueryEvaluateException e){
			throw e;
		}catch(XQueryEvaluateNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e); 
		}
	}
	public String evaluate(Reader source, InputStream xquery) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		return this.evaluate(source, xquery, DEFAULT_RESULT_AS_XML);
	}
	public String evaluate(Reader source, InputStream xquery, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.evaluate(source, xquery, bout, resultAsXml);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(XQueryException e){
			throw e;
		}catch(XQueryNotValidException e){
			throw e;
		}catch(XQueryEvaluateException e){
			throw e;
		}catch(XQueryEvaluateNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e); 
		}
	}
	public String evaluate(Reader source, Reader xquery) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		return this.evaluate(source, xquery, DEFAULT_RESULT_AS_XML);
	}
	public String evaluate(Reader source, Reader xquery, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.evaluate(source, xquery, bout, resultAsXml);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(XQueryException e){
			throw e;
		}catch(XQueryNotValidException e){
			throw e;
		}catch(XQueryEvaluateException e){
			throw e;
		}catch(XQueryEvaluateNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e); 
		}
	}
	
	// Destination: OutputStream
	public void evaluate(Reader source, String xquery, OutputStream out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Reader source, String xquery, OutputStream out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Reader source, File xquery, OutputStream out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Reader source, File xquery, OutputStream out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Reader source, InputStream xquery, OutputStream out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Reader source, InputStream xquery, OutputStream out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Reader source, Reader xquery, OutputStream out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Reader source, Reader xquery, OutputStream out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	
	// Destination: Writer
	public void evaluate(Reader source, String xquery, Writer out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Reader source, String xquery, Writer out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Reader source, File xquery, Writer out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Reader source, File xquery, Writer out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Reader source, InputStream xquery, Writer out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Reader source, InputStream xquery, Writer out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Reader source, Reader xquery, Writer out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Reader source, Reader xquery, Writer out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	
	// Destination: File
	public void evaluate(Reader source, String xquery, File out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Reader source, String xquery, File out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Reader source, File xquery, File out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Reader source, File xquery, File out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Reader source, InputStream xquery, File out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Reader source, InputStream xquery, File out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Reader source, Reader xquery, File out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Reader source, Reader xquery, File out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	
	// Destination: Node
	public void evaluate(Reader source, String xquery, Node out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Reader source, String xquery, Node out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Reader source, File xquery, Node out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Reader source, File xquery, Node out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Reader source, InputStream xquery, Node out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Reader source, InputStream xquery, Node out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(Reader source, Reader xquery, Node out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(Reader source, Reader xquery, Node out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	
	
	/* ***** PUBLIC METHOD (SRC as File) ***** */
	
	// Destination: String
	public String evaluate(File source, String xquery) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		return this.evaluate(source, xquery, DEFAULT_RESULT_AS_XML);
	}
	public String evaluate(File source, String xquery, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.evaluate(source, xquery, bout,resultAsXml);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(XQueryException e){
			throw e;
		}catch(XQueryNotValidException e){
			throw e;
		}catch(XQueryEvaluateException e){
			throw e;
		}catch(XQueryEvaluateNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e); 
		}
	}
	public String evaluate(File source, File xquery) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		return this.evaluate(source, xquery, DEFAULT_RESULT_AS_XML);
	}
	public String evaluate(File source, File xquery, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.evaluate(source, xquery, bout, resultAsXml);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(XQueryException e){
			throw e;
		}catch(XQueryNotValidException e){
			throw e;
		}catch(XQueryEvaluateException e){
			throw e;
		}catch(XQueryEvaluateNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e); 
		}
	}
	public String evaluate(File source, InputStream xquery) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		return this.evaluate(source, xquery, DEFAULT_RESULT_AS_XML);
	}
	public String evaluate(File source, InputStream xquery, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.evaluate(source, xquery, bout, resultAsXml);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(XQueryException e){
			throw e;
		}catch(XQueryNotValidException e){
			throw e;
		}catch(XQueryEvaluateException e){
			throw e;
		}catch(XQueryEvaluateNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e); 
		}
	}
	public String evaluate(File source, Reader xquery) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		return this.evaluate(source, xquery, DEFAULT_RESULT_AS_XML);
	}
	public String evaluate(File source, Reader xquery, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.evaluate(source, xquery, bout, resultAsXml);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(XQueryException e){
			throw e;
		}catch(XQueryNotValidException e){
			throw e;
		}catch(XQueryEvaluateException e){
			throw e;
		}catch(XQueryEvaluateNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e); 
		}
	}
	
	// Destination: OutputStream
	public void evaluate(File source, String xquery, OutputStream out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(File source, String xquery, OutputStream out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(File source, File xquery, OutputStream out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(File source, File xquery, OutputStream out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(File source, InputStream xquery, OutputStream out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(File source, InputStream xquery, OutputStream out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(File source, Reader xquery, OutputStream out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(File source, Reader xquery, OutputStream out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	
	// Destination: Writer
	public void evaluate(File source, String xquery, Writer out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(File source, String xquery, Writer out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(File source, File xquery, Writer out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(File source, File xquery, Writer out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(File source, InputStream xquery, Writer out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(File source, InputStream xquery, Writer out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(File source, Reader xquery, Writer out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(File source, Reader xquery, Writer out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	
	// Destination: File
	public void evaluate(File source, String xquery, File out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(File source, String xquery, File out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(File source, File xquery, File out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(File source, File xquery, File out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(File source, InputStream xquery, File out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(File source, InputStream xquery, File out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(File source, Reader xquery, File out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(File source, Reader xquery, File out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	
	// Destination: Node
	public void evaluate(File source, String xquery, Node out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(File source, String xquery, Node out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(File source, File xquery, Node out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(File source, File xquery, Node out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(File source, InputStream xquery, Node out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(File source, InputStream xquery, Node out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(File source, Reader xquery, Node out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(File source, Reader xquery, Node out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	
	
	
	/* ***** PUBLIC METHOD (SRC as String) ***** */
	
	// Destination: String
	public String evaluate(String source, String xquery) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		return this.evaluate(source, xquery, DEFAULT_RESULT_AS_XML);
	}
	public String evaluate(String source, String xquery, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.evaluate(source, xquery, bout,resultAsXml);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(XQueryException e){
			throw e;
		}catch(XQueryNotValidException e){
			throw e;
		}catch(XQueryEvaluateException e){
			throw e;
		}catch(XQueryEvaluateNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e); 
		}
	}
	public String evaluate(String source, File xquery) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		return this.evaluate(source, xquery, DEFAULT_RESULT_AS_XML);
	}
	public String evaluate(String source, File xquery, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.evaluate(source, xquery, bout, resultAsXml);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(XQueryException e){
			throw e;
		}catch(XQueryNotValidException e){
			throw e;
		}catch(XQueryEvaluateException e){
			throw e;
		}catch(XQueryEvaluateNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e); 
		}
	}
	public String evaluate(String source, InputStream xquery) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		return this.evaluate(source, xquery, DEFAULT_RESULT_AS_XML);
	}
	public String evaluate(String source, InputStream xquery, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.evaluate(source, xquery, bout, resultAsXml);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(XQueryException e){
			throw e;
		}catch(XQueryNotValidException e){
			throw e;
		}catch(XQueryEvaluateException e){
			throw e;
		}catch(XQueryEvaluateNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e); 
		}
	}
	public String evaluate(String source, Reader xquery) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		return this.evaluate(source, xquery, DEFAULT_RESULT_AS_XML);
	}
	public String evaluate(String source, Reader xquery, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.evaluate(source, xquery, bout, resultAsXml);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(XQueryException e){
			throw e;
		}catch(XQueryNotValidException e){
			throw e;
		}catch(XQueryEvaluateException e){
			throw e;
		}catch(XQueryEvaluateNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new XQueryException(e.getMessage(),e); 
		}
	}
	
	// Destination: OutputStream
	public void evaluate(String source, String xquery, OutputStream out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(String source, String xquery, OutputStream out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(String source, File xquery, OutputStream out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(String source, File xquery, OutputStream out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(String source, InputStream xquery, OutputStream out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(String source, InputStream xquery, OutputStream out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(String source, Reader xquery, OutputStream out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(String source, Reader xquery, OutputStream out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	
	// Destination: Writer
	public void evaluate(String source, String xquery, Writer out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(String source, String xquery, Writer out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(String source, File xquery, Writer out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(String source, File xquery, Writer out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(String source, InputStream xquery, Writer out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(String source, InputStream xquery, Writer out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(String source, Reader xquery, Writer out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(String source, Reader xquery, Writer out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	
	// Destination: File
	public void evaluate(String source, String xquery, File out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(String source, String xquery, File out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(String source, File xquery, File out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(String source, File xquery, File out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(String source, InputStream xquery, File out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(String source, InputStream xquery, File out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(String source, Reader xquery, File out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(String source, Reader xquery, File out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	
	// Destination: Node
	public void evaluate(String source, String xquery, Node out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(String source, String xquery, Node out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(String source, File xquery, Node out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(String source, File xquery, Node out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(String source, InputStream xquery, Node out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(String source, InputStream xquery, Node out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
	public void evaluate(String source, Reader xquery, Node out) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, DEFAULT_RESULT_AS_XML));
	}
	public void evaluate(String source, Reader xquery, Node out, boolean resultAsXml) throws XQueryException, XQueryNotValidException, XQueryEvaluateException, XQueryEvaluateNotFoundException{
		this._evaluate(this._buildXdmNode(source), this._buildEvaluator(xquery), this._buildDestination(out, resultAsXml));
	}
}

class WrapperFileXdmNode extends XdmNode{

	protected XdmNode original;
	protected InputStream is;
	
	public WrapperFileXdmNode(XdmNode original,InputStream is) {
		super(original.getUnderlyingNode());
		this.original = original;
		this.is = is;
	}
	
}

class WrapperDestination {
	
	protected Destination destination;
	
	protected Node node;
	
	protected CountingOutputStream cout;
	
	protected File file;
	
	protected Writer writer;
}
