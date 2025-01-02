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

package org.openspcoop2.protocol.sdk.archive;

import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.SortedMap;

/**
 *  ArchiveSortedMap
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveSortedMap<T> extends SortedMap<T> {

	public void add(T archive) throws ProtocolException{
		try{
			if( !(archive instanceof IArchiveObject) ){
				throw new ProtocolException("Archivio fornito non implementa l'interfaccia "+IArchiveObject.class.getName());
			}
			this.add(((IArchiveObject)archive).key(), archive);
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
}
