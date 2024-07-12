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
package org.openspcoop2.pdd.core.byok;

import java.util.Map;

import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.keystore.BYOKUnwrapManager;
import org.openspcoop2.utils.certificate.byok.BYOKProvider;
import org.openspcoop2.utils.certificate.byok.BYOKRequestParams;
import org.openspcoop2.utils.transport.http.IBYOKUnwrapManager;

/**
 * DriverBYOKUtilities
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BYOKUnwrapPolicyUtilities {

	private BYOKUnwrapPolicyUtilities() {}
	
	public static BYOKRequestParams getBYOKRequestParams(String keystoreByokPolicy, Map<String,Object> dynamicMap) throws SecurityException {
		BYOKRequestParams byokParams = null;
		if(BYOKProvider.isPolicyDefined(keystoreByokPolicy)) {
			try {
				byokParams = BYOKProvider.getBYOKRequestParamsByUnwrapBYOKPolicy(keystoreByokPolicy, 
					dynamicMap );
			}catch(Exception e) {
				throw new SecurityException(e.getMessage(), e);
			}
		}
		return byokParams;
	}
	
	public static IBYOKUnwrapManager getBYOKUnwrapManager(String keystoreByokPolicy, Map<String,Object> dynamicMap) throws SecurityException {
		BYOKRequestParams byokParams = getBYOKRequestParams(keystoreByokPolicy, dynamicMap);
		BYOKUnwrapManager byokUnwrapManager = null;
		if(byokParams!=null) {
			byokUnwrapManager = new BYOKUnwrapManager(keystoreByokPolicy, byokParams);
		}
		return byokUnwrapManager;
	}
	
}
