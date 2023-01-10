/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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



package org.openspcoop2.core.id;

import org.openspcoop2.core.constants.TipologiaServizio;
import org.openspcoop2.utils.Utilities;

/**
 * Classe utilizzata per rappresentare un identificatore di un Servizio nel registro dei servizi
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IDServizio implements java.io.Serializable, Cloneable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/* ********  F I E L D S  P R I V A T I  ******** */

	protected String tipo;
	protected String nome;
	protected Integer versione = 1;
    protected IDSoggetto soggettoErogatore;
    
    // Campi opzionali
    protected String portType;
	protected String azione;
    protected String uriAccordoServizioParteComune;
    protected TipologiaServizio tipologia;
	
  

	public String getTipo() {
		return this.tipo;
	}
	public String getNome() {
		return this.nome;
	}
	public Integer getVersione() {
		return this.versione;
	}
	public IDSoggetto getSoggettoErogatore() {
		return this.soggettoErogatore;
	}
	
	@Deprecated
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	@Deprecated
	public void setNome(String nome) {
		this.nome = nome;
	}
	@Deprecated
	public void setVersione(Integer versione) {
		this.versione = versione;
	}
	@Deprecated
	public void setSoggettoErogatore(IDSoggetto erogatore) {
		this.soggettoErogatore = erogatore;
	}
	
	
	// Campi opzionali
    public String getPortType() {
		return this.portType;
	}
	public void setPortType(String portType) {
		this.portType = portType;
	}
	public String getAzione() {
		return this.azione;
	}
	public void setAzione(String azione) {
		this.azione = azione;
	}
	public String getUriAccordoServizioParteComune() {
		return this.uriAccordoServizioParteComune;
	}
	public void setUriAccordoServizioParteComune(String uriAccordoServizioParteComune) {
		this.uriAccordoServizioParteComune = uriAccordoServizioParteComune;
	}
	public TipologiaServizio getTipologia() {
		return this.tipologia;
	}
	public void setTipologia(TipologiaServizio tipologia) {
		this.tipologia = tipologia;
	}
	


	@Override 
	public String toString(){
		return this.toString(true);
	}
	public String toString(boolean printAzione){
		StringBuilder bf = new StringBuilder();
		if(this.soggettoErogatore!=null){
			bf.append(this.soggettoErogatore.getTipo());
			bf.append("/");
			bf.append(this.soggettoErogatore.getNome());
		}
		bf.append(":");
		bf.append(this.tipo);
		bf.append("/");
		bf.append(this.nome);
		bf.append(":");
		bf.append(this.versione);
		if(printAzione && this.azione!=null){
			bf.append(":");
			bf.append(this.azione);
		}
		return bf.toString();
	}



	
	@Override
	public boolean equals(Object object){
		
		if(object==null)
			return false;
		if(!Utilities.equalsClass(object,this))
			return false;
		IDServizio id = (IDServizio) object;
		
		return this.equals(id, true);
	}
	
	public boolean equals(IDServizio id, boolean checkAzione){
		
		// TIPO
		if(this.getTipo()==null){
			if(id.getTipo()!=null)
				return false;
		}else{
			if(this.getTipo().equals(id.getTipo())==false)
				return false;
		}
		// NOME
		if(this.getNome()==null){
			if(id.getNome()!=null)
				return false;
		}else{
			if(this.getNome().equals(id.getNome())==false)
				return false;
		}
		// VERSIONE
		if(this.getVersione()==null) {
			if(id.getVersione()!=null) {
				return false;
			}
		}
		else {
			if(id.getVersione()==null) {
				return false;
			}
			if(this.getVersione().intValue()!=id.getVersione().intValue()){
				return false;
			}
		}
		
		// AZIONE
		if(checkAzione) {
			if(this.getAzione()==null){
				if(id.getAzione()!=null)
					return false;
			}else{
				if(this.getAzione().equals(id.getAzione())==false)
					return false;
			}
		}
		
		// Soggetto EROGATORE
		if(this.getSoggettoErogatore()==null){
			if(id.getSoggettoErogatore()!=null)
				return false;
		}else{
			if(this.getSoggettoErogatore().equals(id.getSoggettoErogatore())==false)
				return false;
		}


		return true;
	}

	// Utile per usare l'oggetto in hashtable come chiave
	@Override
	public int hashCode(){
		return this.toString().hashCode();
	}
	
	@Override
	public IDServizio clone(){
		IDServizio s = null;
		try {
			s = (IDServizio) super.clone();
		}catch(Throwable t) {
			s = new IDServizio();
		}
		
		if(this.soggettoErogatore!=null){
			IDSoggetto sogg = this.soggettoErogatore.clone();
			s.setSoggettoErogatore(sogg);
		}
		
		if(this.tipo!=null)
			s.setTipo(new String(this.tipo));
		if(this.nome!=null)
			s.setNome(new String(this.nome));
		s.setVersione(this.versione);
		
		if(this.portType!=null)
			s.setPortType(new String(this.portType));
		
		if(this.azione!=null)
			s.setAzione(new String(this.azione));
		
		if(this.tipologia!=null)
			s.setTipologia(this.tipologia);
				
		if(this.uriAccordoServizioParteComune!=null)
			s.setUriAccordoServizioParteComune(new String(this.uriAccordoServizioParteComune));
				
		return s;
	}

	
	
	public String toFormatString(){
		StringBuilder sb = new StringBuilder();
		sb.append(this.soggettoErogatore.getTipo());
		sb.append("/");
		sb.append(this.soggettoErogatore.getNome());
		sb.append("/");
		sb.append(this.tipo);
		sb.append("/");
		sb.append(this.nome);
		sb.append("/");
		sb.append(this.versione);
		return sb.toString();
	}
	
	public static IDServizio toIDServizio(String formatString) throws Exception {
		String [] tmp = formatString.split("/");
		if(tmp.length!=5) {
			throw new Exception("Formato non supportato, attesi 5 valori, trovati "+tmp.length);
		}
		String tipoSoggettoErogatore = tmp[0];
		String nomeSoggettoErogatore = tmp[1];
		String tipo = tmp[2];
		String nome = tmp[3];
		String versioneS = tmp[4];
		int versione = Integer.valueOf(versioneS);
		IDServizio idServizio = new IDServizio();
		idServizio.soggettoErogatore = new IDSoggetto(tipoSoggettoErogatore, nomeSoggettoErogatore);
		idServizio.tipo=tipo;
		idServizio.nome=nome;
		idServizio.versione=versione;
		return idServizio;
	}
}






