# Expense Logger Android Application

## About This Project
Built by Alex Orr as part of JHUB Coding Scheme Module 2: Creating an App. This is my first proper Android application using Kotlin!

## What It Does
This app helps you track expenses by taking photos of receipts. Perfect for keeping track of what you need to claim back. You can snap a picture of any receipt, add how much you want to claim, and the app keeps a running total for you.

## Main Features
The app includes everything required for the assignment:
* Camera functionality to photograph receipts
* Automatic timestamps for when each receipt was captured
* Manual entry for claim amounts
* Running total displayed on the main screen
* List view showing all your expenses
* Tap any expense to see the full receipt
* Delete function for removing old expenses
* All data saved locally using SQLite

## Technical Bits
**Language**: Kotlin (brand new to me!)  
**IDE**: Android Studio  
**Minimum SDK**: 24 (Android 7.0)  
**Target SDK**: 34 (Android 14)  
**Database**: SQLite for local storage  
**Design**: Material Design components

## Known Issues
**Important**: The camera doesn't work properly on the Android emulator in Windows. This is a known emulator limitation, not a bug in my code! The app uses a test mode on the emulator but works perfectly on actual Android phones. Spent ages trying to get the webcam working with the emulator before discovering this is a common problem.

## How The Code Is Organised
MainActivity.kt          → Main screen showing your expense list
AddExpenseActivity.kt    → Screen for adding new expenses after taking photo
ExpenseDetailActivity.kt → Shows full receipt image and details
Expense.kt              → Data model (just holds the expense info)
ExpenseAdapter.kt       → Handles the list display
DatabaseHelper.kt       → All the database stuff

## Getting It Running
You'll need Android Studio installed. Then:

1. Clone or download the code from GitHub
2. Open the project in Android Studio
3. Let it sync (this takes a while first time)
4. Either connect your Android phone or use the emulator
5. Hit the run button

Remember to allow camera permissions when it asks!

## How To Use It
Pretty straightforward really:

**Adding expenses**: Tap the camera button (bottom right), take a photo, enter the amount you want to claim, save it.

**Viewing expenses**: They all show up on the main screen with little thumbnail images. The total to claim is shown at the top.

**Checking details**: Just tap any expense to see the full receipt photo.

**Deleting**: Open any expense and hit the delete button.

## What I Learned
This project taught me loads about Android development. ViewBinding was completely new to me (much better than findViewById!). Also learned about Android permissions the hard way when the camera wouldn't work at first. SQLite was interesting too, quite different from the databases I've used before.

## Future Ideas
If I had more time, I'd love to add:
* Edit feature for expenses
* Categories (food, transport, etc)
* Export to spreadsheet
* Maybe OCR to read amounts automatically?

## Testing Notes
Tested thoroughly on:
* Pixel emulator (using test mode due to camera issues)
* Would recommend testing on actual device for full camera functionality

All assignment requirements have been met and tested.

## Credits
Created by Alex Orr for JHUB Coding Scheme Module 2.
