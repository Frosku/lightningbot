# Lightning Bot

## Purpose

Lightning Bot is a Discord bot designed for use on pony-themed Discord servers.

## Functionality

- Add operators on a per-guild basis;
- Decide which Philomena server to use -- defaults to Ponybooru;
- Each channel has its own filters, using Philomena's native filter system;
- Fetch random image based on query;
- Fetch image based on ID.

## Usage

### Environment variables

You will need to set these for the bot to work:

- `LIGHTNING_DISCORD_TOKEN` - Bot token.
- `LIGHTNING_DISCORD_PREFIX` - Character to start commands with.
- `LIGHTNING_DISCORD_GLOBAL_ADMIN` - Discord ID of the global admin user.
- `LIGHTNING_PHILOMENA_ROOT` - Root URL for Philomena API (can be used to switch to alternate booru).
- `LIGHTNING_PHILOMENA_KEY` - API key for the chosen Philomena booru.
- `LIGHTNING_CRUX_DB` - Database name for Crux.
- `LIGHTNING_CRUX_DB_DIR` - Database directory for Crux.
- `LIGHTNING_CRUX_EVT_DIR` - Event log directory for Crux.
