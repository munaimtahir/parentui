# Future Managed-Device Track (Design Note)

This mega run explicitly targets the **consumer launcher-first** EasyUI product.

## What stays in consumer EasyUI

EasyUI is a *safer home screen / guided-control launcher*:
- Parent-approved apps shown inside EasyUI.
- Calm child home screen.
- Home / School / Sleep modes (optional Exam/Travel if enabled).
- Local-only parent PIN gate for parent controls.
- Setup Health + honest limitations.
- Offline-first behavior.

EasyUI does **not** claim:
- full device lockdown
- impossible-to-bypass control
- “blocks everything”
- system-level enforcement outside Android’s normal app boundaries

## What a managed-device edition could be (later, separate)

If a deeper “kiosk-like” or stronger enforcement product is ever pursued, it should be a **separate track** with separate messaging and requirements, such as:
- enterprise/managed-device APIs (Device Owner / work profile) *only where legally/ethically appropriate*
- explicit admin provisioning flows
- clear disclaimers about supported devices and reset/escape procedures
- separate distribution and support expectations

This track is **not** in scope for the consumer EasyUI launcher and must not be implied in consumer UI copy.

## Why separate tracks matter

Mixing consumer “launcher-first” promises with managed-device enforcement:
- creates user trust issues (“full lockdown” expectations)
- increases permissions and compliance complexity
- adds provisioning and support burden

Consumer EasyUI should remain honest: a safer home screen that complements Android settings and (optionally) Google Family Link for deeper system restrictions.

