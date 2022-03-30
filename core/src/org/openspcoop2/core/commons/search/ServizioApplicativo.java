/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.core.commons.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 * &lt;complexType name="servizio-applicativo"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tipologia_fruizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tipologia_erogazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="as_client" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="id-soggetto" type="{http://www.openspcoop2.org/core/commons/search}id-soggetto" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="servizio-applicativo-ruolo" type="{http://www.openspcoop2.org/core/commons/search}servizio-applicativo-ruolo" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
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
  	"nome",
  	"tipologiaFruizione",
  	"tipologiaErogazione",
  	"tipo",
  	"asClient",
  	"idSoggetto",
  	"servizioApplicativoRuolo"
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
		return Long.valueOf(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=Long.valueOf(-1);
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
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

  public java.lang.String getTipo() {
    return this.tipo;
  }

  public void setTipo(java.lang.String tipo) {
    this.tipo = tipo;
  }

  public java.lang.Integer getAsClient() {
    return this.asClient;
  }

  public void setAsClient(java.lang.Integer asClient) {
    this.asClient = asClient;
  }

  public IdSoggetto getIdSoggetto() {
    return this.idSoggetto;
  }

  public void setIdSoggetto(IdSoggetto idSoggetto) {
    this.idSoggetto = idSoggetto;
  }

  public void addServizioApplicativoRuolo(ServizioApplicativoRuolo servizioApplicativoRuolo) {
    this.servizioApplicativoRuolo.add(servizioApplicativoRuolo);
  }

  public ServizioApplicativoRuolo getServizioApplicativoRuolo(int index) {
    return this.servizioApplicativoRuolo.get( index );
  }

  public ServizioApplicativoRuolo removeServizioApplicativoRuolo(int index) {
    return this.servizioApplicativoRuolo.remove( index );
  }

  public List<ServizioApplicativoRuolo> getServizioApplicativoRuoloList() {
    return this.servizioApplicativoRuolo;
  }

  public void setServizioApplicativoRuoloList(List<ServizioApplicativoRuolo> servizioApplicativoRuolo) {
    this.servizioApplicativoRuolo=servizioApplicativoRuolo;
  }

  public int sizeServizioApplicativoRuoloList() {
    return this.servizioApplicativoRuolo.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.commons.search.model.ServizioApplicativoModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.commons.search.ServizioApplicativo.modelStaticInstance==null){
  			org.openspcoop2.core.commons.search.ServizioApplicativo.modelStaticInstance = new org.openspcoop2.core.commons.search.model.ServizioApplicativoModel();
	  }
  }
  public static org.openspcoop2.core.commons.search.model.ServizioApplicativoModel model(){
	  if(org.openspcoop2.core.commons.search.ServizioApplicativo.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.commons.search.ServizioApplicativo.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=true,nillable=false)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipologia_fruizione",required=true,nillable=false)
  protected java.lang.String tipologiaFruizione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipologia_erogazione",required=true,nillable=false)
  protected java.lang.String tipologiaErogazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo",required=false,nillable=false)
  protected java.lang.String tipo;

  @javax.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="as_client",required=false,nillable=false)
  protected java.lang.Integer asClient;

  @XmlElement(name="id-soggetto",required=true,nillable=false)
  protected IdSoggetto idSoggetto;

  @XmlElement(name="servizio-applicativo-ruolo",required=true,nillable=false)
  protected List<ServizioApplicativoRuolo> servizioApplicativoRuolo = new ArrayList<ServizioApplicativoRuolo>();

  /**
   * @deprecated Use method getServizioApplicativoRuoloList
   * @return List&lt;ServizioApplicativoRuolo&gt;
  */
  @Deprecated
  public List<ServizioApplicativoRuolo> getServizioApplicativoRuolo() {
  	return this.servizioApplicativoRuolo;
  }

  /**
   * @deprecated Use method setServizioApplicativoRuoloList
   * @param servizioApplicativoRuolo List&lt;ServizioApplicativoRuolo&gt;
  */
  @Deprecated
  public void setServizioApplicativoRuolo(List<ServizioApplicativoRuolo> servizioApplicativoRuolo) {
  	this.servizioApplicativoRuolo=servizioApplicativoRuolo;
  }

  /**
   * @deprecated Use method sizeServizioApplicativoRuoloList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeServizioApplicativoRuolo() {
  	return this.servizioApplicativoRuolo.size();
  }

}
