package org.openspcoop2.monitor.engine.config;

import org.openspcoop2.core.transazioni.constants.TipoMessaggio;

/**
 * TransactionResource
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransactionResource {

	private TipoMessaggio tipoMessaggio;
	private String nome;
	private String valore;
	
	public TipoMessaggio getTipoMessaggio() {
		return this.tipoMessaggio;
	}
	public void setTipoMessaggio(TipoMessaggio tipoMessaggio) {
		this.tipoMessaggio = tipoMessaggio;
	}
	public String getNome() {
		return this.nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getValore() {
		return this.valore;
	}
	public void setValore(String valore) {
		this.valore = valore;
	}
	
}
