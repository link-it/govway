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

package org.openspcoop2.pdd.core.transazioni;

import java.io.Serializable;



/**     
 * StatefulObjectType
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum StatefulObjectType implements Serializable{

	MSGDIAGNOSTICO,
	TRACCIA,
	MESSAGGIO,
//	DATA_USCITA_RICHIESTA,DATA_INGRESSO_RISPOSTA,
//	DIMENSIONE_USCITA_RICHIESTA,DIMENSIONE_INGRESSO_RISPOSTA,
//	SCENARIO_COOPERAZIONE, TIPO_CONNETTORE, LOCATION,
//	CODICE_TRASPORTO_RICHIESTA,
	OUT_REQUEST_STATEFUL_OBJECT,IN_RESPONSE_STATEFUL_OBJECT;

	@Override
	public String toString(){
		return this.name();
	}
	public boolean equals(StatefulObjectType esito){
		return this.toString().equals(esito.toString());
	}

}

