import simplejson, urllib
import pandas as pd
import numpy as np
import time

shop_coordinates = pd.read_csv("shop_coordinates.csv",delimiter="^")

shops       = np.array(shop_coordinates["Shop"])
addresses   = np.array(shop_coordinates["Address"])
lats        = np.array(shop_coordinates["Latitude"])
lons        = np.array(shop_coordinates["Longitude"])

distances_sec_strings = [""] * len(shops)
distances_sec_list = np.zeros((len(shops),len(shops)))

for i in range(124,125):#for i in range(len(shops)-103,len(shops)-102):#
    # Per shop, make a string of distances
    distances_shop_string = ""
    # Also make a list per shop
    distances_shop_list = [0] * len(shops)

    orig_coord = addresses[i]
    for j in range(0,len(shops)):
        if i!=j:
            # If j > i, the distance should be calculated
            if j > i:
                print shops[i]
                dest_coord = addresses[j]
                #dest_coord = lats[j], lons[j]
                origins = str(orig_coord) + ", Nijmegen"
                #origins = str(orig_coord[0]) + "," + str(orig_coord[1])
                #print origins
                destinations = str(dest_coord) + ", Nijmegen"
                #destinations = str(dest_coord[0]) + "," + str(dest_coord[1])
                #print destinations
                key = "AIzaSyCIGiMludAd4zKIeQB7_Ho7urNTWAHG8rc"
                url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins={0}&destinations={1}&key={2}&mode=walking&language=en-EN&sensor=false".format(origins,destinations,key)
                result = simplejson.load(urllib.urlopen(url))
                time.sleep(0.1)
                print result
                walking_time = result['rows'][0]['elements'][0]['duration']['value']
                print walking_time
                #print "From " + str(shops[i]) + " to " + str(shops[j]) +  " takes " + str(walking_time) + " seconds."
                distances_shop_string += str(walking_time) + ";"
                distances_shop_list[j] = walking_time
            # Otherwise, the distance is already calculated and can be taken from there
            else:
                # Get the calculated value (distance)
                distance = distances_sec_list[j][i]
                distances_shop_string += str(distance) + ";"
                distances_shop_list[j] = distance
        else:
            distances_shop_string += "0;"
            distances_shop_list[j] = 0
    distances_sec_strings[i] = distances_shop_string
    distances_sec_list[i] = distances_shop_list

### Save distances to shops ###
# First load shops data
shops = pd.read_csv("DATA_shops.csv",delimiter="^")
# Then add the distances
shops['7'] = pd.Series(distances_sec_strings, index=shops.index)
# Then save again
shops.to_csv("DATA_shops_distances2.csv",sep="^",index=False)