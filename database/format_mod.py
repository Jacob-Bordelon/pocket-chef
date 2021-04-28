import sys


file1 = open(sys.argv[1], 'r')
 
while True:

    line = file1.readline().strip()

    if not line:
        break
    # replace brackets to meet google's json
    line = line.replace("[","{").replace("]","}")

    # Find beginning of a json object
    if line.strip() == "{":
        # Read the next line with the first content of the json object
        line2 = file1.readline().strip()
        
        # the very begging with double brackets {{ 
        if line2.strip() == "{":
            line2 = file1.readline().strip()
            # If it starts with the fdcId, then start reformatting
            if line2.strip().find("fdcId") != -1:
                fdcId = line2.split(':')[1].strip()[0:-1]
                print('"'+fdcId+'"'+": {")
                print(line2)
                # add the image field
                print('"image":' + '"",')
        # If fdcId, then start reformatting
        elif line2.strip().find("fdcId") != -1:
            fdcId = line2.split(':')[1].strip()[0:-1]
            print('"'+fdcId+'"'+": {")
            print(line2)
            # add the image field
            print('"image":' + '"",')
        # if it starts with number, it is nutrient
        elif line2.strip().find("number") != -1:
            # reformatting
            nutrientName = file1.readline().strip().split(': ')[1].strip()[0:-1]
            nutrientName = nutrientName.replace(".", " ").replace("/"," or ")
            # next line in the nutrient's i=objects
            nextNutrientLine = file1.readline().strip()
            # only allow specific nutrients
            allowed = nutrientName.find("difference") != -1 or nutrientName.find("Atwater Specific") != -1 or nutrientName.find("Protein") != -1 or nutrientName.find("Sodium") != -1 or nutrientName.find("Total fat") != -1
            if allowed:
                print(nutrientName+": {")
            # do until the end of the nutrient's jsonarray
            while nextNutrientLine.find("}") == -1:
                    if allowed:
                        # only amount and unitname
                        if nextNutrientLine.find("amount") != -1:
                            print(nextNutrientLine)
                        elif nextNutrientLine.find("unitName") != -1:
                            print(nextNutrientLine.replace(",",""))
                            print("},")
                    # next line for looping
                    nextNutrientLine = file1.readline().strip()
            # print close bracket termination

        else:
            print(line2)

    try:
        # look for description to build name and fullname      
        if line.find("description") != -1:
            fullName = line.split(':')[1].strip()[0:-1]
            fullName = fullName.replace(".", " ").replace("/"," or ")
            name = fullName.split(", ")[1] + " " + fullName.split(", ")[0] 
            print('"fullName":' +fullName+",")
            print('"name":' +'"'+name.replace('"',"")+'"'+",")
        # ignore this objects
        elif line.find("dataType") != -1 or line.find("publicationDate") != -1 or line.find("ndbNumber") != -1 or line.find("derivationDescription") != -1:
            True
        else:
            # the { is being printed at object starters
            if line != "{":
                print(line)
        
    except IndexError:
        print(line)
    
    
 
file1.close()