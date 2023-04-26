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
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for statistica-mensile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="statistica-mensile"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="statistica-base" type="{http://www.openspcoop2.org/core/statistiche}statistica" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="statistica-mensile-contenuti" type="{http://www.openspcoop2.org/core/statistiche}statistica-contenuti" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "statistica-mensile", 
  propOrder = {
  	"statisticaBase",
  	"statisticaMensileContenuti"
  }
)

@XmlRootElement(name = "statistica-mensile")

public class StatisticaMensile extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public StatisticaMensile() {
    super();
  }

  public Statistica getStatisticaBase() {
    return this.statisticaBase;
  }

  public void setStatisticaBase(Statistica statisticaBase) {
    this.statisticaBase = statisticaBase;
  }

  public void addStatisticaMensileContenuti(StatisticaContenuti statisticaMensileContenuti) {
    this.statisticaMensileContenuti.add(statisticaMensileContenuti);
  }

  public StatisticaContenuti getStatisticaMensileContenuti(int index) {
    return this.statisticaMensileContenuti.get( index );
  }

  public StatisticaContenuti removeStatisticaMensileContenuti(int index) {
    return this.statisticaMensileContenuti.remove( index );
  }

  public List<StatisticaContenuti> getStatisticaMensileContenutiList() {
    return this.statisticaMensileContenuti;
  }

  public void setStatisticaMensileContenutiList(List<StatisticaContenuti> statisticaMensileContenuti) {
    this.statisticaMensileContenuti=statisticaMensileContenuti;
  }

  public int sizeStatisticaMensileContenutiList() {
    return this.statisticaMensileContenuti.size();
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.statistiche.model.StatisticaMensileModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.statistiche.StatisticaMensile.modelStaticInstance==null){
  			org.openspcoop2.core.statistiche.StatisticaMensile.modelStaticInstance = new org.openspcoop2.core.statistiche.model.StatisticaMensileModel();
	  }
  }
  public static org.openspcoop2.core.statistiche.model.StatisticaMensileModel model(){
	  if(org.openspcoop2.core.statistiche.StatisticaMensile.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.statistiche.StatisticaMensile.modelStaticInstance;
  }


  @XmlElement(name="statistica-base",required=true,nillable=false)
  protected Statistica statisticaBase;

  @XmlElement(name="statistica-mensile-contenuti",required=true,nillable=false)
  private List<StatisticaContenuti> statisticaMensileContenuti = new ArrayList<>();

  /**
   * Use method getStatisticaMensileContenutiList
   * @return List&lt;StatisticaContenuti&gt;
  */
  public List<StatisticaContenuti> getStatisticaMensileContenuti() {
  	return this.getStatisticaMensileContenutiList();
  }

  /**
   * Use method setStatisticaMensileContenutiList
   * @param statisticaMensileContenuti List&lt;StatisticaContenuti&gt;
  */
  public void setStatisticaMensileContenuti(List<StatisticaContenuti> statisticaMensileContenuti) {
  	this.setStatisticaMensileContenutiList(statisticaMensileContenuti);
  }

  /**
   * Use method sizeStatisticaMensileContenutiList
   * @return lunghezza della lista
  */
  public int sizeStatisticaMensileContenuti() {
  	return this.sizeStatisticaMensileContenutiList();
  }

}
