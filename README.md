# NoCPVP

A lightweight Paper plugin that disables crystal PVP mechanics and related exploits on your server.

## Features

| Feature | Description |
|---|---|
| **Ender Crystal damage** | Cancels all damage dealt by ender crystals |
| **Respawn anchor explosions** | Cancels respawn anchor explosions (Overworld/End) |
| **Bed explosions** | Cancels bed explosions (Nether/End) |
| **Debuff potion effects** | Blocks configured potion effects from being applied to players |
| **Splash/lingering potion throws** | Cancels debuff splash and lingering potions before they land |
| **Tipped arrow crafting** | Prevents crafting of tipped arrows with configured potion types |
| **Per-world config** | Restrict all protections to specific worlds only |

## Installation

1. Download the latest jar from releases
2. Drop it into your server's `plugins/` folder
3. Restart the server — `plugins/NoCPVP/config.yml` is generated automatically
4. Edit the config as needed, then run `/nocpvp reload`

**Requires:** Paper 1.21+

## Commands

| Command | Description | Permission |
|---|---|---|
| `/nocpvp reload` | Reloads the config without restarting | `nocpvp.reload` (default: op) |

## Configuration

```yaml
# NoCPVP Configuration
# After editing, run: /nocpvp reload

ender-crystal:
  disable-damage: true

debuff-potions:
  enabled: true
  block-throw: true        # also cancels the throw, not just the landing effect
  blocked-effects:
    - POISON
    - WITHER
    - WEAKNESS
    - SLOWNESS
    - MINING_FATIGUE
    - NAUSEA
    - BLINDNESS
    - HUNGER
    - UNLUCK
    - DARKNESS
    - INSTANT_DAMAGE

non-craftable-tipped-arrows:
  enabled: true
  blocked-potion-types:
    - SLOW_FALLING

respawn-anchor:
  disable-explosion: true

beds:
  disable-explosion: true

# Restrict all protections to these worlds only.
# Leave empty to apply in every world.
enabled-worlds: []
```

### Config reference

**`ender-crystal.disable-damage`** — Cancels any damage event where the damager is an ender crystal.

**`debuff-potions.enabled`** — Master toggle for potion effect blocking and potion throw blocking.

**`debuff-potions.block-throw`** — When true, cancels splash/lingering potion projectiles thrown by players whose effects match the blocked list. Requires `debuff-potions.enabled: true`.

**`debuff-potions.blocked-effects`** — List of [`PotionEffectType`](https://jd.papermc.io/paper/1.21/org/bukkit/potion/PotionEffectType.html) names. Both the effect landing on a player and the potion throw are blocked.

**`non-craftable-tipped-arrows.enabled`** — Master toggle for tipped arrow crafting restrictions.

**`non-craftable-tipped-arrows.blocked-potion-types`** — List of [`PotionType`](https://jd.papermc.io/paper/1.21/org/bukkit/potion/PotionType.html) names. Arrows of these types cannot be crafted.

**`respawn-anchor.disable-explosion`** — Cancels respawn anchor block explosions entirely. Useful for Overworld/End where they deal damage.

**`beds.disable-explosion`** — Cancels bed block explosions entirely. Covers all 16 bed colors. Useful for Nether/End.

**`enabled-worlds`** — List of world names (e.g. `world`, `world_nether`). When non-empty, protections only apply in the listed worlds. Defaults to empty (all worlds).

## Building

```bash
./gradlew shadowJar
```

Output: `build/libs/NoCPVP-1.0-all.jar`

## License

See [LICENSE](LICENSE).
