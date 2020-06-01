# GreenLight-Messenger-App
# #30DaysOfKotlin

Developed my first android kotlin app ie. GreenLight in accordance with #30DaysOfKotlin. 

GreenLight is a messenger app with the features of sending text and image messages, deleting the sent messages etc along with the functionality of showing online status of the corresponding user, push notification on any message received and users can also update their profile picture and cover picture and can link their facebook ID, instagram ID and also their website so that friends can identify them inside the app as they can find each other by ‘find friends’ feature. For sending and receiving messages, users have to register or login first.

GreenLight is made with the help of Firebase for storing and maintaining the uploaded images by users and database on login details, chats etc.

For the working of the project you must add your own google-services.json file from your firebase project as I removed mine from it and also have to replace the corresponding authorization key within this project’s APIservice interface file.


Implementation Guide:-
1 - Project

  - Open the Project in your android studio.
  - IMPORTANT: Change the Package Name. (https://stackoverflow.com/questions/16804093/android-studio-rename-package).

2 - Firebase Panel

  - Create Firebase Project (https://console.firebase.google.com/).
  - Import the file google-service.json into your project.
  - Connect to firebase console authentication and database from your IDE.
  - in firebase Storage Rules, change the value of "allow read, write:" from "if request.auth != null" to "if true;".
  - For sending notification, paste your Firebase project key into your project APIService.java.
  - When you change database settings, you likely will need to uninstall and reinstall apps to avoid app crashes due to app caches.
