# Pavlov Stats
### A Pavlov VR server stats API

A simple API that scrapes user statistics from a 
Pavlov server using RCON commands, then captures any 
changes in a Redis database. If you don't have a
Redis cluster handy, this comes with an embedded 
Redis if you point to localhost on any port that 
isn't the default.

This will store basic player stats, like kills, deaths,
assists and last played timestamp, as well as the 
summary of each match.

It also provides 2 APIs:

 - /scoreboard : returns the current state of the 
game in play on the server, including a ranked 
scoreboard for each team
 - /leaderboard : returns a list of all users 
in the database, sorted by kills

An early WIP, and needs a proper UI since most folks
aren't amenable to parsing JSON with their eyeballs.

###Configuration 
TODO
