# 1. Update version from 17 to 18
sed -i 's/versionCode = 26/versionCode = 27/' app/build.gradle.kts
sed -i 's/versionName = "26"/versionName = "27"/' app/build.gradle.kts

# 2. Add, Commit and Push (Build will NOT skip)
git add .
git commit -m "Trigger signed build 27"
git push origin master
