/* eslint-disable max-len */
const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.checkForAllFields = functions.database
    .ref("recipeBook/{recipeId}")
    .onCreate((snapshot, context)=>{
      functions.logger.debug("default status set");
      return snapshot.ref.update({status: 0});
    });

exports.deleteImageOnRecipeDelete = functions.database
    .ref("recipeBook/{recipeId}")
    .onDelete((snapshot) => {
      const bucket = "pocketchef-9fb14.appspot.com";
      const img = snapshot.child("image").toJSON().toString();
      if (img != "none") {
        const filePath = img.split("/o/")[1].split("?alt=")[0].replace("%2F", "/");
        console.log(filePath);
        const fileRef = admin.storage().bucket(bucket).file(filePath);

        return fileRef.delete().then(function() {
        // File deleted successfully
          console.log("File Deleted");
        }).catch(function(error) {
        // Some Error occurred
          console.log("File was not deleted"+error);
        });
      }

      return null;
    });

exports.testFunction = functions.database
    .ref("recipeBook/{recipeId}")
    .onCreate((snapshot)=>{
      functions.logger.debug("test function");
      return snapshot.ref.child("Blah").set({
        name: "Blah",
      });
    });


exports.formatFood = functions.database
    .ref("/food/{foodItem}/foodNutrients/")
    .onWrite((change )=> {
      const list = ["Total fat (NLEA)", "Cholesterol", "Protein", "Carbohydrate, by difference", "Sodium, Na", "Energy (Atwater Specific Factors)"];
      return change.after.forEach((child)=>{
        if (list.indexOf(child.key) <= -1) {
          change.after.ref.child(child.key).set(null);
        }
      });
    });
