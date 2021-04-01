#!/bin/bash

sed '/ndbNumber/d' $1 > 1.json
sed '/derivationCode/d' 1.json > 2.json
sed '/derivationDescription/d' 2.json > 3.json
sed '/number/d' 3.json > 4.json
sed '/dataType/d' 4.json > 5.json
sed '/publicationDate/d' 5.json > 6.json

python3 format.py > temp

head -c -4 < temp | tail -c +3 > temp2

cat temp2 >> food.json
echo "} } }" >> food.json

rm 1.json 2.json 3.json 4.json 5.json 6.json temp temp2