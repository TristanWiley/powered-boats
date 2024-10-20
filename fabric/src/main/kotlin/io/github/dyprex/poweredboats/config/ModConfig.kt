package io.github.dyprex.poweredboats.config

import java.nio.file.Path
import java.util.*
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

data class ModConfig(val boatSpeed: Float = 5.0f) {

    init {
        require(boatSpeed > 0 && boatSpeed <= 10) {
            """boatSpeed must be between 0 and 10.0 (current value is "$boatSpeed")"""
        }
    }

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
