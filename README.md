# Version 2?
I am making a new version which has better code and will be on spigot check it out on my github, its currently in developmental stages (not released yet)

https://github.com/DemonDxv/Open-Source-Anticheat-V2

# Disabler / Bypasses

if you run the latest commit of this anticheat (not 1.1.4 that is released) you can disable the anticheat.

Cancel the transaction ID's between 2469 and 3000 for 100 ticks (5 seconds) and then move between deltaY < 0 && deltaY > -1.493E-13 and the anticheat checks will all disable when doing this.

If you run version 1.1.4, you can spoof ground fly (collision/always on ground) if you take velocity/fall damage it will allow you to fly in the air for about 5 - 10 seconds before lagging back
(delay transactions)

# Open Source Anticheat
A free anticheat for the community that is open source.

***Note: this project is discontinued (Expect some possible Bugs and Bypasses)***
 
Feel free to report any issues in the "Issues" tab
 
****Main Developer(s): demon****

****Main Contributor(s): Rhys****

**Spigot Page: https://www.spigotmc.org/resources/anticheat.93504/**

If your planning on forking / taking code to resell make sure to leave credits.


*Supported Game Verison: 1.7.x - 1.8.x*

**(Highly recommended to use 1.8.x or 1.7.x as all version above have not been tested)**

*Tested Game Versions: 1.7.x - 1.8.x*


**Permissions:**

* anticheat.command - use "/ac" command(s)

* anticheat.alerts - see alerts permission

* anticheat.bypass - bypass the anticheat with a permission

**Commands:**

* /ac - displays anticheat commands and information

* /ac alerts - toggles on and off alerts

* /ac check (check&checktype) - toggles on and off detections

* /ac forceban (player) - bans the player using the anticheat

* /ac logs (player) - shows logs from MongoDB database collection

* /ac ping (player) - shows the players ping

* /ac gui - currently underdevelopment

* /ac devalerts - shows development alerts


**Credits:**
Rhys (made the anticheat base, helped a shit ton, + lots of utils)
funkemunky & Luke (bounding boxes, reflection, protocol from Atlas https://github.com/funkemunky/Atlas) 
(original code of Atlas is from Luke (firefly dev) but I still gotta give funke his credit lol)

Sim0n (AutoClicker utilites from Baldr)


**DEPENDENCIES:**
None required...
