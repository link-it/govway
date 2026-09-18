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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.HttpLibraryMode;
import org.openspcoop2.message.constants.MessageType;

/**
* TransferModeSoap11TestEngine
*
* Modalita' di data transfer del connettore con messaggi SOAP 1.1.
* La variante SOAP 1.2 e' verificata da TransferModeSoapTestEngine.
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class TransferModeSoap11TestEngine extends ConfigLoader {

	// Le dimensioni utilizzate attraversano le soglie oltre le quali il messaggio non viene piu' gestito
	// in memoria: 'soapMessage.reader.bufferThreshold' (30K), 'dump.nonRealTime.inMemory.threshold' (60K)
	// e 'dumpBinario.inMemory.threshold' (1M). Small=1K, Medium=50K, Large=500K, Big=2M.

	// La trasformazione configurata per 'contentBuild' distingue le due versioni di SOAP
	// (namespace, 'actor'/'role' e mustUnderstand '1'/'true'), per cui la configurazione
	// e' utilizzabile anche con SOAP 1.1 senza modifiche.

	private HttpLibraryMode libraryMode = null;
	protected void setHttpLibraryMode(HttpLibraryMode mode) {
		this.libraryMode = mode;
	}

	@Test
	public void defaultSmallStreaming() throws Exception {
		test(TransferModeUtils.MODE_DEFAULT, TransferModeUtils.CONFIG_STREAMING, Bodies.SMALL_SIZE);
	}

	@Test
	public void defaultMediumStreaming() throws Exception {
		test(TransferModeUtils.MODE_DEFAULT, TransferModeUtils.CONFIG_STREAMING, Bodies.SIZE_50K);
	}

	@Test
	public void defaultLargeStreaming() throws Exception {
		test(TransferModeUtils.MODE_DEFAULT, TransferModeUtils.CONFIG_STREAMING, Bodies.SIZE_500K);
	}

	@Test
	public void defaultBigStreaming() throws Exception {
		test(TransferModeUtils.MODE_DEFAULT, TransferModeUtils.CONFIG_STREAMING, Bodies.BIG_SIZE);
	}

	@Test
	public void lengthSmallStreaming() throws Exception {
		test(TransferModeUtils.MODE_CONTENT_LENGTH, TransferModeUtils.CONFIG_STREAMING, Bodies.SMALL_SIZE);
	}

	@Test
	public void lengthMediumStreaming() throws Exception {
		test(TransferModeUtils.MODE_CONTENT_LENGTH, TransferModeUtils.CONFIG_STREAMING, Bodies.SIZE_50K);
	}

	@Test
	public void lengthLargeStreaming() throws Exception {
		test(TransferModeUtils.MODE_CONTENT_LENGTH, TransferModeUtils.CONFIG_STREAMING, Bodies.SIZE_500K);
	}

	@Test
	public void lengthBigStreaming() throws Exception {
		test(TransferModeUtils.MODE_CONTENT_LENGTH, TransferModeUtils.CONFIG_STREAMING, Bodies.BIG_SIZE);
	}

	@Test
	public void transferSmallStreaming() throws Exception {
		test(TransferModeUtils.MODE_TRANSFER_ENCODING_CHUNKED, TransferModeUtils.CONFIG_STREAMING, Bodies.SMALL_SIZE);
	}

	@Test
	public void transferMediumStreaming() throws Exception {
		test(TransferModeUtils.MODE_TRANSFER_ENCODING_CHUNKED, TransferModeUtils.CONFIG_STREAMING, Bodies.SIZE_50K);
	}

	@Test
	public void transferLargeStreaming() throws Exception {
		test(TransferModeUtils.MODE_TRANSFER_ENCODING_CHUNKED, TransferModeUtils.CONFIG_STREAMING, Bodies.SIZE_500K);
	}

	@Test
	public void transferBigStreaming() throws Exception {
		test(TransferModeUtils.MODE_TRANSFER_ENCODING_CHUNKED, TransferModeUtils.CONFIG_STREAMING, Bodies.BIG_SIZE);
	}

	@Test
	public void defaultSmallDebugConnettoriLog() throws Exception {
		test(TransferModeUtils.MODE_DEFAULT, TransferModeUtils.CONFIG_DEBUG_CONNETTORI_LOG, Bodies.SMALL_SIZE);
	}

	@Test
	public void defaultMediumDebugConnettoriLog() throws Exception {
		test(TransferModeUtils.MODE_DEFAULT, TransferModeUtils.CONFIG_DEBUG_CONNETTORI_LOG, Bodies.SIZE_50K);
	}

	@Test
	public void defaultLargeDebugConnettoriLog() throws Exception {
		test(TransferModeUtils.MODE_DEFAULT, TransferModeUtils.CONFIG_DEBUG_CONNETTORI_LOG, Bodies.SIZE_500K);
	}

	@Test
	public void defaultBigDebugConnettoriLog() throws Exception {
		test(TransferModeUtils.MODE_DEFAULT, TransferModeUtils.CONFIG_DEBUG_CONNETTORI_LOG, Bodies.BIG_SIZE);
	}

	@Test
	public void lengthSmallDebugConnettoriLog() throws Exception {
		test(TransferModeUtils.MODE_CONTENT_LENGTH, TransferModeUtils.CONFIG_DEBUG_CONNETTORI_LOG, Bodies.SMALL_SIZE);
	}

	@Test
	public void lengthMediumDebugConnettoriLog() throws Exception {
		test(TransferModeUtils.MODE_CONTENT_LENGTH, TransferModeUtils.CONFIG_DEBUG_CONNETTORI_LOG, Bodies.SIZE_50K);
	}

	@Test
	public void lengthLargeDebugConnettoriLog() throws Exception {
		test(TransferModeUtils.MODE_CONTENT_LENGTH, TransferModeUtils.CONFIG_DEBUG_CONNETTORI_LOG, Bodies.SIZE_500K);
	}

	@Test
	public void lengthBigDebugConnettoriLog() throws Exception {
		test(TransferModeUtils.MODE_CONTENT_LENGTH, TransferModeUtils.CONFIG_DEBUG_CONNETTORI_LOG, Bodies.BIG_SIZE);
	}

	@Test
	public void transferSmallDebugConnettoriLog() throws Exception {
		test(TransferModeUtils.MODE_TRANSFER_ENCODING_CHUNKED, TransferModeUtils.CONFIG_DEBUG_CONNETTORI_LOG, Bodies.SMALL_SIZE);
	}

	@Test
	public void transferMediumDebugConnettoriLog() throws Exception {
		test(TransferModeUtils.MODE_TRANSFER_ENCODING_CHUNKED, TransferModeUtils.CONFIG_DEBUG_CONNETTORI_LOG, Bodies.SIZE_50K);
	}

	@Test
	public void transferLargeDebugConnettoriLog() throws Exception {
		test(TransferModeUtils.MODE_TRANSFER_ENCODING_CHUNKED, TransferModeUtils.CONFIG_DEBUG_CONNETTORI_LOG, Bodies.SIZE_500K);
	}

	@Test
	public void transferBigDebugConnettoriLog() throws Exception {
		test(TransferModeUtils.MODE_TRANSFER_ENCODING_CHUNKED, TransferModeUtils.CONFIG_DEBUG_CONNETTORI_LOG, Bodies.BIG_SIZE);
	}

	@Test
	public void defaultSmallRegistrazioneMessaggio() throws Exception {
		test(TransferModeUtils.MODE_DEFAULT, TransferModeUtils.CONFIG_REGISTRAZIONE_MESSAGGIO, Bodies.SMALL_SIZE);
	}

	@Test
	public void defaultMediumRegistrazioneMessaggio() throws Exception {
		test(TransferModeUtils.MODE_DEFAULT, TransferModeUtils.CONFIG_REGISTRAZIONE_MESSAGGIO, Bodies.SIZE_50K);
	}

	@Test
	public void defaultLargeRegistrazioneMessaggio() throws Exception {
		test(TransferModeUtils.MODE_DEFAULT, TransferModeUtils.CONFIG_REGISTRAZIONE_MESSAGGIO, Bodies.SIZE_500K);
	}

	@Test
	public void defaultBigRegistrazioneMessaggio() throws Exception {
		test(TransferModeUtils.MODE_DEFAULT, TransferModeUtils.CONFIG_REGISTRAZIONE_MESSAGGIO, Bodies.BIG_SIZE);
	}

	@Test
	public void lengthSmallRegistrazioneMessaggio() throws Exception {
		test(TransferModeUtils.MODE_CONTENT_LENGTH, TransferModeUtils.CONFIG_REGISTRAZIONE_MESSAGGIO, Bodies.SMALL_SIZE);
	}

	@Test
	public void lengthMediumRegistrazioneMessaggio() throws Exception {
		test(TransferModeUtils.MODE_CONTENT_LENGTH, TransferModeUtils.CONFIG_REGISTRAZIONE_MESSAGGIO, Bodies.SIZE_50K);
	}

	@Test
	public void lengthLargeRegistrazioneMessaggio() throws Exception {
		test(TransferModeUtils.MODE_CONTENT_LENGTH, TransferModeUtils.CONFIG_REGISTRAZIONE_MESSAGGIO, Bodies.SIZE_500K);
	}

	@Test
	public void lengthBigRegistrazioneMessaggio() throws Exception {
		test(TransferModeUtils.MODE_CONTENT_LENGTH, TransferModeUtils.CONFIG_REGISTRAZIONE_MESSAGGIO, Bodies.BIG_SIZE);
	}

	@Test
	public void transferSmallRegistrazioneMessaggio() throws Exception {
		test(TransferModeUtils.MODE_TRANSFER_ENCODING_CHUNKED, TransferModeUtils.CONFIG_REGISTRAZIONE_MESSAGGIO, Bodies.SMALL_SIZE);
	}

	@Test
	public void transferMediumRegistrazioneMessaggio() throws Exception {
		test(TransferModeUtils.MODE_TRANSFER_ENCODING_CHUNKED, TransferModeUtils.CONFIG_REGISTRAZIONE_MESSAGGIO, Bodies.SIZE_50K);
	}

	@Test
	public void transferLargeRegistrazioneMessaggio() throws Exception {
		test(TransferModeUtils.MODE_TRANSFER_ENCODING_CHUNKED, TransferModeUtils.CONFIG_REGISTRAZIONE_MESSAGGIO, Bodies.SIZE_500K);
	}

	@Test
	public void transferBigRegistrazioneMessaggio() throws Exception {
		test(TransferModeUtils.MODE_TRANSFER_ENCODING_CHUNKED, TransferModeUtils.CONFIG_REGISTRAZIONE_MESSAGGIO, Bodies.BIG_SIZE);
	}

	@Test
	public void defaultSmallContentBuild() throws Exception {
		test(TransferModeUtils.MODE_DEFAULT, TransferModeUtils.CONFIG_CONTENT_BUILD, Bodies.SMALL_SIZE);
	}

	@Test
	public void defaultMediumContentBuild() throws Exception {
		test(TransferModeUtils.MODE_DEFAULT, TransferModeUtils.CONFIG_CONTENT_BUILD, Bodies.SIZE_50K);
	}

	@Test
	public void defaultLargeContentBuild() throws Exception {
		test(TransferModeUtils.MODE_DEFAULT, TransferModeUtils.CONFIG_CONTENT_BUILD, Bodies.SIZE_500K);
	}

	@Test
	public void defaultBigContentBuild() throws Exception {
		test(TransferModeUtils.MODE_DEFAULT, TransferModeUtils.CONFIG_CONTENT_BUILD, Bodies.BIG_SIZE);
	}

	@Test
	public void lengthSmallContentBuild() throws Exception {
		test(TransferModeUtils.MODE_CONTENT_LENGTH, TransferModeUtils.CONFIG_CONTENT_BUILD, Bodies.SMALL_SIZE);
	}

	@Test
	public void lengthMediumContentBuild() throws Exception {
		test(TransferModeUtils.MODE_CONTENT_LENGTH, TransferModeUtils.CONFIG_CONTENT_BUILD, Bodies.SIZE_50K);
	}

	@Test
	public void lengthLargeContentBuild() throws Exception {
		test(TransferModeUtils.MODE_CONTENT_LENGTH, TransferModeUtils.CONFIG_CONTENT_BUILD, Bodies.SIZE_500K);
	}

	@Test
	public void lengthBigContentBuild() throws Exception {
		test(TransferModeUtils.MODE_CONTENT_LENGTH, TransferModeUtils.CONFIG_CONTENT_BUILD, Bodies.BIG_SIZE);
	}

	@Test
	public void transferSmallContentBuild() throws Exception {
		test(TransferModeUtils.MODE_TRANSFER_ENCODING_CHUNKED, TransferModeUtils.CONFIG_CONTENT_BUILD, Bodies.SMALL_SIZE);
	}

	@Test
	public void transferMediumContentBuild() throws Exception {
		test(TransferModeUtils.MODE_TRANSFER_ENCODING_CHUNKED, TransferModeUtils.CONFIG_CONTENT_BUILD, Bodies.SIZE_50K);
	}

	@Test
	public void transferLargeContentBuild() throws Exception {
		test(TransferModeUtils.MODE_TRANSFER_ENCODING_CHUNKED, TransferModeUtils.CONFIG_CONTENT_BUILD, Bodies.SIZE_500K);
	}

	@Test
	public void transferBigContentBuild() throws Exception {
		test(TransferModeUtils.MODE_TRANSFER_ENCODING_CHUNKED, TransferModeUtils.CONFIG_CONTENT_BUILD, Bodies.BIG_SIZE);
	}

	@Test
	public void defaultSmallSbustamentoSoap() throws Exception {
		test(TransferModeUtils.MODE_DEFAULT, TransferModeUtils.CONFIG_SBUSTAMENTO_SOAP, Bodies.SMALL_SIZE);
	}

	@Test
	public void defaultMediumSbustamentoSoap() throws Exception {
		test(TransferModeUtils.MODE_DEFAULT, TransferModeUtils.CONFIG_SBUSTAMENTO_SOAP, Bodies.SIZE_50K);
	}

	@Test
	public void defaultLargeSbustamentoSoap() throws Exception {
		test(TransferModeUtils.MODE_DEFAULT, TransferModeUtils.CONFIG_SBUSTAMENTO_SOAP, Bodies.SIZE_500K);
	}

	@Test
	public void defaultBigSbustamentoSoap() throws Exception {
		test(TransferModeUtils.MODE_DEFAULT, TransferModeUtils.CONFIG_SBUSTAMENTO_SOAP, Bodies.BIG_SIZE);
	}

	@Test
	public void lengthSmallSbustamentoSoap() throws Exception {
		test(TransferModeUtils.MODE_CONTENT_LENGTH, TransferModeUtils.CONFIG_SBUSTAMENTO_SOAP, Bodies.SMALL_SIZE);
	}

	@Test
	public void lengthMediumSbustamentoSoap() throws Exception {
		test(TransferModeUtils.MODE_CONTENT_LENGTH, TransferModeUtils.CONFIG_SBUSTAMENTO_SOAP, Bodies.SIZE_50K);
	}

	@Test
	public void lengthLargeSbustamentoSoap() throws Exception {
		test(TransferModeUtils.MODE_CONTENT_LENGTH, TransferModeUtils.CONFIG_SBUSTAMENTO_SOAP, Bodies.SIZE_500K);
	}

	@Test
	public void lengthBigSbustamentoSoap() throws Exception {
		test(TransferModeUtils.MODE_CONTENT_LENGTH, TransferModeUtils.CONFIG_SBUSTAMENTO_SOAP, Bodies.BIG_SIZE);
	}

	@Test
	public void transferSmallSbustamentoSoap() throws Exception {
		test(TransferModeUtils.MODE_TRANSFER_ENCODING_CHUNKED, TransferModeUtils.CONFIG_SBUSTAMENTO_SOAP, Bodies.SMALL_SIZE);
	}

	@Test
	public void transferMediumSbustamentoSoap() throws Exception {
		test(TransferModeUtils.MODE_TRANSFER_ENCODING_CHUNKED, TransferModeUtils.CONFIG_SBUSTAMENTO_SOAP, Bodies.SIZE_50K);
	}

	@Test
	public void transferLargeSbustamentoSoap() throws Exception {
		test(TransferModeUtils.MODE_TRANSFER_ENCODING_CHUNKED, TransferModeUtils.CONFIG_SBUSTAMENTO_SOAP, Bodies.SIZE_500K);
	}

	@Test
	public void transferBigSbustamentoSoap() throws Exception {
		test(TransferModeUtils.MODE_TRANSFER_ENCODING_CHUNKED, TransferModeUtils.CONFIG_SBUSTAMENTO_SOAP, Bodies.BIG_SIZE);
	}

	private void test(String mode, String config, int size) throws Exception {
		TransferModeUtils.test(logCore, TransferModeUtils.API_SOAP, MessageType.SOAP_11,
				mode, config, size, this.libraryMode);
	}

}
