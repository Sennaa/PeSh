__author__ = 'Senna'

import numpy as np
import pandas as pd
import re

## Shops: name, address, time, tags, similarities
# Step 1: Read in file
shops_path  = "DATA_shops.csv"
shops       = pd.read_csv(shops_path, delimiter='^')

# Step 2: Convert to right format
shops_tags      = np.array(shops["3"])

total_value = [0] * len(shops_tags)

for shop in range(0,len(shops_tags)):
    tags = re.split(';',shops_tags[shop])
    total_value[shop] = tags.count('1')

print np.mean(total_value)

print np.std(total_value)
