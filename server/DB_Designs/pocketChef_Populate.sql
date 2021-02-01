INSERT INTO MEASURE (Measure) VALUES('CUP'), ('TEASPOON'), ('TABLESPOON'), ('UNIT');

INSERT INTO FOOD (FName,FCalories) VALUES('egg',10), ('salt',10), ('sugar',10), ('chocolate',10), ('vanilla extract',10), ('flour',10);

INSERT INTO RECIPE (RName, Description,  Instructions) VALUES('Boiled Egg', 'A single boiled egg', 'Add egg to cold water. Bring water to boil. Cook.');

INSERT INTO RECIPE (RName, Description, Instructions) VALUES('Chocolate Cake', 'Yummy cake', 'Add eggs, flour, chocolate to pan. Bake at 350 for 1 hour');

INSERT INTO REC_INGREDIENT (RIID, Food_ID, Measure_ID, Amount) VALUES (1, 1, 4, 1);

INSERT INTO REC_INGREDIENT (RIID, Food_ID, Measure_ID, Amount)  VALUES (2, 1, 4, 3);

INSERT INTO REC_INGREDIENT (RIID, Food_ID, Measure_ID, Amount)  VALUES (2, 2, 2, 1);

INSERT INTO REC_INGREDIENT (RIID, Food_ID, Measure_ID, Amount)  VALUES (2, 3, 1, 2);

INSERT INTO REC_INGREDIENT (RIID, Food_ID, Measure_ID, Amount)  VALUES (2, 4, 1, 1);