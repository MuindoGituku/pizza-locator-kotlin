# pizza-locator-kotlin

Brought to you by:
  Emre Deniz
  Muindo Gituku

This is a simple application built in Kotlin language with the aim of helping a ser locate various restaurants around different cities in the Ontario, Canada.

The app utilizes the Google Maps API to fetch the coordinates of a town using the geoCoder and parsing the name of the town and limiting the results to just the first one. 
From the result we fetch the coordinates (longitude and latitude) which are the passed to the map fragment activity using intent along with the name of the city.

In the second activity, we use Google Places API to filter out locations, of type "restaurant" and adding a blue marker on each location. The marker, on tap, shows the name of
the restaurant as a title, and its vicinity (address to location e.g 241 Markham Road) as a snippet.

![mobile_mockup_1](https://github.com/MuindoGituku/pizza-locator-kotlin/assets/66807339/f48fc7df-b7f0-472e-88c7-0a9bd283f234)
