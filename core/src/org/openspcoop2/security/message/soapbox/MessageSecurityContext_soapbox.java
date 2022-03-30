/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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



package org.openspcoop2.security.message.soapbox;

import org.adroitlogic.soapbox.CryptoSupport;
import org.slf4j.Logger;
import org.apache.xml.security.exceptions.AlgorithmAlreadyRegisteredException;
import org.apache.xml.security.transforms.Transform;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.IMessageSecurityContext;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.constants.WSSAttachmentsConstants;
import org.openspcoop2.security.message.signature.XMLSecAttachmentContentTransform;

/**
 * WSSContext_soapbox
 *
 * @author Andrea Poli (apoli@link.it)
 * @author Giovanni Bussu (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageSecurityContext_soapbox implements IMessageSecurityContext{

	private static boolean initialized = false;
	private static synchronized void init_engine(MessageSecurityContext wssContext)throws SecurityException {
		if(MessageSecurityContext_soapbox.initialized==false){
			
			try{ 		
				MessageSecurityContext_soapbox.initWsuIdAllocator(wssContext.getPrefixWsuId(),wssContext.getLog());
	    	}catch(Throwable e){
	    		wssContext.getLog().error("Inizializzazione wsu id allocator non riuscita: "+e.getMessage(),e);
	    		throw new SecurityException(e.getMessage(),e);
	    	}
			
			try{ 		
				CryptoSupport.initializeInstance(100, 100, 100);
			}catch(Throwable e){
	    		wssContext.getLog().error("Inizializzazione CryptoSupport non riuscita: "+e.getMessage(),e);
	    		throw new SecurityException(e.getMessage(),e);
	    	}
				
//			try{ 
//				org.apache.xml.security.Init.init();
//			}catch(Throwable e){
//	    		wssContext.getLog().error("Inizializzazione org.apache.xml.security non riuscita: "+e.getMessage(),e);
//	    		throw new SecurityException(e.getMessage(),e);
//	    	}
			
			// Li registro entrambi in modo da poter switchare tra le implementazioni
			try{ 
				try{
					wssContext.getLog().info("Transformer registrato: ["+XMLSecAttachmentContentTransform.class.getName()+"]");
					Transform.register(WSSAttachmentsConstants.ATTACHMENT_CONTENT_SIGNATURE_TRANSFORM_URI, XMLSecAttachmentContentTransform.class.getName());
				}catch(AlgorithmAlreadyRegisteredException e){
					// Succede in caso di hot redeploy
					wssContext.getLog().debug("Registrazione org.apache.xml.security.transforms.Transform non riuscita, classe gia registrata: "+e.getMessage());
				}
				catch(Throwable e){
		    		wssContext.getLog().error("Registrazione org.apache.xml.security.transforms.Transform non riuscita: "+e.getMessage(),e);
		    		throw new SecurityException(e.getMessage(),e);
		    	}
			}
			catch(SecurityException e){
				throw e;
			}
			catch(Throwable e){
	    		wssContext.getLog().error("Inizializzazione Trasform Security non riuscita: "+e.getMessage(),e);
	    		throw new SecurityException(e.getMessage(),e);
	    	}
			
			
			MessageSecurityContext_soapbox.initialized = true;
		}
	}
	
	@Override
	public void init(MessageSecurityContext wssContext) throws SecurityException {

		if(MessageSecurityContext_soapbox.initialized==false){
			MessageSecurityContext_soapbox.init_engine(wssContext);
		}
		
	}


	/** WSS Id Allocator */
	private static org.apache.wss4j.dom.WsuIdAllocator wsuIdAllocator = null;
	private static String prefixWsuId = null;
	public static org.apache.wss4j.dom.WsuIdAllocator getWsuIdAllocator() {
		return MessageSecurityContext_soapbox.wsuIdAllocator;
	}
	private static synchronized void initWsuIdAllocator(String prefixWsuIdParam,Logger log) throws Exception{
		if(MessageSecurityContext_soapbox.wsuIdAllocator==null){
			MessageSecurityContext_soapbox.prefixWsuId=prefixWsuIdParam;
			if(prefixWsuIdParam==null || "".equals(prefixWsuIdParam)){
				MessageSecurityContext_soapbox.wsuIdAllocator = new org.openspcoop2.security.message.WsuIdAllocator("openspcoop2_soapbox_");// Default di wss4j
			}
			else{
				MessageSecurityContext_soapbox.wsuIdAllocator = new org.openspcoop2.security.message.WsuIdAllocator(prefixWsuIdParam);
			}
			log.info("WsuIdAllocator="+MessageSecurityContext_soapbox.wsuIdAllocator.getClass().getName());
		}
		else{
			if(MessageSecurityContext_soapbox.prefixWsuId==null){
				throw new Exception("WsuIdAllocator istanziato con la classe ["+MessageSecurityContext_soapbox.wsuIdAllocator.getClass().getName()+"] e variabile prefixWsuId non istanziata??");
			}
			else if(!MessageSecurityContext_soapbox.prefixWsuId.equals(prefixWsuIdParam)){
				throw new Exception("WsuIdAllocator gia' istanziato con la classe ["+MessageSecurityContext_soapbox.wsuIdAllocator.getClass().getName()
						+"] e variabile prefixWsuId uguale al valore ["+MessageSecurityContext_soapbox.prefixWsuId+"]. Nuovo valore ["+prefixWsuIdParam+"] non impostabile.");
			}
		}
	}

}