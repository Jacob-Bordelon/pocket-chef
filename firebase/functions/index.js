const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();


exports.deleteImageOnRecipeDelete = functions.database
    .ref("recipeBook/{recipeId}")
    .onDelete((snapshot) => {
      const bucket = "pocketchef-be978.appspot.com";
      const img = snapshot.image.toJSON().toString();
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
      const bucket = "pocketchef-be978.appspot.com";
      const img = snapshot.image.toJSON().toString();
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
    .ref("bufferLayer/{recipeId}")
    .onUpdate(async (change) => {
      const data = change.after.val();
      const before = change.before;
      const after = change.after;

      if(data){
        if(!data.image_status){
            if(data.image == "" || data.image == "none") {
                data.image_status=-1;
            } 
            
        }

        if(data.image_status == 0 && data.text_status == 0){
            console.log("send to recipeBook")
        }

        if(data.image_status > 0 || data.text_status > 0){
            console.log("send to review layer")
        }


      }
      return null;
      
    });

            // data.text_status = null;
            // data.image_status = null;
            // data.status = 0;
            // return admin.database().ref("recipeBook").child(data.id).set(data)
            // .then(() => change.after.ref.set(null));
            // data.status = 1;
            // return admin.database().ref("reviewLayer").child(data.id).set(data)
            // .then(() => change.after.ref.set(null));

exports.recipeReported = functions.database
    .ref("recipeBook/{recipeId}")
    .onUpdate((change)=>{
      const data = change.after.val();
      if(data.status > 0){
        const bucket = "pocketchef-be978.appspot.com";
        const img = data.image.toString();
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

exports.recipeReviewed = functions.database
    .ref("reviewLayer/{recipeId}")
    .onUpdate((change)=>{
      const data = change.after.val();
      if(data.status == 0){
        const img = data.image.toJSON().toString();
        data.image_status = null;
        data.text_status = null;
        
        return admin.database().ref("recipeBook").child(data.id).set(data)
            .then(() => change.after.ref.set(null));
      }
    });

/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 'use strict';

 const capitalizeSentence = require('capitalize-sentence');
 const Filter = require('bad-words');
 const badWordsFilter = new Filter();
 
 // Moderates messages by lowering all uppercase messages and removing swearwords.
 exports.moderateText = functions.database.ref('bufferLayer/{recipeId}')
   .onCreate((snap) => {
   const message = snap.val();
 
   if (message) {
 
 
     const mod_dec = moderateMessage(message.description) !== message.description;
     const mod_title = moderateMessage(message.title) !== message.title;
 
     var mod_steps = false;
     console.log(mod_title);
     const steps = Object.values(message.instructions);
     for(const step of steps){
       if(moderateMessage(step) !== step){
         mod_steps = true;
         break;
       }
     } 
 
     if(mod_dec || mod_title || mod_steps){
       return snap.ref.update({
         text_status : 1
       });
     }
 
     return snap.ref.update({
       text_status : 0
     });
     
   }
 
   return null;
 });
 
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
const fs = require('fs');
const mkdirp = fs.promises.mkdir;
const {promisify} = require('util');
const exec = promisify(require('child_process').exec);
const path = require('path');
const os = require('os');

// Vision API
const vision = require('@google-cloud/vision');

// Where we'll save blurred images
const BLURRED_FOLDER = 'blurred';

/**
 * When an image is uploaded we check if it is flagged as Adult or Violence by the Cloud Vision
 * API and if it is we blur it using ImageMagick.
 */
exports.moderateImages = functions.storage.object().onFinalize(async (object) => {
  // Ignore things we've already blurred
  if (object.name.startsWith(`${BLURRED_FOLDER}/`)) {
    functions.logger.log(`Ignoring upload "${object.name}" because it was already blurred.`);
    return null;
  }
  
  // Check the image content using the Cloud Vision API.
  const visionClient = new vision.ImageAnnotatorClient();
  const data = await visionClient.safeSearchDetection(
    `gs://${object.bucket}/${object.name}`
  );
  const safeSearchResult = data[0].safeSearchAnnotation;
  functions.logger.log(`SafeSearch results on image "${object.name}"`, safeSearchResult);

  // Tune these detection likelihoods to suit your app.
  // The current settings show the most strict configuration
  // Available likelihoods are defined in https://cloud.google.com/vision/docs/reference/rest/v1/AnnotateImageResponse#likelihood
  if (
    safeSearchResult.adult !== 'VERY_UNLIKELY' ||
    safeSearchResult.spoof !== 'VERY_UNLIKELY' ||
    safeSearchResult.medical !== 'VERY_UNLIKELY' ||
    safeSearchResult.violence !== 'VERY_UNLIKELY' ||
    safeSearchResult.racy !== 'VERY_UNLIKELY'
  ) {
    functions.logger.log('Offensive image found. Blurring.');
    let name = object.name;
    name = name.replace("recipes/", "");
    name = name.replace(".jpg", "");
    admin.database().ref("bufferLayer/"+name).update({image_status:1});

    return null;
  }else{
    let name = object.name;
    name = name.replace("recipes/", "");
    name = name.replace(".jpg", "");
    admin.database().ref("bufferLayer/"+name).update({image_status:0});
  }

  return null;
});
