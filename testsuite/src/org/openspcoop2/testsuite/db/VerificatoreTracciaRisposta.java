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

package org.openspcoop2.testsuite.db;

import java.sql.Connection;

import org.openspcoop2.core.constants.CostantiDB;

/**
 * Verifica le tracce di risposta
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author Carlo Ciurli (ciurli@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class VerificatoreTracciaRisposta extends AbstractVerificatoreTraccia {

	
	public VerificatoreTracciaRisposta(Connection con,String protocollo){
		super(con, protocollo);
	}
	

	@Override
	protected String getTipoTraccia() {
		return "TracciaRisposta";
	}

	@Override
	protected String getColumnId() {
		return CostantiDB.TRACCE_COLUMN_RIFERIMENTO_MESSAGGIO;
	}
	
	
}
