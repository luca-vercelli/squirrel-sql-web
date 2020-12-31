#!/bin/bash

#open the two main log files within Kate

GLASSFISHDIR=~/glassfish4.1.2
EDITOR=kate

$EDITOR $GLASSFISHDIR/glassfish/domains/domain1/logs/server.log &
$EDITOR ~/.squirrel-sql/logs/squirrel-sql.log &

