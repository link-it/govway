package org.openspcoop2.web.monitor.core.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.id.IDServizio;

import it.link.pdd.core.utenti.Ruolo;
import it.link.pdd.core.utenti.Utente;
import it.link.pdd.core.utenti.UtenteSoggetto;
import org.openspcoop2.web.monitor.core.utils.ParseUtility;

public class UserDetailsBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5504135605630617383L;
	
	public static final String RUOLO_USER = "ROLE_USER";
	public static final String RUOLO_OPERATORE = "ROLE_OPERATORE";
	public static final String RUOLO_CONFIGURATORE = "ROLE_CONFIG";
	public static final String RUOLO_AMMINISTRATORE = "ROLE_ADMIN";

	private String username;
	private String password;
	private List<RuoloBean> authorities;
	private List<UtenteSoggetto> utenteSoggettoList;

	private Utente utente;
	
	public UserDetailsBean() {
		this.authorities = new ArrayList<RuoloBean>();
		this.authorities.add(new RuoloBean( UserDetailsBean.RUOLO_USER));
	}

	public void setUtente(Utente u) {
		this.username = u.getLogin();
		this.password = u.getPassword();
		
		this.utenteSoggettoList = u.getUtenteSoggettoList();
		
		if(u.getRuoloList() != null){
			for (Ruolo r : u.getRuoloList()) {
				this.authorities.add(new RuoloBean(r.getRuolo()));
			}
		}
		
		this.utente = u;
	}
	public Utente getUtente(){
		return this.utente;
	}
	
	
	public boolean isAdmin(){
		for (RuoloBean auth : this.authorities) {
			if(UserDetailsBean.RUOLO_AMMINISTRATORE.equals(auth.getAuthority()))
				return true;
		}
		
		return false;
	}
	
	public boolean isOperatore(){
		for (RuoloBean auth : this.authorities) {
			if(UserDetailsBean.RUOLO_OPERATORE.equals(auth.getAuthority()))
				return true;
		}
		
		return false;
	}
	
	public List<UtenteSoggetto> getUtenteSoggettoList(){
		return this.utenteSoggettoList;
	}
	
	public int getSizeSoggetti(){
		return this.utenteSoggettoList!=null ? this.utenteSoggettoList.size() : 0;
	}
	
	public String getLabelUnicoSoggettoServizioAssociato(){
		if(this.utenteSoggettoList.get(0).getServizio()==null){
			return "Soggetto Locale: ";
		}
		else{
			return "Servizio: ";
		}
	}
	public String getValueUnicoSoggettoServizioAssociato(){
		IDServizio idServizio = new IDServizio(this.utenteSoggettoList.get(0).getSoggetto().getTipo(), this.utenteSoggettoList.get(0).getSoggetto().getNome());
		if(this.utenteSoggettoList.get(0).getServizio()!=null){
			idServizio.setTipoServizio(this.utenteSoggettoList.get(0).getServizio().getTipo());
			idServizio.setServizio(this.utenteSoggettoList.get(0).getServizio().getNome());
		}
		return ParseUtility.convertToSoggettoServizio(idServizio);
	}

	public List<String> getTipiNomiSoggettiAssociati(){
		List<String> lst = new ArrayList<String>();
		for (UtenteSoggetto utenteSoggetto : this.utenteSoggettoList) {
			String tipoNome = utenteSoggetto.getSoggetto().getTipo() + "/" + utenteSoggetto.getSoggetto().getNome();
			if(lst.contains(tipoNome)==false){
				lst.add(tipoNome);
			}
		}
		return lst;
	}
	
	public String getLabelTipiNomiSoggettiServiziAssociati(){
		boolean foundSoggetti = false;
		boolean foundServizi = false;
		for (UtenteSoggetto utenteSoggetto : this.utenteSoggettoList) {
			if(utenteSoggetto.getServizio()==null){
				foundSoggetti = true;
			}
			else{
				foundServizi = true;
			}
		}
		if(foundSoggetti && foundServizi){
			return "Soggetto Locale / Servizio";
		}
		else if(foundServizi){
			return "Servizio";
		}
		else{
			return "Soggetto Locale";
		}
	}
	
	public List<String> getTipiNomiSoggettiServiziAssociati(){
		List<String> lst = new ArrayList<String>();
		for (UtenteSoggetto utenteSoggetto : this.utenteSoggettoList) {
			IDServizio idServizio = new IDServizio(utenteSoggetto.getSoggetto().getTipo(), utenteSoggetto.getSoggetto().getNome());
			if(utenteSoggetto.getServizio()!=null){
				idServizio.setTipoServizio(utenteSoggetto.getServizio().getTipo());
				idServizio.setServizio(utenteSoggetto.getServizio().getNome());
			}
			lst.add(ParseUtility.convertToSoggettoServizio(idServizio));
		}
		return lst;
	}


	public String getPassword() {
		return this.password;
	}

	public String getUsername() {
		return this.username;
	}

	public List<RuoloBean> getAuthorities() {
		return this.authorities;
	}
	
	public class RuoloBean implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String authority;
		
		public RuoloBean (String r){
			this.authority = r;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof RuoloBean) {
				return this.getAuthority().equals(
						(((RuoloBean) o).getAuthority()));
			}
			return false;
		}

		public String getAuthority() {
			return this.authority;
		}

	}
}
