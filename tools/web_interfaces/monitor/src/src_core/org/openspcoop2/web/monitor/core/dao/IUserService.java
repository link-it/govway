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
package org.openspcoop2.web.monitor.core.dao;

import java.util.Date;
import java.util.List;

import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.web.lib.users.dao.Stato;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.lib.users.dao.UserPassword;

/**
 * IUserService
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public interface IUserService extends IService<User, String>{

	public Stato getTableState(String nomeTabella,User utente);

	public void saveTableState(String nomeTabella, User user, Stato stato);
	
	public void savePassword(Long idUser, String login, String newPassword, Date dataAggiornamentoPassword) throws Exception;
	
	public void savePasswordStorico(Long idUser, String login, String newPassword, Date dataAggiornamentoPassword, List<UserPassword> storicoPassword) throws Exception;
	
	public void salvaModalita(String login, String modalita) throws NotFoundException, ServiceException;
	
	public void salvaSoggettoPddMonitor(String login, String soggetto) throws NotFoundException, ServiceException;
}
