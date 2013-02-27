import csv
from numpy import polynomial as P
import matplotlib.pyplot as plt

#headings = ["weight","speed","resistance"]
#deg = 2
#filename = "speed_resistance_constant_slope.log"

headings = ["slope","speed","resistance"]
deg = 2
filename = "slope_speed_resistance_constant_weight.log"


x = []
y = []

z = []
k = []

def do_curve_fit():
    c, stats = P.polyfit(x,y,deg, full= True)
    print c
    z.append(weight)
    k.append(c)

with open(filename, 'rb') as csvfile:
    spamreader = csv.reader(csvfile, delimiter=';')
    found_header = False
    weight = None
    for row in spamreader:
        if len(row) < 2:
            continue
        if len(row) > 0 and row == headings:
            if found_header is True:
                break
            found_header = True
            continue
        if not found_header:
            continue
        if weight and row[0] != weight:
            do_curve_fit()
            x = []
            y = []
        weight = row[0]
        x.append(float(row[1]))
        y.append(float(row[2]))

with open("out.log", 'w+') as csvfile:
    for i,var in enumerate(z):
        if (i > len(k)):
            break
        csvfile.write(var)
        csvfile.write(";")
        csvfile.write(';'.join(map(str,k[i])))
        csvfile.write("\n")
        




