/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 *
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.rs.server.model.ContestoEnum;
import org.openspcoop2.core.config.rs.server.model.FonteEnum;
import org.openspcoop2.core.config.rs.server.model.ListaRuoli;
import org.openspcoop2.core.config.rs.server.model.Problem;
import org.openspcoop2.core.config.rs.server.model.Ruolo;

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
public interface RuoliApi  {

    /**
     * Creazione di un ruolo
     *
     * Questa operazione consente di creare un ruolo
     *
     */
    @POST
    @Path("/ruoli")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Creazione di un ruolo", tags={ "ruoli" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void createRuolo(@Valid Ruolo body);

    /**
     * Elimina un ruolo
     *
     * Questa operazione consente di eliminare un ruolo identificato dal nome
     *
     */
    @DELETE
    @Path("/ruoli/{nome}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina un ruolo", tags={ "ruoli" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Ruolo eliminato con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void deleteRuolo(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome);

    /**
     * Ricerca ruoli
     *
     * Elenca i ruoli registrati
     *
     */
    @GET
    @Path("/ruoli")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Ricerca ruoli", tags={ "ruoli" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Ricerca eseguita correttamente", content = @Content(schema = @Schema(implementation = ListaRuoli.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ListaRuoli findAllRuoli(@QueryParam("q") String q, @QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset, @QueryParam("fonte") FonteEnum fonte, @QueryParam("contesto") ContestoEnum contesto);

    /**
     * Restituisce il dettaglio di un ruolo
     *
     * Questa operazione consente di ottenere il dettaglio di un ruolo identificato dal nome
     *
     */
    @GET
    @Path("/ruoli/{nome}")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce il dettaglio di un ruolo", tags={ "ruoli" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dati del ruolo restituiti con successo", content = @Content(schema = @Schema(implementation = Ruolo.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public Ruolo getRuolo(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome);

    /**
     * Modifica i dati di un ruolo
     *
     * Questa operazione consente di aggiornare i dati di un ruolo identificato dal nome
     *
     */
    @PUT
    @Path("/ruoli/{nome}")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Modifica i dati di un ruolo", tags={ "ruoli" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Il Ruolo è stato aggiornato correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateRuolo(@Valid Ruolo body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome);
}
