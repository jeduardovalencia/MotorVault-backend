MotorVault — Sistema de Gestión de Vehículos Personales

Aplicación fullstack para que los usuarios registren y gestionen su flota de vehículos personales, con autenticación segura mediante JWT y panel de administración completo.


📋 Tabla de Contenidos

Descripción
Tecnologías
Características
Arquitectura
Requisitos Previos
Instalación
Configuración
Uso
Endpoints API
Base de Datos
Credenciales de Prueba


📖 Descripción
MotorVault es un sistema web fullstack tipo garage digital personal, donde cada usuario puede registrarse, autenticarse y gestionar sus propios vehículos. El sistema cuenta con dos roles: usuario y administrador, cada uno con su propio panel de control.

🛠 Tecnologías
Backend
Mostrar imagen
Mostrar imagen
Mostrar imagen
Mostrar imagen
Mostrar imagen
Mostrar imagen
Mostrar imagen
Mostrar imagen
Mostrar imagen
Frontend
Mostrar imagen
Mostrar imagen
Mostrar imagen
Mostrar imagen
Mostrar imagen
Mostrar imagen

✨ Características
👤 Panel de Usuario

✅ Registro e inicio de sesión con JWT
✅ CRUD completo de vehículos (marca, modelo, año, placa, color)
✅ Subida de foto del vehículo (base64)
✅ Búsqueda por placa o modelo
✅ Filtrado por año o marca
✅ Importación masiva desde archivo CSV / Excel
✅ Plantilla CSV descargable
✅ Diseño responsive (mobile-first)
✅ Modo oscuro / claro

👑 Panel de Administración

✅ Dashboard con estadísticas globales (usuarios, vehículos, marcas)
✅ Gestión de usuarios (activar/bloquear, cambiar roles)
✅ Gestión de todos los vehículos del sistema
✅ Logs de actividad (login, creación, eliminación)
✅ Filtros por fecha, acción y usuario

🔐 Seguridad

✅ Autenticación stateless con JWT (HS512)
✅ Roles: ROLE_USER y ROLE_ADMIN
✅ Control de acceso por propietario del recurso
✅ Soft delete (nunca se elimina físicamente)
✅ Auditoría automática (createdAt, updatedAt)


🏗 Arquitectura
motorvault-backend/
├── config/          # SecurityConfig, CorsConfig, OpenApiConfig
├── controller/      # AuthController, VehicleController, AdminController
├── dto/             # Request y Response DTOs
├── entity/          # User, Vehicle, ActivityLog
├── exception/       # GlobalExceptionHandler
├── mapper/          # MapStruct mappers
├── repository/      # JPA Repositories
├── security/        # JWT, UserDetailsService, CustomUserDetails
└── service/         # Lógica de negocio

motorvault-frontend/
├── api/             # axiosConfig, authApi, vehicleApi, adminApi
├── components/      # layout, ui, vehicles, admin
├── context/         # AuthContext, ThemeContext
├── hooks/           # useAuth
├── pages/           # LoginPage, RegisterPage, DashboardPage
└── pages/admin/     # AdminDashboard, AdminUsuarios, AdminVehiculos, AdminLogs

📦 Requisitos Previos

Java 21+
Maven 3.8+
Node.js 18+
SQL Server 2019+ (con TCP/IP habilitado en puerto 1433)
Git


🚀 Instalación
1. Clonar repositorios
bashgit clone https://github.com/jeduardovalencia/MotorVault-backend.git
git clone https://github.com/jeduardovalencia/MotorVault-frontend.git
2. Base de datos
Ejecutar en SQL Server Management Studio:
sqlCREATE DATABASE MotorVaultDB COLLATE Latin1_General_CI_AS;
GO

USE MotorVaultDB;

CREATE TABLE users (
    id             BIGINT IDENTITY(1,1) NOT NULL,
    first_name     NVARCHAR(80)         NOT NULL,
    last_name      NVARCHAR(80)         NOT NULL,
    email          NVARCHAR(150)        NOT NULL,
    password       NVARCHAR(255)        NOT NULL,
    role           NVARCHAR(20)         NOT NULL DEFAULT 'ROLE_USER',
    enabled        BIT                  NOT NULL DEFAULT 1,
    created_at     DATETIME2            NOT NULL DEFAULT GETDATE(),
    updated_at     DATETIME2            NOT NULL DEFAULT GETDATE(),
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE vehicles (
    id             BIGINT IDENTITY(1,1) NOT NULL,
    placa          NVARCHAR(10)         NOT NULL,
    marca          NVARCHAR(50)         NOT NULL,
    modelo         NVARCHAR(50)         NOT NULL,
    anio           INT                  NOT NULL,
    color          NVARCHAR(30)         NULL,
    foto_url       NVARCHAR(MAX)        NULL,
    activo         BIT                  NOT NULL DEFAULT 1,
    creado_en      DATETIME2            NOT NULL DEFAULT GETDATE(),
    actualizado_en DATETIME2            NOT NULL DEFAULT GETDATE(),
    usuario_id     BIGINT               NOT NULL,
    CONSTRAINT pk_vehicles PRIMARY KEY (id),
    CONSTRAINT fk_vehicles_usuario FOREIGN KEY (usuario_id) REFERENCES users(id)
);

CREATE TABLE activity_logs (
    id            BIGINT IDENTITY(1,1) NOT NULL,
    accion        NVARCHAR(100)        NOT NULL,
    descripcion   NVARCHAR(500)        NULL,
    usuario_id    BIGINT               NOT NULL,
    usuario_email NVARCHAR(150)        NULL,
    ip            NVARCHAR(50)         NULL,
    creado_en     DATETIME2            NOT NULL DEFAULT GETDATE(),
    CONSTRAINT pk_activity_logs PRIMARY KEY (id)
);
3. Backend
bashcd MotorVault-backend
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
4. Frontend
bashcd MotorVault-frontend
npm install
npm run dev

⚙️ Configuración
application.yml (perfil dev)
yamlspring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=MotorVaultDB;encrypt=false;trustServerCertificate=true
    username: sa
    password: TuPassword
  jpa:
    hibernate:
      ddl-auto: update

app:
  jwt:
    secret: motorvault-dev-secret-key-change-in-production-must-be-at-least-64-chars
    expiration-ms: 86400000
  cors:
    allowed-origins: http://localhost:5173

🌐 Endpoints API
La documentación completa está disponible en Swagger:
http://localhost:8080/swagger-ui/index.html
Autenticación
MétodoEndpointDescripciónPOST/api/auth/registerRegistrar usuarioPOST/api/auth/loginIniciar sesión
Vehículos (requiere JWT)
MétodoEndpointDescripciónGET/api/v1/vehicles/mis-vehiculosListar mis vehículosPOST/api/v1/vehiclesRegistrar vehículoPUT/api/v1/vehicles/{id}Actualizar vehículoDELETE/api/v1/vehicles/{id}Eliminar vehículoGET/api/v1/vehicles/buscarBuscar con filtros
Administración (requiere ROLE_ADMIN)
MétodoEndpointDescripciónGET/api/v1/admin/statsEstadísticas globalesGET/api/v1/admin/usuariosListar usuariosPATCH/api/v1/admin/usuarios/{id}/toggleActivar/bloquearPATCH/api/v1/admin/usuarios/{id}/rolCambiar rolGET/api/v1/admin/vehiculosTodos los vehículosGET/api/v1/admin/logsLogs de actividad

🗄️ Base de Datos
Motor: SQL Server
Base de datos: MotorVaultDB
Diagrama de tablas
users (1) ──────────── (N) vehicles
  id                         id
  first_name                 placa
  last_name                  marca
  email                      modelo
  password                   anio
  role                       color
  enabled                    foto_url
  created_at                 activo
  updated_at                 creado_en
                             actualizado_en
                             usuario_id (FK)

activity_logs
  id
  accion
  descripcion
  usuario_id
  usuario_email
  ip
  creado_en

🔑 Credenciales de Prueba
EmailPasswordRoladmin@motorvault.comAdmin123!ROLE_ADMINjuan@motorvault.comTest123!ROLE_USER

⚠️ Cambiar las contraseñas en producción.


👨‍💻 Autor
Juan Eduardo Valencia
GitHub: @jeduardovalencia

📄 Licencia
Este proyecto fue desarrollado como prueba Ufinet