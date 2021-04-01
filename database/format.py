file1 = open('6.json', 'r')
count = 0
 
while True:
    line = file1.readline().strip()

    if not line:
        break

    line = line.replace("[","{").replace("]","}")

    try:
        if line.find("unitName") != -1:
                line3 = line.rstrip(',')
                print(line3)
        else:
            if line != "{":
                print(line)
    except IndexError:
        print(line)

    if line.strip() == "{":
        line2 = file1.readline().strip()
        if line2.strip().find("fdcId") != -1:
            line3 = file1.readline().strip().split(':')[1].strip()[0:-1]
            line3 = line3.replace(".", " ").replace("/"," or ")
            print(line3+": {")
            print(line2)
        elif line2.strip() == "{":
            line2 = file1.readline()
            line3 = file1.readline().strip().split(':')[1].strip()[0:-1]
            line3 = line3.replace(".", " ").replace("/"," or ")
            print("{")
            print(line3+": {")
            print(line2.strip())
        else:
            try:
                if line2.strip().find("name") != -1:
                    line3 = line2.strip().split(': ')[1].strip()[0:-1]
                    line3 = line3.replace(".", " ").replace("/"," or ")
                    print(line3+": {")
                else:
                    print(line2)
            except IndexError:
                print(line2)
    
 
file1.close()