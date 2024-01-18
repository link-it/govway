/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.services.skeleton;

import java.io.ByteArrayOutputStream;

import org.openspcoop2.message.OpenSPCoop2Message;


/**
 * <p>Java class for IntegrationManagerMessage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="IntegrationManagerMessage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IDApplicativo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="imbustamento" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="servizioApplicativo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="protocolHeaderInfo" type="{http://services.pdd.openspcoop2.org}ProtocolHeaderInfo"/>
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
@javax.xml.bind.annotation.XmlType(name = "IntegrationManagerMessage", propOrder = {
    "idApplicativo",
    "imbustamento",
    "message",
    "servizioApplicativo",
    "protocolHeaderInfo"
})
public class IntegrationManagerMessage implements java.io.Serializable{

    @javax.xml.bind.annotation.XmlElement(name = "IDApplicativo", required = true, nillable = true)
    protected String idApplicativo;
    
    protected boolean imbustamento;
    @javax.xml.bind.annotation.XmlElement(required = true, nillable = true)
    protected byte[] message;
    @javax.xml.bind.annotation.XmlElement(required = true, nillable = true)
    protected String servizioApplicativo;
    @javax.xml.bind.annotation.XmlElement(required = true, nillable = true)
    protected ProtocolHeaderInfo protocolHeaderInfo;

	/**
	 * SerialUID
	 */
	private static final long serialVersionUID = 1L;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore. 
	 *
	 * @param m Messaggio Applicativo
	 * @param imbustamento Indicazione se il messaggio applicativo deve essere imbustato o meno
	 * @param protocol Informazioni presenti nella busta
	 * 
	 */    
	public IntegrationManagerMessage(byte[] m,boolean imbustamento,ProtocolHeaderInfo protocol) throws Exception{
		this.message = m;
		this.imbustamento = imbustamento;
		this.protocolHeaderInfo = protocol;
	}
	/**
	 * Costruttore. 
	 *
	 * @param m Messaggio Applicativo
	 * @param imbustamento Indicazione se il messaggio applicativo deve essere imbustato o meno
	 * 
	 */    
	public IntegrationManagerMessage(byte[] m,boolean imbustamento) throws Exception{
		this(m,imbustamento,null);
	}
	/**
	 * Costruttore. 
	 *
	 * @param m Messaggio Applicativo
	 * @param imbustamento Indicazione se il messaggio applicativo deve essere imbustato o meno
	 * @param protocol Informazioni presenti nella busta
	 * 
	 */    
	public IntegrationManagerMessage(OpenSPCoop2Message m,boolean imbustamento,ProtocolHeaderInfo protocol) throws Exception{
		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
		m.writeTo(byteBuffer, true);
		this.message = byteBuffer.toByteArray();
		byteBuffer.close();
		this.imbustamento = imbustamento;
		this.protocolHeaderInfo = protocol;
	}
	/**
	 * Costruttore. 
	 *
	 * @param m Messaggio Applicativo
	 * @param imbustamento Indicazione se il messaggio applicativo deve essere imbustato o meno
	 * 
	 */    
	public IntegrationManagerMessage(OpenSPCoop2Message m,boolean imbustamento) throws Exception{
		this(m,true,null);
	}
	/**
	 * Costruttore. 
	 *
	 * 
	 */   
	public IntegrationManagerMessage(){
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
	public void setMessage(OpenSPCoop2Message m) throws Exception{
		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
		m.writeTo(byteBuffer,true);
		this.message = byteBuffer.toByteArray();
		byteBuffer.close();
	}
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
	 * header info
	 * 
	 * @return the protocolHeaderInfo
	 */
	public ProtocolHeaderInfo getProtocolHeaderInfo() {
		return this.protocolHeaderInfo;
	}
	/**
	 * header info
	 * 
	 * @param headerInfo the protocolHeaderInfo to set
	 */
	public void setProtocolHeaderInfo(ProtocolHeaderInfo headerInfo) {
		this.protocolHeaderInfo = headerInfo;
	}
}
