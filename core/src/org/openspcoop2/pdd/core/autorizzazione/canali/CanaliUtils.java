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
package org.openspcoop2.pdd.core.autorizzazione.canali;

import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.StatoFunzionalita;

/**
 * CanaliUtils
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CanaliUtils {

	public static String getCanale(CanaliConfigurazione configCanali, String canaleApi, PortaDelegata pd) {
		String canalePorta = null;
		if(pd!=null) {
			canalePorta = pd.getCanale();
		}
		return getCanale(configCanali, canaleApi, canalePorta);
	}
	public static String getCanale(CanaliConfigurazione configCanali, String canaleApi, PortaApplicativa pa) {
		String canalePorta = null;
		if(pa!=null) {
			canalePorta = pa.getCanale();
		}
		return getCanale(configCanali, canaleApi, canalePorta);
	}
	public static String getCanale(CanaliConfigurazione configCanali, String canaleApi, String canalePorta) {
		String canale = null;
		if(configCanali!=null && StatoFunzionalita.ABILITATO.equals(configCanali.getStato()) && configCanali.sizeCanaleList()>0) {
			
			if(canalePorta!=null && !"".equals(canalePorta)) {
				canale = canalePorta;
			}
			else if(canaleApi!=null && !"".equals(canaleApi)) {
				canale = canaleApi;
			}
			else {
				for (CanaleConfigurazione canaleConfig : configCanali.getCanaleList()) {
					if(canaleConfig.isCanaleDefault()) {
						canale = canaleConfig.getNome();
						break;
					}
				}
				if(canale==null) {
					// non dovrebbe mai succedere
					canale = configCanali.getCanale(0).getNome();
				}
			}
		}
		return canale;
	}
	
}
