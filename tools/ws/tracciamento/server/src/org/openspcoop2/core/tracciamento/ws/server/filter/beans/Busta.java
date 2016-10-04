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
package org.openspcoop2.core.tracciamento.ws.server.filter.beans;

/**
 * <p>Java class for Busta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="busta">
 *     &lt;sequence>
 *         &lt;element name="mittente" type="{http://www.openspcoop2.org/core/tracciamento/management}soggetto" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="destinatario" type="{http://www.openspcoop2.org/core/tracciamento/management}soggetto" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="profilo-collaborazione" type="{http://www.openspcoop2.org/core/tracciamento/management}profilo-collaborazione" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="servizio" type="{http://www.openspcoop2.org/core/tracciamento/management}servizio" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="identificativo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="riferimento-messaggio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="servizio-applicativo-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="servizio-applicativo-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="protocollo" type="{http://www.openspcoop2.org/core/tracciamento/management}protocollo" minOccurs="0" maxOccurs="1" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.tracciamento.ws.server.filter.beans.Soggetto;
import org.openspcoop2.core.tracciamento.ws.server.filter.beans.Servizio;
import org.openspcoop2.core.tracciamento.ws.server.filter.beans.ProfiloCollaborazione;
import org.openspcoop2.core.tracciamento.ws.server.filter.beans.Protocollo;

/**     
 * Busta
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "busta", namespace="http://www.openspcoop2.org/core/tracciamento/management", propOrder = {
    "mittente",
    "destinatario",
    "profiloCollaborazione",
    "servizio",
    "azione",
    "identificativo",
    "riferimentoMessaggio",
    "servizioApplicativoFruitore",
    "servizioApplicativoErogatore",
    "protocollo"
})
@javax.xml.bind.annotation.XmlRootElement(name = "busta")
public class Busta extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="mittente",required=false,nillable=false)
	private Soggetto mittente;
	
	public void setMittente(Soggetto mittente){
		this.mittente = mittente;
	}
	
	public Soggetto getMittente(){
		return this.mittente;
	}
	
	
	@XmlElement(name="destinatario",required=false,nillable=false)
	private Soggetto destinatario;
	
	public void setDestinatario(Soggetto destinatario){
		this.destinatario = destinatario;
	}
	
	public Soggetto getDestinatario(){
		return this.destinatario;
	}
	
	
	@XmlElement(name="profilo-collaborazione",required=false,nillable=false)
	private ProfiloCollaborazione profiloCollaborazione;
	
	public void setProfiloCollaborazione(ProfiloCollaborazione profiloCollaborazione){
		this.profiloCollaborazione = profiloCollaborazione;
	}
	
	public ProfiloCollaborazione getProfiloCollaborazione(){
		return this.profiloCollaborazione;
	}
	
	
	@XmlElement(name="servizio",required=false,nillable=false)
	private Servizio servizio;
	
	public void setServizio(Servizio servizio){
		this.servizio = servizio;
	}
	
	public Servizio getServizio(){
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
  @XmlElement(name="identificativo",required=false,nillable=false)
	private String identificativo;
	
	public void setIdentificativo(String identificativo){
		this.identificativo = identificativo;
	}
	
	public String getIdentificativo(){
		return this.identificativo;
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
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="servizio-applicativo-fruitore",required=false,nillable=false)
	private String servizioApplicativoFruitore;
	
	public void setServizioApplicativoFruitore(String servizioApplicativoFruitore){
		this.servizioApplicativoFruitore = servizioApplicativoFruitore;
	}
	
	public String getServizioApplicativoFruitore(){
		return this.servizioApplicativoFruitore;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="servizio-applicativo-erogatore",required=false,nillable=false)
	private String servizioApplicativoErogatore;
	
	public void setServizioApplicativoErogatore(String servizioApplicativoErogatore){
		this.servizioApplicativoErogatore = servizioApplicativoErogatore;
	}
	
	public String getServizioApplicativoErogatore(){
		return this.servizioApplicativoErogatore;
	}
	
	
	@XmlElement(name="protocollo",required=false,nillable=false)
	private Protocollo protocollo;
	
	public void setProtocollo(Protocollo protocollo){
		this.protocollo = protocollo;
	}
	
	public Protocollo getProtocollo(){
		return this.protocollo;
	}
	
	
	
	
}