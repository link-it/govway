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
package org.openspcoop2.security.keystore;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.byok.BYOKRequestParams;
import org.openspcoop2.utils.transport.http.IBYOKUnwrapManager;

/**
 * BYOKUnwrapManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BYOKUnwrapManager implements IBYOKUnwrapManager {

	private String ksmId;
	private BYOKRequestParams byokParams;
	
	public BYOKUnwrapManager(String ksmId, BYOKRequestParams byokParams) {
		this.ksmId = ksmId;
		this.byokParams = byokParams;
	}
	
	@Override
	public byte[] unwrap(byte[] archive) throws UtilsException {
		try {
			return StoreUtils.unwrapBYOK(archive, this.byokParams);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

	@Override
	public String getPolicy() {
		return this.ksmId;
	}

}
