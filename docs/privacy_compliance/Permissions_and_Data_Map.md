# Permissions and Data Map

## Purpose

This document defines the expected MVP data and permission model.

The default answer should be: **do not request a permission unless the feature cannot work without it.**

## MVP data map

| Data item | Required | Stored where | Leaves device | Notes |
|---|---:|---|---:|---|
| Parent PIN | Yes | Local encrypted/shared preferences or secure store | No | Store securely; avoid plaintext |
| Approved app package names | Yes | Local database/preferences | No | Needed for launcher tiles |
| Mode assignments | Yes | Local database/preferences | No | Home/School/Sleep |
| Parent phone number | Yes | Local storage | No | Manual entry preferred |
| Emergency phone number | Yes | Local storage | No | Manual entry preferred |
| Child name | No | Avoid in MVP | No | Not needed |
| Location | No | Not stored | No | Not in MVP |
| Contacts list | No | Not stored | No | Manual entry preferred |
| SMS/call logs | No | Not stored | No | Rejected |
| Advertising ID | No | Not stored | No | Rejected |
| Analytics events | No | Not stored | No | Rejected for MVP |

## Permission candidates

| Permission/API | MVP decision | Reason |
|---|---|---|
| INTERNET | Avoid | No backend, no analytics, no ads |
| CALL_PHONE | Avoid initially | Use dial intent unless direct call is essential |
| READ_CONTACTS | Avoid | Manual contact entry |
| ACCESS_FINE_LOCATION | Reject | No location tracking |
| ACCESS_COARSE_LOCATION | Reject | No location tracking |
| RECORD_AUDIO | Reject | No monitoring |
| READ_SMS | Reject | No message monitoring |
| READ_CALL_LOG | Reject | No call monitoring |
| CAMERA | Avoid unless in-app camera feature added | Launch external camera app if allowed |
| QUERY_ALL_PACKAGES | Avoid if possible | Use package visibility carefully; justify if needed |
| POST_NOTIFICATIONS | Avoid in MVP | No notification features needed |
| SYSTEM_ALERT_WINDOW | Reject | High-risk, unnecessary |
| Accessibility Service | Reject in MVP | Avoid using accessibility as control workaround |

## Package visibility

The app may need to list installed launchable apps.

Implementation should:

- use Android package manager APIs responsibly
- avoid collecting/transmitting app list
- avoid using installed app list for analytics
- explain locally stored app selections in privacy documentation

## Data Safety expectation

For MVP, the intended Data Safety posture should be:

- no data shared
- no data collected off-device
- local app settings only
- no ads
- no analytics

Final Play Console answers must match the actual build and SDKs.
