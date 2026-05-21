# Play Store and Compliance Notes

## Important

This is not legal advice. The final release must be checked against the current Google Play Developer Program Policies and local legal requirements before submission.

## Target audience

The app is intended for parents/guardians to configure a child-facing launcher.

Because children are part of the target user group, Google Play Families policy requirements must be considered from the start.

## Policy-sensitive decisions

MVP should remain:

- no ads
- no ad SDKs
- no analytics SDKs
- no child account
- no cloud backend
- no location tracking
- no message monitoring
- no contact harvesting
- no device identifiers for tracking
- no unnecessary permissions

## Play Console declarations

Before release, prepare accurate declarations for:

- Target Audience and Content
- Data Safety
- App access instructions
- Privacy Policy
- Permissions declarations if any sensitive permissions are used
- Ads declaration
- Content rating questionnaire

## Families policy direction

If children are included in the target audience:

- data practices must be accurate and child-safe
- identifiers such as advertising ID and hardware identifiers must not be transmitted from children or users of unknown age
- ad SDKs must be Families-compliant if ads are ever used
- the app must not endanger children or enable inappropriate child interaction

## Data Safety direction

For MVP, the preferred Data Safety posture is:

- no data collected off-device
- no data shared
- settings stored locally
- no ads
- no analytics

Actual Play Console answers must match the final implementation and included SDKs.

## Privacy policy direction

Privacy policy should say clearly:

- what data is stored locally
- that no account is required
- that no child activity is remotely monitored
- that no location/SMS/call log/browsing history is collected
- that parents configure the launcher locally
- that uninstall/default launcher behavior depends on Android/device settings

## Store description wording

Use:

> EasyUI Guardian Launcher helps parents create a simpler, safer home screen for a child’s phone. Choose approved apps, add parent and emergency contact tiles, and switch between Home, School, and Sleep modes.

Avoid:

> Fully locks your child’s phone and blocks everything.

## Official references to review before release

- Google Play Families Policy
- Google Play Data Safety section guidance
- Google Play Target Audience and Content guidance
- Google Play User Data policy
- Google Play Permissions and APIs that Access Sensitive Information policy
- Google Play SDK requirements
