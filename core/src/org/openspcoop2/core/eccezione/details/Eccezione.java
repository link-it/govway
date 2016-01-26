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
package org.openspcoop2.core.eccezione.details;

import java.io.Serializable;


/** <p>Java class Eccezione.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class Eccezione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;

  protected String codice;

  protected String descrizione;

  protected String rilevanza;

  protected String contestoCodifica;

  protected String tipo;


  public Eccezione() {
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

  public String getCodice() {
    if(this.codice!=null && ("".equals(this.codice)==false)){
		return this.codice.trim();
	}else{
		return null;
	}

  }

  public void setCodice(String codice) {
    this.codice = codice;
  }

  public String getDescrizione() {
    if(this.descrizione!=null && ("".equals(this.descrizione)==false)){
		return this.descrizione.trim();
	}else{
		return null;
	}

  }

  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }

  public String getRilevanza() {
    if(this.rilevanza!=null && ("".equals(this.rilevanza)==false)){
		return this.rilevanza.trim();
	}else{
		return null;
	}

  }

  public void setRilevanza(String rilevanza) {
    this.rilevanza = rilevanza;
  }

  public String getContestoCodifica() {
    if(this.contestoCodifica!=null && ("".equals(this.contestoCodifica)==false)){
		return this.contestoCodifica.trim();
	}else{
		return null;
	}

  }

  public void setContestoCodifica(String contestoCodifica) {
    this.contestoCodifica = contestoCodifica;
  }

  public String getTipo() {
    if(this.tipo!=null && ("".equals(this.tipo)==false)){
		return this.tipo.trim();
	}else{
		return null;
	}

  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
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

  public static final String CODICE = "codice";

  public static final String DESCRIZIONE = "descrizione";

  public static final String RILEVANZA = "rilevanza";

  public static final String CONTESTO_CODIFICA = "contestoCodifica";

  public static final String TIPO = "tipo";

}
