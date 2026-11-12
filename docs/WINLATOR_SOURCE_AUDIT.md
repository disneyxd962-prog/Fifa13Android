# Auditoría de integración de Winlator WB64

Fecha de revisión: 2026-11-12.

## Resultado

El repositorio WB64 no es una biblioteca Android que se pueda añadir con una línea de Gradle. Es otra aplicación Android completa, con su propio identificador de paquete (`com.winlator`), actividades, instalador de sistema de archivos Linux y código nativo CMake.

Fuentes revisadas:
- https://github.com/winebox64/winlator-wb64
- https://github.com/winebox64/winlator-wb64/blob/main/app/build.gradle
- https://github.com/winebox64/winlator-wb64/blob/main/app/src/main/AndroidManifest.xml
- https://github.com/winebox64/winlator-wb64/blob/main/app/src/main/cpp/CMakeLists.txt

## Qué implica para FIFA 13

El README de WB64 describe el proyecto como entorno para aplicaciones Windows x86_64 con Wine y Box86/Box64. FIFA 13 PC es una aplicación Windows x86 de 32 bits. La documentación consultada no demuestra que esta variante concreta pueda ejecutar ese juego en el Galaxy A52 5G.

Por eso no se debe conectar el botón JUGAR ni afirmar que el runtime está integrado hasta validar:
1. Una ruta real de ejecución de Windows x86 de 32 bits en ARM64.
2. Wine y sus componentes nativos en Android.
3. Direct3D 9 y el controlador gráfico del dispositivo.
4. Audio, entrada táctil, ventana y almacenamiento.
5. Una prueba reproducible con una aplicación x86 de prueba y, después, FIFA 13.

## Por qué no se ha copiado el proyecto WB64 dentro de esta app

Copiar su carpeta `app` o añadirla como dependencia no produce un APK único: hay que fusionar manifiestos y recursos, adaptar el ciclo de vida de las actividades, integrar CMake y las bibliotecas nativas, preparar los componentes del entorno Linux y revisar las licencias de cada componente. Además, la variante WB64 revisada no demuestra por sí sola el requisito x86 de 32 bits.

Una integración incompleta podría generar un APK que instala pero falla al iniciar el juego. Se mantiene el puente externo como opción de prueba, claramente separado del runtime integrado.

## Estado en este repositorio

- La app principal sigue siendo un launcher experimental.
- El puente externo solo abre Winlator si está instalado.
- No se distribuyen archivos de FIFA 13.
- El botón de juego permanece desactivado.
- No se afirma que Wine, Box86/Box64, DXVK o Direct3D 9 estén integrados en este APK.

## Siguiente hito técnico verificable

Elegir una base de runtime Android que demuestre soporte para Windows x86 de 32 bits en ARM64, fijar sus versiones y licencias, y construir primero una prueba mínima independiente. Solo después se debe portar ese runtime al APK de FIFA13Android.
