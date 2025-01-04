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

The app will have to obtain a token that needs to be used in order to access
the endpoints related to audio upload and querying. The description for the
token endpoint is Table 2.

A user can delete its account using the API in Table 3. When an account is
deleted, all the songs that it has uploaded are removed.

 Record and upload audio
 The user must be able to record an audio through the use use of the microphone
 of its phone, similarly to Shazam 3. Before proceeding with the upload, the
 user must be able to check the audio s/he recorded and repeat the process, if
 needed. If the user is connected through a mobile network, it must be possible
 to postpone the upload once a Wi-Fi connection is available. The user will
 receive a notification once the connection is available and the upload can be
 performed cheaply and safely. A description of the REST endpoint responsible
 for the audio upload is provided in Table 6. 
 The response received contains whether the upload has been performed
 successfully and information on the audio content (described in Table 5). The
 app must save locally this information and the user must be able to access it in
 order to see its contributions, even without a connection.

 Management of uploaded songs
 The user must be able to see the songs s/he uploaded through the app, see the
 information the backend extracted on each song and interact with each song
 by hiding it from other users or displaying it again. The information must be
 saved locally and the user must be able to inspect them at will even without
 a connection. Note that once a song is uploaded, it will not be possible to
 download them from the back-end. The app must save the audio file locally, so
 that the user can listen them again. To obtain all the songs uploaded by a user,
 the app must use the API of Table 6.
 A user can hide one of its uploaded songs by using the API in Table 7 and
 show it using the API in Table 8.
 Finally, a user can delete a song, which will be completely erased from the
 backend, by using the endpoint of Table 9.

  Map
 Table 10 describes the API to obtain all the songs uploaded by other users
 and their location. The app must display each song in a map. Note that the
 endpoint does not return any of the tags associated with a song. The app must
 obtain those information using the API of Table 11. Since many songs might


 be available in the database of the backend, the app MUST NOT fetch the
 details of every song in the database. Instead, the user must be able to click in
 a marker and see its details. An optional extension, which allows achieving a
 better grade, it to automatically fetch the details of each marker once a zoom
 level is reached. For example, if the user zooms into the maps and sees only
 5 markers, then the app automatically retrieves the information for each song
 displayed in the map. In that case, the user can filter the songs based on the
 features of Table 5. For example, only visualizing the locations for dance music.


 
