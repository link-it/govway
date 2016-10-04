/*
 * OpenSPCoop - Customizable API Gateway 
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


/** <p>Java class Soggetto.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class Soggetto extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;

  protected SoggettoIdentificativo identificativo;

  protected String identificativoPorta;


  public Soggetto() {
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

  public SoggettoIdentificativo getIdentificativo() {
    return this.identificativo;
  }

  public void setIdentificativo(SoggettoIdentificativo identificativo) {
    this.identificativo = identificativo;
  }

  public String getIdentificativoPorta() {
    if(this.identificativoPorta!=null && ("".equals(this.identificativoPorta)==false)){
		return this.identificativoPorta.trim();
	}else{
		return null;
	}

  }

  public void setIdentificativoPorta(String identificativoPorta) {
    this.identificativoPorta = identificativoPorta;
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

  public static final String IDENTIFICATIVO = "identificativo";

  public static final String IDENTIFICATIVO_PORTA = "identificativoPorta";

}
