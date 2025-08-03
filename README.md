Monster Blaster
Monster Blaster is an addictive 2D monster-shooting mobile game built with Java and LibGDX. Players choose from four unique characters, battle waves of enemies on a forest-themed map, and upgrade their weapons and abilities to survive increasingly challenging levels.
Features

Single Map: A forest-themed 2D map (800x600 pixels) with trees as obstacles.
Four Characters:
Sniper: High damage, long range, slow fire rate, low health.
Tank: High health, short range, moderate damage, slow movement.
Mage: Area-of-effect (AoE) attacks, medium health, slow fire rate.
Rogue: Fast movement, high fire rate, low damage, medium health.


Gameplay: Shoot waves of monsters (Goblins), earn points, and level up.
Upgrades: Improve damage, fire rate, or health using earned points.
Levels: Increasing difficulty with more and stronger enemies per wave.
Mobile-Friendly: Touch-based controls for movement and shooting.

Prerequisites

Java Development Kit (JDK) 8 or higher
LibGDX 1.12.1
Gradle
Android SDK (for Android deployment)
IDE (e.g., IntelliJ IDEA, Eclipse)

Setup Instructions

Clone the Repository:
git clone https://github.com/jediApe/monster-blaster.git
cd monster-blaster


Set Up LibGDX:

Use the LibGDX setup tool to generate a project, or ensure the following dependencies are in your build.gradle:
dependencies {
    implementation "com.badlogicgames.gdx:gdx:1.12.1"
    implementation "com.badlogicgames.gdx:gdx-backend-android:1.12.1"
}




Add Assets:

Place placeholder textures (forest_map.png, character_sniper.png, character_tank.png, character_mage.png, character_rogue.png, bullet.png, enemy.png) in the android/assets folder.
Replace with actual sprite assets for better visuals.


Build and Run:

For desktop: Run gradlew desktop:run.
For Android: Run gradlew android:build and deploy to an emulator or device.



Gameplay

Objective: Survive waves of enemies by shooting them and earning points.
Controls:
Move: Drag on the screen to move the character.
Shoot: Tap to shoot at the touched location.


Progression: Earn points by defeating enemies to unlock upgrades and level up.
Waves: Each wave increases enemy count and strength.

Project Structure

core/src/com/monsterblaster/MonsterBlasterGame.java: Main game logic, including character classes, enemy AI, and game loop.
android/assets/: Placeholder for game assets (textures).

Contributing

Fork the repository.
Create a new branch (git checkout -b feature/your-feature).
Make changes and commit (git commit -m "Add your feature").
Push to your branch (git push origin feature/your-feature).
Open a pull request.

Future Improvements

Add a character selection menu using LibGDX's Scene2D.
Implement sound effects and background music.
Introduce more enemy types and map variations.
Create a HUD for health, score, and upgrades.

License
This project is licensed under the MIT License. See the LICENSE file for details.
Contact
For questions or suggestions, open an issue or contact [your email or GitHub handle].