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



package org.openspcoop2.utils.jdbc;

import org.openspcoop2.utils.TipiDatabase;

/**
 * Implementazione dell'interfaccia JDBCAdapter 
 * che definisce un adapter JDBC per la gestione del repository del messaggi
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class BytesJDBCAdapter extends AbstractJDBCAdapter {
	
    public BytesJDBCAdapter(TipiDatabase tipoDatabase) {
		super(tipoDatabase);
	}
	
}





