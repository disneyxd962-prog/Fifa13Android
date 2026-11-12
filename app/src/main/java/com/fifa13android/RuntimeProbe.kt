package com.fifa13android

import android.content.Context
import java.io.File

/**
 * Checks whether an externally supplied compatibility runtime is present.
 *
 * This is deliberately a readiness check, not a Wine implementation. A working
 * runtime must be packaged/provisioned separately and validated on real devices.
 */
internal object RuntimeProbe {
    data class Result(
        val wineExecutablePresent: Boolean,
        val runtimeManifestPresent: Boolean,
        val runtimeDirectoryPresent: Boolean
    ) {
        val readyForFurtherValidation: Boolean
            get() = wineExecutablePresent && runtimeManifestPresent && runtimeDirectoryPresent

        fun summary(): String {
            val wine = if (wineExecutablePresent) "encontrado" else "falta"
            val manifest = if (runtimeManifestPresent) "encontrado" else "falta"
            val directory = if (runtimeDirectoryPresent) "encontrado" else "falta"
            return "Carpeta compat-runtime: $directory\\nWine: $wine\\nManifest del runtime: $manifest"
        }
    }

    fun inspect(context: Context): Result {
        val runtime = File(context.filesDir, "compat-runtime")
        val wine = File(runtime, "bin/wine")
        val manifest = File(runtime, "manifest.json")
        return Result(
            wineExecutablePresent = wine.isFile && wine.canExecute(),
            runtimeManifestPresent = manifest.isFile,
            runtimeDirectoryPresent = runtime.isDirectory
        )
    }
}
