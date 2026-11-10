# Verifica strutturale delle pagine .rst del manuale.
#
# Ogni pagina deve avere un solo titolo di sezione. Le sezioni interne alla pagina:
# - producono nel TOC laterale di Sphinx voci subordinate alla pagina stessa, generando
#   una struttura non desiderata;
# - sul sito govway.org fanno comparire il footer Link.it in mezzo al contenuto (il
#   post-processing del sito lo inserisce prima del secondo '</section>' della pagina).
#
# Un titolo è una riga di testo non indentata, preceduta da una riga vuota (o da inizio
# file o da una overline), seguita da una riga composta da un solo carattere di
# punteggiatura ripetuto (=, -, ~, ^, ", *, +, #, ...) lunga almeno quanto il testo.
# Il controllo non dipende quindi dal carattere scelto per la sottolineatura.
#
# Le pagine identificate vanno o estratte in pagine a sé stanti, oppure degradate a
# paragrafi '**bold**' se il loro contenuto è strettamente legato a quello della pagina
# contenente.

export LC_ALL=C.UTF-8

found=0
for f in $(find . -name "*.rst" -type f -not -path "*/_build/*" | sort); do
  titoli=$(awk '
    function isAdorn(s,   c, t) {
      if (length(s) < 3) return 0
      c = substr(s, 1, 1)
      if (index("!\"#$%&'\''()*+,-./:;<=>?@[\\]^_`{|}~", c) == 0) return 0
      t = s
      gsub(/./, c, t)
      return t == s
    }
    {
      sub(/\r$/, "")
      cur = $0
      if (NR > 1 && isAdorn(cur) && prev != "" && prev !~ /^[ \t]/ && !isAdorn(prev) \
          && length(cur) >= length(prev) && (prev2 == "" || isAdorn(prev2))) {
        printf "    %d: %s\n", NR - 1, prev
      }
      prev2 = prev
      prev = cur
    }' "$f")
  n=$(printf '%s' "$titoli" | grep -c '^ ')
  if [ "$n" -gt 1 ]; then
    echo "$f"
    echo "$titoli"
    found=1
  fi
done

if [ $found -eq 0 ]; then
  echo "OK: nessuna pagina .rst con sezioni interne"
  exit 0
else
  echo "KO: rilevate pagine con sezioni interne (vedi elenco sopra); estrarle in pagine separate o degradarle a paragrafi '**bold**'"
  exit 1
fi
