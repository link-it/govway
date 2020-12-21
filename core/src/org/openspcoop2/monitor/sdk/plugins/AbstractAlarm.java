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

package org.openspcoop2.monitor.sdk.plugins;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.exceptions.ParameterException;
import org.openspcoop2.monitor.sdk.exceptions.SearchException;
import org.openspcoop2.monitor.sdk.exceptions.ValidationException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;

/**
 * AbstractAlarm
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractAlarm implements IAlarmProcessing {

	// Serve per la generazione automatica del nome dell'allarme
	@Override
	public String getAutomaticPrefixName(Context context){
		return null;
	}
	@Override
	public String getAutomaticSuffixName(Context context){
		return null;
	}
	
	
	@Override
	public List<Parameter<?>> getParameters(Context context) throws SearchException, ParameterException{
		return new ArrayList<Parameter<?>>();
	}
	
	@Override
	public void updateRendering(Parameter<?> parameter,Context context) throws SearchException, ParameterException{
		
	}
	
	@Override
	public void onChangeValue(Parameter<?> parameter, Context context) throws SearchException, ParameterException{
		
	}

	@Override
	public void validate(Context context) throws ValidationException,
			SearchException, ParameterException {
		
	}
		
	
}
