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

package org.openspcoop2.protocol.modipa.example.soap.blocking;

import jakarta.xml.ws.BindingType;

import org.openspcoop2.protocol.modipa.example.soap.blocking.stub.ErrorMessageException;
import org.openspcoop2.protocol.modipa.example.soap.blocking.stub.ErrorMessageFault;
import org.openspcoop2.protocol.modipa.example.soap.blocking.stub.MResponseType;
import org.openspcoop2.protocol.modipa.example.soap.blocking.stub.MType;
import org.openspcoop2.protocol.modipa.example.soap.blocking.stub.SOAPBlockingImpl;

/**
 * ServerImpl
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@BindingType(value = jakarta.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class ServerImpl implements SOAPBlockingImpl {

	@Override
	public MResponseType mRequest(MType body) throws ErrorMessageException {

		if(body==null) {
    		String descrizione = "Dati richiesta non forniti";
    		throwFault(400, descrizione);
    	}
		
		Integer idResource = body.getOId();
		
		System.out.println("Ricevuta richiesta per risorsa '"+idResource+"'");
    	
    	if(idResource==null || idResource<=0) {
    		String descrizione = "IdRisorsa non fornito";
    		throwFault(400, descrizione);
    	}
    	
    	System.out.println("Parametri richiesta:");
    	System.out.println("\tA1-List: "+(body.getA()!=null && body.getA().getA1S()!=null ? body.getA().getA1S().getA1() : null));
    	System.out.println("\tA2: "+(body.getA()!=null ? body.getA().getA2() : null));
    	System.out.println("\tB: "+body.getB());
    	    	    	
    	MResponseType response = new MResponseType();
    	response.setC("Risultato C");
    	return response;
    	
    }
    
	public void throwFault(int code, String descrizione) throws ErrorMessageException {
		
		ErrorMessageFault fault = new ErrorMessageFault();
		fault.setCustomFaultCode("SERVER-ERROR-"+code);
		throw new ErrorMessageException(descrizione, fault);

	}

}
