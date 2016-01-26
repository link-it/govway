/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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


import java.util.Vector;
import java.util.concurrent.BlockingQueue;

/**
 * @author Fabio Tronci (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SbustamentoRisposteProducer extends ModuloAlternativoProducer
		implements IProducer {
	private static String idModulo = "SbustamentoRisposte";
	
	public SbustamentoRisposteProducer(BlockingQueue<IWorker> coda) {
		super(SbustamentoRisposteProducer.idModulo,coda);
	}

	@Override
	public void creaWorkers(Vector<MessageIde> messaggiTrovati) {
		for (int i=0;i<messaggiTrovati.size();i++){
			MessageIde ide = messaggiTrovati.elementAt(i);
			System.out.println(this.ID_MODULO+ "Producer: trovato messaggio, creo task...");
			SbustamentoRisposteWorker task = new SbustamentoRisposteWorker(ide);
			try {
				this.coda.put(task);
			} catch (InterruptedException e) {
				System.out.println(this.ID_MODULO+" :errore "+e);
			}
		}
			
	}
}
