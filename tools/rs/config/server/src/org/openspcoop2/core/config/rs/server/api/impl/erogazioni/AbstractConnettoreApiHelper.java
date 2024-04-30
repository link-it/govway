/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.rs.server.api.impl.erogazioni;

import static org.openspcoop2.utils.service.beans.utils.BaseHelper.evalnull;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.rs.server.model.ConnettoreConfigurazioneHttpBasic;
import org.openspcoop2.core.config.rs.server.model.ConnettoreHttp;
import org.openspcoop2.core.config.rs.server.model.OneOfApplicativoServerConnettore;
import org.openspcoop2.core.config.rs.server.model.OneOfConnettoreErogazioneConnettore;
import org.openspcoop2.core.config.rs.server.model.OneOfConnettoreFruizioneConnettore;

/**
 * AbstractConnettoreApiHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
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
		T connettore = buildConnettore(sa.getInvocazioneServizio().getConnettore().getProperties(),sa.getInvocazioneServizio().getConnettore().getTipo());
		if(sa.getInvocazioneServizio().getCredenziali()!=null && 
				sa.getInvocazioneServizio().getCredenziali().getUser()!=null &&
				sa.getInvocazioneServizio().getCredenziali().getPassword()!=null &&
				connettore instanceof ConnettoreHttp) {
			ConnettoreHttp c = (ConnettoreHttp) connettore;
		
			ConnettoreConfigurazioneHttpBasic http = new ConnettoreConfigurazioneHttpBasic();
			http.setPassword(evalnull( () -> sa.getInvocazioneServizio().getCredenziali().getPassword())); 
			http.setUsername(evalnull( () -> sa.getInvocazioneServizio().getCredenziali().getUser()));
			if ( !StringUtils.isAllEmpty(http.getPassword(), http.getUsername()) ) {
				c.setAutenticazioneHttp(http);
			}
		}
		return connettore;
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
