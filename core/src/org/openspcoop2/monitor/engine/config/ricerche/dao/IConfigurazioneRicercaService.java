/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.monitor.engine.config.ricerche.dao;

import org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca;
import org.openspcoop2.generic_project.dao.IServiceWithId;
import org.openspcoop2.monitor.engine.config.ricerche.IdConfigurazioneRicerca;

/**     
 * Service can be used both for research that will make persistent objects on the backend of type org.openspcoop2.monitor.engine.config.ricerche.ConfigurazioneRicerca 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IConfigurazioneRicercaService extends IServiceWithId<ConfigurazioneRicerca, IdConfigurazioneRicerca> {

}
