# PacketScope

BLE and NFC packet scanner for Android with real-time packet capture and analysis.

## Features

- **BLE Scanning**: Scan for nearby Bluetooth Low Energy devices
- **NFC Reading**: Read NFC tags and cards
- **Packet Analysis**: View detailed packet information
- **Dark Theme**: Professional dark UI with cyan and purple accents
- **Device Management**: Save and manage discovered devices

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
- Navigation setup (3 screens)
- Dark theme with custom palette
- Permission handling
- UI layouts for all screens

🚧 **In Progress:**
- BLE scanning implementation
- NFC reading implementation
- Packet parsing and display
- Device persistence

## License

Copyright © 2026 PacMac
