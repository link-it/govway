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



package org.openspcoop2.core.id;

/**
 * Classe utilizzata per rappresentare un identificatore di un Servizio nel registro dei servizi
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IDServizio implements java.io.Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/* ********  F I E L D S  P R I V A T I  ******** */

	protected String tipoServizio;
	protected String servizio;
	protected String versioneServizio = "1";
    protected IDSoggetto soggettoErogatore;
    protected String uriAccordo;
    protected String azione;
	private String tipologiaServizio;
	
   
   
    
   
    



	/* ********  C O S T R U T T O R E  ******** */


	/**
	 * Costruttore. 
	 *
	 * @param soggettoErogatore Identificatore del Soggetto Erogatore
	 * @param tipoServizio tipo di Servizio
	 * @param servizio Servizio
	 * @param azione Azione
	 * 
	 */
	public IDServizio(IDSoggetto soggettoErogatore, 
			String tipoServizio, String servizio,
			String azione){
		this.soggettoErogatore = soggettoErogatore;
		this.tipoServizio = tipoServizio;
		this.servizio = servizio;
		this.azione = azione;
	}
	/**
	 * Costruttore. 
	 *
	 * @param soggettoErogatore Identificatore del Soggetto Erogatore
	 * @param tipoServizio tipo di Servizio
	 * @param servizio Servizio
	 * 
	 */
	public IDServizio(IDSoggetto soggettoErogatore, 
			String tipoServizio, String servizio){
		this.soggettoErogatore = soggettoErogatore;
		this.tipoServizio = tipoServizio;
		this.servizio = servizio;
		this.azione = null;
	}
	/**
	 * Costruttore. 
	 *
	 * @param aTipoSoggetto Tipo del Soggetto Erogatore
	 * @param aSoggetto Soggetto
	 * @param tipoServizio tipo di Servizio
	 * @param servizio Servizio
	 * @param azione Azione
	 * 
	 */
	public IDServizio(String aTipoSoggetto, String aSoggetto, 
			String tipoServizio, String servizio,
			String azione){
		this.soggettoErogatore = new IDSoggetto(aTipoSoggetto,aSoggetto);
		this.tipoServizio = tipoServizio;
		this.servizio = servizio;
		this.azione = azione;
	}
	/**
	 * Costruttore. 
	 *
	 * @param aTipoSoggetto Tipo del Soggetto Erogatore
	 * @param aSoggetto Soggetto
	 * @param tipoServizio tipo di Servizio
	 * @param servizio Servizio
	 * 
	 */
	public IDServizio(String aTipoSoggetto, String aSoggetto, 
			String tipoServizio, String servizio){
		this.soggettoErogatore = new IDSoggetto(aTipoSoggetto,aSoggetto);
		this.tipoServizio = tipoServizio;
		this.servizio = servizio;
		this.azione = null;
	}
	/**
	 * Costruttore. 
	 *
	 * @param soggettoErogatore Identificatore del Soggetto Erogatore
	 * 
	 */
	public IDServizio(IDSoggetto soggettoErogatore){
		this.soggettoErogatore = soggettoErogatore;
	}
	/**
	 * Costruttore. 
	 *
	 * @param aTipoSoggetto Tipo del Soggetto Erogatore
	 * @param aSoggetto Soggetto Erogatore
	 * 
	 */
	public IDServizio(String aTipoSoggetto, String aSoggetto){
		this.soggettoErogatore = new IDSoggetto(aTipoSoggetto,aSoggetto);
	}
	/**
	 * Costruttore. 
	 *
	 * 
	 */
	public IDServizio(){}







	/* ********  S E T T E R   ******** */

	/**
	 * Imposta l'identificativo del Soggetto Erogatore.
	 *
	 * @param soggettoErogatore Identificativo del Soggetto Erogatore
	 * 
	 */
	public void setSoggettoErogatore(IDSoggetto soggettoErogatore){
		this.soggettoErogatore = soggettoErogatore;
	}
	/**
	 * Imposta l'identificativo del Soggetto Erogatore.
	 *
	 * @param aTipoSoggetto Tipo del Soggetto Erogatore
	 * @param aSoggetto Soggetto Erogatore
	 * 
	 */
	public void setSoggettoErogatore(String aTipoSoggetto, String aSoggetto){
		this.soggettoErogatore = new IDSoggetto(aTipoSoggetto,aSoggetto);
	}

	/**
	 * Imposta il tipo del Servizio associato alla Porta Applicativa
	 *
	 * @param tipoServizio tipo del Servizio
	 * 
	 */
	public void setTipoServizio(String tipoServizio){
		this.tipoServizio = tipoServizio;
	}

	/**
	 * Imposta il servizio associato alla Porta Applicativa
	 *
	 * @param servizio Servizio
	 * 
	 */
	public void setServizio(String servizio){
		this.servizio = servizio;
	}

	/**
	 * Imposta l'azione associata alla Porta Applicativa
	 *
	 * @param azione azione
	 * 
	 */
	public void setAzione(String azione){
		this.azione = azione;
	}



	/* ********  G E T T E R   ******** */

	/**
	 * Ritorna l'identificativo del Soggetto Erogatore.
	 *
	 * @return Identificativo del Soggetto Erogatore.
	 * 
	 */
	public IDSoggetto getSoggettoErogatore(){
		return this.soggettoErogatore;
	}

	/**
	 * Ritorna il tipo del Servizio associato alla Porta Applicativa
	 *
	 * @return tipo del Servizio
	 * 
	 */
	public String getTipoServizio(){
		return this.tipoServizio;
	}

	/**
	 * Ritorna il servizio associato alla Porta Applicativa
	 *
	 * @return Servizio
	 * 
	 */
	public String getServizio(){
		return this.servizio;
	}

	/**
	 * Ritorna l'azione associata alla Porta Applicativa
	 *
	 * @return azione
	 * 
	 */
	public String getAzione(){
		return this.azione;
	}

	@Override 
	public String toString(){
		StringBuffer bf = new StringBuffer();
		if(this.soggettoErogatore!=null){
			bf.append(this.soggettoErogatore.getTipo());
			bf.append("/");
			bf.append(this.soggettoErogatore.getNome());
		}
		bf.append("--");
		bf.append(this.tipoServizio);
		bf.append("/");
		bf.append(this.servizio);
		if(this.versioneServizio!=null){
			bf.append(":");
			bf.append(this.versioneServizio);
		}
		if(this.azione!=null){
			bf.append("--");
			bf.append(this.azione);
		}
		return bf.toString();
	}

	public String getUriAccordo() {
		return this.uriAccordo;
	}
	public void setUriAccordo(String uriAccordo) {
		this.uriAccordo = uriAccordo;
	}

	public String getTipologiaServizio() {
		return this.tipologiaServizio;
	}
	public void setTipologiaServizio(String tipologiaServizio) {
		this.tipologiaServizio = tipologiaServizio;
	}
	public String getVersioneServizio() {
		if(this.versioneServizio==null){
			return "1";
		}else{
			return this.versioneServizio;
		}
	}
	public int getVersioneServizioAsInt() {
		return Integer.parseInt(this.getVersioneServizio());
	}
	public void setVersioneServizio(String versioneServizio) {
		if(versioneServizio==null)
			this.versioneServizio = "1";
		else
			this.versioneServizio = versioneServizio;
	}
	
	
	@Override
	public boolean equals(Object servizio){
		if(servizio==null)
			return false;
		if(servizio.getClass().getName().equals(this.getClass().getName()) == false)
			return false;
		IDServizio id = (IDServizio) servizio;

		// TIPO
		if(this.getTipoServizio()==null){
			if(id.getTipoServizio()!=null)
				return false;
		}else{
			if(this.getTipoServizio().equals(id.getTipoServizio())==false)
				return false;
		}
		// NOME
		if(this.getServizio()==null){
			if(id.getServizio()!=null)
				return false;
		}else{
			if(this.getServizio().equals(id.getServizio())==false)
				return false;
		}
		// VERSIONE
		if(this.getVersioneServizio()==null){
			if(id.getVersioneServizio()!=null)
				return false;
		}else{
			if(this.getVersioneServizio().equals(id.getVersioneServizio())==false)
				return false;
		}
		// AZIONE
		if(this.getAzione()==null){
			if(id.getAzione()!=null)
				return false;
		}else{
			if(this.getAzione().equals(id.getAzione())==false)
				return false;
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
		IDServizio s = new IDServizio();
		if(this.azione!=null)
			s.setAzione(new String(this.azione));
		if(this.tipologiaServizio!=null)
			s.setTipologiaServizio(new String(this.tipologiaServizio));
		if(this.servizio!=null)
			s.setServizio(new String(this.servizio));
		if(this.versioneServizio!=null)
			s.setVersioneServizio(new String(this.versioneServizio));
		if(this.tipoServizio!=null)
			s.setTipoServizio(new String(this.tipoServizio));
		if(this.uriAccordo!=null)
			s.setUriAccordo(new String(this.uriAccordo));
		
		if(this.soggettoErogatore!=null){
			IDSoggetto sogg = this.soggettoErogatore.clone();
			s.setSoggettoErogatore(sogg);
		}
		
		return s;
	}

	
}






