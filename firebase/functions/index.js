/* eslint-disable max-len */
const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();


exports.deleteImageOnRecipeDelete = functions.database
    .ref("recipeBook/{recipeId}")
    .onDelete((snapshot) => {
      const bucket = "pocketchef-9fb14.appspot.com";
      const img = snapshot.child("image").toJSON().toString();
      if (img != "none" && img.includes(bucket)) {
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

exports.deleteImageOnReviewDelete = functions.database
    .ref("reviewLayer/{recipeId}")
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

exports.checkFlags = functions.database
    .ref("/bufferLayer/{recipeId}")
    .onUpdate(async (change) => {
      const data = change.after.val();

      if (data) {
        console.log("Data exits");

        if (change.after.child("text_status").exists() && change.after.child("text_status").exists()) {
          console.log("both status fields found");

          if (data.text_status < 0 || data.image_status < 0) {
            console.log("Move to review layer");
            data.text_status = null;
            data.image_status = null;
            data.status = 1;
            return admin.database().ref("reviewLayer").child(data.id).set(data)
            .then(() => change.after.ref.set(null));
            
          } else {
            console.log("Move to recipeBook");
            data.text_status = null;
            data.image_status = null;
            data.status = 0;
            return admin.database().ref("recipeBook").child(data.id).set(data)
            .then(() => change.after.ref.set(null));
          }
        }
        console.log("status field missing");
        return null;
      }
      console.log("No data exits: "+data);
      return null;
    });

exports.recipeReported = functions.database
    .ref("/recipeBook/{recipeId}")
    .onUpdate((change)=>{
      const data = change.after.val();
      if(data.status > 0){
        const bucket = "pocketchef-9fb14.appspot.com";
        const img = data.child("image").toJSON().toString();
        if(!img.includes(bucket)){
          var imageRef = admin.storage().bucket(bucket).getSignedUrl(img);
          console.log(imageRef);
        }


        const filePath = img.split("/o/")[1].split("?alt=")[0].replace("%2F", "/");
        data.image_status = filePath;
        return admin.database().ref("reviewLayer").child(data.id).set(data)
            .then(() => change.after.ref.set(null));
      }
    });

exports.recipeReported = functions.database
    .ref("/reviewLayer/{recipeId}")
    .onUpdate((change)=>{
      const data = change.after.val();
      if(data.status == 0){
        const img = data.child("image").toJSON().toString();
        if()
        return admin.database().ref("recipeBook").child(data.id).set(data)
            .then(() => change.after.ref.set(null));
      }
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
