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


package org.openspcoop2.utils.id;

import java.util.UUID;

/**
 * AbstractUniversallyUniqueIdentifierGenerator
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractUniversallyUniqueIdentifierGenerator implements IUniqueIdentifierGenerator {

	@Override
	public IUniqueIdentifier convertFromString(String value)
			throws UniqueIdentifierException {
		UniversallyUniqueIdentifier uuidOpenSPCoop = new UniversallyUniqueIdentifier();
		uuidOpenSPCoop.setUuid(UUID.fromString(value));
		return uuidOpenSPCoop;
	}

	@Override
	public void init(Object... o) throws UniqueIdentifierException {
		
	}
	
	@Override
	public boolean isBufferSupperted() {
		return true;
	}
	@Override
	public final IUniqueIdentifier newID() throws UniqueIdentifierException {
		return newID(true);
	}
	@Override
	public final IUniqueIdentifier newID(boolean useBuffer) throws UniqueIdentifierException {
		UniversallyUniqueIdentifier uuidOpenSPCoop = null;
		UniversallyUniqueIdentifierProducer uuidProducer = useBuffer ? UniversallyUniqueIdentifierProducer.getInstance() : null;
		if ( uuidProducer != null ) {
			uuidOpenSPCoop = (UniversallyUniqueIdentifier) uuidProducer.newUniqueIdentifier();
		} else {
			uuidOpenSPCoop = new UniversallyUniqueIdentifier();
			uuidOpenSPCoop.setUuid( this.generateUUID() );
		}
		return uuidOpenSPCoop;
	}

	protected abstract UUID generateUUID();
}
