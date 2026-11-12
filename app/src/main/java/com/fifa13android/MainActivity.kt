package com.fifa13android

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.app.ActivityManager
import android.content.pm.PackageManager
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile

class MainActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("fifa13_settings", MODE_PRIVATE) }
    private lateinit var folderStatus: TextView
    private lateinit var scanStatus: TextView
    private val folderPicker = 13013

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildScreen()
        val saved = prefs.getString("game_folder", null)
        folderStatus.text = if (saved == null) "Carpeta del juego: no seleccionada"
            else "Carpeta del juego: seleccionada"
    }

    private fun buildScreen() {
        val scroll = ScrollView(this)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 28, 24, 28)
            setBackgroundColor(0xFF101820.toInt())
        }
        scroll.addView(root)

        fun heading(text: String) {
            root.addView(TextView(this).apply {
                this.text = text
                textSize = 21f
                setTextColor(0xFFFFFFFF.toInt())
                setPadding(0, 18, 0, 8)
            })
        }
        fun note(text: String) {
            root.addView(TextView(this).apply {
                this.text = text
                textSize = 14f
                setTextColor(0xFFCCD6DD.toInt())
                setPadding(0, 4, 0, 10)
            })
        }
        fun actionButton(label: String, onClick: () -> Unit) {
            root.addView(Button(this).apply {
                text = label
                setOnClickListener { onClick() }
            })
        }
        fun setting(label: String, values: List<String>, key: String, default: String) {
            note(label)
            val spinner = Spinner(this)
            spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, values)
            val selected = values.indexOf(prefs.getString(key, default)).coerceAtLeast(0)
            spinner.setSelection(selected)
            spinner.onItemSelectedListener = SimpleSelectionListener { value ->
                prefs.edit().putString(key, value).apply()
            }
            root.addView(spinner)
        }

        heading("FIFA 13 Android")
        note("Contenedor Android experimental · versión 0.2.0")
        heading("Instalación del juego")
        folderStatus = TextView(this).apply {
            text = "Carpeta del juego: no seleccionada"
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 15f
        }
        root.addView(folderStatus)
        actionButton("Seleccionar carpeta de FIFA 13") {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            startActivityForResult(intent, folderPicker)
        }
        note("Selecciona la carpeta que contiene fifa13.exe. La app busca dentro de la carpeta elegida y sus subcarpetas; no copia ni incluye archivos del juego.")
        actionButton("Detectar FIFA 13") { scanForGame() }
        scanStatus = TextView(this).apply {
            text = "Estado: todavía no se ha comprobado la carpeta."
            setTextColor(0xFFB8D7FF.toInt())
            textSize = 14f
        }
        root.addView(scanStatus)

        heading("Compatibilidad del dispositivo")
        note(deviceCompatibilitySummary())

        heading("Gráficos")
        setting("Resolución objetivo", listOf("Automática", "1920 × 1080", "1600 × 900", "1280 × 720", "960 × 540"), "resolution", "960 × 540")
        setting("Calidad", listOf("Baja", "Media", "Alta"), "quality", "Baja")
        setting("Límite de FPS", listOf("Automático", "30", "45", "60"), "fps", "30")
        note("Perfil inicial para el Galaxy A52 5G con 6 GB: 960 × 540, calidad baja y límite de 30 FPS. Son preferencias guardadas; todavía no modifican el renderizado porque falta integrar el motor de compatibilidad. Android administra la memoria y la app no reserva 2,5 GB de forma fija.")

        heading("Controles táctiles · diseño preliminar")
        note("Distribución visual inspirada en un mando Xbox. Todavía no envía entradas al juego.")
        val pad = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }
        listOf("LB       LT                         RT       RB", "                         Y", "                 X      A      B", "                         MENU").forEach { line ->
            pad.addView(TextView(this).apply {
                text = line
                textSize = 17f
                gravity = Gravity.CENTER
                setTextColor(0xFFFFFFFF.toInt())
                setPadding(8, 12, 8, 12)
            })
        }
        root.addView(pad)

        heading("Diagnóstico del runtime")
        val runtime = RuntimeProbe.inspect(this)
        note(runtime.summary())
        note(if (runtime.readyForFurtherValidation) {
            "Se han encontrado los archivos básicos del runtime. Aún hay que validar la traducción x86 de 32 bits, Direct3D 9, audio, entrada y arranque real del juego."
        } else {
            "No se puede iniciar FIFA 13 todavía: falta instalar un runtime Wine/Hangover compatible en el almacenamiento privado de la app."
        })
        val playButton = Button(this).apply {
            text = "JUGAR A FIFA 13"
            isEnabled = false
        }
        root.addView(playButton)
        note("El botón permanecerá desactivado hasta integrar y probar todos los componentes de ejecución. No basta con detectar fifa13.exe.")
        setContentView(scroll)
    }

    private fun deviceCompatibilitySummary(): String {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val glVersion = manager.deviceConfigurationInfo?.glEsVersion ?: "desconocido"
        val abis = Build.SUPPORTED_ABIS.joinToString()\n        val memoryClassMb = (getSystemService(ACTIVITY_SERVICE) as ActivityManager).memoryClass
        val vulkan = if (Build.VERSION.SDK_INT >= 24 &&
            packageManager.hasSystemFeature(PackageManager.FEATURE_VULKAN_HARDWARE_LEVEL)) "sí" else "no detectado"
        return "Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n" +
            "Arquitecturas: $abis\nOpenGL ES: $glVersion\nVulkan: $vulkan\n" +
            "Nota: detectar Vulkan/OpenGL no garantiza que Direct3D 9 o FIFA 13 sean compatibles."
    }

    private fun scanForGame() {
        val rawUri = prefs.getString("game_folder", null)
        if (rawUri == null) {
            Toast.makeText(this, "Primero selecciona la carpeta del juego", Toast.LENGTH_SHORT).show()
            return
        }
        scanStatus.text = "Buscando fifa13.exe…"
        Thread {
            val root = DocumentFile.fromTreeUri(this, Uri.parse(rawUri))
            val result = if (root == null || !root.isDirectory) null else findGameExecutable(root, 0, intArrayOf(0))
            runOnUiThread {
                if (result != null) {
                    scanStatus.text = "FIFA 13 detectado: ${result.name}\nUbicación: ${result.uri}\nEsto confirma que existe el ejecutable, no que el juego ya pueda arrancar."
                    Toast.makeText(this, "Se ha encontrado fifa13.exe", Toast.LENGTH_LONG).show()
                } else {
                    scanStatus.text = "No se encontró fifa13.exe en la carpeta seleccionada. Selecciona la carpeta principal correcta."
                    Toast.makeText(this, "No se encontró fifa13.exe", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    private fun findGameExecutable(folder: DocumentFile, depth: Int, visited: IntArray): DocumentFile? {
        if (depth > 8 || visited[0] >= 3000) return null
        val children = try { folder.listFiles() } catch (_: Exception) { return null }
        for (child in children) {
            visited[0]++
            if (visited[0] >= 3000) return null
            if (child.isFile && child.name.equals("fifa13.exe", ignoreCase = true)) return child
        }
        for (child in children) {
            if (child.isDirectory) {
                val found = findGameExecutable(child, depth + 1, visited)
                if (found != null) return found
            }
        }
        return null
    }

    @Deprecated("Deprecated by Android, kept for broad device compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == folderPicker && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            val flags = data.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            contentResolver.takePersistableUriPermission(uri, flags)
            prefs.edit().putString("game_folder", uri.toString()).apply()
            folderStatus.text = "Carpeta del juego: seleccionada"
            scanStatus.text = "Carpeta guardada. Pulsa «Detectar FIFA 13» para buscar el ejecutable."
            Toast.makeText(this, "Carpeta guardada", Toast.LENGTH_SHORT).show()
        }
    }
}

private class SimpleSelectionListener(
    private val selected: (String) -> Unit
) : android.widget.AdapterView.OnItemSelectedListener {
    override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
        selected(parent?.getItemAtPosition(position)?.toString() ?: return)
    }
    override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
}
