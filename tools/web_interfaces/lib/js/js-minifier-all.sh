# jquery-1.4.js
rm -f jquery-1.4.min.js
rm -f jquery-latest.js
./js-minifier.sh jquery-1.4.js jquery-1.4.min.js
./js-minifier.sh jquery-1.4.js jquery-latest.js

# jquery.context-menu.src.js
rm -f jquery.context-menu.min.js
./js-minifier.sh jquery.context-menu.src.js jquery.context-menu.min.js

# ui.dialog.js
rm -f ui.dialog.min.js
./js-minifier.sh ui.dialog.js ui.dialog.min.js

# jquery.searchabledropdown-1.0.8.src.js
rm -f jquery.searchabledropdown-1.0.8.min.js
./js-minifier.sh jquery.searchabledropdown-1.0.8.src.js jquery.searchabledropdown-1.0.8.min.js


