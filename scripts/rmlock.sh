#!/bin/bash

# remove Derby db lock files

GLASSFISHDIR=~/glassfish4.1.2
DBNAME=test

rm  $GLASSFISHDIR/glassfish/domains/domain1/config/$DBNAME/db*lck
