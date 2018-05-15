package org.openspcoop2.web.monitor.core.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;
import org.openspcoop2.web.lib.users.dao.User;
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
	private List<IDSoggetto> utenteSoggettoList;
	private List<IDServizio> utenteServizioList;

	private User utente;
	
	public UserDetailsBean() {
		this.authorities = new ArrayList<RuoloBean>();
		this.authorities.add(new RuoloBean( UserDetailsBean.RUOLO_USER));
	}

	public void setUtente(User u) {
		this.username = u.getLogin();
		this.password = u.getPassword();
		
		this.utenteSoggettoList = u.getSoggetti();
		this.utenteServizioList = u.getServizi();
		
		int foundSoggetti = this.utenteSoggettoList != null ? this.utenteSoggettoList.size() : 0;
		int foundServizi = this.utenteServizioList !=  null ? this.utenteServizioList.size() : 0;
		
		boolean admin = (foundServizi + foundSoggetti) == 0;
		boolean operatore = (foundServizi + foundSoggetti) > 0;
		
		PermessiUtente permessi = u.getPermessi();
		
		if(permessi.isDiagnostica()) {
			if(admin)
				this.authorities.add(new RuoloBean(UserDetailsBean.RUOLO_AMMINISTRATORE));
			
			if(operatore)
				this.authorities.add(new RuoloBean(UserDetailsBean.RUOLO_OPERATORE));
		}
		
		if(permessi.isSistema()) {
			this.authorities.add(new RuoloBean(UserDetailsBean.RUOLO_CONFIGURATORE));
		}
		
		this.utente = u;
	}
	public User getUtente(){
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
	
	public List<IDSoggetto> getUtenteSoggettoList(){
		return this.utenteSoggettoList;
	}
	
	
	public int getSizeSoggetti(){
		return this.utenteSoggettoList!=null ? this.utenteSoggettoList.size() : 0;
	}
	
	public List<IDServizio> getUtenteServizioList(){
		return this.utenteServizioList;
	}
	
	
	public int getSizeServizio(){
		return this.utenteServizioList!=null ? this.utenteServizioList.size() : 0;
	}
	
	public String getLabelUnicoSoggettoServizioAssociato(){
		boolean foundSoggetti = this.utenteSoggettoList != null && this.utenteSoggettoList.size() > 0;
		boolean foundServizi = this.utenteServizioList !=  null && this.utenteServizioList.size() > 0;
		
		if(foundSoggetti){
			return "Soggetto Locale: ";
		}
		else if(foundServizi){
			return "Servizio: ";
		}
		else{
			return null;
		}
	}
	
	@SuppressWarnings("deprecation")
	public String getValueUnicoSoggettoServizioAssociato(){
		boolean foundSoggetti = this.utenteSoggettoList != null && this.utenteSoggettoList.size() > 0;
		boolean foundServizi = this.utenteServizioList !=  null && this.utenteServizioList.size() > 0;
		
		if(foundSoggetti){
			IDServizio idServizio = new IDServizio();
			idServizio.setSoggettoErogatore(new IDSoggetto(this.utenteServizioList.get(0).getTipo(), this.utenteSoggettoList.get(0).getNome())); 
			return ParseUtility.convertToSoggettoServizio(idServizio);
		}
		else if(foundServizi){
			return ParseUtility.convertToSoggettoServizio(this.utenteServizioList.get(0));
		}
		else{
			return null;
		}
	}

	public List<String> getTipiNomiSoggettiAssociati(){
		List<String> lst = new ArrayList<String>();
		for (IDSoggetto utenteSoggetto : this.utenteSoggettoList) {
			String tipoNome = utenteSoggetto.getTipo() + "/" + utenteSoggetto.getNome();
			if(lst.contains(tipoNome)==false){
				lst.add(tipoNome);
			}
		}
		return lst;
	}
	
	public String getLabelTipiNomiSoggettiServiziAssociati(){
		boolean foundSoggetti = this.utenteSoggettoList != null && this.utenteSoggettoList.size() > 0;
		boolean foundServizi = this.utenteServizioList !=  null && this.utenteServizioList.size() > 0;

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
	
	@SuppressWarnings("deprecation")
	public List<String> getTipiNomiSoggettiServiziAssociati(){
		List<String> lst = new ArrayList<String>();
		for (IDSoggetto utenteSoggetto : this.utenteSoggettoList) {
			IDServizio idServizio = new IDServizio();
			idServizio.setSoggettoErogatore(new IDSoggetto(utenteSoggetto.getTipo(), utenteSoggetto.getNome())); 
			lst.add(ParseUtility.convertToSoggettoServizio(idServizio));
		}
		for (IDServizio idServizio : this.utenteServizioList) {
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
