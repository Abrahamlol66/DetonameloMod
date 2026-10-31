# MoreUsefulEnchants

Mod de Fabric para Minecraft Java Edition **26.2** que agrega nueve encantamientos:

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

Detonate, Treecapitator y Veinminer son mutuamente excluyentes entre sí. Auto-Smelt es
incompatible solo con Toque de Seda (sigue siendo compatible con Fortuna). El resto no
tiene restricciones de exclusividad entre sí y todos siguen siendo compatibles con los
demás encantamientos normales de su herramienta (Fortuna, Toque de seda, Eficiencia,
Irrompibilidad, Reparación, Saqueo).

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

## Nota conocida: badge de mod en REI

Si usas [REI](https://modrinth.com/mod/roughly-enough-items), los tres encantamientos
aparecen con el badge/namespace de "Minecraft" en vez de "MoreUsefulEnchants" al buscarlos o al
filtrar con `@`. Esto es un límite real de REI, no un bug del mod ni algo arreglable con un
plugin de integración: la `ResourceKey` y los tags de los tres encantamientos sí usan el
namespace `moreusefulenchants` correctamente (confirmado revisando el bytecode de
`Enchantment`/tags en el jar de 26.2).

Se investigó el jar real de REI 26.2 (`me.shedaniel.rei.api.common.entry.type.EntryDefinition`
y `ModArgumentType`, que implementa el filtro `@modid`): el namespace que REI le asigna a una
entrada sale directo de `getContainingNamespace()`, que por default es
`getIdentifier(stack).getNamespace()` — es decir, el namespace del **Item real** detrás del
stack, sin ningún gancho en la API pública de plugins para sobrescribirlo. Como la pestaña
creativa usa libros encantados de verdad (`minecraft:enchanted_book`, solo con distintos
componentes), REI siempre los va a leer como `minecraft`, sin importar qué plugin se registre.

Las únicas dos formas reales de cambiar esto serían registrar ítems propios en vez de libros
encantados vanilla (dejarían de ser libros encantados "de verdad" a menos que se les copie ese
comportamiento a mano), o un mixin dentro de las clases internas de REI (no su API pública,
más frágil ante actualizaciones). Se decidió no hacer ninguna de las dos por ahora porque es
solo cosmético.

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
