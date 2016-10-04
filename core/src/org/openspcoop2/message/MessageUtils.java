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

package org.openspcoop2.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.Text;
import javax.xml.transform.TransformerException;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.xml.PrettyPrintXMLUtils;

import com.ctc.wstx.exc.WstxException;


/**
 * MessageUtils
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageUtils {

	
	public static void equalsSoapElements(SOAPElement element1,SOAPElement element2,boolean checkTextComment) throws TransformerException, IOException{
		try{
			equalsSoapElements(element1, element2, new Vector<String>(),checkTextComment);
		}catch(Exception e){
			String soapReq = PrettyPrintXMLUtils.prettyPrintWithTrAX(element1);
			String soapRes = PrettyPrintXMLUtils.prettyPrintWithTrAX(element2);
			throw new IOException("Element1 ["+soapReq+"] risulta differente da Element2 ["+soapRes+"] (motivo:"+e.getMessage()+")",e);
		}
	}

	private static String getQualifiedName(QName attr){
		if(attr.getNamespaceURI()!=null){
			return "{"+ attr.getNamespaceURI()+" }"+attr.getLocalPart();
		}
		else{
			return attr.getLocalPart();
		}
	}
	
	/**
	 * Confronta due SoapElement.
	 * TODO: Un errore non mi permette di individuare tutti i nameSpace in un nodo
	 * @throws IOException 
	 * 
	 */
	private static void equalsSoapElements(SOAPElement el1,SOAPElement el2,Vector<String> namespacePrefixEl1,boolean checkTextComment) throws IOException{
		
		/**************** controllo nome del nodo *****************************/
		if(!el1.getNodeName().equals(el2.getNodeName())){
			//System.out.println("NOME DIVERSO");
			throw new IOException("Node1 possiede un nome ["+el1.getNodeName()+"] differente dal nome del Node2 ["+el2.getNodeName()+"]");
		}
		
		
		Iterator<?> it=el1.getAllAttributesAsQNames();
		Iterator<?> it2=el2.getAllAttributesAsQNames();
		Vector <String>vet=new Vector<String>();
		Vector <String>vet2=new Vector<String>();
		/**************** controllo se gli attributi sono uguali*****************************/
		while(it.hasNext()){
			if(!it2.hasNext()){
				//System.out.println("ATTR 1");
				throw new IOException("Node1 ["+el1.getNodeName()+"] possiede degli attributi, mentre nel node2 ["+el2.getNodeName()+"] non ve ne sono");
			}
			//Attributes att=(Attributes)it.next();
			QName attr1 = ((QName) it.next());
			vet.add( getQualifiedName(attr1) );
			
			QName attr2 = ((QName) it2.next());
			vet2.add( getQualifiedName(attr2) );
			
		}
		if(it2.hasNext()){
			//System.out.println("ATTR 2");
			throw new IOException("Node2 ["+el2.getNodeName()+"] possiede piu' attributi del Node1 ["+el1.getNodeName()+"]");
		}
		if(!vet.containsAll(vet2)){
			//System.out.println("ATTR 3");
			
			StringBuffer bfNode1 = new StringBuffer();
			for (int i = 0; i < vet.size(); i++) {
				if(i>0){
					bfNode1.append(",");
				}
				bfNode1.append(vet.get(i));
			}
			
			StringBuffer bfNode2 = new StringBuffer();
			for (int i = 0; i < vet2.size(); i++) {
				if(i>0){
					bfNode2.append(",");
				}
				bfNode2.append(vet2.get(i));
			}
			
			throw new IOException("Node1 ["+el1.getNodeName()+"] e Node2 ["+el2.getNodeName()+"] non hanno gli stessi attributi. Attributi("+vet.size()+") di Node1: "+bfNode1+ " . Attributi("+vet2.size()+") di Node2: "+bfNode2);
		}


		for(int i=0;i<vet.size();i++){
			String value=vet.get(i);
			if(!el1.getAttribute(value).equals(el2.getAttribute(value))){
				throw new IOException("L'attributo ["+value+"] di Node1 ["+el1.getNodeName()+"] possiede un valore ("+
							el1.getAttribute(value)+") differente dal valore presente nello stesso attributo nel Node2 ["+el2.getNodeName()+"] (valore:"+el2.getAttribute(value)+")");
			}
		}

		/***************************** Namespace URI del nodo ********************************/
        String str1=el1.getNamespaceURI();
        String str2=el2.getNamespaceURI();
       // System.out.println("el1 -- il namespace Uri del nodo e' "+str1);
        //System.out.println("el2 -- il namespace Uri del nodo e' "+str2);
        boolean namespaceIdentico = false;
        if(str1!=null && str2!=null){
        	namespaceIdentico = str1.equals(str2);
        }
        else if(str1==null && str2==null){
        	namespaceIdentico = true;
        }
        if(!namespaceIdentico){
        	//System.out.println("URI");
        	throw new IOException("Node1 ["+el1.getNodeName()+"] possiede un namespace ["+str1+"] differente dal namespace del Node2 ["+el2.getNodeName()+"] (namespace:"+str2+")");
        }
		
		
		/*****************************Controllo se i namespace sono uguali********************************/
        Iterator<?> nameSp1=el1.getNamespacePrefixes();
        Iterator<?> nameSp2=el2.getNamespacePrefixes();
        Vector <String>nameSpVet1=new Vector<String>();
        Vector <String>nameSpVet2=new Vector<String>();
        String prefix1, prefix2, urlPrefix1, urlPrefix2;
        while(nameSp1.hasNext() && nameSp2.hasNext())
        {
            prefix1=(String) nameSp1.next();
            try{
            	urlPrefix1 = el1.getNamespaceURI(prefix1);
            }catch(Exception e){
            	urlPrefix1 = el1.getNamespaceURI();
            }
            nameSpVet1.add(prefix1+"="+urlPrefix1);
            
            if(namespacePrefixEl1.contains((prefix1+"="+urlPrefix1))==false){
            	//System.out.println("ADD COMPLESSIVO: "+prefix1+"="+urlPrefix1);
            	namespacePrefixEl1.add(prefix1+"="+urlPrefix1);
            }
            
            prefix2=(String) nameSp2.next();
            try{
            	urlPrefix2 = el2.getNamespaceURI(prefix2);
            }catch(Exception e){
            	urlPrefix2 = el2.getNamespaceURI();
            }
            nameSpVet2.add(prefix2+"="+urlPrefix2);            
        }
        
        // Controllo uguaglianza
        for(int i=0; i<nameSpVet1.size(); i++){
        	String n1 = (String) nameSpVet1.get(i);
        	boolean trovato = false;
        	for(int j=0; j<nameSpVet2.size(); j++){
        		String n2 = (String) nameSpVet2.get(j);
        		if(n1.equals(n2)){
        			trovato = true;
        			break;
        		}			
        	}
        	if(trovato==false){
        		// Cerco nei namespaces del padre
        		if(namespacePrefixEl1.contains(n1)==false){
        			//System.out.println("NON TROVATO: "+n1);
        			throw new IOException("Node1 ["+el1.getNodeName()+"] non contiene il prefix: "+n1);
        		}
        	}
        }
        

        if(!(nameSpVet1.size() == nameSpVet2.size())){
        	//System.out.println("SIZE NAMESPACE");
        	
			StringBuffer bfNode1 = new StringBuffer();
			for (int i = 0; i < nameSpVet1.size(); i++) {
				if(i>0){
					bfNode1.append(",");
				}
				bfNode1.append(nameSpVet1.get(i));
			}
			
			StringBuffer bfNode2 = new StringBuffer();
			for (int i = 0; i < nameSpVet2.size(); i++) {
				if(i>0){
					bfNode2.append(",");
				}
				bfNode2.append(nameSpVet2.get(i));
			}
        	
        	throw new IOException("Node1 ["+el1.getNodeName()+"] e Node2 ["+el2.getNodeName()+"] non hanno gli stessi prefix. Attributi("+nameSpVet1.size()+") di Node1: "+bfNode1+ " . Attributi("+nameSpVet2.size()+") di Node2: "+bfNode2);
        }    
        


		/*****************chiamata ricorsiva per i figli********************/
		Iterator<?> child=el1.getChildElements();
		Iterator<?> child2=el2.getChildElements();
		while(child.hasNext()){
			if(checkTextComment){
				if(!child2.hasNext()){
					//System.out.println("CHILD1");
					throw new IOException("Node2 ["+el2.getNodeName()+"] non ha child element, mentre il Node1 ["+el1.getNodeName()+"] ne possiede"); 
				}
			}
			Object obj=null;
			if(child.hasNext())
				obj = child.next();
			
			Object obj2=null;
			if(child2.hasNext())
				obj2=child2.next();
			
			if(checkTextComment==false){
				
				while( (obj!=null) && (obj instanceof Text) ){
					if(child.hasNext()){
						obj=child.next();
					}
					else{
						obj=null;
					}
				}
				
				while( (obj2!=null) && (obj2 instanceof Text) ){
					if(child2.hasNext()){
						obj2=child2.next();
					}
					else{
						obj2=null;
					}
				}
			
				if(obj==null){
					if(obj2!=null){
						throw new IOException("Node2 ["+el2.getNodeName()+"] possiede ulteriori child element ("+((SOAPElement)obj2).getNodeName()+") non presenti nel Node1 ["+el1.getNodeName()+"]");
					}
					else{
						break; // elementi terminati
					}
				}
				else{
					if(obj2==null){
						throw new IOException("Node1 ["+el1.getNodeName()+"] possiede ulteriori child element ("+((SOAPElement)obj).getNodeName()+") non presenti nel Node2 ["+el2.getNodeName()+"]");
					}
				}
			}
			
			
			if (obj instanceof Text) {
				Text text = (Text) obj;
				if (!(obj2 instanceof Text)){
					//System.out.println("CHILD2");
					throw new IOException("Node2 ["+el2.getNodeName()+"] non possiede l'element Text presente nel Node1 ["+el1.getNodeName()+"] (valore: "+text.toString()+")"); 
				}
				else{
					Text text2 = (Text) obj2;
					boolean value = text.toString().equals(text2.toString());
					//System.out.println("CHILD3 ["+value+"]");
					if(value==false){
						throw new IOException("Node2 ["+el2.getNodeName()+"] possiede un element Text con valore ("+text2.toString()+") differente da quello presente nel Node1 ["+el1.getNodeName()+"] (valore:"+text.toString()+")");
					}
				}
			}
			else{
				if(obj2 instanceof Text){
					//System.out.println("CHILD4");
					throw new IOException("Node2 ["+el2.getNodeName()+"] possiede un element Text ("+((Text)obj2).toString()+") non presente nel Node1 ["+el1.getNodeName()+"]"); 
				}
				@SuppressWarnings("unchecked")
				Vector<String> namespacePrefixEl1Parent = (Vector<String>) namespacePrefixEl1.clone();
				equalsSoapElements((SOAPElement)obj, (SOAPElement)obj2 , namespacePrefixEl1Parent,checkTextComment);
			}
		}


	}
	
	
	
	
	
	
	public static String dumpMessage(OpenSPCoop2Message msg,boolean dumpAllAttachments) throws SOAPException, IOException{
		StringBuffer out = new StringBuffer();
		out.append("------ SOAPEnvelope ------\n");
		out.append(msg.getAsString(msg.getSOAPPart().getEnvelope(),false));
		java.util.Iterator<?> it = msg.getAttachments();
	    while(it.hasNext()){
	    	AttachmentPart ap = 
	    		(AttachmentPart) it.next();
	    	if(ap.getContentId()!=null)
	    		out.append("\n------ Attachment id["+ap.getContentId()+"]------");
	    	else if(ap.getContentLocation()!=null)
	    		out.append("\n------ Attachment location["+ap.getContentLocation()+"]------");
	    	out.append("\nId["+ap.getContentId()+"] location["+ap.getContentLocation()+"] type["+ap.getContentType()+"]: ");
			
	    	if(dumpAllAttachments){
	    		out.append(MessageUtils.dumpAttachment(msg, ap));
	    	}else{
	    		//Object o = ap.getContent(); NON FUNZIONA CON TOMCAT
	    		Object o = ap.getDataHandler().getContent();
	    		//System.out.println("["+o.getClass().getName()+"])"+ap.getContentType()+"(");
	    		if(Costanti.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP.equals(ap.getContentType())){
	    			 // reimposto handler
	    			 MessageUtils.rebuildAttachmentAsByteArray(msg, ap);
	    		}
	    		
	    		if(o instanceof String){
	    			out.append((String)o);
	    		}else{
	    			 out.append("Contenuto attachments non è visualizzabile, tipo: "+o.getClass().getName());
	    		}
	    	}
	    }
	    return out.toString();
	}
	
	public static String dumpAttachment(OpenSPCoop2Message msg,AttachmentPart ap) throws SOAPException,IOException{
		Object o = _dumpAttachment(msg, ap);
		if(o == null){
			throw new SOAPException("Dump error (return null reference)");
		}
		if(o instanceof String){
			return (String) o;
		}
		else if(o instanceof java.io.ByteArrayOutputStream){
			java.io.ByteArrayOutputStream bout = null;
			try{
				bout = (java.io.ByteArrayOutputStream) o;
				return bout.toString();
			}finally{
				try{
					if(bout!=null){
						bout.close();
					}
				}catch(Exception eClose){}
			}
		}
		else{
			throw new SOAPException("Dump error (return type "+o.getClass().getName()+" unknown)");
		}
	}
	public static byte[] dumpAttachmentAsByteArray(OpenSPCoop2Message msg,AttachmentPart ap) throws SOAPException,IOException{
		Object o = _dumpAttachment(msg, ap);
		if(o == null){
			throw new SOAPException("Dump error (return null reference)");
		}
		if(o instanceof String){
			return ((String) o).getBytes();
		}
		else if(o instanceof java.io.ByteArrayOutputStream){
			java.io.ByteArrayOutputStream bout = null;
			try{
				bout = (java.io.ByteArrayOutputStream) o;
				return bout.toByteArray();
			}finally{
				try{
					if(bout!=null){
						bout.close();
					}
				}catch(Exception eClose){}
			}
		}
		else{
			throw new SOAPException("Dump error (return type "+o.getClass().getName()+" unknown)");
		}
	}
	
	private static Object _dumpAttachment(OpenSPCoop2Message msg,AttachmentPart ap) throws SOAPException,IOException{
		java.io.ByteArrayOutputStream bout = null;
		//Object o = ap.getContent(); NON FUNZIONA CON TOMCAT
		//java.io.InputStream inputDH = dh.getInputStream(); NON FUNZIONA CON JBOSS7 e JAVA7 e imbustamentoSOAP con GestioneManifest e rootElementMaggioreUno (tipo: application/octet-stream)
		Object o = ap.getDataHandler().getContent();
		String s = null;
		if(o!=null){
			if(o instanceof byte[]){
				byte[] b = (byte[]) o;
				bout = new ByteArrayOutputStream();
				bout.write(b);
				bout.flush();
				bout.close();
			}
			else if(o instanceof InputStream){
				InputStream is = (InputStream) o;
				bout = new java.io.ByteArrayOutputStream();
		    	byte [] readB = new byte[8192];
				int readByte = 0;
				while((readByte = is.read(readB))!= -1)
					bout.write(readB,0,readByte);
				is.close();
				bout.flush();
				bout.close();
			}
			else if(o instanceof String){
				s = (String) o;
				bout = new ByteArrayOutputStream();
				bout.write(s.getBytes());
				bout.flush();
				bout.close();
			}
			else{
				// Provo con codiceOriginale ma in jboss non funziona sempre
				javax.activation.DataHandler dh= ap.getDataHandler();  
		    	java.io.InputStream inputDH = dh.getInputStream();
				bout = new java.io.ByteArrayOutputStream();
		    	byte [] readB = new byte[8192];
				int readByte = 0;
				while((readByte = inputDH.read(readB))!= -1)
					bout.write(readB,0,readByte);
				inputDH.close();
				bout.flush();
				bout.close();		
			}
		}
		else{
			// Provo con codiceOriginale ma in jboss non funziona sempre
			javax.activation.DataHandler dh= ap.getDataHandler();  
	    	java.io.InputStream inputDH = dh.getInputStream();
			bout = new java.io.ByteArrayOutputStream();
	    	byte [] readB = new byte[8192];
			int readByte = 0;
			while((readByte = inputDH.read(readB))!= -1)
				bout.write(readB,0,readByte);
			inputDH.close();
			bout.flush();
			bout.close();		
		}
		// Per non perdere quanto letto
		if(Costanti.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP.equals(ap.getContentType())){
			 // reimposto handler
			 MessageUtils.rebuildAttachmentAsByteArray(msg, ap);
		}
		else if(MailcapActivationReader.existsDataContentHandler(ap.getContentType())){
			//ap.setDataHandler(new javax.activation.DataHandler(bout.toByteArray(),ap.getContentType()));
			// In axiom l'update del data handler non funziona
			if(ap.getContentType()!=null && ap.getContentType().startsWith("text/plain")){
				if(s!=null){
					msg.updateAttachmentPart(ap, s, ap.getContentType());
				}
				else{
					msg.updateAttachmentPart(ap, bout.toString(),ap.getContentType());
				}
			}else{
				msg.updateAttachmentPart(ap, bout.toByteArray(),ap.getContentType());
			}
		}
		if(s!=null){
			return s;
		}else{
			return bout;
		}
	}
		
	private static void rebuildAttachmentAsByteArray(OpenSPCoop2Message msg,AttachmentPart ap) throws SOAPException,IOException{
		javax.activation.DataHandler dh= ap.getDataHandler();  
    	java.io.InputStream inputDH = dh.getInputStream();
		java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
    	byte [] readB = new byte[8192];
		int readByte = 0;
		while((readByte = inputDH.read(readB))!= -1)
			bout.write(readB,0,readByte);
		inputDH.close();
		bout.flush();
		bout.close();
		//ap.setDataHandler(new javax.activation.DataHandler(bout.toByteArray(),ap.getContentType()));
		// In axiom l'update del data handler non funziona
		msg.updateAttachmentPart(ap, bout.toByteArray(),ap.getContentType());
	}

	
		
	// Parse-Exception
	
	public static ParseException buildParseException(Throwable e){
		
		ParseException pe = new ParseException();
		pe.setSourceException(e);
		
		if(e==null){
			pe.setParseException(new Exception("Occurs Parsing Error"));
			pe.setSourceException(new Exception("Occurs Parsing Error"));
			return pe;
		}
		
		Throwable tmp = MessageUtils.getParseException(e);
		if(tmp!=null){
			pe.setParseException(tmp);
			return pe;
		}
		
		pe.setParseException(getInnerNotEmptyMessageException(e));
		return pe;
	}
	
	public static Throwable getParseException(Throwable e){
		
		// Prima verifico presenza di eccezioni che sicuramente non evidenziano problemi di parsing
		if(e instanceof java.net.SocketException){
			return null;
		}
		else if(Utilities.existsInnerException(e, java.net.SocketException.class)){
			return null;
		}
		
		
		// Cerco eccezione di parsing
		
		boolean found = false;
		
		Throwable tmp = null;		
				
		if(tmp==null){
			if(Utilities.isExceptionInstanceOf("org.apache.axiom.om.OMException", e)){
				tmp = e;
			}
			else if(Utilities.existsInnerException(e, "org.apache.axiom.om.OMException")){
				tmp = Utilities.getInnerException(e, "org.apache.axiom.om.OMException");
			}
			if(tmp!=null){
				if( ! (tmp.getMessage()!=null && !"".equals(tmp.getMessage()) && !"null".equalsIgnoreCase(tmp.getMessage())) ){
					// cerco prossima eccezione, in questa c'è null come message
					tmp = null;
					found = true;
				}
			}
		}
		
		if(tmp==null){
			if(e instanceof WstxException){
				tmp = e;
			}
			else if(Utilities.existsInnerException(e, WstxException.class)){
				tmp = Utilities.getInnerException(e, WstxException.class);
			}
			if(tmp!=null){
				if( ! (tmp.getMessage()!=null && !"".equals(tmp.getMessage()) && !"null".equalsIgnoreCase(tmp.getMessage())) ){
					// cerco prossima eccezione, in questa c'è null come message
					tmp = null;
					found = true;
				}
			}
		}
		
		if(tmp==null){
			if(Utilities.isExceptionInstanceOf("com.ctc.wstx.exc.WstxIOException", e)){
				tmp = e;
			}
			else if(Utilities.existsInnerException(e, "com.ctc.wstx.exc.WstxIOException")){
				tmp = Utilities.getInnerException(e, "com.ctc.wstx.exc.WstxIOException");
			}
			if(tmp!=null){
				if( ! (tmp.getMessage()!=null && !"".equals(tmp.getMessage()) && !"null".equalsIgnoreCase(tmp.getMessage())) ){
					// cerco prossima eccezione, in questa c'è null come message
					tmp = null;
					found = true;
				}
			}
		}
		
		if(tmp==null){
			if(Utilities.isExceptionInstanceOf("com.ctc.wstx.exc.WstxParsingException", e)){
				tmp = e;
			}
			else if(Utilities.existsInnerException(e, "com.ctc.wstx.exc.WstxParsingException")){
				tmp = Utilities.getInnerException(e, "com.ctc.wstx.exc.WstxParsingException");
			}
			if(tmp!=null){
				if( ! (tmp.getMessage()!=null && !"".equals(tmp.getMessage()) && !"null".equalsIgnoreCase(tmp.getMessage())) ){
					// cerco prossima eccezione, in questa c'è null come message
					tmp = null;
					found = true;
				}
			}
		}
		
		if(tmp==null){
			if(Utilities.isExceptionInstanceOf("com.ctc.wstx.exc.WstxUnexpectedCharException", e)){
				tmp = e;
			}
			else if(Utilities.existsInnerException(e, "com.ctc.wstx.exc.WstxUnexpectedCharException")){
				tmp = Utilities.getInnerException(e, "com.ctc.wstx.exc.WstxUnexpectedCharException");
			}
			if(tmp!=null){
				if( ! (tmp.getMessage()!=null && !"".equals(tmp.getMessage()) && !"null".equalsIgnoreCase(tmp.getMessage())) ){
					// cerco prossima eccezione, in questa c'è null come message
					tmp = null;
					found = true;
				}
			}
		}
		
		if(tmp==null){
			if(Utilities.isExceptionInstanceOf("org.xml.sax.SAXParseException", e)){
				tmp = e;
			}
			else if(Utilities.existsInnerException(e, "org.xml.sax.SAXParseException")){
				tmp = Utilities.getInnerException(e, "org.xml.sax.SAXParseException");
			}
			if(tmp!=null){
				if( ! (tmp.getMessage()!=null && !"".equals(tmp.getMessage()) && !"null".equalsIgnoreCase(tmp.getMessage())) ){
					// cerco prossima eccezione, in questa c'è null come message
					tmp = null;
					found = true;
				}
			}
		}

		if(tmp!=null){
			return tmp;
		}
		if(found){
			return getInnerNotEmptyMessageException(e);
		}
		return null;
		
	}
	
	public static boolean isEmpytMessageException(Throwable e){
		if(e.getMessage()==null ||
				"".equals(e.getMessage()) || 
				"null".equalsIgnoreCase(e.getMessage()) || 
				"com.ctc.wstx.exc.WstxIOException: null".equalsIgnoreCase(e.getMessage())){
			return true;
		}
		else{
			return false;
		}
	}
	
	public static Throwable getInnerNotEmptyMessageException(Throwable e){
		if(!isEmpytMessageException(e)){
			return e;
		}
		
		if(e.getCause()!=null){
			//System.out.println("INNER ["+e.getClass().getName()+"]...");
			return getInnerNotEmptyMessageException(e.getCause());
		}
		else{
			return e; // sono nella foglia, ritorno comunque questa eccezione
		}
	}

}
