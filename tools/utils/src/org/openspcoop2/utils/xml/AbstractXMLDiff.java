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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.input.ReaderInputStream;
import org.slf4j.Logger;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.XMLUnit;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Classe utilizzabile per ricerche effettuate tramite espressioni XQuery
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractXMLDiff {

	
	@SuppressWarnings("unused")
	private static Logger logger = LoggerWrapperFactory.getLogger(AbstractXMLDiff.class);
	public static void setLogger(Logger logger) {
		AbstractXMLDiff.logger = logger;
	}
	
	// Static options, la libreria XMLUnit permette di definire queste proprietà staticamente.
	private static XMLDiffOptions xmlDiffOptions = new XMLDiffOptions();
	private static XMLDiffImplType implType = XMLDiffImplType.XML_UNIT;
	private static DocumentBuilderFactory dbf_org_w3c_dom_document_impl = null;
	public static XMLDiffImplType getImplType() {
		return AbstractXMLDiff.implType;
	}
	public static void setImplType(XMLDiffImplType implType) {
		AbstractXMLDiff.implType = implType;
	}
	public static XMLDiffOptions getXmlDiffOptions() {
		return AbstractXMLDiff.xmlDiffOptions;
	}
	public static void setXmlDiffOptions(XMLDiffOptions xmlDiffOptions) {
		AbstractXMLDiff.xmlDiffOptions = xmlDiffOptions;
	}
	private static boolean initialized = false;
	private static synchronized void initialize(AbstractXMLUtils instance) throws XMLException{
		if(initialized==false){
			
			dbf_org_w3c_dom_document_impl = instance.newDocumentBuilderFactory();
			dbf_org_w3c_dom_document_impl.setNamespaceAware(true);
			dbf_org_w3c_dom_document_impl.setCoalescing(AbstractXMLDiff.xmlDiffOptions.isIgnoreDiffBetweenTextAndCDATA());
			dbf_org_w3c_dom_document_impl.setIgnoringElementContentWhitespace(AbstractXMLDiff.xmlDiffOptions.isIgnoreWhitespace());
			dbf_org_w3c_dom_document_impl.setIgnoringComments(AbstractXMLDiff.xmlDiffOptions.isIgnoreComments());
			
			if(XMLDiffImplType.XML_UNIT.equals(implType)){
				XMLUnit.setIgnoreAttributeOrder(true);
				XMLUnit.setExpandEntityReferences(true);
				XMLUnit.setIgnoreComments(AbstractXMLDiff.xmlDiffOptions.isIgnoreComments());
				XMLUnit.setIgnoreDiffBetweenTextAndCDATA(AbstractXMLDiff.xmlDiffOptions.isIgnoreDiffBetweenTextAndCDATA());
				XMLUnit.setIgnoreWhitespace(AbstractXMLDiff.xmlDiffOptions.isIgnoreWhitespace());
				XMLUnit.setNormalize(AbstractXMLDiff.xmlDiffOptions.isNormalize());
				XMLUnit.setNormalizeWhitespace(AbstractXMLDiff.xmlDiffOptions.isNormalize());
			}
			
			initialized = true;
		}
	}
	private static synchronized void _initialize(XMLDiffImplType implType,XMLDiffOptions xmlDiffOptions,AbstractXMLUtils instance) throws XMLException{
		AbstractXMLDiff.implType = implType;
		AbstractXMLDiff.xmlDiffOptions = xmlDiffOptions;
		AbstractXMLDiff.initialize(instance);
	}

	
	/* ***** INIT METHOD (ha bisogno degli abstract method) ***** */
	
	public void initialize(XMLDiffImplType implType,XMLDiffOptions xmlDiffOptions) throws XMLException{
		AbstractXMLDiff._initialize(implType,xmlDiffOptions,this.getXMLUtils());
	}
	
	
	/* ***** ABSTRACT METHOD ***** */
	
	public abstract AbstractXMLUtils getXMLUtils();
	public abstract Element readXPathElement(Element contenutoAsElement);
	public abstract void normalizeDocument(Document document);

	
	/* ***** EXCEPTION METHOD ***** */

	private String difference;
	
	public String getDifferenceDetails() throws XMLException {
		if(!XMLDiffImplType.XML_UNIT.equals(implType)){
			throw new XMLException("Difference details permit only on implementation: "+XMLDiffImplType.XML_UNIT);
		}
		return this.difference;
	}

	
	
	
		
	
	/* ***** ENGINE ***** */
	
	private boolean _diff(Object original, Object compare) throws XMLException{
		
		if(initialized==false){
			throw new XMLException("Library not initialized. Invoke initialize method");
		}
		
		if(XMLDiffImplType.XML_UNIT.equals(implType)){
			return this._diffXmlUnit(original, compare);
		}
		else if(XMLDiffImplType.ORG_W3C_DOM_DOCUMENT.equals(implType)){
			return this._diffW3cDomDocument(original, compare);
		}
		else{
			throw new XMLException("Implementation ["+implType+"] not supported");
		}

	}
	
	private boolean _diffXmlUnit(Object original, Object compare) throws XMLException{
		
		DetailedDiff diff = null;
		
		if( (original instanceof Document) || (original instanceof Element) || (original instanceof Node) ){
			// Ottengo anche il compare come Document e li confronto
			Document docOriginal = this._getDiffW3cDomDocument(original, "original");
			Document docCompare = this._getDiffW3cDomDocument(compare, "compare");
			if(AbstractXMLDiff.xmlDiffOptions.isNormalize()){
				normalizeDocument(docOriginal);
				normalizeDocument(docCompare);
			}
			diff = new DetailedDiff(XMLUnit.compareXML(docOriginal,docCompare));
		}
		else if( (original instanceof Reader) && (compare instanceof Reader) ){
			Reader rOriginal = (Reader) original;
			Reader rCompare = (Reader) compare;
			try{
				diff = new DetailedDiff(XMLUnit.compareXML(rOriginal,rCompare));
			}catch(Exception e){
				throw new XMLException(e.getMessage(),e);
			}
		}
		else if( (original instanceof Reader) && (compare instanceof String) ){
			Reader rOriginal = (Reader) original;
			String sCompare = (String) compare;
			try{
				diff = new DetailedDiff(XMLUnit.compareXML(rOriginal,sCompare));
			}catch(Exception e){
				throw new XMLException(e.getMessage(),e);
			}
		}
		else if( (original instanceof String) && (compare instanceof Reader) ){
			String sOriginal = (String) original;
			Reader rCompare = (Reader) compare;
			try{
				diff = new DetailedDiff(XMLUnit.compareXML(sOriginal,rCompare));
			}catch(Exception e){
				throw new XMLException(e.getMessage(),e);
			}
		}
		else if( (original instanceof String) && (compare instanceof String) ){
			String sOriginal = (String) original;
			String sCompare = (String) compare;
			try{
				diff = new DetailedDiff(XMLUnit.compareXML(sOriginal,sCompare));
			}catch(Exception e){
				throw new XMLException(e.getMessage(),e);
			}
		}
		else if( 
				( (original instanceof InputStream) || (original instanceof File) ) 
				&& 
				( (compare instanceof InputStream) || (compare instanceof File) ) 
				){
			InputSource isOriginal = null;
			InputSource isCompare = null;
			FileInputStream finOriginal = null;
			FileInputStream finCompare = null;
			try{
				
				if(original instanceof InputStream){
					isOriginal = new InputSource((InputStream)original);
				}
				else if(original instanceof File){
					finOriginal = new FileInputStream((File)original);
					isOriginal = new InputSource(finOriginal);
				}
				
				if(compare instanceof InputStream){
					isCompare = new InputSource((InputStream)compare);
				}
				else if(compare instanceof File){
					finCompare = new FileInputStream((File)compare);
					isCompare = new InputSource(finCompare);
				}
				
				diff = new DetailedDiff(XMLUnit.compareXML(isOriginal,isCompare));
				
			}catch(Exception e){
				throw new XMLException(e.getMessage(),e);
			}finally{
				try{
					if(finOriginal!=null){
						finOriginal.close();
					}
				}catch(Exception eClose){}
				try{
					if(finCompare!=null){
						finCompare.close();
					}
				}catch(Exception eClose){}
			}
		}
		else{
			// Ottengo anche il compare come Document e li confronto
			Document docOriginal = this._getDiffW3cDomDocument(original, "original");
			Document docCompare = this._getDiffW3cDomDocument(compare, "compare");
			if(AbstractXMLDiff.xmlDiffOptions.isNormalize()){
				normalizeDocument(docOriginal);
				normalizeDocument(docCompare);
			}
			diff = new DetailedDiff(XMLUnit.compareXML(docOriginal,docCompare));
		}
		
		
		if(diff.identical()==false){
			StringBuffer bfDifferences = new StringBuffer();
			List<?> allDifferences = diff.getAllDifferences();
			bfDifferences.append("allDifferences: "+ allDifferences.size());
			bfDifferences.append("\n");
			int index = 1;
	        for (Object object : allDifferences) {
	        	 if(object instanceof Difference){
	        		 Difference d = (Difference) object;
	        		 bfDifferences.append("Diff-"+index+": "+d.toString());
	        		 bfDifferences.append("\n");
	        	 }
	        	 index++;
			}	       
	        this.difference = bfDifferences.toString();
		}
		return diff.identical();
		
	}
	
	private boolean _diffW3cDomDocument(Object original, Object compare) throws XMLException{
		
		Document docOriginal = this._getDiffW3cDomDocument(original, "original");
		Document docCompare = this._getDiffW3cDomDocument(compare, "compare");
		if(AbstractXMLDiff.xmlDiffOptions.isNormalize()){
			normalizeDocument(docOriginal);
			normalizeDocument(docCompare);
		}
		return docOriginal.isEqualNode(docCompare);
	}
	
	private Document _getDiffW3cDomDocument(Object o,String parameterName) throws XMLException{
		
		if(o==null){
			throw new XMLException("Object is null for parameter ["+parameterName+"]");
		}
		
		if(o instanceof Document){
			return (Document) o;
		}
		else if(o instanceof Element){
			return ((Element) o).getOwnerDocument();
		}
		else if(o instanceof Node){
			return ((Node) o).getOwnerDocument();
		}
		else if( 
				(o instanceof String) ||
				(o instanceof File) ||
				(o instanceof InputStream) ||
				(o instanceof Reader)
				){
			ReaderInputStream ris = null;
			try{
				DocumentBuilder db = AbstractXMLDiff.dbf_org_w3c_dom_document_impl.newDocumentBuilder();
				if(o instanceof String){
					byte [] b = ((String)o).getBytes();
					return db.parse(new ByteArrayInputStream(b));
				}
				else if(o instanceof File){
					return db.parse((File)o);
				}
				else if(o instanceof InputStream){
					return db.parse((InputStream)o);
				}
				else if(o instanceof Reader){
					Reader r = (Reader) o;
					ris = new ReaderInputStream(r,Charset.forName("UTF-8"));
					return db.parse( ris );
				}
				else{
					throw new XMLException("Object type ["+o.getClass().getName()+"] not supported ?? for parameter ["+parameterName+"]");
				}
			}catch(Exception e){
				throw new XMLException("Object type ["+o.getClass().getName()+"] parser error for parameter ["+parameterName+"]: "+e.getMessage(),e);
			}finally{
				try{
					if(ris!=null){
						ris.close();
					}
				}catch(Exception eClose){}
			}
		}
		else{
			throw new XMLException("Object type ["+o.getClass().getName()+"] not supported for parameter ["+parameterName+"]");
		}
	}

	
	
	/* ***** PUBLIC METHOD (SRC as Node) ***** */
	
	public boolean diff(String original, String compare)throws XMLException{
		return this._diff(original, compare);
	}
	public boolean diff(File original, File compare)throws XMLException{
		return this._diff(original, compare);
	}
	public boolean diff(InputStream original, InputStream compare)throws XMLException{
		return this._diff(original, compare);
	}
	public boolean diff(Reader original, Reader compare)throws XMLException{
		return this._diff(original, compare);
	}
	public boolean diff(Document original, Document compare)throws XMLException{
		return this._diff(original, compare);
	}
	public boolean diff(Element original, Element compare)throws XMLException{
		return this._diff(original, compare);
	}
	public boolean diff(Node original, Node compare)throws XMLException{
		return this._diff(original, compare);
	}
	public boolean diff(Object original, Object compare)throws XMLException{
		return this._diff(original, compare);
	}


}