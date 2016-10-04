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
package org.openspcoop2.core.diagnostica.ws.server.filter.beans;

/**
 * <p>Java class for FiltroInformazioneProtocollo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="filtro-informazione-protocollo">
 *     &lt;sequence>
 *         &lt;element name="tipo-porta" type="{http://www.openspcoop2.org/core/diagnostica}TipoPdD" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="nome-porta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="ricerca-solo-messaggi-correlati-informazioni-protocollo" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" default="new Boolean("false")" />
 *         &lt;element name="fruitore" type="{http://www.openspcoop2.org/core/diagnostica/management}soggetto" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="erogatore" type="{http://www.openspcoop2.org/core/diagnostica/management}soggetto" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="servizio" type="{http://www.openspcoop2.org/core/diagnostica/management}servizio" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="identificativo-correlazione-richiesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="identificativo-correlazione-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="correlazione-applicativa-and-match" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" default="new Boolean("false")" />
 *         &lt;element name="servizio-applicativo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.diagnostica.ws.server.filter.beans.Servizio;
import org.openspcoop2.core.diagnostica.ws.server.filter.beans.Soggetto;
import org.openspcoop2.core.diagnostica.constants.TipoPdD;

/**     
 * FiltroInformazioneProtocollo
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "filtro-informazione-protocollo", namespace="http://www.openspcoop2.org/core/diagnostica/management", propOrder = {
    "tipoPorta",
    "nomePorta",
    "ricercaSoloMessaggiCorrelatiInformazioniProtocollo",
    "fruitore",
    "erogatore",
    "servizio",
    "azione",
    "identificativoCorrelazioneRichiesta",
    "identificativoCorrelazioneRisposta",
    "correlazioneApplicativaAndMatch",
    "servizioApplicativo"
})
@javax.xml.bind.annotation.XmlRootElement(name = "filtro-informazione-protocollo")
public class FiltroInformazioneProtocollo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="tipo-porta",required=false,nillable=false)
	private TipoPdD tipoPorta;
	
	public void setTipoPorta(TipoPdD tipoPorta){
		this.tipoPorta = tipoPorta;
	}
	
	public TipoPdD getTipoPorta(){
		return this.tipoPorta;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-porta",required=false,nillable=false)
	private String nomePorta;
	
	public void setNomePorta(String nomePorta){
		this.nomePorta = nomePorta;
	}
	
	public String getNomePorta(){
		return this.nomePorta;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="ricerca-solo-messaggi-correlati-informazioni-protocollo",required=false,nillable=false,defaultValue="false")
	private Boolean ricercaSoloMessaggiCorrelatiInformazioniProtocollo = new Boolean("false");
	
	public void setRicercaSoloMessaggiCorrelatiInformazioniProtocollo(Boolean ricercaSoloMessaggiCorrelatiInformazioniProtocollo){
		this.ricercaSoloMessaggiCorrelatiInformazioniProtocollo = ricercaSoloMessaggiCorrelatiInformazioniProtocollo;
	}
	
	public Boolean getRicercaSoloMessaggiCorrelatiInformazioniProtocollo(){
		return this.ricercaSoloMessaggiCorrelatiInformazioniProtocollo;
	}
	
	
	@XmlElement(name="fruitore",required=false,nillable=false)
	private Soggetto fruitore;
	
	public void setFruitore(Soggetto fruitore){
		this.fruitore = fruitore;
	}
	
	public Soggetto getFruitore(){
		return this.fruitore;
	}
	
	
	@XmlElement(name="erogatore",required=false,nillable=false)
	private Soggetto erogatore;
	
	public void setErogatore(Soggetto erogatore){
		this.erogatore = erogatore;
	}
	
	public Soggetto getErogatore(){
		return this.erogatore;
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
  @XmlElement(name="identificativo-correlazione-richiesta",required=false,nillable=false)
	private String identificativoCorrelazioneRichiesta;
	
	public void setIdentificativoCorrelazioneRichiesta(String identificativoCorrelazioneRichiesta){
		this.identificativoCorrelazioneRichiesta = identificativoCorrelazioneRichiesta;
	}
	
	public String getIdentificativoCorrelazioneRichiesta(){
		return this.identificativoCorrelazioneRichiesta;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo-correlazione-risposta",required=false,nillable=false)
	private String identificativoCorrelazioneRisposta;
	
	public void setIdentificativoCorrelazioneRisposta(String identificativoCorrelazioneRisposta){
		this.identificativoCorrelazioneRisposta = identificativoCorrelazioneRisposta;
	}
	
	public String getIdentificativoCorrelazioneRisposta(){
		return this.identificativoCorrelazioneRisposta;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="correlazione-applicativa-and-match",required=false,nillable=false,defaultValue="false")
	private Boolean correlazioneApplicativaAndMatch = new Boolean("false");
	
	public void setCorrelazioneApplicativaAndMatch(Boolean correlazioneApplicativaAndMatch){
		this.correlazioneApplicativaAndMatch = correlazioneApplicativaAndMatch;
	}
	
	public Boolean getCorrelazioneApplicativaAndMatch(){
		return this.correlazioneApplicativaAndMatch;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="servizio-applicativo",required=false,nillable=false)
	private String servizioApplicativo;
	
	public void setServizioApplicativo(String servizioApplicativo){
		this.servizioApplicativo = servizioApplicativo;
	}
	
	public String getServizioApplicativo(){
		return this.servizioApplicativo;
	}
	
	
	
	
}