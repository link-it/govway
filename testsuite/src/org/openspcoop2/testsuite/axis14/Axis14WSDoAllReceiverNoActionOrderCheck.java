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


package org.openspcoop2.testsuite.axis14;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.SOAPPart;
import org.apache.ws.axis.security.WSDoAllReceiver;
import org.apache.ws.security.SOAPConstants;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.handler.WSHandler;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.handler.WSHandlerResult;
import org.apache.ws.security.message.token.Timestamp;
import org.apache.ws.security.util.WSSecurityUtil;
import org.apache.xml.security.utils.XMLUtils;
import org.openspcoop2.utils.date.DateManager;
import org.w3c.dom.Document;

import javax.security.auth.callback.CallbackHandler;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import java.io.ByteArrayOutputStream;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

/**
 * 
 *
 * @author Werner Dittmann (werner@apache.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class

Axis14WSDoAllReceiverNoActionOrderCheck extends WSDoAllReceiver {

	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
    private static Log tlog =
        LogFactory.getLog("org.apache.ws.security.TIME");
        
    /**
     * Axis calls invoke to handle a message.
     * <p/>
     *
     * @param msgContext message context.
     * @throws AxisFault
     */
    @SuppressWarnings(value = "unchecked")
    @Override public void invoke(MessageContext msgContext) throws AxisFault {

    	boolean doDebug = WSDoAllReceiver.log.isDebugEnabled();

        if (doDebug) {
        	try{
        		WSDoAllReceiver.log.debug("WSDoAllReceiverNoActionOrderCheck: enter invoke() with msg type: "
        				+ msgContext.getCurrentMessage().getMessageType());
        	}catch(Exception e){
        		WSDoAllReceiver.log.debug("Errore WSDoAllReceiverNoActionOrderCheck",e);
        	}
        }
        long t0 = 0, t1 = 0, t2 = 0, t3 = 0, t4 = 0;
        if (Axis14WSDoAllReceiverNoActionOrderCheck.tlog.isDebugEnabled()) {
            t0 = DateManager.getTimeMillis();
        }        

        RequestData reqData = new RequestData();
        /*
        * The overall try, just to have a finally at the end to perform some
        * housekeeping.
        */
        try {
            reqData.setMsgContext(msgContext);

            @SuppressWarnings("rawtypes")
			Vector actions = new Vector();
            String action = null;
            if ((action = (String) getOption(WSHandlerConstants.ACTION)) == null) {
                action = (String) msgContext
                        .getProperty(WSHandlerConstants.ACTION);
            }
            if (action == null) {
                throw new AxisFault("WSDoAllReceiverNoActionOrderCheck: No action defined");
            }
            int doAction = WSSecurityUtil.decodeAction(action, actions);

            String actor = (String) getOption(WSHandlerConstants.ACTOR);

            Message sm = msgContext.getCurrentMessage();
            Document doc = null;

            /**
             * We did not receive anything...Usually happens when we get a
             * HTTP 202 message (with no content)
             */
            if(sm == null){
                return;
            }

            try {
                doc = sm.getSOAPEnvelope().getAsDocument();
                if (doDebug) {
                    WSDoAllReceiver.log.debug("Received SOAP request: ");
                    WSDoAllReceiver.log.debug(org.apache.axis.utils.XMLUtils
                            .PrettyDocumentToString(doc));
                }
            } catch (Exception ex) {
                throw new AxisFault(
                        "WSDoAllReceiverNoActionOrderCheck: cannot convert into document", ex);
            }
            /*
            * Check if it's a response and if its a fault. Don't process
            * faults.
            */
            String msgType = sm.getMessageType();
            if (msgType != null && msgType.equals(Message.RESPONSE)) {
                SOAPConstants soapConstants = WSSecurityUtil
                        .getSOAPConstants(doc.getDocumentElement());
                if (WSSecurityUtil.findElement(doc.getDocumentElement(),
                        "Fault", soapConstants.getEnvelopeURI()) != null) {
                    return;
                }
            }

            /*
            * To check a UsernameToken or to decrypt an encrypted message we
            * need a password.
            */
            CallbackHandler cbHandler = null;
            if ((doAction & (WSConstants.ENCR | WSConstants.UT)) != 0) {
                cbHandler = getPasswordCB(reqData);
            }

            /*
            * Get and check the Signature specific parameters first because
            * they may be used for encryption too.
            */
            doReceiverAction(doAction, reqData);
            
            @SuppressWarnings("rawtypes")
			Vector wsResult = null;
            if (Axis14WSDoAllReceiverNoActionOrderCheck.tlog.isDebugEnabled()) {
                t1 = DateManager.getTimeMillis();
            }        

            try {
                wsResult = WSHandler.secEngine.processSecurityHeader(doc, actor,
                        cbHandler, reqData.getSigCrypto(), reqData.getDecCrypto());
            } catch (WSSecurityException ex) {
                ex.printStackTrace();
                throw new AxisFault(
                        "WSDoAllReceiverNoActionOrderCheck: security processing failed", ex);
            }

            if (Axis14WSDoAllReceiverNoActionOrderCheck.tlog.isDebugEnabled()) {
                t2 = DateManager.getTimeMillis();
            }        

            if (wsResult == null) { // no security header found
                if (doAction == WSConstants.NO_SECURITY) {
                    return;
                } else {
                    throw new AxisFault(
                            "WSDoAllReceiverNoActionOrderCheck: Request does not contain required Security header");
                }
            }

            if (reqData.getWssConfig().isEnableSignatureConfirmation() && msgContext.getPastPivot()) {
                checkSignatureConfirmation(reqData, wsResult);
            }
            /*
            * save the processed-header flags
            */
            ArrayList<javax.xml.namespace.QName> processedHeaders = new ArrayList<javax.xml.namespace.QName>();
            @SuppressWarnings("rawtypes")
			Iterator iterator = sm.getSOAPEnvelope().getHeaders().iterator();
            while (iterator.hasNext()) {
                org.apache.axis.message.SOAPHeaderElement tempHeader = (org.apache.axis.message.SOAPHeaderElement) iterator
                        .next();
                if (tempHeader.isProcessed()) {
                    processedHeaders.add(tempHeader.getQName());
                }
            }

            /*
            * If we had some security processing, get the original SOAP part of
            * Axis' message and replace it with new SOAP part. This new part
            * may contain decrypted elements.
            */
            SOAPPart sPart = (org.apache.axis.SOAPPart) sm.getSOAPPart();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            XMLUtils.outputDOM(doc, os, true);
            sPart.setCurrentMessage(os.toByteArray(), SOAPPart.FORM_BYTES);
            if (doDebug) {
                WSDoAllReceiver.log.debug("Processed received SOAP request");
                WSDoAllReceiver.log.debug(org.apache.axis.utils.XMLUtils
                        .PrettyDocumentToString(doc));
            }
            if (Axis14WSDoAllReceiverNoActionOrderCheck.tlog.isDebugEnabled()) {
                t3 = DateManager.getTimeMillis();
            }        

            /*
            * set the original processed-header flags
            */
            iterator = processedHeaders.iterator();
            while (iterator.hasNext()) {
                QName qname = (QName) iterator.next();
                @SuppressWarnings("rawtypes")
				Enumeration headersByName = sm.getSOAPEnvelope().getHeadersByName(
                        qname.getNamespaceURI(), qname.getLocalPart());
                while (headersByName.hasMoreElements()) {
                    org.apache.axis.message.SOAPHeaderElement tempHeader =
                        (org.apache.axis.message.SOAPHeaderElement) headersByName.nextElement();
                    tempHeader.setProcessed(true);
                }
            }

            /*
            * After setting the new current message, probably modified because
            * of decryption, we need to locate the security header. That is, we
            * force Axis (with getSOAPEnvelope()) to parse the string, build
            * the new header. Then we examine, look up the security header and
            * set the header as processed.
            *
            * Please note: find all header elements that contain the same actor
            * that was given to processSecurityHeader(). Then check if there is
            * a security header with this actor.
            */

            SOAPHeader sHeader = null;
            try {
                sHeader = sm.getSOAPEnvelope().getHeader();
            } catch (Exception ex) {
                throw new AxisFault(
                        "WSDoAllReceiverNoActionOrderCheck: cannot get SOAP header after security processing",
                        ex);
            }

            @SuppressWarnings("rawtypes")
			Iterator headers = sHeader.examineHeaderElements(actor);

            SOAPHeaderElement headerElement = null;
            while (headers.hasNext()) {
                org.apache.axis.message.SOAPHeaderElement hE = (org.apache.axis.message.SOAPHeaderElement) headers.next();
                if (hE.getLocalName().equals(WSConstants.WSSE_LN)
                        && hE.getNamespaceURI().equals(WSConstants.WSSE_NS)) {
                    headerElement = hE;
                    break;
                }
            }
            ((org.apache.axis.message.SOAPHeaderElement) headerElement)
                    .setProcessed(true);

            /*
            * Now we can check the certificate used to sign the message. In the
            * following implementation the certificate is only trusted if
            * either it itself or the certificate of the issuer is installed in
            * the keystore.
            *
            * Note: the method verifyTrust(X509Certificate) allows custom
            * implementations with other validation algorithms for subclasses.
            */

            // Extract the signature action result from the action vector
            WSSecurityEngineResult actionResult = WSSecurityUtil
                    .fetchActionResult(wsResult, WSConstants.SIGN);

            if (actionResult != null) {
                X509Certificate returnCert = (X509Certificate) actionResult.get(WSSecurityEngineResult.TAG_X509_CERTIFICATE) ;

                if (returnCert != null) {
                    if (!verifyTrust(returnCert, reqData)) {
                        throw new AxisFault(
                                "WSDoAllReceiverNoActionOrderCheck: The certificate used for the signature is not trusted");
                    }
                }
            }

            /*
            * Perform further checks on the timestamp that was transmitted in
            * the header. In the following implementation the timestamp is
            * valid if it was created after (now-ttl), where ttl is set on
            * server side, not by the client.
            *
            * Note: the method verifyTimestamp(Timestamp) allows custom
            * implementations with other validation algorithms for subclasses.
            */

            // Extract the timestamp action result from the action vector
            actionResult = WSSecurityUtil.fetchActionResult(wsResult,
                    WSConstants.TS);

            if (actionResult != null) {
                Timestamp timestamp = (Timestamp) actionResult.get(WSSecurityEngineResult.TAG_TIMESTAMP);
      
                if (timestamp != null) {
                    if (!verifyTimestamp(timestamp, decodeTimeToLive(reqData))) {
                        throw new AxisFault(
                                "WSDoAllReceiverNoActionOrderCheck: The timestamp could not be validated");
                    }
                }
            }

            /*
            * now check the security actions: do they match, in right order?
            * eliminato per compatibilita' con Microsoft ed Oracle
            */
             /*
            if (!checkReceiverResults(wsResult, actions)) {
                throw new AxisFault(
                    "WSDoAllReceiverNoActionOrderCheck: security processing failed (actions mismatch)");                
                
            }
              */

            /*
            * All ok up to this point. Now construct and setup the security
            * result structure. The service may fetch this and check it.
            */
            Vector<WSHandlerResult> results = null;
            if ((results = ((Vector<WSHandlerResult>) msgContext
			    .getProperty(WSHandlerConstants.RECV_RESULTS))) == null) {
                results = new Vector<WSHandlerResult>();
                msgContext
                        .setProperty(WSHandlerConstants.RECV_RESULTS, results);
            }
            WSHandlerResult rResult = new WSHandlerResult(actor, wsResult);
            results.add(0, rResult);
            if (Axis14WSDoAllReceiverNoActionOrderCheck.tlog.isDebugEnabled()) {
                t4 = DateManager.getTimeMillis();
                Axis14WSDoAllReceiverNoActionOrderCheck.tlog.debug("Receive request: total= " + (t4 - t0) +
                        " request preparation= " + (t1 - t0) +
                        " request processing= " + (t2 - t1) +
                        " request to Axis= " + (t3 - t2) + 
                        " header, cert verify, timestamp= " + (t4 - t3) +
                        "\n");                                
            }        

            if (doDebug) {
                WSDoAllReceiver.log.debug("WSDoAllReceiverNoActionOrderCheck: exit invoke()");
            }
        } catch (WSSecurityException e) {
            throw new AxisFault(e.getMessage(), e);
        } finally {
            reqData.clear();
            reqData = null;
        }
    }
}
