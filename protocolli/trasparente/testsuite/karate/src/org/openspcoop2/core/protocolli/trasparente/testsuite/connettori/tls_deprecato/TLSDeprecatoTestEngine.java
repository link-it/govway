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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.tls_deprecato;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.HttpLibraryMode;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * Verifica che, per ogni tipologia SSL del connettore https, la libreria http utilizzata negozi gli stessi protocolli
 * e cipher previsti dal JDK per quella tipologia (gli stessi riportati nella sezione 'Runtime' della console),
 * contro tutte le varianti del backend {@link TLSDeprecatoMockServer}.
 *
 * Per ogni tipologia viene eseguita un'invocazione per variante e, al termine, viene riportata la tabella degli scostamenti:
 * <ul>
 *   <li>esito atteso OK: risposta 200 con protocollo (e cipher) negoziati dal backend uguali agli attesi, transazione con esito OK
 *       e diagnostici emessi dalla libreria attesa;</li>
 *   <li>esito atteso KO: risposta di errore, transazione con esito 'ERRORE_INVOCAZIONE' e diagnostici emessi dalla libreria attesa.</li>
 * </ul>
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class TLSDeprecatoTestEngine extends TLSDeprecatoBase {

	private final HttpLibraryMode mode;

	protected TLSDeprecatoTestEngine(HttpLibraryMode mode) {
		this.mode = mode;
	}

	@Test
	public void ssl() throws Exception {
		verifica(TipologiaSSL.SSL);
	}
	@Test
	public void sslv3() throws Exception {
		verifica(TipologiaSSL.SSLV3);
	}
	@Test
	public void tls() throws Exception {
		verifica(TipologiaSSL.TLS);
	}
	@Test
	public void tlsv1() throws Exception {
		verifica(TipologiaSSL.TLSV1);
	}
	@Test
	public void tlsv11() throws Exception {
		verifica(TipologiaSSL.TLSV1_1);
	}
	@Test
	public void tlsv12() throws Exception {
		verifica(TipologiaSSL.TLSV1_2);
	}
	@Test
	public void tlsv13() throws Exception {
		verifica(TipologiaSSL.TLSV1_3);
	}
	@Test
	public void sslv2Hello() throws Exception {
		verifica(TipologiaSSL.SSLV2HELLO);
	}

	private void verifica(TipologiaSSL tipologia) throws Exception {
		Risultati risultati = new Risultati("Tipologia '"+tipologia.getSslType()+"' "+
				this.mode.getLibrary()+(this.mode.isNIO() ? " NIO" : " BIO")+" ("+informazioniSSL.getProtocolli(tipologia)+")");
		for (VarianteServerTLS variante : VarianteServerTLS.values()) {
			mock.start(variante);
			EsitoHandshake atteso = informazioniSSL.calcolaAtteso(tipologia, variante);
			invoke(tipologia, variante, atteso, risultati);
		}
		risultati.verifica();
	}

	private void invoke(TipologiaSSL tipologia, VarianteServerTLS variante, EsitoHandshake atteso, Risultati risultati) throws Exception {
		String url = System.getProperty("govway_base_path") + "/in/"+TipologiaSSL.SOGGETTO+"/"+TipologiaSSL.API+"/v1/"+tipologia.getAzione();

		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.GET);
		request.setUrl(url);
		this.mode.patchRequest(request);

		HttpResponse response = HttpUtilities.httpInvoke(request);
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");

		EsitoHandshake rilevato = null;
		if(response.getResultHTTPOperation()==200) {
			rilevato = EsitoHandshake.ok(response.getHeaderFirstValue(TLSDeprecatoMockServer.HEADER_PROTOCOL),
					response.getHeaderFirstValue(TLSDeprecatoMockServer.HEADER_CIPHER));
		}
		else {
			rilevato = EsitoHandshake.ko("http "+response.getResultHTTPOperation()+": "+
					abbrevia(response.getContent()!=null ? new String(response.getContent()) : null));
		}

		// esito della transazione e diagnostici della libreria attesa, verificati solo se l'handshake corrisponde all'atteso
		String erroreVerifica = null;
		if(atteso.corrisponde(rilevato)) {
			if(idTransazione==null) {
				erroreVerifica = "header GovWay-Transaction-ID non presente";
			}
			else {
				try {
					long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(
							atteso.isOk() ? EsitoTransazioneName.OK : EsitoTransazioneName.ERRORE_INVOCAZIONE);
					DBVerifier.verify(idTransazione, esitoExpected, this.mode);
				}catch(AssertionError e) {
					erroreVerifica = "transazione "+idTransazione+": "+abbrevia(e.getMessage());
				}
			}
		}
		risultati.add(variante, atteso, rilevato, erroreVerifica);
	}
}
