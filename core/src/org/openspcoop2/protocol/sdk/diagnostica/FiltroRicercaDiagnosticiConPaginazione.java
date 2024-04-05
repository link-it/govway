/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.sdk.diagnostica;

import java.io.Serializable;

/**
 * Oggetto contenente informazioni per la ricerca di diagnostici
 * 
 * @author Stefano Corallo (corallo@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class FiltroRicercaDiagnosticiConPaginazione extends FiltroRicercaDiagnostici implements Serializable{


	private static final long serialVersionUID = 2103096411857601491L;
    
    protected int limit;
    protected int offset;
    protected boolean asc = true;

    protected long[] offsetMap = {0,0,0};
    protected boolean partial;
    
    public FiltroRicercaDiagnosticiConPaginazione() {
    	super();
	}
    public FiltroRicercaDiagnosticiConPaginazione(FiltroRicercaDiagnostici filtro) {
    	super();
    	
        this.dataFine = filtro.dataFine;
        this.dataInizio = filtro.dataInizio;

        this.idTransazione = filtro.idTransazione;

        this.idFunzione = filtro.idFunzione;
        this.dominio = filtro.dominio;
        
        this.idBustaRichiesta = filtro.idBustaRichiesta;
        this.idBustaRisposta = filtro.idBustaRisposta;
        
        this.severita = filtro.severita;
        
        this.codice = filtro.codice;
        
        this.messaggioCercatoInternamenteTestoDiagnostico = filtro.messaggioCercatoInternamenteTestoDiagnostico;
        
        this.protocollo = filtro.protocollo;
        
        this.properties = filtro.properties;
    	
	}
    
  
	
	public boolean isPartial() {
		return this.partial;
	}
	
    public void setPartial(boolean partial) {
		this.partial = partial;
	}
    
    /**
     * Mappa per gli offset
     * In caso di search complessa (union) mi serve questa mappa per gestire gli offset
     * offsetmap[0] = offset correlazione
     * offsetmap[1] = offset diagnostici
     * offsetmap[2] = offset notexist
     */
    public long[] getOffsetMap() {
		return this.offsetMap;
	}
    /**
     * Mappa per gli offset
     * In caso di search complessa (union) mi serve questa mappa per gestire gli offset
     * offsetmap[0] = offset correlazione
     * offsetmap[1] = offset diagnostici
     * offsetmap[2] = offset notexist
     */
    public void setOffsetMap(long[] offsetMap) {
		this.offsetMap = offsetMap;
	}
    
	public int getOffset() {
		return this.offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public int getLimit() {
		return this.limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
    public boolean isAsc() {
		return this.asc;
	}

	public void setAsc(boolean asc) {
		this.asc = asc;
	}
	
	@Override
	public String toString() {
		return super.toString() + " " + 
			" offset ["+this.offset+"]" +
			" limit  ["+this.limit+"]" +
			" asc  ["+this.asc+"]";
	}

 
}


