# billyclubgolf

### Overview

This application intended to serve my golf group, where we play a regular points game each week. 
We play a points quota game, where players earn the following points:
* Bogey = 1 pt
* Par = 2 pt
* Birdie = 4 pt
* Eagle = 8 pt

Quota is adjusted 1 point for every 2 points plus or minus, after each event, for a maximum of 3 points.
* Example:  Billy's quota is 25 and Billy made 30 points for this event.  Billy was +5, so next time Billy plays an
event his quota will be 27.

Our group plays a team game and a skins game, total ante is $10 with half going to each game.

* Team game
    * Less than 8 players - winner takes all
    * 8 or more players
      * Teams are chosen by one of the following
        * Each player draws cards to determine team
        * Application chooses teams and determine winners
      * With an odd number of players, player with worst score gets refund for team ante
* Skins game
  * Make a birdie or better on a hole.  If no other birdies on that hole, you win a skin.
  * Each skins winner collects skins pot / number of skins.

### Features

#### Security
Springboot security is implemented to require user registration and login. 2 roles control authorization; User and Administrator.

* Users - have the ability to:
  * browse event list and drill down to details
  * move themselves in and out of events
  * "Calculate Scores" once all players in an event are posted.
* Admins - have ability to:
  * create new events
  * edit existing events
  * add players to events

#### Players can post own score for an event
All event players must "Post" a points total for each event. Administrators are able to "Post" for any player.
A total score and selecting any eagles or birdies the player had is required.  When all players have posted, the Calculate button is revealed

#### Waiting List

When number of players in an event exceeds the maximum allowed with given number of tee times, a waiting list is started. 
First in, First out (FIFO) rules apply. If a player removes himself from the event, the first on waiting list will take the place.
When number of tee times change for and event, the application will manage who is removed or added to the waiting list.

#### Email notifications

Notifications are sent in 4 circumstances:

1. When a new event is created and email goes out to the entire user list.  This email contains a link directly to the event.
2. When number of tee times for an event causes waiting list to shrink or grow.
3. When the status of an event is changed to Cancel, Rain Delay, or Frost Delay, event players are notified.
4. Forgot password is clicked.  The system uses email for password reset.