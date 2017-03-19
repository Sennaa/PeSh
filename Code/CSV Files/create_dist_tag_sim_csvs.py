__author__ = 'Senna'

###########################################################################################
# This code creates the csv files for the distances between any combination of two shops, #
# the similarities for any combination of two shops and the tags for each shop.           #
###########################################################################################

import numpy as np
import pandas as pd

# just for testing
import random

# all shops data
data = pd.read_csv("DATA_shops_distances.csv",delimiter="^")

# all tags
alltagsdata = pd.read_csv("DATA_tags.csv",delimiter="^")
alltags = np.array(alltagsdata["0"])

# tagsstring, similaritiesstring and distancestring of all shops
tagsstring         = np.array(data["3"])
similaritiesstring = np.array(data["4"])
distancesstring    = np.array(data["5"])

# all shops names
names = np.array(data["0"])

### Initialize data columns ###
# Shop-Tags
shoptags_name               = [""]  * len(names) * len(alltags)
shoptags_tag                = [""]  * len(names) * len(alltags)
# Shop-Shop-Similarity
shopsimilarity_shop1        = [""]  * len(names) * len(names)
shopsimilarity_shop2        = [""]  * len(names) * len(names)
shopsimilarity_similarity   = [0.0] * len(names) * len(names)
# Shop-Shop-Distance
shopdistance_shop1          = [""]  * len(names) * len(names)
shopdistance_shop2          = [""]  * len(names) * len(names)
shopdistance_distance       = [0.0] * len(names) * len(names)

# Go through each shop separately
for i in range(0,len(names)):
    # First, make shop-tag combinations
    tags         =  tagsstring[i].split(';')
    #print tags
    for j in range(0,len(tags)):
        if tags[j] == '1':
            index = (i * len(tags)) + j # The index in the shoptags-lists is: i (the i-th shop) * len(tags) (the amount of tags) + j (the position of the current shop)
            shoptags_name[index] = names[i]
            shoptags_tag[index]  = alltags[j]
    # Then make shop-shop-similarity combinations
    similarities = similaritiesstring[i].split(';')
    #print similarities
    for j in range(0, len(similarities)):
        index = (i * len(similarities)) + j # Similar as the index for shoptags
        if j >= i:
            shopsimilarity_shop1[index]        = names[i]
            shopsimilarity_shop2[index]        = names[j]
            shopsimilarity_similarity[index]   = similarities[j]
    # Last, make shop-shop-distance combinations
    distances = []
    #print distancesstring[i]
    if distancesstring.__class__ == float.__class__:
        distances    = distancesstring[i].split(';')
        distances.remove('')
    else:
        # If not yet available, for now add some dummy data
        distances = [random.random()*500] * len(names)
    #print i, distances
    for j in range(0, len(distances)):
        index = (i * len(distances)) + j # Similar as the index for shoptags
        if j >= i:
            shopdistance_shop1[index]          = names[i]
            shopdistance_shop2[index]          = names[j]
            shopdistance_distance[index]       = distances[j]

# Delete empty cells (empty cells are the ones that would be doubles, left out because of the constraint "if j >= i")
shoptags_name = filter(lambda a: a != "", shoptags_name)
shoptags_tag  = filter(lambda a: a != "", shoptags_tag)

shopsimilarity_shop1        = filter(lambda a: a != "", shopsimilarity_shop1)
shopsimilarity_shop2        = filter(lambda a: a != "", shopsimilarity_shop2)
shopsimilarity_similarity   = filter(lambda a: a != 0.0, shopsimilarity_similarity)

shopdistance_shop1          = filter(lambda a: a != "", shopdistance_shop1)
shopdistance_shop2          = filter(lambda a: a != "", shopdistance_shop2)
shopdistance_distance       = filter(lambda a: a != 0.0, shopdistance_distance)

# Check whether lengts are correct
if len(shopsimilarity_shop1) == len(shopsimilarity_shop2) and len(shopsimilarity_shop2) == len(shopsimilarity_similarity):
    print "OK"
else:
    print "NOT OK"

if len(shopdistance_shop1) == len(shopdistance_shop2) and len(shopdistance_shop2) == len(shopdistance_distance):
    print "OK"
else:
    print "NOT OK"

# Save new data
# Tags
df_tags = pd.DataFrame(np.column_stack((shoptags_name, shoptags_tag)))
df_tags.to_csv("DATA_shoptags.csv", sep="^", index=False)
# Similarities
df_similarities = pd.DataFrame(np.column_stack((shopsimilarity_shop1, shopsimilarity_shop2, shopsimilarity_similarity)))
df_similarities.to_csv("DATA_shopsimilarities.csv", sep="^", index=False)
# Distances
df_distances = pd.DataFrame(np.column_stack((shopdistance_shop1, shopdistance_shop2, shopdistance_distance)))
df_distances.to_csv("DATA_shopdistances.csv", sep="^", index=False)