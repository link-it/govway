/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
import org.openspcoop2.core.config.rs.server.model.ModISoggetto;
import org.openspcoop2.core.config.rs.server.model.ModISoggettoPDND;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
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

	public static ProtocolProperties getProtocolProperties(org.openspcoop2.core.config.rs.server.model.Soggetto body) {
		/**		if(body.getModi() == null) {
		//			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare la configurazione 'ModI'");
		//		}*/

		ProtocolProperties p = new ProtocolProperties();

		if(body.getModi() != null && body.getModi().getPdnd()!=null && body.getModi().getPdnd().getIdEnte()!=null && StringUtils.isNotEmpty(body.getModi().getPdnd().getIdEnte())) {
			p.addProperty(ModICostanti.MODIPA_SOGGETTI_ID_ENTE_ID, body.getModi().getPdnd().getIdEnte());
		}

		return p;

	}
	


	public static void populateProtocolInfo(Soggetto soggetto, SoggettiEnv env, org.openspcoop2.core.config.rs.server.model.Soggetto ret) throws CoreException, UtilsException, ProtocolException, DriverConfigurazioneException {

		Map<String, AbstractProperty<?>> p = SoggettiApiHelper.getProtocolPropertiesMap(soggetto, env);
		String idEnte = ProtocolPropertiesHelper.getStringProperty(p, ModICostanti.MODIPA_SOGGETTI_ID_ENTE_ID, false);
		if(idEnte != null && StringUtils.isNotEmpty(idEnte)) {
			if(ret.getModi()==null) {
				ret.setModi(new ModISoggetto());
			}
			if(ret.getModi().getPdnd()==null) {
				ret.getModi().setPdnd(new  ModISoggettoPDND());
			}
			ret.getModi().getPdnd().setIdEnte(idEnte);
		}
		
	}

}
