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

package org.openspcoop2.monitor.sdk.alarm;

import java.lang.reflect.Method;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.monitor.sdk.exceptions.AlarmException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.slf4j.Logger;


/**
 * AlarmFactory
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AlarmFactory {

	public static IAlarm getAlarm(String idAllarme) throws AlarmException {
		return getAlarm(LoggerWrapperFactory.getLogger(AlarmFactory.class), idAllarme);
	}
	public static IAlarm getAlarm(Logger log, String idAllarme) throws AlarmException {
		try {
			Class<?> c = Class.forName("org.openspcoop2.monitor.engine.alarm.AlarmManager");
			
			Class<?>[] paramTypes = new Class[3];
			paramTypes[0] = String.class;
			paramTypes[1] = Logger.class;
			paramTypes[2] = DAOFactory.class;
			
			DAOFactory daoFactory = DAOFactory.getInstance(log);
			
			Object[] params = new Object[3];
			params[0] = idAllarme;
			params[1] = log;
			params[2] = daoFactory;
						
			Method m = c.getMethod("getAlarm", paramTypes);
			return (IAlarm) m.invoke(Utilities.newInstance(c), params);
		} catch (Exception e) {
			throw new AlarmException (e.getMessage(),e);
		}
	}
	
}
