# Android Firebase App

- [Android Project with Firebase](#android-project-with-firebase)
- [Proyecto Android con Firebase](#Proyecto-Android-con-Firebase)

# Android Project with Firebase

This repository contains the source code for a native Android mobile application written in Kotlin.
The application utilizes various tools from the Firebase suite to efficiently manage the backend services.
The key features and Firebase tools used are outlined below:

## Key Features

- **Authentication:**
  - Anonymous authentication.
  - Session-protected authentication with email and password.
  - Google login.

![image](https://github.com/Camilo-Hernandez/android-firebase-app/assets/36543483/b1c3b535-dde5-4a8b-8fc9-442281e0eab3)

- **Data Storage:**
  - Contact storage using **Realtime Database**.
 
![image](https://github.com/Camilo-Hernandez/android-firebase-app/assets/36543483/e1210b1b-e075-4267-983d-98dfe284d75b)

  - Storage of photos taken with the camera in **Cloud Storage**.
 
![image](https://github.com/Camilo-Hernandez/android-firebase-app/assets/36543483/a4fdc709-be29-4420-9dbc-bae89e1f4369)

  
  - Note storage using **Cloud Firestore**.

![image](https://github.com/Camilo-Hernandez/android-firebase-app/assets/36543483/d7d6b67d-2346-4d32-97d8-eba3a33d4a9e)


- **Other Features:**
  - Integration of **Analytics** for user behavior analysis.
  - Use of **Crashlytics** for error and crash monitoring.
  - Implementation of **RemoteConfig** to dynamically change the app's appearance without relaunching it.
  - **Room Local Database** to perform synchronization with **Cloud Firestore**.
  - Utilization of **Firebase Cloud Messaging (FCM)** for sending personalized push notifications with specific content.

![image](https://github.com/Camilo-Hernandez/android-firebase-app/assets/36543483/b5bd6b2b-9445-4bbe-a7cf-af036d1116cc)


## Project Setup

1. Clone this repository: `git clone https://github.com/your_username/your_project.git`
2. Open the project in Android Studio.
3. Configure your project in the [Firebase Console](https://console.firebase.google.com/).
4. Download the `google-services.json` configuration file and place it in the `app/` directory of your project.

## Project Structure

The project is structured as follows:

- **`app/`**: Contains the source code of the application.
- **`gradle/`**: Gradle-specific configurations.

## Usage Instructions

To run the application, follow these steps:

1. Connect an Android device or use an emulator.
2. Run the application from Android Studio.

The application should now be up and running on your device or emulator.

## Contributions

If you wish to contribute to this project, please follow these steps:

1. Fork the repository.
2. Create a new branch for your feature: `git checkout -b feature/new_feature`.
3. Make your changes and commit: `git commit -m 'Add new_feature'`.
4. Push the branch: `git push origin feature/new_feature`.
5. Open a pull request on GitHub.

Thank you for contributing!

## License

This project is under the [MIT License](LICENSE).


We hope you enjoy exploring and contributing to this project. Happy coding!

---
---

# Proyecto Android con Firebase

Este repositorio contiene el código fuente de una aplicación móvil nativa para Android escrita en Kotlin.
La aplicación utiliza varias herramientas de la suite de Firebase para gestionar el backend de manera rápida y efectiva.
A continuación, se describen las principales características y las herramientas de Firebase utilizadas:

## Características Principales

- **Autenticación:**
  - Autenticación anónima.
  - Autenticación de sesión protegida con correo y contraseña.
  - Inicio de sesión con Google.

- **Almacenamiento de Datos:**
  - Guardado de contactos utilizando **Realtime Database**.

![image](https://github.com/Camilo-Hernandez/android-firebase-app/assets/36543483/e1210b1b-e075-4267-983d-98dfe284d75b)

  - Guardado de notas utilizando **Cloud Firestore**.
 
![image](https://github.com/Camilo-Hernandez/android-firebase-app/assets/36543483/d7d6b67d-2346-4d32-97d8-eba3a33d4a9e)

  - Almacenamiento de fotos tomadas con la cámara en **Cloud Storage**.

![image](https://github.com/Camilo-Hernandez/android-firebase-app/assets/36543483/a4fdc709-be29-4420-9dbc-bae89e1f4369)

- **Otras Características:**
  - Integración de **Analytics** para el análisis del comportamiento del usuario.
  - Uso de **Crashlytics** para el monitoreo de errores y fallos.
  - Implementación de **RemoteConfig** para cambiar dinámicamente el aspecto de la aplicación sin necesidad de relanzarla.
  - **Room Local Database** para realizar la sincronización con **Cloud Firestore**.
  - Uso de **Firebase Cloud Messaging (FCM)** para enviar notificaciones push personalizadas con contenido específico.

![image](https://github.com/Camilo-Hernandez/android-firebase-app/assets/36543483/16fb7d49-d643-4b7f-b849-93a886777688)



## Configuración del Proyecto

1. Clona este repositorio: `git clone https://github.com/tu_usuario/tu_proyecto.git`
2. Abre el proyecto en Android Studio.
3. Configura tu proyecto en la [Consola de Firebase](https://console.firebase.google.com/).
4. Descarga el archivo de configuración `google-services.json` y colócalo en el directorio `app/` de tu proyecto.

## Estructura del Proyecto

El proyecto está estructurado de la siguiente manera:

- **`app/`**: Contiene el código fuente de la aplicación.
- **`gradle/`**: Configuraciones específicas de Gradle.

## Instrucciones de Uso

Para ejecutar la aplicación, sigue estos pasos:

1. Conecta un dispositivo Android o utiliza un emulador.
2. Ejecuta la aplicación desde Android Studio.

¡Listo! La aplicación ahora debería estar en funcionamiento en tu dispositivo o emulador.

## Contribuciones

Si deseas contribuir a este proyecto, por favor sigue los siguientes pasos:

1. Haz un *fork* del repositorio.
2. Crea una nueva rama para tu función: `git checkout -b feature/nueva_funcion`.
3. Realiza tus cambios y commitea: `git commit -m 'Añade nueva_funcion'`.
4. Haz push a la rama: `git push origin feature/nueva_funcion`.
5. Abre un *pull request* en GitHub.

¡Gracias por contribuir!

## Licencia

Este proyecto está bajo la [Licencia MIT](LICENSE).

---

Espero que disfrutes explorando y contribuyendo a este proyecto. Happy coding!
