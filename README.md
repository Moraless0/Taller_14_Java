# RoboFix - Taller de Reparación de Robots

## Descripción del Proyecto

RoboFix es un aplicativo de consola en Java para administrar robots que llegan con fallas a un taller de reparación. El sistema permite registrar robots, ejecutar diagnósticos, consultar repuestos, modificar configuraciones y realizar reparaciones.

Este proyecto es resultado de un taller de refactorización donde se implementaron patrones de diseño para resolver problemas arquitectónicos y mejorar la calidad del código.

## Problemas Resueltos

### 1. Diagnóstico Incompatible (Patrón Adapter)

El escáner antiguo (EscanerAntiguo) devuelve resultados en formato de texto delimitado por | (ej: "RX-01|BATERIA|CRITICO|15"), mientras que el resto de la aplicación espera datos estructurados. Esto obligaba a cada componente que usaba el diagnóstico a parsear manualmente el string, creando acoplamiento y duplicación de lógica.

Solución: Se implementó el patrón Adapter creando:
- Interfaz Diagnostico que define el contrato moderno para obtener resultados
- Clase ResultadoDiagnostico que encapsula los datos de manera estructurada
- Clase EscanerAntiguoAdapter que implementa Diagnostico y envuelve al EscanerAntiguo, transformando el formato pipe-delimited en objetos ResultadoDiagnostico

Utilidad: El patrón Adapter permite integrar el sistema externo sin modificarlo, eliminando el acoplamiento directo con el formato de texto. Ahora el código que utiliza el diagnóstico trabaja con objetos tipados y el parsing se centraliza en el adapter. Si el formato del escáner cambia, solo se modifica el adapter sin afectar el resto de la aplicación.

### 2. Inventarios Duplicados (Patrón Singleton)

Cada instancia de Taller creaba su propio objeto Inventario, lo que causaba inconsistencias. Si un taller descontaba una batería, el otro continuaba creyendo que tenía el stock original. Esto podía llevar a intentar reparaciones con repuestos que ya no existían.

Solución: Se implementó el patrón Singleton en la clase Inventario:
- Constructor privado para evitar instanciación directa
- Campo estático privado para la única instancia
- Método público estático getInstance() con inicialización perezosa
- Todos los objetos Taller ahora obtienen la misma instancia a través de Inventario.getInstance()

Utilidad: El Singleton garantiza que exista una única instancia del inventario compartida por todos los talleres, eliminando inconsistencias en el stock. Además, se agregó validación en el método descontar() para prevenir cantidades negativas y verificar que existan suficientes repuestos antes de realizar una operación.

### 3. Configuraciones Irreversibles (Patrón Memento)

Cuando se modificaba la energía o el modo de un robot, no existía mecanismo para deshacer el cambio. Si una reparación o configuración salía mal, no había forma de recuperar el estado anterior, lo que podía dejar el robot en un estado inconsistente o inoperativo.

Solución: Se implementó el patrón Memento con:
- Clase RobotMemento que almacena el estado inmutable (energía y modo)
- Método guardarEstado() en Robot que crea y retorna un RobotMemento
- Método restaurarEstado(RobotMemento) en Robot que recupera el estado
- Clase RobotCaretaker que gestiona el memento guardado y proporciona métodos para guardar y restaurar configuraciones

Utilidad: El patrón Memento permite capturar el estado interno de un objeto sin violar su encapsulamiento. Ahora es posible guardar una configuración antes de realizar cambios y restaurarla si algo sale mal, proporcionando seguridad en las operaciones de modificación.

## Informe de Diagnóstico - Defectos Encontrados

Basado en la exploración inicial del código original, se identificaron los siguientes 5 defectos principales:

### 1. Falta de Validación de Energía

Causa: Los métodos setEnergia() de la clase Robot y la opción 4 del menú no validan que el valor esté dentro de un rango aceptable.

Consecuencia: Es posible asignar valores negativos (ej: -10) o mayores al máximo lógico (ej: 150), lo que deja el robot en estados inconsistentes o sin sentido físico.

### 2. Falta de Validación de Modo

Causa: El método setModo() y la opción 4 del menú aceptan cualquier string sin verificar que sea uno de los modos permitidos.

Consecuencia: Se pueden asignar modos inexistentes (ej: "INVALID", "TURBO", etc.), lo que puede causar errores en lógica futura que dependa de modos específicos.

### 3. Reparación sin Verificación de Inventario

Causa: El método reparar() descontaba repuestos del inventario sin verificar primero si existía suficiente stock.

Consecuencia: El inventario puede quedar con cantidades negativas (ej: BATERIA: -1), representando un estado imposible en la realidad. Además, se reporta éxito incluso cuando la reparación no pudo completarse por falta de repuestos.

### 4. Inventarios Duplicados por Instancia

Causa: Cada objeto Taller instanciaba su propio objeto Inventario en el constructor: private Inventario inventario = new Inventario();

Consecuencia: Los talleres no comparten el stock real. Si taller1 descontara una batería, taller2 seguiría mostrando la cantidad original, lo que genera inconsistencias y podría permitir reparaciones con repuestos que ya no existen.

### 5. Ausencia de Mecanismo de Restauración

Causa: No existe ninguna funcionalidad para guardar el estado actual del robot (energía y modo) ni para recuperarlo posteriormente.

Consecuencia: Si una modificación de configuración o una reparación sale mal, no hay forma de revertir al estado anterior. El robot queda permanentemente en el estado modificado, lo que puede ser problemático si el cambio fue accidental o produjo un estado no deseado.

## Resultados de Pruebas de Aceptación

### P1: Consultar el robot
Acción: Seleccionar opción 1 del menú.
Resultado esperado: Mostrar código, nombre, energía y modo.
Resultado obtenido: PASÓ - Muestra correctamente: Codigo: RX-01, Nombre: Explorador, Energia: 80, Modo: NORMAL

### P2: Ejecutar diagnóstico
Acción: Seleccionar opción 2 del menú.
Resultado esperado: Obtener los datos interpretados mediante Adapter.
Resultado obtenido: PASÓ - Muestra: Robot: RX-01, Componente: BATERIA, Estado: CRITICO, Consumo reportado: 15 (datos estructurados, no parseo manual)

### P3: Consultar el inventario desde ambos talleres
Acción: Seleccionar opción 6 del menú.
Resultado esperado: Ambos muestran las mismas cantidades.
Resultado obtenido: PASÓ - Ambos talleres muestran: MOTOR: 3, SENSOR: 4, BATERIA: 2 (Singleton funcionando)

### P4: Descontar una batería desde un taller
Acción: Reparar robot (op 3) desde taller1, luego comparar inventarios (op 6).
Resultado esperado: El otro taller refleja el descuento.
Resultado obtenido: PASÓ - Después de reparar, ambos talleres muestran BATERIA: 1 (cambio compartido)

### P5: Intentar descontar más repuestos de los disponibles
Acción: Reparar 3 veces (se requieren 3 baterías, hay solo 2).
Resultado esperado: Rechazar la operación sin alterar el inventario.
Resultado obtenido: PASÓ - Tercera reparación falla con mensaje "Error: No hay suficientes repuestos de BATERIA para reparar" y el inventario queda en 0, no en negativo

### P6: Configurar energía en -10
Acción: Opción 4, ingresar energía -10.
Resultado esperado: Rechazar el valor.
Resultado obtenido: PASÓ - Muestra "Error: La energia debe estar entre 0 y 100" y restaura el valor anterior (80)

### P7: Configurar energía en 150
Acción: Opción 4, ingresar energía 150.
Resultado esperado: Rechazar el valor.
Resultado obtenido: PASÓ - Muestra "Error: La energia debe estar entre 0 y 100" y restaura el valor anterior (80)

### P8: Configurar un modo desconocido
Acción: Opción 4, ingresar modo "INVALID".
Resultado esperado: Rechazar el modo y conservar el estado anterior.
Resultado obtenido: PASÓ - Muestra "Error: El modo debe ser NORMAL, AHORRO o MANTENIMIENTO" y restaura el modo anterior (NORMAL)

### P9: Guardar, modificar y restaurar el robot
Acción: Guardar configuración (op 8), cambiar energía a 50 y modo a AHORRO, luego restaurar (op 9).
Resultado esperado: Recuperar la energía y el modo guardados.
Resultado obtenido: PASÓ - Después de guardar y modificar, la restauración recupera Energia: 80, Modo: NORMAL (valores originales)

### P10: Restaurar sin haber guardado
Acción: Seleccionar opción 9 sin haber usado opción 8.
Resultado esperado: Mostrar un mensaje y mantener el estado actual.
Resultado obtenido: PASÓ - Muestra "Error: No hay ninguna configuracion guardada para restaurar" y el robot mantiene Energia: 80, Modo: NORMAL

### P11: Reparar sin repuestos suficientes
Acción: Intentar reparar cuando no hay baterías disponibles.
Resultado esperado: Informar el fallo y evitar una reparación parcial.
Resultado obtenido: PASÓ - Muestra "Error: No hay suficientes repuestos de BATERIA para reparar" y "La reparacion fallo". El inventario no queda en negativo y la energía del robot no se modifica.

### P12: Revisar EscanerAntiguo
Acción: Comparar la clase EscanerAntiguo con el código original.
Resultado esperado: Confirmar que no fue modificado.
Resultado obtenido: PASÓ - La clase EscanerAntiguo en el paquete com.mycompany.robofix.legacy es idéntica al código original proporcionado. Solo se movió de ubicación y se agregó el package, pero su lógica permanece intacta.

## Estructura del Proyecto

```
RoboFix/
├── pom.xml
├── README.md
└── src/
    └── main/
        └── java/
            └── com/
                └── mycompany/
                    └── robofix/
                        ├── Main.java
                        ├── model/
                        │   ├── Robot.java
                        │   └── Inventario.java
                        ├── service/
                        │   └── Taller.java
                        ├── legacy/
                        │   └── EscanerAntiguo.java
                        └── patterns/
                            ├── adapter/
                            │   ├── Diagnostico.java
                            │   ├── ResultadoDiagnostico.java
                            │   └── EscanerAntiguoAdapter.java
                            └── memento/
                                ├── RobotMemento.java
                                └── RobotCaretaker.java
```

## Compilación y Ejecución

Compilar:
```bash
javac -d target/classes src/main/java/com/mycompany/robofix/Main.java \
  src/main/java/com/mycompany/robofix/model/Robot.java \
  src/main/java/com/mycompany/robofix/model/Inventario.java \
  src/main/java/com/mycompany/robofix/legacy/EscanerAntiguo.java \
  src/main/java/com/mycompany/robofix/service/Taller.java \
  src/main/java/com/mycompany/robofix/patterns/adapter/Diagnostico.java \
  src/main/java/com/mycompany/robofix/patterns/adapter/ResultadoDiagnostico.java \
  src/main/java/com/mycompany/robofix/patterns/adapter/EscanerAntiguoAdapter.java \
  src/main/java/com/mycompany/robofix/patterns/memento/RobotMemento.java \
  src/main/java/com/mycompany/robofix/patterns/memento/RobotCaretaker.java
```

Ejecutar:
```bash
java -cp target/classes com.mycompany.robofix.Main
```

## Menú de Opciones

1. Ver robot - Muestra la información actual del robot (código, nombre, energía, modo)
2. Diagnosticar robot - Ejecuta un diagnóstico usando el escáner antiguo a través del Adapter
3. Reparar robot - Intenta reparar el robot verificando disponibilidad de repuestos
4. Cambiar configuración - Modifica la energía y el modo del robot con validación
5. Ver inventario - Muestra el inventario de repuestos actual
6. Comparar inventarios - Muestra el inventario desde ambos talleres para verificar el Singleton
7. Salir - Opción de compatibilidad (funciona igual que opción 10)
8. Guardar configuración - Guarda el estado actual del robot (energía y modo)
9. Restaurar configuración - Restaura la configuración previamente guardada
10. Salir - Cierra la aplicación

## Conclusión Técnica

### Mejoras Realizadas

1. Arquitectura y Patrones de Diseño:
   - Se implementaron correctamente los tres patrones solicitados (Adapter, Singleton, Memento)
   - El código ahora sigue principios de diseño más sólidos con menor acoplamiento
   - La separación de responsabilidades es más clara con la estructura de paquetes

2. Validación y Robustez:
   - Se agregó validación completa para energía (rango 0-100) y modos (NORMAL, AHORRO, MANTENIMIENTO)
   - Las operaciones que pueden fallar ahora retornan valores booleanos para indicar éxito/error
   - Se implementó validación de inventario para prevenir cantidades negativas
   - Las configuraciones parciales no dejan el sistema en estado inconsistente

3. Calidad del Código:
   - Se definieron constantes para valores mágicos (ENERGIA_MIN, ENERGIA_MAX, modos válidos)
   - Los métodos tienen responsabilidades claras y nombres descriptivos
   - Se eliminó la duplicación de lógica de parsing del diagnóstico
   - La estructura de paquetes organiza el código por dominio (model, service, legacy, patterns)

4. Funcionalidad:
   - Se agregaron las opciones de guardar y restaurar configuraciones (menú 8 y 9)
   - El inventario ahora es compartido correctamente entre todos los talleres
   - Los errores se comunican claramente al usuario con mensajes descriptivos

### Limitaciones Pendientes

1. Persistencia: Las configuraciones guardadas y el inventario solo existen en memoria. Si se reinicia la aplicación, se pierden todos los cambios. Una mejora futura sería agregar persistencia en archivos o base de datos.

2. Multi-robot: El sistema actual maneja un solo robot. Para un taller real, sería necesario gestionar múltiples robots simultáneamente, posiblemente con una colección y un identificador único para cada uno.

3. Historial de Mementos: La implementación actual del Memento solo guarda un estado anterior. Una versión más avanzada podría mantener un historial de cambios (stack de mementos) para permitir deshacer múltiples operaciones.

4. Manejo de Excepciones: Aunque se validan las entradas, el código usa principalmente valores de retorno booleanos. Una aplicación más robusta podría usar excepciones personalizadas para diferentes tipos de errores.

5. Interfaz de Usuario: La interfaz es de consola básica. Una mejora sería agregar una interfaz gráfica o una interfaz web para mejor usabilidad.

6. Concurrencia: El Singleton no es thread-safe. En un entorno multi-hilo, podría crearse más de una instancia si múltiples hilos llaman a getInstance() simultáneamente. Se podría usar inicialización eager o sincronización para resolver esto.

7. Extensibilidad del Escáner: Aunque el Adapter ayuda, agregar nuevos tipos de escáner requeriría crear nuevos adapters. Una arquitectura más flexible podría usar un factory o inyección de dependencias.

A pesar de estas limitaciones, el aplicativo ha mejorado significativamente en términos de diseño, robustez y mantenibilidad. Los patrones implementados resuelven los problemas originales de manera efectiva y el código ahora es más fácil de extender y mantener.
