package org.openspcoop2.core.id;

public class IdentificativiFruizione implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private IDSoggetto soggettoFruitore;
	private IDServizio idServizio;
	
	public IDSoggetto getSoggettoFruitore() {
		return this.soggettoFruitore;
	}
	public void setSoggettoFruitore(IDSoggetto soggettoFruitore) {
		this.soggettoFruitore = soggettoFruitore;
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
		if(this.soggettoFruitore!=null)
			bf.append("SoggettoFruitore:"+this.soggettoFruitore);
		else
			bf.append("SoggettoFruitore:NonDefinita");
		bf.append(" ");
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
		IdentificativiFruizione id = (IdentificativiFruizione) object;
		
		if(this.soggettoFruitore==null){
			if(id.soggettoFruitore!=null)
				return false;
		}else{
			if(this.soggettoFruitore.equals(id.soggettoFruitore)==false)
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
	public IdentificativiFruizione clone(){
		IdentificativiFruizione id = new IdentificativiFruizione();
		if(this.soggettoFruitore!=null){
			id.soggettoFruitore = this.soggettoFruitore.clone();
		}
		if(this.idServizio!=null){
			id.idServizio = this.idServizio.clone();
		}
		return id;
	}
}
