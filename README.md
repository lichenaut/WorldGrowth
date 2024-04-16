# WorldGrowth
WorldGrowth sets and expands world borders as a gameplay mechanic.

![WorldGrowth Demo GIF](wgdemo.gif)

[![Github All Releases](https://img.shields.io/github/downloads/lichenaut/WorldGrowth/total.svg)]()

## Unification Events
"What is a 'Unification Event'?" I hear you ask. A Unification Event is something players vote for to happen instead of a World Border Growth. The reason players might do this is because it temporarily expands all world borders to the largest world border, which means a comparatively huge amount of blocks become accessible, compared to a permanent World Border Growth's number.<br>

For example, because 1 block in The Nether is 8 in The Overworld, The Nether's world border is 8 times as small as The Overworld's! This means it would take a lot of World Border Growths to make most Nether structures accessible. By default, this plugin makes The End's world border 8 times as large as The Overworld's, which makes a Unification Event expand The Nether's world border by 64 times! Just be careful to not overextend, else you won't have time to outrun the shrinking border ;)<br>

Unification Events take one minute each to fully expand and shrink borders.

## Commands and Permissions
| Command               | Permission          | Description                                                                                        |
|-----------------------|---------------------|----------------------------------------------------------------------------------------------------|
| `/worldgrowth`        | worldgrowth.command | Base command.                                                                                     |
| `/wg boost [multiplier] [ticks]` | (console only)       | Boosts point gaining by the specified multiplier for the specified tick duration.               |
| `/wg help`            | worldgrowth.help    | Links to this README.md.                                                                          |
| `/wg reload`          | worldgrowth.reload  | Reloads WorldGrowth.                                                                              |
| `/wg stats`           | worldgrowth.stats   | Displays information about voting, Unification Events, border sizes, and the current boost.      |
| `/wg vote [yes/no/y/n]` | worldgrowth.vote  | A player uses this to vote for or against a Unification Event.                                    |

