# pSMS publication review

Reviewed 2026-11-16. Source commit: unavailable (no readable Git metadata).

- Default SMS role and restricted SMS permission declaration.
- Owner must confirm developer identity/contact, production support and privacy URLs, countries, pricing, ads, target age and content rating.
- Audit runtime traffic, dependencies and final merged release manifest before signing off Data safety. Source manifest alone is not a complete data audit.
- Create a signed release AAB, test the release on a device and complete the Play Console testing/review requirements applicable to this account. No release binary was built by this documentation task.

## Source evidence

Manifest: `app/src/main/AndroidManifest.xml`. Build: `app/build.gradle.kts`.

- network: `app/src/main/java/x/vladgba/sms/AboutPage.kt`, `app/src/main/java/x/vladgba/sms/pui/PAbout.kt`
- storage: `app/src/main/java/x/vladgba/sms/Db.kt`, `app/src/main/java/x/vladgba/sms/Labels.kt`, `app/src/main/java/x/vladgba/sms/Picks.kt`, `app/src/main/java/x/vladgba/sms/Rules.kt`, `app/src/main/java/x/vladgba/sms/Theme.kt`
- sharing: `app/src/main/java/x/vladgba/sms/BackupPage.kt`
- learning_clipboard: No matches in scanned module sources; absence is not proof.
- speech_translation: No matches in scanned module sources; absence is not proof.
