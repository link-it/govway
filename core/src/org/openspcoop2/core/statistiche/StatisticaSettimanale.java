/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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


/** <p>Java class for statistica-settimanale complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="statistica-settimanale"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="statistica-base" type="{http://www.openspcoop2.org/core/statistiche}statistica" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="statistica-settimanale-contenuti" type="{http://www.openspcoop2.org/core/statistiche}statistica-contenuti" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "statistica-settimanale", 
  propOrder = {
  	"statisticaBase",
  	"statisticaSettimanaleContenuti"
  }
)

@XmlRootElement(name = "statistica-settimanale")

public class StatisticaSettimanale extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public StatisticaSettimanale() {
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

  public void addStatisticaSettimanaleContenuti(StatisticaContenuti statisticaSettimanaleContenuti) {
    this.statisticaSettimanaleContenuti.add(statisticaSettimanaleContenuti);
  }

  public StatisticaContenuti getStatisticaSettimanaleContenuti(int index) {
    return this.statisticaSettimanaleContenuti.get( index );
  }

  public StatisticaContenuti removeStatisticaSettimanaleContenuti(int index) {
    return this.statisticaSettimanaleContenuti.remove( index );
  }

  public List<StatisticaContenuti> getStatisticaSettimanaleContenutiList() {
    return this.statisticaSettimanaleContenuti;
  }

  public void setStatisticaSettimanaleContenutiList(List<StatisticaContenuti> statisticaSettimanaleContenuti) {
    this.statisticaSettimanaleContenuti=statisticaSettimanaleContenuti;
  }

  public int sizeStatisticaSettimanaleContenutiList() {
    return this.statisticaSettimanaleContenuti.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.statistiche.model.StatisticaSettimanaleModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.statistiche.StatisticaSettimanale.modelStaticInstance==null){
  			org.openspcoop2.core.statistiche.StatisticaSettimanale.modelStaticInstance = new org.openspcoop2.core.statistiche.model.StatisticaSettimanaleModel();
	  }
  }
  public static org.openspcoop2.core.statistiche.model.StatisticaSettimanaleModel model(){
	  if(org.openspcoop2.core.statistiche.StatisticaSettimanale.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.statistiche.StatisticaSettimanale.modelStaticInstance;
  }


  @XmlElement(name="statistica-base",required=true,nillable=false)
  protected Statistica statisticaBase;

  @XmlElement(name="statistica-settimanale-contenuti",required=true,nillable=false)
  protected List<StatisticaContenuti> statisticaSettimanaleContenuti = new ArrayList<StatisticaContenuti>();

  /**
   * @deprecated Use method getStatisticaSettimanaleContenutiList
   * @return List&lt;StatisticaContenuti&gt;
  */
  @Deprecated
  public List<StatisticaContenuti> getStatisticaSettimanaleContenuti() {
  	return this.statisticaSettimanaleContenuti;
  }

  /**
   * @deprecated Use method setStatisticaSettimanaleContenutiList
   * @param statisticaSettimanaleContenuti List&lt;StatisticaContenuti&gt;
  */
  @Deprecated
  public void setStatisticaSettimanaleContenuti(List<StatisticaContenuti> statisticaSettimanaleContenuti) {
  	this.statisticaSettimanaleContenuti=statisticaSettimanaleContenuti;
  }

  /**
   * @deprecated Use method sizeStatisticaSettimanaleContenutiList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeStatisticaSettimanaleContenuti() {
  	return this.statisticaSettimanaleContenuti.size();
  }

}
