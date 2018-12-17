package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets RuoloAllegatoAPIImpl
 */
public enum RuoloAllegatoAPIImpl {
ALLEGATO("allegato"),
  SPECIFICASEMIFORMALE("specificaSemiFormale"),
  SPECIFICASICUREZZA("specificaSicurezza"),
  SPECIFICALIVELLOSERVIZIO("specificaLivelloServizio");

  private String value;

  RuoloAllegatoAPIImpl(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static RuoloAllegatoAPIImpl fromValue(String text) {
    for (RuoloAllegatoAPIImpl b : RuoloAllegatoAPIImpl.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
