/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.core.statistiche;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.statistiche.constants.TipoIntervalloStatistico;
import java.io.Serializable;


/** <p>Java class for statistica-info complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="statistica-info">
 * 		&lt;sequence>
 * 			&lt;element name="tipo-statistica" type="{http://www.openspcoop2.org/core/statistiche}tipo-intervallo-statistico" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="data-ultima-generazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/>
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
@XmlType(name = "statistica-info", 
  propOrder = {
  	"tipoStatistica",
  	"dataUltimaGenerazione"
  }
)

@XmlRootElement(name = "statistica-info")

public class StatisticaInfo extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public StatisticaInfo() {
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

  public void set_value_tipoStatistica(String value) {
    this.tipoStatistica = (TipoIntervalloStatistico) TipoIntervalloStatistico.toEnumConstantFromString(value);
  }

  public String get_value_tipoStatistica() {
    if(this.tipoStatistica == null){
    	return null;
    }else{
    	return this.tipoStatistica.toString();
    }
  }

  public org.openspcoop2.core.statistiche.constants.TipoIntervalloStatistico getTipoStatistica() {
    return this.tipoStatistica;
  }

  public void setTipoStatistica(org.openspcoop2.core.statistiche.constants.TipoIntervalloStatistico tipoStatistica) {
    this.tipoStatistica = tipoStatistica;
  }

  public java.util.Date getDataUltimaGenerazione() {
    return this.dataUltimaGenerazione;
  }

  public void setDataUltimaGenerazione(java.util.Date dataUltimaGenerazione) {
    this.dataUltimaGenerazione = dataUltimaGenerazione;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.statistiche.model.StatisticaInfoModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.statistiche.StatisticaInfo.modelStaticInstance==null){
  			org.openspcoop2.core.statistiche.StatisticaInfo.modelStaticInstance = new org.openspcoop2.core.statistiche.model.StatisticaInfoModel();
	  }
  }
  public static org.openspcoop2.core.statistiche.model.StatisticaInfoModel model(){
	  if(org.openspcoop2.core.statistiche.StatisticaInfo.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.statistiche.StatisticaInfo.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipoStatistica;

  @XmlElement(name="tipo-statistica",required=true,nillable=false)
  protected TipoIntervalloStatistico tipoStatistica;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="data-ultima-generazione",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date dataUltimaGenerazione;

}
