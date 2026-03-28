# NoCPVP

**Tired of crystal PVP ruining your server?** NoCPVP is a zero-dependency, highly configurable Paper plugin that shuts down the most common crystal PVP exploits — with no lag and no bloat.

---

## What it blocks

**Ender crystals** — Explosion damage from ender crystals is cancelled outright. No more one-shot-from-nowhere kills.

**Respawn anchors** — Respawn anchor explosions are cancelled in the Overworld and End, where they deal massive AoE damage and are virtually indefensible.

**Beds** — Bed explosions in the Nether and End are blocked. A classic griefing and PVP exploit, gone.

**Debuff splash & lingering potions** — Players can't throw splash or lingering potions containing blocked effects. The throw is cancelled before it even lands — not just the effect.

**Debuff potion effects** — Even if a potion somehow slips through (admin commands, other plugins), the effects listed in your config will never be applied to a player.

**Tipped arrow crafting** — Prevent crafting of tipped arrows with specific potion types. Configurable per-type.

---

## Highly configurable

Every feature can be toggled independently. You control exactly which potion effects are blocked, which arrow types can't be crafted, and whether protections apply globally or only in specific worlds.

```yaml
# Only enforce protections in your PVP arena world
enabled-worlds:
  - pvp_arena

debuff-potions:
  enabled: true
  block-throw: true
  blocked-effects:
    - POISON
    - WITHER
    - WEAKNESS
    - SLOWNESS
    - BLINDNESS
    - INSTANT_DAMAGE
    # ... and more
```

---

## Commands & Permissions

| Command | Permission | Default |
|---|---|---|
| `/nocpvp reload` | `nocpvp.reload` | OP |

Hot-reload your config at any time — no restart required.

---

## Requirements

- **Paper** 1.21 or newer
- No dependencies

---

## Why NoCPVP?

Crystal PVP exploits — ender crystals, respawn anchors, debuff potions — are designed for technical PVP servers and are wildly out of place on survival, SMP, and semi-vanilla servers. Vanilla doesn't give you the tools to stop them. NoCPVP does, in a single small jar with a clean config and no performance overhead.
