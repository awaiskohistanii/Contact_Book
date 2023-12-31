# Contact_Book
The Contact Book App is a simple Android application that allows users to manage their contacts effectively. It provides a user-friendly interface to add, view, edit, and delete contacts from the device's local database. The app is built using Java and follows best practices for Android app development.

# Features
**1. Add New Contacts :** Users can add new contacts by providing details such as name, phone number, email address, and optional image. The input is validated to ensure data integrity.

**2. View All Contacts:** The main activity displays a list of all the saved contacts using a Custom RecyclerView with animation to provide smooth and visually appealing scrolling.

**3. View Contact Details:** Users can click on a contact in the list to view its details, including all the contact information and the associated image.

**4. Edit Contact Details:** The app enables users to edit contact details and update them in the contact book. Changes are saved using SQLite CRUD operations.

**5. Delete Contact:** Users can delete unwanted contacts, and the app provides a confirmation dialog to avoid accidental deletions.

**6. Database Management:** The app uses SQLite and the DBHelper class to manage the local database, ensuring data persistence.

**7. Content Provider:** The ContactBookProvider class abstracts data access, allowing other apps to interact with the contact data securely.

**8. Permissions Handling:** The app requests necessary permissions (e.g., READ_CONTACTS, WRITE_CONTACTS) to access the user's contact data.

**9. Sorting:** Contacts can be sorted alphabetically or by other attributes, enhancing the user experience.

**10. Contact Book Statistics:** The ShowContactBook activity displays the total number of contacts or other relevant statistics.

**11. Contact Image Support:** The app allows users to set contact images or import them from their device's gallery using a custom gallery photo picker.

**12. Contact Sharing:** Users can easily share contact details with others through various communication channels like SMS, phone call, WhatsApp call, or social media.

**13. Custom Toolbar and Collapsing Toolbar:** Implement a custom toolbar with an expandable collapsing toolbar for an aesthetically pleasing and user-friendly UI.

**14. Lottie Animation:** Use Lottie animations to enhance the user experience with engaging and interactive animations.

**15. LoaderManager and AsyncTaskLoader:** Implement LoaderManager and AsyncTaskLoader to efficiently load and manage data from the database, avoiding UI freezes.

**16. RecyclerView with Swipe to Delete:** The main activity displays a list of contacts using a RecyclerView with a swipe-to-delete feature. 

# Intent Features

**1. Phone Call and SMS:** Users can initiate a phone call or send an SMS directly from the contact details screen. Tapping the phone number will prompt the user to choose the default phone app to make the call or open the SMS app to send a text message.

**2. WhatsApp Call and SMS:** If the contact has a WhatsApp number associated, users can initiate a WhatsApp call or send a WhatsApp message directly from the contact details screen.

# Demo
https://github.com/awaiskohistanii/Contact_Book/assets/107536933/3a08d7c7-9b1b-4cfd-ad6c-964b32f31d6b

# Screen Shots
<p align="center">
<img src="https://github.com/awaiskohistanii/Contact_Book/assets/107536933/3a26139c-960a-4005-a882-bf3f00b3a2db" width="200" height="400">
<img src="https://github.com/awaiskohistanii/Contact_Book/assets/107536933/1c5ff884-66c1-41d3-af2e-a10b3d5ba351" width="200" height="400">
<img src="https://github.com/awaiskohistanii/Contact_Book/assets/107536933/62d4e42d-f39c-4f65-aa67-cade44ad9e9b" width="200" height="400">
</p>

<p align="center">
<img src="https://github.com/awaiskohistanii/Contact_Book/assets/107536933/96afd64f-d596-48d0-900c-f595dc4ad64a" width="200" height="400">
<img src="https://github.com/awaiskohistanii/Contact_Book/assets/107536933/f00ba906-b29d-4356-817c-5f2fda257355" width="200" height="400">
<img src="https://github.com/awaiskohistanii/Contact_Book/assets/107536933/20cba41b-f36d-441e-8e55-3ba354a39f16" width="200" height="400">
</p>

<p align="center">
<img src="https://github.com/awaiskohistanii/Contact_Book/assets/107536933/716ad47f-59b8-4921-bb8f-a57bd9925f41" width="200" height="400">
<img src="https://github.com/awaiskohistanii/Contact_Book/assets/107536933/107456d2-8cbc-4956-9850-b22aa1ed3033" width="200" height="400">
<img src="https://github.com/awaiskohistanii/Contact_Book/assets/107536933/4ab485b6-8eae-4f8e-8f9b-2434adc84b97" width="200" height="400">
</p>
