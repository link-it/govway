package org.openspcoop2.core.controllo_traffico.beans;

public class IDUnivocoGroupByPolicyMapId extends IDUnivocoGroupByPolicy  {

	private static final long serialVersionUID = 1L;
	
	private String uniqueMapId = QUALSIASI;
	
	public IDUnivocoGroupByPolicyMapId() {
	}
	
	public IDUnivocoGroupByPolicyMapId(IDUnivocoGroupByPolicy idSuper, String uniqueMapId) {
		this.setAzione(idSuper.getAzione());
		this.setErogatore(idSuper.getErogatore());
		this.setFruitore(idSuper.getFruitore());
		this.setIdentificativoAutenticato(idSuper.getIdentificativoAutenticato());
		this.setNomeKey(idSuper.getNomeKey());
		this.setProtocollo(idSuper.getProtocollo());
		this.setRuoloPorta(idSuper.getRuoloPorta());
		this.setServizio(idSuper.getServizio());
		this.setServizioApplicativoErogatore(idSuper.getServizioApplicativoErogatore());
		this.setServizioApplicativoFruitore(idSuper.getServizioApplicativoFruitore());
		this.setTipoKey(idSuper.getTipoKey());
		this.setTokenClientId(idSuper.getTokenClientId());
		this.setTokenEMail(idSuper.getTokenEMail());
		this.setTokenIssuer(idSuper.getTokenIssuer());
		this.setTokenSubject(idSuper.getTokenSubject());
		this.setTokenUsername(idSuper.getTokenUsername());
		this.setUniqueMapId(uniqueMapId);
		this.setValoreKey(idSuper.getValoreKey());
	}
	
	@Override
	public boolean match(IDUnivocoGroupByPolicy filtro){
		if (filtro instanceof IDUnivocoGroupByPolicyMapId) {
			return this.uniqueMapId.equals(((IDUnivocoGroupByPolicyMapId) filtro).uniqueMapId) && super.match(filtro);
		} else {
			return super.match(filtro);
		}
	}
	
	
	@Override
	public boolean equals(Object param){
		if(param==null || !(param instanceof IDUnivocoGroupByPolicyMapId))
			return false;
		return this.match((IDUnivocoGroupByPolicyMapId) param);
	}
	
	
	public String getUniqueMapId() {
		return this.uniqueMapId;
	}
	
	public void setUniqueMapId(String value) {
		this.uniqueMapId = value;
	}
	
	@Override
	public String toString(boolean filterGroupByNotSet){
		
		StringBuilder bf = new StringBuilder(super.toString(filterGroupByNotSet));

		if(!QUALSIASI.equals(this.uniqueMapId) || !filterGroupByNotSet){
			if(filterGroupByNotSet){
				if(bf.length()>0){
					bf.append("\n");
				}
				bf.append("\t");
			}
			else{
				bf.append(" ");
			}
			bf.append("UniqueMapId:");
			if(filterGroupByNotSet){
				bf.append(" ");
			}
			bf.append(this.uniqueMapId);
		}
		
		return bf.toString();
	}
	
	
}
