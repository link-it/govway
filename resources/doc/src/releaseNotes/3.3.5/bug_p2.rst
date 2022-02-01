.. _3.3.5.2_bug:

Bug Fix 3.3.5.p2
----------------

Sono stati risolti i seguenti bug:

- libreria 'log4j2' aggiornata alla versione '2.16.0' per risolvere la vulnerabilità 'CVE-2021-45046';

- nella console di gestione, se configurata in Load Balancing per gestire molteplici nodi, l'accesso alla sezione 'Runtime' causava delle invocazioni verso i nodi gestiti ancor prima di averne selezionato qualcuno o aver richiesto operazioni di gestione (es. svuota cache).
