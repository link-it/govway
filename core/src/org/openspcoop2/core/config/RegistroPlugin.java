/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for registro-plugin complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="registro-plugin"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="archivio" type="{http://www.openspcoop2.org/core/config}registro-plugin-archivio" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="compatibilita" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="posizione" type="{http://www.w3.org/2001/XMLSchema}int" use="required"/&gt;
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="data" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "registro-plugin", 
  propOrder = {
  	"archivio",
  	"compatibilita"
  }
)

@XmlRootElement(name = "registro-plugin")

public class RegistroPlugin extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public RegistroPlugin() {
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

  public String getOldNome() {
    if(this.oldNome!=null && ("".equals(this.oldNome)==false)){
		return this.oldNome.trim();
	}else{
		return null;
	}

  }

  public void setOldNome(String oldNome) {
    this.oldNome=oldNome;
  }

  public void addArchivio(RegistroPluginArchivio archivio) {
    this.archivio.add(archivio);
  }

  public RegistroPluginArchivio getArchivio(int index) {
    return this.archivio.get( index );
  }

  public RegistroPluginArchivio removeArchivio(int index) {
    return this.archivio.remove( index );
  }

  public List<RegistroPluginArchivio> getArchivioList() {
    return this.archivio;
  }

  public void setArchivioList(List<RegistroPluginArchivio> archivio) {
    this.archivio=archivio;
  }

  public int sizeArchivioList() {
    return this.archivio.size();
  }

  public void addCompatibilita(java.lang.String compatibilita) {
    this.compatibilita.add(compatibilita);
  }

  public java.lang.String getCompatibilita(int index) {
    return this.compatibilita.get( index );
  }

  public java.lang.String removeCompatibilita(int index) {
    return this.compatibilita.remove( index );
  }

  public List<java.lang.String> getCompatibilitaList() {
    return this.compatibilita;
  }

  public void setCompatibilitaList(List<java.lang.String> compatibilita) {
    this.compatibilita=compatibilita;
  }

  public int sizeCompatibilitaList() {
    return this.compatibilita.size();
  }

  public void set_value_stato(String value) {
    this.stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_stato() {
    if(this.stato == null){
    	return null;
    }else{
    	return this.stato.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getStato() {
    return this.stato;
  }

  public void setStato(org.openspcoop2.core.config.constants.StatoFunzionalita stato) {
    this.stato = stato;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public int getPosizione() {
    return this.posizione;
  }

  public void setPosizione(int posizione) {
    this.posizione = posizione;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public java.util.Date getData() {
    return this.data;
  }

  public void setData(java.util.Date data) {
    this.data = data;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlTransient
  protected String oldNome;

  @XmlElement(name="archivio",required=true,nillable=false)
  protected List<RegistroPluginArchivio> archivio = new ArrayList<RegistroPluginArchivio>();

  /**
   * @deprecated Use method getArchivioList
   * @return List&lt;RegistroPluginArchivio&gt;
  */
  @Deprecated
  public List<RegistroPluginArchivio> getArchivio() {
  	return this.archivio;
  }

  /**
   * @deprecated Use method setArchivioList
   * @param archivio List&lt;RegistroPluginArchivio&gt;
  */
  @Deprecated
  public void setArchivio(List<RegistroPluginArchivio> archivio) {
  	this.archivio=archivio;
  }

  /**
   * @deprecated Use method sizeArchivioList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeArchivio() {
  	return this.archivio.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="compatibilita",required=true,nillable=false)
  protected List<java.lang.String> compatibilita = new ArrayList<java.lang.String>();

  /**
   * @deprecated Use method getCompatibilitaList
   * @return List&lt;java.lang.String&gt;
  */
  @Deprecated
  public List<java.lang.String> getCompatibilita() {
  	return this.compatibilita;
  }

  /**
   * @deprecated Use method setCompatibilitaList
   * @param compatibilita List&lt;java.lang.String&gt;
  */
  @Deprecated
  public void setCompatibilita(List<java.lang.String> compatibilita) {
  	this.compatibilita=compatibilita;
  }

  /**
   * @deprecated Use method sizeCompatibilitaList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeCompatibilita() {
  	return this.compatibilita.size();
  }

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_stato;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalita stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="int")
  @XmlAttribute(name="posizione",required=true)
  protected int posizione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="data",required=true)
  protected java.util.Date data;

}
