import json

with open('pocketchef-be978-food-rtdb-export.json') as f:
  data = json.load(f)

fdcId = 0
searchNames = {}

for (k, v) in data.items():
    if k == 'items':
        for (key, values) in v.items():
            fdcId = key
            for (innerKey, innerValues) in values.items():
                if innerKey == 'name':
                    searchNames[str(innerValues)] = int(fdcId)


with open('searchNames.json', 'w') as json_file:
  json.dump(searchNames, json_file, indent = 4, sort_keys=True)
