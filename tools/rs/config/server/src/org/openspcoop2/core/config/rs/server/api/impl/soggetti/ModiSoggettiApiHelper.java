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

package org.openspcoop2.core.config.rs.server.api.impl.soggetti;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.rs.server.api.impl.ProtocolPropertiesHelper;
import org.openspcoop2.core.config.rs.server.model.DominioEnum;
import org.openspcoop2.core.config.rs.server.model.ModISoggetto;
import org.openspcoop2.core.config.rs.server.model.ModISoggettoPDND;
import org.openspcoop2.core.config.rs.server.model.TracciamentoPDNDSoggettoEnum;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.properties.AbstractProperty;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.utils.UtilsException;

/**
 * ModiApplicativiApiHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ModiSoggettiApiHelper {
	
	private ModiSoggettiApiHelper() {}

	public static ProtocolProperties getProtocolProperties(org.openspcoop2.core.config.rs.server.model.Soggetto body) throws ProtocolException {
		/**		if(body.getModi() == null) {
		//			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare la configurazione 'ModI'");
		//		}*/

		ProtocolProperties p = new ProtocolProperties();

		if(body.getModi() != null && body.getModi().getPdnd()!=null ) {
			if(body.getModi().getPdnd().getIdEnte()!=null && StringUtils.isNotEmpty(body.getModi().getPdnd().getIdEnte())) {
				p.addProperty(CostantiDB.MODIPA_SOGGETTI_ID_ENTE_ID, body.getModi().getPdnd().getIdEnte());
			}
			
			TracciamentoPDNDSoggettoEnum tracciamentoPdnd = body.getModi().getPdnd().getTracciamentoPdnd();
			if (tracciamentoPdnd != null) {
				
				if (DominioEnum.INTERNO.equals(body.getDominio())) {
					p.addProperty(CostantiDB.MODIPA_SOGGETTI_PDND_TRACING_ID, toTracciamentoPDNDSoggettoId(tracciamentoPdnd));
				} else {
					throw new ProtocolException("Il campo tracing_pdnd è valido solo per soggetti con dominio interno");
				}
			
			}
		}
		

		return p;

	}
	


	public static void initializePdnd(org.openspcoop2.core.config.rs.server.model.Soggetto ret) {
		if(ret.getModi()==null) {
			ret.setModi(new ModISoggetto());
		}
		if(ret.getModi().getPdnd()==null) {
			ret.getModi().setPdnd(new  ModISoggettoPDND());
		}
	}
	
	public static void populateProtocolInfo(Soggetto soggetto, SoggettiEnv env, org.openspcoop2.core.config.rs.server.model.Soggetto ret) throws CoreException, UtilsException, ProtocolException, DriverConfigurazioneException {

		Map<String, AbstractProperty<?>> p = SoggettiApiHelper.getProtocolPropertiesMap(soggetto, env);
		String idEnte = ProtocolPropertiesHelper.getStringProperty(p, CostantiDB.MODIPA_SOGGETTI_ID_ENTE_ID, false);
		if(idEnte != null && StringUtils.isNotEmpty(idEnte)) {
			initializePdnd(ret);
			ret.getModi().getPdnd().setIdEnte(idEnte);
		}
		
		String tracciamentoPdnd = ProtocolPropertiesHelper.getStringProperty(p, CostantiDB.MODIPA_SOGGETTI_PDND_TRACING_ID, false);
		if (tracciamentoPdnd != null && DominioEnum.INTERNO.equals(ret.getDominio())) {
			initializePdnd(ret);
			ret.getModi().getPdnd().setTracciamentoPdnd(toTracciamentoPDNDSoggettoEnum(tracciamentoPdnd));
		}
		
	}
	
	private static TracciamentoPDNDSoggettoEnum toTracciamentoPDNDSoggettoEnum(String idValue) {
		if (CostantiDB.MODIPA_SOGGETTI_PDND_TRACING_DISABLE_ID.equals(idValue))
			return TracciamentoPDNDSoggettoEnum.DISABILITATO;
		if (CostantiDB.MODIPA_SOGGETTI_PDND_TRACING_ENABLE_ID.equals(idValue))
			return TracciamentoPDNDSoggettoEnum.ABILITATO;
		return TracciamentoPDNDSoggettoEnum.DEFAULT;
	}
	
	private static String toTracciamentoPDNDSoggettoId(TracciamentoPDNDSoggettoEnum enumValue) {
		if (enumValue == null)
			return  CostantiDB.MODIPA_SOGGETTI_PDND_TRACING_DEFAULT_ID;
		
		switch (enumValue) {
		case ABILITATO: return CostantiDB.MODIPA_SOGGETTI_PDND_TRACING_ENABLE_ID;
		case DEFAULT: return CostantiDB.MODIPA_SOGGETTI_PDND_TRACING_DEFAULT_ID;
		case DISABILITATO: return CostantiDB.MODIPA_SOGGETTI_PDND_TRACING_DISABLE_ID;
		}
		
		return CostantiDB.MODIPA_SOGGETTI_PDND_TRACING_DEFAULT_ID;
	}

}
