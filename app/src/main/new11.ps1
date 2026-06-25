# 1. Update version from 17 to 18 in build.gradle.kts
$path = "app/build.gradle.kts"
(Get-Content $path) -replace 'versionCode = 17', 'versionCode = 18' -replace 'versionName = "17"', 'versionName = "18"' | Set-Content $path

# 2. Add, Commit and Push
git add .
git commit -m "Trigger signed build v18"
git push origin master
