#!/bin/bash

DIR=$1
if [ -z "$DIR" ]
then
	echo "Indicare directory o file"
	exit 2
fi

CONVERT=$2
if [ -z "$CONVERT" ]
then
	echo "Indicare conversione true/false"
	exit 2
fi

echo "🔍 Verifica codifica .properties nel progetto  $DIR (deve essere UTF-8 o ASCII) in corso ..."
echo

# Trova tutti i file .properties
find ${DIR} -type f -name "*.properties" | while read -r file; do
  # Ottiene il tipo MIME e codifica
  #encoding=$(file -i "$file" | awk -F'charset=' '{print $2}')
  enconding=$(uchardet "$file")

  if [[ "${enconding}" != "utf-8" && "${enconding}" != "UTF-8" && "${enconding}" != "us-ascii" && "${enconding}" != "ASCII" ]]; then
    echo "❌ Codifica non valida: $(realpath "$file")  (rilevata: ${enconding}])"
    
    if [ "${CONVERT}" == "true" ]
    then
	    abs_path=$(realpath "$file")
	    backup_path="${abs_path}.bak"
	    
	    echo "⚠️  Converto: $abs_path (rilevata: ${enconding})"
	    cp "$file" "$backup_path"

	    # Prova la conversione
	    iconv -f "${enconding}" -t UTF-8 "$backup_path" -o "$file"
	    if [[ $? -eq 0 ]]; then
	    
	    	if grep -qP '\xC3[\x80-\xBF]' "$file"; then
			echo "Mojibake di caratteri accentati rilevato: ritento con WINDOWS-1252…"
			iconv -f WINDOWS-1252 -t UTF-8//TRANSLIT "$backup_path" -o "$file"
	    	fi
	    	if grep -qP '\xC3[\x80-\xBF]' "$file"; then
			echo "Rilevati sempre Mojibake di caratteri accentati anche provando con WINDOWS-1252, ripristino originale"	    	
			cp $backup_path $file
		else
		      echo "✅ Conversione riuscita → UTF-8 (backup: $backup_path)"
	        fi
	    else
	      echo "❌ Errore nella conversione di: $abs_path"
	      mv "$backup_path" "$file"  # Ripristina in caso di errore
	    fi
	fi

    echo    
  fi
done

echo "Verifica completata."
