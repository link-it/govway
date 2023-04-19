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


package org.openspcoop2.pdd.mdb.threads;


import org.openspcoop2.pdd.core.state.OpenSPCoopStateException;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.mdb.EsitoLib;
import org.openspcoop2.pdd.mdb.GenericMessage;
import org.openspcoop2.pdd.mdb.Imbustamento;

/**
 * @author Fabio Tronci (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ImbustamentoWorker extends ModuloAlternativoWorker implements IWorker {

	 


	/* ********  F I E L D S  P R I V A T I S T A T I C I  ******** */

	/** Variabile che indica il Nome del modulo dell'architettura di OpenSPCoop rappresentato da questa classe */
	public static final String idModulo = "Imbustamento";

	public ImbustamentoWorker(MessageIde ide){
		super(ImbustamentoWorker.idModulo, ide);
	}
	
	@Override
	protected EsitoLib onMessage(GenericMessage messaggio, String idMessaggio) throws OpenSPCoopStateException{
		EsitoLib esito = new EsitoLib();
		Imbustamento lib = null;
		try{
			lib = new Imbustamento(this.log);
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
