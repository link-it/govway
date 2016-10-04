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
package org.openspcoop2.pdd.core.handlers;

import org.slf4j.Logger;
import org.openspcoop2.protocol.sdk.IProtocolFactory;

/**
 * PostOutRequestContext
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PostOutRequestContext extends OutRequestContext {

	public PostOutRequestContext(Logger logger, IProtocolFactory protocolFactory){
		super(logger,protocolFactory);
	}
	
	public PostOutRequestContext(OutRequestContext outRequestContext){
		super(outRequestContext.getLogCore(),outRequestContext.getProtocolFactory());
		
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
	private java.util.Properties propertiesTrasportoRisposta;
	
	private int codiceTrasporto;

	public java.util.Properties getPropertiesTrasportoRisposta() {
		return this.propertiesTrasportoRisposta;
	}

	public void setPropertiesTrasportoRisposta(
			java.util.Properties propertiesTrasportoRisposta) {
		this.propertiesTrasportoRisposta = propertiesTrasportoRisposta;
	}

	public int getCodiceTrasporto() {
		return this.codiceTrasporto;
	}

	public void setCodiceTrasporto(int returnCode) {
		this.codiceTrasporto = returnCode;
	}
}
