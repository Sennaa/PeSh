__author__ = 'Senna'

import numpy as np
import pandas as pd
import re

### Files ###

## Shops: name, address, time, tags, similarities
# Step 1: Read in file
shops_path  = "KeywordsShops.csv"
shops       = pd.read_csv(shops_path, delimiter='^')
# Step 2: Convert to right format
shops_cols = ["0", "1", "2", "3", "4", "5", "6", "7"]

shops_name      = np.array(shops["0"])
shops_address   = np.array(shops["1"])
shops_time      = np.array([10] * len(shops_name)) # NOTE: EVERY SHOP 10 MINUTES
shops_tags      = [""] * len(shops_name)

## Tags: name, similarities
# Step 1: Read in file
tags_path   = "Tags.csv"
tags        = pd.read_csv(tags_path, delimiter='^')
# Step 2: Convert to right format
tags_name = np.array(tags["name"])
tags_similarities = [""] * len(tags_name)

## BEGIN    Shopstags and similarities
for index, row in shops.iterrows():
    #print index
    shops_tags_shop      = [0] * len(tags_name)
    # Look at tags per shop
    for i in range(2, len(row)):
        if str(row[i]) == 'nan':
            continue
        tag = row[i]
        tag_list = tags_name.tolist()
        tag_index = tag_list.index(tag)
        shops_tags_shop[tag_index] = 1
    #print ';'.join([str(tag) for tag in shops_tags_shop])
    #print [str(tag) for tag in shops_tags_shop]
    #print ';'.join([str(tag) for tag in shops_tags_shop])
    #print ';'.join([str(tag) for tag in shops_tags_shop])
    shops_tags[index] = ''.join([str(tag) for tag in shops_tags_shop])
    #print shops_tags[index]

#print shops_tags

shops_similarities = [""] * len(shops_name)
for shop1 in range(0, len(shops_name)):
    shops_similarities_shop = [0.0] * len(shops_name)
    for shop2 in range(0, len(shops_name)):
        shop1tags = shops_tags[shop1]
        shop1tagslist = re.split(';',shop1tags)
        shop2tags = shops_tags[shop2]
        shop2tagslist = re.split(';',shop2tags)
        # TODO: is this good?
        # Amount of equal 1s (1-1) / Total amount of 1s
        equals = [i for i in range(0,len(shop1tagslist)) if shop1tagslist[i] == shop2tagslist[i] and shop1tagslist[i] == 1]
        equals_amount = len(equals)
        len1 = len([i for i in range(0, len(shop1tagslist)) if shop1tagslist[i] == 1])
        len2 = len([i for i in range(0, len(shop2tagslist)) if shop2tagslist[i] == 1])
        total = len1 + len2
        shops_similarities_shop[shop2] = float(float(equals_amount) / float(total))
    shops_similarities[shop1] = ';'.join(['%.3f' % shop for shop in shops_similarities_shop])
## END      Shopstags and similarities

# Calculate how many shops tag1 and tag2 both occur or both not occur
for tag1 in range(0, len(tags_name)):
    tags_similarities_tag = [0.0] * len(tags_name)
    for tag2 in range(0, len(tags_name)):
        shop_count = 0
        #print '   Tag 2: ' + tags_name[tag2]
        for shop in range(0, len(shops_name)):
            shops_tags_list = re.split(';',shops_tags[shop])

            index_tag1 = tags_name.tolist().index(tags_name[tag1])
            index_tag2 = tags_name.tolist().index(tags_name[tag2])
            if shops_tags_list[index_tag1] == shops_tags_list[index_tag2]:
                # shop_count += 1
                shop_count += 1
        tags_similarities_tag[tag2] = float(shop_count) / float(len(shops_name))
    tags_similarities[tag1] = ';'.join(['%.3f' % tag for tag in tags_similarities_tag])

tags_cols = ["name, tagsimilarities"]

## Personas: ID, name, postags, negtags, visitedlist, ratinglist, description
# Step 1: Read in files
persona_column_names = ['a' + str(i) for i in range(0,351)] #max length is 351
personas_path_1    = "personas_Beauty_fashionliefhebber_man.csv"
personas_1         = pd.read_csv(personas_path_1, names=persona_column_names, delimiter='^', header=None)
personas_path_2    = "personas_Beauty_fashionliefhebber_vrouw.csv"
personas_2         = pd.read_csv(personas_path_2, names=persona_column_names, delimiter='^', header=None)
personas_path_3    = "personas_Binnenhuisarchitect.csv"
personas_3         = pd.read_csv(personas_path_3, names=persona_column_names, delimiter='^', header=None)
personas_path_4    = "personas_Boekenfanaat.csv"
personas_4         = pd.read_csv(personas_path_4, names=persona_column_names, delimiter='^', header=None)
personas_path_5    = "personas_Bouwliefhebber.csv"
personas_5         = pd.read_csv(personas_path_5, names=persona_column_names, delimiter='^', header=None)
personas_path_6    = "personas_Dier_natuurvriend.csv"
personas_6         = pd.read_csv(personas_path_6, names=persona_column_names, delimiter='^', header=None)
personas_path_7    = "personas_Elektronicaliefhebber.csv"
personas_7         = pd.read_csv(personas_path_7, names=persona_column_names, delimiter='^', header=None)
personas_path_8    = "personas_Gezondheidsfreak.csv"
personas_8         = pd.read_csv(personas_path_8, names=persona_column_names, delimiter='^', header=None)
personas_path_9    = "personas_Kind_4_12.csv"
personas_9         = pd.read_csv(personas_path_9, names=persona_column_names, delimiter='^', header=None)
personas_path_10   = "personas_Kunstliefhebber.csv"
personas_10        = pd.read_csv(personas_path_10, names=persona_column_names, delimiter='^', header=None)
personas_path_11   = "personas_Ouders_van_jonge_kinderen.csv"
personas_11        = pd.read_csv(personas_path_11, names=persona_column_names, delimiter='^', header=None)
personas_path_12   = "personas_Sieradenpersoon.csv"
personas_12        = pd.read_csv(personas_path_12, names=persona_column_names, delimiter='^', header=None)
personas_path_13   = "personas_Tiener_boy.csv"
personas_13        = pd.read_csv(personas_path_13, names=persona_column_names, delimiter='^', header=None)
personas_path_14   = "personas_Tiener_girl.csv"
personas_14        = pd.read_csv(personas_path_14, names=persona_column_names, delimiter='^', header=None)

# Persona names
personas_8_name = "Gezondheidsfreak"
personas_7_name = "Elektronicaliefhebber"
personas_11_name = "Ouder van jonge kinderen"
personas_9_name = "Kind (4-12)"
personas_13_name = "Tiener (jongen)"
personas_14_name = "Tiener (meisje)"
personas_4_name = "Boekenfanaat"
personas_6_name = "Dier & Natuurvriend"
personas_12_name = "Sieradenliefhebber"
personas_10_name = "Kunstliefhebber"
personas_5_name = "Bouwliefhebber / Doe-het-zelver"
personas_1_name = "Beauty & Fashion Liefhebber (man)"
personas_2_name = "Beauty & Fashion Liefhebber (vrouw)"
personas_3_name = "Binnenhuisarchitect"

# Persona descriptions
personas_1_description = "You love fashion and beauty products. You find personal healthcare important, as well as fashionable clothing. You want to look perfect according to the latest fashion. You're not a handyman."
personas_2_description = "You love fashion and beauty products. You find personal healthcare important, as well as fashionable clothing. You want to look perfect according to the latest fashion. You're not a handyman."
personas_3_description = "You like items that can improve the interior design of a house, ranging from furniture to kitchenware. However, you don't like to build the items yourself."
personas_4_description = "You like reading books, whether they're old or new, fiction or non-fiction. However, you're not really into doing sports."
personas_5_description = "You like making and building things. Whether it's inside or outside, for example your home or your garden, you find joy in making stuff. You're not a Beauty & Fashion lover."
personas_6_description = "You love animals and like to visit stores that are related to animals, whether they sell animals themselves or accessories for animals. Also, you love nature and therefore like to visit nature related stores as well."
personas_7_description = "You like electronic gadgets. Products like a mobile phone, television and computer are interesting. Doing sports yourself is not much fun, watching it on your television, tablet or phone is fun though. Further, you like to play (computer)games every now and then. Lastly, music is a big love of yours."
personas_8_description = "You are a health-freak. You find it fun to be active and you do fitness or another sports multiple times a week. Further, you find it important to eat healthy food, fast-food is odious. Staying in shape is of great importance."
personas_9_description = "You like shops that sell products for children, like toys and children's clothing. Furthermore, you like candy that you buy with your spare money. You don't like visiting shops that have nothing to do with children."
personas_10_description = "You're an art-lover in a broad sense. You like antiques, paintings, music, books, etc. However, you do not like to make any of them yourself."
personas_11_description = "As a parent of young children, you want to shop for both your children and yourself. Your kids have to look well and so do you. Therefore, you want to have clothing for yourself and your kids. Also, the kids might like some toys to play with. Further, you might look for clothes for your partner and buy things you need in the household. You already have a house, so there's no need for products like furniture."
personas_12_description = "Jewelry is what you immediately go looking for when you go shopping. Necklaces, bracelets, rings, you wear them all. On the other hand, you have no affinity with electronics or computers."
personas_13_description = "You like to visit clothing shops with clothes and shoes for children of your age. Further, you play computer games in your spare time and like electronics. Also, you like eating candy. You don't like shops or products concerning the household."
personas_14_description = "You like to visit clothing shops with clothes and shoes for children of your age. Also, you take good  personal care and like fashion accessories. You like eating candy. You don't like electronics."

persona_ids          = [""] * 14
persona_names        = [""] * 14
persona_postags_all  = [""] * 14
persona_negtags_all  = [""] * 14
persona_shoplists    = [""] * 14
persona_vislists     = [""] * 14
persona_ratlists     = [""] * 14
persona_descriptions = [""] * 14

def convert_personas(id_persona, persona, name, description):
    # Step 2: Convert to right format
    persona_id   = id_persona - 1
    persona_name = name
    for index, row in persona.iterrows():
        persona_postags = [2] * len(tags_name)
        persona_negtags = [2] * len(tags_name)
        persona_vislist = [2] * len(row)
        persona_ratlist = [2] * len(row)
        persona_shoplist = [2] * len(shops_name)
        # Skip first column (sort of header)
        if (index == 0): # postags
            for i in range(1,len(row)):
                if str(row[i]) == 'nan':
                    continue
                tag = row[i]
                tags_list = tags_name.tolist()
                tag_index = tags_list.index(tag)
                persona_postags[tag_index] = 1
            persona_postags_all[persona_id] = ';'.join(['%.0f' % tag for tag in persona_postags])
        if (index == 1): # negtags
            #print row
            for i in range(1,len(row)):
                if str(row[i]) == 'nan':
                    continue
                tag = row[i]
                tags_list = tags_name.tolist()
                tag_index = tags_list.index(tag)
                persona_negtags[tag_index] = 1
            persona_negtags_all[persona_id] = ';'.join(['%.0f' % tag for tag in persona_negtags])
        #if (index == 2): # shoplist
        #    for i in range(1,len(row)):
        #        if str(row[i]) == 'nan':
        #            continue
        #        shop = row[i]
        #        shops_list = shops_name.tolist()
        #        shop_index = shops_list.index(shop)
        #        persona_shoplist[shop_index] = 1
        if (index == 3): # vislist
            for i in range(1,len(row)):
                if str(row[i]) == 'nan':
                    continue
                persona_vislist[i-1] = row[i]
            persona_vislists[persona_id] = ';'.join(['%.0f' % float(vis) for vis in persona_vislist])
            #print persona_vislists[persona_id]
        if (index == 4): # ratlist
            for i in range(1,len(row)):
                if str(row[i]) == 'nan':
                    continue
                persona_ratlist[i-1] = row[i]
            persona_ratlists[persona_id] = ';'.join(['%.3f' % float(rat) for rat in persona_ratlist])

        persona_description = description

        #persona_postags_all[persona_id] = ';'.join(['%.0f' % tag for tag in persona_postags])
        #persona_negtags_all[persona_id] = ';'.join(['%.0f' % tag for tag in persona_negtags])
        #persona_vislists[persona_id] = ';'.join(['%.0s' % vis for vis in persona_vislist])
        #persona_ratlists[persona_id] = ';'.join(['%.0s' % rat for rat in persona_ratlist])

    #print persona_postags_all

    persona_ids[persona_id] = id_persona
    persona_names[persona_id] = persona_name
    persona_descriptions[persona_id] = persona_description


convert_personas(1, personas_1, personas_1_name, personas_1_description)
convert_personas(2,personas_2, personas_2_name, personas_2_description)
convert_personas(3, personas_3, personas_3_name, personas_3_description)
convert_personas(4, personas_4, personas_4_name, personas_4_description)
convert_personas(5, personas_5, personas_5_name, personas_5_description)
convert_personas(6, personas_6, personas_6_name, personas_6_description)
convert_personas(7, personas_7, personas_7_name, personas_7_description)
convert_personas(8, personas_8, personas_8_name, personas_8_description)
convert_personas(9, personas_9, personas_9_name, personas_9_description)
convert_personas(10, personas_10, personas_10_name, personas_10_description)
convert_personas(11, personas_11, personas_11_name, personas_11_description)
convert_personas(12, personas_12, personas_12_name, personas_12_description)
convert_personas(13, personas_13, personas_13_name, personas_13_description)
convert_personas(14, personas_14, personas_14_name, personas_14_description)

personas_cols = ["ID", "name", "postags", "negtags", "visitedlist", "ratinglist", "description"]

## TODO: add shopdistances

tags_name = np.array(tags_name)
tags_similarities = np.array(tags_similarities)

persona_ids = np.array(persona_ids)
persona_names = np.array(persona_names)
persona_postags_all = np.array(persona_postags_all)
persona_negtags_all = np.array(persona_negtags_all)
persona_vislists = np.array(persona_vislists)
persona_ratlists = np.array(persona_ratlists)
persona_descriptions = np.array(persona_descriptions)

shops_name = np.array(shops_name)
shops_address = np.array(shops_address)
shops_time = np.array(shops_time)
shops_tags = np.array(shops_tags)
shops_similarities = np.array(shops_similarities)


### Save ###
## Tags
df_tags = pd.DataFrame(np.column_stack((tags_name, tags_similarities)))
df_tags.to_csv('DATA_tags_new.csv', sep='^', index=False)

## Personas
df_personas = pd.DataFrame(np.column_stack((persona_ids, persona_names, persona_postags_all, persona_negtags_all, persona_vislists, persona_ratlists, persona_descriptions)))
df_personas.to_csv('DATA_personas_new.csv', sep='^', index=False)

## Shops
df_shops = pd.DataFrame(np.column_stack((shops_name, shops_address, shops_time, shops_tags, shops_similarities)))
df_shops.to_csv('DATA_shops_new.csv', sep='^', index=False)
