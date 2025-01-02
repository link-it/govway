/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import java.util.Map;

import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.monitor.sdk.exceptions.AlarmException;
import org.openspcoop2.monitor.sdk.exceptions.AlarmNotifyException;
import org.openspcoop2.monitor.sdk.parameters.Parameter;

/**     
 * IAlarm
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IAlarm {
	
	public String getId();
	
	public String getNome();
	
	public Allarme getConfigAllarme();
	
	public AlarmStatus getStatus();
	
	public Parameter<?> getParameter(String paramID);
	
	public Map<String, Parameter<?>> getParameters();
		
	public IAlarmLogger getLogger();
	
	public DAOFactory getDAOFactory();

	public void changeStatus(AlarmStatus statoAllarme) throws AlarmException, AlarmNotifyException;

	public boolean isManuallyUpdateState();
	
	public boolean isManuallyAckCriteria();
	
	public IAlarmThreadStatus getActiveThreadStatus();
}
