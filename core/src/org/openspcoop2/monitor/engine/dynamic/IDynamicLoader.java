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
package org.openspcoop2.monitor.engine.dynamic;

import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.monitor.sdk.exceptions.SearchException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;

import java.util.List;

/**
 * IDynamicLoader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IDynamicLoader{
	
	public String getClassName() throws SearchException;
	
	public String getClassSimpleName() throws SearchException;
	
	public Object newInstance() throws SearchException;

	public List<Parameter<?>> getParameters(Context context) throws SearchException;
	
	public void updateRendering(Parameter<?> parameter, Context context) throws SearchException;

	public void valueSelectedListener(Parameter<?> parameter, Context context);

	public List<StatisticType> getEnabledStatisticType(Context context) throws SearchException;
}
