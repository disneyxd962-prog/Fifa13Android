package com.fifa13android

import android.content.Context
import org.json.JSONObject
import java.io.File

/** Checks the app-private embedded runtime manifest and its required files. */
internal object RuntimeProbe {
    data class Result(
        val runtimeDirectoryPresent: Boolean,
        val manifestPresent: Boolean,
        val winePresent: Boolean,
        val x86TranslatorPresent: Boolean,
        val direct3d9Present: Boolean,
        val errors: List<String>
    ) {
        val readyForFurtherValidation: Boolean
            get() = runtimeDirectoryPresent && manifestPresent && winePresent &&
                x86TranslatorPresent && direct3d9Present && errors.isEmpty()

        fun summary(): String = buildString {
            appendLine("Runtime integrado: ${if (runtimeDirectoryPresent) "carpeta encontrada" else "no instalado"}")
            appendLine("Manifiesto: ${if (manifestPresent) "correcto" else "ausente"}")
            appendLine("Wine: ${if (winePresent) "encontrado" else "falta"}")
            appendLine("Traductor x86: ${if (x86TranslatorPresent) "encontrado" else "falta"}")
            append("Direct3D 9: ${if (direct3d9Present) "encontrado" else "falta"}")
            if (errors.isNotEmpty()) appendLine().append(errors.joinToString("\n"))
        }
    }

    fun inspect(context: Context): Result {
        val root = File(context.filesDir, "compat-runtime")
        val manifest = File(root, "manifest.json")
        if (!root.isDirectory || !manifest.isFile) {
            return Result(root.isDirectory, manifest.isFile, false, false, false, emptyList())
        }
        return try {
            val json = JSONObject(manifest.readText())
            fun check(key: String, executable: Boolean = false): Boolean {
                val relative = json.optString(key)
                if (relative.isBlank() || relative.startsWith("/") || relative.split('/').any { it == ".." }) return false
                val file = File(root, relative).canonicalFile
                if (!file.path.startsWith(root.canonicalPath + File.separator)) return false
                return file.isFile && (!executable || file.canExecute())
            }
            Result(true, true, check("wine", true), check("x86Translator", true), check("direct3d9"), emptyList())
        } catch (e: Exception) {
            Result(true, true, false, false, false, listOf("Manifiesto inválido: ${e.message ?: "error de lectura"}"))
        }
    }
}
