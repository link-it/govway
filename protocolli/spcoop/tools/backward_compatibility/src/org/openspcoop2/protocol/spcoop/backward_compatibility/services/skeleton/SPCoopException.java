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


package org.openspcoop2.protocol.spcoop.backward_compatibility.services.skeleton;


/**
 * <p>Java class for SPCoopException complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SPCoopException">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codiceEccezione" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="descrizioneEccezione" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="identificativoFunzione" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="identificativoPorta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="oraRegistrazione" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tipoEccezione" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */

/**
 * Contiene la definizione di una eccezione lanciata dal servizio IntegrationManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "SPCoopException", propOrder = {
    "codiceEccezione",
    "descrizioneEccezione",
    "identificativoFunzione",
    "identificativoPorta",
    "oraRegistrazione",
    "tipoEccezione"
})
@javax.xml.ws.WebFault(name = "org.openspcoop.pdd.services.SPCoopException", targetNamespace = "http://services.pdd.openspcoop.org")

// org.apache.cxf.binding.soap.SoapFault
public class SPCoopException extends Exception implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4100781617691120752L;
	
	public final static String ECCEZIONE_BUSTA = "EccezioneBusta";
	public final static String ECCEZIONE_PROCESSAMENTO = "EccezioneProcessamento";
	
	public final static String MSG_CONTEXT_CODICE_ECCEZIONE = "SPCoopExceptionCodiceEccezione";
	
	/** Variabili */
	@javax.xml.bind.annotation.XmlElement(required = true, nillable = true)
    public String codiceEccezione;
	@javax.xml.bind.annotation.XmlElement(required = true, nillable = true)
    public String descrizioneEccezione;
	@javax.xml.bind.annotation.XmlElement(required = true, nillable = true)
    public String identificativoFunzione;
	@javax.xml.bind.annotation.XmlElement(required = true, nillable = true)
    public String identificativoPorta;
	@javax.xml.bind.annotation.XmlElement(required = true, nillable = true)
    public String oraRegistrazione;
	@javax.xml.bind.annotation.XmlElement(required = true, nillable = true)
    public String tipoEccezione;
	
	
	/** Costruttori */
	public SPCoopException(String descrizioneEccezione) { 
		super(descrizioneEccezione);
	}
	
	
	/**
	 * Imposta il codice dell'eccezione
	 *
	 * @param codice Codice dell'eccezione
	 * 
	 */
	public void setCodiceEccezione(String codice){
		this.codiceEccezione = codice;
	}
	/**
	 * Imposta la descrizione dell'eccezione
	 *
	 * @param descrizione Descrizione dell'eccezione
	 * 
	 */
	public void setDescrizioneEccezione(String descrizione){
		this.descrizioneEccezione = descrizione;
	}
	/**
	 * Imposta il tipo dell'eccezione
	 *
	 * @param tipo Tipo dell'eccezione
	 * 
	 */
	public void setTipoEccezione(String tipo){
		this.tipoEccezione = tipo;
	}
	/**
	 * Imposta l'ora di Registrazione
	 *
	 * @param ora Ora dell'eccezione
	 * 
	 */
	public void setOraRegistrazione(String ora){
		this.oraRegistrazione = ora;
	}
	/**
	 * Imposta l'identificativo della porta di dominio che ha lanciato l'eccezione
	 *
	 * @param id Identificativo della porta di dominio che ha lanciato l'eccezione
	 * 
	 */
	public void setIdentificativoPorta(String id){
		this.identificativoPorta = id;
	}
	/**
	 * Imposta l'identificativo del modulo funzionale che ha lanciato l'eccezione
	 *
	 * @param id Identificativo del modulo funzionale che ha lanciato l'eccezione
	 * 
	 */
	public void setIdentificativoFunzione(String id){
		this.identificativoFunzione = id;
	}


	/**
	 * Ritorna il codice dell'eccezione
	 *
	 * @return codice dell'eccezione
	 * 
	 */
	public String getCodiceEccezione(){
		return this.codiceEccezione;
	}
	/**
	 * Ritorna la descrizione dell'eccezione
	 *
	 * @return descrizione dell'eccezione
	 * 
	 */
	public String getDescrizioneEccezione(){
		return this.descrizioneEccezione;
	}
	/**
	 * Ritorna il tipo dell'eccezione
	 *
	 * @return Tipo dell'eccezione
	 * 
	 */
	public String getTipoEccezione(){
		return this.tipoEccezione;
	}
	/**
	 * Ritorna l'ora di Registrazione
	 *
	 * @return Ora dell'eccezione
	 * 
	 */
	public String getOraRegistrazione(){
		return this.oraRegistrazione;
	}
	/**
	 * Ritorna l'identificativo della porta di dominio che ha lanciato l'eccezione
	 *
	 * @return Identificativo della porta di dominio che ha lanciato l'eccezione
	 * 
	 */
	public String getIdentificativoPorta(){
		return this.identificativoPorta;
	}
	/**
	 * Ritorna l'identificativo del modulo funzionale che ha lanciato l'eccezione
	 *
	 * @return Identificativo del modulo funzionale che ha lanciato l'eccezione
	 * 
	 */
	public String getIdentificativoFunzione(){
		return this.identificativoFunzione;
	}
}
