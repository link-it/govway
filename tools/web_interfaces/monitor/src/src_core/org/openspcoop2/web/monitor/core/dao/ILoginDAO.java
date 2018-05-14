package org.openspcoop2.web.monitor.core.dao;

import java.io.Serializable;

import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.ServiceException;

import org.openspcoop2.core.commons.search.IdSoggetto;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.bean.UserDetailsBean;
import org.openspcoop2.web.monitor.core.exception.UserInvalidException;

public interface ILoginDAO extends Serializable{

	public boolean login(String username, String pwd) throws ServiceException;

	public Soggetto getSoggetto(IdSoggetto idSog);
	
	public User getUtente(UserDetailsBean user);
	
	public UserDetailsBean loadUserByUsername(String username) throws NotFoundException, ServiceException, UserInvalidException;
}
