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
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;


/**
 * Classe utilizzabile per ottenere documenti xml
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DOM3LS_XMLUtils {

	public static AbstractXMLUtils xmlUtils = XMLUtils.getInstance();
	public static synchronized void setXMLUtils(AbstractXMLUtils xmlUtilsParam) {
		xmlUtils = xmlUtilsParam;
	}

	
	
	// *********** prettyPrintWithDOM3LS **************
	
	/*
	 * DOM Level 3 Load and Save (LS) is the new API for pretty-printing XML.  
	 * Support for LS is included with JAXP 1.4 which is bundled as part of Sun’s JDK 1.5, 
	 * but unfortunately pretty-print formatting only works in JDK 1.6 or better.
	 */
	
	private static void _serialize(Document documentImplementation,Node n, Object out, boolean prettyPrint) {
		// Pretty-prints a DOM document to XML using DOM Load and Save's LSSerializer.
		// Note that the "format-pretty-print" DOM configuration parameter can only be set in JDK 1.6+.
		DOMImplementation domImplementation = documentImplementation.getImplementation();
		if (domImplementation.hasFeature("LS", "3.0") && domImplementation.hasFeature("Core", "2.0")) {
			DOMImplementationLS domImplementationLS = (DOMImplementationLS) domImplementation.getFeature("LS", "3.0");
			LSSerializer lsSerializer = domImplementationLS.createLSSerializer();
			DOMConfiguration domConfiguration = lsSerializer.getDomConfig();
			if(prettyPrint) {
				if (domConfiguration.canSetParameter("format-pretty-print", Boolean.TRUE)) {
					lsSerializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
				} else {
					throw new RuntimeException("DOMConfiguration 'format-pretty-print' parameter isn't settable.");
				}
			}
			LSOutput lsOutput = domImplementationLS.createLSOutput();
			lsOutput.setEncoding("UTF-8");
			if(out instanceof OutputStream) {
				lsOutput.setByteStream((OutputStream)out);
			}
			else if(out instanceof Writer) {
				lsOutput.setCharacterStream((Writer) out);
			}
			lsSerializer.write(n, lsOutput);
		} else {
			throw new RuntimeException("DOM 3.0 LS and/or DOM 2.0 Core not supported.");
		}
	}
	
	
	// OUTPUT STREAM
	
	public static void serialize(Document document,OutputStream out, boolean prettyPrint){
		serialize(document, document, out, prettyPrint);
	}
	public static void serialize(Document document,OutputStream out){
		serialize(document, document, out, false);
	}
	public static void serialize(Element element,OutputStream out, boolean prettyPrint){
		serialize(element.getOwnerDocument(), element, out, prettyPrint);
	}
	public static void serialize(Element element,OutputStream out){
		serialize(element.getOwnerDocument(), element, out, false);
	}
	public static void serialize(Node node,OutputStream out, boolean prettyPrint){
		serialize(node.getOwnerDocument(), node, out, prettyPrint);
	}
	public static void serialize(Node node,OutputStream out){
		serialize(node.getOwnerDocument(), node, out, false);
	}
	public static void serialize(Document documentImplementation,Node n, OutputStream out, boolean prettyPrint){
		_serialize(documentImplementation, n, out, prettyPrint);
	}
	
	
	// WRITER
	
	public static void serialize(Document document,Writer writer, boolean prettyPrint){
		serialize(document, document, writer, prettyPrint);
	}
	public static void serialize(Document document,Writer writer){
		serialize(document, document, writer, false);
	}
	public static void serialize(Element element,Writer writer, boolean prettyPrint){
		serialize(element.getOwnerDocument(), element, writer, prettyPrint);
	}
	public static void serialize(Element element,Writer writer){
		serialize(element.getOwnerDocument(), element, writer, false);
	}
	public static void serialize(Node node,Writer writer, boolean prettyPrint){
		serialize(node.getOwnerDocument(), node, writer, prettyPrint);
	}
	public static void serialize(Node node,Writer writer){
		serialize(node.getOwnerDocument(), node, writer, false);
	}
	public static void serialize(Document documentImplementation,Node n, Writer writer, boolean prettyPrint){
		_serialize(documentImplementation, n, writer, prettyPrint);
	}
	
	
	// FILE
	
	public static void serialize(Document document,File file, boolean prettyPrint) throws FileNotFoundException{
		serialize(document, document, file, prettyPrint);
	}
	public static void serialize(Document document,File file) throws FileNotFoundException{
		serialize(document, document, file, false);
	}
	public static void serialize(Element element,File file, boolean prettyPrint) throws FileNotFoundException{
		serialize(element.getOwnerDocument(), element, file, prettyPrint);
	}
	public static void serialize(Element element,File file) throws FileNotFoundException{
		serialize(element.getOwnerDocument(), element, file, false);
	}
	public static void serialize(Node node,File file, boolean prettyPrint) throws FileNotFoundException{
		serialize(node.getOwnerDocument(), node, file, prettyPrint);
	}
	public static void serialize(Node node,File file) throws FileNotFoundException{
		serialize(node.getOwnerDocument(), node, file, false);
	}
	public static void serialize(Document documentImplementation,Node n, File file, boolean prettyPrint) throws FileNotFoundException{
		_serialize(documentImplementation, n, file, prettyPrint);
	}
	
	public static void _serialize(Document documentImplementation,Node n, File file, boolean prettyPrint) throws FileNotFoundException{
		FileOutputStream fout = null;
		try{
			fout = new FileOutputStream(file);
			DOM3LS_XMLUtils._serialize(documentImplementation,n,fout,prettyPrint);
		}finally{
			try{
				fout.flush();
			}catch(Exception eClose){}
			try{
				fout.close();
			}catch(Exception eClose){}
		}
	}
	
	
	// TO STRING
	
	public static String toString(Document document, boolean prettyPrint){
		return toString(document, document, prettyPrint);
	}
	public static String toString(Document document){
		return toString(document, document, false);
	}
	public static String toString(Element element, boolean prettyPrint){
		return toString(element.getOwnerDocument(), element, prettyPrint);
	}
	public static String toString(Element element){
		return toString(element.getOwnerDocument(), element, false);
	}
	public static String toString(Node node, boolean prettyPrint){
		return toString(node.getOwnerDocument(), node, prettyPrint);
	}
	public static String toString(Node node){
		return toString(node.getOwnerDocument(), node, false);
	}
	public static String toString(Document documentImplementation,Node n, boolean prettyPrint){
		return _toString(documentImplementation, n, prettyPrint);
	}
	
	public static String _toString(Document documentImplementation,Node n, boolean prettyPrint){
		StringWriter stringWriter = new StringWriter();
		DOM3LS_XMLUtils._serialize(documentImplementation,n,stringWriter,prettyPrint);
		return stringWriter.toString();
	}
	
	
	
	// TO BYTE ARRAY 
	
	public static byte[] toByteArray(Document document, boolean prettyPrint){
		return toByteArray(document, document, prettyPrint);
	}
	public static byte[] toByteArray(Document document){
		return toByteArray(document, document, false);
	}
	public static byte[] toByteArray(Element element, boolean prettyPrint){
		return toByteArray(element.getOwnerDocument(), element, prettyPrint);
	}
	public static byte[] toByteArray(Element element){
		return toByteArray(element.getOwnerDocument(), element, false);
	}
	public static byte[] toByteArray(Node node, boolean prettyPrint){
		return toByteArray(node.getOwnerDocument(), node, prettyPrint);
	}
	public static byte[] toByteArray(Node node){
		return toByteArray(node.getOwnerDocument(), node, false);
	}
	public static byte[] toByteArray(Document documentImplementation,Node n, boolean prettyPrint){
		return _toByteArray(documentImplementation, n, prettyPrint);
	}
	
	public static byte[] _toByteArray(Document documentImplementation,Node n, boolean prettyPrint) {
		ByteArrayOutputStream bout = null;
		try{
			bout = new ByteArrayOutputStream();
			DOM3LS_XMLUtils._serialize(documentImplementation,n,bout,prettyPrint);
		}finally{
			try{
				bout.flush();
			}catch(Exception eClose){}
			try{
				bout.close();
			}catch(Exception eClose){}
		}
		return bout.toByteArray();
	}
}
