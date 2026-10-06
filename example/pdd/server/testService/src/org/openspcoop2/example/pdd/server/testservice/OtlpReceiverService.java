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



package org.openspcoop2.example.pdd.server.testservice;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * Receiver OTLP/HTTP per test: memorizza gli ultimi payload ricevuti in POST (es. le metriche inviate
 * in push da GovWay) senza decodificarli, così da consentirne la verifica ai test.
 * I payload sono memorizzati separatamente per ciascun path (es. '/otlp/v1/metrics', '/otlp/custom/v1/metrics'),
 * così da poter verificare collettori differenti.
 *
 * - POST: memorizza il payload e risponde con un ExportMetricsServiceResponse vuoto (HTTP 200);
 * - GET: restituisce l'ultimo payload ricevuto sul path (HTTP 404 se non ne è stato ricevuto alcuno), indicando
 *   negli header 'govway-otlp-*' il numero di payload ricevuti, l'istante di ricezione e gli header
 *   Content-Type e Authorization della richiesta;
 * - DELETE: elimina i payload memorizzati per il path.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OtlpReceiverService extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static final String HEADER_COUNT = "govway-otlp-count";
	public static final String HEADER_RECEIVED = "govway-otlp-received";
	public static final String HEADER_CONTENT_TYPE = "govway-otlp-content-type";
	public static final String HEADER_AUTHORIZATION = "govway-otlp-authorization";

	private static final String CONTENT_TYPE_PROTOBUF = "application/x-protobuf";
	private static final int MAX_PAYLOADS = 10;

	private static class Payload {
		private final long received = System.currentTimeMillis();
		private String contentType;
		private String authorization;
		private byte[] content;
	}

	private static class Receiver {
		private final Deque<Payload> payloads = new ArrayDeque<>();
		private long count = 0;
	}

	private static final Map<String, Receiver> receivers = new HashMap<>();

	private static String path(HttpServletRequest req) {
		return req.getPathInfo()!=null ? req.getPathInfo() : "/";
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		Payload p = new Payload();
		p.contentType = req.getContentType();
		p.authorization = req.getHeader("Authorization");
		p.content = req.getInputStream().readAllBytes();
		synchronized (receivers) {
			Receiver r = receivers.computeIfAbsent(path(req), k -> new Receiver());
			r.payloads.addLast(p);
			if(r.payloads.size()>MAX_PAYLOADS) {
				r.payloads.removeFirst();
			}
			r.count++;
		}
		// ExportMetricsServiceResponse vuoto: un messaggio protobuf senza campi ha una codifica vuota
		res.setStatus(HttpServletResponse.SC_OK);
		res.setContentType(CONTENT_TYPE_PROTOBUF);
		res.setContentLength(0);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		Payload p = null;
		long c = 0;
		synchronized (receivers) {
			Receiver r = receivers.get(path(req));
			if(r!=null) {
				p = r.payloads.peekLast();
				c = r.count;
			}
		}
		res.setHeader(HEADER_COUNT, String.valueOf(c));
		if(p==null) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		res.setStatus(HttpServletResponse.SC_OK);
		res.setHeader(HEADER_RECEIVED, String.valueOf(p.received));
		if(p.contentType!=null) {
			res.setHeader(HEADER_CONTENT_TYPE, p.contentType);
		}
		if(p.authorization!=null) {
			res.setHeader(HEADER_AUTHORIZATION, p.authorization);
		}
		res.setContentType(CONTENT_TYPE_PROTOBUF);
		res.setContentLength(p.content.length);
		res.getOutputStream().write(p.content);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse res) {
		synchronized (receivers) {
			receivers.remove(path(req));
		}
		res.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}
}
