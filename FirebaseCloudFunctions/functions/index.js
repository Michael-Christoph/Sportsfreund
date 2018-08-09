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


// Listens for new messages added to /games/:pushId/participants and creates an
// uppercase version of the message to /games/:pushId/uppercase
exports.reactOnUpdate = functions.database.ref('/games/{pushId}/participants')
//parameter war vorher: (snapshot,context)
    .onUpdate((change,context) => {
      
      const participantsBefore = change.before.val();
      const participantsAfter = change.after.val();
      console.log('grabbed participants', context.params.pushId, participantsAfter);
      
      //game = change.after.ref.parent.child('gameName').val()
      //if (participantsBefore.length > participantsAfter.length){
          const payload = {
            notification: {
            title: 'Neuer Teilnehmer in einem Spiel!',
            body: `Bist du weiterhin interessiert?`
            }
          };
     // } else {
     //     const payload = {
        //    notification: {
           //     title: 'Ein Teilnehmer hat sich aus einem Spiel ausgetragen.',
             //   body: `Bist du weiterhin interessiert?`
           // }
        //  };
   // }
    token = 'e9EDLv8XAJ0:APA91bEtu8agRCKAehoQSTh5OJNOD_YDMCHhMcz8fyhLGXVv4D5xpthgYCVCNRdxpoIXexxcxyBTAHMk3qzgKmz1Qv4LVkaY5nKQUaeURU2nW3LRZvteTpxmN3eh8uXJCJq5p1eJMl-qgiq04qptskYkvGtHxbGiLg'
    console.log('token erstellt: ' ,token);
    return admin.messaging().sendToDevice(token,payload);
      // You must return a Promise when performing asynchronous tasks inside a Functions such as
      // writing to the Firebase Realtime Database.
      // Setting an "uppercase" sibling in the Realtime Database returns a Promise.
      //return change.after.ref.parent.parent.parent.child('test').set(participantsAfter);
    }).then((response) => {
        //teilweise auskommentiert, da noch unvollständig; vgl. https://github.com/firebase/functions-samples/blob/master/fcm-notifications/functions/index.js#L77
        console.log('entered then clause');
        // For each message check if there was an error.
        const tokensToRemove = [];
        response.results.forEach((result, index) => {
          const error = result.error;
          
          if (error) {
               console.error('Failure sending notification to certain tokens');
            //console.error('Failure sending notification to', tokens[index], error);
            /*
            // Cleanup the tokens who are not registered anymore.
            if (error.code === 'messaging/invalid-registration-token' ||
                error.code === 'messaging/registration-token-not-registered') {
              tokensToRemove.push(tokensSnapshot.ref.child(tokens[index]).remove());
            }
          }
          */
        });
        //return Promise.all(tokensToRemove);
        
      });
});
    





    
    