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

![Screenshot 2024-01-10 094924](https://github.com/Mnbel555/PetAdoption-App/assets/125232753/0081def1-6599-4afa-bacf-c75af13f7418)  

![Screenshot 2024-01-10 095124](https://github.com/Mnbel555/PetAdoption-App/assets/125232753/ac7bba8a-a145-4272-a1e2-865dedb6040b)


![Screenshot 2024-01-10 101501](https://github.com/Mnbel555/PetAdoption-App/assets/125232753/6a3b99e2-a11f-49ed-bce1-a5c8ed1b640a)

![Screenshot 2024-01-10 095223](https://github.com/Mnbel555/PetAdoption-App/assets/125232753/b7667f0f-a563-4158-9ead-8dc2a37613fd)
![Screenshot 2024-01-16 173850](https://github.com/Mnbel555/PetAdoption-App/assets/125232753/61de16d6-8728-4f72-afef-bbd13d021336)
![Screenshot 2024-01-16 173927](https://github.com/Mnbel555/PetAdoption-App/assets/125232753/eb89f5d4-4e12-4c1a-ae6f-2c4cec82c69e)



## Features


- **User Authentication:** The application provides a secure login system. After successful authentication, users can access the main features of the application.
- **Personalized Pet Profiles:** Create profiles for each of your pets, including essential details and preferences.
- **Listed Pets:** Once logged in, users can view a list of pets. These pets are displayed with important details for users to browse through.
- **Add, Edit, and Delete Pets:** Users have the ability to add new pets to the list, edit existing pet details, and delete pets from the list.
- **Favorite Pets:** Users can add pets to their favorites for easy access in the future.
- **Community Interaction:** Connect with other pet owners, share experiences, and seek advice in the PetAdapt community.(InProcess)

## Installation

To run PetAdapt, follow these steps:

1. Clone the repository:

   ```bash
   git clone https://github.com/Mnbel555/PetAdoption-App.git

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

<details>
    <summary>ChatActivity based on database structure</summary>
The default structure in app is for demonstration purpose only, this is more convenient as it handles user chat according to petId, therefore it assigns each pet unique id while the default structure sets up chat on recepient user irrespective of pet. thanks    

## Firebase Data Structure

### Users
- **NnkSuEp3yu2rCHofvy3**
  - *User data fields go here*

### Conversations
- **AxSpkAySWQWPDpGJ6rCHGoOVH723**
  - **messages**
    - **NoGo_c1lHPk-2vDzqhf**
      - **messageId**: "-NoGo_c1lHPk-2vDzqhf"
      - **message**: "helo"
      - **receiverUid**: "AxSpkAySWQWPDpGJ6rCHGoOVH723"
      - **senderUid**: "vDM7N21WzBe1sDWw8KGRZvQR8FX2"
      - **timestamp**: 1705401009298
    - *Additional messages go here*

**Use this**

```kotlin
package ipca.study.petadapt

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ipca.study.petadapt.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var adapterChat: AdapterChat
    private lateinit var databaseReference: DatabaseReference
    private lateinit var otherUserId: String
    private lateinit var petId: String
    private lateinit var otherUserName: String
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var receiverUsername: String
    private lateinit var receiverImageUrl: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val chatId = intent.getStringExtra("otherUserId")
        // Retrieve information passed from PetDetailsActivity
        otherUserId = intent.getStringExtra("otherUserId") ?: ""
        otherUserName = intent.getStringExtra("otherUserName") ?: ""
        petId = intent.getStringExtra("petId") ?: ""

        // Check if information is not empty
        if (otherUserId.isNotEmpty() && otherUserName.isNotEmpty()) {
            setupChat()
        } else {
            // Handle the case when information is missing
        }
    }

    private fun setupChat() {
        adapterChat = AdapterChat(this, ArrayList())
        binding.chatRv.layoutManager = LinearLayoutManager(this)
        binding.chatRv.adapter = adapterChat

        // Set up a listener to load and display chat messages
        databaseReference = FirebaseDatabase.getInstance().getReference("conversations")
            .child(petId)
            .child(otherUserId)
            .child("messages")
        // Fetch receiver's details
        val usersReference = FirebaseDatabase.getInstance().getReference("users")
        usersReference.child(otherUserId).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val modelUser = snapshot.getValue(Model::class.java)
                        if (modelUser != null) {
                            receiverUsername = modelUser.username ?: ""
                            receiverImageUrl = modelUser.imageUrl ?: ""

                            // Update UI with receiver's details
                            updateUIWithReceiverDetails()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error if needed
                }
            })

        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val modelChat = snapshot.getValue(ModelChat::class.java)
                if (modelChat != null) {
                    if (modelChat.senderUid == firebaseAuth.uid && modelChat.receiverUid == otherUserId ||
                        modelChat.senderUid == otherUserId && modelChat.receiverUid == firebaseAuth.uid
                    ) {
                        // Add the chat message to the adapter
                        adapterChat.addMessage(modelChat)
                        binding.chatRv.scrollToPosition(adapterChat.itemCount - 1)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
               
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
              
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
               
            }

            override fun onCancelled(error: DatabaseError) {
                
            }
        })

        // Example: Set up a listener to send messages when the send button is clicked
        binding.sendFab.setOnClickListener {
            sendMessage()
        }
    }

    private fun updateUIWithReceiverDetails() {
        // Set the receiver's username in a TextView
        binding.toolbarTitle.text = receiverUsername

        // Load the receiver's profile picture using an image-loading library (e.g., Glide)
        Glide.with(this).load(receiverImageUrl).into(binding.toolbarProfile)
    }
    private fun sendMessage() {
        // Get the message from the input field
        val messageText = binding.message.text.toString().trim()

        // Check if the message is not empty
        if (messageText.isNotEmpty()) {
            val timestamp = System.currentTimeMillis()

            // Create a ModelChat object without MessageType
            val modelChat = ModelChat(
                messageId = databaseReference.push().key ?: "",
                message = messageText,
                senderUid = firebaseAuth.uid ?: "",
                receiverUid = otherUserId,
                timestamp = timestamp
            )

            // Add the message to Firebase Realtime Database
            databaseReference.child(modelChat.messageId).setValue(modelChat)

            // Clear the input field after sending the message
            binding.message.text.clear()
        }
    }

}
```



</details>
