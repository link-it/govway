.. _profiliEgov:

Profili di gestione della busta eGov
------------------------------------

L'interfaccia *completa* fornisce la possibilità di fruire/erogare di
servizi SPCoop che non seguono le Linee Guida 1.1 ma si basano sul
documento e-Gov 1.1. Questa funzionalità è utile sia per backward
compatibility in quei domini dove i servizi non sono ancora stati
adeguati al profilo descritto nelle Linee Guida 1.1, sia per usufruire
di servizi infrastrutturali quali *consegna affidabile*, *consegna in
ordine*, *conversazioni* che non sono presenti nel profilo Linee Guida
1.1.

**Fruizione di un servizio.**

Supponiamo di essere in un contesto dove vogliamo usufruire di un
servizio erogato da un soggetto la cui PdD non è ancora stata adeguata a
quanto descritto nelle Linee Guida 1.1. Per usufruire del servizio, il
soggetto fruitore deve inviare buste conformi al profilo e-Gov 1.1,
nonostante la propria porta di dominio sia già conforme alle Linee Guida
1.1. Per gestire tale contesto è possibile definire il soggetto
erogatore con profilo *eGov1.1*. In un successivo momento, la PdD del
soggetto erogatore può iniziare ad adeguarsi alle Linee Guida 1.1.
Supponiamo che l'adeguamento sia incrementale, fornito per un servizio
alla volta. Per usufruire dei servizi erogati da tale soggetto, con la
giusta modalita (Linee Guida 1.1 o e-Gov 1.1) *è possibile ridefinire il
profilo di gestione all'interno del servizio*.

**Erogazione di un servizio.**

Poniamoci in un contesto in cui la Porta di Dominio eroga dei servizi
che rispettano quanto descritto nelle Linee Guida 1.1. In questo
contesto, i soggetti di PdD che non si sono ancora adeguate alle linee
guida, non potrebbero usufruire dei servizi. La PdD può essere
configurata, in modo da erogare i servizi, per questi soggetti, secondo
il profilo *eGov 1.1*. Questa configurazione richiede che al soggetto
fruitore venga associato un profilo *eGov 1.1*. In un successivo
momento, la PdD di un soggetto fruitore può iniziare ad adeguarsi alle
Linee Guida 1.1. Si creano quindi due situazioni di transizione dove
devono coesistere entrambe le specifiche:

-  Un soggetto fruisce per alcuni servizi erogati secondo le specidiche
   e-Gov1.1, per altri secondo le Linee Guida 1.1

-  Uno o più fruitori accedono al un servizio erogato secondo le
   specidiche e-Gov1.1, altri secondo le Linee Guida 1.1

In entrmabi i casi, per erogare il servizio con la giusta modalita
(linee guida o e-gov 1.1) è *possibile ridefinire il profilo di gestione
impostandolo nella lista dei fruitori del servizio*.

Profilo di gestione e-Gov 1.1
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Il documento delle linee guida ha deprecato alcune opzioni al fine di
snellire la specifica. Per mantenere la conpatibilità con la vecchia
versione viene sempre offerta la possibilità di specificare tali opzioni
all'interno degli accordi di servizio. Tali funzionalità vengono
impostate/validate all'interno della busta e-Gov solo se il servizio
viene fruito/erogato con profilo *eGov1.1*.

.. table:: Opzioni della busta eGov
   :widths: 25 10 65

   =====================  ==========       ===============
   Nome                   Default          Funzionalità
   =====================  ==========       ===============
   Filtro duplicati       true             Funzionalità di filtro delle buste duplicate (Imposta l'attributo inoltro del profilo di trasmissione al valore EGOV\_IT\_ALPIUUNAVOLTA).
   Conferma Ricezione     false            Funzionalità di consegna affidabile delle buste spcoop attraverso l'utilizzo dei riscontri (Imposta l'attributo confermaRicezione del profilo di trasmissione al valore true).
   ID Collaborazione      false            Aggiunge un elemento Collaborazione alla busta (Diverse istanze di cooperazione possono essere correlate in un'unica conversazione).
   Consegna in ordine     false            Consegna in ordine delle buste (Richiede Filtro Duplicati e Conferma Ricezione)
   Scadenza                                Assegna una scadenza temporale alla busta SPCoop
   =====================  ==========       ===============

Di seguito un esempio di creazione di un accordo di servizio che
richiede consegna affidabile tramite riscontri, filtro duplicati e id di
collaborazione per un servizio sincrono.

   .. figure:: ../_figure_console/funzionalitaEGovDeprecate.jpg
    :scale: 100%
    :align: center
    :name: funzionalitaEgov

    Controlli avanzati sulle informazioni eGov relative all'accordo di servizio

