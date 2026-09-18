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
* TransferModeRestXmlTestEngine
*
* Modalita' di data transfer del connettore con messaggi di tipo XML.
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class TransferModeRestXmlTestEngine extends ConfigLoader {

	// Le dimensioni utilizzate attraversano le soglie oltre le quali il messaggio non viene piu' gestito
	// in memoria: 'soapMessage.reader.bufferThreshold' (30K), 'dump.nonRealTime.inMemory.threshold' (60K)
	// e 'dumpBinario.inMemory.threshold' (1M). Small=1K, Medium=50K, Large=500K, Big=2M.

	// La configurazione 'contentBuild' non e' applicabile: la trasformazione impostata sulle relative
	// porte applicative invoca 'request.addSimpleJsonElement(..)' ed e' quindi specifica per i messaggi json.
	// Resta verificata dal test sul medesimo formato: TransferModeRestTestEngine.

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

	private void test(String mode, String config, int size) throws Exception {
		TransferModeUtils.test(logCore, TransferModeUtils.API_REST, MessageType.XML,
				mode, config, size, this.libraryMode);
	}

}
