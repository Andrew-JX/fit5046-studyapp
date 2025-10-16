# Project Introduction：
# Work Distribution Table：
## MinyuJi(Backend/data and business logic manager):
### Responsibilities:
Responsible for the app's data and business logic backbone: the Room data layer (tables/DAOs/repositories), repository interfaces and implementations, Hilt dependency injection, Entity↔Domain↔UI mappers, and end-to-end data flow integration for the three core modules (Task/Subject/Session).

ViewModel Description: I will first complete the ViewModel framework and data flow contracts (state structure, events, repository calls, error/loading state conventions) for Task/Subject/Session/Onboarding/Resources. JX will then adjust and refine these based on actual UI interaction details (such as event names/parameters, validation timing, and UI feedback) to ensure consistency with the front-end interface.

### Completed content：
Local Data Layer (Room)：
Three Tables: TaskEntity / SubjectEntity / SessionEntity / ResourceEntity
Three DAOs: TaskDao / SubjectDao / SessionDao / ResourceDao
AppDatabase Master Repository

Repository Pattern：
TaskRepo / SubjectRepo / SessionRepo / ResourceRepo Interfaces
Room Implementation + Hilt RepoModule Binding

Data Mapper：
Entity ↔ Domain ↔ UI Three-way Mapping to Ensure Cross-Layer Decoupling and Testability

ViewModel (Framework Built by Me, UI Details Adjusted by JX)：
TaskViewModel: Create/Update/Complete Switching, List Subscription
SubjectViewModel: Add, Delete, Modify, Color Palettes/Target Hours
SessionViewModel: Timers, Save, and History
OnboardingViewModel: Preference Implementation (Interface Integration) DataStore)
ResourcesViewModel: Resource Search/Filtering (Network Skeleton)

Network Skeleton (Resources)：
Retrofit Service, DTO, Mapper, DI Injection, VM Unified Error/Loading/Null State

Interfacing with Preferences/Authentication：
Using ZR's UserPreferencesRepository (focusLength, breakLength, username, onboardingDone)

# Project merge steps:
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

