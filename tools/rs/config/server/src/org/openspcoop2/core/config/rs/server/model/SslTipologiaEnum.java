package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets SslTipologiaEnum
 */
public enum SslTipologiaEnum {
SSLV2HELLO("SSLv2Hello"),
  SSLV3("SSLv3"),
  TLSV1("TLSv1"),
  TLSV1_1("TLSv1.1"),
  TLSV1_2("TLSv1.2");

  private String value;

  SslTipologiaEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static SslTipologiaEnum fromValue(String text) {
    for (SslTipologiaEnum b : SslTipologiaEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
