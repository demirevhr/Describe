# Describe!
## Rules

-	The game is played in teams. 
-	Each round one member per team describes a word to their teammate(s) using different methods :
    -	S - Speaking 	    (round lasts 1 minute)
    -	G - using Gestures	    (round lasts 1 minute)
    -	D - Drawing	    (round lasts 2 minutes)
    -	A - word Associations  (round lasts 2 minutes)
        -	the describing player can say only 3 words, somehow connected to the drawn word
-	A team is considered *one player* (one device per team). Each round the team member who will be describing to their teammates has the device.
-	Each player sees a 10 x 11 board and has a pawn that represents their place on the board.
- 	Each field on the board is marked with a letter, stating the method the player should use to describe the drawn word when on this field. (reference above)

- 	How a round goes :
    -	A player draws a word for 3, 4 or 5 points (consider points as difficulty).
    -           After drawing a word, the player has time to prepare. Only when he says, does the clock start ticking.
    -	In the given timespan, the player should describe the drawn word to their teammates using the method stated on the field they are currently occupying.
    -	In the first round each player decides which method to use to describe their word.
    -           If a team guesses the word correctly, they move forward 3, 4 or 5 fields, depending on the points they chose for the word.

-	The player who gets to the end first is the winner.


## Game Server

The Game Server has the following functionality :

-	Create game
-	List all games : shown for each game is the game state (pending or in progress), and the number of players in it
-	Join game
-	Join a random game

In order to play, a server should be running. 
```bash
$ java GameServer <hostname> <port>
```
If hostname and port are not specified, the server will run on localhost:4444

## Game Client

When running the application the player must specify their username and the hostname and port of the server they want to connect to.
```bash
$ java GameClient.java <username> <hostname> <port>

Welcome to Describe!
Here are the available commands :
create-game <game-name>	 - create a game with name "game-name"
join-game [<game-name>] 	- join a game with a given name or join a random game
list-games              	- get a list of pending games
disconnect             	 	- disconnect from server
```

The client can do the following actions :
- 	Create a game

```bash
$ java GameClient player1 <hostname> <port>
create-game my-game
|||=================================|||
 |||===============================|||
  |||====== D E S C R I B E ======|||
   |||===========================|||
    |||=========================|||
=======================================
============    my-game     ===========
=======================================
||1.| player1                        ||
```
-	Join a game

```bash
$ java GameClient player2 <hostname> <port>
list-games
| NAME | CREATOR | STATUS | PLAYERS |
|----------+---------+-------------+---------|
| my-game  | player1 | pending     |   1/4   |
| my-game-2| player6 | in progress |   3/4   |

join-game my-game

|||=================================|||
 |||===============================|||
  |||====== D E S C R I B E ======|||
   |||===========================|||
    |||=========================|||
=======================================
============    my-game     ===========
=======================================
||1.| player1                        ||
||2.| player2                        ||
Type "start" to start the game
```

-	When the game is running

    - Not your turn
    
```bash
    
 ___ ___ ___ ___ ___ ___ ___ ___ ___ ___ ___
|D  |A $|S  |   |D  |A  |S  |   |D  |A  |S  |
|___|___|___|___|___|___|___|___|___|___|___|
|G  |   |G  |   |G  |   |G  |   |G  |   |G  |
|___|___|___|___|___|___|___|___|___|___|___|
|S  |   |D  |   |S  |   |D  |   |S  |   |D  |
|___|___|___|___|___|___|___|___|___|___|___|
|A  |   |A  |   |A  |   |A  |   |A  |   |A  |
|___|___|___|___|___|___|___|___|___|___|___|
|D *|   |S  |   |D  |   |S  |   |D  |   |S  |
|___|___|___|___|___|___|___|___|___|___|___|
|G  |   |G  |   |G  |   |G  |   |G  |   |G  |
|___|___|___|___|___|___|___|___|___|___|___|
|S  |   |D  |   |S  |   |D  |   |S  |   |   |
|___|___|___|___|___|___|___|___|___|___|___|
|   |   |A  |   |A  |   |A  |   |A  |   |   |
|___|___|___|___|___|___|___|___|___|___|___|
|GO |   |S  |G  |D  |   |S  |G  |D  |   |FIN|
|___|___|___|___|___|___|___|___|___|___|___|


```


    Your turn
    

```bash

 ___ ___ ___ ___ ___ ___ ___ ___ ___ ___ ___
|D *|A $|S  |   |D  |A  |S  |   |D  |A  |S  |
|___|___|___|___|___|___|___|___|___|___|___|
|G  |   |G  |   |G  |   |G  |   |G  |   |G  |
|___|___|___|___|___|___|___|___|___|___|___|
|S  |   |D  |   |S  |   |D  |   |S  |   |D  |
|___|___|___|___|___|___|___|___|___|___|___|
|A  |   |A  |   |A  |   |A  |   |A  |   |A  |
|___|___|___|___|___|___|___|___|___|___|___|
|D  |   |S  |   |D  |   |S  |   |D  |   |S  |
|___|___|___|___|___|___|___|___|___|___|___|
|G  |   |G  |   |G  |   |G  |   |G  |   |G  |
|___|___|___|___|___|___|___|___|___|___|___|
|S  |   |D  |   |S  |   |D  |   |S  |   |   |
|___|___|___|___|___|___|___|___|___|___|___|
|   |   |A  |   |A  |   |A  |   |A  |   |   |
|___|___|___|___|___|___|___|___|___|___|___|
|GO |   |S  |G  |D  |   |S  |G  |D  |   |FIN|
|___|___|___|___|___|___|___|___|___|___|___|

It's your turn, player1! ($)
[type \"p N\" (where N is 3, 4 or 5 points)]
Choose points :

$ p 5

You chose a wrod for 5 points.
You got the word : superstition

Describe it by word associations
Type "go" when you are ready to Describe!

$ go

Your word is : superstition
Describe it by word associations
...

Time remaining 27 seconds.

...

Enter "yes" if you guessed correctly and "no" if you didn't

$ yes
...
...
-------------GAME OVER-------------
       The winner is player1!
```