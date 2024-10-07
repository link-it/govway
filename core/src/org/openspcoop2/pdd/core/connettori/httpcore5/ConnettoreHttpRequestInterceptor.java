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
package org.openspcoop2.pdd.core.connettori.httpcore5;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.openspcoop2.pdd.core.connettori.ConnettoreLogger;

/**
 * ConnettoreHttpRequestInterceptor
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHttpRequestInterceptor implements HttpRequestInterceptor {

	private ConnettoreLogger connettoreLogger;
	private Map<String,List<String>> recHeaderForInterceptor;
	
	public ConnettoreHttpRequestInterceptor(ConnettoreLogger connettoreLogger, Map<String,List<String>> recHeaderForInterceptor) {
		this.connettoreLogger = connettoreLogger;
		this.recHeaderForInterceptor = recHeaderForInterceptor;
	}
	
	@Override
	public void process(HttpRequest request, EntityDetails details, HttpContext context) throws HttpException, IOException {
		if(request!=null) {
			for (Header hdr : request.getHeaders()) {
				if(!this.recHeaderForInterceptor.containsKey(hdr.getName())) {
					this.connettoreLogger.info("Set Transport Header (httpcore5) ["+hdr.getName()+"]=["+hdr.getValue()+"]",false);
				}
				else {
					List<String> recList = this.recHeaderForInterceptor.get(hdr.getName());
					if(recList==null || !recList.contains(hdr.getValue())) {
						this.connettoreLogger.info("Set Transport Header (httpcore5; different value) ["+hdr.getName()+"]=["+hdr.getValue()+"]",false);
					}
				}
			}
		}
	}

	
}
