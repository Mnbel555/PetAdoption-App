package ipca.study.petadapt

import android.os.Parcel
import android.os.Parcelable


data class Pet(
    var petId: String? = "",
    val petName: String? = "",
    val petAge: String? = "",
    val petBreed: String? = "",
    val sexMale: Boolean? = false,
    val vaccinatedYes: Boolean? = false,
    val adTitle: String? = "",
    val adDescription: String? = "",
    val imageUrls: List<String>? = ArrayList(),
    val userId: String? = "",
    var isFavourite: Boolean? = false

    ): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(petId)
        parcel.writeString(petName)
        parcel.writeString(petAge)
        parcel.writeString(petBreed)
        parcel.writeValue(sexMale)
        parcel.writeValue(vaccinatedYes)
        parcel.writeString(adTitle)
        parcel.writeString(adDescription)
        parcel.writeStringList(imageUrls)
        parcel.writeString(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Pet> {
        // Create a new instance of Parcelable class, from given Parcel
        override fun createFromParcel(parcel: Parcel): Pet {
            return Pet(parcel)
        }

        // New array of Parcelable class
        override fun newArray(size: Int): Array<Pet?> {
            return arrayOfNulls(size)
        }
    }
}
