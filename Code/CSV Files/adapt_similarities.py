__author__ = 'Senna'

import numpy as np
import pandas as pd
import re

### Files ###

## Shops: name, address, time, tags, similarities
# Step 1: Read in file
shops_path  = "DATA_shops.csv"
shops       = pd.read_csv(shops_path, delimiter='^')
# Step 2: Convert to right format

shops_name      = np.array(shops["0"])
shops_address   = np.array(shops["1"])
shops_time      = np.array(shops["2"])
shops_tags      = np.array(shops["3"])
shops_lat       = np.array(shops["5"])
shops_lng       = np.array(shops["6"])
shops_dist      = np.array(shops["7"])

shops_similarities = [""] * len(shops_name)
for shop1 in range(0, len(shops_name)):
    shops_similarities_shop = [0.0] * len(shops_name)
    for shop2 in range(0, len(shops_name)):
        shop1tags = shops_tags[shop1]
        shop1tagslist = re.split(';',shop1tags)
        shop2tags = shops_tags[shop2]
        shop2tagslist = re.split(';',shop2tags)
        # Amount of equal 1s (1-1) / Total amount of 1s
        equals = [i for i in range(0,len(shop1tagslist)) if shop1tagslist[i] == shop2tagslist[i] and float(shop1tagslist[i]) == 1]
        equals_amount = len(equals)
        len1 = len([i for i in range(0, len(shop1tagslist)) if float(shop1tagslist[i]) == 1])
        len2 = len([i for i in range(0, len(shop2tagslist)) if float(shop2tagslist[i]) == 1])
        total = len1 + len2
        shops_similarities_shop[shop2] = float(float(equals_amount) / float(total))
    shops_similarities[shop1] = ';'.join(['%.3f' % shop for shop in shops_similarities_shop])

## Shops
df_shops = pd.DataFrame(np.column_stack((shops_name, shops_address, shops_time, shops_tags, shops_similarities, shops_lat, shops_lng, shops_dist)))
df_shops.to_csv('DATA_shops_final_new.csv', sep='^', index=False)
