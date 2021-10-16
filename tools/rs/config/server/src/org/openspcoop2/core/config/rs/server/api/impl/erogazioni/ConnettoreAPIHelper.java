package org.openspcoop2.core.config.rs.server.api.impl.erogazioni;

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.rs.server.model.ConnettoreEnum;
import org.openspcoop2.core.config.rs.server.model.ConnettoreErogazione;
import org.openspcoop2.core.config.rs.server.model.ConnettoreFruizione;
import org.openspcoop2.core.config.rs.server.model.OneOfApplicativoServerConnettore;
import org.openspcoop2.core.config.rs.server.model.OneOfConnettoreErogazioneConnettore;
import org.openspcoop2.core.config.rs.server.model.OneOfConnettoreFruizioneConnettore;
import org.openspcoop2.core.constants.TipiConnettore;

public class ConnettoreAPIHelper {

	public static String getUrlConnettore(ServizioApplicativo sa, boolean isRidefinito) throws Exception {
		if(isRidefinito) {
			return "Connettori ridefiniti nei gruppi";
		}

		return getUrlConnettore(sa.getInvocazioneServizio().getConnettore().getProperties(), sa.getInvocazioneServizio().getConnettore().getTipo(), sa.getInvocazioneServizio().getConnettore().getCustom(), sa.getInvocazioneServizio().getGetMessage().equals(StatoFunzionalita.ABILITATO));
	}

	private static String getUrlConnettore(Map<String, String> properties, String tipo, Boolean custom, boolean isMessageBox) throws Exception {
		IConnettoreApiHelper helper = ConnettoreAPIHelper.getHelper(tipo, custom, isMessageBox);
		
		if(helper == null) return null;
		return helper.getUrlConnettore(properties, tipo);
	}

	private static String CUSTOM_KEY = "CUSTOM";
	private static Map<String, IConnettoreApiHelper> helpers;
	private static Map<ConnettoreEnum, IConnettoreApiHelper> helpersByEnum;
	
	private static IConnettoreApiHelper getHelper(String tipo, Boolean custom, boolean isMessageBox) throws Exception {
		
		if(helpers== null) {
			helpers = new HashMap<>();
			ConnettoreHTTPApiHelper connHttpApiHelper = new ConnettoreHTTPApiHelper();
			helpers.put(TipiConnettore.HTTP.toString(), connHttpApiHelper);
			helpers.put(TipiConnettore.HTTPS.toString(), connHttpApiHelper);
			helpers.put(TipiConnettore.FILE.toString(), new ConnettoreFileApiHelper());
			helpers.put(TipiConnettore.JMS.toString(), new ConnettoreJmsApiHelper());
			helpers.put(TipiConnettore.NULL.toString(), new ConnettoreNullApiHelper());
			helpers.put(TipiConnettore.NULLECHO.toString(), new ConnettoreEchoApiHelper());
			helpers.put(TipiConnettore.DISABILITATO.toString(), new ConnettoreMessageBoxApiHelper());
			helpers.put(CUSTOM_KEY, new ConnettorePluginApiHelper());
		}
		
		if(helpers.containsKey(tipo)) {
			
			IConnettoreApiHelper iConnettoreApiHelper = helpers.get(tipo);
			if(iConnettoreApiHelper instanceof ConnettoreMessageBoxApiHelper && !isMessageBox) {
				return null; //Connettore disabilitato
			}
			return iConnettoreApiHelper;
		} else if(custom && helpers.containsKey(CUSTOM_KEY)) {
			return helpers.get(CUSTOM_KEY);
		} else {
			return null;
//			throw new Exception("Connettore tipo ["+tipo+"] custom ["+custom+"] non supportato");
		}
	}

	private static IConnettoreApiHelper getHelper(ConnettoreEnum tipo) throws Exception {
		
		if(helpersByEnum== null) {
			helpersByEnum = new HashMap<>();
			helpersByEnum.put(ConnettoreEnum.HTTP, new ConnettoreHTTPApiHelper());
			helpersByEnum.put(ConnettoreEnum.FILE, new ConnettoreFileApiHelper());
			helpersByEnum.put(ConnettoreEnum.JMS, new ConnettoreJmsApiHelper());
			helpersByEnum.put(ConnettoreEnum.NULL, new ConnettoreNullApiHelper());
			helpersByEnum.put(ConnettoreEnum.ECHO, new ConnettoreEchoApiHelper());
			helpersByEnum.put(ConnettoreEnum.PLUGIN, new ConnettorePluginApiHelper());
			helpersByEnum.put(ConnettoreEnum.MESSAGE_BOX, new ConnettoreMessageBoxApiHelper());
		}
		
		if(helpersByEnum.containsKey(tipo)) {
			return helpersByEnum.get(tipo);
		} else {
			throw new Exception("Connettore tipo ["+tipo+"] non supportato");
		}
	}

	public static String getUrlConnettore(org.openspcoop2.core.registry.Connettore connettore, boolean isRidefinito) throws Exception {
		if(isRidefinito) {
			return "Connettori ridefiniti nei gruppi";
		}

		if(connettore == null) return null;
		
		boolean isMessageBox = false; //FRUIZIONI NO MSG BOX
		return getUrlConnettore(connettore.getProperties(), connettore.getTipo(), connettore.getCustom(), isMessageBox);
	}

	public static final ConnettoreErogazione buildConnettoreErogazione(ServizioApplicativo sa) throws Exception {
		ConnettoreErogazione connettoreErogazione = new ConnettoreErogazione();
		connettoreErogazione.setConnettore(ConnettoreAPIHelper.getConnettoreErogazione(sa));
		return connettoreErogazione;
	}
	public static final ConnettoreFruizione buildConnettoreFruizione(org.openspcoop2.core.registry.Connettore connettore) throws Exception {
		ConnettoreFruizione connettoreFruizione = new ConnettoreFruizione();
		connettoreFruizione.setConnettore(ConnettoreAPIHelper.getConnettoreFruizione(connettore));
		return connettoreFruizione;
	}

	private static OneOfConnettoreFruizioneConnettore getConnettoreFruizione(
			org.openspcoop2.core.registry.Connettore connettore) throws Exception {
		IConnettoreApiHelper helper = ConnettoreAPIHelper.getHelper(connettore.getTipo(), connettore.getCustom(), false); //MessageBox disabilitato in fruizioni
		
		if(helper == null) return null;
		return helper.buildConnettoreFruizione(connettore.getProperties(), connettore.getTipo());
	}

	private static OneOfConnettoreErogazioneConnettore getConnettoreErogazione(ServizioApplicativo sa) throws Exception {
		IConnettoreApiHelper helper = ConnettoreAPIHelper.getHelper(sa.getInvocazioneServizio().getConnettore().getTipo(), sa.getInvocazioneServizio().getConnettore().getCustom(), sa.getInvocazioneServizio().getGetMessage().equals(StatoFunzionalita.ABILITATO));
		
		if(helper == null) return null;
		return helper.buildConnettoreErogazione(sa);
	}

	public static OneOfApplicativoServerConnettore getConnettoreApplicativoServer(ServizioApplicativo sa) throws Exception {
		IConnettoreApiHelper helper = ConnettoreAPIHelper.getHelper(sa.getInvocazioneServizio().getConnettore().getTipo(), sa.getInvocazioneServizio().getConnettore().getCustom(), sa.getInvocazioneServizio().getGetMessage().equals(StatoFunzionalita.ABILITATO));
		
		if(helper == null) return null;
		return helper.buildConnettoreApplicativoServer(sa);
	}

	public static boolean connettoreCheckData(OneOfApplicativoServerConnettore connettore, ErogazioniEnv erogEnv, boolean erogazione) throws Exception {
		IConnettoreApiHelper helper = ConnettoreAPIHelper.getHelper(connettore.getTipo());
		
		if(helper == null) return true;
		return helper.connettoreCheckData(connettore, erogEnv, erogazione);
	}

	public static boolean connettoreCheckData(OneOfConnettoreErogazioneConnettore connettore, ErogazioniEnv erogEnv, boolean erogazione) throws Exception {
		IConnettoreApiHelper helper = ConnettoreAPIHelper.getHelper(connettore.getTipo()); 
		
		if(helper == null) return true;
		return helper.connettoreCheckData(connettore, erogEnv, erogazione);
	}

	public static boolean connettoreCheckData(OneOfConnettoreFruizioneConnettore connettore, ErogazioniEnv erogEnv, boolean erogazione) throws Exception {
		IConnettoreApiHelper helper = ConnettoreAPIHelper.getHelper(connettore.getTipo());
		
		if(helper == null) return true;
		return helper.connettoreCheckData(connettore, erogEnv, erogazione);
	}

	public static void fillConnettoreConfigurazione(ServizioApplicativo sa, ErogazioniEnv erogEnv, OneOfApplicativoServerConnettore connettore, String oldConnT) throws Exception {
		IConnettoreApiHelper helper = ConnettoreAPIHelper.getHelper(connettore.getTipo());
		
		if(helper == null) return;
		helper.buildConnettoreConfigurazione(sa, erogEnv, connettore, oldConnT);
	}

	public static void fillConnettoreConfigurazione(ServizioApplicativo sa, ErogazioniEnv erogEnv, OneOfConnettoreErogazioneConnettore connettore, String oldConnT) throws Exception {
		IConnettoreApiHelper helper = ConnettoreAPIHelper.getHelper(connettore.getTipo());
		
		if(helper == null) return;
		helper.buildConnettoreConfigurazione(sa, erogEnv, connettore, oldConnT);
	}

	public static void fillConnettoreConfigurazione(Connettore connis, ErogazioniEnv erogEnv, OneOfConnettoreFruizioneConnettore connettore, String oldConnT) throws Exception {
		IConnettoreApiHelper helper = ConnettoreAPIHelper.getHelper(connettore.getTipo());
		
		if(helper == null) return;
		helper.buildConnettoreConfigurazione(connis, erogEnv, connettore, oldConnT);
	}

	public static void fillConnettoreRegistro(org.openspcoop2.core.registry.Connettore connis, ErogazioniEnv erogEnv, OneOfApplicativoServerConnettore connettore, String oldConnT) throws Exception {
		IConnettoreApiHelper helper = ConnettoreAPIHelper.getHelper(connettore.getTipo());
		
		if(helper == null) return;
		helper.fillConnettoreRegistro(connis, erogEnv, connettore, oldConnT);
	}

	public static void fillConnettoreRegistro(org.openspcoop2.core.registry.Connettore connis, ErogazioniEnv erogEnv, OneOfConnettoreErogazioneConnettore connettore, String oldConnT) throws Exception {
		IConnettoreApiHelper helper = ConnettoreAPIHelper.getHelper(connettore.getTipo());
		
		if(helper == null) return;
		helper.fillConnettoreRegistro(connis, erogEnv, connettore, oldConnT);
	}

	public static void fillConnettoreRegistro(org.openspcoop2.core.registry.Connettore connis, ErogazioniEnv erogEnv, OneOfConnettoreFruizioneConnettore connettore, String oldConnT) throws Exception {
		IConnettoreApiHelper helper = ConnettoreAPIHelper.getHelper(connettore.getTipo());
		
		if(helper == null) return;
		helper.fillConnettoreRegistro(connis, erogEnv, connettore, oldConnT);
	}

	public static org.openspcoop2.core.registry.Connettore buildConnettoreRegistro(ErogazioniEnv erogEnv, OneOfApplicativoServerConnettore connettore) throws Exception {
		IConnettoreApiHelper helper = ConnettoreAPIHelper.getHelper(connettore.getTipo());
		
		if(helper == null) return null;
		return helper.buildConnettoreRegistro(erogEnv, connettore);
	}

	public static org.openspcoop2.core.registry.Connettore buildConnettoreRegistro(ErogazioniEnv erogEnv, OneOfConnettoreErogazioneConnettore connettore) throws Exception {
		IConnettoreApiHelper helper = ConnettoreAPIHelper.getHelper(connettore.getTipo());
		
		if(helper == null) return null;
		return helper.buildConnettoreRegistro(erogEnv, connettore);
	}

	public static org.openspcoop2.core.registry.Connettore buildConnettoreRegistro(ErogazioniEnv erogEnv, OneOfConnettoreFruizioneConnettore connettore) throws Exception {
		IConnettoreApiHelper helper = ConnettoreAPIHelper.getHelper(connettore.getTipo());
		
		if(helper == null) return null;
		return helper.buildConnettoreRegistro(erogEnv, connettore);
	}

}