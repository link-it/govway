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
public class FasiBloccantiTest extends ConfigLoader {
	
	private static final String API = "TestTracciamentoDatabaseBloccante";
	
	
	public TracciamentoVerifica get4Fasi() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(true);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.TRUE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.TRUE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.TRUE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
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
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.NULL();
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
				false, // expectedOk,
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
				false, // expectedOk,
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
				false, // expectedOk,
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
				false, // expectedOk,
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
				false, // expectedOk,
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
				false, // expectedOk,
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
	
}

