/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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


package org.openspcoop2.utils.xml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Classe utilizzabile per ottenere documenti xml
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class PrettyPrintXMLUtils {

	public static AbstractXMLUtils xmlUtils = XMLUtils.getInstance();
	public static synchronized void setXMLUtils(AbstractXMLUtils xmlUtilsParam) {
		PrettyPrintXMLUtils.xmlUtils = xmlUtilsParam;
	}


	
	// *********** prettyPrintWithXMLSerializer **************
	
	/*
	 * (using JDK 1.5). You can configure Xerces-J’s XMLSerialzer directly to pretty-print XML.  
	 * Although this is the easiest method, it depends on an internal class in the Xerces parser and is not JAXP compliant!  
	 * You can read more in Question 11 of the JAXP FAQ (https://jaxp.dev.java.net/1.4/JAXP-FAQ.html).
	 */
	
	@Deprecated
	@SuppressWarnings("deprecation")
	public static void prettyPrintWithXMLSerializer(Document doc,OutputStream os)throws TransformerException,IOException{
		// All is not lost if you're still on JDK 1.5: just use XMLSerializer with the appropriate OutputFormat.
		// The following will pretty-print the DOM document to XML.
		org.apache.xml.serialize.OutputFormat format = new org.apache.xml.serialize.OutputFormat(doc);
		format.setLineWidth(65);
		format.setIndenting(true);
		format.setIndent(2);
		org.apache.xml.serialize.XMLSerializer serializer = new org.apache.xml.serialize.XMLSerializer(os, format);
		serializer.serialize(doc);
	}
	@Deprecated
	@SuppressWarnings("deprecation")
	public static void prettyPrintWithXMLSerializer(Document doc,Writer writer)throws TransformerException,IOException{
		// All is not lost if you're still on JDK 1.5: just use XMLSerializer with the appropriate OutputFormat.
		// The following will pretty-print the DOM document to XML.
		org.apache.xml.serialize.OutputFormat format = new org.apache.xml.serialize.OutputFormat(doc);
		format.setLineWidth(65);
		format.setIndenting(true);
		format.setIndent(2);
		org.apache.xml.serialize.XMLSerializer serializer = new org.apache.xml.serialize.XMLSerializer(writer, format);
		serializer.serialize(doc);
	}
	@Deprecated
	public static void prettyPrintWithXMLSerializer(Document doc,File file)throws TransformerException,IOException{
		// All is not lost if you're still on JDK 1.5: just use XMLSerializer with the appropriate OutputFormat.
		// The following will pretty-print the DOM document to XML.
		FileOutputStream fout = null;
		try{
			fout = new FileOutputStream(file);
			PrettyPrintXMLUtils.prettyPrintWithXMLSerializer(doc,fout);
		}finally{
			try{
				fout.flush();
			}catch(Exception eClose){}
			try{
				fout.close();
			}catch(Exception eClose){}
		}
	}
	@Deprecated
	public static String prettyPrintWithXMLSerializer(Document doc)throws TransformerException,IOException{
		// All is not lost if you're still on JDK 1.5: just use XMLSerializer with the appropriate OutputFormat.
		// The following will pretty-print the DOM document to XML.
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		PrettyPrintXMLUtils.prettyPrintWithXMLSerializer(doc,bout);
		bout.flush();
		bout.close();
		return bout.toString();
	}


	
	
	
	
	
	// *********** prettyPrintWithTrAX **************
	/*
	 * Using the TrAX API is straightforward, however dealing with whitespace in a DOM is rather tricky.  
	 * It’s important to note that if your DOM was parsed from XML containing whitespace, this will by default be preserved in the object model.  
	 * Furthermore, the default TrAX indentation engine (Xalan-Java) won’t reformat existing whitespace. 
	 * Therefore, to reliably pretty-print XML with TrAX you’ll need to supply an XSLT stylesheet (pretty-print.xsl) that removes whitespace from the DOM during transformation.  
	 * This is a subtle point about serialization using TrAX that’s missed on the Xalan website which can conflate a bug found in JDK 1.5.  
	 * You can read more about this phenomenon (http://forums.sun.com/thread.jspa?threadID=562510) and (http://www.coderanch.com/t/126529/XML-Related-Technologies/do-pretty-print-xml). 
	 * */
	
	public static void prettyPrintWithTrAX(Document doc,OutputStream os)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(doc, os, new XMLErrorListener(),false);
	}
	public static void prettyPrintWithTrAX(Document doc,OutputStream os,ErrorListener errorListener)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(doc, os,errorListener,false);
	}
	public static void prettyPrintWithTrAX(Document doc,OutputStream os,boolean omitXMLDeclaration)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(doc, os, new XMLErrorListener(),omitXMLDeclaration);
	}
	public static void prettyPrintWithTrAX(Document doc,OutputStream os,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(doc, os,errorListener,omitXMLDeclaration);
	}

	public static void prettyPrintWithTrAX(Document doc,Writer writer)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(doc, writer, new XMLErrorListener(),false);
	}
	public static void prettyPrintWithTrAX(Document doc,Writer writer,ErrorListener errorListener)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(doc, writer,errorListener,false);
	}
	public static void prettyPrintWithTrAX(Document doc,Writer writer,boolean omitXMLDeclaration)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(doc, writer, new XMLErrorListener(),omitXMLDeclaration);
	}
	public static void prettyPrintWithTrAX(Document doc,Writer writer,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(doc, writer,errorListener,omitXMLDeclaration);
	}

	public static void prettyPrintWithTrAX(Document doc,File file)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(doc, file, new XMLErrorListener(),false);
	}
	public static void prettyPrintWithTrAX(Document doc,File file,ErrorListener errorListener)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(doc, file,errorListener,false);
	}
	public static void prettyPrintWithTrAX(Document doc,File file,boolean omitXMLDeclaration)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(doc, file, new XMLErrorListener(),omitXMLDeclaration);
	}
	public static void prettyPrintWithTrAX(Document doc,File file,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(doc, file,errorListener,omitXMLDeclaration);
	}
	
	public static String prettyPrintWithTrAX(Document doc)throws TransformerException,IOException{
		return PrettyPrintXMLUtils.prettyPrintWithTrAX(doc, new XMLErrorListener(),false);
	}
	public static String prettyPrintWithTrAX(Document doc,ErrorListener errorListener)throws TransformerException,IOException{
		return PrettyPrintXMLUtils.prettyPrintWithTrAX(doc, errorListener,false);
	}
	public static String prettyPrintWithTrAX(Document doc,boolean omitXMLDeclaration)throws TransformerException,IOException{
		return PrettyPrintXMLUtils.prettyPrintWithTrAX(doc, new XMLErrorListener(),omitXMLDeclaration);
	}
	public static String prettyPrintWithTrAX(Document doc,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(doc, bout, errorListener,omitXMLDeclaration);
		bout.flush();
		bout.close();
		return bout.toString();
	}

	public static void prettyPrintWithTrAX(Element element,OutputStream os)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(element, os, new XMLErrorListener(),false);
	}
	public static void prettyPrintWithTrAX(Element element,OutputStream os,ErrorListener errorListener)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(element, os,errorListener,false);
	}
	public static void prettyPrintWithTrAX(Element element,OutputStream os,boolean omitXMLDeclaration)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(element, os, new XMLErrorListener(),omitXMLDeclaration);
	}
	public static void prettyPrintWithTrAX(Element element,OutputStream os,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(element, os,errorListener,omitXMLDeclaration);
	}

	public static void prettyPrintWithTrAX(Element element,Writer writer)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(element, writer, new XMLErrorListener(),false);
	}
	public static void prettyPrintWithTrAX(Element element,Writer writer,ErrorListener errorListener)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(element, writer,errorListener,false);
	}
	public static void prettyPrintWithTrAX(Element element,Writer writer,boolean omitXMLDeclaration)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(element, writer, new XMLErrorListener(),omitXMLDeclaration);
	}
	public static void prettyPrintWithTrAX(Element element,Writer writer,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(element, writer,errorListener,omitXMLDeclaration);
	}

	public static void prettyPrintWithTrAX(Element element,File file)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(element, file, new XMLErrorListener(),false);
	}
	public static void prettyPrintWithTrAX(Element element,File file,ErrorListener errorListener)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(element, file,errorListener,false);
	}
	public static void prettyPrintWithTrAX(Element element,File file,boolean omitXMLDeclaration)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(element, file, new XMLErrorListener(),omitXMLDeclaration);
	}
	public static void prettyPrintWithTrAX(Element element,File file,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(element, file,errorListener,omitXMLDeclaration);
	}

	public static String prettyPrintWithTrAX(Element element)throws TransformerException,IOException{
		return PrettyPrintXMLUtils.prettyPrintWithTrAX(element, new XMLErrorListener(),false);
	}
	public static String prettyPrintWithTrAX(Element element,ErrorListener errorListener)throws TransformerException,IOException{
		return PrettyPrintXMLUtils.prettyPrintWithTrAX(element, errorListener,false);
	}
	public static String prettyPrintWithTrAX(Element element,boolean omitXMLDeclaration)throws TransformerException,IOException{
		return PrettyPrintXMLUtils.prettyPrintWithTrAX(element, new XMLErrorListener(),omitXMLDeclaration);
	}
	public static String prettyPrintWithTrAX(Element element,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(element, bout, errorListener,omitXMLDeclaration);
		bout.flush();
		bout.close();
		return bout.toString();
	}
	
	public static void prettyPrintWithTrAX(Node node,OutputStream os)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(node,os, new XMLErrorListener(),false);
	}
	public static void prettyPrintWithTrAX(Node node,OutputStream os,ErrorListener errorListener)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(node,os,errorListener,false);
	}
	public static void prettyPrintWithTrAX(Node node,OutputStream os,boolean omitXMLDeclaration)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(node,os, new XMLErrorListener(),omitXMLDeclaration);
	}
	public static void prettyPrintWithTrAX(Node node,OutputStream os,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(node,os,errorListener,omitXMLDeclaration);
	}

	public static void prettyPrintWithTrAX(Node node,Writer writer)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(node,writer, new XMLErrorListener(),false);
	}
	public static void prettyPrintWithTrAX(Node node,Writer writer,ErrorListener errorListener)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(node,writer,errorListener,false);
	}
	public static void prettyPrintWithTrAX(Node node,Writer writer,boolean omitXMLDeclaration)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(node,writer, new XMLErrorListener(),omitXMLDeclaration);
	}
	public static void prettyPrintWithTrAX(Node node,Writer writer,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(node,writer,errorListener,omitXMLDeclaration);
	}

	public static void prettyPrintWithTrAX(Node node,File file)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(node,file, new XMLErrorListener(),false);
	}
	public static void prettyPrintWithTrAX(Node node,File file,ErrorListener errorListener)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(node,file,errorListener,false);
	}
	public static void prettyPrintWithTrAX(Node node,File file,boolean omitXMLDeclaration)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(node,file, new XMLErrorListener(),omitXMLDeclaration);
	}
	public static void prettyPrintWithTrAX(Node node,File file,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException{
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(node,file,errorListener,omitXMLDeclaration);
	}
	
	public static String prettyPrintWithTrAX(Node node)throws TransformerException,IOException{
		return PrettyPrintXMLUtils.prettyPrintWithTrAX(node, new XMLErrorListener(),false);
	}
	public static String prettyPrintWithTrAX(Node node,ErrorListener errorListener)throws TransformerException,IOException{
		return PrettyPrintXMLUtils.prettyPrintWithTrAX(node, errorListener,false);
	}
	public static String prettyPrintWithTrAX(Node node,boolean omitXMLDeclaration)throws TransformerException,IOException{
		return PrettyPrintXMLUtils.prettyPrintWithTrAX(node, new XMLErrorListener(),omitXMLDeclaration);
	}
	public static String prettyPrintWithTrAX(Node node,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		PrettyPrintXMLUtils.prettyPrintWithTrAX_engine(node, bout, errorListener,omitXMLDeclaration);
		bout.flush();
		bout.close();
		return bout.toString();
	}
	
	private static InputStream readPrettyPrintXslt() throws IOException{
		return PrettyPrintXMLUtils.class.getResourceAsStream("/org/openspcoop2/utils/xml/pretty-print.xsl");
	}
	
	private static void prettyPrintWithTrAX_engine(Node doc,OutputStream os,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException{
		// Pretty-prints a DOM document to XML using TrAX.
		// Note that a stylesheet is needed to make formatting reliable.
		InputStream is = null;
		try{
			is = PrettyPrintXMLUtils.readPrettyPrintXslt();
			Source source = new DOMSource(doc);
			StreamResult result = new StreamResult(os);
			Transformer transformer = null;
			try{
				transformer = PrettyPrintXMLUtils.xmlUtils.getTransformerFactory().newTransformer(new StreamSource(is));
			}catch(Exception e){
				throw new TransformerException(e.getMessage(),e);
			}
			if(omitXMLDeclaration)
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
			transformer.setErrorListener(errorListener);
			transformer.transform(source, result);
			os.flush();
		}finally{
			try{
				if(is!=null){
					is.close();
				}
			}catch(Exception eClose){}
		}
	}
	private static void prettyPrintWithTrAX_engine(Node doc,Writer writer,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException{
		// Pretty-prints a DOM document to XML using TrAX.
		// Note that a stylesheet is needed to make formatting reliable.
		InputStream is = null;
		try{
			is = PrettyPrintXMLUtils.readPrettyPrintXslt();
			Source source = new DOMSource(doc);
			StreamResult result = new StreamResult(writer);
			Transformer transformer = null;
			try{
				transformer = PrettyPrintXMLUtils.xmlUtils.getTransformerFactory().newTransformer(new StreamSource(is));
			}catch(Exception e){
				throw new TransformerException(e.getMessage(),e);
			}
			if(omitXMLDeclaration)
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
			transformer.setErrorListener(errorListener);
			transformer.transform(source, result);
			writer.flush();
		}finally{
			try{
				if(is!=null){
					is.close();
				}
			}catch(Exception eClose){}
		}
	}
	private static void prettyPrintWithTrAX_engine(Node doc,File file,ErrorListener errorListener,boolean omitXMLDeclaration)throws TransformerException,IOException{
		// Pretty-prints a DOM document to XML using TrAX.
		// Note that a stylesheet is needed to make formatting reliable.
		InputStream is = null;
		try{
			is = PrettyPrintXMLUtils.readPrettyPrintXslt();
			Source source = new DOMSource(doc);
			StreamResult result = new StreamResult(file);
			Transformer transformer = null;
			try{
				transformer = PrettyPrintXMLUtils.xmlUtils.getTransformerFactory().newTransformer(new StreamSource(is));
			}catch(Exception e){
				throw new TransformerException(e.getMessage(),e);
			}
			if(omitXMLDeclaration)
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
			transformer.setErrorListener(errorListener);
			transformer.transform(source, result);
		}finally{
			try{
				if(is!=null){
					is.close();
				}
			}catch(Exception eClose){}
		}
	}
		
	

	
	
	
	// *********** prettyPrintWithDOM3LS **************
	
	/*
	 * DOM Level 3 Load and Save (LS) is the new API for pretty-printing XML.  
	 * Support for LS is included with JAXP 1.4 which is bundled as part of Sun’s JDK 1.5, 
	 * but unfortunately pretty-print formatting only works in JDK 1.6 or better.
	 */
	
	public static void prettyPrintWithDOM3LS(Document document,OutputStream out){
		DOM3LS_XMLUtils.serialize(document, out);
	}
	
	public static void prettyPrintWithDOM3LS(Document document,Writer writer){
		DOM3LS_XMLUtils.serialize(document, writer);
	}
	
	public static void prettyPrintWithDOM3LS(Document document,File file) throws FileNotFoundException{
		DOM3LS_XMLUtils.serialize(document, file);
	}
	
	public static String prettyPrintWithDOM3LS(Document document){
		return DOM3LS_XMLUtils.toString(document);
	}
	
}
