# Gestión de Contactos – Programación Concurrente

Aplicación de escritorio desarrollada en **Java Swing** para administrar contactos. En esta versión se incorpora **programación concurrente** para que la interfaz siga fluida mientras se ejecutan tareas pesadas en segundo plano.

Proyecto de la asignatura **Programación de Interfaces** – Universidad Politécnica Salesiana (Unidad 3).

## Características

- Registrar, editar, eliminar y buscar contactos.
- Interfaz con diseño visual personalizado e iconos vectoriales.
- Soporte multilingüe: español (Ecuador), inglés (EE. UU.) y portugués (Brasil).
- Exportación de contactos a CSV.
- Estadísticas de contactos.
- Simulación de carga masiva (5,000 registros) para pruebas de rendimiento.

## Concurrencia y sincronización

| Funcionalidad | Técnica utilizada |
|---|---|
| Validación de duplicados antes de guardar | `Thread` independiente |
| Búsqueda de contactos | `SwingWorker` (`doInBackground` / `done`) |
| Exportación a CSV | `Thread` + bloque `synchronized` con objeto monitor |
| Notificaciones en pantalla | Hilo + `SwingUtilities.invokeLater()` |
| Edición exclusiva de un contacto | Métodos `synchronized` y bloqueo (`intentarBloquear` / `liberarBloqueo`) |
| Acceso a la lista de contactos | `Collections.synchronizedList` |

Todas las actualizaciones de componentes Swing se realizan en el Event Dispatch Thread (EDT).

## Arquitectura (MVC)

```
src/
├── Main.java
├── modelo/
│   ├── persona.java
│   └── personaDAO.java
├── vista/
│   └── ventana.java
├── controlador/
│   └── logica_ventana.java
└── recursos/
    ├── mensajes_es_EC.properties
    ├── mensajes_en_US.properties
    └── mensajes_pt_BR.properties
```

## Requisitos

- JDK 11 o superior
- Eclipse IDE (opcional)

## Cómo ejecutar

**Desde Eclipse**

1. Clona el repositorio:
   ```bash
   git clone https://github.com/TU_USUARIO/NOMBRE_DEL_REPO.git
   ```
2. En Eclipse: *File > Import > Existing Projects into Workspace*.
3. Ejecuta `Main.java` (*Run As > Java Application*).

**Desde la terminal**

```bash
cd NOMBRE_DEL_REPO
mkdir bin
javac -d bin -sourcepath src src/Main.java
cp -r src/recursos bin/
java -cp bin Main
```

## Evolución del proyecto

- **Unidad 1:** aplicación base de gestión de contactos.
- **Unidad 2:** mejora visual, prototipado e internacionalización.
- **Unidad 3:** programación concurrente y sincronización.

## Autor

**George Suárez**
Universidad Politécnica Salesiana
