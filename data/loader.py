# -*- coding: utf-8 -*-
"""
Created on Sat Dec 02 00:12:19 2017

@author: Ruo Xi
"""

from GoogleMaps import GoogleMaps

g = GoogleMaps()

locations = dict()

f = open("EntertainmentLatlongs.txt", "w")

for l in open("EntertainmentLocations.txt"):
    r = g.getLatLong(l)["results"]
    if len(r) == 0:
        print "Couldnt find " + l
        continue
    r = r[0]["geometry"]["location"]
    locations[l] = str(r["lat"]) + "," + str(r["lng"])
    f.write(l[:len(l) - 1] + "|" + locations[l] + "\n")
    f.flush()