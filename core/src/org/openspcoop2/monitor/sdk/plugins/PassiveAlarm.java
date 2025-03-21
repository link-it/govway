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

package org.openspcoop2.monitor.sdk.plugins;

import org.openspcoop2.monitor.sdk.alarm.IAlarm;
import org.openspcoop2.monitor.sdk.constants.AlarmType;
import org.openspcoop2.monitor.sdk.exceptions.AlarmException;

/**
 * PassiveAlarm
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class PassiveAlarm extends AbstractAlarm {

	@Override
	public final AlarmType getAlarmType(){
		return AlarmType.PASSIVE;
	}
	
	@Override
	public final void check(IAlarm allarme) throws AlarmException {
		throw new AlarmException("L'allarme ["+allarme.getId()+"] non e' invocabile in modalità attiva");
	}	
	
}
