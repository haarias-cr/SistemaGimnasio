============================
PROYECTO GIMNASIO FIDNESS
Avance 2 - Documentación
============================

Autor: Harvi Arias
Curso: Programación Cliente / Servidor
Fecha: Julio 2025

------------------------------------
📌 DESCRIPCIÓN GENERAL DEL PROYECTO
------------------------------------

Este proyecto es una aplicación Java con interfaz gráfica (Swing) que permite a los usuarios crear, guardar y visualizar rutinas de ejercicio personalizadas.

En este segundo avance se incorporaron las siguientes mejoras:
- Aplicación de herencia y polimorfismo para representar diferentes tipos de ejercicios (Pierna, Brazo, Cardio).
- Implementación de persistencia de datos mediante serialización de objetos (usuarios y rutinas).
- Reestructuración del proyecto en paquetes (`modelo`, `interfaz`, `util`) para mejorar la organización del código.

------------------------------------
✅ CAMBIOS REALIZADOS EN EL AVANCE 2
------------------------------------

1. **Herencia y Polimorfismo**
   - Se creó una clase abstracta `Ejercicio` con el método `mostrarDetalle()`.
   - Se implementaron las subclases: `EjercicioPierna`, `EjercicioBrazo`, `EjercicioCardio`, que heredan de `Ejercicio`.

2. **Serialización de datos**
   - Los objetos `Usuario` y `Rutina` ahora implementan la interfaz `Serializable`.
   - Se agregaron métodos en `GestorDeDatos` para guardar y cargar datos desde archivos `.dat`.

3. **Persistencia en disco**
   - Al crear un nuevo usuario o rutina, los datos son almacenados localmente en archivos binarios:
     - `usuarios.dat`
     - `rutinas.dat`
   - Al iniciar la aplicación, los archivos son cargados para mantener la continuidad del sistema.

4. **Organización del proyecto**
   - Se reestructuró el código en paquetes:
     - `modelo` → Clases de lógica interna (Usuario, Rutina, Ejercicio, GestorDeDatos, etc.).
     - `interfaz` → Ventanas del sistema (`VentanaLogin`, `VentanaPrincipal`, etc.).
     - `util` → Clases de prueba o herramientas auxiliares.

5. **Correcciones adicionales**
   - Se solucionó el problema visual que mostraba `modelo.Usuario` en la GUI agregando un método `toString()` adecuado.
   - Se depuraron errores relacionados con la carga de archivos serializados tras la reorganización del paquete.

-------------------------------
🧪 PASOS PARA PROBAR LA APLICACIÓN
-------------------------------

1. Ejecutar `Main.java`.

2. En la ventana de inicio de sesión:
   - Registrar un nuevo usuario usando el botón **"Registrarse"**.
   - Luego, iniciar sesión con ese usuario.

3. Crear una nueva rutina:
   - Seleccionar ejercicios desde el panel izquierdo.
   - Presionar **"Agregar a rutina"**.
   - Verificar que los ejercicios aparecen en el panel derecho.
   - Hacer clic en **"Guardar rutina"**.
   - Cerrar la ventana de rutina.
   - Ir a **"Ver Rutinas"** y confirmar que aparece la rutina creada.

4. Cerrar completamente la aplicación.

5. Volver a ejecutar `Main.java` e iniciar sesión con el mismo usuario:
   - Confirmar que la rutina creada se mantiene y los datos persisten.

🖼️ Aquí puede insertar capturas de pantalla:
- Después del login exitoso
- Interfaz para agregar ejercicios a la rutina
- Vista de la rutina guardada
- Comprobación de persistencia tras reinicio

---------------------
📂 ARCHIVOS RELEVANTES
---------------------

- `/src/modelo`: Clases del modelo lógico.
- `/src/interfaz`: Ventanas de usuario (GUI).
- `/src/util`: Clases auxiliares de prueba.
- `usuarios.dat`: Archivo binario con usuarios registrados.
- `rutinas.dat`: Archivo binario con las rutinas guardadas.
- `README.txt`: Este documento.

-----------------
📌 NOTA ADICIONAL
-----------------

En caso de que los archivos `.dat` hayan sido generados con versiones incompatibles del modelo, puede ser necesario eliminarlos para evitar errores de deserialización.
