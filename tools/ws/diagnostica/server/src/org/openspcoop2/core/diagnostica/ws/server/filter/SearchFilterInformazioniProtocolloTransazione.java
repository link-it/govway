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
package org.openspcoop2.core.diagnostica.ws.server.filter;

/**
 * <p>Java class for SearchFilterInformazioniProtocolloTransazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="search-filter-informazioni-protocollo-transazione">
 *     &lt;sequence>
 *         &lt;element name="tipoPdD" type="{http://www.openspcoop2.org/core/diagnostica}TipoPdD" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="identificativo-richiesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="dominio" type="{http://www.openspcoop2.org/core/diagnostica/management}dominio-transazione" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="ora-registrazione-min" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="ora-registrazione-max" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="nome-porta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="fruitore" type="{http://www.openspcoop2.org/core/diagnostica/management}soggetto" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="erogatore" type="{http://www.openspcoop2.org/core/diagnostica/management}soggetto" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="servizio" type="{http://www.openspcoop2.org/core/diagnostica/management}servizio" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="identificativo-correlazione-richiesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="identificativo-correlazione-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="correlazione-applicativa-and-match" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" default="new Boolean("false")" />
 *         &lt;element name="protocollo" type="{http://www.openspcoop2.org/core/diagnostica/management}protocollo" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="filtro-servizio-applicativo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="filtro-informazioni-diagnostici" type="{http://www.openspcoop2.org/core/diagnostica/management}filtro-informazioni-diagnostici" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="limit" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="offset" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1" />
 *         &lt;element name="descOrder" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0" maxOccurs="1" default="new Boolean("false")" />
 *     &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
 
import java.io.Serializable;
 
import javax.xml.bind.annotation.XmlElement;
import org.openspcoop2.core.diagnostica.ws.server.filter.beans.Protocollo;
import org.openspcoop2.core.diagnostica.ws.server.filter.beans.Servizio;
import org.openspcoop2.core.diagnostica.ws.server.filter.beans.Soggetto;
import java.util.Date;
import org.openspcoop2.core.diagnostica.ws.server.filter.beans.FiltroInformazioniDiagnostici;
import org.openspcoop2.core.diagnostica.constants.TipoPdD;
import org.openspcoop2.core.diagnostica.ws.server.filter.beans.DominioTransazione;

/**     
 * SearchFilterInformazioniProtocolloTransazione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "search-filter-informazioni-protocollo-transazione", namespace="http://www.openspcoop2.org/core/diagnostica/management", propOrder = {
    "tipoPdD",
    "identificativoRichiesta",
    "dominio",
    "oraRegistrazioneMin",
    "oraRegistrazioneMax",
    "nomePorta",
    "fruitore",
    "erogatore",
    "servizio",
    "azione",
    "identificativoCorrelazioneRichiesta",
    "identificativoCorrelazioneRisposta",
    "correlazioneApplicativaAndMatch",
    "protocollo",
    "filtroServizioApplicativo",
    "filtroInformazioniDiagnostici",
    "limit",
    "offset",
    "descOrder"
})
@javax.xml.bind.annotation.XmlRootElement(name = "search-filter-informazioni-protocollo-transazione")
public class SearchFilterInformazioniProtocolloTransazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
	
	private static final long serialVersionUID = -1L;
	
	@XmlElement(name="tipoPdD",required=false,nillable=false)
	private TipoPdD tipoPdD;
	
	public void setTipoPdD(TipoPdD tipoPdD){
		this.tipoPdD = tipoPdD;
	}
	
	public TipoPdD getTipoPdD(){
		return this.tipoPdD;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo-richiesta",required=false,nillable=false)
	private String identificativoRichiesta;
	
	public void setIdentificativoRichiesta(String identificativoRichiesta){
		this.identificativoRichiesta = identificativoRichiesta;
	}
	
	public String getIdentificativoRichiesta(){
		return this.identificativoRichiesta;
	}
	
	
	@XmlElement(name="dominio",required=false,nillable=false)
	private DominioTransazione dominio;
	
	public void setDominio(DominioTransazione dominio){
		this.dominio = dominio;
	}
	
	public DominioTransazione getDominio(){
		return this.dominio;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="ora-registrazione-min",required=false,nillable=false)
	private Date oraRegistrazioneMin;
	
	public void setOraRegistrazioneMin(Date oraRegistrazioneMin){
		this.oraRegistrazioneMin = oraRegistrazioneMin;
	}
	
	public Date getOraRegistrazioneMin(){
		return this.oraRegistrazioneMin;
	}
	
	
	@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="ora-registrazione-max",required=false,nillable=false)
	private Date oraRegistrazioneMax;
	
	public void setOraRegistrazioneMax(Date oraRegistrazioneMax){
		this.oraRegistrazioneMax = oraRegistrazioneMax;
	}
	
	public Date getOraRegistrazioneMax(){
		return this.oraRegistrazioneMax;
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
	
	
	@XmlElement(name="protocollo",required=false,nillable=false)
	private Protocollo protocollo;
	
	public void setProtocollo(Protocollo protocollo){
		this.protocollo = protocollo;
	}
	
	public Protocollo getProtocollo(){
		return this.protocollo;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro-servizio-applicativo",required=false,nillable=false)
	private String filtroServizioApplicativo;
	
	public void setFiltroServizioApplicativo(String filtroServizioApplicativo){
		this.filtroServizioApplicativo = filtroServizioApplicativo;
	}
	
	public String getFiltroServizioApplicativo(){
		return this.filtroServizioApplicativo;
	}
	
	
	@XmlElement(name="filtro-informazioni-diagnostici",required=false,nillable=false)
	private FiltroInformazioniDiagnostici filtroInformazioniDiagnostici;
	
	public void setFiltroInformazioniDiagnostici(FiltroInformazioniDiagnostici filtroInformazioniDiagnostici){
		this.filtroInformazioniDiagnostici = filtroInformazioniDiagnostici;
	}
	
	public FiltroInformazioniDiagnostici getFiltroInformazioniDiagnostici(){
		return this.filtroInformazioniDiagnostici;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="limit",required=false,nillable=false)
	private Integer limit;
	
	public void setLimit(Integer limit){
		this.limit = limit;
	}
	
	public Integer getLimit(){
		return this.limit;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="offset",required=false,nillable=false)
	private Integer offset;
	
	public void setOffset(Integer offset){
		this.offset = offset;
	}
	
	public Integer getOffset(){
		return this.offset;
	}
	
	
	@javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlElement(name="descOrder",required=false,nillable=false,defaultValue="false")
	private Boolean descOrder = new Boolean("false");
	
	public void setDescOrder(Boolean descOrder){
		this.descOrder = descOrder;
	}
	
	public Boolean getDescOrder(){
		return this.descOrder;
	}
	
	
	
	
}