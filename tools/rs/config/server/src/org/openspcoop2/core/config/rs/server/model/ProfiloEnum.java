package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets ProfiloEnum
 */
public enum ProfiloEnum {
APIGATEWAY("APIGateway"),
  SPCOOP("SPCoop"),
  FATTURAPA("FatturaPA"),
  EDELIVERY("eDelivery");

  private String value;

  ProfiloEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static ProfiloEnum fromValue(String text) {
    for (ProfiloEnum b : ProfiloEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
