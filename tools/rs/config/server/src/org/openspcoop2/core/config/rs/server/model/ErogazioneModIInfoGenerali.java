/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

public class ErogazioneModIInfoGenerali  {
  
  @Schema(description = "")
  private String serviceId = null;
  
  @Schema(description = "")
  private List<String> descriptorId = null;
  
  @Schema(description = "")
  private ErogazioneModISignalHub signalHub = null;
 /**
   * Get serviceId
   * @return serviceId
  **/
  @JsonProperty("service_id")
  @Valid
 @Size(max=4000)  public String getServiceId() {
    return this.serviceId;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  public ErogazioneModIInfoGenerali serviceId(String serviceId) {
    this.serviceId = serviceId;
    return this;
  }

 /**
   * Get descriptorId
   * @return descriptorId
  **/
  @JsonProperty("descriptor_id")
  @Valid
  public List<String> getDescriptorId() {
    return this.descriptorId;
  }

  public void setDescriptorId(List<String> descriptorId) {
    this.descriptorId = descriptorId;
  }

  public ErogazioneModIInfoGenerali descriptorId(List<String> descriptorId) {
    this.descriptorId = descriptorId;
    return this;
  }

  public ErogazioneModIInfoGenerali addDescriptorIdItem(String descriptorIdItem) {
    this.descriptorId.add(descriptorIdItem);
    return this;
  }

 /**
   * Get signalHub
   * @return signalHub
  **/
  @JsonProperty("signal_hub")
  @Valid
  public ErogazioneModISignalHub getSignalHub() {
    return this.signalHub;
  }

  public void setSignalHub(ErogazioneModISignalHub signalHub) {
    this.signalHub = signalHub;
  }

  public ErogazioneModIInfoGenerali signalHub(ErogazioneModISignalHub signalHub) {
    this.signalHub = signalHub;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErogazioneModIInfoGenerali {\n");
    
    sb.append("    serviceId: ").append(ErogazioneModIInfoGenerali.toIndentedString(this.serviceId)).append("\n");
    sb.append("    descriptorId: ").append(ErogazioneModIInfoGenerali.toIndentedString(this.descriptorId)).append("\n");
    sb.append("    signalHub: ").append(ErogazioneModIInfoGenerali.toIndentedString(this.signalHub)).append("\n");
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
