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

import org.openspcoop2.monitor.sdk.alarm.AlarmStatus;
import org.openspcoop2.monitor.sdk.alarm.IAlarm;
import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.constants.AlarmType;
import org.openspcoop2.monitor.sdk.exceptions.AlarmException;

/**
 * IAlarmProcessing
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IAlarmProcessing extends ISearchArguments {

	public AlarmType getAlarmType();
	
	// Serve per la generazione automatica del nome dell'allarme
	public String getAutomaticPrefixName(Context context);
	public String getAutomaticSuffixName(Context context);
	
	// Per comprendere se l'allarme è configurabile con criteri di filtro o raggruppamento
	public boolean isUsableFilter();
	public boolean isUsableGroupBy();
	
	/* Solo per allarmi di tipo Attivo */
	public void check(IAlarm allarme) throws AlarmException;
	
	/* 
	 * Vale sia per attivo che per passivo. Viene notificato un cambio di stato in modo da implementare una segnalazione via API
	 * Differente da quelle utilizzabili infrastrutturalmente (Mail o Script)
	 **/
	public void changeStatusNotify(IAlarm allarme, AlarmStatus vecchioStato, AlarmStatus nuovoStato) throws AlarmException;
	
}
