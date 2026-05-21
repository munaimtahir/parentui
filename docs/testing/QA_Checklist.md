# QA Checklist

## Product QA

- [ ] App name appears correctly.
- [ ] Onboarding explains launcher purpose.
- [ ] Onboarding does not overpromise lockdown.
- [ ] Parent can set PIN.
- [ ] Parent can add parent contact.
- [ ] Parent can add emergency contact.
- [ ] Parent can select approved apps.
- [ ] Child home screen is simple and readable.
- [ ] Child home screen does not show full app drawer.
- [ ] Home Mode works.
- [ ] School Mode works.
- [ ] Sleep Mode works.
- [ ] Parent dashboard is PIN protected.
- [ ] Layout editing is not available to child.
- [ ] App launches approved apps correctly.

## Privacy QA

- [ ] No ads.
- [ ] No analytics.
- [ ] No backend calls.
- [ ] No location tracking.
- [ ] No SMS access.
- [ ] No call log access.
- [ ] No contact harvesting.
- [ ] No microphone permission.
- [ ] No unnecessary internet permission.
- [ ] Local data is documented.
- [ ] Privacy policy draft matches implementation.

## Store QA

- [ ] App description avoids full lockdown claims.
- [ ] Screenshots avoid surveillance framing.
- [ ] Target Audience answers prepared.
- [ ] Data Safety answers match implementation.
- [ ] Privacy policy URL prepared before release.
- [ ] Permissions declarations prepared if needed.

## Technical QA

- [ ] Build passes.
- [ ] Unit tests pass.
- [ ] UI tests pass or limitations documented.
- [ ] Lint passes.
- [ ] No hardcoded secrets.
- [ ] No crash on missing apps.
- [ ] Works after restart.
- [ ] Settings persist after app restart.
- [ ] PIN is not stored as plaintext.
