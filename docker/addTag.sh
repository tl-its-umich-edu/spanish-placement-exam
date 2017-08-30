#!/bin/bash
#oc tag 172.30.183.221:5000/spanish-placement-exam-dev/spanish-placement-exam spanish-placement-exam:spedevA

SOURCE=172.30.183.221:5000/spanish-placement-exam-dev/spanish-placement-exam
DEST=spanish-placement-exam
TAG=spedevA
set -x
oc tag ${SOURCE} ${DEST}:${TAG}
#end
