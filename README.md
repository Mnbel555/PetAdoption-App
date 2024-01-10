# PetAdapt App - Android

Welcome to PetAdapt, an Android app dedicated to connecting pets in need with loving homes. Built with passion and Kotlin, this app uses Firebase for seamless adoption processes.

## Table of Contents

- [Description](#description)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [Coding](#coding)
- [License](#license)

## Description

PetAdapt is a pet adoption app that facilitates the process of finding forever homes for pets in need. Developed using Kotlin and Firebase, this app aims to connect pet lovers with pets available for adoption.
![App Screen](readme_assets/app_screen.png "App Screen")

## Features

- **User Authentication:** The application provides a secure login system. After successful authentication, users can access the main features of the application.
- **Personalized Pet Profiles:** Create profiles for each of your pets, including essential details and preferences.
- **Listed Pets:** Once logged in, users can view a list of pets. These pets are displayed with important details for users to browse through.
- **Add, Edit, and Delete Pets:** Users have the ability to add new pets to the list, edit existing pet details, and delete pets from the list.
- **Favorite Pets:** Users can add pets to their favorites for easy access in the future.
- **Community Interaction:** Connect with other pet owners, share experiences, and seek advice in the PetAdapt community.(UnderGOING)

## Installation

To run PetAdapt, follow these steps:

1. Clone the repository:

   ```bash
   git clone https://github.com/Mnbel555/PetAdapt.git

2. Navigate to the PetAdapt directory.
3. Open the project in Android Studio, compile, and deploy to your phone or emulator.
4. Start using PetAdapt to enhance your pet parenting experience!

## Usage

Explore the app to view available pets, create an adopter profile, and communicate with pet owners to initiate the adoption process.

## Contributing

If you'd like to contribute to PetAdapt, follow these steps:

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Make your changes and commit them with clear messages.
4. Push your changes to your fork.
5. Submit a pull request, explaining the changes you made.

 Or if you encounter any issues or have any questions about the PetAdapt application, please contact our support team.  
 
## Coding  

In this part, there are steps to initialize firebase, so you can test application. go through it and see firebase latest versions if you face any error.

<details>
    <summary>Firebase Integration</summary>

PetAdapt leverages Firebase for its backend infrastructure. Follow these steps to integrate Firebase into your project:

1. **Add your `google-services.json` file:**

   Place your Firebase configuration file (`google-services.json`) in the `app` directory of your project.

2. **Update Firebase dependencies in `build.gradle`:**

   Open your app module's `build.gradle` file and add the following Firebase dependencies. Make sure to replace the version numbers with the latest available.

   ```gradle
   // Add these dependencies to your app module build.gradle file, CHECKOUT firebsae latest version too.
    
   dependencies {
       // ... other dependencies
       implementation 'com.google.firebase:firebase-database:23.0.0'
       implementation 'com.google.firebase:firebase-auth:23.0.0'
       implementation 'com.google.firebase:firebase-storage:23.0.0'
       // ... other dependencies
   }

**Sync your project with Gradle:**

After adding the dependencies, sync your project with Gradle to ensure the changes take effect.
Note: Make sure to replace the version numbers (23.0.0) with the latest version available from the Firebase SDK.
Now, your PetAdapt app is ready to utilize Firebase for backend services. This integration enables features like real-time pet updates and user authentication within the app.

</details>
