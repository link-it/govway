/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.connettori.httpcore5;

import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.message.BasicHeaderElementIterator;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;

/**
 * CustomResponseHandler
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CustomKeepAliveStrategy implements ConnectionKeepAliveStrategy {

	@Override
	public TimeValue getKeepAliveDuration(HttpResponse response, HttpContext context) {
		
		 // Honor 'keep-alive' header
        BasicHeaderElementIterator it = new BasicHeaderElementIterator(
                response.headerIterator("Keep-Alive"));
        while (it.hasNext()) {
            HeaderElement he = it.next();
            String param = he.getName(); 
            String value = he.getValue();
            if (value != null && param.equalsIgnoreCase("timeout")) {
                try {
                	/**System.out.println("RETURN HEADER ["+ (Long.parseLong(value) * 1000)+"]");*/
                    long v = Long.parseLong(value) * 1000;
                    return TimeValue.ofMilliseconds(v);
                } catch(NumberFormatException ignore) {
                	// ignore
                }
            }
        }
        /**HttpHost target = (HttpHost) context.getAttribute(
                ExecutionContext.HTTP_TARGET_HOST);
        if ("www.naughty-server.com".equalsIgnoreCase(target.getHostName())) {
            // Keep alive for 5 seconds only
            return 5 * 1000;
        } else {
            // otherwise keep alive for 30 seconds
            return 30 * 1000;
        }*/
        // otherwise keep alive for 2 minutes
        /**System.out.println("RETURN 2 minuti");*/
        long v = 2l * 60l * 1000l;
        return TimeValue.ofMilliseconds(v);
		
	}

}
