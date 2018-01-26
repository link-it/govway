/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for DatiBeniServiziType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatiBeniServiziType">
 * 		&lt;sequence>
 * 			&lt;element name="DettaglioLinee" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DettaglioLineeType" minOccurs="1" maxOccurs="unbounded"/>
 * 			&lt;element name="DatiRiepilogo" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DatiRiepilogoType" minOccurs="1" maxOccurs="unbounded"/>
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
@XmlType(name = "DatiBeniServiziType", 
  propOrder = {
  	"dettaglioLinee",
  	"datiRiepilogo"
  }
)

@XmlRootElement(name = "DatiBeniServiziType")

public class DatiBeniServiziType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public DatiBeniServiziType() {
  }

  public void addDettaglioLinee(DettaglioLineeType dettaglioLinee) {
    this.dettaglioLinee.add(dettaglioLinee);
  }

  public DettaglioLineeType getDettaglioLinee(int index) {
    return this.dettaglioLinee.get( index );
  }

  public DettaglioLineeType removeDettaglioLinee(int index) {
    return this.dettaglioLinee.remove( index );
  }

  public List<DettaglioLineeType> getDettaglioLineeList() {
    return this.dettaglioLinee;
  }

  public void setDettaglioLineeList(List<DettaglioLineeType> dettaglioLinee) {
    this.dettaglioLinee=dettaglioLinee;
  }

  public int sizeDettaglioLineeList() {
    return this.dettaglioLinee.size();
  }

  public void addDatiRiepilogo(DatiRiepilogoType datiRiepilogo) {
    this.datiRiepilogo.add(datiRiepilogo);
  }

  public DatiRiepilogoType getDatiRiepilogo(int index) {
    return this.datiRiepilogo.get( index );
  }

  public DatiRiepilogoType removeDatiRiepilogo(int index) {
    return this.datiRiepilogo.remove( index );
  }

  public List<DatiRiepilogoType> getDatiRiepilogoList() {
    return this.datiRiepilogo;
  }

  public void setDatiRiepilogoList(List<DatiRiepilogoType> datiRiepilogo) {
    this.datiRiepilogo=datiRiepilogo;
  }

  public int sizeDatiRiepilogoList() {
    return this.datiRiepilogo.size();
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="DettaglioLinee",required=true,nillable=false)
  protected List<DettaglioLineeType> dettaglioLinee = new ArrayList<DettaglioLineeType>();

  /**
   * @deprecated Use method getDettaglioLineeList
   * @return List<DettaglioLineeType>
  */
  @Deprecated
  public List<DettaglioLineeType> getDettaglioLinee() {
  	return this.dettaglioLinee;
  }

  /**
   * @deprecated Use method setDettaglioLineeList
   * @param dettaglioLinee List<DettaglioLineeType>
  */
  @Deprecated
  public void setDettaglioLinee(List<DettaglioLineeType> dettaglioLinee) {
  	this.dettaglioLinee=dettaglioLinee;
  }

  /**
   * @deprecated Use method sizeDettaglioLineeList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeDettaglioLinee() {
  	return this.dettaglioLinee.size();
  }

  @XmlElement(name="DatiRiepilogo",required=true,nillable=false)
  protected List<DatiRiepilogoType> datiRiepilogo = new ArrayList<DatiRiepilogoType>();

  /**
   * @deprecated Use method getDatiRiepilogoList
   * @return List<DatiRiepilogoType>
  */
  @Deprecated
  public List<DatiRiepilogoType> getDatiRiepilogo() {
  	return this.datiRiepilogo;
  }

  /**
   * @deprecated Use method setDatiRiepilogoList
   * @param datiRiepilogo List<DatiRiepilogoType>
  */
  @Deprecated
  public void setDatiRiepilogo(List<DatiRiepilogoType> datiRiepilogo) {
  	this.datiRiepilogo=datiRiepilogo;
  }

  /**
   * @deprecated Use method sizeDatiRiepilogoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeDatiRiepilogo() {
  	return this.datiRiepilogo.size();
  }

}
