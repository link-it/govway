/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.TransazioneContestoEnum;
import org.openspcoop2.utils.service.beans.TransazioneExtInformazioniSoggetto;
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
 @XmlType(name = "TransazioneExtTipo", propOrder =
    { "profilo", "contesto", "idCluster", "informazioniEmittente", "stato"
})

@XmlRootElement(name="TransazioneExtTipo")
public class TransazioneExtTipo  {
  @XmlElement(name="profilo", required = true)
  
  @Schema(required = true, description = "")
  private ProfiloEnum profilo = null;
  @XmlElement(name="contesto", required = true)
  
  @Schema(required = true, description = "")
  private TransazioneContestoEnum contesto = null;
  @XmlElement(name="id_cluster")
  
  @Schema(description = "")
  private String idCluster = null;
  @XmlElement(name="informazioni_emittente")
  
  @Schema(description = "")
  private TransazioneExtInformazioniSoggetto informazioniEmittente = null;
  @XmlElement(name="stato")
  
  @Schema(description = "")
  private String stato = null;
 /**
   * Get profilo
   * @return profilo
  **/
  @JsonProperty("profilo")
  @NotNull
  @Valid
  public ProfiloEnum getProfilo() {
    return this.profilo;
  }

  public void setProfilo(ProfiloEnum profilo) {
    this.profilo = profilo;
  }

  public TransazioneExtTipo profilo(ProfiloEnum profilo) {
    this.profilo = profilo;
    return this;
  }

 /**
   * Get contesto
   * @return contesto
  **/
  @JsonProperty("contesto")
  @NotNull
  @Valid
  public TransazioneContestoEnum getContesto() {
    return this.contesto;
  }

  public void setContesto(TransazioneContestoEnum contesto) {
    this.contesto = contesto;
  }

  public TransazioneExtTipo contesto(TransazioneContestoEnum contesto) {
    this.contesto = contesto;
    return this;
  }

 /**
   * Get idCluster
   * @return idCluster
  **/
  @JsonProperty("id_cluster")
  @Valid
  public String getIdCluster() {
    return this.idCluster;
  }

  public void setIdCluster(String idCluster) {
    this.idCluster = idCluster;
  }

  public TransazioneExtTipo idCluster(String idCluster) {
    this.idCluster = idCluster;
    return this;
  }

 /**
   * Get informazioniEmittente
   * @return informazioniEmittente
  **/
  @JsonProperty("informazioni_emittente")
  @Valid
  public TransazioneExtInformazioniSoggetto getInformazioniEmittente() {
    return this.informazioniEmittente;
  }

  public void setInformazioniEmittente(TransazioneExtInformazioniSoggetto informazioniEmittente) {
    this.informazioniEmittente = informazioniEmittente;
  }

  public TransazioneExtTipo informazioniEmittente(TransazioneExtInformazioniSoggetto informazioniEmittente) {
    this.informazioniEmittente = informazioniEmittente;
    return this;
  }

 /**
   * Get stato
   * @return stato
  **/
  @JsonProperty("stato")
  @Valid
  public String getStato() {
    return this.stato;
  }

  public void setStato(String stato) {
    this.stato = stato;
  }

  public TransazioneExtTipo stato(String stato) {
    this.stato = stato;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneExtTipo {\n");
    
    sb.append("    profilo: ").append(TransazioneExtTipo.toIndentedString(this.profilo)).append("\n");
    sb.append("    contesto: ").append(TransazioneExtTipo.toIndentedString(this.contesto)).append("\n");
    sb.append("    idCluster: ").append(TransazioneExtTipo.toIndentedString(this.idCluster)).append("\n");
    sb.append("    informazioniEmittente: ").append(TransazioneExtTipo.toIndentedString(this.informazioniEmittente)).append("\n");
    sb.append("    stato: ").append(TransazioneExtTipo.toIndentedString(this.stato)).append("\n");
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
