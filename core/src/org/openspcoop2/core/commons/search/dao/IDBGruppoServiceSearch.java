/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.core.commons.search.dao;

import org.openspcoop2.core.commons.search.Gruppo;
import org.openspcoop2.generic_project.dao.IDBServiceSearchWithId;
import org.openspcoop2.core.commons.search.IdGruppo;


/** 
* Service can be used for research objects on the backend of type org.openspcoop2.core.commons.search.Gruppo  
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IDBGruppoServiceSearch extends IGruppoServiceSearch,IDBServiceSearchWithId<Gruppo, IdGruppo> {
}