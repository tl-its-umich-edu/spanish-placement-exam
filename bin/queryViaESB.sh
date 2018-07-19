####
# Sample framework script for directly querying the ESB SPE api.  It
# is useful for diagnosing problems.  This won't work out of the box.

TOKEN=<IBM API Token>
KEY=<IBM API application key>

REQUEST="https://apigw-tst.it.umich.edu/um/aa/Unizin/data/CourseId/187539/AssignmentTitle/Spanish%20Placement%20Exam"

GRADEDAFTERTIME="2017-04-01 18:00:00"
####

set -x
curl --request get \
     --url ${REQUEST} \
     --header "gradedAfterTime: ${GRADEDAFTERTIME}" \
  --header 'accept: application/json' \
  --header "authorization: Bearer ${TOKEN}" \
  --header "x-ibm-client-id: ${KEY}"

#end
