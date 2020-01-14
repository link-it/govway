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


/** <p>Java class for statistica-oraria complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="statistica-oraria">
 * 		&lt;sequence>
 * 			&lt;element name="statistica-base" type="{http://www.openspcoop2.org/core/statistiche}statistica" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="statistica-oraria-contenuti" type="{http://www.openspcoop2.org/core/statistiche}statistica-contenuti" minOccurs="0" maxOccurs="unbounded"/>
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
@XmlType(name = "statistica-oraria", 
  propOrder = {
  	"statisticaBase",
  	"statisticaOrariaContenuti"
  }
)

@XmlRootElement(name = "statistica-oraria")

public class StatisticaOraria extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public StatisticaOraria() {
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

  public void addStatisticaOrariaContenuti(StatisticaContenuti statisticaOrariaContenuti) {
    this.statisticaOrariaContenuti.add(statisticaOrariaContenuti);
  }

  public StatisticaContenuti getStatisticaOrariaContenuti(int index) {
    return this.statisticaOrariaContenuti.get( index );
  }

  public StatisticaContenuti removeStatisticaOrariaContenuti(int index) {
    return this.statisticaOrariaContenuti.remove( index );
  }

  public List<StatisticaContenuti> getStatisticaOrariaContenutiList() {
    return this.statisticaOrariaContenuti;
  }

  public void setStatisticaOrariaContenutiList(List<StatisticaContenuti> statisticaOrariaContenuti) {
    this.statisticaOrariaContenuti=statisticaOrariaContenuti;
  }

  public int sizeStatisticaOrariaContenutiList() {
    return this.statisticaOrariaContenuti.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.statistiche.model.StatisticaOrariaModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.statistiche.StatisticaOraria.modelStaticInstance==null){
  			org.openspcoop2.core.statistiche.StatisticaOraria.modelStaticInstance = new org.openspcoop2.core.statistiche.model.StatisticaOrariaModel();
	  }
  }
  public static org.openspcoop2.core.statistiche.model.StatisticaOrariaModel model(){
	  if(org.openspcoop2.core.statistiche.StatisticaOraria.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.statistiche.StatisticaOraria.modelStaticInstance;
  }


  @XmlElement(name="statistica-base",required=true,nillable=false)
  protected Statistica statisticaBase;

  @XmlElement(name="statistica-oraria-contenuti",required=true,nillable=false)
  protected List<StatisticaContenuti> statisticaOrariaContenuti = new ArrayList<StatisticaContenuti>();

  /**
   * @deprecated Use method getStatisticaOrariaContenutiList
   * @return List<StatisticaContenuti>
  */
  @Deprecated
  public List<StatisticaContenuti> getStatisticaOrariaContenuti() {
  	return this.statisticaOrariaContenuti;
  }

  /**
   * @deprecated Use method setStatisticaOrariaContenutiList
   * @param statisticaOrariaContenuti List<StatisticaContenuti>
  */
  @Deprecated
  public void setStatisticaOrariaContenuti(List<StatisticaContenuti> statisticaOrariaContenuti) {
  	this.statisticaOrariaContenuti=statisticaOrariaContenuti;
  }

  /**
   * @deprecated Use method sizeStatisticaOrariaContenutiList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeStatisticaOrariaContenuti() {
  	return this.statisticaOrariaContenuti.size();
  }

}
