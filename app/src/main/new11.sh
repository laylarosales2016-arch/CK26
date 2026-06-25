# 1. Update version from 17 to 18
sed -i 's/versionCode = 17/versionCode = 18/' app/build.gradle.kts
sed -i 's/versionName = "17"/versionName = "18"/' app/build.gradle.kts

# 2. Add, Commit and Push (Build will NOT skip)
git add .
git commit -m "Trigger signed build v18"
git push origin master
