# SQLite Data Migrator - Android Kotlin App

A clean and user-friendly Android application for managing contacts using **SQLite Database**. 

You can easily **Add, View, Edit, Delete** contacts and also **Import/Export** your data in CSV format for backup or migration purposes.

Perfect for learning Android SQLite CRUD operations with CSV handling in Kotlin.

## ✨ Features

- **Full CRUD Operations**
  - Add new contact
  - View all contacts in RecyclerView
  - Edit existing contact
  - Delete contact

- **CSV Import & Export**
  - Export all contacts to `.csv` file
  - Import contacts from any `.csv` file (with header support)

- **Empty State Handling** – Shows nice message when no data is available
- **Bottom Sheet Dialog** for Add/Edit operations
- **Material Design 3** + Edge-to-Edge support
- **Toast notifications** for all actions
- Lightweight and fast (no Room, pure SQLite)

## 🛠 Tech Stack

- **Language**: Kotlin
- **Database**: SQLite (SQLiteOpenHelper)
- **UI**: RecyclerView, FloatingActionButton, BottomSheetDialog
- **File Handling**: Storage Access Framework (SAF) for CSV Import/Export
- **Architecture**: Simple MVVM-like structure (Activity + Helper)

## 🚀 How to Use

1. **Add Contact** → Click on the Floating Action Button (+) 
2. **Edit Contact** → Tap the edit icon on any contact
3. **Delete Contact** → Tap the delete icon
4. **Export Data** → Click "Export" button → Choose location and save as `contacts_backup.csv`
5. **Import Data** → Click "Import" button → Select your CSV file

**CSV Format Example:**
```csv
Name,Phone,Email
John Doe,+91 9876543210,john@example.com
Jane Smith,+91 9988776655,jane@example.com
