package org.openspcoop2.core.monitor.rs.server.model;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TransazioneSearchFilter  {
@XmlType(name="TipoEnum")
@XmlEnum(String.class)
public enum TipoEnum {

@XmlEnumValue("intervalloTemporale") INTERVALLOTEMPORALE(String.valueOf("intervalloTemporale")), @XmlEnumValue("identificativoApplicativo") IDENTIFICATIVOAPPLICATIVO(String.valueOf("identificativoApplicativo")), @XmlEnumValue("identificativoMessaggio") IDENTIFICATIVOMESSAGGIO(String.valueOf("identificativoMessaggio")), @XmlEnumValue("identificativoTransazione") IDENTIFICATIVOTRANSAZIONE(String.valueOf("identificativoTransazione"));


    private String value;

    TipoEnum (String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static TipoEnum fromValue(String v) {
        for (TipoEnum b : TipoEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}
  
  @Schema(example = "intervalloTemporale", required = true, description = "")
  private TipoEnum tipo = null;
  
  @Schema(description = "")
  private Object filtro = null;
 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @NotNull
  public String getTipo() {
    if (this.tipo == null) {
      return null;
    }
    return this.tipo.value();
  }

  public void setTipo(TipoEnum tipo) {
    this.tipo = tipo;
  }

  public TransazioneSearchFilter tipo(TipoEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get filtro
   * @return filtro
  **/
  @JsonProperty("filtro")
  public Object getFiltro() {
    return this.filtro;
  }

  public void setFiltro(Object filtro) {
    this.filtro = filtro;
  }

  public TransazioneSearchFilter filtro(Object filtro) {
    this.filtro = filtro;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneSearchFilter {\n");
    
    sb.append("    tipo: ").append(TransazioneSearchFilter.toIndentedString(this.tipo)).append("\n");
    sb.append("    filtro: ").append(TransazioneSearchFilter.toIndentedString(this.filtro)).append("\n");
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
