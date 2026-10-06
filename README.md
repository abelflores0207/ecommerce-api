# 🛒 E-commerce API

API REST para un sistema de e-commerce construida con **Java 17**, **Spring Boot 3**, **Spring Data JPA** y **MySQL**, siguiendo una arquitectura limpia por capas.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8-blue)
![Maven](https://img.shields.io/badge/Build-Maven-red)

## ✨ Características

- CRUD completo de **productos** y registro/consulta de **usuarios**.
- Creación de **órdenes** con múltiples productos, cálculo automático de subtotales y total.
- **Control de stock transaccional**: si algún producto no tiene stock suficiente se lanza `StockInsuficienteException` y se hace *rollback* de toda la orden.
- **Bloqueo optimista** (`@Version`) para evitar inconsistencias de stock en compras concurrentes.
- **Manejo global de excepciones** con `@RestControllerAdvice` y un formato de error JSON uniforme.
- **Validaciones** con Bean Validation (`@NotBlank`, `@Min`, `@Email`...).
- Contraseñas hasheadas con **BCrypt**; nunca se exponen en las respuestas.
- Uso de **DTOs** (Java records) para desacoplar la API del modelo de datos.
- Precio histórico guardado en cada detalle de orden.

## 🛠️ Tecnologías

| Tecnología | Uso |
|------------|-----|
| Java 17 | Lenguaje |
| Spring Boot 3 (Web) | API REST |
| Spring Data JPA / Hibernate | Persistencia |
| MySQL (XAMPP) | Base de datos |
| Lombok | Reducción de boilerplate |
| Bean Validation | Validación de datos de entrada |
| Maven | Gestión de dependencias y build |

## 🧱 Arquitectura

```
Controller  →  Service  →  Repository  →  MySQL
   (DTO)      (negocio)     (JPA)
```

```
src/main/java/com/ecommerce/api
├── EcommerceApiApplication.java
├── config/        PasswordConfig
├── controller/    ProductoController, UsuarioController, OrdenController
├── service/       ProductoService (+ impl/ProductoServiceImpl), UsuarioService, OrdenService
├── repository/    ProductoRepository, UsuarioRepository, OrdenRepository
├── model/         Producto, Usuario, Orden, DetalleOrden, EstadoOrden
├── dto/           Requests / Responses / ErrorResponse
└── exception/     Excepciones personalizadas + GlobalExceptionHandler
```

### Modelo de datos

```
Usuario 1 ───< Orden 1 ───< DetalleOrden >─── 1 Producto
```

## 🚀 Cómo ejecutarlo

### Requisitos

- **JDK 17+**
- **Maven 3.8+**
- **XAMPP** (módulo MySQL) corriendo en `localhost:3306`
- Git

### Pasos

1. **Clonar el repositorio**

   ```bash
   git clone https://github.com/<tu-usuario>/ecommerce-api.git
   cd ecommerce-api
   ```

2. **Iniciar MySQL en XAMPP**: abrí el panel de control de XAMPP y presioná **Start** en el módulo *MySQL*.

3. **Base de datos**: no hace falta crearla a mano; `ecommerce_db` y sus tablas se generan al iniciar la app.
   (Opcional: crearla desde phpMyAdmin con `CREATE DATABASE ecommerce_db;`).

4. **Credenciales** (solo si tu MySQL no usa el `root` sin contraseña por defecto de XAMPP):

   ```bash
   # Linux / macOS
   export DB_USER=root
   export DB_PASSWORD=tu_contraseña

   # Windows (PowerShell)
   $env:DB_USER="root"; $env:DB_PASSWORD="tu_contraseña"
   ```

5. **Ejecutar la aplicación**

   ```bash
   mvn spring-boot:run
   ```

La API queda disponible en **http://localhost:8080**.

## 📚 Endpoints

### Productos

| Método | Endpoint                | Descripción                              |
|--------|-------------------------|------------------------------------------|
| POST   | `/api/productos`        | Crear producto                           |
| GET    | `/api/productos`        | Listar (filtro opcional `?categoria=`)   |
| GET    | `/api/productos/{id}`   | Obtener por id                           |
| PUT    | `/api/productos/{id}`   | Actualizar                               |
| DELETE | `/api/productos/{id}`   | Eliminar                                 |

### Usuarios

| Método | Endpoint              | Descripción        |
|--------|-----------------------|--------------------|
| POST   | `/api/usuarios`       | Registrar usuario  |
| GET    | `/api/usuarios`       | Listar usuarios    |
| GET    | `/api/usuarios/{id}`  | Obtener por id     |

### Órdenes

| Método | Endpoint                        | Descripción                               |
|--------|---------------------------------|-------------------------------------------|
| POST   | `/api/ordenes`                  | Crear orden (valida y descuenta stock)    |
| GET    | `/api/ordenes/{id}`             | Obtener orden con su detalle              |
| GET    | `/api/ordenes?usuarioId={id}`   | Historial de órdenes de un usuario        |

## 🧪 Ejemplos

**Crear producto**

```bash
curl -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Teclado mecánico","descripcion":"Switch red","precio":45000.50,"stock":10,"categoria":"Periféricos"}'
```

**Crear usuario**

```bash
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Ana Pérez","email":"ana@mail.com","contrasena":"Secreta123"}'
```

**Crear orden**

```bash
curl -X POST http://localhost:8080/api/ordenes \
  -H "Content-Type: application/json" \
  -d '{"usuarioId":1,"items":[{"productoId":1,"cantidad":2}]}'
```

Respuesta `201 Created`:

```json
{
  "id": 1,
  "usuarioId": 1,
  "fecha": "2026-10-06T15:30:00",
  "estado": "CREADA",
  "total": 90001.00,
  "detalles": [
    { "productoId": 1, "productoNombre": "Teclado mecánico", "cantidad": 2,
      "precioUnitario": 45000.50, "subtotal": 90001.00 }
  ]
}
```

## ⚠️ Manejo de errores

Todos los errores comparten el mismo formato:

```json
{
  "timestamp": "2026-10-06T15:31:12",
  "status": 400,
  "error": "Bad Request",
  "mensaje": "Stock insuficiente para 'Teclado mecánico': disponible 8, solicitado 20",
  "path": "/api/ordenes"
}
```

| Código | Cuándo ocurre                                                   |
|--------|-----------------------------------------------------------------|
| 400    | Validación fallida, JSON inválido, **stock insuficiente**       |
| 404    | Producto, usuario u orden inexistente                           |
| 409    | Email duplicado, conflicto de concurrencia o de integridad      |
| 500    | Error inesperado (el detalle solo queda en el log del servidor) |

## 🔭 Próximos pasos

- [ ] Autenticación y autorización con Spring Security + JWT
- [ ] Tests unitarios (JUnit 5 + Mockito) y de integración (Testcontainers)
- [ ] Documentación interactiva con Swagger / OpenAPI
- [ ] Cancelación de órdenes con reposición de stock
- [ ] Paginación y ordenamiento en los listados
- [ ] Migraciones con Flyway y Dockerfile / docker-compose

## 👤 Autor

**Abel Adam Flores** — [LinkedIn](https://www.linkedin.com/in/abel-adam-flores-b67242370) · [GitHub](https://github.com/abelflores0207)
