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
package org.openspcoop2.utils.service.beans;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneEsito", propOrder =
    { "codice", "descrizione"
})

@XmlRootElement(name="TransazioneEsito")
public class TransazioneEsito  {
  @XmlElement(name="codice", required = true)
  
  @Schema(required = true, description = "Codice che rappresenta l'esito dell'invocazione. Può essere un http status per i protocolli basati su HTTP, o un altra codifica (es. OK/KO)")
 /**
   * Codice che rappresenta l'esito dell'invocazione. Può essere un http status per i protocolli basati su HTTP, o un altra codifica (es. OK/KO)  
  **/
  private String codice = null;
  @XmlElement(name="descrizione", required = true)
  
  @Schema(required = true, description = "")
  private String descrizione = null;
 /**
   * Codice che rappresenta l&#x27;esito dell&#x27;invocazione. Può essere un http status per i protocolli basati su HTTP, o un altra codifica (es. OK/KO)
   * @return codice
  **/
  @JsonProperty("codice")
  @NotNull
  @Valid
  public String getCodice() {
    return this.codice;
  }

  public void setCodice(String codice) {
    this.codice = codice;
  }

  public TransazioneEsito codice(String codice) {
    this.codice = codice;
    return this;
  }

 /**
   * Get descrizione
   * @return descrizione
  **/
  @JsonProperty("descrizione")
  @NotNull
  @Valid
  public String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }

  public TransazioneEsito descrizione(String descrizione) {
    this.descrizione = descrizione;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneEsito {\n");
    
    sb.append("    codice: ").append(TransazioneEsito.toIndentedString(this.codice)).append("\n");
    sb.append("    descrizione: ").append(TransazioneEsito.toIndentedString(this.descrizione)).append("\n");
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
