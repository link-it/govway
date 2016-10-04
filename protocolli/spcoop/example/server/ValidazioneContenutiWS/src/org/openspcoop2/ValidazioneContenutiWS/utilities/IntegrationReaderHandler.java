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



package org.openspcoop2.ValidazioneContenutiWS.utilities;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.transport.http.HTTPConstants;


/**
*
* @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/

public class IntegrationReaderHandler extends BasicHandler
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

    	javax.servlet.http.HttpServletRequest req = 
			(javax.servlet.http.HttpServletRequest) msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);

    	java.util.Properties headerTrasporto = 
			new java.util.Properties();	    
		java.util.Enumeration<?> enTrasporto = req.getHeaderNames();
		String tipoMittente = null;
		String mittente = null;
		String tipoDestinatario = null;
		String destinatario = null;
		String tipoServizio = null;
		String servizio = null;
		String azione = null;
		String idEGov = null;
		while(enTrasporto.hasMoreElements()){
			String nomeProperty = (String)enTrasporto.nextElement();
			headerTrasporto.setProperty(nomeProperty,req.getHeader(nomeProperty));
			//System.out.println("Proprieta' Trasporto: nome["+nomeProperty+"] valore["+req.getHeader(nomeProperty)+"]");
			
			if(nomeProperty!=null){
				// Egov
				if(nomeProperty.equalsIgnoreCase("X-OpenSPCoop2-TipoMittente"))
					tipoMittente = req.getHeader(nomeProperty);
				else if(nomeProperty.equalsIgnoreCase("X-OpenSPCoop2-Mittente"))
					mittente = req.getHeader(nomeProperty); 
				else if(nomeProperty.equalsIgnoreCase("X-OpenSPCoop2-TipoDestinatario"))
					tipoDestinatario = req.getHeader(nomeProperty);
				else if(nomeProperty.equalsIgnoreCase("X-OpenSPCoop2-Destinatario"))
					destinatario = req.getHeader(nomeProperty); 
				else if(nomeProperty.equalsIgnoreCase("X-OpenSPCoop2-TipoServizio"))
					tipoServizio = req.getHeader(nomeProperty);
				else if(nomeProperty.equalsIgnoreCase("X-OpenSPCoop2-Servizio"))
					servizio = req.getHeader(nomeProperty); 
				else if(nomeProperty.equalsIgnoreCase("X-OpenSPCoop2-Azione"))
					azione = req.getHeader(nomeProperty); 
				else if(nomeProperty.equalsIgnoreCase("X-OpenSPCoop2-IdMessaggio"))
					idEGov = req.getHeader(nomeProperty); 
			}
		}
		IntegrationInfo infoIntegrazione = new IntegrationInfo();
		if(tipoMittente!=null)
			infoIntegrazione.setTipoMittente(tipoMittente);
		if( mittente!=null)
			infoIntegrazione.setMittente(mittente);
		if(tipoDestinatario!=null)
			infoIntegrazione.setTipoDestinatario(tipoDestinatario);
		if(destinatario!=null)
			infoIntegrazione.setDestinatario(destinatario);
		if(tipoServizio!=null)
			infoIntegrazione.setTipoServizio(tipoServizio);
		if(servizio!=null)
			infoIntegrazione.setServizio(servizio);
		if(azione!=null)
			infoIntegrazione.setAzione(azione);
		if(idEGov!=null)
			infoIntegrazione.setIdEGov(idEGov);
		
		IntegrationInfoRepository.put(infoIntegrazione);
		
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
