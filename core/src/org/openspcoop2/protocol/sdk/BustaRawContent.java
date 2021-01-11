/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

package org.openspcoop2.protocol.sdk;

import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;

/**
 * BustaRawContent
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class BustaRawContent<T> {

	protected T element;
	
	public BustaRawContent(T element){
		this.element = element;
	}
	
	public T getElement() {
		return this.element;
	}
	
	public abstract String toString(TipoSerializzazione tipoSerializzazione) throws ProtocolException;
	
	public abstract byte[] toByteArray(TipoSerializzazione tipoSerializzazione) throws ProtocolException;
}
