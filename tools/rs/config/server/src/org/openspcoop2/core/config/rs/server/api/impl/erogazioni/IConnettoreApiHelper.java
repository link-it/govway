/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import java.util.Map;

import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.rs.server.model.OneOfApplicativoServerConnettore;
import org.openspcoop2.core.config.rs.server.model.OneOfConnettoreErogazioneConnettore;
import org.openspcoop2.core.config.rs.server.model.OneOfConnettoreFruizioneConnettore;

/**
 * IConnettoreApiHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public interface IConnettoreApiHelper {

	public boolean connettoreCheckData(OneOfConnettoreErogazioneConnettore conn, ErogazioniEnv env, boolean erogazione) throws Exception;
	public boolean connettoreCheckData(OneOfConnettoreFruizioneConnettore conn, ErogazioniEnv env, boolean erogazione) throws Exception;
	public boolean connettoreCheckData(OneOfApplicativoServerConnettore conn, ErogazioniEnv env, boolean erogazione) throws Exception;
	
	public org.openspcoop2.core.registry.Connettore fillConnettoreRegistro(org.openspcoop2.core.registry.Connettore regConnettore,ErogazioniEnv env,OneOfConnettoreErogazioneConnettore conn,String oldConnT) throws Exception;
	public org.openspcoop2.core.registry.Connettore fillConnettoreRegistro(org.openspcoop2.core.registry.Connettore regConnettore,ErogazioniEnv env,OneOfConnettoreFruizioneConnettore conn,String oldConnT) throws Exception;
	public org.openspcoop2.core.registry.Connettore fillConnettoreRegistro(org.openspcoop2.core.registry.Connettore regConnettore,ErogazioniEnv env,OneOfApplicativoServerConnettore conn,String oldConnT) throws Exception;
	
	public org.openspcoop2.core.registry.Connettore buildConnettoreRegistro(ErogazioniEnv env,OneOfConnettoreErogazioneConnettore conn) throws Exception;
	public org.openspcoop2.core.registry.Connettore buildConnettoreRegistro(ErogazioniEnv env,OneOfConnettoreFruizioneConnettore conn) throws Exception;
	public org.openspcoop2.core.registry.Connettore buildConnettoreRegistro(ErogazioniEnv env,OneOfApplicativoServerConnettore conn) throws Exception;
	

	public org.openspcoop2.core.config.Connettore buildConnettoreConfigurazione(ServizioApplicativo sa, ErogazioniEnv env, OneOfConnettoreErogazioneConnettore conn, String oldConnType) throws Exception;
	public org.openspcoop2.core.config.Connettore buildConnettoreConfigurazione(org.openspcoop2.core.config.Connettore regConnettore, ErogazioniEnv env, OneOfConnettoreFruizioneConnettore conn, String oldConnType) throws Exception;
	public org.openspcoop2.core.config.Connettore buildConnettoreConfigurazione(ServizioApplicativo sa, ErogazioniEnv env, OneOfApplicativoServerConnettore conn, String oldConnType) throws Exception;
	
	public OneOfConnettoreErogazioneConnettore buildConnettoreErogazione(ServizioApplicativo sa) throws Exception;
	public OneOfConnettoreFruizioneConnettore buildConnettoreFruizione(Map<String, String> props, String tipo) throws Exception;
	public OneOfApplicativoServerConnettore buildConnettoreApplicativoServer(ServizioApplicativo sa) throws Exception;

	public String getUrlConnettore(Map<String, String> props, String tipoConnettore) throws Exception ;

}
