/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.web.monitor.statistiche.constants;

import org.openspcoop2.web.monitor.core.constants.Costanti;

/**
 * StatisticheCostanti
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class StatisticheCostanti {
	
	private StatisticheCostanti() {}
	
	// Export 
	public static final String PARAMETER_IDS = "ids";
	public static final String PARAMETER_IS_ALL = "isAll";
	public static final String PARAMETER_RUOLO = "ruolo";
	
	public static final String PARAMETER_IDS_ORIGINALI = "idsOriginali";
	public static final String PARAMETER_IS_ALL_ORIGINALE = "isAllOriginale";
	public static final String PARAMETER_RUOLO_ORIGINALE = "ruoloOriginale";
	
	public static final String STATISTICHE_PDND_EXPORTER_SERVLET_NAME = "tracingpdndexporter";

	public static final String NON_SELEZIONATO = "--"; 

	/* CHIAVI PROPERTIES DA FILE messages_it.properties */ 
	
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_DISTRIBUZIONE_LABEL_KEY = Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_DISTRIBUZIONE_LABEL_KEY;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_PERSONALIZZATA_LABEL_KEY = Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_PERSONALIZZATA_LABEL_KEY;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKEN_INFO_LABEL_KEY = Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKEN_INFO_LABEL_KEY;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_IDENTIFICATIVO_AUTENTICATO_LABEL_KEY = Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_IDENTIFICATIVO_AUTENTICATO_LABEL_KEY;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_INDIRIZZO_IP_LABEL_KEY = Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_INDIRIZZO_IP_LABEL_KEY;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_APPLICATIVO_LABEL_KEY = Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_APPLICATIVO_LABEL_KEY;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_ERRORI_LABEL_KEY = Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_ERRORI_LABEL_KEY;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_AZIONE_LABEL_KEY = Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_AZIONE_LABEL_KEY;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SERVIZIO_LABEL_KEY = Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SERVIZIO_LABEL_KEY;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE_LABEL_KEY = Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE_LABEL_KEY;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO_LABEL_KEY = Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO_LABEL_KEY;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_ESITI_LABEL_KEY = Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_ESITI_LABEL_KEY;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TEMPORALE_LABEL_KEY = Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TEMPORALE_LABEL_KEY;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_MITTENTE_LABEL_KEY = Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_MITTENTE_LABEL_KEY;
	
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKEN_INFO_ISSUER_LABEL_KEY = "stats.analisiStatistica.tipoDistribuzione.tokenInfo.issuer.label";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKEN_INFO_CLIENT_ID_LABEL_KEY = "stats.analisiStatistica.tipoDistribuzione.tokenInfo.clientID.label";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKEN_INFO_SUBJECT_LABEL_KEY = "stats.analisiStatistica.tipoDistribuzione.tokenInfo.subject.label";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKEN_INFO_USERNAME_LABEL_KEY = "stats.analisiStatistica.tipoDistribuzione.tokenInfo.username.label";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKEN_INFO_EMAIL_LABEL_KEY = "stats.analisiStatistica.tipoDistribuzione.tokenInfo.email.label";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKEN_INFO_PDND_ORGANIZATION_NAME_LABEL_KEY = "stats.analisiStatistica.tipoDistribuzione.tokenInfo.pdndOrganizationName.label";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKEN_INFO_PDND_ORGANIZATION_EXTERNAL_ID_LABEL_KEY = "stats.analisiStatistica.tipoDistribuzione.tokenInfo.pdndOrganizationExternalId.label";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKEN_INFO_PDND_ORGANIZATION_CONSUMER_ID_LABEL_KEY = "stats.analisiStatistica.tipoDistribuzione.tokenInfo.pdndOrganizationConsumerId.label";
	
	/**public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TEMPORALE_LABEL_SHORT_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TEMPORALE_LABEL_KEY + ".short";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_ESITI_LABEL_SHORT_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_ESITI_LABEL_KEY + ".short";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_ERRORI_LABEL_SHORT_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_ERRORI_LABEL_KEY + ".short";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_AZIONE_LABEL_SHORT_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_AZIONE_LABEL_KEY + ".short";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_MITTENTE_SERVIZIO_APPLICATIVO_LABEL_SHORT_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_APPLICATIVO_LABEL_KEY + ".short";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_MITTENTE_IDENTIFICATIVO_AUTENTICATO_SHORT_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_IDENTIFICATIVO_AUTENTICATO_LABEL_KEY + ".short";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_MITTENTE_INDIRIZZO_IP_SHORT_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_INDIRIZZO_IP_LABEL_KEY + ".short";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_MITTENTE_TOKEN_INFO_SHORT_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKEN_INFO_LABEL_KEY + ".short";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SERVIZIO_LABEL_SHORT_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SERVIZIO_LABEL_KEY + ".short";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO_LABEL_SHORT_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO_LABEL_KEY + ".short";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE_LABEL_SHORT_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE_LABEL_KEY + ".short";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_PERSONALIZZATA_LABEL_SHORT_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_PERSONALIZZATA_LABEL_KEY + ".short";*/
	
	private static final String SUFFIX = ".suffix";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_ERRORI_LABEL_SUFFIX_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_ERRORI_LABEL_KEY + SUFFIX;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_AZIONE_LABEL_SUFFIX_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_AZIONE_LABEL_KEY + SUFFIX;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_MITTENTE_LABEL_SUFFIX_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_MITTENTE_LABEL_KEY + SUFFIX;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_MITTENTE_LABEL_SUFFIX_AND_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_MITTENTE_LABEL_KEY + ".suffixAnd";
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_MITTENTE_SERVIZIO_APPLICATIVO_LABEL_SUFFIX_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_APPLICATIVO_LABEL_KEY + SUFFIX;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_MITTENTE_IDENTIFICATIVO_AUTENTICATO_SUFFIX_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_IDENTIFICATIVO_AUTENTICATO_LABEL_KEY + SUFFIX;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_MITTENTE_INDIRIZZO_IP_SUFFIX_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_INDIRIZZO_IP_LABEL_KEY + SUFFIX;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_MITTENTE_TOKEN_INFO_SUFFIX_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKEN_INFO_LABEL_KEY + SUFFIX;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SERVIZIO_LABEL_SUFFIX_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SERVIZIO_LABEL_KEY + SUFFIX;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SERVIZIO_LABEL_EROGAZIONE_SUFFIX_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SERVIZIO_LABEL_KEY + ".erogazione" + SUFFIX;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SERVIZIO_LABEL_FRUIZIONE_SUFFIX_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SERVIZIO_LABEL_KEY + ".fruizione" + SUFFIX;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO_LABEL_SUFFIX_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO_LABEL_KEY + SUFFIX;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE_LABEL_SUFFIX_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE_LABEL_KEY + SUFFIX;
	public static final String STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_PERSONALIZZATA_LABEL_SUFFIX_KEY = STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_PERSONALIZZATA_LABEL_KEY + SUFFIX;
	
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_EROGATORE_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.tooltip.nosvg.erogatore.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_SERVIZIO_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.tooltip.nosvg.servizio.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_ERRORI_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.tooltip.nosvg.errori.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_ERRORI_DESCRIZIONE_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.tooltip.nosvg.errori.descrizione.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_AZIONE_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.tooltip.nosvg.azione.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_SOGGETTO_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.tooltip.nosvg.soggetto.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_APPLICATIVO_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.tooltip.nosvg.sa.applicativo.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_IDENTIFICATIVO_AUTENTICATO_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.tooltip.nosvg.sa.identificativoAutenticato.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_IDENTIFICATIVO_AUTENTICATO_AUTENTICAZIONE_KEY = "stats.analisiStatistica.tipoDistribuzione.tooltip.nosvg.sa.identificativoAutenticato.autenticazione";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_INDIRIZZO_IP_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.tooltip.nosvg.sa.indirizzoIP.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_TOKENINFO_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.tooltip.nosvg.sa.tokenInfo.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_TOKENINFO_ISSUER_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.tooltip.nosvg.sa.tokenInfo.issuer.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_TOKENINFO_CLIENTID_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.tooltip.nosvg.sa.tokenInfo.clientID.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_TOKENINFO_SUBJECT_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.tooltip.nosvg.sa.tokenInfo.subject.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_TOKENINFO_USERNAME_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.tooltip.nosvg.sa.tokenInfo.username.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOOLTIP_NOSVG_TOKENINFO_EMAIL_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.tooltip.nosvg.sa.tokenInfo.email.pattern";
	
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SERVIZIO_TOOLTIP_SVG_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.servizio.tooltip.svg.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_ERRORI_TOOLTIP_SVG_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.errori.tooltip.svg.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_AZIONE_TOOLTIP_SVG_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.azione.tooltip.svg.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO_TOOLTIP_SVG_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.applicativo.tooltip.svg.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO_CLIENT_ID_TOOLTIP_SVG_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.applicativo.clientId.tooltip.svg.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_IDENTIFICATIVO_AUTENTICATO_TOOLTIP_SVG_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.identificativoAutenticato.tooltip.svg.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_INDIRIZZO_IP_TOOLTIP_SVG_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.indirizzoIP.tooltip.svg.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKENINFO_ISSUER_TOOLTIP_SVG_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.tokenInfo.issuer.tooltip.svg.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKENINFO_CLIENTID_TOOLTIP_SVG_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.tokenInfo.clientID.tooltip.svg.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKENINFO_CLIENTID_APPLICATIVO_TOOLTIP_SVG_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.tokenInfo.clientID.applicativo.tooltip.svg.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKENINFO_SUBJECT_TOOLTIP_SVG_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.tokenInfo.subject.tooltip.svg.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKENINFO_USERNAME_TOOLTIP_SVG_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.tokenInfo.username.tooltip.svg.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKENINFO_EMAIL_TOOLTIP_SVG_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.tokenInfo.email.tooltip.svg.pattern";
	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKENINFO_PDND_ORGANIZATION_NAME_TOOLTIP_SVG_PATTERN_KEY = "stats.analisiStatistica.tipoDistribuzione.tokenInfo.pdndOrganizationName.tooltip.svg.pattern";

	public static final String  STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TOKEN_INFO_CLIENTID_PDNDINFO = "stats.search.claim.clientIdConPDND";
	
	public static final String  STATS_ANALISI_STATISTICA_HEATMAP_TOOLTIP_CATEGORIA_ALTRI_PATTERN_KEY = "stats.analisiStatistica.heatmap.tooltip.categoriaAltri.pattern";
	
	public static final String[] SEARCH_FORM_FIELDS_DA_NON_SALVARE= {
			"_value_tipoReport",
			"opened",
			"_value_modalitaTemporale",
			"_value_tipoVisualizzazione",
			"_value_tipoBanda",
			"_value_tipoLatenza",
			"_value_tipoStatistica",
			"_value_numeroDimensioni",
			"_value_numeroDimensioniCustom",
			"distribuzionePerSoggettoRemota",
			"andamentoTemporalePerEsiti",
			"isMostraUnitaTempoDistribuzioneNonTemporale",
			"isMostraUnitaTempoDistribuzioneNonTemporale_periodoPersonalizzato",
			"andamentoTemporalePerEsiti",
			"statisticheLatenzaPortaEnabled",
			"isCloned",
			"action",
			"periodoDellaRicerca",
	};
	
	public static final String[] STATISTICHE_PERSONALIZZATE_SEARCH_FORM_FIELDS_DA_NON_SALVARE= {
			"filtroReport",
			"service",
			"statisticaSelezionataParameters",
			"tabellaStatistichePersonalizzate",
			"mBean",
	};

	public static final String STATS_PDND_TRACING_NOME_ACTION_RICERCA = "statistichePdndTracingList";
	
	public static final String STATS_PDND_TRACING_SEARCH_TIPO_RICERCA_TEMPORALE_LABEL_KEY = "statistichePdndTracing.search.tipoRicerca.temporale.label";
	public static final String STATS_PDND_TRACING_SEARCH_TIPO_RICERCA_ID_LABEL_KEY = "statistichePdndTracing.search.tipoRicerca.id.label";
	
	public static final String STATS_PDND_TRACING_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_BASE_LABEL_KEY = "statistichePdndTracing.search.tipoRicerca.temporale.ricercaTemporale.label"; 
	public static final String STATS_PDND_TRACING_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_BASE_ICON_KEY = "statistichePdndTracing.search.tipoRicerca.temporale.ricercaTemporale.icona"; 
	public static final String STATS_PDND_TRACING_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_BASE_BREADCUMP_KEY = "statistichePdndTracing.search.tipoRicerca.temporale.ricercaTemporale.breadcrumb"; 
	
	public static final String STATS_PDND_TRACING_SEARCH_TIPO_RICERCA_ID_RICERCA_TRACING_ID_LABEL_KEY = "statistichePdndTracing.search.tipoRicerca.id.tracingId.label"; 
	public static final String STATS_PDND_TRACING_SEARCH_TIPO_RICERCA_ID_RICERCA_TRACING_ID_ICON_KEY = "statistichePdndTracing.search.tipoRicerca.id.tracingId.icona"; 
	public static final String STATS_PDND_TRACING_SEARCH_TIPO_RICERCA_ID_RICERCA_TRACING_ID_BREADCUMP_KEY = "statistichePdndTracing.search.tipoRicerca.id.tracingId.breadcrumb"; 
	
	public static final String STATS_PDND_TRACING_STATO_PUBLISHED_LABEL_KEY = "statistichePdndTracing.stato.PUBLISHED.label";
	public static final String STATS_PDND_TRACING_STATO_FAILED_LABEL_KEY = "statistichePdndTracing.stato.FAILED.label";
	public static final String STATS_PDND_TRACING_STATO_IN_ATTESA_LABEL_KEY = "statistichePdndTracing.stato.IN_ATTESA.label";
	public static final String STATS_PDND_TRACING_STATO_PDND_WAITING_LABEL_KEY= "statistichePdndTracing.statoPdnd.WAITING.label";
	public static final String STATS_PDND_TRACING_STATO_PDND_PENDING_LABEL_KEY = "statistichePdndTracing.statoPdnd.PENDING.label";
	public static final String STATS_PDND_TRACING_STATO_PDND_OK_LABEL_KEY = "statistichePdndTracing.statoPdnd.OK.label";
	public static final String STATS_PDND_TRACING_STATO_PDND_ERROR_LABEL_KEY = "statistichePdndTracing.statoPdnd.ERROR.label";
	public static final String STATS_PDND_TRACING_METHOD_RECOVER_LABEL_KEY = "statistichePdndTracing.method.RECOVER.label";
	public static final String STATS_PDND_TRACING_METHOD_REPLACE_LABEL_KEY = "statistichePdndTracing.method.REPLACE.label";
	public static final String STATS_PDND_TRACING_METHOD_SUBMIT_LABEL_KEY = "statistichePdndTracing.method.SUBMIT.label";
	
	public static final String STATS_PDND_TRACING_MISSING_PARAMETERS_TRACING_ID_LABEL_KEY = "statistichePdndTracing.search.missing_parameters.tracingId";
	
	public static final String STATS_PDND_TRACING_STATO_IN_ATTESA_VALUE = "IN_ATTESA";

}
