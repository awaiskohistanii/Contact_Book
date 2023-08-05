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

https://github.com/awaiskohistanii/Contact_Book/assets/107536933/4ae7d996-1445-41f7-8481-81096a1831e3

![lv_0_20230805172933_AdobeExpress](https://github.com/awaiskohistanii/Contact_Book/assets/107536933/d90f7066-c02f-4dfd-9985-a89957a93050)
