.. _urlInvocazione:

Url di Invocazione
------------------

Per scoprire quale sia la url di una API protetta da GovWay da fornire
ai client esterni, il gestore può utilizzare la govwayConsole, la quale
fornisce nella visualizzazione del dettaglio di una erogazione o
fruizione di API la url di invocazione (es. :numref:`inst_erogazione_urlInvocazioneFig`).

    .. _urlInvocazioneFig:
    
    .. figure:: ../_figure_installazione/govwayConsole_erogazione_urlInvocazione.png
        :scale: 100%
        :align: center
	:name: inst_erogazione_urlInvocazioneFig

        Url di Invocazione di una Erogazione

L'url fornita ha per default un prefisso *http://localhost:8080/govway*
che può non andar bene se il gateway è stato dispiegato in modo da
essere raggiungibile tramite un host, porta o contesto differente.

Per modificare i prefissi delle url di invocazioni accedere alla voce
*'Configurazione - Generale'* del menù. Nella sezione *'Gestione
Profilo'* è possibile configurare i prefissi di una erogazione e di una
fruizione per ogni profilo.

    .. figure:: ../_figure_installazione/govwayConsole_urlInvocazione.png
        :scale: 100%
        :align: center
	:name: inst_urlInvocazioneFig

        Configurazione prefissi per le Url di Invocazione
