package org.openspcoop2.core.id;

public class IdentificativiErogazione implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private IDSoggetto soggettoVirtuale;
	private IDServizio idServizio;
	
	public IDSoggetto getSoggettoVirtuale() {
		return this.soggettoVirtuale;
	}
	public void setSoggettoVirtuale(IDSoggetto soggettoVirtuale) {
		this.soggettoVirtuale = soggettoVirtuale;
	}
	public IDServizio getIdServizio() {
		return this.idServizio;
	}
	public void setIdServizio(IDServizio idServizio) {
		this.idServizio = idServizio;
	}
	
	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
		if(this.soggettoVirtuale!=null){
			bf.append("SoggettoVirtuale:"+this.soggettoVirtuale);
			bf.append(" ");
		}
		if(this.idServizio!=null)
			bf.append("Servizio:"+this.idServizio.toString());
		else
			bf.append("Servizio:NonDefinito");
		return bf.toString();
	}
	
	@Override 
	public boolean equals(Object object){
		if(object == null)
			return false;
		if(object.getClass().getName().equals(this.getClass().getName()) == false)
			return false;
		IdentificativiErogazione id = (IdentificativiErogazione) object;
		
		if(this.soggettoVirtuale==null){
			if(id.soggettoVirtuale!=null)
				return false;
		}else{
			if(this.soggettoVirtuale.equals(id.soggettoVirtuale)==false)
				return false;
		}
		
		if(this.idServizio==null){
			if(id.idServizio!=null)
				return false;
		}else{
			if(this.idServizio.equals(id.idServizio)==false)
				return false;
		}
		
		return true;
	}
	
	// Utile per usare l'oggetto in hashtable come chiave
	@Override
	public int hashCode(){
		return this.toString().hashCode();
	}
	
	@Override
	public IdentificativiErogazione clone(){
		IdentificativiErogazione id = new IdentificativiErogazione();
		if(this.soggettoVirtuale!=null){
			id.soggettoVirtuale = this.soggettoVirtuale.clone();
		}
		if(this.idServizio!=null){
			id.idServizio = this.idServizio.clone();
		}
		return id;
	}
}
