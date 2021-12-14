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



package org.openspcoop2.utils.id;


/**
 * OpenSPCoop UniqueIdentifier
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IUniqueIdentifierGenerator {

	public abstract void init(Object ... o) throws UniqueIdentifierException;
	
	public abstract IUniqueIdentifier newID() throws UniqueIdentifierException;
	public default IUniqueIdentifier newID(boolean useBuffer) throws UniqueIdentifierException{
		return newID();
	}
	
	public abstract IUniqueIdentifier convertFromString(String value) throws UniqueIdentifierException;
	
	public default boolean isBufferSupperted() {
		return false;
	}
}
