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
package org.openspcoop2.core.protocolli.trasparente.testsuite.tracciamento.database;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.core.protocolli.trasparente.testsuite.tracciamento.TestTracciamentoCostanti;
import org.openspcoop2.core.protocolli.trasparente.testsuite.tracciamento.TracciamentoUtils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.tracciamento.TracciamentoVerifica;
import org.openspcoop2.monitor.sdk.transaction.FaseTracciamento;
import org.openspcoop2.utils.BooleanNullable;

/**
* FasiBloccantiTest
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class FasiNonBloccantiTest extends ConfigLoader {
	
	private static final String API = "TestTracciamentoDatabaseNonBloccante";
	
	
	public TracciamentoVerifica get4Fasi() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(true);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.TRUE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.TRUE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.TRUE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		
		tracciamentoVerifica.verificaContenuti = true;
		
		return tracciamentoVerifica;
	}
	@Test
	public void erogazione4fasi() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_4FASI, 
				get4Fasi(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	@Test
	public void fruizione4fasi() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_4FASI, 
				get4Fasi(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}

	
	
	public TracciamentoVerifica get4FasiRequestInError() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(true);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		
		tracciamentoVerifica.faseTracciamentoErroreDB = true;
		tracciamentoVerifica.faseTracciamentoErrore = FaseTracciamento.IN_REQUEST;
		
		return tracciamentoVerifica;
	}

	@Test
	public void erogazione4fasiRequestInError() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_4FASI, 
				get4FasiRequestInError(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_IN_REQUEST, 
				true, 
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_IN_REQUEST);
	}
	@Test
	public void fruizione4fasiRequestInError() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_4FASI, 
				get4FasiRequestInError(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_IN_REQUEST, 
				true, 
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_IN_REQUEST);
	}
	
	
	
	
	public TracciamentoVerifica get4FasiRequestOutError() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(true);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.TRUE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		
		tracciamentoVerifica.faseTracciamentoErroreDB = true;
		tracciamentoVerifica.faseTracciamentoErrore = FaseTracciamento.OUT_REQUEST;
		tracciamentoVerifica.checkDiagnosticFromFase = FaseTracciamento.OUT_REQUEST;
		tracciamentoVerifica.checkLogDetailFromFase = FaseTracciamento.OUT_REQUEST;
		
		return tracciamentoVerifica;
	}

	@Test
	public void erogazione4fasiRequestOutError() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_4FASI, 
				get4FasiRequestOutError(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_REQUEST, 
				true, 
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_REQUEST);
	}
	@Test
	public void fruizione4fasiRequestOutError() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_4FASI, 
				get4FasiRequestOutError(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_REQUEST, 
				true, 
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_REQUEST);
	}
	
	
	
	
	
	public TracciamentoVerifica get4FasiResponseOutError() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(true);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.TRUE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.TRUE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		
		tracciamentoVerifica.faseTracciamentoErroreDB = true;
		tracciamentoVerifica.faseTracciamentoErrore = FaseTracciamento.OUT_RESPONSE;
		tracciamentoVerifica.checkDiagnosticFromFase = FaseTracciamento.OUT_RESPONSE;
		tracciamentoVerifica.checkLogDetailFromFase = FaseTracciamento.OUT_RESPONSE;
		
		return tracciamentoVerifica;
	}

	@Test
	public void erogazione4fasiResponseOutError() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_4FASI, 
				get4FasiResponseOutError(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_RESPONSE, 
				true, 
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_RESPONSE);
	}
	@Test
	public void fruizione4fasiResponseOutError() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_4FASI, 
				get4FasiResponseOutError(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_RESPONSE, 
				true, 
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_RESPONSE);
	}
	
	
	
	
	
	public TracciamentoVerifica get4FasiResponsePostOutError() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(true);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.TRUE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.TRUE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.TRUE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		
		tracciamentoVerifica.faseTracciamentoErroreDB = true;
		tracciamentoVerifica.faseTracciamentoErrore = FaseTracciamento.POST_OUT_RESPONSE;
		tracciamentoVerifica.checkDiagnosticFromFase = FaseTracciamento.POST_OUT_RESPONSE;
		tracciamentoVerifica.checkLogDetailFromFase = FaseTracciamento.POST_OUT_RESPONSE;
		
		return tracciamentoVerifica;
	}

	@Test
	public void erogazione4fasiResponsePostOutError() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_4FASI, 
				get4FasiResponsePostOutError(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	@Test
	public void fruizione4fasiResponsePostOutError() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_4FASI, 
				get4FasiResponsePostOutError(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}

	
	
	
	
	
	
	
	public TracciamentoVerifica getInRequest() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(true);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.TRUE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		
		tracciamentoVerifica.verificaContenuti = true;
		
		return tracciamentoVerifica;
	}
	@Test
	public void erogazioneInRequest() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_IN_REQUEST, 
				getInRequest(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	@Test
	public void fruizioneInRequest() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_IN_REQUEST, 
				getInRequest(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}

	
	
	public TracciamentoVerifica getInRequestError() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(true);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		
		tracciamentoVerifica.faseTracciamentoErroreDB = true;
		tracciamentoVerifica.faseTracciamentoErrore = FaseTracciamento.IN_REQUEST;
		
		return tracciamentoVerifica;
	}

	@Test
	public void erogazioneInRequestError() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_IN_REQUEST, 
				getInRequestError(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_IN_REQUEST, 
				true, 
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_IN_REQUEST);
	}
	@Test
	public void fruizioneInRequestError() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_IN_REQUEST, 
				getInRequestError(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_IN_REQUEST, 
				true, 
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_IN_REQUEST);
	}
	
	
	
	
	
	
	
	
	public TracciamentoVerifica getOutRequest() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(true);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.TRUE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		
		tracciamentoVerifica.verificaContenuti = true;
		
		return tracciamentoVerifica;
	}
	@Test
	public void erogazioneOutRequest() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_OUT_REQUEST, 
				getOutRequest(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	@Test
	public void fruizioneOutRequest() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_OUT_REQUEST, 
				getOutRequest(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}

	
	
	public TracciamentoVerifica getOutRequestError() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(true);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		
		tracciamentoVerifica.faseTracciamentoErroreDB = true;
		tracciamentoVerifica.faseTracciamentoErrore = FaseTracciamento.OUT_REQUEST;
		
		return tracciamentoVerifica;
	}

	@Test
	public void erogazioneOutRequestError() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_OUT_REQUEST, 
				getOutRequestError(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_REQUEST, 
				true, 
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_REQUEST);
	}
	@Test
	public void fruizioneOutRequestError() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_OUT_REQUEST, 
				getOutRequestError(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_REQUEST, 
				true, 
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_REQUEST);
	}
	
	
	
	
	
	
	
	
	public TracciamentoVerifica getOutResponse() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(true);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.TRUE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		
		tracciamentoVerifica.verificaContenuti = true;
		
		return tracciamentoVerifica;
	}
	@Test
	public void erogazioneOutResponse() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_OUT_RESPONSE, 
				getOutResponse(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	@Test
	public void fruizioneOutResponse() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_OUT_RESPONSE, 
				getOutResponse(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}

	
	
	public TracciamentoVerifica getOutResponseError() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(true);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		
		tracciamentoVerifica.faseTracciamentoErroreDB = true;
		tracciamentoVerifica.faseTracciamentoErrore = FaseTracciamento.OUT_RESPONSE;
		
		return tracciamentoVerifica;
	}

	@Test
	public void erogazioneOutResponseError() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_OUT_RESPONSE, 
				getOutResponseError(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_RESPONSE, 
				true, 
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_RESPONSE);
	}
	@Test
	public void fruizioneOutResponseError() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_OUT_RESPONSE, 
				getOutResponseError(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_RESPONSE, 
				true, 
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_RESPONSE);
	}

	
	
	
	
	
	
	
	public TracciamentoVerifica getInRequestOutRequest() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(true);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.TRUE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.TRUE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		
		tracciamentoVerifica.verificaContenuti = true;
		
		return tracciamentoVerifica;
	}
	@Test
	public void erogazioneInRequestOutRequest() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_IN_REQUEST_OUT_REQUEST, 
				getInRequestOutRequest(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	@Test
	public void fruizioneInRequestOutRequest() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_IN_REQUEST_OUT_REQUEST, 
				getInRequestOutRequest(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}

	
	
	public TracciamentoVerifica getInRequestErrorOutRequest() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(true);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		
		tracciamentoVerifica.faseTracciamentoErroreDB = true;
		tracciamentoVerifica.faseTracciamentoErrore = FaseTracciamento.IN_REQUEST;
		
		tracciamentoVerifica.checkDiagnosticFromFase = FaseTracciamento.IN_REQUEST;
		tracciamentoVerifica.checkLogDetailFromFase = FaseTracciamento.IN_REQUEST;
		
		return tracciamentoVerifica;
	}

	@Test
	public void erogazioneInRequestErrorOutRequest() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_IN_REQUEST_OUT_REQUEST, 
				getInRequestErrorOutRequest(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_IN_REQUEST, 
				true, 
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_IN_REQUEST);
	}
	@Test
	public void fruizioneInRequestErrorOutRequest() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_IN_REQUEST_OUT_REQUEST, 
				getInRequestErrorOutRequest(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_IN_REQUEST, 
				true, 
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_IN_REQUEST);
	}

	
	
	public TracciamentoVerifica getInRequestOutRequestError() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(true);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.TRUE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		
		tracciamentoVerifica.faseTracciamentoErroreDB = true;
		tracciamentoVerifica.faseTracciamentoErrore = FaseTracciamento.OUT_REQUEST;
		
		tracciamentoVerifica.checkDiagnosticFromFase = FaseTracciamento.OUT_REQUEST;
		tracciamentoVerifica.checkLogDetailFromFase = FaseTracciamento.OUT_REQUEST;
		
		return tracciamentoVerifica;
	}

	@Test
	public void erogazioneInRequestOutRequestError() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_IN_REQUEST_OUT_REQUEST, 
				getInRequestOutRequestError(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_REQUEST, 
				true, 
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_REQUEST);
	}
	@Test
	public void fruizioneInRequestOutRequestError() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_IN_REQUEST_OUT_REQUEST, 
				getInRequestOutRequestError(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_REQUEST, 
				true, 
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_REQUEST);
	}

	
	
	
	
	
	
	
	public TracciamentoVerifica getInRequestOutResponse() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(true);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.TRUE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.TRUE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		
		tracciamentoVerifica.verificaContenuti = true;
		
		return tracciamentoVerifica;
	}
	@Test
	public void erogazioneInRequestOutResponse() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_IN_REQUEST_OUT_RESPONSE, 
				getInRequestOutResponse(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	@Test
	public void fruizioneInRequestOutResponse() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_IN_REQUEST_OUT_RESPONSE, 
				getInRequestOutResponse(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}

	
	
	public TracciamentoVerifica getInRequestErrorOutResponse() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(true);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		
		tracciamentoVerifica.faseTracciamentoErroreDB = true;
		tracciamentoVerifica.faseTracciamentoErrore = FaseTracciamento.IN_REQUEST;
		
		tracciamentoVerifica.checkDiagnosticFromFase = FaseTracciamento.IN_REQUEST;
		tracciamentoVerifica.checkLogDetailFromFase = FaseTracciamento.IN_REQUEST;
		
		return tracciamentoVerifica;
	}

	@Test
	public void erogazioneInRequestErrorOutResponse() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_IN_REQUEST_OUT_RESPONSE, 
				getInRequestErrorOutResponse(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_IN_REQUEST, 
				true, 
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_IN_REQUEST);
	}
	@Test
	public void fruizioneInRequestErrorOutResponse() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_IN_REQUEST_OUT_RESPONSE, 
				getInRequestErrorOutResponse(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_IN_REQUEST, 
				true, 
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_IN_REQUEST);
	}

	
	
	public TracciamentoVerifica getInRequestOutResponseError() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(true);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.TRUE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		
		tracciamentoVerifica.faseTracciamentoErroreDB = true;
		tracciamentoVerifica.faseTracciamentoErrore = FaseTracciamento.OUT_RESPONSE;
		
		tracciamentoVerifica.checkDiagnosticFromFase = FaseTracciamento.OUT_RESPONSE;
		tracciamentoVerifica.checkLogDetailFromFase = FaseTracciamento.OUT_RESPONSE;
		
		return tracciamentoVerifica;
	}

	@Test
	public void erogazioneInRequestOutResponseError() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_IN_REQUEST_OUT_RESPONSE, 
				getInRequestOutResponseError(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_RESPONSE, 
				true, 
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_RESPONSE);
	}
	@Test
	public void fruizioneInRequestOutResponseError() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_IN_REQUEST_OUT_RESPONSE, 
				getInRequestOutResponseError(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_RESPONSE, 
				true, 
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_RESPONSE);
	}

	
	
	
	
	
	
	
	public TracciamentoVerifica getOutRequestOutResponse() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(true);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.TRUE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.TRUE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		
		tracciamentoVerifica.verificaContenuti = true;
		
		return tracciamentoVerifica;
	}
	@Test
	public void erogazioneOutRequestOutResponse() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_OUT_REQUEST_OUT_RESPONSE, 
				getOutRequestOutResponse(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	@Test
	public void fruizioneOutRequestOutResponse() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_OUT_REQUEST_OUT_RESPONSE, 
				getOutRequestOutResponse(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}

	
	
	public TracciamentoVerifica getOutRequestErrorOutResponse() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(true);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		
		tracciamentoVerifica.faseTracciamentoErroreDB = true;
		tracciamentoVerifica.faseTracciamentoErrore = FaseTracciamento.OUT_REQUEST;
		
		tracciamentoVerifica.checkDiagnosticFromFase = FaseTracciamento.OUT_REQUEST;
		tracciamentoVerifica.checkLogDetailFromFase = FaseTracciamento.OUT_REQUEST;
		
		return tracciamentoVerifica;
	}

	@Test
	public void erogazioneOutRequestErrorOutResponse() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_OUT_REQUEST_OUT_RESPONSE, 
				getOutRequestErrorOutResponse(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_REQUEST, 
				true, 
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_REQUEST);
	}
	@Test
	public void fruizioneOutRequestErrorOutResponse() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_OUT_REQUEST_OUT_RESPONSE, 
				getOutRequestErrorOutResponse(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_REQUEST, 
				true, 
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_REQUEST);
	}

	
	
	public TracciamentoVerifica getOutRequestOutResponseError() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(true);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.TRUE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		
		tracciamentoVerifica.faseTracciamentoErroreDB = true;
		tracciamentoVerifica.faseTracciamentoErrore = FaseTracciamento.OUT_RESPONSE;
		
		tracciamentoVerifica.checkDiagnosticFromFase = FaseTracciamento.OUT_RESPONSE;
		tracciamentoVerifica.checkLogDetailFromFase = FaseTracciamento.OUT_RESPONSE;
		
		return tracciamentoVerifica;
	}

	@Test
	public void erogazioneOutRequestOutResponseError() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_OUT_REQUEST_OUT_RESPONSE, 
				getOutRequestOutResponseError(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_RESPONSE, 
				true, 
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_RESPONSE);
	}
	@Test
	public void fruizioneOutRequestOutResponseError() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_OUT_REQUEST_OUT_RESPONSE, 
				getOutRequestOutResponseError(),
				true, // expectedOk,
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_RESPONSE, 
				true, 
				TestTracciamentoCostanti.ERRORE_DATABASE_FASE_OUT_RESPONSE);
	}
}

