Use cases for manual testing before release
=====
 
 
 *Automate as much as possible!*
 
 * Record route -> Add image, add note, add transport change -> save -> Appears in list

 * Recorded route -> one detail -> show icons for note, image for photo, icon for record -> 
on click record -> starts playing

 * Record route -> Add image, add note, add transport change -> stop recordnig without save
  -> images and recordings are deleted.
  
NFC  
----------
Write - Route detail, nfc mark click, attach tag, should close write activity and say the tag is fine

Read
* *Cold start* attach tag Should start navigation, on back press list of routes
* On list of routes - attach tag - should show navigation above list, no double lists
* on other screen, attach tag, closes everything else except list, starst navigation
* On non existing route in tag, error snackbar