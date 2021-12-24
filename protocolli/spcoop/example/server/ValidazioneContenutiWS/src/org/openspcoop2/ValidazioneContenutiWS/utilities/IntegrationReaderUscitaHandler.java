/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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



package org.openspcoop2.ValidazioneContenutiWS.utilities;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;


/**
*
* @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/

public class IntegrationReaderUscitaHandler extends BasicHandler
{
	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
    /**
     * Metodo contenente la definizione di un handler per il servizio axis   'RicezioneContenutiApplicativi' e 'Gop'.
     *
     * @param msgContext contiene il contesto di Axis.
     * 
     */

    @Override
	public void invoke(MessageContext msgContext) throws AxisFault
    {

    	// Elimina eventuale id se rimasto!
    	IntegrationInfoRepository.clear();
		
    }



    /**
     * Metodo necessario per la definizione dell'handler.
     *
     * @param msgContext contiene il contesto di Axis.
     * 
     */
    @Override public void generateWSDL(MessageContext msgContext) throws AxisFault {
        invoke(msgContext);
    }
}
