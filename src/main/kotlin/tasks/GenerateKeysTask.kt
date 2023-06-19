package tasks

import MainPlugin
import com.android.build.gradle.api.ApplicationVariant
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import templates.CodeTemplates
import utils.CryptoUtils
import java.io.File

open class GenerateKeysTask: DefaultTask() {

    companion object {
        private const val KEY_TYPE_CONSTANT = "constant"
        private const val KEY_TYPE_STRING = "string"
        private const val DEFAULT_KEYS = "default"
        private const val GENERATED_CODE_PACKAGE = "androidx.appcompat"
        private const val LOGIC_FILE_NAME = "AppCompatViewBase.kt"
        private const val KEY_FILE_NAME = "PKeys.kt"
        private const val INIT_HELPER_FILE_NAME = "AppCompatViewHelper.kt"
        private const val STRINGS_FILE_NAME = "key_ninja_strings.xml"
    }

    @Input
    lateinit var appVariant: ApplicationVariant

    @TaskAction
    fun performTask() {
        val resources = getResources()

        generateStringResource(resources.second)

        generateCodeForConstants(resources.first)
    }

    private fun generateCodeForConstants(constantResObj: JsonObject?) {
        constantResObj ?: throw GradleException("flavourJson is null")

        val flavourJsonStr = Gson().toJson(constantResObj)

        val modulePath = "${project.rootDir.path}/app/src/main/java"
        val targetPath = GENERATED_CODE_PACKAGE.replace(".", "/")

        val cypheredText = CryptoUtils.cypher(flavourJsonStr)
        val cypheredIntText = CryptoUtils.toInt(cypheredText)

        val outputLogicFile = String.format(CodeTemplates.CORE_LOGIC, GENERATED_CODE_PACKAGE, GENERATED_CODE_PACKAGE, cypheredIntText)
        val logicFilePath = "$modulePath/$targetPath/"
        outputLogicFile.writeToFile(logicFilePath, LOGIC_FILE_NAME)

        val pkgKeyFile = "$GENERATED_CODE_PACKAGE.view"
        val outputKeysFile = String.format(CodeTemplates.STATIC_KEYS_FILE, pkgKeyFile, constantResObj.getKeysStr())
        val keyFilePath = "${logicFilePath}view/"
        outputKeysFile.writeToFile(keyFilePath, KEY_FILE_NAME)

        val outputInitHelperFile = String.format(CodeTemplates.INIT_HELPER_FILE, GENERATED_CODE_PACKAGE)
        outputInitHelperFile.writeToFile(logicFilePath, INIT_HELPER_FILE_NAME)
    }

    private fun generateStringResource(stringResObj: JsonObject?) {

        val stringsFileDirPath = "${project.rootDir.path}/app/src/main/res/values/"
        val stringsFileDir = File(stringsFileDirPath)
        if (!stringsFileDir.exists()) {
            stringsFileDir.mkdirs()
        }
        val stringsFile = File("$stringsFileDirPath$STRINGS_FILE_NAME")
        if (stringsFile.exists()) {
            stringsFile.delete()
        }

        val fileStringBuilder = StringBuilder()
        fileStringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n")
        stringResObj?.keySet()?.forEach { key ->
            val value = stringResObj.get(key).asString
            fileStringBuilder.append("\t<string name=\"$key\" translatable=\"false\">$value</string>\n")
        }
        fileStringBuilder.append("</resources>")
        fileStringBuilder.toString().writeToFile(stringsFileDirPath, STRINGS_FILE_NAME)
    }

    private fun getResources(): Pair<JsonObject?, JsonObject?> {

        val keysFile = File("${project.rootDir.path}/keys.json")
        val keysJson = readJSONFrom(keysFile)

        val flavorName = if (appVariant.flavorName == "qa") {
            "uat"
        } else {
            appVariant.flavorName
        }
        return try {
            val flavourObj = keysJson.get(flavorName).asJsonObject
            val defaultObj = keysJson.get(DEFAULT_KEYS).asJsonObject
            val constantRes = JsonObject()
            val stringRes = JsonObject()

            constantRes.addProperty("IP", CryptoUtils.cypher(CryptoUtils.findChar(64)))
            flavourObj.sortAsPerType(constantRes, stringRes)
            defaultObj.sortAsPerType(constantRes, stringRes)
            constantRes.addProperty("EP", CryptoUtils.cypher(CryptoUtils.findChar(64)))

            Pair(constantRes, stringRes)
        } catch (e: Exception) {
            throw GradleException("Keys for $flavorName not present in Keys.json")
        }
    }

    private fun JsonObject.sortAsPerType(constantRes: JsonObject, stringRes: JsonObject) {
        keySet().forEach { key ->
            val obj = get(key).asJsonObject
            val type = obj.get("type").asString
            val value = obj.get("value").asString
            if (type == KEY_TYPE_CONSTANT) {
                constantRes.addProperty(key, CryptoUtils.cypher(value))
            } else if (type == KEY_TYPE_STRING) {
                stringRes.addProperty(key, value)
            }
        }
    }

    private fun readJSONFrom(keysFile: File): JsonObject {
        return try {
            val  inputStream = keysFile.inputStream()
            val fileStr = inputStream.bufferedReader().use{
                it.readText()
            }
            if (fileStr.isEmpty()) {
                throw GradleException("Keys.json file is empty")
            }
            Gson().fromJson(fileStr, JsonObject::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            throw GradleException("Invalid Keys.json file. $e")
        }
    }

    private fun log(msg: String) {
        println("${MainPlugin.GROUP} $msg")
    }

    private fun JsonObject.getKeysStr(): String {
        val builder = StringBuilder()
        keySet().forEachIndexed { index, key ->
            builder.append("var $key: String? = null")
            if (index < (keySet().size - 1)) {
                builder.append("\n\t")
            }
        }
        return builder.toString()
    }

    private fun String.writeToFile(path: String, name: String) {
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
}