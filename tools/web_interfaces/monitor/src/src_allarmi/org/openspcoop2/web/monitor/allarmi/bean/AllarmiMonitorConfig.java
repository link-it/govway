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

	public AllarmiMonitorConfig (PddMonitorProperties monitorProperties) throws Exception {
		this.allarmiConfigurazione = monitorProperties.getAllarmiConfigurazione();
		this.allarmiActiveServiceUrl = monitorProperties.getAllarmiActiveServiceUrl();
		this.allarmiActiveServiceUrl_SuffixStartAlarm = monitorProperties.getAllarmiActiveServiceUrl_SuffixStartAlarm();
		this.allarmiActiveServiceUrl_SuffixStopAlarm = monitorProperties.getAllarmiActiveServiceUrl_SuffixStopAlarm();
		this.allarmiActiveServiceUrl_SuffixReStartAlarm = monitorProperties.getAllarmiActiveServiceUrl_SuffixReStartAlarm();
		this.allarmiActiveServiceUrl_SuffixUpdateStateOkAlarm = monitorProperties.getAllarmiActiveServiceUrl_SuffixUpdateStateOkAlarm();
		this.allarmiActiveServiceUrl_SuffixUpdateStateWarningAlarm = monitorProperties.getAllarmiActiveServiceUrl_SuffixUpdateStateWarningAlarm();
		this.allarmiActiveServiceUrl_SuffixUpdateStateErrorAlarm = monitorProperties.getAllarmiActiveServiceUrl_SuffixUpdateStateErrorAlarm();
		this.allarmiActiveServiceUrl_SuffixUpdateAcknoledgementEnabledAlarm = monitorProperties.getAllarmiActiveServiceUrl_SuffixUpdateAcknoledgementEnabledAlarm();
		this.allarmiActiveServiceUrl_SuffixUpdateAcknoledgementDisabledAlarm = monitorProperties.getAllarmiActiveServiceUrl_SuffixUpdateAcknoledgementDisabledAlarm();
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
