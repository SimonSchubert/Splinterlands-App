package com.splintergod.app

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.splintergod.app.models.*
import org.json.JSONArray
import java.io.File
import java.lang.reflect.Type

class Cache(val context: Context) {

    fun getSelectedPlayerName(): String {
        val file = File(context.filesDir, "player_name.json")
        return if (file.exists()) {
            file.readText()
        } else {
            ""
        }
    }

    fun writeSelectedPlayerName(player: String) {
        context.openFileOutput("player_name.json", Context.MODE_PRIVATE).use {
            it.write(player.toByteArray())
        }
    }

    fun getPlayerList(): List<String> {
        val file = File(context.filesDir, "players.json")
        return if (file.exists()) {
            try {
                JSONArray(file.readText()).toStringList()
            } catch (ignore: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun writePlayerToList(player: String) {
        val players = getPlayerList().toMutableList()
        if (!players.contains(player)) {
            players.add(player)
            write(
                fileName = "players.json",
                data = players.joinToString(prefix = "[", postfix = "]")
            )
        }
    }

    fun deletePlayerFromList(player: String) {
        val players = getPlayerList().toMutableList()
        players.remove(player)
        write(
            fileName = "players.json",
            data = players.joinToString(prefix = "[", postfix = "]")
        )
    }

    fun getBalances(player: String): List<Balances> {
        return get<List<Balances>?>(
            fileName = "balances_${player}.json",
            type = object : TypeToken<List<Balances>>() {}.type
        )?.filterBalances() ?: emptyList()
    }

    fun write(fileName: String, data: String) {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(data.toByteArray())
        }
    }

    fun <T> get(fileName: String, type: Class<T>): T? {
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            try {
                return Gson().fromJson(file.readText(), type)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
        return null
    }

    fun <T> get(fileName: String, type: Type): T? {
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            try {
                return Gson().fromJson(file.readText(), type)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
        return null
    }

    fun getCollection(player: String): List<Card> {
        return get(
            fileName = "collection_${player}.json",
            type = CollectionResponse::class.java
        )?.cards?.groupCards() ?: emptyList()
    }

    fun getCardDetails(): List<CardDetail> {
        return get(
            fileName = "card_details.json",
            type = object : TypeToken<List<CardDetail>>() {}.type
        ) ?: emptyList()
    }

    fun getBattleHistory(player: String): List<Battle> {
        val battles = mutableListOf<Battle>()

        val wildBattles: BattleHistory? = get(
            fileName = "battles_${player}_wild.json",
            type = BattleHistory::class.java
        )
        wildBattles?.battles?.let {
            battles.addAll(it)
        }

        val modernBattles: BattleHistory? = get(
            fileName = "battles_${player}_modern.json",
            type = BattleHistory::class.java
        )
        modernBattles?.battles?.let {
            battles.addAll(it)
        }

        return battles.sortedByDescending { it.createdDate }
    }

    fun getPlayerDetails(player: String): PlayerDetails? {
        return get(
            fileName = "details_${player}.json",
            type = PlayerDetails::class.java
        )
    }

    fun getRewardsInfo(player: String): RewardsInfo? {
        return get(
            fileName = "rewards_info_${player}.json",
            type = RewardsInfo::class.java
        )
    }

    fun getSettings(): GameSettings? {
        return get(
            fileName = "game_settings.json",
            type = GameSettings::class.java
        )
    }

    fun getAbilities(): List<Ability> {
        return try {
            Gson().fromJson(abilitiesJson, object : TypeToken<List<Ability>>() {}.type)
        } catch (exception: Exception) {
            exception.printStackTrace()
            emptyList()
        }
    }

    // Copied from SM.min.js / No API(?)
    private val abilitiesJson =
        "[{name:\"Affliction\",desc:\"When a Monster with Affliction hits a target, it has a chance of applying Affliction on the target causing it to be unable to be healed.\",effect_name:\"Afflicted\",effect_desc:\"This monster may not be healed.\"},{name:\"Amplify\",desc:\"Increases Magic Reflect, Return Fire, and Thorns damage to all enemy monsters by 1.\"},{name:\"Backfire\",desc:\"If an enemy misses this Monster with an attack, the attacker takes 2 damage.\"},{name:\"Blast\",desc:\"Does additional damage to Monsters adjacent to the target Monster.\"},{name:\"Blind\",desc:\"All enemy Melee & Ranged attacks have an increased chance of missing their target.\",effect_name:\"Blinded\",effect_desc:\"Reduced chance of hitting with MELEE and RANGED attacks.\"},{name:\"Bloodlust\",desc:\"Every time this Monster defeats an opponent, it gets +1 to all stats (in the Reverse Speed ruleset, -1 to Speed).\"},{name:\"Camouflage\",desc:\"This Monster cannot be targeted for attacks unless it's in the first position.\"},{name:\"Cleanse\",desc:\"Removes all negative effects on the Monster in the first position on the friendly team.\"},{name:\"Deathblow\",desc:\"This Monster does 2x damage if their target is the only Monster left on the enemy team.\"},{name:\"Demoralize\",desc:\"Reduces the Melee attack of all enemy Monsters.\",effect_name:\"Demoralized\",effect_desc:\"-1 to MELEE ATTACK\"},{name:\"Divine Shield\",desc:\"The first time the Monster takes damage it is ignored.\",effect_name:\"Shielded\",effect_desc:\"The first time this monster takes damage it is ignored.\"},{name:\"Dodge\",desc:\"Has an increased chance of evading Melee or Ranged attacks.\"},{name:\"Double Strike\",desc:\"Monster attacks twice each round.\"},{name:\"Enrage\",desc:\"Has increased Melee attack and Speed when damaged.\",effect_name:\"Enraged\",effect_desc:\"Enraged monsters get increased speed and attack damage when not at full health.\"},{name:\"Flying\",desc:\"Has an increased chance of evading Melee or Ranged attacks from Monsters who do not have the Flying ability.\"},{name:\"Forcefield\",desc:\"This Monster takes only 1 damage from attacks with power 5+\"},{name:\"Giant Killer\",desc:\"Does double damage against targets that cost 10 or more mana.\"},{name:\"Halving\",desc:\"Each time this Monster hits a target , the target's attack is cut in half (rounded down).\",effect_name:\"Halved\",effect_desc:\"Attack stats cut in half\"},{name:\"Headwinds\",desc:\"Reduces the Ranged attack of all enemy Monsters.\",effect_name:\"Headwinds\",effect_desc:\"-1 to RANGED ATTACK\"},{name:\"Heal\",desc:\"Restores a portion of the Monster's health each round.\"},{name:\"Tank Heal\",desc:\"Restores a portion of the Monster in the first position's health each round.\"},{name:\"Inspire\",desc:\"Gives all friendly Monsters +1 Melee attack.\",effect_name:\"Inspired\",effect_desc:\"+1 to MELEE ATTACK\"},{name:\"Knock Out\",desc:\"Does double damage when attacking an enemy that is stunned.\"},{name:\"Last Stand\",desc:\"Gains increased stats if it's the only Monster on the team alive.\",effect_name:\"Last Stand\",effect_desc:\"+50% to all stats\"},{name:\"Life Leech\",desc:\"Monster's health increases each time it damages an enemy Monster's health in proportion to the damage dealt.\"},{name:\"Magic Reflect\",desc:\"When hit with Magic damage, does reduced Magic damage back to the attacker.\"},{name:\"Opportunity\",desc:\"Monsters with the Opportunity ability may attack from any position and will target the enemy Monster with the lowest health.\"},{name:\"Oppress\",desc:\"Does double damage when attacking an enemy that has no attack.\"},{name:\"Piercing\",desc:\"If Melee or Ranged attack damage is in excess of the target's Armor, the remainder will damage the target's Health.\"},{name:\"Poison\",desc:\"Attacks have a chance to apply poison, which does automatic damage to the target at the beginning of each round after the poison is applied.\",effect_name:\"Poisoned\",effect_desc:\"Poisoned monsters take 2 damage at the start of each round.\"},{name:\"Protect\",desc:\"All friendly Monsters gain +2 Armor.\",effect_name:\"Protected\",effect_desc:\"+2 to ARMOR\"},{name:\"Reach\",desc:\"Melee attack Monsters with the Reach ability may attack from the second position on the team.\"},{name:\"Recharge\",desc:\"This Monster attacks every other round but does 3x damage.\"},{name:\"Reflection Shield\",desc:\"This Monster doesn't take damage from Blast, Magic Reflect, Thorns, or Return Fire.\"},{name:\"Rebirth\",desc:\"When this Monster dies it will self-resurrect with 1 Health once per battle.\"},{name:\"Redemption\",desc:\"When this Monster dies, it does 2 damage to all enemy monsters.\"},{name:\"Repair\",desc:\"Restores some armor to the friendly Monster whose armor has taken the most damage.\"},{name:\"Resurrect\",desc:\"When a friendly Monster dies it is brought back to life with 1 Health. This ability can only trigger once per battle.\"},{name:\"Retaliate\",desc:\"When hit with a Melee attack, Monsters with Retaliate have a chance of attacking their attacker.\"},{name:\"Return Fire\",desc:\"When hit with a Ranged attack, Monsters with Return Fire will return reduced damage back to their attacker.\"},{name:\"Rust\",desc:\"Reduces the Armor of all enemy Monsters.\",effect_name:\"Rusted\",effect_desc:\"-2 Armor\"},{name:\"Scattershot\",desc:\"This Monster's attacks hit a random enemy target.\"},{name:\"Scavenger\",desc:\"Gains 1 max health each time any monster dies.\"},{name:\"Shatter\",desc:\"Target's armor is destroyed when hit by an attack from Monsters with Shatter.\"},{name:\"Shield\",desc:\"Reduced damage from Melee and Ranged attacks.\"},{name:\"Silence\",desc:\"Reduces the Magic Attack of all enemy Monsters.\",effect_name:\"Silenced\",effect_desc:\"-1 to MAGIC ATTACK\"},{name:\"Slow\",desc:\"Reduces the Speed of all enemy Monsters.\",effect_name:\"Slowed\",effect_desc:\"-1 to SPEED\"},{name:\"Snare\",desc:\"When attacking enemies with Flying, removes the Flying ability and cannot miss.\",effect_name:\"Snared\",effect_desc:\"Loses the Flying ability\"},{name:\"Sneak\",desc:\"Targets the last Monster on the enemy Team instead of the first Monster.\"},{name:\"Snipe\",desc:\"Targets enemy Monsters with Ranged, Magic, or no attack that are not in the first position.\"},{name:\"Strengthen\",desc:\"All friendly Monsters have increased Health.\",effect_name:\"Strengthened\",effect_desc:\"+1 to HEALTH\"},{name:\"Stun\",desc:\"When a Monster with Stun hits a target, it has a chance to stun the target causing it to skip its next turn.\",effect_name:\"Stunned\",effect_desc:\"Stunned monsters skip their next turn.\"},{name:\"Swiftness\",desc:\"All friendly Monsters have increased Speed.\",effect_name:\"Swiftened\",effect_desc:\"+1 to SPEED\"},{name:\"Taunt\",desc:\"All enemy Monsters target this Monster (if they are able to).\"},{name:\"Thorns\",desc:\"When hit with a Melee attack, does damage back to the attacker.\"},{name:\"Trample\",desc:\"When a Monster with Trample hits and kills its target, it will perform another attack on the next Monster on the enemy Team.\"},{name:\"Triage\",desc:\"Heals the friendly back-line Monster that has taken the most damage.\"},{name:\"Void\",desc:\"Reduced damage from Magic attacks.\"},{name:\"Weaken\",desc:\"Reduces the Health of all enemy Monsters.\",effect_name:\"Weakened\",effect_desc:\"-1 to HEALTH\"},{name:\"Void Armor\",desc:\"Magic attacks hit this Monster's armor before its Health.\"},{name:\"Immunity\",desc:\"This monster is immune to negative status effects.\"},{name:\"Cripple\",desc:\"Each time an enemy is hit by a Monster with Cripple it loses one max health.\",effect_name:\"Crippled\",effect_desc:\"-1 to MAX HEALTH\"},{name:\"Close Range\",desc:\"Monsters with the Close Range ability can use Ranged attack from the first position.\"},{name:\"True Strike\",desc:\"This Monster's attacks cannot miss.\"},{name:\"Phase\",desc:\"Magic attacks can miss this Monster (using the same hit/miss calculation as for Melee and Ranged attacks).\"},{name:\"Dispel\",desc:\"When this monster hits an enemy, it clears all buffs and positive status effects on that enemy.\"},{name:\"Fury\",desc:\"This Monster does double damage to targets with the Taunt ability.\"},{name:\"Weapons Training\",desc:\"Adjacent characters gain this character's attack if they have no attack themselves.\",effect_name:\"Trained\",effect_desc:\"Gaining attack from Monster with Weapons Training.\"},{name:\"Martyr\",desc:\"When this Monster dies, adjacent Monsters get +1 to all stats.\"},{name:\"Conscript\",desc:\"Allows the use of one additional Gladiator card in battle.\"}]"
}