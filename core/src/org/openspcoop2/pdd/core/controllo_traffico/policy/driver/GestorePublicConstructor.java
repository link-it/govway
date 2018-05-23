package org.openspcoop2.pdd.core.controllo_traffico.policy.driver;

import java.io.ByteArrayOutputStream;

public class GestorePublicConstructor {

	public static void test(){
		
		// InMemory
		try{
			byte [] a = GestorePolicyAttiveInMemory.getImplDescr().getBytes();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			bout.write(a);
			bout.flush();
			bout.close();
			System.out.println(bout.toString());
		}catch(Exception e){}
		
		// Ws
		try{
			byte [] a = GestorePolicyAttiveWS.getImplDescr().getBytes();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			bout.write(a);
			bout.flush();
			bout.close();
			System.out.println(bout.toString());
		}catch(Exception e){}

		
	}
	
}
