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
package org.openspcoop2.pdd.services.connector.messages;

import java.io.IOException;
import java.util.List;

import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.config.CostantiProprieta;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.error.AbstractErrorGenerator;
import org.openspcoop2.protocol.sdk.constants.IDService;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.MultipartMissingTypeBehavior;
import org.openspcoop2.utils.transport.http.MultipartRelatedHeuristics;

/**
 * Compensa un Content-Type 'multipart/related' privo del parametro 'type' (RFC 2387 §3.1)
 * sulla richiesta SOAP ricevuta su porta delegata o porta applicativa, applicando la
 * strategia configurata (globale o per-API). In caso di successo aggiorna direttamente
 * il MessageType (sia integration che protocol) in {@link RequestInfo}, riallinea il
 * connettore tramite {@link HttpServletConnectorInMessage#updateRequestInfo(RequestInfo)}
 * e aggiorna l'{@link AbstractErrorGenerator}.
 *
 * Per la strategia INFER_FROM_BODY viene anche sostituito {@link HttpServletConnectorInMessage#is}
 * con uno stream ricostruito (peek + SequenceInputStream).
 *
 * La strategia INFER_FROM_REQUEST non è applicabile lato richiesta e viene trattata
 * come NONE (nessuna compensazione: si delega al classifier standard).
 *
 * Nota: la compensazione ha senso solo per servizi SOAP; il chiamante è responsabile
 * di invocarla solo quando il service binding è SOAP.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpServletConnectorMultipartRequestCompensator {

	private final HttpServletConnectorInMessage c;
	private final List<Proprieta> proprietaPorta;
	private final AbstractErrorGenerator generatoreErrore;
	private final MsgDiagnostico msgDiagnostico;

	public HttpServletConnectorMultipartRequestCompensator(HttpServletConnectorInMessage c,
			List<Proprieta> proprietaPorta,
			AbstractErrorGenerator generatoreErrore,
			MsgDiagnostico msgDiagnostico) {
		this.c = c;
		this.proprietaPorta = proprietaPorta;
		this.generatoreErrore = generatoreErrore;
		this.msgDiagnostico = msgDiagnostico;
	}

	public void apply() {
		String contentType;
		try {
			contentType = this.c.getContentType();
			if(!isCompensable(contentType)) {
				return;
			}
		} catch(Exception e) {
			if(this.c.log!=null) {
				this.c.log.warn("Compensazione 'multipart/related missingType' sulla richiesta non applicabile: {}", e.getMessage(), e);
			}
			return;
		}
		MultipartMissingTypeBehavior behavior = resolveBehavior();
		if(!isBehaviorActionable(behavior)) {
			return;
		}
		try {
			String inferredType = computeInferredType(behavior, contentType);
			MessageType inferredMessageType = mapToSoapMessageType(inferredType);
			if(inferredMessageType==null) {
				return;
			}
			applyChanges(inferredMessageType, contentType, inferredType);
			emitDiagnostic(behavior, inferredType, contentType);
		} catch(Exception e) {
			if(this.c.log!=null) {
				this.c.log.warn("Compensazione 'multipart/related missingType' sulla richiesta non applicabile: {}", e.getMessage(), e);
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

	private static boolean isBehaviorActionable(MultipartMissingTypeBehavior behavior) {
		if(behavior==null) {
			return false;
		}
		return !MultipartMissingTypeBehavior.NONE.equals(behavior);
	}

	private String computeInferredType(MultipartMissingTypeBehavior behavior, String contentType) throws UtilsException, IOException  {
		switch(behavior) {
		case INFER_FROM_BODY:
			return inferTypeFromRequestBody(contentType);
		case FORCE_SOAP11:
			return HttpConstants.CONTENT_TYPE_SOAP_1_1;
		case FORCE_SOAP12:
			return HttpConstants.CONTENT_TYPE_SOAP_1_2;
		default:
			return null;
		}
	}

	private String inferTypeFromRequestBody(String contentType) throws UtilsException, IOException {
		if(this.c.is==null) {
			return null;
		}
		String boundary = ContentTypeUtilities.readMultipartBoundaryFromContentType(contentType);
		if(boundary==null) {
			return null;
		}
		int peekBytes = resolvePeekBytes();
		MultipartRelatedHeuristics.PeekResult peek = MultipartRelatedHeuristics.peekFirstPartContentType(this.c.is, boundary, peekBytes);
		this.c.is = peek.getReconstructed();
		return peek.getInferredType();
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

	private void applyChanges(MessageType inferredMessageType, String originalContentType, String inferredType) throws ConnectorException {
		RequestInfo ri = this.c.getRequestInfo();
		if(ri!=null) {
			ri.setIntegrationRequestMessageType(inferredMessageType);
			ri.setProtocolRequestMessageType(inferredMessageType);
			this.c.updateRequestInfo(ri);
		}
		if(this.generatoreErrore!=null) {
			this.generatoreErrore.updateRequestMessageType(inferredMessageType);
		}
		// Content-Type override usato dal solo parser (necessario per SAAJ 1.2, più strict del 1.1).
		// La Map degli header del protocolContext NON viene modificata: dump applicativo e header
		// originali restano riferiti al valore ricevuto dal client.
		this.c.contentTypeForParser = originalContentType + "; "
				+ HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_TYPE + "=\"" + inferredType + "\"";
	}

	private void emitDiagnostic(MultipartMissingTypeBehavior behavior, String inferredType, String originalContentType) {
		if(this.msgDiagnostico!=null) {
			tryEmitDiagnostic(behavior, inferredType, originalContentType);
			return;
		}
		if(this.c.log!=null) {
			this.c.log.warn("Content-Type '{}' della richiesta privo del parametro 'type' (RFC 2387 §3.1): applicata compensazione '{}', type dedotto: '{}'",
					originalContentType, behavior.getValue(), inferredType);
		}
	}

	private void tryEmitDiagnostic(MultipartMissingTypeBehavior behavior, String inferredType, String originalContentType) {
		try {
			this.msgDiagnostico.addKeyword(CostantiPdD.KEY_HTTP_HEADER, originalContentType);
			this.msgDiagnostico.addKeyword(CostantiPdD.KEY_STRATEGIA, behavior.getValue());
			this.msgDiagnostico.addKeyword(CostantiPdD.KEY_TYPE_INFERITO, inferredType);
			this.msgDiagnostico.logPersonalizzato("contentType.multipart.related.missingType.compensated");
		} catch(Exception diagEx) {
			if(this.c.log!=null) {
				this.c.log.warn("Emissione diagnostico 'contentType.multipart.related.missingType.compensated' fallita: {}", diagEx.getMessage(), diagEx);
			}
		}
	}

	private MultipartMissingTypeBehavior resolveBehavior() {
		OpenSPCoop2Properties props = this.c.openspcoopProperties;
		MultipartMissingTypeBehavior defaultValue;
		if(isPortaApplicativa()) {
			defaultValue = props.getMultipartRelatedMissingTypeBehaviorRicezioneBuste();
		} else {
			defaultValue = props.getMultipartRelatedMissingTypeBehaviorRicezioneContenutiApplicativi();
		}
		return CostantiProprieta.getConnettoriMultipartRelatedMissingTypeRequestBehavior(this.proprietaPorta, defaultValue);
	}

	private int resolvePeekBytes() {
		OpenSPCoop2Properties props = this.c.openspcoopProperties;
		int defaultValue;
		if(isPortaApplicativa()) {
			defaultValue = props.getMultipartRelatedMissingTypePeekBytesRicezioneBuste();
		} else {
			defaultValue = props.getMultipartRelatedMissingTypePeekBytesRicezioneContenutiApplicativi();
		}
		return CostantiProprieta.getConnettoriMultipartRelatedMissingTypeRequestPeekBytes(this.proprietaPorta, defaultValue);
	}

	private boolean isPortaApplicativa() {
		IDService s = this.c.getIdModuloAsIDService();
		return IDService.PORTA_APPLICATIVA.equals(s) || IDService.PORTA_APPLICATIVA_NIO.equals(s);
	}
}
