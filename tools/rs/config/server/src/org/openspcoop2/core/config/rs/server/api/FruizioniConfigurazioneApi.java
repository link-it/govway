/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneApplicativi;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneApplicativo;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneRuoli;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneRuolo;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneScope;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneScopes;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneView;
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
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyFruizione;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyFruizioneUpdate;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyFruizioneView;
import org.openspcoop2.core.config.rs.server.model.RegistrazioneMessaggi;
import org.openspcoop2.core.config.rs.server.model.Validazione;

import javax.ws.rs.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * GovWay Config API
 *
 * <p>Servizi per la configurazione di GovWay
 *
 */
@Path("/")
public interface FruizioniConfigurazioneApi  {

    /**
     * Aggiunta di applicativi all'elenco degli applicativi autorizzati
     *
     * Questa operazione consente di aggiungere applicativi all'elenco degli applicativi autorizzati
     *
     */
    @POST
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/applicativi")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Aggiunta di applicativi all'elenco degli applicativi autorizzati", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void addFruizioneControlloAccessiAutorizzazioneApplicativi(@Valid ControlloAccessiAutorizzazioneApplicativo body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Aggiunta di ruoli all'elenco dei ruoli autorizzati
     *
     * Questa operazione consente di aggiungere ruoli all'elenco dei ruoli autorizzati
     *
     */
    @POST
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/ruoli")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Aggiunta di ruoli all'elenco dei ruoli autorizzati", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void addFruizioneControlloAccessiAutorizzazioneRuoli(@Valid ControlloAccessiAutorizzazioneRuolo body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Aggiunta di scope all'elenco degli scope autorizzati
     *
     * Questa operazione consente di aggiungere scope all'elenco degli scope autorizzati
     *
     */
    @POST
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/scope")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Aggiunta di scope all'elenco degli scope autorizzati", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void addFruizioneControlloAccessiAutorizzazioneScope(@Valid ControlloAccessiAutorizzazioneScope body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Aggiunta di una proprietà di configurazione
     *
     * Questa operazione consente di registrare una proprietà di configurazione
     *
     */
    @POST
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/proprieta")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Aggiunta di una proprietà di configurazione", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void addFruizioneProprieta(@Valid Proprieta body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Aggiunta di una policy di rate limiting
     *
     * Questa operazione consente di aggiungere una policy di rate limiting
     *
     */
    @POST
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/rate-limiting")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Aggiunta di una policy di rate limiting", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void addFruizioneRateLimitingPolicy(@Valid RateLimitingPolicyFruizione body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Aggiunta di una regola di correlazione applicativa
     *
     * Questa operazione consente di registrare una regola di correlazione applicativa per la richiesta
     *
     */
    @POST
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/tracciamento/correlazione-applicativa/richiesta")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Aggiunta di una regola di correlazione applicativa", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void addFruizioneTracciamentoCorrelazioneApplicativaRichiesta(@Valid CorrelazioneApplicativaRichiesta body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Aggiunta di una regola di correlazione applicativa
     *
     * Questa operazione consente di registrare una regola di correlazione applicativa per la risposta
     *
     */
    @POST
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/tracciamento/correlazione-applicativa/risposta")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Aggiunta di una regola di correlazione applicativa", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void addFruizioneTracciamentoCorrelazioneApplicativaRisposta(@Valid CorrelazioneApplicativaRisposta body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Elimina applicativi dall'elenco degli applicativi autorizzati
     *
     * Questa operazione consente di eliminare applicativi dall'elenco degli applicativi autorizzati
     *
     */
    @DELETE
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/applicativi/{applicativo_autorizzato}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina applicativi dall'elenco degli applicativi autorizzati", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Applicativi eliminati con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void deleteFruizioneControlloAccessiAutorizzazioneApplicativi(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("applicativo_autorizzato") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String applicativoAutorizzato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Elimina ruoli dall'elenco dei ruoli autorizzati
     *
     * Questa operazione consente di eliminare ruoli dall'elenco dei ruoli autorizzati
     *
     */
    @DELETE
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/ruoli/{ruolo_autorizzato}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina ruoli dall'elenco dei ruoli autorizzati", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Ruoli eliminati con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void deleteFruizioneControlloAccessiAutorizzazioneRuoli(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("ruolo_autorizzato") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String ruoloAutorizzato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Elimina scope dall'elenco degli scope autorizzati
     *
     * Questa operazione consente di eliminare scope dall'elenco degli scope autorizzati
     *
     */
    @DELETE
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/scope/{scope_autorizzato}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina scope dall'elenco degli scope autorizzati", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Scope eliminati con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void deleteFruizioneControlloAccessiAutorizzazioneScope(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("scope_autorizzato") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String scopeAutorizzato, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Elimina la proprietà di configurazione dall'elenco di quelle attivate
     *
     * Questa operazione consente di eliminare la proprietà di configurazione dall'elenco di quelle attivate
     *
     */
    @DELETE
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/proprieta/{proprieta}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina la proprietà di configurazione dall'elenco di quelle attivate", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Proprietà di configurazione eliminata con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void deleteFruizioneProprietaConfigurazione(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("proprieta") @Size(max=255) String proprieta, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Elimina la policy dall'elenco delle policies attive
     *
     * Questa operazione consente di eliminare la policy dall'elenco delle policies attive
     *
     */
    @DELETE
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/rate-limiting/{id_policy}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina la policy dall'elenco delle policies attive", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Policy eliminata con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void deleteFruizioneRateLimitingPolicy(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("id_policy") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String idPolicy, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Elimina la regola di correlazione applicativa dall'elenco di quelle attivate per la richiesta
     *
     * Questa operazione consente di eliminare la regola di correlazione applicativa dall'elenco di quelle attivate per la richiesta
     *
     */
    @DELETE
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/tracciamento/correlazione-applicativa/richiesta/{elemento}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina la regola di correlazione applicativa dall'elenco di quelle attivate per la richiesta", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Regola eliminata con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void deleteFruizioneTracciamentoCorrelazioneApplicativaRichiesta(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("elemento") @Size(max=255) String elemento, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Elimina la regola di correlazione applicativa dall'elenco di quelle attivate per la risposta
     *
     * Questa operazione consente di eliminare la regola di correlazione applicativa dall'elenco di quelle attivate per la risposta
     *
     */
    @DELETE
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/tracciamento/correlazione-applicativa/risposta/{elemento}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina la regola di correlazione applicativa dall'elenco di quelle attivate per la risposta", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Regola eliminata con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void deleteFruizioneTracciamentoCorrelazioneApplicativaRisposta(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("elemento") @Size(max=255) String elemento, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce la policy XACML associata all'autorizzazione
     *
     * Questa operazione consente di ottenere la policy XACML associata all'autorizzazione
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/download-xacml-policy")
    @Produces({ "application/xml", "application/problem+json" })
    @Operation(summary = "Restituisce la policy XACML associata all'autorizzazione", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Interfaccia dell'API restituita con successo", content = @Content(schema = @Schema(implementation = File.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] downloadFruizioneControlloAccessiAutorizzazioneXacmlPolicy(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce l'elenco delle policy di rate limiting configurate
     *
     * Questa operazione consente di ottenere l'elenco delle policy di rate limiting configurate
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/rate-limiting")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce l'elenco delle policy di rate limiting configurate", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Elenco policy di rate limiting restituito con successo", content = @Content(schema = @Schema(implementation = ListaRateLimitingPolicy.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ListaRateLimitingPolicy findAllFruizioneRateLimitingPolicies(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio, @QueryParam("q") String q, @QueryParam("limit") Integer limit, @QueryParam("offset") @DefaultValue("0") Integer offset, @QueryParam("metrica") RateLimitingCriteriMetricaEnum metrica);

    /**
     * Restituisce l'elenco delle regole di correlazione applicativa per la richiesta
     *
     * Questa operazione consente di ottenere l'elenco delle regole di correlazione applicativa per la richiesta
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/tracciamento/correlazione-applicativa/richiesta")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce l'elenco delle regole di correlazione applicativa per la richiesta", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Elenco regole di correlazione applicativa restituito con successo", content = @Content(schema = @Schema(implementation = ListaCorrelazioneApplicativaRichiesta.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ListaCorrelazioneApplicativaRichiesta findAllFruizioneTracciamentoCorrelazioneApplicativaRichiesta(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio, @QueryParam("q") String q, @QueryParam("limit") Integer limit, @QueryParam("offset") @DefaultValue("0") Integer offset);

    /**
     * Restituisce l'elenco delle regole di correlazione applicativa per la risposta
     *
     * Questa operazione consente di ottenere l'elenco delle regole di correlazione applicativa per la risposta
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/tracciamento/correlazione-applicativa/risposta")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce l'elenco delle regole di correlazione applicativa per la risposta", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Elenco regole di correlazione applicativa restituito con successo", content = @Content(schema = @Schema(implementation = ListaCorrelazioneApplicativaRisposta.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ListaCorrelazioneApplicativaRisposta findAllFruizioneTracciamentoCorrelazioneApplicativaRisposta(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio, @QueryParam("q") String q, @QueryParam("limit") Integer limit, @QueryParam("offset") @DefaultValue("0") Integer offset);

    /**
     * Restituisce la configurazione relativa al caching delle risposte
     *
     * Questa operazione consente di ottenere la configurazione relativa al caching delle risposte
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/caching-risposta")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce la configurazione relativa al caching delle risposte", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dettaglio della configurazione restituito con successo", content = @Content(schema = @Schema(implementation = CachingRisposta.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public CachingRisposta getFruizioneCachingRisposta(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce il canale associato alla fruizione
     *
     * Questa operazione consente di ottenere il canale associato alla fruizione
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/canale")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce il canale associato alla fruizione", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Canale restituito con successo", content = @Content(schema = @Schema(implementation = ApiCanale.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ApiCanale getFruizioneCanale(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce la configurazione relativa all'autenticazione per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di ottenere la configurazione relativa all'autenticazione per quanto concerne il controllo degli accessi
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/controllo-accessi/autenticazione")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce la configurazione relativa all'autenticazione per quanto concerne il controllo degli accessi", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dettaglio della configurazione restituito con successo", content = @Content(schema = @Schema(implementation = ControlloAccessiAutenticazione.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ControlloAccessiAutenticazione getFruizioneControlloAccessiAutenticazione(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce la configurazione relativa all'autorizzazione per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di ottenere la configurazione relativa all'autorizzazione per quanto concerne il controllo degli accessi
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce la configurazione relativa all'autorizzazione per quanto concerne il controllo degli accessi", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dettaglio della configurazione restituito con successo", content = @Content(schema = @Schema(implementation = ControlloAccessiAutorizzazioneView.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ControlloAccessiAutorizzazioneView getFruizioneControlloAccessiAutorizzazione(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce l'elenco degli applicativi autorizzati
     *
     * Questa operazione consente di ottenere l'elenco degli applicativi autorizzati
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/applicativi")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce l'elenco degli applicativi autorizzati", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Elenco applicativi autorizzati restituito con successo", content = @Content(schema = @Schema(implementation = ControlloAccessiAutorizzazioneApplicativi.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ControlloAccessiAutorizzazioneApplicativi getFruizioneControlloAccessiAutorizzazioneApplicativi(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce l'elenco dei ruoli autorizzati
     *
     * Questa operazione consente di ottenere l'elenco dei ruoli autorizzati
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/ruoli")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce l'elenco dei ruoli autorizzati", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Elenco ruoli autorizzati restituito con successo", content = @Content(schema = @Schema(implementation = ControlloAccessiAutorizzazioneRuoli.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ControlloAccessiAutorizzazioneRuoli getFruizioneControlloAccessiAutorizzazioneRuoli(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce l'elenco degli scope autorizzati
     *
     * Questa operazione consente di ottenere l'elenco degli scope autorizzati
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione/scope")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce l'elenco degli scope autorizzati", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Elenco scope autorizzati restituito con successo", content = @Content(schema = @Schema(implementation = ControlloAccessiAutorizzazioneScopes.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ControlloAccessiAutorizzazioneScopes getFruizioneControlloAccessiAutorizzazioneScope(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce la configurazione relativa alla gestione dei token per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di ottenere la configurazione relativa alla gestione dei token
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/controllo-accessi/gestione-token")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce la configurazione relativa alla gestione dei token per quanto concerne il controllo degli accessi", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dettaglio della configurazione restituito con successo", content = @Content(schema = @Schema(implementation = ControlloAccessiGestioneToken.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ControlloAccessiGestioneToken getFruizioneControlloAccessiGestioneToken(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce la configurazione relativa all'identificazione degli attributi per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di ottenere la configurazione relativa all'identificazione degli attributi
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/controllo-accessi/identificazione-attributi")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce la configurazione relativa all'identificazione degli attributi per quanto concerne il controllo degli accessi", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dettaglio della configurazione restituito con successo", content = @Content(schema = @Schema(implementation = ControlloAccessiIdentificazioneAttributi.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ControlloAccessiIdentificazioneAttributi getFruizioneControlloAccessiIdentificazioneAttributi(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce le proprietà di configurazione attivate
     *
     * Questa operazione consente di ottenere le proprietà di configurazione
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/proprieta")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce le proprietà di configurazione attivate", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dettaglio della configurazione restituito con successo", content = @Content(schema = @Schema(implementation = ElencoProprieta.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ElencoProprieta getFruizioneElencoProprieta(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce le informazioni sulla configurazione CORS associata alla fruizione
     *
     * Questa operazione consente di ottenere le informazioni sulla configurazione CORS associata alla fruizione identificata dall'erogatore, dal nome e dalla versione
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/gestione-cors")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce le informazioni sulla configurazione CORS associata alla fruizione", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Informazioni sulla configurazione CORS restituite con successo", content = @Content(schema = @Schema(implementation = GestioneCors.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public GestioneCors getFruizioneGestioneCORS(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce il dettaglio di una proprietà di configurazione
     *
     * Questa operazione consente di ottenere il dettaglio di una proprietà di configurazione
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/proprieta/{proprieta}")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce il dettaglio di una proprietà di configurazione", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dati della proprietà di configurazione restituiti con successo", content = @Content(schema = @Schema(implementation = Proprieta.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public Proprieta getFruizioneProprieta(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("proprieta") @Size(max=255) String proprieta, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce il dettaglio di una policy di rate limiting
     *
     * Questa operazione consente di ottenere il dettaglio di una policy di rate limiting
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/rate-limiting/{id_policy}")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce il dettaglio di una policy di rate limiting", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dati della policy restituiti con successo", content = @Content(schema = @Schema(implementation = RateLimitingPolicyFruizioneView.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public RateLimitingPolicyFruizioneView getFruizioneRateLimitingPolicy(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("id_policy") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String idPolicy, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce la configurazione relativa alla registrazione dei messaggi
     *
     * Questa operazione consente di ottenere la configurazione relativa alla registrazione dei messaggi
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/registrazione-messaggi")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce la configurazione relativa alla registrazione dei messaggi", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dettaglio della configurazione restituito con successo", content = @Content(schema = @Schema(implementation = RegistrazioneMessaggi.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public RegistrazioneMessaggi getFruizioneRegistrazioneMessaggi(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce l'indicazione sullo stato del gruppo
     *
     * Questa operazione consente di ottenere lo stato attuale del gruppo
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/stato")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce l'indicazione sullo stato del gruppo", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dettaglio dello stato restituito con successo", content = @Content(schema = @Schema(implementation = ApiImplStato.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ApiImplStato getFruizioneStato(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce il dettaglio di una regola di correlazione applicativa per la richiesta
     *
     * Questa operazione consente di ottenere il dettaglio di una regola di correlazione applicativa per la richiesta
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/tracciamento/correlazione-applicativa/richiesta/{elemento}")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce il dettaglio di una regola di correlazione applicativa per la richiesta", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dati della regola restituiti con successo", content = @Content(schema = @Schema(implementation = CorrelazioneApplicativaRichiesta.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public CorrelazioneApplicativaRichiesta getFruizioneTracciamentoCorrelazioneApplicativaRichiesta(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("elemento") @Size(max=255) String elemento, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce il dettaglio di una regola di correlazione applicativa per la risposta
     *
     * Questa operazione consente di ottenere il dettaglio di una regola di correlazione applicativa per la risposta
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/tracciamento/correlazione-applicativa/risposta/{elemento}")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce il dettaglio di una regola di correlazione applicativa per la risposta", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dati della regola restituiti con successo", content = @Content(schema = @Schema(implementation = CorrelazioneApplicativaRisposta.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public CorrelazioneApplicativaRisposta getFruizioneTracciamentoCorrelazioneApplicativaRisposta(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("elemento") @Size(max=255) String elemento, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce la configurazione relativa alla validazione dei contenuti applicativi
     *
     * Questa operazione consente di ottenere la configurazione relativa alla validazione dei contenuti applicativi
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/validazione")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce la configurazione relativa alla validazione dei contenuti applicativi", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dettaglio della configurazione restituito con successo", content = @Content(schema = @Schema(implementation = Validazione.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public Validazione getFruizioneValidazione(@PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare la configurazione relativa al caching delle risposte
     *
     * Questa operazione consente di aggiornare la configurazione relativa al caching delle risposte
     *
     */
    @PUT
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/caching-risposta")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la configurazione relativa al caching delle risposte", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateFruizioneCachingRisposta(@Valid CachingRisposta body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare il canale associato alla fruizione
     *
     * Questa operazione consente di aggiornare il canale associato alla fruizione
     *
     */
    @PUT
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/canale")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare il canale associato alla fruizione", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Il canale è stato aggiornato correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateFruizioneCanale(@Valid ConfigurazioneApiCanale body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare la configurazione relativa all'autenticazione per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di aggiornare la configurazione relativa all'autenticazione per quanto concerne il controllo degli accessi
     *
     */
    @PUT
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/controllo-accessi/autenticazione")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la configurazione relativa all'autenticazione per quanto concerne il controllo degli accessi", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateFruizioneControlloAccessiAutenticazione(@Valid ControlloAccessiAutenticazione body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare la configurazione relativa all'autorizzazione per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di aggiornare la configurazione relativa all'autorizzazione per quanto concerne il controllo degli accessi
     *
     */
    @PUT
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/controllo-accessi/autorizzazione")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la configurazione relativa all'autorizzazione per quanto concerne il controllo degli accessi", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateFruizioneControlloAccessiAutorizzazione(@Valid ControlloAccessiAutorizzazione body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare la configurazione relativa alla gestione dei token per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di aggiornare la configurazione relativa alla gestione dei token
     *
     */
    @PUT
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/controllo-accessi/gestione-token")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la configurazione relativa alla gestione dei token per quanto concerne il controllo degli accessi", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateFruizioneControlloAccessiGestioneToken(@Valid ControlloAccessiGestioneToken body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare la configurazione relativa all'identificazione degli attributi per quanto concerne il controllo degli accessi
     *
     * Questa operazione consente di aggiornare la configurazione relativa all'identificazione degli attributi
     *
     */
    @PUT
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/controllo-accessi/identificazione-attributi")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la configurazione relativa all'identificazione degli attributi per quanto concerne il controllo degli accessi", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateFruizioneControlloAccessiIdentificazioneAttributi(@Valid ControlloAccessiIdentificazioneAttributi body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare la configurazione CORS associata alla fruizione
     *
     * Questa operazione consente di aggiornare la configurazione CORS associata alla fruizione identificata dall'erogatore, dal nome e dalla versione
     *
     */
    @PUT
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/gestione-cors")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la configurazione CORS associata alla fruizione", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione della fruizione aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateFruizioneGestioneCORS(@Valid GestioneCors body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Modifica i dati di una proprietà di configurazione
     *
     * Questa operazione consente di aggiornare i dati relativi ad una proprietà di configurazione
     *
     */
    @PUT
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/proprieta/{proprieta}")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Modifica i dati di una proprietà di configurazione", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "La proprietà di configurazione è stata aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateFruizioneProprieta(@Valid Proprieta body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("proprieta") @Size(max=255) String proprieta, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Modifica i dati di una policy di rate limiting
     *
     * Questa operazione consente di aggiornare i dati relativi ad una policy di rate limiting
     *
     */
    @PUT
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/rate-limiting/{id_policy}")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Modifica i dati di una policy di rate limiting", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "La policy è stata aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateFruizioneRateLimitingPolicy(@Valid RateLimitingPolicyFruizioneUpdate body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("id_policy") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String idPolicy, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare la configurazione relativa alla registrazione dei messaggi
     *
     * Questa operazione consente di aggiornare la configurazione relativa alla registrazione dei messaggi
     *
     */
    @PUT
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/registrazione-messaggi")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la configurazione relativa alla registrazione dei messaggi", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateFruizioneRegistrazioneMessaggi(@Valid RegistrazioneMessaggi body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare lo stato del gruppo
     *
     * Questa operazione consente di aggiornare lo stato del gruppo
     *
     */
    @PUT
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/stato")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare lo stato del gruppo", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione del gruppo aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateFruizioneStato(@Valid ApiImplStato body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Modifica i dati di una regola di correlazione applicativa per la richiesta
     *
     * Questa operazione consente di aggiornare i dati relativi ad una regola di correlazione applicativa per la richiesta
     *
     */
    @PUT
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/tracciamento/correlazione-applicativa/richiesta/{elemento}")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Modifica i dati di una regola di correlazione applicativa per la richiesta", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "La regola è stata aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateFruizioneTracciamentoCorrelazioneApplicativaRichiesta(@Valid CorrelazioneApplicativaRichiesta body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("elemento") @Size(max=255) String elemento, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Modifica i dati di una regola di correlazione applicativa per la risposta
     *
     * Questa operazione consente di aggiornare i dati relativi ad una regola di correlazione applicativa per la risposta
     *
     */
    @PUT
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/tracciamento/correlazione-applicativa/risposta/{elemento}")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Modifica i dati di una regola di correlazione applicativa per la risposta", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "La regola è stata aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateFruizioneTracciamentoCorrelazioneApplicativaRisposta(@Valid CorrelazioneApplicativaRisposta body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("elemento") @Size(max=255) String elemento, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare la configurazione relativa alla validazione dei contenuti applicativi
     *
     * Questa operazione consente di aggiornare la configurazione relativa alla validazione dei contenuti applicativi
     *
     */
    @PUT
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/configurazioni/validazione")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la configurazione relativa alla validazione dei contenuti applicativi", tags={ "fruizioni-configurazione" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateFruizioneValidazione(@Valid Validazione body, @PathParam("erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String erogatore, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("gruppo") @Size(max=255) String gruppo, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);
}
