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

import java.io.Reader;
import java.io.StringReader;
import java.util.Enumeration;

import javax.xml.soap.SOAPElement;

import org.slf4j.Logger;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Classe utilizzabile per ricerche effettuate tramite espressioni XPath
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractXPathExpressionEngine {

	private static javax.xml.xpath.XPathFactory xPathFactory = null;
	public static javax.xml.xpath.XPathFactory getXPathFactory() throws XPathException{
		if(AbstractXPathExpressionEngine.xPathFactory==null){
			AbstractXPathExpressionEngine.initXPathFactory();
		}
		return AbstractXPathExpressionEngine.xPathFactory;
	}
	public static synchronized void initXPathFactory() throws XPathException{
		try{
			if(AbstractXPathExpressionEngine.xPathFactory==null){
				AbstractXPathExpressionEngine.xPathFactory = javax.xml.xpath.XPathFactory.newInstance();
			}
		}catch(Exception e){
			throw new XPathException("Inizializzazione XPathFactory non riuscita",e);
		}
	}
	
	
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
	
	private static Integer synchronizedObjectForBugFWK005ParseXerces = new Integer(1);
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
			javax.xml.xpath.XPathFactory factory = AbstractXPathExpressionEngine.getXPathFactory();
	
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
				StringBuffer bfResult = new StringBuffer();
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
								} catch(Exception er) {}
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
							synchronized (AbstractXPathExpressionEngine.synchronizedObjectForBugFWK005ParseXerces) {
								if(reader!=null)
									result = expression.evaluate(new org.xml.sax.InputSource(reader));
								else 
									result = expression.evaluate(this.readXPathElement(contenutoAsElement));
							}
						}catch(Exception e){
							if(Utilities.existsInnerException(e,com.sun.org.apache.xpath.internal.XPathException.class)){
								throw new Exception("Valutazione dell'espressione XPATH contenuta in concat_openspcoop ("+params[i]+") ha causato un errore ("+Utilities.getInnerException(e, com.sun.org.apache.xpath.internal.XPathException.class).getMessage()+")",e);
							}else{
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
							logger.debug("nessun match trovato per l'espressione xpath contenuta in concat_openspcoop ("+params[i]+")");
							throw new XPathNotFoundException("nessun match trovato per l'espressione xpath contenuta in concat_openspcoop ("+params[i]+")");
						}
						else{
							bfResult.append(result);
						}
					}
				}
				
				if(bfResult.length()<=0){
					StringBuffer bfDNC = new StringBuffer();
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
						} catch(Exception er) {}
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
					synchronized (AbstractXPathExpressionEngine.synchronizedObjectForBugFWK005ParseXerces) {
						if(reader!=null)
							result = expression.evaluate(new org.xml.sax.InputSource(reader),returnType.getValore());
						else{
							result = expression.evaluate(this.readXPathElement(contenutoAsElement),returnType.getValore());
						}
					}
				}catch(Exception e){
					if(Utilities.existsInnerException(e,com.sun.org.apache.xpath.internal.XPathException.class)){
						throw new Exception("Valutazione dell'espressione XPATH ha causato un errore ("+Utilities.getInnerException(e, com.sun.org.apache.xpath.internal.XPathException.class).getMessage()+")",e);
					}else{
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
					StringBuffer bfDNC = new StringBuffer();
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
			javax.xml.xpath.XPathFactory factory = AbstractXPathExpressionEngine.getXPathFactory();
	
			// Use the XPathFactory to create a new XPath object
			javax.xml.xpath.XPath xpath = factory.newXPath();
			if(xpath==null){
				throw new Exception("Costruzione xpath non riuscita");
			}
			
			// 3. Repleace the Namespaces:
			path = path.replaceAll("}:", "}");
			int index = 0;
			if(path.indexOf("{")!=-1){
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
		
		StringBuffer resultBuffer = new StringBuffer();
		this.toString(rootNode,resultBuffer,1);
		return resultBuffer.toString();
		
	}
	
	
	/* -------------- UTILITIES PRIVATE ------------------- */
	private int _prefixIndex = 0;
	private synchronized int _getNextPrefixIndex(){
		this._prefixIndex++;
		return this._prefixIndex;
	}
	private final static String AUTO_PREFIX = "_op2PrefixAutoGeneratedIndex";
	private String convertNamespaces(String path,DynamicNamespaceContext dnc)throws UtilsException{
		if(path.indexOf("{")!=-1){
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
					prefix = AUTO_PREFIX + this._getNextPrefixIndex();
					dnc.addNamespace(prefix, namespace);
					prefix = prefix+":";
					//System.out.println("CASO SPECIALEEEEEEEEEEEEEEEE");
				}
				path = path.replace("{"+namespace+"}", prefix);
			}
		}
		return path;
	}
	
	private void toString(NodeList rootNode,StringBuffer resultBuffer, int livello){
		
		if (rootNode.getLength()==1 && rootNode.item(0).getNodeType()==Node.TEXT_NODE && resultBuffer.length()==0){
			//System.out.println("TEXT ["+rootNode.item(0).getNodeType()+"] confronto con ["+Node.TEXT_NODE+"]");
			resultBuffer.append(rootNode.item(0).getTextContent());
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
						// elimino i text node che possono contenere "\n" con axiom
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
				}
			}
			// Risultati multipli per uno stesso elemento
			else if ( (livello==1) && (aNode.getNodeType() == Node.TEXT_NODE) ){
				if(findElementoRisultatoMultiplo){
					resultBuffer.append(", ");
				}
				else {
					findElementoRisultatoMultiplo = true;
				}
				resultBuffer.append("["+index+"]="+aNode.getTextContent());
			}
		}
	}
	
	private void printAttributes(Node aNode,StringBuffer resultBuffer){
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
				String value = attribute.getNodeValue();
				resultBuffer.append(" "+prefix+attribute.getLocalName()+"=\""+value+"\"");
			}else{
				resultBuffer.append(" "+item.toString());
			}
		}
	}
}
