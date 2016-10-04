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



package org.openspcoop2.protocol.trasparente.testsuite.core;

import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;



/**
 * Reader delle proprieta' per l'accesso al Database
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Carlo Ciurli (ciurli@openspcoop.org)
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DatabaseProperties  {

	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di DatabaseProperties
	 */
	public static org.openspcoop2.testsuite.units.utils.DatabaseProperties getInstance(){
		return org.openspcoop2.testsuite.units.utils.DatabaseProperties.getInstance(CostantiTestSuite.PROTOCOL_NAME);
	}


	public static DatabaseComponent getDatabaseComponentErogatore(){
		return org.openspcoop2.testsuite.units.utils.DatabaseProperties.getDatabaseComponentErogatore(CostantiTestSuite.PROTOCOL_NAME);
	}
	
	public static DatabaseComponent getDatabaseComponentFruitore(){
		return org.openspcoop2.testsuite.units.utils.DatabaseProperties.getDatabaseComponentFruitore(CostantiTestSuite.PROTOCOL_NAME);
	}
	
	public static DatabaseMsgDiagnosticiComponent getDatabaseComponentDiagnosticaErogatore(){
		return org.openspcoop2.testsuite.units.utils.DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(CostantiTestSuite.PROTOCOL_NAME);
	}
	
	public static DatabaseMsgDiagnosticiComponent getDatabaseComponentDiagnosticaFruitore(){
		return org.openspcoop2.testsuite.units.utils.DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(CostantiTestSuite.PROTOCOL_NAME);
	}
}


