# pSMS

A tiny, private SMS app for Android. The whole app is about 140 KB and has no internet access at all, yet it filters spam, groups conversations into labels and puts one-time codes one tap away.

<p align="center">
  <img src="store/feature_graphic_1024x500.png" alt="pSMS" width="720">
</p>

## Features

- **Default SMS app.** Messages live in the system message store, so other apps and backups keep working.
- **Conversations.** Thread list with unread badges and contact names, chat bubbles with send status and a live character/part counter.
- **One-time codes.** Codes like "Your code is 482193" get a **Copy** button on the notification, in the chat and on the conversation row. Copied codes are hidden from the clipboard preview on Android 13+.
- **Your own pickers.** Add regular expressions to pull tracking numbers, amounts or links out of messages; they show up as copy chips too, with a live preview while you write them.
- **Allow / block rules.** Match by sender, by text or both, in six modes (contains, equals, starts/ends with, regex, wildcard). Blocked messages go to a local quarantine you can restore from.
- **Labels.** Group conversations by sender or by words, filter with chips on the main screen, and optionally hide a label's conversations from the inbox.
- **Backup and restore.** XML compatible with *SMS Backup & Restore*, or pSMS's own JSON, optionally encrypted with a password (AES-256-GCM).
- **Notifications** with inline reply and mark-as-read, **quick replies** from the incoming-call screen, and a **home-screen widget** with your latest conversations.
- **No ads, no network, no data collection.**

## Permissions

| Permission | Why | Required |
|---|---|---|
| Send, receive and read SMS | Working as the default SMS app | Yes |
| Receive MMS / WAP push | Android requires them from a default SMS app; MMS content is not shown | Yes |
| Contacts | Names in the conversation list and the contact picker | Optional |
| Notifications | New-message notifications with reply and copy-code actions | Optional |

pSMS has no internet permission.

### Notes

- MMS, choosing a SIM on dual-SIM phones and RCS chat are not supported.
- How filter rules decide, and every option of rules, labels and backups, are described in the [guide](docs/GUIDE.md).

## Build

Requires Android 7.0+ (API 24). Built with Kotlin on the plain Android framework, without libraries.

```bash
./gradlew assembleRelease
```

The APK is written to `app/build/outputs/apk/release/`. Every push to `main` also builds it on GitHub: open the latest [Build run](https://github.com/vladgba/pSMS/actions/workflows/build.yml?query=branch%3Amain) and download the APK from **Artifacts**.

## License

See [LICENSE](LICENSE).
