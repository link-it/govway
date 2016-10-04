/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


package org.openspcoop2.pdd.mdb.threads;


import org.openspcoop2.pdd.core.state.OpenSPCoopStateException;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.mdb.EsitoLib;
import org.openspcoop2.pdd.mdb.GenericMessage;
import org.openspcoop2.pdd.mdb.InoltroRisposte;


/**
 * Thread che si occupa di costruire un InoltroRisposteMessage a partire dall'id di un
 * messaggio. Una volta ricostruito il messaggio invoca la libreria InoltroRisposte e 
 * a seconda del booleano da essa ritornato marca il messaggio affinche' venga riprocessato
 * oppure reso disponibile per il modulo successivo
 * 
 *
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 * 
 */
public class InoltroRisposteWorker extends ModuloAlternativoWorker{

	 


	/* ********  F I E L D S  P R I V A T I S T A T I C I  ******** */

	/** Variabile che indica il Nome del modulo dell'architettura di OpenSPCoop rappresentato da questa classe */
	public final static String idModulo = "InoltroRisposte";

	public InoltroRisposteWorker(MessageIde ide){
		super(InoltroRisposteWorker.idModulo, ide);
	}
	
	@Override
	protected EsitoLib onMessage(GenericMessage messaggio, String idMessaggio) throws OpenSPCoopStateException{
		EsitoLib esito = new EsitoLib();
		InoltroRisposte lib = null;
		try{
			lib = new InoltroRisposte(this.log);
		}catch(Exception e){
			throw new OpenSPCoopStateException(e.getMessage(),e);
		}
		OpenSPCoopStateful openspcoopstate = new OpenSPCoopStateful();
		openspcoopstate.setMessageLib( messaggio  );
		openspcoopstate.setIDMessaggioSessione(idMessaggio);
		esito = lib.onMessage(openspcoopstate);
		return esito;
	}
}
