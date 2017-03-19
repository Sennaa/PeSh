__author__ = 'Senna'

import pandas as pd
import numpy as np
import re

# Step 1: Read in file
shops_path  = "DATA_shops_final_new.csv"
shops       = pd.read_csv(shops_path, delimiter='^')
# Step 2: Convert to right format
shops_name          = np.array(shops["0"])
shops_address       = np.array(shops["1"])
shops_time          = np.array(shops["2"])
shops_tags          = np.array(shops["3"])
shops_similarities  = np.array(shops["4"])
shops_lat           = np.array(shops["5"])
shops_lng           = np.array(shops["6"])
shops_dist          = np.array(shops["7"])

# for ease, create matrix of shop distances
shops_dist_matrix = [[]] * len(shops_dist)
for shop in range(0,len(shops_dist)):
    distances = re.split(';',shops_dist[shop])
    shops_dist_matrix[shop] = distances

for i in range(0,len(shops_dist_matrix)):
    for j in range(0,len(shops_dist_matrix)):
        print "(" + str(i) + "," + str(j) + ") " + shops_dist_matrix[i][j]

new_shops_dist = [""] * len(shops_dist)
for shop in range(0,len(shops_dist)):
    distances = re.split(';',shops_dist[shop])
    current_shop_distances = [0] * len(distances)
    for dist in range(0,len(distances)):
        if dist >= shop:
            current_shop_distances[dist] = shops_dist_matrix[shop][dist]
        else:
            current_shop_distances[dist] = shops_dist_matrix[dist][shop]
    new_shops_dist[shop] = ';'.join(['%.0f' % float(distance) for distance in current_shop_distances])


## Shops
df_shops = pd.DataFrame(np.column_stack((shops_name, shops_address, shops_time, shops_tags, shops_similarities, shops_lat, shops_lng, new_shops_dist)))
df_shops.to_csv('DATA_shops_final_new2.csv', sep='^', index=False)