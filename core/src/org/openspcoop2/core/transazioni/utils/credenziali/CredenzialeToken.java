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

package org.openspcoop2.core.transazioni.utils.credenziali;

import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.utils.UtilsException;

/**     
 * CredenzialeTrasporto
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CredenzialeToken extends AbstractCredenziale {

	private String credential;
	
	public CredenzialeToken(TipoCredenzialeMittente tipo, String credential) {
		super(tipo);
		this.credential = credential;
	}

	@Override
	public String getCredenziale() throws UtilsException {
		return this.credential;
	}

	@Override
	public void updateCredenziale(String newCredential) throws UtilsException{
		this.credential = newCredential;
	}
}
