__author__ = 'Senna'

import pandas as pd
import numpy as np
import re

shop_data = pd.read_csv("DATA_shops.csv", delimiter="^")

shop_names = np.array(shop_data["0"])
shop_names_list = shop_names.tolist()
delete_shopnames = ["Nonna-nini", "cadeaubazaar.nl"]

shop1 = np.array(shop_data["1"])
shop2 = np.array(shop_data["2"])
shop3 = np.array(shop_data["3"])
shop5 = np.array(shop_data["5"])
shop6 = np.array(shop_data["6"])

delete_shopnames_indices = []
for shop in delete_shopnames:
    delete_shopnames_indices.append(shop_names_list.index(shop))
    print shop_names_list.index(shop)

shop_similarities = np.array(shop_data["4"])
shop_distances = np.array(shop_data["7"])

new_similarities = [""] * shop_names.size
new_distances = [""] * shop_names.size

index_similarities = 0
for shop in shop_similarities:
    similarities = re.split(';',shop)
    print len(similarities)
    temp_list = np.delete(similarities,delete_shopnames_indices)
    temp_list = [float(a) for a in temp_list]
    print len(temp_list)
    temp_string = ""
    for index in range(0,len(temp_list)):
        temp_string = temp_string + str(temp_list[index]) + ";"
    temp_string = temp_string[:-1]
    new_similarities[index_similarities] = temp_string
    index_similarities += 1

index_distances = 0
for shop in shop_distances:
    distances = re.split(';',shop)
    print len(distances)
    temp_list = np.delete(distances, delete_shopnames_indices)
    print len(temp_list)
    temp_string = ""
    for index in range(0, len(temp_list)):
        temp_string = temp_string + str(temp_list[index]) + ";"
    temp_string = temp_string[:-1]
    new_distances[index_distances] = temp_string
    index_distances += 1

new_similarities = np.array(new_similarities)
new_distances = np.array(new_distances)

#new_data_shops = pd.DataFrame(np.column_stack((shop_names, shop1, shop2, shop3, new_similarities, shop5, shop6, new_distances)))
#new_data_shops.drop(new_data_shops.index[delete_shopnames_indices])
#new_data_shops.to_csv('DATA_shops.csv', sep='^', index=False)