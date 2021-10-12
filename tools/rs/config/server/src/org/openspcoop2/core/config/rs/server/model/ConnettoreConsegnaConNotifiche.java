/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.rs.server.model;

import java.util.List;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ConnettoreConsegnaConNotifiche extends BaseConnettoriMultipliPresaInCarico implements OneOfConnettoreErogazioneConnettoreMultiplo {
  
  @Schema(required = true, description = "")
  private ConnettoreMultiploEnum tipo = null;
  
  @Schema(required = true, description = "")
  private String connettoreDefault = null;
  
  @Schema(description = "le notifiche avvengono solamente se la transazione termina con uno degli esiti indicat")
 /**
   * le notifiche avvengono solamente se la transazione termina con uno degli esiti indicat  
  **/
  private List<ConnettoreConsegnaConNotificheEsitiEnum> esiti = null;
  
  @Schema(description = "")
  private ConnettoreMultiploConfigurazioneCondizionalitaSelezioneConnettore consegnaCondizionale = null;
 /**
   * Get tipo
   * @return tipo
  **/
  @Override
@JsonProperty("tipo")
  @NotNull
  @Valid
  public ConnettoreMultiploEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(ConnettoreMultiploEnum tipo) {
    this.tipo = tipo;
  }

  public ConnettoreConsegnaConNotifiche tipo(ConnettoreMultiploEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get connettoreDefault
   * @return connettoreDefault
  **/
  @JsonProperty("connettore_default")
  @NotNull
  @Valid
 @Size(max=255)  public String getConnettoreDefault() {
    return this.connettoreDefault;
  }

  public void setConnettoreDefault(String connettoreDefault) {
    this.connettoreDefault = connettoreDefault;
  }

  public ConnettoreConsegnaConNotifiche connettoreDefault(String connettoreDefault) {
    this.connettoreDefault = connettoreDefault;
    return this;
  }

 /**
   * le notifiche avvengono solamente se la transazione termina con uno degli esiti indicat
   * @return esiti
  **/
  @JsonProperty("esiti")
  @Valid
  public List<ConnettoreConsegnaConNotificheEsitiEnum> getEsiti() {
    return this.esiti;
  }

  public void setEsiti(List<ConnettoreConsegnaConNotificheEsitiEnum> esiti) {
    this.esiti = esiti;
  }

  public ConnettoreConsegnaConNotifiche esiti(List<ConnettoreConsegnaConNotificheEsitiEnum> esiti) {
    this.esiti = esiti;
    return this;
  }

  public ConnettoreConsegnaConNotifiche addEsitiItem(ConnettoreConsegnaConNotificheEsitiEnum esitiItem) {
    this.esiti.add(esitiItem);
    return this;
  }

 /**
   * Get consegnaCondizionale
   * @return consegnaCondizionale
  **/
  @JsonProperty("consegna_condizionale")
  @Valid
  public ConnettoreMultiploConfigurazioneCondizionalitaSelezioneConnettore getConsegnaCondizionale() {
    return this.consegnaCondizionale;
  }

  public void setConsegnaCondizionale(ConnettoreMultiploConfigurazioneCondizionalitaSelezioneConnettore consegnaCondizionale) {
    this.consegnaCondizionale = consegnaCondizionale;
  }

  public ConnettoreConsegnaConNotifiche consegnaCondizionale(ConnettoreMultiploConfigurazioneCondizionalitaSelezioneConnettore consegnaCondizionale) {
    this.consegnaCondizionale = consegnaCondizionale;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreConsegnaConNotifiche {\n");
    sb.append("    ").append(ConnettoreConsegnaConNotifiche.toIndentedString(super.toString())).append("\n");
    sb.append("    tipo: ").append(ConnettoreConsegnaConNotifiche.toIndentedString(this.tipo)).append("\n");
    sb.append("    connettoreDefault: ").append(ConnettoreConsegnaConNotifiche.toIndentedString(this.connettoreDefault)).append("\n");
    sb.append("    esiti: ").append(ConnettoreConsegnaConNotifiche.toIndentedString(this.esiti)).append("\n");
    sb.append("    consegnaCondizionale: ").append(ConnettoreConsegnaConNotifiche.toIndentedString(this.consegnaCondizionale)).append("\n");
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
