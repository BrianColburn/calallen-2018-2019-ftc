package org.firstinspires.ftc.teamcode.settings

import java.io.BufferedReader
import java.io.FileReader

class RobotSettings {
    val FILE_PATH = "/storage/emulated/0/robot-settings.txt"

    fun getSettings(): Map<String, String> {
        val settings = HashMap<String, String>()
        val reader = BufferedReader(FileReader(FILE_PATH))
        reader.useLines { sequence ->
            sequence.map { it.split("=") }
                    .map { settings.put(it[0],it[1]) }}
        reader.close()
        return settings
    }
}