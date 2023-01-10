/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.rs.server.api.impl.api;

import org.openspcoop2.core.config.rs.server.model.Api;
import org.openspcoop2.core.config.rs.server.model.ApiAzione;
import org.openspcoop2.core.config.rs.server.model.ApiRisorsa;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;

/**
 * ApiApiHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class EDeliveryApiApiHelper {

	public static ProtocolProperties getProtocolProperties(Api body) throws Exception {
		return null;
	}

	public static ProtocolProperties getProtocolProperties(ApiRisorsa body)throws Exception  {
		return null;
	}

	public static ProtocolProperties getProtocolProperties(ApiAzione body)throws Exception  {
		return null;
	}

	public static void populateApiRisorsaWithProtocolInfo(AccordoServizioParteComune as, Resource res, ApiEnv env, ApiRisorsa ret) throws Exception {}

	public static void populateApiAzioneWithProtocolInfo(AccordoServizioParteComune as, Operation az, ApiEnv env, ApiAzione ret) throws Exception {}

}
