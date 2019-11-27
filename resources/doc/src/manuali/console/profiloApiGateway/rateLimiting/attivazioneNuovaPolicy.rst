.. _rateLimiting_attivazioneNuovaPolicy:

Registrazione di una policy
+++++++++++++++++++++++++++++++++++

Per attivare una nuova policy dalla sezione di rate limiting si procede
utilizzando il pulsante *Aggiungi* che apre il form di :numref:`erogazioneRateLimiting`.

   .. figure:: ../../_figure_console/ErogazioneRateLimiting_Aggiungi.png
    :scale: 50%
    :align: center
    :name: erogazioneRateLimiting

    Attivazione di una policy di Rate Limiting

Si compilano i campi seguenti:

-  *Policy*: la policy da attivare. Si compone di:

   -  *Nome*: Identificativo univoco della policy.

   -  *Stato*: Lo stato della policy. Sono
      disponibili le seguenti opzioni:

      -  *Abilitato*: le violazioni rilevate saranno gestite in maniera
         restrittiva (negazione del servizio).

      -  *WarningOnly*: la policy è abilitata in modalità
         WarningOnly. Questo significa che le violazioni rilevate
         saranno solo segnalate tramite messaggi diagnostici ma non ci
         saranno ripercussioni sull'elaborazione della richiesta.

      -  *Disabilitato*: La policy è disabilitata.

   -  *Elaborazione*: Indica quale azione attuare per la policy, nell'ambito del flusso di elaborazione delle policy di eguale metrica, nel caso in cui venga superato il controllo (maggiori dettagli sull'algoritmo di valutazione delle policy sono disponibili nella sezione :ref:`rateLimiting_criteriValutazione`):

      - *Interrompi*: non verranno valutate ulteriori policy che seguono nell'ordine tra quelle di eguale metrica.

      - *Prosegui*: si procede con la valutazione della successiva policy nell'ordine tra quelle di eguale metrica.

   -  *Identificazione Policy*: Scelta tra due opzioni:

        - *Scegli Criteri*: permette di indicare direttamente i criteri che la politica deve garantire; tra i criteri utilizzabili: la metrica (numero richieste, occupazione banda, tempi medi, ….), l’intervallo temporale (minuto, ora, giorno) e le condizioni di applicabilità (congestione, degrado prestazionale).

        - *Selezione dal Registro*: permette di utilizzare una politica arbitraria, precedentemente definita dall’utente.

	.. note::
		La descrizione che segue assume che venga attuata una identificazione della policy per criteri. Per i dettagli sulla configurazione di policy personalizzate dall'utente si faccia riferimento alla sezione :ref:`configurazioneRateLimiting_descrizione`.

   -  *Criteri*: devono essere forniti la metrica e l'intervallo di osservazione scelti tra i valori descritti in precedenza (:ref:`rateLimiting`). Possono inoltre essere indicate le seguenti opzioni:

        - *Applicata solo in presenza di Congestione del Traffico*: attivando questa opzione la policy risulta applicabile solo nel caso in cui il gateway sia entrato in modalità "Congestione", sulla base di quanto descritto nella sezione :ref:`traffico`.
        - *Applicata solo in presenza di Degrado Prestazionale*: attivando questa opzione la policy risulta applicabile solo nel caso in cui il gateway abbia rilevato un degrado prestazionale e cioè un tempo medio di risposta del servizio superiore alla soglia configurata.

- *Valori di Soglia*: Le soglie per la valutazione della policy:

    - *Ridefinisci Valori di Soglia*: Opzione che consente di variare la soglia predefinita.

    - *Soglia*: Questo campo riporta, in base alla metrica selezionata sopra, il valore di riferimento. Tale valore risulta modificabile attivando l'opzione al punto precedente.

    - *Raggruppamento*: In questa sezione è possibile attivare opzionalmente alcuni criteri per il raggruppamento dei dati utilizzati come soglie di confronto. Ad esempio se la policy limita a 20 il numero di richieste su base per minuti, significa che al raggiungimento della ventunesima richiesta, nella stessa finestra temporale, si otterrà una violazione della policy. Aggiungendo un raggruppamento per risorsa, saranno conteggiate separatamente le richieste in base alla specifica risorsa invocata. In questo caso la policy risulterà violata solo al raggiungimento della ventunesima richiesta, nella stessa finestra temporale, relativa alla medesima risorsa. È ammesso anche il raggruppamento su criteri multipli. La logica è del tutto analoga a quella dell'operatore GROUP BY del linguaggio SQL. I criteri di raggruppamento selezionabili sono:

       - *Risorsa/Azione*: il valore di soglia rappresenta il totale per ciascuna azione/risorsa

       - *Richiedente*: il valore di soglia rappresenta il totale ripartito per ciascun mittente

       - *Token*: il valore di soglia rappresenta il totale ripartito tra le richieste in base al token di provenienza. Si possono specificare i "claims" da prendere in considerazione per distinguere i token.

       - *Chiave*: il valore di soglia rappresente il totale ripartito tra le richieste raggruppate in base ad una chiave personalizzata il cui valore viene fornito secondo uno dei metodi selezionati tra i seguenti:

          -  *Header HTTP*: La chiave è presente nell'header di trasporto
             indicato nella proprietà "Nome".

          -  *Url di Invocazione*: La chiave è presente nella URL ricavabile tramite
             l'espressione regolare fornita nell'elemento seguente.

          -  *Parametro della Url*: La chiave viene fornita in modalità Form Encoded
             con il parametro indicato nell'elemento "Nome".

          -  *SOAPAction*: La chiave corrisponde al valore della
             SoapAction.

          -  *Contenuto*: La chiave è presente nel body del messaggio e
             viene ricavata tramite una espressione XPath o JsonPath fornito nell'elemento
             seguente.

          -  *Client IP*: La chiave corrisponde all'indirizzo IP del client.

          -  *X-Forwarded-For*: La chiave corrisponde all'indirizzo IP del client presente negli header http utilizzati per il mantenimento dell’IP di origine nel caso di nodi intermedi (es. X-Forwarded-For).

          -  *Plugin Personalizzato*: La chiave viene restituita tramite l'esecuzione
             di una classe il cui nome viene fornito con il campo "Tipo". Per maggiori dettagli si rimanda alla sezione :ref:`configurazioneRateLimiting_filtriRaggruppamentiPersonalizzati`

-  *Filtro*: Abilitando questa sezione è possibile indicare i criteri affinché la policy sia applicabile in base alle caratteristiche di ciascuna richiesta in ingresso. In assenza di filtro, la policy sarà valutata su tutte le richieste in ingresso che riguardano l'erogazione/fruizione che si sta configurando. Per la creazione del filtro sono disponibili i seguenti campi:

   -  *Risorsa/Azione*: Opzione per filtrare le richieste in base all'azione/risorsa invocata.

   -  *Ruolo Richiedente*: Opzione per filtrare le richieste in base al ruolo posseduto dal richiedente (sia che si tratti di un soggetto che di un applicativo).

   -  *Soggetto o Applicativo Fruitore*: In aternativca al filtro per ruolo, è possibile specificare un soggetto fruitore ed eventualmente uno dei suoi applicativi.

   -  *Chiave*: Si tratta di un'opzione avanzata che consente
      di filtrare le richieste in ingresso sul gateway in base ad una
      chiave che può essere specificata in maniera personalizzata
      effettuando una delle seguenti scelte per il campo *Tipologia*:

      -  *Header HTTP*: Occorre fornire i dati “Nome” e “Valore”. La
         policy si applicherà soltanto alle richieste che hanno un header http che corrisponde.

      -  *Url di Invocazione*: Occorre fornire i dati “Espressione Regolare” e
         “Valore”. La policy si applicherà soltanto alle richieste ove,
         applicando l'espressione regolare alla URL di invocazione, si
         ottiene un valore identico a quello fornito.

      -  *Parametro della Url*: Occorre fornire i dati “Nome” e “Valore”. La
         policy si applicherà soltanto alle richieste che contengono
         nella url di invocazione un parametro corrispondente ai dati forniti.

      -  *SOAPAction*: Occorre fornire il dato “Valore”. La
         policy si applicherà soltanto alle richieste che si presentano
         con una SOAPAction avente il valore fornito.

      -  *Contenuto*: Occorre fornire i dati “Pattern” e
         “Valore”. La policy si applicherà soltanto alle richieste dove,
         applicando l'espressione XPath o JsonPath al messaggio di richiesta, si
         ottiene un valore identico a quello fornito.

      -  *Client IP*: La policy si applicherà soltanto alle richieste che provengono dall'indirizzo IP indicato.

      -  *X-Forwarded-For*: La policy si applicherà soltanto alle richieste che provengono dall'indirizzo IP indicato presente negli header http utilizzati per il mantenimento dell’IP di origine nel caso di nodi intermedi (es. X-Forwarded-For).

      -  *Plugin Personalizzato*:  Permette di definire un criterio di filtro personalizzato. Per maggiori dettagli si rimanda alla sezione :ref:`configurazioneRateLimiting_filtriRaggruppamentiPersonalizzati`

