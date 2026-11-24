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
package org.openspcoop2.core.protocolli.trasparente.testsuite.other.wssecurity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * Verifica, in ricezione, degli elementi firmati e cifrati attesi (WS-Security), con le configurazioni che producono le maschere
 * della console (messageSecurity/wss4j/receiver/Signature.xml e Encrypt.xml) per ogni opzione di "Elementi firmati attesi"
 * ed "Elementi cifrati attesi": nessuna verifica, Body, Timestamp, Attachments, loro combinazioni e configurazione manuale.
 *
 * Per ogni opzione il messaggio viene protetto da tutte le azioni sender della stessa famiglia (maschere
 * messageSecurity/wss4j/sender/Signature.xml ed Encrypt.xml), che proteggono parti diverse del messaggio:
 * l'azione sender dell'erogazione applica WS-Security alla risposta (SOAP with Attachments) restituita dal backend
 * e il messaggio ottenuto viene inviato come richiesta all'azione receiver.
 * La verifica è esatta: il messaggio viene accettato solo se le parti protette coincidono con quelle attese
 * (o se la verifica non è configurata); altrimenti viene segnalata ogni parte attesa mancante e ogni parte protetta non attesa.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@RunWith(Parameterized.class)
public class WSSecurityPartiAtteseTest extends ConfigLoader {

	public static final String API = "TestWSSecurityPartiAttese";
	public static final String HEADER_FILE_RISPOSTA = "govway-testsuite-struttura-envelope-file";

	private static final String SOAP_11 = "http://schemas.xmlsoap.org/soap/envelope/";
	private static final String WSU = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";

	/** Parti del messaggio che possono essere protette */
	enum Parte { TIMESTAMP, BODY, ATTACHMENTS }

	enum Famiglia {
		/** Firma senza Timestamp ('Elementi firmati attesi' senza l'azione Timestamp) */
		FIRMA("signature", "{Element}{"+SOAP_11+"}Body"),
		/** Firma con Timestamp */
		FIRMA_TIMESTAMP("signature", "{Element}{"+SOAP_11+"}Body"),
		/** Cifratura ('Elementi cifrati attesi') */
		CIFRATURA("encryption", "{Content}{"+SOAP_11+"}Body");

		private final String tipo;
		private final String partBody;
		Famiglia(String tipo, String partBody) {
			this.tipo = tipo;
			this.partBody = partBody;
		}
		/** Parte come indicata nel diagnostico (namespace SOAP risolto) */
		String getPart(Parte parte) {
			switch (parte) {
			case TIMESTAMP:
				return "{Element}{"+WSU+"}Timestamp";
			case BODY:
				return this.partBody;
			default:
				return "{}cid:Attachments";
			}
		}
	}

	/** Azione sender: parti protette */
	enum Sender {
		FIRMA_BODY(Famiglia.FIRMA, "firma-body", Parte.BODY),
		FIRMA_ATTACHMENTS(Famiglia.FIRMA, "firma-attachments", Parte.ATTACHMENTS),
		FIRMA_BODY_ATTACHMENTS(Famiglia.FIRMA, "firma-body-attachments", Parte.BODY, Parte.ATTACHMENTS),
		FIRMA_TS_TIMESTAMP(Famiglia.FIRMA_TIMESTAMP, "firma-ts-timestamp", Parte.TIMESTAMP),
		FIRMA_TS_BODY(Famiglia.FIRMA_TIMESTAMP, "firma-ts-body", Parte.BODY),
		FIRMA_TS_TIMESTAMP_BODY(Famiglia.FIRMA_TIMESTAMP, "firma-ts-timestamp-body", Parte.TIMESTAMP, Parte.BODY),
		FIRMA_TS_ATTACHMENTS(Famiglia.FIRMA_TIMESTAMP, "firma-ts-attachments", Parte.ATTACHMENTS),
		FIRMA_TS_TIMESTAMP_ATTACHMENTS(Famiglia.FIRMA_TIMESTAMP, "firma-ts-timestamp-attachments", Parte.TIMESTAMP, Parte.ATTACHMENTS),
		FIRMA_TS_BODY_ATTACHMENTS(Famiglia.FIRMA_TIMESTAMP, "firma-ts-body-attachments", Parte.BODY, Parte.ATTACHMENTS),
		FIRMA_TS_TIMESTAMP_BODY_ATTACHMENTS(Famiglia.FIRMA_TIMESTAMP, "firma-ts-timestamp-body-attachments", Parte.TIMESTAMP, Parte.BODY, Parte.ATTACHMENTS),
		CIFRA_BODY(Famiglia.CIFRATURA, "cifra-body", Parte.BODY),
		CIFRA_ATTACHMENTS(Famiglia.CIFRATURA, "cifra-attachments", Parte.ATTACHMENTS),
		CIFRA_BODY_ATTACHMENTS(Famiglia.CIFRATURA, "cifra-body-attachments", Parte.BODY, Parte.ATTACHMENTS);

		private final Famiglia famiglia;
		private final String azione;
		private final Set<Parte> parti;
		Sender(Famiglia famiglia, String azione, Parte ... parti) {
			this.famiglia = famiglia;
			this.azione = azione;
			this.parti = EnumSet.copyOf(Arrays.asList(parti));
		}
	}

	/** Azione receiver: opzione della maschera e parti attese (null = nessuna verifica) */
	enum Receiver {
		VERIFICA_FIRMA_NONE(Famiglia.FIRMA, "verifica-firma-none"),
		VERIFICA_FIRMA_BODY(Famiglia.FIRMA, "verifica-firma-body", Parte.BODY),
		VERIFICA_FIRMA_ATTACHMENTS(Famiglia.FIRMA, "verifica-firma-attachments", Parte.ATTACHMENTS),
		VERIFICA_FIRMA_BODY_ATTACHMENTS(Famiglia.FIRMA, "verifica-firma-body-attachments", Parte.BODY, Parte.ATTACHMENTS),
		/** Configurazione manuale: Body (con namespace esplicito) e Attachments */
		VERIFICA_FIRMA_CUSTOM(Famiglia.FIRMA, "verifica-firma-custom", Parte.BODY, Parte.ATTACHMENTS),
		VERIFICA_FIRMA_TS_NONE(Famiglia.FIRMA_TIMESTAMP, "verifica-firma-ts-none"),
		VERIFICA_FIRMA_TS_BODY(Famiglia.FIRMA_TIMESTAMP, "verifica-firma-ts-body", Parte.BODY),
		VERIFICA_FIRMA_TS_TIMESTAMP(Famiglia.FIRMA_TIMESTAMP, "verifica-firma-ts-timestamp", Parte.TIMESTAMP),
		VERIFICA_FIRMA_TS_TIMESTAMP_BODY(Famiglia.FIRMA_TIMESTAMP, "verifica-firma-ts-timestamp-body", Parte.TIMESTAMP, Parte.BODY),
		VERIFICA_FIRMA_TS_ATTACHMENTS(Famiglia.FIRMA_TIMESTAMP, "verifica-firma-ts-attachments", Parte.ATTACHMENTS),
		VERIFICA_FIRMA_TS_TIMESTAMP_ATTACHMENTS(Famiglia.FIRMA_TIMESTAMP, "verifica-firma-ts-timestamp-attachments", Parte.TIMESTAMP, Parte.ATTACHMENTS),
		VERIFICA_FIRMA_TS_BODY_ATTACHMENTS(Famiglia.FIRMA_TIMESTAMP, "verifica-firma-ts-body-attachments", Parte.BODY, Parte.ATTACHMENTS),
		VERIFICA_FIRMA_TS_TIMESTAMP_BODY_ATTACHMENTS(Famiglia.FIRMA_TIMESTAMP, "verifica-firma-ts-timestamp-body-attachments", Parte.TIMESTAMP, Parte.BODY, Parte.ATTACHMENTS),
		/** Configurazione manuale: Timestamp e Body (con namespace esplicito) */
		VERIFICA_FIRMA_TS_CUSTOM(Famiglia.FIRMA_TIMESTAMP, "verifica-firma-ts-custom", Parte.TIMESTAMP, Parte.BODY),
		DECIFRA_NONE(Famiglia.CIFRATURA, "decifra-none"),
		DECIFRA_BODY(Famiglia.CIFRATURA, "decifra-body", Parte.BODY),
		DECIFRA_ATTACHMENTS(Famiglia.CIFRATURA, "decifra-attachments", Parte.ATTACHMENTS),
		DECIFRA_BODY_ATTACHMENTS(Famiglia.CIFRATURA, "decifra-body-attachments", Parte.BODY, Parte.ATTACHMENTS),
		/** Configurazione manuale: Body (con namespace esplicito) e Attachments */
		DECIFRA_CUSTOM(Famiglia.CIFRATURA, "decifra-custom", Parte.BODY, Parte.ATTACHMENTS);

		private final Famiglia famiglia;
		private final String azione;
		private final Set<Parte> partiAttese;
		Receiver(Famiglia famiglia, String azione, Parte ... partiAttese) {
			this.famiglia = famiglia;
			this.azione = azione;
			this.partiAttese = partiAttese.length==0 ? null : EnumSet.copyOf(Arrays.asList(partiAttese));
		}
	}

	@Parameters(name = "{index}: {0} <- {1}")
	public static Collection<Object[]> data() {
		Collection<Object[]> l = new ArrayList<>();
		for (Receiver receiver : Receiver.values()) {
			for (Sender sender : Sender.values()) {
				if(sender.famiglia.equals(receiver.famiglia)) {
					l.add(new Object[] {receiver, sender});
				}
			}
		}
		return l;
	}

	private final Receiver receiver;
	private final Sender sender;
	public WSSecurityPartiAtteseTest(Receiver receiver, Sender sender) {
		this.receiver = receiver;
		this.sender = sender;
	}


	@Test
	public void verificaPartiAttese() throws Exception {

		// messaggio protetto dall'azione sender (risposta SOAP with Attachments del backend)
		HttpResponse protetto = invoke(this.sender.azione, getMessaggio().getBytes(StandardCharsets.UTF_8), HttpConstants.CONTENT_TYPE_SOAP_1_1, getMessaggioConAllegati());
		assertEquals("Applicazione di WS-Security ("+this.sender.azione+") fallita: "+new String(protetto.getContent()), 200, protetto.getResultHTTPOperation());
		String contentType = protetto.getContentType();
		assertTrue("Atteso messaggio con allegati, content-type: "+contentType, contentType!=null && contentType.contains(HttpConstants.CONTENT_TYPE_MULTIPART_RELATED));

		// verifica in ricezione
		HttpResponse response = invoke(this.receiver.azione, protetto.getContent(), contentType, getMessaggio());
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);

		List<String> erroriAttesi = getErroriAttesi();
		if(erroriAttesi.isEmpty()) {
			assertEquals("Atteso messaggio accettato ("+this.receiver.azione+" <- "+this.sender.azione+"): "+new String(response.getContent()),
					200, response.getResultHTTPOperation());
			DBVerifier.verify(idTransazione, getEsito(EsitoTransazioneName.OK));
		}
		else {
			assertEquals("Atteso errore di sicurezza ("+this.receiver.azione+" <- "+this.sender.azione+"): "+new String(response.getContent()),
					500, response.getResultHTTPOperation());
			assertEquals("BadRequest", response.getHeaderFirstValue(Headers.GovWayTransactionErrorType));
			DBVerifier.verify(idTransazione, getEsito(EsitoTransazioneName.ERRORE_SICUREZZA_MESSAGGIO_RICHIESTA));
			for (String errore : erroriAttesi) {
				DBVerifier.existsDiagnostico(idTransazione, errore);
			}
		}
	}

	/**
	 * @return frammenti attesi nei diagnostici: una parte attesa mancante e una parte protetta non attesa per ciascuna differenza
	 * (lista vuota se il messaggio deve essere accettato)
	 */
	private List<String> getErroriAttesi() {
		List<String> l = new ArrayList<>();
		if(this.receiver.partiAttese==null) {
			return l; // nessuna verifica
		}
		Famiglia famiglia = this.receiver.famiglia;
		for (Parte parte : this.receiver.partiAttese) {
			if(!this.sender.parti.contains(parte)) {
				l.add("Expected "+famiglia.tipo+" part("+famiglia.getPart(parte)+") not found");
			}
		}
		for (Parte parte : this.sender.parti) {
			if(!this.receiver.partiAttese.contains(parte)) {
				l.add("Found "+famiglia.tipo+" part(");
			}
		}
		return l;
	}


	// ---- Messaggi ----

	private static final String BOUNDARY = "----=_Part_WSSecurityPartiAttese";

	private static String getMessaggio() {
		return "<soap:Envelope xmlns:soap=\""+SOAP_11+"\">"+
				"<soap:Header><ns2:TestHdr xmlns:ns2=\"http://govway.org/example/header\">header</ns2:TestHdr></soap:Header>"+
				"<soap:Body><ns2:Test xmlns:ns2=\"http://govway.org/example\"><ns2:valore>parti attese</ns2:valore></ns2:Test></soap:Body>"+
				"</soap:Envelope>";
	}
	/**
	 * Messaggio SOAP with Attachments (due allegati): la risposta del backend protetta dalle azioni sender.
	 * La prima riga del boundary termina con il solo LF: il TestService ricava il boundary del Content-Type
	 * con MultipartUtils.findBoundary, che con CRLF includerebbe il '\r' (inviato come spazio nell'header
	 * e quindi boundary non più riconosciuto da SAAJ); le righe successive restano CRLF, richiesto da SAAJ.
	 */
	private static String getMessaggioConAllegati() {
		return "--"+BOUNDARY+"\n"+
				"Content-Type: text/xml; charset=UTF-8\r\n"+
				"Content-Transfer-Encoding: binary\r\n"+
				"Content-ID: <root.message@govway.org>\r\n\r\n"+
				getMessaggio()+"\r\n"+
				"--"+BOUNDARY+"\r\n"+
				"Content-Type: text/plain\r\n"+
				"Content-Transfer-Encoding: binary\r\n"+
				"Content-ID: <allegato1@govway.org>\r\n\r\n"+
				"primo allegato\r\n"+
				"--"+BOUNDARY+"\r\n"+
				"Content-Type: application/xml\r\n"+
				"Content-Transfer-Encoding: binary\r\n"+
				"Content-ID: <allegato2@govway.org>\r\n\r\n"+
				"<allegato>secondo allegato</allegato>\r\n"+
				"--"+BOUNDARY+"--\r\n";
	}

	/**
	 * Invia la richiesta all'erogazione; il backend restituirà 'rispostaBackend' così com'è (multipart se contiene allegati).
	 */
	private static HttpResponse invoke(String azione, byte[] richiesta, String contentType, String rispostaBackend) throws Exception {
		File f = File.createTempFile("partiAttese", ".bin");
		try {
			Files.write(f.toPath(), rispostaBackend.getBytes(StandardCharsets.UTF_8));
			// GovWay e TestService possono girare con un utente diverso da quello della testsuite
			f.setReadable(true, false);
			f.setWritable(true, false);

			HttpRequest request = new HttpRequest();
			request.setReadTimeout(20000);
			request.setMethod(HttpRequestMethod.POST);
			request.setContentType(contentType);
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, azione);
			request.addHeader(HEADER_FILE_RISPOSTA, f.getAbsolutePath());
			request.setContent(richiesta);
			request.setUrl(System.getProperty("govway_base_path") + "/SoggettoInternoTest/"+API+"/v1/"+azione);
			return HttpUtilities.httpInvoke(request);
		}finally {
			Files.deleteIfExists(f.toPath());
		}
	}

	private static long getEsito(EsitoTransazioneName name) throws Exception {
		return EsitiProperties.getInstanceFromProtocolName(ConfigLoader.getLoggerCore(), Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(name);
	}
}
