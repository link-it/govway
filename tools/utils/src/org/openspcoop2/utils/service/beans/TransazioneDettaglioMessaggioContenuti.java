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
package org.openspcoop2.utils.service.beans;

import org.openspcoop2.utils.service.beans.TransazioneContenutoMessaggio;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneDettaglioMessaggioContenuti", propOrder =
    { "contenuti"
})

@XmlRootElement(name="TransazioneDettaglioMessaggioContenuti")
public class TransazioneDettaglioMessaggioContenuti  {
  @XmlElement(name="contenuti")
  
  @Schema(description = "")
  private TransazioneContenutoMessaggio contenuti = null;
 /**
   * Get contenuti
   * @return contenuti
  **/
  @JsonProperty("contenuti")
  @Valid
  public TransazioneContenutoMessaggio getContenuti() {
    return this.contenuti;
  }

  public void setContenuti(TransazioneContenutoMessaggio contenuti) {
    this.contenuti = contenuti;
  }

  public TransazioneDettaglioMessaggioContenuti contenuti(TransazioneContenutoMessaggio contenuti) {
    this.contenuti = contenuti;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneDettaglioMessaggioContenuti {\n");
    
    sb.append("    contenuti: ").append(TransazioneDettaglioMessaggioContenuti.toIndentedString(this.contenuti)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private static String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
