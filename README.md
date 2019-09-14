Submitted to PennApps XX 

## Inspiration
As a team we were determined to tackle an environmental or health related issue. When collecting thoughts on public transportation we realized that there exists a variety of awareness campaigns, company sponsored employee benefits and student discounts to incentivize the general public. What seemed to be missing was the ability for every single public transport user to access the benefits and more importantly customize them.

Several studies time and again have proven that the rise in carbon emissions is heavily influenced by large cities and its inhabitants. According to the Scientific American, residents of just over a 100 cities account for close to 20% of humanity's carbon footprint in terms of CO~2~ emissions. These include large metropolises such as New York, Chicago and Los Angeles in the United States, Seoul and Pusan in South Korea, Tokyo and Nagoya in Japan, Shanghai and Guanghou in China and Taipei in Taiwan. A solution to help ease and lessen the carbon burden would be to reduce the usage of motor vehicles and incentivize the use of public transport. In the United States, 29% of carbon emissions come about as a result of transportation as reported by the Environmental Protection Agency. Similarly, in cities such as Shanghai, Seoul and Taipei, which have state-of-the-art public transport that reach far and wide, people still opt to use personal vehicles.

Inspired by our own interests, identified market gap and scientific rational we started to configure a service that would have the capacity to be educational and habit changing through the distraction of a reward system.

## What it does
SeeC is there for you to "See Carbon(CO~2~)" and motivate you to commute using greener alternatives. How? In the first place, our application tracks your usage of public transportation and proportionally rewards you with Carbon Credit - a virtual in-app currency. Similarly, the users have the opportunity to convert their walking/running miles. The earned Carbon Credits in return can be used to purchase vouchers from various partner vendors.

In that way, using metro for a week could easily earn you a cheaper ticket for the next time or a delicious free smoothie from your favorite juice bar.

Most importantly, in the background of you collecting your next benefit, you have also managed to be eco-friendly and active.

## How does it work
The current version is an Android application that sports a variety of features. The android app was built using Android Studio which is primarily Java-based. The primary feature is logging your journeys on some mode of public transport. For instance, if you were to go on a journey from Metro Station A to Metro Station B, you would scan a QR code at A to start logging your journey, and scan again at B to stop logging your journey. The distance is then calculated and then the appropriate amount of Carbon Credits is added to the user's wallet on the app. The QR codes are geotagged, and all calculations take the user's live location into account to prevent misuse of the app. The virtual wallet and all transactions (adding and subtracting credits from a user's wallet) is handled using Capital One's Nessie API. The location data and other GPS/Geotagging related information was handled using a Google Maps API. In addition to the databases provided by Capital One, we also used a Real Time Database provided by Google's Firebase to store other details that were not part of the Nessie API. Communication between these different databases, as well as between the different APIs and calculations were handled using a backend built with Python's Flask framework which was hosted on Google Cloud Services.

## Challenges we ran into
Our biggest challenges were definitely related to technical and logistical questions. Out of those challenges some merely required a little more focus and learning, whereas other required us to reroute our plans to more feasible tracks. Here is the list of our most memorable brain teasers:

Syncing the Firebase records with the Capital One records proved to be quite challenging because of the various IDs involved with different records
Working with the live locations in the android app was a headache after time after time being geographically placed onto a completely different continent
Our attempt to use the Google Maps history and trails to infer walking data required a lot of effort and time without ever delivering us the information we were actually after
Configuring the communication between the database and the android interface.
Hosting the API was not the most straightforward procedure. After running through many options, we opted to use Google Cloud as our host.
Accomplishments that we're proud of
Of course in the environment of an intense quick-speed hacking every finished task or a fixed bug feels like a true victory, however considering the product that we managed to create under those time-constraints, we feel extremely proud of managing to implement a legitimately working cohesive application that is both thought through on the user interface side and well-connected and efficient on the backend side.

## What we learned
We definitely polished our skills when it comes to giving as quick and thorough of a crash course to a teammate as humanely possible.

## What's next for SeeC
Although we accomplished more than we could have imagined, there is definitely quite a few details that could and should be refined. In addition, after seeing how easily such a platform could come together we would be happy to see such a project become reality whether through us or someone else.
