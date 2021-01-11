/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.monitor.engine.alarm.utils;

/**     
 * AllarmiConfig
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface AllarmiConfig {

	public String getAllarmiConfigurazione();
	
	public String getAllarmiActiveServiceUrl();
	
	public String getAllarmiActiveServiceUrl_SuffixStartAlarm();
	
	public String getAllarmiActiveServiceUrl_SuffixStopAlarm();
	
	public String getAllarmiActiveServiceUrl_SuffixReStartAlarm();
	
	public String getAllarmiActiveServiceUrl_SuffixUpdateStateOkAlarm();
	
	public String getAllarmiActiveServiceUrl_SuffixUpdateStateWarningAlarm();
	
	public String getAllarmiActiveServiceUrl_SuffixUpdateStateErrorAlarm();
	
	public String getAllarmiActiveServiceUrl_SuffixUpdateAcknoledgementEnabledAlarm();
	
	public String getAllarmiActiveServiceUrl_SuffixUpdateAcknoledgementDisabledAlarm();
	
	public boolean isAllarmiConsultazioneModificaStatoAbilitata();
	
	public boolean isAllarmiAssociazioneAcknowledgedStatoAllarme();
	
	public boolean isAllarmiNotificaMailVisualizzazioneCompleta();
	
	public boolean isAllarmiMonitoraggioEsternoVisualizzazioneCompleta();

	public boolean isAllarmiGroupByApi();

	public boolean isAllarmiFiltroApi();

	public boolean isAllarmiFiltroApiSoggettoErogatore();

}
