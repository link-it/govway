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
package org.openspcoop2.core.monitor.rs.server.api.impl.utils;

import org.openspcoop2.core.monitor.rs.server.model.EsitoTransazioneFullSearchEnum;
import org.openspcoop2.core.monitor.rs.server.model.FiltroEsito;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneErrori;

/**
 * CheckNotNull
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class CheckNotNull {

	public static EsitoTransazioneFullSearchEnum getEsitoTransazione(FiltroEsito filtro) {
		return (filtro.getTipo() != null) ? filtro.getTipo() : EsitoTransazioneFullSearchEnum.QUALSIASI;
	}
	
	public static void setTipoEsito(RicercaStatisticaDistribuzioneErrori body) {
		if(body.getEsito().getTipo()==null) {
			body.getEsito().setTipo(EsitoTransazioneFullSearchEnum.FALLITE_E_FAULT);
		}
	}
	
}
