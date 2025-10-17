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

## Zhiruo Zhai (Frontend Architecture & Authentication/Preferences Manager):

### Responsibilities:
Responsible for the app's user authentication system and navigation architecture: Firebase Authentication integration (login/sign-up), navigation drawer with cross-screen routing, profile management (username/password modification, study preferences), and DataStore Preferences for local user settings. Also responsible for form validation guidelines (password strength, character limits).

### Completed content:

Authentication System (Firebase):
Firebase project setup and configuration (google-services.json, gradle dependencies)
AuthViewModel: StateFlow-based state management, sign-in/sign-up/sign-out logic with error handling
Password strength utility (PasswordStrength.kt): Real-time complexity evaluation with color feedback

Navigation Connection:
AppDrawer.kt: Navigation drawer items configuration
StudyNav.kt: Navigation callbacks integration (onNavigateToSession/onNavigateBack/onOpenDrawer) across Dashboard/Session/Task/Subject/Profile/Resources screens

User Preferences & Onboarding:
UserPreferencesRepository (DataStore): Type-safe preferences storage for username, focusLength, breakLength, major, difficulty, weeklyTargetHours, hasSeenOnboarding

Profile Management:
ProfileScreen: User account information display, username editing with character counter (20 char limit), password change with re-authentication, study settings (focus/break length sliders)
ProfileViewModel: Username update, password change with EmailAuthProvider re-authentication, focus/break length persistence, logout functionality

### Interfacing with Other Modules:
Using JMY's UserPreferencesRepository for focusLength, breakLength, username, onboardingDone (DataStore integration)
Providing navigation structure and authentication guards for all team members' screens

## Junxing Peng (Frontend UI & ViewModel Integration Lead)
### Responsibilities:
Responsible for the app’s frontend UI implementation and ViewModel-to-UI integration across the Task, Session, Subject, and Dashboard modules.
Built all Jetpack Compose screens and reusable UI components, connected them with ViewModels via Kotlin Coroutines and StateFlow, and ensured reactive, single-source-of-truth data flow with Room through Hilt-injected repositories.

Focused on UI logic, form validation, date/time picker behavior, and real-time state updates for user-facing modules.
Worked closely with Minyu Ji (backend/data layer) to align repository contracts and ensure proper state/event propagation to the UI layer.

### Completed content:
Core Screens:
TaskScreen: Task creation, editing, deletion, priority selection, subject linking, and completion status toggle.
Includes form validation (title length, blank fields).
Integrated TaskDatePicker with custom logic preventing selection of past dates.
Implemented snackbar feedback and bottom-sheet subject selector (SubjectListBottomSheet).
SessionScreen: Learning timer with start/pause/cancel/finish actions.
Implemented real-time timer updates using elapsedMillis and progress visualization with Compose CircularProgressIndicator.
Added history list (studySessionsList) and deletion dialog (DeleteDialog).
SubjectScreen: Subject list display, add/edit/delete, goal hour management, and color palette selection.
UI bound to SubjectViewModel with dynamic Flow updates.
DashboardScreen: Overview of total study progress, subject goal tracking, and task/session summaries.
Built basic filtering placeholders (All/Today/This Week) for future data aggregation integration.

### UI Components (presentation.components):
TaskCheckBox — Pure UI component for task completion toggle.
TaskList / studySessionsList — LazyColumn-based reusable list templates.
DeleteDialog — Reusable confirmation dialog for task/session deletion.
TaskDatePicker — Date picker dialog integrated with SelectableDates logic.
AddSubjectDialog — Subject creation form with validation.
SubjectListBottomSheet — Bottom sheet selector for linking tasks/sessions to subjects.

### ViewModel Integration:
Subscribed to StateFlow data (tasks, subjects, sessionsUi) using collectAsState().
Implemented event-driven feedback (snackbarHostState, TaskEvent.Saved/Error).
Used LaunchedEffect for one-time ViewModel event collection.
Connected ViewModel actions (saveNewTask(), toggleCompleted(), finishAndSave()) directly to UI event handlers.
Ensured smooth communication between Compose UI and Repository through ViewModel functions provided by backend.

### Cross-cutting tasks:
Designed and built consistent Compose layouts with Material 3, Scaffold, and LazyColumn.
Applied Hilt dependency injection for ViewModel and repository access.
Standardized form error messages and validation logic across screens.
Built modular, reusable UI components (dialogs, pickers, bars, lists).
Managed state restoration and lifecycle consistency with rememberSaveable.
Ensured cohesive color, typography, and layout alignment with ZR’s Material3 global theme.

### Interfacing with Other Modules:
Worked with Minyu Ji to connect ViewModels to Room repositories via Hilt injection (TaskRepo, SubjectRepo, SessionRepo).
Integrated with Zhiruo Zhai’s navigation and DataStore-based user preferences, ensuring all Study, Task, and Session screens are accessible within the navigation flow.

## Jiahui Qing (API Integration & System Behavior Lead)
### Responsibilities:
Led the integration of external APIs, system-level notification scheduling, and data visualization across the Login and Dashboard modules.  
Focused on enhancing user engagement through dynamic content injection, real-time reminders, and intuitive progress tracking.  
Implemented black-box testing across all screens and resolved critical UI-state inconsistencies.  
Collaborated with frontend and backend leads to ensure seamless data flow, permission handling, and lifecycle consistency.

### Completed Content:
Core Features:  
**LoginScreen**:
- Integrated public quote API to fetch and display motivational quotes on login.
- Injected API service via Hilt and bound quote data to UI using reactive StateFlow.
- Enabled user-triggered refresh with debounce logic and loading feedback.

**DashboardScreen**:
- Implemented dual-mode study time visualization using Jetpack Compose and MPAndroidChart.
   - **Pie Chart**: Displays time allocation across subjects.
   - **Bar Chart**: Shows daily study duration trends.
- Connected chart data to ViewModel via Flow, ensuring real-time updates and lifecycle awareness.

**Task Reminder System**:
- Built AlarmManager-based notification scheduler to alert users on task due dates.
- Designed permission request dialog for POST_NOTIFICATIONS on Android 13+.
- Ensured compatibility with system notification channels and user preferences.

**Session Management Fixes**:
- Conducted black-box testing across all screens using manual test cases.
- Identified and resolved Dashboard bug preventing session deletion.
- Verified session lifecycle consistency and UI state restoration post-deletion.

**Documentation & Compliance**:
- Authored final project report.
- Drafted AI usage declaration outlining model integration, ethical considerations, and transparency practices.

### ViewModel Integration:
- Injected QuoteRepository via Hilt and exposed quoteState using StateFlow.
- Subscribed to ViewModel events with collectAsState() and LaunchedEffect for one-time triggers.
- Connected chart data and alarm scheduling logic to ViewModel actions (e.g., scheduleAlarm(), refreshQuote()).
- Ensured reactive updates and permission-aware behavior across UI components.

### Cross-cutting Tasks:
- Applied Material 3 design principles across dialogs, charts, and permission flows.
- Modularized chart components and permission dialogs.
- Ensured lifecycle-safe alarm scheduling and notification dispatch.
- Validated UI behavior under edge cases through black-box testing.

### Interfacing with Other Modules:
- Coordinated with backend lead to define API contract for quote retrieval and alarm persistence.
- Integrated with navigation and user preference modules to ensure notification settings and chart filters are preserved.
- Verified compatibility with Jetpack Compose UI components and Hilt-injected repositories across modules.

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

