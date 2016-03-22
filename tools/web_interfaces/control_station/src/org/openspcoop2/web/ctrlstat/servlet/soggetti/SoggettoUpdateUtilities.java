/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.web.ctrlstat.servlet.soggetti;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.PortaDelegataSoggettoErogatore;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataSoggettoErogatoreIdentificazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoCooperazionePartecipanti;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.ac.AccordiCooperazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;

/**
 * SoggettoUpdateUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoggettoUpdateUtilities {

	List<Object> oggettiDaAggiornare = null;
	private SoggettiCore soggettiCore;
	private ServiziApplicativiCore saCore;
	private AccordiServizioParteComuneCore apcCore;
	private AccordiServizioParteSpecificaCore apsCore;
	private PorteDelegateCore porteDelegateCore;
	private PorteApplicativeCore porteApplicativeCore;
	private AccordiCooperazioneCore acCore;
	private SoggettoCtrlStat sog;
	private String oldnomeprov;
	private String nomeprov;
	private String oldtipoprov;
	private String tipoprov;
	private IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
	private IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();

	public SoggettoUpdateUtilities(SoggettiCore soggettiCore,
			String oldnomeprov,String nomeprov,
			String oldtipoprov,String tipoprov,
			SoggettoCtrlStat sog) throws Exception{
		this.oggettiDaAggiornare = new ArrayList<Object>();
		this.soggettiCore = soggettiCore;
		this.saCore = new ServiziApplicativiCore(this.soggettiCore);
		this.apcCore = new AccordiServizioParteComuneCore(this.soggettiCore);
		this.apsCore = new AccordiServizioParteSpecificaCore(this.soggettiCore);
		this.porteDelegateCore = new PorteDelegateCore(this.soggettiCore);
		this.porteApplicativeCore = new PorteApplicativeCore(this.soggettiCore);
		this.acCore = new AccordiCooperazioneCore(this.soggettiCore);
		this.oldnomeprov = oldnomeprov;
		this.nomeprov = nomeprov;
		this.oldtipoprov = oldtipoprov;
		this.tipoprov = tipoprov;
		this.sog = sog;
	}

	public List<Object> getOggettiDaAggiornare() {
		return this.oggettiDaAggiornare;
	}
	
	public void addSoggetto(){
		this.oggettiDaAggiornare.add(this.sog);
	}

	public void checkServiziApplicativi() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		// SERVIZI - APPLICATIVI 
		// Se e' cambiato il tipo o il nome del soggetto devo effettuare la modifica dei servizi applicativi
		// poiche il cambio si riflette sul nome dei connettori del servizio applicativo

		if (!this.oldnomeprov.equals(this.nomeprov) || !this.oldtipoprov.equals(this.tipoprov)) {

			List<Long> idListSA = new ArrayList<Long>();
			List<ServizioApplicativo> listSA = new ArrayList<ServizioApplicativo>();
			List<ServizioApplicativo> tmpListSA = this.saCore.getServiziApplicativiByIdErogatore(this.sog.getId());
			for (ServizioApplicativo servizioApplicativo : tmpListSA) {
				long idSA = servizioApplicativo.getId();
				if (!idListSA.contains(idSA)) {
					idListSA.add(idSA);
					servizioApplicativo.setTipoSoggettoProprietario(this.tipoprov);
					servizioApplicativo.setNomeSoggettoProprietario(this.nomeprov);
					servizioApplicativo.setOldNomeSoggettoProprietarioForUpdate(this.oldnomeprov);
					servizioApplicativo.setOldTipoSoggettoProprietarioForUpdate(this.oldtipoprov);
					listSA.add(servizioApplicativo);
				}

			}

			// aggiungo tutti i servizi applicativi trovati
			for (ServizioApplicativo sa : listSA) {
				this.oggettiDaAggiornare.add(sa);
			}

		}

	}

	public void checkAccordiCooperazione() throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		// Accordi di Cooperazione
		// Se e' cambiato il tipo o il nome del soggetto devo effettuare la modifica degli accordi di cooperazione:
		// - soggetto referente
		// - soggetti partecipanti
		if(this.soggettiCore.isRegistroServiziLocale()){
			if (!this.oldnomeprov.equals(this.nomeprov) || !this.oldtipoprov.equals(this.tipoprov)) {

				// - Soggetto referente
				FiltroRicercaAccordi filtroRicercaAccordi = new FiltroRicercaAccordi();
				filtroRicercaAccordi.setTipoSoggettoReferente(this.oldtipoprov);
				filtroRicercaAccordi.setNomeSoggettoReferente(this.oldnomeprov);
				List<IDAccordoCooperazione> idAccordiCooperazione = null;
				try{
					idAccordiCooperazione = this.acCore.getAllIdAccordiCooperazione(filtroRicercaAccordi);
				}catch(DriverRegistroServiziNotFound dNot){}
				if(idAccordiCooperazione!=null){
					for(int i=0; i<idAccordiCooperazione.size(); i++){
						AccordoCooperazione ac = this.acCore.getAccordoCooperazione(idAccordiCooperazione.get(i));
						ac.getSoggettoReferente().setTipo(this.tipoprov);
						ac.getSoggettoReferente().setNome(this.nomeprov);

						// Check se come soggetto partecipante c'e' il soggetto stesso
						if(ac.getElencoPartecipanti()!=null){
							AccordoCooperazionePartecipanti partecipanti = ac.getElencoPartecipanti();
							for(int j=0; j<partecipanti.sizeSoggettoPartecipanteList(); j++){
								if(partecipanti.getSoggettoPartecipante(j).getTipo().equals(this.oldtipoprov) && 
										partecipanti.getSoggettoPartecipante(j).getNome().equals(this.oldnomeprov)){
									partecipanti.getSoggettoPartecipante(j).setTipo(this.tipoprov);
									partecipanti.getSoggettoPartecipante(j).setNome(this.nomeprov);
								}
							}
						}

						this.oggettiDaAggiornare.add(ac);
						//System.out.println("AC ["+IDAccordoCooperazione.getUriFromAccordo(ac)+"]");
					}
				}


				// - Soggetti Partecipanti
				List<AccordoCooperazione> acs = this.acCore.accordiCoopWithSoggettoPartecipante(new IDSoggetto(this.oldtipoprov,this.oldnomeprov));
				for(int i=0; i<acs.size(); i++){
					AccordoCooperazione ac = acs.get(i);

					// check accordi con referente (non effettuo il change 2 volte)
					boolean find = false;
					if(idAccordiCooperazione!=null){
						for(int j=0; j<idAccordiCooperazione.size(); j++){
							if(this.idAccordoCooperazioneFactory.getUriFromIDAccordo(idAccordiCooperazione.get(j)).equals(this.idAccordoCooperazioneFactory.getUriFromAccordo(ac))){
								find = true;
								break;
							}
						}
					}
					if(find==false){
						if(ac.getElencoPartecipanti()!=null){
							AccordoCooperazionePartecipanti partecipanti = ac.getElencoPartecipanti();
							for(int j=0; j<partecipanti.sizeSoggettoPartecipanteList(); j++){
								if(partecipanti.getSoggettoPartecipante(j).getTipo().equals(this.oldtipoprov) && 
										partecipanti.getSoggettoPartecipante(j).getNome().equals(this.oldnomeprov)){
									partecipanti.getSoggettoPartecipante(j).setTipo(this.tipoprov);
									partecipanti.getSoggettoPartecipante(j).setNome(this.nomeprov);
								}
							}
						}
						this.oggettiDaAggiornare.add(ac);
						//System.out.println("AC PARTECIPANTE ["+IDAccordoCooperazione.getUriFromAccordo(ac)+"]");
					}
				}
			}
		}
	}



	public void checkAccordiServizioParteComune() throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		// Accordi di Servizio
		// Se e' cambiato il tipo o il nome del soggetto devo effettuare la modifica degli accordi di servizio 
		// poiche il cambio si riflette sul soggetto gestore

		if(this.soggettiCore.isRegistroServiziLocale()){
			if (!this.oldnomeprov.equals(this.nomeprov) || !this.oldtipoprov.equals(this.tipoprov)) {

				/*try{
					as = core.getAllIdAccordiWithSoggettoReferente(new IDSoggetto(oldtipoprov,oldnomeprov));
				}catch(DriverRegistroServiziNotFound dNotFound){}
				if(as!=null){
					for(int i=0; i<as.length; i++){
						as[i].getSoggettoReferente().setTipo(tipoprov);
						as[i].getSoggettoReferente().setNome(nomeprov);
						oggettiDaAggiornare.add(as[i]);
					}
				}*/

				FiltroRicercaAccordi filtroRicercaAccordi = new FiltroRicercaAccordi();
				filtroRicercaAccordi.setTipoSoggettoReferente(this.oldtipoprov);
				filtroRicercaAccordi.setNomeSoggettoReferente(this.oldnomeprov);
				List<IDAccordo> idAccordoServizio = null;
				try{
					idAccordoServizio = this.apcCore.getAllIdAccordiServizio(filtroRicercaAccordi);
				}catch(DriverRegistroServiziNotFound dNot){}
				if(idAccordoServizio!=null){
					for(int i=0; i<idAccordoServizio.size(); i++){
						AccordoServizioParteComune as = this.apcCore.getAccordoServizio(idAccordoServizio.get(i));
						as.getSoggettoReferente().setTipo(this.tipoprov);
						as.getSoggettoReferente().setNome(this.nomeprov);
						IDAccordo idAccordoOLD = this.idAccordoFactory.getIDAccordoFromValues(as.getNome(), this.oldtipoprov, this.oldnomeprov, as.getVersione());
						as.setOldIDAccordoForUpdate(idAccordoOLD);
						this.oggettiDaAggiornare.add(as);
						//System.out.println("AS ["+IDAccordo.getUriFromAccordo(as)+"]");
					}
				}


				// - soggetti erogatore di servizi componenti
				List<AccordoServizioParteComune> ass = this.apcCore.accordiServizio_serviziComponentiConSoggettoErogatore(new IDSoggetto(this.oldtipoprov,this.oldnomeprov));
				for(int i=0; i<ass.size(); i++){
					AccordoServizioParteComune as = ass.get(i);
					// check accordi con referente (non effettuo il change 2 volte)
					boolean find = false;
					if(idAccordoServizio!=null){
						for(int j=0; j<idAccordoServizio.size(); j++){
							if(this.idAccordoFactory.getUriFromIDAccordo(idAccordoServizio.get(j)).equals(IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as))){
								find = true;
								break;
							}
						}
					}
					if(find==false){
						if(as.getServizioComposto()!=null){
							for(int j=0;j<as.getServizioComposto().sizeServizioComponenteList();j++){
								if(as.getServizioComposto().getServizioComponente(j).getTipoSoggetto().equals(this.oldtipoprov) &&
										as.getServizioComposto().getServizioComponente(j).getNomeSoggetto().equals(this.oldnomeprov)){
									as.getServizioComposto().getServizioComponente(j).setTipoSoggetto(this.tipoprov);
									as.getServizioComposto().getServizioComponente(j).setNomeSoggetto(this.nomeprov);
								}
							}
							// Devo anche controllare che non sia cambiato il soggetto referente di questo as.
							if(as.getSoggettoReferente()!=null){
								if(this.oldtipoprov.equals(as.getSoggettoReferente().getTipo()) &&
										this.oldnomeprov.equals(as.getSoggettoReferente().getNome())){
									as.getSoggettoReferente().setTipo(this.tipoprov);
									as.getSoggettoReferente().setNome(this.nomeprov);
									IDAccordo idAccordoOLD = IDAccordoFactory.getInstance().getIDAccordoFromValues(as.getNome(), this.oldtipoprov, this.oldnomeprov, as.getVersione());
									as.setOldIDAccordoForUpdate(idAccordoOLD);
								}
							}
							this.oggettiDaAggiornare.add(as);
							//System.out.println("As SERVIZIO COMPONENTE ["+IDAccordo.getUriFromAccordo(as)+"]");
						}
					}
				}

			}
		}
	}

	private Vector<AccordoServizioParteSpecifica> accordiServizioParteSpecifica = null;
	public void checkAccordiServizioParteSpecifica() throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		// SERVIZI
		// Se e' cambiato il tipo o il nome del soggetto devo effettuare la modifica dei servizi 
		// poiche il cambio si riflette sul nome dei connettori del servizio 
		this.accordiServizioParteSpecifica = new Vector<AccordoServizioParteSpecifica>();
		if (!this.oldnomeprov.equals(this.nomeprov) || !this.oldtipoprov.equals(this.tipoprov)) {
			FiltroRicercaServizi filtroRicercaServizi = new FiltroRicercaServizi();
			filtroRicercaServizi.setTipoSoggettoErogatore(this.oldtipoprov);
			filtroRicercaServizi.setNomeSoggettoErogatore(this.oldnomeprov);
			List<IDServizio> idServizio = null;
			try{
				idServizio = this.apsCore.getAllIdServizi(filtroRicercaServizi);
			}catch(DriverRegistroServiziNotFound dNot){}
			if(idServizio!=null){
				for(int i=0; i<idServizio.size(); i++){
					AccordoServizioParteSpecifica asps = this.apsCore.getServizio(idServizio.get(i));
					Servizio servizio = asps.getServizio();
					servizio.setTipoSoggettoErogatore(this.tipoprov);
					servizio.setNomeSoggettoErogatore(this.nomeprov);
					servizio.setOldTipoSoggettoErogatoreForUpdate(this.oldtipoprov);
					servizio.setOldNomeSoggettoErogatoreForUpdate(this.oldnomeprov);

					// Check accordo di Servizio
					IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune());
					if(idAccordo.getSoggettoReferente()!=null && 
							idAccordo.getSoggettoReferente().getTipo().equals(this.oldtipoprov) &&
							idAccordo.getSoggettoReferente().getNome().equals(this.oldnomeprov)){
						idAccordo.getSoggettoReferente().setTipo(this.tipoprov);
						idAccordo.getSoggettoReferente().setNome(this.nomeprov);
						asps.setAccordoServizioParteComune(this.idAccordoFactory.getUriFromIDAccordo(idAccordo));
					}

					// Fruitori
					for(int j=0; j<asps.sizeFruitoreList(); j++){
						if(asps.getFruitore(j).getTipo().equals(this.oldtipoprov) && 
								asps.getFruitore(j).getNome().equals(this.oldnomeprov)){
							asps.getFruitore(j).setTipo(this.tipoprov);
							asps.getFruitore(j).setNome(this.nomeprov);
						}
					}

					if(this.soggettiCore.isRegistroServiziLocale()){
						this.oggettiDaAggiornare.add(asps);
					}
					this.accordiServizioParteSpecifica.add(asps);
				}
			}
		}
	}


	public void checkPorteDelegate() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		// Porte Delegate
		// Se e' cambiato il tipo o il nome del soggetto devo effettuare la modifica delle porte delegate
		// poiche il cambio si riflette sul nome della porta delegata
		Hashtable<Long, PortaDelegata> listaPD = new Hashtable<Long, PortaDelegata>();

		if (!this.oldnomeprov.equals(this.nomeprov) || !this.oldtipoprov.equals(this.tipoprov)) {

			// ArrayList<Long> idListPdProprietario=new ArrayList<Long>();

			// Tutte le porte delegate di questo soggetto con la location
			// "di default"
			// devono essere cambiate
			// (1) se ci sono porte delegate in loopback cioe' se il
			// soggetto proprietario e' anche erogatore
			// allora aggiorno anche la seconda parte del pattern
			// pattern (fruitore)/(erogatore)/(servizio)
			//
			// (2) Questo soggetto ha tra le sue porte delegate anche quelle
			// automaticamente create
			// all'aggiunta di un ruolo al servizio applicativo associato al
			// soggetto
			// bisogna cambiare anche il nome di tale porta
			// il pattern di tale porta e' (nome
			// ServizioApplicativo)/(tipo+nome Soggetto)/(nome Accordo)
			List<PortaDelegata> tmpList = this.porteDelegateCore.porteDelegateList(this.sog.getId().intValue(), new Search());
			for (PortaDelegata portaDelegata : tmpList) {
				String oldLocation = portaDelegata.getLocation() != null ? portaDelegata.getLocation() : "";
				// se la location e' quella di default cioe'
				// (fruitore)/(erogatore)/(servizio)
				String regex = "(.*)\\/(.*)\\/(.*)";
				if (oldLocation.matches(regex)) {

					String[] val = oldLocation.split("\\/");
					String pat1 = val[0];
					String pat2 = val[1];
					String pat3 = val[2];

					// vedo se matcha la prima parte del pattern [il
					// fruitore (caso 1)]
					if (pat1.equals(this.oldtipoprov + this.oldnomeprov)) {
						pat1 = this.tipoprov + this.nomeprov;
					}

					// vedo se matcha la seconda parte del pattern
					// [(erogatore loopback)(caso1), (tipo+nome Soggetto)
					// (caso2)]
					if (pat2.equals(this.oldtipoprov + this.oldnomeprov)) {
						pat2 = this.tipoprov + this.nomeprov;
					}

					String newLocation = pat1 + "/" + pat2 + "/" + pat3;
					portaDelegata.setLocation(newLocation);

					// controllo se anche nome porta di default ed effettuo
					// gli stessi cambiamenti di prima
					String oldNomePD = portaDelegata.getNome();
					if (oldNomePD.matches(regex)) {
						// String newNomePD =
						// oldNomePD.replaceFirst(oldtipoprov+oldnomeprov,
						// tipoprov+nomeprov);
						// vedo se matcha il fruitore
						pat1 = val[0];
						pat2 = val[1];
						pat3 = val[2];
						// vedo se matcha la prima parte del pattern [il
						// fruitore (caso 1)]
						if (pat1.equals(this.oldtipoprov + this.oldnomeprov)) {
							pat1 = this.tipoprov + this.nomeprov;
						}
						// vedo se matcha la seconda parte del pattern
						// [(erogatore loopback)(caso1), (tipo+nome
						// Soggetto) (caso2)]
						if (pat2.equals(this.oldtipoprov + this.oldnomeprov)) {
							pat2 = this.tipoprov + this.nomeprov;
						}
						String newNomePD = pat1 + "/" + pat2 + "/" + pat3;
						portaDelegata.setNome(newNomePD);
						portaDelegata.setOldNomeForUpdate(oldNomePD);
					}

					// modifica della descrizione
					String descrizionePD = portaDelegata.getDescrizione();
					if (descrizionePD != null && !descrizionePD.equals("")) {
						// caso 2
						// pattern descrizione: Ruolo(.*)del Servizio
						// Applicativo(.*)appartenente a (pat2)
						String descrRegex = "Ruolo(.*)del Servizio Applicativo(.*)appartenente a(.*)";// +oldtipoprov+oldnomeprov;
						if (descrizionePD.matches(descrRegex)) {
							// rimpiazzo il vecchio nome con il nuovo
							descrizionePD = descrizionePD.replaceFirst((this.oldtipoprov + this.oldnomeprov), pat2);
						}

						// caso 1
						// pattern descrizione: Invocazione
						// servizio(.*)erogato da(.*) (pat1)
						descrRegex = "Invocazione servizio(.*)erogato da(.*)";// +oldtipoprov+oldnomeprov;
						if (descrizionePD.matches(descrRegex)) {
							String tmpDescrizione = descrizionePD.substring(0,descrizionePD.indexOf("erogato da")+"erogato da".length());
							descrizionePD = tmpDescrizione + " " + pat1;
							//descrizionePD.replaceFirst((oldtipoprov + oldnomeprov), pat1);
						}

						portaDelegata.setDescrizione(descrizionePD);
					}

					// aggiungo la pd alle lista di porte da modificare
					// listaPD.put(portaDelegata.getId(),portaDelegata);
					// aggiunto id alla lista per tener traccia delle pd che
					// devono esserela modificate
					// idListPdProprietario.add(portaDelegata.getId());

				}// fine controllo location

				/*
				 * CONTROLLO PATTERN AZIONE inoltre va controllato anche il
				 * pattern dell'azione in caso il pattern azione fosse
				 * URLBASED e fosse quello di default allora va cambiato i
				 * pattern di default sono 2 1)
				 * .(fruitore)/(erogatore)/(servizio)/([^/|^?]). 2) .(nome
				 * ServizioApplicativo)/(soggetto)/(nome
				 * Accordo)/[^/]+/([^/|^?]).
				 */
				// regex del pattern azione
				// .*(fruitore)/(erogatore)/(servizio)/([^/|^?]*).*
				PortaDelegataAzione pdAzione = portaDelegata.getAzione();
				PortaDelegataAzioneIdentificazione identificazione = pdAzione != null ? pdAzione.getIdentificazione() : null;
				String patterAzione = pdAzione != null ? (pdAzione.getPattern() != null ? pdAzione.getPattern() : "") : "";
				String patternAzionePrefix = ".*";
				String patternAzioneSuffix1 = "/([^/|^?]*).*";
				String patternAzioneSuffix2 = "/[^/]+/([^/|^?]*).*";
				// se identificazione urlbased procedo con i controlli
				if (PortaDelegataAzioneIdentificazione.URL_BASED.equals(identificazione)) {
					// caso 2 (prima controllo questo perche' il suffix2 e'
					// piu specifico del suffix1
					if (patterAzione.startsWith(patternAzionePrefix) && patterAzione.endsWith(patternAzioneSuffix2)) {
						// caso2
						int startidx = patternAzionePrefix.length();
						int endidx = patterAzione.lastIndexOf(patternAzioneSuffix2);
						String tmp = patterAzione.substring(startidx, endidx);
						// a questo punto ho una stringa del tipo
						// (nome ServizioApplicativo)/(soggetto)/(nome
						// Accordo)
						if (tmp.matches(regex)) {
							String[] val = tmp.split("/");
							String nomeSA = val[0];
							String soggetto = val[1];
							String nomeAccordo = val[2];

							if (soggetto.equals(this.oldtipoprov + this.oldnomeprov)) {
								soggetto = this.tipoprov + this.nomeprov;
							}

							String newPatternAzione = patternAzionePrefix + nomeSA + "/" + soggetto + "/" + nomeAccordo + patternAzioneSuffix2;
							pdAzione.setPattern(newPatternAzione);
							portaDelegata.setAzione(pdAzione);

							// //controllo se la porta delegata non e' stata
							// gia' inserita in quelle da cambiare
							// if(!idListPdProprietario.contains(portaDelegata.getId())){
							// //inserisco la porta delegata per la modifica
							// listaPDProprietario.add(portaDelegata);
							// idListPdProprietario.add(portaDelegata.getId());
							// }
						}
					} else if (patterAzione.startsWith(patternAzionePrefix) && patterAzione.endsWith(patternAzioneSuffix1)) {
						// caso1
						int startidx = patternAzionePrefix.length();
						int endidx = patterAzione.lastIndexOf(patternAzioneSuffix1);
						String tmp = patterAzione.substring(startidx, endidx);
						// a questo punto ottengo una stringa del tipo
						// (fruitore)/(erogatore)/(servizio)
						// se rispetta la regex allora vuol dire che il
						// pattern azione e' quello di default
						// e devo effettuare i cambiamenti
						if (tmp.matches(regex)) {
							String[] val = tmp.split("/");
							String fruitore = val[0];
							String erogatore = val[1];
							String servizio = val[2];

							// vedo se matcha il fruitore
							if (fruitore.equals(this.oldtipoprov + this.oldnomeprov)) {
								fruitore = this.tipoprov + this.nomeprov;
								// vedo se matcha anche erogatore (loopback)
								if (erogatore.equals(this.oldtipoprov + this.oldnomeprov)) {
									erogatore = this.tipoprov + this.nomeprov;
								}

								String newPatternAzione = patternAzionePrefix + fruitore + "/" + erogatore + "/" + servizio + patternAzioneSuffix1;
								pdAzione.setPattern(newPatternAzione);
								portaDelegata.setAzione(pdAzione);

								// //controllo se la porta delegata non e'
								// stata gia' inserita in quelle da cambiare
								// if(!idListPdProprietario.contains(portaDelegata.getId())){
								// //inserisco la porta delegata per la
								// modifica
								// listaPDProprietario.add(portaDelegata);
								// idListPdProprietario.add(portaDelegata.getId());
								// }
							}
						}
					}
				}// fine controllo azione

				/*
				 * Controllo del pattern del pattern Soggetto Erogatore
				 * .(nome ServizioApplicativo)/(soggetto)/(nome
				 * Accordo)/([^/]).
				 */
				PortaDelegataSoggettoErogatore soggettoErogatore = portaDelegata.getSoggettoErogatore();
				PortaDelegataSoggettoErogatoreIdentificazione identificazioneSE = soggettoErogatore != null ? soggettoErogatore.getIdentificazione() : null;
				String patternSE = soggettoErogatore != null ? (soggettoErogatore.getPattern() != null ? soggettoErogatore.getPattern() : "") : "";
				String patternSEprefix = ".*";
				String patternSEsuffix = "/([^/]*).*";
				// se identificazione urlbased procedo con i controlli
				if (PortaDelegataSoggettoErogatoreIdentificazione.URL_BASED.equals(identificazioneSE)) {

					if (patternSE.startsWith(patternSEprefix) && patternSE.endsWith(patternSEsuffix)) {
						int startidx = patternSEprefix.length();
						int endidx = patternSE.lastIndexOf(patternSEsuffix);
						String tmp = patternSE.substring(startidx, endidx);
						// ho una stringa del tipo (nome
						// ServizioApplicativo)/(soggetto)/(nome Accordo)
						if (tmp.matches(regex)) {
							String[] val = tmp.split("/");
							String nomeSA = val[0];
							String soggetto = val[1];
							String nomeAccordo = val[2];

							if (soggetto.equals(this.oldtipoprov + this.oldnomeprov)) {
								soggetto = this.tipoprov + this.nomeprov;
							}

							String newPatternSE = patternSEprefix + nomeSA + "/" + soggetto + "/" + nomeAccordo + patternSEsuffix;
							soggettoErogatore.setPattern(newPatternSE);
							portaDelegata.setSoggettoErogatore(soggettoErogatore);

							// //controllo se la porta delegata non e' stata
							// gia' inserita in quelle da cambiare
							// if(!idListPdProprietario.contains(portaDelegata.getId())){
							// //inserisco la porta delegata per la modifica
							// listaPDProprietario.add(portaDelegata);
							// idListPdProprietario.add(portaDelegata.getId());
							// }
						}
					}
				}

				// aggiungo la porta
				portaDelegata.setTipoSoggettoProprietario(this.tipoprov);
				portaDelegata.setNomeSoggettoProprietario(this.nomeprov);
				portaDelegata.setOldTipoSoggettoProprietarioForUpdate(this.oldtipoprov);
				portaDelegata.setOldNomeSoggettoProprietarioForUpdate(this.oldnomeprov);
				listaPD.put(portaDelegata.getId(), portaDelegata);

			}// fine for porte delegate


			// PORTE DELEGATE
			// Modifica servizio
			// per ogni servizio da modificare devo effettuare le modifiche
			// alle porte delegate
			for (int j=0; j<this.accordiServizioParteSpecifica.size(); j++) {

				AccordoServizioParteSpecifica asps = this.accordiServizioParteSpecifica.get(j);
				Servizio servizio = asps.getServizio();

				List<PortaDelegata> tmpListPD = null;
				// modifico le porte delegate interessate dal cambiamento
				// del nome del soggetto
				// recupero lo porte delegate per location
				// e aggiorno il nome e la location
				String locationPrefix = "";
				String locationSuffix = "/" + this.oldtipoprov + this.oldnomeprov + "/" + servizio.getTipo() + servizio.getNome();
				for (Fruitore fruitore : asps.getFruitoreList()) {
					locationPrefix = fruitore.getTipo() + fruitore.getNome();
					String location = locationPrefix + locationSuffix;
					tmpListPD = this.porteDelegateCore.porteDelegateWithLocationList(location);

					// Per tutte le porte delegate che rispettano la
					// location cambio la vecchia location e inoltre
					// controllo pure il pattern azione

					for (PortaDelegata portaDelegata : tmpListPD) {
						Long idPorta = portaDelegata.getId();

						// se la porta delegata e' gia stata inserita
						// nella lista per modifiche
						// allora prendo quella ed effettuo ulteriori
						// modifiche
						if (listaPD.containsKey(idPorta)) {
							portaDelegata = listaPD.get(idPorta);
						}
						// new locationSuffix
						String newLocationSuffix = "/" + this.tipoprov + this.nomeprov + "/" + servizio.getTipo() + servizio.getNome();
						String newLocationPrefix = "";
						if(fruitore.getTipo().equals(this.oldtipoprov))
							newLocationPrefix = newLocationPrefix + this.tipoprov;
						else
							newLocationPrefix = newLocationPrefix + fruitore.getTipo();
						if(fruitore.getNome().equals(this.oldnomeprov))
							newLocationPrefix = newLocationPrefix + this.nomeprov;
						else
							newLocationPrefix = newLocationPrefix + fruitore.getNome();
						String newLocation = newLocationPrefix + newLocationSuffix;

						if(portaDelegata.getOldNomeForUpdate()==null){
							// Il vecchio nome e' gia essere stato messo prima nella passata sopra.
							portaDelegata.setOldNomeForUpdate(portaDelegata.getNome());
						}
						portaDelegata.setNome(newLocation);
						portaDelegata.setLocation(newLocation);
						// aggiorno la descrizione della porta
						String descrizionePD = portaDelegata.getDescrizione();
						if (descrizionePD != null && !descrizionePD.equals("")) {
							// pattern descrizione: Invocazione
							// servizio(.*)erogato da(.*) (old tipo/nome
							// soggetto)
							String descrRegex = "Invocazione servizio(.*)erogato da(.*)";// +oldtipoprov+oldnomeprov;
							if (  portaDelegata.getOldNomeForUpdate()==null  && descrizionePD.matches(descrRegex)) {
								String tmpDescrizione = descrizionePD.substring(0,descrizionePD.indexOf("erogato da")+"erogato da".length());
								descrizionePD = tmpDescrizione;
								//descrizionePD = descrizionePD.replaceFirst((oldtipoprov + oldnomeprov), (tipoprov + nomeprov));
							}

							portaDelegata.setDescrizione(descrizionePD);
						}
						// aggiorno anche il soggetto
						PortaDelegataSoggettoErogatore sogErogatore = portaDelegata.getSoggettoErogatore();
						sogErogatore.setTipo(this.sog.getTipo());
						sogErogatore.setNome(this.sog.getNome());
						portaDelegata.setSoggettoErogatore(sogErogatore);


						// CONTROLLO PATTERN AZIONE inoltre va
						// controllato anche il pattern dell'azione in
						// caso il pattern azione fosse URLBASED e fosse
						// quello di default allora va cambiato

						String regex = "(.*)\\/(.*)\\/(.*)";
						PortaDelegataAzione pdAzione = portaDelegata.getAzione();
						PortaDelegataAzioneIdentificazione identificazione = pdAzione != null ? pdAzione.getIdentificazione() : null;
						String patterAzione = pdAzione != null ? (pdAzione.getPattern() != null ? pdAzione.getPattern() : "") : "";
						String patternAzionePrefix = ".*";
						String patternAzioneSuffix = "/([^/|^?]*).*";
						// se identificazione urlbased procedo con i
						// controlli
						if (PortaDelegataAzioneIdentificazione.URL_BASED.equals(identificazione)) {
							if (patterAzione.startsWith(patternAzionePrefix) && patterAzione.endsWith(patternAzioneSuffix)) {
								int startidx = patternAzionePrefix.length();
								int endidx = patterAzione.lastIndexOf(patternAzioneSuffix);
								String tmp = patterAzione.substring(startidx, endidx);
								// a questo punto ottengo una stringa
								// del tipo
								// (fruitore)/(erogatore)/(servizio)
								// se rispetta la regex allora vuol dire
								// che il pattern azione e' quello di
								// default
								// e devo effettuare i cambiamenti
								if (tmp.matches(regex)) {
									// il nuovo pattern sara' come
									// quello della location di default
									String newPatternAzione = patternAzionePrefix + newLocation + patternAzioneSuffix;
									pdAzione.setPattern(newPatternAzione);
									portaDelegata.setAzione(pdAzione);

								}
							}
						}// fine controllo azione

						// (ri)aggiungo pd
						listaPD.put(idPorta, portaDelegata);

					}// fine foreach pd
				}// fine foreach fruitore


				// recupero le porte delegate per id_soggetto_erogatore
				tmpListPD = this.porteDelegateCore.porteDelegateWithSoggettoErogatoreList(this.sog.getId());
				for (PortaDelegata portaDelegata : tmpListPD) {
					Long idPorta = portaDelegata.getId();

					if (listaPD.containsKey(idPorta)) {
						// se la porta delegata e' gia stata modificata e
						// inserita
						// nella lista, allora prendo la porta inserita in
						// lista
						// ed effettuo ulteriori modifiche
						portaDelegata = listaPD.get(idPorta);
					}

					// modifico le informazioni sul soggetto erogatore
					PortaDelegataSoggettoErogatore sogErogatore = portaDelegata.getSoggettoErogatore();
					sogErogatore.setTipo(this.sog.getTipo());
					sogErogatore.setNome(this.sog.getNome());
					portaDelegata.setSoggettoErogatore(sogErogatore);
					// (re)inserisco la porta nella lista
					listaPD.put(idPorta, portaDelegata);

				}

				// recupero le porte delegate per tipo/nome soggetto
				// erogatore
				tmpListPD = this.porteDelegateCore.porteDelegateWithTipoNomeErogatoreList(this.sog.getOldTipoForUpdate(), this.sog.getOldNomeForUpdate());
				for (PortaDelegata portaDelegata : tmpListPD) {
					Long idPorta = portaDelegata.getId();
					if (listaPD.containsKey(idPorta)) {
						// se la porta delegata e' gia stata modificata e
						// inserita
						// nella lista, allora prendo la porta inserita in
						// lista
						// ed effettuo ulteriori modifiche
						portaDelegata = listaPD.get(idPorta);
					}
					// modifico informazioni su soggetto erogatore
					PortaDelegataSoggettoErogatore sogErogatore = portaDelegata.getSoggettoErogatore();
					sogErogatore.setTipo(this.sog.getTipo());
					sogErogatore.setNome(this.sog.getNome());
					portaDelegata.setSoggettoErogatore(sogErogatore);


					// inserisco la porta
					listaPD.put(idPorta, portaDelegata);

				}

			}// for each servizio



		}// if oldnome || oldtipo changed


		// aggiorno le porte delegate
		Enumeration<PortaDelegata> en = listaPD.elements();
		while (en.hasMoreElements()) {
			PortaDelegata portaDelegata = en.nextElement();
			this.oggettiDaAggiornare.add(portaDelegata);
		}
	}


	public void checkPorteApplicative() throws DriverConfigurazioneException{
		if(this.soggettiCore.isRegistroServiziLocale()){
			// PORTE APPLICATIVE
			// Se e' cambiato il tipo o il nome del soggetto virtuale devo effettuare la modifica delle porte applicative
			// poiche il cambio si riflette all'interno delle informazioni delle porte applicative
			Hashtable<Long, PortaApplicativa> listaPA = new Hashtable<Long, PortaApplicativa>();

			if (!this.oldnomeprov.equals(this.nomeprov) || !this.oldtipoprov.equals(this.tipoprov)) {

				// SoggettoVirtuale
				List<PortaApplicativa> tmpList = this.porteApplicativeCore.getPorteApplicativeBySoggettoVirtuale(new IDSoggetto(this.oldtipoprov,this.oldnomeprov));
				for (PortaApplicativa portaApplicativa : tmpList) {
					portaApplicativa.getSoggettoVirtuale().setTipo(this.tipoprov);
					portaApplicativa.getSoggettoVirtuale().setNome(this.nomeprov);
					listaPA.put(portaApplicativa.getId(), portaApplicativa);
				}
				
				
				// NomePorta
				// Tutte le porte applicativa di questo soggetto con nome "di default"
				// devono essere cambiate
				// Nome della Porta: 
				tmpList = this.porteApplicativeCore.porteAppList(this.sog.getId().intValue(), new Search());
				for (PortaApplicativa portaApplicativa : tmpList) {
					String nome = portaApplicativa.getNome();
					// se il nome e' quello di default cioe' (erogatore)/(servizio) o (erogatore)/(servizio)/azione
					String regex = "(.*)\\/(.*)";
					if (nome.matches(regex)) {

						String[] val = nome.split("\\/");
						String patErogatore = val[0];
						String patServizio = val[1];
						String patAzione = null;
						if(val.length>2){
							patAzione = "";
							for (int i = 2; i < val.length; i++) {
								if(i>2){
									patAzione = patAzione + "/";
								}
								patAzione = patAzione + val[i];
							}
						}

						// erogatore
						if (patErogatore.equals(this.oldtipoprov + this.oldnomeprov)) {
							patErogatore = this.tipoprov + this.nomeprov;
						}

						String newNome = patErogatore + "/" + patServizio;
						if(patAzione!=null){
							newNome = newNome + "/" + patAzione;
						}
						
						portaApplicativa.setNome(newNome);
						portaApplicativa.setOldNomeForUpdate(nome);

						// modifica della descrizione
						String descrizionePA = portaApplicativa.getDescrizione();
						if (descrizionePA != null && !descrizionePA.equals("")) {

							// caso 1
							// pattern descrizione: Invocazione
							// servizio(.*)erogato da(.*) (pat1)
							String descrRegex = "Servizio(.*)erogato da(.*)";
							if (descrizionePA.matches(descrRegex)) {
								String tmpDescrizione = descrizionePA.substring(0,descrizionePA.indexOf("erogato da")+"erogato da".length());
								descrizionePA = tmpDescrizione + " " + patErogatore;
								//descrizionePD.replaceFirst((oldtipoprov + oldnomeprov), pat1);
							}

							portaApplicativa.setDescrizione(descrizionePA);
						}
					}// fine controllo nome
						
					portaApplicativa.setTipoSoggettoProprietario(this.tipoprov);
					portaApplicativa.setNomeSoggettoProprietario(this.nomeprov);
					portaApplicativa.setOldTipoSoggettoProprietarioForUpdate(this.oldtipoprov);
					portaApplicativa.setOldNomeSoggettoProprietarioForUpdate(this.oldnomeprov);
					listaPA.put(portaApplicativa.getId(), portaApplicativa);
				}

			}

			// aggiorno le porte applicative
			Enumeration<PortaApplicativa> enPA = listaPA.elements();
			while (enPA.hasMoreElements()) {
				PortaApplicativa portaApplicativa = (PortaApplicativa) enPA.nextElement();
				this.oggettiDaAggiornare.add(portaApplicativa);
			}
		}
	}


	public void checkFruitori() throws DriverRegistroServiziException{
		// Fruitori nei servizi 
		if(this.soggettiCore.isRegistroServiziLocale()){
			List<AccordoServizioParteSpecifica> sfruitori = this.apsCore.servizioWithSoggettoFruitore(new IDSoggetto(this.oldtipoprov,this.oldnomeprov));
			for(int i=0; i<sfruitori.size(); i++){
				AccordoServizioParteSpecifica asps = sfruitori.get(i);
				Servizio s = asps.getServizio();
				if(s.getTipoSoggettoErogatore().equals(this.oldtipoprov) &&
						s.getNomeSoggettoErogatore().equals(this.oldnomeprov)){
					s.setTipoSoggettoErogatore(this.tipoprov);
					s.setNomeSoggettoErogatore(this.nomeprov);
				}
				// Check accordo di Servizio
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
				if(idAccordo.getSoggettoReferente()!=null && 
						idAccordo.getSoggettoReferente().getTipo().equals(this.oldtipoprov) &&
						idAccordo.getSoggettoReferente().getNome().equals(this.oldnomeprov)){
					idAccordo.getSoggettoReferente().setTipo(this.tipoprov);
					idAccordo.getSoggettoReferente().setNome(this.nomeprov);
					asps.setAccordoServizioParteComune(IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordo));
				}	
				// Fruitori
				for(int j=0; j<asps.sizeFruitoreList(); j++){
					if(asps.getFruitore(j).getTipo().equals(this.oldtipoprov) && 
							asps.getFruitore(j).getNome().equals(this.oldnomeprov)){
						asps.getFruitore(j).setTipo(this.tipoprov);
						asps.getFruitore(j).setNome(this.nomeprov);
					}
				}			
				this.oggettiDaAggiornare.add(asps);
			}
		}
	}

	
}
