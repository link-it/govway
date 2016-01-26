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
package org.openspcoop2.core.registry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for registro-servizi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="registro-servizi">
 * 		&lt;sequence>
 * 			&lt;element name="accordo-cooperazione" type="{http://www.openspcoop2.org/core/registry}accordo-cooperazione" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="accordo-servizio-parte-comune" type="{http://www.openspcoop2.org/core/registry}accordo-servizio-parte-comune" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="porta-dominio" type="{http://www.openspcoop2.org/core/registry}porta-dominio" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="soggetto" type="{http://www.openspcoop2.org/core/registry}soggetto" minOccurs="0" maxOccurs="unbounded"/>
 * 			&lt;element name="connettore" type="{http://www.openspcoop2.org/core/registry}connettore" minOccurs="0" maxOccurs="unbounded"/>
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
@XmlType(name = "registro-servizi", 
  propOrder = {
  	"accordoCooperazione",
  	"accordoServizioParteComune",
  	"portaDominio",
  	"soggetto",
  	"connettore"
  }
)

@XmlRootElement(name = "registro-servizi")

public class RegistroServizi extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public RegistroServizi() {
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

  public void addAccordoCooperazione(AccordoCooperazione accordoCooperazione) {
    this.accordoCooperazione.add(accordoCooperazione);
  }

  public AccordoCooperazione getAccordoCooperazione(int index) {
    return this.accordoCooperazione.get( index );
  }

  public AccordoCooperazione removeAccordoCooperazione(int index) {
    return this.accordoCooperazione.remove( index );
  }

  public List<AccordoCooperazione> getAccordoCooperazioneList() {
    return this.accordoCooperazione;
  }

  public void setAccordoCooperazioneList(List<AccordoCooperazione> accordoCooperazione) {
    this.accordoCooperazione=accordoCooperazione;
  }

  public int sizeAccordoCooperazioneList() {
    return this.accordoCooperazione.size();
  }

  public void addAccordoServizioParteComune(AccordoServizioParteComune accordoServizioParteComune) {
    this.accordoServizioParteComune.add(accordoServizioParteComune);
  }

  public AccordoServizioParteComune getAccordoServizioParteComune(int index) {
    return this.accordoServizioParteComune.get( index );
  }

  public AccordoServizioParteComune removeAccordoServizioParteComune(int index) {
    return this.accordoServizioParteComune.remove( index );
  }

  public List<AccordoServizioParteComune> getAccordoServizioParteComuneList() {
    return this.accordoServizioParteComune;
  }

  public void setAccordoServizioParteComuneList(List<AccordoServizioParteComune> accordoServizioParteComune) {
    this.accordoServizioParteComune=accordoServizioParteComune;
  }

  public int sizeAccordoServizioParteComuneList() {
    return this.accordoServizioParteComune.size();
  }

  public void addPortaDominio(PortaDominio portaDominio) {
    this.portaDominio.add(portaDominio);
  }

  public PortaDominio getPortaDominio(int index) {
    return this.portaDominio.get( index );
  }

  public PortaDominio removePortaDominio(int index) {
    return this.portaDominio.remove( index );
  }

  public List<PortaDominio> getPortaDominioList() {
    return this.portaDominio;
  }

  public void setPortaDominioList(List<PortaDominio> portaDominio) {
    this.portaDominio=portaDominio;
  }

  public int sizePortaDominioList() {
    return this.portaDominio.size();
  }

  public void addSoggetto(Soggetto soggetto) {
    this.soggetto.add(soggetto);
  }

  public Soggetto getSoggetto(int index) {
    return this.soggetto.get( index );
  }

  public Soggetto removeSoggetto(int index) {
    return this.soggetto.remove( index );
  }

  public List<Soggetto> getSoggettoList() {
    return this.soggetto;
  }

  public void setSoggettoList(List<Soggetto> soggetto) {
    this.soggetto=soggetto;
  }

  public int sizeSoggettoList() {
    return this.soggetto.size();
  }

  public void addConnettore(Connettore connettore) {
    this.connettore.add(connettore);
  }

  public Connettore getConnettore(int index) {
    return this.connettore.get( index );
  }

  public Connettore removeConnettore(int index) {
    return this.connettore.remove( index );
  }

  public List<Connettore> getConnettoreList() {
    return this.connettore;
  }

  public void setConnettoreList(List<Connettore> connettore) {
    this.connettore=connettore;
  }

  public int sizeConnettoreList() {
    return this.connettore.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="accordo-cooperazione",required=true,nillable=false)
  protected List<AccordoCooperazione> accordoCooperazione = new ArrayList<AccordoCooperazione>();

  /**
   * @deprecated Use method getAccordoCooperazioneList
   * @return List<AccordoCooperazione>
  */
  @Deprecated
  public List<AccordoCooperazione> getAccordoCooperazione() {
  	return this.accordoCooperazione;
  }

  /**
   * @deprecated Use method setAccordoCooperazioneList
   * @param accordoCooperazione List<AccordoCooperazione>
  */
  @Deprecated
  public void setAccordoCooperazione(List<AccordoCooperazione> accordoCooperazione) {
  	this.accordoCooperazione=accordoCooperazione;
  }

  /**
   * @deprecated Use method sizeAccordoCooperazioneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeAccordoCooperazione() {
  	return this.accordoCooperazione.size();
  }

  @XmlElement(name="accordo-servizio-parte-comune",required=true,nillable=false)
  protected List<AccordoServizioParteComune> accordoServizioParteComune = new ArrayList<AccordoServizioParteComune>();

  /**
   * @deprecated Use method getAccordoServizioParteComuneList
   * @return List<AccordoServizioParteComune>
  */
  @Deprecated
  public List<AccordoServizioParteComune> getAccordoServizioParteComune() {
  	return this.accordoServizioParteComune;
  }

  /**
   * @deprecated Use method setAccordoServizioParteComuneList
   * @param accordoServizioParteComune List<AccordoServizioParteComune>
  */
  @Deprecated
  public void setAccordoServizioParteComune(List<AccordoServizioParteComune> accordoServizioParteComune) {
  	this.accordoServizioParteComune=accordoServizioParteComune;
  }

  /**
   * @deprecated Use method sizeAccordoServizioParteComuneList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeAccordoServizioParteComune() {
  	return this.accordoServizioParteComune.size();
  }

  @XmlElement(name="porta-dominio",required=true,nillable=false)
  protected List<PortaDominio> portaDominio = new ArrayList<PortaDominio>();

  /**
   * @deprecated Use method getPortaDominioList
   * @return List<PortaDominio>
  */
  @Deprecated
  public List<PortaDominio> getPortaDominio() {
  	return this.portaDominio;
  }

  /**
   * @deprecated Use method setPortaDominioList
   * @param portaDominio List<PortaDominio>
  */
  @Deprecated
  public void setPortaDominio(List<PortaDominio> portaDominio) {
  	this.portaDominio=portaDominio;
  }

  /**
   * @deprecated Use method sizePortaDominioList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizePortaDominio() {
  	return this.portaDominio.size();
  }

  @XmlElement(name="soggetto",required=true,nillable=false)
  protected List<Soggetto> soggetto = new ArrayList<Soggetto>();

  /**
   * @deprecated Use method getSoggettoList
   * @return List<Soggetto>
  */
  @Deprecated
  public List<Soggetto> getSoggetto() {
  	return this.soggetto;
  }

  /**
   * @deprecated Use method setSoggettoList
   * @param soggetto List<Soggetto>
  */
  @Deprecated
  public void setSoggetto(List<Soggetto> soggetto) {
  	this.soggetto=soggetto;
  }

  /**
   * @deprecated Use method sizeSoggettoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeSoggetto() {
  	return this.soggetto.size();
  }

  @XmlElement(name="connettore",required=true,nillable=false)
  protected List<Connettore> connettore = new ArrayList<Connettore>();

  /**
   * @deprecated Use method getConnettoreList
   * @return List<Connettore>
  */
  @Deprecated
  public List<Connettore> getConnettore() {
  	return this.connettore;
  }

  /**
   * @deprecated Use method setConnettoreList
   * @param connettore List<Connettore>
  */
  @Deprecated
  public void setConnettore(List<Connettore> connettore) {
  	this.connettore=connettore;
  }

  /**
   * @deprecated Use method sizeConnettoreList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeConnettore() {
  	return this.connettore.size();
  }

}
