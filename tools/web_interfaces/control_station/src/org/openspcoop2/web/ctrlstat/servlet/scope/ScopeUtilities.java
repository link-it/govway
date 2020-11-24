/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.web.ctrlstat.servlet.scope;

import java.util.HashMap;
import java.util.List;

import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;

/**
 * ScopeUtilities
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ScopeUtilities {

	public static void findOggettiDaAggiornare(IDScope oldIdScope, Scope scopeNEW, ScopeCore scopeCore, List<Object> listOggettiDaAggiornare) throws Exception {
		
		// Cerco se utilizzato in porte delegate
		PorteDelegateCore pdCore = new PorteDelegateCore(scopeCore);
		FiltroRicercaPorteDelegate filtroRicercaPD = new FiltroRicercaPorteDelegate();
		filtroRicercaPD.setIdScope(oldIdScope);
		List<IDPortaDelegata> listPD = pdCore.getAllIdPorteDelegate(filtroRicercaPD);
		if(listPD!=null && listPD.size()>0){
			for (IDPortaDelegata idPD : listPD) {
				PortaDelegata portaDelegata = pdCore.getPortaDelegata(idPD);
				if(portaDelegata.getScope()!=null){
					for (org.openspcoop2.core.config.Scope scopeConfig : portaDelegata.getScope().getScopeList()) {
						if(scopeConfig.getNome().equals(oldIdScope.getNome())){
							scopeConfig.setNome(scopeNEW.getNome());
						}
					}
				}
				listOggettiDaAggiornare.add(portaDelegata);
			}
		}
		
		
		
		// Cerco se utilizzato in porte applicative
		PorteApplicativeCore paCore = new PorteApplicativeCore(scopeCore);
		FiltroRicercaPorteApplicative filtroRicercaPA = new FiltroRicercaPorteApplicative();
		filtroRicercaPA.setIdScope(oldIdScope);
		List<IDPortaApplicativa> listPA = paCore.getAllIdPorteApplicative(filtroRicercaPA);
		if(listPA!=null && listPA.size()>0){
			for (IDPortaApplicativa idPA : listPA) {
				PortaApplicativa portaApplicativa = paCore.getPortaApplicativa(idPA);
				if(portaApplicativa.getScope()!=null){
					for (org.openspcoop2.core.config.Scope scopeConfig : portaApplicativa.getScope().getScopeList()) {
						if(scopeConfig.getNome().equals(oldIdScope.getNome())){
							scopeConfig.setNome(scopeNEW.getNome());
						}
					}
				}
				listOggettiDaAggiornare.add(portaApplicativa);
			}
		}
		
	}
	
	public static boolean deleteScope(Scope scope, String userLogin, ScopeCore scopeCore, ScopeHelper scopeHelper, StringBuilder inUsoMessage, String newLine) throws Exception {
		HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
		boolean normalizeObjectIds = !scopeHelper.isModalitaCompleta();
		boolean scopeInUso = scopeCore.isScopeInUso(scope.getNome(),whereIsInUso,normalizeObjectIds);
		
		if (scopeInUso) {
			inUsoMessage.append(DBOggettiInUsoUtils.toString(new IDScope(scope.getNome()), whereIsInUso, true, newLine));
			inUsoMessage.append(newLine);

		} else {
			scopeCore.performDeleteOperation(userLogin, scopeHelper.smista(), scope);
			
			return true;
		}
		
		return false;
	}
	
}
