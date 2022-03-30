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

package org.openspcoop2.pdd.core.state;

import org.openspcoop2.protocol.sdk.state.StateMap;
import org.openspcoop2.protocol.sdk.state.StatefulMessage;
import org.openspcoop2.utils.UtilsException;


/**
 * Oggetto che rappresenta lo stato di una richiesta/risposta all'interno della PdD
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Fabio Tronci (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoopStateful extends OpenSPCoopState {
	
	
	
	/* ---------- Costruttori  ----------*/
	public OpenSPCoopStateful(){
		this.useConnection = true;
	}
	
	
	
	
	
	/* ----------- Init resource ------------*/

	@Override
	public void updateStatoRichiesta() throws UtilsException{
		
		StateMap pstmt = null;
		if(this.richiestaStato!=null && this.richiestaStato.getPreparedStatement()!=null)
			pstmt = this.richiestaStato.getPreparedStatement();
		this.richiestaStato = new StatefulMessage(this.connectionDB,this.logger);
		if(pstmt!=null){
			this.richiestaStato.addPreparedStatement(pstmt.getPreparedStatement());
		}
	}
	@Override
	public void updateStatoRisposta() throws UtilsException{
		
		StateMap pstmt = null;
		if(this.rispostaStato!=null && this.rispostaStato.getPreparedStatement()!=null)
			pstmt = this.rispostaStato.getPreparedStatement();
		this.rispostaStato = new StatefulMessage(this.connectionDB,this.logger);
		if(pstmt!=null){
			this.rispostaStato.addPreparedStatement(pstmt.getPreparedStatement());
		}
	}
	

	
}
