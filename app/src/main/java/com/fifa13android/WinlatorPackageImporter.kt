package com.fifa13android

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.zip.ZipInputStream

/**
 * Imports runtime payloads from a user-selected Winlator APK into app-private storage.
 * This stages the packages only; it does not install/extract Wine or execute Windows binaries.
 */
internal object WinlatorPackageImporter {
    data class Result(val files: List<String>, val rootfsSha256: String, val message: String)

    private data class Payload(val apkPath: String, val outputPath: String, val maxBytes: Long)

    private val payloads = listOf(
        Payload("assets/rootfs.txz", "packages/rootfs.txz", 250L * 1024 * 1024),
        Payload("assets/rootfs_patches.tzst", "packages/rootfs_patches.tzst", 64L * 1024 * 1024),
        Payload("assets/box64/box64-0.3.4.tzst", "packages/box64-0.3.4.tzst", 64L * 1024 * 1024),
        Payload("assets/dxwrapper/dxvk-2.4.1.tzst", "packages/dxvk-2.4.1.tzst", 64L * 1024 * 1024),
        Payload("assets/dxwrapper/dxvk-1.10.3.tzst", "packages/dxvk-1.10.3.tzst", 64L * 1024 * 1024),
        Payload("assets/dxwrapper/vkd3d-2.13.tzst", "packages/vkd3d-2.13.tzst", 64L * 1024 * 1024)
    )

    fun import(context: Context, apkUri: Uri): Result {
        val root = File(context.filesDir, "compat-runtime")
        val staging = File(context.filesDir, "compat-runtime-staging")
        staging.deleteRecursively()
        check(staging.mkdirs() || staging.isDirectory) { "No se pudo crear el almacenamiento temporal." }

        val wanted = payloads.associateBy { it.apkPath }
        val copied = mutableListOf<String>()
        val sizes = mutableMapOf<String, Long>()
        context.contentResolver.openInputStream(apkUri).use { source ->
            requireNotNull(source) { "No se pudo abrir el APK seleccionado." }
            ZipInputStream(source.buffered()).use { zip ->
                while (true) {
                    val entry = zip.nextEntry ?: break
                    val payload = wanted[entry.name]
                    if (payload != null && !entry.isDirectory) {
                        val output = File(staging, payload.outputPath)
                        check(output.canonicalPath.startsWith(staging.canonicalPath + File.separator)) {
                            "Ruta de paquete no válida."
                        }
                        output.parentFile?.mkdirs()
                        var total = 0L
                        FileOutputStream(output).buffered().use { out ->
                            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                            while (true) {
                                val count = zip.read(buffer)
                                if (count < 0) break
                                total += count
                                require(total <= payload.maxBytes) { "Paquete demasiado grande: ${entry.name}" }
                                out.write(buffer, 0, count)
                            }
                        }
                        require(total > 0) { "Paquete vacío: ${entry.name}" }
                        copied += payload.outputPath
                        sizes[payload.outputPath] = total
                    }
                    zip.closeEntry()
                }
            }
        }

        val rootfs = File(staging, "packages/rootfs.txz")
        val box64 = File(staging, "packages/box64-0.3.4.tzst")
        require(rootfs.isFile && box64.isFile) {
            staging.deleteRecursively()
            "El APK no contiene los paquetes mínimos esperados (rootfs.txz y Box64)."
        }
        val digest = sha256(rootfs)
        val manifest = File(staging, "import-manifest.txt")
        manifest.writeText(buildString {
            appendLine("source=Winlator APK selected by user")
            appendLine("rootfs.sha256=$digest")
            sizes.forEach { (name, size) -> appendLine("$name=$size") }
            appendLine("status=staged-not-installed")
        })

        if (root.exists()) root.deleteRecursively()
        check(staging.renameTo(root)) { "No se pudo guardar el runtime importado." }
        return Result(copied, digest, "Paquetes copiados al almacenamiento privado. Wine todavía no está instalado ni ejecutándose.")
    }

    private fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().buffered().use { input ->
            val buffer = ByteArray(32 * 1024)
            while (true) {
                val count = input.read(buffer)
                if (count < 0) break
                digest.update(buffer, 0, count)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}
