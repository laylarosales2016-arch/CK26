# 1. Update version from 17 to 18
sed -i 's/versionCode = 30/versionCode = 31/' app/build.gradle.kts
sed -i 's/versionName = "30"/versionName = "31"/' app/build.gradle.kts

# 2. Add, Commit and Push (Build will NOT skip)
git add .
git commit -m "Trigger signed build 31"
git push origin master
