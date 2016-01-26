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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for servizio-applicativo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="servizio-applicativo">
 * 		&lt;sequence>
 * 			&lt;element name="ruolo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="invocazione-porta" type="{http://www.openspcoop2.org/core/config}invocazione-porta" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="invocazione-servizio" type="{http://www.openspcoop2.org/core/config}invocazione-servizio" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="risposta-asincrona" type="{http://www.openspcoop2.org/core/config}risposta-asincrona" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="id-soggetto" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/>
 * 		&lt;attribute name="tipo-soggetto-proprietario" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="nome-soggetto-proprietario" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="tipologia-fruizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="tipologia-erogazione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="optional"/>
 * 		&lt;attribute name="old-nome-soggetto-proprietario-for-update" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="old-tipo-soggetto-proprietario-for-update" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
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
  	"ruolo",
  	"invocazionePorta",
  	"invocazioneServizio",
  	"rispostaAsincrona"
  }
)

@XmlRootElement(name = "servizio-applicativo")

public class ServizioApplicativo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ServizioApplicativo() {
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

  public String getOldNomeForUpdate() {
    if(this.oldNomeForUpdate!=null && ("".equals(this.oldNomeForUpdate)==false)){
		return this.oldNomeForUpdate.trim();
	}else{
		return null;
	}

  }

  public void setOldNomeForUpdate(String oldNomeForUpdate) {
    this.oldNomeForUpdate=oldNomeForUpdate;
  }

  public String getOldNomeSoggettoProprietarioForUpdate() {
    if(this.oldNomeSoggettoProprietarioForUpdate!=null && ("".equals(this.oldNomeSoggettoProprietarioForUpdate)==false)){
		return this.oldNomeSoggettoProprietarioForUpdate.trim();
	}else{
		return null;
	}

  }

  public void setOldNomeSoggettoProprietarioForUpdate(String oldNomeSoggettoProprietarioForUpdate) {
    this.oldNomeSoggettoProprietarioForUpdate=oldNomeSoggettoProprietarioForUpdate;
  }

  public String getOldTipoSoggettoProprietarioForUpdate() {
    if(this.oldTipoSoggettoProprietarioForUpdate!=null && ("".equals(this.oldTipoSoggettoProprietarioForUpdate)==false)){
		return this.oldTipoSoggettoProprietarioForUpdate.trim();
	}else{
		return null;
	}

  }

  public void setOldTipoSoggettoProprietarioForUpdate(String oldTipoSoggettoProprietarioForUpdate) {
    this.oldTipoSoggettoProprietarioForUpdate=oldTipoSoggettoProprietarioForUpdate;
  }

  public void addRuolo(java.lang.String ruolo) {
    this.ruolo.add(ruolo);
  }

  public java.lang.String getRuolo(int index) {
    return this.ruolo.get( index );
  }

  public java.lang.String removeRuolo(int index) {
    return this.ruolo.remove( index );
  }

  public List<java.lang.String> getRuoloList() {
    return this.ruolo;
  }

  public void setRuoloList(List<java.lang.String> ruolo) {
    this.ruolo=ruolo;
  }

  public int sizeRuoloList() {
    return this.ruolo.size();
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

  @XmlTransient
  private Long id;

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


  @XmlTransient
  private String oldNomeForUpdate;

  @javax.xml.bind.annotation.XmlTransient
  protected String oldNomeSoggettoProprietarioForUpdate;

  @javax.xml.bind.annotation.XmlTransient
  protected String oldTipoSoggettoProprietarioForUpdate;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="ruolo",required=true,nillable=false)
  protected List<java.lang.String> ruolo = new ArrayList<java.lang.String>();

  /**
   * @deprecated Use method getRuoloList
   * @return List<java.lang.String>
  */
  @Deprecated
  public List<java.lang.String> getRuolo() {
  	return this.ruolo;
  }

  /**
   * @deprecated Use method setRuoloList
   * @param ruolo List<java.lang.String>
  */
  @Deprecated
  public void setRuolo(List<java.lang.String> ruolo) {
  	this.ruolo=ruolo;
  }

  /**
   * @deprecated Use method sizeRuoloList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeRuolo() {
  	return this.ruolo.size();
  }

  @XmlElement(name="invocazione-porta",required=false,nillable=false)
  protected InvocazionePorta invocazionePorta;

  @XmlElement(name="invocazione-servizio",required=false,nillable=false)
  protected InvocazioneServizio invocazioneServizio;

  @XmlElement(name="risposta-asincrona",required=false,nillable=false)
  protected RispostaAsincrona rispostaAsincrona;

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
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="ora-registrazione",required=false)
  protected java.util.Date oraRegistrazione;

}
