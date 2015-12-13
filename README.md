# Android Developer NanoDegree

## Project 1 - Popular Movies Stage 1 & 2

This is an Android Application developed by me (Saurabh Agrawal), as an 
academic requirement for the Udacity's Android Developer NanoDegree Program.

## What is this App about?

- This app displays a list of currently running popular movies in a grid view. 
- The list can be changed from the "Overflow Menu" to display highest rated movies too.
- Opon clicking a movie poster, the app navigates to the details screen where the movie details are displayed.
- Any movie can be favorited in the app, which then is stored into a local database.
- All the movie details which are favorited can be accessed by other apps using the shared content URI.
- The app also displays a list of movies which are favorited by selecting the same in the overflow menu.
- The user can share the first trailer and can also view all the trailers as desired.

The project uses <a href="https://github.com/holgerbrandl/themoviedbapi">TheMovieDb API</a>, a
a java-wrapper around the JSON API provided by TMdB, which is an open database for movie and film content.

## Guide to run the App

The App uses themoviedb.org API for getting the movie data. It requires an API key to 
query for the data.
Make a new class called <b> Keys.java </b> in the package "src/.../constants" and just 
enter with your Key as a public string. Name it as <b> API_KEY </b> and you're good to go.

## References

The icons/images used in the App (stored in src/res/drawable folder) are taken from:
http://www.iconarchive.com/show/whistlepuff-icons-by-firstfear/pictures-icon.html
http://www.iconarchive.com/show/origami-colored-pencil-icons-by-double-j-design/red-cross-icon.html
http://www.iconarchive.com/show/oxygen-icons-by-oxygen-icons.org/Actions-rating-icon.html
http://www.iconarchive.com/show/oxygen-icons-by-oxygen-icons.org/Actions-go-jump-today-icon.html
http://www.iconarchive.com/show/valentine-icons-by-custom-icon-design/heart-icon.html
http://www.iconarchive.com/show/3d-cartoon-vol3-icons-by-hopstarter/Windows-Movie-Maker-icon.html
http://www.iconarchive.com/show/outline-icons-by-iconsmind/Heart-2-icon.html
https://www.youtube.com/yt/brand/downloads.html

All of the images licensed free to use for personal projects.
