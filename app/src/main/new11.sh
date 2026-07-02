# 1. Update version from 17 to 18
sed -i 's/versionCode = 21/versionCode = 22/' app/build.gradle.kts
sed -i 's/versionName = "21"/versionName = "22"/' app/build.gradle.kts

# 2. Add, Commit and Push (Build will NOT skip)
git add .
git commit -m "Trigger signed build v22"
git push origin master
