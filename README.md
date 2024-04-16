# WorldGrowth
WorldGrowth sets and changes world borders as a gameplay mechanic.

![WorldGrowth Demo GIF](wgdemo.gif)

[![Github All Releases](https://img.shields.io/github/downloads/lichenaut/WorldGrowth/total.svg)]()

## Point System
WorldGrowth's point system is essentially a series of quotas for different types of actions, such as block placement or brewing, which translates into points to fulfill the World Border Growth point quota. The World Border Growth point quota increases by default as the sizes of the world borders increase.

Additionally, points can only be gained up to the World Border Growth point quota, and there are maximum limits on how much world borders can grow per hour. Point-gaining turns off for the remainder of the server hour once this maximum amount of blocks has been reached.

Server maintainers can use the boost command, perhaps as a donor reward, to hasten the rate at which points are gained towards World Border Growth point quotas.

## Unification Events
"What is a 'Unification Event?'", I hear you ask. A Unification Event is something players vote for to happen instead of a World Border Growth. The reason players might do this is because it temporarily expands all world borders to the largest world border, which means a comparatively huge amount of blocks become accessible compared to a permanent World Border Growth.

For example, because 1 block in The Nether is 8 in The Overworld, The Nether's world border is 8 times smaller than The Overworld's! This means it would take a lot of World Border Growths to make most Nether structures accessible. By default, this plugin makes The End's world border 8 times larger than The Overworld's, which means a Unification Event expands The Nether's world border by 64 times! Just be careful not to overextend, or else you won't have time to outrun the shrinking border ;)

Unification Events take one minute each to fully expand and shrink borders, while World Border Growths change border sizes at a rate of one block per second.

## Commands and Permissions
| Command               | Permission          | Description                                                                                        |
|-----------------------|---------------------|----------------------------------------------------------------------------------------------------|
| `/worldgrowth`        | worldgrowth.command | Base command.                                                                                     |
| `/wg boost [multiplier] [ticks]` | (console only)       | Boosts point gaining by the specified multiplier for the specified tick duration.               |
| `/wg help`            | worldgrowth.help    | Links to this README.md.                                                                          |
| `/wg reload`          | worldgrowth.reload  | Reloads WorldGrowth.                                                                              |
| `/wg stats`           | worldgrowth.stats   | Displays information about voting, Unification Events, border sizes, and the current boost.      |
| `/wg vote [yes/no/y/n]` | worldgrowth.vote  | A player uses this to vote for or against a Unification Event.                                    |

