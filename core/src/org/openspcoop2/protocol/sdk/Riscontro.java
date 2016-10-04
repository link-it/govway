/*
 * OpenSPCoop - Customizable API Gateway 
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
import org.openspcoop2.core.tracciamento.TipoData;
import org.openspcoop2.core.tracciamento.constants.TipoTempo;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;


/**
 * Classe utilizzata per rappresentare un Riscontro.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Riscontro implements java.io.Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private org.openspcoop2.core.tracciamento.Riscontro riscontro;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore. 
	 *
	 * 
	 */
	public Riscontro(){
		this.riscontro = new org.openspcoop2.core.tracciamento.Riscontro();
	}

	public Riscontro(org.openspcoop2.core.tracciamento.Riscontro riscontro){
		this.riscontro = riscontro;
	}


	// base

	public org.openspcoop2.core.tracciamento.Riscontro getRiscontro() {
		return this.riscontro;
	}
	public void setRiscontro(org.openspcoop2.core.tracciamento.Riscontro riscontro) {
		this.riscontro = riscontro;
	}



	// id  [Wrapper]

	public Long getId() {
		return this.riscontro.getId();
	}
	public void setId(Long id) {
		this.riscontro.setId(id);
	}



	// identificativo riscontro [wrapper]
	public void setID(String id ){
		this.riscontro.setIdentificativo(id);
	}
	public String getID(){
		return this.riscontro.getIdentificativo();
	}



	// date [wrapped]

	public TipoOraRegistrazione getTipoOraRegistrazione() {
		if(this.riscontro.getOraRegistrazione()!=null && 
				this.riscontro.getOraRegistrazione().getSorgente()!=null &&
				this.riscontro.getOraRegistrazione().getSorgente().getTipo()!=null){
			switch (this.riscontro.getOraRegistrazione().getSorgente().getTipo()) {
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
	public void setTipoOraRegistrazione(TipoOraRegistrazione tipoOraRegistrazione) {
		if(tipoOraRegistrazione!=null){
			if(this.riscontro.getOraRegistrazione()==null){
				this.riscontro.setOraRegistrazione(new Data());
			}
			if(this.riscontro.getOraRegistrazione().getSorgente()==null){
				this.riscontro.getOraRegistrazione().setSorgente(new TipoData());
			}
			switch (tipoOraRegistrazione) {
			case LOCALE:
				this.riscontro.getOraRegistrazione().getSorgente().setTipo(TipoTempo.LOCALE);
				break;
			case SINCRONIZZATO:
				this.riscontro.getOraRegistrazione().getSorgente().setTipo(TipoTempo.SINCRONIZZATO);
				break;
			case UNKNOWN:
				this.riscontro.getOraRegistrazione().getSorgente().setTipo(TipoTempo.SCONOSCIUTO);
				break;
			}
		}
		else {
			if(this.riscontro.getOraRegistrazione()!=null){
				if(this.riscontro.getOraRegistrazione().getSorgente()!=null){
					if(this.riscontro.getOraRegistrazione().getSorgente().getBase()==null){
						this.riscontro.getOraRegistrazione().setSorgente(null);
					}
					else{
						this.riscontro.getOraRegistrazione().getSorgente().setTipo(null);
					}	
				}
				if(this.riscontro.getOraRegistrazione().getSorgente()==null && this.riscontro.getOraRegistrazione().getDateTime()==null){
					this.riscontro.setOraRegistrazione(null);
				}
			}
		}
	}

	public void setTipoOraRegistrazione(TipoOraRegistrazione tipo, String value) {
		this.setTipoOraRegistrazione(tipo);
		this.setTipoOraRegistrazioneValue(value);
	}

	public String getTipoOraRegistrazioneValue(IProtocolFactory protocolFactory) throws ProtocolException {
		String tipoOraRegistrazioneValue = null;
		if(this.riscontro.getOraRegistrazione()!=null && this.riscontro.getOraRegistrazione().getSorgente()!=null){
			tipoOraRegistrazioneValue = this.riscontro.getOraRegistrazione().getSorgente().getBase();
		}
		return tipoOraRegistrazioneValue == null ? protocolFactory.createTraduttore().toString(this.getTipoOraRegistrazione()) : tipoOraRegistrazioneValue;
	}
	public void setTipoOraRegistrazioneValue(String tipoOraRegistrazioneValue) {
		if(tipoOraRegistrazioneValue!=null){
			if(this.riscontro.getOraRegistrazione()==null){
				this.riscontro.setOraRegistrazione(new Data());
			}
			if(this.riscontro.getOraRegistrazione().getSorgente()==null){
				this.riscontro.getOraRegistrazione().setSorgente(new TipoData());
			}
			this.riscontro.getOraRegistrazione().getSorgente().setBase(tipoOraRegistrazioneValue);
		}
		else {
			if(this.riscontro.getOraRegistrazione()!=null){
				if(this.riscontro.getOraRegistrazione().getSorgente()!=null){
					if(this.riscontro.getOraRegistrazione().getSorgente().getTipo()==null){
						this.riscontro.getOraRegistrazione().setSorgente(null);
					}
					else{
						this.riscontro.getOraRegistrazione().getSorgente().setBase(null);
					}	
				}
				if(this.riscontro.getOraRegistrazione().getSorgente()==null && this.riscontro.getOraRegistrazione().getDateTime()==null){
					this.riscontro.setOraRegistrazione(null);
				}
			}
		}
	}

	public Date getOraRegistrazione() {
		if(this.riscontro.getOraRegistrazione()!=null){
			return this.riscontro.getOraRegistrazione().getDateTime();
		}
		return null;
	}
	public void setOraRegistrazione(Date value) {
		if(value!=null){
			if(this.riscontro.getOraRegistrazione()==null){
				this.riscontro.setOraRegistrazione(new Data());
			}
			this.riscontro.getOraRegistrazione().setDateTime(value);
		}
		else {
			if(this.riscontro.getOraRegistrazione()!=null){
				if(this.riscontro.getOraRegistrazione().getSorgente()==null){
					this.riscontro.setOraRegistrazione(null);
				}
				else{
					this.riscontro.getOraRegistrazione().setDateTime(null);
				}
			}
		}
	}






	@Override
	public Riscontro clone(){

		Riscontro clone = new Riscontro();

		// id
		clone.setId(this.getId()!=null ? new Long(this.getId()) : null);
		
		clone.setID(this.getID()!=null ? new String(this.getID()) : null);

		clone.setOraRegistrazione(this.getOraRegistrazione()!=null ? new Date(this.getOraRegistrazione().getTime()) : null);

		clone.setTipoOraRegistrazione(this.getTipoOraRegistrazione());
		clone.setTipoOraRegistrazioneValue(this.riscontro.getOraRegistrazione()!=null && 
				this.riscontro.getOraRegistrazione().getSorgente()!=null &&
				this.riscontro.getOraRegistrazione().getSorgente().getBase()!=null ? new String(this.riscontro.getOraRegistrazione().getSorgente().getBase()) : null);

		return clone;
	}
}





