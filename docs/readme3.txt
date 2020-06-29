Languages Supported:
[ français ] French  fr
[ čeština ] Cestina/Czech cs
[ Türkçe ] Turkish tr


Favourites Feature:
- For the user story: "When new data is downloaded from the City of Surrey's server,
I want to be told if there are any new inspections for my favourite restaurants."

    - When new inspections of your favourite restaurants are there after an update,
    a "View Favourites" button will appear in the MapsActivity

        - After clicking it, it will show you the list of your favourited restaurants
            with recent inspections.


Additional Information:
- To run our application, the Google Play Services may need to be updated
    - To update, start our application up and there should be a button that prompts the user to
    update their Google Play Services in the MapsActivity

- If the application does not work, it could be that the Google Maps API key that we provided
has expired or it has reached its limit
    - Follow this link: https://developers.google.com/maps/documentation/embed/get-api-key
        to generate an API key

     - Then inside our application, go to app/src/debug/res/values/google_maps_api.xml
        and paste your API key to
        <string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">
        YOUR_API_KEY
        </string>