# Proyecto: SistemaGimnasio (Cliente-Servidor + MySQL + GUI)

---

## 📂 Estructura del Proyecto

```bash
SistemaGimnasio/
│── src/
│   ├── modelo/
│   │   ├── Ejercicio.java            # ✅ Clase principal de ejercicios (se usa en todo el sistema)
│   │   ├── EjercicioBrazo.java       # ⚠️ Ya no se usa, opcional para pruebas
│   │   ├── EjercicioCardio.java      # ⚠️ Ya no se usa, opcional para pruebas
│   │   ├── EjercicioPierna.java      # ⚠️ Ya no se usa, opcional para pruebas
│   │   ├── Rutina.java               # ✅ Clase principal para rutinas
│   │   ├── Usuario.java              # ✅ Clase principal para usuarios
│   │
│   ├── dao/
│   │   ├── EjercicioDAO.java         # ✅ Manejo persistencia de ejercicios
│   │   ├── RutinaDAO.java            # ✅ Manejo persistencia de rutinas
│   │   ├── UsuarioDAO.java           # ✅ Manejo persistencia de usuarios
│   │   ├── GestorDeDatos.java        # ⚠️ Obsoleto, ya no se usa (reemplazado por DAO)
│   │
│   ├── gui/
│   │   ├── VentanaLogin.java         # ✅ Interfaz de login
│   │   ├── VentanaPrincipal.java     # ✅ Interfaz principal
│   │   ├── VentanaRutina.java        # ✅ Gestión visual de rutinas
│   │   ├── VentanaRegistro.java      # ✅ Registro de usuarios
│   │
│   ├── main/
│   │   ├── Main.java                 # ✅ Punto de entrada del programa
│
│── .gitignore                        # Ignora archivos temporales y .dat
│── rutina.txt                        # Archivo de ejemplo de rutinas
│── rutinas.dat                       # ⚠️ Archivo binario opcional (se ignora en Git)
│── usuarios.dat                      # ⚠️ Archivo binario opcional (se ignora en Git)
│── proyectoGimnasio.iml              # Configuración de IntelliJ
│── out/                              # Carpeta de compilación (ignorada en Git)
                            # Carpeta de compilación (ignorada en Git)

```

🛠️ Requisitos y Dependencias

- JDK 17 o superior (probado en Temurin 21)
- MySQL Server instalado y corriendo (Windows/Linux)
- MySQL Connector/J (versión recomendada: **mysql-connector-j-9.4.0.jar**
) agregado al classpath
- IDE recomendado: IntelliJ IDEA, aunque funciona en NetBeans o Eclipse con soporte para Swing

---

## 🔑 Notas importantes

- Clases en uso real (core del sistema):
Ejercicio, Rutina, Usuario, EjercicioDAO, RutinaDAO, UsuarioDAO, VentanaLogin, VentanaPrincipal, VentanaRutina, VentanaRegistro, Main.

- Clases opcionales / pruebas:
EjercicioBrazo, EjercicioCardio, EjercicioPierna.
Se mantienen como ejemplo de especialización, pero ya no son llamadas en el flujo principal.

- GestorDeDatos:
Este fue reemplazado por los DAOs. Lo podés dejar con comentario que está deprecated.

- Archivos .dat:
rutinas.dat y usuarios.dat ya no son obligatorios porque la persistencia se maneja con DAO + serialización.
Podés dejarlos fuera del Git (.gitignore) si no son necesarios para entregar el proyecto.

---

## 🗄️ Estructura de la Base de Datos (Tablas SQL)

### Tabla: `ejercicios`

<pre>CREATE TABLE IF NOT EXISTS ejercicios (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  nombre      VARCHAR(120) NOT NULL,
  tipo        VARCHAR(40)  NOT NULL,         -- Abdomen, Brazo, Cardio, Espalda, Hombro, Pecho, Pierna
  descripcion TEXT         NULL,
  CONSTRAINT uq_ejercicios_nombre UNIQUE (nombre)
);</pre>

### Tabla: `rutinas`

<pre>CREATE TABLE IF NOT EXISTS rutinas (
  id              INT AUTO_INCREMENT PRIMARY KEY,
  nombre          VARCHAR(120) NOT NULL,
  usuario_id      INT          NOT NULL,     -- identificador lógico del creador (del sistema local)
  fecha_creacion  DATE         NOT NULL,
  INDEX idx_rutinas_usuario (usuario_id)
);</pre>

### Tabla: `rutina_ejercicios`

<pre>CREATE TABLE IF NOT EXISTS rutina_ejercicio (
  rutina_id     INT          NOT NULL,
  ejercicio_id  INT          NOT NULL,
  orden         INT          NOT NULL,
  series        INT          NOT NULL,
  repeticiones  INT          NOT NULL,
  PRIMARY KEY (rutina_id, ejercicio_id, orden),

  CONSTRAINT fk_re_rutina
    FOREIGN KEY (rutina_id)
    REFERENCES rutinas(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

  CONSTRAINT fk_re_ejercicio
    FOREIGN KEY (ejercicio_id)
    REFERENCES ejercicios(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
);</pre>

---

## 🚀 Instrucciones para probar el Proyecto

1. Clona o descarga este repositorio en tu máquina local.
2. Crea la base de datos en MySQL e importa las tablas mostradas arriba.
3. Agrega el MySQL Connector/J al classpath del proyecto.
4. Compila el proyecto en tu IDE.
5. Configura las credenciales en db.properties.

---

## Ejecución

1. Ejecuta Main.java para iniciar el sistema.
2. Inicia sesión con un usuario existente o crea uno nuevo.
3. Accede al menú principal para gestionar rutinas y ejercicios.
  - Usa la interfaz gráfica para:
  - Crear, listar, editar y eliminar rutinas.
  - Administrar ejercicios (brazo, pierna, cardio).

---

## ✨ Características adicionales

1. Validación de usuarios: no permite correos repetidos.
2. Gestión de rutinas personalizadas: cada usuario puede crear y administrar sus propias rutinas.
3. DAO y separación lógica: se usan clases DAO para gestionar la persistencia de datos.
4. Serialización opcional: archivos usuarios.dat y rutinas.dat pueden usarse como respaldo local (en caso de no usar la BD).
5. Interfaz Swing amigable: ventanas para login, ejercicios, rutinas y menú principal.

---

## 🧑‍💻 Autor
Harvi Arias
GitHub: haarias-cr
