__author__ = 'Senna'

import numpy as np
import pandas as pd
import time
import simplejson
import urllib

# Read shops data
shops = pd.read_csv("DATA_shops_new.csv", delimiter='^')

# Get names and addresses arrays
names = np.array(shops["0"])
addresses = np.array(shops["1"])

# Initialize coordinates
coordinates = np.zeros((len(names),2)) # 2 because latitude and longitude are saved separately

# Find the coordinates for each shop
for i in range(0,len(names)):
    key = "AIzaSyCsED5wQGkVi16KtY9Wns_JE4Gnu_B-P3U"
    address = addresses[i] + ", Nijmegen"
    address = address.replace(" ", "+")
    url = "https://maps.googleapis.com/maps/api/geocode/json?address={0}&key={1}".format(address, key)
    result = simplejson.load(urllib.urlopen(url))
    print result
    # Set time.sleep to prevent exceeding the max requests per second
    time.sleep(0.1)
    lat = result['results'][0]['geometry']['location']['lat']
    lng = result['results'][0]['geometry']['location']['lng']
    print lat, lng
    coordinates[i,0] = lat
    coordinates[i,1] = lng

# Save names | addresses | latitudes | longitudes
nf = np.column_stack((names,addresses,coordinates[:,0], coordinates[:,1]))
df = pd.DataFrame(nf,columns=["Shop","Address","Latitude","Longitude"])
df.to_csv("DATA_shops_coordinates.csv",sep="^",index=False)