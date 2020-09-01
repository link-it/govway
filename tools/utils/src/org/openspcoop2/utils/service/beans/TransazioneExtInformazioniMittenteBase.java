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

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneExtInformazioniMittenteBase", propOrder =
    { "informazioniFruitore", "applicativo"
})


public class TransazioneExtInformazioniMittenteBase extends TransazioneInformazioniMittente {
  @XmlElement(name="informazioni_fruitore")
  
  @Schema(description = "")
  private TransazioneExtInformazioniSoggetto informazioniFruitore = null;
  @XmlElement(name="applicativo")
  
  @Schema(description = "")
  private String applicativo = null;
 /**
   * Get informazioniFruitore
   * @return informazioniFruitore
  **/
  @JsonProperty("informazioni_fruitore")
  @Valid
  public TransazioneExtInformazioniSoggetto getInformazioniFruitore() {
    return this.informazioniFruitore;
  }

  public void setInformazioniFruitore(TransazioneExtInformazioniSoggetto informazioniFruitore) {
    this.informazioniFruitore = informazioniFruitore;
  }

  public TransazioneExtInformazioniMittenteBase informazioniFruitore(TransazioneExtInformazioniSoggetto informazioniFruitore) {
    this.informazioniFruitore = informazioniFruitore;
    return this;
  }

 /**
   * Get applicativo
   * @return applicativo
  **/
  @JsonProperty("applicativo")
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getApplicativo() {
    return this.applicativo;
  }

  public void setApplicativo(String applicativo) {
    this.applicativo = applicativo;
  }

  public TransazioneExtInformazioniMittenteBase applicativo(String applicativo) {
    this.applicativo = applicativo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneExtInformazioniMittenteBase {\n");
    sb.append("    ").append(TransazioneExtInformazioniMittenteBase.toIndentedString(super.toString())).append("\n");
    sb.append("    informazioniFruitore: ").append(TransazioneExtInformazioniMittenteBase.toIndentedString(this.informazioniFruitore)).append("\n");
    sb.append("    applicativo: ").append(TransazioneExtInformazioniMittenteBase.toIndentedString(this.applicativo)).append("\n");
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
