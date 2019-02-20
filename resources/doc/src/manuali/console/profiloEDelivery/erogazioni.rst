.. _erogazioneEdelivery:

Erogazione di servizi in modalità eDelivery
-------------------------------------------

Configurare un'erogazione eDelivery permette ad un'applicazione interna
di ricevere i messaggi inviati da un generico access point eDelivery
esterno.

Il primo passo di configurazione prevede che venga censito il soggetto
esterno mittente dei messaggi. La creazione di tale soggetto si realizza
dalla sezione *Registro > Soggetti* della govwayConsole, impostando le
proprietà eDelivery già descritte nella sezione precedente per il
soggetto interno.

Il passo successivo è quello di registrare le API corrispondenti al
servizio eDelivery alla sezione *Registro > API*. Le proprietà
eDelivery, presenti nel form di creazione, sono quelle mostrate in :numref:`apiEdelivery`.

   .. figure:: ../_figure_console/edev-api-props.png
    :scale: 100%
    :align: center
    :name: apiEdelivery

    Registrazione API eDelivery - Proprietà specifiche

Le proprietà da specificare sono le seguenti:

-  *Service Info - Type*: Identificativo assegnato come tipo del
   servizio (opzionale).

-  *Service Info - Name*: Nome del servizio.

-  *Payload Profiles - File*: Campo per l'upload del descrittore XML che
   rappresenta il formato dei messaggi inviati dal mittente. Campo
   opzionale, utilizzabile per aggiungere nuovi profili rispetto a
   quelli già presenti nell'installazione standard di Domibus. Per la
   specifica del formato XML da adottare si consulti la documentazione
   ufficiale di Domibus.

-  *Properties - File*: Campo per l'upload del descrittore XML che
   definisce le proprietà custom che saranno presenti nei messaggi
   inviati dal mittente. Campo opzionale, utilizzabile per aggiungere
   nuove property rispetto a quelle già presenti nell'installazione
   standard di Domibus. Per la specifica del formato XML da adottare si
   consulti la documentazione ufficiale di Domibus.

Dopo aver effettuato il salvataggio è necessario completare la
configurazione del servizio utilizzando il link presente nella colonna
*Risorse* o *Servizi*, a seconda che si tratti di un servizio Rest o
Soap, in corrispondenza dell'elemento presente nell'indice dei servizi.
Per ciascuna delle azioni/risorse elencate per il servizio (o create,
nel caso che, non disponendo del descrittore del servizio, si proceda
con la configurazione manuale delle azioni), si accede al dettaglio per
completare la configurazione delle proprerty eDelivery (:numref:`apiAzioniEdelivery`).

   .. figure:: ../_figure_console/edev-api-azione.png
    :scale: 100%
    :align: center
    :name: apiAzioniEdelivery

    Proprietà eDelivery relative alle azioni delle API


I valori da impostare nel form sono:

-  *Action Info - Name*: Nome dell'azione.

-  *Payload - Profile*: Payload Profile, tra quelli disponibili, da
   utilizzare per l'azione.

-  *Payload - Compress*: Indicare se l'invio del messaggio farà uso di
   compressione dei dati.

Dopo aver creato l'API si procede con la configurazione dell'erogazione
alla sezione *Registro > Erogazioni* della govwayConsole (:numref:`erogazioneEdeliveryFig`).

   .. figure:: ../_figure_console/edev-erogazione.png
    :scale: 100%
    :align: center
    :name: erogazioneEdeliveryFig

    Proprietà eDelivery relative all’erogazione del servizio

L'unica impostazione eDelivery da fornire in questo contesto è:

-  *Security Profile*: profilo di sicurezza adottato dagli access point
   durante la comunicazione. E' necessario scegliere tra i valori
   presenti, che corrispondono alle policy standard, già presenti in
   Domibus con l'installazione.

.. note::
    L'endpoint fornito alla voce Connettore sarà quello utilizzato da
    GovWay per la consegna dei messaggi consegnati all'access point
    Domibus interno.

.. note::
    Affinché le configurazioni apportate in modalità eDelivery possano
    essere attuate sull'access point Domibus è necessario procedere alla
    generazione del PMODE nel modo descritto alla sezione :ref:`pmode`.
