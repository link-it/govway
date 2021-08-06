/**
 * 
 */
package org.openspcoop2.core.config.rs.server.api.impl.erogazioni;

import java.util.Map;

import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.rs.server.model.OneOfApplicativoServerConnettore;
import org.openspcoop2.core.config.rs.server.model.OneOfConnettoreErogazioneConnettore;
import org.openspcoop2.core.config.rs.server.model.OneOfConnettoreFruizioneConnettore;

public abstract class AbstractConnettoreApiHelper<T> implements IConnettoreApiHelper {

	protected abstract T getConnettore(OneOfConnettoreErogazioneConnettore conn) throws Exception;
	protected abstract T getConnettore(OneOfConnettoreFruizioneConnettore conn) throws Exception;
	protected abstract T getConnettore(OneOfApplicativoServerConnettore conn) throws Exception;

	protected abstract org.openspcoop2.core.registry.Connettore fillConnettoreRegistro(org.openspcoop2.core.registry.Connettore regConnettore, ErogazioniEnv env, T connettore, String oldConnT) throws Exception;
	protected abstract org.openspcoop2.core.config.Connettore buildConnettoreConfigurazione(org.openspcoop2.core.config.Connettore regConnettore, ErogazioniEnv env, T connettore, String oldConnT) throws Exception;

	protected org.openspcoop2.core.config.Connettore buildConnettoreConfigurazione(ServizioApplicativo sa, ErogazioniEnv env, T connettore, String oldConnT) throws Exception {
		return buildConnettoreConfigurazione(sa.getInvocazioneServizio().getConnettore(), env, connettore, oldConnT);
	}
	
	protected abstract boolean connettoreCheckData(T conn, ErogazioniEnv env, boolean erogazione) throws Exception;
	
	protected abstract T buildConnettore(Map<String, String> props, String tipo) throws Exception;
	protected T buildConnettore(ServizioApplicativo sa) throws Exception {
		return buildConnettore(sa.getInvocazioneServizio().getConnettore().getProperties(),sa.getInvocazioneServizio().getConnettore().getTipo());
	}

	@Override
	public OneOfConnettoreErogazioneConnettore buildConnettoreErogazione(ServizioApplicativo sa) throws Exception {
		return (OneOfConnettoreErogazioneConnettore) buildConnettore(sa);
	}

	@Override
	public OneOfConnettoreFruizioneConnettore buildConnettoreFruizione(Map<String, String> props, String tipo) throws Exception {
		return (OneOfConnettoreFruizioneConnettore) buildConnettore(props, tipo);
	}
	
	@Override
	public OneOfApplicativoServerConnettore buildConnettoreApplicativoServer(ServizioApplicativo sa) throws Exception {
		return (OneOfApplicativoServerConnettore) buildConnettore(sa);
	}

	@Override
	public boolean connettoreCheckData(OneOfConnettoreFruizioneConnettore conn, ErogazioniEnv env, boolean isErogazione) throws Exception {
		return connettoreCheckData(getConnettore(conn), env, isErogazione);
	}

	@Override
	public boolean connettoreCheckData(OneOfConnettoreErogazioneConnettore conn, ErogazioniEnv env, boolean isErogazione) throws Exception {
		return connettoreCheckData(getConnettore(conn), env, isErogazione);
	}

	@Override
	public boolean connettoreCheckData(OneOfApplicativoServerConnettore conn, ErogazioniEnv env, boolean isErogazione) throws Exception {
		return connettoreCheckData(getConnettore(conn), env, isErogazione);
	}

	@Override
	public org.openspcoop2.core.registry.Connettore buildConnettoreRegistro(ErogazioniEnv env,
			OneOfConnettoreFruizioneConnettore conn) throws Exception {
		org.openspcoop2.core.registry.Connettore regConnettore = new org.openspcoop2.core.registry.Connettore();
		return fillConnettoreRegistro(regConnettore, env, getConnettore(conn), "");
	}

	@Override
	public org.openspcoop2.core.registry.Connettore buildConnettoreRegistro(ErogazioniEnv env,
			OneOfConnettoreErogazioneConnettore conn) throws Exception {
		org.openspcoop2.core.registry.Connettore regConnettore = new org.openspcoop2.core.registry.Connettore();
		return fillConnettoreRegistro(regConnettore, env, getConnettore(conn), "");
	}

	@Override
	public org.openspcoop2.core.registry.Connettore buildConnettoreRegistro(ErogazioniEnv env,
			OneOfApplicativoServerConnettore conn) throws Exception {
		org.openspcoop2.core.registry.Connettore regConnettore = new org.openspcoop2.core.registry.Connettore();
		return fillConnettoreRegistro(regConnettore, env, getConnettore(conn), "");
	}

	@Override
	public org.openspcoop2.core.config.Connettore buildConnettoreConfigurazione(
			org.openspcoop2.core.config.Connettore regConnettore, ErogazioniEnv env, OneOfConnettoreFruizioneConnettore conn,
			String oldConnT) throws Exception {
		return buildConnettoreConfigurazione(regConnettore, env, getConnettore(conn), oldConnT);

	}

	@Override
	public org.openspcoop2.core.config.Connettore buildConnettoreConfigurazione(
			ServizioApplicativo sa, ErogazioniEnv env, OneOfConnettoreErogazioneConnettore conn,
			String oldConnT) throws Exception {
		return buildConnettoreConfigurazione(sa, env, getConnettore(conn), oldConnT);

	}

	@Override
	public org.openspcoop2.core.config.Connettore buildConnettoreConfigurazione(
			ServizioApplicativo sa, ErogazioniEnv env, OneOfApplicativoServerConnettore conn,
			String oldConnT) throws Exception {
		return buildConnettoreConfigurazione(sa, env, getConnettore(conn), oldConnT);

	}

	@Override
	public org.openspcoop2.core.registry.Connettore fillConnettoreRegistro(
			org.openspcoop2.core.registry.Connettore regConnettore, ErogazioniEnv env, OneOfConnettoreFruizioneConnettore conn,
			String oldConnT) throws Exception {
		return fillConnettoreRegistro(regConnettore, env, getConnettore(conn), oldConnT);

	}

	@Override
	public org.openspcoop2.core.registry.Connettore fillConnettoreRegistro(
			org.openspcoop2.core.registry.Connettore regConnettore, ErogazioniEnv env, OneOfConnettoreErogazioneConnettore conn,
			String oldConnT) throws Exception {
		return fillConnettoreRegistro(regConnettore, env, getConnettore(conn), oldConnT);

	}

	@Override
	public org.openspcoop2.core.registry.Connettore fillConnettoreRegistro(
			org.openspcoop2.core.registry.Connettore regConnettore, ErogazioniEnv env, OneOfApplicativoServerConnettore conn,
			String oldConnT) throws Exception {
		return fillConnettoreRegistro(regConnettore, env, getConnettore(conn), oldConnT);

	}


}
