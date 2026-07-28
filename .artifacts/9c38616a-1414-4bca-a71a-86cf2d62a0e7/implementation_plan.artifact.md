# Implementation Plan - Fix Blank Icon and Update Design

The app icon is currently appearing blank on the device. This is likely because the `foreground` layer of the adaptive icon is using a JPEG file (`ic_launcher_foreground_photo.jpeg`), which is not recommended and often fails to render in adaptive icons (which expect transparency).

The user also requested a more "good looking" icon based on a new image. I will recreate this design using high-quality Vector Drawables, which are more reliable and scale perfectly.

## Proposed Changes

### [app] Resources

#### [MODIFY] [ic_launcher_background.xml](file:///home/mr/StudioProjects/Lyrra/app/src/main/res/drawable/ic_launcher_background.xml)
- Update the background gradient to match the rich purple/violet tones in the user's provided image.

#### [MODIFY] [ic_launcher_foreground.xml](file:///home/mr/StudioProjects/Lyrra/app/src/main/res/drawable/ic_launcher_foreground.xml)
- Redesign the foreground to feature a centered, soft-pink rounded "card" containing a deep purple music note, matching the user's reference.
- Use vector paths to ensure it renders correctly on all devices.

#### [MODIFY] [ic_launcher.xml](file:///home/mr/StudioProjects/Lyrra/app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml)
- Revert the foreground layer to `@drawable/ic_launcher_foreground` (now updated with the new design).
- Ensure the monochrome layer also uses the new design.

#### [MODIFY] [ic_launcher_round.xml](file:///home/mr/StudioProjects/Lyrra/app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml)
- Sync with `ic_launcher.xml`.

## Verification Plan

### Automated Tests
- Run `./gradlew assembleDebug` to ensure no resource errors.
- Run `./gradlew lintDebug` to verify resource health.

### Manual Verification
- Deploy to the device.
- Verify the icon is no longer blank and matches the new design.
