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

package org.openspcoop2.web.monitor.allarmi.bean;

import org.openspcoop2.monitor.engine.alarm.utils.AllarmiConfig;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;

/**
 * AllarmiMonitorConfig
 *
 * @author Andrea Poli (apoli@link.it)
 * @author pintori
 * @author $Author$
 * @version $Rev$, $Date$

 *
 */
public class AllarmiMonitorConfig implements AllarmiConfig{

	private String allarmiConfigurazione;
	private boolean allarmiConsultazioneModificaStatoAbilitata;
	private boolean allarmiAssociazioneAcknowledgedStatoAllarme;
	private boolean allarmiNotificaMailVisualizzazioneCompleta;
	private boolean allarmiMonitoraggioEsternoVisualizzazioneCompleta;
	private boolean allarmiGroupByApi;
	private boolean allarmiFiltroApi;
	private boolean allarmiFiltroApiSoggettoErogatore;

	public AllarmiMonitorConfig (PddMonitorProperties monitorProperties) throws Exception {
		this.allarmiConfigurazione = monitorProperties.getAllarmiConfigurazione();
		this.allarmiConsultazioneModificaStatoAbilitata = monitorProperties.isAllarmiConsultazioneModificaStatoAbilitata();
		this.allarmiAssociazioneAcknowledgedStatoAllarme = monitorProperties.isAllarmiAssociazioneAcknowledgedStatoAllarme();
		this.allarmiNotificaMailVisualizzazioneCompleta = monitorProperties.isAllarmiNotificaMailVisualizzazioneCompleta();
		this.allarmiMonitoraggioEsternoVisualizzazioneCompleta = monitorProperties.isAllarmiMonitoraggioEsternoVisualizzazioneCompleta();
		this.allarmiGroupByApi = monitorProperties.isAllarmiGroupByApi();
		this.allarmiFiltroApi = monitorProperties.isAllarmiFiltroApi();
		this.allarmiFiltroApiSoggettoErogatore = monitorProperties.isAllarmiFiltroApiSoggettoErogatore();
		
	}

	@Override
	public String getAllarmiConfigurazione() {
		return this.allarmiConfigurazione;
	}

	@Override
	public boolean isAllarmiConsultazioneModificaStatoAbilitata() {
		return this.allarmiConsultazioneModificaStatoAbilitata;
	}

	@Override
	public boolean isAllarmiAssociazioneAcknowledgedStatoAllarme() {
		return this.allarmiAssociazioneAcknowledgedStatoAllarme;
	}

	@Override
	public boolean isAllarmiNotificaMailVisualizzazioneCompleta() {
		return this.allarmiNotificaMailVisualizzazioneCompleta;
	}

	@Override
	public boolean isAllarmiMonitoraggioEsternoVisualizzazioneCompleta() {
		return this.allarmiMonitoraggioEsternoVisualizzazioneCompleta;
	}

	@Override
	public boolean isAllarmiGroupByApi() {
		return this.allarmiGroupByApi;
	}
	
	@Override
	public boolean isAllarmiFiltroApi()  {
		return this.allarmiFiltroApi;
	}
	
	@Override
	public boolean isAllarmiFiltroApiSoggettoErogatore() {
		return this.allarmiFiltroApiSoggettoErogatore;
	}

}
