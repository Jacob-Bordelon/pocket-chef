    
def initialReformat(infile, outfile):
    with open(infile,'r') as json_file:
        data = json.load(json_file)
        data = data["food"]
        count = 0
        final_file = {}

        for i in data:
            p = reformat(i,data[i])
            final_file.update(p)

        with open(outfile,'w') as new_foods:
            new_foods.write(json.dumps(final_file, indent=2)) 

def repeatedValues(file):
    with open(file,'r') as json_file:
        data = json.load(json_file)
        
        count = 0

        
        with open("names.json",'w') as temp_file:
            for i in data:
                temp_file.writelines(data[i]["name"].lstrip(" ")+"\n")
        temp_file.close()

def getCopyCount(file):
    with open(file,'r') as json_file:
        data = json.load(json_file)
        with open('names.json','r') as temp_file:
            pos =temp_file.read().split('\n')
            for a in data:
                j = data[a]["name"].lstrip(" ")
                if(pos.count(j) > 2):
                    print(breakDown(data[a]['fullName']))
                
def breakDown(word):
    if("(" in word):
         word = re.sub(r'\([^)]*\)', ',', word)
    # words = word.split(",")
    # name = words[::-1]
    return word

def toDict(L):
    if(L==[]):
        return {}
    head = L.pop()
    return {head:toDict(L)}

def mapToJSON(L,D):
    if(L==[]):
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
            if("(" in i):
                i = re.sub(r'\([^)]*\)', ',',i)
            split = i.split(',')
            split[-1] = split[-1].rstrip("\n")            
            tree = mapToJSON(split[::-1],tree)
        print(json.dumps(tree,indent=2))


def compareCharacters(word):
    for a in removeList:
        if(a.lower() in word.lower()):
            return True
    return False

def getParts():
    tree = {}
    with open('new_foods.json','r') as file:
        data = json.load(file)
        for i in data:
            if(compareCharacters(data[i]["fullName"])!=True):
                tree.update({i:data[i]})
    print(json.dumps(tree, indent=2))