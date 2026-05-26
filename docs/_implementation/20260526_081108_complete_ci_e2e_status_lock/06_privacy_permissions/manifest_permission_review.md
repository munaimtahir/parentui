# Manifest Permission Review

The app requests the following permissions:
- `android.permission.QUERY_ALL_PACKAGES`: Justified because it is a launcher app and needs to query installed apps to build the allowed apps list.
- `android.permission.ACCESS_NETWORK_STATE`: ACCEPTABLE for checking online/offline status, doesn't actually provide network/internet access.
- `android.permission.SCHEDULE_EXACT_ALARM`: ACCEPTABLE for scheduled mode switching.

Permissions missing (as requested by safety guidelines):
- No `INTERNET` permission (Offline-first architecture).
- No `ACCESS_FINE_LOCATION` or `ACCESS_COARSE_LOCATION` permission.
- No `READ_CONTACTS` or `WRITE_CONTACTS` permission (Wait, does the app need to pick contacts? Need to verify if the contact picker relies on intents instead of permissions).
- No `RECORD_AUDIO`, `CAMERA`, or `SEND_SMS`.

Verdict: ACCEPTABLE
