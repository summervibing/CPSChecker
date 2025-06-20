# CPSChecker
A lightweight Spigot Minecraft plugin that precisely tracks players' clicks per second to provide tools for monitoring, checking and limiting the cps of players to assist server moderation against the usage of auto clickers.
It depends on [ProtocolLib](https://github.com/dmulloy2/ProtocolLib/) to read and write packet data across various Minecraft versions.

## Features

- Live CPS monitor displaying click patterns in the action bar
- Multiple monitor modes: `BASIC`, `HISTORY` and `STREAK`
- Events and notifications when flagging suspicious click rates

## Usage

Place the built `CPSChecker-1.0-SNAPSHOT.jar` in your server's `plugins` folder. On startup the plugin creates two configuration files:

- `configuration.yml` – configure plugin settings
- `messages.yml` – configure messages sent by the plugin

The main command is `/cps`:

```
/cps <username|uuid|off|list|stop|help> [mode|username|uuid]
```

To access this command the `cps.use` permission is required. Players with `cps.use.admin` may also list all active monitors with `/cps list` or stop others' monitors with `/cps stop <username|uuid>`.
While monitoring a player, the staff member can press their drop key to cycle through the different monitor modes. Action bar messages show cps, attack-cps and the pattern history depending on the selected mode. A detailed pattern explanation page can be opened via `/cps help`.

## Patterns

When starting the monitor, it is possible to choose between three different monitor modes:

- `BASIC`: This mode only shows the clicks per second on the left and the attacks per second on the right side.  
**Default display**: `§f%player_name% §8┃ §e%cps%§7/§e%attack_cps%`  
**Example display**: `summervibing ┃ 17/12`

- `HISTORY`: In addition to the clicks per second and the attacks per second, this mode also shows the click pattern of the last two seconds or 40 ticks (by default).  
**Default display**: `§f%player_name% §8┃ §e%cps%§7/§e%attack_cps% §8┃ §a%pattern%`  
**Example display**: `summervibing ┃ 15/7 ┃ CCCAA AAA   CCC CCAA CCCCCC   AAAACC CCC`

- `STREAK`: This mode behaves in a similar way to History mode. However, consecutive ticks in which clicks have occurred are indicated by bracketing and the height of the streak in front of the respective streaks from 6 streaks. Streaks up to 10 consecutive ticks are colored yellow and streaks with 10 or more consecutive ticks with clicks are colored red.  
**Default display**: `§f%player_name% §8┃ §e%cps%§7/§e%attack_cps% §8┃ §a%pattern%`  
**Example display**: `summervibing ┃ 18/10 ┃ CCC    8(CCCCCAAA) AA  15(CCCCAAAAAACCCCC) C CC`

## Build

This project uses Gradle. Run the following command to compile the plugin:
```bash
./gradlew build
```

## Tests

Unit tests are located under `src/test/java`. Execute them with:
```bash
./gradlew test
```

## License

CPSChecker is licensed under the Apache 2.0 License. See the [LICENSE](LICENSE) file for details.