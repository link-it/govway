package org.openspcoop2.core.monitor.rs.server.model;

import org.joda.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.Valid;

public class GenerazioneReport  {
  public enum TipoReportEnum {
    TEMPORALE("Distribuzione Temporale"),
    PER_ESITI("Distribuzione per Esiti"),
    PER_SOGGETTO_REMOTO("Distribuzione per Soggetto Remoto"),
    PER_SOGGETTO_LOCALE("Distribuzione per Soggetto Locale"),
    PER_SERVIZIO("Distribuzione per Servizio"),
    PER_AZIONE("Distribuzione per Azione"),
    PER_SERVIZIO_APPLICATIVO("Distribuzione per Servizio Applicativo"),
    PERSONALIZZATA("Distribuzione Personalizzata");

    private String value;

    TipoReportEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return this.value;
    }

    @Override
    public String toString() {
      return String.valueOf(this.value);
    }
    @JsonCreator
    public static TipoReportEnum fromValue(String text) {
      for (TipoReportEnum b : TipoReportEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(example = "Distribuzione Temporale", description = "")
  private TipoReportEnum tipoReport = null;
  
  @Schema(description = "")
  private LocalDate dataDa = null;
  
  @Schema(description = "")
  private LocalDate dataA = null;
  public enum ProtocolloEnum {
    SPCOOP("spcoop"),
    TRASPARENTE("trasparente"),
    SDI("sdi"),
    EDELIVERY("edelivery");

    private String value;

    ProtocolloEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return this.value;
    }

    @Override
    public String toString() {
      return String.valueOf(this.value);
    }
    @JsonCreator
    public static ProtocolloEnum fromValue(String text) {
      for (ProtocolloEnum b : ProtocolloEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
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
  
  @Schema(example = "servizio1", description = "")
  private String servizio = null;
  
  @Schema(example = "azione1", description = "")
  private String azione = null;
  public enum TipologiaEnum {
    FRUZIONE("fruzione"),
    EROGAZIONE("erogazione");

    private String value;

    TipologiaEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return this.value;
    }

    @Override
    public String toString() {
      return String.valueOf(this.value);
    }
    @JsonCreator
    public static TipologiaEnum fromValue(String text) {
      for (TipologiaEnum b : TipologiaEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(example = "fruizione", description = "")
  private TipologiaEnum tipologia = null;
  public enum EsitoEnum {
    FALLITE("fallite"),
    FAULT_APPLICATIVO("fault applicativo"),
    COMPLETATE_CON_SUCCESSO("completate con successo");

    private String value;

    EsitoEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return this.value;
    }

    @Override
    public String toString() {
      return String.valueOf(this.value);
    }
    @JsonCreator
    public static EsitoEnum fromValue(String text) {
      for (EsitoEnum b : EsitoEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(example = "fallite", description = "")
  private EsitoEnum esito = null;
  public enum DettaglioEsitoEnum {
    OK("ok"),
    OK_PRESENZA_ANOMALIE("ok (presenza anomalie"),
    FAULT_APPLICATIVO("fault applicativo"),
    AUTENTICAZIONE_FALLITA("autenticazione fallita"),
    AUTORIZZAZIONE_NEGATA("autorizzazione negata"),
    RICHIESTA_CLIENT_RIFIUTATA("richiesta client rifiutata"),
    ERRORE_DI_CONNESSIONE("errore di connessione"),
    ERRORE_DI_PROTOCOLLO("errore di protocollo"),
    VIOLAZIONE_RATE_LIMITING("violazione rate limiting"),
    VIOLAZIONE_RATE_LIMITING_WARNINGONLY("violazione rate limiting warningOnly"),
    SUPERAMENTO_LIMITE_RICHIESTE("superamento limite richieste"),
    SUPERAMENTO_LIMITE_RICHIESTE_WARNINGONLY("superamento limite richieste warningOnly"),
    FAULT_PDD_ESTERNA("fault pdd esterna"),
    CONTENUTO_RICHIESTA_NON_RICONOSCIUTO("contenuto richiesta non riconosciuto"),
    CONTENUTO_RISPOSTA_NON_RICONOSCIUTO("contenuto risposta non riconosciuto"),
    CONNESSIONE_CLIENT_INTERROTTA("connessione client interrotta"),
    FAULT_GENERATO_DALLA_PDD("fault generato dalla pdd"),
    ERRORE_INTERNO_PDD("errore interno pdd");

    private String value;

    DettaglioEsitoEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return this.value;
    }

    @Override
    public String toString() {
      return String.valueOf(this.value);
    }
    @JsonCreator
    public static DettaglioEsitoEnum fromValue(String text) {
      for (DettaglioEsitoEnum b : DettaglioEsitoEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(example = "ok", description = "")
  private DettaglioEsitoEnum dettaglioEsito = null;
  public enum ContestoEnum {
    APPLICATIVO("applicativo"),
    SISTEMA("sistema");

    private String value;

    ContestoEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return this.value;
    }

    @Override
    public String toString() {
      return String.valueOf(this.value);
    }
    @JsonCreator
    public static ContestoEnum fromValue(String text) {
      for (ContestoEnum b : ContestoEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(example = "applicativo", description = "")
  private ContestoEnum contesto = null;
  public enum TipoGraficoEnum {
    LINE("Line"),
    PIE("Pie"),
    BAR("Bar"),
    TABLE("Table");

    private String value;

    TipoGraficoEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return this.value;
    }

    @Override
    public String toString() {
      return String.valueOf(this.value);
    }
    @JsonCreator
    public static TipoGraficoEnum fromValue(String text) {
      for (TipoGraficoEnum b : TipoGraficoEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(example = "Line", description = "")
  private TipoGraficoEnum tipoGrafico = null;
  public enum UnitaTempoEnum {
    ORARIO("orario"),
    GIORNALIERO("giornaliero"),
    SETTIMANALE("settimanale"),
    MENSILE("mensile");

    private String value;

    UnitaTempoEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return this.value;
    }

    @Override
    public String toString() {
      return String.valueOf(this.value);
    }
    @JsonCreator
    public static UnitaTempoEnum fromValue(String text) {
      for (UnitaTempoEnum b : UnitaTempoEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(example = "giornaliero", description = "")
  private UnitaTempoEnum unitaTempo = null;
  public enum VisualizzaEnum {
    NUMEROTRANSAZIONI("numeroTransazioni"),
    OCCUPAZIONEBANDA("occupazioneBanda"),
    TEMPOMEDIORISPOSTA("TempoMedioRisposta");

    private String value;

    VisualizzaEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return this.value;
    }

    @Override
    public String toString() {
      return String.valueOf(this.value);
    }
    @JsonCreator
    public static VisualizzaEnum fromValue(String text) {
      for (VisualizzaEnum b : VisualizzaEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(example = "numeroTransazioni", description = "")
  private VisualizzaEnum visualizza = null;
 /**
   * Get tipoReport
   * @return tipoReport
  **/
  @JsonProperty("tipoReport")
  @Valid
  public String getTipoReport() {
    if (this.tipoReport == null) {
      return null;
    }
    return this.tipoReport.getValue();
  }

  public void setTipoReport(TipoReportEnum tipoReport) {
    this.tipoReport = tipoReport;
  }

  public GenerazioneReport tipoReport(TipoReportEnum tipoReport) {
    this.tipoReport = tipoReport;
    return this;
  }

 /**
   * Get dataDa
   * @return dataDa
  **/
  @JsonProperty("dataDa")
  @Valid
  public LocalDate getDataDa() {
    return this.dataDa;
  }

  public void setDataDa(LocalDate dataDa) {
    this.dataDa = dataDa;
  }

  public GenerazioneReport dataDa(LocalDate dataDa) {
    this.dataDa = dataDa;
    return this;
  }

 /**
   * Get dataA
   * @return dataA
  **/
  @JsonProperty("dataA")
  @Valid
  public LocalDate getDataA() {
    return this.dataA;
  }

  public void setDataA(LocalDate dataA) {
    this.dataA = dataA;
  }

  public GenerazioneReport dataA(LocalDate dataA) {
    this.dataA = dataA;
    return this;
  }

 /**
   * Get protocollo
   * @return protocollo
  **/
  @JsonProperty("protocollo")
  @Valid
  public String getProtocollo() {
    if (this.protocollo == null) {
      return null;
    }
    return this.protocollo.getValue();
  }

  public void setProtocollo(ProtocolloEnum protocollo) {
    this.protocollo = protocollo;
  }

  public GenerazioneReport protocollo(ProtocolloEnum protocollo) {
    this.protocollo = protocollo;
    return this;
  }

 /**
   * Get soggettoLocale
   * @return soggettoLocale
  **/
  @JsonProperty("soggettoLocale")
  @Valid
  public String getSoggettoLocale() {
    return this.soggettoLocale;
  }

  public void setSoggettoLocale(String soggettoLocale) {
    this.soggettoLocale = soggettoLocale;
  }

  public GenerazioneReport soggettoLocale(String soggettoLocale) {
    this.soggettoLocale = soggettoLocale;
    return this;
  }

 /**
   * Get soggettoRemoto
   * @return soggettoRemoto
  **/
  @JsonProperty("soggettoRemoto")
  @Valid
  public String getSoggettoRemoto() {
    return this.soggettoRemoto;
  }

  public void setSoggettoRemoto(String soggettoRemoto) {
    this.soggettoRemoto = soggettoRemoto;
  }

  public GenerazioneReport soggettoRemoto(String soggettoRemoto) {
    this.soggettoRemoto = soggettoRemoto;
    return this;
  }

 /**
   * Get servizio
   * @return servizio
  **/
  @JsonProperty("servizio")
  @Valid
  public String getServizio() {
    return this.servizio;
  }

  public void setServizio(String servizio) {
    this.servizio = servizio;
  }

  public GenerazioneReport servizio(String servizio) {
    this.servizio = servizio;
    return this;
  }

 /**
   * Get azione
   * @return azione
  **/
  @JsonProperty("azione")
  @Valid
  public String getAzione() {
    return this.azione;
  }

  public void setAzione(String azione) {
    this.azione = azione;
  }

  public GenerazioneReport azione(String azione) {
    this.azione = azione;
    return this;
  }

 /**
   * Get tipologia
   * @return tipologia
  **/
  @JsonProperty("tipologia")
  @Valid
  public String getTipologia() {
    if (this.tipologia == null) {
      return null;
    }
    return this.tipologia.getValue();
  }

  public void setTipologia(TipologiaEnum tipologia) {
    this.tipologia = tipologia;
  }

  public GenerazioneReport tipologia(TipologiaEnum tipologia) {
    this.tipologia = tipologia;
    return this;
  }

 /**
   * Get esito
   * @return esito
  **/
  @JsonProperty("esito")
  @Valid
  public String getEsito() {
    if (this.esito == null) {
      return null;
    }
    return this.esito.getValue();
  }

  public void setEsito(EsitoEnum esito) {
    this.esito = esito;
  }

  public GenerazioneReport esito(EsitoEnum esito) {
    this.esito = esito;
    return this;
  }

 /**
   * Get dettaglioEsito
   * @return dettaglioEsito
  **/
  @JsonProperty("dettaglioEsito")
  @Valid
  public String getDettaglioEsito() {
    if (this.dettaglioEsito == null) {
      return null;
    }
    return this.dettaglioEsito.getValue();
  }

  public void setDettaglioEsito(DettaglioEsitoEnum dettaglioEsito) {
    this.dettaglioEsito = dettaglioEsito;
  }

  public GenerazioneReport dettaglioEsito(DettaglioEsitoEnum dettaglioEsito) {
    this.dettaglioEsito = dettaglioEsito;
    return this;
  }

 /**
   * Get contesto
   * @return contesto
  **/
  @JsonProperty("contesto")
  @Valid
  public String getContesto() {
    if (this.contesto == null) {
      return null;
    }
    return this.contesto.getValue();
  }

  public void setContesto(ContestoEnum contesto) {
    this.contesto = contesto;
  }

  public GenerazioneReport contesto(ContestoEnum contesto) {
    this.contesto = contesto;
    return this;
  }

 /**
   * Get tipoGrafico
   * @return tipoGrafico
  **/
  @JsonProperty("tipoGrafico")
  @Valid
  public String getTipoGrafico() {
    if (this.tipoGrafico == null) {
      return null;
    }
    return this.tipoGrafico.getValue();
  }

  public void setTipoGrafico(TipoGraficoEnum tipoGrafico) {
    this.tipoGrafico = tipoGrafico;
  }

  public GenerazioneReport tipoGrafico(TipoGraficoEnum tipoGrafico) {
    this.tipoGrafico = tipoGrafico;
    return this;
  }

 /**
   * Get unitaTempo
   * @return unitaTempo
  **/
  @JsonProperty("unitaTempo")
  @Valid
  public String getUnitaTempo() {
    if (this.unitaTempo == null) {
      return null;
    }
    return this.unitaTempo.getValue();
  }

  public void setUnitaTempo(UnitaTempoEnum unitaTempo) {
    this.unitaTempo = unitaTempo;
  }

  public GenerazioneReport unitaTempo(UnitaTempoEnum unitaTempo) {
    this.unitaTempo = unitaTempo;
    return this;
  }

 /**
   * Get visualizza
   * @return visualizza
  **/
  @JsonProperty("visualizza")
  @Valid
  public String getVisualizza() {
    if (this.visualizza == null) {
      return null;
    }
    return this.visualizza.getValue();
  }

  public void setVisualizza(VisualizzaEnum visualizza) {
    this.visualizza = visualizza;
  }

  public GenerazioneReport visualizza(VisualizzaEnum visualizza) {
    this.visualizza = visualizza;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GenerazioneReport {\n");
    
    sb.append("    tipoReport: ").append(GenerazioneReport.toIndentedString(this.tipoReport)).append("\n");
    sb.append("    dataDa: ").append(GenerazioneReport.toIndentedString(this.dataDa)).append("\n");
    sb.append("    dataA: ").append(GenerazioneReport.toIndentedString(this.dataA)).append("\n");
    sb.append("    protocollo: ").append(GenerazioneReport.toIndentedString(this.protocollo)).append("\n");
    sb.append("    soggettoLocale: ").append(GenerazioneReport.toIndentedString(this.soggettoLocale)).append("\n");
    sb.append("    soggettoRemoto: ").append(GenerazioneReport.toIndentedString(this.soggettoRemoto)).append("\n");
    sb.append("    servizio: ").append(GenerazioneReport.toIndentedString(this.servizio)).append("\n");
    sb.append("    azione: ").append(GenerazioneReport.toIndentedString(this.azione)).append("\n");
    sb.append("    tipologia: ").append(GenerazioneReport.toIndentedString(this.tipologia)).append("\n");
    sb.append("    esito: ").append(GenerazioneReport.toIndentedString(this.esito)).append("\n");
    sb.append("    dettaglioEsito: ").append(GenerazioneReport.toIndentedString(this.dettaglioEsito)).append("\n");
    sb.append("    contesto: ").append(GenerazioneReport.toIndentedString(this.contesto)).append("\n");
    sb.append("    tipoGrafico: ").append(GenerazioneReport.toIndentedString(this.tipoGrafico)).append("\n");
    sb.append("    unitaTempo: ").append(GenerazioneReport.toIndentedString(this.unitaTempo)).append("\n");
    sb.append("    visualizza: ").append(GenerazioneReport.toIndentedString(this.visualizza)).append("\n");
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
