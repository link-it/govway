package org.openspcoop2.pdd.services.error;

import java.util.List;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.IntegrationError;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.ParseException;
import org.openspcoop2.pdd.services.RequestInfo;
import org.openspcoop2.protocol.engine.builder.ErroreApplicativoBuilder;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.slf4j.Logger;

public class RicezioneContenutiApplicativiInternalErrorGenerator extends AbstractErrorGenerator {

	private ProprietaErroreApplicativo proprietaErroreAppl = null;

	public RicezioneContenutiApplicativiInternalErrorGenerator(Logger log, String idModulo, RequestInfo requestInfo) throws ProtocolException{
		
		super(log, idModulo, requestInfo, TipoPdD.DELEGATA, true);
		
		this.proprietaErroreAppl = this.openspcoopProperties.getProprietaGestioneErrorePD(this.protocolFactory.createProtocolManager());
		this.proprietaErroreAppl.setDominio(this.identitaPdD.getCodicePorta());
		this.proprietaErroreAppl.setIdModulo(this.idModulo);
		
	}
	
	public ProprietaErroreApplicativo getProprietaErroreAppl() {
		return this.proprietaErroreAppl;
	}
	
	public void updateProprietaErroreApplicativo(ProprietaErroreApplicativo proprietaErroreApplicativo){
		this.proprietaErroreAppl = proprietaErroreApplicativo;
	}
		
	@Override
	public void updateDominio(IDSoggetto identitaPdD){
		super.updateDominio(identitaPdD);
		this.proprietaErroreAppl.setDominio(this.identitaPdD.getCodicePorta());
	}
	
	public ErroreApplicativoBuilder getErroreApplicativoBuilder(MessageType messageTypeError) throws ProtocolException{
		ErroreApplicativoBuilder erroreApplicativoBuilder = new ErroreApplicativoBuilder(this.log, this.protocolFactory, 
				this.identitaPdD, this.mittente, this.idServizio, 
				this.idModulo, 
				this.proprietaErroreAppl, messageTypeError, 
				this.tipoPdD, this.servizioApplicativo);
		return erroreApplicativoBuilder;
	}
	
	
	
	public OpenSPCoop2Message build(IntegrationError integrationError, Eccezione ecc, IDSoggetto idSoggettoProduceEccezione, ParseException parseException) {
		return build(integrationError, ecc, idSoggettoProduceEccezione, null, parseException);
	}
	public OpenSPCoop2Message build(IntegrationError integrationError, Eccezione ecc, IDSoggetto idSoggettoProduceEccezione, DettaglioEccezione dettaglioEccezione, ParseException parseException) {
		
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		try{
			ErroreApplicativoBuilder erroreApplicativoBuilder = getErroreApplicativoBuilder(msgTypeErrorResponse);
			OpenSPCoop2Message msg = erroreApplicativoBuilder.toMessage(ecc,idSoggettoProduceEccezione,dettaglioEccezione,parseException);		
			int httpReturnCode = this.getReturnCodeForError(integrationError);
			msg.setForcedResponseCode(httpReturnCode+"");	
			return msg;
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione integrazione",e);
			return OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(msgTypeErrorResponse, e);
		}
	}
	
	public OpenSPCoop2Message build(IntegrationError integrationError, ErroreIntegrazione erroreIntegrazione, Throwable eProcessamento, ParseException parseException) {
		
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		try{
			ErroreApplicativoBuilder erroreApplicativoBuilder = getErroreApplicativoBuilder(msgTypeErrorResponse);
			OpenSPCoop2Message msg = erroreApplicativoBuilder.toMessage(erroreIntegrazione,eProcessamento,parseException);		
			int httpReturnCode = this.getReturnCodeForError(integrationError);
			msg.setForcedResponseCode(httpReturnCode+"");	
			return msg;
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione integrazione",e);
			return OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(msgTypeErrorResponse, e);
		}
	}
	
	public byte[] buildAsByteArray(IntegrationError integrationError, ErroreIntegrazione erroreIntegrazione, List<Integer> returnCode) {
		
		MessageType msgTypeErrorResponse = this.getMessageTypeForErrorSafeMode(integrationError);
		try{
			ErroreApplicativoBuilder erroreApplicativoBuilder = getErroreApplicativoBuilder(msgTypeErrorResponse);
			byte[] bytes = erroreApplicativoBuilder.toByteArray(erroreIntegrazione);
			int httpReturnCode = this.getReturnCodeForError(integrationError);
			returnCode.add(httpReturnCode);
			return bytes;
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione integrazione",e);
			try{
				OpenSPCoop2Message msgError = OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(msgTypeErrorResponse, e);
				java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
				msgError.writeTo(bout,true);
				bout.flush();
				bout.close();
				return  bout.toByteArray();
			}catch(Exception eInternal){
				throw new RuntimeException(eInternal.getMessage(),eInternal); // non dovrebbe mai accadere
			}
		}
	}
	
}
