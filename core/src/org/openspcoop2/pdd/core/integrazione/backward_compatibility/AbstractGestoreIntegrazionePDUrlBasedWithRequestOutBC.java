/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.integrazione.backward_compatibility;

import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazioneException;
import org.openspcoop2.pdd.core.integrazione.OutRequestPDMessage;
import org.openspcoop2.protocol.sdk.constants.TipoIntegrazione;




/**
 * Classe utilizzata per la ricezione di informazioni di integrazione 
 * dai servizi applicativi verso la porta di dominio.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AbstractGestoreIntegrazionePDUrlBasedWithRequestOutBC extends AbstractGestoreIntegrazionePDUrlBasedBC{
	
	public AbstractGestoreIntegrazionePDUrlBasedWithRequestOutBC(boolean openspcoop2) {
		super(openspcoop2);
	}

	@Override
	public void setOutRequestHeader(HeaderIntegrazione integrazione,
			OutRequestPDMessage outRequestPDMessage) throws HeaderIntegrazioneException{
	
		try{
			this.utilitiesBC.setUrlProperties(integrazione, outRequestPDMessage.getProprietaUrlBased(),
					this.getProtocolFactory().createProtocolManager().buildIntegrationProperties(outRequestPDMessage.getBustaRichiesta(), true, TipoIntegrazione.URL));
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDUrlBased, "+e.getMessage(),e);
		}
		
	}
	
}