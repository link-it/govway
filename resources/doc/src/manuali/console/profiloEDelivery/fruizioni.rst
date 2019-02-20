.. _fruizioneEdelivery:

Fruizione di servizi in modalità eDelivery
------------------------------------------

Configurare una fruizione eDelivery permette ad un'applicazione interna
di inviare messaggi da veicolare verso un generico access point
eDelivery esterno.

Il processo di configurazione della fruizione eDelivery prevede
inizialmente i medesimi passi già descritti per l'erogazione nella sezione :ref:`erogazioneEdelivery`. 
Dovranno quindi essere configurati i dati eDelivery
relativi ai soggetti interlocutori, interno ed esterno, dovranno inoltre
essere censite le API relative al servizio da fruire.

Dopo aver censito le API si procede con la configurazione della
fruizione creando un nuovo elemento nella sezione *Registro > Fruizioni*
della govwayConsole. Analogamente al caso dell'erogazione si dovrà
selezionare la security policy necessaria per gli scambi tra gli access
point.

.. note::
    Affinché le configurazioni apportate in modalità eDelivery possano
    essere attuate sull'access point Domibus è necessario procedere alla
    generazione del PMODE nel modo descritto alla sezione :ref:`pmode`.
