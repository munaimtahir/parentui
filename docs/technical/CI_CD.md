# CI/CD

## CI goals

The CI should protect the app from regressions and produce evidence for each AI-agent sprint.

## Recommended workflows

### 1. Android Build

Trigger:

- manual dispatch
- pull request
- main branch push

Checks:

- Gradle build
- lint
- unit tests

### 2. Emulator UI Tests

Trigger:

- manual dispatch
- release candidate branch

Checks:

- start Android emulator
- install debug APK
- run connected tests
- capture screenshots/logs

### 3. Release Candidate Artifact

Trigger:

- manual dispatch

Outputs:

- debug APK
- release unsigned/signed artifact depending on setup
- test report
- lint report

## Required artifact outputs

- APK/AAB where applicable
- unit test report
- lint report
- UI test report
- screenshots if generated

## AI-agent rule

The agent must not mark a sprint as complete unless it has run the relevant available CI/local checks or clearly documents why a check could not run.
