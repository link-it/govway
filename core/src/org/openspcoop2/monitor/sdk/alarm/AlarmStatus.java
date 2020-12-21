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

import org.openspcoop2.monitor.sdk.constants.AlarmStateValues;

/**     
 * AlarmStatus
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AlarmStatus {

	protected AlarmStateValues stato;
	protected String dettaglio;
	
	public AlarmStateValues getStatus() {
		return this.stato;
	}
	public void setStatus(AlarmStateValues stato) {
		this.stato = stato;
	}
	public String getDetail() {
		return this.dettaglio;
	}
	public void setDetail(String dettaglio) {
		this.dettaglio = dettaglio;
	}
	
	@Override
	public Object clone(){
		AlarmStatus s = new AlarmStatus();
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
}
