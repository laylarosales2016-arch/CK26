# 1. Update version from 17 to 18
sed -i 's/versionCode = 29/versionCode = 30/' app/build.gradle.kts
sed -i 's/versionName = "29"/versionName = "30"/' app/build.gradle.kts

# 2. Add, Commit and Push (Build will NOT skip)
git add .
git commit -m "Trigger signed build 30"
git push origin master
