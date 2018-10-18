/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.soggetti;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.PortaDelegataSoggettoErogatore;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoCooperazionePartecipanti;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
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
					IDServizioApplicativo oldIDServizioApplicativoForUpdate = new IDServizioApplicativo();
					oldIDServizioApplicativoForUpdate.setNome(servizioApplicativo.getNome());
					oldIDServizioApplicativoForUpdate.setIdSoggettoProprietario(new IDSoggetto(this.oldtipoprov, this.oldnomeprov));
					servizioApplicativo.setOldIDServizioApplicativoForUpdate(oldIDServizioApplicativoForUpdate);
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
					asps.setTipoSoggettoErogatore(this.tipoprov);
					asps.setNomeSoggettoErogatore(this.nomeprov);
					IDServizio oldIDServizioForUpdate = IDServizioFactory.getInstance().getIDServizioFromValues(asps.getTipo(), asps.getNome(), 
							new IDSoggetto(this.oldtipoprov, this.oldnomeprov), 
							asps.getVersione());
					asps.setOldIDServizioForUpdate(oldIDServizioForUpdate);

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

			// Vanno modificate tutte le Porte Delegate (appartenenti al soggetto modificato oppure che possiedono come soggetto erogatore il soggetto modificato) 
			// che presentano la seguente situazione:
			// - pattern (fruitore)/(erogatore)/....
			//   se (fruitore) contiene tipoNomeSoggetto proprietario della PortaDelegata deve essere modificato
			//   se (erogatore) contiene tipoNomeSoggetto erogatore definito all'interno della porta delegata deve essere modificato.
			List<PortaDelegata> list = new ArrayList<PortaDelegata>();
			
			FiltroRicercaPorteDelegate filtroRicerca = new FiltroRicercaPorteDelegate();
			filtroRicerca.setTipoSoggetto(this.oldtipoprov);
			filtroRicerca.setNomeSoggetto(this.oldnomeprov);
			List<IDPortaDelegata> listID = this.porteDelegateCore.getAllIdPorteDelegate(filtroRicerca);
			if(listID!=null && listID.size()>0){
				for (IDPortaDelegata idPortaDelegata : listID) {
					list.add(this.porteDelegateCore.getPortaDelegata(idPortaDelegata));
				}
			}
			filtroRicerca = new FiltroRicercaPorteDelegate();
			filtroRicerca.setTipoSoggettoErogatore(this.oldtipoprov);
			filtroRicerca.setNomeSoggettoErogatore(this.oldnomeprov);
			listID = this.porteDelegateCore.getAllIdPorteDelegate(filtroRicerca);
			if(listID!=null && listID.size()>0){
				for (IDPortaDelegata idPortaDelegata : listID) {
					list.add(this.porteDelegateCore.getPortaDelegata(idPortaDelegata));
				}
			}
			
			if(list!=null && list.size()>0){
				List<String> checkUnique = new ArrayList<String>();
				for (PortaDelegata portaDelegata : list) {
					
					if(checkUnique.contains((portaDelegata.getId().longValue()+""))){
						continue;
					}
					
					boolean modificatoNome = true;
					
					boolean soggettoProprietarioCombaciaSoggettoInModifica = false;
					String tipoNomeSoggettoFruitore = portaDelegata.getTipoSoggettoProprietario()+"_"+portaDelegata.getNomeSoggettoProprietario();
					if(tipoNomeSoggettoFruitore.equals(this.oldtipoprov+"_"+this.oldnomeprov)){
						soggettoProprietarioCombaciaSoggettoInModifica = true;
						
						portaDelegata.setTipoSoggettoProprietario(this.tipoprov);
						portaDelegata.setNomeSoggettoProprietario(this.nomeprov);
						//portaDelegata.setOldTipoSoggettoProprietarioForUpdate(this.oldtipoprov);
						//portaDelegata.setOldNomeSoggettoProprietarioForUpdate(this.oldnomeprov);
					}
					
					String nomeAttuale = portaDelegata.getNome();
					String regex = ".*\\/.*\\/.*";
					// pattern (fruitore)/(erogatore)/....
					if(nomeAttuale.matches(regex)){
						String[] tmp = nomeAttuale.split("\\/");
						String partFruitore = tmp[0];
						String partErogatore = tmp[1];
						String rimanente = "";
						int lengthParteRimanente = (partFruitore+"/"+partErogatore).length();
						if(nomeAttuale.length()>lengthParteRimanente){
							rimanente = nomeAttuale.substring(lengthParteRimanente);
						}							
						
						// se (fruitore) contiene tipoNomeSoggetto proprietario della PortaDelegata deve essere modificato
						if(tipoNomeSoggettoFruitore.equals(partFruitore)){
							if(tipoNomeSoggettoFruitore.equals(this.oldtipoprov + "_" + this.oldnomeprov)){
								partFruitore = this.tipoprov + "_" + this.nomeprov;
								modificatoNome = true;
							}
						}
						else if(("__"+tipoNomeSoggettoFruitore).equals(partFruitore)){
							if((tipoNomeSoggettoFruitore).equals(this.oldtipoprov + "_" + this.oldnomeprov)){
								partFruitore = "__"+this.tipoprov + "_" + this.nomeprov;
								modificatoNome = true;
							}
						}
						
						// se (erogatore) contiene tipoNomeSoggetto erogatore della PortaDelegata deve essere modificato
						if(portaDelegata.getSoggettoErogatore()!=null && 
								portaDelegata.getSoggettoErogatore().getTipo()!=null && !"".equals(portaDelegata.getSoggettoErogatore().getTipo()) &&
								portaDelegata.getSoggettoErogatore().getNome()!=null && !"".equals(portaDelegata.getSoggettoErogatore().getNome())){
							String tipoNomeSoggettoErogatore = portaDelegata.getSoggettoErogatore().getTipo()+"_"+portaDelegata.getSoggettoErogatore().getNome();
							if(tipoNomeSoggettoErogatore.equals(partErogatore)){
								if(tipoNomeSoggettoErogatore.equals(this.oldtipoprov + "_" + this.oldnomeprov)){
									partErogatore = this.tipoprov + "_" + this.nomeprov;
									portaDelegata.getSoggettoErogatore().setTipo(this.tipoprov);
									portaDelegata.getSoggettoErogatore().setNome(this.nomeprov);
									modificatoNome = true;
								}
							}
						}
						
						if(modificatoNome){
							String nuovoNome = partFruitore + "/" + partErogatore + rimanente;
							IDPortaDelegata oldIDPortaDelegataForUpdate = new IDPortaDelegata();
							oldIDPortaDelegataForUpdate.setNome(nomeAttuale);
							portaDelegata.setOldIDPortaDelegataForUpdate(oldIDPortaDelegataForUpdate);
							portaDelegata.setNome(nuovoNome);
							
							// modifica della descrizione
							String descrizionePD = portaDelegata.getDescrizione();
							if (descrizionePD != null && !descrizionePD.equals("")) {

								// Caso 1: subscription default
								// Subscription from gw/ENTE for service gw/ErogatoreEsterno:gw/EsempioREST:1
								String match_caso1_testFrom = " "+this.oldtipoprov+"/"+this.oldnomeprov+" ";
								if(descrizionePD.contains(match_caso1_testFrom)) {
									String replace_caso1_testFrom = " "+this.tipoprov+"/"+this.nomeprov+" ";
									descrizionePD = descrizionePD.replace(match_caso1_testFrom, replace_caso1_testFrom);
								}
								String match_caso1_testService = " "+this.oldtipoprov+"/"+this.oldnomeprov+":";
								if(descrizionePD.contains(match_caso1_testService)) {
									String replace_caso1_testService = " "+this.tipoprov+"/"+this.nomeprov+": ";
									descrizionePD = descrizionePD.replace(match_caso1_testService, replace_caso1_testService);
								}
							
								// Caso 2: altra subscription
								// Internal Subscription 'Specific1' for gw_ENTE/gw_ErogatoreEsterno/gw_EsempioREST/1
								String match_caso2_testMittente = " "+this.oldtipoprov+"_"+this.oldnomeprov+"/";
								if(descrizionePD.contains(match_caso2_testMittente)) {
									String replace_caso2_testMittente = " "+this.tipoprov+"_"+this.nomeprov+"/";
									descrizionePD = descrizionePD.replace(match_caso2_testMittente, replace_caso2_testMittente);
								}
								String match_caso2_testErogatore = "/"+this.oldtipoprov+"_"+this.oldnomeprov+"/";
								if(descrizionePD.contains(match_caso2_testErogatore)) {
									String replace_caso2_testErogatore = "/"+this.tipoprov+"_"+this.nomeprov+"/";
									descrizionePD = descrizionePD.replace(match_caso2_testErogatore, replace_caso2_testErogatore);
								}
								
								portaDelegata.setDescrizione(descrizionePD);

							}
							
							// regex del pattern azione
							// .*(fruitore)/(erogatore)/(servizio)/([^/|^?]*).*
							PortaDelegataAzione pdAzione = portaDelegata.getAzione();
							PortaDelegataAzioneIdentificazione identificazione = pdAzione != null ? pdAzione.getIdentificazione() : null;
							String patterAzione = pdAzione != null ? (pdAzione.getPattern() != null ? pdAzione.getPattern() : "") : "";
							String patternAzionePrefix = ".*/";
							String patternAzioneSuffix1 = "/([^/|^?]*).*";
							// se identificazione urlbased procedo con i controlli
							if (PortaDelegataAzioneIdentificazione.URL_BASED.equals(identificazione)) {
								if (patterAzione.startsWith(patternAzionePrefix) && patterAzione.endsWith(patternAzioneSuffix1)) {
									// caso1
									int startidx = patternAzionePrefix.length();
									int endidx = patterAzione.lastIndexOf(patternAzioneSuffix1);
									String tmpPat = patterAzione.substring(startidx, endidx);
									// a questo punto ottengo una stringa del tipo
									// (fruitore)/(erogatore)/(servizio)
									// se rispetta la regex allora vuol dire che il
									// pattern azione e' quello di default
									// e devo effettuare i cambiamenti
									if (tmpPat.matches(regex)) {
										String[] val = tmpPat.split("/");
										String fruitore = val[0];
										String erogatore = val[1];
										String rimanenteRegExp = "";
										int lengthParteRimanenteRegExp = (fruitore+"/"+erogatore).length();
										if(tmpPat.length()>lengthParteRimanenteRegExp){
											rimanenteRegExp = tmpPat.substring(lengthParteRimanenteRegExp);
										}	

										boolean match = false;
										String partOld = "(?:"+this.oldtipoprov+"_)?"+this.oldnomeprov+"";
										String partNew = "(?:"+this.tipoprov+"_)?"+this.nomeprov+"";
										
										// vedo se matcha il fruitore
										if (fruitore.equals(partOld)) {
											fruitore = partNew;
											match = true;
										}
										
										// vedo se matcha anche erogatore (loopback)
										if (erogatore.equals(partOld)) {
											erogatore = partNew;
											match = true;
										}

										if(match){
											String newPatternAzione = patternAzionePrefix + fruitore + "/" + erogatore + rimanenteRegExp + patternAzioneSuffix1;
											pdAzione.setPattern(newPatternAzione);
											portaDelegata.setAzione(pdAzione);
										}
									}
								}
							}// fine controllo azione
	
						}
					}
					
					// inserisco la porta
					if(soggettoProprietarioCombaciaSoggettoInModifica || modificatoNome){
						listaPD.put(portaDelegata.getId(), portaDelegata);
						checkUnique.add((portaDelegata.getId().longValue()+""));
					}
				}
			}


			// PORTE DELEGATE
			// Modifica servizio
			// per ogni servizio da modificare devo effettuare le modifiche
			// alle porte delegate
			for (int j=0; j<this.accordiServizioParteSpecifica.size(); j++) {

				AccordoServizioParteSpecifica asps = this.accordiServizioParteSpecifica.get(j);

				List<PortaDelegata> tmpListPD = null;
				// modifico le porte delegate interessate dal cambiamento
				// del nome del soggetto
				// recupero lo porte delegate per location
				// e aggiorno il nome e la location
				String locationPrefix = "";
				String locationSuffix = "/" + this.oldtipoprov +"_" + this.oldnomeprov + "/" + asps.getTipo()+"_" +asps.getNome() + "/" + asps.getVersione().intValue();
				for (Fruitore fruitore : asps.getFruitoreList()) {
					locationPrefix = fruitore.getTipo()+"_" + fruitore.getNome();
					String location = locationPrefix + locationSuffix;
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setNome(location);
					
					PortaDelegata portaDelegata = null;
					try {
						portaDelegata = this.porteDelegateCore.getPortaDelegata(idPD);
					}catch(DriverConfigurazioneNotFound notFound) {}
					
					if(portaDelegata!=null){

						// Per tutte le porte delegate che rispettano la
						// location cambio la vecchia location e inoltre
						// controllo pure il pattern azione

						Long idPorta = portaDelegata.getId();

						// se la porta delegata e' gia stata inserita
						// nella lista per modifiche
						// allora prendo quella ed effettuo ulteriori
						// modifiche
						if (listaPD.containsKey(idPorta)) {
							portaDelegata = listaPD.get(idPorta);
						}
						// new locationSuffix
						String newLocationSuffix = "/" + this.tipoprov+"_" +this.nomeprov + "/" + asps.getTipo()+"_" +asps.getNome() + "/" + asps.getVersione().intValue();
						String newLocationPrefix = null;
						if(fruitore.getTipo().equals(this.oldtipoprov) && fruitore.getNome().equals(this.oldnomeprov)){
							newLocationPrefix = this.tipoprov+ "_" +this.nomeprov;
						}
						else{
							newLocationPrefix = fruitore.getTipo()+ "_" +fruitore.getNome();
						}
						String newLocation = newLocationPrefix + newLocationSuffix;

						if(portaDelegata.getOldIDPortaDelegataForUpdate()==null){
							// Il vecchio nome e' gia essere stato messo prima nella passata sopra.
							IDPortaDelegata oldIDPortaDelegataForUpdate = new IDPortaDelegata();
							oldIDPortaDelegataForUpdate.setNome(portaDelegata.getNome());
							portaDelegata.setOldIDPortaDelegataForUpdate(oldIDPortaDelegataForUpdate);
						}
						portaDelegata.setNome(newLocation);
						// aggiorno la descrizione della porta
						String descrizionePD = portaDelegata.getDescrizione();
						if (descrizionePD != null && !descrizionePD.equals("")) {
							
							// Caso 1: subscription default
							// Subscription from gw/ENTE for service gw/ErogatoreEsterno:gw/EsempioREST:1
							String match_caso1_testFrom = " "+this.oldtipoprov+"/"+this.oldnomeprov+" ";
							if(descrizionePD.contains(match_caso1_testFrom)) {
								String replace_caso1_testFrom = " "+this.tipoprov+"/"+this.nomeprov+" ";
								descrizionePD = descrizionePD.replace(match_caso1_testFrom, replace_caso1_testFrom);
							}
							String match_caso1_testService = " "+this.oldtipoprov+"/"+this.oldnomeprov+":";
							if(descrizionePD.contains(match_caso1_testService)) {
								String replace_caso1_testService = " "+this.tipoprov+"/"+this.nomeprov+": ";
								descrizionePD = descrizionePD.replace(match_caso1_testService, replace_caso1_testService);
							}
						
							// Caso 2: altra subscription
							// Internal Subscription 'Specific1' for gw_ENTE/gw_ErogatoreEsterno/gw_EsempioREST/1
							String match_caso2_testMittente = " "+this.oldtipoprov+"_"+this.oldnomeprov+"/";
							if(descrizionePD.contains(match_caso2_testMittente)) {
								String replace_caso2_testMittente = " "+this.tipoprov+"_"+this.nomeprov+"/";
								descrizionePD = descrizionePD.replace(match_caso2_testMittente, replace_caso2_testMittente);
							}
							String match_caso2_testErogatore = "/"+this.oldtipoprov+"_"+this.oldnomeprov+"/";
							if(descrizionePD.contains(match_caso2_testErogatore)) {
								String replace_caso2_testErogatore = "/"+this.tipoprov+"_"+this.nomeprov+"/";
								descrizionePD = descrizionePD.replace(match_caso2_testErogatore, replace_caso2_testErogatore);
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
						String patternAzionePrefix = ".*/";
						String patternAzioneSuffix = "/([^/|^?]*).*";
						// se identificazione urlbased procedo con i
						// controlli
						if (PortaDelegataAzioneIdentificazione.URL_BASED.equals(identificazione)) {
							if (patterAzione.startsWith(patternAzionePrefix) && patterAzione.endsWith(patternAzioneSuffix)) {
								int startidx = patternAzionePrefix.length();
								int endidx = patterAzione.lastIndexOf(patternAzioneSuffix);
								String tmpPat = patterAzione.substring(startidx, endidx);
								// a questo punto ottengo una stringa del tipo
								// (fruitore)/(erogatore)/(servizio)
								// se rispetta la regex allora vuol dire che il
								// pattern azione e' quello di default
								// e devo effettuare i cambiamenti
								if (tmpPat.matches(regex)) {
									String[] val = tmpPat.split("/");
									String fruitoreA = val[0];
									String erogatore = val[1];
									String rimanenteRegExp = "";
									int lengthParteRimanenteRegExp = (fruitoreA+"/"+erogatore).length();
									if(tmpPat.length()>lengthParteRimanenteRegExp){
										rimanenteRegExp = tmpPat.substring(lengthParteRimanenteRegExp);
									}	

									boolean match = false;
									String partOld = "(?:"+this.oldtipoprov+"_)?"+this.oldnomeprov+"";
									String partNew = "(?:"+this.tipoprov+"_)?"+this.nomeprov+"";
									
									// vedo se matcha il fruitore
									if (fruitoreA.equals(partOld)) {
										fruitoreA = partNew;
										match = true;
									}
									
									// vedo se matcha anche erogatore (loopback)
									if (erogatore.equals(partOld)) {
										erogatore = partNew;
										match = true;
									}

									if(match){
										String newPatternAzione = patternAzionePrefix + fruitoreA + "/" + erogatore + rimanenteRegExp + patternAzioneSuffix;
										pdAzione.setPattern(newPatternAzione);
										portaDelegata.setAzione(pdAzione);
									}
								}

							}
						}// fine controllo azione

						// (ri)aggiungo pd
						listaPD.put(idPorta, portaDelegata);

					}// fine foreach pd
				}// fine foreach fruitore


				// recupero le porte delegate per id_soggetto_erogatore
				try {
					tmpListPD = this.porteDelegateCore.porteDelegateWithSoggettoErogatoreList(this.sog.getId());
				}catch(DriverConfigurazioneNotFound notFound) {}
				if(tmpListPD!=null && !tmpListPD.isEmpty()) {
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
				}

				// recupero le porte delegate per tipo/nome soggetto erogatore
				try {
					tmpListPD = this.porteDelegateCore.porteDelegateWithTipoNomeErogatoreList(this.sog.getOldTipoForUpdate(), this.sog.getOldNomeForUpdate());
				}catch(DriverConfigurazioneNotFound notFound) {}
				if(tmpListPD!=null && !tmpListPD.isEmpty()) {
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
			Hashtable<String, PortaApplicativa> listaPA = new Hashtable<String, PortaApplicativa>();
			Hashtable<String, ServizioApplicativo> listaPA_SA = new Hashtable<String, ServizioApplicativo>();

			if (!this.oldnomeprov.equals(this.nomeprov) || !this.oldtipoprov.equals(this.tipoprov)) {

				// SoggettoVirtuale
				List<PortaApplicativa> tmpList = this.porteApplicativeCore.getPorteApplicativeBySoggettoVirtuale(new IDSoggetto(this.oldtipoprov,this.oldnomeprov));
				for (PortaApplicativa portaApplicativa : tmpList) {
					portaApplicativa.getSoggettoVirtuale().setTipo(this.tipoprov);
					portaApplicativa.getSoggettoVirtuale().setNome(this.nomeprov);
					listaPA.put(portaApplicativa.getNome(), portaApplicativa);
				}
				
				
				// NomePorta
				// Tutte le porte applicativa di questo soggetto con nome "di default"
				// devono essere cambiate
				// Nome della Porta: 
				tmpList = this.porteApplicativeCore.porteAppList(this.sog.getId().intValue(), new Search());
				for (PortaApplicativa portaApplicativa : tmpList) {
					
					IDPortaApplicativa oldIDPortaApplicativaForUpdate = null;
					String nome = portaApplicativa.getNome();
					// se il nome e' quello di default cioe' (erogatore)/(servizio)/(versioneServizio) o (erogatore)/(servizio)/(versioneServizio)/azione
					String regex = "(.*)\\/(.*)\\/(.*)";
					if (nome.matches(regex)) {

						String[] val = nome.split("\\/");
						String patErogatore = val[0];
						String patServizio = val[1];
						String patVersioneServizio = val[2];
						String patAzione = null;
						if(val.length>3){
							patAzione = "";
							for (int i = 3; i < val.length; i++) {
								if(i>3){
									patAzione = patAzione + "/";
								}
								patAzione = patAzione + val[i];
							}
						}

						// erogatore
						if (patErogatore.equals(this.oldtipoprov+ "_" +this.oldnomeprov)) {
							patErogatore = this.tipoprov+ "_" +this.nomeprov;
						}
						else if (patErogatore.equals("__"+this.oldtipoprov+ "_" +this.oldnomeprov)) {
							patErogatore = "__"+this.tipoprov+ "_" +this.nomeprov;
						}

						String newNome = patErogatore + "/" + patServizio + "/" + patVersioneServizio ;
						if(patAzione!=null){
							newNome = newNome + "/" + patAzione;
						}
						
						portaApplicativa.setNome(newNome);
						oldIDPortaApplicativaForUpdate = new IDPortaApplicativa();
						oldIDPortaApplicativaForUpdate.setNome(nome);
						portaApplicativa.setOldIDPortaApplicativaForUpdate(oldIDPortaApplicativaForUpdate);

						// modifica della descrizione
						String descrizionePA = portaApplicativa.getDescrizione();
						if (descrizionePA != null && !descrizionePA.equals("")) {

							// Caso 1: implementation default
							// Service implementation gw/ENTE:gw/TEST:1
							String match_caso1_testService = " "+this.oldtipoprov+"/"+this.oldnomeprov+":";
							if(descrizionePA.contains(match_caso1_testService)) {
								String replace_caso1_testService = " "+this.tipoprov+"/"+this.nomeprov+": ";
								descrizionePA = descrizionePA.replace(match_caso1_testService, replace_caso1_testService);
							}
						
							// Caso 2: altra subscription
							// Internal Implementation 'Specific1' for gw_ENTE/gw_TEST/1
							String match_caso2_testService = " "+this.oldtipoprov+"_"+this.oldnomeprov+"/";
							if(descrizionePA.contains(match_caso2_testService)) {
								String replace_caso2_testService = " "+this.tipoprov+"_"+this.nomeprov+"/";
								descrizionePA = descrizionePA.replace(match_caso2_testService, replace_caso2_testService);
							}
							
							portaApplicativa.setDescrizione(descrizionePA);
						}
						
						// regex del pattern azione
						// .*(erogatore)/(servizio)/([^/|^?]*).*
						PortaApplicativaAzione paAzione = portaApplicativa.getAzione();
						PortaApplicativaAzioneIdentificazione identificazione = paAzione != null ? paAzione.getIdentificazione() : null;
						String patterAzione = paAzione != null ? (paAzione.getPattern() != null ? paAzione.getPattern() : "") : "";
						String patternAzionePrefix = ".*/";
						String patternAzioneSuffix1 = "/([^/|^?]*).*";
						// se identificazione urlbased procedo con i controlli
						if (PortaApplicativaAzioneIdentificazione.URL_BASED.equals(identificazione)) {
							if (patterAzione.startsWith(patternAzionePrefix) && patterAzione.endsWith(patternAzioneSuffix1)) {
								// caso1
								int startidx = patternAzionePrefix.length();
								int endidx = patterAzione.lastIndexOf(patternAzioneSuffix1);
								String tmpPat = patterAzione.substring(startidx, endidx);
								// a questo punto ottengo una stringa del tipo
								// (fruitore)/(erogatore)/(servizio)
								// se rispetta la regex allora vuol dire che il
								// pattern azione e' quello di default
								// e devo effettuare i cambiamenti
								if (tmpPat.matches(regex)) {
									val = tmpPat.split("/");
									String partErogatore = val[0];
									String partServizio = val[1];
									String partVersione = val[2];
									String rimanenteRegExp = "";
									int lengthParteRimanenteRegExp = (partErogatore+"/"+partServizio+"/"+partVersione).length();
									if(tmpPat.length()>lengthParteRimanenteRegExp){
										rimanenteRegExp = tmpPat.substring(lengthParteRimanenteRegExp);
									}	
									
									boolean matchURL = false;
									String partOld = "(?:"+this.oldtipoprov+"_)?"+this.oldnomeprov+"";
									String partNew = "(?:"+this.tipoprov+"_)?"+this.nomeprov+"";
									
									// vedo se matcha il fruitore
									if (partErogatore.equals(partOld)) {
										partErogatore = partNew;
										matchURL = true;
									}
									
									if(matchURL){
										String newPatternAzione = patternAzionePrefix + partErogatore+ "/" + partServizio+ "/" + partVersione + rimanenteRegExp + patternAzioneSuffix1;
										paAzione.setPattern(newPatternAzione);
										portaApplicativa.setAzione(paAzione);
									}
									
								}
							}
						}
						
						
					}// fine controllo nome
					
					// controlloSoggetti Autorizzati
					if(portaApplicativa.getSoggetti()!=null && portaApplicativa.getSoggetti().sizeSoggettoList()>0) {
						for (PortaApplicativaAutorizzazioneSoggetto portaApplicativaAuthSoggetto : portaApplicativa.getSoggetti().getSoggettoList()) {
							if (this.oldtipoprov.equals(portaApplicativaAuthSoggetto.getTipo()) && 
									this.oldnomeprov.equals(portaApplicativaAuthSoggetto.getNome())) {
								portaApplicativaAuthSoggetto.setTipo(this.tipoprov);
								portaApplicativaAuthSoggetto.setNome(this.nomeprov);
							}
						}
					}
					// fine controlloSoggetti Autorizzati	
					
					// controlloServiziApplicativi Autorizzati
					if(portaApplicativa.getServiziApplicativiAutorizzati()!=null && portaApplicativa.getServiziApplicativiAutorizzati().sizeServizioApplicativoList()>0) {
						for (PortaApplicativaAutorizzazioneServizioApplicativo portaApplicativaAuthSa : portaApplicativa.getServiziApplicativiAutorizzati().getServizioApplicativoList()) {
							if (this.oldtipoprov.equals(portaApplicativaAuthSa.getTipoSoggettoProprietario()) && 
									this.oldnomeprov.equals(portaApplicativaAuthSa.getNomeSoggettoProprietario())) {
								portaApplicativaAuthSa.setTipoSoggettoProprietario(this.tipoprov);
								portaApplicativaAuthSa.setNomeSoggettoProprietario(this.nomeprov);
							}
						}
					}
					// fine controlloSoggetti Autorizzati
										
					portaApplicativa.setTipoSoggettoProprietario(this.tipoprov);
					portaApplicativa.setNomeSoggettoProprietario(this.nomeprov);
					listaPA.put(portaApplicativa.getNome(), portaApplicativa);
					
					// modifica nome Servizi Applicativi che riflette il nome della PA
					if(oldIDPortaApplicativaForUpdate!=null && portaApplicativa.sizeServizioApplicativoList()>0) {
						for (PortaApplicativaServizioApplicativo portaApplicativaSA : portaApplicativa.getServizioApplicativoList()) {
							if(portaApplicativaSA.getNome().equals(oldIDPortaApplicativaForUpdate.getNome())) {
								// devo aggiornare il nome del SA
								IDServizioApplicativo idSA = new IDServizioApplicativo();
								idSA.setNome(oldIDPortaApplicativaForUpdate.getNome());
								idSA.setIdSoggettoProprietario(new IDSoggetto(this.oldtipoprov, this.oldnomeprov));
								ServizioApplicativo sa = this.saCore.getServizioApplicativo(idSA);
								
								IDServizioApplicativo oldIDServizioApplicativoForUpdate = new IDServizioApplicativo();
								oldIDServizioApplicativoForUpdate.setNome(sa.getNome());
								oldIDServizioApplicativoForUpdate.setIdSoggettoProprietario(idSA.getIdSoggettoProprietario());
								sa.setOldIDServizioApplicativoForUpdate(oldIDServizioApplicativoForUpdate);
								sa.setTipoSoggettoProprietario(this.tipoprov);
								sa.setNomeSoggettoProprietario(this.nomeprov);
								
								// __gw_ENTE/gw_TEST/1__Specific2
								// gw_ENTE/gw_TEST/1
								if(sa.getNome().startsWith(this.oldtipoprov+"_"+this.oldnomeprov+"/")) {
									sa.setNome(sa.getNome().replace(this.oldtipoprov+"_"+this.oldnomeprov+"/", this.tipoprov+"_"+this.nomeprov+"/"));
								}
								else if(sa.getNome().startsWith("__"+this.oldtipoprov+"_"+this.oldnomeprov+"/")) {
									sa.setNome(sa.getNome().replace("__"+this.oldtipoprov+"_"+this.oldnomeprov+"/", "__"+this.tipoprov+"_"+this.nomeprov+"/"));
								}
								listaPA_SA.put(sa.getNome(), sa);
								break;
							}
						}
					}
					// modifica nome Servizi Applicativi che riflette il nome della PA
				}

				
				// controlloSoggetti Autorizzati per le porte applicative che hanno un proprietario differente
				FiltroRicercaPorteApplicative filtroRicercaPAConAuthSoggetti = new FiltroRicercaPorteApplicative();
				filtroRicercaPAConAuthSoggetti.setIdSoggettoAutorizzato(new IDSoggetto(this.oldtipoprov, this.oldnomeprov));
				List<IDPortaApplicativa> list = this.porteApplicativeCore.getAllIdPorteApplicative(filtroRicercaPAConAuthSoggetti);
				if(list!=null && !list.isEmpty()) {
					for (IDPortaApplicativa idPortaApplicativa : list) {
						if(listaPA.containsKey(idPortaApplicativa.getNome())) {
							continue; // modifica gia' effettuata
						}
						PortaApplicativa pa = null;
						try {
							pa = this.porteApplicativeCore.getPortaApplicativa(idPortaApplicativa);
						}catch(DriverConfigurazioneNotFound notFound) {}
						if(pa!=null && pa.getSoggetti()!=null && pa.getSoggetti().sizeSoggettoList()>0) {
							for (PortaApplicativaAutorizzazioneSoggetto portaApplicativaAuthSoggetto : pa.getSoggetti().getSoggettoList()) {
								if (this.oldtipoprov.equals(portaApplicativaAuthSoggetto.getTipo()) && 
										this.oldnomeprov.equals(portaApplicativaAuthSoggetto.getNome())) {
									portaApplicativaAuthSoggetto.setTipo(this.tipoprov);
									portaApplicativaAuthSoggetto.setNome(this.nomeprov);
									listaPA.put(pa.getNome(), pa);
									break;
								}
							}
						}
					}
				}
				// fine controlloSoggetti Autorizzati per le porte applicative che hanno un proprietario differente
				
			}			

			// aggiorno le porte applicative
			Enumeration<PortaApplicativa> enPA = listaPA.elements();
			while (enPA.hasMoreElements()) {
				PortaApplicativa portaApplicativa = (PortaApplicativa) enPA.nextElement();
				this.oggettiDaAggiornare.add(portaApplicativa);
			}
			// aggiorno i servizi applicativi associati alle porte applicative
			Enumeration<ServizioApplicativo> enSA = listaPA_SA.elements();
			while (enSA.hasMoreElements()) {
				ServizioApplicativo sa = (ServizioApplicativo) enSA.nextElement();
				this.oggettiDaAggiornare.add(sa);
			}
		}
	}


	public void checkFruitori() throws DriverRegistroServiziException{
		// Fruitori nei servizi 
		if(this.soggettiCore.isRegistroServiziLocale()){
			List<AccordoServizioParteSpecifica> sfruitori = this.apsCore.servizioWithSoggettoFruitore(new IDSoggetto(this.oldtipoprov,this.oldnomeprov));
			for(int i=0; i<sfruitori.size(); i++){
				AccordoServizioParteSpecifica asps = sfruitori.get(i);
				if(asps.getTipoSoggettoErogatore().equals(this.oldtipoprov) &&
						asps.getNomeSoggettoErogatore().equals(this.oldnomeprov)){
					asps.setTipoSoggettoErogatore(this.tipoprov);
					asps.setNomeSoggettoErogatore(this.nomeprov);
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
