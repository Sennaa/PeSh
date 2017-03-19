__author__ = 'Senna'

import pandas as pd
import numpy as np
import re

persona_data = pd.read_csv("DATA_personas.csv", delimiter="^")

persona0 = np.array(persona_data["0"])
persona1 = np.array(persona_data["1"])
persona2 = np.array(persona_data["2"])
persona3 = np.array(persona_data["3"])
persona4 = np.array(persona_data["4"])
persona5 = np.array(persona_data["5"])
persona6 = np.array(persona_data["6"])

new_p4 = []
new_p5 = []

for shop in persona4:
    p4 = re.split(';', shop)
    print len(p4)
    temp_p4 = ""
    for p in range(0,len(p4)):
        if p == 89 or p == 6:
            i = 1
        else:
            temp_p4 += p4[p] + ";"
    temp_p4 = temp_p4[:-1]
    new_p4.append(temp_p4)
    print len(re.split(';',temp_p4))

for shop in persona5:
    p5 = re.split(';', shop)
    print len(p5)
    temp_p5 = ""
    for p in range(0,len(p5)):
        if p == 89 or p == 6:
            i = 1
        else:
            temp_p5 += p5[p] + ";"
    temp_p5 = temp_p5[:-1]
    new_p5.append(temp_p5)
    print len(re.split(';',temp_p5))

new_data_personas = pd.DataFrame(np.column_stack((persona0, persona1, persona2, persona3, new_p4, new_p5, persona6)))
new_data_personas.to_csv('DATA_personas_new.csv', sep='^', index=False)