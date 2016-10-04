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

import org.apache.commons.lang.NotImplementedException;
import org.openspcoop2.core.tracciamento.CodiceEccezione;
import org.openspcoop2.core.tracciamento.constants.TipoCodificaEccezione;
import org.openspcoop2.core.tracciamento.constants.TipoRilevanzaEccezione;
import org.openspcoop2.message.SOAPFaultCode;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ContestoCodificaEccezione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.LivelloRilevanza;
import org.openspcoop2.protocol.sdk.constants.SubCodiceErrore;




/**
 * Classe utilizzata per rappresentare una Eccezione.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Eccezione implements java.io.Serializable{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private org.openspcoop2.core.tracciamento.Eccezione eccezione;
	
	private ErroreCooperazione errore;
	
	private SOAPFaultCode soapFaultCode;
	

	
	
	/* Metodi da utilizzare solo nelle implementazioni dei protocolli */
	public Eccezione() {
		this.eccezione = new org.openspcoop2.core.tracciamento.Eccezione();
	}
	
    public Eccezione(String codiceEcc, String descrizione, boolean isErroreValidazione, IProtocolFactory protocolFactory) throws ProtocolException{
		this(new ErroreCooperazione(descrizione,CodiceErroreCooperazione.UNKNOWN), isErroreValidazione, null, protocolFactory); // codice lo imposto subito dopo
		this.setCodiceEccezioneValue(codiceEcc);
		this.setCodiceEccezione(protocolFactory.createTraduttore().toCodiceErroreCooperazione(codiceEcc));
		this.setDescrizione(descrizione); // in modo che non venga tradotto alla chiamata della get
	}
    public Eccezione(CodiceErroreCooperazione codiceEcc, String descrizione, boolean isErroreValidazione, IProtocolFactory protocolFactory) throws ProtocolException{
		this(new ErroreCooperazione(descrizione,codiceEcc), isErroreValidazione, null, protocolFactory);
		this.setDescrizione(descrizione); // in modo che non venga tradotto alla chiamata della get
	}
    
    /* Metodi da utilizzare per fare attuare la traduzione della descrizione */
	public Eccezione(ErroreCooperazione errore,boolean isErroreValidazione, String modulo,IProtocolFactory protocolFactory) throws ProtocolException{
		this.eccezione = new org.openspcoop2.core.tracciamento.Eccezione();
		if(isErroreValidazione){
			this.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			this.setContestoCodificaValue(protocolFactory.createTraduttore().toString(this.getContestoCodifica()));
		}else{
			this.setContestoCodifica(ContestoCodificaEccezione.PROCESSAMENTO);
			this.setContestoCodificaValue(protocolFactory.createTraduttore().toString(this.getContestoCodifica()));
		}
		this.errore = errore;
		this.setCodiceEccezione(this.errore.getCodiceErrore()); // viene tradotto dopo nel metodo get
		//this.setDescrizione(this.errore.getDescrizioneRawValue()); // viene tradotto dopo nel metodo get
		this.setRilevanza(LivelloRilevanza.ERROR);
	}
	
	/* Wrapper */
	public Eccezione(org.openspcoop2.core.tracciamento.Eccezione eccezione){
		this.eccezione = eccezione;
		this.errore = new ErroreCooperazione(this.eccezione.getDescrizione(),this.getCodiceEccezione());
	}

	// base
	
	public org.openspcoop2.core.tracciamento.Eccezione getEccezione() {
		return this.eccezione;
	}
	public void setEccezione(org.openspcoop2.core.tracciamento.Eccezione eccezione) {
		this.eccezione = eccezione;
		this.errore = new ErroreCooperazione(this.eccezione.getDescrizione(),this.getCodiceEccezione());
	}
	
	
	
	// id  [Wrapper]
	
	public Long getId() {
		return this.eccezione.getId();
	}
	public void setId(Long id) {
		this.eccezione.setId(id);
	}
	
	
	
	/* Metodi da utilizzare solo nelle implementazioni dei protocolli */
	public static Eccezione newEccezione(){
		Eccezione ecc = new Eccezione();
		return ecc;
	}
	public static Eccezione getEccezioneValidazione(CodiceErroreCooperazione codiceEcc, String descrizione, IProtocolFactory protocolFactory) throws ProtocolException{
		return new Eccezione(codiceEcc,descrizione, true, protocolFactory);
	}
	public static Eccezione getEccezioneProcessamento(CodiceErroreCooperazione codiceEcc, String descrizione, IProtocolFactory protocolFactory) throws ProtocolException{
		return new Eccezione(codiceEcc,descrizione, false, protocolFactory);
	}
	
	/* Metodi da utilizzare per fare attuare la traduzione della descrizione */
	public static Eccezione getEccezioneValidazione(ErroreCooperazione errore, IProtocolFactory protocolFactory) throws ProtocolException{
		return new Eccezione(errore, true, null, protocolFactory);
	}
	public static Eccezione getEccezioneProcessamento(ErroreCooperazione errore, IProtocolFactory protocolFactory) throws ProtocolException{
		return new Eccezione(errore, false, null, protocolFactory);
	}
	
	
	
	
	
	/* --- CODICE ECCEZIONE [Wrapper] --- */
	
    public CodiceErroreCooperazione getCodiceEccezione() {
    	if(this.eccezione.getCodice()!=null && this.eccezione.getCodice().getTipo()!=null){
    		return CodiceErroreCooperazione.toCodiceErroreCooperazione(this.eccezione.getCodice().getTipo());
    	}
        return null;
    }
    public void setCodiceEccezione(CodiceErroreCooperazione value) {
        if(value!=null){
        	if(this.eccezione.getCodice()==null){
        		this.eccezione.setCodice(new CodiceEccezione());
        	}
        	this.eccezione.getCodice().setTipo(value.getCodice());
        }
        else{
        	if(this.eccezione.getCodice()!=null){
        		if(this.eccezione.getCodice().getBase()==null && this.eccezione.getCodice().getSottotipo()==null){
        			this.eccezione.setCodice(null);
        		}
        		else{
        			this.eccezione.getCodice().setTipo(null);
        		}
        	}
        }
    }
    
    public String getCodiceEccezioneValue(IProtocolFactory protocolFactory) throws ProtocolException {
    	String codiceEccezioneValue = null;
    	if(this.eccezione.getCodice()!=null){
    		codiceEccezioneValue = this.eccezione.getCodice().getBase();
    	}
    	return codiceEccezioneValue == null ? protocolFactory.createTraduttore().toString(this.getCodiceEccezione(),this.getSubCodiceEccezione()) : codiceEccezioneValue;
    }
    public void setCodiceEccezioneValue(String value) {
        if(value!=null){
        	if(this.eccezione.getCodice()==null){
        		this.eccezione.setCodice(new CodiceEccezione());
        	}
        	this.eccezione.getCodice().setBase(value);
        }
        else{
        	if(this.eccezione.getCodice()!=null){
        		if(this.eccezione.getCodice().getTipo()==null && this.eccezione.getCodice().getSottotipo()==null){
        			this.eccezione.setCodice(null);
        		}
        		else{
        			this.eccezione.getCodice().setBase(null);
        		}
        	}
        }
    }
    
	public SubCodiceErrore getSubCodiceEccezione() {
		if(this.eccezione.getCodice()!=null && this.eccezione.getCodice().getSottotipo()!=null){
			SubCodiceErrore sub = new SubCodiceErrore();
			sub.setSubCodice(this.eccezione.getCodice().getSottotipo());
			return sub;
    	}
        return null;
	}
	public void setSubCodiceEccezione(SubCodiceErrore subCodiceEccezione) {
		if(subCodiceEccezione!=null){
        	if(this.eccezione.getCodice()==null){
        		this.eccezione.setCodice(new CodiceEccezione());
        	}
        	this.eccezione.getCodice().setSottotipo(subCodiceEccezione.getSubCodice());
        }
        else{
        	if(this.eccezione.getCodice()!=null){
        		if(this.eccezione.getCodice().getTipo()==null && this.eccezione.getCodice().getBase()==null){
        			this.eccezione.setCodice(null);
        		}
        		else{
        			this.eccezione.getCodice().setSottotipo(null);
        		}
        	}
        }
	}
    
	
	
	
	/* --- DESCRIZIONE ECCEZIONE [Wrapper] --- */
	
	public String getDescrizione(IProtocolFactory protocolFactory) throws ProtocolException {
		String descrizione = this.eccezione.getDescrizione();
        return descrizione == null ? protocolFactory.createTraduttore().toString(this.getErrore()) : descrizione;
    }
    public void setDescrizione(String value) {
       this.eccezione.setDescrizione(value);
    }

    
    
    
    /* --- ERRORE COOPERAZIONE --- */
    
    public ErroreCooperazione getErrore() {
		return this.errore;
	}
    
    
    
    
	/* --- CONTESTO CODIFICA ECCEZIONE --- */

    public ContestoCodificaEccezione getContestoCodifica() {
    	if(this.eccezione.getContestoCodifica()!=null && this.eccezione.getContestoCodifica().getTipo()!=null){
    		switch (this.eccezione.getContestoCodifica().getTipo()) {
			case ECCEZIONE_PROCESSAMENTO:
				return ContestoCodificaEccezione.PROCESSAMENTO;
			case ECCEZIONE_VALIDAZIONE_PROTOCOLLO:
				return ContestoCodificaEccezione.INTESTAZIONE;
			case SCONOSCIUTO:
				return null;
			}
    	}
        return null;
    }
    public void setContestoCodifica(ContestoCodificaEccezione value) {
    	if(value!=null){
        	if(this.eccezione.getContestoCodifica()==null){
        		this.eccezione.setContestoCodifica(new org.openspcoop2.core.tracciamento.ContestoCodificaEccezione());
        	}
        	switch (value) {
			case INTESTAZIONE:
				this.eccezione.getContestoCodifica().setTipo(TipoCodificaEccezione.ECCEZIONE_VALIDAZIONE_PROTOCOLLO);
				break;
			case PROCESSAMENTO:
				this.eccezione.getContestoCodifica().setTipo(TipoCodificaEccezione.ECCEZIONE_PROCESSAMENTO);
				break;
			default:
				this.eccezione.getContestoCodifica().setTipo(TipoCodificaEccezione.SCONOSCIUTO);
				break;
			}
        }
        else{
        	if(this.eccezione.getContestoCodifica()!=null){
        		if(this.eccezione.getContestoCodifica().getBase()==null){
        			this.eccezione.setContestoCodifica(null);
        		}
        		else{
        			this.eccezione.getContestoCodifica().setTipo(null);
        		}
        	}
        }
    }
    
    public String getContestoCodificaValue(IProtocolFactory protocolFactory) throws ProtocolException {
    	String contestoCodificaValue = null;
    	if(this.eccezione.getContestoCodifica()!=null){
    		contestoCodificaValue = this.eccezione.getContestoCodifica().getBase();
    	}
    	return contestoCodificaValue == null ? protocolFactory.createTraduttore().toString(this.getContestoCodifica()) : contestoCodificaValue;
    }
    public void setContestoCodificaValue(String value) {
    	if(value!=null){
        	if(this.eccezione.getContestoCodifica()==null){
        		this.eccezione.setContestoCodifica(new org.openspcoop2.core.tracciamento.ContestoCodificaEccezione());
        	}
        	this.eccezione.getContestoCodifica().setBase(value);
        }
        else{
        	if(this.eccezione.getContestoCodifica()!=null){
        		if(this.eccezione.getContestoCodifica().getTipo()==null){
        			this.eccezione.setContestoCodifica(null);
        		}
        		else{
        			this.eccezione.getContestoCodifica().setBase(null);
        		}
        	}
        }
    }
 
   
    
    
    
    /* --- LIVELLO RILEVANZA ECCEZIONE --- */
    
    public LivelloRilevanza getRilevanza() {
    	if(this.eccezione.getRilevanza()!=null && this.eccezione.getRilevanza().getTipo()!=null){
    		switch (this.eccezione.getRilevanza().getTipo()) {
			case DEBUG:
				return LivelloRilevanza.DEBUG;
			case INFO:
				return LivelloRilevanza.INFO;
			case WARN:
				return LivelloRilevanza.WARN;
			case ERROR:
				return LivelloRilevanza.ERROR;
			case FATAL:
				return LivelloRilevanza.FATAL;
			case SCONOSCIUTO:
				return LivelloRilevanza.UNKNOWN;
			}
    	}
        return null;
    }
    public void setRilevanza(LivelloRilevanza value) {
    	if(value!=null){
        	if(this.eccezione.getRilevanza()==null){
        		this.eccezione.setRilevanza(new org.openspcoop2.core.tracciamento.RilevanzaEccezione());
        	}
        	switch (value) {
			case DEBUG:
				this.eccezione.getRilevanza().setTipo(TipoRilevanzaEccezione.DEBUG);
				break;
			case INFO:
				this.eccezione.getRilevanza().setTipo(TipoRilevanzaEccezione.INFO);
				break;
			case ERROR:
				this.eccezione.getRilevanza().setTipo(TipoRilevanzaEccezione.ERROR);
				break;
			case FATAL:
				this.eccezione.getRilevanza().setTipo(TipoRilevanzaEccezione.FATAL);
				break;
			case WARN:
				this.eccezione.getRilevanza().setTipo(TipoRilevanzaEccezione.WARN);
				break;
			case UNKNOWN:
				this.eccezione.getRilevanza().setTipo(TipoRilevanzaEccezione.SCONOSCIUTO);
				break;
			}
        }
        else{
        	if(this.eccezione.getRilevanza()!=null){
        		if(this.eccezione.getRilevanza().getBase()==null){
        			this.eccezione.setRilevanza(null);
        		}
        		else{
        			this.eccezione.getRilevanza().setTipo(null);
        		}
        	}
        }
    }
    
    public String getRilevanzaValue(IProtocolFactory protocolFactory) throws ProtocolException {
    	String rilevanzaValue = null;
    	if(this.eccezione.getRilevanza()!=null){
    		rilevanzaValue = this.eccezione.getRilevanza().getBase();
    	}
		return rilevanzaValue == null ? protocolFactory.createTraduttore().toString(this.getRilevanza()) : rilevanzaValue;
    }
    public void setRilevanzaValue(String value) {
    	if(value!=null){
        	if(this.eccezione.getRilevanza()==null){
        		this.eccezione.setRilevanza(new org.openspcoop2.core.tracciamento.RilevanzaEccezione());
        	}
        	this.eccezione.getRilevanza().setBase(value);
        }
        else{
        	if(this.eccezione.getRilevanza()!=null){
        		if(this.eccezione.getRilevanza().getTipo()==null){
        			this.eccezione.setRilevanza(null);
        		}
        		else{
        			this.eccezione.getRilevanza().setBase(null);
        		}
        	}
        }
    }



    
    
    /* --- MODULO FUNZIONALE --- */
    
	public String getModulo() {
		return this.eccezione.getModulo();
	}
	public void setModulo(String modulo) {
		this.eccezione.setModulo(modulo);
	}
	
	
	public SOAPFaultCode getSoapFaultCode() {
		return this.soapFaultCode;
	}
	
	public void setSoapFaultCode(SOAPFaultCode soapFaultCode) {
		this.soapFaultCode = soapFaultCode;
	}
	
	
	
	
	
	/* --- TO STRING --- */
	@Override
	public String toString(){
		throw new NotImplementedException("Use with protocolFactory");
	}
	
	public String toString(IProtocolFactory protocolFactory) throws ProtocolException{
		StringBuffer bf = new StringBuffer();
		bf.append("Eccezione");
		
		if(this.getRilevanza()!=null){
			bf.append(" ");	
			bf.append(this.getRilevanzaValue(protocolFactory));
		}
		
		if(this.getCodiceEccezione()!=null){
			bf.append(" con codice [");
			bf.append(this.getCodiceEccezioneValue(protocolFactory));
			bf.append("]");
		}
		
		if(this.getCodiceEccezione()!=null && this.getContestoCodifica()!=null){
			bf.append(" -");
		}
		
		if(this.getContestoCodifica()!=null){
			bf.append(" ");
			bf.append(this.getContestoCodificaValue(protocolFactory));
		}
		
		//if(this.descrizione!=null){
		String descrizioneErrore = this.getDescrizione(protocolFactory);
		if(descrizioneErrore!=null && !"".equals(descrizioneErrore)){
			//bf.append(" , descrizione errore: ");
			bf.append(": ");
			bf.append(descrizioneErrore);
		}
		//}
		
		return bf.toString();
	}

	@Override
	public Eccezione clone(){
		Eccezione clone = new Eccezione();
		
		// id
		clone.setId(this.getId()!=null ? new Long(this.getId()) : null);
		
		clone.setCodiceEccezione(this.getCodiceEccezione());
		clone.setCodiceEccezioneValue(this.eccezione.getCodice()!=null && this.eccezione.getCodice().getBase()!=null ? 
				new String(this.eccezione.getCodice().getBase()) : null );
		clone.setSubCodiceEccezione(this.getSubCodiceEccezione()!=null ? this.getSubCodiceEccezione().clone() : null);
		
		clone.setDescrizione(this.eccezione.getDescrizione()!=null ? new String(this.eccezione.getDescrizione()) : null);
		
		clone.errore = (this.errore!=null ? this.errore.clone() : null);
		
		clone.setContestoCodifica(this.getContestoCodifica());
		clone.setContestoCodificaValue(this.eccezione.getContestoCodifica()!=null && this.eccezione.getContestoCodifica().getBase()!=null ? 
				new String(this.eccezione.getContestoCodifica().getBase()) : null);
		
		clone.setRilevanza(this.getRilevanza());
		clone.setRilevanzaValue(this.eccezione.getRilevanza()!=null && this.eccezione.getRilevanza().getBase()!=null ? 
				new String(this.eccezione.getRilevanza().getBase()) : null);
		
		clone.setModulo(this.getModulo()!=null ? new String(this.getModulo()) : null);
		
		clone.setSoapFaultCode(this.soapFaultCode);
		
		return clone;
	}

}





