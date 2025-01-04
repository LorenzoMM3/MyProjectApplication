The Project: “Your city is listening to”

Overview
In the following project, the student is required to implement an interactive application that crowd-sources the music that is played throughout its city. 
The app will allow its users to authenticate and record audio files from the smartphone’s microphone. Each audio will be geo-tagged and sent to a back
end service, NOT developed by the student, that will answer back with details on the audio that has just being sent. The details received on an audio include
information such as the music genre, the instruments detected, and so on. A user will be able to see the audios uploaded by other users in the map of its city
and interact with it. Theendpoint of the back-end is described throughout the document, alongside examples of requests using cURL 2. Each endpoint that might answer with code
400 requires authentication, which needs to be supplied through the header Authentication using Bearer <token>. Description on how to obtain the
token are in the section below. The app MUST implement the features listed below to be sufficient. Adding features is strongly encouraged and has good
repercussions in the evaluation. A project that follows the specifications to its minimum but has no extra feature cannot reach high grades.

Authentication
The app has to interact with the provided back-end to allow its user to sign-up.
The user must be able to log-out from the system and log-in again by using the
password created during the sign-up phase. A description of the REST endpoint
responsible for the signup phase is provided below in Table 1.

