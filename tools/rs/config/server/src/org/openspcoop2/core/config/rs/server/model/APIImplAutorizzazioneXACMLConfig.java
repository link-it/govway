package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.APIImplAutorizzazioneXACMLBaseConfig;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class APIImplAutorizzazioneXACMLConfig extends APIImplAutorizzazioneXACMLBaseConfig {
  
  @Schema(required = true, description = "")
  private byte[] policy = null;
 /**
   * Get policy
   * @return policy
  **/
  @JsonProperty("policy")
  @NotNull
  @Valid
  public byte[] getPolicy() {
    return this.policy;
  }

  public void setPolicy(byte[] policy) {
    this.policy = policy;
  }

  public APIImplAutorizzazioneXACMLConfig policy(byte[] policy) {
    this.policy = policy;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class APIImplAutorizzazioneXACMLConfig {\n");
    sb.append("    ").append(APIImplAutorizzazioneXACMLConfig.toIndentedString(super.toString())).append("\n");
    sb.append("    policy: ").append(APIImplAutorizzazioneXACMLConfig.toIndentedString(this.policy)).append("\n");
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
