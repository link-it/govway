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

package org.openspcoop2.message.soap;

import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.SoapEnvelopeStructureException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Verifica della struttura dell'Envelope SOAP su un DOM già costruito.
 *
 * Le librerie SAAJ non applicano lo schema dell'Envelope in lettura: restituiscono il primo Header/Body/Fault trovato
 * e ignorano gli altri. Componenti diversi (es. WS-Security, che risolve gli elementi firmati ovunque nel documento,
 * e l'elaborazione del Body) possono così lavorare su parti diverse dello stesso messaggio (XML Signature Wrapping).
 *
 * La verifica opera sui soli figli diretti dell'Envelope e del Body, senza scendere nel contenuto applicativo:
 * - SOAP 1.1: Header (0..1) e Body (esattamente 1) in quest'ordine; dopo il Body nessun elemento (WS-I BP R1011),
 *   a meno di abilitare gli elementi di altro namespace ammessi dallo schema (##other);
 * - SOAP 1.2: Header (0..1) e Body (esattamente 1) in quest'ordine, nessun elemento dopo il Body;
 * - nessun altro elemento del namespace SOAP e nessun testo come figlio dell'Envelope;
 * - al massimo un Fault e, se presente, unico figlio del Body (WS-I BP R1000).
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoapEnvelopeStructureValidator {

	private SoapEnvelopeStructureValidator() {}

	public static final String ERROR_MORE_THAN_ONE_BODY = "more than one Body element";
	public static final String ERROR_MORE_THAN_ONE_HEADER = "more than one Header element";
	public static final String ERROR_HEADER_AFTER_BODY = "Header element after the Body";
	public static final String ERROR_BODY_NOT_FOUND = "Body element not found";
	public static final String ERROR_MORE_THAN_ONE_FAULT = "more than one Fault element";
	public static final String ERROR_FAULT_NOT_ONLY_CHILD = "Fault element must be the only child of the Body";
	public static final String ERROR_UNEXPECTED_ENVELOPE_CHILD = "unexpected child element of the Envelope";
	public static final String ERROR_ELEMENT_AFTER_BODY = "element after the Body";
	public static final String ERROR_UNEXPECTED_TEXT = "unexpected text content in the Envelope";
	/** Rilevabile solo in streaming: con il DOM il contenuto dopo l'Envelope viene rifiutato dal parser */
	public static final String ERROR_CONTENT_AFTER_ENVELOPE = "content after the end of the Envelope";

	private static final String HEADER = "Header";
	private static final String BODY = "Body";
	private static final String FAULT = "Fault";


	// ---- Configurazione (impostata all'avvio del gateway) ----

	private static boolean requestValidationEnabled = true;
	private static boolean responseValidationEnabled = true;
	private static boolean soap11OtherNamespaceElementsAfterBodyAllowed = false;

	public static boolean isRequestValidationEnabled() {
		return requestValidationEnabled;
	}
	public static void setRequestValidationEnabled(boolean requestValidationEnabled) {
		SoapEnvelopeStructureValidator.requestValidationEnabled = requestValidationEnabled;
	}
	public static boolean isResponseValidationEnabled() {
		return responseValidationEnabled;
	}
	public static void setResponseValidationEnabled(boolean responseValidationEnabled) {
		SoapEnvelopeStructureValidator.responseValidationEnabled = responseValidationEnabled;
	}
	public static boolean isSoap11OtherNamespaceElementsAfterBodyAllowed() {
		return soap11OtherNamespaceElementsAfterBodyAllowed;
	}
	public static void setSoap11OtherNamespaceElementsAfterBodyAllowed(boolean soap11OtherNamespaceElementsAfterBodyAllowed) {
		SoapEnvelopeStructureValidator.soap11OtherNamespaceElementsAfterBodyAllowed = soap11OtherNamespaceElementsAfterBodyAllowed;
	}

	/**
	 * Verifica la struttura secondo la configurazione: richieste o risposte (comprese le risposte di fault) a seconda del ruolo.
	 * I messaggi senza ruolo (costruiti internamente dal gateway) non vengono verificati.
	 */
	public static void validate(Element envelope, MessageType messageType, MessageRole messageRole) throws SoapEnvelopeStructureException {
		if(isValidationEnabled(messageRole)) {
			validate(envelope, messageType, soap11OtherNamespaceElementsAfterBodyAllowed);
		}
	}
	
	/**
	 * Solleva l'anomalia rilevata durante la lettura in streaming del messaggio (senza costruzione del DOM), se la verifica è abilitata per il ruolo.
	 */
	public static void validate(SoapEnvelopeStructureException streamReaderError, MessageRole messageRole) throws SoapEnvelopeStructureException {
		if(streamReaderError!=null && isValidationEnabled(messageRole)) {
			throw streamReaderError;
		}
	}
	
	/**
	 * @return true se la verifica della struttura è abilitata per il ruolo del messaggio
	 */
	public static boolean isValidationEnabled(MessageRole messageRole) {
		if(messageRole==null) {
			return false;
		}
		switch (messageRole) {
		case REQUEST:
			return requestValidationEnabled;
		case RESPONSE:
		case FAULT:
			return responseValidationEnabled;
		default:
			return false;
		}
	}

	/**
	 * @param envelope elemento Envelope del messaggio
	 * @param messageType versione SOAP
	 * @param allowSoap11OtherNamespaceElementsAfterBody se true, in SOAP 1.1 sono ammessi dopo il Body elementi di un namespace diverso da quello SOAP (schema SOAP 1.1, ##other)
	 * @throws SoapEnvelopeStructureException se la struttura non è conforme
	 */
	public static void validate(Element envelope, MessageType messageType, boolean allowSoap11OtherNamespaceElementsAfterBody) throws SoapEnvelopeStructureException {

		if(envelope==null) {
			return;
		}
		boolean soap11 = MessageType.SOAP_11.equals(messageType);
		EnvelopeScan scan = new EnvelopeScan(soap11 ? Costanti.SOAP_ENVELOPE_NAMESPACE : Costanti.SOAP12_ENVELOPE_NAMESPACE,
				soap11 && allowSoap11OtherNamespaceElementsAfterBody);

		Node child = envelope.getFirstChild();
		while(child!=null) {
			short type = child.getNodeType();
			if(type==Node.ELEMENT_NODE) {
				validateEnvelopeChild(child, scan);
			}
			else if(type==Node.TEXT_NODE || type==Node.CDATA_SECTION_NODE) {
				validateEnvelopeText(child);
			}
			// commenti e altri nodi non rilevanti
			child = child.getNextSibling();
		}

		if(scan.body==null) {
			throw new SoapEnvelopeStructureException(ERROR_BODY_NOT_FOUND);
		}

		validateBody(scan.body, scan.soapNamespace);
	}
	
	/** Stato della scansione dei figli dell'Envelope */
	private static class EnvelopeScan {
		private final String soapNamespace;
		private final boolean otherNamespaceElementsAfterBodyAllowed;
		private int headers = 0;
		private int bodies = 0;
		private Element body = null;
		private EnvelopeScan(String soapNamespace, boolean otherNamespaceElementsAfterBodyAllowed) {
			this.soapNamespace = soapNamespace;
			this.otherNamespaceElementsAfterBodyAllowed = otherNamespaceElementsAfterBodyAllowed;
		}
	}
	
	private static void validateEnvelopeChild(Node child, EnvelopeScan scan) throws SoapEnvelopeStructureException {
		String namespace = child.getNamespaceURI();
		String localName = getLocalName(child);
		if(scan.soapNamespace.equals(namespace)) {
			validateSoapEnvelopeChild(child, localName, scan);
		}
		else if(scan.bodies==0) {
			throw new SoapEnvelopeStructureException(ERROR_UNEXPECTED_ENVELOPE_CHILD+" '"+toQName(namespace, localName)+"'");
		}
		else if(!scan.otherNamespaceElementsAfterBodyAllowed) {
			throw new SoapEnvelopeStructureException(ERROR_ELEMENT_AFTER_BODY+" '"+toQName(namespace, localName)+"'");
		}
	}
	
	private static void validateSoapEnvelopeChild(Node child, String localName, EnvelopeScan scan) throws SoapEnvelopeStructureException {
		if(HEADER.equals(localName)) {
			if(scan.bodies>0) {
				throw new SoapEnvelopeStructureException(ERROR_HEADER_AFTER_BODY);
			}
			scan.headers++;
			if(scan.headers>1) {
				throw new SoapEnvelopeStructureException(ERROR_MORE_THAN_ONE_HEADER+" ("+scan.headers+")");
			}
		}
		else if(BODY.equals(localName)) {
			scan.bodies++;
			if(scan.bodies>1) {
				throw new SoapEnvelopeStructureException(ERROR_MORE_THAN_ONE_BODY+" ("+scan.bodies+")");
			}
			scan.body = (Element) child;
		}
		else {
			throw new SoapEnvelopeStructureException(ERROR_UNEXPECTED_ENVELOPE_CHILD+" '"+toQName(scan.soapNamespace, localName)+"'");
		}
	}
	
	private static void validateEnvelopeText(Node child) throws SoapEnvelopeStructureException {
		String text = child.getNodeValue();
		if(text!=null && !text.trim().isEmpty()) {
			throw new SoapEnvelopeStructureException(ERROR_UNEXPECTED_TEXT);
		}
	}

	private static void validateBody(Element body, String soapNamespace) throws SoapEnvelopeStructureException {
		int elements = 0;
		int faults = 0;
		Node child = body.getFirstChild();
		while(child!=null) {
			if(child.getNodeType()==Node.ELEMENT_NODE) {
				elements++;
				if(soapNamespace.equals(child.getNamespaceURI()) && FAULT.equals(getLocalName(child))) {
					faults++;
				}
			}
			child = child.getNextSibling();
		}
		if(faults>1) {
			throw new SoapEnvelopeStructureException(ERROR_MORE_THAN_ONE_FAULT+" ("+faults+")");
		}
		if(faults==1 && elements>1) {
			throw new SoapEnvelopeStructureException(ERROR_FAULT_NOT_ONLY_CHILD);
		}
	}

	private static String getLocalName(Node node) {
		String localName = node.getLocalName();
		if(localName==null) {
			localName = node.getNodeName();
			if(localName!=null && localName.contains(":")) {
				localName = localName.substring(localName.indexOf(':')+1);
			}
		}
		return localName;
	}

	private static String toQName(String namespace, String localName) {
		return namespace!=null ? "{"+namespace+"}"+localName : localName;
	}
}
