package io.github.dyprex.poweredboats.config

import java.nio.file.Path
import java.util.*
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

data class ModConfig(val boatSpeed: Float = 10.0f) {
    constructor(props: Properties) : this(props.getProperty("boatSpeed").toFloat())

    companion object {

        fun loadFrom(configFile: Path): Result<ModConfig> = runCatching {
            val props = configFile.inputStream().use { Properties().apply { load(it) } }
            ModConfig(props)
        }

        fun ModConfig.saveTo(filePath: Path) {
            val props = Properties().apply { setProperty("boatSpeed", boatSpeed.toString()) }
            filePath.toFile().parentFile.mkdirs()
            filePath.outputStream().use { props.store(it, null) }
        }
    }
}
