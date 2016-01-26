/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.core.eccezione.errore_applicativo;

import java.io.Serializable;


/** <p>Java class DatiCooperazione.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class DatiCooperazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;

  protected Soggetto fruitore;

  protected Soggetto erogatore;

  protected Servizio servizio;

  protected String azione;

  protected String servizioApplicativo;


  public DatiCooperazione() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public Soggetto getFruitore() {
    return this.fruitore;
  }

  public void setFruitore(Soggetto fruitore) {
    this.fruitore = fruitore;
  }

  public Soggetto getErogatore() {
    return this.erogatore;
  }

  public void setErogatore(Soggetto erogatore) {
    this.erogatore = erogatore;
  }

  public Servizio getServizio() {
    return this.servizio;
  }

  public void setServizio(Servizio servizio) {
    this.servizio = servizio;
  }

  public String getAzione() {
    if(this.azione!=null && ("".equals(this.azione)==false)){
		return this.azione.trim();
	}else{
		return null;
	}

  }

  public void setAzione(String azione) {
    this.azione = azione;
  }

  public String getServizioApplicativo() {
    if(this.servizioApplicativo!=null && ("".equals(this.servizioApplicativo)==false)){
		return this.servizioApplicativo.trim();
	}else{
		return null;
	}

  }

  public void setServizioApplicativo(String servizioApplicativo) {
    this.servizioApplicativo = servizioApplicativo;
  }

  private static final long serialVersionUID = 1L;

	@Override
	public String serialize(org.openspcoop2.utils.beans.WriteToSerializerType type) throws org.openspcoop2.utils.UtilsException {
		if(type!=null && org.openspcoop2.utils.beans.WriteToSerializerType.JAXB.equals(type)){
			throw new org.openspcoop2.utils.UtilsException("Jaxb annotations not generated");
		}
		else{
			return super.serialize(type);
		}
	}
	@Override
	public String toXml_Jaxb() throws org.openspcoop2.utils.UtilsException {
		throw new org.openspcoop2.utils.UtilsException("Jaxb annotations not generated");
	}

  public static final String FRUITORE = "fruitore";

  public static final String EROGATORE = "erogatore";

  public static final String SERVIZIO = "servizio";

  public static final String AZIONE = "azione";

  public static final String SERVIZIO_APPLICATIVO = "servizioApplicativo";

}
