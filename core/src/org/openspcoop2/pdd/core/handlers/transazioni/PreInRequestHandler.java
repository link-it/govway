/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.core.handlers.transazioni;

import java.util.List;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.PreInRequestContext;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.core.transazioni.TransactionDeletedException;
import org.openspcoop2.pdd.core.transazioni.TransactionNotExistsException;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.messages.ConnectorInMessage;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**     
 * PreInRequestHandler
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PreInRequestHandler extends FirstPositionHandler implements org.openspcoop2.pdd.core.handlers.PreInRequestHandler{

	// Viene realizzato come FirstPositionHandler per filtrare subito le violazioni di max threads
	
	@Override
	public void invoke(PreInRequestContext context) throws HandlerException {
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		if(op2Properties.isTransazioniEnabled()==false) {
			return;
		}
		
		String idTransazione = (String) context.getPddContext().getObject(Costanti.ID_TRANSAZIONE);
		
		// Get Transazione
		Transaction tr =  null;
		try{
			tr = TransactionContext.getTransaction(idTransazione);
		}catch(TransactionNotExistsException e){
			throw new HandlerException(e);
			// Non dovrebbe avvenire in questo handler
		}
		
		if(op2Properties.isControlloTrafficoEnabled()){
			tr.getTempiElaborazione().startControlloTraffico_maxRequests();
			try {
				PreInRequestHandler_GestioneControlloTraffico preInRequestHandler_gestioneControlloTraffico = new PreInRequestHandler_GestioneControlloTraffico();
				preInRequestHandler_gestioneControlloTraffico.process(context);
			}finally {
				tr.getTempiElaborazione().endControlloTraffico_maxRequests();
			}
		}
		
		/* ---- Analisi Remote IP ----- */
		ConnectorInMessage req = null;
		try{
			req = (ConnectorInMessage) context.getTransportContext().get(PreInRequestContext.SERVLET_REQUEST);
		}catch(Throwable e){
			context.getLogCore().error("Errore durante il recupero delle informazioni servlet: "+e.getMessage(),e);
		}
		if(req!=null){
			readClientAddress(context.getLogCore(), req, context.getPddContext());
		}
						
		/* ---- Analisi RequestInfo ----- */
		RequestInfo requestInfo = null;
		try{
			if(context.getRequestInfo()!=null) {
				requestInfo = context.getRequestInfo();
			}
			else {
				requestInfo = (RequestInfo) context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
			}
			
			setInfoInvocation(tr, requestInfo, req);
			
		}
		catch(TransactionDeletedException e){
			throw new HandlerException(e);
			// Non dovrebbe avvenire in questo handler
		}
		catch(Throwable e){
			context.getLogCore().error("Errore durante il recupero delle informazioni dall'oggetto request info: "+e.getMessage(),e);
		}
	}

	public static void readClientAddress(Logger log,ConnectorInMessage req, PdDContext pddContext) {
		if(req!=null){
			try{
				String remoteAddr = req.getRemoteAddress();
				if(remoteAddr!=null){
					pddContext.addObject(Costanti.CLIENT_IP_REMOTE_ADDRESS,remoteAddr);
				}
			}catch(Throwable e){
				log.error("Errore durante l'identificazione dell'indirizzo ip del chiamante (via socket): "+e.getMessage(),e);
			}
			try{
				String transportAddr = getIPClientAddressFromHeader(HttpUtilities.getClientAddressHeaders(), req);
				if(transportAddr!=null){
					pddContext.addObject(Costanti.CLIENT_IP_TRANSPORT_ADDRESS,transportAddr);
				}
			}catch(Throwable e){
				log.error("Errore durante l'identificazione dell'indirizzo ip del chiamante (via trasporto): "+e.getMessage(),e);
			}
		}
	}
	private static String getIPClientAddressFromHeader(List<String> headers, ConnectorInMessage req) throws ConnectorException{
		if(headers.size()>0){
			for (String header : headers) {
				String transportAddr = TransportUtils.getFirstValue(req.getHeaderValues(header)); // gestisce nell'implementazione il case insensitive
				if(transportAddr!=null){
					return transportAddr;
				}
			}
		}
		return null;
	}
	public static void setInfoInvocation(Transaction tr, RequestInfo requestInfo, ConnectorInMessage req) throws Exception {
		
		tr.setRequestInfo(requestInfo);
		
		if(req.getCredential()!=null) {
			Credenziali credenziali = new Credenziali(req.getCredential());
			String credenzialiFornite = "";
			if(credenziali!=null){
				credenzialiFornite = credenziali.toString(!Credenziali.SHOW_BASIC_PASSWORD,
						Credenziali.SHOW_ISSUER,
						!Credenziali.SHOW_DIGEST_CLIENT_CERT,
						Credenziali.SHOW_SERIAL_NUMBER_CLIENT_CERT,
						"","","\n"); // riporto anche l'issuer ed il serial number del cert e formatto differentemente
			}
			tr.setCredenziali(credenzialiFornite);
		}
		
		if(req.getURLProtocolContext()!=null){
			String urlInvocazione = req.getURLProtocolContext().getUrlInvocazione_formBased();
			if(req.getURLProtocolContext().getFunction()!=null){
				urlInvocazione = "["+req.getURLProtocolContext().getFunction()+"] "+urlInvocazione;
			}
			//System.out.println("SET URL INVOCAZIONE ["+urlInvocazione+"]");
			tr.setUrlInvocazione(urlInvocazione);
		}
	}
}
