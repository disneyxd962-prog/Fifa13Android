# Integración del motor de compatibilidad

## Estado actual

La aplicación Android selecciona una carpeta mediante Storage Access Framework y localiza `fifa13.exe`. Eso no ejecuta el archivo: Android no puede iniciar directamente un ejecutable PE de Windows.

El runtime todavía NO está incluido. No se debe habilitar el botón de juego hasta superar las comprobaciones de esta guía.

## Arquitectura necesaria

Para ejecutar una instalación legítima de FIFA 13 PC en Android ARM64 hacen falta, como mínimo:

1. **Wine para ARM64**, con soporte WoW64 adecuado.
2. **Traducción de código x86 de 32 bits a ARM64** (por ejemplo, la ruta Box32/Box64 o una integración Hangover compatible).
3. **Gráficos Direct3D 9** mediante WineD3D/OpenGL o una ruta DXVK/Vulkan compatible con el dispositivo.
4. **Servidor gráfico y entrada** integrados con la ventana Android, más audio y mandos.
5. **Acceso al contenido del juego**. La URI SAF seleccionada no equivale a una ruta POSIX que Wine pueda abrir; habrá que copiar o exponer el contenido de forma segura al entorno Linux, conservando los permisos y evitando modificar el original.

## Evaluación de proyectos existentes

- Winlator es una aplicación Android que combina Wine y Box86/Box64, con controladores gráficos y componentes adicionales. Su código fuente puede servir como referencia de integración Android: https://github.com/winebox64/winlator
- Hangover ejecuta aplicaciones Windows x86/x86_64 sobre Wine ARM64 en Linux. No es una biblioteca Android lista para añadir como dependencia Gradle: https://github.com/AndreRH/hangover
- Box64 documenta distintas rutas para ejecutar Wine de 32 bits y 64 bits en ARM64; algunas opciones siguen siendo experimentales: https://github.com/ptitSeb/box64

No se deben descargar binarios aleatorios ni asumir que un paquete Linux funciona dentro de una app Android. Antes de redistribuir cualquier runtime hay que revisar sus licencias, avisos y obligaciones de código fuente.

## Fases de integración

### Fase 1 — Inventario del dispositivo
Registrar ABI, versión de Android, versión real de Vulkan/OpenGL, memoria disponible y espacio libre. La presencia de Vulkan no garantiza que el controlador tenga las extensiones necesarias.

### Fase 2 — Runtime ARM64
Elegir una distribución reproducible de Wine ARM64 y su traductor x86 de 32 bits. Fijar versiones y hashes de los componentes. No incluirlos en el APK hasta que la compilación Android y las licencias estén verificadas.

### Fase 3 — Gráficos y ventana
Probar primero una aplicación Direct3D 9 mínima. Elegir WineD3D o DXVK según las capacidades reales del dispositivo. Integrar la superficie gráfica con Android.

### Fase 4 — Entrada, audio y almacenamiento
Conectar controles táctiles y mando físico, audio, ciclo de vida Android y acceso al directorio del juego.

### Fase 5 — Prueba del juego
Probar el arranque, menú, partido, audio, controles y estabilidad. Registrar errores y rendimiento antes de activar el botón «JUGAR».

## Criterio para habilitar «JUGAR A FIFA 13»

El botón debe permanecer desactivado hasta que estén presentes el runtime y sus manifiestos, el proceso Wine pueda iniciarse, la ruta gráfica funcione y una prueba real valide la ejecución de una aplicación Windows de 32 bits. La detección de `fifa13.exe` por sí sola nunca será suficiente.
