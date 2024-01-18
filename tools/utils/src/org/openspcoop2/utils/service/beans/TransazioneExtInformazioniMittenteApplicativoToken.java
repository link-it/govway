/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
 @XmlType(name = "TransazioneExtInformazioniMittenteApplicativoToken", propOrder =
    { "nome", "soggetto", "informazioniSoggetto"
})

@XmlRootElement(name="TransazioneExtInformazioniMittenteApplicativoToken")
public class TransazioneExtInformazioniMittenteApplicativoToken  {
  @XmlElement(name="nome")
  
  @Schema(description = "")
  private String nome = null;
  @XmlElement(name="soggetto")
  
  @Schema(description = "")
  private String soggetto = null;
  @XmlElement(name="informazioni_soggetto")
  
  @Schema(description = "")
  private TransazioneExtInformazioniSoggetto informazioniSoggetto = null;
 /**
   * Get nome
   * @return nome
  **/
  @JsonProperty("nome")
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public TransazioneExtInformazioniMittenteApplicativoToken nome(String nome) {
    this.nome = nome;
    return this;
  }

 /**
   * Get soggetto
   * @return soggetto
  **/
  @JsonProperty("soggetto")
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getSoggetto() {
    return this.soggetto;
  }

  public void setSoggetto(String soggetto) {
    this.soggetto = soggetto;
  }

  public TransazioneExtInformazioniMittenteApplicativoToken soggetto(String soggetto) {
    this.soggetto = soggetto;
    return this;
  }

 /**
   * Get informazioniSoggetto
   * @return informazioniSoggetto
  **/
  @JsonProperty("informazioni_soggetto")
  @Valid
  public TransazioneExtInformazioniSoggetto getInformazioniSoggetto() {
    return this.informazioniSoggetto;
  }

  public void setInformazioniSoggetto(TransazioneExtInformazioniSoggetto informazioniSoggetto) {
    this.informazioniSoggetto = informazioniSoggetto;
  }

  public TransazioneExtInformazioniMittenteApplicativoToken informazioniSoggetto(TransazioneExtInformazioniSoggetto informazioniSoggetto) {
    this.informazioniSoggetto = informazioniSoggetto;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneExtInformazioniMittenteApplicativoToken {\n");
    
    sb.append("    nome: ").append(TransazioneExtInformazioniMittenteApplicativoToken.toIndentedString(this.nome)).append("\n");
    sb.append("    soggetto: ").append(TransazioneExtInformazioniMittenteApplicativoToken.toIndentedString(this.soggetto)).append("\n");
    sb.append("    informazioniSoggetto: ").append(TransazioneExtInformazioniMittenteApplicativoToken.toIndentedString(this.informazioniSoggetto)).append("\n");
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
