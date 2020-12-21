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

package org.openspcoop2.monitor.engine.alarm;

import org.openspcoop2.monitor.sdk.alarm.AlarmStatus;
import org.openspcoop2.monitor.sdk.constants.AlarmStateValues;

/**
 * AlarmStatusWithAck
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AlarmStatusWithAck extends AlarmStatus {

	private boolean ack;
	
	@Override
	public Object clone(){
		AlarmStatusWithAck s = new AlarmStatusWithAck();
		if(this.ack){
			s.setAck(true);
		}
		else{
			s.setAck(false);
		}
		if(this.dettaglio!=null)
			s.setDetail(new String(this.dettaglio));
		if(this.stato!=null){
			switch (this.stato) {
			case OK:
				s.setStatus(AlarmStateValues.OK);
				break;
			case WARNING:
				s.setStatus(AlarmStateValues.WARNING);
				break;
			case ERROR:
				s.setStatus(AlarmStateValues.ERROR);
				break;
			}
		}
		return s;
	}
	
	public boolean isAck() {
		return this.ack;
	}

	public void setAck(boolean ack) {
		this.ack = ack;
	}
}
