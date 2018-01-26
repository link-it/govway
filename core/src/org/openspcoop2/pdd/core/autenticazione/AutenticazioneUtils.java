/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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



package org.openspcoop2.pdd.core.autenticazione;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.utils.transport.http.HttpConstants;


/**
 * AutenticazioneUtils
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author: apoli $
 * @version $Rev: 12942 $, $Date: 2017-05-02 13:01:02 +0200 (Tue, 02 May 2017) $
 */
public class AutenticazioneUtils {

	public static void cleanHeaderAuthorization(OpenSPCoop2Message message) throws AutenticazioneException {
		try {
			if(message.getTransportRequestContext()!=null) {
				if(message.getTransportRequestContext().getParametersTrasporto()!=null) {
					message.getTransportRequestContext().getParametersTrasporto().remove(HttpConstants.AUTHORIZATION);
					message.getTransportRequestContext().getParametersTrasporto().remove(HttpConstants.AUTHORIZATION.toLowerCase());
					message.getTransportRequestContext().getParametersTrasporto().remove(HttpConstants.AUTHORIZATION.toUpperCase());
	    		}
			}
		}catch(Throwable t) {
			throw new AutenticazioneException("Clean Header Authorization failed: "+t.getMessage(),t);
		}
    }
	
}
