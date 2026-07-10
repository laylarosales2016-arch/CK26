# 1. Update version from 17 to 18
sed -i 's/versionCode = 22/versionCode = 23/' app/build.gradle.kts
sed -i 's/versionName = "22"/versionName = "23"/' app/build.gradle.kts

# 2. Add, Commit and Push (Build will NOT skip)
git add .
git commit -m "Trigger signed build v23"
git push origin master
