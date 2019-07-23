/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
import org.openspcoop2.core.config.rs.server.model.ListaScope;
import org.openspcoop2.core.config.rs.server.model.Problem;
import org.openspcoop2.core.config.rs.server.model.Scope;

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
public interface ScopeApi  {

    /**
     * Creazione di uno scope
     *
     * Questa operazione consente di creare uno scope
     *
     */
    @POST
    @Path("/scope")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Creazione di uno scope", tags={ "scope" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void createScope(@Valid Scope body);

    /**
     * Elimina uno scope
     *
     * Questa operazione consente di eliminare uno scope identificato dal nome
     *
     */
    @DELETE
    @Path("/scope/{nome}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina uno scope", tags={ "scope" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Scope eliminato con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void deleteScope(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome);

    /**
     * Ricerca scope
     *
     * Elenca gli scope registrati
     *
     */
    @GET
    @Path("/scope")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Ricerca scope", tags={ "scope" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Ricerca eseguita correttamente", content = @Content(schema = @Schema(implementation = ListaScope.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ListaScope findAllScope(@QueryParam("q") String q, @QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset, @QueryParam("contesto") ContestoEnum contesto);

    /**
     * Restituisce il dettaglio di uno scope
     *
     * Questa operazione consente di ottenere il dettaglio di uno scope identificato dal nome
     *
     */
    @GET
    @Path("/scope/{nome}")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce il dettaglio di uno scope", tags={ "scope" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dati dello scope restituiti con successo", content = @Content(schema = @Schema(implementation = Scope.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public Scope getScope(@PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome);

    /**
     * Modifica i dati di uno scope
     *
     * Questa operazione consente di aggiornare i dati di uno scope identificato dal nome
     *
     */
    @PUT
    @Path("/scope/{nome}")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Modifica i dati di uno scope", tags={ "scope" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Lo scope è stato aggiornato correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateScope(@Valid Scope body, @PathParam("nome") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nome);
}
