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
package org.openspcoop2.core.registry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for servizio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="servizio">
 * 		&lt;sequence>
 * 			&lt;element name="connettore" type="{http://www.openspcoop2.org/core/registry}connettore" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="parametri-azione" type="{http://www.openspcoop2.org/core/registry}servizio-azione" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="tipo-soggetto-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="nome-soggetto-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="tipologia-servizio" type="{http://www.openspcoop2.org/core/registry}TipologiaServizio" use="optional" default="normale"/>
 * 		&lt;attribute name="old-nome-for-update" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="old-tipo-for-update" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="old-nome-soggetto-erogatore-for-update" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="old-tipo-soggetto-erogatore-for-update" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "servizio", 
  propOrder = {
  	"connettore",
  	"parametriAzione"
  }
)

@XmlRootElement(name = "servizio")

public class Servizio extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Servizio() {
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

  public String getOldTipoForUpdate() {
    if(this.oldTipoForUpdate!=null && ("".equals(this.oldTipoForUpdate)==false)){
		return this.oldTipoForUpdate.trim();
	}else{
		return null;
	}

  }

  public void setOldTipoForUpdate(String oldTipoForUpdate) {
    this.oldTipoForUpdate=oldTipoForUpdate;
  }

  public String getOldNomeSoggettoErogatoreForUpdate() {
    if(this.oldNomeSoggettoErogatoreForUpdate!=null && ("".equals(this.oldNomeSoggettoErogatoreForUpdate)==false)){
		return this.oldNomeSoggettoErogatoreForUpdate.trim();
	}else{
		return null;
	}

  }

  public void setOldNomeSoggettoErogatoreForUpdate(String oldNomeSoggettoErogatoreForUpdate) {
    this.oldNomeSoggettoErogatoreForUpdate=oldNomeSoggettoErogatoreForUpdate;
  }

  public String getOldTipoSoggettoErogatoreForUpdate() {
    if(this.oldTipoSoggettoErogatoreForUpdate!=null && ("".equals(this.oldTipoSoggettoErogatoreForUpdate)==false)){
		return this.oldTipoSoggettoErogatoreForUpdate.trim();
	}else{
		return null;
	}

  }

  public void setOldTipoSoggettoErogatoreForUpdate(String oldTipoSoggettoErogatoreForUpdate) {
    this.oldTipoSoggettoErogatoreForUpdate=oldTipoSoggettoErogatoreForUpdate;
  }

  public Connettore getConnettore() {
    return this.connettore;
  }

  public void setConnettore(Connettore connettore) {
    this.connettore = connettore;
  }

  public void addParametriAzione(ServizioAzione parametriAzione) {
    this.parametriAzione.add(parametriAzione);
  }

  public ServizioAzione getParametriAzione(int index) {
    return this.parametriAzione.get( index );
  }

  public ServizioAzione removeParametriAzione(int index) {
    return this.parametriAzione.remove( index );
  }

  public List<ServizioAzione> getParametriAzioneList() {
    return this.parametriAzione;
  }

  public void setParametriAzioneList(List<ServizioAzione> parametriAzione) {
    this.parametriAzione=parametriAzione;
  }

  public int sizeParametriAzioneList() {
    return this.parametriAzione.size();
  }

  public java.lang.String getTipoSoggettoErogatore() {
    return this.tipoSoggettoErogatore;
  }

  public void setTipoSoggettoErogatore(java.lang.String tipoSoggettoErogatore) {
    this.tipoSoggettoErogatore = tipoSoggettoErogatore;
  }

  public java.lang.String getNomeSoggettoErogatore() {
    return this.nomeSoggettoErogatore;
  }

  public void setNomeSoggettoErogatore(java.lang.String nomeSoggettoErogatore) {
    this.nomeSoggettoErogatore = nomeSoggettoErogatore;
  }

  public java.lang.String getTipo() {
    return this.tipo;
  }

  public void setTipo(java.lang.String tipo) {
    this.tipo = tipo;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public void set_value_tipologiaServizio(String value) {
    this.tipologiaServizio = (TipologiaServizio) TipologiaServizio.toEnumConstantFromString(value);
  }

  public String get_value_tipologiaServizio() {
    if(this.tipologiaServizio == null){
    	return null;
    }else{
    	return this.tipologiaServizio.toString();
    }
  }

  public org.openspcoop2.core.registry.constants.TipologiaServizio getTipologiaServizio() {
    return this.tipologiaServizio;
  }

  public void setTipologiaServizio(org.openspcoop2.core.registry.constants.TipologiaServizio tipologiaServizio) {
    this.tipologiaServizio = tipologiaServizio;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlTransient
  protected String oldNomeForUpdate;

  @javax.xml.bind.annotation.XmlTransient
  protected String oldTipoForUpdate;

  @javax.xml.bind.annotation.XmlTransient
  protected String oldNomeSoggettoErogatoreForUpdate;

  @javax.xml.bind.annotation.XmlTransient
  protected String oldTipoSoggettoErogatoreForUpdate;

  @XmlElement(name="connettore",required=false,nillable=false)
  protected Connettore connettore;

  @XmlElement(name="parametri-azione",required=true,nillable=false)
  protected List<ServizioAzione> parametriAzione = new ArrayList<ServizioAzione>();

  /**
   * @deprecated Use method getParametriAzioneList
   * @return List<ServizioAzione>
  */
  @Deprecated
  public List<ServizioAzione> getParametriAzione() {
  	return this.parametriAzione;
  }

  /**
   * @deprecated Use method setParametriAzioneList
   * @param parametriAzione List<ServizioAzione>
  */
  @Deprecated
  public void setParametriAzione(List<ServizioAzione> parametriAzione) {
  	this.parametriAzione=parametriAzione;
  }

  /**
   * @deprecated Use method sizeParametriAzioneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeParametriAzione() {
  	return this.parametriAzione.size();
  }

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo-soggetto-erogatore",required=false)
  protected java.lang.String tipoSoggettoErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome-soggetto-erogatore",required=false)
  protected java.lang.String nomeSoggettoErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo",required=true)
  protected java.lang.String tipo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @XmlTransient
  protected java.lang.String _value_tipologiaServizio;

  @XmlAttribute(name="tipologia-servizio",required=false)
  protected TipologiaServizio tipologiaServizio = (TipologiaServizio) TipologiaServizio.toEnumConstantFromString("normale");

}
