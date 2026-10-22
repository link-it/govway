/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

package org.openspcoop2.utils.beans.test;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.beans.BaseBean;
import org.openspcoop2.utils.beans.CloneMode;
import org.openspcoop2.utils.jaxb.DecimalWrapper;

/**
 * Verifica che l'implementazione del clone di {@link BaseBean} con cache delle informazioni di reflection
 * (modalità {@link CloneMode#REFLECTION_CACHE}) produca esattamente lo stesso risultato della modalità {@link CloneMode#REFLECTION}
 * in tutti i casi gestiti: field statici, DecimalWrapper e tipi compatibili, valori nulli, array di byte, enum,
 * liste di ogni tipo supportato (e non supportato) con elementi clonabili, stringhe, nulli e altri oggetti,
 * field clonabili, field copiati per riferimento, field '_default', metodi get/set/clone mancanti ed eccezioni.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BaseBeanCloneTest {

	public static void main(String[] args) throws Exception {
		test();
	}

	private static int verifiche = 0;

	public static void test() throws Exception {
		verifiche = 0;
		CloneMode modalitaOriginale = BaseBean.getCloneMode();
		try {
			testBeanCompleto();
			testBeanConValoriNulli();
			testCloneRipetuto();
			testErrori();
			testSelezioneImplementazione();
		}finally {
			BaseBean.setCloneMode(modalitaOriginale);
		}
		System.out.println("Test clone BaseBean completato: "+verifiche+" verifiche effettuate");
	}


	/* ********** BEAN DI TEST ********* */

	public enum TipoTest { UNO, DUE }

	/** Bean annidato che dichiara direttamente l'interfaccia Cloneable: viene clonato in profondità */
	public static class ChildBean extends BaseBean implements Cloneable {
		private static final long serialVersionUID = 1L;
		private String nome;
		public ChildBean() {}
		public ChildBean(String nome) { this.nome = nome; }
		public String getNome() { return this.nome; }
		public void setNome(String nome) { this.nome = nome; }
	}
	/** Bean che eredita Cloneable senza dichiararlo: viene copiato per riferimento (come nella modalità REFLECTION) */
	public static class InheritedCloneableBean extends ChildBean {
		private static final long serialVersionUID = 1L;
		public InheritedCloneableBean() {}
		public InheritedCloneableBean(String nome) { super(nome); }
	}

	/** Bean che copre tutti i casi gestiti dal clone */
	public static class FullBean extends BaseBean implements Cloneable {
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unused")
		private static String campoStatico = "statico";      // field statico: ignorato
		private DecimalWrapper decimal;                     // tipo DecimalWrapper: ignorato (non serve il getter)
		private Object oggetto;                             // tipo assegnabile da DecimalWrapper: ignorato
		private Serializable serializzabile;                // tipo assegnabile da DecimalWrapper: ignorato

		private byte[] bytes;                               // array di byte: copia
		private byte[] _default;                            // nome '_default': metodi getDefault/setDefault
		private TipoTest tipo;                              // enum: stesso valore

		private List<Object> list;                          // liste supportate: copia degli elementi
		private ArrayList<Object> arrayList;
		private Vector<Object> vector;
		private Stack<Object> stack;
		private LinkedList<Object> linkedList;
		private CopyOnWriteArrayList<Object> copyOnWriteArrayList;
		private List<Object> listNonSupportata;             // tipo di lista non gestito: nel clone diventa null

		private ChildBean child;                            // dichiara Cloneable: clone in profondità
		private InheritedCloneableBean inherited;           // eredita Cloneable: copia per riferimento
		private Date date;                                  // Date dichiara Cloneable: clone in profondità
		private String stringa;                             // copia per riferimento
		private Map<String, String> map;                    // copia per riferimento
		private int numero;                                 // primitivo: copia del valore
		private boolean flag;                               // primitivo: copia del valore

		public DecimalWrapper decimalValue() { return this.decimal; }
		public void decimalValue(DecimalWrapper d) { this.decimal = d; }
		public Object oggettoValue() { return this.oggetto; }
		public void oggettoValue(Object o) { this.oggetto = o; }
		public Serializable serializzabileValue() { return this.serializzabile; }
		public void serializzabileValue(Serializable s) { this.serializzabile = s; }

		public byte[] getBytes() { return this.bytes; }
		public void setBytes(byte[] bytes) { this.bytes = bytes; }
		public byte[] getDefault() { return this._default; }
		public void setDefault(byte[] d) { this._default = d; }
		public TipoTest getTipo() { return this.tipo; }
		public void setTipo(TipoTest tipo) { this.tipo = tipo; }
		public List<Object> getList() { return this.list; }
		public void setList(List<Object> list) { this.list = list; }
		public ArrayList<Object> getArrayList() { return this.arrayList; }
		public void setArrayList(ArrayList<Object> arrayList) { this.arrayList = arrayList; }
		public Vector<Object> getVector() { return this.vector; }
		public void setVector(Vector<Object> vector) { this.vector = vector; }
		public Stack<Object> getStack() { return this.stack; }
		public void setStack(Stack<Object> stack) { this.stack = stack; }
		public LinkedList<Object> getLinkedList() { return this.linkedList; }
		public void setLinkedList(LinkedList<Object> linkedList) { this.linkedList = linkedList; }
		public CopyOnWriteArrayList<Object> getCopyOnWriteArrayList() { return this.copyOnWriteArrayList; }
		public void setCopyOnWriteArrayList(CopyOnWriteArrayList<Object> c) { this.copyOnWriteArrayList = c; }
		public List<Object> getListNonSupportata() { return this.listNonSupportata; }
		public void setListNonSupportata(List<Object> l) { this.listNonSupportata = l; }
		public ChildBean getChild() { return this.child; }
		public void setChild(ChildBean child) { this.child = child; }
		public InheritedCloneableBean getInherited() { return this.inherited; }
		public void setInherited(InheritedCloneableBean inherited) { this.inherited = inherited; }
		public Date getDate() { return this.date; }
		public void setDate(Date date) { this.date = date; }
		public String getStringa() { return this.stringa; }
		public void setStringa(String stringa) { this.stringa = stringa; }
		public Map<String, String> getMap() { return this.map; }
		public void setMap(Map<String, String> map) { this.map = map; }
		public int getNumero() { return this.numero; }
		public void setNumero(int numero) { this.numero = numero; }
		public boolean getFlag() { return this.flag; }
		public void setFlag(boolean flag) { this.flag = flag; }
	}

	/** Classe senza field */
	public static class EmptyBean extends BaseBean implements Cloneable {
		private static final long serialVersionUID = 1L;
	}
	/** Field con nome di un solo carattere */
	public static class SingleCharFieldBean extends BaseBean implements Cloneable {
		private static final long serialVersionUID = 1L;
		private byte[] b;
		public byte[] getB() { return this.b; }
		public void setB(byte[] b) { this.b = b; }
	}

	/** Field senza metodo get: errore */
	public static class NoGetterBean extends BaseBean implements Cloneable {
		private static final long serialVersionUID = 1L;
		@SuppressWarnings("unused")
		private String valore = "x";
	}
	/** Field '_default' senza getDefault: errore */
	public static class DefaultNoGetterBean extends BaseBean implements Cloneable {
		private static final long serialVersionUID = 1L;
		@SuppressWarnings("unused")
		private String _default = "x";
	}
	/** Field '_default' con getDefault ma senza setDefault: errore solo se valorizzato */
	public static class DefaultNoSetterBean extends BaseBean implements Cloneable {
		private static final long serialVersionUID = 1L;
		private byte[] _default;
		public byte[] getDefault() { return this._default; }
		public void valorizza(byte[] b) { this._default = b; }
	}
	/** Field con get ma senza set: errore solo se valorizzato */
	public static class NoSetterBean extends BaseBean implements Cloneable {
		private static final long serialVersionUID = 1L;
		private byte[] bytes;
		public byte[] getBytes() { return this.bytes; }
		public void valorizza(byte[] b) { this.bytes = b; }
	}
	/** Classe che non implementa Cloneable: errore di Object.clone() */
	public static class NotCloneableBean extends BaseBean {
		private static final long serialVersionUID = 1L;
		private String valore = "x";
		public String getValore() { return this.valore; }
		public void setValore(String valore) { this.valore = valore; }
	}
	/** Tipo che dichiara Cloneable senza metodo clone pubblico */
	public static class CloneableSenzaClonePubblico implements Cloneable {
		@Override
		public String toString() { return "CloneableSenzaClonePubblico"; }
	}
	/** Field il cui tipo dichiara Cloneable senza metodo clone pubblico: errore solo se valorizzato */
	public static class CloneNonPubblicoBean extends BaseBean implements Cloneable {
		private static final long serialVersionUID = 1L;
		private CloneableSenzaClonePubblico valore;
		public CloneableSenzaClonePubblico getValore() { return this.valore; }
		public void setValore(CloneableSenzaClonePubblico valore) { this.valore = valore; }
	}
	/** Lista con un elemento Cloneable senza metodo clone pubblico: errore */
	public static class ListaElementoNonClonabileBean extends BaseBean implements Cloneable {
		private static final long serialVersionUID = 1L;
		private List<Object> list;
		public List<Object> getList() { return this.list; }
		public void setList(List<Object> list) { this.list = list; }
	}
	/** Metodo get che solleva un'eccezione: errore */
	public static class GetterEccezioneBean extends BaseBean implements Cloneable {
		private static final long serialVersionUID = 1L;
		@SuppressWarnings("unused")
		private String valore;
		public String getValore() { throw new IllegalStateException("errore getter"); }
		public void setValore(String valore) { this.valore = valore; }
	}
	/** Field di tipo Cloneable: viene gestito come array di byte (errore se il valore non è un array di byte) */
	public static class CampoCloneableBean extends BaseBean implements Cloneable {
		private static final long serialVersionUID = 1L;
		private Cloneable valore;
		public Cloneable getValore() { return this.valore; }
		public void setValore(Cloneable valore) { this.valore = valore; }
	}


	/* ********** TEST ********* */

	private static List<Object> elementi(List<Object> l) {
		l.add(new ChildBean("elemento"));      // Cloneable: clonato
		l.add(new String("testo"));            // String: nuova istanza
		l.add(null);                           // null: mantenuto
		l.add(Integer.valueOf(5));             // altro oggetto: non riportato nel clone
		return l;
	}
	private static FullBean buildFullBean() {
		FullBean b = new FullBean();
		b.decimalValue(new DecimalWrapper());
		b.oggettoValue(new Object());
		b.serializzabileValue("serializzabile");
		b.setBytes(new byte[] {1,2,3});
		b.setDefault(new byte[] {9});
		b.setTipo(TipoTest.DUE);
		b.setList(elementi(new ArrayList<>()));
		b.setArrayList((ArrayList<Object>) elementi(new ArrayList<>()));
		b.setVector((Vector<Object>) elementi(new Vector<>()));
		b.setStack((Stack<Object>) elementi(new Stack<>()));
		b.setLinkedList((LinkedList<Object>) elementi(new LinkedList<>()));
		b.setCopyOnWriteArrayList((CopyOnWriteArrayList<Object>) elementi(new CopyOnWriteArrayList<>()));
		b.setListNonSupportata(Collections.unmodifiableList(elementi(new ArrayList<>())));
		b.setChild(new ChildBean("figlio"));
		b.setInherited(new InheritedCloneableBean("ereditato"));
		b.setDate(new Date(1000L));
		b.setStringa("stringa");
		Map<String, String> m = new HashMap<>();
		m.put("k", "v");
		b.setMap(m);
		b.setNumero(42);
		b.setFlag(true);
		return b;
	}

	private static void testBeanCompleto() throws Exception {
		FullBean originale = buildFullBean();
		FullBean cloneOld = (FullBean) originale.clone(CloneMode.REFLECTION);
		FullBean cloneNew = (FullBean) originale.clone(CloneMode.REFLECTION_CACHE);
		verificaEquivalenza("bean completo", originale, cloneOld, cloneNew);

		// verifiche puntuali sul comportamento atteso (uguale per entrambe le implementazioni)
		for (FullBean c : new FullBean[] {cloneOld, cloneNew}) {
			check("istanza differente", c!=originale);
			check("decimal (ignorato): stesso riferimento", c.decimalValue()==originale.decimalValue());
			check("oggetto (ignorato): stesso riferimento", c.oggettoValue()==originale.oggettoValue());
			check("serializzabile (ignorato): stesso riferimento", c.serializzabileValue()==originale.serializzabileValue());
			check("bytes: copia", c.getBytes()!=originale.getBytes() && Arrays.equals(c.getBytes(), originale.getBytes()));
			check("_default: copia", c.getDefault()!=originale.getDefault() && Arrays.equals(c.getDefault(), originale.getDefault()));
			check("enum: stesso valore", c.getTipo()==TipoTest.DUE);
			for (List<Object> l : List.of(c.getList(), c.getArrayList(), c.getVector(), c.getStack(), c.getLinkedList(), c.getCopyOnWriteArrayList())) {
				check("lista: 3 elementi (Integer non riportato)", l.size()==3);
				check("lista: elemento Cloneable clonato", l.get(0) instanceof ChildBean && "elemento".equals(((ChildBean)l.get(0)).getNome()));
				check("lista: stringa copiata", "testo".equals(l.get(1)));
				check("lista: null mantenuto", l.get(2)==null);
			}
			check("lista: tipo mantenuto (Vector)", c.getVector().getClass()==Vector.class);
			check("lista: tipo mantenuto (Stack)", c.getStack().getClass()==Stack.class);
			check("lista: tipo mantenuto (LinkedList)", c.getLinkedList().getClass()==LinkedList.class);
			check("lista: tipo mantenuto (CopyOnWriteArrayList)", c.getCopyOnWriteArrayList().getClass()==CopyOnWriteArrayList.class);
			check("lista: elemento clonato differente", c.getList().get(0)!=originale.getList().get(0));
			check("lista non supportata: null nel clone", c.getListNonSupportata()==null);
			check("child: clone in profondità", c.getChild()!=originale.getChild() && "figlio".equals(c.getChild().getNome()));
			check("inherited: stesso riferimento", c.getInherited()==originale.getInherited());
			check("date: clone", c.getDate()!=originale.getDate() && c.getDate().equals(originale.getDate()));
			check("stringa: stesso riferimento", stessaIstanza(c.getStringa(), originale.getStringa()));
			check("map: stesso riferimento", c.getMap()==originale.getMap());
			check("primitivi", c.getNumero()==42 && c.getFlag());
		}
	}

	private static void testBeanConValoriNulli() throws Exception {
		// tutti i field non valorizzati: nessun get/set/clone viene eseguito
		FullBean vuoto = new FullBean();
		verificaEquivalenza("bean vuoto", vuoto, (FullBean) vuoto.clone(CloneMode.REFLECTION), (FullBean) vuoto.clone(CloneMode.REFLECTION_CACHE));

		// metodi set o clone mancanti non generano errori se il field non è valorizzato
		verificaEquivalenza("set mancante con valore nullo", new NoSetterBean(), new NoSetterBean().clone(CloneMode.REFLECTION), new NoSetterBean().clone(CloneMode.REFLECTION_CACHE));
		verificaEquivalenza("setDefault mancante con valore nullo", new DefaultNoSetterBean(), new DefaultNoSetterBean().clone(CloneMode.REFLECTION), new DefaultNoSetterBean().clone(CloneMode.REFLECTION_CACHE));
		verificaEquivalenza("clone non pubblico con valore nullo", new CloneNonPubblicoBean(), new CloneNonPubblicoBean().clone(CloneMode.REFLECTION), new CloneNonPubblicoBean().clone(CloneMode.REFLECTION_CACHE));
		CampoCloneableBean campoCloneable = new CampoCloneableBean();
		campoCloneable.setValore(new byte[] {7});
		verificaEquivalenza("field Cloneable con array di byte", campoCloneable, campoCloneable.clone(CloneMode.REFLECTION), campoCloneable.clone(CloneMode.REFLECTION_CACHE));

		// classe senza field e field con nome di un solo carattere
		EmptyBean empty = new EmptyBean();
		verificaEquivalenza("classe senza field", empty, empty.clone(CloneMode.REFLECTION), empty.clone(CloneMode.REFLECTION_CACHE));
		SingleCharFieldBean singleChar = new SingleCharFieldBean();
		singleChar.setB(new byte[] {3});
		SingleCharFieldBean singleCharOld = (SingleCharFieldBean) singleChar.clone(CloneMode.REFLECTION);
		SingleCharFieldBean singleCharNew = (SingleCharFieldBean) singleChar.clone(CloneMode.REFLECTION_CACHE);
		verificaEquivalenza("field con nome di un carattere", singleChar, singleCharOld, singleCharNew);
		check("field con nome di un carattere: copia", singleCharNew.getB()!=singleChar.getB() && Arrays.equals(singleCharNew.getB(), singleChar.getB()));
	}

	private static void testCloneRipetuto() throws Exception {
		// clone ripetuti: la seconda invocazione utilizza le informazioni in cache
		FullBean originale = buildFullBean();
		for (int i = 0; i < 3; i++) {
			verificaEquivalenza("clone ripetuto "+i, originale, (FullBean) originale.clone(CloneMode.REFLECTION), (FullBean) originale.clone(CloneMode.REFLECTION_CACHE));
		}
		// clone di un clone
		FullBean clone = (FullBean) originale.clone(CloneMode.REFLECTION_CACHE);
		verificaEquivalenza("clone di un clone", clone, (FullBean) clone.clone(CloneMode.REFLECTION), (FullBean) clone.clone(CloneMode.REFLECTION_CACHE));
	}

	private static void testErrori() throws Exception {
		verificaStessoErrore("get mancante", new NoGetterBean());
		verificaStessoErrore("getDefault mancante", new DefaultNoGetterBean());

		NoSetterBean noSetter = new NoSetterBean();
		noSetter.valorizza(new byte[] {1});
		verificaStessoErrore("set mancante", noSetter);

		DefaultNoSetterBean noSetDefault = new DefaultNoSetterBean();
		noSetDefault.valorizza(new byte[] {1});
		verificaStessoErrore("setDefault mancante", noSetDefault);

		verificaStessoErrore("classe non Cloneable", new NotCloneableBean());

		CloneNonPubblicoBean cloneNonPubblico = new CloneNonPubblicoBean();
		cloneNonPubblico.setValore(new CloneableSenzaClonePubblico());
		verificaStessoErrore("clone non pubblico", cloneNonPubblico);

		ListaElementoNonClonabileBean lista = new ListaElementoNonClonabileBean();
		List<Object> l = new ArrayList<>();
		l.add(new CloneableSenzaClonePubblico());
		lista.setList(l);
		verificaStessoErrore("elemento di lista senza clone pubblico", lista);
		verificaStessoErrore("elemento di lista senza clone pubblico (ripetuto)", lista);

		verificaStessoErrore("eccezione nel get", new GetterEccezioneBean());

		CampoCloneableBean campoCloneable = new CampoCloneableBean();
		campoCloneable.setValore(new Date(1000L));
		verificaStessoErrore("field Cloneable con valore diverso da array di byte", campoCloneable);
	}

	private static void testSelezioneImplementazione() throws Exception {
		FullBean originale = buildFullBean();
		
		BaseBean.setCloneMode(null);
		check("modalità nulla: default REFLECTION_CACHE", CloneMode.REFLECTION_CACHE.equals(BaseBean.getCloneMode()));
		
		BaseBean.setCloneMode(CloneMode.REFLECTION_CACHE);
		check("modalità REFLECTION_CACHE", CloneMode.REFLECTION_CACHE.equals(BaseBean.getCloneMode()));
		verificaEquivalenza("clone() in modalità REFLECTION_CACHE", originale, (FullBean) originale.clone(CloneMode.REFLECTION), (FullBean) originale.clone());
		
		BaseBean.setCloneMode(CloneMode.REFLECTION);
		check("modalità REFLECTION", CloneMode.REFLECTION.equals(BaseBean.getCloneMode()));
		verificaEquivalenza("clone() in modalità REFLECTION", originale, (FullBean) originale.clone(), (FullBean) originale.clone(CloneMode.REFLECTION_CACHE));
		
		verificaStessoErrore("errore in modalità REFLECTION", new NoGetterBean());
		
		BaseBean.setCloneMode(CloneMode.REFLECTION_CACHE);
	}
	
	
	/* ********** UTILITY ********* */

	private static void check(String descrizione, boolean condizione) throws Exception {
		verifiche++;
		if(!condizione) {
			throw new Exception("Verifica fallita: "+descrizione);
		}
	}

	private static void verificaEquivalenza(String caso, Object originale, Object cloneOld, Object cloneNew) throws Exception {
		check(caso+": classe del clone", cloneOld.getClass()==originale.getClass() && cloneNew.getClass()==originale.getClass());
		String firmaOld = firma(originale, cloneOld);
		String firmaNew = firma(originale, cloneNew);
		if(!firmaOld.equals(firmaNew)) {
			throw new Exception("Clone differente nel caso '"+caso+"'\nprecedente: "+firmaOld+"\nnuovo:      "+firmaNew);
		}
		verifiche++;
	}

	private static void verificaStessoErrore(String caso, BaseBean bean) throws Exception {
		Throwable errOld = null;
		Throwable errNew = null;
		try {
			bean.clone(CloneMode.REFLECTION);
		}catch(Throwable t) {
			errOld = t;
		}
		try {
			bean.clone(CloneMode.REFLECTION_CACHE);
		}catch(Throwable t) {
			errNew = t;
		}
		check(caso+": errore atteso in modalità REFLECTION", errOld instanceof UtilsRuntimeException);
		check(caso+": errore atteso in modalità REFLECTION_CACHE", errNew instanceof UtilsRuntimeException);
		String descOld = descriviErrore(errOld);
		String descNew = descriviErrore(errNew);
		if(!descOld.equals(descNew)) {
			throw new Exception("Errore differente nel caso '"+caso+"'\nprecedente: "+descOld+"\nnuovo:      "+descNew);
		}
		verifiche++;
	}
	private static String descriviErrore(Throwable t) {
		StringBuilder sb = new StringBuilder();
		while(t!=null) {
			sb.append(t.getClass().getName()).append(": ").append(t.getMessage()).append(" | ");
			t = t.getCause();
		}
		return sb.toString();
	}

	/** Descrizione del clone rispetto all'originale: per ogni valore indica se è lo stesso riferimento dell'originale o una copia, e il suo contenuto */
	private static String firma(Object originale, Object clone) throws Exception {
		if(clone==null) {
			return "null";
		}
		if(clone==originale) {
			return "STESSO_RIFERIMENTO";
		}
		if(clone instanceof byte[]) {
			return "COPIA:byte[]"+Arrays.toString((byte[])clone);
		}
		if(clone instanceof List<?>) {
			List<?> lClone = (List<?>) clone;
			List<?> lOriginale = (originale instanceof List<?>) ? (List<?>) originale : Collections.emptyList();
			StringBuilder sb = new StringBuilder("COPIA:"+clone.getClass().getName()+"[");
			for (Object e : lClone) {
				Object eOriginale = trovaElementoOriginale(lOriginale, e);
				sb.append(firma(eOriginale, e)).append(",");
			}
			return sb.append("]").toString();
		}
		if(clone instanceof BaseBean) {
			StringBuilder sb = new StringBuilder("COPIA:"+clone.getClass().getName()+"{");
			Class<?> c = clone.getClass();
			while(c!=null && c!=Object.class) {
				for (Field f : c.getDeclaredFields()) {
					if(Modifier.isStatic(f.getModifiers())) {
						continue;
					}
					f.setAccessible(true);
					Object vOriginale = (originale!=null && c.isInstance(originale)) ? f.get(originale) : null;
					Object vClone = f.get(clone);
					sb.append(c.getSimpleName()).append(".").append(f.getName()).append("=");
					if(vClone!=null && (vClone.getClass().isPrimitive() || vClone instanceof Number || vClone instanceof Boolean)) {
						sb.append(vClone);
					}
					else {
						sb.append(firma(vOriginale, vClone));
					}
					sb.append(";");
				}
				c = c.getSuperclass();
			}
			return sb.append("}").toString();
		}
		return "COPIA:"+clone.getClass().getName()+":"+clone;
	}
	private static Object trovaElementoOriginale(List<?> lOriginale, Object elemento) {
		for (Object o : lOriginale) {
			if(o==elemento) {
				return o;
			}
		}
		return null;
	}

	/** Confronto di identita' (non di contenuto): verifica che il clone mantenga lo stesso riferimento */
	private static boolean stessaIstanza(Object a, Object b) {
		return a == b;
	}
}
