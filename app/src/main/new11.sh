# 1. Update version from 17 to 18
sed -i 's/versionCode = 28/versionCode = 29/' app/build.gradle.kts
sed -i 's/versionName = "28"/versionName = "29"/' app/build.gradle.kts

# 2. Add, Commit and Push (Build will NOT skip)
git add .
git commit -m "Trigger signed build 29"
git push origin master
