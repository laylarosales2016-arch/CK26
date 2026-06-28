# 1. Update version from 17 to 18
sed -i 's/versionCode = 18/versionCode = 19/' app/build.gradle.kts
sed -i 's/versionName = "18"/versionName = "19"/' app/build.gradle.kts

# 2. Add, Commit and Push (Build will NOT skip)
git add .
git commit -m "Trigger signed build v19"
git push origin master
