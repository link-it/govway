/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.id.IDServizioApplicativo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for servizio-applicativo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="servizio-applicativo"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="invocazione-porta" type="{http://www.openspcoop2.org/core/config}invocazione-porta" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="invocazione-servizio" type="{http://www.openspcoop2.org/core/config}invocazione-servizio" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="risposta-asincrona" type="{http://www.openspcoop2.org/core/config}risposta-asincrona" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="proprieta" type="{http://www.openspcoop2.org/core/config}proprieta" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="protocol-property" type="{http://www.openspcoop2.org/core/config}protocol-property" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="proprieta-oggetto" type="{http://www.openspcoop2.org/core/config}proprieta-oggetto" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="id-soggetto" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/&gt;
 * 		&lt;attribute name="tipo-soggetto-proprietario" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="nome-soggetto-proprietario" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="tipologia-fruizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="tipologia-erogazione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="use-as-client" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "servizio-applicativo", 
  propOrder = {
  	"invocazionePorta",
  	"invocazioneServizio",
  	"rispostaAsincrona",
  	"proprieta",
  	"protocolProperty",
  	"proprietaOggetto"
  }
)

@XmlRootElement(name = "servizio-applicativo")

public class ServizioApplicativo extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ServizioApplicativo() {
    super();
  }

  public IDServizioApplicativo getOldIDServizioApplicativoForUpdate() {
    return this.oldIDServizioApplicativoForUpdate;
  }

  public void setOldIDServizioApplicativoForUpdate(IDServizioApplicativo oldIDServizioApplicativoForUpdate) {
    this.oldIDServizioApplicativoForUpdate=oldIDServizioApplicativoForUpdate;
  }

  public InvocazionePorta getInvocazionePorta() {
    return this.invocazionePorta;
  }

  public void setInvocazionePorta(InvocazionePorta invocazionePorta) {
    this.invocazionePorta = invocazionePorta;
  }

  public InvocazioneServizio getInvocazioneServizio() {
    return this.invocazioneServizio;
  }

  public void setInvocazioneServizio(InvocazioneServizio invocazioneServizio) {
    this.invocazioneServizio = invocazioneServizio;
  }

  public RispostaAsincrona getRispostaAsincrona() {
    return this.rispostaAsincrona;
  }

  public void setRispostaAsincrona(RispostaAsincrona rispostaAsincrona) {
    this.rispostaAsincrona = rispostaAsincrona;
  }

  public void addProprieta(Proprieta proprieta) {
    this.proprieta.add(proprieta);
  }

  public Proprieta getProprieta(int index) {
    return this.proprieta.get( index );
  }

  public Proprieta removeProprieta(int index) {
    return this.proprieta.remove( index );
  }

  public List<Proprieta> getProprietaList() {
    return this.proprieta;
  }

  public void setProprietaList(List<Proprieta> proprieta) {
    this.proprieta=proprieta;
  }

  public int sizeProprietaList() {
    return this.proprieta.size();
  }

  public void addProtocolProperty(ProtocolProperty protocolProperty) {
    this.protocolProperty.add(protocolProperty);
  }

  public ProtocolProperty getProtocolProperty(int index) {
    return this.protocolProperty.get( index );
  }

  public ProtocolProperty removeProtocolProperty(int index) {
    return this.protocolProperty.remove( index );
  }

  public List<ProtocolProperty> getProtocolPropertyList() {
    return this.protocolProperty;
  }

  public void setProtocolPropertyList(List<ProtocolProperty> protocolProperty) {
    this.protocolProperty=protocolProperty;
  }

  public int sizeProtocolPropertyList() {
    return this.protocolProperty.size();
  }

  public ProprietaOggetto getProprietaOggetto() {
    return this.proprietaOggetto;
  }

  public void setProprietaOggetto(ProprietaOggetto proprietaOggetto) {
    this.proprietaOggetto = proprietaOggetto;
  }

  public java.lang.Long getIdSoggetto() {
    return this.idSoggetto;
  }

  public void setIdSoggetto(java.lang.Long idSoggetto) {
    this.idSoggetto = idSoggetto;
  }

  public java.lang.String getTipoSoggettoProprietario() {
    return this.tipoSoggettoProprietario;
  }

  public void setTipoSoggettoProprietario(java.lang.String tipoSoggettoProprietario) {
    this.tipoSoggettoProprietario = tipoSoggettoProprietario;
  }

  public java.lang.String getNomeSoggettoProprietario() {
    return this.nomeSoggettoProprietario;
  }

  public void setNomeSoggettoProprietario(java.lang.String nomeSoggettoProprietario) {
    this.nomeSoggettoProprietario = nomeSoggettoProprietario;
  }

  public java.lang.String getTipologiaFruizione() {
    return this.tipologiaFruizione;
  }

  public void setTipologiaFruizione(java.lang.String tipologiaFruizione) {
    this.tipologiaFruizione = tipologiaFruizione;
  }

  public java.lang.String getTipologiaErogazione() {
    return this.tipologiaErogazione;
  }

  public void setTipologiaErogazione(java.lang.String tipologiaErogazione) {
    this.tipologiaErogazione = tipologiaErogazione;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getTipo() {
    return this.tipo;
  }

  public void setTipo(java.lang.String tipo) {
    this.tipo = tipo;
  }

  public boolean isUseAsClient() {
    return this.useAsClient;
  }

  public boolean getUseAsClient() {
    return this.useAsClient;
  }

  public void setUseAsClient(boolean useAsClient) {
    this.useAsClient = useAsClient;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public java.util.Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(java.util.Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.config.model.ServizioApplicativoModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.config.ServizioApplicativo.modelStaticInstance==null){
  			org.openspcoop2.core.config.ServizioApplicativo.modelStaticInstance = new org.openspcoop2.core.config.model.ServizioApplicativoModel();
	  }
  }
  public static org.openspcoop2.core.config.model.ServizioApplicativoModel model(){
	  if(org.openspcoop2.core.config.ServizioApplicativo.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.config.ServizioApplicativo.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlTransient
  protected IDServizioApplicativo oldIDServizioApplicativoForUpdate;

  @XmlElement(name="invocazione-porta",required=false,nillable=false)
  protected InvocazionePorta invocazionePorta;

  @XmlElement(name="invocazione-servizio",required=false,nillable=false)
  protected InvocazioneServizio invocazioneServizio;

  @XmlElement(name="risposta-asincrona",required=false,nillable=false)
  protected RispostaAsincrona rispostaAsincrona;

  @XmlElement(name="proprieta",required=true,nillable=false)
  private List<Proprieta> proprieta = new ArrayList<>();

  /**
   * Use method getProprietaList
   * @return List&lt;Proprieta&gt;
  */
  public List<Proprieta> getProprieta() {
  	return this.getProprietaList();
  }

  /**
   * Use method setProprietaList
   * @param proprieta List&lt;Proprieta&gt;
  */
  public void setProprieta(List<Proprieta> proprieta) {
  	this.setProprietaList(proprieta);
  }

  /**
   * Use method sizeProprietaList
   * @return lunghezza della lista
  */
  public int sizeProprieta() {
  	return this.sizeProprietaList();
  }

  @XmlElement(name="protocol-property",required=true,nillable=false)
  private List<ProtocolProperty> protocolProperty = new ArrayList<>();

  /**
   * Use method getProtocolPropertyList
   * @return List&lt;ProtocolProperty&gt;
  */
  public List<ProtocolProperty> getProtocolProperty() {
  	return this.getProtocolPropertyList();
  }

  /**
   * Use method setProtocolPropertyList
   * @param protocolProperty List&lt;ProtocolProperty&gt;
  */
  public void setProtocolProperty(List<ProtocolProperty> protocolProperty) {
  	this.setProtocolPropertyList(protocolProperty);
  }

  /**
   * Use method sizeProtocolPropertyList
   * @return lunghezza della lista
  */
  public int sizeProtocolProperty() {
  	return this.sizeProtocolPropertyList();
  }

  @XmlElement(name="proprieta-oggetto",required=false,nillable=false)
  protected ProprietaOggetto proprietaOggetto;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.Long idSoggetto;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo-soggetto-proprietario",required=false)
  protected java.lang.String tipoSoggettoProprietario;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome-soggetto-proprietario",required=false)
  protected java.lang.String nomeSoggettoProprietario;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipologia-fruizione",required=false)
  protected java.lang.String tipologiaFruizione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipologia-erogazione",required=false)
  protected java.lang.String tipologiaErogazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo",required=false)
  protected java.lang.String tipo;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="use-as-client",required=false)
  protected boolean useAsClient = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="ora-registrazione",required=false)
  protected java.util.Date oraRegistrazione;

}
