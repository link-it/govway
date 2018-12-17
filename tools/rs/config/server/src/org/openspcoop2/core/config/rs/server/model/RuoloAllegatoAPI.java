package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets RuoloAllegatoAPI
 */
public enum RuoloAllegatoAPI {
ALLEGATO("allegato"),
  SPECIFICASEMIFORMALE("specificaSemiFormale");

  private String value;

  RuoloAllegatoAPI(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static RuoloAllegatoAPI fromValue(String text) {
    for (RuoloAllegatoAPI b : RuoloAllegatoAPI.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
