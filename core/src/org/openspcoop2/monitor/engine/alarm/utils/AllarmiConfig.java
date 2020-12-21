package org.openspcoop2.monitor.engine.alarm.utils;

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
