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
package org.openspcoop2.pdd.config;

import java.util.Map;

import org.openspcoop2.pdd.core.byok.BYOKUnwrapPolicyUtilities;
import org.openspcoop2.pdd.core.dynamic.DynamicMapBuilderUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.byok.BYOKProvider;
import org.openspcoop2.utils.transport.http.IBYOKUnwrapFactory;
import org.openspcoop2.utils.transport.http.IBYOKUnwrapManager;
import org.slf4j.Logger;

/**
 * BYOKUnwrapManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BYOKUnwrapFactory implements IBYOKUnwrapFactory {

	@Override
	public IBYOKUnwrapManager newInstance(String policy, Logger log) throws UtilsException {
		return getBYOKUnwrapManager(policy, log);
	}

	public static IBYOKUnwrapManager getBYOKUnwrapManager(String idPolicy, Logger log) throws UtilsException{
		IBYOKUnwrapManager byokUnwrapManager = null;
		if(BYOKProvider.isPolicyDefined(idPolicy)) {
			Map<String, Object> dynamicMap = DynamicMapBuilderUtils.buildDynamicMap(null, null, null, 
					log);
			try {
				byokUnwrapManager = BYOKUnwrapPolicyUtilities.getBYOKUnwrapManager(idPolicy, dynamicMap);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
		return byokUnwrapManager;
	}
}
