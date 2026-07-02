# PROYECTO POKÉDEX — WildDex
## DOSW · 2026 INTERSEMESTRAL
### Análisis de Requerimientos
Desarrollo y Operaciones de Software (DOSW)
 
---
 
| **DOSW** Desarrollo y Operaciones de Software | **ANÁLISIS DE REQUERIMIENTOS** | **Fecha:** | 01/07/2026 |
---
 
## FUNCIONALIDAD
 
| **Código:** | PKX – 001 |
|---|---|
| **Nombre:** | Registro de usuario |
 
| **Descripción:** | El sistema debe permitir a un usuario nuevo crear una cuenta proporcionando sus datos personales básicos para acceder a las funcionalidades de la Pokédex. |
|---|---|
| **Cómo se ejecutará:** | El usuario accede a la pantalla de registro, diligencia el formulario con sus datos y confirma la creación de la cuenta. |
| **Actor principal:** | Usuario no registrado |
| **Precondiciones:** | El usuario no debe tener una cuenta existente con el mismo email. |
 
### DATOS DE ENTRADA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| Username | Identificador de usuario | txt | Mínimo 4 caracteres, alfanumérico, no pueden existir duplicados | SI |
| Correo | Email (Funciona como UK) | txt | Debe cumplir formato válido de correos y debe ser existente | SI |
| Contraseña | Clave de acceso a la cuenta | txt | Debe tener más de 8 caracteres, al menos un carácter especial, una mayúscula y al menos 1 número | SI |
| Confirmación de contraseña | Validación de contraseña | txt | Debe coincidir con el campo de la contraseña | SI |
 
### DATOS DE SALIDA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| Confirmación | Registro exitoso, mensaje notifica al usuario por correo | txt | Se muestra al usuario tras la creación exitosa de la cuenta | SI |
| ErrorMessage | Registro no exitoso, se notifica dentro de la UI de la app | txt | Se muestra si algún dato no cumple las reglas o si el correo/nombre ya existe | SI |
 
### FLUJO BÁSICO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| 1 | Usuario | Accede a la pantalla de registro desde la pantalla de inicio | N/H |
| 2 | Sistema | Muestra el formulario de registro con los campos requeridos | N/H |
| 3 | Usuario | Diligencia campos: nombre de usuario, correo electrónico, contraseña y confirmar contraseña | N/H |
| 4 | Usuario | Presiona el botón de registro | N/H |
| 5 | Sistema | Valida que todos los campos sean obligatorios y cumplan las reglas definidas | E1, E2, E3 |
| 6 | Sistema | Verifica que el correo y el nombre de usuario no existan en el sistema | E4 |
| 7 | Sistema | Cifra la contraseña y almacena los datos del nuevo usuario | N/H |
| 8 | Sistema | Muestra mensaje de confirmación y redirige a la pantalla de inicio de sesión | N/H |
 
### FLUJO ALTERNO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| E1 | Sistema | Si algún campo obligatorio está vacío, muestra mensaje indicando los campos faltantes | N/H |
| E2 | Sistema | Si la contraseña no cumple los requisitos de seguridad, muestra mensaje con los criterios requeridos | N/H |
| E3 | Sistema | Si las contraseñas no coinciden, muestra mensaje de notificación | N/H |
| E4 | Sistema | Si el correo o nombre de usuario ya existe, muestra mensaje de notificación | N/H |
 
| **Notas y comentarios:** | N/H |
|---|---|
 
### ANEXOS
- Diagrama de casos de Uso
- Prototipos
### REGLAS DE NEGOCIO
 
| **No.** | **Descripción** |
|---|---|
| 1 | No pueden existir dos usuarios con el mismo correo electrónico. |
| 2 | No pueden existir dos usuarios con el mismo nombre de usuario. |
| 3 | Las contraseñas deben almacenarse cifradas (puede ser en hashMap), nunca en texto plano. |
 
---
 
## FUNCIONALIDAD
 
| **Código:** | PKX – 002 |
|---|---|
| **Nombre:** | Inicio de sesión |
 
| **Descripción:** | El sistema debe permitir a un usuario registrado autenticarse mediante su correo electrónico y contraseña para acceder a las funcionalidades de la Pokédex. |
|---|---|
| **Cómo se ejecutará:** | El usuario ingresa sus credenciales en la pantalla de login y el sistema valida la información para conceder acceso. |
| **Actor principal:** | Usuario registrado |
| **Precondiciones:** | El usuario debe tener una cuenta registrada en el sistema. |
 
### DATOS DE ENTRADA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| Correo | Correo asociado al ID del usuario | txt | Debe existir en el sistema | SI |
| Contraseña | Clave de acceso a la cuenta del usuario | txt | Se compara cifrada con la almacenada | SI |
 
### DATOS DE SALIDA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| Código OTP | Autenticación en 2 pasos, se envía código de 6 dígitos al correo del usuario | txt | Se genera al autenticar exitosamente. Tiene un tiempo de expiración definido | SI |
| Mensaje de Error | Credenciales incorrectas | txt | Se muestra si el correo no existe o la contraseña es incorrecta | SI |
 
### FLUJO BÁSICO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| 1 | Usuario | Accede a la pantalla de inicio de sesión | N/H |
| 2 | Sistema | Muestra el formulario con campos de correo y contraseña | N/H |
| 3 | Usuario | Ingresa su correo electrónico y contraseña | N/H |
| 4 | Usuario | Presiona el botón "Iniciar sesión" | N/H |
| 5 | Sistema | Valida que los campos no estén vacíos | E1 |
| 6 | Sistema | Verifica las credenciales en DB | E2 |
| 7 | Sistema | Genera el token de sesión y redirige al usuario a la pantalla principal de la Pokédex | N/H |
 
### FLUJO ALTERNO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| E1 | Sistema | Si algún campo está vacío, muestra mensaje solicitando completar los campos | N/H |
| E2 | Sistema | Si las credenciales son incorrectas, muestra mensaje de error | N/H |
 
| **Notas y comentarios:** | N/H |
|---|---|
 
### ANEXOS
- Diagrama de casos de Uso
- Prototipos
### REGLAS DE NEGOCIO
 
| **No.** | **Descripción** |
|---|---|
| 1 | El código OTP expira tras 30 minutos. |
 
---
 
## FUNCIONALIDAD
 
| **Código:** | PKX – 003 |
|---|---|
| **Nombre:** | Cerrar sesión |
 
| **Descripción:** | El sistema debe permitir al usuario finalizar su sesión activa, invalidando el token de autenticación y redirigiendo a la pantalla de inicio de sesión. |
|---|---|
| **Cómo se ejecutará:** | El usuario selecciona la opción de cerrar sesión desde el menú de la aplicación. |
| **Actor principal:** | Usuario autenticado |
| **Precondiciones:** | El usuario debe tener una sesión activa. |
 
### DATOS DE ENTRADA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| Token de sesión | Identificador de la sesión activa actual | txt | Se envía automáticamente desde el almacenamiento local del dispositivo | SI |
 
### DATOS DE SALIDA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| Mensaje de confirmación | Notificación de cierre exitoso | txt | Se muestra brevemente antes de redirigir | SI |
 
### FLUJO BÁSICO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| 1 | Usuario | Selecciona la opción "Cerrar sesión" desde el menú | N/H |
| 2 | Sistema | Invalida el token de sesión del usuario | N/H |
| 3 | Sistema | Limpia los datos de sesión almacenados localmente en el dispositivo | N/H |
| 4 | Sistema | Redirige al usuario a la pantalla de inicio de sesión | N/H |
 
**FLUJO ALTERNO:** N/H
 
| **Notas y comentarios:** | N/H |
|---|---|
 
### ANEXOS
- Diagrama de casos de Uso
- Prototipos
### REGLAS DE NEGOCIO
 
| **No.** | **Descripción** |
|---|---|
| 1 | Al cerrar sesión, toda la información en caché del usuario debe eliminarse del dispositivo. |
 
---
 
## FUNCIONALIDAD
 
| **Código:** | PKX – 004 |
|---|---|
| **Nombre:** | Gestión de perfil |
 
| **Descripción:** | El sistema debe permitir al usuario autenticado visualizar y modificar los datos de su perfil, como nombre de usuario y foto de perfil. |
|---|---|
| **Cómo se ejecutará:** | El usuario accede a la sección de perfil desde el menú y puede editar sus datos. |
| **Actor principal:** | Usuario autenticado |
| **Precondiciones:** | El usuario debe tener una sesión activa. |
 
### DATOS DE ENTRADA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| Nombre de usuario | Nuevo nombre de usuario | .txt | Mínimo 4 caracteres, alfanumérico, no pueden existir duplicados | NO |
| Foto de perfil | Imagen del usuario | .png o .jpg | Formatos aceptados: JPG, PNG. Tamaño máximo: 2 MB | NO |
| Contraseña actual | Contraseña vigente para confirmar cambios sensibles | .txt (contraseña) | Debe tener más de 8 caracteres, al menos un carácter especial, una mayúscula y al menos 1 número | CONDICIONAL |
| Nueva contraseña | Nueva clave de acceso | .txt (contraseña) | Debe tener más de 8 caracteres, al menos un carácter especial, una mayúscula y al menos 1 número | CONDICIONAL |
 
### DATOS DE SALIDA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| Datos del perfil actualizados | Información del perfil con los cambios aplicados | .Json | Se muestran los datos vigentes del usuario | SI |
| Mensaje de confirmación | Notificación de actualización exitosa | .txt | Se muestra tras guardar cambios | SI |
| Mensaje de error | Notificación de error en la actualización | .txt | Se muestra si alguna validación falla | SI |
 
### FLUJO BÁSICO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| 1 | USUARIO | Accede a la sección "Mi perfil" desde el menú | N/H |
| 2 | USUARIO | Muestra los datos actuales del perfil del usuario | N/H |
| 3 | USUARIO | Modifica los campos que desea actualizar | N/H |
| 4 | USUARIO | Presiona el botón "Guardar cambios" | N/H |
| 5 | SISTEMA | Valida los datos ingresados según las reglas definidas | E1, E2 |
| 6 | SISTEMA | Actualiza los datos en la base de datos y muestra mensaje de confirmación | N/H |
 
### FLUJO ALTERNO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| E1 | SISTEMA | Si el nuevo nombre de usuario ya existe, muestra mensaje indicando duplicado | N/H |
| E2 | SISTEMA | Si la contraseña actual es incorrecta al intentar cambiar contraseña, muestra error | N/H |
 
| **Notas y comentarios:** | El correo electrónico no es editable. |
|---|---|
 
### ANEXOS
- Prototipos
### REGLAS DE NEGOCIO
 
| **No.** | **Descripción** |
|---|---|
| 1 | El correo electrónico no puede ser modificado. |
| 2 | Para cambiar la contraseña, el usuario debe ingresar la contraseña actual. |
 
---
 
## FUNCIONALIDAD
 
| **Código:** | PKX – 005 |
|---|---|
| **Nombre:** | Listar Pokémon |
 
| **Descripción:** | El sistema debe permitir al usuario visualizar el catálogo completo de Pokémon disponibles, mostrando su información básica en formato de cuadrícula con paginación. |
|---|---|
| **Cómo se ejecutará:** | El usuario accede a la pantalla principal de la app y el sistema carga automáticamente el listado de Pokémon. |
| **Actor principal:** | Usuario autenticado |
| **Precondiciones:** | El usuario debe tener una sesión activa y conexión a internet. |
 
### DATOS DE ENTRADA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| Página actual | Número de página | numérico | Valor mínimo 1. Por defecto carga la página 1 | NO |
| Límite por página | Cantidad de Pokémon por página | numérico | Por defecto 20 Pokémon por página | NO |
 
### DATOS DE SALIDA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| Lista de Pokémon | Colección de Pokémon con información básica | lista | Cada elemento incluye número, nombre, sprite e imagen y tipo(s) | SI |
| Total de Pokémon | Cantidad total de Pokémon en el catálogo | numérico | Se usa para calcular el total de páginas | NO |
| Mensaje de error | Notificación si el catálogo no puede cargarse | .txt | Se muestra si hay fallo de conexión o error en la API | NO |
 
### FLUJO BÁSICO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| 1 | USUARIO | Accede a la pantalla principal de la app | N/H |
| 2 | SISTEMA | Realiza petición para obtener el listado | E1 |
| 3 | SISTEMA | Procesa la respuesta y organiza los datos | N/H |
| 4 | SISTEMA | Muestra los Pokémon en cuadrícula de 2 columnas con número, nombre, sprite y tipo(s) | N/H |
| 5 | USUARIO | Hace scroll hacia abajo para cargar más Pokémon | N/H |
| 6 | SISTEMA | Carga la siguiente página automáticamente | E1 |
 
### FLUJO ALTERNO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| E1 | SISTEMA | Si hay error de conexión o la API no responde, muestra mensaje de error con opción de reintentar | N/H |
 
| **Notas y comentarios:** | N/H |
|---|---|
 
### ANEXOS
- Diagrama de casos de Uso
- Prototipos
### REGLAS DE NEGOCIO
 
| **No.** | **Descripción** |
|---|---|
| 1 | La carga de Pokémon debe implementarse con paginación o scroll infinito para no afectar el rendimiento. |
| 2 | Cargar datos en Lazy. |
 
---
 
## FUNCIONALIDAD
 
| **Código:** | PKX – 006 |
|---|---|
| **Nombre:** | Buscar Pokémon |
 
| **Descripción:** | El sistema debe permitir al usuario buscar un Pokémon específico por su nombre o número de identificación, mostrando los resultados en tiempo real. |
|---|---|
| **Cómo se ejecutará:** | El usuario escribe en la barra de búsqueda y el sistema filtra los resultados mientras el usuario escribe. |
| **Actor principal:** | Usuario autenticado |
| **Precondiciones:** | El usuario debe tener una sesión activa y conexión a internet. |
 
### DATOS DE ENTRADA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| Término de búsqueda | Nombre o número del Pokémon a buscar | txt | Mínimo 1 carácter. No distingue mayúsculas de minúsculas. Acepta números enteros positivos para búsqueda por ID | SI |
 
### DATOS DE SALIDA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| Resultados de búsqueda | Lista de Pokémon que coinciden con el término | lista | Muestra número, nombre, sprite y tipo(s) de cada resultado | SI |
| Mensaje sin resultados | Notificación cuando no hay coincidencias | txt | Se muestra cuando la búsqueda no arroja resultados | NO |
| Mensaje de error | Notificación de error en la búsqueda | txt | Se muestra si hay fallo de conexión o error en la API | NO |
 
### FLUJO BÁSICO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| 1 | USUARIO | Selecciona la barra de búsqueda en la pantalla principal | N/H |
| 2 | SISTEMA | Activa el teclado y pone foco en el campo de búsqueda | N/H |
| 3 | USUARIO | Escribe el nombre o número del Pokémon | N/H |
| 4 | SISTEMA | Filtra los resultados en tiempo real mientras el usuario escribe | E1, E2 |
| 5 | SISTEMA | Muestra los Pokémon coincidentes en la cuadrícula | N/H |
| 6 | USUARIO | Selecciona un Pokémon del listado de resultados | N/H |
| 7 | SISTEMA | Redirige a la pantalla de detalle del Pokémon seleccionado | N/H |
 
### FLUJO ALTERNO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| E1 | SISTEMA | Si no hay coincidencias, muestra mensaje "No se encontraron resultados para tu búsqueda" | N/H |
| E2 | SISTEMA | Si hay error de conexión, muestra mensaje de error con opción de reintentar | N/H |
 
| **Notas y comentarios:** | Se recomienda implementar debounce de 300ms para no disparar búsquedas en cada tecla y reducir llamadas a la API. |
|---|---|
 
### ANEXOS
- Diagrama de casos de Uso
- Prototipos
### REGLAS DE NEGOCIO
 
| **No.** | **Descripción** |
|---|---|
| 1 | La búsqueda no distingue entre mayúsculas y minúsculas. |
| 2 | Si el usuario busca por número, debe ser un entero positivo válido dentro del rango de la PokéAPI. |
 
---
 
## FUNCIONALIDAD
 
| **Código:** | PKX – 007 |
|---|---|
| **Nombre:** | Filtrar Pokémon |
 
| **Descripción:** | El sistema debe permitir al usuario filtrar el catálogo de Pokémon por tipo y generación para reducir los resultados según sus intereses. |
|---|---|
| **Cómo se ejecutará:** | El usuario accede al panel de filtros desde el ícono de filtro en la pantalla principal, selecciona los criterios deseados y aplica el filtro. |
| **Actor principal:** | Usuario autenticado |
| **Precondiciones:** | El usuario debe tener una sesión activa y conexión a internet. |
 
### DATOS DE ENTRADA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| Tipo de Pokémon | Tipo elemental por el que filtrar | selector múltiple | Opciones: Fire, Water, Grass, Electric, Psychic, Normal, Fighting, entre otros. Permite selección múltiple | NO |
| Generación | Generación del Pokémon por la que filtrar | selector | Opciones: Generación 1 al 9 | NO |
 
### DATOS DE SALIDA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| Lista filtrada | Pokémon que cumplen los criterios seleccionados | lista | Muestra número, nombre, sprite y tipo(s) | SI |
| Cantidad de resultados | Total de Pokémon que cumplen el filtro | numérico | Se muestra al usuario antes de aplicar el filtro | SI |
| Mensaje sin resultados | Notificación cuando no hay Pokémon con esos criterios | txt | Se muestra cuando el filtro no arroja resultados | NO |
 
### FLUJO BÁSICO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| 1 | USUARIO | Presiona el ícono de filtro en la pantalla principal | N/H |
| 2 | SISTEMA | Muestra el panel de filtros con las opciones disponibles | N/H |
| 3 | USUARIO | Selecciona uno o más tipos y/o una generación | N/H |
| 4 | SISTEMA | Muestra en tiempo real la cantidad de resultados que coinciden | N/H |
| 5 | USUARIO | Presiona el botón "Aplicar filtros" | N/H |
| 6 | SISTEMA | Muestra el catálogo filtrado con los Pokémon que cumplen los criterios | E1 |
 
### FLUJO ALTERNO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| E1 | SISTEMA | Si no hay Pokémon que cumplan los criterios, muestra mensaje indicando que no hay resultados y sugiere limpiar los filtros | N/H |
| E2 | USUARIO | Presiona "Limpiar filtros" para restablecer el catálogo completo | N/H |
 
| **Notas y comentarios:** | Los filtros pueden combinarse entre sí. |
|---|---|
 
### ANEXOS
- Diagrama de casos de Uso
- Prototipos
### REGLAS DE NEGOCIO
 
| **No.** | **Descripción** |
|---|---|
| 1 | Los filtros de tipo y generación pueden aplicarse simultáneamente. |
| 2 | Un Pokémon con tipo dual aparece en los resultados si al menos uno de sus tipos coincide con el filtro. |
| 3 | Al limpiar filtros, el catálogo vuelve al listado completo. |
 
---
 
## FUNCIONALIDAD
 
| **Código:** | PKX – 008 |
|---|---|
| **Nombre:** | Ver detalle de Pokémon |
 
| **Descripción:** | El sistema debe permitir al usuario visualizar la información completa de un Pokémon seleccionado, incluyendo sus estadísticas base, tipos, habilidades y descripción. |
|---|---|
| **Cómo se ejecutará:** | El usuario selecciona un Pokémon del catálogo y el sistema carga su pantalla de detalle completa. |
| **Actor principal:** | Usuario autenticado |
| **Precondiciones:** | El usuario debe tener una sesión activa y conexión a internet. |
 
### DATOS DE ENTRADA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| ID del Pokémon | Número de identificación del Pokémon seleccionado | numérico | Debe ser un ID válido dentro del rango de la PokéAPI. Se envía automáticamente al seleccionar del listado | SI |
 
### DATOS DE SALIDA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| Nombre | Nombre del Pokémon | txt | Formateado con primera letra en mayúscula | SI |
| Número | Número de identificación en la Pokédex | numérico | Mostrado con formato #001, #025, etc. | SI |
| Sprite / Imagen | Imagen oficial del Pokémon | imagen | Se consume desde la PokéAPI | SI |
| Tipo(s) | Tipo o tipos elementales del Pokémon | lista | Máximo 2 tipos. Mostrados como badges con color por tipo | SI |
| Estadísticas base | HP, Ataque, Defensa, Ataque Especial, Defensa Especial, Velocidad | lista numérica | Mostradas como barras de progreso proporcionales al valor máximo posible | SI |
| Habilidades | Lista de habilidades del Pokémon | lista (txt) | Incluye nombre de la habilidad y si es oculta o no | SI |
| Descripción | Texto descriptivo del Pokémon | txt | Consumido desde la PokéAPI en español si está disponible | SI |
| Mensaje de error | Notificación si el detalle no puede cargarse | txt | Se muestra si hay fallo de conexión | NO |
 
### FLUJO BÁSICO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| 1 | USUARIO | Selecciona un Pokémon del catálogo, búsqueda o colección | N/H |
| 2 | SISTEMA | Realiza petición a la PokéAPI con el ID del Pokémon | E1 |
| 3 | SISTEMA | Procesa y organiza la información recibida | N/H |
| 4 | SISTEMA | Muestra la pantalla de detalle con toda la información del Pokémon | N/H |
| 5 | USUARIO | Puede marcar el Pokémon como capturado o favorito desde esta pantalla | N/H |
 
### FLUJO ALTERNO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| E1 | SISTEMA | Si hay error de conexión o el ID no es válido, muestra mensaje de error con opción de volver al listado | N/H |
 
| **Notas y comentarios:** | La descripción se muestra en español si la PokéAPI la tiene disponible, de lo contrario en inglés. |
|---|---|
 
### ANEXOS
- Diagrama de casos de Uso
- Prototipos
### REGLAS DE NEGOCIO
 
| **No.** | **Descripción** |
|---|---|
| 1 | La descripción del Pokémon se muestra en español si está disponible, de lo contrario en inglés. |
| 2 | Las estadísticas se muestran como barras de progreso con el valor numérico visible. |
 
---
 
## FUNCIONALIDAD
 
| **Código:** | PKX – 009 |
|---|---|
| **Nombre:** | Ver cadena evolutiva |
 
| **Descripción:** | El sistema debe permitir al usuario visualizar la cadena evolutiva completa del Pokémon que está consultando, mostrando cada etapa con su sprite y condición de evolución. |
|---|---|
| **Cómo se ejecutará:** | Desde la pantalla de detalle del Pokémon, el sistema muestra automáticamente la cadena evolutiva en la parte inferior de la pantalla. |
| **Actor principal:** | Usuario autenticado |
| **Precondiciones:** | El usuario debe estar en la pantalla de detalle de un Pokémon con sesión activa y conexión a internet. |
 
### DATOS DE ENTRADA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| ID de la cadena evolutiva | Identificador de la cadena evolutiva del Pokémon | numérico | Se obtiene automáticamente desde la PokéAPI a partir del ID del Pokémon | SI |
 
### DATOS DE SALIDA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| Etapas de evolución | Lista de Pokémon que conforman la cadena evolutiva | lista | Cada etapa incluye sprite, nombre y número | SI |
| Condición de evolución | Requisito para evolucionar de una etapa a la siguiente | txt | Ejemplos: nivel mínimo, piedra de evolución, amistad | SI |
| Mensaje sin cadena | Notificación cuando el Pokémon no tiene evoluciones | txt | Se muestra para Pokémon que no evolucionan | NO |
 
### FLUJO BÁSICO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| 1 | SISTEMA | Al cargar la pantalla de detalle, realiza petición a la PokéAPI para obtener la cadena evolutiva | E1 |
| 2 | SISTEMA | Procesa la cadena y organiza las etapas en orden | N/H |
| 3 | SISTEMA | Muestra la cadena en formato horizontal con flechas entre etapas y condición de evolución | N/H |
| 4 | USUARIO | Puede presionar cualquier Pokémon de la cadena para ir a su pantalla de detalle | N/H |
 
### FLUJO ALTERNO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| E1 | SISTEMA | Si hay error al cargar la cadena evolutiva, muestra mensaje sin bloquear el resto del detalle | N/H |
| E2 | SISTEMA | Si el Pokémon no tiene evoluciones, muestra mensaje "Este Pokémon no tiene evoluciones" | N/H |
 
| **Notas y comentarios:** | Pokémon con evoluciones ramificadas (como Eevee) deben mostrar todas las ramas posibles. |
|---|---|
 
### ANEXOS
- Diagrama de casos de Uso
- Prototipos
### REGLAS DE NEGOCIO
 
| **No.** | **Descripción** |
|---|---|
| 1 | Pokémon con evoluciones ramificadas deben mostrar todas las ramas posibles. |
| 2 | Al presionar un Pokémon de la cadena evolutiva, navega a su pantalla de detalle. |
| 3 | La cadena evolutiva no bloquea la carga del resto del detalle si falla. |
 
---
 
## FUNCIONALIDAD
 
| **Código:** | PKX – 010 |
|---|---|
| **Nombre:** | Marcar Pokémon como capturado |
 
| **Descripción:** | El sistema debe permitir al usuario registrar un Pokémon en su colección personal marcándolo como capturado. |
|---|---|
| **Cómo se ejecutará:** | Desde la pantalla de detalle del Pokémon, el usuario presiona el botón "Marcar como capturado" y el sistema registra el Pokémon en su colección. |
| **Actor principal:** | Usuario autenticado |
| **Precondiciones:** | El usuario debe tener una sesión activa y estar en la pantalla de detalle de un Pokémon. |
 
### DATOS DE ENTRADA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| ID del Pokémon | Identificador del Pokémon a marcar como capturado | numérico | Debe ser un ID válido de la PokéAPI. Se envía automáticamente desde la pantalla de detalle | SI |
| ID del usuario | Identificador del usuario autenticado | numérico | Se obtiene automáticamente desde el token de sesión | SI |
 
### DATOS DE SALIDA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| Confirmación | Notificación de Pokémon marcado como capturado | txt | Se muestra al usuario tras el registro exitoso | SI |
| Estado del botón | El botón cambia visualmente para indicar que ya está capturado | UI | El botón se activa o desactiva según el estado actual | SI |
| Mensaje de error | Notificación de error al registrar | txt | Se muestra si hay fallo de conexión o error en el servidor | NO |
 
### FLUJO BÁSICO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| 1 | USUARIO | Presiona el botón "Marcar como capturado" en la pantalla de detalle | N/H |
| 2 | SISTEMA | Verifica si el Pokémon ya está en la colección del usuario | E1 |
| 3 | SISTEMA | Registra el Pokémon en la colección del usuario en la base de datos | E2 |
| 4 | SISTEMA | Actualiza el estado del botón y muestra mensaje de confirmación | N/H |
 
### FLUJO ALTERNO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| E1 | SISTEMA | Si el Pokémon ya está marcado como capturado, el botón permite desmarcarlo | N/H |
| E2 | SISTEMA | Si hay error de conexión, muestra mensaje de error | N/H |
 
| **Notas y comentarios:** | N/H |
|---|---|
 
### ANEXOS
- Diagrama de casos de Uso
- Prototipos
### REGLAS DE NEGOCIO
 
| **No.** | **Descripción** |
|---|---|
| 1 | Un mismo Pokémon no puede registrarse dos veces como capturado para el mismo usuario. |
| 2 | El Pokémon capturado queda disponible para publicar en el mercado. |
 
---
 
## FUNCIONALIDAD
 
| **Código:** | PKX – 011 |
|---|---|
| **Nombre:** | Marcar Pokémon como favorito |
 
| **Descripción:** | El sistema debe permitir al usuario agregar o quitar un Pokémon de su lista de favoritos. |
|---|---|
| **Cómo se ejecutará:** | Desde la pantalla de detalle del Pokémon, el usuario presiona el botón de favorito y el sistema actualiza su lista. |
| **Actor principal:** | Usuario autenticado |
| **Precondiciones:** | El usuario debe tener una sesión activa y estar en la pantalla de detalle de un Pokémon. |
 
### DATOS DE ENTRADA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| ID del Pokémon | Identificador del Pokémon a marcar como favorito | numérico | Debe ser un ID válido de la PokéAPI. Se envía automáticamente desde la pantalla de detalle | SI |
| ID del usuario | Identificador del usuario autenticado | numérico | Se obtiene automáticamente desde el token de sesión | SI |
 
### DATOS DE SALIDA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| Confirmación | Notificación de Pokémon agregado o eliminado de favoritos | txt | Se muestra al usuario tras la acción exitosa | SI |
| Estado del botón | El ícono de favorito cambia visualmente según el estado | UI | Ícono relleno si es favorito, vacío si no lo es | SI |
| Mensaje de error | Notificación de error al actualizar favoritos | txt | Se muestra si hay fallo de conexión o error en el servidor | NO |
 
### FLUJO BÁSICO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| 1 | USUARIO | Presiona el ícono de favorito en la pantalla de detalle | N/H |
| 2 | SISTEMA | Verifica si el Pokémon ya está en la lista de favoritos del usuario | N/H |
| 3 | SISTEMA | Agrega o elimina el Pokémon de la lista de favoritos según el estado actual | E1 |
| 4 | SISTEMA | Actualiza el ícono visualmente y muestra mensaje de confirmación | N/H |
 
### FLUJO ALTERNO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| E1 | SISTEMA | Si hay error de conexión, muestra mensaje de error y no cambia el estado del ícono | N/H |
 
| **Notas y comentarios:** | No es necesario que el Pokémon esté capturado para marcarlo como favorito. |
|---|---|
 
### ANEXOS
- Diagrama de casos de Uso
- Prototipos
### REGLAS DE NEGOCIO
 
| **No.** | **Descripción** |
|---|---|
| 1 | Un Pokémon puede ser favorito sin estar capturado. |
| 2 | El usuario puede tener ilimitados Pokémon favoritos. |
 
---
 
## FUNCIONALIDAD
 
| **Código:** | PKX – 012 |
|---|---|
| **Nombre:** | Ver colección personal |
 
| **Descripción:** | El sistema debe permitir al usuario visualizar su colección personal de Pokémon capturados y favoritos en una pantalla dedicada. |
|---|---|
| **Cómo se ejecutará:** | El usuario accede a la sección "Mi colección" desde la barra de navegación y el sistema carga sus listas. |
| **Actor principal:** | Usuario autenticado |
| **Precondiciones:** | El usuario debe tener una sesión activa. |
 
### DATOS DE ENTRADA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| ID del usuario | Identificador del usuario autenticado | numérico | Se obtiene automáticamente desde el token de sesión | SI |
| Tab seleccionado | Indica si el usuario ve capturados o favoritos | selector | Valores posibles: "Capturados" o "Favoritos". Por defecto muestra "Capturados" | NO |
 
### DATOS DE SALIDA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| Lista de capturados | Pokémon marcados como capturados por el usuario | lista | Muestra número, nombre, sprite y tipo(s) de cada Pokémon | SI |
| Lista de favoritos | Pokémon marcados como favoritos por el usuario | lista | Muestra número, nombre, sprite y tipo(s) de cada Pokémon | SI |
| Mensaje colección vacía | Notificación cuando el usuario no tiene Pokémon en la lista | txt | Se muestra si la lista seleccionada está vacía | NO |
 
### FLUJO BÁSICO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| 1 | USUARIO | Accede a la sección "Mi colección" desde la barra de navegación | N/H |
| 2 | SISTEMA | Carga la lista de Pokémon capturados del usuario por defecto | E1 |
| 3 | SISTEMA | Muestra los Pokémon en cuadrícula con número, nombre, sprite y tipo(s) | N/H |
| 4 | USUARIO | Cambia al tab "Favoritos" para ver su lista de favoritos | N/H |
| 5 | SISTEMA | Carga y muestra la lista de Pokémon favoritos del usuario | E1 |
| 6 | USUARIO | Selecciona un Pokémon para ver su detalle | N/H |
| 7 | SISTEMA | Redirige a la pantalla de detalle del Pokémon seleccionado | N/H |
 
### FLUJO ALTERNO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| E1 | SISTEMA | Si la lista está vacía, muestra mensaje indicando que no hay Pokémon en esa sección | N/H |
 
| **Notas y comentarios:** | N/H |
|---|---|
 
### ANEXOS
- Diagrama de casos de Uso
- Prototipos
### REGLAS DE NEGOCIO
 
| **No.** | **Descripción** |
|---|---|
| 1 | Solo se muestran los Pokémon asociados al usuario autenticado. |
| 2 | La colección se organiza en dos tabs: Capturados y Favoritos. |
 
---
 
## FUNCIONALIDAD
 
| **Código:** | PKX – 013 |
|---|---|
| **Nombre:** | Comparar Pokémon |
 
| **Descripción:** | El sistema debe permitir al usuario seleccionar dos Pokémon y comparar sus estadísticas base lado a lado. |
|---|---|
| **Cómo se ejecutará:** | El usuario accede a la pantalla de comparación, selecciona dos Pokémon y el sistema muestra sus estadísticas enfrentadas. |
| **Actor principal:** | Usuario autenticado |
| **Precondiciones:** | El usuario debe tener una sesión activa y conexión a internet. |
 
### DATOS DE ENTRADA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| ID Pokémon 1 | Identificador del primer Pokémon a comparar | numérico | Debe ser un ID válido de la PokéAPI. Debe ser diferente al ID del Pokémon 2 | SI |
| ID Pokémon 2 | Identificador del segundo Pokémon a comparar | numérico | Debe ser un ID válido de la PokéAPI. Debe ser diferente al ID del Pokémon 1 | SI |
 
### DATOS DE SALIDA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| Comparación de stats | HP, Ataque, Defensa, Ataque Especial, Defensa Especial y Velocidad de ambos Pokémon | lista numérica | La estadística superior se resalta visualmente en cada categoría | SI |
| Sprites | Imágenes de ambos Pokémon | imagen | Se consumen desde la PokéAPI | SI |
| Nombres y tipos | Nombre y tipo(s) de cada Pokémon | txt / lista | Mostrados como encabezado de cada columna | SI |
| Mensaje de error | Notificación si no puede cargarse algún Pokémon | txt | Se muestra si hay fallo de conexión | NO |
 
### FLUJO BÁSICO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| 1 | USUARIO | Accede a la pantalla de comparación desde la barra de navegación | N/H |
| 2 | SISTEMA | Muestra dos slots vacíos para seleccionar los Pokémon a comparar | N/H |
| 3 | USUARIO | Selecciona el primer Pokémon buscándolo por nombre o número | N/H |
| 4 | USUARIO | Selecciona el segundo Pokémon buscándolo por nombre o número | E1 |
| 5 | SISTEMA | Consulta la PokéAPI para obtener los datos de ambos Pokémon | E2 |
| 6 | SISTEMA | Muestra las estadísticas de ambos Pokémon lado a lado, resaltando el valor superior en cada categoría | N/H |
 
### FLUJO ALTERNO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| E1 | SISTEMA | Si el usuario selecciona el mismo Pokémon dos veces, muestra mensaje indicando que deben ser Pokémon diferentes | N/H |
| E2 | SISTEMA | Si hay error de conexión, muestra mensaje de error con opción de reintentar | N/H |
 
| **Notas y comentarios:** | N/H |
|---|---|
 
### ANEXOS
- Diagrama de casos de Uso
- Prototipos
### REGLAS DE NEGOCIO
 
| **No.** | **Descripción** |
|---|---|
| 1 | Los dos Pokémon seleccionados deben ser diferentes. |
| 2 | La estadística más alta de cada categoría se resalta visualmente. |
 
---
 
## FUNCIONALIDAD
 
| **Código:** | PKX – 014 |
|---|---|
| **Nombre:** | Publicar Pokémon en el mercado |
 
| **Descripción:** | El sistema debe permitir al usuario ofrecer un Pokémon de su colección personal para intercambio en el mercado. |
|---|---|
| **Cómo se ejecutará:** | El usuario accede a la pantalla del mercado, selecciona un Pokémon de su colección de capturados y lo publica para intercambio. |
| **Actor principal:** | Usuario autenticado |
| **Precondiciones:** | El usuario debe tener una sesión activa y al menos un Pokémon capturado en su colección. |
 
### DATOS DE ENTRADA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| ID del Pokémon | Identificador del Pokémon a publicar | numérico | Debe pertenecer a la colección de capturados del usuario. No puede estar ya publicado en el mercado | SI |
| Descripción de la oferta | Nota o condición que el usuario desea incluir en la publicación | txt | Máximo 200 caracteres | NO |
 
### DATOS DE SALIDA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| Confirmación | Notificación de publicación exitosa en el mercado | txt | Se muestra al usuario tras publicar exitosamente | SI |
| Publicación en mercado | El Pokémon aparece en el listado del mercado visible para otros usuarios | objeto | Incluye sprite, nombre, tipo(s), nombre del usuario dueño y descripción | SI |
| Mensaje de error | Notificación de error al publicar | txt | Se muestra si hay fallo de conexión o el Pokémon ya está publicado | NO |
 
### FLUJO BÁSICO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| 1 | USUARIO | Accede a la pantalla de publicar desde el mercado | N/H |
| 2 | SISTEMA | Muestra la colección de Pokémon capturados del usuario disponibles para publicar | E1 |
| 3 | USUARIO | Selecciona el Pokémon que desea publicar | N/H |
| 4 | USUARIO | Agrega una descripción opcional a la publicación | N/H |
| 5 | USUARIO | Presiona el botón "Publicar" | N/H |
| 6 | SISTEMA | Registra la publicación en el mercado y muestra confirmación | E2 |
 
### FLUJO ALTERNO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| E1 | SISTEMA | Si el usuario no tiene Pokémon capturados, muestra mensaje indicando que debe capturar Pokémon primero | N/H |
| E2 | SISTEMA | Si hay error de conexión al publicar, muestra mensaje de error | N/H |
 
| **Notas y comentarios:** | N/H |
|---|---|
 
### ANEXOS
- Diagrama de casos de Uso
- Prototipos
### REGLAS DE NEGOCIO
 
| **No.** | **Descripción** |
|---|---|
| 1 | Solo se pueden publicar Pokémon que estén en la colección de capturados del usuario. |
| 2 | Un Pokémon no puede publicarse dos veces simultáneamente en el mercado. |
| 3 | El usuario puede eliminar su publicación del mercado en cualquier momento. |
 
---
 
## FUNCIONALIDAD
 
| **Código:** | PKX – 015 |
|---|---|
| **Nombre:** | Explorar el mercado |
 
| **Descripción:** | El sistema debe permitir al usuario visualizar las publicaciones de otros usuarios con Pokémon disponibles para intercambio. |
|---|---|
| **Cómo se ejecutará:** | El usuario accede a la sección de mercado desde la barra de navegación y el sistema carga las publicaciones disponibles. |
| **Actor principal:** | Usuario autenticado |
| **Precondiciones:** | El usuario debe tener una sesión activa y conexión a internet. |
 
### DATOS DE ENTRADA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| ID del usuario | Identificador del usuario autenticado | numérico | Se obtiene desde el token de sesión. Se usa para excluir las propias publicaciones del listado | SI |
 
### DATOS DE SALIDA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| Lista de publicaciones | Pokémon publicados por otros usuarios disponibles para intercambio | lista | Cada publicación incluye sprite, nombre, tipo(s), nombre del dueño y descripción | SI |
| Mensaje mercado vacío | Notificación cuando no hay publicaciones disponibles | txt | Se muestra si no hay publicaciones en el mercado | NO |
| Mensaje de error | Notificación si el mercado no puede cargarse | txt | Se muestra si hay fallo de conexión | NO |
 
### FLUJO BÁSICO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| 1 | USUARIO | Accede a la sección "Mercado" desde la barra de navegación | N/H |
| 2 | SISTEMA | Carga las publicaciones disponibles excluyendo las del usuario autenticado | E1, E2 |
| 3 | SISTEMA | Muestra las publicaciones en formato de lista con la información de cada Pokémon | N/H |
| 4 | USUARIO | Selecciona una publicación para solicitar un intercambio | N/H |
| 5 | SISTEMA | Redirige al flujo de solicitud de intercambio (PKX-016) | N/H |
 
### FLUJO ALTERNO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| E1 | SISTEMA | Si no hay publicaciones disponibles, muestra mensaje indicando que el mercado está vacío | N/H |
| E2 | SISTEMA | Si hay error de conexión, muestra mensaje de error con opción de reintentar | N/H |
 
| **Notas y comentarios:** | N/H |
|---|---|
 
### ANEXOS
- Diagrama de casos de Uso
- Prototipos
### REGLAS DE NEGOCIO
 
| **No.** | **Descripción** |
|---|---|
| 1 | El usuario no puede ver ni solicitar intercambio sobre sus propias publicaciones. |
| 2 | Solo se muestran publicaciones activas que no tengan un intercambio en proceso. |
 
---
 
## FUNCIONALIDAD
 
| **Código:** | PKX – 016 |
|---|---|
| **Nombre:** | Solicitar intercambio |
 
| **Descripción:** | El sistema debe permitir al usuario enviar una solicitud de intercambio al dueño de una publicación en el mercado, ofreciendo uno de sus Pokémon capturados a cambio. |
|---|---|
| **Cómo se ejecutará:** | El usuario selecciona una publicación del mercado, elige el Pokémon que ofrece a cambio y envía la solicitud al dueño. |
| **Actor principal:** | Usuario autenticado |
| **Precondiciones:** | El usuario debe tener una sesión activa, al menos un Pokémon capturado disponible y haber seleccionado una publicación del mercado. |
 
### DATOS DE ENTRADA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| ID de la publicación | Identificador de la publicación del mercado sobre la que se solicita el intercambio | numérico | Debe corresponder a una publicación activa de otro usuario | SI |
| ID del Pokémon ofrecido | Identificador del Pokémon que el usuario ofrece a cambio | numérico | Debe pertenecer a la colección de capturados del usuario solicitante | SI |
| Mensaje opcional | Texto adicional que el usuario puede incluir en la solicitud | txt | Máximo 200 caracteres | NO |
 
### DATOS DE SALIDA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| Confirmación | Notificación de solicitud enviada exitosamente | txt | Se muestra al usuario tras enviar la solicitud | SI |
| Notificación al dueño | El dueño de la publicación recibe una notificación de la solicitud | notificación | Se envía al usuario receptor dentro de la app | SI |
| Mensaje de error | Notificación de error al enviar la solicitud | txt | Se muestra si hay fallo de conexión o la publicación ya no está disponible | NO |
 
### FLUJO BÁSICO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| 1 | USUARIO | Selecciona una publicación del mercado y presiona "Solicitar intercambio" | N/H |
| 2 | SISTEMA | Muestra la colección de Pokémon capturados del usuario disponibles para ofrecer | E1 |
| 3 | USUARIO | Selecciona el Pokémon que desea ofrecer a cambio | N/H |
| 4 | USUARIO | Agrega un mensaje opcional y confirma la solicitud | N/H |
| 5 | SISTEMA | Registra la solicitud y envía notificación al dueño de la publicación | E2 |
| 6 | SISTEMA | Muestra confirmación al usuario solicitante | N/H |
 
### FLUJO ALTERNO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| E1 | SISTEMA | Si el usuario no tiene Pokémon capturados disponibles, muestra mensaje indicando que debe capturar Pokémon primero | N/H |
| E2 | SISTEMA | Si la publicación ya no está disponible, muestra mensaje indicando que fue retirada o ya tiene un intercambio en proceso | N/H |
 
| **Notas y comentarios:** | N/H |
|---|---|
 
### ANEXOS
- Diagrama de casos de Uso
- Prototipos
### REGLAS DE NEGOCIO
 
| **No.** | **Descripción** |
|---|---|
| 1 | Un usuario no puede enviarse solicitudes de intercambio a sí mismo. |
| 2 | El Pokémon ofrecido debe pertenecer a la colección de capturados del solicitante. |
 
---
 
## FUNCIONALIDAD
 
| **Código:** | PKX – 017 |
|---|---|
| **Nombre:** | Aceptar o rechazar intercambio |
 
| **Descripción:** | El sistema debe permitir al usuario gestionar las solicitudes de intercambio que ha recibido, pudiendo aceptarlas o rechazarlas. |
|---|---|
| **Cómo se ejecutará:** | El usuario accede a su bandeja de solicitudes recibidas, revisa cada oferta y decide aceptar o rechazar. |
| **Actor principal:** | Usuario autenticado |
| **Precondiciones:** | El usuario debe tener una sesión activa y al menos una solicitud de intercambio pendiente. |
 
### DATOS DE ENTRADA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| ID de la solicitud | Identificador de la solicitud de intercambio a gestionar | numérico | Debe corresponder a una solicitud activa recibida por el usuario | SI |
| Decisión | Acción que toma el usuario sobre la solicitud | booleano | Valores posibles: Aceptar o Rechazar | SI |
 
### DATOS DE SALIDA
 
| **Nombre** | **Descripción** | **Tipo de campo** | **Reglas / Aplicación** | **Obligatorio** |
|---|---|---|---|---|
| Confirmación de intercambio | Si acepta, los Pokémon se intercambian entre las colecciones de ambos usuarios | objeto | El Pokémon ofrecido pasa a la colección del receptor y viceversa | CONDICIONAL |
| Notificación al solicitante | El solicitante recibe notificación con la decisión tomada | notificación | Se envía dentro de la app indicando si fue aceptado o rechazado | SI |
| Confirmación de rechazo | Si rechaza, la publicación vuelve a estar disponible en el mercado | txt | Se muestra al usuario receptor tras rechazar | CONDICIONAL |
| Mensaje de error | Notificación de error al procesar la decisión | txt | Se muestra si hay fallo de conexión | NO |
 
### FLUJO BÁSICO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| 1 | USUARIO | Accede a la pantalla de gestión de solicitudes recibidas | N/H |
| 2 | SISTEMA | Muestra la lista de solicitudes pendientes con el Pokémon ofrecido y el usuario solicitante | E1 |
| 3 | USUARIO | Revisa los detalles de una solicitud | N/H |
| 4 | USUARIO | Presiona "Aceptar" o "Rechazar" | N/H |
| 5a | SISTEMA | Si acepta: intercambia los Pokémon entre ambas colecciones, cierra la publicación y notifica al solicitante | E2 |
| 5b | SISTEMA | Si rechaza: descarta la solicitud, la publicación vuelve a estar disponible y notifica al solicitante | E2 |
 
### FLUJO ALTERNO
 
| **Paso** | **Actor** | **Descripción** | **Excepciones** |
|---|---|---|---|
| E1 | SISTEMA | Si no hay solicitudes pendientes, muestra mensaje indicando que no tiene solicitudes activas | N/H |
| E2 | SISTEMA | Si hay error de conexión al procesar la decisión, muestra mensaje de error y no realiza ningún cambio | N/H |
 
| **Notas y comentarios:** | Al aceptar un intercambio, ambas colecciones deben actualizarse de forma atómica para evitar inconsistencias. |
|---|---|
 
### ANEXOS
- Diagrama de casos de Uso
- Prototipos
### REGLAS DE NEGOCIO
 
| **No.** | **Descripción** |
|---|---|
| 1 | Al aceptar el intercambio, los Pokémon se transfieren simultáneamente en ambas colecciones. |

---
 
## ABREVIATURAS
 
| **Abreviatura** | **Significado** |
|---|---|
| PKX | Código de requerimiento del proyecto Pokédex WildDex |
| DB | Base de datos |
| API | Application Programming Interface |
| UI | User Interface |
| N/H | No Hay |
 
---
 
| William Santiago Ruiz Medina | | 01/07/2026 | Versión inicial del documento. |
