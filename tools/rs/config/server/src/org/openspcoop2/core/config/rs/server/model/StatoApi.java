package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets StatoApi
 */
public enum StatoApi {
OK("ok"),
  WARN("warn"),
  ERROR("error");

  private String value;

  StatoApi(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static StatoApi fromValue(String text) {
    for (StatoApi b : StatoApi.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
