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
package org.openspcoop2.web.monitor.core.dao;

import java.io.Serializable;

import org.openspcoop2.core.commons.search.IdSoggetto;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.utils.IVersionInfo;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.bean.UserDetailsBean;
import org.openspcoop2.web.monitor.core.exception.UserInvalidException;

/**
 * ILoginDAO
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public interface ILoginDAO extends Serializable{

	public boolean login(String username, String pwd) throws ServiceException;

	public Soggetto getSoggetto(IdSoggetto idSog);
	
	public void salvaUtente(User user) throws NotFoundException, ServiceException;
	
	public void salvaModalita(User user) throws NotFoundException, ServiceException;
	
	public void salvaSoggettoPddMonitor(User user) throws NotFoundException, ServiceException;
	
	public UserDetailsBean loadUserByUsername(String username) throws NotFoundException, ServiceException, UserInvalidException;
	
	public Configurazione readConfigurazioneGenerale() throws ServiceException;
	
	public IVersionInfo readVersionInfo() throws ServiceException;
}
