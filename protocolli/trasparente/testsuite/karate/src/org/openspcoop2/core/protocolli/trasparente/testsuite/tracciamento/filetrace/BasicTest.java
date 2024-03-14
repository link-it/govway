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
package org.openspcoop2.core.protocolli.trasparente.testsuite.tracciamento.filetrace;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.token.validazione.ValidazioneJWTTest;
import org.openspcoop2.core.protocolli.trasparente.testsuite.tracciamento.TestTracciamentoCostanti;
import org.openspcoop2.core.protocolli.trasparente.testsuite.tracciamento.TracciamentoUtils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.tracciamento.TracciamentoVerifica;
import org.openspcoop2.monitor.sdk.transaction.FaseTracciamento;
import org.openspcoop2.utils.BooleanNullable;

/**
* BasicTest
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class BasicTest extends ConfigLoader {
	
	private static final String API = "TestTracciamentoFiletrace";

	
	public TracciamentoVerifica getDefault() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(false);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.FALSE();
		return tracciamentoVerifica;
	}
	@Test
	public void erogazioneDefault() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_DEFAULT, 
				getDefault(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	@Test
	public void fruizioneDefault() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_DEFAULT, 
				getDefault(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	
	
	
	
	
	
	
	
	
	
	
	public TracciamentoVerifica getAbilitato() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(false);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		return tracciamentoVerifica;
	}
	@Test
	public void erogazioneAbilitato() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_ABILITATO, 
				getAbilitato(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	@Test
	public void fruizioneAbilitato() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_ABILITATO, 
				getAbilitato(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	
	
	
	public TracciamentoVerifica getAbilitatoError() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(false);		
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.forzaVerificaDBPostOutResponse=true;
		
		tracciamentoVerifica.faseTracciamentoErroreDB = false;
		tracciamentoVerifica.faseTracciamentoErrore = FaseTracciamento.POST_OUT_RESPONSE;
		
		return tracciamentoVerifica;
	}
	@Test
	public void erogazioneAbilitatoError() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_ABILITATO, 
				getAbilitatoError(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_FILETRACE_FASE_POST_OUT_RESPONSE, 
				true,
				TestTracciamentoCostanti.ERRORE_FILETRACE_FASE_POST_OUT_RESPONSE);
	}
	@Test
	public void fruizioneAbilitatoError() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_ABILITATO, 
				getAbilitatoError(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_FILETRACE_FASE_POST_OUT_RESPONSE, 
				true,
				TestTracciamentoCostanti.ERRORE_FILETRACE_FASE_POST_OUT_RESPONSE);
	}
	
	
	
	
	
	
	
	public TracciamentoVerifica getDisabilitato() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(false);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.FALSE();
		return tracciamentoVerifica;
	}
	@Test
	public void erogazioneDisabilitato() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_DISABILITATO, 
				getDisabilitato(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	@Test
	public void fruizioneDisabilitato() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_DISABILITATO, 
				getDisabilitato(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}

	
	
	
	
	
	public TracciamentoVerifica getFaultNonTracciato() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(false);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.FALSE();
		
		tracciamentoVerifica.queryParameters.put("problem", "true");
		
		return tracciamentoVerifica;
	}
	@Test
	public void erogazioneFaultNonTracciato() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_FAULT, 
				getFaultNonTracciato(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	@Test
	public void fruizioneFaultNonTracciato() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_FAULT, 
				getFaultNonTracciato(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	
	
	
	
	public TracciamentoVerifica getFaultTracciato() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(false);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		
		tracciamentoVerifica.queryParameters.put("problem", "true");
		
		return tracciamentoVerifica;
	}
	@Test
	public void erogazioneFaultTracciato() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_ABILITATO, 
				getFaultTracciato(),
				true, // expectedOk,
				TestTracciamentoCostanti.DETAIL_MESSAGGIO, true, TestTracciamentoCostanti.DETAIL_MESSAGGIO );// diagnosticoErrore, error, detail)
	}
	@Test
	public void fruizioneFaultTracciato() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_ABILITATO, 
				getFaultTracciato(),
				true, // expectedOk,
				TestTracciamentoCostanti.DETAIL, true, "errore HTTP 500" );// diagnosticoErrore, error, detail)
	}
	
	
	
	
	
	
	public TracciamentoVerifica getInformazioni() throws Exception {
		
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(false);
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		tracciamentoVerifica.checkInfo = true;
		tracciamentoVerifica.headers.put(TestTracciamentoCostanti.HTTP_TOKEN, 
				ValidazioneJWTTest.buildJWT(true, 
						tracciamentoVerifica.mapExpectedTokenInfo));
		tracciamentoVerifica.tempiElaborazioneExpected = false; // non vengono tracciati
		
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		return tracciamentoVerifica;
	}
	@Test
	public void erogazioneInformazioni() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_INFO, 
				getInformazioni(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	@Test
	public void fruizioneInformazioni() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_INFO, 
				getInformazioni(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	
	
	
	
	
	public TracciamentoVerifica getInformazioniInvertite() throws Exception {
		
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(false);
		
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheToken(logCore);
		org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.resetCacheAutorizzazione(logCore);
		
		tracciamentoVerifica.checkInfo = true;
		tracciamentoVerifica.headers.put(TestTracciamentoCostanti.HTTP_TOKEN, 
				ValidazioneJWTTest.buildJWT(true, 
						tracciamentoVerifica.mapExpectedTokenInfo));
		tracciamentoVerifica.mapExpectedTokenInfo.clear(); // non vengono tracciate
		tracciamentoVerifica.tempiElaborazioneExpected = true;
		
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		return tracciamentoVerifica;
	}
	@Test
	public void erogazioneInformazioniInvertite() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_INFO_INVERTITE, 
				getInformazioniInvertite(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	@Test
	public void fruizioneInformazioniInvertite() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_INFO_INVERTITE, 
				getInformazioniInvertite(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	
	
	
	
	
	
	
	
	
	
	public TracciamentoVerifica getDumpServerDisabled() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(false);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		
		tracciamentoVerifica.server=false;
		
		return tracciamentoVerifica;
	}
	@Test
	public void erogazioneDumpServerDisabled() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_DUMP_SERVER_DISABLED, 
				getDumpServerDisabled(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	@Test
	public void fruizioneDumpServerDisabled() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_DUMP_SERVER_DISABLED, 
				getDumpServerDisabled(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	
	
	
	
	
	
	
	
	
	public TracciamentoVerifica getDumpClientDisabled() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(false);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		
		tracciamentoVerifica.client=false;
		
		return tracciamentoVerifica;
	}
	@Test
	public void erogazioneDumpClientDisabled() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_DUMP_CLIENT_DISABLED, 
				getDumpClientDisabled(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	@Test
	public void fruizioneDumpClientDisabled() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_DUMP_CLIENT_DISABLED, 
				getDumpClientDisabled(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	
	
	
	
	
	
	
	
	public TracciamentoVerifica getExternal() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(false);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		return tracciamentoVerifica;
	}
	@Test
	public void erogazioneExternal() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_EXTERNAL_CONFIG_PROPERTIES, 
				getExternal(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	@Test
	public void fruizioneExternal() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_EXTERNAL_CONFIG_PROPERTIES, 
				getExternal(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	
	
	
	
	
	
	
	public TracciamentoVerifica getProperties() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(false);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		return tracciamentoVerifica;
	}
	@Test
	public void erogazioneProperties() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_RIDEFINITO_CONFIG_PROPERTIES, 
				getProperties(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	@Test
	public void fruizioneProperties() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_RIDEFINITO_CONFIG_PROPERTIES, 
				getProperties(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
}

