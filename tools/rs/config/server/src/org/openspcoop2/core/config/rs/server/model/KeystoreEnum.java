package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets KeystoreEnum
 */
public enum KeystoreEnum {
JKS("jks"),
  PKCS12("pkcs12"),
  JCEKS("jceks"),
  BKS("bks"),
  UBER("uber"),
  GKR("gkr");

  private String value;

  KeystoreEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static KeystoreEnum fromValue(String text) {
    for (KeystoreEnum b : KeystoreEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
