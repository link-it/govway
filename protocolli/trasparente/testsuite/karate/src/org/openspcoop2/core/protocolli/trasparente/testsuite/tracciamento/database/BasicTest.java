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
* BasicTest
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class BasicTest extends ConfigLoader {
	
	private static final String API = "TestTracciamentoDatabase";

	
	public TracciamentoVerifica getDefault() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(true);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
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
	
	
	
	public TracciamentoVerifica getDefaultError() {
		TracciamentoVerifica tracciamentoVerifica = new TracciamentoVerifica(true);
		tracciamentoVerifica.verificaInRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutRequest = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaOutResponse = BooleanNullable.FALSE();
		tracciamentoVerifica.verificaPostOutResponse = BooleanNullable.TRUE();
		
		tracciamentoVerifica.faseTracciamentoErroreDB = true;
		tracciamentoVerifica.faseTracciamentoErrore = FaseTracciamento.POST_OUT_RESPONSE;
		
		return tracciamentoVerifica;
	}
	@Test
	public void erogazioneDefaultError() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.EROGAZIONE,
				API, TestTracciamentoCostanti.RISORSA_DEFAULT, 
				getDefaultError(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	@Test
	public void fruizioneDefaultError() throws Exception {
		
		TracciamentoUtils.test(
				logCore,
				TipoServizio.FRUIZIONE,
				API, TestTracciamentoCostanti.RISORSA_DEFAULT, 
				getDefaultError(),
				true, // expectedOk,
				null, false );// diagnosticoErrore, error, detail)
	}
	
	
	
}

