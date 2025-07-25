/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import java.util.List;

import org.openspcoop2.web.lib.users.DriverUsersDBException;
import org.openspcoop2.web.lib.users.dao.RicercaUtente;
import org.openspcoop2.web.monitor.core.bean.RicercaUtenteBean;
import org.openspcoop2.web.monitor.core.bean.RicercheUtenteSearchForm;

/**
 * IRicercheUtenteService
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public interface IRicercheUtenteService extends ISearchFormService<RicercaUtenteBean,Long,RicercheUtenteSearchForm> {

	public boolean isTimeoutEvent();
	
	public List<RicercaUtenteBean> listaRicercheDisponibiliPerUtente(String login, String modulo, String modalitaRicerca, String protocollo, String soggetto);
	
	public void insertRicerca(String login, RicercaUtente ricercaPersonalizzata) throws DriverUsersDBException;
	
	public void updateRicerca(String login, RicercaUtente ricercaPersonalizzata) throws DriverUsersDBException;
	
	public int totaleRicercheUtente(String login);
	
	public RicercaUtenteBean leggiRicercaUtente(long idUtente, long idRicerca);
	
	public boolean esisteRicerca(String login, boolean escludiUtenteCorrente, // fornire login null per non filtrare per utente
			String label, String modulo, String modalitaRicerca, String visibilita);
	
	public boolean esisteRicercaPubblicaAltroUtente(String login, String label, String modulo, String modalitaRicerca);
	
	public boolean esisteRicercaPrivataUtenteCorrente(String login, String label, String modulo, String modalitaRicerca);
	
	public String calcolaLabelRicerca(String login, String label, String modulo, String modalitaRicerca, String visibilita);
}
