/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.NotImplementedException;
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

/**
 * <p>Java class for eccezione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eccezione">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codiceEccezione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contestoCodifica" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="posizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rilevanza" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eccezione", propOrder = {
    "codiceEccezione",
    "contestoCodifica",
    "posizione",
    "rilevanza"
})

public class Eccezione implements java.io.Serializable{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	@XmlTransient
	private CodiceErroreCooperazione codiceEccezione;
	@XmlTransient
	private SubCodiceErrore subCodiceEccezione;
	@XmlElement(name="codiceEccezione")
	protected String codiceEccezioneValue;

    protected String descrizione;
    
    @XmlTransient
    private ErroreCooperazione errore;
	
	@XmlTransient
	private ContestoCodificaEccezione contestoCodifica;
    protected String contestoCodificaValue;
    
	@XmlTransient
    private LivelloRilevanza rilevanza;
	@XmlElement(name="rilevanza")
	protected String rilevanzaValue;
    
	private String modulo;
		
	@XmlTransient
	private SOAPFaultCode soapFaultCode;
	
	/* Metodi da utilizzare solo nelle implementazioni dei protocolli */
	public Eccezione() {
	}
	
    public Eccezione(String codiceEcc, String descrizione, boolean isErroreValidazione, IProtocolFactory protocolFactory) throws ProtocolException{
		this(new ErroreCooperazione(descrizione,CodiceErroreCooperazione.UNKNOWN), isErroreValidazione, null, protocolFactory); // codice lo imposto subito dopo
		this.codiceEccezioneValue = codiceEcc;
		this.codiceEccezione = protocolFactory.createTraduttore().toCodiceErroreCooperazione(codiceEcc);
		this.descrizione = descrizione; // in modo che non venga tradotto alla chiamata della get
	}
    public Eccezione(CodiceErroreCooperazione codiceEcc, String descrizione, boolean isErroreValidazione, IProtocolFactory protocolFactory) throws ProtocolException{
		this(new ErroreCooperazione(descrizione,codiceEcc), isErroreValidazione, null, protocolFactory);
		this.descrizione = descrizione; // in modo che non venga tradotto alla chiamata della get
	}
    
    /* Metodi da utilizzare per fare attuare la traduzione della descrizione */
	public Eccezione(ErroreCooperazione errore,boolean isErroreValidazione, String modulo,IProtocolFactory protocolFactory) throws ProtocolException{
		if(isErroreValidazione){
			this.contestoCodifica = ContestoCodificaEccezione.INTESTAZIONE;
			this.contestoCodificaValue = protocolFactory.createTraduttore().toString(this.contestoCodifica);
		}else{
			this.contestoCodifica = ContestoCodificaEccezione.PROCESSAMENTO;
			this.contestoCodificaValue = protocolFactory.createTraduttore().toString(this.contestoCodifica);
		}
		this.errore = errore;
		this.codiceEccezione = this.errore.getCodiceErrore(); // viene tradotto dopo nel metodo get
		//this.descrizione = this.errore.getDescrizioneRawValue(); // viene tradotto dopo nel metodo get
		this.rilevanza = LivelloRilevanza.ERROR;
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
	
	
	
	
	/* --- CODICE ECCEZIONE --- */
	
    public CodiceErroreCooperazione getCodiceEccezione() {
        return this.codiceEccezione;
    }
    public void setCodiceEccezione(CodiceErroreCooperazione value) {
        this.codiceEccezione = value;
    }
    
    public String getCodiceEccezioneValue(IProtocolFactory protocolFactory) throws ProtocolException {
    	return this.codiceEccezioneValue == null ? protocolFactory.createTraduttore().toString(this.codiceEccezione,this.subCodiceEccezione) : this.codiceEccezioneValue;
    }
    public void setCodiceEccezioneValue(String value) {
        this.codiceEccezioneValue = value;
    }
    
	public SubCodiceErrore getSubCodiceEccezione() {
		return this.subCodiceEccezione;
	}
	public void setSubCodiceEccezione(SubCodiceErrore subCodiceEccezione) {
		this.subCodiceEccezione = subCodiceEccezione;
	}
    
	
	
	
	/* --- DESCRIZIONE ECCEZIONE --- */
	
	public String getDescrizione(IProtocolFactory protocolFactory) throws ProtocolException {
        return this.descrizione == null ? protocolFactory.createTraduttore().toString(this.errore) : this.descrizione;
    }
    public void setDescrizione(String value) {
        this.descrizione = value;
    }

    
    
    
    /* --- ERRORE COOPERAZIONE --- */
    
    @XmlTransient
    public ErroreCooperazione getErrore() {
		return this.errore;
	}
    
    
    
    
	/* --- CONTESTO CODIFICA ECCEZIONE --- */

    public ContestoCodificaEccezione getContestoCodifica() {
        return this.contestoCodifica;
    }
    public void setContestoCodifica(ContestoCodificaEccezione value) {
        this.contestoCodifica = value;
    }
    
    public String getContestoCodificaValue(IProtocolFactory protocolFactory) throws ProtocolException {
    	return this.contestoCodificaValue == null ? protocolFactory.createTraduttore().toString(this.contestoCodifica) : this.contestoCodificaValue;
    }
    public void setContestoCodificaValue(String value) {
        this.contestoCodificaValue = value;
    }
 
   
    
    
    
    /* --- LIVELLO RILEVANZA ECCEZIONE --- */
    
    public LivelloRilevanza getRilevanza() {
        return this.rilevanza;
    }
    public void setRilevanza(LivelloRilevanza value) {
        this.rilevanza = value;
    }
    
    public String getRilevanzaValue(IProtocolFactory protocolFactory) throws ProtocolException {
		return this.rilevanzaValue == null ? protocolFactory.createTraduttore().toString(this.rilevanza) : this.rilevanzaValue;
    }
    public void setRilevanzaValue(String value) {
        this.rilevanzaValue = value;
    }


    
    
    /* --- MODULO FUNZIONALE --- */
    
	public String getModulo() {
		return this.modulo;
	}
	public void setModulo(String modulo) {
		this.modulo = modulo;
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
		
		if(this.rilevanza!=null){
			bf.append(" ");	
			bf.append(this.getRilevanzaValue(protocolFactory));
		}
		
		if(this.codiceEccezione!=null){
			bf.append(" con codice [");
			bf.append(this.getCodiceEccezioneValue(protocolFactory));
			bf.append("]");
		}
		
		if(this.codiceEccezione!=null && this.contestoCodifica!=null){
			bf.append(" -");
		}
		
		if(this.contestoCodifica!=null){
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
		
		clone.setCodiceEccezione(this.codiceEccezione);
		clone.setCodiceEccezioneValue(this.codiceEccezioneValue!=null ? new String(this.codiceEccezioneValue) : null );
		clone.setSubCodiceEccezione(this.subCodiceEccezione!=null ? this.subCodiceEccezione.clone() : null);
		
		clone.setDescrizione(this.descrizione!=null ? new String(this.descrizione) : null);
		
		clone.errore = (this.errore!=null ? this.errore.clone() : null);
		
		clone.setContestoCodifica(this.contestoCodifica);
		clone.setContestoCodificaValue(this.contestoCodificaValue!=null ? new String(this.contestoCodificaValue) : null);
		
		clone.setRilevanza(this.rilevanza);
		clone.setRilevanzaValue(this.rilevanzaValue!=null ? new String(this.rilevanzaValue) : null);
		
		clone.setModulo(this.modulo!=null ? new String(this.modulo) : null);
		
		clone.setSoapFaultCode(this.soapFaultCode);
		
		return clone;
	}

}





