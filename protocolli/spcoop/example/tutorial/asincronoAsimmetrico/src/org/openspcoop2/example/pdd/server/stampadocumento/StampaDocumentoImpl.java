/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.example.pdd.server.stampadocumento;

import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * StampaDocumentoImpl
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.jws.WebService(
                      serviceName = "StampaDocumentoService",
                      portName = "StampaDocumentoInterfaceEndpoint",
                      targetNamespace = "http://openspcoop2.org/example/pdd/server/StampaDocumento",
                      wsdlLocation = "file:configurazionePdD/wsdl/implementativoErogatore.wsdl",
                      endpointInterface = "org.openspcoop2.example.pdd.server.stampadocumento.StampaDocumento")
                      
public class StampaDocumentoImpl implements StampaDocumento {

    private XMLGregorianCalendar datacompletamento = null;
    /* (non-Javadoc)
     * @see org.openspcoop2.example.pdd.server.stampadocumento.StampaDocumento#stato(long  statoDocumentoRequestPart )*
     */
    @Override
	public java.lang.String stato(long statoDocumentoRequestPart) { 
        
        System.out.println("Richiesto stato documento n " + statoDocumentoRequestPart);
        try {
        	GregorianCalendar calendar = new GregorianCalendar();
            Date trialTime = new Date();
            calendar.setTime(trialTime);
            
            XMLGregorianCalendar completamento = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
            java.lang.String _return = "Incompleto";
        	if(completamento.compare(this.datacompletamento) == DatatypeConstants.GREATER) _return = "Completato";
           
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.openspcoop2.example.pdd.server.stampadocumento.StampaDocumento#stampa(org.openspcoop2.example.pdd.server.stampadocumento.StampaDocumento_Type  stampaDocumentoRequestPart )*
     */
    @Override
	public org.openspcoop2.example.pdd.server.stampadocumento.PresaConsegnaStampa stampa(StampaDocumento_Type stampaDocumentoRequestPart) { 
        System.out.println("Richiesta stampa di " + stampaDocumentoRequestPart.getCodiceDocumento() + " per cf = " + stampaDocumentoRequestPart.getCF());
        try {
            org.openspcoop2.example.pdd.server.stampadocumento.PresaConsegnaStampa _return = new PresaConsegnaStampa();
            _return.setIdStampa(new java.util.Random().nextLong());
            GregorianCalendar calendar = new GregorianCalendar();
            Date trialTime = new Date();
            calendar.setTime(trialTime);
            
            XMLGregorianCalendar completamento = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
            completamento.add(DatatypeFactory.newInstance().newDuration(4000));
            this.datacompletamento = completamento;
            _return.setStimaCompletamento(completamento);
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

}
