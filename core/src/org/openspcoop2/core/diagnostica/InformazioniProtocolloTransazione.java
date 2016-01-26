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
package org.openspcoop2.core.diagnostica;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.diagnostica.constants.TipoPdD;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for informazioni-protocollo-transazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="informazioni-protocollo-transazione">
 * 		&lt;sequence>
 * 			&lt;element name="tipoPdD" type="{http://www.openspcoop2.org/core/diagnostica}TipoPdD" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="identificativo-richiesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="dominio" type="{http://www.openspcoop2.org/core/diagnostica}dominio-transazione" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="nome-porta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="fruitore" type="{http://www.openspcoop2.org/core/diagnostica}soggetto" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="erogatore" type="{http://www.openspcoop2.org/core/diagnostica}soggetto" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="servizio" type="{http://www.openspcoop2.org/core/diagnostica}servizio" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="identificativo-correlazione-richiesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="identificativo-correlazione-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="protocollo" type="{http://www.openspcoop2.org/core/diagnostica}protocollo" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="servizio-applicativo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "informazioni-protocollo-transazione", 
  propOrder = {
  	"tipoPdD",
  	"identificativoRichiesta",
  	"dominio",
  	"oraRegistrazione",
  	"nomePorta",
  	"fruitore",
  	"erogatore",
  	"servizio",
  	"azione",
  	"identificativoCorrelazioneRichiesta",
  	"identificativoCorrelazioneRisposta",
  	"protocollo",
  	"servizioApplicativo"
  }
)

@XmlRootElement(name = "informazioni-protocollo-transazione")

public class InformazioniProtocolloTransazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public InformazioniProtocolloTransazione() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public void set_value_tipoPdD(String value) {
    this.tipoPdD = (TipoPdD) TipoPdD.toEnumConstantFromString(value);
  }

  public String get_value_tipoPdD() {
    if(this.tipoPdD == null){
    	return null;
    }else{
    	return this.tipoPdD.toString();
    }
  }

  public org.openspcoop2.core.diagnostica.constants.TipoPdD getTipoPdD() {
    return this.tipoPdD;
  }

  public void setTipoPdD(org.openspcoop2.core.diagnostica.constants.TipoPdD tipoPdD) {
    this.tipoPdD = tipoPdD;
  }

  public java.lang.String getIdentificativoRichiesta() {
    return this.identificativoRichiesta;
  }

  public void setIdentificativoRichiesta(java.lang.String identificativoRichiesta) {
    this.identificativoRichiesta = identificativoRichiesta;
  }

  public DominioTransazione getDominio() {
    return this.dominio;
  }

  public void setDominio(DominioTransazione dominio) {
    this.dominio = dominio;
  }

  public java.util.Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(java.util.Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  public java.lang.String getNomePorta() {
    return this.nomePorta;
  }

  public void setNomePorta(java.lang.String nomePorta) {
    this.nomePorta = nomePorta;
  }

  public Soggetto getFruitore() {
    return this.fruitore;
  }

  public void setFruitore(Soggetto fruitore) {
    this.fruitore = fruitore;
  }

  public Soggetto getErogatore() {
    return this.erogatore;
  }

  public void setErogatore(Soggetto erogatore) {
    this.erogatore = erogatore;
  }

  public Servizio getServizio() {
    return this.servizio;
  }

  public void setServizio(Servizio servizio) {
    this.servizio = servizio;
  }

  public java.lang.String getAzione() {
    return this.azione;
  }

  public void setAzione(java.lang.String azione) {
    this.azione = azione;
  }

  public java.lang.String getIdentificativoCorrelazioneRichiesta() {
    return this.identificativoCorrelazioneRichiesta;
  }

  public void setIdentificativoCorrelazioneRichiesta(java.lang.String identificativoCorrelazioneRichiesta) {
    this.identificativoCorrelazioneRichiesta = identificativoCorrelazioneRichiesta;
  }

  public java.lang.String getIdentificativoCorrelazioneRisposta() {
    return this.identificativoCorrelazioneRisposta;
  }

  public void setIdentificativoCorrelazioneRisposta(java.lang.String identificativoCorrelazioneRisposta) {
    this.identificativoCorrelazioneRisposta = identificativoCorrelazioneRisposta;
  }

  public boolean isCorrelazioneApplicativaAndMatch() {
    return this.correlazioneApplicativaAndMatch;
  }

  public boolean getCorrelazioneApplicativaAndMatch() {
    return this.correlazioneApplicativaAndMatch;
  }

  public void setCorrelazioneApplicativaAndMatch(boolean correlazioneApplicativaAndMatch) {
    this.correlazioneApplicativaAndMatch = correlazioneApplicativaAndMatch;
  }

  public Protocollo getProtocollo() {
    return this.protocollo;
  }

  public void setProtocollo(Protocollo protocollo) {
    this.protocollo = protocollo;
  }

  public void addServizioApplicativo(java.lang.String servizioApplicativo) {
    this.servizioApplicativo.add(servizioApplicativo);
  }

  public java.lang.String getServizioApplicativo(int index) {
    return this.servizioApplicativo.get( index );
  }

  public java.lang.String removeServizioApplicativo(int index) {
    return this.servizioApplicativo.remove( index );
  }

  public List<java.lang.String> getServizioApplicativoList() {
    return this.servizioApplicativo;
  }

  public void setServizioApplicativoList(List<java.lang.String> servizioApplicativo) {
    this.servizioApplicativo=servizioApplicativo;
  }

  public int sizeServizioApplicativoList() {
    return this.servizioApplicativo.size();
  }

  public java.lang.String getFiltroServizioApplicativo() {
    return this.filtroServizioApplicativo;
  }

  public void setFiltroServizioApplicativo(java.lang.String filtroServizioApplicativo) {
    this.filtroServizioApplicativo = filtroServizioApplicativo;
  }

  public FiltroInformazioniDiagnostici getFiltroInformazioniDiagnostici() {
    return this.filtroInformazioniDiagnostici;
  }

  public void setFiltroInformazioniDiagnostici(FiltroInformazioniDiagnostici filtroInformazioniDiagnostici) {
    this.filtroInformazioniDiagnostici = filtroInformazioniDiagnostici;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.diagnostica.model.InformazioniProtocolloTransazioneModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione.modelStaticInstance==null){
  			org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione.modelStaticInstance = new org.openspcoop2.core.diagnostica.model.InformazioniProtocolloTransazioneModel();
	  }
  }
  public static org.openspcoop2.core.diagnostica.model.InformazioniProtocolloTransazioneModel model(){
	  if(org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione.modelStaticInstance;
  }


  @XmlTransient
  protected java.lang.String _value_tipoPdD;

  @XmlElement(name="tipoPdD",required=true,nillable=false)
  protected TipoPdD tipoPdD;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo-richiesta",required=true,nillable=false)
  protected java.lang.String identificativoRichiesta;

  @XmlElement(name="dominio",required=true,nillable=false)
  protected DominioTransazione dominio;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="ora-registrazione",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date oraRegistrazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-porta",required=false,nillable=false)
  protected java.lang.String nomePorta;

  @XmlElement(name="fruitore",required=true,nillable=false)
  protected Soggetto fruitore;

  @XmlElement(name="erogatore",required=true,nillable=false)
  protected Soggetto erogatore;

  @XmlElement(name="servizio",required=true,nillable=false)
  protected Servizio servizio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="azione",required=false,nillable=false)
  protected java.lang.String azione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo-correlazione-richiesta",required=false,nillable=false)
  protected java.lang.String identificativoCorrelazioneRichiesta;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo-correlazione-risposta",required=false,nillable=false)
  protected java.lang.String identificativoCorrelazioneRisposta;

  @javax.xml.bind.annotation.XmlTransient
  protected boolean correlazioneApplicativaAndMatch = false;

  @XmlElement(name="protocollo",required=true,nillable=false)
  protected Protocollo protocollo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="servizio-applicativo",required=true,nillable=false)
  protected List<java.lang.String> servizioApplicativo = new ArrayList<java.lang.String>();

  /**
   * @deprecated Use method getServizioApplicativoList
   * @return List<java.lang.String>
  */
  @Deprecated
  public List<java.lang.String> getServizioApplicativo() {
  	return this.servizioApplicativo;
  }

  /**
   * @deprecated Use method setServizioApplicativoList
   * @param servizioApplicativo List<java.lang.String>
  */
  @Deprecated
  public void setServizioApplicativo(List<java.lang.String> servizioApplicativo) {
  	this.servizioApplicativo=servizioApplicativo;
  }

  /**
   * @deprecated Use method sizeServizioApplicativoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeServizioApplicativo() {
  	return this.servizioApplicativo.size();
  }

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String filtroServizioApplicativo;

  @javax.xml.bind.annotation.XmlTransient
  protected FiltroInformazioniDiagnostici filtroInformazioniDiagnostici;

}
