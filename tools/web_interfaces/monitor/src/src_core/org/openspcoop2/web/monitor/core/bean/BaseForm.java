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
package org.openspcoop2.web.monitor.core.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.resources.MapReader;

import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.core.commons.search.IdSoggetto;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.web.monitor.core.bean.UserDetailsBean;
import org.openspcoop2.web.monitor.core.core.PermessiUtenteOperatore;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.exception.UserInvalidException;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;

/****
 * BaseForm
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class BaseForm implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();
	
	// private String nomeMittente;
	// private String nomeDestinatario;
	// private String soggettoLocale;
	
	private String nomeAzione;
	private String nomeServizio;
	private String uriAccordo;
	private String servizioApplicativo;
	private String tipoNomeMittente;
	private String tipoNomeDestinatario;
	private String tipoNomeTrafficoPerSoggetto;
	private String tipoNomeSoggettoLocale;
	
	private String protocollo;
	private List<SelectItem> protocolli= null;
	private User user;
	
	public BaseForm(){
		
	}
	
	public String ripulisci(){
		ripulisciValori();
		return null;
	}

	protected void ripulisciValori(){
		this.initListener(null);
	}
	
	public void initListener(ActionEvent ae) {
		this.nomeAzione = null;
		// this.nomeDestinatario=null;
		// this.nomeMittente=null;
		this.nomeServizio = null;
		// this.soggettoLocale=null;
		// this.trafficoPerSoggetto=null;
		this.servizioApplicativo = null;
		this.tipoNomeDestinatario = null;
		this.tipoNomeMittente = null;
		this.tipoNomeSoggettoLocale = null;
		this.tipoNomeTrafficoPerSoggetto = null;
		this.protocollo = null;
	}
	
	public void soggettoLocaleSelected(ActionEvent ae){
		this.setServizioApplicativo(null);
	}

	public void destinatarioSelected(ActionEvent ae) {
		this.nomeServizio = null;
		this.nomeAzione = null;
	}

	public void servizioSelected(ActionEvent ae) {
		this.nomeAzione = null;
	}

	public void azioneSelected(ActionEvent ae) {
	}
	
	/**
	 * Se l'utente non e' impostato, allora lo prendo dal contesto di spring
	 * security Qualora l'utente fosse stato gia' impostato tramite la setUser
	 * ad esempio non sono in un contesto j2ee e quindi spring-security non lo
	 * utilizzo, allora viene ritornato l'utente gia' impostato
	 * 
	 * @return utente collegato
	 */
	public User getUser() {

		if (this.user != null) {
			// e' stato impostato manualmente l'utente
			return this.user;
		}

		return Utility.getLoggedUtente();
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public String getTipoMittente() {
		return Utility.parseTipoSoggetto(this.tipoNomeMittente);
	}

	public String getNomeMittente() {
		return Utility.parseNomeSoggetto(this.tipoNomeMittente);
	}

	/**
	 * La stringa di input sara' del tipo tipoSoggetto/nomeSoggetto
	 * 
	 * @param tipoNomeMittente
	 */
	public void setTipoNomeMittente(String tipoNomeMittente) {
		this.tipoNomeMittente = tipoNomeMittente;

		if (StringUtils.isEmpty(this.tipoNomeMittente) || "--".equals(this.tipoNomeMittente))
			this.tipoNomeMittente = null;
	}

	public String getNomeAzione() {
		return this.nomeAzione;
	}

	public void setNomeAzione(String nomeAzione) {
		this.nomeAzione = nomeAzione;

		if (StringUtils.isEmpty(nomeAzione) || "--".equals(nomeAzione))
			this.nomeAzione = null;
	}

	public String getNomeServizio() {
		return this.nomeServizio;
	}

	public void setNomeServizio(String nomeServizio) {
		this.nomeServizio = nomeServizio;

		if (StringUtils.isEmpty(nomeServizio) || "--".equals(nomeServizio))
			this.nomeServizio = null;
	}

	public String getTipoDestinatario() {
		return Utility.parseTipoSoggetto(this.tipoNomeDestinatario);
	}

	public String getNomeDestinatario() {
		return Utility.parseNomeSoggetto(this.tipoNomeDestinatario);
	}

	public void setTipoNomeDestinatario(String nomeDestinatario) {
		this.tipoNomeDestinatario = nomeDestinatario;

		if (StringUtils.isEmpty(nomeDestinatario) || "--".equals(nomeDestinatario))
			this.tipoNomeDestinatario = null;

	}
	
	public PermessiUtenteOperatore getPermessiUtenteOperatore() throws CoreException, UserInvalidException { 

		User u = getUser();
		UserDetailsBean user = new UserDetailsBean();
		user.setUtente(u);

		String tipoSoggettoLocale = null;
		String nomeSoggettoLocale = null;
		
		if(this.tipoNomeSoggettoLocale!=null && !StringUtils.isEmpty(this.tipoNomeSoggettoLocale) && !"--".equals(this.tipoNomeSoggettoLocale)){
			tipoSoggettoLocale = Utility.parseTipoSoggetto(this.tipoNomeSoggettoLocale);
			nomeSoggettoLocale = Utility.parseNomeSoggetto(this.tipoNomeSoggettoLocale);
		}
		
		return PermessiUtenteOperatore.getPermessiUtenteOperatore(user, tipoSoggettoLocale, nomeSoggettoLocale);

	}

	public String getTipoTrafficoPerSoggetto() {
		return Utility.parseTipoSoggetto(this.tipoNomeTrafficoPerSoggetto);
	}

	public String getTrafficoPerSoggetto() {
		return Utility.parseNomeSoggetto(this.tipoNomeTrafficoPerSoggetto);
	}

	public void setTipoNomeTrafficoPerSoggetto(String trafficoPerSoggetto) {
		this.tipoNomeTrafficoPerSoggetto = trafficoPerSoggetto;
		if (StringUtils.isEmpty(trafficoPerSoggetto) || "--".equals(trafficoPerSoggetto))
			this.tipoNomeTrafficoPerSoggetto = null;
	}

	public String getTipoSoggettoLocale() {
		User u = getUser();
		if (u.getSoggetti().size() == 1) {
			IDSoggetto s = u.getSoggetti().get(0);
			this.tipoNomeSoggettoLocale = s.getTipo() + "/" + s.getNome();
		}

		return Utility.parseTipoSoggetto(this.tipoNomeSoggettoLocale);
	}

	/**
	 * ritorna il nome del soggetto locale
	 * 
	 * @return Soggetto Locale
	 */
	public String getSoggettoLocale() {
		User u = getUser();
		if (u.getSoggetti().size() == 1) {
			IDSoggetto s = u.getSoggetti().get(0);
			this.tipoNomeSoggettoLocale = s.getTipo() + "/" + s.getNome();
		}

		return Utility.parseNomeSoggetto(this.tipoNomeSoggettoLocale);
	}

	public void setTipoNomeSoggettoLocale(String soggettoLocale) {
		this.tipoNomeSoggettoLocale = soggettoLocale;

		if (StringUtils.isEmpty(soggettoLocale) || "--".equals(soggettoLocale)) {
			this.tipoNomeSoggettoLocale = null;
		}
	}

	/**
	 * I nomi spcoop dei soggetti associati all'utente loggato
	 */
	public List<Soggetto> getSoggettiGestione() {
		ArrayList<Soggetto> soggetti = new ArrayList<Soggetto>();

		User u = getUser();

		// se il soggetto locale e' specificato allora ritorno solo quello
		if (StringUtils.isNotEmpty(this.tipoNomeSoggettoLocale)) {

			// nomi.add(this.soggettoLocale);
			String tipo = Utility.parseTipoSoggetto(this.tipoNomeSoggettoLocale);
			String nome = Utility.parseNomeSoggetto(this.tipoNomeSoggettoLocale);

			for (IDSoggetto idSog : u.getSoggetti()) {
				if (idSog.getTipo().equals(tipo) && idSog.getNome().equals(nome)) {
					IdSoggetto idsog2 = new IdSoggetto();
					//idsog2.setId(idSog.getId());
					idsog2.setNome(idSog.getNome());
					idsog2.setTipo(idSog.getTipo());
					Soggetto soggetto = Utility.getSoggetto(idsog2);
					soggetti.add(soggetto);
					break;
				}
			}

			return soggetti;
		} else {
			for (IDSoggetto idSog : u.getSoggetti()) {
				IdSoggetto idsog2 = new IdSoggetto();
				//idsog2.setId(idSog.getId());
				idsog2.setNome(idSog.getNome());
				idsog2.setTipo(idSog.getTipo());

				Soggetto s = Utility.getSoggetto(idsog2);
				soggetti.add(s);
			}
			return soggetti;
		}

	}

	// public void setSoggettoGestione(String soggettoGestione) {
	//
	// this.soggettoGestione = soggettoGestione;
	//
	// //l'identificativo porta dipende dal soggetto selezionato
	// this.identificativoPorta = this.soggettoGestione+"SPCoopIT";
	//
	// if(StringUtils.isEmpty(soggettoGestione) ||
	// "--".equals(soggettoGestione)){
	// this.soggettoGestione = null;
	// this.identificativoPorta = null;
	// }
	// }
	
	public String getServizioApplicativo() {
		return this.servizioApplicativo;
	}

	public void setServizioApplicativo(String servizioApplicativo) {
		this.servizioApplicativo = servizioApplicativo;

		if (StringUtils.isEmpty(servizioApplicativo) || "--".equals(servizioApplicativo))
			this.servizioApplicativo = null;
	}
	

	public String getTipoNomeMittente() {
		return this.tipoNomeMittente;
	}

	public String getTipoNomeDestinatario() {
		return this.tipoNomeDestinatario;
	}

	public String getTipoNomeTrafficoPerSoggetto() {
		return this.tipoNomeTrafficoPerSoggetto;
	}

	public String getTipoNomeSoggettoLocale() {
		return this.tipoNomeSoggettoLocale;
	}
	
	public String getProtocollo() {
		return this.protocollo;
	}

	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;

		if (StringUtils.isEmpty(protocollo)
				|| "*".equals(protocollo))
			this.protocollo = null;
	}

	public List<SelectItem> getProtocolli() {
		//		if(this.protocolli == null)
		this.protocolli = new ArrayList<SelectItem>();
		this.protocolli.add(new SelectItem("*"));

		try {
			List<Soggetto> listaSoggettiGestione = this.getSoggettiGestione();
			ProtocolFactoryManager pfManager = org.openspcoop2.protocol.engine.ProtocolFactoryManager.getInstance();
			MapReader<String, IProtocolFactory<?>> protocolFactories = pfManager.getProtocolFactories();	

			List<String> listaNomiProtocolli = new  ArrayList<String>();

			if(listaSoggettiGestione != null && listaSoggettiGestione.size() > 0){
				List<String> tipiSoggetti = new ArrayList<String>();
				for (Soggetto soggetto : listaSoggettiGestione) {
					String tipoSoggetto = soggetto.getTipoSoggetto();

					if(!tipiSoggetti.contains(tipoSoggetto))
						tipiSoggetti.add(tipoSoggetto); 
				}

				for (String tipo : tipiSoggetti) {
					String protocolBySubjectType = pfManager.getProtocolByOrganizationType(tipo);
					if(!listaNomiProtocolli.contains(protocolBySubjectType))
						listaNomiProtocolli.add(protocolBySubjectType);
				}

			} else {
				// Tutti i protocolli
				Enumeration<String> keys = protocolFactories.keys();
				while (keys.hasMoreElements()) {
					String protocolKey = (String) keys.nextElement();
					if(!listaNomiProtocolli.contains(protocolKey))
						listaNomiProtocolli.add(protocolKey);
				}
			}

			for (String protocolKey : listaNomiProtocolli) {
				IProtocolFactory<?> protocollo = protocolFactories.get(protocolKey);
				this.protocolli.add(new SelectItem(protocollo.getProtocol()));
			}

		} catch (ProtocolException e) {
			log.error("Si e' verificato un errore durante il caricamento della lista protocolli: " + e.getMessage(), e);
		}  


		return this.protocolli;
	}
	
	public boolean isShowListaProtocolli(){
		try {
			ProtocolFactoryManager pfManager = org.openspcoop2.protocol.engine.ProtocolFactoryManager.getInstance();
			MapReader<String, IProtocolFactory<?>> protocolFactories = pfManager.getProtocolFactories();	
			int numeroProtocolli = protocolFactories.size();

			// se c'e' installato un solo protocollo non visualizzo la select List.
			if(numeroProtocolli == 1)
				return false;

			List<Soggetto> listaSoggettiGestione = this.getSoggettiGestione();
			List<String> listaNomiProtocolli = new  ArrayList<String>();

			if(listaSoggettiGestione != null && listaSoggettiGestione.size() > 0){
				List<String> tipiSoggetti = new ArrayList<String>();
				for (Soggetto soggetto : listaSoggettiGestione) {
					String tipoSoggetto = soggetto.getTipoSoggetto();

					if(!tipiSoggetti.contains(tipoSoggetto))
						tipiSoggetti.add(tipoSoggetto); 
				}

				for (String tipo : tipiSoggetti) {
					String protocolBySubjectType = pfManager.getProtocolByOrganizationType(tipo);
					if(!listaNomiProtocolli.contains(protocolBySubjectType))
						listaNomiProtocolli.add(protocolBySubjectType);
				}

			} else {
				// Tutti i protocolli
				Enumeration<String> keys = protocolFactories.keys();
				while (keys.hasMoreElements()) {
					String protocolKey = (String) keys.nextElement();
					if(!listaNomiProtocolli.contains(protocolKey))
						listaNomiProtocolli.add(protocolKey);
				}
			}

			numeroProtocolli = listaNomiProtocolli.size();

			// se c'e' installato un solo protocollo non visualizzo la select List.
			if(numeroProtocolli == 1)
				return false;

		} catch (ProtocolException e) {
			log.error("Si e' verificato un errore durante il caricamento della lista protocolli: " + e.getMessage(), e);
		}  

		return true;
	}

	public void protocolloSelected(ActionEvent ae) {
	}

	public String getAccordo() {
		return this.uriAccordo;
	}

	public void setAccordo(String uriAccordo) {
		this.uriAccordo = uriAccordo;
	}
	
	public void accordoSelected(ActionEvent ae) {
	}
}
