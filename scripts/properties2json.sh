#!/bin/bash

SRC_FOLDER=~/git/squirrel-sql-git/sql12/core/src

function do_sed {
  sed -e '1i{' -e '/^$/d' -e 's/"/\\"/g' -e 's/^/"/g' -e 's/=/": "/g' -e 's/$/",/g' -e 's/\\!/!/g' -e '$a}'  $1 > $1.json
}


#see https://stackoverflow.com/questions/4321456
export -f do_sed
find $SRC_FOLDER -name 'I18NStrings.properties' -exec bash -c 'do_sed "$0"' {} \;

