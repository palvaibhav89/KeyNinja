package utils

import com.google.gson.JsonObject
import org.gradle.api.GradleException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

object KeychainHelper {

    private const val KEY_CHAIN_NAME = "KeyNinja"
    private const val KEY_CHAIN_KEY_NAME = "KeysJson"
    private const val KEY_CHAIN_SERVICE = "KeyNinjaService"

    fun getData(
        keyJsonVersion: Int
    ): JsonObject? {

        createKeyChain()

        var mainData = fetchData(keyJsonVersion)
        if (!mainData.isNullOrEmpty() && !mainData.contains("The specified item could not be found in the keychain")) {
            //Got the data from keychain. Decoding the data and check for version compatibility
            val decodedData = Base64.getMimeDecoder().decode(mainData)
            mainData = String(decodedData)
            val mainDataObj = mainData.jsonToJsonObject()

            if (mainDataObj?.get("version")?.asInt != keyJsonVersion) {
                //Version is different from specified in app level build.gradle file. Will try to read data from file.
                return mainDataObj
            }
        }
        return null
    }

    fun addData(mainData: String, keyJsonVersion: Int) {
        deleteKeyChain()
        createKeyChain()

        val keyName = "${KEY_CHAIN_KEY_NAME}_$keyJsonVersion"
        val base64Data = Base64.getEncoder().encodeToString(mainData.toByteArray())
        val storeCommand = listOf("security", "add-generic-password", "-U", "-s", KEY_CHAIN_SERVICE, "-a", keyName, "-w", base64Data)
        executeCommand(storeCommand, "Failed to save data in keychain.")
    }

    private fun fetchData(keyJsonVersion: Int): String? {
        val keyName = "${KEY_CHAIN_KEY_NAME}_$keyJsonVersion"

        val retrieveCommand = listOf("security", "find-generic-password", "-s", KEY_CHAIN_SERVICE, "-a", keyName, "-w", KEY_CHAIN_NAME)
        return executeCommand(retrieveCommand)
    }

    private fun deleteKeyChain() {
        val deleteKeyChainCommand = listOf("security", "delete-keychain", KEY_CHAIN_NAME)
        executeCommand(deleteKeyChainCommand, "Error Deleting keychain.")
    }

    private fun createKeyChain() {
        val createKeyChainCommand = listOf("security", "create-keychain", "-p", "", KEY_CHAIN_NAME)
        val response = executeCommand(createKeyChainCommand)
        if (!response.isNullOrEmpty() && !response.contains("already exists")) {
            throw GradleException("KeyChain creation failed. $response")
        }
    }

    private fun executeCommand(commands: List<String>, errorMessage: String? = null): String? {
        try {
            val process = ProcessBuilder(commands)
                .redirectErrorStream(true)
                .start()

            val status = process.waitFor()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val builder = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                builder.append(line)
                builder.append(System.getProperty("line.separator"))
            }

            val response = builder.toString()

            if (status != 0 && !errorMessage.isNullOrEmpty()) {
                throw GradleException("$errorMessage : $response")
            }

            return builder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}