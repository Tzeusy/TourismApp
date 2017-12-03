# -*- coding: utf-8 -*-
"""
Created on Sat Dec 02 00:31:21 2017

@author: Ruo Xi
"""

from GoogleMaps import GoogleMaps

g = GoogleMaps()

locations = []

for l in open('EntertainmentLatlongs.txt'):
    arr = l.split("|")
    locations.append((arr[0], arr[1]))
    
d = open("taxiDistanceE.csv" , "w")
t = open("taxiTimeE.csv", "w")

taxiTimes = []
taxiDistances = []

for i in range(len(locations)):
    taxiTimes.append([])
    taxiDistances.append([])
    for j in range(len(locations)):
        taxiTimes[i].append(0)
        taxiDistances[i].append(0)
        
for i in range(len(locations)):
    for j in range(len(locations)):
        if locations[i][0] == locations[j][0]:
            continue
        r = g.getDistanceData(locations[i][1],locations[j][1])["rows"][0]["elements"][0]
        print r
        if r['status'] == "ZERO_RESULTS":
            print "Failed " + locations[i][0] + "->" + locations[j][0]
            continue
        taxiTimes[i][j] = r["duration"]["value"]
        taxiDistances[i][j] = r["distance"]["value"]
        x = taxiTimes[i][j]
        y = taxiDistances[i][j] 
        print x
        print y

for i in locations:
    d.write("," + str(i))
    t.write("," + str(i))

d.write("\n")
t.write("\n")

for a in range(len(locations)):
    d.write(str(locations[a]))
    t.write(str(locations[a]))
    for b in range(len(locations)):
        d.write("," + str(taxiTimes[a][b]))
        t.write("," + str(taxiDistances[a][b]))
    d.write("\n")
    t.write("\n")
