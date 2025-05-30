.. _mon_stats_filtri:

Criteri di Filtro
~~~~~~~~~~~~~~~~~~~

Per ogni report è possibile specificare dei criteri di filtro, per
limitare i dati da presentare (:numref:`mon_filtroStatistiche_fig`). I criteri di filtro selezionabili
variano in funzione del tipo di report selezionato.

.. figure:: ../../_figure_monitoraggio/FiltroStatistiche.png
    :scale: 100%
    :align: center
    :name: mon_filtroStatistiche_fig

    Maschera di selezione per la generazione dei report statistici

I dati da indicare per generare un report sono:

-  Analisi Statistica

    - **Profilo Interoperabilità**: visibile solo quando il profilo di interoperabilità, sulla testata dell'applicazione, è stato impostato a "Tutti". Il campo permette di selezionare il profilo con cui effettuare la ricerca delle transazioni. Questa scelta è obbligatoria.

    - **Soggetto Locale**: visibile, in modalità multi-tenant, solo quando il soggetto locale non è stato selezionato sulla testata dell'applicazione. Consente di filtrare le transazioni in base al soggetto locale che vi partecipa.

    - **Tipo**: opzione per selezionare le transazioni appartenenti a casi di erogazione o fruizione.

-  Filtro Temporale

   -  **Unità di Tempo:**\ consente di scegliere su quale unità
      temporale basare il dato numerico relativo alle transazioni
      (oraria, giornaliera, settimanale, mensile)

   -  **Periodo:**\ consente di indicare un intervallo temporale di
      interesse

-  Dati API

   -  **Soggetto Erogatore**: Opzione, presente nel caso delle fruizioni, per selezionare il soggetto erogatore relativo alle transazioni interessate.

   -  **Tag**: permette di filtrare rispetto ad un tag di classificazione delle API.

   -  **API**: permette di filtrare su una specifica API tra quelle erogate dal soggetto indicato.

   -  **Azione/Risorsa**: permette di selezionare una tra le
      azioni/risorse della API selezionata.

-  Dati Mittente: permette di filtrare le transazioni da
   includere nel report sulla base dei dati legati alla richiesta
   inviata dal mittente.

   -  **Tipo**: consente di scegliere su quale dato applicare il filtro
      tra:

      -  Soggetto: si seleziona il soggetto mittente delle transazioni.

      -  Applicativo: si seleziona l'applicativo mittente delle
         transazioni.

      -  Identificativo Autenticato: si seleziona il metodo di
         autenticazione e si specifica il criterio di confronto con
         l'identificativo ricavato dal processo di autenticazione.

      -  Token Info: si seleziona il claim del token e si specifica il
         criterio per il confronto con il token ricavato durante il
         processo di autenticazione. Selezionando il profilo di interoperabilità "ModI" e il tipo di transazione "Erogazione", tra i claim forniti come criterio di ricerca è possibile utilizzare anche la voce "PDND - Organization" che consente di ricercare transazioni in cui le richieste provengono dall’organizzazione del client indicata o la voce 'PDND - External o Consumer ID' che consente di ricercare transazioni indicando l'identificativo esterno dell'organizzazione (es. codice IPA) o l'identificativo con cui l'organizzazione è stata censita sulla PDND (consumerId); le informazioni PDND vengono recuperate tramite l'integrazione con le API PDND. 

-  Esito

   -  **Esito**: permette di selezionare una categoria di esiti a cui le
      transazioni appartengono. Valgono le medesime considerazioni
      effettuate nella sezione :ref:`mon_live` relativamente allo stesso campo presente tra i
      criteri di ricerca.

   -  **Dettaglio Esito**: i valori selezionabili cambiano in base alla
      scelta effettuata al punto precedente. Valgono le medesime
      considerazioni effettuate nella sezione :ref:`mon_live` relativamente allo stesso campo
      presente tra i criteri di ricerca.

-  Report

   -  **Visualizza Per**: è possibile scegliere il dato che si vuole visualizzare nel report tra:
      *Occupazione Banda*, in termini di KB gestiti, *Numero Transazioni* o *Tempo Medio Risposta*;

   -  **2 o 3 Dimensioni (senza o con data)** (:numref:`mon_reportisticaFiltroDimensioni_fig`): ad eccezione della 'Distribuzione Temporale' e della 'Distribuzione per Esiti', e solo per i report di tipo 'Bar Chart' o 'Tabella', è possibile scegliere se generare un report in 2 dimensioni (senza includere la data) o in 3 dimensioni (includendo la data). In alternativa, è possibile generare un report in 3 dimensioni dove, al posto della data, è possibile indicare un'altra informazione desiderata (:numref:`mon_reportisticaFiltroDimensioniCustom_fig`).

      .. figure:: ../../_figure_monitoraggio/ReportisticaFiltroDimensioni.png
          :scale: 70%
          :align: center
          :name: mon_reportisticaFiltroDimensioni_fig

          Esempio di impostazione per generare un report a 2 o 3 dimensioni
          
      .. figure:: ../../_figure_monitoraggio/ReportisticaFiltroDimensioniCustom.png
          :scale: 70%
          :align: center
          :name: mon_reportisticaFiltroDimensioniCustom_fig

          Esempio di impostazione per generare un report a 3 dimensioni personalizzato

   -  **Tipo Banda** (:numref:`mon_reportisticaFiltroTipoBanda_fig`): scegliendo una visualizzazione per 'Occupazione Banda' è inoltre possibile selezionare il tipo di banda desiderata;

      .. figure:: ../../_figure_monitoraggio/ReportisticaTipoFiltroBanda.png
          :scale: 70%
          :align: center
          :name: mon_reportisticaFiltroTipoBanda_fig

          Esempio di impostazione del tipo di banda

   -  **Tipo Latenza** (:numref:`mon_reportisticaFiltroTipoLatenza_fig`): scegliendo una visualizzazione per 'Tempo Medio Risposta' è inoltre possibile selezionare il tipo di latenza desiderata.

      .. figure:: ../../_figure_monitoraggio/ReportisticaTipoFiltroLatenza.png
          :scale: 70%
          :align: center
          :name: mon_reportisticaFiltroTipoLatenza_fig

          Esempio di impostazione del tipo di latenza

Dopo aver selezionato i parametri di interesse si genera il report
utilizzando il pulsante "Genera Report". L'area di visualizzazione del
report, grafico o tabellare, si trova sotto il form di selezione e
presenta alla base i seguenti elementi fissi:

-  Alcuni link che consentono lo spostamento dell'intervallo temporale
   senza dover tornare alla maschera di selezione (ad es. mese
   successivo o precedente, ecc.)

-  I link per l'esportazione dei dati visualizzati nei formati CSV, XLS,
   PDF e PNG.

Sono inoltre presenti alcuni elementi di personalizzazione che dipendono
dal tipo di grafico generato:

-  Nel caso di rappresentazione grafica a torta o istogramma (denominate
   rispettivamente pie chart o bar chart) è consentito impostare il
   numero massimo di elementi visualizzabili (ordinati per cardinalità
   decrescente). Gli elementi rimanenti saranno raggruppati in un unico
   altro elemento riportante l'etichetta "Altri".

-  Nel caso di rappresentazione grafica a linea o istogramma (denominate
   rispettivamente line chart o bar chart) è possibile decidere
   l'orientamento delle etichette dei risultati visualizzati.

-  In tutte le rappresentazioni grafiche è possibile allargare o
   restringere il grafico se la pagina del browser (e la risoluzione) lo
   permette.

Vediamo adesso con maggior dettaglio le singole tipologie di report.
