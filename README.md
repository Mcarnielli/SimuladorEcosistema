# Simulador de Ecosistema — versión Swing (2° Instancia Evaluativa, Interfaz Gráfica)

Es el simulador de la IE1 (plantas, conejos y lobos que interactúan por turnos) convertido en una aplicación de escritorio con **Swing**. Está organizado en **MVC + DAO** y la simulación se **guarda en un archivo .txt** para poder retomarla al volver a abrir la aplicación.

## Autor

| Integrante | Rol |
|---|---|
| Matias Carnielli | Proyecto completo: diseño en Figma, modelo, DAO, controlador y vista (trabajo individual autorizado por la docente) |

## Links

- **Diseño en Figma:** https://www.figma.com/design/AV9zxMcmuW8AZZXFKSzbux/Simulador-Ecosistema-IE2 (prototipo navegable: Configuración → Simulación → Reporte)
- **Video explicativo:** PEGAR_ACÁ_EL_LINK_DEL_VIDEO

## Cómo ejecutarlo

- **JDK:** 11 o superior (probado con Temurin 21).
- **NetBeans:** Apache NetBeans 25 (también funciona en versiones más nuevas).
- **Librerías externas:** ninguna.

Pasos:

1. En NetBeans, andá a `File > Open Project` y elegí la carpeta del repositorio (es un proyecto Maven).
2. Ejecutalo con `Run Project` (F6). La clase principal es `principal.Main`.

La primera vez, Maven descarga sus plugins, así que necesita conexión a internet.

La simulación se guarda en `datos/simulacion.txt`, dentro de la carpeta del proyecto (es una ruta relativa). Si el archivo no existe, la aplicación arranca en la pantalla de configuración. Si el archivo tiene datos inválidos, avisa con un mensaje y deja empezar una simulación nueva.

## Estructura del proyecto

```
src/main/java/
├── principal/
│   └── Main.java                → crea la vista, el DAO y el controlador, los conecta y muestra la ventana
├── modelo/                      → reglas de la simulación (sin Swing, sin Scanner, sin System.out)
│   ├── Entidad, Animal          → clases abstractas (herencia)
│   ├── Planta, PlantaVenenosa, Conejo, Lobo
│   ├── Reproducible, Mortal, Peligroso  → interfaces
│   ├── Clima                    → enum con los 4 climas y sus efectos
│   ├── Ecosistema               → procesa cada turno, guarda los eventos y aplica las reglas de intervención
│   └── ReporteFinal             → datos del reporte, ya calculados por el modelo
├── dao/
│   ├── SimulacionDAO            → guarda y lee la simulación en datos/simulacion.txt
│   └── DatosInvalidosException  → avisa al controlador cuando el archivo no sirve
├── controlador/
│   ├── ControladorSimulacion    → recibe las acciones de la vista, valida y coordina el modelo y el DAO
│   ├── VistaSimulacion          → interfaz con lo que el controlador le pide a la vista
│   └── DatosSimulacion, DatosReporte → datos ya preparados para mostrar
└── vista/
    └── VentanaPrincipal (.java + .form) → JFrame armado con el diseñador de NetBeans
src/main/resources/vista/iconos/ → íconos de planta, conejo y lobo (ImageIcon)
```

### Qué hace cada capa

- **Modelo:** contiene las clases de la IE1 con su herencia, polimorfismo y encapsulamiento.
  - `procesarTurno()` aplica las reglas y **devuelve la lista de eventos** del turno, en lugar de imprimirlos como en la IE1.
  - `puedeIntervenir()`, `puedeAgregarLobo()` y `isFinalizada()` concentran las reglas del juego.
  - `generarReporteFinal()` calcula todos los datos del reporte.
- **DAO:** escribe y lee el archivo de texto.
  - Formato: una línea por registro (`GENERAL`, `HISTORIAL`, `ENTIDAD`, `EVENTO`…) y los campos separados por `|`.
  - Guarda primero en un archivo temporal y después lo reemplaza, para no perder el guardado anterior si algo falla.
  - `guardar()` devuelve `true`/`false`, y `cargar()` lanza `DatosInvalidosException` si el archivo está dañado. El DAO no muestra mensajes.
- **Controlador:** no tiene código Swing.
  - Valida los datos del formulario: campos vacíos, que sean números y que estén dentro de los rangos.
  - Le pide al modelo que procese el turno y al DAO que guarde.
  - Le pasa a la vista los datos listos para mostrar, mediante la interfaz `VistaSimulacion`.
- **Vista:** la arma el diseñador de NetBeans, con tres pantallas en un `CardLayout`: configuración, simulación y reporte.
  - Tiene una barra de menú (`JMenuBar`) con Simulación y Reportes.
  - Por código sólo carga datos en tablas, listas y combos, habilita o deshabilita componentes, cambia de pantalla y muestra mensajes con `JOptionPane`.

### Flujo al presionar «Avanzar turno»

1. Vista → `controlador.avanzarTurno()`.
2. Controlador → `ecosistema.procesarTurno()`, que devuelve los eventos.
3. Controlador → `dao.guardar(ecosistema)`, que devuelve `true`/`false`. Si devuelve `false`, llama a `vista.mostrarError(...)`.
4. Controlador → arma un `DatosSimulacion` y llama a `vista.actualizarSimulacion(...)`.
5. Si la simulación terminó, el controlador avisa y llama a `vista.mostrarPantallaReporte(...)`.

## Funcionalidades

- **Configuración:** formulario con plantas (5–30), conejos (2–15), lobos (1–5), clima y turnos (10–50). Los errores se muestran en rojo debajo del formulario, y la configuración se confirma antes de iniciar.
- **Simulación:**
  - Contadores con íconos: turno, clima, plantas, conejos y lobos.
  - Tabla de entidades vivas, donde las plantas venenosas aparecen diferenciadas.
  - Lista de eventos del turno.
- **Intervención cada 3 turnos:** cambiar el clima o agregar una entidad, con energía opcional (es la sobrecarga de `agregarEntidad`). Se permite una acción por intervención y un máximo de 5 lobos en total. El panel indica si se puede intervenir y cuándo es la próxima intervención.
- **Guardado y retoma:** se guarda automáticamente después de cada acción. Al abrir la aplicación, la simulación sigue donde estaba. «Nueva simulación» pide confirmación si hay una en curso.
- **Reporte final** en pestañas:
  - Resumen: causa de fin, turno de mayor actividad, el más longevo de cada tipo y el lobo con más cacerías.
  - Nacimientos y muertes por tipo.
  - Historial con los máximos y mínimos de cada población.
  - Entidades peligrosas ordenadas por nivel.

## Diseño en Figma (UX/UI)

**Persona y escenario.** Lucía, 20 años, estudia Biología y usa la aplicación para ver cómo cambian las poblaciones según el clima. La usa en la computadora de la facultad, en sesiones cortas, y necesita poder cerrarla y seguir otro día.

**Pantallas del diseño:**
- configuración (vacía y con errores);
- simulación (sin intervención y con la intervención habilitada);
- confirmación de una acción;
- error al cargar el archivo;
- reporte final.

**Decisiones de diseño justificadas:**

1. **Visibilidad del estado del sistema (Nielsen):** los contadores de turno, clima y poblaciones están siempre arriba, y cada acción deja un mensaje de estado abajo a la izquierda.
2. **Prevención de errores:** los rangos se muestran al lado de cada campo. Los botones de intervención se deshabilitan cuando no se puede intervenir, y las acciones importantes piden confirmación.
3. **Jerarquía visual y ley de proximidad (Gestalt):** la acción principal («Avanzar turno» o «Iniciar simulación») es el botón más grande y está abajo a la derecha. Los controles de la intervención están agrupados en un mismo panel con título.
4. **Consistencia:** las tres pantallas comparten el encabezado verde, los colores (verde para lo positivo, rojo para los errores, gris para la ayuda) y la ubicación de los botones.
5. **Reconocer antes que recordar:** se usan íconos para diferenciar plantas, conejos y lobos, y el reporte se divide en pestañas con nombres claros.

_(Si algo cambió entre el diseño y la aplicación, explicarlo acá.)_

## Desafíos encontrados

- **Sacar todo el `System.out` del modelo.** En la IE1, `Mortal.verificarMuerte()`, `Animal.moverse()` y `mostrarEstado()` imprimían por consola. Ahora devuelven texto: el `Ecosistema` guarda los eventos en una lista y el controlador se la pasa a la vista.
- **Guardar lo necesario para retomar sin perder el reporte.** Se guardan todas las entidades que existieron (incluidas las muertas, para calcular la más longeva), los contadores de nacimientos, muertes y agregados, el historial y los eventos del último turno.
- **Archivo dañado o inexistente.** El DAO valida la cabecera, la marca `FIN` y cada campo. Si algo falla, el controlador muestra el error y la aplicación sigue funcionando.
- **Que el controlador no dependa de Swing.** Se resolvió con la interfaz `VistaSimulacion`: el controlador sólo conoce esa interfaz, y `VentanaPrincipal` la implementa.

## Uso de IA / herramientas externas

- **Matias Carnielli:** el proyecto se hizo con ayuda de Claude (IA de Anthropic), que se usó para:
  - adaptar el código de la IE1 a MVC + DAO;
  - generar la vista y el archivo `.form`;
  - redactar este README.

  - generar la base de las pantallas de Figma (en SVG), que después importé, ordené, edité y conecté como prototipo.

  Revisé, ejecuté y probé el código.
  - Conversación completa: PEGAR_ACÁ_EL_LINK
