/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import java.util.List;
import java.util.Map;

import org.openspcoop2.protocol.sdk.IProtocolFactory;

/**
 * PostOutRequestContext
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PostOutRequestContext extends OutRequestContext {

	public PostOutRequestContext(Logger logger, IProtocolFactory<?> protocolFactory){
		super(logger,protocolFactory,null);
	}
	
	public PostOutRequestContext(OutRequestContext outRequestContext){
		super(outRequestContext.getLogCore(),outRequestContext.getProtocolFactory(),outRequestContext.getStato());
		
		this.setConnettore(outRequestContext.getConnettore());
		this.setProtocollo(outRequestContext.getProtocollo());
		this.setIntegrazione(outRequestContext.getIntegrazione());
		
		this.setDataElaborazioneMessaggio(outRequestContext.getDataElaborazioneMessaggio());
		this.setMessaggio(outRequestContext.getMessaggio());
		this.setTipoPorta(outRequestContext.getTipoPorta());
		this.setIdModulo(outRequestContext.getIdModulo());
		this.setPddContext(outRequestContext.getPddContext());

	}
	
	/** Proprieta' di trasporto della risposta */
	private Map<String, List<String>> responseHeaders;
	
	private int codiceTrasporto;

	public Map<String, List<String>> getResponseHeaders() {
		return this.responseHeaders;
	}

	public void setResponseHeaders(
			Map<String, List<String>> propertiesRispostaTrasporto) {
		this.responseHeaders = propertiesRispostaTrasporto;
	}

	public int getCodiceTrasporto() {
		return this.codiceTrasporto;
	}

	public void setCodiceTrasporto(int returnCode) {
		this.codiceTrasporto = returnCode;
	}
}
