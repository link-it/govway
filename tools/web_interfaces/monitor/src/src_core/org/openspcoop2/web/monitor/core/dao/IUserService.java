package org.openspcoop2.web.monitor.core.dao;

import java.util.List;

import org.openspcoop2.web.lib.users.dao.Stato;
import org.openspcoop2.web.lib.users.dao.User;

public interface IUserService extends IService<User, String>{

	public List<String> getTipiNomiSoggettiAssociati(User utente);
	
	public Stato getTableState(String nomeTabella,User utente);

	public void saveTableState(String nomeTabella, User user, Stato stato);
	
}
