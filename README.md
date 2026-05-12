# Cofi

Cofi es una aplicación web orientada a personas mayores cuyo objetivo es facilitar la interacción social mediante eventos, comunidad, chats y actividades accesibles.

---

# Apertura sencilla del proyecto

Para utilizar la aplicación sin descargar nada en local: Ir a la URL http://34.175.159.154

En caso de que esta URL no esté activa, hay que correr la aplicación en local con los siguientes pasos:

# Requisitos previos

Para ejecutar el proyecto en local es necesario tener instalado:

- Java 21
- Maven
- MySQL Server
- Git

---

# Instalación en local

## 1. Clonar el repositorio

```bash
git clone https://github.com/Mauricio-NCB/main.git
```

---

## 2. Crear la base de datos

Abrir MySQL y crear una base de datos:

```sql
CREATE DATABASE website;
```

Importar posteriormente el script SQL proporcionado en el proyecto.

---

## 3. Configurar application.properties

Modificar el archivo:

```properties
src/main/resources/application.properties
```

Con los datos de tu base de datos:

```properties
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD
```

---

## 4. Instalar dependencias y ejecutar

Ejecutar:

```bash
mvn spring-boot:run
```

O ejecutar directamente la clase principal desde Visual Studio o Eclipse.

---

## 5. Acceder a la aplicación

Abrir en el navegador:

```
http://localhost:8080
```

---

# Autores

- Mauricio Nilton Calderón Barazorda
- Mario Gallego Hernández