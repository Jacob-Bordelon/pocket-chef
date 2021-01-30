Use DB_Recipe_Remote;

/* Get All Recipes*/
SELECT r.RName AS 'Recipe', 
	r.Instructions, 
	ri.Amount AS 'Amount', 
	mu.Measure AS 'Measure', 
	i.FName AS 'Ingredient' 
FROM RECIPE r 
JOIN REC_INGREDIENT ri on r.id = ri.RIID 
JOIN FOOD i on i.FID = ri.Food_ID 
LEFT OUTER JOIN MEASURE mu on mu.MID = Measure_ID;

/* Search for an specific recipe. ? = placeHolder. ? is substitued with recipe name*/
SELECT r.RName AS 'Recipe',
	r.Instructions, 
	ri.Amount AS 'Amount', 
	mu.Measure AS 'Measure', 
	i.FName AS 'Ingredient' 
FROM RECIPE r 
JOIN REC_INGREDIENT ri on r.id = ri.RIID 
JOIN FOOD i on i.FID = ri.Food_ID 
LEFT OUTER JOIN MEASURE mu on mu.MID = Measure_ID where r.RName = ?;

/* Search for ingredients of an specific recipe by NAME. ? = placeHolder. ? is substitued with recipe name */
select FName 
from ((select ri.Food_ID from RECIPE r join REC_INGREDIENT ri on r.id = ri.RIID where r.RName = ?) x 
join (select f.FID,f.FName from FOOD f) y on x.Food_ID = y.FID)

/* Search for ingredients of an specific recipe by ID. ? = placeHolder. ? is substitued with recipe name */
select FName 
from ((select ri.Food_ID from RECIPE r join REC_INGREDIENT ri on r.id = ri.RIID where r.id = ?) x 
join (select f.FID,f.FName from FOOD f) y on x.Food_ID = y.FID)

/* Number of recipes */
select COUNT(*) from RECIPE;