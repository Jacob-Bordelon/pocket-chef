#!/bin/bash

#args: $1=dataType(i.e:SR Legacy,Foundation) ; $2=pageNumer  ;  $3=outputFile  
if [ $# -lt 3 ]; then
    echo "Usage: ./curl_command.sh <dataType> <pageNumber> <outputFile>"
    exit 1
fi

curl --location --request POST 'https://api.nal.usda.gov/fdc/v1/foods/list?api_key=dk7T1Zttmx4mKhgMRKpulx8exWIvE4eq5BbkngcG' \
--header 'Content-Type: application/json' \
--data-raw '{
"dataType": [
"'"$1"'"
],
"pageSize": 200,
"pageNumber": '"$2"',
"sortBy": "dataType.keyword",
"sortOrder": "asc"
}' | python -m json.tool > $3