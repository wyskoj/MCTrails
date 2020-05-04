# MCTrails
MCTrails is a simple PaperMC plugin that spawns particles around players. You can create trails based off of any in-game particle, as well
as spawn rate and amount. You can give trails names, and assign which players can use which trails.

Commands are permission based. You can use LuckPerms, etc. to manage permissions.

## Commands
* `/trails` – General use command that opens a simple GUI for enabling and disabling your trail, as well as selecting from your
available trails.
* `/alltrails` – Lists all trails.
* `/createtrail [name] [particle] [icon] [rate] [amount]` – Creates a new trail.
  * `name` – The name of this trail.
  * `particle` – The [particle](https://papermc.io/javadocs/paper/1.15/org/bukkit/Particle.html#enum.constant.summary) to spawn.
  * `icon` – The [material](https://papermc.io/javadocs/paper/1.15/org/bukkit/Particle.html#enum.constant.summary) to represent this
  trail in the trail selection screen.
  * `rate` – The number of milliseconds in between spawn cycles.
  * `amount` – The number of particles to spawn each cycle.
* `/deletetrail [name]` – Deletes a trail.
* `/permittrail [name] [username]` – Allows a player to use a trail.
* `/revoketrail [name] [username]` – Disallows a player to use a trail.
* `/settrailrate [name] [rate]` – Sets the spawn rate, in milliseconds, of an existing trail.
* `/settrailamount [name] [amount]` – Sets the spawn amount of an existing trail.
