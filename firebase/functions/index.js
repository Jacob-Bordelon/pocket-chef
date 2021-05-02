const functions = require("firebase-functions");
const admin = require("firebase-admin");
const vision = require('@google-cloud/vision');
const capitalizeSentence = require('capitalize-sentence');
const Filter = require('bad-words');
const badWordsFilter = new Filter();

admin.initializeApp();

exports.checkNewRecipe = functions.database
    .ref("bufferLayer/{recipeID}")
    .onCreate(async (snap) => {
      const data = snap.val();
      
      // Check image for innaproriate content
      var image_status = 0;
      if(data.image != "none"){
        image_status = await checkImage(data.image);
      }

      // Check text for innaproriate language
      const text_status = checkText(data);

      if(text_status==0 && image_status==0){
        // move to recipeBook
        data.status = 0;
        return admin.database().ref("recipeBook").child(data.id).set(data)
        .then(() => snap.ref.set(null));
      }else{
        // move to reviewLayer
        var code = 0;
        if(text_status!=0){
          code=code+1;
        }

        if(image_status!=0){
          code=code+2;
        }

        data.status = code;
        return admin.database().ref("reviewLayer").child(data.id).set(data)
        .then(() => snap.ref.set(null));
      }
    });


exports.reportRecipe = functions.database
    .ref("recipeBook/{recipeId}")
    .onUpdate(async (change)=>{
      const data = change.after.val();
      if(data.status !== 0){
        await change.after.ref.remove();
        return admin.database().ref("reviewLayer").child(data.id).set(data);
      }
      return null;
    });

exports.reviewedRecipe = functions.database
    .ref("reviewLayer/{recipeId}")
    .onUpdate(async (change)=>{
      const data = change.after.val();
      switch (data.status) {
        case 0:
          await change.after.ref.remove();
          return admin.database().ref("recipeBook").child(data.id).set(data);
        case -1:
          return change.after.ref.remove();
      }
      return null;
    });

exports.deleteImageOnDelete = functions.database
    .ref("reviewLayer/{recipeId}")
    .onDelete(async (snapshot)=>{
      const bucket = "pocketchef-be978-recipes";
      const data = snapshot.val();
      const img = data.image.toString();
      if (img != "none" && img.includes(bucket) && data.status>0) {
        const filePath = img.split("/o/")[1].split("?alt=")[0].replace("%2F", "/");
        console.log(filePath);
        const fileRef = admin.storage().bucket(bucket).file(filePath);

        return fileRef.delete().then(function() {
          console.log("File Deleted");
        }).catch(function(error) {
          console.log("File was not deleted: "+error);
        });
      }

      return null;
    });


// Moderates messages by lowering all uppercase messages and removing swearwords.
function checkText(message){
    const mod_dec = moderateMessage(message.description) !== message.description;
    const mod_title = moderateMessage(message.title) !== message.title;

    var mod_steps = false;
    const steps = Object.values(message.instructions);
    for(const step of steps){
      if(moderateMessage(step) !== step){
        mod_steps = true;
        break;
      }
    } 

    if(mod_dec || mod_title || mod_steps){
      return 1;
    }

    return 0;
};

// Moderates the given message if appropriate.
function moderateMessage(message) {
  // Re-capitalize if the user is Shouting.
  if (isShouting(message)) {
    functions.logger.log('User is shouting. Fixing sentence case...');
    message = stopShouting(message);
  }

  // Moderate if the user uses SwearWords.
  if (containsSwearwords(message)) {
    functions.logger.log('User is swearing. moderating...');
    message = moderateSwearwords(message);
  }

  return message;
}

// Returns true if the string contains swearwords.
function containsSwearwords(message) {
  return message !== badWordsFilter.clean(message);
}

// Hide all swearwords. e.g: Crap => ****.
function moderateSwearwords(message) {
  return badWordsFilter.clean(message);
}

// Detect if the current message is shouting. i.e. there are too many Uppercase
// characters or exclamation points.
function isShouting(message) {
  return message.replace(/[^A-Z]/g, '').length > message.length / 2 || message.replace(/[^!]/g, '').length >= 3;
}

// Correctly capitalize the string as a sentence (e.g. uppercase after dots)
// and remove exclamation points.
function stopShouting(message) {
  return capitalizeSentence(message.toLowerCase()).replace(/!+/g, '.');
}


/// Moderate Images 
// Node.js core modules


// Vision API
async function checkImage(filePath){

 const visionClient = new vision.ImageAnnotatorClient();

 try {
     const data = await visionClient.safeSearchDetection(filePath);
     const safeSearchResult = data[0].safeSearchAnnotation;
     functions.logger.log(`Image Results Are: ${safeSearchResult}`);
     if(safeSearchResult == null){
       return 0;
     }

     if (safeSearchResult.adult !== 'VERY_UNLIKELY' || 
         safeSearchResult.spoof !== 'VERY_UNLIKELY' || 
         safeSearchResult.medical !== 'VERY_UNLIKELY' || 
         safeSearchResult.violence !== 'VERY_UNLIKELY' ||
         safeSearchResult.racy !== 'VERY_UNLIKELY') {
       return 1;
     }

     return 0;
 } catch (e) {
   console.error(e);
 }

}


