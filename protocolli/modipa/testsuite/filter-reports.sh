#!/bin/bash

find RisultatiTestSuiteV2/jreports -name \*.xml -exec perl -0777 -pi -e 's/message=".*?"//gs'  {} \;
find RisultatiTestSuiteV2/jreports -name \*.xml -exec perl -0777 -pi -e 's/<!\[CDATA(.|\n|\r)*\]\]>//gs' {} \;
