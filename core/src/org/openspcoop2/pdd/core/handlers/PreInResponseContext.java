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
package org.openspcoop2.pdd.core.handlers;

import org.slf4j.Logger;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;

/**
 * PreInResponseContext
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PreInResponseContext extends PostOutRequestContext {

	public PreInResponseContext(Logger logger,IProtocolFactory<?> protocolFactory){
		super(logger,protocolFactory);
	}
	
	public PreInResponseContext(PostOutRequestContext postOutRequestContext){
		super(postOutRequestContext);
		
		this.setResponseHeaders(postOutRequestContext.getResponseHeaders());
		this.setCodiceTrasporto(postOutRequestContext.getCodiceTrasporto());
		
	}
	
	/** NotifierInputStreamParameter */
	private NotifierInputStreamParams notifierInputStreamParams;
	
	public NotifierInputStreamParams getNotifierInputStreamParams() {
		return this.notifierInputStreamParams;
	}

	public void setNotifierInputStreamParams(
			NotifierInputStreamParams notifierInputStreamParams) {
		this.notifierInputStreamParams = notifierInputStreamParams;
	}
}
