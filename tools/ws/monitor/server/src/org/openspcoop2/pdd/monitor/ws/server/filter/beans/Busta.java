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
package org.openspcoop2.pdd.monitor.ws.server.filter.beans;

/**
 * <p>Java class for Busta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="busta">
 *     &lt;sequence>
 *         &lt;element name="attesa-riscontro" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" default="new Boolean("false")" />
 *         &lt;element name="mittente" type="{http://www.openspcoop2.org/pdd/monitor/management}busta-soggetto" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="destinatario" type="{http://www.openspcoop2.org/pdd/monitor/management}busta-soggetto" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="servizio" type="{http://www.openspcoop2.org/pdd/monitor/management}busta-servizio" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="profilo-collaborazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="collaborazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="riferimento-messaggio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.pdd.monitor.ws.server.filter.beans.BustaSoggetto;
import org.openspcoop2.pdd.monitor.ws.server.filter.beans.BustaServizio;

/**     
 * Busta
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "busta", namespace="http://www.openspcoop2.org/pdd/monitor/management", propOrder = {
    "attesaRiscontro",
    "mittente",
    "destinatario",
    "servizio",
    "azione",
    "profiloCollaborazione",
    "collaborazione",
    "riferimentoMessaggio"
})
@javax.xml.bind.annotation.XmlRootElement(name = "busta")
public class Busta extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="attesa-riscontro",required=false,nillable=false,defaultValue="false")
	private Boolean attesaRiscontro = new Boolean("false");
	
	public void setAttesaRiscontro(Boolean attesaRiscontro){
		this.attesaRiscontro = attesaRiscontro;
	}
	
	public Boolean getAttesaRiscontro(){
		return this.attesaRiscontro;
	}
	
	
	@XmlElement(name="mittente",required=false,nillable=false)
	private BustaSoggetto mittente;
	
	public void setMittente(BustaSoggetto mittente){
		this.mittente = mittente;
	}
	
	public BustaSoggetto getMittente(){
		return this.mittente;
	}
	
	
	@XmlElement(name="destinatario",required=false,nillable=false)
	private BustaSoggetto destinatario;
	
	public void setDestinatario(BustaSoggetto destinatario){
		this.destinatario = destinatario;
	}
	
	public BustaSoggetto getDestinatario(){
		return this.destinatario;
	}
	
	
	@XmlElement(name="servizio",required=false,nillable=false)
	private BustaServizio servizio;
	
	public void setServizio(BustaServizio servizio){
		this.servizio = servizio;
	}
	
	public BustaServizio getServizio(){
		return this.servizio;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="azione",required=false,nillable=false)
	private String azione;
	
	public void setAzione(String azione){
		this.azione = azione;
	}
	
	public String getAzione(){
		return this.azione;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="profilo-collaborazione",required=false,nillable=false)
	private String profiloCollaborazione;
	
	public void setProfiloCollaborazione(String profiloCollaborazione){
		this.profiloCollaborazione = profiloCollaborazione;
	}
	
	public String getProfiloCollaborazione(){
		return this.profiloCollaborazione;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="collaborazione",required=false,nillable=false)
	private String collaborazione;
	
	public void setCollaborazione(String collaborazione){
		this.collaborazione = collaborazione;
	}
	
	public String getCollaborazione(){
		return this.collaborazione;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="riferimento-messaggio",required=false,nillable=false)
	private String riferimentoMessaggio;
	
	public void setRiferimentoMessaggio(String riferimentoMessaggio){
		this.riferimentoMessaggio = riferimentoMessaggio;
	}
	
	public String getRiferimentoMessaggio(){
		return this.riferimentoMessaggio;
	}
	
	
	
	
}