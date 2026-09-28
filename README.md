# Simulador de Ecosistema — 1° Instancia Evaluativa (Interfaz Gráfica)

Simulación por turnos en consola, hecha en Java, con plantas, conejos y lobos. El jugador configura el ecosistema inicial y cada 3 turnos puede intervenir: cambiar el clima o agregar entidades.

## Integrantes y rol de cada uno

| Integrante | Parte a cargo | Archivos |
|---|---|---|
| _(Nombre 1)_ | Jerarquía base e interfaces | `Entidad`, `Animal`, `Reproducible`, `Mortal`, `Peligroso`, `Clima` |
| _(Nombre 2)_ | Entidades concretas | `Planta`, `PlantaVenenosa`, `Conejo`, `Lobo` |
| _(Nombre 3)_ | Lógica del ecosistema | `Ecosistema` (turno, agregarEntidad sobrecargado, colapso) |
| _(Nombre 4)_ | Consola y reporte | `Simulador`, `Main`, `generarReporteFinal()` + estadísticas bonus |

> Completen la tabla con los nombres reales. Cada integrante tiene que hacer commits sobre sus archivos: si alguien no tiene commits, pierde el 50% de la nota.

## Cómo ejecutarlo

**Requisitos:** JDK 11 o superior (probado con JDK 21) y Apache NetBeans 17 o superior. No usa librerías externas.

**En NetBeans:** `File > Open Project` → elegir la carpeta `SimuladorEcosistema` (NetBeans la reconoce como proyecto Maven) → `Run Project` (F6). La clase principal es `simulador.Main`.

**Por consola:**
```bash
mvn compile exec:java
# o sin Maven:
javac -d out $(find src -name "*.java")
java -cp out simulador.Main
```

## Estructura del proyecto

```
src/main/java/simulador/
├── Main.java                 → punto de entrada
├── Simulador.java            → menús con Scanner, loop principal e intervenciones
├── ecosistema/
│   └── Ecosistema.java       → listas de entidades, procesarTurno(), estadísticas y reporte
├── interfaces/
│   ├── Reproducible.java     → reproducirse(), puedeReproducirse(), default intentarReproduccion()
│   ├── Mortal.java           → estaVivo(), morir(), default verificarMuerte()
│   └── Peligroso.java        → getNivelPeligro()   (BONUS)
└── modelo/
    ├── Clima.java            → enum con los 4 climas y sus efectos
    ├── Entidad.java          → clase abstracta base
    ├── Animal.java           → clase abstracta intermedia (moverse())
    ├── Planta.java           → extends Entidad implements Reproducible, Mortal
    ├── PlantaVenenosa.java   → extends Planta implements Peligroso (BONUS)
    ├── Conejo.java           → extends Animal implements Reproducible
    └── Lobo.java             → extends Animal implements Peligroso
documentacion/                → capturas / links de prompts (entrega individual)
```

## Orden de cada turno (`Ecosistema.procesarTurno()`)

1. **Plantas:** hacen fotosíntesis (+8) y se reproducen si tienen energía ≥ 40. La probabilidad base es 50% y se multiplica según el clima (en Invierno no se reproducen).
2. **Conejos:** se aplica el efecto del clima, comen una planta viva al azar si tienen hambre (energía < 50) y, si no encuentran, pierden 15. Después intentan reproducirse: necesitan energía > 60 y otro conejo vivo.
3. **Lobos:** intentan cazar un conejo al azar. **La probabilidad crece con la energía:** `0.05 + (energia/100) * 0.45`, con +20% en Invierno y limitada entre 5% y 90%.
4. **Todas envejecen** (`envejecer()`: edad +1 y se descuenta la energía base de cada tipo).
5. **Mueren las que quedaron sin energía** (`verificarMuerte()`, el método default de `Mortal`).
6. Se muestra el estado. Cada 3 turnos aparece el menú de intervención.

## Cómo se cumplen los requisitos de POO

| Requisito | Dónde |
|---|---|
| Entidad abstracta con métodos abstractos sobreescritos | `Entidad.actuar()`, `mostrarEstado()`, `getTipo()` → sobreescritos en Planta, Conejo y Lobo |
| Animal como capa intermedia con método concreto | `Animal.moverse()`, usado por Conejo y Lobo |
| Reproducible en Planta y Conejo, con polimorfismo | `Ecosistema.getReproducibles()` arma un `ArrayList<Reproducible>` con plantas y conejos y lo recorre una sola vez en `mostrarEstado()`. Además, `intentarReproduccion()` (default) se llama desde `actuar()` |
| Mortal con método default aprovechado | `verificarMuerte()` se usa en el paso 5 sobre un `ArrayList<Mortal>`. Planta lo sobreescribe para cambiar el mensaje |
| Encapsulamiento | Todos los atributos son `private` y los setters validan: energía entre 0 y 100, tamaño entre 1 y 5, edad ≥ 0, velocidad ≥ 1, peso > 0 |
| Sobrecarga | `Ecosistema.agregarEntidad(String tipo)` y `agregarEntidad(String tipo, double energiaInicial)`. Las dos se usan desde el menú de intervención |
| Colecciones | `ArrayList`, `LinkedHashMap` (contadores por tipo), historial `ArrayList<int[]>` |

### Bonus implementados
- **PlantaVenenosa:** el 15% de las plantas que se crean son venenosas. Se guardan en el mismo `ArrayList<Planta>` y el conejo la elige sin saberlo. `serComida()` devuelve −30.
- **Estadísticas en tiempo real:** historial de población turno a turno, con el máximo y el mínimo de cada población en el reporte final.
- **Interface Peligroso:** la implementan Lobo (nivel = 5 + 2×cacerías + energía/25) y PlantaVenenosa (nivel 3). El reporte las lista ordenadas de mayor a menor.

### Climas
Están implementados los 4 climas de la consigna (se pedía un mínimo de 2), en el enum `Clima`.

## Decisiones de diseño y desafíos encontrados

- **Colapso inmediato:** en las primeras pruebas el ecosistema colapsaba en 3 turnos, porque cada conejo comía una planta por turno y cada lobo cazaba con mucha probabilidad. Lo resolvimos así:
  - los conejos sólo comen si tienen hambre (energía < 50);
  - se bajó la probabilidad de caza;
  - se agregó una capacidad máxima del terreno: 40 plantas y 30 conejos.

  Todos los valores son constantes `public static final`, así que se pueden ajustar fácilmente.
- **Modificar una lista mientras se recorre:** cuando nace una entidad durante el recorrido se produce una `ConcurrentModificationException`. Por eso se recorre una copia (`new ArrayList<>(plantas)`) y las entidades muertas se eliminan al final del turno (`limpiarMuertos()`).
- **Límite de 5 lobos:** se cuentan todos los lobos creados en la simulación (los iniciales más los agregados), no sólo los vivos.
- **Entidad más longeva:** se guardan todas las entidades que existieron (`todasLasEntidades`), así el reporte puede considerar también las que murieron.
- En Sequía e Invierno el ecosistema colapsa rápido si no se interviene. Es intencional: son los climas difíciles.

## Uso de IA / herramientas externas

> **Obligatorio y de cada integrante.** Cada uno pega acá el link a sus conversaciones completas con la IA y/o las páginas que consultó, o deja las capturas en `documentacion/` con su nombre.

- _(Nombre 1)_: link a la conversación / páginas consultadas
- _(Nombre 2)_: …
- _(Nombre 3)_: …
- _(Nombre 4)_: …
