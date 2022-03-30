/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for porta-applicativa-autorizzazione-soggetti complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="porta-applicativa-autorizzazione-soggetti"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="soggetto" type="{http://www.openspcoop2.org/core/config}porta-applicativa-autorizzazione-soggetto" minOccurs="1" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "porta-applicativa-autorizzazione-soggetti", 
  propOrder = {
  	"soggetto"
  }
)

@XmlRootElement(name = "porta-applicativa-autorizzazione-soggetti")

public class PortaApplicativaAutorizzazioneSoggetti extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public PortaApplicativaAutorizzazioneSoggetti() {
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

  public void addSoggetto(PortaApplicativaAutorizzazioneSoggetto soggetto) {
    this.soggetto.add(soggetto);
  }

  public PortaApplicativaAutorizzazioneSoggetto getSoggetto(int index) {
    return this.soggetto.get( index );
  }

  public PortaApplicativaAutorizzazioneSoggetto removeSoggetto(int index) {
    return this.soggetto.remove( index );
  }

  public List<PortaApplicativaAutorizzazioneSoggetto> getSoggettoList() {
    return this.soggetto;
  }

  public void setSoggettoList(List<PortaApplicativaAutorizzazioneSoggetto> soggetto) {
    this.soggetto=soggetto;
  }

  public int sizeSoggettoList() {
    return this.soggetto.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="soggetto",required=true,nillable=false)
  protected List<PortaApplicativaAutorizzazioneSoggetto> soggetto = new ArrayList<PortaApplicativaAutorizzazioneSoggetto>();

  /**
   * @deprecated Use method getSoggettoList
   * @return List&lt;PortaApplicativaAutorizzazioneSoggetto&gt;
  */
  @Deprecated
  public List<PortaApplicativaAutorizzazioneSoggetto> getSoggetto() {
  	return this.soggetto;
  }

  /**
   * @deprecated Use method setSoggettoList
   * @param soggetto List&lt;PortaApplicativaAutorizzazioneSoggetto&gt;
  */
  @Deprecated
  public void setSoggetto(List<PortaApplicativaAutorizzazioneSoggetto> soggetto) {
  	this.soggetto=soggetto;
  }

  /**
   * @deprecated Use method sizeSoggettoList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeSoggetto() {
  	return this.soggetto.size();
  }

}
