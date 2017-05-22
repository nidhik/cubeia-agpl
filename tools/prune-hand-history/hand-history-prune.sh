#!/bin/bash

# Prune HistoricHand collection and store only the DAYS_TO_SAVE number of days 
# of the most recent hands.
# 
# Running the script without arguments will do a dry run and only count the 
# number of records to remove.
# Add the '-r' flag to remove records.
#
# Author: w@cubeia.com

FOR_REAL=0

while getopts "r" opt; do
  case "$opt" in
    r)  
      FOR_REAL=1
      ;;
    '?')
      echo "Use -r flag to remove, otherwise a dry run will be made without removing"
      exit 1
      ;;
  esac
done

DAYS_TO_SAVE=150

echo Days to save: $DAYS_TO_SAVE

PRUNE_TIME_MS=$(( (`date +%s` - $DAYS_TO_SAVE * 24 * 60 * 60  ) * 1000  ))
PRUNE_TIME_UT=$(( $PRUNE_TIME_MS / 1000 ))
PRUNE_DATE_UT=`date --date="@$PRUNE_TIME_UT"`

echo "Prune from $PRUNE_DATE_UT (ms: $PRUNE_TIME_MS)"


MONGO_CMD_COUNT="db.HistoricHand.find({ startTime: { \$lt: $PRUNE_TIME_MS }}).count();"

COUNT=`mongo poker --quiet --eval "$MONGO_CMD_COUNT"`

echo Rows to remove: $COUNT


if [ "1" -eq $FOR_REAL ]
  then
    echo "Pruning history..."
    
    MONGO_CMD_RM="db.HistoricHand.remove({ startTime: { \$lt: $PRUNE_TIME_MS }})"
    mongo poker --eval "$MONGO_CMD_RM"
    
    echo "Done"
  else
    echo "Dry run, use -r to remove"
fi



