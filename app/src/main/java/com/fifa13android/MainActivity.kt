package com.fifa13android

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("fifa13_settings", MODE_PRIVATE) }
    private lateinit var folderStatus: TextView
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
        note("Contenedor Android experimental · versión 0.1.0")
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
        note("Selecciona la carpeta donde está fifa13.exe. La app solo guarda el permiso de acceso; no copia ni incluye los archivos del juego.")

        heading("Gráficos")
        setting("Resolución objetivo", listOf("Automática", "1920 × 1080", "1600 × 900", "1280 × 720", "960 × 540"), "resolution", "Automática")
        setting("Calidad", listOf("Baja", "Media", "Alta"), "quality", "Baja")
        setting("Límite de FPS", listOf("Automático", "30", "45", "60"), "fps", "30")
        note("Estos valores se guardan como preferencias. Todavía no modifican el renderizado del juego.")

        heading("Controles táctiles · diseño preliminar")
        note("Distribución de botones de mando. Aún no envía entradas al juego.")
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

        heading("Estado del motor")
        note("Wine/Hangover: pendiente de integración\nTraducción x86 → ARM64: pendiente\nDirect3D 9: pendiente\nEl juego todavía no se puede ejecutar desde esta versión.")
        actionButton("Comprobar archivos de la carpeta") {
            val uri = prefs.getString("game_folder", null)?.let(Uri::parse)
            if (uri == null) {
                Toast.makeText(this, "Primero selecciona la carpeta del juego", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Carpeta guardada. La validación completa está pendiente.", Toast.LENGTH_LONG).show()
            }
        }
        setContentView(scroll)
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
