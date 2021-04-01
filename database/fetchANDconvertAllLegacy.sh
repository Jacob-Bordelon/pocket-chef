#!/bin/bash
for i in {1..39}
do
   curl --location --request POST 'https://api.nal.usda.gov/fdc/v1/foods/list?api_key=dk7T1Zttmx4mKhgMRKpulx8exWIvE4eq5BbkngcG' \
--header 'Content-Type: application/json' \
--data-raw '{
"dataType": [
"SR Legacy"
],
"pageSize": 200,
"pageNumber": '"$i"',
"sortBy": "dataType.keyword",
"sortOrder": "asc"
}' | python -m json.tool > response$i.json

sed '/ndbNumber/d' response$i.json > 1.json
sed '/derivationCode/d' 1.json > 2.json
sed '/derivationDescription/d' 2.json > 3.json
sed '/number/d' 3.json > 4.json
sed '/dataType/d' 4.json > 5.json
sed '/publicationDate/d' 5.json > 6.json

python3 format.py > temp

head -c -4 < temp | tail -c +3 > temp2

cat temp2 >> food.json
echo "}," >> food.json

rm 1.json 2.json 3.json 4.json 5.json 6.json temp temp2 response$i.json

done