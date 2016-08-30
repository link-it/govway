/*
 * AdroitLogic UltraESB Enterprise Service Bus
 *
 * Copyright (c) 2010 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 *
 * GNU Affero General Public License Usage
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program (See LICENSE-AGPL.TXT).
 * If not, see http://www.gnu.org/licenses/agpl-3.0.html
 *
 * Commercial Usage
 *
 * Licensees holding valid UltraESB Commercial licenses may use this file in accordance with the UltraESB Commercial
 * License Agreement provided with the Software or, alternatively, in accordance with the terms contained in a written
 * agreement between you and AdroitLogic.
 *
 * If you are unsure which license is appropriate for your use, or have questions regarding the use of this file,
 * please contact AdroitLogic at info@adroitlogic.com
 */
/*
 * Modificato per supportare le seguenti funzionalita':
 * - firma e cifratura degli attachments
 * - cifratura con chiave simmetrica
 * - supporto CRL 
 * 
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, 
 * either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope 
 * that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openspcoop2.security.message.soapbox;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.adroitlogic.soapbox.CryptoUtil;
import org.adroitlogic.soapbox.InvalidMessageDataException;
import org.adroitlogic.soapbox.MessageSecurityContext;
import org.adroitlogic.soapbox.Processor;
import org.adroitlogic.soapbox.SBConstants;
import org.adroitlogic.soapbox.SecurityConfig;
import org.adroitlogic.soapbox.SecurityFailureException;
import org.slf4j.Logger;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.w3c.dom.Element;

/**
 * ProcessTimestampedMessage
 *
 * @author Andrea Poli <apoli@link.it>
 * @author Giovanni Bussu <bussu@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProcessTimestampedMessage implements Processor {
    
    private static final Logger logger = LoggerWrapperFactory.getLogger(ProcessTimestampedMessage.class);
    
    private final SimpleDateFormat zuluMillisecondsPrecision = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // SimpleDateFormat non e' thread-safe
    private final SimpleDateFormat zulu = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // SimpleDateFormat non e' thread-safe

    public ProcessTimestampedMessage() {
        this.zuluMillisecondsPrecision.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.zulu.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
	public void process(SecurityConfig secConfig, MessageSecurityContext msgSecCtx) {

        Element elem = CryptoUtil.getSecurityProcessorElement(
            msgSecCtx.getDocument().getDocumentElement(), SBConstants.WSU, "Timestamp");

        if (elem == null) {
            if (ProcessTimestampedMessage.logger.isDebugEnabled()) {
                ProcessTimestampedMessage.logger.debug("Message is not timestamped - skipping ProcessTimestampedMessage");
            }
            throw new SecurityFailureException("WS-Security failure - Message is not timestamped");
        }

        String createdTime = CryptoUtil.getFirstChild(elem, SBConstants.WSU, "Created").getTextContent();
        String expiryTime  = CryptoUtil.getFirstChild(elem, SBConstants.WSU, "Expires").getTextContent();

        Long futureTimeToLive = (Long) msgSecCtx.getProperty(SecurityConstants.TIMESTAMP_FUTURE_TTL);
        boolean isStrict = (Boolean) msgSecCtx.getProperty(SecurityConstants.TIMESTAMP_STRICT);
        
        Calendar created, expiry, valid = null;
        boolean precisionInSeconds = false;
        created = new GregorianCalendar();
        expiry = new GregorianCalendar();
        valid = new GregorianCalendar();
        
    	try {

            synchronized(this.zuluMillisecondsPrecision) {
                created.setTime(this.zuluMillisecondsPrecision.parse(createdTime));
            }
            
    	} catch (ParseException e) {
    		try {

    			synchronized(this.zulu) {
    				created.setTime(this.zulu.parse(createdTime));
    			}
    			
    			precisionInSeconds = true;
    		} catch (ParseException e1) {
    			throw new InvalidMessageDataException("Invalid timestamp data. Created : " + createdTime + " Expected in Zulu format : " + e1.getMessage());
    		}
    	}

        valid.setTimeInMillis(created.getTime().getTime()-futureTimeToLive.longValue());
        SimpleDateFormat format = (precisionInSeconds) ? this.zulu : this.zuluMillisecondsPrecision;

        try {
    		synchronized (format) {
                expiry.setTime(format.parse(expiryTime));
            }
        } catch (ParseException e) {
            throw new InvalidMessageDataException("Invalid timestamp data. " +
                " Expiry : " + expiryTime + " Expected in Zulu format : " + e.getMessage());
        }

        Calendar rightNow;
		try {
			rightNow = DateManager.getCalendar();
		} catch (UtilsException e) {
			rightNow = new GregorianCalendar();
			rightNow.setTimeInMillis(new Date().getTime());
		}
		
        if (expiry.before(created)) {
        	if(isStrict) {
        		throw new SecurityFailureException("Message expiry : " + expiryTime + " before created time : " + createdTime);
        	} else {
                if (ProcessTimestampedMessage.logger.isInfoEnabled()) {
                    ProcessTimestampedMessage.logger.warn("Message expiry : " + expiryTime + " before created time : " + createdTime);
                }
        	}
        } else if (rightNow.before(valid)) {
            synchronized (format) {
            	String msg = "Message is not yet valid. Created timestamp is : " + createdTime + " " +
                        	" Current system time : " + format.format(rightNow.getTime()) + 
                        	" Future time to live set to "+futureTimeToLive/1000l+" seconds";
            	if(isStrict) {
            		throw new SecurityFailureException(msg);
            	} else {
            		if (ProcessTimestampedMessage.logger.isInfoEnabled()) {
                        ProcessTimestampedMessage.logger.warn(msg);
                    }
            	}
            }
        } else if (rightNow.after(expiry)) {
            synchronized (format) {
            	String msg = "Message has expired at : " + expiryTime +
        				" Current system time : " + format.format(rightNow.getTime());
            	if(isStrict) {
            		throw new SecurityFailureException(msg);
            	} else {
            		if (ProcessTimestampedMessage.logger.isInfoEnabled()) {
                        ProcessTimestampedMessage.logger.warn(msg);
                    }
            	}
            }
        } else {
            if (ProcessTimestampedMessage.logger.isDebugEnabled()) {
                synchronized (format) {
                    ProcessTimestampedMessage.logger.debug("Timestamp verified and valid at : " + format.format(rightNow.getTime()));
                }
            }
        }
    }
}
