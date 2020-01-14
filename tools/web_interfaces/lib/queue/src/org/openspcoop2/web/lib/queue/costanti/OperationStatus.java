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



package org.openspcoop2.web.lib.queue.costanti;
/**
 *
 * Costanti che rappresentano lo stato delle operazioni.
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public enum OperationStatus {

	SUCCESS, // l'operazione e' stata eseguita (commit)
	ERROR, // l'operazione e' andata in errore (rollback)
	INVALID, // l'operazione non puo' essere portata a termine (commit)
	DELETED, // l'operazione e' stata eliminata (commit) 
	NOT_SET, // stato iniziale 
	WAIT; // l'operazione e' in attesa di un altro evento (rollback)
	
}


