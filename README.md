Project merge steps:
1. Switch to the target branch (team branch)
git checkout V1

A. 2. Merge your branch (MJ-V1 is an example)
   git merge MJ-V1

   3. Push to the remote
   git push origin V1

B. 2. Pull remote changes
   git pull origin V1

   3. If there are merge conflicts, Git will indicate which files are in conflict.

   4. After resolving conflicts, mark them resolved
   git add .

   5. Complete the merge
   git commit -m "Merge remote changes from V1"

   6. Push
   git push origin V1
