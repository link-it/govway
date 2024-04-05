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
package org.openspcoop2.core.plugins;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for configurazione-filtro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-filtro"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-mittente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="nome-mittente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="idporta-mittente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-destinatario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="nome-destinatario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="idporta-destinatario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo-servizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="nome-servizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="versione-servizio" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0" maxOccurs="1" default="1"/&gt;
 * 			&lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "configurazione-filtro", 
  propOrder = {
  	"nome",
  	"descrizione",
  	"tipoMittente",
  	"nomeMittente",
  	"idportaMittente",
  	"tipoDestinatario",
  	"nomeDestinatario",
  	"idportaDestinatario",
  	"tipoServizio",
  	"nomeServizio",
  	"versioneServizio",
  	"azione"
  }
)

@XmlRootElement(name = "configurazione-filtro")

public class ConfigurazioneFiltro extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ConfigurazioneFiltro() {
    super();
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

  public java.lang.String getTipoMittente() {
    return this.tipoMittente;
  }

  public void setTipoMittente(java.lang.String tipoMittente) {
    this.tipoMittente = tipoMittente;
  }

  public java.lang.String getNomeMittente() {
    return this.nomeMittente;
  }

  public void setNomeMittente(java.lang.String nomeMittente) {
    this.nomeMittente = nomeMittente;
  }

  public java.lang.String getIdportaMittente() {
    return this.idportaMittente;
  }

  public void setIdportaMittente(java.lang.String idportaMittente) {
    this.idportaMittente = idportaMittente;
  }

  public java.lang.String getTipoDestinatario() {
    return this.tipoDestinatario;
  }

  public void setTipoDestinatario(java.lang.String tipoDestinatario) {
    this.tipoDestinatario = tipoDestinatario;
  }

  public java.lang.String getNomeDestinatario() {
    return this.nomeDestinatario;
  }

  public void setNomeDestinatario(java.lang.String nomeDestinatario) {
    this.nomeDestinatario = nomeDestinatario;
  }

  public java.lang.String getIdportaDestinatario() {
    return this.idportaDestinatario;
  }

  public void setIdportaDestinatario(java.lang.String idportaDestinatario) {
    this.idportaDestinatario = idportaDestinatario;
  }

  public java.lang.String getTipoServizio() {
    return this.tipoServizio;
  }

  public void setTipoServizio(java.lang.String tipoServizio) {
    this.tipoServizio = tipoServizio;
  }

  public java.lang.String getNomeServizio() {
    return this.nomeServizio;
  }

  public void setNomeServizio(java.lang.String nomeServizio) {
    this.nomeServizio = nomeServizio;
  }

  public java.lang.Integer getVersioneServizio() {
    return this.versioneServizio;
  }

  public void setVersioneServizio(java.lang.Integer versioneServizio) {
    this.versioneServizio = versioneServizio;
  }

  public java.lang.String getAzione() {
    return this.azione;
  }

  public void setAzione(java.lang.String azione) {
    this.azione = azione;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.plugins.model.ConfigurazioneFiltroModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.plugins.ConfigurazioneFiltro.modelStaticInstance==null){
  			org.openspcoop2.core.plugins.ConfigurazioneFiltro.modelStaticInstance = new org.openspcoop2.core.plugins.model.ConfigurazioneFiltroModel();
	  }
  }
  public static org.openspcoop2.core.plugins.model.ConfigurazioneFiltroModel model(){
	  if(org.openspcoop2.core.plugins.ConfigurazioneFiltro.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.plugins.ConfigurazioneFiltro.modelStaticInstance;
  }


  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=true,nillable=false)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="descrizione",required=false,nillable=false)
  protected java.lang.String descrizione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-mittente",required=false,nillable=false)
  protected java.lang.String tipoMittente;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-mittente",required=false,nillable=false)
  protected java.lang.String nomeMittente;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="idporta-mittente",required=false,nillable=false)
  protected java.lang.String idportaMittente;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-destinatario",required=false,nillable=false)
  protected java.lang.String tipoDestinatario;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-destinatario",required=false,nillable=false)
  protected java.lang.String nomeDestinatario;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="idporta-destinatario",required=false,nillable=false)
  protected java.lang.String idportaDestinatario;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo-servizio",required=false,nillable=false)
  protected java.lang.String tipoServizio;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome-servizio",required=false,nillable=false)
  protected java.lang.String nomeServizio;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="unsignedInt")
  @XmlElement(name="versione-servizio",required=false,nillable=false,defaultValue="1")
  protected java.lang.Integer versioneServizio = java.lang.Integer.valueOf("1");

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="azione",required=false,nillable=false)
  protected java.lang.String azione;

}
