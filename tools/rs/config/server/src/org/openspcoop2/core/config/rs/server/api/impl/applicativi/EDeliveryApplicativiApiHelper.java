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

package org.openspcoop2.core.config.rs.server.api.impl.applicativi;

import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.rs.server.model.Applicativo;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;

/**
 * EDeliveryApplicativiApiHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class EDeliveryApplicativiApiHelper {

	private EDeliveryApplicativiApiHelper() {}
	
	public static void populateProtocolInfo(ServizioApplicativo sa, ApplicativiEnv env, Applicativo ret) {
		// nop
	}

	public static ProtocolProperties getProtocolProperties(Applicativo body) {
		if(body!=null) {
			// nop
		}
		return null;
	}

}
