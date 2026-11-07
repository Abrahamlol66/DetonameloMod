# MoreUsefulEnchants

Mod de Fabric para Minecraft Java Edition **26.2** que agrega once encantamientos:

- **Detonate** (morado, nivel máximo 1, picos y palas): al minar un bloque, también
  se rompen los bloques del mismo tipo en un prisma 3x3x2 alrededor (el plano 3x3
  perpendicular a la cara que se minó, repetido 1 capa más hacia adentro). Fortuna,
  Toque de seda, Eficiencia, Irrompibilidad y Reparación funcionan normalmente en
  cada bloque extra (se rompen con las mecánicas vanilla de verdad, no son copias
  simuladas).
- **Treecapitator** (verde, nivel máximo 1, hachas): al minar un tronco, se tala el
  árbol completo (troncos conectados + hojas naturales cercanas) y se replanta
  automáticamente un brote del mismo tipo de árbol en la base.
- **Veinminer** (azul claro / `aqua`, nivel máximo 1, picos): al minar una mena de
  mineral (carbón, cobre, diamante, esmeralda, oro, hierro, lapislázuli, redstone,
  cuarzo del Nether o ancient debris), se minan también todas las menas del mismo
  tipo conectadas por cara a esa.
- **Auto-Smelt / Fundición** (`#FF8C1A`, nivel máximo 1, picos y palas): cada drop con
  receta de fundición se reemplaza por su resultado (misma cantidad), dando también la
  XP de esa receta. Aplica a cada bloque roto por Detonate y Veinminer también.
- **Replenish / Replantar** (`#55C24A`, nivel máximo 1, azadas): al romper un cultivo
  maduro, descuenta 1 semilla de los drops (o del inventario) y lo vuelve a plantar en
  edad 0. Romper un cultivo no maduro se cancela, salvo agachado.
- **Harvest / Cosecha** (`#E3B23C`, nivel máximo 3, azadas): al romper un cultivo
  maduro, cosecha también los cultivos maduros en un área horizontal (3x3 / 5x5 / 7x7
  según el nivel). Se detiene antes de romper la azada; agacharse desactiva el área.
- **Telekinesis / Telequinesis** (`#B266FF`, nivel máximo 1, herramientas de minería y
  armas cuerpo a cuerpo): los drops de bloques (incluidos los extra de Detonate,
  Veinminer, Treecapitator y Harvest) y los drops + XP de mobs van directo al
  inventario; lo que no cabe cae en la posición del jugador.
- **Lifesteal / Robavidas** (`#C2185B`, nivel máximo 3, espadas y hachas, encantamiento
  de tesoro): al golpear con un ataque cargado, cura 10/15/20% del daño infligido según
  el nivel, máximo 4 de vida por golpe.
- **Head Thief / Ladrón de cabezas** (`#E0D6B8`, nivel máximo 3, espadas y hachas): al
  matar, probabilidad de soltar la cabeza del mob (zombi, esqueleto, creeper, piglin,
  jugador), +2% por nivel de Saqueo. Compatible con Telekinesis.
- **Max Health / Vida máxima** (`#FF6B81`, nivel máximo 5, pecheras, encantamiento de
  tesoro): +2 corazones de vida máxima por nivel (nivel V = +10 corazones, 40 de vida
  máxima en total). 100% data-driven (efecto `minecraft:attributes` sobre
  `minecraft:max_health`, sin código Java): al quitarse la pechera la vida se ajusta
  sola al máximo normal, igual que vanilla con cualquier otro atributo. Comerciable con
  bibliotecarios (precio doble) y aparece en botín de cofres. Compatible con
  Protección y el resto de encantamientos de armadura.
- **Lightning / Relámpago** (`#FFE14D`, nivel máximo 3, espadas y hachas): al golpear
  con un ataque cargado, probabilidad de que caiga un rayo sobre el objetivo (10/20/30%
  según el nivel). Funciona con cualquier clima, a diferencia de Canalización. El rayo
  es puramente visual: el daño, el fuego y las conversiones (creeper cargado, aldeano
  convertido en bruja, etc.) se aplican manualmente solo al objetivo, así que nunca
  daña al jugador que ataca ni prende fuego a los bloques de alrededor. Máximo 1 rayo
  por objetivo por segundo. Compatible con Telekinesis, Head Thief, Lifesteal, Saqueo y
  Aspecto ígneo.

Detonate, Treecapitator y Veinminer son mutuamente excluyentes entre sí. Auto-Smelt es
incompatible solo con Toque de Seda (sigue siendo compatible con Fortuna). El resto no
tiene restricciones de exclusividad entre sí y todos siguen siendo compatibles con los
demás encantamientos normales de su herramienta (Fortuna, Toque de seda, Eficiencia,
Irrompibilidad, Reparación, Saqueo).

## Libro encantado propio del mod

Cualquier libro encantado que tenga al menos un encantamiento de este mod es, en
realidad, el ítem `moreusefulenchants:enchanted_book` (no `minecraft:enchanted_book`),
para que REI, EMI, JEI y cualquier otro visor de recetas lo muestren con el
namespace/badge de "MoreUsefulEnchants" en vez de "Minecraft". Un libro que solo tenga
encantamientos vanilla sigue siendo el libro de siempre.

El libro del mod copia exactamente el comportamiento del libro encantado vanilla
(mismos encantamientos guardados, mismo brillo, misma rareza, pila de 1, mismo tooltip,
mismo nombre, y reutiliza la textura vanilla desde su modelo de ítem sin copiar el
archivo) y participa igual que él en:

- **Yunque**: aplicar el libro a una herramienta/arma/armadura soportada, y combinar
  dos libros entre sí (incluyendo mod + vanilla). El resultado sigue la regla general:
  si queda con algún encantamiento del mod, es el libro del mod; si no, es el vanilla.
- **Piedra de afilar**: quitarle los encantamientos (menos maldiciones) devuelve un
  libro normal con la misma XP que con el libro vanilla.
- **Mesa de encantamiento**: encantar un libro normal da el libro del mod si sale algún
  encantamiento del mod.
- **Aldeanos bibliotecarios**: los intercambios de "esmeralda + libro" que salgan con un
  encantamiento del mod entregan el libro del mod.
- **Botín de cofres, pesca** y cualquier otra loot table que encante libros al azar o
  con un encantamiento fijo.
- **Libreros cincelados**: lo aceptan igual que a cualquier otro libro (está en el tag
  `minecraft:bookshelf_books`).
- **Pestaña creativa**: la pestaña propia de MoreUsefulEnchants tiene un libro del mod
  por cada nivel de cada encantamiento del mod; esos mismos libros ya no aparecen
  duplicados (con el namespace vanilla) en la pestaña de Ingredientes.
- **Migración**: un libro vanilla guardado de una partida anterior con encantamientos
  del mod se convierte solo al libro del mod la primera vez que entra al inventario del
  jugador o se abre un contenedor (cofre, barril, shulker box, etc.) que lo contenga —
  sin revisar inventarios cada tick.

Nota de adaptación: en esta versión los atriles (`lectern_books`) nunca aceptan libros
encantados (solo libros normales/escritos), y Fabric API 0.161.0+26.2 no trae tags
convencionales `c:` para libros — solo existe el tag vanilla `bookshelf_books` — así que
no hizo falta tocar ninguno de los dos.

## Compilar

Requiere **Java 25** (coincide con lo que pide Minecraft 26.2 y Fabric Loom
1.19-SNAPSHOT). El proyecto incluye el Gradle Wrapper, no hace falta instalar Gradle.

```bash
./gradlew build
```

El jar queda en `build/libs/moreusefulenchants-1.0.0.jar`.

Para probar en un cliente de desarrollo (descarga assets, puede tardar la primera vez):

```bash
./gradlew runClient
```

## Instalar

1. Instala [Fabric Loader](https://fabricmc.net/use/) para Minecraft 26.2.
2. Copia a la carpeta `mods` de tu instalación:
   - `moreusefulenchants-1.0.0.jar` (este mod)
   - [Fabric API](https://modrinth.com/mod/fabric-api) `0.161.0+26.2` o compatible
     (es una dependencia obligatoria, declarada en `fabric.mod.json`).
3. Lanza Minecraft 26.2 con el perfil de Fabric Loader.

## Versiones usadas

Sacadas directamente de la rama `26.2` oficial de
[fabric-example-mod](https://github.com/FabricMC/fabric-example-mod/tree/26.2)
(no se inventó ningún número de versión):

| Componente     | Versión          |
|----------------|------------------|
| Minecraft      | 26.2             |
| Fabric Loader  | 0.20.1           |
| Fabric Loom    | 1.19-SNAPSHOT    |
| Fabric API     | 0.161.0+26.2     |
| Java           | 25               |

Desde la 26.1, Minecraft ya no viene ofuscado: el mod usa directamente los nombres
oficiales de Mojang (`ServerPlayer`, `ServerLevel`, `BlockPos`, `ItemStack`, etc.),
sin mappings de Yarn.

## Nota sobre el color del tooltip

El esquema de `description` en 26.2 permite poner el color directamente en el JSON
del encantamiento (`{"translate": "...", "color": "dark_purple"}` / `"green"` / `"aqua"`).
Se revisó el bytecode de `Enchantment.getFullname` / `ComponentUtils.mergeStyles` en
el jar de Minecraft 26.2: cuando la descripción ya trae un color explícito, ese color
tiene prioridad sobre el gris que vanilla aplicaría por defecto a los encantamientos
sin maldición. Es decir, **no hace falta un mixin** para que Detonate se vea morado,
Treecapitator verde y Veinminer azul claro; de todas formas, confírmalo tú mismo en el
juego (ver checklist). Si en tu partida ves el nombre en gris, es señal de que algo más
(otro mod, un resource pack) está sobrescribiendo el estilo, no un problema de este mod.

Nota: los 16 colores de texto de Minecraft (`ChatFormatting`) no incluyen un "light_blue"
literal (ese es un color de tinte/lana, no de texto); el más cercano a "azul claro" es
`aqua` (celeste/cian claro), que es el que se usó.

## Checklist de pruebas manuales

- [ ] `/enchant @s moreusefulenchants:detonate` funciona en un pico y en una pala en la mano
      principal, y falla (mensaje de error) en una espada o en un hacha.
- [ ] `/enchant @s moreusefulenchants:treecapitator` funciona en un hacha, y falla en un pico.
- [ ] `/enchant @s moreusefulenchants:veinminer` funciona en un pico, y falla en una pala o un hacha.
- [ ] El tooltip de Detonate se ve en **morado** (`dark_purple`), el de Treecapitator en
      **verde** (`green`) y el de Veinminer en **azul claro** (`aqua`), no en gris.
- [ ] Detonate y Treecapitator son **incompatibles entre sí**: no se pueden combinar
      en la misma herramienta (ni en el yunque ni en modo creativo).
- [ ] Veinminer sí se puede combinar con Detonate en el mismo pico (junto con Fortuna
      o Toque de seda si quieres).
- [ ] Un pico con Detonate + Fortuna y otro pico con Detonate + Toque de seda se
      pueden combinar en el yunque (ambos libros se dejan aplicar junto con Detonate).
      Fortuna aumenta los drops de los bloques extra igual que en el bloque central;
      Toque de seda hace que los bloques extra dropen el bloque en sí.
  - Fortuna y Toque de seda siguen siendo **incompatibles entre sí** (como en vanilla):
    no se pueden combinar en el mismo yunque.
- [ ] Detonate/Treecapitator/Veinminer + Eficiencia + Irrompibilidad + Reparación
      conviven sin problema en la misma herramienta.
- [ ] En modo creativo o combinando con un libro encantado en el yunque, Detonate NO se
      deja poner en una espada ni Veinminer en un hacha (a diferencia de los
      encantamientos vanilla, que sí se pueden "forzar" así).
- [ ] Minar con Detonate rompe un prisma 3x3x2 (3 de ancho, 3 de alto, 2 de profundidad)
      del mismo tipo de bloque alrededor del bloque minado, centrado en el plano
      perpendicular a la cara que se minó y extendido 1 capa hacia adentro.
- [ ] Al minar con Detonate, solo se rompen bloques del **mismo tipo** que el central
      (p. ej. piedra con piedra alrededor: el pasto, tierra, cobblestone, andesita,
      deepslate o menas vecinas no se ven afectadas).
- [ ] En modo creativo, Detonate rompe el área sin generar drops (igual que vanilla).
- [ ] Si la herramienta con Detonate se rompe a la mitad del área, el resto de los
      bloques se queda sin minar.
- [ ] Detonate no hace nada si la herramienta encantada está en la mano **secundaria**.
- [ ] Treecapitator tala un árbol completo (troncos + hojas conectadas) de un solo
      golpe, suelta todos los drops normales (incluye manzanas/brotes con sus
      probabilidades vanilla) y replanta un brote del mismo tipo en la base.
- [ ] Al talar un árbol 2x2 (roble oscuro, pale oak, abeto gigante o jungla gigante),
      Treecapitator replanta los 4 brotes en la base para que el árbol pueda volver a
      crecer igual.
- [ ] Treecapitator sobre una "casa de troncos" (sin hojas naturales pegadas) solo
      rompe el tronco individual, sin talar nada extra.
- [ ] En un bosque denso con árboles muy pegados, Treecapitator en un árbol NO le quita
      las hojas a los árboles vecinos (solo las que realmente están más cerca de su
      propio tronco).
- [ ] Si el hacha con Treecapitator se rompe a la mitad de la tala, se detiene el
      resto del árbol.
- [ ] Veinminer mina toda la veta conectada (por cara) de un mismo mineral (p. ej. una
      veta de hierro completa de un solo golpe), aplicando Fortuna/Toque de
      seda/experiencia/desgaste normalmente en cada mena extra.
- [ ] Veinminer no se extiende a una mena de **otro** mineral, aunque esté pegada
      (p. ej. hierro pegado a cobre: solo se mina el hierro).
- [ ] Si el pico con Veinminer se rompe a la mitad de la veta, se detiene el resto.
- [ ] `./gradlew build` compila sin errores y `./gradlew runClient` carga el juego sin
      errores del mod en el log (encantamientos registrados, JSON válidos).
- [ ] `/enchant @s moreusefulenchants:max_health 5` en una pechera da +10 corazones
      (40 de vida máxima en total); al quitarte la pechera vuelve a 20.
- [ ] `/enchant @s moreusefulenchants:max_health` falla en un casco/pantalones/botas.
- [ ] Max Health no sale en la mesa de encantamiento (es de tesoro), pero sí aparece
      como intercambio de un bibliotecario (precio doble de esmeraldas) y puede salir
      en botín de cofres.
- [ ] `/enchant @s moreusefulenchants:lightning 3` en una espada o hacha: al golpear
      cargado (soltar el click después de esperar), ~30% de las veces cae un rayo
      visual sobre el mob golpeado, con sonido, en cualquier clima (prueba en un día
      despejado). El rayo daña/incendia/convierte (creeper cargado, aldeano → bruja)
      solo al objetivo: el jugador que ataca no recibe daño ni se prende fuego, y los
      bloques alrededor del objetivo no se incendian.
- [ ] Lightning no cae más de una vez por segundo sobre el mismo objetivo aunque lo
      golpees varias veces rápido.
- [ ] Un libro encantado con Max Health o Lightning (desde la pestaña creativa del
      mod, un bibliotecario o la mesa de encantamiento) se ve como
      `moreusefulenchants:enchanted_book` en REI/EMI/JEI al buscar `@moreusefulenchants`
      (no como `minecraft:enchanted_book`), con el mismo tooltip y brillo que un libro
      vanilla.
- [ ] Ese libro del mod se puede aplicar a una herramienta/arma/armadura soportada en
      el yunque, y se puede combinar con otro libro (del mod o vanilla) en el yunque.
- [ ] Vaciar ese libro en la piedra de afilar devuelve un libro normal (no el libro del
      mod vacío) con la XP correspondiente.
- [ ] La pestaña creativa de MoreUsefulEnchants tiene un libro del mod por cada nivel
      de Max Health y Lightning (y de los demás encantamientos del mod); esos libros no
      aparecen duplicados con el namespace vanilla en la pestaña de Ingredientes.
- [ ] Un libro vanilla con un encantamiento del mod (por ejemplo colocado a mano con
      `/give` usando `minecraft:enchanted_book` + componentes) se convierte al libro del
      mod al recogerlo del suelo o al abrir el cofre/barril/shulker box donde esté.
