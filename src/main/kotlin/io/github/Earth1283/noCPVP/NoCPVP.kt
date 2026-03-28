package io.github.Earth1283.noCPVP

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.Tag
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.EnderCrystal
import org.bukkit.entity.Player
import org.bukkit.entity.ThrownPotion
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType

class NoCPVP : JavaPlugin(), Listener {

    private var crystalDisableDamage = true
    private var debuffEnabled = true
    private var blockedEffects = mutableSetOf<PotionEffectType>()
    private var arrowsEnabled = true
    private var blockedPotionTypes = mutableSetOf<PotionType>()
    private var respawnAnchorEnabled = true
    private var bedExplosionEnabled = true
    private var blockThrowEnabled = true
    private var enabledWorlds = emptySet<String>()

    private fun isWorldAllowed(worldName: String): Boolean =
        enabledWorlds.isEmpty() || worldName in enabledWorlds

    override fun onEnable() {
        saveDefaultConfig()
        loadPluginConfig()
        server.pluginManager.registerEvents(this, this)
        logger.info("NoCPVP enabled.")
    }

    override fun onDisable() {
        logger.info("NoCPVP disabled.")
    }

    private fun loadPluginConfig() {
        reloadConfig()

        crystalDisableDamage = config.getBoolean("ender-crystal.disable-damage", true)

        debuffEnabled = config.getBoolean("debuff-potions.enabled", true)
        blockedEffects = config.getStringList("debuff-potions.blocked-effects")
            .mapNotNull { name ->
                Registry.EFFECT.get(NamespacedKey.minecraft(name.lowercase()))
                    .also { if (it == null) logger.warning("Unknown PotionEffectType '$name' in config, skipping.") }
            }
            .toMutableSet()

        arrowsEnabled = config.getBoolean("non-craftable-tipped-arrows.enabled", true)
        blockedPotionTypes = config.getStringList("non-craftable-tipped-arrows.blocked-potion-types")
            .mapNotNull { name ->
                runCatching { PotionType.valueOf(name) }.getOrNull()
                    .also { if (it == null) logger.warning("Unknown PotionType '$name' in config, skipping.") }
            }
            .toMutableSet()

        respawnAnchorEnabled = config.getBoolean("respawn-anchor.disable-explosion", true)
        bedExplosionEnabled  = config.getBoolean("beds.disable-explosion", true)
        blockThrowEnabled    = config.getBoolean("debuff-potions.block-throw", true)
        enabledWorlds        = config.getStringList("enabled-worlds").toSet()

        logger.info("Config loaded: crystal-disable=$crystalDisableDamage, " +
                "debuffs=${blockedEffects.size} blocked, " +
                "blocked-arrow-types=${blockedPotionTypes.size}, " +
                "respawn-anchor=$respawnAnchorEnabled, beds=$bedExplosionEnabled, " +
                "block-throw=$blockThrowEnabled, worlds=${enabledWorlds.ifEmpty { setOf("all") }}")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name.equals("nocpvp", ignoreCase = true) && args.getOrNull(0).equals("reload", ignoreCase = true)) {
            if (!sender.hasPermission("nocpvp.reload")) {
                sender.sendMessage("§cYou don't have permission to do that.")
                return true
            }
            loadPluginConfig()
            sender.sendMessage("§aNoCPVP config reloaded.")
            return true
        }
        return false
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onEnderCrystalDamage(event: EntityDamageByEntityEvent) {
        if (!crystalDisableDamage) return
        if (!isWorldAllowed(event.entity.world.name)) return
        if (event.damager is EnderCrystal) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPotionEffect(event: EntityPotionEffectEvent) {
        if (!debuffEnabled || event.entity !is Player) return
        if (!isWorldAllowed(event.entity.world.name)) return
        val newEffectType = event.newEffect?.type ?: return
        if (newEffectType in blockedEffects) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPrepareCraft(event: PrepareItemCraftEvent) {
        if (!arrowsEnabled) return
        val playerWorld = (event.viewers.firstOrNull() as? Player)?.world?.name
        if (playerWorld != null && !isWorldAllowed(playerWorld)) return
        val result = event.inventory.result ?: return
        if (result.type != Material.TIPPED_ARROW) return
        val meta = result.itemMeta as? PotionMeta ?: return
        val isBlocked = meta.basePotionType in blockedPotionTypes ||
            meta.customEffects.any { it.type in blockedEffects }
        if (isBlocked) {
            event.inventory.result = null
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onRespawnAnchorExplode(event: BlockExplodeEvent) {
        if (!respawnAnchorEnabled) return
        if (event.block.type != Material.RESPAWN_ANCHOR) return
        if (!isWorldAllowed(event.block.world.name)) return
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBedExplode(event: BlockExplodeEvent) {
        if (!bedExplosionEnabled) return
        if (!Tag.BEDS.isTagged(event.block.type)) return
        if (!isWorldAllowed(event.block.world.name)) return
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPotionThrow(event: ProjectileLaunchEvent) {
        if (!debuffEnabled || !blockThrowEnabled) return
        val potion = event.entity as? ThrownPotion ?: return
        if (potion.shooter !is Player) return
        if (!isWorldAllowed(potion.location.world.name)) return
        val meta = potion.item.itemMeta as? PotionMeta ?: return
        val isBlocked = meta.basePotionType in blockedPotionTypes ||
            meta.customEffects.any { it.type in blockedEffects }
        if (isBlocked) event.isCancelled = true
    }
}
