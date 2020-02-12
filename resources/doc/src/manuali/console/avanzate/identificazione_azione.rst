.. _identificazioneAzione:

Modalità di identificazione dell'azione
---------------------------------------

Nel contesto dei servizi Soap, sia erogazioni che fruizioni, si ha la
possibilità di selezionare una tra diverse opzioni che riguardano la
modalità di identificazione dell'azione. Dopo aver acceduto la sezione
*URL di Invocazione*, relativamente alla fruizione o erogazione, si può
selezionare una tra le seguenti opzioni:

-  *Contenuto* (Soap e Rest): il dato viene ricavato dal messaggio
   di richiesta utilizzando come criterio l'espressione XPath o JsonPath indicata
   nel campo *Pattern* sottostante.

-  *Header HTTP* (Soap e Rest): il dato viene ricavato da un valore
   passato come Http Header. Il campo sottostante consente di
   specificare il nome di tale header.

-  *Header di Integrazione* (Soap e Rest): il dato viene ricavato dall'header di
   integrazione fornito con il messaggio di richiesta. Per conoscere
   come gli applicativi client forniscono tale informazione vedere la
   sezione :ref:`headerClientGW`.

-  *Specifica di Interfaccia dell'API* (Soap e Rest): il dato viene ricavato in automatico
   sulla base delle informazioni fornite con la richiesta (messaggio e
   parametri) confrontandole con la descrizione dell'interfaccia dell'API.

-  *Url di Invocazione* (Soap): il dato viene ricavato dinamicamente dalla url di
   invocazione utilizzando come criterio l'espressione regolare inserita
   nel campo *Espressione Regolare* sottostante.

-  *SOAPAction* (Soap): Questa opzione consente di ricavare il
   dato dal campo *SOAPAction* presente nell'header di trasporto delle
   comunicazioni SOAP.

Attivando il flag *Identificazione tramite API*, in caso di fallimento
dell'identificazione dell'azione nella modalità prevista al passo
precedente, si tenterà di utilizzare la modalità "Specifica di Interfaccia dell'API" come
seconda opzione.

Il campo *Azioni* illustra l'elenco delle azioni presenti per semplice
comodità.

