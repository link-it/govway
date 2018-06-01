package org.openspcoop2.pdd.core.handlers.transazioni;

import java.util.List;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
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
import org.openspcoop2.utils.transport.http.HttpUtilities;

public class PreInRequestHandler extends FirstPositionHandler implements org.openspcoop2.pdd.core.handlers.PreInRequestHandler{

	// Viene realizzato come FirstPositionHandler per filtrare subito le violazioni di max threads
	
	@Override
	public void invoke(PreInRequestContext context) throws HandlerException {
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		if(op2Properties.isTransazioniEnabled()==false) {
			return;
		}
		
		String idTransazione = (String) context.getPddContext().getObject(Costanti.ID_TRANSAZIONE);
		
		if(op2Properties.isControlloTrafficoEnabled()){
			PreInRequestHandler_GestioneControlloTraffico preInRequestHandler_gestioneControlloTraffico = new PreInRequestHandler_GestioneControlloTraffico();
			preInRequestHandler_gestioneControlloTraffico.process(context);
		}
		
		/* ---- Analisi Remote IP ----- */
		ConnectorInMessage req = null;
		try{
			req = (ConnectorInMessage) context.getTransportContext().get(PreInRequestContext.SERVLET_REQUEST);
		}catch(Throwable e){
			context.getLogCore().error("Errore durante il recupero delle informazioni servlet: "+e.getMessage(),e);
		}
		if(req!=null){
			try{
				String remoteAddr = req.getRemoteAddress();
				if(remoteAddr!=null){
					context.getPddContext().addObject(Costanti.CLIENT_IP_REMOTE_ADDRESS,remoteAddr);
				}
			}catch(Throwable e){
				context.getLogCore().error("Errore durante la comprensione dell'indirizzo ip del chiamante (via socket): "+e.getMessage(),e);
			}
			try{
				String transportAddr = getIPClientAddressFromHeader(HttpUtilities.getClientAddressHeaders(), req);
				if(transportAddr!=null){
					context.getPddContext().addObject(Costanti.CLIENT_IP_TRANSPORT_ADDRESS,transportAddr);
				}
			}catch(Throwable e){
				context.getLogCore().error("Errore durante la comprensione dell'indirizzo ip del chiamante (via trasporto): "+e.getMessage(),e);
			}
		}
				
		// Creo Transazione
		Transaction tr =  null;
		try{
			tr = TransactionContext.getTransaction(idTransazione);
		}catch(TransactionNotExistsException e){
			throw new HandlerException(e);
			// Non dovrebbe avvenire in questo handler
		}
		
		/* ---- Analisi RequestInfo ----- */
		RequestInfo requestInfo = null;
		try{
			requestInfo = (RequestInfo) context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
			tr.setRequestInfo(requestInfo);
			
			if(req.getCredential()!=null) {
				Credenziali cr = new Credenziali(req.getCredential());
				tr.setCredenziali(cr.toString());
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
		catch(TransactionDeletedException e){
			throw new HandlerException(e);
			// Non dovrebbe avvenire in questo handler
		}
		catch(Throwable e){
			context.getLogCore().error("Errore durante il recupero delle informazioni dall'oggetto request info: "+e.getMessage(),e);
		}
	}

	private String getIPClientAddressFromHeader(List<String> headers, ConnectorInMessage req) throws ConnectorException{
		if(headers.size()>0){
			for (String header : headers) {
				String transportAddr = req.getHeader(header);
				if(transportAddr!=null){
					return transportAddr;
				}
				transportAddr = req.getHeader(header.toLowerCase());
				if(transportAddr!=null){
					return transportAddr;
				}
				transportAddr = req.getHeader(header.toUpperCase());
				if(transportAddr!=null){
					return transportAddr;
				}
			}
		}
		return null;
	}
}
