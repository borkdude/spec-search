#!/bin/sh

if [ -z "$CIRCLE_PULL_REQUEST" ] && [ "$CIRCLE_BRANCH" = "master" ]
then
    lein deploy clojars
fi

exit 0;
