# FIFA13Android

Proyecto Android experimental para preparar una aplicación contenedora de compatibilidad para una instalación legítima de FIFA 13 PC en dispositivos Android.

## Estado actual (0.2.0)

Esta versión incluye:
- Aplicación Android nativa en Kotlin.
- Selector de carpeta mediante el selector de documentos de Android.
- Búsqueda real de `fifa13.exe` en la carpeta seleccionada y subcarpetas (búsqueda limitada en profundidad y número de elementos).
- Detección informativa de versión de Android, ABI, OpenGL ES y disponibilidad de la característica Vulkan.
- Preferencias de resolución, calidad y FPS.
- Diseño preliminar de controles táctiles.
- Flujo de GitHub Actions para compilar un APK de depuración.\n- Puente opcional para abrir Winlator si ya está instalado en el dispositivo.\n- Perfil inicial documentado para Samsung Galaxy A52 5G con 6 GB.

**Limitación importante:** el puente abre Winlator como aplicación separada; NO integra su motor dentro de nuestro APK ni inicia FIFA 13 automáticamente. Esta versión todavía NO incluye Wine/Hangover, traducción x86 de 32 bits a ARM64, Direct3D 9 ni controles conectados al juego. Los ajustes gráficos aún se guardan como preferencias y no cambian el renderizado. El botón «JUGAR A FIFA 13» permanece desactivado hasta validar una ruta de ejecución real.

## Estado de compatibilidad A52 5G\n\nConsulta [docs/A52_5G_COMPATIBILITY.md](docs/A52_5G_COMPATIBILITY.md) para el perfil inicial y el bloqueo pendiente de ejecutar aplicaciones Windows x86 de 32 bits.\n\n## Arquitectura prevista

El objetivo inicial es Android ARM64. La compatibilidad real requerirá seleccionar y validar un runtime Wine/Hangover adecuado para Android, una solución de ejecución x86 de 32 bits sobre ARM64 y una ruta gráfica Direct3D 9 compatible con los controladores del dispositivo. No se da por compatible ninguna combinación hasta compilarla y probarla en móviles reales.

## Requisitos para compilar

- Android Studio reciente y JDK 17.
- Gradle 8.9 (configurado en GitHub Actions).
- Una copia legítima de FIFA 13 PC. Los archivos del juego no se incluyen en este repositorio ni en el APK.

## Compilar localmente

Abre el repositorio en Android Studio y espera a que Gradle sincronice. También puedes compilar con Gradle 8.9 y JDK 17:

```bash
gradle assembleDebug
```

El APK de depuración se genera en:

```
app/build/outputs/apk/debug/app-debug.apk
```

## Compilación en GitHub

Cada push a `main` ejecuta GitHub Actions. El APK se publica como artefacto descargable de la ejecución.

## Próximas etapas

1. Confirmar la detección de la instalación con una carpeta real del usuario.
2. Diseñar y probar la capa de entrada táctil.
3. Evaluar runtimes y traductores ARM64/x86 con soporte Android real.
4. Integrar y validar una ruta Direct3D 9.
5. Probar estabilidad y rendimiento en distintos dispositivos.
