# Known Limitations (Internal Pilot)

- EasyUI **cannot force itself** as the default Home app.
- A parent must manually set EasyUI as the default Home app in Android Settings.
- If EasyUI is not the default Home app, pressing **HOME** may return to the phone’s normal launcher.
- EasyUI hides apps **inside EasyUI** only; it does not remove apps from the device.
- EasyUI is **not** a full phone lockdown / kiosk system.
- EasyUI does **not**:
  - track location
  - monitor messages
  - monitor calls
  - record audio
  - read browser history
  - run ads or analytics SDKs
- EasyUI does not replace Google Family Link; it complements Android settings / Family Link for deeper restrictions.
- Some Android/OEM devices show different Settings screens.
- Offline use should work, but airplane mode may need **manual** verification depending on OEM restrictions.

