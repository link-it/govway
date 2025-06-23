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
package org.openspcoop2.core.config.rs.server.api;

import org.openspcoop2.core.config.rs.server.model.ApiDescrizione;
import org.openspcoop2.core.config.rs.server.model.Gruppo;
import org.openspcoop2.core.config.rs.server.model.GruppoAzioni;
import org.openspcoop2.core.config.rs.server.model.GruppoNome;
import org.openspcoop2.core.config.rs.server.model.ListaGruppi;
import org.openspcoop2.core.config.rs.server.model.Problem;
import org.openspcoop2.utils.service.beans.ProfiloEnum;

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
public interface ErogazioniGruppiApi  {

    /**
     * Aggiunta di azioni o risorse dell'API al gruppo
     *
     * Questa operazione consente di aggiungere azioni o risorse dell'API al gruppo
     *
     */
    @POST
    @Path("/erogazioni/{nome}/{versione}/gruppi/{nome_gruppo}/azioni")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Aggiunta di azioni o risorse dell'API al gruppo", tags={ "erogazioni-gruppi" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void addErogazioneGruppoAzioni(@Valid GruppoAzioni body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_gruppo") @Size(max=255) String nomeGruppo, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Creazione di un gruppo di azioni o risorse dell'API erogata
     *
     * Questa operazione consente di creare un gruppo di azioni o risorse dell'API erogata
     *
     */
    @POST
    @Path("/erogazioni/{nome}/{versione}/gruppi")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Creazione di un gruppo di azioni o risorse dell'API erogata", tags={ "erogazioni-gruppi" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void createErogazioneGruppo(@Valid Gruppo body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Elimina il gruppo identificato dal nome
     *
     * Questa operazione consente di eliminare il gruppo identificato dal nome
     *
     */
    @DELETE
    @Path("/erogazioni/{nome}/{versione}/gruppi/{nome_gruppo}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina il gruppo identificato dal nome", tags={ "erogazioni-gruppi" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Gruppo eliminate con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void deleteErogazioneGruppo(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_gruppo") @Size(max=255) String nomeGruppo, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Elimina l'azione o la risorsa dell'API associata al gruppo
     *
     * Questa operazione consente di eliminare l'azione o la risorsa dell'API associate al gruppo
     *
     */
    @DELETE
    @Path("/erogazioni/{nome}/{versione}/gruppi/{nome_gruppo}/azioni/{nome_azione}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina l'azione o la risorsa dell'API associata al gruppo", tags={ "erogazioni-gruppi" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Azione eliminate con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void deleteErogazioneGruppoAzione(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_gruppo") @Size(max=255) String nomeGruppo, @PathParam("nome_azione") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nomeAzione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Ricerca i gruppi in cui sono stati classificate le azioni o le risorse dell'API
     *
     * Elenca i gruppi in cui sono stati classificate le azioni o le risorse dell'API
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/gruppi")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Ricerca i gruppi in cui sono stati classificate le azioni o le risorse dell'API", tags={ "erogazioni-gruppi" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Ricerca eseguita correttamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListaGruppi.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public ListaGruppi findAllErogazioneGruppi(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio, @QueryParam("limit") Integer limit, @QueryParam("offset") @DefaultValue("0") Integer offset, @QueryParam("azione") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String azione);

    /**
     * Restituisce la descrizione del gruppo
     *
     * Questa operazione consente di ottenere la descrizione del gruppo
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/gruppi/{nome_gruppo}/descrizione")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce la descrizione del gruppo", tags={ "erogazioni-gruppi" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dati della descrizione restituiti con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiDescrizione.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public ApiDescrizione getErogazioneDescrizioneGruppo(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_gruppo") @Size(max=255) String nomeGruppo, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Restituisce azioni/risorse associate al gruppo identificato dal nome
     *
     * Questa operazione consente di ottenere le azioni associate al gruppo identificato dal nome
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/gruppi/{nome_gruppo}")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce azioni/risorse associate al gruppo identificato dal nome", tags={ "erogazioni-gruppi" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Elenco di azioni/risorse restituite con successo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GruppoAzioni.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public GruppoAzioni getErogazioneGruppoAzioni(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_gruppo") @Size(max=255) String nomeGruppo, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare la descrizione del gruppo
     *
     * Questa operazione consente di aggiornare la descrizione del gruppo
     *
     */
    @PUT
    @Path("/erogazioni/{nome}/{versione}/gruppi/{nome_gruppo}/descrizione")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare la descrizione del gruppo", tags={ "erogazioni-gruppi" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "La descrizione è stato aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void updateErogazioneDescrizioneGruppo(@Valid ApiDescrizione body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_gruppo") @Size(max=255) String nomeGruppo, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);

    /**
     * Consente di modificare il nome del gruppo
     *
     * Questa operazione consente di aggiornare il nome di un gruppo
     *
     */
    @PUT
    @Path("/erogazioni/{nome}/{versione}/gruppi/{nome_gruppo}")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare il nome del gruppo", tags={ "erogazioni-gruppi" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione del gruppo aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = Problem.class))) })
    public void updateErogazioneGruppoNome(@Valid GruppoNome body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_gruppo") @Size(max=255) String nomeGruppo, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String soggetto, @QueryParam("tipo_servizio") @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20) String tipoServizio);
}
