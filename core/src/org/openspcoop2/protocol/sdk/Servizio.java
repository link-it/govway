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



package org.openspcoop2.protocol.sdk;

import java.util.Date;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.Servizio;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;

/**
 * Classe utilizzata per rappresentare un Servizio presente all'interno
 * del Registro dei Servizi.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Servizio implements java.io.Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Identificatore del Servizio nel registro dei servizi. */
	private IDServizio idServizio;
	/** IDAccordo del servizio */
	private IDAccordo idAccordo;
	/** Profilo di Collaborazione. */
	private ProfiloDiCollaborazione profiloDiCollaborazione;
	/** Utilizzo di un ID-Collaborazione. */
	private boolean idCollaborazione;
	/** Consegna in ordine. */
	private boolean ordineConsegna;
	/** Scadenza (null se non presente) */
	private Date scadenza; 
	/** Scadenza */
	private long scadenzaMinuti = -1; 
	/** attributo 'inoltro' del profilo di trasmissione. */
	private Inoltro inoltro;
	/** attributo 'confermaRicezione' del profilo di trasmissione. */
	private boolean confermaRicezione;
	/** Tipo Servizio Correlato associato al servizio */
	private String tipoServizioCorrelato;
	/** Servizio Correlato associato al servizio */
	private String servizioCorrelato;
	/** Indicazione se e' un servizio correlato */
	private boolean correlato;


	
	
	public Busta convertToBusta(String protocollo,IDSoggetto fruitore){
		Busta busta = new Busta(protocollo);
		if(fruitore!=null){
			busta.setTipoMittente(fruitore.getTipo());
			busta.setMittente(fruitore.getNome());
			busta.setIdentificativoPortaMittente(fruitore.getCodicePorta());
		}
		if(this.idServizio!=null){
			if(this.idServizio.getSoggettoErogatore()!=null){
				busta.setTipoDestinatario(this.idServizio.getSoggettoErogatore().getTipo());
				busta.setDestinatario(this.idServizio.getSoggettoErogatore().getNome());
				busta.setIdentificativoPortaDestinatario(this.idServizio.getSoggettoErogatore().getCodicePorta());
			}
			busta.setTipoServizio(this.idServizio.getTipoServizio());
			busta.setServizio(this.idServizio.getServizio());
			busta.setVersioneServizio(this.idServizio.getVersioneServizio());
			busta.setAzione(this.idServizio.getAzione());
		}
		busta.setProfiloDiCollaborazione(this.profiloDiCollaborazione);
		busta.setInoltro(this.inoltro);
		busta.setConfermaRicezione(this.confermaRicezione);
		busta.setTipoServizioCorrelato(this.tipoServizioCorrelato);
		busta.setServizioCorrelato(this.servizioCorrelato);
		return busta;
	}
	
	
	
	
	

	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore. 
	 *
	 * 
	 */
	public Servizio(){
	}






	/* ********  S E T T E R   ******** */

	/**
	 * Imposta l'identificatore del Servizio nel registro dei servizi.
	 *
	 * @param idServizio Identificatore del Servizio.
	 * 
	 */
	public void setIDServizio(IDServizio idServizio){
		this.idServizio = idServizio;
	} 
	/**
	 * Imposta il profilo di Collaborazione associato al servizio registrato nel Registro dei Servizi.
	 *
	 * @param p Profili di Collaborazione.
	 * 
	 */
	public void setProfiloDiCollaborazione(ProfiloDiCollaborazione p ){
		this.profiloDiCollaborazione = p;
	}
	/**
	 * Imposta la presenza o meno della collaborazione nel servizio registrato nel Registro dei Servizi.
	 *
	 * @param c true se la collaborazione e' presente, false altrimenti.
	 * 
	 */
	public void setCollaborazione(boolean c ){
		this.idCollaborazione = c;
	}
	/**
	 * Imposta l'utilizzo della funzionalita' di consegna in ordine nel servizio registrato nel Registro dei Servizi.
	 *
	 * @param s true se la sequenza e' presente, false altrimenti.
	 * 
	 */
	public void setOrdineConsegna(boolean s){
		this.ordineConsegna = s;
	}
	/**
	 * Imposta la scadenza associata al servizio registrato nel Registro dei Servizi.
	 *
	 * @param s null se non e' presente una scadenza, la data di scadenza altrimenti.
	 * 
	 */
	public void setScadenza(Date s){
		this.scadenza = s;
	}
	/**
	 * Imposta il tipo di inoltro del profilo di trasmissione associato al servizio registrato nel Registro dei Servizi.
	 *
	 * @param i tipo di Inoltro.
	 * 
	 */
	public void setInoltro(Inoltro i ){
		this.inoltro = i;
	}
	/**
	 * Imposta la confermaRicezione o meno del profilo di trasmissione associato al servizio registrato nel Registro dei Servizi.
	 *
	 * @param cr true se si desidera una confermaRicezione, false altrimenti.
	 * 
	 */
	public void setConfermaRicezione(boolean cr){
		this.confermaRicezione = cr;
	}

	/**
	 * Imposta il servizio correlato associato al servizio
	 *
	 * @param nome Servizio Correlato
	 * 
	 */
	public void setServizioCorrelato(String nome){
		this.servizioCorrelato = nome;
	} 
	/**
	 * Imposta il tipo di servizio correlato associato al servizio
	 *
	 * @param tipoServizioCorrelato Tipo di Servizio Correlato
	 * 
	 */
	public void setTipoServizioCorrelato(String tipoServizioCorrelato) {
		this.tipoServizioCorrelato = tipoServizioCorrelato;
	} 

	public void setCorrelato(boolean correlato) {
		this.correlato = correlato;
	}
	
	public void setScadenzaMinuti(long scadenzaMinuti) {
		this.scadenzaMinuti = scadenzaMinuti;
	}
	
	



	/* ********  G E T T E R   ******** */

	/**
	 * Ritorna l'identificatore del Servizio nel registro dei servizi.
	 *
	 * @return Identificatore del Servizio.
	 * 
	 */
	public IDServizio getIDServizio(){
		return this.idServizio;
	} 
	/**
	 * Ritorna il profilo di Collaborazione associato al servizio registrato nel Registro dei Servizi.
	 *
	 * @return Profili di Collaborazione.
	 * 
	 */
	public ProfiloDiCollaborazione getProfiloDiCollaborazione(){
		return this.profiloDiCollaborazione;
	}
	/**
	 * Ritorna la presenza o meno della collaborazione nel servizio registrato nel Registro dei Servizi.
	 *
	 * @return true se la collaborazione e' presente, false altrimenti.
	 * 
	 */
	public boolean getCollaborazione(){
		return this.idCollaborazione;
	}
	/**
	 * Ritorna l'indicazione sull'utilizzo o meno della consegna in ordine nel servizio registrato nel Registro dei Servizi.
	 *
	 * @return true se la sequenza e' presente, false altrimenti.
	 * 
	 */
	public boolean getOrdineConsegna(){
		return this.ordineConsegna;
	}
	/**
	 * Ritorna la scadenza associata al servizio registrato nel Registro dei Servizi.
	 *
	 * @return null se non e' presente una scadenza, la data di scadenza, altrimenti.
	 * 
	 */
	public Date getScadenza(){
		return this.scadenza;
	}
	/**
	 * Ritorna il tipo di inoltro del profilo di trasmissione associato al servizio registrato nel Registro dei Servizi.
	 *
	 * @return tipo di Inoltro.
	 * 
	 */
	public Inoltro getInoltro(){
		return this.inoltro;
	}
	/**
	 * Ritorna la confermaRicezione o meno del profilo di trasmissione associato al servizio registrato nel Registro dei Servizi.
	 *
	 * @return true se si desidera una confermaRicezione, false altrimenti.
	 * 
	 */
	public boolean getConfermaRicezione(){
		return this.confermaRicezione;
	}
	/**
	 * Ritorna il servizio correlato associato al servizio
	 *
	 * @return Servizio Correlato
	 * 
	 */
	public String getServizioCorrelato(){
		return this.servizioCorrelato;
	}
	/**
	 * Ritorna il tipo di servizio correlato associato al servizio
	 *
	 * @return Tipo di Servizio Correlato
	 * 
	 */
	public String getTipoServizioCorrelato() {
		return this.tipoServizioCorrelato;
	}

	public boolean isCorrelato() {
		return this.correlato;
	}

	public long getScadenzaMinuti() {
		return this.scadenzaMinuti;
	}

	public IDAccordo getIdAccordo() {
		return this.idAccordo;
	}
	
	public void setIdAccordo(IDAccordo idAccordo) {
		this.idAccordo = idAccordo;
	}

}





