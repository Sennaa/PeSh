__author__ = 'Senna'

# Code to create personas, based on the keywords assigned to them. For each persona a file is created of the following
# form:
# PosTags | PosTag 1 | PosTag 2 | PosTag 3 | etc.
# NegTags | NegTag 1 | NegTag 2 | NegTag 3 | etc.
# ShopList| Shop 1 | Shop 2 | Shop 3 | etc.
# VisList | Vis 1 | Vis 2 | Vis 3 | etc. (where Vis 1, Vis 2, etc. is 0 or 1)
# RatList | Rat 1 | Rat 2 | Rat 3 | etc.
# Where:
# PosTags are the positive tags assigned to the persona
# NegTags are the negative tags assigned to the persona
# ShopList is the list of all shops
# VisList is the list of shops that is 'visited' by the persona (shops that have one of the negative or positive tags)
# RatList is the list of ratings (weights) of the shops based on the three variables above.

import pandas as pd
import numpy as np
import csv

print 'Start...'

### Load files ###
print 'Load files...'
# Load info about personas (tags)
personas        = pd.read_csv("../TagDataPersonas.csv", delimiter='^')

# Load list of shops with keywords
keywordsShops   = pd.read_csv("../../Keywoorden Winkels/KeywordsShops.csv", delimiter='^')


### Initializations ###
maxTags     = 10 # Maximum amount of pos- or neg tags that can be in personas file
amountShops = len(keywordsShops["Winkelnaam"])
shops       = np.array(keywordsShops["Winkelnaam"]).tolist()

### Create the personas files ###
print 'Create personas files...'
for index, row in personas.iterrows():
    print '  Persona ' + str(index+1) + '...'
    # File name is the name of the persona
    filename = row["Persona"]
    ## Get positive and negative tags ##
    # Initialize tags
    posTags = []
    negTags = []
    for i in range(1,maxTags+1):
        posTag = "PosTag" + str(i)
        negTag = "NegTag" + str(i)
        if row[posTag] == row[posTag]:  # if the cell is not empty (nan != nan)
            # Add tag
            posTags.append(row[posTag])
        if row[negTag] == row[negTag]:  # if the cell is not empty (nan != nan)
            # Add tag
            negTags.append(row[negTag])

    ## Get visited list and rating list ##
    # Initialize visited list and rating list
    visList = [0.0] * amountShops
    ratList = [0.0] * amountShops
    # Go through the list of all the shops with their keywords
    for indexShops, rowShops in keywordsShops.iterrows():
        # Amount of overlapping keywords in posTags and the shop's keywords
        amountPos = 0.0
        amountNeg = 0.0
        amountSpec = 0.0
        # Total amount of keywords of the shop
        totalAmount = 0.0
        # Both booleans must be true to add the shop (for special case)
        comb1 = False
        comb2 = False
        # A shop should be added when both of the following tags are a keyword of that shop (for special case)
        combPosTag1 = row["CombPosTags1"]
        combPosTag2 = row["CombPosTags2"]
        # Per shop, look at the keywords (/tags)
        for i in range(1,maxTags+1):
            # indexTag is the header name
            indexTag = "Tag " + str(i)
            # If the tag is not empty..
            if rowShops[indexTag] == rowShops[indexTag]:
                # A keyword is found so the total amount of keywords increases by one..
                totalAmount += 1.0
                # First, look for each tag in the POSITIVE tags #
                for tag in (posTags):
                    # If the tag in the posTags is equal to the tag of the shop..
                    if tag == rowShops[indexTag]:
                        # ..add the shop to the visited list (1 means visited, 0 means not visited)
                        visList[indexShops] = 1.0
                        # An extra overlapping keyword is found, so amount + 1
                        amountPos += 1.0

                # Secondly, look for each tag in the NEGATIVE tags #
                for tag in (negTags):
                    # If the tag in the negTags is equal to the tag of the shop..
                    if tag == rowShops[indexTag]:
                        # ..add the shop to the visited list (1 means visited, 0 means not visited)
                        visList[indexShops] = 1.0
                        # An extra overlapping keyword is found, so amount + 1
                        amountNeg += 1.0

                # Lastly, look at the special cases #
                # A special case is where two tags are needed for a shop to be assigned to a persona (e.g. "Persoonlijke Verzorging" and "Levensmiddelen")
                # Check whether the tag is equal to one of the combined Positive Tags
                if combPosTag1 == rowShops[indexTag]:
                    comb1 = True
                if combPosTag2 == rowShops[indexTag]:
                    comb2 = True

        if comb1 and comb2:
            visList[indexShops] == 1
            # amountSpec + 2 because there are two tags
            amountSpec += 2.0
        # Positive tags, so the rating is added. Rating is calculated by:
        # (Amount of overlapping positive keywords with shop / Total amount of keywords of shop) - (Amount of overlapping negative keywords with shop / Total amount of keywords of shop) + (Amount of overlapping special cases positive keywords with shop / Total amount of keywords of shop)
        ratList[indexShops] = (amountPos / totalAmount) - (amountNeg / totalAmount) + (amountSpec / totalAmount)

    ## Write to file ##
    print '    Write to file...'
    with open('../DataPersonas/personas_' + filename + '.csv', 'wb') as f:
        writer = csv.writer(f, delimiter='^')
        writer.writerow(['PosTags'] + posTags)
        writer.writerow(['NegTags'] + negTags)
        writer.writerow(['ShopList'] + shops)
        writer.writerow(['VisList'] + visList)
        writer.writerow(['RatList'] + ratList)

print 'Finish...'