# 👨‍💼 Employee Manager App

A simple Employee Management Android App built using **Kotlin**, **Room Database**, **MVVM Architecture**, and **LiveData**. It allows users to:

- Add employees
- View a list of employees
- Update employee details
- Delete employees

---

## 🏗 Architecture

This app follows **MVVM (Model-View-ViewModel)** architecture with proper layer separation:


---

## 🛠 Tech Stack

- **Kotlin**
- **Room Database**
- **LiveData**
- **ViewModel**
- **MVVM Architecture**
- **RecyclerView**
- **Material Design**

---

## 📸 Features

- ✅ Add employee data (name, designation, salary)
- ✅ View employees in a RecyclerView
- ✅ Update employee via edit button
- ✅ Delete employee via delete button
- ✅ Live data updates with no manual refresh needed
- ✅ Data stored locally using SQLite via Room

---

## 🚀 Getting Started

1. **Clone the Repository**
    ```bash
    git clone https://github.com/your-username/employee-manager.git
    ```
2. **Open in Android Studio**
3. **Let Gradle Build and Sync**
4. **Run the App** on emulator or physical Android device

---

## 📁 Project Structure


---

## 🔧 Dependencies

```kotlin
// Room Database
implementation("androidx.room:room-runtime:2.6.1")
kapt("androidx.room:room-compiler:2.6.1")

// ViewModel & LiveData
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

// RecyclerView
implementation("androidx.recyclerview:recyclerview:1.3.2")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// Material Design
implementation("com.google.android.material:material:1.11.0")



```
plugins {
    id("kotlin-kapt")
}
