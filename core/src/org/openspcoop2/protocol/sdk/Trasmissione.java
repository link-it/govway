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

import org.openspcoop2.core.tracciamento.Data;
import org.openspcoop2.core.tracciamento.Soggetto;
import org.openspcoop2.core.tracciamento.SoggettoIdentificativo;
import org.openspcoop2.core.tracciamento.TipoData;
import org.openspcoop2.core.tracciamento.constants.TipoTempo;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.utils.date.DateManager;


/**
 * Classe utilizzata per rappresentare una Trasmissione.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class Trasmissione implements java.io.Serializable{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private org.openspcoop2.core.tracciamento.Trasmissione trasmissione;

        
    public Trasmissione() {
    	this.trasmissione = new org.openspcoop2.core.tracciamento.Trasmissione();
    }
    
    public Trasmissione(org.openspcoop2.core.tracciamento.Trasmissione trasmissione){
		this.trasmissione = trasmissione;
	}


	// base

	public org.openspcoop2.core.tracciamento.Trasmissione getTrasmissione() {
		return this.trasmissione;
	}
	public void setTrasmissione(org.openspcoop2.core.tracciamento.Trasmissione trasmissione) {
		this.trasmissione = trasmissione;
	}



	// id  [Wrapper]

	public Long getId() {
		return this.trasmissione.getId();
	}
	public void setId(Long id) {
		this.trasmissione.setId(id);
	}
	
	
	
	

	// mittente [wrapper]
	
    public String getOrigine() {
    	if(this.trasmissione.getOrigine()!=null && this.trasmissione.getOrigine().getIdentificativo()!=null)
			return this.trasmissione.getOrigine().getIdentificativo().getBase();
		else
			return null;
    }
    public void setOrigine(String value) {
    	if(value!=null){
			if(this.trasmissione.getOrigine()==null){
				this.trasmissione.setOrigine(new Soggetto());
			}
			if(this.trasmissione.getOrigine().getIdentificativo()==null){
				this.trasmissione.getOrigine().setIdentificativo(new SoggettoIdentificativo());
			}
			this.trasmissione.getOrigine().getIdentificativo().setBase(value);
		}
		else{
			if(this.trasmissione.getOrigine()!=null){
				if(this.trasmissione.getOrigine().getIdentificativo()!=null){
					if(this.trasmissione.getOrigine().getIdentificativo().getTipo()==null){
						this.trasmissione.getOrigine().setIdentificativo(null);
					}
					else{
						this.trasmissione.getOrigine().getIdentificativo().setBase(null);
					}	
				}
				if(this.trasmissione.getOrigine().getIdentificativo()==null && 
						this.trasmissione.getOrigine().getIdentificativoPorta()==null &&
						this.trasmissione.getOrigine().getIndirizzo()==null){
					this.trasmissione.setOrigine(null);
				}
			}
		}
    }
	
    public String getTipoOrigine() {
    	if(this.trasmissione.getOrigine()!=null && this.trasmissione.getOrigine().getIdentificativo()!=null)
			return this.trasmissione.getOrigine().getIdentificativo().getTipo();
		else
			return null;
    }
    public void setTipoOrigine(String value) {
    	if(value!=null){
			if(this.trasmissione.getOrigine()==null){
				this.trasmissione.setOrigine(new Soggetto());
			}
			if(this.trasmissione.getOrigine().getIdentificativo()==null){
				this.trasmissione.getOrigine().setIdentificativo(new SoggettoIdentificativo());
			}
			this.trasmissione.getOrigine().getIdentificativo().setTipo(value);
		}
		else{
			if(this.trasmissione.getOrigine()!=null){
				if(this.trasmissione.getOrigine().getIdentificativo()!=null){
					if(this.trasmissione.getOrigine().getIdentificativo().getBase()==null){
						this.trasmissione.getOrigine().setIdentificativo(null);
					}
					else{
						this.trasmissione.getOrigine().getIdentificativo().setTipo(null);
					}	
				}
				if(this.trasmissione.getOrigine().getIdentificativo()==null && 
						this.trasmissione.getOrigine().getIdentificativoPorta()==null &&
						this.trasmissione.getOrigine().getIndirizzo()==null){
					this.trasmissione.setOrigine(null);
				}
			}
		}
    }
    
	public String getIdentificativoPortaOrigine() {
		if(this.trasmissione.getOrigine()!=null)
			return this.trasmissione.getOrigine().getIdentificativoPorta();
		else
			return null;
	}
	public void setIdentificativoPortaOrigine(String identificativoPortaOrigine) {
		if(identificativoPortaOrigine!=null){
			if(this.trasmissione.getOrigine()==null){
				this.trasmissione.setOrigine(new Soggetto());
			}
			this.trasmissione.getOrigine().setIdentificativoPorta(identificativoPortaOrigine);
		}
		else{
			if(this.trasmissione.getOrigine()!=null){
				this.trasmissione.getOrigine().setIdentificativoPorta(null);
				if(this.trasmissione.getOrigine().getIdentificativo()==null && 
						this.trasmissione.getOrigine().getIdentificativoPorta()==null &&
						this.trasmissione.getOrigine().getIndirizzo()==null){
					this.trasmissione.setOrigine(null);
				}
			}
		}
	}
    
    public String getIndirizzoOrigine() {
    	if(this.trasmissione.getOrigine()!=null)
			return this.trasmissione.getOrigine().getIndirizzo();
		else
			return null;
    }
    public void setIndirizzoOrigine(String value) {
    	if(value!=null){
			if(this.trasmissione.getOrigine()==null){
				this.trasmissione.setOrigine(new Soggetto());
			}
			this.trasmissione.getOrigine().setIndirizzo(value);
		}
		else{
			if(this.trasmissione.getOrigine()!=null){
				this.trasmissione.getOrigine().setIndirizzo(null);
				if(this.trasmissione.getOrigine().getIdentificativo()==null && 
						this.trasmissione.getOrigine().getIdentificativoPorta()==null &&
						this.trasmissione.getOrigine().getIndirizzo()==null){
					this.trasmissione.setOrigine(null);
				}
			}
		}
    }
	
	
	
	// destinatario [wrapper]
    
    public String getDestinazione() {
    	if(this.trasmissione.getDestinazione()!=null && this.trasmissione.getDestinazione().getIdentificativo()!=null)
			return this.trasmissione.getDestinazione().getIdentificativo().getBase();
		else
			return null;
    }
    public void setDestinazione(String value) {
    	if(value!=null){
			if(this.trasmissione.getDestinazione()==null){
				this.trasmissione.setDestinazione(new Soggetto());
			}
			if(this.trasmissione.getDestinazione().getIdentificativo()==null){
				this.trasmissione.getDestinazione().setIdentificativo(new SoggettoIdentificativo());
			}
			this.trasmissione.getDestinazione().getIdentificativo().setBase(value);
		}
		else{
			if(this.trasmissione.getDestinazione()!=null){
				if(this.trasmissione.getDestinazione().getIdentificativo()!=null){
					if(this.trasmissione.getDestinazione().getIdentificativo().getTipo()==null){
						this.trasmissione.getDestinazione().setIdentificativo(null);
					}
					else{
						this.trasmissione.getDestinazione().getIdentificativo().setBase(null);
					}
				}
				if(this.trasmissione.getDestinazione().getIdentificativo()==null && 
						this.trasmissione.getDestinazione().getIdentificativoPorta()==null &&
						this.trasmissione.getDestinazione().getIndirizzo()==null){
					this.trasmissione.setDestinazione(null);
				}
			}
		}
    }
	
    public String getTipoDestinazione() {
    	if(this.trasmissione.getDestinazione()!=null && this.trasmissione.getDestinazione().getIdentificativo()!=null)
			return this.trasmissione.getDestinazione().getIdentificativo().getTipo();
		else
			return null;
    }
    public void setTipoDestinazione(String value) {
    	if(value!=null){
			if(this.trasmissione.getDestinazione()==null){
				this.trasmissione.setDestinazione(new Soggetto());
			}
			if(this.trasmissione.getDestinazione().getIdentificativo()==null){
				this.trasmissione.getDestinazione().setIdentificativo(new SoggettoIdentificativo());
			}
			this.trasmissione.getDestinazione().getIdentificativo().setTipo(value);
		}
		else{
			if(this.trasmissione.getDestinazione()!=null){
				if(this.trasmissione.getDestinazione().getIdentificativo()!=null){
					if(this.trasmissione.getDestinazione().getIdentificativo().getBase()==null){
						this.trasmissione.getDestinazione().setIdentificativo(null);
					}
					else{
						this.trasmissione.getDestinazione().getIdentificativo().setTipo(null);
					}
				}
				if(this.trasmissione.getDestinazione().getIdentificativo()==null && 
						this.trasmissione.getDestinazione().getIdentificativoPorta()==null &&
						this.trasmissione.getDestinazione().getIndirizzo()==null){
					this.trasmissione.setDestinazione(null);
				}
			}
		}
    }
    
	public String getIdentificativoPortaDestinazione() {
		if(this.trasmissione.getDestinazione()!=null)
			return this.trasmissione.getDestinazione().getIdentificativoPorta();
		else
			return null;
	}
	public void setIdentificativoPortaDestinazione(String identificativoPortaDestinazione) {
		if(identificativoPortaDestinazione!=null){
			if(this.trasmissione.getDestinazione()==null){
				this.trasmissione.setDestinazione(new Soggetto());
			}
			this.trasmissione.getDestinazione().setIdentificativoPorta(identificativoPortaDestinazione);
		}
		else{
			if(this.trasmissione.getDestinazione()!=null){
				this.trasmissione.getDestinazione().setIdentificativoPorta(null);
				if(this.trasmissione.getDestinazione().getIdentificativo()==null && 
						this.trasmissione.getDestinazione().getIdentificativoPorta()==null &&
						this.trasmissione.getDestinazione().getIndirizzo()==null){
					this.trasmissione.setDestinazione(null);
				}
			}
		}
	}
    
    public String getIndirizzoDestinazione() {
    	if(this.trasmissione.getDestinazione()!=null)
			return this.trasmissione.getDestinazione().getIndirizzo();
		else
			return null;
    }
    public void setIndirizzoDestinazione(String value) {
    	if(value!=null){
			if(this.trasmissione.getDestinazione()==null){
				this.trasmissione.setDestinazione(new Soggetto());
			}
			this.trasmissione.getDestinazione().setIndirizzo(value);
		}
		else{
			if(this.trasmissione.getDestinazione()!=null){
				this.trasmissione.getDestinazione().setIndirizzo(null);
				if(this.trasmissione.getDestinazione().getIdentificativo()==null && 
						this.trasmissione.getDestinazione().getIdentificativoPorta()==null &&
						this.trasmissione.getDestinazione().getIndirizzo()==null){
					this.trasmissione.setDestinazione(null);
				}
			}
		}
    }
    
      
  
    
    // date
    
    public TipoOraRegistrazione getTempo() {
		if(this.trasmissione.getOraRegistrazione()!=null && 
				this.trasmissione.getOraRegistrazione().getSorgente()!=null &&
				this.trasmissione.getOraRegistrazione().getSorgente().getTipo()!=null){
			switch (this.trasmissione.getOraRegistrazione().getSorgente().getTipo()) {
			case LOCALE:
				return TipoOraRegistrazione.LOCALE;
			case SINCRONIZZATO:
				return TipoOraRegistrazione.SINCRONIZZATO;
			case SCONOSCIUTO:
				return TipoOraRegistrazione.UNKNOWN;
			}
		}
		return null;
	}
	public void setTempo(TipoOraRegistrazione tipoOraRegistrazione) {
		if(tipoOraRegistrazione!=null){
			if(this.trasmissione.getOraRegistrazione()==null){
				this.trasmissione.setOraRegistrazione(new Data());
			}
			if(this.trasmissione.getOraRegistrazione().getSorgente()==null){
				this.trasmissione.getOraRegistrazione().setSorgente(new TipoData());
			}
			switch (tipoOraRegistrazione) {
			case LOCALE:
				this.trasmissione.getOraRegistrazione().getSorgente().setTipo(TipoTempo.LOCALE);
				break;
			case SINCRONIZZATO:
				this.trasmissione.getOraRegistrazione().getSorgente().setTipo(TipoTempo.SINCRONIZZATO);
				break;
			case UNKNOWN:
				this.trasmissione.getOraRegistrazione().getSorgente().setTipo(TipoTempo.SCONOSCIUTO);
				break;
			}
		}
		else {
			if(this.trasmissione.getOraRegistrazione()!=null){
				if(this.trasmissione.getOraRegistrazione().getSorgente()!=null){
					if(this.trasmissione.getOraRegistrazione().getSorgente().getBase()==null){
						this.trasmissione.getOraRegistrazione().setSorgente(null);
					}
					else{
						this.trasmissione.getOraRegistrazione().getSorgente().setTipo(null);
					}	
				}
				if(this.trasmissione.getOraRegistrazione().getSorgente()==null && this.trasmissione.getOraRegistrazione().getDateTime()==null){
					this.trasmissione.setOraRegistrazione(null);
				}
			}
		}
	}

	public void setTempo(TipoOraRegistrazione tipo, String value) {
		this.setTempo(tipo);
		this.setTempoValue(value);
	}

	public String getTempoValue(IProtocolFactory protocolFactory) throws ProtocolException {
		String tipoOraRegistrazioneValue = null;
		if(this.trasmissione.getOraRegistrazione()!=null && this.trasmissione.getOraRegistrazione().getSorgente()!=null){
			tipoOraRegistrazioneValue = this.trasmissione.getOraRegistrazione().getSorgente().getBase();
		}
		return tipoOraRegistrazioneValue == null ? protocolFactory.createTraduttore().toString(this.getTempo()) : tipoOraRegistrazioneValue;
	}
	public void setTempoValue(String tipoOraRegistrazioneValue) {
		if(tipoOraRegistrazioneValue!=null){
			if(this.trasmissione.getOraRegistrazione()==null){
				this.trasmissione.setOraRegistrazione(new Data());
			}
			if(this.trasmissione.getOraRegistrazione().getSorgente()==null){
				this.trasmissione.getOraRegistrazione().setSorgente(new TipoData());
			}
			this.trasmissione.getOraRegistrazione().getSorgente().setBase(tipoOraRegistrazioneValue);
		}
		else {
			if(this.trasmissione.getOraRegistrazione()!=null){
				if(this.trasmissione.getOraRegistrazione().getSorgente()!=null){
					if(this.trasmissione.getOraRegistrazione().getSorgente().getTipo()==null){
						this.trasmissione.getOraRegistrazione().setSorgente(null);
					}
					else{
						this.trasmissione.getOraRegistrazione().getSorgente().setBase(null);
					}	
				}
				if(this.trasmissione.getOraRegistrazione().getSorgente()==null && this.trasmissione.getOraRegistrazione().getDateTime()==null){
					this.trasmissione.setOraRegistrazione(null);
				}
			}
		}
	}

	public Date getOraRegistrazione() {
		if(this.trasmissione.getOraRegistrazione()!=null){
			return this.trasmissione.getOraRegistrazione().getDateTime();
		}
		return null;
	}
	public void setOraRegistrazione(Date value) {
		if(value!=null){
			if(this.trasmissione.getOraRegistrazione()==null){
				this.trasmissione.setOraRegistrazione(new Data());
			}
			this.trasmissione.getOraRegistrazione().setDateTime(value);
		}
		else {
			if(this.trasmissione.getOraRegistrazione()!=null){
				if(this.trasmissione.getOraRegistrazione().getSorgente()==null){
					this.trasmissione.setOraRegistrazione(null);
				}
				else{
					this.trasmissione.getOraRegistrazione().setDateTime(null);
				}
			}
		}
	}
    
    


	

	@Override
	public Trasmissione clone(){
		
		Trasmissione clone = new Trasmissione();
		
		// id
		clone.setId(this.getId()!=null ? new Long(this.getId()) : null);
		
		// origine
		clone.setOrigine(this.getOrigine()!=null ? new String(this.getOrigine()) : null);
		clone.setTipoOrigine(this.getTipoOrigine()!=null ? new String(this.getTipoOrigine()) : null);
		clone.setIndirizzoOrigine(this.getIndirizzoOrigine()!=null ? new String(this.getIndirizzoOrigine()) : null);
		clone.setIdentificativoPortaOrigine(this.getIdentificativoPortaOrigine()!=null ? new String(this.getIdentificativoPortaOrigine()) : null);
		
		// destinazione
		clone.setDestinazione(this.getDestinazione()!=null ? new String(this.getDestinazione()) : null);
		clone.setTipoDestinazione(this.getTipoDestinazione()!=null ? new String(this.getTipoDestinazione()) : null);
		clone.setIndirizzoDestinazione(this.getIndirizzoDestinazione()!=null ? new String(this.getIndirizzoDestinazione()) : null);
		clone.setIdentificativoPortaDestinazione(this.getIdentificativoPortaDestinazione()!=null ? new String(this.getIdentificativoPortaDestinazione()) : null);

		// date
		clone.setOraRegistrazione(this.getOraRegistrazione()!=null ? new Date(this.getOraRegistrazione().getTime()) : null);
		clone.setTempo(this.getTempo());
		clone.setTempoValue(this.trasmissione.getOraRegistrazione()!=null && 
				this.trasmissione.getOraRegistrazione().getSorgente()!=null &&
				this.trasmissione.getOraRegistrazione().getSorgente().getBase()!=null ? new String(this.trasmissione.getOraRegistrazione().getSorgente().getBase()) : null);

		return clone;
	}

	
	public Trasmissione invertiTrasmissione(TipoOraRegistrazione tipoOraRegistrazione, String tipoTempo){
		
		Trasmissione inverti = new Trasmissione();
				
		// origine
		inverti.setOrigine(this.getDestinazione()!=null ? new String(this.getDestinazione()) : null);
		inverti.setTipoOrigine(this.getTipoDestinazione()!=null ? new String(this.getTipoDestinazione()) : null);
		inverti.setIndirizzoOrigine(this.getIndirizzoDestinazione()!=null ? new String(this.getIndirizzoDestinazione()) : null);
		inverti.setIdentificativoPortaOrigine(this.getIdentificativoPortaDestinazione()!=null ? new String(this.getIdentificativoPortaDestinazione()) : null);
		
		// destinazione
		inverti.setDestinazione(this.getOrigine()!=null ? new String(this.getOrigine()) : null);
		inverti.setTipoDestinazione(this.getTipoOrigine()!=null ? new String(this.getTipoOrigine()) : null);
		inverti.setIndirizzoDestinazione(this.getIndirizzoOrigine()!=null ? new String(this.getIndirizzoOrigine()) : null);
		inverti.setIdentificativoPortaDestinazione(this.getIdentificativoPortaOrigine()!=null ? new String(this.getIdentificativoPortaOrigine()) : null);

		// date
		Date oraRegistrazione = DateManager.getDate();
		inverti.setOraRegistrazione(oraRegistrazione);
		inverti.setTempo(tipoOraRegistrazione);
		inverti.setTempoValue(tipoTempo);

		return inverti;
	}
}





