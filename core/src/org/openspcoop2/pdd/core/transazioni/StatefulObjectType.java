
package org.openspcoop2.pdd.core.transazioni;

import java.io.Serializable;




public enum StatefulObjectType implements Serializable{

	MSGDIAGNOSTICO,
	TRACCIA,
	MESSAGGIO,
	DATA_USCITA_RICHIESTA,DATA_INGRESSO_RISPOSTA,
	DIMENSIONE_USCITA_RICHIESTA,DIMENSIONE_INGRESSO_RISPOSTA,
	SCENARIO_COOPERAZIONE, TIPO_CONNETTORE, LOCATION,
	CODICE_TRASPORTO_RICHIESTA,
	OUT_REQUEST_STATEFUL_OBJECT,IN_RESPONSE_STATEFUL_OBJECT;

	@Override
	public String toString(){
		return this.name();
	}
	public boolean equals(StatefulObjectType esito){
		return this.toString().equals(esito.toString());
	}

}

