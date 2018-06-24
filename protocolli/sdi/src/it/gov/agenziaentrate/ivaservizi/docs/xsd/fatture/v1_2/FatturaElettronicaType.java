/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.FormatoTrasmissioneType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for FatturaElettronicaType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FatturaElettronicaType">
 * 		&lt;sequence>
 * 			&lt;element name="FatturaElettronicaHeader" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}FatturaElettronicaHeaderType" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="FatturaElettronicaBody" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}FatturaElettronicaBodyType" minOccurs="1" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="versione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}FormatoTrasmissioneType" use="required"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FatturaElettronicaType", 
  propOrder = {
  	"fatturaElettronicaHeader",
  	"fatturaElettronicaBody"
  }
)

@XmlRootElement(name = "FatturaElettronicaType")

public class FatturaElettronicaType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public FatturaElettronicaType() {
  }

  public FatturaElettronicaHeaderType getFatturaElettronicaHeader() {
    return this.fatturaElettronicaHeader;
  }

  public void setFatturaElettronicaHeader(FatturaElettronicaHeaderType fatturaElettronicaHeader) {
    this.fatturaElettronicaHeader = fatturaElettronicaHeader;
  }

  public void addFatturaElettronicaBody(FatturaElettronicaBodyType fatturaElettronicaBody) {
    this.fatturaElettronicaBody.add(fatturaElettronicaBody);
  }

  public FatturaElettronicaBodyType getFatturaElettronicaBody(int index) {
    return this.fatturaElettronicaBody.get( index );
  }

  public FatturaElettronicaBodyType removeFatturaElettronicaBody(int index) {
    return this.fatturaElettronicaBody.remove( index );
  }

  public List<FatturaElettronicaBodyType> getFatturaElettronicaBodyList() {
    return this.fatturaElettronicaBody;
  }

  public void setFatturaElettronicaBodyList(List<FatturaElettronicaBodyType> fatturaElettronicaBody) {
    this.fatturaElettronicaBody=fatturaElettronicaBody;
  }

  public int sizeFatturaElettronicaBodyList() {
    return this.fatturaElettronicaBody.size();
  }

  public void set_value_versione(String value) {
    this.versione = (FormatoTrasmissioneType) FormatoTrasmissioneType.toEnumConstantFromString(value);
  }

  public String get_value_versione() {
    if(this.versione == null){
    	return null;
    }else{
    	return this.versione.toString();
    }
  }

  public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.FormatoTrasmissioneType getVersione() {
    return this.versione;
  }

  public void setVersione(it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants.FormatoTrasmissioneType versione) {
    this.versione = versione;
  }

  private static final long serialVersionUID = 1L;

  private static it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.FatturaElettronicaTypeModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.FatturaElettronicaType.modelStaticInstance==null){
  			it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.FatturaElettronicaType.modelStaticInstance = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.FatturaElettronicaTypeModel();
	  }
  }
  public static it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.FatturaElettronicaTypeModel model(){
	  if(it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.FatturaElettronicaType.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.FatturaElettronicaType.modelStaticInstance;
  }


  @XmlElement(name="FatturaElettronicaHeader",required=true,nillable=false)
  protected FatturaElettronicaHeaderType fatturaElettronicaHeader;

  @XmlElement(name="FatturaElettronicaBody",required=true,nillable=false)
  protected List<FatturaElettronicaBodyType> fatturaElettronicaBody = new ArrayList<FatturaElettronicaBodyType>();

  /**
   * @deprecated Use method getFatturaElettronicaBodyList
   * @return List<FatturaElettronicaBodyType>
  */
  @Deprecated
  public List<FatturaElettronicaBodyType> getFatturaElettronicaBody() {
  	return this.fatturaElettronicaBody;
  }

  /**
   * @deprecated Use method setFatturaElettronicaBodyList
   * @param fatturaElettronicaBody List<FatturaElettronicaBodyType>
  */
  @Deprecated
  public void setFatturaElettronicaBody(List<FatturaElettronicaBodyType> fatturaElettronicaBody) {
  	this.fatturaElettronicaBody=fatturaElettronicaBody;
  }

  /**
   * @deprecated Use method sizeFatturaElettronicaBodyList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeFatturaElettronicaBody() {
  	return this.fatturaElettronicaBody.size();
  }

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_versione;

  @XmlAttribute(name="versione",required=true)
  protected FormatoTrasmissioneType versione;

}
