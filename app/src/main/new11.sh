# 1. Update version from 17 to 18
sed -i 's/versionCode = 34/versionCode = 35/' app/build.gradle.kts
sed -i 's/versionName = "34"/versionName = "35"/' app/build.gradle.kts

# 2. Add, Commit and Push (Build will NOT skip)
git add .
git commit -m "Trigger signed build 35"
git push origin master
