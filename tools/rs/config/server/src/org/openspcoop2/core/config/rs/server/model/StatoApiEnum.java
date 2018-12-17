package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets StatoApiEnum
 */
public enum StatoApiEnum {
OK("ok"),
  WARN("warn"),
  ERROR("error");

  private String value;

  StatoApiEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static StatoApiEnum fromValue(String text) {
    for (StatoApiEnum b : StatoApiEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
