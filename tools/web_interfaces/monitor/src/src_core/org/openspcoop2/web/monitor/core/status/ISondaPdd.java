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
package org.openspcoop2.web.monitor.core.status;

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.monitor.engine.status.IStatus;
import org.openspcoop2.monitor.engine.status.SondaStatus;

/**
 * ISondaPdd
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public interface ISondaPdd extends Serializable{
	
	// Aggiorna e restituisce lo stato della sonda
	public List<IStatus> updateStato() throws Exception;
	
	// Restituisce lo stato attuale della sonda
	public List<IStatus> getStato() throws Exception;
	
	// Reset delle metriche che definiscono lo stato
	public void reset() throws Exception;

	// Nome dell sonda
	public String getName();
//	public void setName(String name);
	
	// Identificativo della sonda all'interno del file di properties
	public String getIdentificativo();
	
	// Restituisce lo stato generale della sonda 
	public SondaStatus getStatoSondaPdd() throws Exception;
	
	// Restituisce il messaggio che descrive lo stato generale della sonda
	public String getMessaggioStatoSondaPdd() throws Exception;
	
	// Restituisce un link diretto alla pagina di dettaglio
	public String getLinkDettaglio() throws Exception;
	
}
