/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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


package org.openspcoop2.protocol.engine.driver;

import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * Interfaccia per la gestione dei filtro duplicati
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IFiltroDuplicati {

	public void init(Object context) throws ProtocolException;
	
	public boolean isDuplicata(IProtocolFactory<?> protocolFactory, String id) throws ProtocolException;
	
	public void incrementaNumeroDuplicati(IProtocolFactory<?> protocolFactory, String id) throws ProtocolException;
	
	public void registraBusta(IProtocolFactory<?> protocolFactory, Busta busta) throws ProtocolException;
	
	public boolean releaseRuntimeResourceBeforeCheck();
	
}
