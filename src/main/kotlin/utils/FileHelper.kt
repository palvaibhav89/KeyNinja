package utils

import org.gradle.api.GradleException
import java.io.File

fun File.readJSON(): String? {
    return try {
        val fileStr = inputStream().bufferedReader().use{
            it.readText()
        }
        if (fileStr.isEmpty()) {
            throw GradleException("Keys.json file is empty")
        }
        fileStr
    } catch (e: Exception) {
        e.printStackTrace()
        throw GradleException("Invalid Keys.json file. $e")
    }
}

fun String.writeToFile(path: String, name: String) {
    try {
        File(path).let {
            if (!it.exists()) {
                it.mkdirs()
            }
        }
        val fullPath = "$path$name"
        File(fullPath).apply {
            if (exists()) {
                delete()
            }
            printWriter().use { out ->
                forEach {
                    out.print(it)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}