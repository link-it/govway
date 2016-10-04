/*
 * OpenSPCoop - Customizable API Gateway 
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

package org.openspcoop2.protocol.sdk.state;

import java.io.Serializable;
import java.sql.Connection;

import org.slf4j.Logger;
import org.openspcoop2.protocol.sdk.Busta;

/**
 * Oggetto che rappresenta lo stato (stateless) di una busta
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Fabio Tronci (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatelessMessage extends StateMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private Busta busta = null;
	
	private Busta bustaCorrelata = null; // opzionale, puo' contenere la busta di richiesta, quando viene effettuata la validazione della busta di risposta

	public StatelessMessage() {
	
	}
	
	public StatelessMessage (StateMessage temp){
		super(temp.getConnectionDB(), temp.getLog(), temp.getPreparedStatement());
	}
	
	public StatelessMessage(Connection con, Logger log, Busta busta){
		super(con, log);
		this.busta = busta;
	}
	
	public StatelessMessage(Connection con, Logger log){
		super(con, log);
	}


	public Busta getBusta() {
		return this.busta;
	}


	public void setBusta(Busta busta) {
		this.busta = busta;
	}

	public Busta getBustaCorrelata() {
		return this.bustaCorrelata;
	}

	public void setBustaCorrelata(Busta bustaCorrelata) {
		this.bustaCorrelata = bustaCorrelata;
	}

}
