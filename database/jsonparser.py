import sys


file1 = open(sys.argv[1], 'r')
 
while True:

    line = file1.readline().strip()

    if not line:
        break

    if line == "},":
        line2 = file1.readline().strip()
        if line2 == "}":
            print("}")
            print("}")
        else:
            print(line)
            print(line2)
    else:
        print(line)
