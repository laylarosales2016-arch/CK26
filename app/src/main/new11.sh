# 1. Update version to 17
sed -i 's/versionCode = 16/versionCode = 17/' app/build.gradle.kts
sed -i 's/versionName = "16"/versionName = "17"/' app/build.gradle.kts

# 2. Add, Commit and Push (Build will NOT skip)
git add .
git commit -m "Trigger signed build v17"
git push origin master
    