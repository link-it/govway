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
package org.openspcoop2.web.monitor.core.dao;

import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.search.AccordoServizioParteComune;
import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComune;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo;
import org.openspcoop2.core.commons.search.PortType;
import org.openspcoop2.core.commons.search.PortaApplicativa;
import org.openspcoop2.core.commons.search.PortaDelegata;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.constants.TipoPdD;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.web.monitor.core.core.PermessiUtenteOperatore;



/****
 * 
 * Interfaccia che definisce i metodi per il supporto ai componenti dinamici dell'interfaccia grafica
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public interface IDynamicUtilsService {
	
	
	public int countPdD(String protocollo);

	/***
	 * 
	 * supporto all'autocompletamento dell'inpu soggetti
	 * 
	 * @param input
	 * @return Soggetti trovati
	 */
	public List<Soggetto> soggettiAutoComplete(String tipoProtocollo,String input) ;
	public List<Soggetto> soggettiAutoComplete(String tipoProtocollo,String input,Boolean searchTipo) ;
	
	
//	/****
//	 * 
//	 * Restituisce l'elenco dei soggetti
//	 * 
//	 * @return
//	 */
//	public List<Soggetto> findElencoSoggetti();
//	
//	public int countElencoSoggetti();
//	
	
	/****
	 * 
	 * Restituisce l'elenco dei soggetti
	 * 
	 * @return Soggetti trovati
	 */
	public List<Soggetto> findElencoSoggetti(String tipoProtocollo, String idPorta);
	public int countElencoSoggetti(String tipoProtocollo, String idPorta);
	public List<Soggetto> findElencoSoggetti(String tipoProtocollo, String idPorta, String input);
	public int countElencoSoggetti(String tipoProtocollo, String idPorta, String input);
	public List<Soggetto> findElencoSoggetti(String tipoProtocollo, String idPorta, String input,Boolean searchTipo);
	public int countElencoSoggetti(String tipoProtocollo, String idPorta, String input,Boolean searchTipo);
	
	/****
	 * 
	 * Restituisce il soggetto di tipo e nome passati come parametri
	 * 
	 * @param tipoSoggetto
	 * @param nomeSoggetto
	 * @return Soggetto trovato
	 */
	public Soggetto findSoggettoByTipoNome(String tipoSoggetto,	String nomeSoggetto);
	
	/****
	 * 
	 * Restituisce il soggetto con id passato come parametri
	 *
	 * @param idSoggetto
	 * @return Soggetto trovato
	 */
	public Soggetto findSoggettoById(Long idSoggetto);
	
	/***
	 * 
	 * Restituisce l'elenco dei soggetti con il tipo passato come parametro
	 * 
	 * @param tipoSoggetto
	 * @return Soggetti trovati
	 */
	public List<Soggetto> findElencoSoggettiFromTipoSoggetto(String tipoSoggetto);
	
	public int countElencoSoggettiFromTipoSoggetto(String tipoSoggetto);
	
	
	/***
	 * 
	 * Restituisce l'elenco dei soggetti con il tipo PdD passato come parametro
	 * 
	 * @param tipoPdD
	 * @return Soggetti trovati
	 */
	public List<Soggetto> findElencoSoggettiFromTipoPdD(String tipoProtocollo, TipoPdD tipoPdD);
	
	public int countElencoSoggettiFromTipoTipoPdD(String tipoProtocollo, TipoPdD tipoPdD);
	
	public boolean checkTipoPdd(String nome,TipoPdD tipoPdD);
	
	/***
	 * 
	 * Restituisce l'elenco dei servizi associati al soggetto passato come parametro
	 * 
	 * 
	 * La Mappa contiene 
	 * 
	 * Nome Servizio 
	 * AccordoServizioParteComune
	 * 
	 * @param soggetto
	 * @return Servizi trovati
	 */
	public List<Map<String, Object>> findElencoServizi(String tipoProtocollo,Soggetto soggetto) ;
	
	public int countElencoServizi(String tipoProtocollo,Soggetto soggetto) ;
	
	/***
	 * 
	 * Restituisce l'elenco dei servizi associati al soggetto passato come parametro
	 * 
	 * 
	 * La Mappa contiene 
	 * 
	 * Nome Servizio 
	 * AccordoServizioParteComune
	 * 
	 * @param soggetto
	 * @return Servizi trovati
	 */
	public List<Map<String, Object>> findElencoServizi(String tipoProtocollo,Soggetto soggetto, String val) ;
	public List<Map<String, Object>> findElencoServizi(String tipoProtocollo,Soggetto soggetto, String val,Boolean searchTipo) ;
	
	
	/***
	 * Restituisce l'accordo di servizio parte comune relativo al servizio passato
	 * 
	 * @param tipoProtocollo
	 * @param idSoggetto
	 * @param tipoServizio
	 * @param nomeServizio
	 * @return Accordi Servizio Parte Comune trovati
	 */
	public AccordoServizioParteComune getAccordoServizio(String tipoProtocollo, IDSoggetto idSoggetto, String tipoServizio, String nomeServizio, Integer versioneServizio);
	
	public List<IdAccordoServizioParteComuneGruppo> getAccordoServizioGruppi(IdAccordoServizioParteComune id);
	
	/***
	 * 
	 * Restituisce l'elenco dei servizi  
	 * 
	 * 
	 * La Mappa contiene 
	 * 
	 * Nome Servizio 
	 * AccordoServizioParteComune
	 * 
	 * @param tipoProtocollo
	 * @return Servizi trovati
	 */
	public List<Map<String, Object>> findElencoServizi(String tipoProtocollo ) ;
	
	public int countElencoServizi(String tipoProtocollo ) ;
	
	/****
	 * 
	 * Restituisce l'elenco dei nomi dei servizi applicativi associati al soggetto passato.
	 * 
	 * @param soggetto
	 * @return Servizi Applicativi trovati
	 */
	public List<Object> findElencoServiziApplicativi(String tipoProtocollo,Soggetto soggetto);


	public int countElencoServiziApplicativi(String tipoProtocollo,Soggetto soggetto);
	/****
	 * 
	 * Restituisce l'elenco delle azioni corrispondenti al servizio selezionato
	 * 
	 * @param nomeServizio
	 * @return Azione dell'Accordo trovate
	 */
	public Map<String, String> findAzioniFromServizio(String tipoProtocollo,String tipoServizio ,String nomeServizio,String tipoErogatore , String nomeErogatore, Integer versioneServizio, String val);
	
	public int countAzioniFromServizio(String tipoProtocollo,String tipoServizio ,String nomeServizio,String tipoErogatore , String nomeErogatore, Integer versioneServizio, String val);
 
	/***
	 * 
	 * Restituisce il port type associato al servizio passato come parametro
	 * 
	 * 
	 * @param idAccordo
	 * @param nomeServizio
	 * @return PortType trovati
	 */
	public PortType getPortTypeFromAccordoServizio(String tipoProtocollo,IDAccordo idAccordo ,String nomeServizio) ;
	
	public int countPortTypeFromAccordoServizio(String tipoProtocollo,IDAccordo idAccordo ,String nomeServizio) ;
	
	
	public List<IDGruppo> getGruppi(String tipoProtocollo);
	public int countGruppi(String tipoProtocollo);
	
	
	/****
	 * 
	 * Restituisce la lista degli accordi di servizio che sono erogati o utilizzano come referente il soggetto passato come parametro.
	 * 
	 * @param nomeSoggetto
	 * @return Accordi trovati
	 */
	public List<AccordoServizioParteComune> getAccordiServizio(String tipoProtocollo,String tipoSoggetto, String nomeSoggetto, Boolean isReferente, Boolean isErogatore, String tag);
	
	public int countAccordiServizio(String tipoProtocollo,String tipoSoggetto, String nomeSoggetto, Boolean isReferente, Boolean isErogatore, String tag);
	/***
	 * 
	 * Restituisce la lista dei servizi che implementano l'accordo di servizio ed erogati dal soggetto passato come parametro.
	 * 
	 * @param uriAccordoServizio
	 * @return Servizi Trovati
	 */
	public List<AccordoServizioParteSpecifica> getServizi(String tipoProtocollo,String uriAccordoServizio, String tipoSoggetto , String nomeSoggetto);
	public List<AccordoServizioParteSpecifica> getServizi(String tipoProtocollo,String uriAccordoServizio, String tipoSoggetto , String nomeSoggetto, String val);
	public List<AccordoServizioParteSpecifica> getServizi(String tipoProtocollo,String uriAccordoServizio, String tipoSoggetto , String nomeSoggetto, String val,Boolean searchTipo);
	public int countServizi(String tipoProtocollo,String uriAccordoServizio, String tipoSoggetto , String nomeSoggetto);
	
	public List<Soggetto> getSoggettiErogatoreAutoComplete(String tipoProtocollo,String uriAccordoServizio, String input);
	
	public List<Soggetto> getSoggettiFruitoreAutoComplete(String tipoProtocollo,String uriAccordoServizio  , String input);
	
	public AccordoServizioParteSpecifica getAspsFromValues(String tipoServizio, String nomeServizio, String tipoErogatore, String nomeErogatore, Integer versioneServizio);
	public AccordoServizioParteSpecifica getAspsFromId(Long idServizio);

	public List<IDServizio> getServiziErogazione(String tipoProtocollo, String tipoSoggetto , String nomeSoggetto, String val,Boolean searchTipo, Boolean distinct);
	public List<IDServizio> getServiziErogazione(String tipoProtocollo, String tipoSoggetto , String nomeSoggetto, 
			String tipoServizio ,String nomeServizio, String tipoErogatore, String nomeErogatore, Integer versioneServizio, String nomeAzione, 
			String val,Boolean searchTipo, Boolean distinct);
	public int countServiziErogazione(String tipoProtocollo, String tipoSoggetto, String nomeSoggetto, String val,Boolean searchTipo);
	
	public List<IDServizio> getConfigurazioneServiziErogazione(String tipoProtocollo, String tipoSoggetto , String nomeSoggetto, 
			String val,Boolean searchTipo, PermessiUtenteOperatore permessiUtenteOperatore, Boolean distinct);
	public List<IDServizio> getConfigurazioneServiziErogazione(String tipoProtocollo, String tipoSoggetto , String nomeSoggetto, 
			String tipoServizio ,String nomeServizio, String tipoErogatore, String nomeErogatore, Integer versioneServizio, String nomeAzione, 
			String val,Boolean searchTipo, PermessiUtenteOperatore permessiUtenteOperatore, Boolean distinct);
	public int countConfigurazioneServiziErogazione(String tipoProtocollo, String tipoSoggetto, String nomeSoggetto, 
			String tipoServizio ,String nomeServizio, String tipoErogatore, String nomeErogatore, Integer versioneServizio, String nomeAzione, 
			String val,Boolean searchTipo, PermessiUtenteOperatore permessiUtenteOperatore);
	
	public List<IDServizio> getServiziFruizione(String tipoProtocollo, String tipoSoggettoErogatore , String nomeSoggettoErogatore, String val,Boolean searchTipo, Boolean distinct);
	public List<IDServizio> getServiziFruizione(String tipoProtocollo, 
			String tipoSoggetto, String nomeSoggetto, 
			String tipoSoggettoErogatore , String nomeSoggettoErogatore, 
			String tipoServizio ,String nomeServizio, Integer versioneServizio, String nomeAzione, 
			String val,Boolean searchTipo, Boolean distinct);
	public int countServiziFruizione(String tipoProtocollo, String tipoSoggettoErogatore , String nomeSoggettoErogatore, String val,Boolean searchTipo);
	
	public List<IDServizio> getConfigurazioneServiziFruizione(String tipoProtocollo, String tipoSoggetto, String nomeSoggetto, 
			String tipoServizio ,String nomeServizio, String tipoErogatore, String nomeErogatore, Integer versioneServizio, String nomeAzione, 
			String val,Boolean searchTipo, PermessiUtenteOperatore permessiUtenteOperatore, Boolean distinct);
	public int countConfigurazioneServiziFruizione(String tipoProtocollo, String tipoSoggetto, String nomeSoggetto, 
			String tipoServizio ,String nomeServizio, String tipoErogatore, String nomeErogatore, Integer versioneServizio, String nomeAzione, 
			String val,Boolean searchTipo, PermessiUtenteOperatore permessiUtenteOperatore);

	public PortaApplicativa getPortaApplicativa(String nomePorta);
	public PortaDelegata getPortaDelegata(String nomePorta);
	
}
