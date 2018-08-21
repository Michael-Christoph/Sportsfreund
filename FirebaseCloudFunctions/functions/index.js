const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();


// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

//begin addMessage-Function
// Take the text parameter passed to this HTTP endpoint and insert it into the
// Realtime Database under the path /messages/:pushId/original
exports.addMessage = functions.https.onRequest((req, res) => {
  // Grab the text parameter.
  const original = req.query.text;
  // Push the new message into the Realtime Database using the Firebase Admin SDK.
  return admin.database().ref('/messages').push({original: original}).then((snapshot) => {
    // Redirect with 303 SEE OTHER to the URL of the pushed object in the Firebase console.
    return res.redirect(303, snapshot.ref.toString());
  });
});
//end addMessage-Function

// Listens for new messages added to /messages/:pushId/original and creates an
// uppercase version of the message to /messages/:pushId/uppercase
exports.makeUppercase = functions.database.ref('/messages/{pushId}/original')
    .onCreate((snapshot, context) => {
      // Grab the current value of what was written to the Realtime Database.
      const original = snapshot.val();
      console.log('Uppercasing', context.params.pushId, original);
      const uppercase = original.toUpperCase();
      // You must return a Promise when performing asynchronous tasks inside a Functions such as
      // writing to the Firebase Realtime Database.
      // Setting an "uppercase" sibling in the Realtime Database returns a Promise.
      return snapshot.ref.parent.child('uppercase').set(uppercase);
    });
    
//    https://mobikul.com/sending-notification-firebase-cloud-functions/

/*
exports.sendNotification = functions.database.ref('/games/{userId}/{pushId}').onUpdate(event => {
    const snapshot = event.data;
    const userId = event.params.userId;
    if (snapshot.previous.val()) {
        return;
      }
    if (snapshot.val().name != "ADMIN") {
        return;
    }
    const text = snapshot.val().text;
    const payload = {
        notification: {
          title: `New message by ${snapshot.val().name}`,
          body: text
            ? text.length <= 100 ? text : text.substring(0, 97) + "..."
            : ""
        }
    };
    return admin
        .database()
        .ref(`data/${userId}/customerData`)
        .once('value')
        .then(data => {
          console.log('inside', data.val().notificationKey);
          if (data.val().notificationKey) {
            return admin.messaging().sendToDevice(data.val().notificationKey, payload);
          }
    });
});
*/

//https://firebase.google.com/docs/functions/database-events



// Listens for new participants added to /games/:pushId/participants
exports.reactOnUpdate = functions.database.ref('/games/{pushId}/participants')
    .onUpdate((change,context) => {
      //declared here in order to make it available everywhere
      var payload;
      var participantsToBeInformed;
      const participantsBefore = change.before.val();
      const participantsAfter = change.after.val();
      console.log('This is how change.before.val() looks: ',participantsBefore);
      console.log('change.before.val() is of type: ',typeof participantsBefore);
      console.log('This is how change.after.val() looks: ',participantsAfter);
      console.log('change.after.val() is of type: ',typeof participantsAfter);
      
      /*
      var participantsBefore = Object.keys(change.before).map(function(key){
        return change.before[key];
      })
      
      var participantsAfter = Object.keys(change.after).map(function(key){
        return change.after[key];
      })
      */
      
      for (entry in participantsAfter){
        console.log('entry: ',entry);
        console.log('value at entry: ',participantsAfter[entry]);
        console.log('type of entry: ',typeof entry);
      }
      
      const getGameRefPromise = admin.database().ref('games').child(context.params.pushId).once('value');
      return getGameRefPromise.then(function(snapshot){
        //const gameKey = snapshot.val();
        const gameKey = context.params.pushId;
        console.log('grabbed gameKey',gameKey);
        const updatedGameName = snapshot.child('gameName').val();

        var numParticipantsBefore = 0;
        participantsBefore.forEach(function(entry){
          if (entry != null){
            numParticipantsBefore++;
          }
        });
        var numParticipantsAfter = 0;
        participantsAfter.forEach(function(entry){
          if (entry != null){
            numParticipantsAfter++;
          }
        });
        console.log('Anzahl participantsBefore:',numParticipantsBefore);
        console.log('Anzahl participantsAfter:',numParticipantsAfter);
        
        if (numParticipantsBefore > numParticipantsAfter){
          payload = {
            data: {
              typeOfChange: 'participant left',
              gameName: updatedGameName,
              gameKey: gameKey
            }
          };
          participantsToBeInformed = participantsAfter;
        } else {
          payload = {
            data: {
              typeOfChange: 'new participant',
              gameName: updatedGameName,
              gameKey: gameKey
            }
          };
          participantsToBeInformed = participantsBefore
        }
        
        
        const getUserTokenPromise = admin.database().ref('/users').once('value');
        return getUserTokenPromise;
      }).then(function(snapshot){
        tokenArray = [];

        tokensAtUserNode = [];
        
        snapshot.forEach(function(childSnapshot){
          var key = childSnapshot.key;
          var value = childSnapshot.val();
          
          function myContainsFunction(array,obj){
            var i = array.length;
            while (i--){
              if (array[i] === obj){
                return true;
              }
            }
            return false;
          }
          if (myContainsFunction(participantsToBeInformed,key)){
            tokenArray.push(value);
          }
          tokensAtUserNode.push(value);
          console.log('tokensAtUserNode: ',tokensAtUserNode);
        })
        
        /*
        allKeysArray = [];
        allTokensArray = [];
        snapshot.forEach(function(childSnapshot){
          var key = childSnapshot.key;
          console.log("key: ",key);
          allKeysArray.push(key);
          var value = childSnapshot.val();
          allTokensArray.push(value);
        });
        participantsAfter.forEach(function(entry){
          console.log('allKeysArray.includes(entry): ',allKeysArray.includes(entry));
          if (allKeysArray.includes(entry)){
            console.log('snapshot[entry]: ',snapshot[entry]);
            tokenArray.push(snapshot[entry]);
          }
        });
        
        //funktionierende Version:
        snapshot.forEach(function(childSnapshot){
          var key = childSnapshot.key;
          console.log("key: ",key);
          var value = childSnapshot.val();
          tokenArray.push(value);
            
        });
        */
        
        return tokenArray;

        /*
        console.log('snapshot.val',typeof snapshot.val());
        var keyValArray = snapshot.val();
        console.log("keyValArray, Typ: ", typeof keyValArray);
        keyValArray = typeof keyValArray == 'array' ? keyValArray : [keyValArray];
        tokenArray = [];
        keyValArray.forEach(function(entry){
          console.log("entry",typeof entry);
          tokenArray.push(entry)
        });
        return snapshot.val();
        */
        
      }).then(function(tokenArray){
  //token = 'eEsEoa0zP6Q:APA91bFWMZOL_3cj7-ohmJPvn2hsUUoMEReNyqw2MPSje36D2yXVglztoWzoEHeOlzu3RwWaAjE-M85NjJreTdxcbIYreo9Py2BF4hvjncVUhoBiJeo6zSQ92SkIBDW270uFj4tO3cY8YVw-mkSHbC92EvwkaK3Vsg';
  console.log('tokenArray geholt: ' ,tokenArray);
  return admin.messaging().sendToDevice(tokenArray,payload);
  // You must return a Promise when performing asynchronous tasks inside a Functions such as
  // writing to the Firebase Realtime Database.
  // Setting an "uppercase" sibling in the Realtime Database returns a Promise.
  //return change.after.ref.parent.parent.parent.child('test').set(participantsAfter);
      });
      
   
    /*
    }).then((response) => {
        //teilweise auskommentiert, da noch unvollst�ndig; vgl. https://github.com/firebase/functions-samples/blob/master/fcm-notifications/functions/index.js#L77
        console.log('entered then clause');
        // For each message check if there was an error.
        const tokensToRemove = [];
        response.results.forEach((result, index) => {
          const error = result.error;
          
          if (error) {
               console.error('Failure sending notification to certain tokens');
            //console.error('Failure sending notification to', tokens[index], error);
            
            // Cleanup the tokens who are not registered anymore.
            if (error.code === 'messaging/invalid-registration-token' ||
                error.code === 'messaging/registration-token-not-registered') {
              tokensToRemove.push(tokensSnapshot.ref.child(tokens[index]).remove());
            }
          
          }
          
        });
        //return Promise.all(tokensToRemove);
      */
    
    });
    





    
    