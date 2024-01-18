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
package org.openspcoop2.core.protocolli.trasparente.testsuite.autenticazione.applicativi_token;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.transport.http.HttpResponse;

/**
* RestAutenticazioneTest
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class RestAutenticazioneTest extends ConfigLoader {

	private static final String API = "TestAutenticazioneTokenREST";
	
	
	// ** pubblico **
	
	@Test
	public void erogazioneSoloToken_clientIdNonRegistrato() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "pubblico",
				null, Utilities.clientIdNonRegistrato,	null,
				null, null, null, 
				Utilities.DIAGNOSTICO_NESSUN_APPLICATIVO_IDENTIFICATO);
	}
	@Test
	public void fruizioneSoloToken_clientIdNonRegistrato() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "pubblico",
				null, Utilities.clientIdNonRegistrato,	null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				Utilities.DIAGNOSTICO_NESSUN_APPLICATIVO_IDENTIFICATO);
	}
	
	@Test
	public void erogazioneSoloToken_clientIdRegistrato_applicativoEsterno() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "pubblico",
				Utilities.idApplicativoToken1SoggettoEsterno, Utilities.clientIdApplicativoToken1SoggettoEsterno, null,
				null, null, null, 
				null);
	}
	@Test
	public void fruizioneSoloToken_clientIdRegistrato_applicativoEsterno() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "pubblico",
				null, Utilities.clientIdApplicativoToken1SoggettoEsterno, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"soggetto proprietario ("+Utilities.idApplicativoToken1SoggettoEsterno.getIdSoggettoProprietario()+") dell'applicativo token identificato ("+Utilities.idApplicativoToken1SoggettoEsterno.getNome()+
					") differente dal soggetto proprietario della porta invocata ("+Utilities.soggettoInternoTestFruitore+")");
	}
	
	@Test
	public void erogazioneSoloToken_clientIdRegistrato_applicativoInternoSoggettoFruitore() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "pubblico",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				null, null, null, 
				null);
	}
	@Test
	public void fruizioneSoloToken_clientIdRegistrato_applicativoInternoSoggettoFruitore() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "pubblico",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore,	null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				null);
	}
	
	@Test
	public void erogazioneSoloToken_clientIdRegistrato_applicativoInternoSoggettoErogatore() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "pubblico",
				Utilities.idApplicativoToken1SoggettoInterno, Utilities.clientIdApplicativoToken1SoggettoInterno, null,
				null, null, null, 
				null);
	}
	@Test
	public void fruizioneSoloToken_clientIdRegistrato_applicativoInternoSoggettoErogatore() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "pubblico",
				null, Utilities.clientIdApplicativoToken1SoggettoInterno, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"soggetto proprietario ("+Utilities.idApplicativoToken1SoggettoInterno.getIdSoggettoProprietario()+") dell'applicativo token identificato ("+Utilities.idApplicativoToken1SoggettoInterno.getNome()+
					") differente dal soggetto proprietario della porta invocata ("+Utilities.soggettoInternoTestFruitore+")");
	}
	
	
	
	// ** puntuale **
	
	@Test
	public void erogazioneAuthzPuntuale_applicativoEsterno() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "puntuale",
				Utilities.idApplicativoToken1SoggettoEsterno, Utilities.clientIdApplicativoToken1SoggettoEsterno, null,
				null, null, null, 
				null);
	}
	@Test
	public void erogazioneAuthzPuntuale_applicativoInternoSoggettoFruitore() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "puntuale",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				null, null, null, 
				null);
	}
	@Test
	public void erogazioneAuthzPuntuale_applicativoInternoSoggettoErogatore() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "puntuale",
				Utilities.idApplicativoToken1SoggettoInterno, Utilities.clientIdApplicativoToken1SoggettoInterno, null,
				null, null, null, 
				null);
	}
	@Test
	public void fruizioneAuthzPuntuale_applicativoInternoSoggettoFruitore() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "puntuale",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				null);
	}
	
	@Test
	public void erogazioneAuthzPuntuale_clientIdNonRegistrato_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "puntuale",
				null, Utilities.clientIdNonRegistrato, null,
				null, null, null, 
				"Il mittente non è autorizzato ad invocare il servizio gw/"+API+" (versione:1) erogato da "+Utilities.soggettoInternoTest);
	}
	@Test
	public void erogazioneAuthzPuntuale_applicativoEsterno_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "puntuale",
				Utilities.idApplicativoToken2SoggettoEsterno, Utilities.clientIdApplicativoToken2SoggettoEsterno, null,
				null, null, null, 
				"L'applicativo "+Utilities.idApplicativoToken2SoggettoEsterno.getNome()+" (soggetto "+Utilities.idApplicativoToken2SoggettoEsterno.getIdSoggettoProprietario()+
					") non è autorizzato ad invocare il servizio gw/"+API+" (versione:1) erogato da "+Utilities.soggettoInternoTest);
		
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "puntuale",
				Utilities.idApplicativoToken3SoggettoEsterno, Utilities.clientIdApplicativoToken3SoggettoEsterno, null,
				null, null, null, 
				"L'applicativo "+Utilities.idApplicativoToken3SoggettoEsterno.getNome()+" (soggetto "+Utilities.idApplicativoToken3SoggettoEsterno.getIdSoggettoProprietario()+
					") non è autorizzato ad invocare il servizio gw/"+API+" (versione:1) erogato da "+Utilities.soggettoInternoTest);
	}
	@Test
	public void erogazioneAuthzPuntuale_applicativoInternoSoggettoFruitore_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "puntuale",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				null, null, null, 
				"L'applicativo "+Utilities.idApplicativoToken2SoggettoInternoFruitore.getNome()+" (soggetto "+Utilities.idApplicativoToken2SoggettoInternoFruitore.getIdSoggettoProprietario()+
					") non è autorizzato ad invocare il servizio gw/"+API+" (versione:1) erogato da "+Utilities.soggettoInternoTest);
		
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "puntuale",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, null,
				null, null, null, 
				"L'applicativo "+Utilities.idApplicativoToken3SoggettoInternoFruitore.getNome()+" (soggetto "+Utilities.idApplicativoToken3SoggettoInternoFruitore.getIdSoggettoProprietario()+
					") non è autorizzato ad invocare il servizio gw/"+API+" (versione:1) erogato da "+Utilities.soggettoInternoTest);
	}
	@Test
	public void erogazioneAuthzPuntuale_applicativoInternoSoggettoErogatore_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "puntuale",
				Utilities.idApplicativoToken2SoggettoInterno, Utilities.clientIdApplicativoToken2SoggettoInterno, null,
				null, null, null, 
				"L'applicativo "+Utilities.idApplicativoToken2SoggettoInterno.getNome()+" (soggetto "+Utilities.idApplicativoToken2SoggettoInterno.getIdSoggettoProprietario()+
					") non è autorizzato ad invocare il servizio gw/"+API+" (versione:1) erogato da "+Utilities.soggettoInternoTest);
		
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "puntuale",
				Utilities.idApplicativoToken3SoggettoInterno, Utilities.clientIdApplicativoToken3SoggettoInterno, null,
				null, null, null, 
				"L'applicativo "+Utilities.idApplicativoToken3SoggettoInterno.getNome()+" (soggetto "+Utilities.idApplicativoToken3SoggettoInterno.getIdSoggettoProprietario()+
					") non è autorizzato ad invocare il servizio gw/"+API+" (versione:1) erogato da "+Utilities.soggettoInternoTest);
	}
	@Test
	public void fruizioneAuthzPuntuale_clientIdNonRegistrato_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "puntuale",
				null, Utilities.clientIdNonRegistrato, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Il servizio applicativo Anonimo non risulta autorizzato a fruire del servizio richiesto");
	}
	@Test
	public void fruizioneAuthzPuntuale_applicativoInternoSoggettoFruitore_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "puntuale",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Il servizio applicativo "+Utilities.idApplicativoToken2SoggettoInternoFruitore.getNome()+" non risulta autorizzato a fruire del servizio richiesto");
		
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "puntuale",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Il servizio applicativo "+Utilities.idApplicativoToken3SoggettoInternoFruitore.getNome()+" non risulta autorizzato a fruire del servizio richiesto");
	}
	
	
	// ** ruoliAll **
	
	@Test
	public void erogazioneAuthzRuoliAll_applicativo1_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliAll",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				null, null, null, 
				"Role '"+Utilities.Ruolo1FonteQualsiasi+"' not found in application '"+Utilities.idApplicativoToken1SoggettoInternoFruitore.getNome()+"@"+Utilities.idApplicativoToken1SoggettoInternoFruitore.getIdSoggettoProprietario().getNome()+"' and in request context");
	}
	@Test
	public void fruizioneAuthzRuoliAll_applicativo1_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliAll",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Role '"+Utilities.Ruolo1FonteQualsiasi+"' not found in application '"+Utilities.idApplicativoToken1SoggettoInternoFruitore.getNome()+"@"+Utilities.idApplicativoToken1SoggettoInternoFruitore.getIdSoggettoProprietario().getNome()+"' and in request context");
	}
	
	@Test
	public void erogazioneAuthzRuoliAll_applicativo1_role3_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliAll",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, Utilities.listaRuolo3,
				null, null, null, 
				"Role '"+Utilities.Ruolo1FonteQualsiasi+"' not found in application '"+Utilities.idApplicativoToken1SoggettoInternoFruitore.getNome()+"@"+Utilities.idApplicativoToken1SoggettoInternoFruitore.getIdSoggettoProprietario().getNome()+"' and in request context");
	}
	@Test
	public void fruizioneAuthzRuoliAll_applicativo1_role3_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliAll",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, Utilities.listaRuolo3,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Role '"+Utilities.Ruolo1FonteQualsiasi+"' not found in application '"+Utilities.idApplicativoToken1SoggettoInternoFruitore.getNome()+"@"+Utilities.idApplicativoToken1SoggettoInternoFruitore.getIdSoggettoProprietario().getNome()+"' and in request context");
	}
	
	@Test
	public void erogazioneAuthzRuoliAll_applicativo2_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliAll",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				null, null, null, 
				"Role '"+Utilities.Ruolo3FonteEsterna_idGovWay+"' not found in application '"+Utilities.idApplicativoToken2SoggettoInternoFruitore.getNome()+"@"+Utilities.idApplicativoToken2SoggettoInternoFruitore.getIdSoggettoProprietario().getNome()+"' and in request context");
	}
	@Test
	public void fruizioneAuthzRuoliAll_applicativo2_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliAll",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Role '"+Utilities.Ruolo3FonteEsterna_idGovWay+"' not found in application '"+Utilities.idApplicativoToken2SoggettoInternoFruitore.getNome()+"@"+Utilities.idApplicativoToken2SoggettoInternoFruitore.getIdSoggettoProprietario().getNome()+"' and in request context");
	}
	
	@Test
	public void erogazioneAuthzRuoliAll_applicativo2_role3_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliAll",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, Utilities.listaRuolo3,
				null, null, null, 
				null);
	}
	@Test
	public void fruizioneAuthzRuoliAll_applicativo2_role3_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliAll",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, Utilities.listaRuolo3,
				Utilities.soggettoInternoTestFruitore, null, null, 
				null);
	}
	
	@Test
	public void erogazioneAuthzRuoliAll_applicativo3_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliAll",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, null,
				null, null, null, 
				"Role '"+Utilities.Ruolo1FonteQualsiasi+"' not found in application '"+Utilities.idApplicativoToken3SoggettoInternoFruitore.getNome()+"@"+Utilities.idApplicativoToken3SoggettoInternoFruitore.getIdSoggettoProprietario().getNome()+"' and in request context");
	}
	@Test
	public void fruizioneAuthzRuoliAll_applicativo3_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliAll",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Role '"+Utilities.Ruolo1FonteQualsiasi+"' not found in application '"+Utilities.idApplicativoToken3SoggettoInternoFruitore.getNome()+"@"+Utilities.idApplicativoToken3SoggettoInternoFruitore.getIdSoggettoProprietario().getNome()+"' and in request context");
	}
	
	@Test
	public void erogazioneAuthzRuoliAll_applicativo3_role3_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliAll",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, Utilities.listaRuolo3,
				null, null, null, 
				"Role '"+Utilities.Ruolo1FonteQualsiasi+"' not found in application '"+Utilities.idApplicativoToken3SoggettoInternoFruitore.getNome()+"@"+Utilities.idApplicativoToken3SoggettoInternoFruitore.getIdSoggettoProprietario().getNome()+"' and in request context");
	}
	@Test
	public void fruizioneAuthzRuoliAll_applicativo3_role3_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliAll",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, Utilities.listaRuolo3,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Role '"+Utilities.Ruolo1FonteQualsiasi+"' not found in application '"+Utilities.idApplicativoToken3SoggettoInternoFruitore.getNome()+"@"+Utilities.idApplicativoToken3SoggettoInternoFruitore.getIdSoggettoProprietario().getNome()+"' and in request context");
	}
	
	
	
	
	// ** ruoliAny **
	
	@Test
	public void erogazioneAuthzRuoliAny_applicativo1_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliAny",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				null, null, null, 
				"Roles not found in application '"+Utilities.idApplicativoToken1SoggettoInternoFruitore.getNome()+"@"+Utilities.idApplicativoToken1SoggettoInternoFruitore.getIdSoggettoProprietario().getNome()+"' and in request context");
	}
	@Test
	public void fruizioneAuthzRuoliAny_applicativo1_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliAny",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Roles not found in application '"+Utilities.idApplicativoToken1SoggettoInternoFruitore.getNome()+"@"+Utilities.idApplicativoToken1SoggettoInternoFruitore.getIdSoggettoProprietario().getNome()+"' and in request context");
	}
	
	@Test
	public void erogazioneAuthzRuoliAny_applicativo1_role3_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliAny",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, Utilities.listaRuolo3,
				null, null, null, 
				null);
	}
	@Test
	public void fruizioneAuthzRuoliAny_applicativo1_role3_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliAny",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, Utilities.listaRuolo3,
				Utilities.soggettoInternoTestFruitore, null, null, 
				null);
	}
	
	@Test
	public void erogazioneAuthzRuoliAny_applicativo2_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliAny",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				null, null, null, 
				null);
	}
	@Test
	public void fruizioneAuthzRuoliAny_applicativo2_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliAny",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				null);
	}
	
	@Test
	public void erogazioneAuthzRuoliAny_applicativo2_role3_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliAny",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, Utilities.listaRuolo3,
				null, null, null, 
				null);
	}
	@Test
	public void fruizioneAuthzRuoliAny_applicativo2_role3_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliAny",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, Utilities.listaRuolo3,
				Utilities.soggettoInternoTestFruitore, null, null, 
				null);
	}
	
	@Test
	public void erogazioneAuthzRuoliAny_applicativo3_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliAny",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, null,
				null, null, null, 
				null);
	}
	@Test
	public void fruizioneAuthzRuoliAny_applicativo3_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliAny",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				null);
	}
	
	@Test
	public void erogazioneAuthzRuoliAny_applicativo3_role3_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliAny",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, Utilities.listaRuolo3,
				null, null, null, 
				null);
	}
	@Test
	public void fruizioneAuthzRuoliAny_applicativo3_role3_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliAny",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, Utilities.listaRuolo3,
				Utilities.soggettoInternoTestFruitore, null, null, 
				null);
	}
	
	
	
	
	// ** ruoliRegistroAny **
	
	@Test
	public void erogazioneAuthzRuoliRegistroAny_applicativo1_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliRegistroAny",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				null, null, null, 
				"Roles not found in application '"+Utilities.idApplicativoToken1SoggettoInternoFruitore.getNome()+"@"+Utilities.idApplicativoToken1SoggettoInternoFruitore.getIdSoggettoProprietario().getNome());
	}
	@Test
	public void fruizioneAuthzRuoliRegistroAny_applicativo1_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliRegistroAny",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Roles not found in application '"+Utilities.idApplicativoToken1SoggettoInternoFruitore.getNome()+"@"+Utilities.idApplicativoToken1SoggettoInternoFruitore.getIdSoggettoProprietario().getNome());
	}
	
	@Test
	public void erogazioneAuthzRuoliRegistroAny_applicativo1_role3_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliRegistroAny",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, Utilities.listaRuolo3,
				null, null, null, 
				"Roles not found in application '"+Utilities.idApplicativoToken1SoggettoInternoFruitore.getNome()+"@"+Utilities.idApplicativoToken1SoggettoInternoFruitore.getIdSoggettoProprietario().getNome());
	}
	@Test
	public void fruizioneAuthzRuoliRegistroAny_applicativo1_role3_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliRegistroAny",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, Utilities.listaRuolo3,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Roles not found in application '"+Utilities.idApplicativoToken1SoggettoInternoFruitore.getNome()+"@"+Utilities.idApplicativoToken1SoggettoInternoFruitore.getIdSoggettoProprietario().getNome());
	}
	
	@Test
	public void erogazioneAuthzRuoliRegistroAny_applicativo2_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliRegistroAny",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				null, null, null, 
				null);
	}
	@Test
	public void fruizioneAuthzRuoliRegistroAny_applicativo2_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliRegistroAny",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				null);
	}
	
	@Test
	public void erogazioneAuthzRuoliRegistroAny_applicativo2_role3_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliRegistroAny",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, Utilities.listaRuolo3,
				null, null, null, 
				null);
	}
	@Test
	public void fruizioneAuthzRuoliRegistroAny_applicativo2_role3_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliRegistroAny",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, Utilities.listaRuolo3,
				Utilities.soggettoInternoTestFruitore, null, null, 
				null);
	}
	
	@Test
	public void erogazioneAuthzRuoliRegistroAny_applicativo3_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliRegistroAny",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, null,
				null, null, null, 
				null);
	}
	@Test
	public void fruizioneAuthzRuoliRegistroAny_applicativo3_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliRegistroAny",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				null);
	}
	
	@Test
	public void erogazioneAuthzRuoliRegistroAny_applicativo3_role3_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliRegistroAny",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, Utilities.listaRuolo3,
				null, null, null, 
				null);
	}
	@Test
	public void fruizioneAuthzRuoliRegistroAny_applicativo3_role3_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliRegistroAny",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, Utilities.listaRuolo3,
				Utilities.soggettoInternoTestFruitore, null, null, 
				null);
	}
	
	
	// ** ruoliEsterniAny **
	
	@Test
	public void erogazioneAuthzRuoliEsterniAny_applicativo1_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliEsterniAny",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				null, null, null, 
				"Roles not found in request context");
	}
	@Test
	public void fruizioneAuthzRuoliEsterniAny_applicativo1_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliEsterniAny",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Roles not found in request context");
	}
	
	@Test
	public void erogazioneAuthzRuoliEsterniAny_applicativo1_role3_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliEsterniAny",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, Utilities.listaRuolo3,
				null, null, null, 
				null);
	}
	@Test
	public void fruizioneAuthzRuoliEsterniAny_applicativo1_role3_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliEsterniAny",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, Utilities.listaRuolo3,
				Utilities.soggettoInternoTestFruitore, null, null, 
				null);
	}
	
	@Test
	public void erogazioneAuthzRuoliEsterniAny_applicativo2_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliEsterniAny",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				null, null, null, 
				"Roles not found in request context");
	}
	@Test
	public void fruizioneAuthzRuoliEsterniAny_applicativo2_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliEsterniAny",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Roles not found in request context");
	}
	
	@Test
	public void erogazioneAuthzRuoliEsterniAny_applicativo2_role3_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliEsterniAny",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, Utilities.listaRuolo3,
				null, null, null, 
				null);
	}
	@Test
	public void fruizioneAuthzRuoliEsterniAny_applicativo2_role3_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliEsterniAny",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, Utilities.listaRuolo3,
				Utilities.soggettoInternoTestFruitore, null, null, 
				null);
	}
	
	@Test
	public void erogazioneAuthzRuoliEsterniAny_applicativo3_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliEsterniAny",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, null,
				null, null, null, 
				"Roles not found in request context");
	}
	@Test
	public void fruizioneAuthzRuoliEsterniAny_applicativo3_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliEsterniAny",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Roles not found in request context");
	}
	
	@Test
	public void erogazioneAuthzRuoliEsterniAny_applicativo3_role3_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliEsterniAny",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, Utilities.listaRuolo3,
				null, null, null, 
				null);
	}
	@Test
	public void fruizioneAuthzRuoliEsterniAny_applicativo3_role3_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliEsterniAny",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, Utilities.listaRuolo3,
				Utilities.soggettoInternoTestFruitore, null, null, 
				null);
	}
	
	@Test
	public void erogazioneAuthzRuoliEsterniAny_clientIdNonRegistrato_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliEsterniAny",
				null, Utilities.clientIdNonRegistrato, null,
				null, null, null, 
				"Roles not found in request context");
	}
	@Test
	public void fruizioneAuthzRuoliEsterniAny_clientIdNonRegistrato_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliEsterniAny",
				null, Utilities.clientIdNonRegistrato, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Roles not found in request context");
	}
	
	@Test
	public void erogazioneAuthzRuoliEsterniAny_clientIdNonRegistrato_role3_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "ruoliEsterniAny",
				null, Utilities.clientIdNonRegistrato, Utilities.listaRuolo3,
				null, null, null, 
				null);
	}
	@Test
	public void fruizioneAuthzRuoliEsterniAny_clientIdNonRegistrato_role3_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "ruoliEsterniAny",
				null, Utilities.clientIdNonRegistrato, Utilities.listaRuolo3,
				Utilities.soggettoInternoTestFruitore, null, null, 
				null);
	}
	
	
	
	/* ** puntualeRuolo */
	
	@Test
	public void erogazioneAuthzPuntualeOrRuoli_applicativo1_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "puntualeRuolo",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				null, null, null, 
				null);
	}

	@Test
	public void fruizioneAuthzPuntualeOrRuoli_applicativo1_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "puntualeRuolo",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				null);
	}
	
	@Test
	public void erogazioneAuthzPuntualeOrRuoli_applicativo2_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "puntualeRuolo",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				null, null, null, 
				null);
	}

	@Test
	public void fruizioneAuthzPuntualeOrRuoli_applicativo2_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "puntualeRuolo",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				null);
	}
	
	@Test
	public void erogazioneAuthzPuntualeOrRuoli_applicativo3_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "puntualeRuolo",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, null,
				null, null, null, 
				"Roles not found in application '"+Utilities.idApplicativoToken3SoggettoInternoFruitore.getNome()+"@"+Utilities.idApplicativoToken3SoggettoInternoFruitore.getIdSoggettoProprietario().getNome()+"' and in request context");
	}

	@Test
	public void fruizioneAuthzPuntualeOrRuoli_applicativo3_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "puntualeRuolo",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Roles not found in application '"+Utilities.idApplicativoToken3SoggettoInternoFruitore.getNome()+"@"+Utilities.idApplicativoToken3SoggettoInternoFruitore.getIdSoggettoProprietario().getNome()+"' and in request context");
	}
	
	@Test
	public void erogazioneAuthzPuntualeOrRuoli_applicativo3_role3_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "puntualeRuolo",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, Utilities.listaRuolo3,
				null, null, null, 
				null);
	}

	@Test
	public void fruizioneAuthzPuntualeOrRuoli_applicativo3_role3_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "puntualeRuolo",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, Utilities.listaRuolo3,
				Utilities.soggettoInternoTestFruitore, null, null, 
				null);
	}
	
	@Test
	public void erogazioneAuthzPuntualeOrRuoli_clientIdNonRegistrato_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "puntualeRuolo",
				null, Utilities.clientIdNonRegistrato, null,
				null, null, null, 
				"Roles not found in request context");
	}
	@Test
	public void fruizioneAuthzPuntualeOrRuoli_clientIdNonRegistrato_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "puntualeRuolo",
				null, Utilities.clientIdNonRegistrato, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Roles not found in request context");
	}
	
	@Test
	public void erogazioneAuthzPuntualeOrRuoli_clientIdNonRegistrato_role3_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "puntualeRuolo",
				null, Utilities.clientIdNonRegistrato, Utilities.listaRuolo3,
				null, null, null, 
				null);
	}
	@Test
	public void fruizioneAuthzPuntualeOrRuoli_clientIdNonRegistrato_role3_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "puntualeRuolo",
				null, Utilities.clientIdNonRegistrato, Utilities.listaRuolo3,
				Utilities.soggettoInternoTestFruitore, null, null, 
				null);
	}
	
	
	
	/* ** puntualeRuoloTokenTrasporto */
	
	@Test
	public void erogazioneAuthzPuntualeOrRuoliTokenTrasporto_trasportoApplicativo1_tokenApplicativo1_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "puntualeRuoloTokenTrasporto",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				null, Utilities.idApplicativoTrasporto1SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto1SoggettoInternoFruitore, 
				null);
	}

	@Test
	public void fruizioneAuthzPuntualeOrRuoliTokenTrasporto_trasportoApplicativo1_tokenApplicativo1_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "puntualeRuoloTokenTrasporto",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, Utilities.idApplicativoTrasporto1SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto1SoggettoInternoFruitore, 
				null);
	}
	
	@Test
	public void erogazioneAuthzPuntualeOrRuoliTokenTrasporto_trasportoApplicativo4_tokenApplicativo1_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "puntualeRuoloTokenTrasporto",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				null, Utilities.idApplicativoTrasporto4SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto4SoggettoInternoFruitore, 
				"L'applicativo "+Utilities.idApplicativoTrasporto4SoggettoInternoFruitore.getNome()+" (soggetto "+Utilities.idApplicativoTrasporto4SoggettoInternoFruitore.getIdSoggettoProprietario()+
					") non è autorizzato ad invocare il servizio gw/"+API+" (versione:1) erogato da "+Utilities.soggettoInternoTest);
	}

	@Test
	public void fruizioneAuthzPuntualeOrRuoliTokenTrasporto_trasportoApplicativo4_tokenApplicativo1_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "puntualeRuoloTokenTrasporto",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, Utilities.idApplicativoTrasporto4SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto4SoggettoInternoFruitore, 
				"Il servizio applicativo "+Utilities.idApplicativoTrasporto4SoggettoInternoFruitore.getNome()+" non risulta autorizzato a fruire del servizio richiesto");
	}
	
	
	@Test
	public void erogazioneAuthzPuntualeOrRuoliTokenTrasporto_trasportoApplicativo2_tokenApplicativo2_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "puntualeRuoloTokenTrasporto",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				null,  Utilities.idApplicativoTrasporto2SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto2SoggettoInternoFruitore, 
				null);
	}

	@Test
	public void fruizioneAuthzPuntualeOrRuoliTokenTrasporto_trasportoApplicativo2_tokenApplicativo2_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "puntualeRuoloTokenTrasporto",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, Utilities.idApplicativoTrasporto2SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto2SoggettoInternoFruitore, 
				null);
	}
	
	@Test
	public void erogazioneAuthzPuntualeOrRuoliTokenTrasporto_trasportoApplicativo2_applicativo3_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "puntualeRuoloTokenTrasporto",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, null,
				null, Utilities.idApplicativoTrasporto2SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto2SoggettoInternoFruitore, 
				"Roles not found in application '"+Utilities.idApplicativoToken3SoggettoInternoFruitore.getNome()+"@"+Utilities.idApplicativoToken3SoggettoInternoFruitore.getIdSoggettoProprietario().getNome()+"' and in request context");
	}

	@Test
	public void fruizioneAuthzPuntualeOrRuoliTokenTrasporto_trasportoApplicativo2_applicativo3_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "puntualeRuoloTokenTrasporto",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, Utilities.idApplicativoTrasporto2SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto2SoggettoInternoFruitore, 
				"Roles not found in application '"+Utilities.idApplicativoToken3SoggettoInternoFruitore.getNome()+"@"+Utilities.idApplicativoToken3SoggettoInternoFruitore.getIdSoggettoProprietario().getNome()+"' and in request context");
	}
	
	@Test
	public void erogazioneAuthzPuntualeOrRuoliTokenTrasporto_trasportoApplicativo3_tokenApplicativo2_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "puntualeRuoloTokenTrasporto",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				null, Utilities.idApplicativoTrasporto3SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto3SoggettoInternoFruitore, 
				"L'applicativo "+Utilities.idApplicativoTrasporto3SoggettoInternoFruitore.getNome()+" (soggetto "+Utilities.idApplicativoTrasporto3SoggettoInternoFruitore.getIdSoggettoProprietario()+
					") non è autorizzato ad invocare il servizio gw/"+API+" (versione:1) erogato da "+Utilities.soggettoInternoTest);
	}

	@Test
	public void fruizioneAuthzPuntualeOrRuoliTokenTrasporto_trasportoApplicativo3_tokenApplicativo2_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "puntualeRuoloTokenTrasporto",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, Utilities.idApplicativoTrasporto3SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto3SoggettoInternoFruitore, 
				"Il servizio applicativo "+Utilities.idApplicativoTrasporto3SoggettoInternoFruitore.getNome()+" non risulta autorizzato a fruire del servizio richiesto");
	}
	
	@Test
	public void erogazioneAuthzPuntualeOrRuoliTokenTrasporto_trasportoApplicativo3_tokenApplicativo3_role3_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "puntualeRuoloTokenTrasporto",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, Utilities.listaRuolo3,
				null, Utilities.idApplicativoTrasporto3SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto3SoggettoInternoFruitore, 
				null);
	}

	@Test
	public void fruizioneAuthzPuntualeOrRuoliTokenTrasporto_trasportoApplicativo3_tokenApplicativo3_role3_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "puntualeRuoloTokenTrasporto",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, Utilities.listaRuolo3,
				Utilities.soggettoInternoTestFruitore, Utilities.idApplicativoTrasporto3SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto3SoggettoInternoFruitore, 
				null);
	}

	@Test
	public void erogazioneAuthzPuntualeOrRuoliTokenTrasporto_trasportoApplicativo3_clientIdNonRegistrato_role3_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "puntualeRuoloTokenTrasporto",
				null, Utilities.clientIdNonRegistrato, Utilities.listaRuolo3,
				null, Utilities.idApplicativoTrasporto3SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto3SoggettoInternoFruitore, 
				null);
	}
	@Test
	public void fruizioneAuthzPuntualeOrRuoliTokenTrasporto_trasportoApplicativo3_clientIdNonRegistrato_role3_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "puntualeRuoloTokenTrasporto",
				null, Utilities.clientIdNonRegistrato, Utilities.listaRuolo3,
				Utilities.soggettoInternoTestFruitore, Utilities.idApplicativoTrasporto3SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto3SoggettoInternoFruitore, 
				null);
	}
	
	
	
	
	/* ** xacmlPolicy */
	
	@Test
	public void erogazioneAuthzXacmlPolicy_applicativo1_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "xacmlPolicy",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				null, null, null, 
				null);
	}

	@Test
	public void fruizioneAuthzXacmlPolicy_applicativo1_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "xacmlPolicy",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				null);
	}
	
	@Test
	public void erogazioneAuthzXacmlPolicy_applicativo2_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "xacmlPolicy",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				null, null, null, 
				"Il mittente non è autorizzato ad invocare il servizio gw/"+API+" (versione:1) erogato da "+Utilities.soggettoInternoTest+" (result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok)");
	}

	@Test
	public void fruizioneAuthzXacmlPolicy_applicativo2_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "xacmlPolicy",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Il servizio applicativo Anonimo non risulta autorizzato a fruire del servizio richiesto: result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok");
	}
	
	@Test
	public void erogazioneAuthzXacmlPolicy_clientIdNonRegistrato_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "xacmlPolicy",
				null, Utilities.clientIdNonRegistrato, null,
				null, null, null, 
				"Il mittente non è autorizzato ad invocare il servizio gw/"+API+" (versione:1) erogato da "+Utilities.soggettoInternoTest+" (result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok)");
	}

	@Test
	public void fruizioneAuthzXacmlPolicy_clientIdNonRegistrato_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "xacmlPolicy",
				null, Utilities.clientIdNonRegistrato, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Il servizio applicativo Anonimo non risulta autorizzato a fruire del servizio richiesto: result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok");
	}
	
	@Test
	public void erogazioneAuthzXacmlPolicy_clientIdNonRegistrato_role3_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "xacmlPolicy",
				null, Utilities.clientIdNonRegistrato, Utilities.listaRuolo3,
				null, null, null, 
				null);
	}

	@Test
	public void fruizioneAuthzXacmlPolicy_clientIdNonRegistrato_role3_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "xacmlPolicy",
				null, Utilities.clientIdNonRegistrato, Utilities.listaRuolo3,
				Utilities.soggettoInternoTestFruitore, null, null, 
				null);
	}
	
	
	
	
	/* ** xacmlPolicy_ruoliEsterni */
	
	@Test
	public void erogazioneAuthzXacmlPolicy_ruoliEsterni_applicativo1_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "xacmlPolicy_ruoliEsterni",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				null, null, null, 
				"Il mittente non è autorizzato ad invocare il servizio gw/"+API+" (versione:1) erogato da "+Utilities.soggettoInternoTest+" (result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok)");
	}

	@Test
	public void fruizioneAuthzXacmlPolicy_ruoliEsterni_applicativo1_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "xacmlPolicy_ruoliEsterni",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Il servizio applicativo Anonimo non risulta autorizzato a fruire del servizio richiesto: result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok");
	}
	
	@Test
	public void erogazioneAuthzXacmlPolicy_ruoliEsterni_applicativo2_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "xacmlPolicy_ruoliEsterni",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				null, null, null, 
				"Il mittente non è autorizzato ad invocare il servizio gw/"+API+" (versione:1) erogato da "+Utilities.soggettoInternoTest+" (result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok)");
	}

	@Test
	public void fruizioneAuthzXacmlPolicy_ruoliEsterni_applicativo2_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "xacmlPolicy_ruoliEsterni",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Il servizio applicativo Anonimo non risulta autorizzato a fruire del servizio richiesto: result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok");
	}
	
	@Test
	public void erogazioneAuthzXacmlPolicy_ruoliEsterni_clientIdNonRegistrato_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "xacmlPolicy_ruoliEsterni",
				null, Utilities.clientIdNonRegistrato, null,
				null, null, null, 
				"Il mittente non è autorizzato ad invocare il servizio gw/"+API+" (versione:1) erogato da "+Utilities.soggettoInternoTest+" (result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok)");
	}

	@Test
	public void fruizioneAuthzXacmlPolicy_ruoliEsterni_clientIdNonRegistrato_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "xacmlPolicy_ruoliEsterni",
				null, Utilities.clientIdNonRegistrato, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Il servizio applicativo Anonimo non risulta autorizzato a fruire del servizio richiesto: result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok");
	}
	
	@Test
	public void erogazioneAuthzXacmlPolicy_ruoliEsterni_clientIdNonRegistrato_role3_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "xacmlPolicy_ruoliEsterni",
				null, Utilities.clientIdNonRegistrato, Utilities.listaRuolo3,
				null, null, null, 
				null);
	}

	@Test
	public void fruizioneAuthzXacmlPolicy_ruoliEsterni_clientIdNonRegistrato_role3_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "xacmlPolicy_ruoliEsterni",
				null, Utilities.clientIdNonRegistrato, Utilities.listaRuolo3,
				Utilities.soggettoInternoTestFruitore, null, null, 
				null);
	}
	
	
	
	
	/* ** xacmlPolicy_ruoliRegistro */
	
	@Test
	public void erogazioneAuthzXacmlPolicy_ruoliRegistro_applicativo1_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "xacmlPolicy_ruoliRegistro",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				null, null, null, 
				null);
	}

	@Test
	public void fruizioneAuthzXacmlPolicy_ruoliRegistro_applicativo1_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "xacmlPolicy_ruoliRegistro",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				null);
	}
	
	@Test
	public void erogazioneAuthzXacmlPolicy_ruoliRegistro_applicativo2_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "xacmlPolicy_ruoliRegistro",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				null, null, null, 
				"Il mittente non è autorizzato ad invocare il servizio gw/"+API+" (versione:1) erogato da "+Utilities.soggettoInternoTest+" (result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok)");
	}

	@Test
	public void fruizioneAuthzXacmlPolicy_ruoliRegistro_applicativo2_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "xacmlPolicy_ruoliRegistro",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Il servizio applicativo Anonimo non risulta autorizzato a fruire del servizio richiesto: result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok");
	}
	
	@Test
	public void erogazioneAuthzXacmlPolicy_ruoliRegistro_clientIdNonRegistrato_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "xacmlPolicy_ruoliRegistro",
				null, Utilities.clientIdNonRegistrato, null,
				null, null, null, 
				"Il mittente non è autorizzato ad invocare il servizio gw/"+API+" (versione:1) erogato da "+Utilities.soggettoInternoTest+" (result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok)");
	}

	@Test
	public void fruizioneAuthzXacmlPolicy_ruoliRegistro_clientIdNonRegistrato_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "xacmlPolicy_ruoliRegistro",
				null, Utilities.clientIdNonRegistrato, null,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Il servizio applicativo Anonimo non risulta autorizzato a fruire del servizio richiesto: result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok");
	}
	
	@Test
	public void erogazioneAuthzXacmlPolicy_ruoliRegistro_clientIdNonRegistrato_role3_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "xacmlPolicy_ruoliRegistro",
				null, Utilities.clientIdNonRegistrato, Utilities.listaRuolo3,
				null, null, null, 
				"Il mittente non è autorizzato ad invocare il servizio gw/"+API+" (versione:1) erogato da "+Utilities.soggettoInternoTest+" (result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok)");
	}

	@Test
	public void fruizioneAuthzXacmlPolicy_ruoliRegistro_clientIdNonRegistrato_role3_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "xacmlPolicy_ruoliRegistro",
				null, Utilities.clientIdNonRegistrato, Utilities.listaRuolo3,
				Utilities.soggettoInternoTestFruitore, null, null, 
				"Il servizio applicativo Anonimo non risulta autorizzato a fruire del servizio richiesto: result-1 DENY code:urn:oasis:names:tc:xacml:1.0:status:ok");
	}
	
	
	
	/* ** authzContenuti */
	
	@Test
	public void erogazioneAuthzContenuti_applicativo1_success() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "authzContenuti",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				null, Utilities.idApplicativoTrasporto1SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto1SoggettoInternoFruitore,  
				null);
		
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "authzContenuti",
				Utilities.idApplicativoToken1SoggettoInterno, Utilities.clientIdApplicativoToken1SoggettoInterno, null,
				null, Utilities.idApplicativoTrasporto1SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto1SoggettoInternoFruitore,  
				null);
		
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "authzContenuti",
				Utilities.idApplicativoToken1SoggettoEsterno, Utilities.clientIdApplicativoToken1SoggettoEsterno, null,
				null, Utilities.idApplicativoTrasporto1SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto1SoggettoInternoFruitore,  
				null);
	}

	@Test
	public void fruizioneAuthzContenuti_applicativo1_success() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "authzContenuti",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, Utilities.idApplicativoTrasporto1SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto1SoggettoInternoFruitore,  
				null);
	}
	
	@Test
	public void erogazioneAuthzContenuti_applicativo2_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "authzContenuti",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				null, Utilities.idApplicativoTrasporto1SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto1SoggettoInternoFruitore,  
				//org.openspcoop2.core.protocolli.trasparente.testsuite.Utils.isJenkins() ?
						"Resource '${tokenClient:nome}' with unexpected value '"+Utilities.idApplicativoToken2SoggettoInternoFruitore.getNome()+"'"
				//		:
				//		"(Resource '${tokenClientApplicationConfig:authzContenutiTest}' not verifiable; unprocessable dynamic value 'true': Proprieta' '${tokenClientApplicationConfig:authzContenutiTest}' contiene un valore non corretto: Placeholder [{tokenClientApplicationConfig:authzContenutiTest}] resolution failed: object [java.util.HashMap] 'authzContenutiTest' not exists in map)"
				);
	}

	@Test
	public void fruizioneAuthzContenuti_applicativo2_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "authzContenuti",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, Utilities.idApplicativoTrasporto1SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto1SoggettoInternoFruitore,  
				"Resource '${tokenClient:nome}' with unexpected value '"+Utilities.idApplicativoToken2SoggettoInternoFruitore.getNome()+"'"
				//"(Resource '${tokenClientApplicationConfig:authzContenutiTest}' not verifiable; unprocessable dynamic value 'true': Proprieta' '${tokenClientApplicationConfig:authzContenutiTest}' contiene un valore non corretto: Placeholder [{tokenClientApplicationConfig:authzContenutiTest}] resolution failed: object [java.util.HashMap] 'authzContenutiTest' not exists in map)"
				);
	}
	
	@Test
	public void erogazioneAuthzContenuti_applicativo3_deny() throws Exception {
		Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "authzContenuti",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, null,
				null, Utilities.idApplicativoTrasporto1SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto1SoggettoInternoFruitore,  
				"(Resource '${tokenClient:nome}' with unexpected value '"+Utilities.idApplicativoToken3SoggettoInternoFruitore.getNome()+"')");
	}

	@Test
	public void fruizioneAuthzContenuti_applicativo3_deny() throws Exception {
		Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "authzContenuti",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, Utilities.idApplicativoTrasporto1SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto1SoggettoInternoFruitore,  
				"(Resource '${tokenClient:nome}' with unexpected value '"+Utilities.idApplicativoToken3SoggettoInternoFruitore.getNome()+"')");
	}
	
	
	
	/* ** trasformazione */
	
	@Test
	public void erogazioneTrasformazione_applicativo1_success() throws Exception {
		HttpResponse resp = Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "trasformazione",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				null, null, null,  
				null);
		Utilities.validateResponseHeaderTrasformazioni(resp, true, Utilities.idApplicativoToken1SoggettoInternoFruitore.getNome());
	}

	@Test
	public void erogazioneTrasformazione_applicativo1_esterno_success() throws Exception {
		// match grazie al soggetto
		HttpResponse resp = Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "trasformazione",
				Utilities.idApplicativoToken1SoggettoEsterno, Utilities.clientIdApplicativoToken1SoggettoEsterno, null,
				null, null, null,  
				null);
		Utilities.validateResponseHeaderTrasformazioni(resp, true, Utilities.idApplicativoToken1SoggettoEsterno.getNome());
	}

	@Test
	public void fruizioneTrasformazione_applicativo1_success() throws Exception {
		HttpResponse resp = Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "trasformazione",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null,  
				null);
		Utilities.validateResponseHeaderTrasformazioni(resp, true, Utilities.idApplicativoToken1SoggettoInternoFruitore.getNome());
	}
	
	@Test
	public void erogazioneTrasformazione_applicativo2_deny() throws Exception {
		// non soddisfa i criteri di applicabilità
		HttpResponse resp = Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "trasformazione",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				null, null, null,  
				null);
		Utilities.validateResponseHeaderTrasformazioni(resp, false, null);
	}

	@Test
	public void erogazioneTrasformazione_applicativo2_esterno_success() throws Exception {
		// match grazie al soggetto
		HttpResponse resp = Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "trasformazione",
				Utilities.idApplicativoToken2SoggettoEsterno, Utilities.clientIdApplicativoToken2SoggettoEsterno, null,
				null, null, null,  
				null);
		Utilities.validateResponseHeaderTrasformazioni(resp, true, Utilities.idApplicativoToken2SoggettoEsterno.getNome());
	}
	
	@Test
	public void fruizioneTrasformazione_applicativo2_deny() throws Exception {
		// non soddisfa i criteri di applicabilità
		HttpResponse resp = Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "trasformazione",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null,  
				null);
		Utilities.validateResponseHeaderTrasformazioni(resp, false, null);
	}
	
	
	
	/* ** trasformazioneTrasporto */
	
	@Test
	public void erogazioneTrasformazioneTrasporto_applicativo1_success() throws Exception {
		// match sull'applicativo token
		HttpResponse resp = Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "trasformazioneTrasporto",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				null, Utilities.idApplicativoTrasporto1SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto1SoggettoInternoFruitore,  
				null);
		Utilities.validateResponseHeaderTrasformazioni(resp, true, Utilities.idApplicativoToken1SoggettoInternoFruitore.getNome());
	}

	@Test
	public void erogazioneTrasformazioneTrasporto_applicativo1_esterno_success() throws Exception {
		// match grazie al soggetto token
		HttpResponse resp = Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "trasformazioneTrasporto",
				Utilities.idApplicativoToken1SoggettoEsterno, Utilities.clientIdApplicativoToken1SoggettoEsterno, null,
				null, Utilities.idApplicativoTrasporto1SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto1SoggettoInternoFruitore,  
				null);
		Utilities.validateResponseHeaderTrasformazioni(resp, true, Utilities.idApplicativoToken1SoggettoEsterno.getNome());
	}

	@Test
	public void fruizioneTrasformazioneTrasporto_applicativo1_success() throws Exception {
		// match sull'applicativo token
		HttpResponse resp = Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "trasformazioneTrasporto",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, Utilities.idApplicativoTrasporto1SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto1SoggettoInternoFruitore,  
				null);
		Utilities.validateResponseHeaderTrasformazioni(resp, true, Utilities.idApplicativoToken1SoggettoInternoFruitore.getNome());
	}
	
	@Test
	public void erogazioneTrasformazioneTrasporto_applicativo2_deny() throws Exception {
		// non soddisfa i criteri di applicabilità
		HttpResponse resp = Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "trasformazioneTrasporto",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				null, Utilities.idApplicativoTrasporto1SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto1SoggettoInternoFruitore,  
				null);
		Utilities.validateResponseHeaderTrasformazioni(resp, false, null);
	}

	@Test
	public void erogazioneTrasformazioneTrasporto_applicativo2_esterno_success() throws Exception {
		// match grazie al soggetto token
		HttpResponse resp = Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "trasformazioneTrasporto",
				Utilities.idApplicativoToken2SoggettoEsterno, Utilities.clientIdApplicativoToken2SoggettoEsterno, null,
				null, Utilities.idApplicativoTrasporto1SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto1SoggettoInternoFruitore,  
				null);
		Utilities.validateResponseHeaderTrasformazioni(resp, true, Utilities.idApplicativoToken2SoggettoEsterno.getNome());
	}
	
	@Test
	public void fruizioneTrasformazioneTrasporto_applicativo2_deny() throws Exception {
		// non soddisfa i criteri di applicabilità
		HttpResponse resp = Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "trasformazioneTrasporto",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, Utilities.idApplicativoTrasporto1SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto1SoggettoInternoFruitore,  
				null);
		Utilities.validateResponseHeaderTrasformazioni(resp, false, null);
	}
	
	@Test
	public void erogazioneTrasformazioneTrasporto_applicativo3Token_applicativoTrasporto4_success() throws Exception {
		// match sull'applicativo trasporto
		HttpResponse resp = Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "trasformazioneTrasporto",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, null,
				null, Utilities.idApplicativoTrasporto4SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto4SoggettoInternoFruitore,  
				null);
		Utilities.validateResponseHeaderTrasformazioni(resp, true, Utilities.idApplicativoToken3SoggettoInternoFruitore.getNome());
	}

	@Test
	public void erogazioneTrasformazioneTrasporto_applicativo3Token_soggettoTrasporto_success() throws Exception {
		// match grazie al soggetto trasporto
		HttpResponse resp = Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "trasformazioneTrasporto",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, null,
				null, Utilities.idSoggettoTrasportoFruitore, Utilities.basicUsernameSoggettoTrasportoFruitore,  
				null);
		Utilities.validateResponseHeaderTrasformazioni(resp, true, Utilities.idApplicativoToken3SoggettoInternoFruitore.getNome());
	}

	@Test
	public void fruizioneTrasformazioneTrasporto_applicativo3Token_applicativoTrasporto4_success() throws Exception {
		// match sull'applicativo trasporto
		HttpResponse resp = Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "trasformazioneTrasporto",
				Utilities.idApplicativoToken3SoggettoInternoFruitore, Utilities.clientIdApplicativoToken3SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, Utilities.idApplicativoTrasporto4SoggettoInternoFruitore, Utilities.basicUsernameApplicativoTrasporto4SoggettoInternoFruitore,  
				null);
		Utilities.validateResponseHeaderTrasformazioni(resp, true, Utilities.idApplicativoToken3SoggettoInternoFruitore.getNome());
	}

	
	
	
	/* ** integrazione */
	
	@Test
	public void erogazioneIntegrazione_applicativo1() throws Exception {
		HttpResponse resp = Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "integrazione",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				null, null, null,  
				null);
		Utilities.validateResponseHeaderIntegrazione(resp, Utilities.idApplicativoToken1SoggettoInternoFruitore);
	}

	@Test
	public void fruizioneIntegrazione_applicativo1() throws Exception {
		HttpResponse resp = Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "integrazione",
				Utilities.idApplicativoToken1SoggettoInternoFruitore, Utilities.clientIdApplicativoToken1SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null,  
				null);
		Utilities.validateResponseHeaderIntegrazione(resp, Utilities.idApplicativoToken1SoggettoInternoFruitore);
	}
	@Test
	public void erogazioneIntegrazione_applicativo2() throws Exception {
		HttpResponse resp = Utilities._test(TipoServizio.EROGAZIONE, ConfigLoader.logCore, API, "integrazione",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				null, null, null,  
				null);
		Utilities.validateResponseHeaderIntegrazione(resp, Utilities.idApplicativoToken2SoggettoInternoFruitore);
	}

	@Test
	public void fruizioneIntegrazione_applicativo2() throws Exception {
		HttpResponse resp = Utilities._test(TipoServizio.FRUIZIONE, ConfigLoader.logCore, API, "integrazione",
				Utilities.idApplicativoToken2SoggettoInternoFruitore, Utilities.clientIdApplicativoToken2SoggettoInternoFruitore, null,
				Utilities.soggettoInternoTestFruitore, null, null,  
				null);
		Utilities.validateResponseHeaderIntegrazione(resp, Utilities.idApplicativoToken2SoggettoInternoFruitore);
	}
}
