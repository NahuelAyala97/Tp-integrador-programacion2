# 🏥 Sistema de Gestión de Pacientes e Historias Clínicas

## 📌 Descripción del dominio
Este proyecto implementa un **sistema de gestión de pacientes e historias clínicas** desarrollado en Java.
El dominio elegido corresponde al ámbito **sanitario**, donde cada paciente está asociado de manera **1:1** con una historia clínica.

### Funcionalidades principales:
* **Registrar pacientes:** Carga de datos personales y médicos con asignación automática de historia clínica.
* **Listar pacientes:** Visualización de todos los pacientes activos.
* **Búsqueda:** Localización rápida de pacientes por DNI.
* **Actualización:** Modificación de datos personales y médicos.
* **Baja física:** Eliminación completa del paciente y su historia clínica asociada.
* **Validaciones:** Reglas de negocio (unicidad de DNI, grupo sanguíneo válido, fecha de nacimiento no futura).

> La aplicación se ejecuta por **consola** y utiliza **MariaDB** como motor de base de datos.

---

## ⚙️ Requisitos del Sistema

* **Java 17** o superior (JDK configurado en el PATH).
* **MariaDB 10.x** o superior.
* **Driver JDBC** para MariaDB (`mariadb-java-client-3.x.x.jar`).
* **IDE recomendado:** NetBeans, IntelliJ IDEA o Eclipse.

---

## 🗄️ Instalación de la Base de Datos

El proyecto incluye un script SQL para la creación automática de la estructura y la carga de datos.

1.  Accede a tu cliente de MariaDB (ej: Workbench, DBeaver, HeidiSQL o terminal).
2.  Localiza el script en la carpeta del repositorio: `database/schema.sql`.
3.  Ejecuta el script.

Si usas la terminal:
```sql
SOURCE database/schema.sql;
```

Este script:
Crea el esquema bdd_tpi.
Define las tablas paciente y historiaclinica con sus constraints.
Implementa triggers para validar fechas de nacimiento.
Genera datos masivos de prueba (nombres, apellidos, historias clínicas).
Inserta pacientes y sus historias clínicas asociadas

```
USE bdd_tpi;
SELECT COUNT(*) FROM paciente;
SELECT COUNT(*) FROM historiaclinica;
```

## 🚀 Cómo compilar y ejecutar
1. Compilación
Desde la carpeta raíz del proyecto:

```
javac -d bin src/config/*.java src/dao/*.java src/entities/*.java src/services/*.java src/main/*.java
```

3. Ejecución
Ejecutar la clase principal:

```
java -cp bin:lib/mariadb-java-client-x.x.x.jar main.Main
```

## 🔐 Credenciales de prueba
En el archivo DataBaseConnection.java se configuran las credenciales de acceso a la base de datos. Ejemplo:
```
private static final String URL = "jdbc:mariadb://localhost:3306/bdd_tpi";
private static final String USER = "root";
private static final String PASSWORD = "tu_password";
```

Flujo de uso
Al iniciar la aplicación se muestra el menú principal:
=== Sistema de Gestión de Pacientes ===

=== MENÚ PRINCIPAL ===
1. Crear paciente
2. Listar pacientes activos
3. Buscar paciente por DNI
4. Actualizar datos del paciente
5. Eliminar (baja física)
0. Salir
Seleccione una opción:

Opción 1: Crear paciente → solicita datos personales y médicos, asigna historia clínica automática.
Opción 2: Listar pacientes → muestra todos los pacientes registrados.
Opción 3: Buscar por DNI → devuelve el paciente correspondiente.
Opción 4: Actualizar → permite modificar datos personales y médicos, manteniendo valores si se deja en blanco.
Opción 5: Eliminar → elimina paciente y su historia clínica asociada.
Opción 0: Salir del sistema.

## Link Video de YouTube
https://www.youtube.com/watch?v=aLBZj1iuBL4



