/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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


package org.openspcoop2.web.lib.users.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;


/**
 * User
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */

public class User implements Serializable {
	private Long id;

	protected String login;
	protected String password;
	private InterfaceType interfaceType;
	private boolean permitInterfaceComplete = false;
	private PermessiUtente permessi;
	private List<String> protocolliSupportati;
	private String protocolloSelezionato;
	private boolean permitMultiTenant = false;
	private List<IDSoggetto> soggetti = new ArrayList<>();
	private List<IDServizio> servizi = new ArrayList<>();
	private List<Stato> stati = new ArrayList<>();
	
	public List<String> getProtocolliSupportati() {
		return this.protocolliSupportati;
	}
	public String getProtocolliSupportatiAsString() {
		if(this.protocolliSupportati==null || this.protocolliSupportati.size()<=0) {
			return null;
		}
		else {
			StringBuffer bf = new StringBuffer();
			for (String p : this.protocolliSupportati) {
				if(bf.length()>0) {
					bf.append(",");
				}
				bf.append(p);
			}
			return bf.toString();
		}
	}

	public void setProtocolliSupportati(List<String> protocolliSupportati) {
		this.protocolliSupportati = protocolliSupportati;
	}
	public void addProtocolloSupportato(String protocolloSupportato) {
		protocolloSupportato = protocolloSupportato.trim();
		if(this.protocolliSupportati==null) {
			this.protocolliSupportati = new ArrayList<>();
		}
		if(this.protocolliSupportati.contains(protocolloSupportato)==false) {
			this.protocolliSupportati.add(protocolloSupportato);
		}
	}
	public void setProtocolliSupportatiFromString(String v) {
		if(v==null) {
			this.protocolliSupportati=null;
			return;
		}
		if(v.contains(",")) {
			String [] tmp = v.split(",");
			for (int i = 0; i < tmp.length; i++) {
				String p = tmp[i].trim();
				this.addProtocolloSupportato(p);
			}
		}
		else {
			this.addProtocolloSupportato(v);
		}
	}
	public void clearProtocolliSupportati() {
		if(this.protocolliSupportati==null) {
			this.protocolliSupportati = new ArrayList<>();
		} else 
			this.protocolliSupportati.clear();
	}

	public String getProtocolloSelezionato() {
		return this.protocolloSelezionato;
	}

	public void setProtocolloSelezionato(String protocolloSelezionato) {
		this.protocolloSelezionato = protocolloSelezionato;
	}
	
	public boolean isPermitInterfaceComplete() {
		return this.permitInterfaceComplete;
	}
	public void setPermitInterfaceComplete(boolean permitInterfaceComplete) {
		this.permitInterfaceComplete = permitInterfaceComplete;
	}
	
	public InterfaceType getInterfaceType() {
		return this.interfaceType;
	}

	public void setInterfaceType(InterfaceType interfaceType) {
		this.interfaceType = interfaceType;
	}

	public PermessiUtente getPermessi() {
		return this.permessi;
	}

	public void setPermessi(PermessiUtente permessi) {
		this.permessi = permessi;
	}

	public Long getId() {
		if (this.id != null)
			return this.id;
		else
			return Long.valueOf(-1);
	}

	public void setId(Long id) {
		if (id != null)
			this.id = id;
		else
			this.id = Long.valueOf(-1);
	}

	public String getLogin() {
		if (this.login != null && ("".equals(this.login) == false)) {
			return this.login.trim();
		} else {
			return null;
		}
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		if (this.password != null && ("".equals(this.password) == false)) {
			return this.password.trim();
		} else {
			return null;
		}
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean hasOnlyPermessiUtenti() {
		if(this.permessi != null)
			return (this.permessi.isUtenti() && !this.permessi.isAccordiCooperazione() && !this.permessi.isAuditing() && !this.permessi.isCodeMessaggi() && !this.permessi.isDiagnostica() && !this.permessi.isServizi() && !this.permessi.isSistema());
		else
			return false;
	}

	public boolean isPermitMultiTenant() {
		return this.permitMultiTenant;
	}
	public void setPermitMultiTenant(boolean permitMultiTenant) {
		this.permitMultiTenant = permitMultiTenant;
	}
	
	public List<IDSoggetto> getSoggetti() {
		return this.soggetti;
	}
	public List<IDServizio> getServizi() {
		return this.servizi;
	}
	public List<Stato> getStati() {
		return this.stati;
	}
	
	
	private static final long serialVersionUID = 1L;

}
