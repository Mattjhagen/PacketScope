# PacketScope

Android app for BLE and NFC packet scanning with hidden camera detection features.

**⚠️ This is scaffolding only. BLE scanning and NFC reading are NOT yet implemented.**

## Current Screens

- **Scan**: Idle state with "Start Scan" and "Scan NFC" buttons (non-functional)
- **Detect**: Hidden camera detector placeholder with lens check guide, WiFi device list, and magnetometer reading
- **History**: Tab-based history view (Scans/Devices/Threats) with paywall UI placeholder
- **Map**: Saved scan location placeholder (no tracking, no logging)

## Features (Planned)

- BLE packet scanning
- NFC tag reading
- Hidden camera detection tools
- Location-tagged scan history
- Dark theme with cyan and purple accents

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material3
- **Architecture**: MVVM with StateFlow
- **Min SDK**: 31 (Android 12)
- **Target SDK**: 34 (Android 14)

## Theme Colors

- Background: `#0B0B14`
- Surface: `#1A1A26`
- Cyan Accent: `#00D8FF`
- Purple Accent: `#8B5CF6`
- Text Primary: `#FFFFFF`
- Text Secondary: `#9AA0B4`
- Bottom Nav: `#12121E`

## Build Requirements

1. **Android Studio**: Arctic Fox or newer
2. **JDK**: 17 or newer
3. **Android SDK**: API 31+

## Building

### In Android Studio

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle
4. Run on device or emulator

### Command Line

```bash
# Set Android SDK location (if not in default location)
echo "sdk.dir=/path/to/Android/Sdk" > local.properties

# Build debug APK
./gradlew assembleDebug

# Output: app/build/outputs/apk/debug/app-debug.apk
```

## Permissions

The app requires:

- `BLUETOOTH_SCAN` - Scan for BLE devices
- `BLUETOOTH_CONNECT` - Connect to BLE devices
- `ACCESS_FINE_LOCATION` - Required for BLE scanning on Android 12+
- `NFC` - Read NFC tags

All permissions are requested at runtime with rationale dialogs.

## Project Structure

```
app/src/main/java/com/pacmac/packetscope/
├── MainActivity.kt                 # Entry point with navigation
├── ui/
│   ├── screens/
│   │   ├── ScanScreen.kt          # BLE/NFC scanning interface
│   │   ├── MyDevicesScreen.kt     # Saved devices list
│   │   └── DetailScreen.kt        # Device details view
│   └── theme/
│       ├── Color.kt               # App color palette
│       ├── Theme.kt               # Material3 theme
│       └── Type.kt                # Typography
└── viewmodel/
    └── ScannerViewModel.kt        # Scanner state management
```

## Current Status

✅ **Completed:**
- Project scaffolding
- 4-tab navigation (Scan, Detect, History, Map)
- Dark theme with custom palette
- Permission declarations in manifest
- UI shell for all screens

🚧 **TODO:**
- Implement BLE scanning
- Implement NFC reading
- Implement camera detection features
- Add packet parsing and display
- Add device persistence
- Implement location tagging
- Add billing integration

## License

Copyright © 2026 PacMac
