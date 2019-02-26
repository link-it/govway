/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
package org.openspcoop2.utils.service.authentication.preauth.role;

import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.authority.mapping.MappableAttributesRetriever;

/**
 * AbstractRoleRetriever
 * 
 * Classe prototipo per la gestione di un elenco di ruoli custom da utilizzare come input 
 * per i check tramite la funzione: public boolean isUserInRole(String role); dell'interfaccia HttpServletRequest.
 * La classe default utilizzata da spring security effettua la lettura dei ruoli previsti dal web.xml,
 * nel caso in cui i ruoli siano definiti come generici '*' prova a invocare il metodo  isUserInRole con parametro '*' facendo fallire l'autenticazione.
 * definire la funzione 'getElencoRuoliCustomAbilitati' restituendo l'elenco dei ruoli abilitati per l'applicazione.
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractRoleRetriever implements ResourceLoaderAware, MappableAttributesRetriever, InitializingBean {
	
	@Override
	public void afterPropertiesSet() throws Exception {
	}

	@Override
	public Set<String> getMappableAttributes() {
		return getElencoRuoliCustomAbilitati();
	}

	@Override
	public void setResourceLoader(ResourceLoader arg0) {
	}

	protected abstract Set<String> getElencoRuoliCustomAbilitati();
}