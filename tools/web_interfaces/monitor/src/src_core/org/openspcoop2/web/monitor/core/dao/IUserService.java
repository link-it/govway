package org.openspcoop2.web.monitor.core.dao;

import java.util.List;

import it.link.pdd.core.utenti.Ruolo;
import it.link.pdd.core.utenti.StatoTabella;
import it.link.pdd.core.utenti.Utente;
import org.openspcoop2.web.monitor.core.dao.ISearchFormService;
import org.openspcoop2.web.monitor.core.bean.UtentiSearchForm;

public interface IUserService extends ISearchFormService<Utente, String, UtentiSearchForm>{

	public Utente find(Utente utente);
	
	public List<Ruolo> getRoles(Utente utente) ;
	
	public List<String> getTipiNomiSoggettiAssociati(Utente utente);
	
	public List<String> nomeUtenteAutoComplete(String val);
	
	public StatoTabella getTableState(String nomeTabella,Utente utente);

	public void saveTableState(String nomeTabella, Utente user, StatoTabella stato);
	
}
