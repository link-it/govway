# Verifica strutturale delle pagine .rst del manuale.
#
# Segnala i file che presentano contemporaneamente sia underline '-----' sia underline
# '=====': pattern che indica la presenza di sezioni H2 interne alla pagina, le quali
# producono nel TOC laterale di Sphinx voci subordinate alla pagina stessa, generando
# una struttura non desiderata. Le pagine identificate vanno o estratte in pagine a sé
# stanti, oppure degradate a paragrafi '**bold**' se il loro contenuto è strettamente
# legato a quello della pagina contenente.

found=0
for f in $(find . -name "*.rst" -type f -not -path "*/_build/*"); do
  has_dash=$(awk '/^-+$/' "$f" | head -1)
  has_eq=$(awk '/^=+$/' "$f" | head -1)
  if [ -n "$has_dash" ] && [ -n "$has_eq" ]; then
    echo "$f"
    found=1
  fi
done

if [ $found -eq 0 ]; then
  echo "OK: nessuna pagina .rst con sezioni H2 interne"
  exit 0
else
  echo "KO: rilevate pagine con sezioni H2 interne (vedi elenco sopra); estrarle in pagine separate o degradarle a paragrafi '**bold**'"
  exit 1
fi
