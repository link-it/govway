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



package org.openspcoop2.protocol.spcoop.backward_compatibility.services.skeleton;



/**
 * <p>Java class for SPCoopMessage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SPCoopMessage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IDApplicativo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="imbustamento" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="servizioApplicativo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="spcoopHeaderInfo" type="{http://services.pdd.openspcoop.org}SPCoopHeaderInfo"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */

/**
 * Classe utilizzata per raccogliere il contenuto applicativo da ritornare
 * ai servizi applicativi che utilizzano il servizio Gop.
 *
 * @author Lo Votrico Fabio (fabio@link.it)
 * @author Poli Andrea (apoli@link.i
 * @author Tronci Fabio (tronci@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "SPCoopMessage", propOrder = {
    "idApplicativo",
    "imbustamento",
    "message",
    "servizioApplicativo",
    "spcoopHeaderInfo"
})
public class SPCoopMessage implements java.io.Serializable{

    @javax.xml.bind.annotation.XmlElement(name = "IDApplicativo", required = true, nillable = true)
    protected String idApplicativo;
    
    protected boolean imbustamento;
    @javax.xml.bind.annotation.XmlElement(required = true, nillable = true)
    protected byte[] message;
    @javax.xml.bind.annotation.XmlElement(required = true, nillable = true)
    protected String servizioApplicativo;
    @javax.xml.bind.annotation.XmlElement(required = true, nillable = true)
    protected SPCoopHeaderInfo spcoopHeaderInfo;

	/**
	 * SerialUID
	 */
	private static final long serialVersionUID = 1L;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore. 
	 *
	 * 
	 */   
	public SPCoopMessage(){
		this.message = null;
		this.imbustamento = false;
	}
	
	





	/* ********  S E T T E R   ******** */

	/**
	 * Imposta il messaggio applicativo
	 *
	 * @param m Messaggio Applicativo
	 * 
	 */
	public void setMessage(byte [] m) {
		this.message = m;
	}
	



	/* ********  G E T T E R   ******** */

	/**
	 * Ritorna il messaggio applicativo
	 *
	 * @return Messaggio Applicativo
	 * 
	 */
	public byte[] getMessage(){
		return this.message;
	}
	





	/**
	 * Ritorna l'indicazione se il messaggio applicativo deve essere imbustato o meno
	 * 
	 * @return l'indicazione se il messaggio applicativo deve essere imbustato o meno
	 */
	public boolean getImbustamento() {
		return this.imbustamento;
	}


	/**
	 * Imposta l'indicazione se il messaggio applicativo deve essere imbustato o meno
	 * 
	 * @param imbustamento indicazione se il messaggio applicativo deve essere imbustato o meno
	 */
	public void setImbustamento(boolean imbustamento) {
		this.imbustamento = imbustamento;
	}
	/**
	 * ID Applicativo per correlazione applicativa
	 * 
	 * @return the iDApplicativo
	 */
	public String getIdApplicativo() {
		return this.idApplicativo;
	}
	/**
	 * ID Applicativo per correlazione applicativa
	 * 
	 * @param applicativo the iDApplicativo to set
	 */
	public void setIdApplicativo(String applicativo) {
		this.idApplicativo = applicativo;
	}
	/**
	 * Servizio Applicativo
	 * 
	 * @return the servizioApplicativo
	 */
	public String getServizioApplicativo() {
		return this.servizioApplicativo;
	}
	/**
	 * @param servizioApplicativo the servizioApplicativo to set
	 */
	public void setServizioApplicativo(String servizioApplicativo) {
		this.servizioApplicativo = servizioApplicativo;
	}
	/**
	 * SPCoop header info
	 * 
	 * @return the spcoopHeaderInfo
	 */
	public SPCoopHeaderInfo getSpcoopHeaderInfo() {
		return this.spcoopHeaderInfo;
	}
	/**
	 * SPCoop header info
	 * 
	 * @param spcoopHeaderInfo the spcoopHeaderInfo to set
	 */
	public void setSpcoopHeaderInfo(SPCoopHeaderInfo spcoopHeaderInfo) {
		this.spcoopHeaderInfo = spcoopHeaderInfo;
	}
}
