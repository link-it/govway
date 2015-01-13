/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Soggetto;

/**
 * IRegistryReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IRegistryReader {

	public boolean existsSoggettoByCodiceIPA(String codiceIPA) throws RegistryNotFound;
	
	public boolean existsSoggetto(IDSoggetto idSoggetto) throws RegistryNotFound;
	
	public IDSoggetto getIdSoggettoByCodiceIPA(String codiceIPA) throws RegistryNotFound;
	
	public String getCodiceIPA(IDSoggetto idSoggetto) throws RegistryNotFound;
	
	public Soggetto getSoggetto(IDSoggetto idSoggetto) throws RegistryNotFound;
	
	public AccordoServizioParteComune getAccordoServizioParteComune(IDAccordo idAccordo) throws RegistryNotFound;
	public AccordoServizioParteComune getAccordoServizioParteComune(IDAccordo idAccordo,boolean readAllegati) throws RegistryNotFound;
	
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDAccordo idAccordo) throws RegistryNotFound;
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDAccordo idAccordo,boolean readAllegati) throws RegistryNotFound;
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio) throws RegistryNotFound;
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio,boolean readAllegati) throws RegistryNotFound;
	
	public IDServizio convertToIDServizio(IDAccordo idAccordoServizioParteSpecifica) throws RegistryNotFound;
	
	public IDAccordo convertToIDAccordo(IDServizio idServizio) throws RegistryNotFound;
	
	public AccordoCooperazione getAccordoCooperazione(IDAccordoCooperazione idAccordo) throws RegistryNotFound;
	public AccordoCooperazione getAccordoCooperazione(IDAccordoCooperazione idAccordo,boolean readAllegati) throws RegistryNotFound;
	
}
