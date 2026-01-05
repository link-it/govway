/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.core.statistiche.dao;

import java.util.List;

import org.openspcoop2.core.statistiche.StatistichePdndTracing;
import org.openspcoop2.generic_project.dao.IServiceWithoutId;
import org.openspcoop2.generic_project.expression.IExpression;

/**     
 * Service can be used both for research that will make persistent objects on the backend of type org.openspcoop2.core.statistiche.StatistichePdndTracing 
 *
 * @author Poli Andrea (poli@link.it)
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IStatistichePdndTracingService extends IServiceWithoutId<StatistichePdndTracing> {
	
	public void forcePublish(IExpression expr) throws Exception;
	
	public void forcePublish(List<Long> ids) throws Exception;
}
