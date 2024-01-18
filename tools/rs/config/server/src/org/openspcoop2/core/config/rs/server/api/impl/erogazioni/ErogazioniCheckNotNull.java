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
package org.openspcoop2.core.config.rs.server.api.impl.erogazioni;

import org.openspcoop2.core.config.rs.server.model.ApiImplAllegatoSpecificaLivelloServizio;
import org.openspcoop2.core.config.rs.server.model.ApiImplAllegatoSpecificaSemiformale;
import org.openspcoop2.core.config.rs.server.model.ApiImplAllegatoSpecificaSicurezza;
import org.openspcoop2.core.config.rs.server.model.ConnettoreConfigurazioneHttpsClient;
import org.openspcoop2.core.config.rs.server.model.GruppoNuovaConfigurazione;
import org.openspcoop2.core.config.rs.server.model.TipoSpecificaLivelloServizioEnum;
import org.openspcoop2.core.config.rs.server.model.TipoSpecificaSicurezzaEnum;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.constants.TipiDocumentoLivelloServizio;
import org.openspcoop2.core.registry.constants.TipiDocumentoSemiformale;
import org.openspcoop2.core.registry.constants.TipiDocumentoSicurezza;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;

/**
 * ErogazioniCheckNotNull
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ErogazioniCheckNotNull {

	public static String getHttpskeystore(ConnettoreConfigurazioneHttpsClient httpsClient) {
		String httpskeystore = null;
		if ( httpsClient != null ) {
			if ( httpsClient.getKeystorePath() != null || httpsClient.getKeystoreTipo() != null ) {
				httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI;  
			}
			else
				httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
		}
		return httpskeystore;
	}
	
	public static void checkAutenticazione(GruppoNuovaConfigurazione conf) {
		if (conf.getAutenticazione() == null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(GruppoNuovaConfigurazione.class.getName()+": Indicare il campo obbligatorio 'autenticazione'");
		}
	}
	
	public static boolean isNotNullTipoSpecifica(ApiImplAllegatoSpecificaSemiformale allegatoSS, Documento documento) {
		if(allegatoSS.getTipoSpecifica()==null) {
			documento.setTipo(TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString()); // default
			return false;
		}
		return true;
	}
	
	public static boolean isNotNullTipoSpecifica(ApiImplAllegatoSpecificaLivelloServizio allegatoLS, Documento documento) {
		if(allegatoLS.getTipoSpecifica()==null) {
			documento.setTipo(TipiDocumentoLivelloServizio.WSLA.toString()); // default
			return false;
		}
		return true;
	}
	
	public static boolean isNotNullTipoSpecifica(ApiImplAllegatoSpecificaSicurezza allegatoLS, Documento documento) {
		if(allegatoLS.getTipoSpecifica()==null) {
			documento.setTipo(TipiDocumentoSicurezza.LINGUAGGIO_NATURALE.toString()); // default
			return false;
		}
		return true;
	}
	
	public static void documentoToImplAllegato(ApiImplAllegatoSpecificaLivelloServizio allegatoSL, TipiDocumentoLivelloServizio tipo) {
		if(allegatoSL.getTipoSpecifica()==null) {
			if(TipiDocumentoLivelloServizio.WSAGREEMENT.equals(tipo)) {
				allegatoSL.setTipoSpecifica(TipoSpecificaLivelloServizioEnum.WS_AGREEMENT);
			}
		}
	}
	
	public static void documentoToImplAllegato(ApiImplAllegatoSpecificaSicurezza allegatoSSec, TipiDocumentoSicurezza tipo) {
		if(allegatoSSec.getTipoSpecifica()==null) {
			if(TipiDocumentoSicurezza.WSPOLICY.equals(tipo)) {
				allegatoSSec.setTipoSpecifica(TipoSpecificaSicurezzaEnum.WS_POLICY);
			}
			else if(TipiDocumentoSicurezza.XACML_POLICY.equals(tipo)) {
				allegatoSSec.setTipoSpecifica(TipoSpecificaSicurezzaEnum.XACML_POLICY);
			}
		}
	}
}
