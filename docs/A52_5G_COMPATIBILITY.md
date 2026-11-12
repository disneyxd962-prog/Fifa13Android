# Perfil de compatibilidad: Samsung Galaxy A52 5G (6 GB)

## Evaluación inicial

El Galaxy A52 5G se comercializó con Snapdragon 750G y GPU Adreno 619. Esta plataforma supera sobre el papel el mínimo publicado por Winlator WB64 (Snapdragon 460 o superior, Adreno 610 o superior y Vulkan 1.1 o superior).

Referencia de requisitos de Winlator WB64:
https://github.com/winebox64/winlator

Esto solo indica que el dispositivo está dentro de la clase de hardware objetivo. No garantiza compatibilidad ni una tasa de imágenes concreta.

## Perfil inicial conservador

- Resolución de prueba: 960 × 540.
- Calidad: baja.
- Límite objetivo: 30 FPS.
- Mantener margen de memoria para Android y evitar reservar RAM fija.
- Probar primero un programa Windows sencillo y después una aplicación Direct3D 9.

## Bloqueo técnico que falta resolver

FIFA 13 para PC es una aplicación Windows de 32 bits. La variante Winlator WB64 enlazada en este proyecto se describe como entorno para aplicaciones x86_64. Por tanto, no debemos asumir que esa variante ejecutará FIFA 13 de 32 bits. Antes de integrarla como runtime único hay que seleccionar y probar una distribución que incluya una ruta x86 de 32 bits compatible (por ejemplo, WoW64/Box32/Hangover, según versiones y soporte real).

## Estado del proyecto

- [x] Perfil gráfico conservador guardado en la app.
- [x] Puente opcional para abrir Winlator instalado por separado.
- [ ] Seleccionar runtime Android ARM64 con soporte probado para Windows x86 de 32 bits.
- [ ] Integrar y compilar los componentes nativos con sus licencias y avisos.
- [ ] Ejecutar una aplicación Windows x86 de prueba.
- [ ] Validar Direct3D 9, audio, controles y almacenamiento.
- [ ] Probar FIFA 13 con una copia legítima del usuario.
- [ ] Solo entonces activar el botón de juego.

## Por qué no se empaqueta un runtime descargado al vuelo

El runtime incluye varios componentes nativos, bibliotecas gráficas y un root filesystem. No es seguro ni técnicamente correcto descargar un ZIP cualquiera, renombrarlo como Wine o copiar binarios Linux genéricos a una APK Android. La integración debe fijar versiones, hashes, ABI, licencias y pruebas reproducibles.
