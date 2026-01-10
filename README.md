<h1 align="center">Wordsworth</h1>
<p align="center">Wordle-inspired Android word game with vocabulary tools and daily word insights.</p>
<p align="center">
  <a href="https://github.com/callmearya/Wordsworth-Game/releases">Releases</a> |
  <a href="#features">Features</a> |
  <a href="#local-setup">Local setup</a>
</p>
<p align="center">
  <img alt="Android" src="https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white">
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=white">
  <img alt="Jetpack Compose" src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white">
  <img alt="Firebase" src="https://img.shields.io/badge/Firebase-FFCA28?logo=firebase&logoColor=black">
  <img alt="Material 3" src="https://img.shields.io/badge/Material%203-757575?logo=materialdesign&logoColor=white">
</p>

## Inspiration
I was on a video call with my niece and we were playing a game involving vocabulary and words. That moment inspired me to build this app to help her grow her vocabulary. It looks similar to Wordle, but I added a few changes to make it better, like multiple word lengths, word meanings, and a full dictionary and thesaurus dashboard.

## Features
- Wordle-style gameplay with 4- and 5-letter modes, custom keyboard, and clear feedback tiles.
- Word meaning reveal after each round, plus a dictionary and thesaurus built in.
- Daily word cards with cached insights for quick vocabulary practice.
- Stats dashboard with streaks, guess distribution, and recent games (Firebase backed).
- Theme picker with multiple visual styles and dark mode.
- Sound effects and confetti on round completion.

## How it was made
- Jetpack Compose + Material 3 UI, with custom glass components and themed backgrounds.
- MVVM with ViewModel + StateFlow and a repository-driven data layer.
- Custom Wordle engine in `app/src/main/java/com/example/wordgame/logic/WordleEngine.kt`.
- OkHttp + kotlinx.serialization for network calls and JSON parsing.
- Firebase Auth + Firestore for sign-in and cloud stats.

## APIs used
- Dictionary definitions: https://dictionaryapi.dev/
- Random words: https://random-word-api.vercel.app/ (fallback: https://random-word-api.herokuapp.com/)
- Synonyms: https://api.datamuse.com/

## Local setup
1. Install Android Studio and the Android SDK.
2. Create `local.properties` with your SDK path (Android Studio can generate this).
3. Add Firebase config:
   - Download `google-services.json` from your Firebase project and place it at `app/google-services.json`.
   - Provide the manual Firebase values used by `BuildConfig` (choose one):
     - Add to `~/.gradle/gradle.properties`:
       - `firebaseApiKey=...`
       - `firebaseAppId=...`
       - `firebaseProjectId=...`
       - `firebaseSenderId=...` (optional)
       - `firebaseStorageBucket=...` (optional)
     - Or pass them on the command line: `./gradlew assembleDebug -PfirebaseApiKey=...`

## Sensitive/local-only files (gitignored)
- `app/google-services.json`
- `local.properties`
- `key.properties`
- `*.jks`, `*.keystore`, `*.p12`, `*.pem`
- `.env`, `.env.*`

## Releases
https://github.com/callmearya/Wordsworth-Game/releases

## Creators
### Arya Subramani S
[![GitHub](https://img.shields.io/badge/GitHub-@callmearya-181717?logo=github&logoColor=white)](https://github.com/callmearya)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-arya--subramani-0A66C2?logo=linkedin&logoColor=white)](https://www.linkedin.com/in/arya-subramani/)

### Vijaya Lakshmi D S
[![GitHub](https://img.shields.io/badge/GitHub-@viji--saravanan-181717?logo=github&logoColor=white)](https://github.com/viji-saravanan)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-vijaya--lakshmi--saravanan-0A66C2?logo=linkedin&logoColor=white)](https://www.linkedin.com/in/vijaya-lakshmi-saravanan-305972298/)

## Contributing
Issues and PRs are welcome. If you plan to change gameplay or APIs, open an issue first so we can align on the direction.
