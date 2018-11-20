package org.openspcoop2.core.monitor.rs.server.model;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FiltroServizio  {
@XmlType(name="ProtocolloEnum")
@XmlEnum(String.class)
public enum ProtocolloEnum {

@XmlEnumValue("spcoop") SPCOOP(String.valueOf("spcoop")), @XmlEnumValue("trasparente") TRASPARENTE(String.valueOf("trasparente")), @XmlEnumValue("sdi") SDI(String.valueOf("sdi")), @XmlEnumValue("edelivery") EDELIVERY(String.valueOf("edelivery"));


    private String value;

    ProtocolloEnum (String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static ProtocolloEnum fromValue(String v) {
        for (ProtocolloEnum b : ProtocolloEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}
  
  @Schema(example = "trasparente", description = "")
  private ProtocolloEnum protocollo = null;
  
  @Schema(example = "EnteInterno", description = "")
  private String soggettoLocale = null;
  
  @Schema(example = "EnteEsterno", description = "")
  private String soggettoRemoto = null;
  
  @Schema(example = "applicativo1", description = "")
  private String applicativo = null;
  
  @Schema(example = "servizio1", description = "")
  private String servizio = null;
  
  @Schema(example = "azione1", description = "")
  private String azione = null;
 /**
   * Get protocollo
   * @return protocollo
  **/
  @JsonProperty("protocollo")
  public String getProtocollo() {
    if (this.protocollo == null) {
      return null;
    }
    return this.protocollo.value();
  }

  public void setProtocollo(ProtocolloEnum protocollo) {
    this.protocollo = protocollo;
  }

  public FiltroServizio protocollo(ProtocolloEnum protocollo) {
    this.protocollo = protocollo;
    return this;
  }

 /**
   * Get soggettoLocale
   * @return soggettoLocale
  **/
  @JsonProperty("soggettoLocale")
  public String getSoggettoLocale() {
    return this.soggettoLocale;
  }

  public void setSoggettoLocale(String soggettoLocale) {
    this.soggettoLocale = soggettoLocale;
  }

  public FiltroServizio soggettoLocale(String soggettoLocale) {
    this.soggettoLocale = soggettoLocale;
    return this;
  }

 /**
   * Get soggettoRemoto
   * @return soggettoRemoto
  **/
  @JsonProperty("soggettoRemoto")
  public String getSoggettoRemoto() {
    return this.soggettoRemoto;
  }

  public void setSoggettoRemoto(String soggettoRemoto) {
    this.soggettoRemoto = soggettoRemoto;
  }

  public FiltroServizio soggettoRemoto(String soggettoRemoto) {
    this.soggettoRemoto = soggettoRemoto;
    return this;
  }

 /**
   * Get applicativo
   * @return applicativo
  **/
  @JsonProperty("applicativo")
  public String getApplicativo() {
    return this.applicativo;
  }

  public void setApplicativo(String applicativo) {
    this.applicativo = applicativo;
  }

  public FiltroServizio applicativo(String applicativo) {
    this.applicativo = applicativo;
    return this;
  }

 /**
   * Get servizio
   * @return servizio
  **/
  @JsonProperty("servizio")
  public String getServizio() {
    return this.servizio;
  }

  public void setServizio(String servizio) {
    this.servizio = servizio;
  }

  public FiltroServizio servizio(String servizio) {
    this.servizio = servizio;
    return this;
  }

 /**
   * Get azione
   * @return azione
  **/
  @JsonProperty("azione")
  public String getAzione() {
    return this.azione;
  }

  public void setAzione(String azione) {
    this.azione = azione;
  }

  public FiltroServizio azione(String azione) {
    this.azione = azione;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FiltroServizio {\n");
    
    sb.append("    protocollo: ").append(FiltroServizio.toIndentedString(this.protocollo)).append("\n");
    sb.append("    soggettoLocale: ").append(FiltroServizio.toIndentedString(this.soggettoLocale)).append("\n");
    sb.append("    soggettoRemoto: ").append(FiltroServizio.toIndentedString(this.soggettoRemoto)).append("\n");
    sb.append("    applicativo: ").append(FiltroServizio.toIndentedString(this.applicativo)).append("\n");
    sb.append("    servizio: ").append(FiltroServizio.toIndentedString(this.servizio)).append("\n");
    sb.append("    azione: ").append(FiltroServizio.toIndentedString(this.azione)).append("\n");
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
