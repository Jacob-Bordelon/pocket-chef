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

const functions = require('firebase-functions');
const capitalizeSentence = require('capitalize-sentence');
const Filter = require('bad-words');
const badWordsFilter = new Filter();

// Moderates messages by lowering all uppercase messages and removing swearwords.
exports.descriptionModerator = functions.database.ref('/recipeBook/{recipeId}/')
  .onWrite((change) => {
  const message = change.after.val();

  if (message && !message.sanitized) {

    // Run moderation checks on on the message and moderate if needed.
    const moderatedDescription = moderateMessage(message.description);
    
    return change.after.ref.update({
      description: moderatedDescription,
      sanitized: true,
      moderated: message.text !== moderatedDescription,
    });
  }
  console.log("not checked"+message);
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
