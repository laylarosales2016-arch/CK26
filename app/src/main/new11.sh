# 1. Update version from 17 to 18
sed -i 's/versionCode = 32/versionCode = 33/' app/build.gradle.kts
sed -i 's/versionName = "32"/versionName = "33"/' app/build.gradle.kts

# 2. Add, Commit and Push (Build will NOT skip)
git add .
git commit -m "Trigger signed build 33"
git push origin master
