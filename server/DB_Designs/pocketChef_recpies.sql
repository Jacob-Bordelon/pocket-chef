reate database Cookbook; 

connect Cookbook; 
	
create table Recipe (id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, name VARCHAR(25), description VARCHAR(50), instructions VARCHAR(500)) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table Ingredient (id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, name VARCHAR(50)) ENGINE=InnoDB DEFAULT CHARSET=utf8; 

create table Measure (id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, name VARCHAR(30)) ENGINE=InnoDB DEFAULT CHARSET=utf8; 

create table RecipeIngredient (recipe_id INT NOT NULL, ingredient_id INT NOT NULL, measure_id INT, amount INT, 
	CONSTRAINT fk_recipe FOREIGN KEY(recipe_id) REFERENCES Recipe(id), 
	CONSTRAINT fk_ingredient FOREIGN KEY(ingredient_id) REFERENCES Ingredient(id), 
	CONSTRAINT fk_measure FOREIGN KEY(measure_id) REFERENCES Measure(id)) 
	ENGINE=InnoDB DEFAULT CHARSET=utf8; 

INSERT INTO measure (name) VALUES('CUP'), ('TEASPOON'), ('TABLESPOON');

INSERT INTO Ingredient (IName,Callories) VALUES('egg',10), ('salt',10), ('sugar',10), ('chocolate',10), ('vanilla extract',10), ('flour',10);

INSERT INTO Recipe (name, description, instructions) VALUES('Boiled Egg', 'A single boiled egg', 'Add egg to cold water. Bring water to boil. Cook.');

INSERT INTO Recipe (name, description, instructions) VALUES('Chocolate Cake', 'Yummy cake', 'Add eggs, flour, chocolate to pan. Bake at 350 for 1 hour');

INSERT INTO Recipe (name, description, instructions) VALUES('New Orleans Beignets', 'Beignets only have a single rise, unlike doughnuts, which have a second rise after they are cut out. Instead, beignets go from the initial rise to cutting and frying in pretty quick succession. Once fried, they are coated, and I mean absolutely covered, in powdered sugar.', 'Add eggs, flour, chocolate to pan. Bake at 350 for 1 hour. Start the dough: In the bowl of a stand mixer fitted with the paddle attachment, combine 3 cups of flour, salt, and 2 tablespoons of sugar. Bloom the yeast: In a 4-cup measuring glass or medium bowl, combine the warm milk (it should be about 100°F), remaining tablespoon of sugar, and yeast, and allow it to sit until foamy, about 5 minutes. Make the dough: Beat the egg into the foamy milk mixture and add the mixture to the mixing bowl of flour. Mix by hand or using the paddle attachment with your stand mixer set to low or medium low, until you get a wet dough with shaggy dry bits throughout. This can take anywhere from 30 seconds to 1 1/2 minutes depending on how you’re mixing. Swap out the paddle attachment for the dough hook. Add the melted butter. Set the mixer to medium-low until the butter is incorporated, about 1 minute. Knead the dough: Turn the mixer up to medium or medium-high (depending on the size and weight of your mixer) and knead on the hook for about 6 minutes. The dough should be tacky to the touch but not so wet that you can’t handle it. Shape the dough and let it rise: Shape the dough into a ball and place it in a greased bowl. Cover with plastic and allow it to rise until doubled in size, 1-2 hours depending on the temperature of the room.');

INSERT INTO Recipe (name, description, instructions) VALUES('Classic King Crab', 'Alaskan King Crab steamed briefly and served with melted butter.', 'Melt butter: Melt the butter in a small pan and keep warm on its lowest setting. Steam the crab legs: Set a steamer tray inside a large pot and pour enough water inside to steam the crab. Remember, you are only reheating the crab, so you will only need about an inch of water, tops. Bring this to a boil before laying the crab legs on the steamer. Cover the pot and steam for 5 minutes. Make cuts into crab shells: Remove the crab legs and use kitchen shears to cut the shells. You can either totally remove the meat from the shell or just get each one started for your guests. Serve with the melted butter.');

INSERT INTO REC_INGREDIENT (RIID, Food_ID, Measure_ID, Amount) VALUES (1, 1, 4, 1);

INSERT INTO REC_INGREDIENT (RIID, Food_ID, Measure_ID, Amount)  VALUES (2, 1, 4, 3);

INSERT INTO REC_INGREDIENT (RIID, Food_ID, Measure_ID, Amount)  VALUES (2, 2, 2, 1);

INSERT INTO REC_INGREDIENT (RIID, Food_ID, Measure_ID, Amount)  VALUES (2, 3, 1, 2);

INSERT INTO REC_INGREDIENT (RIID, Food_ID, Measure_ID, Amount)  VALUES (2, 4, 1, 1);

SELECT r.name AS 'Recipe', r.instructions, ri.amount AS 'Amount', mu.name AS 'Unit of Measure', i.name AS 'Ingredient' 
FROM Recipe r 
JOIN RecipeIngredient ri on r.id = ri.recipe_id 
JOIN Ingredient i on i.id = ri.ingredient_id 
LEFT OUTER JOIN Measure mu on mu.id = measure_id;