#!/bin/bash
# OSV-Scanner Maven build integration wrapper
#
# Variabili d'ambiente (passate da exec-maven-plugin):
#   OSV_SCANNER_HOME        - path del binario osv-scanner (default: osv-scanner)
#   OSV_SCANNER_OFFLINE     - true/false, usa DB locale cached (default: false)
#   OSV_SCANNER_FORMAT      - lista formati report separati da virgola
#   OSV_SCANNER_OUTPUT      - directory di output per i report vulnerabilita
#   OSV_SCANNER_FAIL        - true/false, fail del build se trovate vulnerabilita (default: false)
#   OSV_SCANNER_CONFIG_DIR  - directory contenente i file .toml dei falsi positivi (default: vuoto)
#   OSV_SCANNER_EXCLUDE     - directory da escludere dalla scansione, separate da virgola (default: vuoto)
#   OSV_SCANNER_RESOLVE     - true/false, risolvi dipendenze transitive (default: false)
#                             false = analizza solo le dipendenze dichiarate direttamente nei pom.xml
#                             (appropriato quando le dipendenze transitive sono escluse con <exclusion>*:*</exclusion>)

OSV_CMD="${OSV_SCANNER_HOME:-osv-scanner}"
OFFLINE="${OSV_SCANNER_OFFLINE:-false}"
FORMATS="${OSV_SCANNER_FORMAT:-json}"
OUTPUT_DIR="${OSV_SCANNER_OUTPUT:-osv-scanner-result}"
FAIL_ON_VULN="${OSV_SCANNER_FAIL:-false}"
RESOLVE="${OSV_SCANNER_RESOLVE:-false}"
EXCLUDE="${OSV_SCANNER_EXCLUDE:-}"
CONFIG_DIR="${OSV_SCANNER_CONFIG_DIR:-}"

# Verifica che osv-scanner sia disponibile
if ! command -v "$OSV_CMD" > /dev/null 2>&1 && [ ! -x "$OSV_CMD" ]; then
	echo "[ERROR] osv-scanner non trovato: $OSV_CMD"
	echo "[ERROR] Specificare il path tramite -Dosv.scanner.home=<path>"
	exit 2
fi

mkdir -p "$OUTPUT_DIR"

OFFLINE_ARGS=""
if [ "$OFFLINE" = "true" ]; then
	OFFLINE_ARGS="--download-offline-databases --offline-vulnerabilities"
fi

RESOLVE_ARGS=""
if [ "$RESOLVE" = "false" ]; then
	RESOLVE_ARGS="--no-resolve"
fi

# Merge dei file .toml dalla directory dei falsi positivi in un unico file temporaneo
CONFIG_ARGS=""
MERGED_CONFIG=""
if [ -n "$CONFIG_DIR" ] && [ -d "$CONFIG_DIR" ]; then
	TOML_FILES=$(find "$CONFIG_DIR" -name "*.toml" -type f 2>/dev/null | sort)
	if [ -n "$TOML_FILES" ]; then
		MERGED_CONFIG=$(mktemp /tmp/osv-scanner-config.XXXXXX.toml)
		for toml_file in $TOML_FILES; do
			echo "# --- $(basename "$toml_file") ---" >> "$MERGED_CONFIG"
			cat "$toml_file" >> "$MERGED_CONFIG"
			echo "" >> "$MERGED_CONFIG"
		done
		CONFIG_ARGS="--config $MERGED_CONFIG"
		echo "[INFO] OSV-Scanner: caricati falsi positivi da $(echo "$TOML_FILES" | wc -l) file in $CONFIG_DIR"
	fi
fi

EXCLUDE_ARGS=""
if [ -n "$EXCLUDE" ]; then
	for excl in $(echo "$EXCLUDE" | tr ',' ' '); do
		excl=$(echo "$excl" | tr -d ' ')
		[ -z "$excl" ] && continue
		EXCLUDE_ARGS="$EXCLUDE_ARGS --experimental-exclude $excl"
	done
fi

VULN_FOUND=0

for fmt in $(echo "$FORMATS" | tr ',' ' '); do
	# trim whitespace
	fmt=$(echo "$fmt" | tr -d ' ')
	[ -z "$fmt" ] && continue

	case "$fmt" in
		table)          EXT="txt" ;;
		vertical)       EXT="vertical.txt" ;;
		json)           EXT="json" ;;
		html)           EXT="html" ;;
		markdown)       EXT="md" ;;
		sarif)          EXT="sarif" ;;
		gh-annotations) EXT="gh-annotations.txt" ;;
		*)              EXT="$fmt" ;;
	esac

	echo "[INFO] OSV-Scanner: generazione report formato $fmt ..."
	"$OSV_CMD" scan source $OFFLINE_ARGS $RESOLVE_ARGS $EXCLUDE_ARGS $CONFIG_ARGS -r . \
		--format "$fmt" \
		--output-file "$OUTPUT_DIR/osv-scanner-report.$EXT"
	SCAN_EXIT=$?

	if [ $SCAN_EXIT -eq 1 ]; then
		# exit 1 = vulnerabilita trovate
		VULN_FOUND=1
	elif [ $SCAN_EXIT -ne 0 ]; then
		echo "[ERROR] osv-scanner terminato con exit code $SCAN_EXIT per formato $fmt"
		# Pulizia file temporaneo prima di uscire
		[ -n "$MERGED_CONFIG" ] && rm -f "$MERGED_CONFIG"
		exit $SCAN_EXIT
	fi
done

# Pulizia file temporaneo
[ -n "$MERGED_CONFIG" ] && rm -f "$MERGED_CONFIG"

if [ $VULN_FOUND -eq 1 ]; then
	echo "[WARNING] OSV-Scanner ha rilevato vulnerabilita. Consultare i report in $OUTPUT_DIR"
fi

if [ "$FAIL_ON_VULN" = "true" ] && [ $VULN_FOUND -eq 1 ]; then
	exit 1
fi
exit 0
