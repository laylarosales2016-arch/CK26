# 1. Update version from 17 to 18
sed -i 's/versionCode = 36/versionCode = 37/' app/build.gradle.kts
sed -i 's/versionName = "36"/versionName = "37"/' app/build.gradle.kts

# 2. Add, Commit and Push (Build will NOT skip)
git add .
git commit -m "Trigger signed build 37"
git push origin master
