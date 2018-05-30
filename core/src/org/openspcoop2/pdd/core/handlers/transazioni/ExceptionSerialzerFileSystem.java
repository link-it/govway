package org.openspcoop2.pdd.core.handlers.transazioni;

import java.io.ByteArrayOutputStream;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.diagnostica.MessaggioDiagnostico;
import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.pdd.core.FileSystemSerializer;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.dump.Messaggio;
import org.openspcoop2.utils.beans.WriteToSerializerType;


public class ExceptionSerialzerFileSystem {

	private Logger logger;
	public ExceptionSerialzerFileSystem(Logger log){
		this.logger = log;
	}
	
	public void registrazioneFileSystem(Transazione transazione,String idTransazione){
		// Registro transazioni non registrate su database su file system
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			transazione.writeTo(bout, WriteToSerializerType.XML_JAXB);
	    	bout.flush();
	    	bout.close();
			FileSystemSerializer.getInstance().registraTransazione(bout.toByteArray(), transazione.getDataIngressoRichiesta());
		}catch(Exception eSerializer){
			this.logger.error("Errore durante la registrazione su file system della transazione ["+idTransazione+"]: "+eSerializer.getMessage(),eSerializer);
		}
	}
	
	public void registrazioneFileSystemDiagnosticiTracceDumpEmessiPdD(Transaction transaction,String idTransazione, Transazione transazioneDTO){
		if(transaction.getTracciaRichiesta()!=null){
			// Registro tracce non registrate su database su file system
			try{
				transaction.getTracciaRichiesta().addPropertyInBusta(Costanti.ID_TRANSAZIONE, idTransazione);
				org.openspcoop2.core.tracciamento.Traccia tr = transaction.getTracciaRichiesta().getTraccia();
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				tr.writeTo(bout, WriteToSerializerType.XML_JAXB);
		    	bout.flush();
		    	bout.close();
				FileSystemSerializer.getInstance().registraTraccia(bout.toByteArray(), tr.getOraRegistrazione());
				if(transazioneDTO!=null)
					transazioneDTO.setTracciaRichiesta(null);
			}catch(Exception eSerializer){
				this.logger.error("Errore durante la registrazione su file system della traccia di richiesta [idTransazione: "+idTransazione+"]: "+eSerializer.getMessage(),eSerializer);
			}
		}
		if(transaction.getTracciaRisposta()!=null){
			// Registro tracce non registrate su database su file system
			try{
				transaction.getTracciaRisposta().addPropertyInBusta(Costanti.ID_TRANSAZIONE, idTransazione);
				org.openspcoop2.core.tracciamento.Traccia tr = transaction.getTracciaRisposta().getTraccia();
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				tr.writeTo(bout, WriteToSerializerType.XML_JAXB);
		    	bout.flush();
		    	bout.close();
				FileSystemSerializer.getInstance().registraTraccia(bout.toByteArray(), tr.getOraRegistrazione());
				if(transazioneDTO!=null)
					transazioneDTO.setTracciaRisposta(null);
			}catch(Exception eSerializer){
				this.logger.error("Errore durante la registrazione su file system della traccia di risposta [idTransazione: "+idTransazione+"]: "+eSerializer.getMessage(),eSerializer);
			}
		}
		if(transaction.getMsgDiagnostici()!=null && transaction.getMsgDiagnostici().size()>0){
			boolean error = false;
			for (MsgDiagnostico msgDiag : transaction.getMsgDiagnostici()) {
				try{
					MessaggioDiagnostico msgDiagOp2 = msgDiag.getMessaggioDiagnostico();
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					msgDiagOp2.writeTo(bout, WriteToSerializerType.XML_JAXB);
			    	bout.flush();
			    	bout.close();
					FileSystemSerializer.getInstance().registraDiagnostico(bout.toByteArray(), msgDiagOp2.getOraRegistrazione());
				}catch(Exception eSerializer){
					error = true;
					this.logger.error("Errore durante la registrazione su file system del messaggio diagnostico [idTransazione: "+idTransazione+"]: "+eSerializer.getMessage(),eSerializer);
				}
			}
			if(!error && transazioneDTO!=null){
				transazioneDTO.setDiagnostici(null);
				transazioneDTO.setDiagnosticiList1(null);
				transazioneDTO.setDiagnosticiList2(null);
			}
		}
		if(transaction.getMessaggi()!=null && transaction.getMessaggi().size()>0){
			boolean error = false;
			for (Messaggio messaggio : transaction.getMessaggi()) {
				try{
					DumpMessaggio messaggioOp2 = messaggio.toDumpMessaggio();
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					messaggioOp2.writeTo(bout, WriteToSerializerType.XML_JAXB);
			    	bout.flush();
			    	bout.close();
					FileSystemSerializer.getInstance().registraDump(bout.toByteArray(), messaggioOp2.getDumpTimestamp());
				}catch(Exception eSerializer){
					error = true;
					this.logger.error("Errore durante la registrazione su file system del messaggio [idTransazione: "+idTransazione+"]: "+eSerializer.getMessage(),eSerializer);
				}
			}
			if(!error && transazioneDTO!=null){
				transazioneDTO.getDumpMessaggioList().clear();
			}
		}
	}
	
}
