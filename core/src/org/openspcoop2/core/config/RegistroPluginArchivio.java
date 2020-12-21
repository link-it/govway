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
import org.openspcoop2.core.config.constants.PluginSorgenteArchivio;
import java.io.Serializable;


/** <p>Java class for registro-plugin-archivio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="registro-plugin-archivio"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="contenuto" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="dir" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="data" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="required"/&gt;
 * 		&lt;attribute name="sorgente" type="{http://www.openspcoop2.org/core/config}PluginSorgenteArchivio" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "registro-plugin-archivio", 
  propOrder = {
  	"contenuto",
  	"url",
  	"dir"
  }
)

@XmlRootElement(name = "registro-plugin-archivio")

public class RegistroPluginArchivio extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public RegistroPluginArchivio() {
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

  public String getNomePlugin() {
    if(this.nomePlugin!=null && ("".equals(this.nomePlugin)==false)){
		return this.nomePlugin.trim();
	}else{
		return null;
	}

  }

  public void setNomePlugin(String nomePlugin) {
    this.nomePlugin=nomePlugin;
  }

  public byte[] getContenuto() {
    return this.contenuto;
  }

  public void setContenuto(byte[] contenuto) {
    this.contenuto = contenuto;
  }

  public java.lang.String getUrl() {
    return this.url;
  }

  public void setUrl(java.lang.String url) {
    this.url = url;
  }

  public java.lang.String getDir() {
    return this.dir;
  }

  public void setDir(java.lang.String dir) {
    this.dir = dir;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.util.Date getData() {
    return this.data;
  }

  public void setData(java.util.Date data) {
    this.data = data;
  }

  public void set_value_sorgente(String value) {
    this.sorgente = (PluginSorgenteArchivio) PluginSorgenteArchivio.toEnumConstantFromString(value);
  }

  public String get_value_sorgente() {
    if(this.sorgente == null){
    	return null;
    }else{
    	return this.sorgente.toString();
    }
  }

  public org.openspcoop2.core.config.constants.PluginSorgenteArchivio getSorgente() {
    return this.sorgente;
  }

  public void setSorgente(org.openspcoop2.core.config.constants.PluginSorgenteArchivio sorgente) {
    this.sorgente = sorgente;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlTransient
  protected String nomePlugin;

  @javax.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlElement(name="contenuto",required=false,nillable=false)
  protected byte[] contenuto;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="url",required=false,nillable=false)
  protected java.lang.String url;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="dir",required=false,nillable=false)
  protected java.lang.String dir;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="data",required=true)
  protected java.util.Date data;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_sorgente;

  @XmlAttribute(name="sorgente",required=true)
  protected PluginSorgenteArchivio sorgente;

}
