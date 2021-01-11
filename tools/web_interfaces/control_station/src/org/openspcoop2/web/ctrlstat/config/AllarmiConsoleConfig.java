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

package org.openspcoop2.web.ctrlstat.config;

import org.openspcoop2.monitor.engine.alarm.utils.AllarmiConfig;

/**
 * AllarmiConsoleConfig
 *
 * @author Andrea Poli (apoli@link.it)
 * @author pintori
 * @author $Author$
 * @version $Rev$, $Date$

 *
 */
public class AllarmiConsoleConfig implements AllarmiConfig{

	private String allarmiConfigurazione;
	private String allarmiActiveServiceUrl;
	private String allarmiActiveServiceUrl_SuffixStartAlarm;
	private String allarmiActiveServiceUrl_SuffixStopAlarm;
	private String allarmiActiveServiceUrl_SuffixReStartAlarm;
	private String allarmiActiveServiceUrl_SuffixUpdateStateOkAlarm;
	private String allarmiActiveServiceUrl_SuffixUpdateStateWarningAlarm;
	private String allarmiActiveServiceUrl_SuffixUpdateStateErrorAlarm;
	private String allarmiActiveServiceUrl_SuffixUpdateAcknoledgementEnabledAlarm;
	private String allarmiActiveServiceUrl_SuffixUpdateAcknoledgementDisabledAlarm;
	private boolean allarmiConsultazioneModificaStatoAbilitata;
	private boolean allarmiAssociazioneAcknowledgedStatoAllarme;
	private boolean allarmiNotificaMailVisualizzazioneCompleta;
	private boolean allarmiMonitoraggioEsternoVisualizzazioneCompleta;
	private boolean allarmiGroupByApi;
	private boolean allarmiFiltroApi;
	private boolean allarmiFiltroApiSoggettoErogatore;

	public AllarmiConsoleConfig (ConsoleProperties consoleProperties) throws Exception {
		this.allarmiConfigurazione = consoleProperties.getAllarmiConfigurazione(); 
		this.allarmiActiveServiceUrl = consoleProperties.getAllarmiActiveServiceUrl();
		this.allarmiActiveServiceUrl_SuffixStartAlarm = consoleProperties.getAllarmiActiveServiceUrl_SuffixStartAlarm();
		this.allarmiActiveServiceUrl_SuffixStopAlarm = consoleProperties.getAllarmiActiveServiceUrl_SuffixStopAlarm();
		this.allarmiActiveServiceUrl_SuffixReStartAlarm = consoleProperties.getAllarmiActiveServiceUrl_SuffixReStartAlarm();
		this.allarmiActiveServiceUrl_SuffixUpdateStateOkAlarm = consoleProperties.getAllarmiActiveServiceUrl_SuffixUpdateStateOkAlarm();
		this.allarmiActiveServiceUrl_SuffixUpdateStateWarningAlarm = consoleProperties.getAllarmiActiveServiceUrl_SuffixUpdateStateWarningAlarm();
		this.allarmiActiveServiceUrl_SuffixUpdateStateErrorAlarm = consoleProperties.getAllarmiActiveServiceUrl_SuffixUpdateStateErrorAlarm();
		this.allarmiActiveServiceUrl_SuffixUpdateAcknoledgementEnabledAlarm = consoleProperties.getAllarmiActiveServiceUrl_SuffixUpdateAcknoledgementEnabledAlarm();
		this.allarmiActiveServiceUrl_SuffixUpdateAcknoledgementDisabledAlarm = consoleProperties.getAllarmiActiveServiceUrl_SuffixUpdateAcknoledgementDisabledAlarm();
		this.allarmiConsultazioneModificaStatoAbilitata = consoleProperties.isAllarmiConsultazioneModificaStatoAbilitata();
		this.allarmiAssociazioneAcknowledgedStatoAllarme = consoleProperties.isAllarmiAssociazioneAcknowledgedStatoAllarme();
		this.allarmiNotificaMailVisualizzazioneCompleta = consoleProperties.isAllarmiNotificaMailVisualizzazioneCompleta();
		this.allarmiMonitoraggioEsternoVisualizzazioneCompleta = consoleProperties.isAllarmiMonitoraggioEsternoVisualizzazioneCompleta();
		this.allarmiGroupByApi = consoleProperties.isAllarmiGroupByApi();
		this.allarmiFiltroApi = consoleProperties.isAllarmiFiltroApi();
		this.allarmiFiltroApiSoggettoErogatore = consoleProperties.isAllarmiFiltroApiSoggettoErogatore();
	}

	@Override
	public String getAllarmiConfigurazione() {
		return this.allarmiConfigurazione;
	}

	@Override
	public String getAllarmiActiveServiceUrl() {
		return this.allarmiActiveServiceUrl;
	}

	@Override
	public String getAllarmiActiveServiceUrl_SuffixStartAlarm() {
		return this.allarmiActiveServiceUrl_SuffixStartAlarm;
	}

	@Override
	public String getAllarmiActiveServiceUrl_SuffixStopAlarm() {
		return this.allarmiActiveServiceUrl_SuffixStopAlarm;
	}

	@Override
	public String getAllarmiActiveServiceUrl_SuffixReStartAlarm() {
		return this.allarmiActiveServiceUrl_SuffixReStartAlarm;
	}

	@Override
	public String getAllarmiActiveServiceUrl_SuffixUpdateStateOkAlarm() {
		return this.allarmiActiveServiceUrl_SuffixUpdateStateOkAlarm;
	}

	@Override
	public String getAllarmiActiveServiceUrl_SuffixUpdateStateWarningAlarm() {
		return this.allarmiActiveServiceUrl_SuffixUpdateStateWarningAlarm;
	}

	@Override
	public String getAllarmiActiveServiceUrl_SuffixUpdateStateErrorAlarm() {
		return this.allarmiActiveServiceUrl_SuffixUpdateStateErrorAlarm;
	}

	@Override
	public String getAllarmiActiveServiceUrl_SuffixUpdateAcknoledgementEnabledAlarm() {
		return this.allarmiActiveServiceUrl_SuffixUpdateAcknoledgementEnabledAlarm;
	}

	@Override
	public String getAllarmiActiveServiceUrl_SuffixUpdateAcknoledgementDisabledAlarm() {
		return this.allarmiActiveServiceUrl_SuffixUpdateAcknoledgementDisabledAlarm;
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
