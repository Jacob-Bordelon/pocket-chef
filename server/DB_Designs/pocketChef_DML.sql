Use DB_Recipe_Remote;

SELECT r.RName AS 'Recipe', 
	r.Instructions, 
	ri.Amount AS 'Amount', 
	mu.Measure AS 'Unit of Measure', 
	i.FName AS 'Ingredient' 
FROM RECIPE r 
JOIN REC_INGREDIENT ri on r.id = ri.RIID 
JOIN FOOD i on i.FID = ri.Food_ID 
LEFT OUTER JOIN MEASURE mu on mu.MID = Measure_ID;