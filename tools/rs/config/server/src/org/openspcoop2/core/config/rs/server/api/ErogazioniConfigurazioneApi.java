/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.rs.server.api;

import org.openspcoop2.core.config.rs.server.model.ApiCanale;
import org.openspcoop2.core.config.rs.server.model.ApiImplStato;
import org.openspcoop2.core.config.rs.server.model.CachingRisposta;
import org.openspcoop2.core.config.rs.server.model.ConfigurazioneApiCanale;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutenticazione;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazione;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneRuoli;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneRuolo;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneScope;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneScopes;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneSoggetti;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneSoggetto;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneView;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiErogazioneAutorizzazioneApplicativi;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiErogazioneAutorizzazioneApplicativo;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiGestioneToken;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiIdentificazioneAttributi;
import org.openspcoop2.core.config.rs.server.model.CorrelazioneApplicativaRichiesta;
import org.openspcoop2.core.config.rs.server.model.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.rs.server.model.ElencoProprieta;
import java.io.File;
import org.openspcoop2.core.config.rs.server.model.GestioneCors;
import org.openspcoop2.core.config.rs.server.model.ListaCorrelazioneApplicativaRichiesta;
import org.openspcoop2.core.config.rs.server.model.ListaCorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.rs.server.model.ListaRateLimitingPolicy;
import org.openspcoop2.core.config.rs.server.model.Problem;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.core.config.rs.server.model.Proprieta;
import org.openspcoop2.core.config.rs.server.model.RateLimitingCriteriMetricaEnum;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyErogazione;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyErogazioneUpdate;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyErogazioneView;
import org.openspcoop2.core.config.rs.server.model.RegistrazioneMessaggi;
import org.openspcoop2.core.config.rs.server.model.Validazione;

import jakarta.ws.rs.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

/**
 * GovWay Config API
 *
 * <p>Servizi per la configurazione di GovWay
 *
 */
@Path("/")
public interface ErogazioniConfigurazioneApi  {

    /**
     * Aggiunta di applicativi all'elenco degli applicativi autorizzati
     *
     * Questa operazione consente di aggiungere applicativi all'elenco degli applicativi autorizzati
     *
     */
    @POST
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/applicativi")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Aggiunta di applicativi all'elenco degli applicativi autorizzati", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void addErogazioneControlloAccessiAutorizzazioneApplicativi(@Valid ControlloAccessiErogazioneAutorizzazioneApplicativo body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Aggiunta di applicativi token all'elenco degli applicativi autorizzati
     *
     * Questa operazione consente di aggiungere applicativi token all'elenco degli applicativi autorizzati
     *
     */
    @POST
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/token/applicativi")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Aggiunta di applicativi token all'elenco degli applicativi autorizzati", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void addErogazioneControlloAccessiAutorizzazioneApplicativiToken(@Valid ControlloAccessiErogazioneAutorizzazioneApplicativo body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Aggiunta di ruoli all'elenco dei ruoli autorizzati
     *
     * Questa operazione consente di aggiungere ruoli all'elenco dei ruoli autorizzati
     *
     */
    @POST
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/ruoli")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Aggiunta di ruoli all'elenco dei ruoli autorizzati", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void addErogazioneControlloAccessiAutorizzazioneRuoli(@Valid ControlloAccessiAutorizzazioneRuolo body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Aggiunta di ruoli all'elenco dei ruoli token autorizzati
     *
     * Questa operazione consente di aggiungere ruoli all'elenco dei ruoli token autorizzati
     *
     */
    @POST
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/token/ruoli")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Aggiunta di ruoli all'elenco dei ruoli token autorizzati", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void addErogazioneControlloAccessiAutorizzazioneRuoliToken(@Valid ControlloAccessiAutorizzazioneRuolo body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Aggiunta di scope all'elenco degli scope autorizzati
     *
     * Questa operazione consente di aggiungere scope all'elenco degli scope autorizzati
     *
     */
    @POST
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/scope")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Aggiunta di scope all'elenco degli scope autorizzati", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void addErogazioneControlloAccessiAutorizzazioneScope(@Valid ControlloAccessiAutorizzazioneScope body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Aggiunta di soggetti all'elenco dei soggetti autorizzati
     *
     * Questa operazione consente di aggiungere soggetti all'elenco dei soggetti autorizzati
     *
     */
    @POST
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/soggetti")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Aggiunta di soggetti all'elenco dei soggetti autorizzati", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void addErogazioneControlloAccessiAutorizzazioneSoggetti(@Valid ControlloAccessiAutorizzazioneSoggetto body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Aggiunta di una proprietà di configurazione
     *
     * Questa operazione consente di registrare una proprietà di configurazione
     *
     */
    @POST
    @Path("/erogazioni/{nome}/{versione}/configurazioni/proprieta")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Aggiunta di una proprietà di configurazione", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void addErogazioneProprieta(@Valid Proprieta body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Aggiunta di una policy di rate limiting
     *
     * Questa operazione consente di aggiungere una policy di rate limiting
     *
     */
    @POST
    @Path("/erogazioni/{nome}/{versione}/configurazioni/rate-limiting")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Aggiunta di una policy di rate limiting", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void addErogazioneRateLimitingPolicy(@Valid RateLimitingPolicyErogazione body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Aggiunta di una regola di correlazione applicativa
     *
     * Questa operazione consente di registrare una regola di correlazione applicativa per la richiesta
     *
     */
    @POST
    @Path("/erogazioni/{nome}/{versione}/configurazioni/tracciamento/correlazione-applicativa/richiesta")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Aggiunta di una regola di correlazione applicativa", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void addErogazioneTracciamentoCorrelazioneApplicativaRichiesta(@Valid CorrelazioneApplicativaRichiesta body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Aggiunta di una regola di correlazione applicativa
     *
     * Questa operazione consente di registrare una regola di correlazione applicativa per la risposta
     *
     */
    @POST
    @Path("/erogazioni/{nome}/{versione}/configurazioni/tracciamento/correlazione-applicativa/risposta")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Aggiunta di una regola di correlazione applicativa", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void addErogazioneTracciamentoCorrelazioneApplicativaRisposta(@Valid CorrelazioneApplicativaRisposta body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Elimina applicativi dall'elenco degli applicativi autorizzati
     *
     * Questa operazione consente di eliminare applicativi dall'elenco degli applicativi autorizzati
     *
     */
    @DELETE
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/applicativi/{applicativo_autorizzato}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina applicativi dall'elenco degli applicativi autorizzati", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Applicativi eliminati con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void deleteErogazioneControlloAccessiAutorizzazioneApplicativi(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("applicativo_autorizzato") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String applicativoAutorizzato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("soggetto_applicativo") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggettoApplicativo, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Elimina applicativi dall'elenco degli applicativi token autorizzati
     *
     * Questa operazione consente di eliminare applicativi token dall'elenco degli applicativi autorizzati
     *
     */
    @DELETE
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/token/applicativi/{applicativo_autorizzato}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina applicativi dall'elenco degli applicativi token autorizzati", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Applicativi Token eliminati con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void deleteErogazioneControlloAccessiAutorizzazioneApplicativiToken(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("applicativo_autorizzato") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String applicativoAutorizzato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("soggetto_applicativo") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggettoApplicativo, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Elimina ruoli dall'elenco dei ruoli autorizzati
     *
     * Questa operazione consente di eliminare ruoli dall'elenco dei ruoli autorizzati
     *
     */
    @DELETE
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/ruoli/{ruolo_autorizzato}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina ruoli dall'elenco dei ruoli autorizzati", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Ruoli eliminati con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void deleteErogazioneControlloAccessiAutorizzazioneRuoli(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("ruolo_autorizzato") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String ruoloAutorizzato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Elimina ruoli dall'elenco dei ruoli token autorizzati
     *
     * Questa operazione consente di eliminare ruoli dall'elenco dei ruoli token autorizzati
     *
     */
    @DELETE
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/token/ruoli/{ruolo_autorizzato}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina ruoli dall'elenco dei ruoli token autorizzati", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Ruoli eliminati con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void deleteErogazioneControlloAccessiAutorizzazioneRuoliToken(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("ruolo_autorizzato") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String ruoloAutorizzato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Elimina scope dall'elenco degli scope autorizzati
     *
     * Questa operazione consente di eliminare scope dall'elenco degli scope autorizzati
     *
     */
    @DELETE
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/scope/{scope_autorizzato}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina scope dall'elenco degli scope autorizzati", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Scope eliminati con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void deleteErogazioneControlloAccessiAutorizzazioneScope(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("scope_autorizzato") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String scopeAutorizzato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Elimina soggetti all'elenco dei soggetti autorizzati
     *
     * Questa operazione consente di eliminare soggetti all'elenco dei soggetti autorizzati
     *
     */
    @DELETE
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/soggetti/{soggetto_autorizzato}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina soggetti all'elenco dei soggetti autorizzati", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Soggetti eliminati con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void deleteErogazioneControlloAccessiAutorizzazioneSoggetti(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("soggetto_autorizzato") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggettoAutorizzato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Elimina la proprietà di configurazione dall'elenco di quelle attivate
     *
     * Questa operazione consente di eliminare la proprietà di configurazione dall'elenco di quelle attivate
     *
     */
    @DELETE
    @Path("/erogazioni/{nome}/{versione}/configurazioni/proprieta/{proprieta}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina la proprietà di configurazione dall'elenco di quelle attivate", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Proprietà di configurazione eliminata con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void deleteErogazioneProprietaConfigurazione(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("proprieta") @Size(max=255) String proprieta, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Elimina la policy dall'elenco delle policies attive
     *
     * Questa operazione consente di eliminare la policy dall'elenco delle policies attive
     *
     */
    @DELETE
    @Path("/erogazioni/{nome}/{versione}/configurazioni/rate-limiting/{id_policy}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina la policy dall'elenco delle policies attive", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Policy eliminata con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void deleteErogazioneRateLimitingPolicy(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("id_policy") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String idPolicy, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Elimina la regola di correlazione applicativa dall'elenco di quelle attivate per la richiesta
     *
     * Questa operazione consente di eliminare la regola di correlazione applicativa dall'elenco di quelle attivate per la richiesta
     *
     */
    @DELETE
    @Path("/erogazioni/{nome}/{versione}/configurazioni/tracciamento/correlazione-applicativa/richiesta/{elemento}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina la regola di correlazione applicativa dall'elenco di quelle attivate per la richiesta", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Regola eliminata con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void deleteErogazioneTracciamentoCorrelazioneApplicativaRichiesta(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("elemento") @Size(max=255) String elemento, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Elimina la regola di correlazione applicativa dall'elenco di quelle attivate per la risposta
     *
     * Questa operazione consente di eliminare la regola di correlazione applicativa dall'elenco di quelle attivate per la risposta
     *
     */
    @DELETE
    @Path("/erogazioni/{nome}/{versione}/configurazioni/tracciamento/correlazione-applicativa/risposta/{elemento}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina la regola di correlazione applicativa dall'elenco di quelle attivate per la risposta", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Regola eliminata con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void deleteErogazioneTracciamentoCorrelazioneApplicativaRisposta(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("elemento") @Size(max=255) String elemento, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce la policy XACML associata all'autorizzazione
     *
     * Questa operazione consente di ottenere la policy XACML associata all'autorizzazione
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/download-xacml-policy")
    @Produces({ "application/xml", "application/problem+json" })
    @Operation(summary = "Restituisce la policy XACML associata all'autorizzazione", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Interfaccia dell'API restituita con successo", content = @Content(mediaType = "application/xml", schema = @Schema(implementation = File.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public byte[] downloadErogazioneControlloAccessiAutorizzazioneXacmlPolicy(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce l'elenco delle policy di rate limiting configurate
     *
     * Questa operazione consente di ottenere l'elenco delle policy di rate limiting configurate
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/rate-limiting")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce l'elenco delle policy di rate limiting configurate", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Elenco policy di rate limiting restituito con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListaRateLimitingPolicy.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public ListaRateLimitingPolicy findAllErogazioneRateLimitingPolicies(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio, @QueryParam("q") String q, @QueryParam("limit") Integer limit, @QueryParam("offset") @DefaultValue("0") Integer offset, @QueryParam("metrica") RateLimitingCriteriMetricaEnum metrica);

    /**
     * Restituisce l'elenco delle regole di correlazione applicativa per la richiesta
     *
     * Questa operazione consente di ottenere l'elenco delle regole di correlazione applicativa per la richiesta
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/tracciamento/correlazione-applicativa/richiesta")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce l'elenco delle regole di correlazione applicativa per la richiesta", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Elenco regole di correlazione applicativa restituito con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListaCorrelazioneApplicativaRichiesta.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public ListaCorrelazioneApplicativaRichiesta findAllErogazioneTracciamentoCorrelazioneApplicativaRichiesta(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio, @QueryParam("q") String q, @QueryParam("limit") Integer limit, @QueryParam("offset") @DefaultValue("0") Integer offset);

    /**
     * Restituisce l'elenco delle regole di correlazione applicativa per la risposta
     *
     * Questa operazione consente di ottenere l'elenco delle regole di correlazione applicativa per la risposta
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/tracciamento/correlazione-applicativa/risposta")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce l'elenco delle regole di correlazione applicativa per la risposta", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Elenco regole di correlazione applicativa restituito con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListaCorrelazioneApplicativaRisposta.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public ListaCorrelazioneApplicativaRisposta findAllErogazioneTracciamentoCorrelazioneApplicativaRisposta(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio, @QueryParam("q") String q, @QueryParam("limit") Integer limit, @QueryParam("offset") @DefaultValue("0") Integer offset);

    /**
     * Restituisce la configurazione relativa al caching delle risposte
     *
     * Questa operazione consente di ottenere la configurazione relativa al caching delle risposte
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/caching-risposta")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce la configurazione relativa al caching delle risposte", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dettaglio della configurazione restituito con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CachingRisposta.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public CachingRisposta getErogazioneCachingRisposta(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce il canale associato all'erogazione
     *
     * Questa operazione consente di ottenere il canale associato all'erogazione
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/canale")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce il canale associato all'erogazione", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Canale restituito con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiCanale.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public ApiCanale getErogazioneCanale(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce la configurazione relativa all'autenticazione per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di ottenere la configurazione relativa all'autenticazione per quanto concerne il controllo degli accessi
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autenticazione")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce la configurazione relativa all'autenticazione per quanto concerne il controllo degli accessi", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dettaglio della configurazione restituito con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ControlloAccessiAutenticazione.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public ControlloAccessiAutenticazione getErogazioneControlloAccessiAutenticazione(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce la configurazione relativa all'autorizzazione per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di ottenere la configurazione relativa all'autorizzazione per quanto concerne il controllo degli accessi
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce la configurazione relativa all'autorizzazione per quanto concerne il controllo degli accessi", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dettaglio della configurazione restituito con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ControlloAccessiAutorizzazioneView.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public ControlloAccessiAutorizzazioneView getErogazioneControlloAccessiAutorizzazione(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce l'elenco degli applicativi autorizzati
     *
     * Questa operazione consente di ottenere l'elenco degli applicativi autorizzati
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/applicativi")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce l'elenco degli applicativi autorizzati", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Elenco applicativi autorizzati restituito con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ControlloAccessiErogazioneAutorizzazioneApplicativi.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public ControlloAccessiErogazioneAutorizzazioneApplicativi getErogazioneControlloAccessiAutorizzazioneApplicativi(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce l'elenco degli applicativi token autorizzati
     *
     * Questa operazione consente di ottenere l'elenco degli applicativi token autorizzati
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/token/applicativi")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce l'elenco degli applicativi token autorizzati", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Elenco applicativi token autorizzati restituito con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ControlloAccessiErogazioneAutorizzazioneApplicativi.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public ControlloAccessiErogazioneAutorizzazioneApplicativi getErogazioneControlloAccessiAutorizzazioneApplicativiToken(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce l'elenco dei ruoli autorizzati
     *
     * Questa operazione consente di ottenere l'elenco dei ruoli autorizzati
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/ruoli")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce l'elenco dei ruoli autorizzati", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Elenco ruoli autorizzati restituito con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ControlloAccessiAutorizzazioneRuoli.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public ControlloAccessiAutorizzazioneRuoli getErogazioneControlloAccessiAutorizzazioneRuoli(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce l'elenco dei ruoli token autorizzati
     *
     * Questa operazione consente di ottenere l'elenco dei ruoli token autorizzati
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/token/ruoli")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce l'elenco dei ruoli token autorizzati", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Elenco ruoli token autorizzati restituito con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ControlloAccessiAutorizzazioneRuoli.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public ControlloAccessiAutorizzazioneRuoli getErogazioneControlloAccessiAutorizzazioneRuoliToken(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce l'elenco degli scope autorizzati
     *
     * Questa operazione consente di ottenere l'elenco degli scope autorizzati
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/scope")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce l'elenco degli scope autorizzati", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Elenco scope autorizzati restituito con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ControlloAccessiAutorizzazioneScopes.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public ControlloAccessiAutorizzazioneScopes getErogazioneControlloAccessiAutorizzazioneScope(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce l'elenco dei soggetti autorizzati
     *
     * Questa operazione consente di ottenere l'elenco dei soggetti autorizzati
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/soggetti")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce l'elenco dei soggetti autorizzati", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Elenco soggetti autorizzati restituito con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ControlloAccessiAutorizzazioneSoggetti.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public ControlloAccessiAutorizzazioneSoggetti getErogazioneControlloAccessiAutorizzazioneSoggetti(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce la configurazione relativa alla gestione dei token per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di ottenere la configurazione relativa alla gestione dei token per quanto concerne il controllo degli accessi
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/gestione-token")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce la configurazione relativa alla gestione dei token per quanto concerne il controllo degli accessi", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dettaglio della configurazione restituito con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ControlloAccessiGestioneToken.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public ControlloAccessiGestioneToken getErogazioneControlloAccessiGestioneToken(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce la configurazione relativa all'identificazione degli attributi per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di ottenere la configurazione relativa all'identificazione degli attributi per quanto concerne il controllo degli accessi
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/identificazione-attributi")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce la configurazione relativa all'identificazione degli attributi per quanto concerne il controllo degli accessi", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dettaglio della configurazione restituito con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ControlloAccessiIdentificazioneAttributi.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public ControlloAccessiIdentificazioneAttributi getErogazioneControlloAccessiIdentificazioneAttributi(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce le proprietà di configurazione attivate
     *
     * Questa operazione consente di ottenere le proprietà di configurazione
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/proprieta")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce le proprietà di configurazione attivate", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dettaglio della configurazione restituito con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ElencoProprieta.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public ElencoProprieta getErogazioneElencoProprieta(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce le informazioni sulla configurazione CORS associata all'erogazione
     *
     * Questa operazione consente di ottenere le informazioni sulla configurazione CORS associata all'erogazione identificata dal nome e dalla versione
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/gestione-cors")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce le informazioni sulla configurazione CORS associata all'erogazione", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Informazioni sulla configurazione CORS restituite con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GestioneCors.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public GestioneCors getErogazioneGestioneCORS(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce il dettaglio di una proprietà di configurazione
     *
     * Questa operazione consente di ottenere il dettaglio di una proprietà di configurazione
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/proprieta/{proprieta}")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce il dettaglio di una proprietà di configurazione", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dati della proprietà di configurazione restituiti con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Proprieta.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public Proprieta getErogazioneProprieta(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("proprieta") @Size(max=255) String proprieta, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce il dettaglio di una policy di rate limiting
     *
     * Questa operazione consente di ottenere il dettaglio di una policy di rate limiting
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/rate-limiting/{id_policy}")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce il dettaglio di una policy di rate limiting", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dati della policy restituiti con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RateLimitingPolicyErogazioneView.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public RateLimitingPolicyErogazioneView getErogazioneRateLimitingPolicy(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("id_policy") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String idPolicy, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce la configurazione relativa alla registrazione dei messaggi
     *
     * Questa operazione consente di ottenere la configurazione relativa alla registrazione dei messaggi
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/registrazione-messaggi")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce la configurazione relativa alla registrazione dei messaggi", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dettaglio della configurazione restituito con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegistrazioneMessaggi.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public RegistrazioneMessaggi getErogazioneRegistrazioneMessaggi(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce l'indicazione sullo stato del gruppo
     *
     * Questa operazione consente di ottenere lo stato attuale del gruppo
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/stato")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce l'indicazione sullo stato del gruppo", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dettaglio dello stato restituito con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiImplStato.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public ApiImplStato getErogazioneStato(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce il dettaglio di una regola di correlazione applicativa per la richiesta
     *
     * Questa operazione consente di ottenere il dettaglio di una regola di correlazione applicativa per la richiesta
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/tracciamento/correlazione-applicativa/richiesta/{elemento}")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce il dettaglio di una regola di correlazione applicativa per la richiesta", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dati della regola restituiti con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CorrelazioneApplicativaRichiesta.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public CorrelazioneApplicativaRichiesta getErogazioneTracciamentoCorrelazioneApplicativaRichiesta(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("elemento") @Size(max=255) String elemento, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce il dettaglio di una regola di correlazione applicativa per la risposta
     *
     * Questa operazione consente di ottenere il dettaglio di una regola di correlazione applicativa per la risposta
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/tracciamento/correlazione-applicativa/risposta/{elemento}")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce il dettaglio di una regola di correlazione applicativa per la risposta", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dati della regola restituiti con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CorrelazioneApplicativaRisposta.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public CorrelazioneApplicativaRisposta getErogazioneTracciamentoCorrelazioneApplicativaRisposta(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("elemento") @Size(max=255) String elemento, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce la configurazione relativa alla validazione dei contenuti applicativi
     *
     * Questa operazione consente di ottenere la configurazione relativa alla validazione dei contenuti applicativi
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/configurazioni/validazione")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce la configurazione relativa alla validazione dei contenuti applicativi", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dettaglio della configurazione restituito con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Validazione.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public Validazione getErogazioneValidazione(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare la configurazione relativa al caching delle risposte
     *
     * Questa operazione consente di aggiornare la configurazione relativa al caching delle risposte
     *
     */
    @PUT
    @Path("/erogazioni/{nome}/{versione}/configurazioni/caching-risposta")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la configurazione relativa al caching delle risposte", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void updateErogazioneCachingRisposta(@Valid CachingRisposta body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare il canale associato all'erogazione
     *
     * Questa operazione consente di aggiornare il canale associato all'erogazione
     *
     */
    @PUT
    @Path("/erogazioni/{nome}/{versione}/configurazioni/canale")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare il canale associato all'erogazione", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Il canale è stato aggiornato correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void updateErogazioneCanale(@Valid ConfigurazioneApiCanale body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare la configurazione relativa all'autenticazione per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di aggiornare la configurazione relativa all'autenticazione per quanto concerne il controllo degli accessi
     *
     */
    @PUT
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autenticazione")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la configurazione relativa all'autenticazione per quanto concerne il controllo degli accessi", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void updateErogazioneControlloAccessiAutenticazione(@Valid ControlloAccessiAutenticazione body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare la configurazione relativa all'autorizzazione per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di aggiornare la configurazione relativa all'autorizzazione per quanto concerne il controllo degli accessi
     *
     */
    @PUT
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la configurazione relativa all'autorizzazione per quanto concerne il controllo degli accessi", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void updateErogazioneControlloAccessiAutorizzazione(@Valid ControlloAccessiAutorizzazione body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare la configurazione relativa alla gestione dei token per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di aggiornare la configurazione relativa alla gestione dei token per quanto concerne il controllo degli accessi
     *
     */
    @PUT
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/gestione-token")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la configurazione relativa alla gestione dei token per quanto concerne il controllo degli accessi", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void updateErogazioneControlloAccessiGestioneToken(@Valid ControlloAccessiGestioneToken body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare la configurazione relativa all'identificazione degli attributi per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di aggiornare la configurazione relativa all'identificazione degli attributi per quanto concerne il controllo degli accessi
     *
     */
    @PUT
    @Path("/erogazioni/{nome}/{versione}/configurazioni/controllo-accessi/identificazione-attributi")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la configurazione relativa all'identificazione degli attributi per quanto concerne il controllo degli accessi", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void updateErogazioneControlloIdentificazioneAttributi(@Valid ControlloAccessiIdentificazioneAttributi body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare la configurazione CORS associata all'erogazione
     *
     * Questa operazione consente di aggiornare la configurazione CORS associata all'erogazione identificata dal nome e dalla versione
     *
     */
    @PUT
    @Path("/erogazioni/{nome}/{versione}/configurazioni/gestione-cors")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la configurazione CORS associata all'erogazione", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione dell'erogazione aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void updateErogazioneGestioneCORS(@Valid GestioneCors body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Modifica i dati di una proprietà di configurazione
     *
     * Questa operazione consente di aggiornare i dati relativi ad una proprietà di configurazione
     *
     */
    @PUT
    @Path("/erogazioni/{nome}/{versione}/configurazioni/proprieta/{proprieta}")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Modifica i dati di una proprietà di configurazione", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "La proprietà di configurazione è stata aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void updateErogazioneProprieta(@Valid Proprieta body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("proprieta") @Size(max=255) String proprieta, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Modifica i dati di una policy di rate limiting
     *
     * Questa operazione consente di aggiornare i dati relativi ad una policy di rate limiting
     *
     */
    @PUT
    @Path("/erogazioni/{nome}/{versione}/configurazioni/rate-limiting/{id_policy}")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Modifica i dati di una policy di rate limiting", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "La policy è stata aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void updateErogazioneRateLimitingPolicy(@Valid RateLimitingPolicyErogazioneUpdate body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("id_policy") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String idPolicy, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare la configurazione relativa alla registrazione dei messaggi
     *
     * Questa operazione consente di aggiornare la configurazione relativa alla registrazione dei messaggi
     *
     */
    @PUT
    @Path("/erogazioni/{nome}/{versione}/configurazioni/registrazione-messaggi")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la configurazione relativa alla registrazione dei messaggi", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void updateErogazioneRegistrazioneMessaggi(@Valid RegistrazioneMessaggi body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare lo stato del gruppo
     *
     * Questa operazione consente di aggiornare lo stato del gruppo
     *
     */
    @PUT
    @Path("/erogazioni/{nome}/{versione}/configurazioni/stato")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare lo stato del gruppo", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione del gruppo aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void updateErogazioneStato(@Valid ApiImplStato body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Modifica i dati di una regola di correlazione applicativa per la richiesta
     *
     * Questa operazione consente di aggiornare i dati relativi ad una regola di correlazione applicativa per la richiesta
     *
     */
    @PUT
    @Path("/erogazioni/{nome}/{versione}/configurazioni/tracciamento/correlazione-applicativa/richiesta/{elemento}")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Modifica i dati di una regola di correlazione applicativa per la richiesta", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "La regola è stata aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void updateErogazioneTracciamentoCorrelazioneApplicativaRichiesta(@Valid CorrelazioneApplicativaRichiesta body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("elemento") @Size(max=255) String elemento, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Modifica i dati di una regola di correlazione applicativa per la risposta
     *
     * Questa operazione consente di aggiornare i dati relativi ad una regola di correlazione applicativa per la risposta
     *
     */
    @PUT
    @Path("/erogazioni/{nome}/{versione}/configurazioni/tracciamento/correlazione-applicativa/risposta/{elemento}")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Modifica i dati di una regola di correlazione applicativa per la risposta", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "La regola è stata aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void updateErogazioneTracciamentoCorrelazioneApplicativaRisposta(@Valid CorrelazioneApplicativaRisposta body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("elemento") @Size(max=255) String elemento, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare la configurazione relativa alla validazione dei contenuti applicativi
     *
     * Questa operazione consente di aggiornare la configurazione relativa alla validazione dei contenuti applicativi
     *
     */
    @PUT
    @Path("/erogazioni/{nome}/{versione}/configurazioni/validazione")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la configurazione relativa alla validazione dei contenuti applicativi", tags={ "erogazioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void updateErogazioneValidazione(@Valid Validazione body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);
}
