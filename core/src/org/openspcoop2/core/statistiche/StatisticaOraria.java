/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for statistica-oraria complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="statistica-oraria"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="statistica-base" type="{http://www.openspcoop2.org/core/statistiche}statistica" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="statistica-oraria-contenuti" type="{http://www.openspcoop2.org/core/statistiche}statistica-contenuti" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author Tommaso Burlon (tommaso.burlon@link.it)
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

public class StatisticaOraria extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public StatisticaOraria() {
    super();
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
  private List<StatisticaContenuti> statisticaOrariaContenuti = new ArrayList<>();

  /**
   * Use method getStatisticaOrariaContenutiList
   * @return List&lt;StatisticaContenuti&gt;
  */
  public List<StatisticaContenuti> getStatisticaOrariaContenuti() {
  	return this.getStatisticaOrariaContenutiList();
  }

  /**
   * Use method setStatisticaOrariaContenutiList
   * @param statisticaOrariaContenuti List&lt;StatisticaContenuti&gt;
  */
  public void setStatisticaOrariaContenuti(List<StatisticaContenuti> statisticaOrariaContenuti) {
  	this.setStatisticaOrariaContenutiList(statisticaOrariaContenuti);
  }

  /**
   * Use method sizeStatisticaOrariaContenutiList
   * @return lunghezza della lista
  */
  public int sizeStatisticaOrariaContenuti() {
  	return this.sizeStatisticaOrariaContenutiList();
  }

}
