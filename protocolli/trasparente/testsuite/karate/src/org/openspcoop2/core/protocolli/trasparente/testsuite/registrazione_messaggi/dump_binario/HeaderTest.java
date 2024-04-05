/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
package org.openspcoop2.core.protocolli.trasparente.testsuite.registrazione_messaggi.dump_binario;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.registrazione_messaggi.DumpUtils;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
* HeaderTest
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class HeaderTest extends ConfigLoader {

	@Test
	public void erogazioneDumpCompleto() throws Exception {
		List<String> l = new ArrayList<>();
		testDumpAbilitato(TipoServizio.EROGAZIONE, "default",
				false, l,
				false, l,
				false, l,
				false, l);
	}
	@Test
	public void fruizioneDumpCompleto() throws Exception {
		List<String> l = new ArrayList<>();
		testDumpAbilitato(TipoServizio.FRUIZIONE, "default",
				false, l,
				false, l,
				false, l,
				false, l);
	}
	
	
	@Test
	public void erogazioneWhiteList() throws Exception {
		testDumpAbilitato(TipoServizio.EROGAZIONE, "whiteList",
				true, DumpUtils.getHeaderWhiteList(),
				true, DumpUtils.getHeaderWhiteList(),
				true, DumpUtils.getHeaderWhiteList(),
				true, DumpUtils.getHeaderWhiteList());
	}
	@Test
	public void fruizioneWhiteList() throws Exception {
		testDumpAbilitato(TipoServizio.FRUIZIONE, "whiteList",
				true, DumpUtils.getHeaderWhiteList(),
				true, DumpUtils.getHeaderWhiteList(),
				true, DumpUtils.getHeaderWhiteList(),
				true, DumpUtils.getHeaderWhiteList());
	}
	
	
	
	
	@Test
	public void erogazioneBlackList() throws Exception {
		testDumpAbilitato(TipoServizio.EROGAZIONE, "blackList",
				false, DumpUtils.getHeaderBlackList(),
				false, DumpUtils.getHeaderBlackList(),
				false, DumpUtils.getHeaderBlackList(),
				false, DumpUtils.getHeaderBlackList());
	}
	@Test
	public void fruizioneBlackList() throws Exception {
		testDumpAbilitato(TipoServizio.FRUIZIONE, "blackList",
				false, DumpUtils.getHeaderBlackList(),
				false, DumpUtils.getHeaderBlackList(),
				false, DumpUtils.getHeaderBlackList(),
				false, DumpUtils.getHeaderBlackList());
	}
	
	
	
	
	@Test
	public void erogazioneWhiteListCustom() throws Exception {
		testDumpAbilitato(TipoServizio.EROGAZIONE, "whiteListCustom",
				true, DumpUtils.getHeaderListCustom(DumpUtils.HEADER_TEST_2),
				true, DumpUtils.getHeaderListCustom(DumpUtils.HEADER_TEST_3),
				true, DumpUtils.getHeaderListCustom(DumpUtils.HEADER_TEST_4),
				true, DumpUtils.getHeaderListCustom(DumpUtils.HEADER_TEST_5));
	}
	@Test
	public void fruizioneWhiteListCustom() throws Exception {
		testDumpAbilitato(TipoServizio.FRUIZIONE, "whiteListCustom",
				true, DumpUtils.getHeaderListCustom(DumpUtils.HEADER_TEST_2),
				true, DumpUtils.getHeaderListCustom(DumpUtils.HEADER_TEST_3),
				true, DumpUtils.getHeaderListCustom(DumpUtils.HEADER_TEST_4),
				true, DumpUtils.getHeaderListCustom(DumpUtils.HEADER_TEST_5));
	}
	
	
	
	
	@Test
	public void erogazioneBlackListCustom() throws Exception {
		testDumpAbilitato(TipoServizio.EROGAZIONE, "blackListCustom",
				false, DumpUtils.getHeaderListCustom(DumpUtils.HEADER_TEST_1),
				false, DumpUtils.getHeaderListCustom(DumpUtils.HEADER_TEST_2),
				false, DumpUtils.getHeaderListCustom(DumpUtils.HEADER_TEST_3),
				false, DumpUtils.getHeaderListCustom(DumpUtils.HEADER_TEST_4));
	}
	@Test
	public void fruizioneBlackListCustom() throws Exception {
		testDumpAbilitato(TipoServizio.FRUIZIONE, "blackListCustom",
				false, DumpUtils.getHeaderListCustom(DumpUtils.HEADER_TEST_1),
				false, DumpUtils.getHeaderListCustom(DumpUtils.HEADER_TEST_2),
				false, DumpUtils.getHeaderListCustom(DumpUtils.HEADER_TEST_3),
				false, DumpUtils.getHeaderListCustom(DumpUtils.HEADER_TEST_4));
	}
	
	
	
	
	
	
	@Test
	public void erogazioneMixed() throws Exception {
		testDumpAbilitato(TipoServizio.EROGAZIONE, "mixed",
				true, DumpUtils.getHeaderListCustom(DumpUtils.HEADER_TEST_1, DumpUtils.HEADER_TEST_2),
				true, DumpUtils.getHeaderWhiteList(),
				true, DumpUtils.getHeaderWhiteList(),
				false, DumpUtils.getHeaderListCustom(DumpUtils.HEADER_TEST_3, DumpUtils.HEADER_TEST_4));
	}
	@Test
	public void fruizioneMixed() throws Exception {
		testDumpAbilitato(TipoServizio.FRUIZIONE, "mixed",
				true, DumpUtils.getHeaderListCustom(DumpUtils.HEADER_TEST_1, DumpUtils.HEADER_TEST_2),
				false, DumpUtils.getHeaderBlackList(),
				false, DumpUtils.getHeaderBlackList(),
				false, DumpUtils.getHeaderListCustom(DumpUtils.HEADER_TEST_3, DumpUtils.HEADER_TEST_4));
	}
	
	

	
	
	private HttpResponse testDumpAbilitato(TipoServizio tipo, String operazione,
			boolean richiestaIngressoHeaderWhiteList, List<String> richiestaIngressoHeaderList,
			boolean richiestaUscitaHeaderWhiteList, List<String> richiestaUscitaHeaderList,
			boolean rispostaIngressoHeaderWhiteList, List<String> rispostaIngressoHeaderList,
			boolean rispostaUscitaHeaderWhiteList, List<String> rispostaUscitaHeaderList) throws Exception {
		byte[] content = Bodies.getJson(Bodies.SMALL_SIZE).getBytes();
		return DumpUtils.testHeader(true, true, 
				tipo, operazione, HttpConstants.CONTENT_TYPE_JSON, content, content.length, MessageType.JSON,
				richiestaIngressoHeaderWhiteList, richiestaIngressoHeaderList,
				richiestaUscitaHeaderWhiteList, richiestaUscitaHeaderList,
				rispostaIngressoHeaderWhiteList, rispostaIngressoHeaderList,
				rispostaUscitaHeaderWhiteList, rispostaUscitaHeaderList);
	}
}
