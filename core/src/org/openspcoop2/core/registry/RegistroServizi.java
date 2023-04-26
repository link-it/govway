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
package org.openspcoop2.core.registry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for registro-servizi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="registro-servizi"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="accordo-cooperazione" type="{http://www.openspcoop2.org/core/registry}accordo-cooperazione" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="accordo-servizio-parte-comune" type="{http://www.openspcoop2.org/core/registry}accordo-servizio-parte-comune" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="porta-dominio" type="{http://www.openspcoop2.org/core/registry}porta-dominio" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="ruolo" type="{http://www.openspcoop2.org/core/registry}ruolo" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="scope" type="{http://www.openspcoop2.org/core/registry}scope" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="gruppo" type="{http://www.openspcoop2.org/core/registry}gruppo" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="soggetto" type="{http://www.openspcoop2.org/core/registry}soggetto" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="connettore" type="{http://www.openspcoop2.org/core/registry}connettore" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "registro-servizi", 
  propOrder = {
  	"accordoCooperazione",
  	"accordoServizioParteComune",
  	"portaDominio",
  	"ruolo",
  	"scope",
  	"gruppo",
  	"soggetto",
  	"connettore"
  }
)

@XmlRootElement(name = "registro-servizi")

public class RegistroServizi extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public RegistroServizi() {
    super();
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

  public void addRuolo(Ruolo ruolo) {
    this.ruolo.add(ruolo);
  }

  public Ruolo getRuolo(int index) {
    return this.ruolo.get( index );
  }

  public Ruolo removeRuolo(int index) {
    return this.ruolo.remove( index );
  }

  public List<Ruolo> getRuoloList() {
    return this.ruolo;
  }

  public void setRuoloList(List<Ruolo> ruolo) {
    this.ruolo=ruolo;
  }

  public int sizeRuoloList() {
    return this.ruolo.size();
  }

  public void addScope(Scope scope) {
    this.scope.add(scope);
  }

  public Scope getScope(int index) {
    return this.scope.get( index );
  }

  public Scope removeScope(int index) {
    return this.scope.remove( index );
  }

  public List<Scope> getScopeList() {
    return this.scope;
  }

  public void setScopeList(List<Scope> scope) {
    this.scope=scope;
  }

  public int sizeScopeList() {
    return this.scope.size();
  }

  public void addGruppo(Gruppo gruppo) {
    this.gruppo.add(gruppo);
  }

  public Gruppo getGruppo(int index) {
    return this.gruppo.get( index );
  }

  public Gruppo removeGruppo(int index) {
    return this.gruppo.remove( index );
  }

  public List<Gruppo> getGruppoList() {
    return this.gruppo;
  }

  public void setGruppoList(List<Gruppo> gruppo) {
    this.gruppo=gruppo;
  }

  public int sizeGruppoList() {
    return this.gruppo.size();
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



  @XmlElement(name="accordo-cooperazione",required=true,nillable=false)
  private List<AccordoCooperazione> accordoCooperazione = new ArrayList<>();

  @XmlElement(name="accordo-servizio-parte-comune",required=true,nillable=false)
  private List<AccordoServizioParteComune> accordoServizioParteComune = new ArrayList<>();

  @XmlElement(name="porta-dominio",required=true,nillable=false)
  private List<PortaDominio> portaDominio = new ArrayList<>();

  @XmlElement(name="ruolo",required=true,nillable=false)
  private List<Ruolo> ruolo = new ArrayList<>();

  @XmlElement(name="scope",required=true,nillable=false)
  private List<Scope> scope = new ArrayList<>();

  @XmlElement(name="gruppo",required=true,nillable=false)
  private List<Gruppo> gruppo = new ArrayList<>();

  @XmlElement(name="soggetto",required=true,nillable=false)
  private List<Soggetto> soggetto = new ArrayList<>();

  @XmlElement(name="connettore",required=true,nillable=false)
  private List<Connettore> connettore = new ArrayList<>();

}
