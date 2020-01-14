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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneInformazioniApi", propOrder =
    { "erogatore", "nome", "versione", "operazione"
})

@XmlRootElement(name="TransazioneInformazioniApi")
public class TransazioneInformazioniApi  {
  @XmlElement(name="erogatore", required = true)
  
  @Schema(required = true, description = "")
  private String erogatore = null;
  @XmlElement(name="nome", required = true)
  
  @Schema(required = true, description = "")
  private String nome = null;
  @XmlElement(name="versione", required = true)
  
  @Schema(required = true, description = "")
  private Integer versione = null;
  @XmlElement(name="operazione")
  
  @Schema(description = "")
  private String operazione = null;
 /**
   * Get erogatore
   * @return erogatore
  **/
  @JsonProperty("erogatore")
  @NotNull
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getErogatore() {
    return this.erogatore;
  }

  public void setErogatore(String erogatore) {
    this.erogatore = erogatore;
  }

  public TransazioneInformazioniApi erogatore(String erogatore) {
    this.erogatore = erogatore;
    return this;
  }

 /**
   * Get nome
   * @return nome
  **/
  @JsonProperty("nome")
  @NotNull
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public TransazioneInformazioniApi nome(String nome) {
    this.nome = nome;
    return this;
  }

 /**
   * Get versione
   * @return versione
  **/
  @JsonProperty("versione")
  @NotNull
  @Valid
  public Integer getVersione() {
    return this.versione;
  }

  public void setVersione(Integer versione) {
    this.versione = versione;
  }

  public TransazioneInformazioniApi versione(Integer versione) {
    this.versione = versione;
    return this;
  }

 /**
   * Get operazione
   * @return operazione
  **/
  @JsonProperty("operazione")
  @Valid
 @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255)  public String getOperazione() {
    return this.operazione;
  }

  public void setOperazione(String operazione) {
    this.operazione = operazione;
  }

  public TransazioneInformazioniApi operazione(String operazione) {
    this.operazione = operazione;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneInformazioniApi {\n");
    
    sb.append("    erogatore: ").append(TransazioneInformazioniApi.toIndentedString(this.erogatore)).append("\n");
    sb.append("    nome: ").append(TransazioneInformazioniApi.toIndentedString(this.nome)).append("\n");
    sb.append("    versione: ").append(TransazioneInformazioniApi.toIndentedString(this.versione)).append("\n");
    sb.append("    operazione: ").append(TransazioneInformazioniApi.toIndentedString(this.operazione)).append("\n");
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
