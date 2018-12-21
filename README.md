# ScavengeHunt

This is a Spigot plugin I wrote in a day to have an easter egg hunt with my friends on my Spigot Minecraft server.

(Yes, I know it's actually "Scavenger Hunt")

To use this, hide your easter eggs (mob spawners) wherever you want in your world. Create zones that cover the location of the easter eggs accordingly to allow players to warp to the general location of the eggs. This allows them to estimate how many eggs they have left to find.

Commands:

* (scavengehunt.admin) `/createzone <x1> <y1> <z1> <x2> <y2> <z2> <zone-name> <world>` - Creates a zone
* (scavengehunt.admin) `/scavstart <time-ms>` - Starts the game with a time limit in milliseconds
* (scavengehunt.admin) `/scavstop` - Ends the game
* (scavengehunt.user) `/zones` - Lists zones. If used while a game is running, it will list how many eggs are in each zone
* (scavengehunt.user) `/warp` - Warps to a zone