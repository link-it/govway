/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import jakarta.xml.soap.SOAPElement;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsMultiException;
import org.slf4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Classe utilizzabile per ricerche effettuate tramite espressioni XPath
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractXPathExpressionEngine {

	
	
	private static Logger logger = LoggerWrapperFactory.getLogger(AbstractXPathExpressionEngine.class);
	public static void setLogger(Logger logger) {
		AbstractXPathExpressionEngine.logger = logger;
	}
	
	
	/*
   
	Per la sintassi vedi: http://www.w3.org/TR/xpath

    Esempio1: /rubrica/persone/nome/text()
    restituisce il valore del tag nome partendo dalla radice del xml (path assoluto)

    Esempio2:
    //nome/text()
    restituisce il valore del tag nome (path relativo)

    Esempio3
    /rubrica/persone/* /text() 
    restituisce il valore di tutti i nodi figli del tag persone

    Esempio4
    //citta/@targa
    restituisce per ogni tag citta il valore dell'attributo targa

    Esempio5
    //persona[position()=3]/text()
    restituisce il terzo nodo persona

    Esempio6
    //citta[@targa='mi']/text()
    restituisce il valore della citta che ha come valore dell'attributo targa, mi
	
	Esempio7
	http://saxon.sourceforge.net/saxon6.5.3/expressions.html
	Ci sono le funzioni per content based
	  substring-after(name(//soap:Body/*),":")
	Prende il nome dell'elemento dopo il Body. Sbustring per non prendere il namespace
	Devono essere usati per il nome di un elemento.
	
	Altri esempi per avere il nome del primo child.
	Il piu' corretto e' il seguente, il quale non richiede di conoscere ne namespace del soap envelope, ne namespace del primo child, e funziona sia che il child possiede che non possieda un prefix:*/
	// Viene preso il local name del primo elemento figlio dell'ultimo figlio dell'envelope. L'ultimo figlio dell'envelope e' chiaramente il Body (per evitare casi in cui nel messaggio vi sia l'header)
	//ESPRESSIONE:	local-name(/*/*[last()]/*)
	
	// Anche la seguente funziona per trovare il child pero' funziona SOLO se il child possiede un prefix.
	// substring-after(name(/*/*[last()]/*),&#34;:&#34;)
	
	/*
	Esempio8
	Per concatenare piu' risultati
	concat(//citta[@targa='mi']/text(),//citta[@targa='bo']/text())
	*/
	
	// Altri esempi:
	// http://msdn.microsoft.com/en-us/library/ms256086.aspx
	
	
	/* ---------- METODI RITORNANO STRINGHE -------------- */
	
	/**
	 * Estrae dal contenuto passato come parametro, l'oggetto identificato dal parametro pattern e dal tipo returnType
	 * 
	 * @param element Elemento SOAP da cui estrarre l'oggetto
	 * @param dnc DynamicNamespaceContext 
	 * @param pattern Pattern che identifica l'oggetto
	 * @return Oggetto identificato dal parametro pattern e dal tipo returnType
	 * @throws XPathException
	 * @throws XPathNotFoundException
	 */
	public String getStringMatchPattern(SOAPElement element, DynamicNamespaceContext dnc,String pattern) 
		throws XPathException,XPathNotFoundException,XPathNotValidException{
		return (String) this.getMatchPattern(element, dnc, pattern, XPathReturnType.STRING);
	}
	
	/**
	 * Estrae dal contenuto passato come parametro, l'oggetto identificato dal parametro pattern e dal tipo returnType
	 * 
	 * @param document Document da cui estrarre l'oggetto
	 * @param dnc DynamicNamespaceContext 
	 * @param pattern Pattern che identifica l'oggetto
	 * @return Oggetto identificato dal parametro pattern e dal tipo returnType
	 * @throws XPathException
	 * @throws XPathNotFoundException
	 */
	public String getStringMatchPattern(Document document, DynamicNamespaceContext dnc,String pattern) 
		throws XPathException,XPathNotFoundException,XPathNotValidException{
		return (String) this.getMatchPattern(document.getDocumentElement(), dnc, pattern, XPathReturnType.STRING);
	}
	
	/**
	 * Estrae dal contenuto passato come parametro, l'oggetto identificato dal parametro pattern e dal tipo returnType
	 * 
	 * @param element Elemento da cui estrarre l'oggetto
	 * @param dnc DynamicNamespaceContext 
	 * @param pattern Pattern che identifica l'oggetto
	 * @return Oggetto identificato dal parametro pattern e dal tipo returnType
	 * @throws XPathException
	 * @throws XPathNotFoundException
	 */
	public String getStringMatchPattern(Element element, DynamicNamespaceContext dnc,String pattern) 
		throws XPathException,XPathNotFoundException,XPathNotValidException{
		return (String) this.getMatchPattern(element, dnc, pattern, XPathReturnType.STRING);
	}
	
	/**
	 * Estrae dal contenuto passato come parametro, l'oggetto identificato dal parametro pattern e dal tipo returnType
	 * 
	 * @param contenuto Contenuto da cui estrarre l'oggetto
	 * @param dnc DynamicNamespaceContext 
	 * @param pattern Pattern che identifica l'oggetto
	 * @return Oggetto identificato dal parametro pattern e dal tipo returnType
	 * @throws XPathException
	 * @throws XPathNotFoundException
	 */
	public String getStringMatchPattern(String contenuto, DynamicNamespaceContext dnc,String pattern) 
		throws XPathException,XPathNotFoundException,XPathNotValidException{
		return (String) this.getMatchPattern(contenuto, dnc, pattern, XPathReturnType.STRING);
	}
	
	
	
	/* ---------- METODI GENERICI -------------- */
	
	public abstract String getAsString(SOAPElement element);
	public abstract AbstractXMLUtils getXMLUtils();
	
	/**
	 * Estrae dal contenuto passato come parametro, l'oggetto identificato dal parametro pattern e dal tipo returnType
	 * 
	 * @param element Elemento SOAP da cui estrarre l'oggetto
	 * @param dnc DynamicNamespaceContext 
	 * @param pattern Pattern che identifica l'oggetto
	 * @param returnType Tipo dell'oggetto ritornato
	 * @return Oggetto identificato dal parametro pattern e dal tipo returnType
	 * @throws XPathException
	 * @throws XPathNotFoundException
	 */
	public Object getMatchPattern(SOAPElement element, DynamicNamespaceContext dnc,String pattern, XPathReturnType returnType) 
		throws XPathException,XPathNotFoundException,XPathNotValidException{
		
		if(element==null)
			throw new XPathException("element xml undefined");
		
		return this._engine_getMatchPattern(element, null, dnc, pattern, returnType);
	}
	
	/**
	 * Estrae dal contenuto passato come parametro, l'oggetto identificato dal parametro pattern e dal tipo returnType
	 * 
	 * @param document Document da cui estrarre l'oggetto
	 * @param dnc DynamicNamespaceContext 
	 * @param pattern Pattern che identifica l'oggetto
	 * @param returnType Tipo dell'oggetto ritornato
	 * @return Oggetto identificato dal parametro pattern e dal tipo returnType
	 * @throws XPathException
	 * @throws XPathNotFoundException
	 */
	public Object getMatchPattern(Document document, DynamicNamespaceContext dnc,String pattern, XPathReturnType returnType) 
		throws XPathException,XPathNotFoundException,XPathNotValidException{
		
		if(document==null)
			throw new XPathException("document xml undefined");
		if(document.getDocumentElement()==null)
			throw new XPathException("document.element xml undefined");
		
		return this._engine_getMatchPattern(document.getDocumentElement(), null, dnc, pattern, returnType);
		
	}
	
	/**
	 * Estrae dal contenuto passato come parametro, l'oggetto identificato dal parametro pattern e dal tipo returnType
	 * 
	 * @param element Elemento da cui estrarre l'oggetto
	 * @param dnc DynamicNamespaceContext 
	 * @param pattern Pattern che identifica l'oggetto
	 * @param returnType Tipo dell'oggetto ritornato
	 * @return Oggetto identificato dal parametro pattern e dal tipo returnType
	 * @throws XPathException
	 * @throws XPathNotFoundException
	 */
	public Object getMatchPattern(Element element, DynamicNamespaceContext dnc,String pattern, XPathReturnType returnType) 
		throws XPathException,XPathNotFoundException,XPathNotValidException{
		
		if(element==null)
			throw new XPathException("element xml undefined");
		
		return this._engine_getMatchPattern(element, null, dnc, pattern, returnType);
	}
	
	/**
	 * Estrae dal contenuto passato come parametro, l'oggetto identificato dal parametro pattern e dal tipo returnType
	 * 
	 * @param content Contenuto da cui estrarre l'oggetto
	 * @param dnc DynamicNamespaceContext 
	 * @param pattern Pattern che identifica l'oggetto
	 * @param returnType Tipo dell'oggetto ritornato
	 * @return Oggetto identificato dal parametro pattern e dal tipo returnType
	 * @throws XPathException
	 * @throws XPathNotFoundException
	 */
	public Object getMatchPattern(String content, DynamicNamespaceContext dnc,String pattern, XPathReturnType returnType) 
		throws XPathException,XPathNotFoundException,XPathNotValidException{
		
		if(content==null)
			throw new XPathException("content undefined");
		
		return this._engine_getMatchPattern(null, content, dnc, pattern, returnType);
		
	}
	
	
	
	/* ---------- ENGINE -------------- */
	
	public abstract Element readXPathElement(Element contenutoAsElement);
	
	//private static Integer synchronizedObjectForBugFWK005ParseXerces = Integer.valueOf(1); // vedi test TestBugFWK005ParseXerces 
	private static final org.openspcoop2.utils.Semaphore lockObjectForBugFWK005ParseXerces = new org.openspcoop2.utils.Semaphore("BugFWK005ParseXerces");
	private Object _engine_getMatchPattern(
			Element contenutoAsElement, String contenutoAsString, 
			DynamicNamespaceContext dncPrivate,String pattern, XPathReturnType returnType) 
			throws XPathException,XPathNotFoundException,XPathNotValidException{

		if( (pattern == null) || (pattern.length() == 0))
			throw new XPathNotFoundException("Pattern di ricerca non fornito");
		if(contenutoAsElement == null && contenutoAsString==null)
			throw new XPathNotFoundException("Contenuto su cui effettuare la ricerca non fornito");
		if(contenutoAsElement != null && contenutoAsString!=null)
			throw new XPathNotFoundException("Contenuto su cui effettuare la ricerca ambiguo");

		
		// Validazione espressione XPAth fornita
		pattern = pattern.trim();
		this.validate(pattern);	
		
		
		try{
			
			// 1. Instantiate an XPathFactory.
			javax.xml.xpath.XPathFactory factory = this.getXMLUtils().getXPathFactory();
	
			// 2. Use the XPathFactory to create a new XPath object
			javax.xml.xpath.XPath xpath = factory.newXPath();
			if(xpath==null){
				throw new Exception("Costruzione xpath non riuscita");
			}
			
			// 3. Set the Namespaces
			
			// 3.1. Clone dnc, verra' modificato in convertNamespaces
			DynamicNamespaceContext dnc = null;
			if(dncPrivate!=null){
				dnc = (DynamicNamespaceContext) dncPrivate.clone();
			}
			
			// 3.2 // Risoluzione namespace {http:///}
			// NOTA: da utilizzare con {http://namespace}localName 
			// BugFix: se per errore viene utilizzato {http://namespace}:localName prima di effettuare la risoluzione del namespace viene eliminato il ':'
			if(dnc!=null){
				pattern = pattern.replaceAll("}:", "}");
				pattern = this.convertNamespaces(pattern, dnc);
			}
			//System.out.println("PATTERN: ["+pattern+"]");

			// 3.3 set dnc in xpath
			if(dnc!=null)
				xpath.setNamespaceContext(dnc);
			
			// 3.4
			// Bug fix: se il contenuto possiede una namespace associato al prefix di default, poi l'xpath engine non trova l'elemento.
			// es. <prova xmlns="www.namespace">TEST</prova> con xpath /prova/text() non funziona.
//			if(dnc.getNamespaceURI(javax.xml.XMLConstants.DEFAULT_NS_PREFIX).equals(javax.xml.XMLConstants.NULL_NS_URI) == false ){
//				//System.out.println("PATCH CONTENUTO PER NAMESPACE DEFAULT: "+dnc.getNamespaceURI(javax.xml.XMLConstants.DEFAULT_NS_PREFIX));
//				contenuto = contenuto.replace("xmlns=\""+dnc.getNamespaceURI(javax.xml.XMLConstants.DEFAULT_NS_PREFIX)+"\"", "");
//			}	
			// !!!! NOTA Il fix sopra indicato non era corretto.
			// e' giusto che non lo trova se viene fornito un xpath senza prefisso, poiche' si sta cercando un elemento che non appartiene a nessun namespace.
			// L'xpath sopra funzionera' su un xml definito come <prova>TEST</prova>
			//
			// Ulteriore spiegazione:
			// But since the nodes you are trying to get use a default namespace, without a prefix, using plain XPath, 
			// you can only access them by the local-name() and namespace-uri() attributes. Examples:
			// *[local-name()="HelloWorldResult"]/text()
			// Or:
			// *[local-name()="HelloWorldResult" and namespace-uri()='http://tempuri.org/']/text()
			// Or:
			//*[local-name()="HelloWorldResponse" and namespace-uri()='http://tempuri.org/']/*[
			//
			// Siccome la modalita' di utilizzare il local-name e il namespace-uri in un xpath e' elaborioso, abbiamo aggiunto in openspcoop2
			// la possibilita' di indicare il namespace {namespace} che verra' convertito in un prefix interno utile al funzionamento dell'xpath.
			// Il pattern da fornire  che funziona sull'xml indicato sopra e' /{www.namespace}prova/text()
			// Per ulteriori dettagli vedi metodo convertNamespaces utilizzato in 3.2, all'interno del metodo viene fornita una descrizione dettagliata.
			
				
			// Concatenazione openspcoop.
			if(pattern.startsWith("concat_openspcoop") && pattern.endsWith(")")){
				
				// Check compatibilita' concat_openspcoop
				if(returnType.equals(XPathReturnType.STRING)==false){
					throw new XPathException("Funzione concat_openspcoop non compatibile con un tipo di ritorno: "+returnType.toString());
				}
				
				// Fix , la funzione concat non ritorna eccezione se non riesce a risolvere qualche espressione, e contiene cmq delle costanti.
				// La concatenazione openspcoop, invece ritornera' errore.
				String param = pattern.substring("concat_openspcoop(".length(),pattern.length()-1);
				String [] params =param.split(",");
				StringBuilder bfResult = new StringBuilder();
				for(int i=0; i<params.length;i++){
					
					// Check se abbiamo una costante od una espressione da valutare
					if(params[i].startsWith("\"") && params[i].endsWith("\"")){
					
						// COSTANTE
						bfResult.append(params[i].substring(1,(params[i].length()-1)));
					
					}else{
						
						// 4. Reader
						Reader reader = null;
						if(contenutoAsString!=null){
							try{
								reader = new StringReader(contenutoAsString);
							}catch(Exception e){
								try{
									if( reader != null )
										reader.close();
								} catch(Exception er) {
									// close
								}
								throw e;
							}		
						}
						
						// ESPRESSIONE
						// 5. Compile an XPath string into an XPathExpression
						javax.xml.xpath.XPathExpression expression = null;
						try{
							expression = xpath.compile(params[i]);
						}catch(Exception e){
							if(Utilities.existsInnerMessageException(e, "Prefix must resolve to a namespace", true)){
								// e' stato usato un prefisso nell'espressione XPath che non e' risolvibile accedendo ai namespaces del messaggio (dnc)
								throw new XPathNotFoundException("Espressione XPATH contenuta in concat_openspcoop ("+params[i]+") non applicabile al messaggio: "+Utilities.getInnerMessageException(e, "Prefix must resolve to a namespace", true));
							}
							else if(Utilities.existsInnerException(e,javax.xml.transform.TransformerException.class)){
								throw new Exception("Compilazione dell'espressione XPATH contenuta in concat_openspcoop ("+params[i]+") ha causato un errore ("+Utilities.getInnerException(e, javax.xml.transform.TransformerException.class).getMessage()+")",e);
							}else{
								if(e.getCause()!=null){
									throw new Exception("Compilazione dell'espressione XPATH contenuta in concat_openspcoop ("+params[i]+") ha causato un errore ("+(Utilities.getLastInnerException(e.getCause())).getMessage()+")",e);
								}else{
									throw new Exception("Compilazione dell'espressione XPATH contenuta in concat_openspcoop ("+params[i]+") ha causato un errore ("+e.getMessage()+")",e);
								}
							}	
						}
						if(expression==null){
							throw new Exception("Costruzione XPathExpression non riuscita per espressione contenuta in concat_openspcoop ("+params[i]+")");
						}
				
												
						// 6. Evaluate the XPath expression on an input document
						String result = null;
						try{
							if(reader!=null) {
								//synchronized (AbstractXPathExpressionEngine.synchronizedObjectForBugFWK005ParseXerces) { // vedi test TestBugFWK005ParseXerces
								lockObjectForBugFWK005ParseXerces.acquire("concat_openspcoopEvaluate");
								try {
									result = expression.evaluate(new org.xml.sax.InputSource(reader));
								}finally {
									lockObjectForBugFWK005ParseXerces.release("concat_openspcoopEvaluate");
								}
							}
							else { 
								result = expression.evaluate(this.readXPathElement(contenutoAsElement));
							}
						}catch(Exception e){
							if(Utilities.existsInnerException(e,"com.sun.org.apache.xpath.internal.XPathException")){
								throw new Exception("Valutazione dell'espressione XPATH contenuta in concat_openspcoop ("+params[i]+") ha causato un errore ("+Utilities.getInnerException(e, "com.sun.org.apache.xpath.internal.XPathException").getMessage()+")",e);
							}
							else if(Utilities.existsInnerException(e,org.apache.xpath.XPathException.class)){
								throw new Exception("Valutazione dell'espressione XPATH contenuta in concat_openspcoop ("+params[i]+") ha causato un errore ("+Utilities.getInnerException(e, org.apache.xpath.XPathException.class).getMessage()+")",e);
							}
							else{
								if(e.getCause()!=null){
									throw new Exception("Valutazione dell'espressione XPATH contenuta in concat_openspcoop ("+params[i]+") ha causato un errore ("+(Utilities.getLastInnerException(e.getCause())).getMessage()+")",e);
								}else{
									throw new Exception("Valutazione dell'espressione XPATH contenuta in concat_openspcoop ("+params[i]+") ha causato un errore ("+e.getMessage()+")",e);
								}
							}
						}
						if(reader!=null)
							reader.close();
				
						if(result == null || "".equals(result)){
							// DEVE ESSERE LANCIATA ECCEZIONE, E' PROPRIO LA FUNZIONALITA DI OPENSPCOOP!
							// Il Concat normale non lancia eccezione e ritorna stringa vuota.
							// Verificato con cxf soap engine
							AbstractXPathExpressionEngine.logger.debug("nessun match trovato per l'espressione xpath contenuta in concat_openspcoop ("+params[i]+")");
							throw new XPathNotFoundException("nessun match trovato per l'espressione xpath contenuta in concat_openspcoop ("+params[i]+")");
						}
						else{
							bfResult.append(result);
						}
					}
				}
				
				if(bfResult.length()<=0){
					StringBuilder bfDNC = new StringBuilder();
					Enumeration<?> en = dnc.getPrefixes();
					while (en.hasMoreElements()) {
						String prefix = (String) en.nextElement();
						bfDNC.append("\n\t- ");
						bfDNC.append("[").append(prefix).append("]=[").append(dnc.getNamespaceURI(prefix)).append("]");
					}
					throw new XPathNotFoundException("nessun match trovato per l'espressione xpath (concat_openspcoop) ["+pattern+"] DynamicNamespaceContext:"+bfDNC.toString());
					
				}else{
					return bfResult.toString();
				}

			}else{
				
				// 4. Reader
				Reader reader = null;
				if(contenutoAsString!=null){
					try{
						reader = new StringReader(contenutoAsString);
					}catch(Exception e){
						try{
							if( reader != null )
								reader.close();
						} catch(Exception er) {
							// close
						}
						throw e;
					}		
				}
				
				
				// 5. Compile an XPath string into an XPathExpression
				javax.xml.xpath.XPathExpression expression = null;
				try{
					expression = xpath.compile(pattern);
				}catch(Exception e){
					if(Utilities.existsInnerMessageException(e, "Prefix must resolve to a namespace", true)){
						// e' stato usato un prefisso nell'espressione XPath che non e' risolvibile accedendo ai namespaces del messaggio (dnc)
						throw new XPathNotFoundException("Espressione XPATH non applicabile al messaggio: "+Utilities.getInnerMessageException(e, "Prefix must resolve to a namespace", true));
					}
					else if(Utilities.existsInnerException(e,javax.xml.transform.TransformerException.class)){
						throw new Exception("Compilazione dell'espressione XPATH ha causato un errore ("+Utilities.getInnerException(e, javax.xml.transform.TransformerException.class).getMessage()+")",e);
					}else{
						if(e.getCause()!=null){
							throw new Exception("Compilazione dell'espressione XPATH ha causato un errore ("+(Utilities.getLastInnerException(e.getCause())).getMessage()+")",e);
						}else{
							throw new Exception("Compilazione dell'espressione XPATH ha causato un errore ("+e.getMessage()+")",e);
						}
					}
				}
				if(expression==null){
					throw new Exception("Costruzione XPathExpression non riuscita");
				}
		
				// 6. Evaluate the XPath expression on an input document
				Object result = null;
				try{
					if(reader!=null) {
						//synchronized (AbstractXPathExpressionEngine.synchronizedObjectForBugFWK005ParseXerces) { // vedi test TestBugFWK005ParseXerces
						lockObjectForBugFWK005ParseXerces.acquire("standardEvaluate");
						try {
							result = expression.evaluate(new org.xml.sax.InputSource(reader),returnType.getValore());
						}finally {
							lockObjectForBugFWK005ParseXerces.release("standardEvaluate");
						}
					}else{
						result = expression.evaluate(this.readXPathElement(contenutoAsElement),returnType.getValore());
					}
				}catch(Exception e){
					if(Utilities.existsInnerException(e,"com.sun.org.apache.xpath.internal.XPathException")){
						throw new Exception("Valutazione dell'espressione XPATH ha causato un errore ("+Utilities.getInnerException(e, "com.sun.org.apache.xpath.internal.XPathException").getMessage()+")",e);
					}
					else if(Utilities.existsInnerException(e,org.apache.xpath.XPathException.class)){
						throw new Exception("Valutazione dell'espressione XPATH ha causato un errore ("+Utilities.getInnerException(e, org.apache.xpath.XPathException.class).getMessage()+")",e);
					}
					else{
						if(e.getCause()!=null){
							throw new Exception("Valutazione dell'espressione XPATH ha causato un errore ("+(Utilities.getLastInnerException(e.getCause())).getMessage()+")",e);
						}else{
							throw new Exception("Valutazione dell'espressione XPATH ha causato un errore ("+e.getMessage()+")",e);
						}
					}
				}
				if(reader!=null)
					reader.close();
		
				boolean notFound = false;
				if(result == null){
					notFound = true;
				}
				else if(result instanceof String){
					if("".equals(result)){
						notFound = true;
					}
				}
				else if( (XPathReturnType.NODESET.equals(returnType)) && (result instanceof NodeList)){
					if(((NodeList)result).getLength()<=0){
						notFound=true;
					}
				}
				
				if(notFound){
					//log.info("ContentBased, nessun match trovato");
					StringBuilder bfDNC = new StringBuilder();
					Enumeration<?> en = dnc.getPrefixes();
					while (en.hasMoreElements()) {
						String prefix = (String) en.nextElement();
						bfDNC.append("\n\t- ");
						bfDNC.append("[").append(prefix).append("]=[").append(dnc.getNamespaceURI(prefix)).append("]");
					}
					throw new XPathNotFoundException("nessun match trovato per l'espressione xpath ["+pattern+"] DynamicNamespaceContext:"+bfDNC.toString());
				}
				
				return result; 
			
			}
		
		}catch(XPathNotFoundException ex){
			throw ex;
		}catch(Exception e){
			throw new XPathException("getMatchPattern pattern["+pattern+"] error: "+e.getMessage(),e);
		}
	} 
	
	
	
	
	
	/* ---------- VALIDATORE -------------- */
	
	public void validate(String path) throws XPathNotValidException{
		try{
			
			path = path.trim();
				
			// Instantiate an XPathFactory.
			javax.xml.xpath.XPathFactory factory = this.getXMLUtils().getXPathFactory();
	
			// Use the XPathFactory to create a new XPath object
			javax.xml.xpath.XPath xpath = factory.newXPath();
			if(xpath==null){
				throw new Exception("Costruzione xpath non riuscita");
			}
			
			// 3. Repleace the Namespaces:
			path = path.replaceAll("}:", "}");
			int index = 0;
			while (path.indexOf("{")!=-1){
				int indexStart = path.indexOf("{");
				int indexEnd = path.indexOf("}");
				if(indexEnd==-1){
					throw new Exception("{ utilizzato senza la rispettiva chiusura }");
				}
				String namespace = path.substring(indexStart+"{".length(),indexEnd);
				if(namespace==null || namespace.equals("")){
					throw new Exception("Namespace non indicato tra {}");
				}
				String prefix = "a"+index+":";
				index++;
				path = path.replace("{"+namespace+"}", prefix);
			}
			//System.out.println("PATTERN PER VALIDAZIONE: ["+path+"]");
			
			if(path.startsWith("concat_openspcoop")){
				// funzione concat
				String param = path.substring("concat_openspcoop(".length(),path.length()-1);
				String [] params =param.split(",");
				for(int i=0; i<params.length;i++){
					
					// Check se abbiamo una costante od una espressione da valutare
					if(params[i].startsWith("\"") && params[i].endsWith("\"")){
					
						// COSTANTE
					
					}else{
						
						javax.xml.xpath.XPathExpression expression = null;
						try{
							expression = xpath.compile(params[i]);
						}catch(Exception e){
							if(Utilities.existsInnerException(e,javax.xml.transform.TransformerException.class)){
								throw new Exception("Compilazione dell'espressione XPATH contenuta in concat_openspcoop ("+params[i]+") ha causato un errore ("+Utilities.getInnerException(e, javax.xml.transform.TransformerException.class).getMessage()+")",e);
							}else{
								if(e.getCause()!=null){
									throw new Exception("Compilazione dell'espressione XPATH contenuta in concat_openspcoop ("+params[i]+") ha causato un errore ("+(Utilities.getLastInnerException(e.getCause())).getMessage()+")",e);
								}else{
									throw new Exception("Compilazione dell'espressione XPATH contenuta in concat_openspcoop ("+params[i]+") ha causato un errore ("+e.getMessage()+")",e);
								}
							}			
						}
						if(expression==null){
							throw new Exception("Costruzione XPathExpression non riuscita per espressione contenuta in concat_openspcoop ("+params[i]+")");
						}
						
					}
				}
			}
			else{
				// Compile an XPath string into an XPathExpression
				javax.xml.xpath.XPathExpression expression = null;
				try{
					expression = xpath.compile(path);
				}catch(Exception e){
					if(Utilities.existsInnerException(e,javax.xml.transform.TransformerException.class)){
						throw new Exception("Compilazione dell'espressione XPATH ha causato un errore ("+Utilities.getInnerException(e, javax.xml.transform.TransformerException.class).getMessage()+")",e);
					}else{
						if(e.getCause()!=null){
							throw new Exception("Compilazione dell'espressione XPATH ha causato un errore ("+(Utilities.getLastInnerException(e.getCause())).getMessage()+")",e);
						}else{
							throw new Exception("Compilazione dell'espressione XPATH ha causato un errore ("+e.getMessage()+")",e);
						}
					}	
				}
				if(expression==null){
					throw new Exception("Costruzione XPathExpression non riuscita");
				}
			}
		}catch(Exception e){
			throw new XPathNotValidException("Validazione dell'xpath indicato ["+path+"] fallita: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	
	/* -------------- UTILITIES PUBBLICHE ------------------- */
	
	/**
	 * Ritorna la rappresentazione testuale di una lista di nodi
	 * 
	 * @param rootNode
	 * @return rappresentazione testuale di una lista di nodi
	 */
	public String toString(NodeList rootNode){
		
		StringBuilder resultBuffer = new StringBuilder();
		this.toString(rootNode,resultBuffer,1);
		return resultBuffer.toString();
		
	}
	public List<String> toList(NodeList rootNode){
		
		return this.toList(rootNode,1);
		
	}
	

	
	/* -------------- UTILITIES PRIVATE ------------------- */
	private int _prefixIndex = 0;
	private synchronized int _getNextPrefixIndex(){
		this._prefixIndex++;
		return this._prefixIndex;
	}
	private static final String AUTO_PREFIX = "_op2PrefixAutoGeneratedIndex";
	private String convertNamespaces(String path,DynamicNamespaceContext dnc)throws UtilsException{
		while (path.indexOf("{")!=-1){
			int indexStart = path.indexOf("{");
			int indexEnd = path.indexOf("}");
			if(indexEnd==-1){
				throw new UtilsException("Errore durante l'interpretazione del valore ["+path+"]: { utilizzato senza la rispettiva chiusura }");
			}
			String namespace = path.substring(indexStart+"{".length(),indexEnd);
			String prefix = dnc.getPrefix(namespace);
			//System.out.println("PREFIX["+prefix+"]->NS["+namespace+"]");
			if(prefix!=null && !javax.xml.XMLConstants.NULL_NS_URI.equals(prefix)){
				prefix = prefix+":";
			}
			else{
				// aggiungo un prefisso nuovo anche nel caso di prefix "" per gestire la problematica che si presente in questo caso:
				// String xmlCampione = "<prova xmlns=\"www.namespace\">TEST</prova>";
				// NON FUNZIONA con un pattern = "/prova/text()"; (poiche' l'elemento prova ha un namespace, quello di default, mentre nell'xpath non viene indicato)
				// 				Questo pattern sarebbe quello generato anche dopo la conversione dei namespace se il prefisso e' javax.xml.XMLConstants.NULL_NS_URI
				// FUNZIONA con un pattern = "*[local-name()='prova' and namespace-uri()='www.namespace']/text()"; ma e' piu' elaborato
				// FUNZIONA se registriamo un nuovo prefix da utilizzare solo nel pattern, ad esempio con un pattern = "/_op2PrefixAutoGeneratedIndexNUMBER:prova/text()"
				//			dove il prefisso auto-generato _op2PrefixAutoGeneratedIndexNUMBER e' associato al namespace www.namespace nel dnc.
				prefix = AbstractXPathExpressionEngine.AUTO_PREFIX + this._getNextPrefixIndex();
				dnc.addNamespace(prefix, namespace);
				prefix = prefix+":";
				//System.out.println("CASO SPECIALEEEEEEEEEEEEEEEE");
			}
			path = path.replace("{"+namespace+"}", prefix);
		}
		return path;
	}
	
	private void toString(NodeList rootNode,StringBuilder resultBuffer, int livello){
		
//		for(int index = 0; index < rootNode.getLength();index ++){
//			Node aNode = rootNode.item(index);
//			try {
//				System.out.println("DEBUG (livello:"+livello+"): "+getXMLUtils().toString(aNode));
//			}catch(Exception e) {}
//		}
		
		if (rootNode.getLength()==1 && 
				(rootNode.item(0).getNodeType()==Node.TEXT_NODE || rootNode.item(0).getNodeType()==Node.ATTRIBUTE_NODE) && 
				resultBuffer.length()==0){
			//System.out.println("TEXT ["+rootNode.item(0).getNodeType()+"] confronto con ["+Node.TEXT_NODE+"] or ["+Node.ATTRIBUTE_NODE+"]");
			// BugEntityReferences: il metodo getTextContent e anche getNodeValue risolve le entity references
			//String textNodeValue = rootNode.item(0).getTextContent();
			String textNodeValue = null;
			try {
				textNodeValue = _getTextValue_fixEntityReferencies(rootNode.item(0));
			}catch(Throwable t) {
				textNodeValue = rootNode.item(0).getTextContent();
			}
			resultBuffer.append(textNodeValue);
			return;
		}
		boolean findElementoRisultatoMultiplo = false;
		for(int index = 0; index < rootNode.getLength();index ++){
			Node aNode = rootNode.item(index);
			//System.out.println("NODE["+index+"] ["+aNode.getNodeType()+"] confronto con  ["+Node.ELEMENT_NODE+"]");
			if (aNode.getNodeType() == Node.ELEMENT_NODE){
				NodeList childNodes = aNode.getChildNodes(); 
				
				if (childNodes.getLength() > 0){
					
					boolean hasChildNodes = false;
					for(int i=0;i<childNodes.getLength();i++){
						// elimino i text node che possono contenere "\n"
						Node n = childNodes.item(i);
						if(n.hasChildNodes()){
							hasChildNodes = true;
							break;
						}
					}
					
					if (hasChildNodes){
						resultBuffer.append("<"+aNode.getNodeName());

						this.printAttributes(aNode,resultBuffer);

						resultBuffer.append(">");
						
						this.toString(aNode.getChildNodes(), resultBuffer, (livello+1));
						resultBuffer.append("</"+aNode.getNodeName()+">");
					}
					else {
						resultBuffer.append("<"+aNode.getNodeName());
						
						this.printAttributes(aNode,resultBuffer);
											
						// BugEntityReferences: il metodo getTextContent e anche getNodeValue risolve le entity references
						// String textNodeValue = aNode.getTextContent();
						String textNodeValue = null;
						try {
							textNodeValue = _getTextValue_fixEntityReferencies(aNode);
						}catch(Throwable t) {
							textNodeValue = aNode.getTextContent();
						}
						
						resultBuffer.append(">")
							.append(textNodeValue) 
							.append("</").append(aNode.getNodeName()).append(">");
					}
				}
			}
			// Risultati multipli per uno stesso elemento
			else if ( (livello==1) && (aNode.getNodeType() == Node.TEXT_NODE || aNode.getNodeType() == Node.ATTRIBUTE_NODE) ){
				if(findElementoRisultatoMultiplo){
					resultBuffer.append(", ");
				}
				else {
					findElementoRisultatoMultiplo = true;
				}
				// BugEntityReferences: il metodo getTextContent e anche getNodeValue risolve le entity references
				//String textNodeValue = aNode.getTextContent();
				String textNodeValue = null;
				try {
					textNodeValue = _getTextValue_fixEntityReferencies(aNode);
				}catch(Throwable t) {
					textNodeValue = aNode.getTextContent();
				}
				resultBuffer.append("["+index+"]="+textNodeValue);
			}
		}
	}
	
	private String _getTextValue_fixEntityReferencies(Node valueEle) throws Exception {
		
		StringBuilder sb = new StringBuilder();
		
		if(valueEle.getNodeType()==Node.TEXT_NODE || valueEle.getNodeType()==Node.ATTRIBUTE_NODE) {
			//sb.append(valueEle.getTextContent());
			// FIX:
			sb.append(getXMLUtils().toString(valueEle,true));
		}
		else {
			NodeList nl = valueEle.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node item = nl.item(i);
				if ((item instanceof CharacterData && !(item instanceof Comment)) || item instanceof EntityReference) {
					//sb.append(item.getNodeValue());
					// FIX:
					sb.append(getXMLUtils().toString(item,true));
				}
			}
		}
		return sb.toString();
	}
	
	private List<String> toList(NodeList rootNode,int livello){
		
		List<String> l = new ArrayList<>();
		
		if (rootNode.getLength()==1 && rootNode.item(0).getNodeType()==Node.TEXT_NODE){
			//System.out.println("TEXT ["+rootNode.item(0).getNodeType()+"] confronto con ["+Node.TEXT_NODE+"]");
			l.add(rootNode.item(0).getTextContent());
			return l;
		}
		for(int index = 0; index < rootNode.getLength();index ++){
			Node aNode = rootNode.item(index);
			//System.out.println("NODE["+index+"] ["+aNode.getNodeType()+"] confronto con  ["+Node.ELEMENT_NODE+"]");
			if (aNode.getNodeType() == Node.ELEMENT_NODE){
				NodeList childNodes = aNode.getChildNodes(); 
				
				if (childNodes.getLength() > 0){
					
					StringBuilder resultBuffer = new StringBuilder();
					
					boolean hasChildNodes = false;
					for(int i=0;i<childNodes.getLength();i++){
						// elimino i text node che possono contenere "\n"
						Node n = childNodes.item(i);
						if(n.hasChildNodes()){
							hasChildNodes = true;
							break;
						}
					}
					
					if (hasChildNodes){
						resultBuffer.append("<"+aNode.getNodeName());

						this.printAttributes(aNode,resultBuffer);

						resultBuffer.append(">");
						
						this.toString(aNode.getChildNodes(), resultBuffer, (livello+1));
						resultBuffer.append("</"+aNode.getNodeName()+">");
					}
					else {
						resultBuffer.append("<"+aNode.getNodeName());
						
						this.printAttributes(aNode,resultBuffer);
						
						resultBuffer.append(">"+aNode.getTextContent()+ "</"+aNode.getNodeName()+">");
					}
					
					l.add(resultBuffer.toString());
				}
			}
			// Risultati multipli per uno stesso elemento
			else if ( (livello==1) && (aNode.getNodeType() == Node.TEXT_NODE) ){
				l.add(aNode.getTextContent());
			}
		}
		
		return l;
	}
	
	private void printAttributes(Node aNode,StringBuilder resultBuffer){
		NamedNodeMap attr = aNode.getAttributes();
		for (int i=0;i<attr.getLength();i++){
			Node item = attr.item(i);
			if(item instanceof Attr){
				Attr attribute = (Attr) item;
				//System.out.println("PREFIX["+attribute.getPrefix()+"] LOCALNAME=["+attribute.getLocalName()+"] NAMESPACE["+attribute.getNodeValue()+"]");
				String prefix = attribute.getPrefix();
				if(prefix!=null && !"".equals(prefix)){
					prefix = prefix + ":";
				}else{
					prefix = "";
				}
				// BugEntityReferences: il metodo getTextContent e anche getNodeValue risolve le entity references
				//String value = attribute.getNodeValue();
				String value = null;
				try {
					value = _getTextValue_fixEntityReferencies(attribute);
				}catch(Throwable t) {
					value = attribute.getNodeValue();
				}
				resultBuffer.append(" "+prefix+attribute.getLocalName()+"=\""+value+"\"");
			}else{
				resultBuffer.append(" "+item.toString());
			}
		}
	}
	
	
	public static String extractAndConvertResultAsString(String contentAsString,DynamicNamespaceContext dnc,AbstractXPathExpressionEngine xPathEngine, String pattern, Logger log) throws Exception {
		return AbstractXPathExpressionEngine._extractAndConvertResultAsString(null, contentAsString,
				dnc, xPathEngine, pattern, log);
	}
	public static String extractAndConvertResultAsString(Element element,AbstractXPathExpressionEngine xPathEngine, String pattern, Logger log) throws Exception {
		DynamicNamespaceContext dnc = new DynamicNamespaceContext();
		dnc.findPrefixNamespace(element);
		return AbstractXPathExpressionEngine._extractAndConvertResultAsString(element, null,
				dnc, xPathEngine, pattern, log);
	}
	public static String extractAndConvertResultAsString(Element element,DynamicNamespaceContext dnc,AbstractXPathExpressionEngine xPathEngine, String pattern, Logger log) throws Exception {
		return AbstractXPathExpressionEngine._extractAndConvertResultAsString(element, null,
				dnc, xPathEngine, pattern, log);
	}
	private static String _extractAndConvertResultAsString(Element element,String contentAsString,
			DynamicNamespaceContext dnc,AbstractXPathExpressionEngine xPathEngine, String pattern, Logger log) throws Exception {
		
		// Provo a cercarlo prima come Node
		NodeList nodeList = null;
		Exception exceptionNodeSet = null;
		String risultato = null;
		try{
			if(element!=null) {
				nodeList = (NodeList) xPathEngine.getMatchPattern(element, dnc, pattern,XPathReturnType.NODESET);
			}
			else {
				nodeList = (NodeList) xPathEngine.getMatchPattern(contentAsString, dnc, pattern,XPathReturnType.NODESET);
			}
			if(nodeList!=null){
				risultato = xPathEngine.toString(nodeList);
			}
		}catch(Exception e){
			exceptionNodeSet = e;
		}
		
		// Se non l'ho trovato provo a cercarlo come string (es. il metodo sopra serve per avere l'xml, ma fallisce in caso di concat, in caso di errori di concat_openspcoop....)
		// Insomma il caso dell'xml sopra e' quello speciale, che pero' deve essere eseguito prima, perche' altrimenti il caso string sotto funziona sempre, e quindi non si ottiene mai l'xml.
		if(risultato==null || "".equals(risultato)){
			try{
				if(element!=null) {
					risultato = xPathEngine.getStringMatchPattern(element, dnc, pattern);
				}
				else {
					risultato = xPathEngine.getStringMatchPattern(contentAsString, dnc, pattern);
				}
			}catch(Exception e){
				if(exceptionNodeSet!=null){
					log.debug("Errore avvenuto durante la getStringMatchPattern("+pattern
							+") ("+e.getMessage()+") invocata in seguito all'errore dell'invocazione getMatchPattern("+
							pattern+",NODESET): "+exceptionNodeSet.getMessage(),exceptionNodeSet);
				}
				// lancio questo errore e se presente anche quello con la ricerca notset nodoset.
				if(exceptionNodeSet!=null) {
					throw new UtilsMultiException(e,exceptionNodeSet);
				}
				else {
					throw e;
				}
			}
		}
		
		if(risultato == null || "".equals(risultato)){
			if(exceptionNodeSet!=null){
				log.debug("Non sono stati trovati risultati tramite l'invocazione del metodo getStringMatchPattern("+pattern
						+") invocato in seguito all'errore dell'invocazione getMatchPattern("+
						pattern+",NODESET): "+exceptionNodeSet.getMessage(),exceptionNodeSet);
				// lancio questo errore.
				// Questo errore puo' avvenire perche' ho provato a fare xpath con nodeset
				//throw exceptionNodeSet;
			}
		}
		
		return risultato;
	}
	
	
	
	public static List<String> extractAndConvertResultAsList(String contentAsString,DynamicNamespaceContext dnc,AbstractXPathExpressionEngine xPathEngine, String pattern, Logger log) throws Exception {
		return AbstractXPathExpressionEngine._extractAndConvertResultAsList(null, contentAsString,
				dnc, xPathEngine, pattern, log);
	}
	public static List<String> extractAndConvertResultAsList(Element element,AbstractXPathExpressionEngine xPathEngine, String pattern, Logger log) throws Exception {
		DynamicNamespaceContext dnc = new DynamicNamespaceContext();
		dnc.findPrefixNamespace(element);
		return AbstractXPathExpressionEngine._extractAndConvertResultAsList(element, null,
				dnc, xPathEngine, pattern, log);
	}
	public static List<String> extractAndConvertResultAsList(Element element,DynamicNamespaceContext dnc,AbstractXPathExpressionEngine xPathEngine, String pattern, Logger log) throws Exception {
		return AbstractXPathExpressionEngine._extractAndConvertResultAsList(element, null,
				dnc, xPathEngine, pattern, log);
	}
	private static List<String> _extractAndConvertResultAsList(Element element,String contentAsString,
			DynamicNamespaceContext dnc,AbstractXPathExpressionEngine xPathEngine, String pattern, Logger log) throws Exception {
		
		
		// Provo a cercarlo prima come Node
		NodeList nodeList = null;
		Exception exceptionNodeSet = null;
		List<String> risultato = new ArrayList<>();
		try{
			if(element!=null) {
				nodeList = (NodeList) xPathEngine.getMatchPattern(element, dnc, pattern,XPathReturnType.NODESET);
			}
			else {
				nodeList = (NodeList) xPathEngine.getMatchPattern(contentAsString, dnc, pattern,XPathReturnType.NODESET);
			}
			if(nodeList!=null){
				risultato = xPathEngine.toList(nodeList);
			}
		}catch(Exception e){
			exceptionNodeSet = e;
		}
		
		// Se non l'ho trovato provo a cercarlo come string (es. il metodo sopra serve per avere l'xml, ma fallisce in caso di concat, in caso di errori di concat_openspcoop....)
		// Insomma il caso dell'xml sopra e' quello speciale, che pero' deve essere eseguito prima, perche' altrimenti il caso string sotto funziona sempre, e quindi non si ottiene mai l'xml.
		if(risultato==null || risultato.isEmpty()){
			try{
				String s = null;
				if(element!=null) {
					s = xPathEngine.getStringMatchPattern(element, dnc, pattern);
				}
				else {
					s = xPathEngine.getStringMatchPattern(contentAsString, dnc, pattern);
				}
				if(risultato==null) {
					risultato = new ArrayList<>();
				}
				risultato.add(s);
			}catch(Exception e){
				if(exceptionNodeSet!=null){
					log.debug("Errore avvenuto durante la getStringMatchPattern("+pattern
							+") ("+e.getMessage()+") invocata in seguito all'errore dell'invocazione getMatchPattern("+
							pattern+",NODESET): "+exceptionNodeSet.getMessage(),exceptionNodeSet);
				}
				// lancio questo errore e se presente anche quello con la ricerca notset nodoset.
				if(exceptionNodeSet!=null) {
					throw new UtilsMultiException(e,exceptionNodeSet);
				}
				else {
					throw e;
				}
			}
		}
		
		if(risultato == null || risultato.isEmpty()){
			if(exceptionNodeSet!=null){
				log.debug("Non sono stati trovati risultati tramite l'invocazione del metodo getStringMatchPattern("+pattern
						+") invocato in seguito all'errore dell'invocazione getMatchPattern("+
						pattern+",NODESET): "+exceptionNodeSet.getMessage(),exceptionNodeSet);
				// lancio questo errore.
				// Questo errore puo' avvenire perche' ho provato a fare xpath con nodeset
				//throw exceptionNodeSet;
			}
		}
		
		return risultato;
	}
}
