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
package org.openspcoop2.core.statistiche;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for statistica-giornaliera complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="statistica-giornaliera"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="statistica-base" type="{http://www.openspcoop2.org/core/statistiche}statistica" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="statistica-giornaliera-contenuti" type="{http://www.openspcoop2.org/core/statistiche}statistica-contenuti" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "statistica-giornaliera", 
  propOrder = {
  	"statisticaBase",
  	"statisticaGiornalieraContenuti"
  }
)

@XmlRootElement(name = "statistica-giornaliera")

public class StatisticaGiornaliera extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public StatisticaGiornaliera() {
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

  public Statistica getStatisticaBase() {
    return this.statisticaBase;
  }

  public void setStatisticaBase(Statistica statisticaBase) {
    this.statisticaBase = statisticaBase;
  }

  public void addStatisticaGiornalieraContenuti(StatisticaContenuti statisticaGiornalieraContenuti) {
    this.statisticaGiornalieraContenuti.add(statisticaGiornalieraContenuti);
  }

  public StatisticaContenuti getStatisticaGiornalieraContenuti(int index) {
    return this.statisticaGiornalieraContenuti.get( index );
  }

  public StatisticaContenuti removeStatisticaGiornalieraContenuti(int index) {
    return this.statisticaGiornalieraContenuti.remove( index );
  }

  public List<StatisticaContenuti> getStatisticaGiornalieraContenutiList() {
    return this.statisticaGiornalieraContenuti;
  }

  public void setStatisticaGiornalieraContenutiList(List<StatisticaContenuti> statisticaGiornalieraContenuti) {
    this.statisticaGiornalieraContenuti=statisticaGiornalieraContenuti;
  }

  public int sizeStatisticaGiornalieraContenutiList() {
    return this.statisticaGiornalieraContenuti.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.statistiche.model.StatisticaGiornalieraModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.statistiche.StatisticaGiornaliera.modelStaticInstance==null){
  			org.openspcoop2.core.statistiche.StatisticaGiornaliera.modelStaticInstance = new org.openspcoop2.core.statistiche.model.StatisticaGiornalieraModel();
	  }
  }
  public static org.openspcoop2.core.statistiche.model.StatisticaGiornalieraModel model(){
	  if(org.openspcoop2.core.statistiche.StatisticaGiornaliera.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.statistiche.StatisticaGiornaliera.modelStaticInstance;
  }


  @XmlElement(name="statistica-base",required=true,nillable=false)
  protected Statistica statisticaBase;

  @XmlElement(name="statistica-giornaliera-contenuti",required=true,nillable=false)
  protected List<StatisticaContenuti> statisticaGiornalieraContenuti = new ArrayList<StatisticaContenuti>();

  /**
   * @deprecated Use method getStatisticaGiornalieraContenutiList
   * @return List&lt;StatisticaContenuti&gt;
  */
  @Deprecated
  public List<StatisticaContenuti> getStatisticaGiornalieraContenuti() {
  	return this.statisticaGiornalieraContenuti;
  }

  /**
   * @deprecated Use method setStatisticaGiornalieraContenutiList
   * @param statisticaGiornalieraContenuti List&lt;StatisticaContenuti&gt;
  */
  @Deprecated
  public void setStatisticaGiornalieraContenuti(List<StatisticaContenuti> statisticaGiornalieraContenuti) {
  	this.statisticaGiornalieraContenuti=statisticaGiornalieraContenuti;
  }

  /**
   * @deprecated Use method sizeStatisticaGiornalieraContenutiList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeStatisticaGiornalieraContenuti() {
  	return this.statisticaGiornalieraContenuti.size();
  }

}
