from os import linesep
import re
import json
from typing import Dict
import argparse
parser = argparse.ArgumentParser()
parser.add_argument("-i", "--input-file",help="Input file name")
parser.add_argument("-o", "--output-file", help="Direct Output to this file")
args = parser.parse_args()



#["foodNutrients"]
all_nutrient_keys = set()
count = 0
keyword = "food"
keyword2 = 'foodNutrients'
limit = 15
# //       const calList =   ["Energy (Atwater Specific Factors)","Energy (Atwater General Factors)", "Energy"]
# //       const carbList =  ["Carbohydrate, by difference","Carbohydrate, by summation"]
# //       const sugarList = ["Sugars, Total NLEA", "Sugars, total including NLEA"]


calList =   ["Energy (Atwater Specific Factors)","Energy (Atwater General Factors)", "Energy"]
carbList =  ["Carbohydrate, by difference","Carbohydrate, by summation"]
sugarList = ["Sugars, Total NLEA", "Sugars, total including NLEA"]
fiberList = ["Fiber, total dietary","Total dietary fiber (AOAC 2011 25)" ]
removeList = [i.rstrip("\n") for i in open('filterout.txt','r').readlines()]
categories = [i.rstrip("\n") for i in open('categories','r').readlines()]
 
def filterWord(word):
    for i in removeList:
        if(i.lower() in word.lower()):
            return True
    return False

def reformat(key, food):
    nutrients = food["foodNutrients"]
    sat_fat = nutrients["Fatty acids, total saturated"]["amount"] if "Fatty acids, total saturated" in nutrients else 0
    tran_fat = nutrients["Fatty acids, total trans"]["amount"] if "Fatty acids, total trans" in nutrients else 0
    total_fat = nutrients["Total fat (NLEA)"]["amount"] if "Total fat (NLEA)" in nutrients else 0
    protein = nutrients["Protein"]["amount"] if "Protein" in nutrients else 0
    sodium  = nutrients["Sodium, Na"]["amount"] if "Sodium, Na" in nutrients else 0
    chloesterol= nutrients["Cholesterol"]["amount"] if "Cholesterol" in nutrients else 0

    cals = [nutrients[i]["amount"] for i in calList if i in nutrients]
    calories = max(cals) if cals != [] else 0

    carbs =  [nutrients[i]["amount"] for i in carbList if i in nutrients]
    carbohydrates = max(carbs) if carbs != [] else 0

    sug =  [nutrients[i]["amount"] for i in sugarList if i in nutrients]
    sugars = max(sug) if sug != [] else 0

    fib =  [nutrients[i]["amount"] for i in fiberList if i in nutrients]
    fiber = max(fib) if fib != [] else 0

    nutrients = {
        "calories":calories,
        "carbs":carbohydrates,
        "total_fat":total_fat,
        "trans_fat":tran_fat,
        "sat_fat":sat_fat,
        "protein":protein,
        "sodium":sodium,
        "chloesterol":chloesterol,
        "fiber":fiber,
        "sugars":sugars
    }

    name = key
    category = getCategory(key)


    new_format = {food["fdcId"]:{
            "fdcId":food["fdcId"],
            "fullName":key,
            "name":name ,
            "category":category,
            "foodNutrients":nutrients,
            "image":food["image"]
        }}

    return new_format

def getCategory(name):
    name = name.split(",")[0]
    if(name.lower() in categories):
        return name
    return None



def generate_food_list():
    with open('food.json','r') as readFile:
        data = json.load(readFile)
        data = data['food']
    with open('new_foods.json','w') as writeFile:
        tree = {}
        for i in data:
            if(filterWord(i)== False):
                tree.update(reformat(i,data[i]))
        writeFile.write(json.dumps(tree, indent=2)) 


def generate_name(name):
    name = name.lower()
    if("(" in name):
        name = re.sub(r'\([^)]*\) ', '',name)
    
    if(len(name.split(","))>4):
        print(name)    
    return name



# Mapping tree
def toDict(L):
    if(len(L)==1):
        return {"fdcID":L[0]}
    head = L.pop()
    return {head:toDict(L)}

def mapToJSON(L,D):
    if(len(L)==1):
        return D
    head = L.pop()
    if(head in D):
        D.update({head:mapToJSON(L,D[head])})
    else:
        D.update({head:toDict(L)})
    return D

def makeTree(file):
    with open(file,'r') as file:
        data = file.readlines()
        tree = {}
        for i in data:
            i = i.lower()
            i = i.rstrip("\n")
            i=i.replace(", ",",")
            if("(" in i):
                i = re.sub(r'\([^)]*\)', '',i)
            split = [a for a in i.split(',') if a != ""]
                   
            tree = mapToJSON(split[::-1],tree)
        print(json.dumps(tree,indent=2))



def getCategoryTree():
    with open("new_foods.json","r") as jsonFile:
        data = json.load(jsonFile)
        for i in data:
            print(data[i]["fullName"]+","+str(data[i]["fdcId"]))


#getCategoryTree()
makeTree("categoryTree")
