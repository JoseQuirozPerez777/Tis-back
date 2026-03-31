# \# 🚀 Sistema de Gestión de Portafolios Profesionales - Backend (TeamSys)

# 

# !\[Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge\&logo=java)

# !\[Spring Boot](https://img.shields.io/badge/Spring\_Boot-3.5.12-brightgreen?style=for-the-badge\&logo=springboot)

# !\[MySQL](https://img.shields.io/badge/MySQL-8.4-blue?style=for-the-badge\&logo=mysql)

# 

# \## 📝 Descripción del Proyecto

# Este sistema robusto de backend permite a profesionales de diversas áreas centralizar su trayectoria, proyectos y certificaciones en un portafolio digital. Desarrollado bajo principios de \*\*arquitectura limpia\*\* y \*\*seguridad por diseño\*\*, el sistema garantiza la integridad de la información y una experiencia de usuario fluida.

# 

# \---

# 

# \## 🛠️ Stack Tecnológico

# \* \*\*Lenguaje:\*\* Java 21 (LTS)

# \* \*\*Framework Principal:\*\* Spring Boot 3.5.12

# \* \*\*Seguridad:\*\* Spring Security 6 + JWT (JSON Web Tokens)

# \* \*\*Gestión de Base de Datos:\*\* Spring Data JPA / Hibernate

# \* \*\*Motor de Base de Datos:\*\* MySQL 8.4

# \* \*\*Herramientas de Productividad:\*\* Lombok, Maven, MapStruct.

# \* \*\*Documentación de API:\*\* Swagger/OpenAPI (Pendiente).

# 

# \---

# 

# \## 🛡️ Seguridad y Fiabilidad (Cumplimiento Pliego 3.2.2)

# Para cumplir con los estándares de seguridad industrial exigidos, se han implementado las siguientes capas:

# 

# 1\.  \*\*Protección de Credenciales:\*\* Las contraseñas nunca se almacenan en texto plano; se utiliza \*\*BCrypt\*\* con un factor de costo robusto.

# 2\.  \*\*Autenticación Stateless:\*\* Implementación de tokens \*\*JWT\*\* para evitar el uso de sesiones en servidor, mejorando la escalabilidad.

# 3\.  \*\*Prevención de Fuerza Bruta:\*\* \* Bloqueo temporal de cuenta de usuario tras 5 intentos fallidos.

# &#x20;   \* \*\*Rate Limiting:\*\* Interceptor de seguridad que limita las peticiones por IP para mitigar ataques DoS.

# 4\.  \*\*Integridad de Datos:\*\* Uso de validaciones a nivel de DTO y restricciones de integridad referencial en MySQL.

# 

# \---

# 

# \## ⚙️ Configuración del Entorno

# 

# \### Requisitos Previos

# \* Java JDK 21 instalado.

# \* Maven 3.9+ instalado.

# \* MySQL Server 8.0+ en ejecución.

# 

# \### Variables de Configuración

# Configura tu archivo `src/main/resources/application.properties`:

# 

# ```properties

# \# Conexión a DB

# spring.datasource.url=jdbc:mysql://localhost:3306/db\_portafolios

# spring.datasource.username=root

# spring.datasource.password=tu\_password

# spring.jpa.hibernate.ddl-auto=update

# 

# \# Configuración de Seguridad (JWT)

# jwt.secret=TU\_FIRMA\_SECRETA\_SUPER\_SEGURA\_DE\_64\_CARACTERES

# jwt.expiration=86400000

