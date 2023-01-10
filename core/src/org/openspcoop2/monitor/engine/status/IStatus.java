/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.engine.status;

import java.io.Serializable;

/**
 * IStatus
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public interface IStatus extends Serializable{

	// Identificativo del nodo
	public String getIdNodoRuntime();
	public void setIdNodoRuntime(String idNodoRuntime);
	
	// Nome dell'oggetto di cui si vuole visualizzare lo stato
	public String getNome();
	public void setNome(String nome);

	// Stato dell'oggetto
	public SondaStatus getStato();
	public void setStato(SondaStatus stato);
	// Stato dell'oggetto in stringa
	

	// Descrizione dello stato rilevato
	public String getDescrizione();
	public void setDescrizione(String descrizione);
	
}
