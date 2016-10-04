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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Classe utilizzabile per raccogliere le informazioni sui namespaces di un messaggio Soap
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DynamicNamespaceContext implements javax.xml.namespace.NamespaceContext
{

	/** Variabile contenente i prefix */
	private java.util.Properties context;
	private String prefixChildSoapBody = null;
	private boolean soapFault = false;
	private boolean soapBodyEmpty = false;

	
	@Override
	public Object clone(){
		DynamicNamespaceContext dnc = new DynamicNamespaceContext();

		if(this.context!=null){
			dnc.context = new Properties();
			if(this.context.size()>0){
				Iterator<?> it = this.context.keySet().iterator();
				while (it.hasNext()) {
					Object key = (Object) it.next();
					dnc.context.put(key, this.context.get(key));
				}
			}
		}
		
		if(this.prefixChildSoapBody!=null){
			dnc.prefixChildSoapBody = new String(this.prefixChildSoapBody);
		}
		
		dnc.soapFault = this.soapFault;
		
		dnc.soapBodyEmpty = this.soapBodyEmpty;
		
		return dnc;
	}
	
	
	public boolean isSoapBodyEmpty() {
		return this.soapBodyEmpty;
	}

	public void setSoapBodyEmpty(boolean soapBodyEmpty) {
		this.soapBodyEmpty = soapBodyEmpty;
	}

	public boolean isSoapFault() {
		return this.soapFault;
	}

	public void setSoapFault(boolean soapFault) {
		this.soapFault = soapFault;
	}

	public String getPrefixChildSoapBody() {
		return this.prefixChildSoapBody;
	}

	public void setPrefixChildSoapBody(String prefixChildSoapBody) {
		this.prefixChildSoapBody = prefixChildSoapBody;
	}
	
	/**
	 * Costruttore
	 *
	 * 
	 */
	public DynamicNamespaceContext(){
		this.context = new java.util.Properties();
		this.context.put(javax.xml.XMLConstants.XML_NS_PREFIX,javax.xml.XMLConstants.XML_NS_URI);
		this.context.put(javax.xml.XMLConstants.XMLNS_ATTRIBUTE,javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
	}

	
	/**
	 * Imposta un nuovo namespace, se non ancora inserito
	 *
	 * @param prefix Prefisso 
	 * @param url URL del Namespace, associato al prefisso
	 * 
	 */
	public void addNamespace(String prefix,String url){
		if(this.context.containsKey(prefix)==false){
			this.context.setProperty(prefix,url);
		}
	}

	/**
	 * Dato come parametro un prefix, ritorna l'associato namespace
	 *
	 * @param prefix Prefisso 
	 * 
	 */
	@Override
	public String getNamespaceURI(String prefix)
	{
		/**
		 * getNamespaceURI(prefix) return value for specified prefixes
		 * -a. 'DEFAULT_NS_PREFIX ("")' -> 	default Namespace URI in the current scope or 
		 * 									XMLConstants.NULL_NS_URI("") when there is no default Namespace URI in the current scope
		 * -b. 'bound prefix'           ->	Namespace URI bound to prefix in current scope
		 * -c. 'unbound prefix' 	    ->  XMLConstants.NULL_NS_URI("")
		 * -d. XMLConstants.XML_NS_PREFIX ("xml")   -> 	XMLConstants.XML_NS_URI ("http://www.w3.org/XML/1998/namespace")
		 * -e. XMLConstants.XMLNS_ATTRIBUTE ("xmlns")  -> 	XMLConstants.XMLNS_ATTRIBUTE_NS_URI ("http://www.w3.org/2000/xmlns/")
		 * -f. null  ->	IllegalArgumentException is thrown
		 */
		
		//System.out.println("------ getNamespaceURI("+prefix+") -----");
		
		// -f. null 
		if(prefix==null){
			//System.out.println("RETURN -f. null ");
			throw new IllegalArgumentException("Prefix ["+prefix+"] not defined");
		}
		
		// -a. 'DEFAULT_NS_PREFIX ("")'
		if(javax.xml.XMLConstants.DEFAULT_NS_PREFIX.equals(prefix)){
			if(this.context.containsKey(prefix)){
				//System.out.println("RETURN ["+this.context.getProperty(prefix)+"] -a. 'DEFAULT_NS_PREFIX'");
				return this.context.getProperty(prefix);
			}else{
				//System.out.println("RETURN ["+javax.xml.XMLConstants.NULL_NS_URI+"] -a. 'DEFAULT_NS_PREFIX' (nullUri)");
				return javax.xml.XMLConstants.NULL_NS_URI;
			}
		}
		
		// -d. XMLConstants.XML_NS_PREFIX
		else if(javax.xml.XMLConstants.XML_NS_PREFIX.equals(prefix)){
			//System.out.println("RETURN ["+this.context.getProperty(prefix)+"] -d. XMLConstants.XML_NS_PREFIX");
			return this.context.getProperty(prefix); // inizializzato nel costruttore
		}
		
		// -e. XMLConstants.XMLNS_ATTRIBUTE
		else if(javax.xml.XMLConstants.XMLNS_ATTRIBUTE.equals(prefix)){
			//System.out.println("RETURN ["+this.context.getProperty(prefix)+"] -e. XMLConstants.XMLNS_ATTRIBUTE");
			return this.context.getProperty(prefix); // inizializzato nel costruttore
		}
		
		// -b. 'bound prefix' 
		else if(this.context.containsKey(prefix)){
			
			java.util.Enumeration<?> en = this.context.propertyNames();
			while(en.hasMoreElements()){
				String key = (String) en.nextElement();
				if(key.equals(prefix)){
					//System.out.println("RETURN ["+this.context.getProperty(key)+"] -b. 'bound prefix' ");
					return this.context.getProperty(key);
				}
			}	
			
		}
		
		// c. unbound prefix
		//System.out.println("RETURN ["+javax.xml.XMLConstants.NULL_NS_URI+"] -c. 'unbound prefix'");
		return javax.xml.XMLConstants.NULL_NS_URI;
		
	}

	/**
	 * Dato come parametro un namespace, ritorna l'associato prefix
	 *
	 * @param namespace URL del Namespace, associato al prefisso
	 * 
	 */
	@Override
	public String getPrefix(String namespace)
	{
		/**
		 * getPrefix(namespaceURI) return value for specified Namespace URIs
		 * -a. '<default Namespace URI>' 	-> XMLConstants.DEFAULT_NS_PREFIX ("")
		 * -b. 'bound Namespace URI' 		-> prefix bound to Namespace URI in the current scope, if multiple prefixes are bound to the Namespace URI in the current scope, 
		 * 										a single arbitrary prefix, whose choice is implementation dependent, is returned
		 * -c. 'unbound Namespace URI'		-> 	null
		 * -d. XMLConstants.XML_NS_URI ("http://www.w3.org/XML/1998/namespace")   ->	XMLConstants.XML_NS_PREFIX ("xml")
		 * -e. XMLConstants.XMLNS_ATTRIBUTE_NS_URI ("http://www.w3.org/2000/xmlns/")  -> 	XMLConstants.XMLNS_ATTRIBUTE ("xmlns")
		 * -f. null  ->	IllegalArgumentException is thrown 
		 **/
		
		//System.out.println("------ getPrefix("+namespace+") -----");
		
		// -f. null 
		if(namespace==null){
			//System.out.println("RETURN -f. null ");
			throw new IllegalArgumentException("Namespace ["+namespace+"] not defined");
		}
		
		// -a. '<default Namespace URI>'
		// -b. 'bound Namespace URI'
		// -d. XMLConstants.XML_NS_URI
		// -e. XMLConstants.XMLNS_ATTRIBUTE_NS_URI
		if(this.context.containsValue(namespace)){
			
			java.util.Enumeration<?> en = this.context.propertyNames();
			while(en.hasMoreElements()){
				String prefix = (String) en.nextElement();
				if(this.context.getProperty(prefix).equals(namespace)){
					if(javax.xml.XMLConstants.DEFAULT_NS_PREFIX.equals(prefix)){
						//System.out.println("RETURN ["+prefix+"] -a. '<default Namespace URI>'");
					}
					else if(javax.xml.XMLConstants.XML_NS_PREFIX.equals(prefix)){
						//System.out.println("RETURN ["+prefix+"] -d. 'XMLConstants.XML_NS_URI'"); // inizializzato nel costruttore
					}
					else if(javax.xml.XMLConstants.XMLNS_ATTRIBUTE.equals(prefix)){
						//System.out.println("RETURN ["+prefix+"] -e. 'XMLConstants.XMLNS_ATTRIBUTE_NS_URI'"); // inizializzato nel costruttore
					}
					else{
						//System.out.println("RETURN ["+prefix+"] -b. 'bound Namespace URI'"); // primo che incontro
					}
					return prefix;
				}
			}	
			
		}
		
		// -c. 'unbound Namespace URI'
		//System.out.println("RETURN ["+javax.xml.XMLConstants.NULL_NS_URI+"] -c. 'unbound Namespace URI'");
		return javax.xml.XMLConstants.NULL_NS_URI;
		
		
	}


	/**
	 * Dato come parametro un namespace, ritorna i prefissi con quell'iterator (Non implementato)
	 *
	 * @param namespace URL del Namespace, associato al prefisso
	 * 
	 */
	@Override
	public java.util.Iterator<?> getPrefixes(String namespace)
	{	
		/**
		 * getPrefixes(namespaceURI) return value for specified Namespace URIs
		 * -a. bound Namespace URI, including the <default Namespace URI> ->
		 * 							 	Iterator over prefixes bound to Namespace URI in the current scope in an arbitrary, implementation dependent, order
		 * -b. unbound Namespace URI  ->	empty Iterator
		 * -c. XMLConstants.XML_NS_URI ("http://www.w3.org/XML/1998/namespace")  ->	Iterator with one element set to XMLConstants.XML_NS_PREFIX ("xml")
		 * -d. XMLConstants.XMLNS_ATTRIBUTE_NS_URI ("http://www.w3.org/2000/xmlns/") ->	Iterator with one element set to XMLConstants.XMLNS_ATTRIBUTE ("xmlns")
		 * -e. null -> IllegalArgumentException is thrown
		 */
		
		//System.out.println("------ getPrefixes("+namespace+") -----");
		
		Vector<String> v = new Vector<String>();
		
		// -e. null 
		if(namespace==null){
			//System.out.println("RETURN -e. null ");
			throw new IllegalArgumentException("Namespace ["+namespace+"] not defined");
		}
		
		// -a. bound Namespace URI, including the <default Namespace URI>
		// -c. XMLConstants.XML_NS_URI 
		// -d. XMLConstants.XMLNS_ATTRIBUTE_NS_URI
		if(this.context.containsValue(namespace)){
			
			java.util.Enumeration<?> en = this.context.propertyNames();
			while(en.hasMoreElements()){
				String prefix = (String) en.nextElement();
				if(this.context.getProperty(prefix).equals(namespace)){
					if(javax.xml.XMLConstants.DEFAULT_NS_PREFIX.equals(prefix)){
						//System.out.println("ADD ["+prefix+"] -a. bound Namespace URI  the <default Namespace URI>");
					}
					else if(javax.xml.XMLConstants.XML_NS_PREFIX.equals(prefix)){
						//System.out.println("RETURN ["+prefix+"] -c. 'XMLConstants.XML_NS_URI'"); // inizializzato nel costruttore
					}
					else if(javax.xml.XMLConstants.XMLNS_ATTRIBUTE.equals(prefix)){
						//System.out.println("RETURN ["+prefix+"] -d. 'XMLConstants.XMLNS_ATTRIBUTE_NS_URI'"); // inizializzato nel costruttore
					}
					else{
						//System.out.println("ADD ["+prefix+"] -a. bound Namespace URI");
					}
					v.add(prefix);
				}
			}	
			return v.iterator();
			
		}

		// -b. unbound Namespace URI
		//System.out.println("RETURN (empty iterator). '-b. unbound Namespace URI' ");
		return v.iterator();
	}
	
	public Enumeration<?> getPrefixes (){
		return this.context.keys();
	}
	


	/**
	 * Trova tutti i riferimenti ad attachments presenti all'interno di un nodo,
	 * fornito attraverso il parametro <var>node</var>.
	 * Ogni riferimento trovato (Content-ID), viene inserito all'interno del vector <var>href</var>.
	 *
	 * @param node Node da esaminare.
	 * 
	 */
	public void findPrefixNamespace(Node node) {
		if(node == null)
			return;

		String namespace = node.getNamespaceURI();
		if(namespace!=null && "".equals(namespace)==false){
		
			String prefix = node.getPrefix();
			if(prefix==null){
				prefix = javax.xml.XMLConstants.DEFAULT_NS_PREFIX;
			}
			
			// Esamino attributi del nodo
			//System.out.println("PREFIX["+prefix+"]  NS["+node.getNamespaceURI()+"]");
			this.addNamespace(prefix,node.getNamespaceURI());
			
		}
			
		NodeList list = node.getChildNodes();
		if(list == null)
			return;

		int nodes = list.getLength();
		for(int i=0;i<nodes;i++){
			Node child = list.item(i);
			findPrefixNamespace(child);
		}
	}
	
}  
