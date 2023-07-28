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
package it.gov.fatturapa.sdi.fatturapa.v1_1;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for FatturaElettronicaType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FatturaElettronicaType"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="FatturaElettronicaHeader" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}FatturaElettronicaHeaderType" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="FatturaElettronicaBody" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}FatturaElettronicaBodyType" minOccurs="1" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="versione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}string" use="required"/&gt;
 * &lt;/complexType&gt;
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
    super();
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

  public java.lang.String getVersione() {
    return this.versione;
  }

  public void setVersione(java.lang.String versione) {
    this.versione = versione;
  }

  private static final long serialVersionUID = 1L;

  private static it.gov.fatturapa.sdi.fatturapa.v1_1.model.FatturaElettronicaTypeModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType.modelStaticInstance==null){
  			it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType.modelStaticInstance = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.FatturaElettronicaTypeModel();
	  }
  }
  public static it.gov.fatturapa.sdi.fatturapa.v1_1.model.FatturaElettronicaTypeModel model(){
	  if(it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType.modelStaticInstance;
  }


  @XmlElement(name="FatturaElettronicaHeader",required=true,nillable=false)
  protected FatturaElettronicaHeaderType fatturaElettronicaHeader;

  @XmlElement(name="FatturaElettronicaBody",required=true,nillable=false)
  private List<FatturaElettronicaBodyType> fatturaElettronicaBody = new ArrayList<>();

  /**
   * Use method getFatturaElettronicaBodyList
   * @return List&lt;FatturaElettronicaBodyType&gt;
  */
  public List<FatturaElettronicaBodyType> getFatturaElettronicaBody() {
  	return this.getFatturaElettronicaBodyList();
  }

  /**
   * Use method setFatturaElettronicaBodyList
   * @param fatturaElettronicaBody List&lt;FatturaElettronicaBodyType&gt;
  */
  public void setFatturaElettronicaBody(List<FatturaElettronicaBodyType> fatturaElettronicaBody) {
  	this.setFatturaElettronicaBodyList(fatturaElettronicaBody);
  }

  /**
   * Use method sizeFatturaElettronicaBodyList
   * @return lunghezza della lista
  */
  public int sizeFatturaElettronicaBody() {
  	return this.sizeFatturaElettronicaBodyList();
  }

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="versione",required=true)
  protected java.lang.String versione;

}
