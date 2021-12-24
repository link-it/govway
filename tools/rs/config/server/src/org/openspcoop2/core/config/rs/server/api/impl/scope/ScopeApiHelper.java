/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.rs.server.api.impl.scope;
import org.openspcoop2.core.config.rs.server.api.impl.Enums;
import org.openspcoop2.core.config.rs.server.api.impl.HttpRequestWrapper;
import org.openspcoop2.core.config.rs.server.model.Scope;
import org.openspcoop2.core.config.rs.server.model.ScopeItem;
import org.openspcoop2.web.ctrlstat.servlet.scope.ScopeCostanti;

/**
 * ScopeApiHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ScopeApiHelper {
	
	
	

	public static final org.openspcoop2.core.registry.Scope apiScopeToRegistroScope(Scope s, String userLogin) {
		org.openspcoop2.core.registry.Scope ret = new org.openspcoop2.core.registry.Scope();
		
		ret.setNome(s.getNome());
		if (s.getIdentificativoEsterno() != null)
			ret.setNomeEsterno(s.getIdentificativoEsterno().trim());
		
		ret.setContestoUtilizzo(Enums.apiContestoToRegistroContesto.get(s.getContesto()));
		ret.setDescrizione(s.getDescrizione());
		ret.setSuperUser(userLogin);
		
		return ret;
	}
	
	public static final ScopeItem registroScopeToScopeItem(org.openspcoop2.core.registry.Scope scope) {
		ScopeItem ret = new ScopeItem();
		ret.setContesto(Enums.registroContestoToApiContesto.get(scope.getContestoUtilizzo()));
		ret.setNome(scope.getNome());
		return ret;
	}
	
	public static final Scope registroScopeToScope(org.openspcoop2.core.registry.Scope scope) {
		Scope ret = new Scope();
		ret.setContesto(Enums.registroContestoToApiContesto.get(scope.getContestoUtilizzo()));
		ret.setNome(scope.getNome());
		ret.setDescrizione(scope.getDescrizione());
		ret.setIdentificativoEsterno(scope.getNomeEsterno());
		return ret;
	}
	
	public static final void ovverrideParameters(HttpRequestWrapper wrap, Scope scope) {

		wrap.overrideParameter(ScopeCostanti.PARAMETRO_SCOPE_NOME, scope.getNome());
		wrap.overrideParameter(ScopeCostanti.PARAMETRO_SCOPE_DESCRIZIONE, scope.getDescrizione());
		wrap.overrideParameter(ScopeCostanti.PARAMETRO_SCOPE_NOME_ESTERNO, scope.getIdentificativoEsterno());
		
	}
}
