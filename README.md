# FIFA13Android

Proyecto Android para preparar una aplicación contenedora de compatibilidad para ejecutar una instalación legítima de FIFA 13 PC en dispositivos Android ARM64.

## Estado actual

Esta primera base incluye:
- Aplicación Android nativa en Kotlin.
- Selector de carpeta para localizar una instalación del juego.
- Panel inicial de ajustes de resolución, calidad y FPS objetivo.
- Controles táctiles de estilo mando para la futura capa de entrada.
- Flujo de GitHub Actions para compilar un APK de depuración.

**Importante:** esta versión es una base de interfaz y configuración. Todavía no incluye Wine/Hangover, traducción x86 a ARM64 ni traducción Direct3D 9 funcional, por lo que aún no ejecuta FIFA 13. Esos componentes requieren integración y pruebas específicas en Android; no basta con añadir sus nombres o archivos al APK.

## Requisitos

- Android Studio reciente y JDK 17 para compilar localmente.
- Android ARM64 para las futuras pruebas de compatibilidad.
- Una copia legítima de FIFA 13 PC. Los archivos del juego no se incluyen en este repositorio.

## Compilar localmente

Abre el repositorio en Android Studio y espera a que Gradle sincronice. También puedes compilar con Gradle 8.13 y JDK 17:

```bash
gradle assembleDebug
```

El APK de depuración se genera en:

```
app/build/outputs/apk/debug/app-debug.apk
```

## Compilación en GitHub

Cada push a `main` ejecuta el flujo de GitHub Actions. El APK se publica como artefacto descargable de la ejecución.

## Próximas etapas

1. Probar selector y detección de archivos sin modificar la instalación.
2. Conectar los controles táctiles a una capa de entrada Android.
3. Diseñar la integración ARM64/x86 y Direct3D 9.
4. Probar compatibilidad y rendimiento en dispositivos reales.
