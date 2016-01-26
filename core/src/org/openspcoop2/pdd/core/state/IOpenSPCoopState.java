/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
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

package org.openspcoop2.pdd.core.state;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.mdb.GenericMessage;
import org.openspcoop2.protocol.sdk.state.IState;

/**
 * Oggetto che rappresenta lo stato di una richiesta/risposta all'interno della PdD
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Fabio Tronci (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IOpenSPCoopState {

	public void initResource(IDSoggetto identitaPdD,String idModulo,String idTransazione) throws OpenSPCoopStateException;


	public void updateResource(String idTransazione) throws OpenSPCoopStateException;
	public void releaseResource();
	public boolean resourceReleased();

	public IState getStatoRichiesta();
	public void setStatoRichiesta(IState statoRichiesta);
	public IState getStatoRisposta();
	public void setStatoRisposta(IState statoRisposta);

	public void commit() throws OpenSPCoopStateException;

	public String getIDMessaggioSessione();
	public GenericMessage getMessageLib();

}
