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
 * AbstractCredenziale
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractCredenziale {

	protected TipoCredenzialeMittente tipo;
	protected Long referenceId;
	
	protected AbstractCredenziale(TipoCredenzialeMittente tipo) {
		this.tipo = tipo;
	}
	protected AbstractCredenziale(TipoCredenzialeMittente tipo, Long referenceId) {
		this.tipo = tipo;
		this.referenceId = referenceId;
	}
	
	public String getTipo() {
		return this.tipo.getRawValue();
	}
	public Long getReferenceId() {
		return this.referenceId;
	}
	
	public abstract String getCredenziale() throws UtilsException;
	
	public abstract void updateCredenziale(String newCredential) throws UtilsException;
}
