# Demo Video
[![Demo Video](https://img.youtube.com/vi/Ncx9Vss0qN8/0.jpg)](https://www.youtube.com/watch?v=Ncx9Vss0qN8&feature=youtu.be "Demo Video")

# [APK File](https://github.com/tomkoptel/uploader-android-client/raw/main/app-debug.apk)

# TODO
* [✓] Access the file using conventional OS API
* [✓] Start upload process as the foreground service
* [✓] Implement API in reactive way listen for changes in tasks
* [✓] Implement in memory Task Store
* [✓] Use proper schedulers during download
* [✓] Setup progress listener during upload using emitter
* [✓] Rewrite the Uploader state listener to expose recomputed state during the processing
* [✓] Make sure to pick multiple files out of the file system
* [✓] Setup libraries/platform plugins
* [✓] Setup uploader module
* [✓] Rename package
* [✓] Setup Github project
* [✓] Setup Hilt
* [✓] Extend Service to consume Uploader and submit progress changes
* [] Add API to track status per task
* [] Refactor request API as FilePicker
* [] Transform emissions from the Uploader as Notification
* [] Build recycler view around API per task
* [] Add test cases to imitate failures for the task
* [] Imitate back-pressure issues
* [] Add unit tests for ProgressRequestBody.kt
* [] Setup Ktlint
* [] Setup Github actions to build project and tests
* [] Setup Code Coverage
* [] Setup Github actions to report code coverage reports

# File Manipulations
- Use `FileProvider` and serve that content via that `ContentProvider` implementation.
- ACTION_OPEN_DOCUMENT from the Storage Access Framework

# Edge cases
- DocumentFile.fromSingleUri resolves null
- File can not be read
- Intent is not resolved
- Request code failed
