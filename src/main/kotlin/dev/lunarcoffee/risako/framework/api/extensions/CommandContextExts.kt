package dev.lunarcoffee.risako.framework.api.extensions

import dev.lunarcoffee.risako.bot.consts.GSON
import dev.lunarcoffee.risako.framework.core.DB
import dev.lunarcoffee.risako.framework.core.commands.CommandContext
import dev.lunarcoffee.risako.framework.core.services.reloaders.*
import dev.lunarcoffee.risako.framework.core.std.idgen.IdGenerator

// Schedules a [Reloadable] so that it can be rescheduled upon a bot restart.
internal suspend inline fun <reified T : Reloadable> CommandContext.scheduleReloadable(
    colName: String,
    vararg args: Any?
) {
    // Construct a new instance of the [Reloadable] represented by the provided type parameter,
    // which should be a user defined class extending [Reloadable].
    val reloadable = T::class.constructors.first().call(*args)
    val col = DB.getCollection<ReloadableJson>(colName)

    // Set the RJID of the reloadable to allow identification after being retrieved from the DB
    // after a restart. This allows calls to [Reloadable#finish] to function properly.
    reloadable.rjid = IdGenerator.generate()

    // [reloadableJson] is a JSON representation of the [Reloadable] object, used so the user
    // defined class can freely define properties while keeping the API friendly.
    val reloadableJson = ReloadableJson(GSON.toJson(reloadable), reloadable.rjid)

    col.insertOne(reloadableJson)
    reloadable.schedule(event, ReloadableCollection(colName, T::class))
}
