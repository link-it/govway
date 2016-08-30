/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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



package org.openspcoop2.testsuite.axis14;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import org.apache.axis.MessageContext;
import org.apache.axis.client.AxisClient;
import org.apache.axis.server.AxisServer;
import org.slf4j.Logger;
import org.apache.ws.security.WSSConfig;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.openspcoop2.utils.LoggerWrapperFactory;

/**
 * Classe di base per la gestione del WS-Security.
 *
 * @author Spadafora Marcello <Ma.Spadafora@finsiel.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Axis14WSSBaseUtils {
    
    private MessageContext messageContext;
    private String actor = null;
    private String actorDefault = null;
    
    private Logger log;
    
    private boolean useActorDefaultIfNotDefined = true;
    
    public Axis14WSSBaseUtils(AxisClient engine,boolean useActorDefaultIfNotDefined,String actorDefault,Logger log){    	
    	this(engine,useActorDefaultIfNotDefined,actorDefault,log,null);
    }
    public Axis14WSSBaseUtils(AxisClient engine,boolean useActorDefaultIfNotDefined,String actorDefault,Logger log,String prefixWsuId){
    	this.messageContext = new MessageContext(engine);
    	this.messageContext.setProperty(org.apache.axis.AxisEngine.PROP_DISABLE_PRETTY_XML, true);
    	this.useActorDefaultIfNotDefined = useActorDefaultIfNotDefined;
    	this.actorDefault = actorDefault;
    	if(log!=null)
    		this.log = log;
    	else
    		this.log = LoggerWrapperFactory.getLogger("OpenSPCoopWSSSecurityEngine");
    	try{
    		initWsuIdAllocator(prefixWsuId,this.log);
    	}catch(Exception e){
    		this.log.error("Inizializzazione wsu id allocator non riuscita: "+e.getMessage(),e);
    	}
    }

    public Axis14WSSBaseUtils(AxisServer engine,boolean useActorDefaultIfNotDefined,String actorDefault,Logger log){    	
    	this(engine,useActorDefaultIfNotDefined,actorDefault,log,null);
    }
    public Axis14WSSBaseUtils(AxisServer engine,boolean useActorDefaultIfNotDefined,String actorDefault,Logger log,String prefixWsuId){
    	this.messageContext = new MessageContext(engine);
    	this.messageContext.setProperty(org.apache.axis.AxisEngine.PROP_DISABLE_PRETTY_XML, true);
    	this.useActorDefaultIfNotDefined = useActorDefaultIfNotDefined;
    	this.actorDefault = actorDefault;
    	if(log!=null)
    		this.log = log;
    	else
    		this.log = LoggerWrapperFactory.getLogger("OpenSPCoopWSSSecurityEngine");
    	try{
    		initWsuIdAllocator(prefixWsuId,this.log);
    	}catch(Exception e){
    		this.log.error("Inizializzazione wsu id allocator non riuscita: "+e.getMessage(),e);
    	}
    }
    
    private static org.apache.ws.security.WsuIdAllocator wsuIdAllocator = null;
    private static String prefixWsuId = null;
    private static synchronized void initWsuIdAllocator(String prefixWsuIdParam,Logger log) throws Exception{
    	if(wsuIdAllocator==null){
    		WSSConfig config = WSSConfig.getDefaultWSConfig();
    		Axis14WSSBaseUtils.prefixWsuId=prefixWsuIdParam;
    		if(prefixWsuIdParam==null || "".equals(prefixWsuIdParam)){
    			wsuIdAllocator = config.getIdAllocator(); // Default di wss4j
    		}
    		else{
    			wsuIdAllocator = new Axis14WsuIdAllocator(prefixWsuIdParam);
    			config.setIdAllocator(wsuIdAllocator);
    		}
    		log.info("WsuIdAllocator="+config.getIdAllocator().getClass().getName());
    	}
    	else{
    		if(Axis14WSSBaseUtils.prefixWsuId==null){
				throw new Exception("WsuIdAllocator istanziato con la classe ["+wsuIdAllocator.getClass().getName()+"] e variabile prefixWsuId non istanziata??");
			}
			else if(!Axis14WSSBaseUtils.prefixWsuId.equals(prefixWsuIdParam)){
				throw new Exception("WsuIdAllocator gia' istanziato con la classe ["+wsuIdAllocator.getClass().getName()
						+"] e variabile prefixWsuId uguale al valore ["+Axis14WSSBaseUtils.prefixWsuId+"]. Nuovo valore ["+prefixWsuIdParam+"] non impostabile.");
			}
    	}
    }
    
    public void setMessageContext(Hashtable<?,?> wssProperties) {
    	
    	boolean actor = false;
    	boolean mustUnderstandTrue=false;
    	
        if (wssProperties != null && wssProperties.size() > 0) {
            for (Enumeration<?> e = wssProperties.keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                String value = (String) wssProperties.get(key);
                if (WSHandlerConstants.ENCRYPTION_USER.equals(key)
                        && WSHandlerConstants.USE_REQ_SIG_CERT.equals(value)) {
                    // value = ...;
                }
                this.messageContext.setProperty(key, value);
                
                // check actor
                if("actor".equals(key)){
                	this.actor = value;
                }
                
                // Check Fix Bug#18 Test-1
                if("mustUnderstand".equals(key)){
                	if("true".equals(value)){
                		mustUnderstandTrue = true;
                	}
                }else if("actor".equals(key)){
                	actor = true;
                }
            }
        }
        
        if( mustUnderstandTrue && actor==false && this.useActorDefaultIfNotDefined ){
        	// Aggiungo actor di default ('openspcoop') Porta di Dominio
        	if(this.actorDefault!=null)
        		this.messageContext.setProperty("actor", this.actorDefault);
        	else
        		this.messageContext.setProperty("actor", "openspcoop");
        }
    }

    protected MessageContext getMessageContext() {
        return this.messageContext;
    }
    
	public String getActor() {
		return this.actor;
	}
    

	
	/** UTILITY */
	
    public static final String WSS_HEADER_ELEMENT = "Security";
    public static final String WSS_HEADER_ELEMENT_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    
    public static boolean existsHeaderWSS(SOAPMessage msg,String actor){
    	return Axis14WSSBaseUtils.existsHeaderWSS(msg, actor,null);
    }
    public static boolean existsHeaderWSS(SOAPMessage msg,String actor,Logger log){
   
    	try{
	    	if(msg==null){
	    		return false;
	    	}
	    	SOAPHeader header = msg.getSOAPHeader();
	    	if(header==null || (header.hasChildNodes()==false) ){
	    		return false;
	    	}
	       	java.util.Iterator<?> it = header.examineAllHeaderElements();
			while( it.hasNext()  ){
	
				// Test Header Element
				SOAPHeaderElement headerElement = (SOAPHeaderElement) it.next();
				if(   Axis14WSSBaseUtils.WSS_HEADER_ELEMENT.equals(headerElement.getLocalName()) &&
						Axis14WSSBaseUtils.WSS_HEADER_ELEMENT_NAMESPACE.equals(headerElement.getNamespaceURI()) ){
					// potenziale header WSS, verifico l'actor
					if(actor==null){
						return headerElement.getActor()==null;
					}else{
						return actor.equals(headerElement.getActor());
					}
				}
			}
			return false;
    	}catch(Exception e){
    		if(log!=null)
    			log.error("existsHeaderWSS error con actor["+actor+"]",e);
    		return false;
    	}
    	
    }

	public boolean isUseActorDefaultIfNotDefined() {
		return this.useActorDefaultIfNotDefined;
	}

	public void setUseActorDefaultIfNotDefined(boolean useActorDefaultIfNotDefined) {
		this.useActorDefaultIfNotDefined = useActorDefaultIfNotDefined;
	}
	public Logger getLog() {
		return this.log;
	}


}

