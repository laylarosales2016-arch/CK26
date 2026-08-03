# 1. Update version from 17 to 18
sed -i 's/versionCode = 35/versionCode = 36/' app/build.gradle.kts
sed -i 's/versionName = "35"/versionName = "36"/' app/build.gradle.kts

# 2. Add, Commit and Push (Build will NOT skip)
git add .
git commit -m "Trigger signed build 36"
git push origin master
