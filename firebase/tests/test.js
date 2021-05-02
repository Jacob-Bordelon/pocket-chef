const assert = require('assert');
const firebase = require("@firebase/testing");
const MY_PROJECT_ID = "pocketchef-9fb14";
const my_database = "recipes"
firebase.loadDatabaseRules({
    databaseName:my_database,
    rules: "{'rules': {'.read': false, '.write': false}}"
});


describe("Pocket Chef app", () => {

    it("assertFails", async () => {

        const app = firebase.initializeTestApp({
            projectId:MY_PROJECT_ID,
            databaseName:my_database
        });
        const db = app.database().ref("recipeBook").child("boiled egg");
        console.log(db.child("author").toString());
        await firebase.assertFails(db.once('value'));
    });

});