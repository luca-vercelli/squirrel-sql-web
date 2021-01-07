#!/bin/bash

SRC_FOLDER=~/git/squirrel-sql-git/sql12/core/src

function do_sed {

  # convert multiline strings in strings containing "\\n"
  # see https://unix.stackexchange.com/questions/26284 and https://stackoverflow.com/questions/2392766
  cat $1 | tr '\n' '\r' | sed -e 's/\\\r/\\n/g'  | tr '\r' '\n' > $1.json
  
  # start with { and end with },
  # remove empty lines and comments,
  # escape " and \!,
  # convert a=b into "a": "b",
  sed -i -r -e '1i{' -e '$a}' -e '/^$/d' -e '/^#/d' -e 's/"/\\"/g' -e 's/\\!/!/g' -e 's/^/"/' -e 's/=/": "/' -e 's/([^\\])$/\1",/'  $1.json

}


#see https://stackoverflow.com/questions/4321456
export -f do_sed
find $SRC_FOLDER -name 'I18NStrings.properties' -exec bash -c 'do_sed "$0"' {} \;

