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
 * OpenSPCoop - Customizable API Gateway 
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
import org.apache.commons.lang.time.FastDateFormat;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * TimestampMessageProcessor
 *
 * @author Andrea Poli <apoli@link.it>
 * @author Giovanni Bussu <bussu@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimestampMessageProcessor implements Processor {

	private final FastDateFormat zulu;
	
	public TimestampMessageProcessor() {
        this.zulu = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("UTC"));
    }
	@Override
	public void process(SecurityConfig secConfig, MessageSecurityContext msgSecCtx) {
        // ensure existence of the wsse:Security header, and create one if none exists
        Document doc = msgSecCtx.getDocument();
        Element wsseSecurityElem = CryptoUtil.getWSSecurityHeader(doc);

        // we will not timestamp an already timestamped document
        if (CryptoUtil.getFirstChildOrNull(wsseSecurityElem, SBConstants.WSU, "Timestamp") != null) {
            throw new InvalidMessageDataException("Message is already timestamped");
        }

        Element timestampElem = doc.createElementNS(SBConstants.WSU, "wsu:Timestamp");
        CryptoUtil.setWsuId(timestampElem, CryptoUtil.getRandomId());
        Element createdElem = doc.createElementNS(SBConstants.WSU, "wsu:Created");
        Element expiresElem = doc.createElementNS(SBConstants.WSU, "wsu:Expires");

        long ttl = msgSecCtx.getTimestampRequest().getTimeForExpiryMillis();
        Calendar currentTime = null;
        Calendar expiryTime = null;
		try {
			currentTime = DateManager.getCalendar();
		} catch (UtilsException e) {
			currentTime = new GregorianCalendar();
			currentTime.setTimeInMillis(new Date().getTime());
		}

        expiryTime = new GregorianCalendar();
        expiryTime.setTimeInMillis(currentTime.getTimeInMillis() + ttl);
		
        synchronized (this.zulu) {
            createdElem.setTextContent(this.zulu.format(currentTime.getTime()));
            expiresElem.setTextContent(this.zulu.format(expiryTime.getTime()));
        }
        timestampElem.appendChild(createdElem);
        timestampElem.appendChild(expiresElem);
        
        Element firstChild = CryptoUtil.getFirstElementChild(wsseSecurityElem);
        if (firstChild != null) {
            wsseSecurityElem.insertBefore(timestampElem, firstChild);
        } else {
            wsseSecurityElem.appendChild(timestampElem);
        }
	}


}
