/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.core.connettori;

import java.io.IOException;

import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.config.CostantiProprieta;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.MultipartMissingTypeBehavior;
import org.openspcoop2.utils.transport.http.MultipartRelatedHeuristics;

/**
 * Compensa un Content-Type 'multipart/related' privo del parametro 'type' (RFC 2387 §3.1)
 * sulla risposta ricevuta dal backend, applicando la strategia configurata
 * (globale o per-API). In caso di successo popola direttamente
 * {@link ConnettoreBaseWithResponse#messageTypeResponse} bypassando la classificazione
 * standard del Content-Type.
 *
 * Per la strategia INFER_FROM_BODY viene anche sostituito
 * {@link ConnettoreBaseWithResponse#isResponse} con uno stream ricostruito
 * (peek + SequenceInputStream).
 *
 * Inoltre viene valorizzato {@link ConnettoreBaseWithResponse#tipoRispostaForParser} con il
 * Content-Type ricostruito (originale + parametro 'type' inferito), che verrà utilizzato dal
 * solo parser SAAJ: necessario perché SAAJ 1.2 (più strict del 1.1) rifiuta un multipart/related
 * senza 'type' con "Unable to internalize message" (bug noto OP-678 / OP-909).
 * {@link ConnettoreBaseWithResponse#tipoRisposta} e {@link ConnettoreBase#propertiesTrasportoRisposta}
 * restano invariati, così dump binario, FileTrace e dump applicativo continuano a riflettere il
 * Content-Type originale del backend.
 *
 * @author Poli Andrea (apoli@link.it)
 */
class ConnettoreMultipartResponseCompensator {

	private final ConnettoreBaseWithResponse c;

	ConnettoreMultipartResponseCompensator(ConnettoreBaseWithResponse c) {
		this.c = c;
	}

	void apply() {
		try {
			if(!isCompensable(this.c.tipoRisposta)) {
				return;
			}
			MultipartMissingTypeBehavior behavior = resolveBehavior();
			if(behavior==null || MultipartMissingTypeBehavior.NONE.equals(behavior)) {
				return;
			}
			String inferredType = computeInferredType(behavior);
			MessageType inferredMessageType = mapToSoapMessageType(inferredType);
			if(inferredMessageType==null) {
				return;
			}
			this.c.messageTypeResponse = inferredMessageType;
			patchContentTypeForParser(inferredType);
			emitDiagnostic(behavior, inferredType);
		} catch(Exception e) {
			if(this.c.logger!=null) {
				this.c.logger.warn("Compensazione 'multipart/related missingType' sulla risposta non applicabile: "+e.getMessage(),e);
			}
		}
	}

	private static boolean isCompensable(String contentType) throws UtilsException {
		if(contentType==null || "".equals(contentType.trim())) {
			return false;
		}
		if(!ContentTypeUtilities.isMultipartRelated(contentType)) {
			return false;
		}
		// se 'type' è già presente non c'è nulla da compensare
		return ContentTypeUtilities.readMultipartTypeFromContentType(contentType)==null;
	}

	private String computeInferredType(MultipartMissingTypeBehavior behavior) throws UtilsException, IOException {
		switch(behavior) {
		case INFER_FROM_REQUEST:
			return inferTypeFromRequestMessage();
		case INFER_FROM_BODY:
			return inferTypeFromResponseBody();
		case FORCE_SOAP11:
			return HttpConstants.CONTENT_TYPE_SOAP_1_1;
		case FORCE_SOAP12:
			return HttpConstants.CONTENT_TYPE_SOAP_1_2;
		default:
			return null;
		}
	}

	private String inferTypeFromRequestMessage() {
		if(this.c.requestMsg==null || this.c.requestMsg.getMessageType()==null) {
			return null;
		}
		return MultipartRelatedHeuristics.inferTypeFromSoapVersion(this.c.requestMsg.getMessageType().name());
	}

	private String inferTypeFromResponseBody() throws UtilsException, IOException {
		if(this.c.isResponse==null) {
			return null;
		}
		String boundary = ContentTypeUtilities.readMultipartBoundaryFromContentType(this.c.tipoRisposta);
		if(boundary==null) {
			return null;
		}
		int peekBytes = resolvePeekBytes();
		MultipartRelatedHeuristics.PeekResult peek = MultipartRelatedHeuristics.peekFirstPartContentType(this.c.isResponse, boundary, peekBytes);
		this.c.isResponse = peek.getReconstructed();
		return peek.getInferredType();
	}

	/**
	 * Valorizza {@link ConnettoreBaseWithResponse#tipoRispostaForParser} con il Content-Type ricostruito
	 * (originale + parametro 'type' inferito), che verrà utilizzato dal solo parser SAAJ (necessario
	 * con SAAJ 1.2, più strict di SAAJ 1.1). {@link ConnettoreBaseWithResponse#tipoRisposta} e
	 * {@link ConnettoreBase#propertiesTrasportoRisposta} restano invariati, preservando la trasparenza
	 * di dump binario, FileTrace e dump applicativo verso il valore originale del backend.
	 */
	private void patchContentTypeForParser(String inferredType) {
		String original = this.c.tipoRisposta;
		if(original==null) {
			return;
		}
		this.c.tipoRispostaForParser = original + "; "
				+ HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_TYPE + "=\"" + inferredType + "\"";
	}

	private static MessageType mapToSoapMessageType(String inferredType) {
		if(inferredType==null || "".equals(inferredType.trim())) {
			return null;
		}
		String inferredTrim = inferredType.trim();
		if(HttpConstants.CONTENT_TYPE_SOAP_1_1.equalsIgnoreCase(inferredTrim)) {
			return MessageType.SOAP_11;
		}
		if(HttpConstants.CONTENT_TYPE_SOAP_1_2.equalsIgnoreCase(inferredTrim)) {
			return MessageType.SOAP_12;
		}
		return null;
	}

	private void emitDiagnostic(MultipartMissingTypeBehavior behavior, String inferredType) {
		if(this.c.msgDiagnostico!=null) {
			tryEmitDiagnostic(behavior, inferredType);
			return;
		}
		if(this.c.logger!=null) {
			this.c.logger.warn("Content-Type '"+this.c.tipoRisposta+"' della risposta privo del parametro 'type' (RFC 2387 §3.1): applicata compensazione '"+behavior.getValue()+"', type dedotto: '"+inferredType+"'");
		}
	}

	private void tryEmitDiagnostic(MultipartMissingTypeBehavior behavior, String inferredType) {
		try {
			this.c.msgDiagnostico.addKeyword(CostantiPdD.KEY_HTTP_HEADER, this.c.tipoRisposta);
			this.c.msgDiagnostico.addKeyword(CostantiPdD.KEY_STRATEGIA, behavior.getValue());
			this.c.msgDiagnostico.addKeyword(CostantiPdD.KEY_TYPE_INFERITO, inferredType);
			this.c.msgDiagnostico.logPersonalizzato("contentType.multipart.related.missingType.compensated");
		} catch(Exception diagEx) {
			if(this.c.logger!=null) {
				this.c.logger.warn("Emissione diagnostico 'contentType.multipart.related.missingType.compensated' fallita: "+diagEx.getMessage(),diagEx);
			}
		}
	}

	private MultipartMissingTypeBehavior resolveBehavior() {
		MultipartMissingTypeBehavior defaultValue;
		if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.c.idModulo)) {
			defaultValue = this.c.openspcoopProperties.getMultipartRelatedMissingTypeBehaviorConsegnaContenutiApplicativi();
		} else {
			defaultValue = this.c.openspcoopProperties.getMultipartRelatedMissingTypeBehaviorInoltroBuste();
		}
		return CostantiProprieta.getConnettoriMultipartRelatedMissingTypeResponseBehavior(this.c.proprietaPorta, defaultValue);
	}

	private int resolvePeekBytes() {
		int defaultValue;
		if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.c.idModulo)) {
			defaultValue = this.c.openspcoopProperties.getMultipartRelatedMissingTypePeekBytesConsegnaContenutiApplicativi();
		} else {
			defaultValue = this.c.openspcoopProperties.getMultipartRelatedMissingTypePeekBytesInoltroBuste();
		}
		return CostantiProprieta.getConnettoriMultipartRelatedMissingTypeResponsePeekBytes(this.c.proprietaPorta, defaultValue);
	}
}
