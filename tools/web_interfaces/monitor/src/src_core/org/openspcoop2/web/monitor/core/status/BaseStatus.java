package org.openspcoop2.web.monitor.core.status;


public class BaseStatus implements IStatus {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

	private String nome;

	private SondaStatus stato;
	
	protected String _value_stato;

	private String descrizione;

	public BaseStatus() {

	}

	@Override
	public String getNome() {
		return this.nome;
	}

	@Override
	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public SondaStatus getStato() {
		return this.stato;
	}

	@Override
	public void setStato(SondaStatus stato) {
		this.stato = stato;
	}

	@Override
	public String getDescrizione() {
		return this.descrizione;
	}

	@Override
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String get_value_stato() {
		if(this.stato == null){
	    	return null;
	    }else{
	    	return this.stato.toString();
	    }
	}

	public void set_value_stato(String _value_stato) {
		this.stato = (SondaStatus) SondaStatus.toEnumConstantFromString(_value_stato);
	}

 

}
