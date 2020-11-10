/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
package org.openspcoop2.protocol.sdk.registry;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.slf4j.Logger;

/**
 * IRegistryReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IRegistryReaderInUso {

	public default void init(IDriverRegistroServiziGet driver,Logger log) {}
	
	// SOGGETTI
		
	public boolean inUso(IDSoggetto idSoggetto) throws RegistryException;
	public String getDettagliInUso(IDSoggetto idSoggetto) throws RegistryException;
		
	
	// ACCORDI PARTE COMUNE
	
	public boolean inUso(IDAccordo idAccordo) throws RegistryException;
	public String getDettagliInUso(IDAccordo idAccordo) throws RegistryException;
	
	
	// ELEMENTI INTERNI ALL'ACCORDO PARTE COMUNE
	
	public boolean inUso(IDPortType id) throws RegistryException;
	public String getDettagliInUso(IDPortType id) throws RegistryException;
	
	public boolean inUso(IDPortTypeAzione id) throws RegistryException;
	public String getDettagliInUso(IDPortTypeAzione id) throws RegistryException;
	
	public boolean inUso(IDResource id) throws RegistryException;
	public String getDettagliInUso(IDResource id) throws RegistryException;
	
}
