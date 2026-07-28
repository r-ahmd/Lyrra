# Walkthrough - Colorful Explore and Bug Fixes

I have improved the UI of the Explore option in the Search section and fixed several lint errors across the project.

## Changes

### [app] UI Improvements

#### [SearchScreen.kt](file:///home/mr/StudioProjects/Lyrra/app/src/main/java/com/lyrra/app/ui/screens/SearchScreen.kt)
- Redesigned the "Explore" entry point from a simple text row to a vibrant gradient card.
- The new card uses a linear gradient (Violet to Purple) and includes a descriptive subtitle to make it more inviting.

### [app] Bug Fixes (Lint)

#### Notification Permissions
- Updated [ArtistReleaseNotificationHelper.kt](file:///home/mr/StudioProjects/Lyrra/app/src/main/java/com/lyrra/app/ArtistReleaseNotificationHelper.kt) and [DownloadNotificationHelper.kt](file:///home/mr/StudioProjects/Lyrra/app/src/main/java/com/lyrra/app/DownloadNotificationHelper.kt) to explicitly check for `POST_NOTIFICATIONS` permission before attempting to post notifications, satisfying Android 13+ requirements.

#### Audio Processor Fixes
- Added missing `super.isActive()` calls in the following audio processors to ensure base class logic is executed:
    - [BassBoostAudioProcessor.kt](file:///home/mr/StudioProjects/Lyrra/app/src/main/java/com/lyrra/app/audio/BassBoostAudioProcessor.kt)
    - [CrossfeedAudioProcessor.kt](file:///home/mr/StudioProjects/Lyrra/app/src/main/java/com/lyrra/app/audio/CrossfeedAudioProcessor.kt)
    - [EqualizerAudioProcessor.kt](file:///home/mr/StudioProjects/Lyrra/app/src/main/java/com/lyrra/app/audio/EqualizerAudioProcessor.kt)
    - [NormalizerAudioProcessor.kt](file:///home/mr/StudioProjects/Lyrra/app/src/main/java/com/lyrra/app/audio/NormalizerAudioProcessor.kt)

#### Media3 & Opt-In Cleanup
- Annotated [PlaybackService.kt](file:///home/mr/StudioProjects/Lyrra/app/src/main/java/com/lyrra/app/PlaybackService.kt) and [PlayerViewModel.kt](file:///home/mr/StudioProjects/Lyrra/app/src/main/java/com/lyrra/app/PlayerViewModel.kt) to correctly handle Media3's `UnstableApi` usage.

#### Code Quality
- Removed an invisible Byte Order Mark (BOM) in [PlaylistCsv.kt](file:///home/mr/StudioProjects/Lyrra/app/src/main/java/com/lyrra/app/PlaylistCsv.kt) that was causing parsing issues in some environments.
- Removed an unnecessary `super.onCleared()` call in [PlayerViewModel.kt](file:///home/mr/StudioProjects/Lyrra/app/src/main/java/com/lyrra/app/PlayerViewModel.kt).

## Verification Results

### Automated Tests
- Ran `./gradlew lintDebug` and the build now passes with no errors.
- Ran `./gradlew assembleDebug` and the project compiles successfully.
