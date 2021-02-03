/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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


/** <p>Java class for soggetto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="soggetto"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="nome-soggetto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-soggetto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="server" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="identificativo-porta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="soggetto-ruolo" type="{http://www.openspcoop2.org/core/commons/search}soggetto-ruolo" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "soggetto", 
  propOrder = {
  	"nomeSoggetto",
  	"tipoSoggetto",
  	"server",
  	"identificativoPorta",
  	"soggettoRuolo"
  }
)

@XmlRootElement(name = "soggetto")

public class Soggetto extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Soggetto() {
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

  public java.lang.String getNomeSoggetto() {
    return this.nomeSoggetto;
  }

  public void setNomeSoggetto(java.lang.String nomeSoggetto) {
    this.nomeSoggetto = nomeSoggetto;
  }

  public java.lang.String getTipoSoggetto() {
    return this.tipoSoggetto;
  }

  public void setTipoSoggetto(java.lang.String tipoSoggetto) {
    this.tipoSoggetto = tipoSoggetto;
  }

  public java.lang.String getServer() {
    return this.server;
  }

  public void setServer(java.lang.String server) {
    this.server = server;
  }

  public java.lang.String getIdentificativoPorta() {
    return this.identificativoPorta;
  }

  public void setIdentificativoPorta(java.lang.String identificativoPorta) {
    this.identificativoPorta = identificativoPorta;
  }

  public void addSoggettoRuolo(SoggettoRuolo soggettoRuolo) {
    this.soggettoRuolo.add(soggettoRuolo);
  }

  public SoggettoRuolo getSoggettoRuolo(int index) {
    return this.soggettoRuolo.get( index );
  }

  public SoggettoRuolo removeSoggettoRuolo(int index) {
    return this.soggettoRuolo.remove( index );
  }

  public List<SoggettoRuolo> getSoggettoRuoloList() {
    return this.soggettoRuolo;
  }

  public void setSoggettoRuoloList(List<SoggettoRuolo> soggettoRuolo) {
    this.soggettoRuolo=soggettoRuolo;
  }

  public int sizeSoggettoRuoloList() {
    return this.soggettoRuolo.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.commons.search.model.SoggettoModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.commons.search.Soggetto.modelStaticInstance==null){
  			org.openspcoop2.core.commons.search.Soggetto.modelStaticInstance = new org.openspcoop2.core.commons.search.model.SoggettoModel();
	  }
  }
  public static org.openspcoop2.core.commons.search.model.SoggettoModel model(){
	  if(org.openspcoop2.core.commons.search.Soggetto.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.commons.search.Soggetto.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-soggetto",required=true,nillable=false)
  protected java.lang.String nomeSoggetto;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-soggetto",required=true,nillable=false)
  protected java.lang.String tipoSoggetto;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="server",required=false,nillable=false)
  protected java.lang.String server;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo-porta",required=false,nillable=false)
  protected java.lang.String identificativoPorta;

  @XmlElement(name="soggetto-ruolo",required=true,nillable=false)
  protected List<SoggettoRuolo> soggettoRuolo = new ArrayList<SoggettoRuolo>();

  /**
   * @deprecated Use method getSoggettoRuoloList
   * @return List&lt;SoggettoRuolo&gt;
  */
  @Deprecated
  public List<SoggettoRuolo> getSoggettoRuolo() {
  	return this.soggettoRuolo;
  }

  /**
   * @deprecated Use method setSoggettoRuoloList
   * @param soggettoRuolo List&lt;SoggettoRuolo&gt;
  */
  @Deprecated
  public void setSoggettoRuolo(List<SoggettoRuolo> soggettoRuolo) {
  	this.soggettoRuolo=soggettoRuolo;
  }

  /**
   * @deprecated Use method sizeSoggettoRuoloList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeSoggettoRuolo() {
  	return this.soggettoRuolo.size();
  }

}
