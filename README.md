# oop1-spring-2025-2026
cow_farm_management_system
🚀 Key Features
CRUD Operations: Complete functionality to Add, View, Update, and Delete cow records seamlessly.
Robust Validation:
Cow ID must be exactly an 8-digit numeric string (prevents duplicates).
Strict type checking for Age (Integers) and Daily Milk Yield (Doubles).
Prevents malformed data inputs (e.g., blocks illegal characters like commas ,).
Interactive JTable: A real-time data table displaying all records. Clicking on any row automatically populates the input fields for fast updating or deletion.
Search Functionality: Case-insensitive partial matching search filter by Cow ID or Breed.
Exception Handling: Completely crash-proof utilizing try-catch blocks for all File I/O operations and input parsing.
🛠️ Tech Stack & Concepts Used
Language: Java (JDK 8 or higher)
GUI Framework: Java Swing & AWT (Grid Layout, Flow Layout, Border Layout)
Architecture: Object-Oriented Programming (OOP)
Encapsulation: Fully private fields with getters and setters in Entity class.
File I/O: BufferedReader, PrintWriter, and File utilities for persistent CSV data storage.
Thread Safety: Safely initialized on the Event Dispatch Thread (EDT) using SwingUtilities.invokeLater().
📂 Project Structure
The project strictly follows a standard package directory separation matching professional Java guidelines:

src/
└── cowmanagementsystem/
    ├── Start.java              # Application entry point containing the main method
    │
    ├── entity/
    │   └── Cow.java            # Livestock Data Model & CSV parsing logic
    │
    ├── fileio/
    │   ├── CowFileIO.java      # Handles text file operations (Read, Write, Update, Delete)
    │   └── cows.txt            # Local database text file containing cow records
    │
    └── gui/
        └── CowGUI.java         # Main User Interface window and event listeners
