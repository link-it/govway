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
	private boolean allarmiConsultazioneModificaStatoAbilitata;
	private boolean allarmiAssociazioneAcknowledgedStatoAllarme;
	private boolean allarmiNotificaMailVisualizzazioneCompleta;
	private boolean allarmiMonitoraggioEsternoVisualizzazioneCompleta;
	private boolean allarmiGroupByApi;
	private boolean allarmiFiltroApi;
	private boolean allarmiFiltroApiSoggettoErogatore;

	public AllarmiConsoleConfig (ConsoleProperties consoleProperties) throws Exception {
		this.allarmiConfigurazione = consoleProperties.getAllarmiConfigurazione(); 
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
